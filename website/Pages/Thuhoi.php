<?php
include '../Controllers/Header.php';
if (!isset($ImS) || empty($ImS['isFounder'])) die('Không có quyền truy cập');

const EMPTY_ITEM_JSON = '[-1,0]';
const EMPTY_PET_BODY  = '[-1,0,"[]",0]';

/* ================= PLAYER REMOVE ================= */

function removeItemInBox($json, $id){
    if(!$json) return $json;
    $a=json_decode($json,true);
    if(!is_array($a)) return $json;
    $new=[];
    foreach($a as $i){
        $d=json_decode($i,true);
        if(is_array($d)&&(int)$d[0]===$id) continue;
        $new[]=$i;
    }
    return json_encode($new);
}

function removeItemSafe(&$bag,&$body,$id){
    foreach([&$bag,&$body] as &$ref){
        $a=json_decode($ref,true);
        if(!is_array($a)) continue;
        foreach($a as $i=>$v){
            $d=json_decode($v,true);
            if(is_array($d)&&(int)$d[0]===$id){
                $a[$i]=EMPTY_ITEM_JSON;
            }
        }
        $ref=json_encode($a);
    }
}

/* ================= PET REMOVE (FIX) ================= */

function removeItemInPet(&$pet,$itemId){
    if(!$pet) return;
    $p=json_decode($pet,true);
    if(!is_array($p)) return;

    // pet inventory
    if(isset($p[2])){
        $inv=json_decode($p[2],true);
        if(is_array($inv)){
            foreach($inv as $i=>$s){
                $d=json_decode($s,true);
                if(is_array($d)&&(int)$d[0]===$itemId){
                    $inv[$i]=json_encode([-1,0,"[]",time()]);
                }
            }
            $p[2]=json_encode($inv);
        }
    }

    // pet equip (đồ đang mặc)
    if(isset($p[3])){
        $eq=json_decode($p[3],true);
        if(is_array($eq)){
            foreach($eq as $i=>$s){
                $d=json_decode($s,true);
                if(is_array($d)&&(int)$d[0]===$itemId){
                    $eq[$i]=json_encode([-1,0]);
                }
            }
            $p[3]=json_encode($eq);
        }
    }

    $pet=json_encode($p);
}

/* ================= COUNT ================= */

function countJson($json,$id){
    if(!$json) return 0;
    $a=json_decode($json,true);
    if(!is_array($a)) return 0;
    $c=0;
    foreach($a as $v){
        $d=json_decode($v,true);
        if(is_array($d)&&(int)$d[0]===$id){
            $c+=max(1,(int)($d[1]??1));
        }
    }
    return $c;
}

function countPet($pet,$id){
    if(!$pet) return 0;
    $p=json_decode($pet,true);
    if(!is_array($p)) return 0;
    $c=0;

    if(isset($p[2])){
        $inv=json_decode($p[2],true);
        if(is_array($inv)){
            foreach($inv as $s){
                $d=json_decode($s,true);
                if(is_array($d)&&(int)$d[0]===$id){
                    $c+=max(1,(int)($d[1]??1));
                }
            }
        }
    }

    if(isset($p[3])){
        $eq=json_decode($p[3],true);
        if(is_array($eq)){
            foreach($eq as $s){
                $d=json_decode($s,true);
                if(is_array($d)&&(int)$d[0]===$id){
                    $c+=max(1,(int)($d[1]??1));
                }
            }
        }
    }

    return $c;
}

/* ================= DATA ================= */

$itemTemplates=$Connect->query(
    "SELECT id,name FROM item_template ORDER BY id ASC"
)->fetchAll(PDO::FETCH_ASSOC);

$list=[];
$itemId=0;
$message='';

/* ================= PREVIEW ================= */

if(isset($_POST['preview'])){
    $itemId=(int)$_POST['item_id'];
    $players=$Connect->query("SELECT * FROM player")->fetchAll(PDO::FETCH_ASSOC);

    foreach($players as $p){
        $bag=countJson($p['items_bag'],$itemId);
        $body=countJson($p['items_body'],$itemId);
        $box=countJson($p['items_box'],$itemId);
        $pet=countPet($p['pet'],$itemId);
        $total=$bag+$body+$box+$pet;

        if($total>0){
            $list[]=[
                'name'=>$p['name'],
                'bag'=>$bag,
                'body'=>$body,
                'box'=>$box,
                'pet'=>$pet,
                'total'=>$total
            ];
        }
    }
}

/* ================= REMOVE ================= */

