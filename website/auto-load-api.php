<?php
/**
 * Auto Load API - Không cần đăng nhập, chạy tự động
 * Xử lý giao dịch MBBank và tự động cộng tiền
 */

require_once 'Controllers/Configs.php';

// Log file để theo dõi
$log_file = 'logs/auto-load.log';
$log_dir = dirname($log_file);
if (!is_dir($log_dir)) {
    mkdir($log_dir, 0777, true);
}

function writeLog($message) {
    global $log_file;
    $timestamp = date('Y-m-d H:i:s');
    file_put_contents($log_file, "[$timestamp] $message\n", FILE_APPEND | LOCK_EX);
}

function echoAndLog($message) {
    echo $message . "\n";
    writeLog($message);
}

echoAndLog("=== AUTO LOAD API START ===");

// Tạo bảng mbbank_log nếu chưa tồn tại
try {
    $create_table_sql = "CREATE TABLE IF NOT EXISTS `mbbank_log` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `transaction_id` varchar(100) NOT NULL,
        `username` varchar(50) NOT NULL,
        `amount` int(11) NOT NULL,
        `description` text,
        `transaction_date` datetime DEFAULT NULL,
        `status` enum('success','error','pending') DEFAULT 'pending',
        `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (`id`),
        UNIQUE KEY `transaction_id` (`transaction_id`),
        KEY `username` (`username`),
        KEY `status` (`status`),
        KEY `created_at` (`created_at`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
    
    $Connect->exec($create_table_sql);
    echoAndLog("✓ Bảng mbbank_log đã sẵn sàng");
} catch (Exception $e) {
    echoAndLog("⚠ Lỗi tạo bảng mbbank_log: " . $e->getMessage());
}

try {
    $api_key = '2603b3626360f20f2440fddce90d0161';
    $api_url = "https://api.sieuthicode.net/historyapimbbankv2/{$api_key}";
    
    echoAndLog("Đang kết nối API MBBank...");
    
    // Gọi API MBBank
    $curl = curl_init();
    curl_setopt_array($curl, [
        CURLOPT_URL => $api_url,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_FOLLOWLOCATION => true,
        CURLOPT_SSL_VERIFYPEER => false,
        CURLOPT_USERAGENT => 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    ]);
    
    $response = curl_exec($curl);
    $httpCode = curl_getinfo($curl, CURLINFO_HTTP_CODE);
    $error = curl_error($curl);
    curl_close($curl);
    
    if ($httpCode !== 200 || !$response) {
        echoAndLog("Lỗi API: HTTP $httpCode - $error");
        exit();
    }
    
    $data = json_decode($response, true);
    
    if (!$data) {
        echoAndLog("Lỗi: API trả về dữ liệu không hợp lệ");
        exit();
    }
    
    // Kiểm tra cấu trúc dữ liệu API
    $transactions = [];
    if (isset($data['data'])) {
        $transactions = $data['data'];
    } elseif (isset($data['transactions'])) {
        $transactions = $data['transactions'];
    } else {
        $transactions = $data;
    }
    
    echoAndLog("Nhận được " . count($transactions) . " giao dịch từ API");
    
    $new_count = 0;
    $processed_count = 0;
    $error_count = 0;
    
    // Xử lý từng giao dịch
    foreach ($transactions as $transaction) {
        // Kiểm tra các trường có thể có trong response
        $description = '';
        $amount = 0;
        $trans_id = '';
        $date = '';
        
        if (isset($transaction['description'])) {
            $description = $transaction['description'];
        } elseif (isset($transaction['content'])) {
            $description = $transaction['content'];
        } elseif (isset($transaction['note'])) {
            $description = $transaction['note'];
        }
        
        if (isset($transaction['amount'])) {
            $amount = intval($transaction['amount']);
        } elseif (isset($transaction['money'])) {
            $amount = intval($transaction['money']);
        }
        
        if (isset($transaction['transactionID'])) {
            $trans_id = $transaction['transactionID'];
        } elseif (isset($transaction['id'])) {
            $trans_id = $transaction['id'];
        } elseif (isset($transaction['ref'])) {
            $trans_id = $transaction['ref'];
        }
        
        if (isset($transaction['date'])) {
            $date = $transaction['date'];
        } elseif (isset($transaction['transactionDate'])) {
            $date = $transaction['transactionDate'];
        } elseif (isset($transaction['created_at'])) {
            $date = $transaction['created_at'];
        }
        
        // Chỉ xử lý giao dịch nhận tiền (IN)
        $is_incoming = false;
        if (isset($transaction['type']) && $transaction['type'] === 'IN') {
            $is_incoming = true;
        } elseif (isset($transaction['direction']) && $transaction['direction'] === 'IN') {
            $is_incoming = true;
        } elseif ($amount > 0) {
            $is_incoming = true;
        }
        
        if ($is_incoming && $description) {
            $description_lower = strtolower($description);
            
            // Tìm giao dịch có "naptien username"
            if (strpos($description_lower, 'naptien') !== false) {
                // Extract username
                if (preg_match('/naptien\s+(\w+)/i', $description, $matches)) {
                    $username = $matches[1];
                    
                    echoAndLog("Tìm thấy giao dịch naptien: $username - $amount VNĐ - ID: $trans_id");
                    
                    // Kiểm tra user tồn tại trước
                    $user_query = "SELECT id, sotien, danap FROM account WHERE username = :username";
                    $user_stmt = $Connect->prepare($user_query);
                    $user_stmt->bindParam(':username', $username);
                    $user_stmt->execute();
                    $user_data = $user_stmt->fetch(PDO::FETCH_ASSOC);
                    
                    if (!$user_data) {
                        echoAndLog("User $username không tồn tại");
                        $error_count++;
                        continue;
                    }
                    
                    // Kiểm tra đã xử lý chưa
                    $already_processed = false;
                    
                    // Kiểm tra trong bảng mbbank_log
                    try {
                        $check_query = "SELECT id FROM mbbank_log WHERE transaction_id = :trans_id";
                        $check_stmt = $Connect->prepare($check_query);
                        $check_stmt->bindParam(':trans_id', $trans_id);
                        $check_stmt->execute();
                        
                        if ($check_stmt->rowCount() > 0) {
                            $already_processed = true;
                        }
                    } catch (Exception $e) {
                        // Fallback: kiểm tra trong bảng payments
                        // Chỉ kiểm tra theo amount và user_id vì description có thể bị cắt
                        $check_query2 = "SELECT id FROM payments WHERE name = :user_id AND amount = :amount";
                        $check_stmt2 = $Connect->prepare($check_query2);
                        $check_stmt2->bindParam(':user_id', $user_data['id']);
                        $check_stmt2->bindParam(':amount', $amount);
                        $check_stmt2->execute();
                        
                        if ($check_stmt2->rowCount() > 0) {
                            $already_processed = true;
                        }
                    }
                    
                    if ($already_processed) {
                        echoAndLog("Giao dịch $trans_id đã được xử lý trước đó");
                        $processed_count++;
                        continue;
                    }
                    
                    try {
                        $Connect->beginTransaction();
                        
                        $old_sotien = intval($user_data['sotien']);
                        $old_danap = intval($user_data['danap']);
                        $new_sotien = $old_sotien + $amount;
                        $new_danap = $old_danap + $amount;
                        
                        // Cập nhật số dư
                        $update_query = "UPDATE account SET sotien = :sotien, danap = :danap WHERE username = :username";
                        $update_stmt = $Connect->prepare($update_query);
                        $update_stmt->bindParam(':sotien', $new_sotien);
                        $update_stmt->bindParam(':danap', $new_danap);
                        $update_stmt->bindParam(':username', $username);
                        $update_stmt->execute();
                        
                        // Lưu vào bảng payments (không dùng description)
                        try {
                            $payment_query = "INSERT INTO payments (name, amount, date, status) VALUES (:user_id, :amount, :date, '1')";
                            $payment_stmt = $Connect->prepare($payment_query);
                            $payment_stmt->bindParam(':user_id', $user_data['id']);
                            $payment_stmt->bindParam(':amount', $amount);
                            $payment_stmt->bindParam(':date', date('Y-m-d H:i:s', strtotime($date)));
                            $payment_stmt->execute();
                            echoAndLog("✓ Đã lưu vào bảng payments");
                        } catch (Exception $paymentError) {
                            echoAndLog("⚠ Không thể lưu vào payments: " . $paymentError->getMessage());
                        }
                        
                        // Lưu log vào mbbank_log
                        try {
                            $log_query = "INSERT INTO mbbank_log (transaction_id, username, amount, description, transaction_date, status, created_at) VALUES (?, ?, ?, ?, ?, 'success', NOW())";
                            $log_stmt = $Connect->prepare($log_query);
                            $log_stmt->execute([
                                $trans_id,
                                $username,
                                $amount,
                                $description,
                                $date
                            ]);
                            echoAndLog("✓ Đã lưu log vào mbbank_log");
                        } catch (Exception $logError) {
                            echoAndLog("⚠ Không thể lưu vào mbbank_log: " . $logError->getMessage());
                        }
                        
                        $Connect->commit();
                        
                        echoAndLog("✅ Cộng tiền thành công cho $username: +$amount VNĐ (Sotien: $old_sotien → $new_sotien, Danap: $old_danap → $new_danap)");
                        $new_count++;
                        
                    } catch (Exception $e) {
                        $Connect->rollback();
                        echoAndLog("❌ Lỗi cộng tiền cho $username: " . $e->getMessage());
                        $error_count++;
                    }
                }
            }
        }
    }
    
    $summary = "Kết quả: Mới xử lý: $new_count, Đã xử lý trước: $processed_count, Lỗi: $error_count";
    echoAndLog($summary);
    echoAndLog("=== AUTO LOAD API END ===");
    
    // Output JSON cho AJAX call
    if (isset($_GET['json'])) {
        header('Content-Type: application/json');
        echo json_encode([
            'status' => 'success',
            'new_transactions' => $new_count,
            'already_processed' => $processed_count,
            'errors' => $error_count,
            'total_api_transactions' => count($transactions),
            'timestamp' => date('Y-m-d H:i:s')
        ]);
    }
    
} catch (Exception $e) {
    $error_msg = "Exception: " . $e->getMessage();
    echoAndLog($error_msg);
    
    if (isset($_GET['json'])) {
        header('Content-Type: application/json');
        echo json_encode([
            'status' => 'error',
            'message' => $error_msg
        ]);
    }
}
?>
