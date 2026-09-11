<?php
include '../Controllers/Header.php';

// =======================
// ID GIFT CODE MUỐN ẨN (ẨN CỨNG)
// =======================
$hiddenGiftcodeIds = [
    6,8
];

// =======================
// ẨN ĐỘNG QUA URL
// ví dụ: giftcode.php?hide=3,7,15
// =======================
if (!empty($_GET['hide'])) {
    $idsFromUrl = array_map('intval', explode(',', $_GET['hide']));
    $hiddenGiftcodeIds = array_unique(array_merge($hiddenGiftcodeIds, $idsFromUrl));
}

// =======================
// Lấy giftcodes từ DB
// =======================
$gcs = $Connect->query("
    SELECT id, code, item 
    FROM giftcode 
    ORDER BY id ASC
")->fetchAll(PDO::FETCH_ASSOC);

// Chuẩn bị lấy item_template
$itemStmt = $Connect->prepare("
    SELECT name, icon_id 
    FROM item_template 
    WHERE id = ?
");
?>

<div class="body" style="font-family:'Segoe UI',sans-serif; background:#fff3e0; padding:20px;">
  <div style="max-width:1100px; margin:0 auto; background:#fff; border-radius:10px; box-shadow:0 2px 10px rgba(0,0,0,0.08);">

    <h2 style="padding:14px 18px; margin:0; background:#f39c12; color:#fff; border-radius:10px 10px 0 0;">
      Giftcode Free
    </h2>

    <table style="width:100%; border-collapse:collapse;">
      <thead>
        <tr style="background:#ffe0b2;">
          <th style="padding:12px; text-align:left; border-bottom:1px solid #e8e8e8;">Giftcode</th>
          <th style="padding:12px; text-align:left; border-bottom:1px solid #e8e8e8;">Danh sách vật phẩm</th>
        </tr>
      </thead>
      <tbody>

      <?php foreach ($gcs as $gc): ?>

        <?php
        // =======================
        // BỎ QUA GIFT CODE BỊ ẨN
        // =======================
        if (in_array((int)$gc['id'], $hiddenGiftcodeIds)) {
            continue;
        }

        $items = json_decode($gc['item'], true) ?: [];
        ?>

        <tr>
          <td style="vertical-align:top; padding:12px; border-bottom:1px solid #f2f2f2; font-weight:600; color:#333;">
            <?= htmlspecialchars($gc['code']) ?>
            <div style="font-size:12px; color:#999;">
              ID: <?= (int)$gc['id'] ?>
            </div>
          </td>

          <td style="padding:12px; border-bottom:1px solid #f2f2f2;">
            <?php if (!$items): ?>
              <em style="color:#999;">(Chưa có vật phẩm)</em>
            <?php else: ?>
              <div style="display:flex; flex-wrap:wrap; gap:10px;">
                <?php foreach ($items as $it): ?>
                  <?php
                  $itemStmt->execute([$it['id']]);
                  $tpl = $itemStmt->fetch(PDO::FETCH_ASSOC);

                  $name = $tpl['name'] ?? ('Item ' . $it['id']);
                  $icon = $tpl['icon_id'] ?? 0;

                  $basePath = "/images/x4/";
                  $iconUrl  = $basePath . $icon . ".png";
                  $fallback = $basePath . $icon . ".gif";
                  ?>
                  <div style="
                    display:flex;
                    align-items:center;
                    gap:8px;
                    background:#fff8ec;
                    border:1px solid #ffe0b2;
                    border-radius:8px;
                    padding:6px 10px;
                  ">
                    <img
                      src="<?= $iconUrl ?>"
                      alt="<?= htmlspecialchars($name) ?>"
                      style="width:28px; height:28px; object-fit:contain; image-rendering:pixelated;"
                      onerror="this.onerror=null;this.src='<?= $fallback ?>';"
                    >
                    <span style="color:#444;">
                      <?= htmlspecialchars($name) ?> x<?= number_format((int)$it['quantity']) ?>
                    </span>
                  </div>
                <?php endforeach; ?>
              </div>
            <?php endif; ?>
          </td>
        </tr>

      <?php endforeach; ?>

      </tbody>
    </table>

  </div>
</div>

<?php include '../Controllers/Footer.php'; ?>
