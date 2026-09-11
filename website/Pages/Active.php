<?php
ob_start();
include '../Controllers/Header.php';
$username = $_SESSION['ImSynZx_Login'];

$bonusMultiply = 1.0; // ⚙️ Hệ số khuyến mãi toàn server (x2, x3...)

$stmt = $Connect->prepare("SELECT vnd, thoi_vang FROM account WHERE username = ?");
$stmt->execute([$username]);
$userData = $stmt->fetch(PDO::FETCH_ASSOC);

$vndBalance = (int)($userData['vnd'] ?? 0);
$thoiVang = (int)($userData['thoi_vang'] ?? 0);
$currentTime = date('Y-m-d H:i:s');

// ===== Xử lý quy đổi (POST) =====
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['exchange_vnd'])) {
    $amount = (int)$_POST['exchange_vnd'];
    $message = '';

    if ($amount < 10000) {
        $message = "Số tiền tối thiểu là 10.000₫";
    } elseif ($vndBalance < $amount) {
        $message = "Không đủ số dư để quy đổi!";
    } else {
        if ($amount < 100000) $rate = 1.2;
        elseif ($amount < 200000) $rate = 1.5;
        elseif ($amount < 500000) $rate = 2.0;
        elseif ($amount < 1000000) $rate = 2.2;
        else $rate = 2.3;

        $finalRate = $rate * $bonusMultiply;
        $goldPer1000 = $finalRate;
        $goldReceive = ($amount / 1000) * $goldPer1000;

        $update = $Connect->prepare("UPDATE account SET vnd = vnd - ?, thoi_vang = thoi_vang + ? WHERE username = ?");
        $update->execute([$amount, $goldReceive, $username]);

        $log = $Connect->prepare("INSERT INTO history_exchange (username, amount_vnd, gold_received, rate, created_at) VALUES (?, ?, ?, ?, NOW())");
        $log->execute([$username, $amount, $goldReceive, $finalRate]);

        $vndBalance -= $amount;
        $thoiVang += $goldReceive;

        // ✅ Lưu thông báo vào session để hiển thị sau redirect
        $_SESSION['exchange_message'] = "Quy đổi " . number_format($amount) . "₫ thành công! Nhận được " . number_format($goldReceive) . " Thỏi vàng (tỷ lệ x" . number_format($finalRate, 2) . ").";

        // ✅ Redirect để tránh resubmit khi reload
        header("Location: " . $_SERVER['REQUEST_URI']);
        exit;
    }

    // Nếu có lỗi thì hiển thị ngay, không redirect
    $_SESSION['exchange_message'] = $message;
    header("Location: " . $_SERVER['REQUEST_URI']);
    exit;
}

// ===== Lấy message sau redirect (nếu có) =====
$message = '';
if (isset($_SESSION['exchange_message'])) {
    $message = $_SESSION['exchange_message'];
    unset($_SESSION['exchange_message']);
}
?>

