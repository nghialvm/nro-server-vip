<?php include 'Controllers/Header.php'; ?>
<script>
    const currentPath = window.location.pathname;
    if (currentPath === "/") {
        window.location.href = "/Trang-Chu";
    }
</script>
<!-- Popup đếm ngược 
<div id="countdownModal" class="modal">
  <div class="modal-content">
    <span class="close" id="btnClose">&times;</span>
    <h3 style="color:white;background:#ff6600;padding:10px;border-radius:5px;">
      Máy chủ Cậu Bé Rồng chính thức khai mở
	  </h3>
      Đếm ngược từ giờ tới game mở TEST thời gian còn :
    </h3>
    <div id="timer" style="font-size:22px;font-weight:bold;margin:15px 0;color:#333;"></div>

    <h4>Hóng game tại đây</h4>
    <div style="display:flex;justify-content:center;gap:20px;margin-top:10px;">
      <a href="https://zalo.me/g/iumbtl736" target="_blank" style="text-align:center;">
        <img src="/images/zalo.jpg" width="60"><br>Box Zalo
      </a>
      <a href="https://www.facebook.com/share/1C8UieH8dN/?mibextid=wwXIfr" target="_blank" style="text-align:center;">
        <img src="/images/fb.png" width="60"><br>Fanpage FB
      </a>
    </div>
    <br>
    <!-- Nút chỉ hiển thị sau khi mở server 
    <div id="afterOpenBtns" style="display:none;">
      <button onclick="understood()" style="padding:8px 16px;background:#28a745;color:#fff;border:none;border-radius:5px;cursor:pointer;">
        Tôi đã hiểu
      </button>
      <button onclick="remindLater()" style="padding:8px 16px;background:#ff9800;color:#fff;border:none;border-radius:5px;cursor:pointer;margin-left:10px;">
        Nhắc lại sau 15 phút
      </button>
    </div>
  </div>
</div>-->

<script>
function showModal() {
  document.getElementById("countdownModal").style.display = "block";
}
function understood() {
  document.getElementById("countdownModal").style.display = "none";
}
function remindLater() {
  document.getElementById("countdownModal").style.display = "none";
  setTimeout(() => {
    showModal();
  }, 15 * 60 * 1000);
}
function hideModal() {
  understood();
}

window.onload = function() {
  showModal();
};

const targetDate = new Date("2025-10-10T10:00:00").getTime();
const timer = setInterval(function() {
  const now = new Date().getTime();
  const distance = targetDate - now;

  if (distance < 0) {
    clearInterval(timer);
    document.getElementById("timer").innerHTML = "✅ Máy chủ đã mở Test!";
    // Hiện nút sau khi open
    document.getElementById("afterOpenBtns").style.display = "block";
    document.getElementById("btnClose").style.display = "inline";
    return;
  }

  const days = Math.floor(distance / (1000 * 60 * 60 * 24));
  const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((distance % (1000 * 60)) / 1000);

  document.getElementById("timer").innerHTML =
    (days < 10 ? "0" + days : days) + " Ngày " +
    (hours < 10 ? "0" + hours : hours) + " Giờ " +
    (minutes < 10 ? "0" + minutes : minutes) + " Phút " +
    (seconds < 10 ? "0" + seconds : seconds) + " Giây";
}, 1000);

// Ban đầu ẩn nút close (X) và nút action
document.getElementById("btnClose").style.display = "none";
</script>


<style>
.modal {
  display:none;
  position:fixed;
  z-index:9999;
  left:0; top:0;
  width:100%; height:100%;
  background:rgba(0,0,0,0.6);
}
.modal-content {
  background:#fff;
  margin:10% auto;
  padding:20px;
  border-radius:10px;
  width:90%; max-width:400px;
  text-align:center;
  box-shadow:0 5px 20px rgba(0,0,0,0.3);
}
.close {
  float:right; font-size:28px;
  cursor:pointer; color:red;
}
</style>

<!-- ========== Nội dung gốc trang chủ ========== -->
<div class="bg_top_22">
    <img src="/images/banner_2.png" width="100%">
</div>
<div class="bg-content">
    <div>
        <div class="title"><h4>Giới Thiệu</h4></div>
        <div class="content">
            <p>Ngọc Rồng Lùa Gà là Trò Chơi Trực Tuyến với cốt truyện xoay quanh bộ truyện tranh 7 viên Ngọc Rồng...</p>
            <p class="content-p">Cơ Bản</p>
            <p class="text-center">
                <img alt="" src="/images/gif/gif_maphongba.gif">&nbsp;
                <img alt="" src="/images/gif/gif_gif_Saiyain.gif">&nbsp;
                <img alt="" src="/images/gif/gif_supber_kame.gif">&nbsp;
            </p>
            <p class="content-p">VIP</p>
            <p class="text-center">
                <img alt="" src="/images/gif/gif_maphongba_VIP.gif">&nbsp;
                <img alt="" src="/images/gif/gif_gif_Saiyain_VIP.gif">&nbsp;
                <img alt="" src="/images/gif/gif_supber_kame_VIP.gif">&nbsp;
            </p>
            <p class="text-center"><a href="/?c=skill">Xem thêm</a></p>
        </div>
    </div>
</div>
<div class="bg-content">
    <div>
        <div class="title"><h4>Hướng Dẫn Tân Thủ</h4></div>
        <div class="content">
            <p><strong>1. Đăng ký tài khoản</strong></p>
            <p>Ngọc Rồng Lùa Gà sử dụng Tài Khoản riêng, không chung với bất kỳ Trò Chơi nào khác...</p>
            <p><strong>2. Hướng dẫn điều khiển</strong></p>
            <p>Đối với máy bàn phím: Dùng phím mũi tên, phím số...</p>
            <p><strong>3. Một số thông tin căn bản</strong></p>
            <p>- Đậu thần dùng để tăng KI và HP ngay lập tức...<br>
               - Tất cả các sách kỹ năng đều có thể học miễn phí...</p>
        </div>
    </div>
</div>
<div class="bg-content">
    <div>
        <div class="title"><h4>Bạn nên tải phiên bản nào?</h4></div>
        <div class="content">
            <p>Nếu bạn dùng điện thoại Nokia cũ... tải bản JAVA</p>
            <p>Nếu bạn dùng máy Android... tải bản APK hoặc Playstore</p>
            <p>Nếu bạn dùng iPhone... tải bản Appstore hoặc TestFlight</p>
            <p>Nếu bạn dùng PC... tải bản PC</p>
        </div>
    </div>
</div>
<?php include 'Controllers/Footer.php'; ?>
