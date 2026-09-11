<?php
require_once('core/config.php');
require_once('core/head.php');

$user_id = $_SESSION['account']['id']; // ID người chơi

// Tổng số ref
$sql = "SELECT COUNT(*) AS total_ref FROM account WHERE ref_id = '$user_id'";
$res = $config->query($sql);
$total_ref = ($res && $res->num_rows > 0) ? $res->fetch_assoc()['total_ref'] : 0;

// Ref đã kích hoạt
$sql2 = "SELECT COUNT(*) AS act_ref FROM account WHERE ref_id = '$user_id' AND tongnap >= 30000";
$res2 = $config->query($sql2);
$act_ref = ($res2 && $res2->num_rows > 0) ? $res2->fetch_assoc()['act_ref'] : 0;

// Đã claim
$sql3 = "SELECT commission_claimed, vnd FROM account WHERE id = '$user_id'";
$res3 = $config->query($sql3);
$row3 = $res3->fetch_assoc();
$commission_claimed = $row3 ? intval($row3['commission_claimed']) : 0;
$current_vnd = $row3 ? intval($row3['vnd']) : 0;

// Tổng hoa hồng (dựa trên số ref đã kích hoạt)
$commission_total = $act_ref * 10000;

// Hoa hồng còn lại
$commission_pending = $commission_total - $commission_claimed;

// Nếu có hoa hồng pending thì cộng tự động
if ($commission_pending > 0) {
    $config->query("UPDATE account 
                    SET vnd = vnd + $commission_pending, 
                        commission_claimed = commission_claimed + $commission_pending 
                    WHERE id = '$user_id'");
    $current_vnd += $commission_pending;
    $commission_claimed += $commission_pending;
    $commission_pending = 0; // đã cộng hết
}

$link = "http://ngocrongluga.com/Register?ref=" . $user_id;
?>
<main>
    <div class="p-1 mt-1 ibox-content"
        style="background-color: rgb(134,0,252); border-radius: 7px; box-shadow: 0px 2px 5px black;">
        <div class="p-1 text-white">
            <h5 class="h3 mb-3 font-weight-normal text-white"
                style="text-align:center;padding-top: 3px; font-weight: bold; text-shadow: 2px 2px 2px #000;">
                Thông tin giới thiệu
            </h5>

            <div class="p-2" style="font-size: 15px;">
                <p>Số người bạn đã giới thiệu: <b><?php echo $total_ref; ?></b></p>
                <p>Số người đã kích hoạt tài khoản: <b><?php echo $act_ref; ?></b></p>
                <p>Tổng hoa hồng đã nhận: <b><?php echo number_format($commission); ?></b></p>
            </div>

            <hr style="border-color: #fff;">

            <h5 style="text-align:center; font-weight:bold;">Giới thiệu người chơi</h5>
            <p>Khi người chơi mới đăng ký tài khoản qua link giới thiệu của bạn và có tổng nạp đạt 
               <b>30.000</b>, bạn sẽ được phần quà <b>10.000</b> vào tài khoản.</p>
            <p>Giới thiệu càng nhiều, phần quà càng lớn.</p>

            <div style="margin-top: 10px; padding: 10px; background:#222; border-radius: 8px;">
                <p><b>Link giới thiệu của bạn:</b></p>
                <input type="text" value="<?php echo $link; ?>" id="refLink" 
                       style="width: 100%; padding: 8px; border-radius: 5px;" readonly>
                <button onclick="copyRef()" class="btn btn-action text-white mt-2" 
                        style="border-radius: 7px;">Copy link</button>
            </div>
        </div>
    </div>
</main>

<script>
function copyRef() {
    var copyText = document.getElementById("refLink");
    copyText.select();
    copyText.setSelectionRange(0, 99999); 
    document.execCommand("copy");
    alert("Đã copy link giới thiệu!");
}
</script>

<?php require_once('core/end.php'); ?>