<style>
body {
    font-family: 'Segoe UI', sans-serif;
    background-color: #fff8e1;
    color: #333;
    display: flex;
    justify-content: center;
    align-items: flex-start;
    min-height: 100vh;
    padding: 40px 10px;
}
.exchange-container {
    width: 100%;
    max-width: 850px;
    background: #fff;
    border-radius: 15px;
    box-shadow: 0 6px 20px rgba(0,0,0,0.1);
    text-align: center;
    overflow: hidden;
}
.exchange-header {
    background: linear-gradient(45deg, #ffa726, #fb8c00);
    color: #fff;
    padding: 20px 0;
    font-size: 24px;
    font-weight: bold;
    letter-spacing: 1px;
}
.exchange-body {
    padding: 30px 40px;
}
.info-card {
    background: #fff3e0;
    border-radius: 12px;
    padding: 15px;
    margin-bottom: 25px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}
.info-card strong {
    color: #e65100;
}
.table-rate {
    width: 100%;
    border-collapse: collapse;
    margin: 20px auto;
    text-align: center;
}
.table-rate th {
    background-color: #ffe0b2;
    color: #5d4037;
    padding: 10px;
    border-bottom: 2px solid #ffb74d;
}
.table-rate td {
    padding: 10px;
    border-bottom: 1px solid #ffcc80;
}
.table-rate tr:hover {
    background-color: #fffaf0;
}
.input-field {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    text-align: center;
    border: 2px solid #ffb74d;
    border-radius: 10px;
    margin-top: 10px;
    transition: 0.3s;
}
.input-field:focus {
    border-color: #fb8c00;
    box-shadow: 0 0 8px rgba(251, 140, 0, 0.4);
}
.preview-box {
    margin: 12px 0;
    font-weight: bold;
    color: #1b5e20;
    min-height: 20px;
}
.btn-submit {
    background-color: #fb8c00;
    color: #fff;
    border: none;
    border-radius: 10px;
    padding: 12px 25px;
    font-size: 17px;
    font-weight: bold;
    cursor: pointer;
    transition: 0.3s;
}
.btn-submit:hover {
    background-color: #ef6c00;
}
.promo-text {
    font-weight: bold;
    color: #e53935;
    margin: 10px 0;
}
.message {
    margin-top: 15px;
    font-size: 15px;
}
</style>

<div class="exchange-container">
    <div class="exchange-header">QUY ĐỔI THỎI VÀNG</div>
    <div class="exchange-body">

        <div class="info-card">
            <p><strong>Tài khoản:</strong> <?= htmlspecialchars($username) ?></p>
            <p><strong>Số dư VND:</strong> <span style="color:#d84315;"><?= number_format($vndBalance) ?> ₫</span></p>
            <p><strong>Thỏi vàng:</strong> <span style="color:#f57c00;"><?= number_format($thoiVang) ?></span></p>
            <p><strong>Thời gian:</strong> <?= date('d/m/Y H:i', strtotime($currentTime)) ?></p>
        </div>

        <h3 style="color:#e65100;">Tỷ lệ quy đổi (x<?= $bonusMultiply ?> khuyến mãi)</h3>

        <table class="table-rate">
            <tr>
                <th>Mốc Coin</th>
                <th>Tỷ lệ cơ bản</th>
                <th>Tỷ lệ cuối (x<?= $bonusMultiply ?>)</th>
            </tr>
            <tr><td>Dưới 100k</td><td>x1.2</td><td>x<?= 1.2 * $bonusMultiply ?></td></tr>
            <tr><td>100k–200k</td><td>x1.5</td><td>x<?= 1.5 * $bonusMultiply ?></td></tr>
            <tr><td>200k–500k</td><td>x2.0</td><td>x<?= 2.0 * $bonusMultiply ?></td></tr>
            <tr><td>500k–1000k</td><td>x2.2</td><td>x<?= 2.2 * $bonusMultiply ?></td></tr>
            <tr><td>Trên 1000k</td><td>x2.3</td><td>x<?= 2.3 * $bonusMultiply ?></td></tr>
        </table>

        <div class="promo-text">Server đang có khuyến mãi x<?= $bonusMultiply ?> lần quy đổi!</div>

        <form method="POST" style="max-width:500px; margin:0 auto;">
            <input type="number" id="vnd-input" name="exchange_vnd" min="10000" step="1000"
                   placeholder="Nhập số tiền muốn quy đổi" class="input-field" required>
            <div id="preview" class="preview-box"></div>
            <button type="submit" class="btn-submit">Quy đổi ngay</button>
        </form>

        <?php if (!empty($message)): ?>
            <div class="message" style="color:<?= strpos($message,'không đủ')!==false || strpos($message,'tối thiểu')!==false ? '#c62828' : '#2e7d32' ?>;">
                <?= htmlspecialchars($message) ?>
            </div>
        <?php endif; ?>
    </div>
</div>

<script>
const bonusMultiply = <?= $bonusMultiply ?>;
document.getElementById('vnd-input').addEventListener('input', function() {
    const amount = parseInt(this.value) || 0;
    let rate = 0;
    if (amount < 100000) rate = 1.2;
    else if (amount < 200000) rate = 1.5;
    else if (amount < 500000) rate = 2.0;
    else if (amount < 1000000) rate = 2.2;
    else rate = 2.3;

    const finalRate = rate * bonusMultiply;
    const goldPer1000 = finalRate;
    const goldReceive = (amount / 1000) * goldPer1000;

    const preview = document.getElementById('preview');
    if (amount >= 10000) {
        preview.innerHTML = `Tỷ lệ x${finalRate.toFixed(2)} → Bạn sẽ nhận <b>${goldReceive.toLocaleString()}</b> Thỏi vàng`;
    } else {
        preview.textContent = '';
    }
});
</script>

<?php include '../Controllers/Footer.php'; ?>
<?php ob_end_flush(); ?>
