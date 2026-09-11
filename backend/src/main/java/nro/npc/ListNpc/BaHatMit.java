package nro.npc.ListNpc;


import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstDailyGift;
import consts.ConstMenu;
import consts.ConstNpc;
import event.EventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.Item.Item;
import models.Item.ItemService;
import nro.combine.CombineService;
import nro.combine.ListCombine.CheTaoCuonSachCu;
import nro.combine.ListCombine.DoiSachTuyetKy;
import nro.combine.ListCombine.NangCapVatPham;
import nro.map.DeathOrAliveArena.DeathOrAliveArena;
import nro.map.DeathOrAliveArena.DeathOrAliveArenaManager;
import nro.map.DeathOrAliveArena.DeathOrAliveArenaService;
import nro.npc.Npc;
import nro.player.DailyGift.DailyGiftService;
import nro.player.Player;
import nro.shop.ShopService;

public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_14 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN, "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                            "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_14 == 0) {
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
            switch (this.mapId) {
                case 5:
                    List<String> menuOptions = new ArrayList<>(Arrays.asList(
                            "Chức năng\nPha lê",
                            "Nâng cấp\nTrang bị",
                            "Võ đài\nSinh Tử",
//                            "Map địa\nngục",
                            "Nâng cấp đệ Tử",
                            "Chức năng khác"
                    ));

                    if (EventManager.LUNNAR_NEW_YEAR) {
                        menuOptions.add("Tặng mâm Ngũ Quả");
                    }

                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", menuOptions.toArray(String[]::new));
                    break;
                case 112: {
                    if (Util.isAfterMidnight(player.lastTimePKVoDaiSinhTu)) {
                        player.haveRewardVDST = false;
                        player.thoiVangVoDaiSinhTu = 0;
                    }
                    if (player.haveRewardVDST) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đây là phần thưởng cho con.", "1 ngọc bí\nbất kì", "1 bí ngô");
                        return;
                    }
                    if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn hủy đăng ký thi đấu võ đài?", "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                            return;
                        }
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó", "Top 100", "Bình chọn", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                        return;
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                            "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                    break;
                }
                case 174:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Quay về", "Từ chối");
                    break;
                case 181:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Quay về", "Từ chối");
                    break;
                default: {
                    List<String> menu = new ArrayList<>(Arrays.asList(
                            "Sách\nTuyệt Kỹ",
                            "Cửa hàng\nBùa",
                            "Nâng cấp\nVật phẩm",
                            "Làm phép\nNhập đá",
                            "Nhập\nNgọc Rồng"
                          
                    ));

                    // Nếu có bất kỳ BT1/2/3 thì chèn "Bông tai Porata" vào slot 4 (index 3)
                    if (InventoryService.gI().findItem(player, 454) // BT1
                            || InventoryService.gI().findItem(player, 921) // BT2
                            || InventoryService.gI().findItem(player, 1943)) { // BT3
                        menu.add(3, "Bông tai\nPorata"); // ĐẢM BẢO Ô SỐ 4 LUÔN LÀ BÔNG TAI
                    }

                    if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                        menu.add(0, "Thưởng\nBùa 1h\nngẫu nhiên");
                    }

                    String[] menus = menu.toArray(new String[0]);
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", menus);
                    break;
                }

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
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
                            player.NhanLiXiForNPC_14++;
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
            } else if (EventManager.HALLOWEEN) {
                if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_KEO_HALLOWEEN) {
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
                            player.NhanKeoHayBiGheoNpc_14++;
                            break;
                        case 1:
                            player.NhanKeoHayBiGheoNpc_14++;
                            break;
                    }
                    return;
                }
            }
            switch (this.mapId) {
                case 5: {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU: {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstMenu.MENU_PHA_LE, "Ta có thể giúp gì cho ngươi ?",
                                            "Ép sao\ntrang bị", "Pha lê\nhoá\ntrang bị"
//                                            , "Nâng cấp\nSao pha lê", "Đánh bóng\nSao pha lê", "Cường hoá\nLỗ sao\npha lê", "Tạo đá Hematite"
                                    );
                                    break;
                                case 1://Chuyển hoá trang bị
                                    createOtherMenu(player, ConstMenu.MENU_NANG_CAP_TRANG_BI, "Ta sẽ biến trang bị của ngươi thành trang bị Kích Hoạt",
                                            "Nâng đồ Hủy diệt","Kích hoạt\nThường", "Kích hoạt\nVIP");
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 112, 200 + Util.nextInt(-100, 100), 408);
                                    break;
//                                case 3:
//                                    ChangeMapService.gI().changeMapNonSpaceship(player, 167, 200 + Util.nextInt(-100, 100), 408);
//                                    break;
                                case 3:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_DE_TU);
                                    break;

                                case 4:
                                    createOtherMenu(player, ConstMenu.CHUC_NANG_BHM_KHAC, "Ngươi tìm ta có việc gì?\n",
                                            //"Đập đồ\nẢo Hóa",
                                            "Build Đồ",
                                            "Pháp sư hoá",
                                            "Siêu hóa\n Cải trang",
                                            "Tinh ấn\ntrang bị",
//                                            "Tinh thạch\ntrang bị",
                                            "Nâng cấp\nGiáp LT"
                                    );
                                    break;
                                case 5:
                                    if (EventManager.LUNNAR_NEW_YEAR) {
                                        Item MamNguQua = InventoryService.gI().findItemBag(player, (short) 1182);
                                        if (MamNguQua == null) {
                                            Service.getInstance().sendThongBao(player, "Bạn không có mâm ngũ quả");
                                        } else {
                                            InventoryService.gI().subQuantityItemsBag(player, MamNguQua, 1);
                                            Item GoiQuaDacBiet = ItemService.gI().createNewItem((short) 1184);
                                            GoiQuaDacBiet.addOptionParam(30, 0);
                                            GoiQuaDacBiet.addOptionParam(93, 30);
                                            InventoryService.gI().addItemBag(player, GoiQuaDacBiet);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Bạn nhận được " + GoiQuaDacBiet.template.name);
                                        }
                                    }
                                    break;
                            }
                            break;
                        }
                        case ConstMenu.CHUC_NANG_BHM_KHAC: {
                            switch (select) {
//                                case 0:
//                                    CombineService.gI().openTabCombine(player, CombineService.DAP_DO_AO_HOA);
//                                    break;
                                case 0:
                                    this.createOtherMenu(player, ConstMenu.BUILD_DO_BHM, "|7|\bChi Tiết:\n|0|"
                                            + "\nMở Khóa GD: tỉ lệ thành công 30%"
                                            + "\nGia hạn Vật Phẩm: tỉ lệ thành công 30% + 3 - 7 ngày, 70% + 1 ngày"
                                            + "\nTẩy đồ: tẩy sao pha lê, chỉ số đặc biệt một số trang bị",
//                                            "Mở Khóa GD",
                                            "Gia hạn\n Vật Phẩm" ,
                                            "Tẩy Đồ"
                                    );
                                    break;
                                case 1:
                                    CombineService.gI().openTabCombine(player, CombineService.PS_HOA_TRANG_BI);
                                    break;
                                case 2:
                                    CombineService.gI().openTabCombine(player, CombineService.SIEU_HOA);
                                    break;
                                case 3:
                                    CombineService.gI().openTabCombine(player, CombineService.AN_TRANG_BI);
                                    break;
//                                case 4:
//                                    CombineService.gI().openTabCombine(player, CombineService.TINH_THACH_HOA);
//                                    break;
                                case 4:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_GIAP_LUYEN_TAP);
                                    break;
                            }
                            break;
                        }
                        case ConstMenu.BUILD_DO_BHM: {
                            switch (select) {
//                                case 0: //Mở khóa Item
//                                    CombineService.gI().openTabCombine(player, CombineService.MO_KHOA_ITEM);
//                                    break;
                                case 0: //Gia hạn vật phẩm
                                    CombineService.gI().openTabCombine(player, CombineService.GIA_HAN_VAT_PHAM);
                                    break;
                                case 1: //Tẩy đồ
                                    CombineService.gI().openTabCombine(player, CombineService.TAY_PS_HOA_TRANG_BI);
                                    break;
                            }
                            break;
                        }
                        case ConstMenu.SHOP_BHM: {
                            switch (select) {
                                case 0:
                                    Service.gI().sendThongBao(player, "Chưa mở");
                                    break;
                                case 1:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_CHAN_MENH);
                                    break;
                                case 2:
                                    ShopService.gI().opendShop(player, "SHOP_BHM", false);
                                    break;
                                case 3:
                                    ShopService.gI().opendShop(player, "SHOP_THOI_BHM", false);
                                    break;
                            }
                            break;
                        }
                        case ConstMenu.MENU_PHA_LE: {
                            switch (select) {
                                case 0: //Ép sao trang bị
                                    CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                    break;
                                case 1: //Pha lê hoá trang bị
                                    createOtherMenu(player, ConstMenu.MENU_PHA_LE_HOA_TRANG_BI, "Ngươi muốn pha lê hoá trang bị bằng cách nào?", "Bằng ngọc", "Từ chối");
                                    break;
                                case 2: //Nâng cấp sao pha lê
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SAO_PHA_LE);
                                    break;
                                case 3: //Đánh bóng sao pha lê
                                    CombineService.gI().openTabCombine(player, CombineService.DANH_BONG_SAO_PHA_LE);
                                    break;
                                case 4: //Cường hoá lỗ sao pha lê
                                    CombineService.gI().openTabCombine(player, CombineService.CUONG_HOA_LO_SAO_PHA_LE);
                                    break;
                                case 5: //Tạo đá Hematite
                                    CombineService.gI().openTabCombine(player, CombineService.TAO_DA_HEMATITE);
                                    break;
                            }
                            break;
                        }
                        case ConstMenu.MENU_NANG_CAP_TRANG_BI: {
                            switch (select) {
                                case 0:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_DO_HD);
                                    break;
                                case 1:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_KICH_HOAT);
                                    break;
                                case 2:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_KICH_HOAT_VIP);
                                    break;
                            }
                            break;
                        }
                        case ConstMenu.MENU_CHUYEN_HOA_TRANG_BI: {
                            switch (select) {
                                case 0: {
                                    CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_TRANG_BI_DUNG_VANG);
                                    break;
                                }
                                case 1: {
                                    CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_TRANG_BI_DUNG_NGOC);
                                    break;
                                }
                            }
                            break;
                        }
                        case ConstMenu.MENU_PHA_LE_HOA_TRANG_BI: {
                            if (select == 0) {
                                CombineService.gI().openTabCombine(player, CombineService.PHA_LE_HOA_TRANG_BI);
                            }
                            break;
                        }
                        case ConstNpc.MENU_START_COMBINE: {
                            switch (player.combine.typeCombine) {
                                case CombineService.PHA_LE_HOA_TRANG_BI: {
                                    switch (select) {
                                        case 0:
                                            CombineService.gI().startCombine(player, 100);
                                            break;
                                        case 1:
                                            CombineService.gI().startCombine(player, 10);
                                            break;
                                        case 2:
                                            CombineService.gI().startCombine(player);
                                            break;
                                    }
                                    break;
                                }

                                case CombineService.NANG_CAP_KICH_HOAT_VIP:
                                case CombineService.NANG_CAP_KICH_HOAT:
                                case CombineService.NANG_CAP_SAO_PHA_LE:
                                case CombineService.DANH_BONG_SAO_PHA_LE:
                                case CombineService.CUONG_HOA_LO_SAO_PHA_LE:
                                case CombineService.TAO_DA_HEMATITE:
                                case CombineService.EP_SAO_TRANG_BI:
                                case CombineService.DAP_DO_AO_HOA:
                                case CombineService.PS_HOA_TRANG_BI:
                                case CombineService.TAY_PS_HOA_TRANG_BI:
                                case CombineService.SIEU_HOA:
                                case CombineService.AN_TRANG_BI:
                                case CombineService.TINH_THACH_HOA:
                                case CombineService.NANG_GIAP_LUYEN_TAP:
                                case CombineService.MO_KHOA_ITEM:
                                case CombineService.GIA_HAN_VAT_PHAM:
                                case CombineService.NANG_CAP_CHAN_MENH:
                                case CombineService.NANG_CAP_DE_TU:
                                case CombineService.NANG_CAP_DO_HD:{

                                    switch (select) {
                                        case 0:
                                            CombineService.gI().startCombine(player);
                                            break;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 112: {
                    if (player.iDMark.isBaseMenu()) {
                        if (player.haveRewardVDST) {
                            switch (select) {
                                case 0: {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) (Util.nextInt(705, 708)));
                                        item.addOptionParam(93, 30);
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                    break;
                                }
                                case 1: {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) 585);
                                        item.addOptionParam(93, 30);
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                    break;
                                }
                            }
                            return;
                        }
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                                switch (select) {
                                    case 0: {
//                                        TopService.showListTop(player, 5);
                                        break;
                                    }
                                    case 1:
                                        this.npcChat("Không thể thực hiện");
                                        break;
                                    case 2: {
                                        break;
                                    }
                                    case 3:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                        break;
                                }
                                return;
                            }
                            switch (select) {
                                case 0: {
//                                    TopService.showListTop(player, 5);
                                    break;
                                }
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.DAT_CUOC_HAT_MIT, "Phí bình chọn là 1 triệu vàng\nkhi trận đấu kết thúc\n90% tổng tiền bình chọn sẽ chia đều cho phe bình chọn chính xác", "Bình chọn cho " + DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().name + " (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocPlayer() + ")", "Bình chọn cho hạt mít (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocBaHatMit() + ")");
                                    break;
                                case 2:
                                    DeathOrAliveArenaService.gI().startChallenge(player);
                                    break;
                                case 3: {
                                    break;
                                }
                                case 4:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                    break;
                            }
                            return;
                        }
                        switch (select) {
                            case 0: {
//                                TopService.showListTop(player, 5);
                                break;
                            }
                            case 1:
                                DeathOrAliveArenaService.gI().startChallenge(player);
                                break;
                            case 2: {
                                break;
                            }
                            case 3:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DAT_CUOC_HAT_MIT) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            switch (select) {
                                case 0: {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocPlayer(vdst.getCuocPlayer() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonPlayer++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.formatNumber(1_000_000 - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng nữa");
                                    }
                                    break;
                                }
                                case 1: {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocBaHatMit(vdst.getCuocBaHatMit() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonHatMit++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.formatNumber(1_000_000 - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng nữa");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case 174:
                case 181: {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                break;
                        }
                    }
                    break;
                }
                case 42:
                case 43:
                case 44:
                case 84: {
                    if (player.iDMark.isBaseMenu()) {
                        if (!DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                            select++;
                        }
                        if (!InventoryService.gI().findItem(player, 454) // BT1
                                && !InventoryService.gI().findItem(player, 921) // BT2
                                && !InventoryService.gI().findItem(player, 1943)) {  // BT3
                            if (select >= 4) {
                                select++;
                            }
                        }

                        switch (select) {
                            case 0:
                                if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                                    int idItem = Util.nextInt(213, 219);
                                    player.charms.addTimeCharms(idItem, 60);
                                    Item bua = ItemService.gI().createNewItem((short) idItem);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận thưởng " + bua.template.name);
                                    DailyGiftService.updateDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI);
                                } else {
                                    Service.gI().sendThongBao(player, "Hôm nay bạn đã nhận bùa miễn phí rồi!!!");
                                }
                                break;
                            case 1:
                                createOtherMenu(player, ConstNpc.MENU_SACH_TUYET_KY, "Ta có thể giúp gì cho ngươi ?",
                                        "Đóng thành\nSách cũ",
                                        "Đổi Sách\nTuyệt kỹ",
                                        "Giám định\nSách",
                                        "Tẩy\nSách",
                                        "Nâng cấp\nSách\nTuyệt kỹ",
                                        "Hồi phục\nSách",
                                        "Phân rã\nSách");
                                break;
                            case 2:
                                createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA, "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để " + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                        "Bùa\n1 giờ",
                                        "Bùa\n8 giờ",
                                        "Bùa\n1 tháng",
                                        "Đóng");//"Bùa\nđệ tử",
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_VAT_PHAM);
                                break;
                            case 4: { // Bông tai Porata
                                if (InventoryService.gI().findItem(player, 454)) {
                                    // Có BT1 -> mở tab nâng BT2
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);

                                } else if (InventoryService.gI().findItem(player, 921)) {
                                    // Có BT2 -> submenu cho 2 chức năng
                                    this.createOtherMenu(player, ConstMenu.MENU_BONG_TAI,
                                            "Ngươi muốn làm gì với Bông Tai [+2]?",
                                            "Nâng cấp lên BT3",
                                            "Mở chỉ số BT2",
                                            "Từ chối");

                                } else if (InventoryService.gI().findItem(player, 1943)) {
                                    // Có BT3 -> mở tab mở chỉ số BT3
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI_3);
                                }
                                break;
                            }

                            case 5:
                                CombineService.gI().openTabCombine(player, CombineService.LAM_PHEP_NHAP_DA);
                                break;
                            case 6:
                                CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_SACH_TUYET_KY) {
                        switch (select) {
                            case 0:
                                CheTaoCuonSachCu.showCombine(player);
                                break;
                            case 1:
                                DoiSachTuyetKy.showCombine(player);
                                break;
                            case 2:
                                CombineService.gI().openTabCombine(player, CombineService.GIAM_DINH_SACH);
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.TAY_SACH);
                                break;
                            case 4:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SACH_TUYET_KY);
                                break;
                            case 5:
                                CombineService.gI().openTabCombine(player, CombineService.HOI_PHUC_SACH);
                                break;
                            case 6:
                                CombineService.gI().openTabCombine(player, CombineService.PHAN_RA_SACH);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstMenu.MENU_BONG_TAI) {
                        // Submenu cho Bông Tai [+2]
                        switch (select) {
                            case 0: // Nâng BT3
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);
                                break;
                            case 1: // Mở chỉ số BT2
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI);
                                break;
                            default:
                                // Từ chối / đóng
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DONG_THANH_SACH_CU) {
                        CheTaoCuonSachCu.cheTaoCuonSachCu(player);
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DOI_SACH_TUYET_KY) {
                        DoiSachTuyetKy.doiSachTuyetKy(player);
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                        switch (select) {
                            case 0:
                                ShopService.gI().opendShop(player, "BUA_1H", false);
                                break;
                            case 1:
                                ShopService.gI().opendShop(player, "BUA_8H", false);
                                break;
                            case 2:
                                ShopService.gI().opendShop(player, "BUA_1M", false);
                                break;
//                            case 3:
//                                ShopService.gI().opendShop(player, "BUA_DETU", false);
//                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combine.typeCombine) {
                            case CombineService.NANG_CAP_BONG_TAI:
                            case CombineService.NANG_CHI_SO_BONG_TAI:
                            case CombineService.NANG_CHI_SO_BONG_TAI_3: // ✅ mở chỉ số BT3
                            case CombineService.LAM_PHEP_NHAP_DA:
//                            case CombineService.NHAP_NGOC_RONG:
                            case CombineService.GIAM_DINH_SACH:
                            case CombineService.TAY_SACH:
                            case CombineService.NANG_CAP_SACH_TUYET_KY:
                            case CombineService.HOI_PHUC_SACH:
                                case CombineService.NHAP_NGOC_RONG: {
        switch (select) {
            case 0 -> player.nhapNgocRongTimes = 1;
            case 1 -> player.nhapNgocRongTimes = 10;
            case 2 -> player.nhapNgocRongTimes = 100;
            default -> { return; }
        }
        CombineService.gI().startCombine(player);
        break;
    }

                            case CombineService.PHAN_RA_SACH: {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                }
                                break;
                            }
                            case CombineService.NANG_CAP_VAT_PHAM: {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                } else if (select == 1) {
                                    NangCapVatPham.nangCapVatPham(player, true);
                                }
                                break;
                            }
                        }
                    }
                    break;
                }

                default: {
                    break;
                }
            }
        }
    }
}






