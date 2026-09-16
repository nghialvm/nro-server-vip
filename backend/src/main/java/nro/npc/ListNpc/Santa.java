package nro.npc.ListNpc;


import Utils.TimeUtil;
import nro.inventory.InventoryService;
import nro.services.Fun.Input;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jbcd.dao.HistoryTransactionDAO;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;

public class Santa extends Npc {

    public Santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (player.iDMark != null && player.iDMark.getIndexMenu() == ConstNpc.MENU_XEM_LS_GIAO_DICH) {
                if (player.lichSuGiaoDichDangXem != null) {
                    player.lichSuGiaoDichDangXem = null;
                }
            }
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_10 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN, "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                            "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_13 == 0) {
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
            List<String> menu = new ArrayList<>(Arrays.asList(
                    "Cửa hàng",
                    "Mở rộng\nHành trang\nRương đồ",
                   "Nhập mã\nquà tặng",
                   "Shop hạn\nsử dụng",
                    "Shop Vip",
                   "Tiệm\nHớt tóc",
                    "Danh\nhiệu",
                    "Kiểm tra\nGiao dịch\n1 ngọc"
            ));

            String[] menus = menu.toArray(String[]::new);

            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào, ta có một số vật phẩm đặc biệt cậu có muốn xem không?", menus);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            int currentMenu = player.iDMark.getIndexMenu();
            if (currentMenu == ConstNpc.MENU_XEM_LS_GIAO_DICH) {
                xuLyPhanTrangLichSu(player, select);
                return;
            }
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI) {
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
                            player.NhanLiXiForNPC_13++;
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
                    return;
                }
            }
            if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0: // Cửa hàng
                            if (!InventoryService.gI().findItemPhieuGiamGiaThuong(player) && !InventoryService.gI().findItemPhieuGiamGiaVip(player)) {
                                createOtherMenu(player, ConstNpc.SHOP_SANTA,
                                        "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                        "Cửa Hàng", "Đóng");
                            } else if (InventoryService.gI().findItemPhieuGiamGiaThuong(player) && !InventoryService.gI().findItemPhieuGiamGiaVip(player)) {
                                createOtherMenu(player, ConstNpc.SHOP_SANTA80,
                                        "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                        "Cửa Hàng", "Cửa Hàng Giảm Giá 80%", "Đóng");
                            } else if (!InventoryService.gI().findItemPhieuGiamGiaThuong(player) && InventoryService.gI().findItemPhieuGiamGiaVip(player)) {
                                createOtherMenu(player, ConstNpc.SHOP_SANTA50_VIP,
                                        "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                        "Cửa Hàng", "Cửa Hàng Giảm Giá 50% VIP", "Đóng");
                            } else if (InventoryService.gI().findItemPhieuGiamGiaThuong(player) && InventoryService.gI().findItemPhieuGiamGiaVip(player)) {
                                createOtherMenu(player, ConstNpc.SHOP_SANTA50_80,
                                        "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                        "Cửa Hàng", "Cửa Hàng Giảm Giá 50%", "Cửa Hàng Giảm Giá 80% VIP", "Đóng");
                            }
                            break;
                        case 1: // Mở rộng hành trang
                            ShopService.gI().opendShop(player, "SANTA_MO_RONG_HANH_TRANG", false);
                            break;
                        case 2: // Nhập mã quà tặng
                            Input.gI().createFormGiftCode(player);
                            break;
                        case 3: // Shop hạn sử dụng
                            ShopService.gI().opendShop(player, "SANTA_HAN_SU_DUNG", false);
                            break;
                        case 4: // SHOP VIP
                            ShopService.gI().opendShop(player, "SHOP_NRO", false);
                            break;
                        case 5: // Đổi tóc
                            ShopService.gI().opendShop(player, "SANTA_HEAD", false);
                            break;
                        case 6: // Shop danh hiệu
                            ShopService.gI().opendShop(player, "SANTA_DANH_HIEU", false);
                            break;
                        case 7: // Kiểm tra lịch sử giao dịch
                            kiemTraVaBatDauXemLichSuGiaoDich(player);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.SHOP_SANTA) {
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "SANTA", false);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.SHOP_SANTA80) {
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "SANTA", false);
                            break;
                        case 1:
                            // Service.gI().chat(player, "Chức năng đang được cập nhât!");
                            ShopService.gI().opendShop(player, "SELL_80", false);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.SHOP_SANTA50_VIP) {
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "SANTA", false);
                            break;
                        case 1:
                            // Service.gI().chat(player, "Chức năng đang được cập nhât!");
                            ShopService.gI().opendShop(player, "SELL_50", false);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.SHOP_SANTA50_80) {
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "SANTA", false);
                            break;
                        case 1:
                            // Service.gI().chat(player, "Chức năng đang được cập nhât!");
                            ShopService.gI().opendShop(player, "SELL_50", false);
                            break;
                        case 2:
                            // Service.gI().chat(player, "Chức năng đang được cập nhât!");
                            ShopService.gI().opendShop(player, "SELL_80", false);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_KEO_HALLOWEEN) {
                    switch (select) {
                        case 0:
                            Item KeoBanTay = ItemService.gI().createNewItem((short) 901, 1);
                            KeoBanTay.addOptionParam(86, 0);
                            KeoBanTay.addOptionParam(93, 35);
                            int quality = Util.nextInt(1, 3);
                            KeoBanTay.quantity = quality;
                            InventoryService.gI().addItemBag(player, KeoBanTay);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().chat(player, "Haha xin được " + quality + " kẹo bàn tay rồi");
                            player.NhanKeoHayBiGheoNpc_10++;
                            break;
                        case 1:
                            player.NhanKeoHayBiGheoNpc_10++;
                            break;
                    }
                }
            }
        }
    }

    private void kiemTraVaBatDauXemLichSuGiaoDich(Player player) {
        if (player == null) {
            return;
        }
        int gem = player.inventory.getRuby();
        if (gem < 1) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc, còn thiếu " + (1 - gem) + " hồng ngọc nữa");
            return;
        }
        player.inventory.subRuby(1);
        batDauXemLichSuGiaoDich(player);
    }

    private void batDauXemLichSuGiaoDich(Player player) {
        List<HistoryTransactionDAO.TransactionLog> tatCaLog = HistoryTransactionDAO.getHistoryForPlayer(player, 50);

        if (tatCaLog == null || tatCaLog.isEmpty()) {
            Service.gI().sendThongBao(player, "Bạn chưa có lịch sử giao dịch nào.");
            if (player.lichSuGiaoDichDangXem != null) {
                player.lichSuGiaoDichDangXem = null;
            }
            return;
        }

        player.lichSuGiaoDichDangXem = tatCaLog;
        player.trangThaiLichSuGd = 1;
        hienThiTrangLichSuGiaoDich(player);
    }

    private boolean appendTransactionItems(StringBuilder sb, String rawItemsString, String timestamp, String actionPrefix, String otherPlayerName) {
        if (rawItemsString == null || rawItemsString.trim().isEmpty()) {
            return false;
        }

        String[] items = rawItemsString.split(",");
        boolean daAppendGiDo = false;

        for (String itemEntry : items) {
            itemEntry = itemEntry.trim();
            if (itemEntry.isEmpty()) {
                continue;
            }

            if (itemEntry.startsWith("Gold:")) {
                try {
                    String goldValueStr = itemEntry.substring("Gold:".length()).trim();
                    long goldAmount = Long.parseLong(goldValueStr);
                    if (goldAmount > 0) {
                        String formattedGold = String.format("%,d", goldAmount).replace(",", ".");
                        sb.append(String.format("[%s] %s %s: %s vàng\n", timestamp, actionPrefix, otherPlayerName, formattedGold));
                        daAppendGiDo = true;
                    }
                } catch (NumberFormatException e) {
                }
            } else {
                sb.append(String.format("[%s] %s %s: %s\n", timestamp, actionPrefix, otherPlayerName, itemEntry));
                daAppendGiDo = true;
            }
        }
        return daAppendGiDo;
    }

    private void hienThiTrangLichSuGiaoDich(Player player) {
        if (player.lichSuGiaoDichDangXem.isEmpty()) {
            Service.gI().sendThongBao(player, "Bạn không có lịch sử giao dịch nào.");
            return;
        }
        List<HistoryTransactionDAO.TransactionLog> tatCaLog = player.lichSuGiaoDichDangXem;
        int trangHienTai = player.trangThaiLichSuGd;
        int tongSoLog = tatCaLog.size();
        int tongSoTrang = (int) Math.ceil((double) tongSoLog / 1);

        if (trangHienTai < 1) {
            trangHienTai = 1;
        }
        if (trangHienTai > tongSoTrang && tongSoTrang > 0) {
            trangHienTai = tongSoTrang;
        }
        player.trangThaiLichSuGd = trangHienTai;

        int startIndex = (trangHienTai - 1) * 1;
        int endIndex = Math.min(startIndex + 1, tongSoLog);

        List<HistoryTransactionDAO.TransactionLog> logTrangNay = new ArrayList<>();
        if (startIndex >= 0 && startIndex < endIndex && endIndex <= tongSoLog) {
            logTrangNay = tatCaLog.subList(startIndex, endIndex);
        }

        StringBuilder noiDungTrang = new StringBuilder();
        String dinhDanhNguoiChoiHienTai = player.name + " (" + player.id + ")";

        if (!logTrangNay.isEmpty()) {
            for (HistoryTransactionDAO.TransactionLog log : logTrangNay) {
                String thoiGianFormat = TimeUtil.formatTime(log.transactionTime.getTime(), "yyyy-MM-dd HH:mm:ss");
                String tenNguoiChoiKhac;
                String vpPlayer1TrongLog = log.itemsExchangedByPlayer1;
                String vpPlayer2TrongLog = log.itemsExchangedByPlayer2;
                String vpBanDuaThucTe;
                String vpBanNhanThucTe;

                if (log.player1NameRecord.equals(dinhDanhNguoiChoiHienTai)) {
                    tenNguoiChoiKhac = log.player2NameRecord;
                    vpBanDuaThucTe = vpPlayer1TrongLog;
                    vpBanNhanThucTe = vpPlayer2TrongLog;
                } else {
                    tenNguoiChoiKhac = log.player1NameRecord;
                    vpBanDuaThucTe = vpPlayer2TrongLog;
                    vpBanNhanThucTe = vpPlayer1TrongLog;
                }

                boolean coGiDua = appendTransactionItems(noiDungTrang, vpBanDuaThucTe, thoiGianFormat, "Cho", tenNguoiChoiKhac);
                boolean coGiNhan = appendTransactionItems(noiDungTrang, vpBanNhanThucTe, thoiGianFormat, "Nhận từ", tenNguoiChoiKhac);

                if (!coGiDua && !coGiNhan) {
                }
                if (coGiDua || coGiNhan) {
                    noiDungTrang.append("\n");
                }
            }
        }

        if (noiDungTrang.length() > 0 && noiDungTrang.charAt(noiDungTrang.length() - 1) == '\n') {
            noiDungTrang.setLength(noiDungTrang.length() - 1);
        }

        String loiNpcNoi = noiDungTrang.toString().trim();
        if (loiNpcNoi.isEmpty()) {
            if (tongSoLog == 0) {
                loiNpcNoi = "Bạn không có lịch sử giao dịch nào.";
            } else {
                loiNpcNoi = "Không có thông tin giao dịch để hiển thị trên trang này.";
            }
        }

        List<String> tuyChonMenu = new ArrayList<>();
        if (tongSoTrang > 0) {
            String trangInfo = String.format("[%d/%d]", trangHienTai, tongSoTrang);
            if (trangHienTai > 1) {
                tuyChonMenu.add("Quay lại " + trangInfo);
            }
            if (trangHienTai < tongSoTrang) {
                tuyChonMenu.add("Tiếp theo " + trangInfo);
            }
        }
        tuyChonMenu.add("Đóng");

        this.createOtherMenu(player, ConstNpc.MENU_XEM_LS_GIAO_DICH, loiNpcNoi, tuyChonMenu.toArray(String[]::new));
    }

    private void xuLyPhanTrangLichSu(Player player, int luaChonSelect) {
        if (player.lichSuGiaoDichDangXem == null) {
            return;
        }
        int trangHienTai = player.trangThaiLichSuGd;
        int tongSoTrang = (int) Math.ceil((double) player.lichSuGiaoDichDangXem.size() / 1);

        List<String> cacTuyChonDaHienThi = new ArrayList<>();
        if (tongSoTrang > 0) {
            if (trangHienTai > 1) {
                cacTuyChonDaHienThi.add("Quay lại");
            }
            if (trangHienTai < tongSoTrang) {
                cacTuyChonDaHienThi.add("Tiếp theo");
            }
        }
        cacTuyChonDaHienThi.add("Đóng");

        if (luaChonSelect < 0 || luaChonSelect >= cacTuyChonDaHienThi.size()) {
            player.lichSuGiaoDichDangXem = null;
            return;
        }

        String hanhDong = cacTuyChonDaHienThi.get(luaChonSelect);

        if (hanhDong.startsWith("Quay lại")) {
            player.trangThaiLichSuGd--;
            hienThiTrangLichSuGiaoDich(player);
        } else if (hanhDong.startsWith("Tiếp theo")) {
            player.trangThaiLichSuGd++;
            hienThiTrangLichSuGiaoDich(player);
        } else if (hanhDong.equals("Đóng")) {
            player.lichSuGiaoDichDangXem = null;
        } else {
            player.lichSuGiaoDichDangXem = null;
        }
    }
}
