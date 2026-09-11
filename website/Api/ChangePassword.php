<?php
require_once '../Controllers/Configs.php';

if (!isset($ImS) || !isset($ImS['username']) || !isset($ImS['password'])) {
    echo json_encode(['success' => false, 'message' => 'Không có thông tin người dùng.']);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $currentPassword = $_POST['currentPassword'] ?? '';
    $newPassword = $_POST['newPassword'] ?? '';
    $confirmPassword = $_POST['confirmPassword'] ?? '';

    if (empty($currentPassword)) {
        echo json_encode(['success' => false, 'message' => 'Mật khẩu hiện tại không được để trống.']);
        exit();
    }
    try {
        $stmt = $Connect->prepare("SELECT password FROM account WHERE username = :username");
        $stmt->bindParam(':username', $ImS['username'], PDO::PARAM_STR);
        $stmt->execute();
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user || $currentPassword !== $user['password']) {
            echo json_encode(['success' => false, 'message' => 'Mật khẩu hiện tại không đúng.']);
            exit();
        }

        if ($newPassword !== $confirmPassword || strlen($newPassword) < 6 || !preg_match('/[A-Za-z]/', $newPassword) || !preg_match('/[0-9]/', $newPassword)) {
            echo json_encode(['success' => false, 'message' => 'Mật khẩu mới phải có ít nhất 6 ký tự, bao gồm chữ và số.']);
            exit();
        }

        $stmt = $Connect->prepare("UPDATE account SET password = :newPassword WHERE username = :username");
        $stmt->bindParam(':newPassword', $newPassword, PDO::PARAM_STR);
        $stmt->bindParam(':username', $ImS['username'], PDO::PARAM_STR);
        $result = $stmt->execute();

        echo json_encode([
            'success' => $result,
            'message' => $result ? 'Đổi mật khẩu thành công.' : 'Đã có lỗi xảy ra, vui lòng thử lại.'
        ]);
    } catch (Exception $e) {
        echo json_encode(['success' => false, 'message' => 'Lỗi hệ thống.']);
    }

    exit();
}
