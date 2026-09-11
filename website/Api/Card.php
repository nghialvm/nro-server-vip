<?php
include '../Controllers/Configs.php';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        $data = $_POST;
    }

    $telco = isset($data['cardType']) ? trim($data['cardType']) : null;
    $amount = isset($data['amount']) ? filter_var($data['amount'], FILTER_VALIDATE_FLOAT) : null;
    $serial = isset($data['cardSerial']) ? trim($data['cardSerial']) : null;
    $code = isset($data['cardPin']) ? trim($data['cardPin']) : null;
    error_log("Received data: " . print_r($data, true));
    error_log("Telco: $telco, Amount: $amount, Serial: $serial, Code: $code");
    if (!$telco || !$amount || !$serial || !$code) {
        sendResponse('error', 'Vui lòng nhập đầy đủ thông tin! Missing: ' .
            (!$telco ? 'telco ' : '') .
            (!$amount ? 'amount ' : '') .
            (!$serial ? 'serial ' : '') .
            (!$code ? 'code' : ''));
    }

    $username = $_SESSION['ImSynZx_Login'];
    $request_id = rand(100000000, 999999999);
    $dataPost = [
        'request_id' => $request_id,
        'code' => $code,
        'partner_id' => PARTNER_ID,
        'serial' => $serial,
        'telco' => $telco,
        'amount' => $amount,
        'command' => 'charging',
        'sign' => md5(PARTNER_KEY . $code . $serial)
    ];

    $ch = curl_init(API_CARD_URL . 'chargingws/v2');
    curl_setopt_array($ch, [
        CURLOPT_POST => true,
        CURLOPT_POSTFIELDS => http_build_query($dataPost),
        CURLOPT_RETURNTRANSFER => true
    ]);
    $result = curl_exec($ch);
    curl_close($ch);
    if ($result === false) {
        sendResponse('error', 'Lỗi khi gửi yêu cầu nạp thẻ!');
    }

    $obj = json_decode($result);
    if (json_last_error() !== JSON_ERROR_NONE || $obj->status != 99) {
        sendResponse('error', $obj->message ?? 'Lỗi khi xử lý yêu cầu!');
    }

    $Payments = "INSERT INTO napthe (user_nap, telco, serial, code, amount, status, request_id) 
                 VALUES (:user_nap, :telco, :serial, :code, :amount, 99, :request_id)";
    $stmt = $Connect->prepare($Payments);
    $stmt->execute([
        ':user_nap' => $username,
        ':telco' => $telco,
        ':serial' => $serial,
        ':code' => $code,
        ':amount' => $amount,
        'request_id' => $request_id
    ]);

    if ($stmt->rowCount() > 0) {
        sendResponse('success', 'Nạp thành công, vui lòng đợi máy chủ duyệt!');
    } else {
        sendResponse('error', 'Lỗi khi lưu dữ liệu vào máy chủ!');
    }
}
