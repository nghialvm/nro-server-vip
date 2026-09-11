<?php
include 'Controllers/Header.php';
$currentServer = 1; // luôn kết nối server 1
$Connect = connectDatabase($servers[$currentServer]);


$page = filter_input(INPUT_GET, 'p', FILTER_VALIDATE_INT) ?: 0;
$size = 10;
$offset = $page * $size;
$stmt = $Connect->prepare("SELECT id, title, created_at, category FROM posts ORDER BY created_at DESC LIMIT :offset, :size");
$stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
$stmt->bindValue(':size', $size, PDO::PARAM_INT);
$stmt->execute();
$posts = $stmt->fetchAll(PDO::FETCH_ASSOC);

$totalStmt = $Connect->query("SELECT COUNT(*) FROM posts");
$total = $totalStmt->fetchColumn();
$totalPages = ceil($total / $size);
?>
<div class="body">
  <div id="box_forums" class="beta_test">
    <div class="box_list_chuyenmuc">
      <div id="stick" style="background: #f38500;">
        <div class="box_botsss">
          <div class="topic_name">
            <div style="width:30px;float:left;margin-right: 3px;">
              <img style="max-width:100%;max-height:100%;" src="/images/avatar/6101.gif" alt="Admin">
            </div>
            <div style="height:23px;display: table-cell; vertical-align: middle;">
              <a style="color:white" href="/Users/Giftcode" title=" Giftcode máy chủ Ngọc Rồng Lùa Gà">
                <b>Giftcode</b>
              </a>
              <img src="/images/gif/hot.gif" border="0">
			  </div>
			 
           
            <div class="box_name_eman" style="font-size:11px; color:#555;"> bởi <a href="javascript:void(0)">Admin</a>
              <span style="color:red">☆</span>
            </div>
			<div style="width:30px;float:left;margin-right: 3px;">
              <img style="max-width:100%;max-height:100%;" src="/images/avatar/6101.gif" alt="Admin">
            </div>
			 <div style="height:23px;display: table-cell; vertical-align: middle;">
              <a style="color:white" href="/Users/Disconnect" title=" Bảng xếp hạng máy chủ Ngọc Rồng Lùa Gà">
                <b>Bảng Xếp Hạng</b>
              </a>
              <img src="/images/gif/hot.gif" border="0">
            </div>
            <div class="box_name_eman" style="font-size:11px; color:#555;"> bởi <a href="javascript:void(0)">Admin</a>
              <span style="color:red">☆</span>
            </div>
          </div>
        </div>
      </div> 

      <!-- Bỏ toàn bộ code BXH và thay bằng dòng chữ chạy -->
      <div style="margin:20px 0; text-align:center;">
        <marquee behavior="scroll" direction="left" scrollamount="5" style="font-size:16px; color:yellow; font-weight:bold;">
		  📢 Server dev by Neru  📢
		</marquee>
      </div>
      <?php 
      $pinnedPosts = array_filter($posts, fn($post) => $post['category'] == 1);
      if (!empty($pinnedPosts)): ?>
        <div id="stick" style="background: #f38500;">
          <?php foreach ($pinnedPosts as $post): ?>
            <div class="box_botsss">
              <div class="topic_name">
                <div style="width:30px;float:left;margin-right: 3px;">
                  <img style="max-width:100%;max-height:100%;" src="/images/avatar/6101.gif" alt="Admin">
                </div>
                <div style="height:23px;display: table-cell; vertical-align: middle;">
                  <a style="color:white" href="/Users/Post?id=<?= $post['id'] ?>" title="<?= htmlspecialchars($post['title']) ?>">
                    <b><?= htmlspecialchars($post['title']) ?></b>
                  </a>
                  <img src="/images/gif/hot.gif" border="0">
                </div>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      <?php endif; ?>

      <br>
      <?php 
      $normalPosts = array_filter($posts, fn($post) => $post['category'] == 0);
      foreach ($normalPosts as $post): ?>
        <div class="box_botsss">
          <div class="topic_name">
            <div style="width:30px;float:left;margin-right: 3px;">
              <img style="max-width:100%;max-height:100%;" src="/images/avatar/6101.gif" alt="Admin">
            </div>
            <div>
              <a href="/Users/Post?id=<?= $post['id'] ?>" title="<?= htmlspecialchars($post['title']) ?>">
                <?= htmlspecialchars($post['title']) ?>
              </a>
              <div class="box_name_eman" style="font-size:11px; color:#555;"> bởi <a href="javascript:void(0)">Admin</a>
                <span style="color:red">☆</span>
                <i style="color:#473d3d; margin-left: 3px;"> <?= timeAgo($post['created_at']) ?> </i>
              </div>
            </div>
          </div>
        </div>
      <?php endforeach; ?>

      <!-- Phân trang -->
      <div class="box_topsss" style="background-color: transparent;">
        <span style="float:right">
          <div class="pagination" style="font-family: Arial, sans-serif;">
            <?php if ($page > 0): ?>
              <a href="?p=<?= $page - 1 ?>" style="padding:6px 12px; border:1px solid #b65d2f; text-decoration:none; margin-right:4px; color:#fff; background:#b65d2f; border-radius:4px;">&laquo;</a>
            <?php endif; ?>
            <?php
              $range = 2;
              for ($i = max(0, $page - $range); $i < min($totalPages, $page + $range + 1); $i++):
            ?>
              <?php if ($i == $page): ?>
                <span style="padding:6px 12px; border:1px solid #b65d2f; background:#b65d2f; color:#fff; margin-right:4px; border-radius:4px;"><?= $i + 1 ?></span>
              <?php else: ?>
                <a href="?p=<?= $i ?>" style="padding:6px 12px; border:1px solid #b65d2f; text-decoration:none; margin-right:4px; color:#b65d2f; background:#fff; border-radius:4px;"><?= $i + 1 ?></a>
              <?php endif; ?>
            <?php endfor; ?>
            <?php if ($page < $totalPages - 1): ?>
              <a href="?p=<?= $page + 1 ?>" style="padding:6px 12px; border:1px solid #b65d2f; text-decoration:none; color:#fff; background:#b65d2f; border-radius:4px;">&raquo;</a>
            <?php endif; ?>
          </div>
        </span>
      </div>
    </div>
  </div>
</div>
<div class="clearfix"></div>
<?php include 'Controllers/Footer.php'; ?>
