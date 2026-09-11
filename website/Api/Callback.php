<?php
// Api/Callback.php
date_default_timezone_set('Asia/Ho_Chi_Minh');

/* ================= LOAD ENV ================= */
$envFile = __DIR__ . '/../.env';
if (!file_exists($envFile)) {
    http_response_code(500);
    exit('ENV_NOT_FOUND');
}
foreach (file($envFile) as $line) {
    $line = trim($line);
    if ($line === '' || $line[0] === '#' || strpos($line, '=') === false) continue;
    [$k, $v] = explode('=', $line, 2);
    putenv("$k=$v");
}

/* ================= SERVER TYPE ================= */
$serverType = $_GET['type'] ?? 's1';
if (!in_array($serverType, ['s1', 's2'], true)) {
    exit('INVALID_SERVER');
}

$cfg = ($serverType === 's1') ? [
    'host' => getenv('DB_HOST'),
    'name' => getenv('DB_NAME'),
    'user' => getenv('DB_USER'),
    'pass' => getenv('DB_PASS'),
    'partner_key' => getenv('PARTNER_KEY_S1')
] : [
    'host' => getenv('DB_HOST2'),
    'name' => getenv('DB_NAME2'),
    'user' => getenv('DB_USER2'),
    'pass' => getenv('DB_PASS2'),
    'partner_key' => getenv('PARTNER_KEY_S2')
];

/* ================= CONNECT DB ================= */
try {
    $pdo = new PDO(
        "mysql:host={$cfg['host']};dbname={$cfg['name']};charset=utf8mb4",
        $cfg['user'],
        $cfg['pass'],
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false
        ]
    );
} catch (Throwable $e) {
    http_response_code(500);
    exit('DB_ERROR');
}

/* ================= LOG ================= */
$logFile = __DIR__ . '/callback_log.txt';
file_put_contents($logFile, "==== ".date('Y-m-d H:i:s')." (server $serverType) ====\n", FILE_APPEND);
file_put_contents($logFile, "GET:\n".print_r($_GET, true), FILE_APPEND);

/* ================= NHẬN CALLBACK (GET) ================= */
$data = $_GET;
if (empty($data)) {
    exit('NO_DATA');
}

/* ================= MAP FIELD ================= */
$partnerKey = $cfg['partner_key'];

$status     = (int)($data['status'] ?? 0);        // 99 | 1 | 2 | 3 | 0
$code       = trim($data['code'] ?? '');
$serial     = trim($data['serial'] ?? '');
$amount     = (int)($data['amount'] ?? 0);        // 👉 tiền thực nhận (8900)
$cardValue  = (int)($data['card_value']
              ?? $data['value']
              ?? $data['declared_value']
              ?? 0);                              // 👉 mệnh giá thẻ (10000)
$sign       = trim($data['callback_sign'] ?? '');
$requestId = trim($data['request_id'] ?? '');

/* ================= VALIDATE ================= */
if ($code === '' || $serial === '' || $cardValue <= 0) {
    exit('INVALID_PARAMS');
}

/* ================= CHECK SIGN (CHUẨN DOITHE1S) =================
   callback_sign = md5(code + serial + card_value + partner_key)
*/
$expectSign = md5($code . $serial . $cardValue . $partnerKey);

if ($sign !== $expectSign) {
    file_put_contents(
        $logFile,
        "SIGN_FAIL expect=$expectSign got=$sign\n",
        FILE_APPEND
    );
    exit('SIGN_INVALID');
}

/* ================= XỬ LÝ DB ================= */
try {
    $pdo->beginTransaction();

    // Khóa dòng chống cộng trùng
    $st = $pdo->prepare(
        "SELECT * FROM napthe WHERE code = :code AND serial = :serial FOR UPDATE"
    );
    $st->execute([
        ':code'   => $code,
        ':serial' => $serial
    ]);

    $row = $st->fetch();
    if (!$row) {
        throw new Exception('CARD_NOT_FOUND');
    }

    // Nếu đã hoàn thành rồi thì bỏ qua
    if ((int)$row['status'] === 1) {
        $pdo->commit();
        echo 'OK';
        exit;
    }

    // Update trạng thái
    $up = $pdo->prepare(
        "UPDATE napthe
         SET status = :status, request_id = :rid
         WHERE code = :code AND serial = :serial"
    );
    $up->execute([
        ':status' => $status,
        ':rid'    => $requestId,
        ':code'   => $code,
        ':serial' => $serial
    ]);

    // CỘNG TIỀN THEO amount (8900)
    if ($status === 1 && $amount > 0) {
        $user = $row['user_nap'];

        $acc = $pdo->prepare(
            "UPDATE account
             SET vnd = vnd + :m,
                 danap = danap + :m,
                 tongnap = tongnap + :m,
                 coin = IFNULL(coin,0) + :m
             WHERE username = :u"
        );
        $acc->execute([
            ':m' => $amount,
            ':u' => $user
        ]);
    }

    $pdo->commit();
    echo 'OK';
} catch (Throwable $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    file_put_contents($logFile, "ERROR: ".$e->getMessage()."\n", FILE_APPEND);
    http_response_code(500);
    echo 'ERROR';
}
