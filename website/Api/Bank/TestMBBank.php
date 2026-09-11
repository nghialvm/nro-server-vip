<?php
/**
 * Test MBBank API - Để kiểm tra và debug
 */

date_default_timezone_set('Asia/Ho_Chi_Minh');

// Include các file cần thiết
require_once '../../Controllers/Configs.php';
require_once 'MBBank.php';

// Cấu hình API Key MBBank
$MBBANK_API_KEY = '2603b3626360f20f2440fddce90d0161';

// Kiểm tra quyền truy cập
if (!isset($_GET['key']) || $_GET['key'] !== $Settings['Username']) {
    http_response_code(403);
    die(json_encode([
        'status' => 'error',
        'message' => 'Không có quyền truy cập!'
    ]));
}

try {
    // Khởi tạo MBBank handler
    $mbBank = new MBBank($MBBANK_API_KEY, $Connect);
    
    echo "<h2>Test MBBank API</h2>";
    echo "<p><strong>API Key:</strong> {$MBBANK_API_KEY}</p>";
    echo "<p><strong>API URL:</strong> https://api.sieuthicode.net/historyapimbbankv2/{$MBBANK_API_KEY}</p>";
    echo "<hr>";
    
    // Test kết nối API
    echo "<h3>1. Test Kết Nối API</h3>";
    $testResult = $mbBank->testConnection();
    
    if ($testResult['success']) {
        echo "<p style='color: green;'><strong>✓ Kết nối API thành công!</strong></p>";
        echo "<p>Số giao dịch: {$testResult['data_count']}</p>";
        
        if ($testResult['sample']) {
            echo "<h4>Mẫu giao dịch:</h4>";
            echo "<pre>" . json_encode($testResult['sample'], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";
        }
    } else {
        echo "<p style='color: red;'><strong>✗ Kết nối API thất bại:</strong> {$testResult['message']}</p>";
    }
    
    echo "<hr>";
    
    // Test xử lý giao dịch donate
    echo "<h3>2. Test Xử Lý Giao Dịch Donate</h3>";
    
    // Lấy lịch sử giao dịch
    $history = $mbBank->getTransactionHistory();
    
    if ($history && isset($history['data'])) {
        echo "<p><strong>Tổng số giao dịch:</strong> " . count($history['data']) . "</p>";
        
        $donateTransactions = [];
        foreach ($history['data'] as $transaction) {
            if (isset($transaction['description']) && 
                stripos($transaction['description'], 'donate') !== false) {
                $donateTransactions[] = $transaction;
            }
        }
        
        echo "<p><strong>Số giao dịch donate:</strong> " . count($donateTransactions) . "</p>";
        
        if (!empty($donateTransactions)) {
            echo "<h4>Danh sách giao dịch donate:</h4>";
            echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
            echo "<tr style='background-color: #f0f0f0;'>";
            echo "<th style='padding: 8px;'>Thời gian</th>";
            echo "<th style='padding: 8px;'>Số tiền</th>";
            echo "<th style='padding: 8px;'>Nội dung</th>";
            echo "<th style='padding: 8px;'>Username</th>";
            echo "</tr>";
            
            foreach ($donateTransactions as $transaction) {
                $username = $mbBank->extractUsernameFromDescription($transaction['description']);
                echo "<tr>";
                echo "<td style='padding: 8px;'>" . (isset($transaction['date']) ? $transaction['date'] : 'N/A') . "</td>";
                echo "<td style='padding: 8px;'>" . (isset($transaction['amount']) ? number_format($transaction['amount']) . ' VND' : 'N/A') . "</td>";
                echo "<td style='padding: 8px;'>" . (isset($transaction['description']) ? htmlspecialchars($transaction['description']) : 'N/A') . "</td>";
                echo "<td style='padding: 8px;'>" . ($username ? htmlspecialchars($username) : 'Không xác định') . "</td>";
                echo "</tr>";
            }
            echo "</table>";
        }
    }
    
    echo "<hr>";
    
    // Test xử lý giao dịch
    echo "<h3>3. Test Xử Lý Giao Dịch</h3>";
    echo "<p><em>Chạy xử lý giao dịch donate...</em></p>";
    
    $processedCount = $mbBank->processDonateTransactions();
    echo "<p style='color: green;'><strong>Đã xử lý: {$processedCount} giao dịch</strong></p>";
    
} catch (Exception $e) {
    echo "<p style='color: red;'><strong>Lỗi:</strong> " . $e->getMessage() . "</p>";
}

echo "<hr>";
echo "<p><strong>Cách sử dụng:</strong></p>";
echo "<ul>";
echo "<li>Test API: <code>?key={$Settings['Username']}</code></li>";
echo "<li>Chạy cron tự động: <code>/Api/Bank/CronMBBank.php?key={$Settings['Username']}</code></li>";
echo "<li>Thiết lập cron job: <code>*/5 * * * * curl \"http://yourdomain.com/Api/Bank/CronMBBank.php?key={$Settings['Username']}\"</code></li>";
echo "</ul>";
?>
