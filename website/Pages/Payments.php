<?php
include '../Controllers/Header.php';

// Lấy username và id của người dùng hiện tại từ $ImS
$username = $ImS['username'];
$userId   = $ImS['id'];

// Phân trang cho Nạp Thẻ (lọc theo username)
$limit = 10;
$page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
$start = ($page - 1) * $limit;
$query = "SELECT * FROM napthe WHERE user_nap = '$username' ORDER BY id DESC LIMIT $start, $limit";
$result = $Connect->query($query);
$totalQuery = "SELECT COUNT(*) as total FROM napthe WHERE user_nap = '$username'";
$totalResult = $Connect->query($totalQuery);
$totalRows = $totalResult->fetch(PDO::FETCH_ASSOC)['total'];
$totalPages = ceil($totalRows / $limit);

// Phân trang cho Chuyển Khoản (lọc theo id của người dùng)
$limit2 = 10;
$page2  = isset($_GET['page2']) ? max(1, (int)$_GET['page2']) : 1;
$start2 = ($page2 - 1) * $limit2;

// (tuỳ – chỉ cần nếu server tắt emulate prepares cho LIMIT/OFFSET)
$Connect->setAttribute(PDO::ATTR_EMULATE_PREPARES, true);

// Data page
$stmt2 = $Connect->prepare("
    SELECT id, amount, status, time
    FROM vp_bank
    WHERE username = :uname
    ORDER BY id DESC
    LIMIT :start, :limit
");
$stmt2->bindValue(':uname', $username, PDO::PARAM_STR);   // $username = $ImS['username']
$stmt2->bindValue(':start', (int)$start2, PDO::PARAM_INT);
$stmt2->bindValue(':limit', (int)$limit2, PDO::PARAM_INT);
$stmt2->execute();
$result2 = $stmt2;

// Total rows
$stmtTotal2 = $Connect->prepare("
    SELECT COUNT(*) AS total
    FROM vp_bank
    WHERE username = :uname
");
$stmtTotal2->execute([':uname' => $username]);
$totalRows2  = (int)$stmtTotal2->fetch(PDO::FETCH_ASSOC)['total'];
$totalPages2 = (int)ceil($totalRows2 / $limit2);



// ====== CẤU HÌNH CỐ ĐỊNH THEO YÊU CẦU ======

// Thông tin bạn đang hiển thị
$chutaikhoan  = 'NGUYEN MINH TOAN';
$stk          = '0982542412';
$tennh        = 'TP BANK';

$MEMO_PREFIX  = 'ngocrongluaga';
$accountId    = (int)$userId;                 // ← Lấy ID từ người dùng hiện tại
$memo         = $MEMO_PREFIX . $accountId;    // VD: nrodark528

$template     = 'qr_only';                    // 'qr_only' | 'compact' | 'normal'
$amount       = null;                         // ví dụ 200000 hoặc null để bỏ

$params = [
  'acc'      => $stk,
  'bank'     => 'TPB',                     // BẮT BUỘC đúng chính tả
  'des'      => $memo,
  'template' => $template,
];
if (!empty($amount)) $params['amount'] = (int)$amount;

$qrUrl = 'https://qr.sepay.vn/img?' . http_build_query($params, '', '&', PHP_QUERY_RFC3986);


?>
<div class="body">
    <table width="100%" border="0" cellspacing="0">
        <tbody>
            <tr class="menu1">
                <td id="selected" style="width:50%; background-color: #ff5601;">Nạp Tiền Tự Động</td>
            </tr>
        </tbody>
    </table>
    <div style="text-align:center; margin-bottom: 20px;">
        <button id="btnCard" style="padding: 10px 20px; background-color: #ff5601; color: #fff; border: none; border-radius: 4px; margin-right: 10px;">Nạp Thẻ Cào Tự Động</button>
        <button id="btnTransfer" style="padding: 10px 20px; background-color: #ccc; color: #000; border: none; border-radius: 4px;">Chuyển Khoản</button>
    </div>

    <!-- Form Nạp Thẻ (Card Payment) -->
    <div id="cardSection"style="display:none;">
        <div class="box_list_chuyenmuc">
            <div class="box_midss">
                <div class="box_detai" style="padding:5px;">
                    <center>
                        <b style="color:red">Vui lòng nhập đầy đủ thông tin</b>
                        <br>
                        <div id="comment_error" style="color:red;"></div>
                    </center>
                    <form id="cardPaymentForm" method="post" action="/Api/Card">
                        <table style="width:100%; max-width:500px; margin:0 auto;">
                            <tbody>
                                <tr>
                                    <td><b>Loại Thẻ</b></td>
                                    <td>
                                        <select id="cardType" name="cardType" style="width:100%; border-radius: 4px; border: 1px solid #CCC; padding: 2px;" required>
                                            <option value="VIETTEL" selected>VIETTEL</option>
                                            <option value="VINAPHONE">VINAPHONE</option>
                                            <option value="MOBIFONE">MOBIFONE</option>
                                            <option value="VNMOBI">VNMOBI</option>
                                            <option value="VCOIN">VCOIN</option>
                                            <option value="ZING">ZING</option>
                                            <option value="GATE">GATE</option>
                                            <option value="GARENA">GARENA</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td><b>Số tiền</b></td>
                                    <td>
                                        <select id="amount" style="width:100%; border-radius: 4px; border: 1px solid #CCC; padding: 2px;" name="amount" required>
                                            <option value="">Chọn mệnh giá</option>
                                            <option value="10000">10.000</option>
                                            <option value="20000">20.000</option>
                                            <option value="30000">30.000</option>
                                            <option value="50000">50.000</option>
                                            <option value="100000">100.000</option>
                                            <option value="200000">200.000</option>
                                            <option value="300000">300.000</option>
                                            <option value="500000">500.000</option>
                                            <option value="1000000">1.000.000</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td><b>Số Seri</b></td>
                                    <td><input type="text" id="cardSerial" name="cardSerial" style="width:100%;" required></td>
                                </tr>
                                <tr>
                                    <td><b>Mã Thẻ</b></td>
                                    <td><input type="text" id="cardPin" name="cardPin" style="width:100%;" required></td>
                                </tr>
                                <tr>
                                    <td></td>
                                    <td style="text-align:center; padding-top:10px;">
                                        <input class="w3-button w3-red" type="submit" value="Xác Nhận">
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </form>
                    <br>
                    <hr>
                    <h3>Lịch Sử Nạp Thẻ</h3>
                    <table width="100%" border="1" cellspacing="0"
                        style="width: 100%; border-collapse: collapse; background-color: #fff;
                               border-radius: 10px; overflow: hidden; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);">
                        <thead>
                            <tr style="background-color: #b65d2f; color: white; font-weight: bold;">
                                <th style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;">Loại Thẻ</th>
                                <th style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;">Mệnh Giá</th>
                                <th style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;">Trạng Thái</th>
                                <th style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;">Thời Gian</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php while ($row = $result->fetch(PDO::FETCH_ASSOC)) { ?>
                                <tr style="transition: 0.3s; cursor: pointer;"
                                    onmouseover="this.style.backgroundColor='#f5f5f5'"
                                    onmouseout="this.style.backgroundColor='transparent'">
                                    <td style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;"><?= htmlspecialchars($row['telco']); ?></td>
                                    <td style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;"><?= Money($row['amount']) ?></td>
                                    <td style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd; color: #b65d2f; font-weight: bold;">
                                        <?= getStatusCard($row['status']); ?>
                                    </td>
                                    <td style="padding: 12px; text-align: center; border-bottom: 1px solid #ddd;">
                                        <?= timeAgo($row['created_at']) ?>
                                    </td>
                                </tr>
                            <?php } ?>
                        </tbody>
                    </table>
                    <div id="pagination" style="text-align:center; padding:15px 0;">
                        <?php for ($i = 1; $i <= $totalPages; $i++) { ?>
                            <a href="?page=<?= $i; ?>"
                                style="display: inline-block; padding: 6px 12px; margin-right: 4px; text-decoration: none; border: 1px solid #b65d2f; border-radius: 4px; transition: 0.3s;
                                <?= ($i == $page) ? 'background:#b65d2f; color:#fff;' : 'background:#fff; color:#b65d2f;'; ?>"

                                onmouseover="this.style.backgroundColor='#b65d2f'; this.style.color='white';"
                                onmouseout="this.style.backgroundColor='<?= ($i == $page) ? '#b65d2f' : '#fff'; ?>'; this.style.color='<?= ($i == $page) ? '#fff' : '#b65d2f'; ?>';">
                                <?= $i; ?>
                            </a>
                        <?php } ?>
                    </div>
                </div>
            </div>
        </div>
    </div>

 <!--Form Chuyển Khoản (Transfer)--> 

