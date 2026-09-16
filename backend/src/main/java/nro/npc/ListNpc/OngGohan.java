
package nro.npc.ListNpc;


import nro.inventory.InventoryService;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Logger;
import Utils.Util;
import consts.ConstDailyGift;
import consts.ConstNpc;
import consts.ConstPlayer;
import consts.ConstTask;
import event.EventGuide;
import event.EventManager;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.List;
import jbcd.ConnectDB;
import jbcd.CrisResultSet;
import jbcd.dao.PlayerDAO;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.DailyGift.DailyGiftService;
import nro.player.Player;
import nro.services.DetuService;

public class OngGohan extends Npc {

    public OngGohan(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) {
            return;
        }

        clearExpiredVe(player);

        if (EventManager.LUNNAR_NEW_YEAR) {
            if (player.NhanLiXiForNPC_1 == 0) {
                String[] chucTetMessages = {
                    "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                    "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                    "Phát tài phát lộc",
                    "Vạn sự như ý",
                    "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                    "Năm mới phát tài phát lộc, vạn sự như ý nha",
                    "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                    "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                    "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                    "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                    "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                    "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                    "Tết đến cười thật nhiều, sống thật chill, vui hết mình",
                    "Chúc mừng năm mới"
                };

                String message = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                createOtherMenu(player, ConstNpc.NHAN_LI_XI, message, "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                return;
            }
        }

        boolean hasDaily = DailyGiftService.checkDailyGift(player, ConstDailyGift.DIEM_DANH_HANG_NGAY);

        List<String> menu = hasDaily
                ? List.of("Điểm Danh Hàng Ngày", "Chức Năng", "Hướng Dẫn Tân Thủ", "Nhận\nĐệ tử",
                        "On/Off Fusion", "Mua\nvé tháng", "Mua\nvé tuần", "Đóng")
                : List.of("Chức Năng", "Hướng Dẫn Tân Thủ", "Nhận\nĐệ tử",
                        "On/Off Fusion", "Mua\nvé tháng", "Mua\nvé tuần", "Đóng");

        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            if (player.baovetaikhoan) {
                this.createOtherMenu(player, ConstNpc.MENU_MA_BAO_VE,
                        "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại",
                        "Quên Mã Bảo Vệ", "Đóng");
                return;
            }

            String mentor;
            switch (player.gender) {
                case ConstPlayer.TRAI_DAT:
                    mentor = "Quy lão Kamê";
                    break;
                case ConstPlayer.NAMEC:
                    mentor = "Trưởng lão Guru";
                    break;
                default:
                    mentor = "Vua Vegeta";
                    break;
            }

            String baseMessage = (TaskService.gI().getIdTask(player) >= ConstTask.TASK_11_0)
                    ? "Con cố gắng theo " + mentor + " học thành tài, đừng lo lắng cho ta."
                    : "Con cần ta giúp gì.";

            String message = baseMessage
                    + "\n\n" + getThongTinVe(player)
                    + "\n\n|5|Vé tháng: 100.000 VND - 30 ngày"
                    + "\n|6|+50 thỏi vàng điểm danh, x4 rơi thỏi vàng"
                    + "\n|5|Vé tuần: 30.000 VND - 7 ngày"
                    + "\n|6|+20 thỏi vàng điểm danh, x2 rơi thỏi vàng";

            this.createOtherMenu(player, ConstNpc.BASE_MENU, message, menu.toArray(new String[0]));
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) {
            return;
        }

        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.NHAN_LI_XI:
                switch (select) {
                    case 1:
                        Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                        String[] chucTetMessages = {
                            "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                            "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                            "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                            "Năm mới phát tài phát lộc, vạn sự như ý nha",
                            "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                            "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                            "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                            "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                            "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                            "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                            "Tết đến cười thật nhiều, sống thật chill, vui hết mình"
                        };

                        String NpcChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                        String PlayerChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                        this.npcChat(player, NpcChat);
                        Service.gI().chat(player, PlayerChat);
                        player.NhanLiXiForNPC_1++;
                        if (Util.isTrue(60, 100)) {
                            Lixi.addOptionParam(30, 0);
                            Lixi.addOptionParam(93, 30);
                            InventoryService.gI().addItemBag(player, Lixi);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Bạn nhận được " + Lixi.template.name);
                        } else {
                            Service.gI().sendThongBao(player, "(>_<)");
                        }
                        break;
                }
                break;

