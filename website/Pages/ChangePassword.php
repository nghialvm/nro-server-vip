<?php include '../Controllers/Header.php'; ?>
<div class="body">
    <table width="100%" border="0" cellspacing="0">
        <tbody>
            <tr class="menu1">
                <td id="selected" style="width:50%; background-color: #ff5601;">Đổi mật khẩu</td>
            </tr>
        </tbody>
    </table>
    <div id="box_forums">
        <div class="box_list_chuyenmuc">
            <div class="box_midss">
                <div class="box_detai">
                    <div style="padding:5px;">
                        <center><b style="color:red">Vui lòng nhập đầy đủ thông tin</b>
                            <br>
                            <div id="comment_error" style="color:red;"></div>
                        </center>
                        <form method="post" name="changepass" id="changepass">
                            <table style="width:100%; max-width:500px; margin:0 auto;">
                                <tbody>
                                    <tr>
                                        <td><b>Mật khẩu hiện tại</b></td>
                                        <td style="position: relative;">
                                            <input type="password" name="currentPassword" id="currentPassword" style="width:100%;" required>
                                            <span style="position: absolute; right:5px; top:50%;">
                                                <span onclick="show_pass(1)" id="show-pass"><i class="bx bx-hide"></i></span>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><b>Mật khẩu mới</b></td>
                                        <td style="position: relative;">
                                            <input type="password" name="newPassword" id="newPassword" style="width:100%;" required>
                                            <span style="position: absolute; right:5px; top:50%;">
                                                <span onclick="show_pass(2)" id="show-npass"><i class="bx bx-hide"></i></span>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><b>Nhập lại mật khẩu mới</b></td>
                                        <td style="position: relative;">
                                            <input type="password" name="confirmPassword" id="confirmPassword" style="width:100%;" required>
                                            <span style="position: absolute; right:5px; top:50%;">
                                                <span onclick="show_pass(3)" id="show-repass"><i class="bx bx-hide"></i></span>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td style="text-align:center; padding-top:10px;">
                                            <input class="w3-button w3-red" type="submit" name="button1" id="button1" value="Đổi Mật Khẩu">
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="clearfix"></div>
</div>
<?php include '../Controllers/Footer.php'; ?>
<script>
    function show_pass(id) {
        var inputId = id === 1 ? '#currentPassword' : id === 2 ? '#newPassword' : '#confirmPassword';
        var input = $(inputId);
        input.attr('type', input.attr('type') === 'password' ? 'text' : 'password');
    }

    $(document).ready(function() {
        $("#changepass").submit(function(event) {
            event.preventDefault();
            $("#comment_error").css("color", "red").text("Đang xử lý...");

            var formData = $(this).serialize();
            $.ajax({
                url: "/Api/ChangePassword",
                type: "POST",
                data: formData,
                dataType: "json",
                success: function(response) {
                    $("#comment_error").css("color", response.success ? "green" : "red").text(response.message);
                    if (response.success) {
                        setTimeout(function() {
                            window.location.href = "/Users/ChangePassword";
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