<div id="transferSection" >
  <div class="box_list_chuyenmuc">
    <div class="box_midss">
      <div class="box_detai" style="padding:5px;">
        <center>
          <b style="color:green">Thông tin chuyển khoản</b>
          <br>
          <div id="transfer_error" style="color:red;"></div>
        </center>

        <div style="max-width:500px; margin:0 auto; display:flex; align-items:center; border:1px solid #ccc; border-radius:8px; overflow:hidden; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
          <div style="flex:0 0 40%; background:#f5f5f5; text-align:center; padding:10px;">
           <img src="<?= htmlspecialchars($qrUrl) ?>" alt="QR Code" style="max-width:100%; height:auto;">
          </div>
          <div style="flex:1; padding:10px; font-family:'Times New Roman', serif;">
            <p style="margin:5px 0;"><b>Tên Tài Khoản:</b> <?= htmlspecialchars($chutaikhoan) ?></p>
            <p style="margin:5px 0;"><b>Số Tài Khoản:</b> <?= htmlspecialchars($stk) ?></p>
            <p style="margin:5px 0;"><b>Ngân Hàng:</b> <?= htmlspecialchars($tennh) ?></p>
            <p style="margin:5px 0;">
              <b>Nội Dung:</b>
              <span id="nd"><?= htmlspecialchars($memo) ?></span>
              <button type="button" onclick="navigator.clipboard.writeText(document.getElementById('nd').innerText)">Copy</button>
            </p>
			<p style="margin:10px 0;"><b>Nạp ATM được cộng thêm 10%</b> </p>
			<p style="margin:10px 0;"><b>Nạp ATM Tối thiểu 10,000 Đ mới cộng nhé</b> </p>
			<p style="margin:10px 0;"><b>Lỗi Nạp Báo ngay Admin: Zalo: 0982542412</b> </p>
          </div>
        </div>

        <br>
        <hr>
        <h3>Lịch Sử Chuyển Khoản</h3>
        <table width="100%" border="1" cellspacing="0"
          style="width:100%; border-collapse:collapse; background:#fff; border-radius:10px; overflow:hidden; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
          <thead>
            <tr style="background-color:#2a9d8f; color:#fff; font-weight:bold;">
              <th style="padding:12px; text-align:center; border-bottom:1px solid #ddd;">Số Tiền</th>
              <th style="padding:12px; text-align:center; border-bottom:1px solid #ddd;">Trạng Thái</th>
              <th style="padding:12px; text-align:center; border-bottom:1px solid #ddd;">Thời Gian</th>
            </tr>
          </thead>
          <tbody>
            <?php while ($row2 = $result2->fetch(PDO::FETCH_ASSOC)) { ?>
              <tr style="transition:.3s; cursor:pointer;"
                  onmouseover="this.style.backgroundColor='#f5f5f5'"
                  onmouseout="this.style.backgroundColor='transparent'">
                <td style="padding:12px; text-align:center; border-bottom:1px solid #ddd;"><?= Money($row2['amount']) ?></td>
                <td style="padding:12px; text-align:center; border-bottom:1px solid #ddd; color:#2a9d8f; font-weight:bold;">
                  <?= ($row2['status'] == 1) ? 'Thành Công' : 'Thất Bại' ?>
                </td>
                <td style="padding:12px; text-align:center; border-bottom:1px solid #ddd;">
                  <?= htmlspecialchars($row2['time'] ?? $row2['date'] ?? '') ?>
                </td>
              </tr>
            <?php } ?>
          </tbody>
        </table>

        <div id="pagination2" style="text-align:center; padding:15px 0;">
          <?php for ($i = 1; $i <= $totalPages2; $i++) { ?>
            <a href="?page2=<?= $i; ?>"
               style="display:inline-block; padding:6px 12px; margin-right:4px; text-decoration:none; border:1px solid #2a9d8f; border-radius:4px; transition:.3s;
               <?= ($i == $page2) ? 'background:#2a9d8f; color:#fff;' : 'background:#fff; color:#2a9d8f;'; ?>"
               onmouseover="this.style.backgroundColor='#2a9d8f'; this.style.color='white';"
               onmouseout="this.style.backgroundColor='<?= ($i == $page2) ? '#2a9d8f' : '#fff'; ?>'; this.style.color='<?= ($i == $page2) ? '#fff' : '#2a9d8f'; ?>';">
              <?= $i; ?>
            </a>
          <?php } ?>
        </div>

      </div>
    </div>
  </div>
