<?php
require_once '../Controllers/Configs.php';
include '../Controllers/Header.php';
if (session_status() === PHP_SESSION_NONE) session_start();

// Chỉ admin
if (empty($ImS['isFounder']) || $ImS['isFounder'] != 1) {
    header('Location: /Forum');
    exit;
}

// ===== HELPER =====
function getCount($sql, $params = []) {
    global $Connect;
    $stmt = $Connect->prepare($sql);
    $stmt->execute($params);
    return (int)$stmt->fetchColumn();
}

// ===== STATS =====
$qThe = "SELECT SUM(amount) AS total_the FROM napthe WHERE status = 1";
$rowThe = $Connect->query($qThe)->fetch(PDO::FETCH_ASSOC) ?: ['total_the' => 0];

$qAtm = "SELECT SUM(amount) AS atm FROM payments WHERE status = 1";
$rowAtm = $Connect->query($qAtm)->fetch(PDO::FETCH_ASSOC) ?: ['atm' => 0];

// ✅ Thêm bảng VP_BANK
$qVPBank = "SELECT SUM(amount) AS vpbank FROM vp_bank WHERE status = 1";
$rowVPBank = $Connect->query($qVPBank)->fetch(PDO::FETCH_ASSOC) ?: ['vpbank' => 0];

$qTheToday = "SELECT SUM(amount) AS total FROM napthe WHERE status = 1 AND DATE(created_at) = CURDATE()";
$rowTheToday = $Connect->query($qTheToday)->fetch(PDO::FETCH_ASSOC) ?: ['total' => 0];

$qAtmToday = "SELECT SUM(amount) AS total FROM payments WHERE status = 1 AND DATE(date) = CURDATE()";
$rowAtmToday = $Connect->query($qAtmToday)->fetch(PDO::FETCH_ASSOC) ?: ['total' => 0];

// ✅ Doanh thu VPBANK hôm nay
$qVPBankToday = "SELECT SUM(amount) AS total FROM vp_bank WHERE status = 1 AND DATE(created_at) = CURDATE()";
$rowVPBankToday = $Connect->query($qVPBankToday)->fetch(PDO::FETCH_ASSOC) ?: ['total' => 0];

// ===== TÍNH TỔNG =====
$the80        = (float)($rowThe['total_the'] ?? 0) * 0.8;
$atm          = (float)($rowAtm['atm'] ?? 0);
$vpbank       = (float)($rowVPBank['vpbank'] ?? 0);
$tongdoanhthu = $the80 + $atm + $vpbank;

$theToday80   = (float)($rowTheToday['total'] ?? 0) * 0.8;
$atmToday     = (float)($rowAtmToday['total'] ?? 0);
$vpbankToday  = (float)($rowVPBankToday['total'] ?? 0);
$doanhThuNgay = $theToday80 + $atmToday + $vpbankToday;

$stats = [
    'total_accounts' => getCount("SELECT COUNT(*) FROM account"),
    'violated'       => getCount("SELECT COUNT(*) FROM account WHERE ban = 1"),
    'active_users'   => getCount("SELECT COUNT(*) FROM account WHERE active = 1 AND isFounder = 0"),
];

// ===== DANH SÁCH NẠP HÔM NAY =====
$listNap = [];

// // Lấy từ napthe
// $stmt = $Connect->query("SELECT user_nap AS username, amount FROM napthe WHERE status = 1 AND DATE(created_at) = CURDATE()");
// $listNap = array_merge($listNap, $stmt->fetchAll(PDO::FETCH_ASSOC));



// Lấy từ vp_bank
$stmt = $Connect->query("SELECT username, amount FROM vp_bank WHERE status = 1 AND DATE(created_at) = CURDATE()");
$listNap = array_merge($listNap, $stmt->fetchAll(PDO::FETCH_ASSOC));

// Gom lại & sắp xếp theo số tiền giảm dần
usort($listNap, function($a, $b){ return $b['amount'] <=> $a['amount']; });

// ===== ITEM COMBOBOX =====
$itemTemplates = [];
$stmt = $Connect->query("SELECT id, name FROM item_template ORDER BY id ASC");
while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
    $itemTemplates[] = $row;
}