            case ConstNpc.BASE_MENU:
                handleBaseMenu(player, select);
                break;

            case ConstNpc.ONG_GIA_MENU_1:
                handleSupportMenu(player, select);
                break;

            case ConstNpc.ONG_GIA_MENU_2:
                handleTutorialMenu(player, select);
                break;

            case ConstNpc.ONG_GIA_MENU_3:
                handleSkipQuestMenu(player, select);
                break;

            case ConstNpc.CONFIRM_THOIVANG:
                Nhanthoivang(player, select);
                break;

            case ConstNpc.NHAN_DE_TU:
                Nhandetu(player, select);
                break;

            case ConstNpc.MENU_MA_BAO_VE:
                if (select == 0) {
                    Input.gI().createFormMBV(player);
                }
                break;

            case ConstNpc.QUY_DOI_HN:
                switch (select) {
                    case 0:
                        Item thoiVang = InventoryService.gI().findItemBag(player, 457);
                        if (thoiVang == null || thoiVang.quantity <= 0) {
                            Service.gI().sendThongBao(player, "Bạn không có Thỏi vàng nào để quy đổi!");
                            return;
                        }
                        Input.gI().createFormDoiNgocHong(player);
                        break;

                    case 1:
                        Service.gI().sendThongBao(player, "Hẹn gặp lại con!");
                        break;
                }
                break;

            case ConstNpc.MTVFREE:
                if (select == 0) {
                    if (!player.getSession().actived) {
                        boolean quaNhiemVu = (player.playerTask != null
                                && player.playerTask.taskMain != null
                                && player.playerTask.taskMain.id >= 25);

                        final int CAN_VND = 20_000;
                        boolean duVnd = player.getSession().tongnap >= CAN_VND;

                        if (quaNhiemVu || duVnd) {
                            player.getSession().actived = true;
                            PlayerDAO.MuaThanhVien(player, 0);
                            Service.gI().sendMoney(player);
                            npcChat(player, "Mở thành viên FREE thành công!");
                        } else {
                            npcChat(player, "Chưa đủ điều kiện! Cần hoàn thành nhiệm vụ hoặc Nạp lần đầu 20K VND.");
                        }
                    } else {
                        npcChat(player, "Bạn đã mở rồi!");
                    }
                    return;
                }
                break;

            case ConstNpc.CONFIRM_BUY_VE_THANG:
                buyVeThang(player, select);
                break;

