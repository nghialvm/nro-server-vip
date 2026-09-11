<?php
include '../Controllers/Header.php';


// Lấy dữ liệu tài khoản
$id_user_query = "SELECT COUNT(id) AS id FROM account";
$ban_count_query = "SELECT COUNT(*) AS ban FROM account WHERE ban = 1";
$active_count_query = "SELECT COUNT(*) AS active FROM account WHERE active = 1";

$id = $Connect->query($id_user_query)->fetchColumn();
$_tongban = $Connect->query($ban_count_query)->fetchColumn();
$_tongactive = $Connect->query($active_count_query)->fetchColumn();

// Lấy dữ liệu người chơi và vật phẩm
$sql_inventory = "SELECT name, items_bag, items_body, items_box, data_inventory FROM player";
$results_inventory = $Connect->query($sql_inventory)->fetchAll();

// Hàm xử lý đếm bộ Ngọc Rồng
function getDragonBallSetCount($results) {
    $dragonBallSetCount = 0;
    $playersWithSets = [];

    foreach ($results as $result) {
        $hasID14 = false;
        $hasID15 = false;
        $hasID16 = false;

        foreach (['items_bag', 'items_body', 'items_box'] as $itemsList) {
            if (isset($result[$itemsList])) {
                $itemsArray = json_decode($result[$itemsList], true);
                if (is_array($itemsArray)) {
                    foreach ($itemsArray as $item) {
                        $itemID = json_decode($item, true)[0]; // Giả sử ID là phần tử đầu tiên trong mảng item
                        if ($itemID == 14) $hasID14 = true;
                        if ($itemID == 15) $hasID15 = true;
                        if ($itemID == 16) $hasID16 = true;
                    }
                }
            }
        }

        if ($hasID14 && $hasID15 && $hasID16) {
            $dragonBallSetCount++;
            $playersWithSets[$result['name']] = 1;
        }
    }

    return [$playersWithSets, $dragonBallSetCount];
}

// Hàm xử lý tổng vật phẩm
function getItemCount($results, $itemCodes) {
    $totalItemCount = 0;
    $playersItems = [];
    foreach ($results as $result) {
        $totalItems = 0;
        foreach (['items_bag', 'items_body', 'items_box'] as $itemsList) {
            if (isset($result[$itemsList])) {
                $itemsArray = json_decode($result[$itemsList], true);
                if (is_array($itemsArray)) {
                    foreach ($itemsArray as $item) {
                        foreach ($itemCodes as $code) {
                            if (strpos($item, $code) !== false) {
                                $itemQuantity = json_decode($item, true)[1];
                                $totalItems += $itemQuantity;
                                $totalItemCount += $itemQuantity;
                            }
                        }
                    }
                }
            }
        }
        $playersItems[$result['name']] = $totalItems;
    }
    arsort($playersItems);
    return [$playersItems, $totalItemCount];
}
// ================== HÀM SEARCH ITEM THEO ID ==================
function getTopPlayersByItemId($results, $itemId) {
    $players = [];
    $total = 0;

    foreach ($results as $result) {
        $count = 0;

        foreach (['items_bag', 'items_body', 'items_box'] as $itemsList) {
            if (empty($result[$itemsList])) continue;

            $itemsArray = json_decode($result[$itemsList], true);
            if (!is_array($itemsArray)) continue;

            foreach ($itemsArray as $itemStr) {
                $item = json_decode($itemStr, true);
                if (!is_array($item)) continue;

                $id = $item[0] ?? -1;
                $quantity = $item[1] ?? 0;

                if ($id == $itemId) {
                    $count += $quantity;
                    $total += $quantity;
                }
            }
        }

        if ($count > 0) {
            $players[$result['name']] = $count;
        }
    }

    arsort($players);
    return [$players, $total];
}