</div>

</div>

<?php include '../Controllers/Footer.php'; ?>

<script>
    $(document).ready(function() {
        $("#btnCard").click(function() {
            $("#cardSection").show();
            $("#transferSection").hide();
            $(this).css({
                "background-color": "#ff5601",
                "color": "#fff"
            });
            $("#btnTransfer").css({
                "background-color": "#ccc",
                "color": "#000"
            });
        });

        $("#btnTransfer").click(function() {
            $("#cardSection").hide();
            $("#transferSection").show();
            $(this).css({
                "background-color": "#ff5601",
                "color": "#fff"
            });
            $("#btnCard").css({
                "background-color": "#ccc",
                "color": "#000"
            });
        });

        $("#cardPaymentForm").submit(function(event) {
            event.preventDefault();
            $("#comment_error").css("color", "red").text("Đang xử lý...");

            var formData = $(this).serialize();
            $.ajax({
                url: "/Api/Card",
                type: "POST",
                data: formData,
                dataType: "json",
                success: function(response) {
                    $("#comment_error").css("color", response.success ? "green" : "red").text(response.message);
                    if (response.success) {
                        setTimeout(function() {
                            window.location.href = "/Users/CardPayment";
                        }, 2000);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    var errorMessage = "Vui lòng thử lại sau.";
                    if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
                        errorMessage = jqXHR.responseJSON.message;
                    }
                    $("#comment_error").css("color", "red").text(errorMessage);
                }
            });
        });
    });
</script>