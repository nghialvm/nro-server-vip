<?php
require_once __DIR__ . '/../Controllers/Configs.php';
require_once __DIR__ . '/../Controllers/Header.php';

/**
 * CHỈ ADMIN
 */
if (!isset($ImS) || empty($ImS['isFounder'])) {
    die('Không có quyền truy cập');
}

/**
 * SEARCH THEO TÊN PLAYER
 */
$searchName = trim($_GET['player_name'] ?? '');
$where  = '';
$params = [];

if ($searchName !== '') {
    $where = "
        WHERE
            ht.player_1 LIKE :p1
         OR ht.player_2 LIKE :p2
    ";
    $params = [
        'p1' => $searchName . '%',
        'p2' => $searchName . '%',
    ];
}

/**
 * QUERY
 */
$sql = "
    SELECT
        ht.id,
        ht.player_1,
        ht.player_2,
        ht.item_player_1,
        ht.item_player_2,
        ht.time_tran
    FROM history_transaction ht
    $where
    ORDER BY ht.id DESC
    LIMIT 200
";

$stmt = $Connect->prepare($sql);
$stmt->execute($params);
$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

/**
 * FORMAT ITEM: mỗi item 1 dòng
 */
function formatItemText($text) {
    if (!$text) return '<span style="color:#888">—</span>';

    $text = str_replace(["\r", "\n"], ' ', $text);
    $text = preg_replace('/\s+/', ' ', $text);

    $items = array_filter(array_map('trim', explode(',', $text)));
    if (!$items) return '<span style="color:#888">—</span>';

    $html = '<ul class="item-list">';
   foreach ($items as $it) {
    $it = ltrim($it, "\t\n\r\0\x0B");
    $html .= '<li>• ' . htmlspecialchars(rtrim($it, ',')) . '</li>';
}

    $html .= '</ul>';
    return $html;
}
?>

<style>
:root{
    --gold:#f5c400;
    --dark:#0d0d0d;
    --dark2:#1a1a1a;
    --line:#333;
}

body{
    background:linear-gradient(180deg,#f5c400,#f0b800);
}

.ls-wrap{
    padding:30px 12px;
    min-height:85vh;
}

.ls-box{
    background:linear-gradient(180deg,#111,#000);
    border-radius:16px;
    padding:22px;
    max-width:1250px;
    margin:auto;
    color:#eee;
    box-shadow:0 12px 30px rgba(0,0,0,.6);
}

.ls-box h2{
    text-align:center;
    margin-bottom:20px;
    color:#ffd700;
    letter-spacing:1px;
}

/* SEARCH */
.ls-search{
    display:flex;
    justify-content:center;
    gap:10px;
    margin-bottom:18px;
}

.ls-search input{
    padding:10px 14px;
    width:320px;
    border-radius:8px;
    border:none;
    outline:none;
    font-size:14px;
}

.ls-search button{
    padding:10px 18px;
    background:#ff3b3b;
    color:#fff;
    border:none;
    border-radius:8px;
    font-weight:bold;
    cursor:pointer;
}
.ls-search button:hover{
    background:#ff5c5c;
}

/* TABLE */
table.ls-table{
    width:100%;
    border-collapse:collapse;
    font-size:13.5px;
    background:var(--dark);
    border-radius:12px;
    overflow:hidden;
}

.ls-table th{
    background:var(--dark2);
    padding:12px 10px;
    color:#ffd700;
    border-bottom:1px solid var(--line);
    text-align:left;
}

.ls-table td{
    padding:10px;
    border-top:1px solid var(--line);
    vertical-align:top;
}

.ls-table tr:hover{
    background:#111;
}

.player{
    color:#ffd700;
    font-weight:bold;
}

.time{
    white-space:nowrap;
    color:#ccc;
    font-size:12.5px;
}

/* ITEM */
.item-list{
    margin:0;
    padding-left:18px;
}
.item-list li{
    margin:2px 0;
    white-space:nowrap;
}
</style>

<div class="ls-wrap">
<div class="ls-box">

    <h2>📜 LỊCH SỬ GIAO DỊCH</h2>

    <form method="get" class="ls-search">
        <input type="text" name="player_name"
               placeholder="Nhập tên player"
               value="<?= htmlspecialchars($searchName) ?>">
        <button>Xem</button>
    </form>

    <table class="ls-table">
        <tr>
            <th style="width:60px">ID</th>
            <th style="width:180px">Player1</th>
            <th style="width:180px">Player2</th>
            <th>Item P1</th>
            <th>Item P2</th>
            <th style="width:160px">Thời gian</th>
        </tr>

        <?php if (!$rows): ?>
            <tr>
                <td colspan="6" style="text-align:center;color:#888">
                    Không có dữ liệu
                </td>
            </tr>
        <?php endif; ?>

        <?php foreach ($rows as $r): ?>
        <tr>
            <td><?= (int)$r['id'] ?></td>
            <td class="player"><?= htmlspecialchars($r['player_1']) ?></td>
            <td class="player"><?= htmlspecialchars($r['player_2']) ?></td>
            <td><?= formatItemText($r['item_player_1']) ?></td>
            <td><?= formatItemText($r['item_player_2']) ?></td>
            <td class="time"><?= htmlspecialchars($r['time_tran']) ?></td>
        </tr>
        <?php endforeach; ?>
    </table>

</div>
</div>

<?php require_once __DIR__ . '/../Controllers/Footer.php'; ?>