// ===== HANDLE FORM =====
$msg = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user = strtolower(trim($_POST['checktk'] ?? ''));
    $money = (int)($_POST['money'] ?? 0);

    if (isset($_POST['tang']) && $user && $money > 0) {
        $stmt = $Connect->prepare("UPDATE account SET vnd = vnd + :m1, danap = danap + :m2 WHERE username = :u");
        $stmt->execute([':m1' => $money, ':m2' => $money, ':u' => $user]);
        $msg = "✅ Cộng {$money} VNĐ cho {$user} thành công!";
    }
    if (isset($_POST['giam']) && $user && $money > 0) {
        $stmt = $Connect->prepare("UPDATE account SET vnd = GREATEST(vnd - :m,0) WHERE username = :u");
        $stmt->execute([':m' => $money, ':u' => $user]);
        $msg = "⚠️ Trừ {$money} VNĐ của {$user} thành công!";
    }
    if (isset($_POST['khoa']) && $user) {
        $Connect->prepare("UPDATE account SET ban = 1 WHERE username = :u")->execute([':u' => $user]);
        $msg = "🔒 Đã KHÓA {$user}";
    }
    if (isset($_POST['mokhoa']) && $user) {
        $Connect->prepare("UPDATE account SET ban = 0 WHERE username = :u")->execute([':u' => $user]);
        $msg = "🔓 Đã MỞ KHÓA {$user}";
    }
    if (isset($_POST['mtv']) && $user) {
        $Connect->prepare("UPDATE account SET active = 1 WHERE username = :u")->execute([':u' => $user]);
        $msg = "👑 Đã mở thành viên cho {$user}";
    }
    if (isset($_POST['khoaip'])) {
        $ip = trim($_POST['ip'] ?? '');
        if ($ip) {
            $Connect->prepare("UPDATE account SET ban = 1 WHERE ip_address = :ip")->execute([':ip' => $ip]);
            $msg = "🚫 Đã KHÓA tất cả tài khoản thuộc IP {$ip}";
        }
    }
    if (isset($_POST['khoatatca'])) {
        $sql = "
            UPDATE account SET ban = 1
            WHERE ip_address IN (
                SELECT ip FROM (
                    SELECT ip_address AS ip FROM account
                    WHERE ip_address <> '127.0.0.1'
                    GROUP BY ip_address HAVING COUNT(*) > 5
                ) t
            )
        ";
        $Connect->exec($sql);
        $msg = "🚫 Đã KHÓA tất cả clone >5 acc/IP!";
    }
}
?>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Admin Dashboard</title>
<style>
body {
    font-family: Arial, sans-serif;
    background:#111 url('/Uploads/bg.jpg') no-repeat center center fixed;
    background-size:cover; color:#eee; margin:0; padding:0;
}
.container { max-width:1100px; margin:20px auto; padding:15px; }
h2 { text-align:center; color:#ffcc00; margin-bottom:20px; text-shadow:1px 1px 3px #000; }
.box {
    background:rgba(0,0,0,0.7); border:1px solid #444;
    border-radius:8px; padding:15px; margin-bottom:20px;
    box-shadow:0 0 10px rgba(255,255,255,0.1);
}
.box h3 { margin-top:0; color:#ff9933; border-bottom:1px solid #333; padding-bottom:5px; }
label { display:block; margin-bottom:6px; font-weight:bold; color:#ffcc66; }
input[type=text], input[type=number] {
    width:100%; padding:8px; margin-bottom:10px;
    border:1px solid #666; border-radius:4px; background:#222; color:#fff;
}
button {
    padding:8px 14px; border:none; border-radius:4px; cursor:pointer;
    font-weight:bold; transition:0.25s;
}
button[name=tang]{background:#28a745;color:#fff;}
button[name=giam]{background:#dc3545;color:#fff;}
button[name=mtv]{background:#007bff;color:#fff;}
button[name=khoa],button[name=khoaip],button[name=khoatatca]{background:#ff3300;color:#fff;}
button[name=mokhoa]{background:#00cc66;color:#fff;}
button:hover{opacity:0.85;}
.table { width:100%; border-collapse:collapse; margin-top:10px; }
.table th,.table td{ padding:8px; border:1px solid #444; text-align:center; }
.table th{ background:#222; color:#ffcc00; }
.table tbody tr:nth-child(even){ background:#1a1a1a; }
.msg{ padding:10px; margin-bottom:15px; border-radius:5px; background:#222; color:#0f0; font-weight:bold; text-align:center; }
.stat-grid{ display:grid; grid-template-columns:repeat(auto-fit,minmax(220px,1fr)); gap:15px; margin-bottom:20px; }
.stat-box{ background:rgba(255,255,255,0.05); padding:12px; border-radius:8px; text-align:center; }
.stat-box h4{margin:0;color:#ffcc00;font-size:14px;}
.stat-box p{margin:5px 0 0;font-size:20px;font-weight:bold;}
</style>
</head>
<body>
<div class="container">
    <h2>📊 Bảng Quản Trị Admin</h2>

    <?php if (!empty($msg)): ?>
    <div class="msg"><?=htmlspecialchars($msg)?></div>
    <?php endif; ?>

    <!-- Stats -->
    <div class="stat-grid">
        <div class="stat-box"><h4>Tổng tài khoản</h4><p><?=$stats['total_accounts']?></p></div>
        <div class="stat-box"><h4>Active Users</h4><p><?=$stats['active_users']?></p></div>
        <div class="stat-box"><h4>Tài khoản bị khóa</h4><p><?=$stats['violated']?></p></div>
        <div class="stat-box"><h4>Tổng Doanh Thu</h4><p><?=number_format($tongdoanhthu)?> đ</p></div>
        <div class="stat-box"><h4>Doanh Thu Hôm Nay</h4><p><?=number_format($doanhThuNgay)?> đ</p></div>
    </div>

    <!-- 💵 Danh sách nạp hôm nay -->
    <div class="box">
        <h3>💵 Danh sách người chơi nạp hôm nay</h3>
        <table class="table">
            <thead><tr><th>#</th><th>Username</th><th>Số tiền</th></tr></thead>
            <tbody>
                <?php if (empty($listNap)): ?>
                    <tr><td colspan="3">Chưa có giao dịch hôm nay</td></tr>
                <?php else: ?>
                    <?php foreach ($listNap as $i => $nap): ?>
                        <tr>
                            <td><?=$i+1?></td>
                            <td><?=htmlspecialchars($nap['username'])?></td>
                            <td><?=number_format($nap['amount'])?> đ</td>
                        </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>

    <!-- Các phần bên dưới giữ nguyên -->
    <!-- Buff VNĐ -->
    <div class="box">
        <h3>💰 Buff VNĐ</h3>
        <form method="post">
            <label>Tên tài khoản</label>
            <input type="text" name="checktk" required>
            <label>Số VNĐ</label>
            <input type="number" name="money" required>
            <button type="submit" name="tang">+ VNĐ</button>
            <button type="submit" name="giam">- VNĐ</button>
        </form>
    </div>

    <!-- Mở thành viên -->
    <div class="box">
        <h3>👑 Mở Thành viên</h3>
        <form method="post">
            <label>Tên tài khoản</label>
            <input type="text" name="checktk" required>
            <button type="submit" name="mtv">Mở Thành viên</button>
        </form>
    </div>

    <!-- Khóa / Mở khóa -->
    <div class="box">
        <h3>🔒 Khóa / Mở khóa tài khoản</h3>
        <form method="post">
            <label>Tên tài khoản</label>
            <input type="text" name="checktk" required>
            <button type="submit" name="khoa">Khóa</button>
            <button type="submit" name="mokhoa">Mở khóa</button>
        </form>
    </div>

    <!-- Khóa theo IP -->
    <div class="box">
        <h3>🌐 Khóa theo IP</h3>
        <form method="post">
            <label>Địa chỉ IP</label>
            <input type="text" name="ip" required>
            <button type="submit" name="khoaip">Khóa IP</button>
        </form>
    </div>

    <!-- Khóa clone -->
    <div class="box">
        <h3>🚫 Khóa Clone (>5 acc / IP)</h3>
        <form method="post">
            <button type="submit" name="khoatatca">Khóa tất cả Clone</button>
        </form>
    </div>
</div>
</body>
</html>
