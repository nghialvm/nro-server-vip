<?php
require_once(__DIR__ . '/Controllers/Configs.php');
require_once(__DIR__ . '/Controllers/Header.php');

if (!$Login || !$ImS) {
    echo "<p style='color:red;text-align:center'>⚠️ Vui lòng đăng nhập để sử dụng chức năng này</p>";
    require_once(__DIR__ . '/Controllers/Footer.php');
    exit();
}

$user_id = (int)$ImS['id'];
$commission_per_ref = 10000;

// Tổng số người được mời
$stmt = $Connect->prepare("SELECT COUNT(*) FROM account WHERE ref_id = :uid");
$stmt->execute(['uid' => $user_id]);
$total_ref = (int)$stmt->fetchColumn();

// Người đã kích hoạt (nạp >= 30k)
$stmt = $Connect->prepare("SELECT COUNT(*) FROM account WHERE ref_id = :uid AND tongnap >= 30000");
$stmt->execute(['uid' => $user_id]);
$act_ref = (int)$stmt->fetchColumn();

// Lấy thông tin tài khoản
$stmt = $Connect->prepare("SELECT commission_claimed, vnd FROM account WHERE id = :uid");
$stmt->execute(['uid' => $user_id]);
$info = $stmt->fetch(PDO::FETCH_ASSOC);
$commission_claimed = (int)($info['commission_claimed'] ?? 0);
$current_vnd = (int)($info['vnd'] ?? 0);

// Tính hoa hồng
$commission_total = $act_ref * $commission_per_ref;
$commission_pending = max(0, $commission_total - $commission_claimed);

// Cộng hoa hồng tự động nếu có
// Cộng hoa hồng tự động nếu có
if ($commission_pending > 0) {
    $stmt = $Connect->prepare("
        UPDATE account 
        SET vnd = vnd + :bonus1, 
            commission_claimed = commission_claimed + :bonus2
        WHERE id = :uid
    ");
    $stmt->execute([
        'bonus1' => $commission_pending,
        'bonus2' => $commission_pending,
        'uid'    => $user_id
    ]);

    $current_vnd += $commission_pending;
    $commission_claimed += $commission_pending;
    $commission_pending = 0;
}


$link = "https://ngocrongluaga.com/Auth/Lor?ref={$user_id}#register";


?>

<main style="font-family:'Tahoma',sans-serif;color:#4b1e00;text-align:center;margin-top:10px;">
  <div style="
      background-color:#ffcc66;
      border:2px solid #d37a00;
      border-radius:10px;
      box-shadow:0 3px 6px rgba(0,0,0,0.4);
      max-width:700px;
      margin:0 auto;
      padding:15px 20px;
  ">
    <h2 style="color:#b32d00;text-shadow:1px 1px #fff;margin-bottom:5px;">
      🌟 Thông tin giới thiệu 🌟
    </h2>
    <hr style="border:1px solid #d37a00;width:80%;">

    <div style="font-size:15px;line-height:1.8;text-align:left;max-width:400px;margin:0 auto;">
      <p>👥 Số người bạn đã mời: <b><?= $total_ref ?></b></p>
      <p>💫 Số người đã kích hoạt: <b><?= $act_ref ?></b></p>
      <p>💎 Tổng hoa hồng đã nhận: <b><?= number_format($commission_claimed) ?> VNĐ</b></p>
      <p>🕒 Hoa hồng đang chờ: <b><?= number_format($commission_pending) ?> VNĐ</b></p>
      <p>💰 Số dư hiện tại: <b style="color:#c40000;"><?= number_format($current_vnd) ?> VNĐ</b></p>
    </div>

    <hr style="border:1px solid #d37a00;width:80%;">
    <h3 style="color:#b32d00;margin-top:10px;">🎁 Giới thiệu người chơi 🎁</h3>
    <p style="color:#4b1e00;font-size:14px;line-height:1.6;">
      Khi người chơi đăng ký qua link của bạn và nạp đạt 
      <b>30.000 VNĐ</b>, bạn sẽ nhận ngay <b>10.000 VNĐ</b> hoa hồng.<br>
      Giới thiệu càng nhiều, phần thưởng càng lớn!
    </p>

    <div style="
        margin-top:15px;
        background:#ffe5b4;
        border:1px solid #d37a00;
        border-radius:8px;
        padding:10px;
        box-shadow:inset 0 0 6px rgba(0,0,0,0.2);
    ">
      <p><b>🔗 Link giới thiệu của bạn:</b></p>
      <input type="text" value="<?= htmlspecialchars($link) ?>" id="refLink"
             style="width:90%;padding:8px;border-radius:5px;border:1px solid #a85c00;
                    text-align:center;background:#fff8e1;color:#4b1e00;font-weight:bold;" readonly>
      <br>
      <button onclick="copyRef()" 
              style="margin-top:10px;background:#ff9900;color:#fff;
                     border:1px solid #b25b00;padding:6px 18px;
                     border-radius:5px;font-weight:bold;
                     box-shadow:0 2px 3px rgba(0,0,0,0.3);
                     cursor:pointer;transition:0.3s;">
        📋 Sao chép link
      </button>
    </div>
  </div>
</main>

<script>
function copyRef(){
  var c=document.getElementById("refLink");
  c.select();c.setSelectionRange(0,99999);
  document.execCommand("copy");
  alert("✅ Đã sao chép link giới thiệu của bạn!");
}
</script>


<script>
function copyRef(){
  var c=document.getElementById("refLink");
  c.select();
  c.setSelectionRange(0,99999);
  document.execCommand("copy");
  alert("✅ Đã copy link giới thiệu của bạn!");
}
</script>

<?php require_once(__DIR__ . '/Controllers/Footer.php'); ?>