// Hàm tính tổng vàng tươi của từng người chơi từ cột data_inventory
function getTotalGoldFromInventory($results) {
    $totalGold = 0;
    $playersGold = [];
    foreach ($results as $result) {
        if (isset($result['data_inventory'])) {
            $inventoryArray = json_decode($result['data_inventory'], true);
            if (is_array($inventoryArray) && count($inventoryArray) > 0) {
                $firstValue = (int) $inventoryArray[0];
                $totalGold += $firstValue;
                $playersGold[$result['name']] = $firstValue;
            }
        }
    }
    arsort($playersGold);
    return [$playersGold, $totalGold];
}

// Hàm tính tổng hồng ngọc của từng người chơi từ cột data_inventory
function getTotalRubyFromInventory($results) {
    $totalRuby = 0;
    $playersRuby = [];
    foreach ($results as $result) {
        if (isset($result['data_inventory'])) {
            $inventoryArray = json_decode($result['data_inventory'], true);
            if (is_array($inventoryArray) && count($inventoryArray) > 2) {
                $rubyCount = (int) $inventoryArray[2];
                $totalRuby += $rubyCount;
                $playersRuby[$result['name']] = $rubyCount;
            }
        }
    }
    arsort($playersRuby);
    return [$playersRuby, $totalRuby];
}

// Gọi hàm lấy số bộ Ngọc Rồng
list($dragonBallSetPlayers, $totalDragonBallSets) = getDragonBallSetCount($results_inventory);

// Gọi hàm lấy vật phẩm cho từng loại
list($goldPlayers, $totalGold) = getItemCount($results_inventory, ['[457']);
list($shirtPlayers, $totalShirts) = getItemCount($results_inventory, ['[555', '[557', '[559']);
list($pantsPlayers, $totalPants) = getItemCount($results_inventory, ['[556', '[558', '[560']);
list($glovesPlayers, $totalGloves) = getItemCount($results_inventory, ['[562', '[564', '[566']);
list($bootsPlayers, $totalBoots) = getItemCount($results_inventory, ['[563', '[565', '[567']);
list($ringsPlayers, $totalRings) = getItemCount($results_inventory, ['[561']);

// gọi hàm check 3s
list($check3sPlayers, $totalCheck3s) = getItemCount($results_inventory, ['[16,']);
list($check2sPlayers, $totalCheck2s) = getItemCount($results_inventory, ['[15,']);


// Tính tổng vàng tươi và hồng ngọc
list($freshGoldPlayers, $totalFreshGold) = getTotalGoldFromInventory($results_inventory);
list($rubyPlayers, $totalRuby) = getTotalRubyFromInventory($results_inventory);

// ================== XỬ LÝ SEARCH ITEM ==================
$searchItemPlayers = [];
$searchItemTotal = 0;
$searchItemId = null;

if (isset($_GET['search_item_id']) && $_GET['search_item_id'] !== '') {
    $searchItemId = (int)$_GET['search_item_id'];
    list($searchItemPlayers, $searchItemTotal) =
        getTopPlayersByItemId($results_inventory, $searchItemId);
}

// Hàm hiển thị bảng top
function displayTopTable($title, $players, $unit) {
    echo "<h6><b>$title:</b></h6>";
    echo "<table border='1' cellpadding='5' cellspacing='0'>";
    echo "<tr><th>Hạng</th><th>Tên</th><th>Số Lượng ($unit)</th></tr>";
    $rank = 1;
    foreach ($players as $player => $count) {
        if ($rank > 50) break;
        echo "<tr><td>$rank</td><td>$player</td><td>" . number_format($count) . "</td></tr>";
        $rank++;
    }
    echo "</table>";
}
?>

