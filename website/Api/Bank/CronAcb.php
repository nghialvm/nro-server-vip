<?php
date_default_timezone_set('Asia/Ho_Chi_Minh');

include 'ACB.php';
include '../../Controllers/Configs.php';

$ConnectS1 = connectDatabase($servers[1]);
$ConnectS2 = connectDatabase($servers[2]);

$rows = 20;
$token_account = getenv('ACB_TOKEN_FILE') ?: (sys_get_temp_dir() . DIRECTORY_SEPARATOR . 'awn-acb-token.txt');
if (!isset($_GET['users']) || $_GET['users'] !== $Settings['Username']) {
    exit('Không tìm thấy key! Không thể truy cập.');
}

$timehientai = time();
$token = getenv('ACB_TOKEN') ?: null;
if (!$token && file_exists($token_account)) {
    $token = trim(file_get_contents($token_account));
}
if (!$token) {
    exit(json_encode(array('status' => '1', 'msg' => 'Chưa cấu hình ACB_TOKEN hoặc ACB_TOKEN_FILE')));
}
$account = $Settings['NumberBank'];
$acb = new ACB($Settings['Username'], $Settings['Password']);
$acb->clientId = "iuSuHYVufIUuNIREV0FB9EoLn9kHsDbm";
$balance = $acb->get_balance($token);
$balance_data = json_decode($balance, true);
if (isset($balance_data['data'][0]['balance'])) {
    $balance_amount = $balance_data['data'][0]['balance'];
    echo "<p>Status: Online</p>";
}

function getTransactionHistory($acb, $account, $rows, $token)
{
    global $token_account;
    $login = $acb->login();
    if (isset($login["errorCode"])) {
        exit(json_encode(array('status' => '1', 'msg' => 'Thông tin đăng nhập chưa đúng!!!')));
    }

    $synzx = $login['accessToken'];
    file_put_contents($token_account, $synzx);
    $token = $synzx;
    $lsgd = $acb->LSGD($account, $rows, $token);
    $transactions = json_decode($lsgd, true);
    if (isset($transactions['messageStatus']) && $transactions['messageStatus'] == 'success') {
        foreach ($transactions['data'] as $transaction) {
            // Check description
            if (stripos($transaction['description'], 's1') !== false) {
                saveToDatabase($transaction, 1);
            } elseif (stripos($transaction['description'], 's2') !== false) {
                saveToDatabase($transaction, 2);
            }
        }
    } else {
        getTransactionHistory($acb, $account, $rows, $token);
    }
}

function saveToDatabase($txn, $server)
{
    global $ConnectS1, $ConnectS2;
    $Connect = ($server == 1) ? $ConnectS1 : $ConnectS2;

    preg_match('/s[12]\s+(\d+)/', $txn['description'], $matches);
    $user_nap = isset($matches[1]) ? trim($matches[1]) : '';

    try {
        $Connect->beginTransaction();
        $stmt_chk = $Connect->prepare("SELECT COUNT(*) AS c FROM payments WHERE refNo = ?");
        $stmt_chk->execute([$txn['transactionNumber']]);
        $row = $stmt_chk->fetch(PDO::FETCH_ASSOC);

        if ($row['c'] == 0 && $txn['amount'] >= 3000) {
            $postingDate = date('Y-m-d H:i:s', $txn['postingDate'] / 1000);
            $stmt_ins = $Connect->prepare("INSERT INTO payments (refNo, name, amount, status, date) VALUES (?, ?, ?, ?, ?)");
            $stmt_ins->execute([$txn['transactionNumber'], $user_nap, $txn['amount'], 1, $postingDate]);
            $stmt_upd = $Connect->prepare("UPDATE account SET sotien = sotien + ?, danap = danap + ? WHERE id = ?");
            $stmt_upd->execute([$txn['amount'], $txn['amount'], $user_nap]);

            $Connect->commit();
            echo '<strong>Done (Server '.$server.')</strong><br>';
        }
    } catch (PDOException $e) {
        $Connect->rollback();
        echo "Lỗi (Server $server): " . $e->getMessage();
    }
}

getTransactionHistory($acb, $account, $rows, $token);
