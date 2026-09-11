<?php
require_once __DIR__ . '/Controllers/Configs.php';
header('Content-Type: application/json; charset=utf-8');
date_default_timezone_set('Asia/Ho_Chi_Minh');

// Nhận JSON từ SePay
$input = file_get_contents('php://input');
$data = json_decode($input);

if (!$data || !is_object($data)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Dữ liệu không hợp lệ']);
    exit;
}

$transaction_id   = $data->referenceCode ?? '';
$amount           = floatval($data->transferAmount ?? 0);
$content          = $data->content ?? '';
$transaction_date = $data->transactionDate ?? date('Y-m-d H:i:s');
$username         = null;

// Prefix xác định user
$prefix = 'ngocrongluaga';

// Tìm userId từ nội dung chuyển khoản (VD: nrodark123)
if (preg_match('/' . preg_quote($prefix, '/') . '(\d+)/i', $content, $m)) {
    $userId = intval($m[1]);
    $stmt = $Connect->prepare("SELECT username FROM account WHERE id = :id");
    $stmt->execute(['id' => $userId]);
    $row = $stmt->fetch();
    if ($row) {
        $username = $row['username'];
    }
}

if (!$username) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Không tìm thấy user hợp lệ trong nội dung chuyển khoản']);
    exit;
}

// Bỏ qua giao dịch nhỏ hơn 10k
if ($amount < 10000) {
    http_response_code(200);
    echo json_encode(['success' => true, 'message' => 'Số tiền nhỏ hơn 10.000 VNĐ, bỏ qua']);
    exit;
}

// Kiểm tra trùng giao dịch
$stmt = $Connect->prepare("SELECT id FROM vp_bank WHERE tid = :tid LIMIT 1");
$stmt->execute(['tid' => $transaction_id]);
if ($stmt->fetch()) {
    http_response_code(200);
    echo json_encode(['success' => true, 'message' => 'Giao dịch đã được xử lý']);
    exit;
}

// Lưu giao dịch
$stmt = $Connect->prepare("
    INSERT INTO vp_bank (tid, description, amount, username, status, time)
    VALUES (:tid, :description, :amount, :username, 1, :time)
");
$stmt->execute([
    'tid'         => $transaction_id,
    'description' => $content,
    'amount'      => (int)$amount,
    'username'    => $username,
    'time'        => $transaction_date
]);

// Xử lý cộng tiền (có bonus 10%)
$amountInt       = (int)round($amount);
$amountWithBonus = (int)round($amountInt * 1.10); // +10%
$coinToAdd       = $amountWithBonus; // coin = tổng nạp có bonus

try {
    $stmt = $Connect->prepare("
        UPDATE account
        SET 
            vnd = vnd + :vnd,
            tongnap = IFNULL(tongnap, 0) + :tongnap,
            danap = IFNULL(danap, 0) + :danap,
            coin = IFNULL(coin, 0) + :coin,
            active = 1
        WHERE username = :username
    ");
    $stmt->execute([
        'vnd'       => $amountWithBonus,
        'tongnap'   => $amountWithBonus,
        'danap'     => $amountWithBonus,
        'coin'      => $coinToAdd,
        'username'  => $username
    ]);
} catch (PDOException $e) {
    file_put_contents(__DIR__ . '/callback_error.log',
        date('Y-m-d H:i:s') . " | SQL Error: " . $e->getMessage() . "\n",
        FILE_APPEND);
}

// Trả kết quả về SePay
http_response_code(200);
echo json_encode([
    'success' => true,
    'message' => 'Đã xử lý giao dịch thành công',
    'username' => $username,
    'amount' => $amountWithBonus
]);
