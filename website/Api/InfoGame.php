<?php
include '../Controllers/Configs.php';
header('Content-Type: application/json; charset=UTF-8');

$expectedKey = 'ImS-InfoGameChubeRong';
$providedKey = filter_input(INPUT_POST, 'key', FILTER_SANITIZE_STRING);
if (!$providedKey || $providedKey !== $expectedKey) {
    http_response_code(400);
    echo json_encode([]);
    exit;
}

$inv_key_param = filter_input(INPUT_POST, 'inv_key', FILTER_VALIDATE_INT);
$item_id_param_raw = filter_input(INPUT_POST, 'item_id', FILTER_SANITIZE_STRING);

$result = [];

if ($inv_key_param !== null && $inv_key_param !== false) {
    $idx = $inv_key_param - 1;
    if ($idx < 0) {
        echo json_encode([]);
        exit;
    }

    $sql = "
        SELECT name AS username,
               CAST(JSON_UNQUOTE(JSON_EXTRACT(data_inventory, '$[$idx]')) AS UNSIGNED) AS quantity
        FROM player
        WHERE JSON_EXTRACT(data_inventory, '$[$idx]') IS NOT NULL
              AND JSON_EXTRACT(data_inventory, '$[$idx]') > 0
        ORDER BY quantity DESC
        LIMIT 10
    ";
    $stmt = $Connect->prepare($sql);
    $stmt->execute();
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($rows as $row) {
        $result[] = [
            'username' => $row['username'],
            'quantity' => (int)$row['quantity'],
        ];
    }

} elseif (!empty($item_id_param_raw)) {
    $item_ids = array_filter(array_map('intval', explode(',', $item_id_param_raw)));

    if (empty($item_ids)) {
        echo json_encode([]);
        exit;
    }

    $topList = [];

    $stmt = $Connect->prepare("SELECT name, items_bag FROM player");
    $stmt->execute();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $username = $row['name'];
        $bagJson = $row['items_bag'];
        $bagArr = json_decode($bagJson, true);
        if (!is_array($bagArr)) continue;

        foreach ($bagArr as $element) {
            $elemArr = json_decode($element, true);
            if (!is_array($elemArr)) continue;
            if (isset($elemArr[0]) && in_array(intval($elemArr[0]), $item_ids)) {
                $qty = isset($elemArr[1]) ? intval($elemArr[1]) : 0;
                if ($qty <= 0) continue;
                if (!isset($topList[$username])) {
                    $topList[$username] = $qty;
                } else {
                    $topList[$username] += $qty;
                }
            }
        }
    }

    arsort($topList);
    $topList = array_slice($topList, 0, 100, true);

    foreach ($topList as $user => $qty) {
        $result[] = [
            'username' => $user,
            'quantity' => $qty,
        ];
    }

} else {
    echo json_encode([]);
    exit;
}

echo json_encode($result, JSON_UNESCAPED_UNICODE);
