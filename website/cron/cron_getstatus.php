<?php
$lines = @file(__DIR__ . '/../.env');
if ($lines === false) {
    die('Không tìm thấy file cấu hình env.');
}
foreach ($lines as $line) {
    if ($line[0] !== '#' && str_contains($line, '=')) {
        list($name, $value) = explode('=', trim($line), 2);
        putenv("$name=$value");
    }
}

$serverType = $_GET['type'] ?? 's1';
$serverConfig = null;

if ($serverType === 's1') {
    $serverConfig = [
        'host' => getenv('DB_HOST'),
        'name' => getenv('DB_NAME'),
        'user' => getenv('DB_USER'),
        'pass' => getenv('DB_PASS'),
        'partner_id' => getenv('PARTNER_ID_S1'),
        'partner_key' => getenv('PARTNER_KEY_S1')
    ];
} elseif ($serverType === 's2') {
    $serverConfig = [
        'host' => getenv('DB_HOST2'),
        'name' => getenv('DB_NAME2'),
        'user' => getenv('DB_USER2'),
        'pass' => getenv('DB_PASS2'),
        'partner_id' => getenv('PARTNER_ID_S2'),
        'partner_key' => getenv('PARTNER_KEY_S2')
    ];
} else {
    die('Invalid server type');
}

// Kết nối DB
try {
    $Connect = new PDO(
        sprintf('mysql:host=%s;dbname=%s;charset=utf8', $serverConfig['host'], $serverConfig['name']),
        $serverConfig['user'],
        $serverConfig['pass'],
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false
        ]
    );
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}

// Lấy danh sách thẻ chưa xử lý
$stmt = $Connect->query("SELECT * FROM napthe WHERE status = 99 LIMIT 20");
$cards = $stmt->fetchAll();

foreach ($cards as $card) {
    $partner_id = $serverConfig['partner_id'];
    $partner_key = $serverConfig['partner_key'];
    $command = "getstatus";
    $request_id = $card['id']; // dùng id DB làm request_id
    $order_code = $card['request_id']; // lưu order_code khi gửi đi

    $sign = md5($partner_key . $partner_id . $command . $request_id);

    $payload = [
        "partner_id"  => $partner_id,
        "command"     => $command,
        "request_id"  => (string)$request_id,
        "order_code"  => $order_code,
        "sign"        => $sign
    ];

    $ch = curl_init("http://doithe-api-domain.com/api/rechargews");
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
    curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
    $response = curl_exec($ch);
    curl_close($ch);

    $res = json_decode($response, true);

    if (isset($res['status']) && $res['status'] === 'success') {
        $data = $res['data'];
        if ($data['status'] === 'success') {
            try {
                $Connect->beginTransaction();

                // update trạng thái
                $upd = $Connect->prepare("UPDATE napthe SET status = 1 WHERE id = ?");
                $upd->execute([$card['id']]);

                // cộng tiền
                $updAcc = $Connect->prepare("UPDATE account SET sotien = sotien + ?, danap = danap + ? WHERE username = ?");
                $updAcc->execute([$card['amount'], $card['amount'], $card['user_nap']]);

                $Connect->commit();
                echo "✅ Nạp thành công cho user {$card['user_nap']} (id={$card['id']})\n";
            } catch (Exception $e) {
                $Connect->rollBack();
                echo "❌ Lỗi cộng tiền: " . $e->getMessage() . "\n";
            }
        } elseif ($data['status'] === 'error') {
            // update thất bại
            $upd = $Connect->prepare("UPDATE napthe SET status = -1 WHERE id = ?");
            $upd->execute([$card['id']]);
            echo "❌ Thẻ lỗi id={$card['id']}\n";
        }
    } else {
        echo "⚠️ Không lấy được trạng thái cho id={$card['id']}\n";
    }
}
