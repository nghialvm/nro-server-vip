<?php
require_once '../Controllers/Configs.php';
header('Content-Type: application/json; charset=utf-8');

$Login = $_SESSION['ImSynZx_Login'] ?? false;
if (!$Login) {
    http_response_code(401);
    echo json_encode(['success' => false, 'message' => 'Bạn chưa đăng nhập']);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Phương thức không được hỗ trợ']);
    exit;
}

if (!isset($IHero) || !isset($IHero['name'])) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Lỗi hệ thống: Không có dữ liệu hero']);
    exit;
}

$name = trim($_POST['name'] ?? '');
if (!$name || $name !== $IHero['name']) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Tên player không hợp lệ']);
    exit;
}

if (!preg_match('/^[\w\s\-]+$/u', $name)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Tên player chứa ký tự không hợp lệ']);
    exit;
}

$host = getenv('GAME_ADMIN_HOST') ?: '127.0.0.1';
$port = (int) (getenv('GAME_ADMIN_PORT') ?: 4424);

$data = [
    'action' => 'unstuck',
    'name' => $name
];

$fp = @fsockopen($host, $port, $errno, $errstr, 5);
if (!$fp) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => "Không kết nối được tới Java server: $errstr ($errno)"]);
    exit;
}

stream_set_timeout($fp, 3);

fwrite($fp, json_encode($data) . "\n");
$response = fgets($fp, 1024);
$info = stream_get_meta_data($fp);
fclose($fp);

if ($response === false || $info['timed_out']) {
    http_response_code(504);
    echo json_encode(['success' => false, 'message' => 'Không nhận được phản hồi từ server hoặc quá thời gian chờ']);
    exit;
}

$respData = json_decode(trim($response), true);
if (json_last_error() === JSON_ERROR_NONE && is_array($respData)) {
    echo json_encode(['success' => true, 'message' => $respData['message'] ?? 'Thao tác thành công', 'data' => $respData]);
} else {
    echo json_encode(['success' => true, 'message' => trim($response)]);
}
