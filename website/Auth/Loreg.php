<?php
include '../Controllers/Header.php';

// ✅ Giữ ref nếu có
if (isset($_GET['ref']) && is_numeric($_GET['ref'])) {
    $_SESSION['ref_id'] = intval($_GET['ref']);
    echo "<script>window.location.hash = '#register';</script>";
}
?>

<style>
.auth-container {
    max-width: 420px;
    margin: 25px auto;
    padding: 20px;
    background: rgba(0,0,0,0.4);
    border: 1px solid #ffcc00;
    border-radius: 12px;
    box-shadow: 0 0 8px #ff9900;
    color: #fff;
    font-family: Tahoma, sans-serif;
}
.auth-container h3 { text-align:center; color:#ffd700; margin-bottom:10px; }
.auth-container table { width:100%; color:#fff; font-size:14px; }
.auth-container input, .auth-container select {
    width:100%; padding:7px; border:1px solid #ffb84d;
    background:rgba(0,0,0,0.6); color:#fff; border-radius:5px;
}
.auth-container button {
    width:100%; padding:8px; border:none; border-radius:8px;
    background:linear-gradient(180deg,#ff9900,#e65c00);
    color:#fff; font-weight:bold;
}
.auth-switch { text-align:center; margin-top:10px; font-size:13px; }
.auth-switch a { color:#ffd700; text-decoration:none; }
</style>

<div class="body">

<!-- 🔑 LOGIN  -->
<div id="loginform-container" class="auth-container">
    <h3>Đăng Nhập</h3>
    <form id="login" method="post">
        <input type="hidden" name="action" value="login">
        <table>
            <tr><td>Tài khoản</td><td><input type="text" name="Username" required></td></tr>
            <tr><td>Mật khẩu</td><td><input type="password" name="Password" required></td></tr>
            <tr><td>Server</td><td><select name="server"><option value="1">Server 1</option></select></td></tr>
        </table>
        <input type="hidden" name="csrf_token" value="<?= generateToken() ?>">
        <button type="submit">Đăng nhập</button>
        <div class="auth-switch">
            <a href="#register">Đăng ký</a> |
            <a href="#forgotpassword">Quên mật khẩu?</a>
        </div>
    </form>
</div>

<!-- 📝 REGISTER -->
<div id="registerform-container" class="auth-container" style="display:none;">
    <h3>Đăng Ký</h3>
    <form id="register" method="post">
        <input type="hidden" name="action" value="register">
        <input type="hidden" name="ref" value="<?= $_SESSION['ref_id'] ?? 0 ?>">
        <table>
            <tr><td>Tài khoản</td><td><input type="text" name="Username" required></td></tr>
            <tr><td>Mật khẩu</td><td><input type="password" name="Password" required></td></tr>
            <tr><td>Xác nhận</td><td><input type="password" name="RePassword" required></td></tr>
            <tr><td>Server</td><td><select name="server"><option value="1">Server 1</option></select></td></tr>
            <tr>
                <td colspan="2" style="text-align:center">
                    <div class="g-recaptcha"
                         data-sitekey="6LeXpz4sAAAAAHpZjtAEdLJMrmPgEchkmT6yx2Hi"></div>
                </td>
            </tr>
        </table>
        <input type="hidden" name="csrf_token" value="<?= generateToken() ?>">
        <button type="submit">Đăng ký</button>
        <div class="auth-switch">
            <a href="#login">Đăng nhập</a>
        </div>
    </form>
</div>

<!-- 🔐 FORGOT PASSWORD -->
<div id="forgotpasswordform-container" class="auth-container" style="display:none;">
    <h3>Quên Mật Khẩu</h3>
    <form id="forgotpassword" method="post">
        <input type="hidden" name="action" value="forgotpassword">
        <table>
            <tr><td>Email</td><td><input type="email" name="Email" required></td></tr>
            <tr><td>Server</td><td><select name="server"><option value="1">Server 1</option></select></td></tr>
        </table>
        <input type="hidden" name="csrf_token" value="<?= generateToken() ?>">
        <button type="submit">Xác nhận</button>
        <div class="auth-switch">
            <a href="#login">Đăng nhập</a> |
            <a href="#register">Đăng ký</a>
        </div>
    </form>
</div>

</div>

<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script>
function showTab(tab){
    ['login','register','forgotpassword'].forEach(id=>{
        document.getElementById(id+'form-container').style.display =
            (tab === id) ? 'block' : 'none';
    });

    // reset CAPTCHA CHỈ khi vào register
    if (tab === 'register' && typeof grecaptcha !== 'undefined') {
        grecaptcha.reset();
    }

    location.hash = tab;
}

document.addEventListener('DOMContentLoaded',()=>{
    showTab(location.hash.replace('#','') || 'login');
});

document.addEventListener('submit', async e => {
    e.preventDefault();

    const form = e.target;
    const action = form.querySelector('input[name="action"]').value;

    // ✅ CHỈ REGISTER MỚI CHECK CAPTCHA
    if (action === 'register') {
        if (typeof grecaptcha !== 'undefined' && !grecaptcha.getResponse()) {
            Swal.fire({
                icon: 'warning',
                title: '⚠️ Vui lòng xác nhận CAPTCHA'
            });
            return;
        }
    }

    const res = await fetch('/Api/Auth.php', {
        method: 'POST',
        body: new FormData(form)
    });

    const result = await res.json();

    if (result.status === 'success') {
        Swal.fire({
            icon: 'success',
            title: result.message,
            timer: 1500,
            showConfirmButton: false
        });
        setTimeout(() => location.href = '/Trang-Chu.php', 1500);
    } else {
        Swal.fire({ icon: 'error', title: result.message });
        if (action === 'register' && typeof grecaptcha !== 'undefined') {
            grecaptcha.reset();
        }
    }
});
</script>

<?php include '../Controllers/Footer.php'; ?>
