<?php
/**
 * MBBank API Handler
 * Kiểm tra lịch sử giao dịch và tự động nạp tiền
 */

class MBBank
{
    private $apiKey;
    private $apiUrl;
    private $connect;

    public function __construct($apiKey, $connect)
    {
        $this->apiKey = $apiKey;
        $this->apiUrl = "https://api.sieuthicode.net/historyapimbbankv2/{$apiKey}";
        $this->connect = $connect;
    }

    /**
     * Lấy lịch sử giao dịch từ API MBBank
     */
    public function getTransactionHistory()
    {
        try {
            $context = stream_context_create([
                'http' => [
                    'method' => 'GET',
                    'header' => 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
                    'timeout' => 30
                ]
            ]);

            $response = file_get_contents($this->apiUrl, false, $context);
            
            if ($response === false) {
                throw new Exception("Không thể kết nối đến API MBBank");
            }

            $data = json_decode($response, true);
            
            if (json_last_error() !== JSON_ERROR_NONE) {
                throw new Exception("Lỗi parse JSON: " . json_last_error_msg());
            }

            return $data;
        } catch (Exception $e) {
            error_log("MBBank API Error: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Xử lý giao dịch donate
     */
    public function processDonateTransactions()
    {
        $history = $this->getTransactionHistory();
        
        if (!$history || !isset($history['data'])) {
            return false;
        }

        $processedCount = 0;

        foreach ($history['data'] as $transaction) {
            // Kiểm tra nội dung chuyển khoản có chứa "donate"
            if (isset($transaction['description']) && 
                stripos($transaction['description'], 'donate') !== false) {
                
                // Lấy username từ nội dung chuyển khoản
                $username = $this->extractUsernameFromDescription($transaction['description']);
                
                if ($username && $this->processDonateTransaction($transaction, $username)) {
                    $processedCount++;
                }
            }
        }

        return $processedCount;
    }

    /**
     * Trích xuất username từ nội dung chuyển khoản
     * Format: "donate username" hoặc "donate <username>"
     */
    private function extractUsernameFromDescription($description)
    {
        // Loại bỏ các ký tự đặc biệt và khoảng trắng thừa
        $description = trim($description);
        
        // Tìm pattern: donate <username> hoặc donate username
        if (preg_match('/donate\s+<?([a-zA-Z0-9_]+)>?/i', $description, $matches)) {
            return trim($matches[1]);
        }
        
        return false;
    }

    /**
     * Xử lý giao dịch donate cho một user cụ thể
     */
    private function processDonateTransaction($transaction, $username)
    {
        try {
            $this->connect->beginTransaction();

            // Kiểm tra user có tồn tại không
            $stmt = $this->connect->prepare("SELECT id, username, sotien, danap FROM account WHERE username = :username");
            $stmt->execute([':username' => $username]);
            
            if ($stmt->rowCount() == 0) {
                throw new Exception("Không tìm thấy user: {$username}");
            }

            $user = $stmt->fetch(PDO::FETCH_ASSOC);
            
            // Kiểm tra giao dịch đã được xử lý chưa
            $checkStmt = $this->connect->prepare("
                SELECT id FROM payments 
                WHERE name = :user_id 
                AND amount = :amount 
                AND date = :date 
                AND status = 1
            ");
            $checkStmt->execute([
                ':user_id' => $user['id'],
                ':amount' => $transaction['amount'],
                ':date' => date('Y-m-d H:i:s', strtotime($transaction['date']))
            ]);

            if ($checkStmt->rowCount() > 0) {
                // Giao dịch đã được xử lý
                $this->connect->rollback();
                return false;
            }

            // Lưu lịch sử giao dịch
            $insertStmt = $this->connect->prepare("
                INSERT INTO payments (name, amount, date, status, description) 
                VALUES (:user_id, :amount, :date, 1, :description)
            ");
            $insertStmt->execute([
                ':user_id' => $user['id'],
                ':amount' => $transaction['amount'],
                ':date' => date('Y-m-d H:i:s', strtotime($transaction['date'])),
                ':description' => $transaction['description']
            ]);

            // Cộng tiền vào tài khoản
            $updateStmt = $this->connect->prepare("
                UPDATE account 
                SET sotien = sotien + :amount, danap = danap + :amount 
                WHERE username = :username
            ");
            $updateStmt->execute([
                ':amount' => $transaction['amount'],
                ':username' => $username
            ]);

            $this->connect->commit();
            
            // Log thành công
            error_log("Đã nạp thành công {$transaction['amount']} VND cho user: {$username}");
            
            return true;

        } catch (Exception $e) {
            $this->connect->rollback();
            error_log("Lỗi xử lý giao dịch donate cho {$username}: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Test API connection
     */
    public function testConnection()
    {
        $history = $this->getTransactionHistory();
        
        if ($history === false) {
            return ['success' => false, 'message' => 'Không thể kết nối đến API'];
        }

        return [
            'success' => true, 
            'message' => 'Kết nối API thành công',
            'data_count' => isset($history['data']) ? count($history['data']) : 0,
            'sample' => isset($history['data'][0]) ? $history['data'][0] : null
        ];
    }
}