<div class="body">
    <div id="box_forums">
        <div class="box_list_parent">
            <div class="box_parent_list_next">
                <div class="box_phantrang">
                    <div class="backlink">
                        <a style="color:#fff;" href="/Forum">Quay lại</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <section class="data-check" style="text-align:center; padding:20px;">
        <h1 class="title" style="margin-bottom:10px; font-size:24px;">Kiểm Tra Dữ Liệu - Server </h1>
        <hr style="border-top:1px solid #ccc; margin-bottom:20px;">
        <div style="text-align:left; margin:0 auto; max-width:700px;">
            <p>Tổng tài khoản: <?php echo $id; ?></p>
            <p>Tổng người nạp lần đầu: <?php echo $_tongactive; ?></p>
            <p>Tài khoản vi phạm: <?php echo $_tongban; ?></p>
            <p><b>Tổng Thỏi Vàng:</b> <?php echo number_format($totalGold); ?> Thỏi Vàng</p>
            <p><b>Tổng Áo Thần Linh:</b> <?php echo number_format($totalShirts); ?> Áo</p>
            <p><b>Tổng Quần Thần Linh:</b> <?php echo number_format($totalPants); ?> Quần</p>
            <p><b>Tổng Găng Thần Linh:</b> <?php echo number_format($totalGloves); ?> Găng</p>
            <p><b>Tổng Giày Thần Linh:</b> <?php echo number_format($totalBoots); ?> Giày</p>
            <p><b>Tổng Nhẫn Thần Linh:</b> <?php echo number_format($totalRings); ?> Nhẫn</p>
            <p><b>Tổng Vàng Tươi:</b> <?php echo number_format($totalFreshGold); ?> Vàng Tươi</p>
            <p><b>Tổng Hồng Ngọc:</b> <?php echo number_format($totalRuby); ?> Hồng Ngọc</p>
            <p><b>Tổng Bộ Ngọc Rồng:</b> <?php echo number_format($totalDragonBallSets); ?> Bộ</p>
			<p><b>Tổng 3S :</b> <?php echo number_format($totalCheck3s); ?> Số lượng</p>
			<p><b>Tổng 2S :</b> <?php echo number_format($totalCheck2s); ?> Số lượng</p>
        </div>
        <hr>
<h3>🔍 Tìm kiếm vật phẩm </h3>

<form method="GET" style="margin-bottom:20px;">
    <input 
        type="number" 
        name="search_item_id" 
        placeholder="Nhập ID vật phẩm (vd: 561, 16, 457...)" 
        value="<?= isset($_GET['search_item_id']) ? (int)$_GET['search_item_id'] : '' ?>"
        required
        style="padding:6px; width:260px;"
    >
    <button type="submit" style="padding:6px 12px;">Tìm</button>
</form>
<?php
if ($searchItemId !== null) {
    echo '<hr>';

    if (!empty($searchItemPlayers)) {
        displayTopTable(
            'Top Người chơi sở hữu vật phẩm ID ' . $searchItemId,
            $searchItemPlayers,
            'Số lượng'
        );
        echo '<p><b>Tổng số lượng:</b> ' . number_format($searchItemTotal) . '</p>';
    } else {
        echo '<p><i>Không có người chơi nào sở hữu vật phẩm này.</i></p>';
    }
}
?>



        <div style="margin:40px auto 20px; text-align:center;">
            <?php 
            displayTopTable('Top Người chơi có nhiều Thỏi Vàng', $goldPlayers, 'Thỏi Vàng');
            displayTopTable('Top Người chơi có nhiều Áo Thần Linh', $shirtPlayers, 'Áo');
            displayTopTable('Top Người chơi có nhiều Quần Thần Linh', $pantsPlayers, 'Quần');
            displayTopTable('Top Người chơi có nhiều Găng Thần Linh', $glovesPlayers, 'Găng');
            displayTopTable('Top Người chơi có nhiều Giày Thần Linh', $bootsPlayers, 'Giày');
            displayTopTable('Top Người chơi có nhiều Nhẫn Thần Linh', $ringsPlayers, 'Nhẫn');
            displayTopTable('Top Người chơi có nhiều Vàng Tươi', $freshGoldPlayers, 'Vàng Tươi');
            displayTopTable('Top Người chơi có nhiều Hồng Ngọc', $rubyPlayers, 'Hồng Ngọc');
			displayTopTable('Top Người chơi có nhiều 3S ', $check3sPlayers, 'Số lượng');
			displayTopTable('Top Người chơi có nhiều 2S ', $check2sPlayers, 'Số lượng');
		
	     	displayTopTable('Top Người chơi có nhiều bộ ngọc rồng ', $dragonBallSetPlayers, 'Số lượng');
		    

            ?>
        </div>
    </section>
</div>

<?php include '../Controllers/Footer.php'; ?>