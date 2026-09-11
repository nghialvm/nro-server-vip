</div>
</div>
<br>
<div class="bg_tree"></div>
<div class="foot_bg"></div>
<div class="left_b_bottom">
    <div class="right_b_bottom">
        <div class="footer">
            <div class="left_bottom"></div>
            <div class="right_bottom"></div>
        </div>
    </div>
</div>
<div class="copyright" style="line-height: 7px">
    <p>Ngọc Rồng Lùa Gà là bản ngọc rồng tựa theo cốt truyện Dragon Ball<br><br>do suu phát triển.</p>
    Bản Quyền thuộc về Zalo : 0982542412
</div>
</div>
<script src="/view/static/js/ThreeCanvas.js" type="text/javascript"></script>
<script src="/view/static/js/Snow3d.js" type="text/javascript"></script>
<script src="/view/static/js/animation.js?v5" type="text/javascript"></script>
<script src="/view/static/js/rocket-loader.min.js" data-cf-settings="3248e74b3f0d3f240922716b-|49" defer></script>
<script>
    $(document).ready(function () {
        var lastPostTime = 0;
            $("form[name='loginform'], form[name='registerform'], form[name='forgotpasswordform']").submit(function (event) {
                event.preventDefault();
                var now = Date.now();
                if (now - lastPostTime < 10000) {
                    var secondsLeft = Math.ceil((10000 - (now - lastPostTime)) / 1000);
                    $("#comment_error").css("color", "red").text("Bạn chỉ có thể post mỗi 10 giây. Vui lòng chờ " + secondsLeft + " giây.");
                    return;
                }

                var form = $(this);
                var formData = form.serialize();
                var action = form.attr("id");
                var csrfToken = $("#csrf_token").val();
                formData += '&csrf_token=' + csrfToken + '&action=' + action;

                $.post("/Api/Auth", formData)
                    .done(function (response) {
                        if (response.status === "success") {
                            $("#comment_error").css("color", "green").text(response.message);
                            lastPostTime = Date.now();
                            if ($("#authModal").length) {
                                $("#authModal").modal("hide");
                            }
                            setTimeout(function () {
                                window.location.reload();
                            }, 2000);
                        } else {
                            $("#comment_error").css("color", "red").text(response.message);
                        }
                    })
                    .fail(function (jqXHR) {
                        var errorMessage = (jqXHR.responseJSON && jqXHR.responseJSON.message) || "Vui lòng thử lại trong ít phút nữa.";
                        $("#comment_error").css("color", "red").text(errorMessage);
                    });
            });
        });
</script>
</body>

</html>