            case ConstNpc.CONFIRM_BUY_VE_TUAN:
                buyVeTuan(player, select);
                break;
        }
    }

    private void handleBaseMenu(Player player, int select) {
        clearExpiredVe(player);
        boolean hasDaily = DailyGiftService.checkDailyGift(player, ConstDailyGift.DIEM_DANH_HANG_NGAY);

        if (hasDaily) {
            switch (select) {
                case 0:
                    if (player.DIEM_DANH == 0) {
                        int soThoiVang = 10;
                        if (hasVeThang(player)) {
                            soThoiVang = 50;
                        } else if (hasVeTuan(player)) {
                            soThoiVang = 20;
                        }

                        player.inventory.ruby += 50;
                        Item item = ItemService.gI().createNewItem((short) 457, soThoiVang);
                        item.addOptionParam(30, 0);
                        InventoryService.gI().addItemBag(player, item);
                        InventoryService.gI().sendItemBag(player);

                        Service.gI().sendMoney(player);
                        player.DIEM_DANH++;
                        DailyGiftService.updateDailyGift(player, ConstDailyGift.DIEM_DANH_HANG_NGAY);
                        Service.getInstance().sendThongBao(player,
                                "Điểm danh thành công, bạn nhận được " + soThoiVang + " thỏi vàng & 50 hồng ngọc");
                    } else {
                        Service.getInstance().sendThongBao(player, "Hôm nay bạn đã điểm danh rồi!");
                    }
                    break;

                case 1:
                    openSupportMenu(player);
                    break;

                case 2:
                    openTutorialMenu(player);
                    break;

                case 3:
                    this.createOtherMenu(player, ConstNpc.NHAN_DE_TU,
                            "Con có muốn nhận đệ tử thường không?",
                            "Nhận đệ tử", "Đóng");
                    break;

                case 4:
                    player.hienThiHopThe = (player.hienThiHopThe == 0 ? 1 : 0);
                    Service.getInstance().sendThongBao(player,
                            player.hienThiHopThe == 1 ? "Đã bật hiển thị hợp thể" : "Đã tắt hiển thị hợp thể");
                    Service.getInstance().Send_Caitrang(player);
                    break;

                case 5:
                    showMenuVeThang(player);
                    break;

                case 6:
                    showMenuVeTuan(player);
                    break;

                default:
                    break;
            }
        } else {
            switch (select) {
                case 0:
                    openSupportMenu(player);
                    break;

                case 1:
                    openTutorialMenu(player);
                    break;

                case 2:
                    this.createOtherMenu(player, ConstNpc.NHAN_DE_TU,
                            "Con có muốn nhận đệ tử thường không?",
                            "Nhận đệ tử", "Đóng");
                    break;

                case 3:
                    player.hienThiHopThe = (player.hienThiHopThe == 0 ? 1 : 0);
                    Service.getInstance().sendThongBao(player,
                            player.hienThiHopThe == 1 ? "Đã bật hiển thị hợp thể" : "Đã tắt hiển thị hợp thể");
                    Service.getInstance().Send_Caitrang(player);
                    break;

                case 4:
                    showMenuVeThang(player);
                    break;

                case 5:
                    showMenuVeTuan(player);
                    break;

                default:
                    break;
            }
        }
    }

    private void showMenuVeThang(Player player) {
        clearExpiredVe(player);

        String trangThai;
        if (hasVeThang(player)) {
            long conLai = getConLaiNgay(player.getSession().vethangExpire);
            trangThai = "\n|1|Bạn đang có vé tháng, còn " + conLai + " ngày";
        } else if (hasVeTuan(player)) {
            long conLai = getConLaiNgay(player.getSession().vetuanExpire);
            trangThai = "\n|1|Bạn đang có vé tuần, còn " + conLai + " ngày";
        } else {
            trangThai = "\n|7|Hiện tại bạn chưa có vé nào";
        }

        this.createOtherMenu(player, ConstNpc.CONFIRM_BUY_VE_THANG,
                "|7|MUA VÉ THÁNG"
                + "\n|2|Giá: 100,000 VND"
                + "\n|6|Thời hạn: 30 ngày"
                + "\n|6|Điểm danh mỗi ngày: + 50 thỏi vàng"
                + "\n|6|Quyền lợi thêm: x4 rơi thỏi vàng"
                + "\n|5|Chỉ mua lại khi vé hiện tại đã hết hạn"
                + trangThai,
                "Mua", "Đóng");
    }

    private void showMenuVeTuan(Player player) {
        clearExpiredVe(player);

        String trangThai;
        if (hasVeTuan(player)) {
            long conLai = getConLaiNgay(player.getSession().vetuanExpire);
            trangThai = "\n|1|Bạn đang có vé tuần, còn " + conLai + " ngày";
        } else if (hasVeThang(player)) {
            long conLai = getConLaiNgay(player.getSession().vethangExpire);
            trangThai = "\n|1|Bạn đang có vé tháng, còn " + conLai + " ngày";
        } else {
            trangThai = "\n|7|Hiện tại bạn chưa có vé nào";
        }

        this.createOtherMenu(player, ConstNpc.CONFIRM_BUY_VE_TUAN,
                "|7|MUA VÉ TUẦN"
                + "\n|2|Giá: 30,000 VND"
                + "\n|6|Thời hạn: 7 ngày"
                + "\n|6|Điểm danh mỗi ngày: + 20 thỏi vàng"
                + "\n|6|Quyền lợi thêm: x2 rơi thỏi vàng"
                + "\n|5|Chỉ mua lại khi vé hiện tại đã hết hạn"
                + trangThai,
                "Mua", "Đóng");
    }

    private boolean hasVeThang(Player player) {
        return player.getSession().vethang == 1
                && player.getSession().vethangExpire > System.currentTimeMillis();
    }

    private boolean hasVeTuan(Player player) {
        return player.getSession().vetuan == 1
                && player.getSession().vetuanExpire > System.currentTimeMillis();
    }

    private boolean hasAnyActiveVe(Player player) {
        return hasVeThang(player) || hasVeTuan(player);
    }

    private void clearExpiredVe(Player player) {
        long now = System.currentTimeMillis();
        boolean needUpdate = false;

        if (player.getSession().vethang == 1 && player.getSession().vethangExpire <= now) {
            player.getSession().vethang = 0;
            player.getSession().vethangExpire = 0;
            needUpdate = true;
        }

        if (player.getSession().vetuan == 1 && player.getSession().vetuanExpire <= now) {
            player.getSession().vetuan = 0;
            player.getSession().vetuanExpire = 0;
            needUpdate = true;
        }

        if (needUpdate) {
            try {
                ConnectDB.executeUpdate(
                        "UPDATE account SET vethang = ?, vethang_expire = ?, vetuan = ?, vetuan_expire = ? WHERE id = ?",
                        player.getSession().vethang,
                        player.getSession().vethangExpire,
                        player.getSession().vetuan,
                        player.getSession().vetuanExpire,
                        player.account_id
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long getConLaiNgay(long expire) {
        long remain = expire - System.currentTimeMillis();
        if (remain <= 0) {
            return 0;
        }
        long days = remain / (24 * 60 * 60 * 1000);
        if (remain % (24 * 60 * 60 * 1000) != 0) {
            days++;
        }
        return days;
    }

    private String getThongTinVe(Player player) {
        clearExpiredVe(player);

        if (hasVeThang(player)) {
            return "|2|Vé tháng: còn " + getConLaiNgay(player.getSession().vethangExpire) + " ngày"
                    + "\n|6|Quyền lợi: điểm danh +50 thỏi vàng, x4 rơi thỏi vàng";
        } else if (hasVeTuan(player)) {
            return "|2|Vé tuần: còn " + getConLaiNgay(player.getSession().vetuanExpire) + " ngày"
                    + "\n|6|Quyền lợi: điểm danh +20 thỏi vàng, x2 rơi thỏi vàng";
        } else {
            return "|7|Bạn chưa có vé tuần / vé tháng";
        }
    }

    private void buyVeThang(Player player, int select) {
        if (select != 0) {
            return;
        }

        clearExpiredVe(player);

        try {
            if (hasAnyActiveVe(player)) {
                if (hasVeThang(player)) {
                    Service.gI().sendThongBao(player, "Bạn đang có vé tháng, chỉ mua lại khi vé hết hạn!");
                } else {
                    Service.gI().sendThongBao(player, "Bạn đang có vé tuần, chỉ mua vé tháng khi vé tuần hết hạn!");
                }
                return;
            }

            final int giaVe = 100_000;
            final long THIRTY_DAYS = 30L * 24 * 60 * 60 * 1000;
            long expire = System.currentTimeMillis() + THIRTY_DAYS;

            if (player.getSession().vnd < giaVe) {
                Service.gI().sendThongBao(player, "Bạn không đủ 100,000 VND để mua vé tháng!");
                return;
            }

            player.getSession().vnd -= giaVe;
            player.getSession().vethang = 1;
            player.getSession().vetuan = 0;
            player.getSession().vethangExpire = expire;
            player.getSession().vetuanExpire = 0;

            ConnectDB.executeUpdate(
                    "UPDATE account SET vnd = ?, vethang = 1, vethang_expire = ?, vetuan = 0, vetuan_expire = 0 WHERE id = ?",
                    player.getSession().vnd,
                    expire,
                    player.account_id
            );

            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player,
                    "Mua vé tháng thành công! Vé có hiệu lực 30 ngày, điểm danh mỗi ngày nhận 50 thỏi vàng và x4 rơi thỏi vàng.");
        } catch (Exception e) {
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Có lỗi xảy ra khi mua vé tháng!");
        }
    }

    private void buyVeTuan(Player player, int select) {
        if (select != 0) {
            return;
        }

        clearExpiredVe(player);

        try {
            if (hasAnyActiveVe(player)) {
                if (hasVeTuan(player)) {
                    Service.gI().sendThongBao(player, "Bạn đang có vé tuần, chỉ mua lại khi vé hết hạn!");
                } else {
                    Service.gI().sendThongBao(player, "Bạn đang có vé tháng, chỉ mua vé tuần khi vé tháng hết hạn!");
                }
                return;
            }

            final int giaVe = 30_000;
            final long SEVEN_DAYS = 7L * 24 * 60 * 60 * 1000;
            long expire = System.currentTimeMillis() + SEVEN_DAYS;

            if (player.getSession().vnd < giaVe) {
                Service.gI().sendThongBao(player, "Bạn không đủ 30,000 VND để mua vé tuần!");
                return;
            }

            player.getSession().vnd -= giaVe;
            player.getSession().vetuan = 1;
            player.getSession().vethang = 0;
            player.getSession().vetuanExpire = expire;
            player.getSession().vethangExpire = 0;

            ConnectDB.executeUpdate(
                    "UPDATE account SET vnd = ?, vetuan = 1, vetuan_expire = ?, vethang = 0, vethang_expire = 0 WHERE id = ?",
                    player.getSession().vnd,
                    expire,
                    player.account_id
            );

            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player,
                    "Mua vé tuần thành công! Vé có hiệu lực 7 ngày, điểm danh mỗi ngày nhận 20 thỏi vàng và x2 rơi thỏi vàng.");
        } catch (Exception e) {
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Có lỗi xảy ra khi mua vé tuần!");
        }
    }

    private void Nhandetu(Player player, int select) {
        switch (select) {
            case 0:
                if (player.Detu == null) {
                    DetuService.gI().createNormalPet(player);
                } else {
                    Service.gI().sendThongBao(player, "Bạn đã có đệ tử rồi!");
                }
                break;
        }
    }

    private void handleSupportMenu(Player player, int select) {
        switch (select) {
            case 0: {
                claimFreeGem(player);
                break;
            }
            case 1: {
                if (!refreshGoldBarFromDatabase(player)) {
                    return;
                }
                int thoivang = player.getSession().goldBar;
                this.createOtherMenu(player, ConstNpc.CONFIRM_THOIVANG,
                        "|2|Bạn có : " + thoivang + " Thỏi vàng\n",
                        "Nhận", "Từ Chối");
                break;
            }
            case 2:
                this.createOtherMenu(player, ConstNpc.MTVFREE,
                        "|7|Mở thành viên FREE"
                        + "\n|6|Yêu cầu hoàn thành nhiệm vụ ADR 19"
                        + "\n|1|OR NẠP lần đầu sẽ được mở FREE",
                        "Đồng ý", "Từ chối");
                break;

            case 3:
                Input.gI().createFormGiftCode(player);
                break;

            case 4:
                Input.gI().createFormChangePassword(player);
                break;

            case 5:
                this.createOtherMenu(player, ConstNpc.ONG_GIA_MENU_3,
                        "|0|Menu Skip Nhiệm Vụ\n\n|2|Mời Quý Khách Lựa Chọn!\n",
                        "Skip Nhiệm Vụ Heo Rừng", "Skip Nhiệm Vụ Bulon", "Skip Nhiệm Vụ\n Thách đấu",
                        "Skip Nhiệm Vụ Đại Hội Võ Thuật", "Skip Nhiệm Vụ Trung Uý Trắng", "Đóng");
                break;

            case 6:
                try {
                    DecimalFormat df = new DecimalFormat("#,###");

                    boolean isActive = player.getSession().actived;
                    String trangThai = isActive ? "§2ĐÃ MỞ THÀNH VIÊN" : "§cCHƯA MỞ THÀNH VIÊN";

                    String soDu = df.format(player.getSession().vnd);
                    String tongNap = df.format(player.getSession().tongnap);

                    StringBuilder sb = new StringBuilder();
                    sb.append("|2|------ THÔNG TIN THÀNH VIÊN ------\n");
                    sb.append("|7|Trạng thái: ").append(trangThai).append("\n");
                    sb.append("|7|Số dư hiện có: ").append(soDu).append(" VND\n");
                    sb.append("|7|Tổng nạp: ").append(tongNap).append(" VND\n");

                    if (!isActive) {
                        sb.append("|1|Hãy nạp lần đầu để mở quyền thành viên!");
                    } else {
                        sb.append("|2|Bạn đã là thành viên! Hãy tận hưởng các quyền lợi đặc biệt.");
                    }

                    this.createOtherMenu(player, ConstNpc.CONFIRM_ACTIVE, sb.toString(), "Đóng");

                } catch (Exception e) {
                    Service.gI().sendThongBao(player, "Lỗi hiển thị thông tin thành viên!");
                    e.printStackTrace();
                }
                break;
        }
    }

    private void handleTutorialMenu(Player player, int select) {
        String tutorialId;
        switch (select) {
            case 0:
                tutorialId = ConstNpc.HUONG_DAN_TANTHU;
                break;
            case 1:
                tutorialId = ConstNpc.HUONG_DAN_TANTHU2;
                break;
            case 2:
                tutorialId = ConstNpc.HUONG_DAN_TANTHU3;
                break;
            case 3:
                if (EventManager.LUNNAR_NEW_YEAR) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_TET;
                } else if (EventManager.CHRISTMAS) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_NOEL;
                } else if (EventManager.VU_LAN_FESTIVAL) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_VU_LAN;
                } else if (EventManager.HALLOWEEN) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_HALLOWEEN;
                } else if (EventManager.INTERNATIONAL_WOMANS_DAY) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_QUOC_TE_83;
                } else if (EventManager.TRUNG_THU) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_TRUNG_THU;
                } else if (EventManager.HUNG_VUONG) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_HUNG_VUONG;
                } else if (EventManager.BLACK_FRIDAY) {
                    tutorialId = EventGuide.HUONG_DAN_SU_KIEN_BLACK_FRIDAY;
                } else {
                    tutorialId = "Đang Update...";
                }
                break;
            default:
                tutorialId = "";
                break;
        }
        if (tutorialId != null) {
            NpcService.gI().createTutorial(player, this.avartar, tutorialId);
        }
    }

    private void handleSkipQuestMenu(Player player, int select) {
        try {
            if (player == null || player.playerTask == null || player.playerTask.taskMain == null) {
                Service.gI().sendThongBao(player, "Bạn hiện không có nhiệm vụ nào để bỏ qua.");
                return;
            }

            int taskId = player.playerTask.taskMain.id;
            int taskIndex = player.playerTask.taskMain.index;

            boolean canSkip = false;
            String taskName = "";

            switch (select) {
                case 0:
                    if (taskId == 13 && taskIndex < player.playerTask.taskMain.subTasks.size() - 1) {
                        canSkip = true;
                        taskName = "Heo Rừng";
                    }
                    break;

                case 1:
                    if (taskId == 15 && taskIndex < player.playerTask.taskMain.subTasks.size() - 1) {
                        canSkip = true;
                        taskName = "Bulon";
                    }
                    break;

                case 2:
                    if (taskId == 16 && taskIndex < player.playerTask.taskMain.subTasks.size() - 1) {
                        canSkip = true;
                        taskName = "Thách đấu 10 người";
                    }
                    break;

                case 3:
                    if (taskId == 18 && taskIndex < player.playerTask.taskMain.subTasks.size() - 1) {
                        canSkip = true;
                        taskName = "Đại Hội Võ Thuật";
                    }
                    break;

                case 4:
                    if (taskId == 19 && taskIndex < player.playerTask.taskMain.subTasks.size() - 1) {
                        canSkip = true;
                        taskName = "Trung Úy Trắng";
                    }
                    break;

                case 5:
                    Service.gI().sendThongBao(player, "Đã đóng menu Skip Nhiệm Vụ.");
                    return;

                default:
                    Service.gI().sendThongBao(player, "Lựa chọn không hợp lệ.");
                    return;
            }

            if (canSkip) {
                TaskService.gI().sendNextTaskMain(player);
                Service.gI().sendThongBaoOK(player, "Bạn đã skip nhiệm vụ " + taskName + " thành công!");
            } else {
                Service.gI().sendThongBao(player, "Không thể skip nhiệm vụ này (đã ở cuối nhánh hoặc không thuộc nhánh).");
            }

        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi skip nhiệm vụ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openSupportMenu(Player player) {
        this.createOtherMenu(player, ConstNpc.ONG_GIA_MENU_1, "|7|Chức Năng Hỗ Trợ\n|2|Xin Mời Quý Khách Lựa Chọn!",
                "Nhận Ngọc Xanh", "Nhận Thỏi Vàng", "MTV FREE", "Nhập GiftCode", "Đổi \nMật Khẩu", "Skip Nhiệm Vụ", "Thông tin", "Đóng");
    }

    private void openTutorialMenu(Player player) {
        this.createOtherMenu(player, ConstNpc.ONG_GIA_MENU_2, "|7| Hướng Dẫn Tân Thủ\n|2|Mời Người Chơi Chọn!",
                "Cách Kiếm Vật Phẩm", "Cách Kiếm Đồ", "Thông Tin Boss", "Thông Tin\nSự Kiện", "Đóng");
    }

    private void openZaloGroup(Player player) {
        try {
            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "|2|Inbox Vào Zalo: 0373713573\n"
                    + "|2|Nhắn Cho ADMIN Để Được Hỗ Trợ Nhé!",
                    "Ok");
            Desktop.getDesktop().browse(new URI("https://zalo.me/g/iumbtl736"));
        } catch (IOException | URISyntaxException e) {
            Service.getInstance().sendThongBao(player, "Lỗi mở Zalo: " + e.getMessage());
        }
    }

    private void claimFreeGem(Player player) {
        synchronized (player) {
            final int freeGemLimit = 2_000_000;
            int currentGem = Math.max(0, player.inventory.gem);
            if (currentGem >= freeGemLimit) {
                Service.gI().sendThongBao(player, "Đớp ít thôi con!");
                return;
            }

            int amount = freeGemLimit - currentGem;
            player.inventory.addGem(amount);
            PlayerDAO.updatePlayer(player);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Bạn nhận được " + Util.format(amount) + " ngọc xanh.");
        }
    }

    private boolean refreshGoldBarFromDatabase(Player player) {
        CrisResultSet result = null;
        try {
            result = ConnectDB.executeQuery(
                    "SELECT thoi_vang FROM account WHERE id = ?",
                    player.account_id
            );
            if (result.next()) {
                player.getSession().goldBar = Math.max(0, result.getInt("thoi_vang"));
            } else {
                player.getSession().goldBar = 0;
            }
            return true;
        } catch (Exception e) {
            Logger.logException(OngGohan.class, e, "Loi dong bo thoi vang");
            Service.gI().sendThongBao(player, "Không thể kiểm tra thỏi vàng, vui lòng thử lại.");
            return false;
        } finally {
            if (result != null) {
                result.dispose();
            }
        }
    }

    private void claimGoldBars(Player player) {
        synchronized (player) {
            if (!refreshGoldBarFromDatabase(player)) {
                return;
            }

            int amount = player.getSession().goldBar;
            if (amount <= 0) {
                Service.gI().sendThongBao(player, "Bạn không có thỏi vàng nào để nhận!");
                return;
            }
            if (!InventoryService.gI().canAddThoiVang(player, amount)) {
                Service.gI().sendThongBao(player, "Hành trang không đủ chỗ để nhận toàn bộ thỏi vàng.");
                return;
            }

            int updated;
            try {
                updated = ConnectDB.executeUpdate(
                        "UPDATE account SET thoi_vang = 0 WHERE id = ? AND thoi_vang = ?",
                        player.account_id,
                        amount
                );
            } catch (Exception e) {
                Logger.logException(OngGohan.class, e, "Loi cap nhat thoi vang");
                Service.gI().sendThongBao(player, "Lỗi khi cập nhật dữ liệu, vui lòng thử lại.");
                return;
            }

            if (updated != 1) {
                refreshGoldBarFromDatabase(player);
                Service.gI().sendThongBao(player, "Dữ liệu thỏi vàng đã thay đổi, vui lòng mở lại menu.");
                return;
            }

            if (!InventoryService.gI().addThoiVang(player, amount)) {
                try {
                    ConnectDB.executeUpdate(
                            "UPDATE account SET thoi_vang = ? WHERE id = ? AND thoi_vang = 0",
                            amount,
                            player.account_id
                    );
                } catch (Exception e) {
                    Logger.logException(OngGohan.class, e, "Loi hoan tac thoi vang");
                }
                refreshGoldBarFromDatabase(player);
                Service.gI().sendThongBao(player, "Không thể thêm thỏi vàng vào hành trang, dữ liệu đã được giữ lại.");
                return;
            }

            player.getSession().goldBar = 0;
            PlayerDAO.updatePlayer(player);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được x" + Util.format(amount) + " Thỏi Vàng!");
            Service.gI().sendMoney(player);
        }
    }

    private void Nhanthoivang(Player player, int select) {
        switch (select) {
            case 0:
                claimGoldBars(player);
                break;

            case 1:
                Service.gI().sendThongBao(player, "Đã thoát.");
                break;

            default:
                Service.gI().sendThongBao(player, "Lựa chọn không hợp lệ!");
                break;
        }
    }
}