if(isset($_POST['remove'])){
    $itemId=(int)$_POST['item_id'];
    $mode=(int)$_POST['mode'];

    if($mode===1){
        $players=$Connect->query("SELECT * FROM player")->fetchAll(PDO::FETCH_ASSOC);
        $cnt=0;
        foreach($players as $p){
            $bag=$p['items_bag'];
            $body=$p['items_body'];
            $box=$p['items_box'];
            $pet=$p['pet'];

            removeItemSafe($bag,$body,$itemId);
            $box=removeItemInBox($box,$itemId);
            removeItemInPet($pet,$itemId);

            if($bag!=$p['items_bag']||$body!=$p['items_body']
            ||$box!=$p['items_box']||$pet!=$p['pet']){
                $st=$Connect->prepare(
                    "UPDATE player SET items_bag=?,items_body=?,items_box=?,pet=? WHERE id=?"
                );
                $st->execute([$bag,$body,$box,$pet,$p['id']]);
                $cnt++;
            }
        }
        $message="✅ Đã thu hồi từ <b>$cnt</b> người chơi";
    }

    if($mode===2){
        $name=$_POST['player_name'];
        $st=$Connect->prepare("SELECT * FROM player WHERE name=?");
        $st->execute([$name]);
        if($p=$st->fetch()){
            $bag=$p['items_bag'];
            $body=$p['items_body'];
            $box=$p['items_box'];
            $pet=$p['pet'];

            removeItemSafe($bag,$body,$itemId);
            $box=removeItemInBox($box,$itemId);
            removeItemInPet($pet,$itemId);

            $up=$Connect->prepare(
                "UPDATE player SET items_bag=?,items_body=?,items_box=?,pet=? WHERE id=?"
            );
            $up->execute([$bag,$body,$box,$pet,$p['id']]);
            $message="✅ Đã thu hồi của <b>$name</b>";
        }
    }
}
?>

<style>
.admin-box{background:#f4f6f8;padding:20px;border-radius:12px}
.search,.select{width:100%;padding:10px;border-radius:6px;border:1px solid #ccc;margin-bottom:10px}
.btn{padding:10px;border:none;border-radius:6px;font-weight:bold;cursor:pointer}
.btn-danger{background:#d60000;color:#fff}
.btn-all{background:#ff3b3b;color:#fff;width:100%;margin:10px 0}
.stat{display:flex;gap:15px;margin:10px 0}
.stat div{flex:1;background:#fff;padding:15px;border-radius:10px;box-shadow:0 2px 6px rgba(0,0,0,.1)}
.table{width:100%;border-collapse:collapse;background:#fff;border-radius:10px;overflow:hidden}
.table th{background:#e9ecef;padding:12px}
.table td{padding:12px;border-top:1px solid #eee;text-align:center}
.table tr.highlight{background:#ffecec}
.total{font-weight:bold;color:#d60000}
</style>

<div class="body" style="max-width:1200px;margin:auto">
<div class="admin-box">

<h2>🔴 THU HỒI VẬT PHẨM</h2>
<?= $message?"<p style='color:green'>$message</p>":"" ?>

<input id="searchItem" class="search" placeholder="🔍 Gõ ID hoặc tên vật phẩm">

<form method="post">
<select name="item_id" id="itemSelect" class="select">
<?php foreach($itemTemplates as $it): ?>
<option value="<?= $it['id'] ?>">[<?= $it['id'] ?>] <?= $it['name'] ?></option>
<?php endforeach ?>
</select>
<button name="preview" class="btn btn-danger" style="width:100%">XEM DANH SÁCH</button>
</form>

<?php if($list): ?>
<div class="stat">
<div>👥 <b><?= count($list) ?></b> người chơi</div>
<div>📦 <b><?= array_sum(array_column($list,'total')) ?></b> vật phẩm</div>
</div>

<input id="searchPlayer" class="search" placeholder="🔍 Tìm player">

<form method="post">
<input type="hidden" name="item_id" value="<?= $itemId ?>">
<input type="hidden" name="mode" value="1">
<button name="remove" class="btn btn-all" onclick="return confirm('THU HỒI TOÀN SERVER?')">
🚨 THU HỒI TOÀN SERVER
</button>
</form>

<table class="table">
<tr>
<th>Player</th><th>Bag</th><th>Body</th><th>Box</th><th>Pet</th><th>Tổng</th><th>Action</th>
</tr>
<?php foreach($list as $pl): ?>
<tr class="<?= $pl['total']>=2?'highlight':'' ?>" data-name="<?= strtolower($pl['name']) ?>">
<td><?= htmlspecialchars($pl['name']) ?></td>
<td><?= $pl['bag'] ?></td>
<td><?= $pl['body'] ?></td>
<td><?= $pl['box'] ?></td>
<td><?= $pl['pet'] ?></td>
<td class="total"><?= $pl['total'] ?></td>
<td>
<form method="post">
<input type="hidden" name="item_id" value="<?= $itemId ?>">
<input type="hidden" name="mode" value="2">
<input type="hidden" name="player_name" value="<?= $pl['name'] ?>">
<button name="remove" class="btn btn-danger">Thu hồi</button>
</form>
</td>
</tr>
<?php endforeach ?>
</table>
<?php endif; ?>

</div>
</div>

<script>
searchItem.oninput=()=>{
 const k=searchItem.value.toLowerCase();
 [...itemSelect.options].forEach(o=>{
  o.style.display=o.text.toLowerCase().includes(k)?'':'none';
 });
};
searchPlayer?.addEventListener('input',()=>{
 const k=searchPlayer.value.toLowerCase();
 document.querySelectorAll('tr[data-name]').forEach(r=>{
  r.style.display=r.dataset.name.includes(k)?'':'none';
 });
});
</script>

<?php include '../Controllers/Footer.php'; ?>
