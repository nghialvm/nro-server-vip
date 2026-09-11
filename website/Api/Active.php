<?php
include '../Controllers/Configs.php';
header('Content-Type: application/json');
if (empty($_SESSION['ImSynZx_Login'])) {
    echo json_encode(['success' => false, 'message' => 'Bạn chưa đăng nhập']);
    exit;
}

if (!isset($_POST['key']) || $_POST['key'] !== 'ImS-ActiveMemberKey') {
    exit;
}

$username = $_SESSION['ImSynZx_Login'];

try {
    $Connect->beginTransaction();

    $stmt = $Connect->prepare("SELECT sotien, active FROM account WHERE username = ? FOR UPDATE");
    $stmt->execute([$username]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        throw new Exception('Tài khoản không tồn tại');
    }
    if ((int)$user['active'] === 1) {
        throw new Exception('Bạn đã là thành viên');
    }
    if ((int)$user['sotien'] < 10000) {
        throw new Exception('Số dư không đủ 10,000 VND');
    }

    $newVnd = $user['sotien'] - 10000;
    $updateStmt = $Connect->prepare("UPDATE account SET sotien = ?, active = 1 WHERE username = ?");
    $updateStmt->execute([$newVnd, $username]);

    $insertStmt = $Connect->prepare("INSERT INTO history_active (username, created_at) VALUES (?, NOW())");
    $insertStmt->execute([$username]);

    $Connect->commit();
    echo json_encode(['success' => true, 'message' => 'Kích hoạt thành viên thành công']);
} catch (Exception $e) {
    $Connect->rollBack();
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
}
