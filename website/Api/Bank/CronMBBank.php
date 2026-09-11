<?php
/**
 * Cron Job để tự động kiểm tra và xử lý giao dịch MBBank
 * Chạy file này định kỳ để tự động nạp tiền từ giao dịch donate
 */

date_default_timezone_set('Asia/Ho_Chi_Minh');

// Include các file cần thiết
require_once '../../Controllers/Configs.php';
require_once 'MBBank.php';

// Cấu hình API Key MBBank
$MBBANK_API_KEY = '2603b3626360f20f2440fddce90d0161';

// Kiểm tra quyền truy cập (bảo mật)
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
    
    // Log thời gian bắt đầu
    $startTime = date('Y-m-d H:i:s');
    error_log("=== Bắt đầu cron MBBank tại: {$startTime} ===");
    
    // Xử lý các giao dịch donate
    $processedCount = $mbBank->processDonateTransactions();
    
    // Log kết quả
    $endTime = date('Y-m-d H:i:s');
    error_log("=== Hoàn thành cron MBBank tại: {$endTime} - Đã xử lý: {$processedCount} giao dịch ===");
    
    // Trả về kết quả
    echo json_encode([
        'status' => 'success',
        'message' => "Đã xử lý thành công {$processedCount} giao dịch donate",
        'processed_count' => $processedCount,
        'start_time' => $startTime,
        'end_time' => $endTime
    ]);

} catch (Exception $e) {
    // Log lỗi
    error_log("=== Lỗi cron MBBank: " . $e->getMessage() . " ===");
    
    // Trả về lỗi
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Lỗi xử lý: ' . $e->getMessage()
    ]);
}
