package nro.services.Fun;

import nro.bot.NewBot;
import nro.bot.ShopBot;
import consts.ConstNpc;
import jbcd.dao.PlayerDAO;
import models.Item.Item;
import nro.map.Zone;
import nro.npc.Npc;
import nro.npc.NpcManager;
import nro.player.Player;
import network.io.Message;
import nro.inventory.Inventory;
import nro.inventory.InventoryService;
import static nro.npc.NpcFactory.PLAYERID_OBJECT;
import nro.server.Client;
import nro.server.Manager;
import nro.server.ServerNotify;
import nro.services.DetuService;
import nro.services.Service;
import models.Item.ItemService;
import models.Item.ActivationSetCatalog;
import nro.services.NapThe;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.TaskService;
import Utils.FormatStyle;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstDetu;
import static consts.ConstNpc.QUY_DOI_HN;
import consts.ConstPlayer;
import event.BakeACake.NauBanh_NewYear;
import jbcd.ConnectDB;
import network.interfaces.ISession;
import nro.clan.Clan;
import nro.clan.ClanMember;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nro.giftcode.GiftCodeService;
import nro.clan.ClanService;
import nro.minigame.MiniGame;
import nro.pariry.PariryServices;
import models.Item.ItemOption;
import nro.minigame.TaiXiu;
import jbcd.CrisResultSet;
import jbcd.data.DatabaseUpdater;
import nro.bot.BotManager;
import nro.services.ChatGlobalService;
import nro.services.MapService;
import nro.minigame.ChanLe;

public class Input {

    public static String LOAI_THE;
    public static String MENH_GIA;
    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int TANG_NGOC_HONG = 837;
    public static final int CHOOSE_LEVEL_GAS = 515;
    public static final int CHOOSE_LEVEL_KGHD = 5143;
    public static final int CHOOSE_LEVEL_CDRD = 51522;
    public static final int VE_TANG_NGOC = 519;
    public static final int VE_TANG_HONG_NGOC = 520;
    public static final int DOI_THOI_VANG = 516;
    public static final int DOI_NGOC_XANH = 517;
    public static final int DOI_NGOC_HONG = 518;
    public static final int QUY_DOI_XU_VANG = 11001;
    public static final int PASS_ADMIN = 508;
    public static final int NAP_TIEN = 600;
    public static final int THONG_BAO = 604;
    public static final int THONG_BAO_RIENG = 605;
    public static final int CON_SO_MAY_MAN_VND = 564;
    public static final int CON_SO_MAY_MAN_NGOC = 507;
    public static final int CON_SO_MAY_MAN_VANG = 512;
    public static final int NAU_BANH_CHUNG = 606;
    public static final int NAU_BANH_TET = 607;
     public static final int SEND_ITEM_OP_VIP = 608;
     public static final int SEND_ITEM_OP_VIP1 = 609;
    

    public static final int SEND_ITEM_OP = 513;
    public static final int SEND_ITEM_SKH = 514;
    public static final int XIU_MD5 = 5164;
    public static final int TAI_MD5 = 5165;
    

    public static final int LOA_TO_THE_GIOI = 2005;
    public static final int LOA_TO_VU_TRU = 2006;
    public static final int BOTQUAI = 2007;
    public static final int BOTITEM = 2008;
    public static final int BOTBOSS = 2009;
    public static final int GIVE_VND = 2010;
    public static final int NEXTNHIEMVU = 2011;
    public static final int THUITEM = 2012;
    public static final int GIVE_IT = 2013;
    public static final int GIVE_IT_OPTION = 2014;
    public static final int GIVE_IT_NPOINT_DETU = 2015;
    public static final int SUB_NPOINT_DETU = 2016;
    public static final int CHAT_ALL = 2017;
    public static final int ACTIVE = 2018;
    public static final int THAY_EXP = 2019;
    public static final int LE = 2020;
    public static final int CHAN = 2021;
    public static final int DISSOLUTION_CLAN = 2022;
    public static final int TNSM_DETU = 2023;
    public static final int FIND_ACCOUNT = 2024;
    public static final int GIVE_HONGNGOC = 2025;
    public static final int CREATE_NGUOI_YEU = 2026;
    public static final int DROP_ITEM = 2027;
    public static final int CHANGE_PET = 2028;
    public static final int CREATE_FATHER = 2029;
    public static final int CREATE_MOTHER = 2030;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;
    public static final byte MBV = 23;
    public static final byte SLLTV = 24;
    public static final byte BANGHOI = 25;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case MBV:
                    int mbv = Integer.parseInt(text[0]);
                    int nmbv = Integer.parseInt(text[1]);
                    int rembv = Integer.parseInt(text[2]);
                    if ((mbv + "").length() != 6 || (nmbv + "").length() != 6 || (rembv + "").length() != 6) {
                        Service.gI().sendThongBao(player, "Trêu bố mày à?");
                    } else if (player.mbv == 0) {
                        Service.gI().sendThongBao(player, "Bạn chưa cài mã bảo vệ!");
                    } else if (player.mbv != mbv) {
                        Service.gI().sendThongBao(player, "Mã bảo vệ không đúng");
                    } else if (nmbv != rembv) {
                        Service.gI().sendThongBao(player, "Mã bảo vệ không trùng khớp");
                    } else {
                        player.mbv = nmbv;
                        Service.gI().sendThongBao(player, "Đổi mã bảo vệ thành công!");
                    }
                    break;
                case ConstNpc.QUY_DOI_HN: {
                    try {
                        int soLuong = Integer.parseInt(text[0].trim());
                        if (soLuong <= 0) {
                            Service.gI().sendThongBao(player, "Số lượng không hợp lệ!");
                            return;
                        }

                        // Tìm thỏi vàng trong túi
                        Item thoiVang = InventoryService.gI().findItemBag(player, (short) 457);
                        if (thoiVang == null || thoiVang.quantity < soLuong) {
                            Service.gI().sendThongBao(player, "Bạn không có đủ Thỏi vàng!");
                            return;
                        }

                        // Quy đổi: 1 Thỏi = 30 Hồng ngọc
                        int rubyNhan = soLuong * 100;

                        // Giới hạn tránh tràn số hoặc vượt quá 2 tỷ Ruby
                        if (player.inventory.ruby + rubyNhan > 2_000_000_000) {
                            Service.gI().sendThongBao(player, "Vượt quá giới hạn Hồng ngọc (2 tỷ)!");
                            return;
                        }

                        // Thực hiện quy đổi
                        InventoryService.gI().subQuantityItemsBag(player, thoiVang, soLuong);
                        InventoryService.gI().sendItemBag(player);

                        player.inventory.addRuby(rubyNhan);
                        Service.gI().sendMoney(player);

                        Service.gI().sendThongBao(player, "Đã quy đổi " + soLuong + " Thỏi vàng thành " + rubyNhan + " Hồng ngọc.");
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Vui lòng nhập số hợp lệ!");
                    }
                    break;
                }
                case QUY_DOI_XU_VANG: {
                    

                    long tongNap2 = player.getSession().coin;
                    long soHopNhan = tongNap2 / 20000; // 20k = 1 hộp

                    if (soHopNhan <= 0) {
                        Service.getInstance().sendThongBao(player, "|7|Bạn chưa đủ 20.000 Coin để nhận 1 Hộp Rocket Ball");
                        return;
                    }

                    // Nhân khuyến mãi nếu có (x2, x3,...)
                    int soLuongNhan = (int) (soHopNhan);

                    // Tạo vật phẩm Hộp Bill (ID 1526)
                    Item hopBill = ItemService.gI().createNewItem((short) 1922, soLuongNhan);
                    InventoryService.gI().addItemBag(player, hopBill);
                    InventoryService.gI().sendItemBag(player);

                    // Tính số coin cần trừ
                    long coinDaTru = soHopNhan * 20000;

                    // Trừ vào tổng nạp (DB + RAM)
                    PlayerDAO.subTongNap2(player, coinDaTru);

                    // Thông báo kết quả
                    Service.getInstance().sendThongBao(player,
                            "|7|Bạn đã nhận được " + Util.format(soLuongNhan) + " Hộp Cải Trang Rocket!"
                            + "\n|1|Đã trừ: " + Util.format(coinDaTru) + " Coin nạp (tổng nạp còn lại: "
                            + Util.format(player.getSession().tongnap2) + ")");
                    break;
                }

                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc muốn đến\nhang kho báu cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }

                    break;
                case CHOOSE_LEVEL_KGHD:
                    level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.MR_POPO, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, 2,
                                    "Cậu có chắc muốn đến\nDestron Gas cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    }
                    break;
                case CHOOSE_LEVEL_CDRD:
                    level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, 3,
                                    "Con có chắc muốn đến\ncon đường rắn độc cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    }
                    break;
                case DISSOLUTION_CLAN: {
                    String xacNhan = text[0];
                    if (xacNhan.equalsIgnoreCase("OK")) {
                        Clan clan = player.clan;
                        if (clan.isLeader(player)) {
                            for (Player member : clan.membersInGame) {
                                if (member != null && MapService.gI().isMapBangHoi(member.zone.map.mapId)) {
                                    ChangeMapService.gI().changeMap(member, 5, -1, Util.nextInt(1050, 1150), 408);
                                    member.clan = null;
                                    member.clanMember = null;
                                    ClanService.gI().sendMyClan(member);
                                    ClanService.gI().sendClanId(member);
                                    Service.gI().sendThongBao(member, "Bang hội của bạn đã bị giải tán.");
                                }
                            }
                            clan.deleteDB(clan.id);
                            Manager.CLANS.remove(clan);
                            player.clan = null;
                            player.clanMember = null;
                            ClanService.gI().sendMyClan(player);
                            ClanService.gI().sendClanId(player);
                            Service.gI().sendThongBao(player, "Bang hội đã giải tán thành công.");
                        }
                    }
                    break;
                }
                case DROP_ITEM: {
                    String xacNhan = text[0];
                    if (player.itemThrow_Drop != null) {
                        if (xacNhan.equalsIgnoreCase("ok") || xacNhan.equalsIgnoreCase("1")) {
                            InventoryService.gI().removeItemBag(player, player.tempItemIndex);
                            InventoryService.gI().sortItems(player.inventory.itemsBag);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Bạn đã vứt bỏ " + player.itemThrow_Drop.template.name);
                            player.tempItemIndex = -1;
                            player.itemThrow_Drop = null;
                        } else {
                            Service.gI().sendThongBao(player, "Xác nhận không thành công.");
                        }
                    }
                    break;
                }
                case CHANGE_PET: {
                    String xacNhan = text[0];
                    if (xacNhan.equalsIgnoreCase("OK") || xacNhan.equalsIgnoreCase("ok")) {
                        if (player.Detu != null) {
                            if (InventoryService.gI().getCountEmptyBody(player.Detu) < ConstPlayer.QTY_MAX_ITEM_BODY_PET) {
                                Service.gI().sendThongBao(player, "Bạn hãy tháo hết đồ của đệ tử trước đã!");
                                return;
                            }

                            int gender = player.Detu.gender + 1;
                            if (gender > 2) {
                                gender = 0;
                            }

                            int type = player.Detu.typeDeTu;

                            switch (type) {
                                case ConstDetu.MABU:
                                    DetuService.gI().changeMabuPet(player, gender);
                                    break;

                                default:
                                    DetuService.gI().changeNormalPet(player, gender);
                                    break;
                            }

                            // Trừ vật phẩm đổi pet (áp dụng cho tất cả)
                            InventoryService.gI().subQuantityItemsBag(player, player.Item_ChangePet, 1);
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không có đệ tử");
                        }
                        player.Item_ChangePet = null;
                        InventoryService.gI().sendItemBag(player);
                    }
                    break;
                }
                case TNSM_DETU:
                    byte CS = Byte.parseByte(text[0]);
                    try {
                        if (CS >= 1 && CS <= 100) {
                            Manager.TNDETU = CS;
                            Service.gI().sendThongBaoFromAdmin(player, "|7|Bội Số Tăng Tiềm Năng Mỗi Giây Là : " + CS + "\nTổng Là : " + Util.format(CS * 20) + " Điểm Tiềm Năng/Giây");
                        } else {
                            Service.gI().sendThongBao(player, "Bội số ít nhất là 1, cao nhất là 100");
                        }
                    } catch (Exception e) {
                        Service.gI().sendThongBao(player, "Lỗi!");
                    }
                    break;
                case BANGHOI:
                    Clan clan = player.clan;
                    if (clan != null) {
                        ClanMember cm = clan.getClanMember((int) player.id);
                        if (clan.isLeader(player)) {
                            if (clan.canUpdateClan(player)) {
                                String tenvt = text[0];
                                if (!Util.haveSpecialCharacter(tenvt) && tenvt.length() > 1 && tenvt.length() < 5) {
                                    clan.name2 = tenvt;
                                    clan.update();
                                    Service.gI().sendThongBao(player, "[" + tenvt + "] OK");
                                } else {
                                    Service.gI().sendThongBaoOK(player, "Chỉ chấp nhận các ký tự a-z, 0-9 và chiều dài từ 2 đến 4 ký tự");
                                }
                            }
                        }
                    }
                    break;
                case SLLTV: {
                    int sltv;
                    try {
                        sltv = Integer.parseInt(text[0]);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ!");
                        return;
                    }
                    if (sltv <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng bán phải lớn hơn 0!");
                        return;
                    }
                    Item thoiVang = InventoryService.gI().findItemBag(player, (short) 457);
                    if (thoiVang == null || thoiVang.quantity == 0) {
                        Service.gI().sendThongBao(player, "Bạn không có Thỏi vàng để bán!");
                        return;
                    }
                    if (thoiVang.quantity < sltv) {
                        Service.gI().sendThongBao(player, "Bạn chỉ có " + thoiVang.quantity + " Thỏi vàng. Không đủ để bán " + sltv + " thỏi!");
                        return;
                    }
                    long costPerItem = 500_000_000L;
                    long totalCost = sltv * costPerItem;
                    if (player.inventory.gold >= Inventory.LIMIT_GOLD) {
                        player.inventory.gold = Inventory.LIMIT_GOLD;
                        Service.gI().sendMoney(player);
                        Service.gI().sendThongBao(player, "Bạn đã đạt giới hạn vàng, không thể bán thêm!");
                        return;
                    }
                    long newGoldAmount = player.inventory.gold + totalCost;
                    if (newGoldAmount > Inventory.LIMIT_GOLD) {
                        long remainingCapacity = Inventory.LIMIT_GOLD - player.inventory.gold;
                        int maxSellableQuantity = (int) (remainingCapacity / costPerItem);

                        if (maxSellableQuantity < 1) {
                            Service.gI().sendThongBao(player, "Vàng sau khi bán sẽ vượt quá giới hạn. Bạn không thể bán thêm Thỏi vàng nào vào lúc này!");
                        } else {
                            maxSellableQuantity = Math.min(maxSellableQuantity, thoiVang.quantity);
                            Service.gI().sendThongBao(player, "Vàng sau khi bán sẽ vượt giới hạn. Bạn chỉ có thể bán tối đa " + maxSellableQuantity + " Thỏi vàng!");
                        }
                        return;
                    }
                    InventoryService.gI().subQuantityItemsBag(player, thoiVang, sltv);
                    InventoryService.gI().sendItemBag(player);
                    player.inventory.gold += totalCost;
                    Service.gI().sendMoney(player);
                    Service.gI().sendThongBao(player, "Đã bán " + sltv + " Thỏi vàng, thu được " + Util.formatNumber(totalCost, FormatStyle.VIETNAMESE) + " vàng.");
                    break;
                }
                case THONG_BAO: {
                    String contentServer = text[0];
                    Integer typeNotiServer = Integer.valueOf(text[1]);
                    switch (typeNotiServer) {
                        case 1:
                            Service.getInstance().sendThongBaoAllPlayer(contentServer);
                            break;
                        case 2:
                            ServerNotify.gI().notify(contentServer);
                            break;
                        default:
                            Service.getInstance().sendThongBaoAllPlayer(contentServer);
                            break;
                    }
                    break;
                }
                case LOA_TO_THE_GIOI: {
                    Item Loa = InventoryService.gI().findItemBag(player, 1652);
                    if (Loa != null) {
                        String contentServer = text[0];
                        ChatGlobalService.gI().chat(player, contentServer);
                        InventoryService.gI().subQuantityItemsBag(player, Loa, 1);
                        InventoryService.gI().sendItemBag(player);
                    } else {
                        Service.gI().sendThongBao(player, "Bạn không có Loa to thế giới");
                    }
                    break;
                }
                case LOA_TO_VU_TRU: {
                    Item Loa = InventoryService.gI().findItemBag(player, 1653);
                    if (Loa != null) {
                        String contentServer = text[0];
                        Service.gI().sendMessageServer(contentServer);
                        InventoryService.gI().subQuantityItemsBag(player, Loa, 1);
                        InventoryService.gI().sendItemBag(player);
                    } else {
                        Service.gI().sendThongBao(player, "Bạn không có Loa to liên vũ trụ");
                    }
                    break;
                }
                case PASS_ADMIN: {
                    if (player.isFounder()) {
                        if (text.length == 0 || text[0] == null) {
                            Service.gI().sendThongBao(player, "Vui lòng nhập mật khẩu");
                            return;
                        }
                        String matKhauNhap = text[0].trim();
                        String matKhauCuaToi = "190823";
                        if (matKhauNhap.equals(matKhauCuaToi)) {
                            Logger.warning("Player " + player.name + " vừa truy cập vào Quyền Điều Hành Hệ Thống\n");
                            Service.gI().OpenMenuKeyOrAdmin(player);
                        } else {
                            int maxWrongAttempts = 10;
                            int wrongAttempts = player.getWrongPasswordAttempts();

                            if (wrongAttempts >= maxWrongAttempts) {
                                PlayerService.gI().KhoaTaiKhoan(player);
                                Logger.warning("Player " + player.name + " bị ban vì nhập sai mật khẩu Quyền Điều Hành Hệ Thống quá nhiều lần\n");
                            } else {
                                player.increaseWrongPasswordAttempts();
                                Service.gI().sendThongBao(player, "Mật khẩu sai. Bạn còn " + (maxWrongAttempts - wrongAttempts) + " lần thử.\n");
                                Logger.warning("Player " + player.name + " nhập sai mật khẩu Quyền Điều Hành Hệ Thống (" + wrongAttempts + "/" + maxWrongAttempts + ")\n");
                            }
                        }
                    } else {
                        PlayerService.gI().KhoaTaiKhoan(player);
                        Logger.warning("Player " + player.name + " có dấu hiệu bug Quyền Điều Hành Hệ Thống, tiến hành ban\n");
                    }
                    break;
                }
                case CREATE_NGUOI_YEU: {
                    String NameDaoLu = text[0];
                    if (NameDaoLu.length() < 4 || NameDaoLu.length() > 20) {
                        Service.gI().sendThongBao(player,
                                "Không ngắn hơn 4 và dài hơn 20 kí tự, Và cho phép kí tự đặt biệt.");
                        break;
                    }
                    byte typeDaoLu = 0;

                    break;
                }
                case CREATE_FATHER: {
                    String name = text[0];
                    if (name.length() < 4 || name.length() > 20) {
                        Service.gI().sendThongBao(player, "Tên phải dài từ 4 đến 20 ký tự.");
                        break;
                    }

                    break;
                }
                case CREATE_MOTHER: {
                    String name = text[0];
                    if (name.length() < 4 || name.length() > 20) {
                        Service.gI().sendThongBao(player, "Tên phải dài từ 4 đến 20 ký tự.");
                        break;
                    }

                    player.NameMother = name;
                    Service.gI().sendThongBao(player, "Mẹ " + name + " đã đến với bạn!");
                    break;
                }
                case THONG_BAO_RIENG: {
                    Player playerNotice = Client.gI().getPlayerByName(text[2]);
                    Integer typeNotiPrivate = Integer.parseInt(text[1]);
                    String contentPrivate = text[0];
                    if (playerNotice != null) {
                        switch (typeNotiPrivate) {
                            case 1:
                                Service.gI().sendThongBao(playerNotice, contentPrivate);
                                break;
                            case 2:
                                Service.gI().sendThongBaoFromAdmin(playerNotice, contentPrivate);
                                break;
                            default:
                                Service.gI().sendThongBaoFromAdmin(playerNotice, contentPrivate);
                                break;
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                }
                case THUITEM: {
                    Player nameT = (Player) PLAYERID_OBJECT.get(player.id);
                    int idT = Integer.parseInt(text[0]);
                    int qT = Integer.parseInt(text[1]);
                    Item itembag = InventoryService.gI().findItemBag(nameT, idT);
                    Item itembody = InventoryService.gI().findItemBody(nameT, idT);
                    Item itembox = InventoryService.gI().findItemBox(nameT, idT);
                    Item idCheck = ItemService.gI().createNewItem((short) idT);
                    if (nameT != null) {
                        if (itembag != null) {
                            Service.gI().sendThongBaoOK(player, "Thu x" + qT + " (" + itembag.template.name + ") từ player : " + nameT.name);
                            InventoryService.gI().subQuantityItemsBag(nameT, itembag, qT);
                            InventoryService.gI().sendItemBag(nameT);
                        } else if (itembody != null) {
                            Service.gI().sendThongBaoOK(player, "Thu x" + qT + " (" + itembody.template.name + ") từ player : " + nameT.name);
                            InventoryService.gI().subQuantityItemsBody(nameT, itembody, qT);
                            InventoryService.gI().sendItemBag(nameT);
                        } else if (itembox != null) {
                            Service.gI().sendThongBaoOK(player, "Thu x" + qT + " (" + itembox.template.name + ") từ player : " + nameT.name);
                            InventoryService.gI().subQuantityItemsBox(nameT, itembox, qT);
                            InventoryService.gI().sendItemBag(nameT);
                        } else {
                            Service.gI().sendThongBaoOK(player, nameT.name + "\n không sở hữu x" + qT + " (" + idCheck.template.name + ")");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Player không online");
                    }
                    break;
                }
                case GIVE_IT: {
                    String name = text[0];
                    int id = Integer.valueOf(text[1]);
                    int q = Integer.valueOf(text[2]);
                    if (Client.gI().getPlayerByName(name) != null) {
                        Item item = ItemService.gI().createNewItem((short) id, q);
                        InventoryService.gI().addItemBag(Client.gI().getPlayerByName(name), item);
                        InventoryService.gI().sendItemBag(Client.gI().getPlayerByName(name));
                        Service.gI().sendThongBaoOK(player, "Buff " + item.template.name + " to player : " + name);
                        Service.gI().sendThongBao(Client.gI().getPlayerByName(name), "Nhận được " + item.template.name + " từ Admin : " + player.name);
                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;
                }
                case GIVE_IT_OPTION: {
                    String name = text[0];
                    int id2 = Integer.parseInt(text[1]);
                    int op2 = Integer.parseInt(text[2]);
                    int pr2 = Integer.parseInt(text[3]);
                    int q2 = Integer.parseInt(text[4]);

                    if (Client.gI().getPlayerByName(name) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id2));
                        List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) id2);
                        if (!ops.isEmpty()) {
                            item.itemOptions = ops;
                        }
                        item.quantity = q2;
                        item.itemOptions.add(new ItemOption(op2, pr2));
                        InventoryService.gI().addItemBag(Client.gI().getPlayerByName(name), item);
                        InventoryService.gI().sendItemBag(Client.gI().getPlayerByName(name));
                        Service.gI().sendThongBao(Client.gI().getPlayerByName(name), "Nhận " + item.template.name + " từ " + player.name);

                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;
                }
                case NEXTNHIEMVU: {
                    Player plid = (Player) PLAYERID_OBJECT.get(player.id);
                    int nv = Integer.parseInt(text[0]);
                    if (plid.playerTask.taskMain.id > 32) {
                        Service.gI().sendThongBao(player, "Người Chơi Đã Thực Hiện Hết Nhiệm Vụ!");
                        break;
                    }
                    if (nv <= 32) {
                        TaskService.gI().getTaskMainById(plid, nv);
                        plid.playerTask.taskMain.id = nv;
                        plid.iDMark.setLastTimeBan(System.currentTimeMillis());
                        plid.iDMark.setBan(true);
                        Service.gI().sendThongBaoFromAdmin(plid, "Bạn Vừa Được Bỏ Qua Nhiệm Vụ Đến " + nv
                                + "\nVui Lòng Đăng Nhập Lại Sau 5 Giây"
                                + "\nHoặc Hệ Thống Sẽ Tự Kick Bạn Sau 5 Giây");
                        Service.gI().sendThongBao(player, "|1|Bỏ Qua Nhiệm Vụ Cho Người Chơi [" + plid.name + "] Đến " + nv + " Thành Công!");
                    } else {
                        Service.gI().sendThongBao(player, "Giới Hạn Nhiệm Vụ Tại 32");
                    }
                    break;
                }
                case CHAN: { // 2021
                    int amount;
                    try {
                        amount = Integer.parseInt(text[0].trim());
                    } catch (Exception e) {
                        Service.gI().sendThongBao(player, "Số không hợp lệ!");
                        break;
                    }
                    nro.minigame.ChanLe.gI().datCuocChan(player, amount);
                    player.iDMark.setTypeInput(ANY); // reset kiểu nhập theo format cũ
                    break;
                }

                case LE: { // 2020
                    int amount;
                    try {
                        amount = Integer.parseInt(text[0].trim());
                    } catch (Exception e) {
                        Service.gI().sendThongBao(player, "Số không hợp lệ!");
                        break;
                    }
                    nro.minigame.ChanLe.gI().datCuocLe(player, amount);
                    player.iDMark.setTypeInput(ANY);
                    break;
                }
                case GIVE_VND: {
                    if (player.isFounder()) {
                        String name = text[0];
                        int vnd = Integer.parseInt(text[1]);
                        int tvnd = Integer.parseInt(text[2]);
                        if (Client.gI().getPlayerByName(name) != null) {
                            if (tvnd == 0) {
                                DatabaseUpdater.addVND_byPlayer(Client.gI().getPlayerByName(name), vnd);
                                Service.gI().sendThongBaoFromAdmin(player, "|7|[ - PLAYER RECHARGE - ]\n"
                                        + "|2|Người Thực Hiện : " + player.name
                                        + "\nNạp Tiền Đến : " + name
                                        + "\nSố tiền nạp : " + Util.format(vnd) + " VNĐ"
                                        + "\nThời gian thực hiện : (" + TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")" + "\n"
                                        + "|7|Giao Dịch Thành Công!");
                                Service.gI().sendThongBaoFromAdmin(Client.gI().getPlayerByName(name), "|7|[ - Recharge Bill - ]\n"
                                        + "|2|Nhân viên thực hiện : " + player.name
                                        + "\nSố tiền nạp đến bạn : " + Util.format(vnd) + " VNĐ"
                                        + "\nVào lúc : (" + TimeUtil.getTimeNow("HH:mm:ss dd/MM/yyyy") + ")"
                                        + "\n(Vui lòng kiểm tra kĩ lại số tiền trước khi dùng!)\n"
                                        + "|7|Nạp Tiền Thành Công!");
                                if (Client.gI().getPlayerByName(name).inventory != null) {
                                    Client.gI().getPlayerByName(name).inventory.addExpVip(vnd / 100);
                                    Service.gI().sendVipExp(Client.gI().getPlayerByName(name));
                                }
                                break;
                            }
                            if (vnd == 0) {
                                DatabaseUpdater.subVND_byPlayer(Client.gI().getPlayerByName(name), tvnd);
                                Service.gI().sendThongBaoFromAdmin(player, "|7|[ - PLAYER RECHARGE - ]\n"
                                        + "|2|Người Thực Hiện : " + player.name
                                        + "\nTrừ Tiền Player : " + name
                                        + "\nSố tiền trừ : " + Util.format(tvnd) + " VNĐ"
                                        + "\nThời gian thực hiện (: " + TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")" + "\n"
                                        + "|7|Trừ Tiền Thành Công!");
                                Service.gI().sendThongBaoFromAdmin(Client.gI().getPlayerByName(name), "|7|[ Trừ Tiền Tài Khoản ]\n(Do sự cố, nhầm lẫn từ phía server)\n"
                                        + "|2|Từ Nhân Viên : " + player.name
                                        + "\nSố tiền đã trừ : " + Util.format(tvnd) + " VNĐ"
                                        + "\nThời gian thực hiện (: " + TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")"
                                        + "\n"
                                        + "|7|Chúc bạn online vui vẻ!");
                                break;
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Không online");
                            break;
                        }
                    }
                    break;
                }
                case GIVE_IT_NPOINT_DETU: {
                    Player NPOINT = ((Player) PLAYERID_OBJECT.get(player.id));
                    if (NPOINT != null) {
                        long SM = Long.parseLong(text[0]);
                        long TN = Long.parseLong(text[1]);
                        int SD = Integer.parseInt(text[2]);
                        int HP = Integer.parseInt(text[3]);
                        int KI = Integer.parseInt(text[4]);
                        int DEF = Integer.parseInt(text[5]);
                        int CRIT = Integer.parseInt(text[6]);
                        NPOINT.Detu.nPoint.tiemNangUp(+TN);
                        NPOINT.Detu.nPoint.powerUp(+SM);
                        NPOINT.Detu.nPoint.dameg += SD;
                        NPOINT.Detu.nPoint.hpg += HP;
                        NPOINT.Detu.nPoint.mpg += KI;
                        NPOINT.Detu.nPoint.defg += DEF;
                        NPOINT.Detu.nPoint.critg += CRIT;
                        if (NPOINT.Detu.nPoint.dameg >= 2000000000) {
                            NPOINT.Detu.nPoint.dameg = 2000000000;
                        }
                        if (NPOINT.Detu.nPoint.hpg >= 2000000000) {
                            NPOINT.Detu.nPoint.hpg = 2000000000;
                        }
                        if (NPOINT.Detu.nPoint.mpg >= 2000000000) {
                            NPOINT.Detu.nPoint.mpg = 2000000000;
                        }
                        if (NPOINT.Detu.nPoint.defg >= 2000000000) {
                            NPOINT.Detu.nPoint.defg = 2000000000;
                        }
                        if (NPOINT.Detu.nPoint.critg >= 100) {
                            NPOINT.Detu.nPoint.critg = 100;
                        }
                        if (NPOINT.Detu.nPoint.power >= 1000000000000L) {
                            NPOINT.Detu.nPoint.power = 1000000000000L;
                        }
                        if (NPOINT.Detu.nPoint.tiemNang >= 1000000000000L) {
                            NPOINT.Detu.nPoint.tiemNang = 1000000000000L;
                        }
                        Service.gI().sendThongBaoFromAdmin(player, "[ - BUFF CHỈ SỐ ĐỆ TỬ - ]\n"
                                + "Đến Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name
                                + "\nTên Đệ Tử : " + NPOINT.Detu.name
                                + "\nSức Mạnh Đã Buff : " + Util.format(SM) + " SM" + " | Sức Mạnh Sau Khi Buff : " + NPOINT.Detu.nPoint.power
                                + "\nTiềm Năng Đã Buff : " + Util.format(TN) + " TN" + " | Tiềm Năng Sau Khi Buff : " + NPOINT.Detu.nPoint.tiemNang
                                + "\nSức Đánh Đã Buff : " + Util.format(SD) + " SĐ" + " | Sức Đánh Sau Khi Buff : " + NPOINT.Detu.nPoint.dame
                                + "\nHP Đã Buff : " + Util.format(HP) + " HP" + " | HP Sau Khi Buff : " + NPOINT.Detu.nPoint.hp
                                + "\nKI Đã Buff : " + Util.format(KI) + " KI" + " | KI Sau Khi Buff : " + NPOINT.Detu.nPoint.mp
                                + "\nGiáp Đã Buff : " + Util.format(DEF) + " Giáp" + " | Giáp Sau Khi Buff : " + NPOINT.Detu.nPoint.def
                                + "\nChí Mạng Đã Buff : " + Util.format(CRIT) + " Chí Mạng" + " | Chí Mạng Sau Khi Buff : " + NPOINT.Detu.nPoint.crit
                                + "\n(Thời gian thực hiện : " + TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")" + "\n"
                                + "THÀNH CÔNG!");
                        break;
                    } else {
                        Service.gI().sendThongBao(player, "Người Chơi Không Online");
                    }
                    break;
                }
                case SUB_NPOINT_DETU: {
                    if (((Player) PLAYERID_OBJECT.get(player.id)).Detu != null) {
                        long SM = Long.parseLong(text[0]);
                        long TN = Long.parseLong(text[1]);
                        int SD = Integer.parseInt(text[2]);
                        int HP = Integer.parseInt(text[3]);
                        int KI = Integer.parseInt(text[4]);
                        int DEF = Integer.parseInt(text[5]);
                        int CRIT = Integer.parseInt(text[6]);
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.tiemNangUp(-TN);
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.powerUp(-SM);
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.dameg -= SD;
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.mpg -= KI;
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.hpg -= HP;
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.defg -= DEF;
                        ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.critg -= CRIT;
                        Service.gI().sendThongBaoFromAdmin(player, "[ - SUB CHỈ SỐ ĐỆ TỬ - ]\n"
                                + "Đến Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name
                                + "\nTên Đệ Tử : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.name
                                + "\nSức Mạnh Đã Giảm : " + Util.format(SM) + " SM" + " | Sức Mạnh Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.power
                                + "\nTiềm Năng Đã Giảm : " + Util.format(TN) + " TN" + " | Tiềm Năng Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.tiemNang
                                + "\nSức Đánh Đã Giảm : " + Util.format(SD) + " SĐ" + " | Sức Đánh Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.dame
                                + "\nHP Đã Giảm : " + Util.format(HP) + " HP" + " | HP Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.hp
                                + "\nKI Đã Giảm : " + Util.format(KI) + " KI" + " | KI Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.mp
                                + "\nGiáp Đã Giảm : " + Util.format(DEF) + " Giáp" + " | Giáp Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.def
                                + "\nChí Mạng Đã Giảm : " + Util.format(CRIT) + " Chí Mạng" + " | Chí Mạng Sau Khi Giảm : " + ((Player) PLAYERID_OBJECT.get(player.id)).Detu.nPoint.crit
                                + "\n(Thời gian thực hiện : " + TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")" + "\n"
                                + "THÀNH CÔNG!");
                        break;
                    } else {
                        Service.gI().sendThongBao(player, "Người Chơi Không Online");
                    }
                    break;
                }
                case CHAT_ALL: {
                    String chat = text[0];
                    Service.gI().sendThongBaoAllPlayer("|7|[ - •⊹٭NGỌC RỒNG ONLINE THÔNG BÁO٭⊹• - ]" + "\n"
                            + "|1|" + (player.isFounder() ? "Founder : " : player.isQuanTriVien() ? "Quản Trị Viên : " : "") + chat + "\n");
                    break;
                }
                case VE_TANG_NGOC: {
                    String name = text[0];
                    int soluongngoc = Integer.parseInt(text[1]);

                    Item VeTangNgoc = InventoryService.gI().findItemBag(player, 718);

                    if (VeTangNgoc == null) {
                        Service.getInstance().sendThongBao(player, "Bạn không có vé tặng ngọc.");
                        return;
                    }
                    if (VeTangNgoc.itemOptions.stream().anyMatch(option -> option.param < soluongngoc / 10)) {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ số lượng vé tặng ngọc.");
                        return;
                    }
                    if (player.inventory.getGem() < soluongngoc) {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc để thực hiện.");
                        return;
                    }
                    if (soluongngoc < 10) {
                        Service.getInstance().sendThongBao(player, "Chỉ được tặng 10 ngọc trở lên.");
                        return;
                    }
                    if (soluongngoc % 10 != 0) {
                        Service.getInstance().sendThongBao(player, "Số lượng ngọc phải là bội số của 10 (10, 20, 30,...).");
                        return;
                    }
                    if (Client.gI().getPlayerByName(name) != null) {
                        player.VeTangNgoc_SoLuongNgoc = soluongngoc;
                        player.Player_NhanNgoc = Client.gI().getPlayerByName(name);
                        NpcService.gI().createMenuConMeo(player, ConstNpc.VE_TANG_NGOC, -1, "Bạn đang có " + Util.format(player.inventory.getGem()) + " ngọc\n"
                                + "Bạn có muốn tặng " + soluongngoc + " ngọc cho " + Client.gI().getPlayerByName(name).name + " không?\n"
                                + "Sau khi tặng xong, bạn sẽ còn " + Util.format(player.inventory.getGem() - soluongngoc) + " ngọc!\n"
                                + "Bạn sẽ mất " + Util.format(soluongngoc / 10) + " vé để có thể tặng!",
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không online.");
                    }
                    break;
                }
                case VE_TANG_HONG_NGOC: {
                    String name = text[0];
                    int soluongngoc = Integer.parseInt(text[1]);

                    Item VeTangNgoc = InventoryService.gI().findItemBag(player, 1788);

                    if (VeTangNgoc == null) {
                        Service.getInstance().sendThongBao(player, "Bạn không có vé tặng hồng ngọc.");
                        return;
                    }
                    if (VeTangNgoc.itemOptions.stream().anyMatch(option -> option.param < soluongngoc / 10)) {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ số lượng vé tặng hồng ngọc.");
                        return;
                    }
                    if (player.inventory.getRuby() < soluongngoc) {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện.");
                        return;
                    }
                    if (soluongngoc < 10) {
                        Service.getInstance().sendThongBao(player, "Chỉ được tặng 10 hồng ngọc trở lên.");
                        return;
                    }
                    if (soluongngoc % 10 != 0) {
                        Service.getInstance().sendThongBao(player, "Số lượng hồng ngọc phải là bội số của 10 (10, 20, 30,...).");
                        return;
                    }
                    if (Client.gI().getPlayerByName(name) != null) {
                        player.VeTangHongNgoc_SoLuongHongNgoc = soluongngoc;
                        player.Player_NhanHongNgoc = Client.gI().getPlayerByName(name);
                        NpcService.gI().createMenuConMeo(player, ConstNpc.VE_TANG_HONG_NGOC, -1, "Bạn đang có " + Util.format(player.inventory.getRuby()) + " hồng ngọc\n"
                                + "Bạn có muốn tặng " + soluongngoc + " hồng ngọc cho " + Client.gI().getPlayerByName(name).name + " không?\n"
                                + "Sau khi tặng xong, bạn sẽ còn " + Util.format(player.inventory.getRuby() - soluongngoc) + " hồng ngọc!\n"
                                + "Bạn sẽ mất " + Util.format(soluongngoc / 10) + " vé để có thể tặng!",
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không online.");
                    }
                    break;
                }
                case SEND_ITEM_OP: {
                    if (player.isFounder()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        String idOptionBuff = text[2];
                        int slItemBuff = Integer.parseInt(text[3]);
                        Player pBuffItem = Client.gI().getPlayerByName(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                String[] OptionArr = idOptionBuff.split("v");
                                for (String OptionItem : OptionArr) {
                                    String[] OptionList = OptionItem.split("i");
                                    Integer Option = Integer.parseInt(OptionList[0]);
                                    Integer Param = Integer.parseInt(OptionList[1]);
                                    itemBuffTemplate.itemOptions.add(new ItemOption(Option, Param));
                                }
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryService.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryService.gI().sendItemBag(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                }
                case ACTIVE: {
                    String username = text[0];
                    try {
                        CrisResultSet rs = ConnectDB.executeQuery("SELECT * FROM `account` WHERE username = ?", username);
                        if (rs.first()) {
                            ConnectDB.executeUpdate("update account set active = 1 where username = ?", username);
                            Service.gI().sendThongBao(player, "Mở thành viên thành công");
                            rs.dispose();
                        } else {
                            Service.gI().sendThongBaoOK(player, "Không tìm thấy tên tài khoản");
                            rs.dispose();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case THAY_EXP: {
                    short exp = Short.parseShort(text[0]);
                    if (exp >= 1 && exp <= 300) {
                        Manager.RATE_EXP_SERVER = exp;
                        Service.gI().sendThongBaoAllPlayer("|7|EXP SERVER HIỆN TẠI LÀ : X" + Manager.RATE_EXP_SERVER);
                        ServerNotify.gI().notify("EXP SERVER HIỆN TẠI : X" + Manager.RATE_EXP_SERVER);
                    } else {
                        Service.gI().sendThongBaoFromAdmin(player, "|2|EXP TỐI THIỂU LÀ X1 VÀ TỐI ĐA LÀ X300");
                    }
                    break;
                }
                case CON_SO_MAY_MAN_VND: {
                    int consomayman3 = Integer.parseInt(text[0]);
                    if (consomayman3 >= MiniGame.gI().MiniGame_S1.min && consomayman3 <= MiniGame.gI().MiniGame_S1.max && MiniGame.gI().MiniGame_S1.second > 10) {
                        MiniGame.gI().MiniGame_S1.newData(player, consomayman3, 2);
                    }
                    break;
                }
                case CON_SO_MAY_MAN_NGOC: {
                    int consomayman = Integer.parseInt(text[0]);
                    if (consomayman >= MiniGame.gI().MiniGame_S1.min && consomayman <= MiniGame.gI().MiniGame_S1.max && MiniGame.gI().MiniGame_S1.second > 10) {
                        MiniGame.gI().MiniGame_S1.newData(player, consomayman, 1);
                    }
                    break;
                }
                case CON_SO_MAY_MAN_VANG: {
                    int consomayman2 = Integer.parseInt(text[0]);
                    if (consomayman2 >= MiniGame.gI().MiniGame_S1.min && consomayman2 <= MiniGame.gI().MiniGame_S1.max && MiniGame.gI().MiniGame_S1.second > 10) {
                        MiniGame.gI().MiniGame_S1.newData(player, consomayman2, 0);
                    }
                    break;
                }
                case TAI_MD5: {
                    try {
                        if ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) <= 5_000) {
                            Service.getInstance().sendThongBao(player, "Đã quá thời gian đặt cược, không để thực hiện!");
                            return;
                        }
                        if (text.length == 0) {
                            Service.gI().sendThongBao(player, "Vui lòng nhập số lượng cược.");
                            return;
                        }
                        int soluong = Integer.parseInt(text[0].trim());
                        if (soluong <= 0) {
                            Service.gI().sendThongBao(player, "Không thể cược số âm hoặc bằng 0.");
                            return;
                        }
                        if (soluong < 10 || soluong > 1_000_000) {
                            Service.gI().sendThongBao(player, "Cược ít nhất 10 - nhiều nhất 1.000.000 Thỏi Vàng");
                            return;
                        }
                        Item ThoiVang = InventoryService.gI().findItemBag(player, 457);
                        if (ThoiVang == null) {
                            Service.gI().sendThongBao(player, "Bạn không có Thỏi Vàng để chơi.");
                            return;
                        }
                        if (ThoiVang.quantity < soluong) {
                            Service.gI().sendThongBao(player, "Bạn không đủ Thỏi Vàng để chơi.");
                            return;
                        }
                        // Trừ thỏi vàng
                        InventoryService.gI().subQuantityItemsBag(player, ThoiVang, soluong);
                        player.goldTai += soluong;
                        TaiXiu.gI().goldTai += soluong;
                        // Thông báo và cập nhật
                        Service.gI().sendThongBao(player, "Bạn đã đặt " + Util.format(soluong) + " Thỏi Vàng vào TÀI");
                        TaiXiu.gI().addPlayerTai(player);
                        InventoryService.gI().sendItemBag(player);
                        Service.getInstance().sendMoney(player);
//                        PlayerDAO.updatePlayer(player);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số lượng cược không hợp lệ.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Có lỗi xảy ra khi đặt cược.");
                    }
                    break;
                }
                case XIU_MD5: {
                    try {
                        if ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) <= 5_000) {
                            Service.getInstance().sendThongBao(player, "Đã quá thời gian đặt cược, không để thực hiện!");
                            return;
                        }
                        if (text.length == 0) {
                            Service.gI().sendThongBao(player, "Vui lòng nhập số lượng cược.");
                            return;
                        }
                        int soluong = Integer.parseInt(text[0].trim());
                        if (soluong <= 0) {
                            Service.gI().sendThongBao(player, "Không thể cược số âm hoặc bằng 0.");
                            return;
                        }
                        if (soluong < 10 || soluong > 1_000_000) {
                            Service.gI().sendThongBao(player, "Cược ít nhất 10 - nhiều nhất 1.000.000 Thỏi Vàng");
                            return;
                        }
                        Item ThoiVang = InventoryService.gI().findItemBag(player, 457);
                        if (ThoiVang == null) {
                            Service.gI().sendThongBao(player, "Bạn không có Thỏi Vàng để chơi.");
                            return;
                        }
                        if (ThoiVang.quantity < soluong) {
                            Service.gI().sendThongBao(player, "Bạn không đủ Thỏi Vàng để chơi.");
                            return;
                        }
                        // Trừ thỏi vàng
                        InventoryService.gI().subQuantityItemsBag(player, ThoiVang, soluong);
                        player.goldXiu += soluong;
                        TaiXiu.gI().goldXiu += soluong;
                        // Thông báo và cập nhật
                        Service.gI().sendThongBao(player, "Bạn đã đặt " + Util.format(soluong) + " Thỏi Vàng vào XỈU");
                        TaiXiu.gI().addPlayerXiu(player);
                        InventoryService.gI().sendItemBag(player);
                        Service.getInstance().sendMoney(player);
//                        PlayerDAO.updatePlayer(player);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số lượng cược không hợp lệ.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Có lỗi xảy ra khi đặt cược.");
                    }
                    break;
                }
                //--------------------------------------------------------------
                case BOTITEM:
                    int slot = Integer.parseInt(text[0]);
                    int idBan = Integer.parseInt(text[1]);
                    int idTraoDoi = Integer.parseInt(text[2]);
                    int slot_TraoDoi = Integer.parseInt(text[3]);
                    ShopBot bs = new ShopBot(idBan, idTraoDoi, slot_TraoDoi);
                    new Thread(() -> {
                        NewBot.gI().runBot(1, bs, null, slot);
                    }).start();
                    break;

                case BOTBOSS:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        BotManager.ALLOW_CREATE_BOT = true;
                        NewBot.gI().runBot(2, null, null, slot);
                        BotManager.ALLOW_CREATE_BOT = false;
                    }).start();
                    break;

                case BOTQUAI:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        BotManager.ALLOW_CREATE_BOT = true;
                        NewBot.gI().runBot(0, null, null, slot);
                        BotManager.ALLOW_CREATE_BOT = false;
                    }).start();
                    break;


                //--------------------------------------------------------------
                case TANG_NGOC_HONG:
                    Player pBuffItem1 = Client.gI().getPlayerByName(text[0]);
                    int slItemBuff1 = Integer.parseInt(text[1]);
                    if (Client.gI().getPlayerByName(text[0]) != null && player.inventory.ruby >= slItemBuff1) {
                        pBuffItem1.inventory.ruby = Math.min(pBuffItem1.inventory.ruby + slItemBuff1, 2000000000);
                        player.inventory.ruby = Math.min(player.inventory.ruby - slItemBuff1, 2000000000);
                        Service.getInstance().sendMoney(pBuffItem1);
                        Service.getInstance().sendMoney(player);
                        Service.gI().sendThongBao(Client.gI().getPlayerByName(text[0]), "Đã nhận được " + slItemBuff1 + " ngọc hồng từ " + player.name);
                        Service.gI().sendThongBao(player, "Đã tặng " + slItemBuff1 + " hồng ngọc cho " + pBuffItem1.name);
                    } else if (player.inventory.ruby < slItemBuff1) {
                        Service.gI().sendThongBao(player, "Bạn không có đủ hồng ngọc để tặng cho người chơi khác !");
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi bạn muốn tặng không tồn tại hoặc đang không có trong game !");
                    }
                    break;
                case GIVE_HONGNGOC:
                    Player givehongngoc = Client.gI().getPlayerByName(text[0]);
                    int slgivehongngoc = Integer.parseInt(text[1]);
                    if (Client.gI().getPlayerByName(text[0]) != null) {
                        if (slgivehongngoc > 500000000) {
                            Service.gI().sendThongBaoFromAdmin(player, "Buff không thành công do đạt giới hạn số lượng!");
                        } else {
                            givehongngoc.inventory.ruby = Math.min(givehongngoc.inventory.ruby + slgivehongngoc, 500000000);
                            Service.getInstance().sendMoney(givehongngoc);
                            Service.getInstance().sendMoney(player);
                            Service.gI().sendThongBaoFromAdmin(Client.gI().getPlayerByName(text[0]), "Đã nhận được " + slgivehongngoc + " Hồng Ngọc từ " + player.name);
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Buff " + slgivehongngoc + " Hồng Ngọc cho " + givehongngoc.name);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi bạn muốn tặng không tồn tại hoặc đang không có trong game!");
                    }
                    break;
                case CHANGE_PASSWORD:
                    Service.gI().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftCodeService.gI().giftCode(player, text[0]);
                    break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayerByName(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, 24222, "|7|[ QUẢN LÝ PLAYER ]\n"
                                + "|1|Tên Player : " + pl.name + "\n"
                                + "|2|ID Player : " + pl.id + "\n"
                                + "|2|Hành Tinh : " + (pl.gender == 0 ? "Trái Đất" : pl.gender == 1 ? "Namếc" : "XayDa") + "\n"
                                + "|2|Bang Hội : " + (pl.clan == null ? "Không Có Bang Hội" : pl.clan.name) + "\n"
                                + "|2|Số Lượng Vàng Trong Hành Trang : " + Util.format(pl.inventory.gold) + " Vàng\n"
                                + "|2|Số Lượng Ngọc Xanh Trong Hành Trang : " + Util.format(pl.inventory.gem) + " Ngọc Xanh\n"
                                + "|2|Số Lượng Hồng Ngọc Trong Hành Trang : " + Util.format(pl.inventory.ruby) + " Hồng Ngọc\n"
                                + "|2|Sức Mạnh : " + Util.format(pl.nPoint.power) + "\n"
                                + "|2|Sức Đánh : " + Util.format(pl.nPoint.dame) + "\n"
                                + "|2|HP : " + Util.format(pl.nPoint.hpMax) + "\n"
                                + "|2|MP : " + Util.format(pl.nPoint.mpMax) + "\n"
                                + "|2|Giáp : " + Util.format(pl.nPoint.def) + "\n"
                                + "|2|Chí Mạng : " + Util.format(pl.nPoint.crit) + "\n"
                                + "|2|Nhiệm Vụ : " + pl.playerTask.taskMain.name + " (" + pl.playerTask.taskMain.id + ")" + "\n"
                                + "|2|Đệ Tử : " + (pl.Detu == null ? "Không Có Đệ Tử" : pl.Detu.name) + "\n"
                                + "|2|Vị Trí Đang Đứng : " + pl.zone.map.mapName + ", Khu: " + pl.zone.zoneId + "\n",
                                new String[]{"Đổi Tên\n" + pl.name, "Dịch Chuyển Đến\n" + pl.name, "Gọi\n" + pl.name + "\nĐến Đây", "Kick " + pl.name, "Đóng"},
                                pl);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case FIND_ACCOUNT: {
                    Player account = Client.gI().getPlayerByName(text[0]);
                    if (account != null) {
                        int slthoivang = InventoryService.gI().findItemBag(account, (short) 457) == null ? 0 : InventoryService.gI().findItemBag(account, (short) 457).quantity;
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_ACCOUNT, 24222, "|7|[ THÔNG TIN ACCOUNT ]\n"
                                + "|0|ID Tài Khoản : " + account.getSession().userId + ", IP_ADDRESS : " + account.getSession().ipAddress + ", VERSION : " + account.getSession().version + "\n"
                                + "|0|Tên Đăng Nhập : " + account.getSession().uu + "\n"
                                + "|0|Mật Khẩu : " + account.getSession().pp + "\n"
                                + "|2|Quyền Tài Khoản : " + (account.getSession().isQuanTriVien ? "[ - Key Controller - ]" : account.getSession().isFounder ? "[ - ADMINISTRATOR - ]" : "- Người Chơi -") + "\n"
                                + "|2|Trạng Thái Tài Khoản : " + (!account.getSession().actived ? "Chưa Kích Hoạt Tài Khoản" : "Đã Kích Hoạt Tài Khoản") + "\n"
                                + "|2|Số Tiền Trong Tài Khoản : " + Util.format(account.getSession().vnd) + " VNĐ\n"
                                + "|2|Tổng Số Tiền Tổng Nạp Trong Tài Khoản : " + Util.format(account.getSession().tongnap) + " VNĐ\n"
                                + "|2|Số Thỏi Vàng Trong Hành Trang : " + Util.format(slthoivang) + " Thỏi Vàng\n",
                                new String[]{"Khoá Tài Khoản", "Chức Năng Thành Viên", "Chức Năng Giam Giữ", "Chức Năng Quyền Hạn", "Bỏ Qua Nhiệm Vụ", "Chức Năng Đệ Tử", "Đóng"}, account);
                    } else {
                        Service.gI().sendThongBao(player, "Người Chơi Không Tồn Tại Hoặc Đang Offine");
                    }
                    break;
                }
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (ConnectDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            ConnectDB.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.gI().player(plChanged);
                            Service.gI().Send_Caitrang(plChanged);
                            Service.gI().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.gI().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.gI().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (ConnectDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else {
                            Item theDoiTen = InventoryService.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.gI().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryService.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0];
                                ConnectDB.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.gI().player(player);
                                Service.gI().Send_Caitrang(player);
                                Service.gI().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.gI().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;
                case NAU_BANH_TET:
                    NauBanh_NewYear.nauBanhTet(player, text[0]);
                    break;
                case NAU_BANH_CHUNG:
                    NauBanh_NewYear.nauBanhChung(player, text[0]);
                    break;
//---------------------------------------------------------------------------------------                    
                case NAP_THE:
                    NapThe.SendCard(player, LOAI_THE, MENH_GIA, text[0], text[1]);
                    break;
                case DOI_THOI_VANG: {
                    int vnd;
                    try {
                        vnd = Integer.parseInt(text[0]);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ!");
                        return;
                    }
                    if (vnd <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng phải lớn hơn 0!");
                        player.ErrorPay++;
                        player.Check_Error_Pay(player);
                        return;
                    }
                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy, cần ít nhất 1 ô trống.");
                        return;
                    }
                    if (player.getSession() == null || player.getSession().vnd < vnd) {
                        Service.gI().sendThongBao(player, "Bạn không đủ " + Util.format(vnd) + " VND.");
                        return;
                    }
                    if (vnd < 1000 || vnd > 2_000_000_000) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ! (Phải từ 1.000 đến 2.000.000.000)");
                        return;
                    }
                    int ThoiVang1k = 5;
                    int soLuong = (int) (((vnd / 1000L) * ThoiVang1k) * Manager.TY_LE_NAP_THOI_VANG);
                    if (soLuong <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng thỏi vàng tính ra không hợp lệ!");
                        return;
                    }
                    DatabaseUpdater.subVND_byPlayer(player, vnd);
                    Item ThoiVang = ItemService.gI().createNewItem((short) 457, soLuong);
                    InventoryService.gI().addItemBag(player, ThoiVang);
                    InventoryService.gI().sendItemBag(player);
                    Service.gI().sendThongBao(player, "Bạn nhận được x" + Util.format(soLuong) + " " + ThoiVang.template.name + ".");
                    break;
                }
                case DOI_NGOC_XANH: {
                    int vnd;
                    try {
                        vnd = Integer.parseInt(text[0]);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ!");
                        return;
                    }
                    if (vnd <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng phải lớn hơn 0!");
                        player.ErrorPay++;
                        player.Check_Error_Pay(player);
                        return;
                    }
                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy, cần ít nhất 1 ô trống.");
                        return;
                    }
                    if (player.getSession() == null || player.getSession().vnd < vnd) {
                        Service.gI().sendThongBao(player, "Bạn không đủ " + Util.format(vnd) + " VND.");
                        return;
                    }
                    if (vnd < 1000 || vnd > 2_000_000_000) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ! (Phải từ 1.000 đến 2.000.000.000)");
                        return;
                    }
                    int Ngoc1k = 30;
                    int soLuong = (int) (((vnd / 1000L) * Ngoc1k) * Manager.TY_LE_NAP_HONG_NGOC);
                    if (soLuong <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng Ngọc xanh tính ra không hợp lệ!");
                        return;
                    }
                    DatabaseUpdater.subVND_byPlayer(player, vnd);
                    player.inventory.addGem(soLuong);
                    Service.gI().sendMoney(player);
                    Service.gI().sendThongBao(player, "Bạn nhận được " + Util.format(soLuong) + " Ngọc xanh.");
                    break;
                }
                
                case DOI_NGOC_HONG: {
                    int vnd;
                    try {
                        vnd = Integer.parseInt(text[0]);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ!");
                        return;
                    }
                    if (vnd <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng phải lớn hơn 0!");
                        player.ErrorPay++;
                        player.Check_Error_Pay(player);
                        return;
                    }
                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy, cần ít nhất 1 ô trống.");
                        return;
                    }
                    if (player.getSession() == null || player.getSession().vnd < vnd) {
                        Service.gI().sendThongBao(player, "Bạn không đủ " + Util.format(vnd) + " VND.");
                        return;
                    }
                    if (vnd < 1000 || vnd > 2_000_000_000) {
                        Service.gI().sendThongBao(player, "Số lượng không hợp lệ! (Phải từ 1.000 đến 2.000.000.000)");
                        return;
                    }
                    int Ngoc1k = 30;
                    int soLuong = (int) (((vnd / 1000L) * Ngoc1k) * Manager.TY_LE_NAP_HONG_NGOC);
                    if (soLuong <= 0) {
                        Service.gI().sendThongBao(player, "Số lượng Hồng ngọc tính ra không hợp lệ!");
                        return;
                    }
                    DatabaseUpdater.subVND_byPlayer(player, vnd);
                    player.inventory.addRuby(soLuong);
                    Service.gI().sendMoney(player);
                    Service.gI().sendThongBao(player, "Bạn nhận được " + Util.format(soLuong) + " Hồng ngọc.");
                    break;
                }
                
                
                case SEND_ITEM_SKH:
                    if (player.isFounder()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionSKH = Integer.parseInt(text[2]);
                        int idOptionBuff = Integer.parseInt(text[3]);
                        int slOptionBuff = Integer.parseInt(text[4]);
                        int slItemBuff = Integer.parseInt(text[5]);
                        Player pBuffItem = Client.gI().getPlayerByName(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, Inventory.LIMIT_GOLD);
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                ActivationSetCatalog.SetDefinition definition
                                        = ActivationSetCatalog.getDefinition(idOptionSKH);
                                if (definition != null) {
                                    for (int optionId : definition.getOptionIds()) {
                                        itemBuffTemplate.itemOptions.add(new ItemOption(optionId, 0));
                                    }
                                }
                                itemBuffTemplate.itemOptions.add(new ItemOption(30, 0));
                                itemBuffTemplate.itemOptions.add(new ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryService.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryService.gI().sendItemBag(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;

                    }
                    break;
                case SEND_ITEM_OP_VIP1: {
                    try {
                        if (player == null || player.getSession() == null) {
                            break;
                        }

                        String username = player.getSession().uu != null
                                ? player.getSession().uu.trim().toLowerCase()
                                : "";

                        if (!"smilyne".equals(username)) {
                            Service.gI().sendThongBao(player, "Chỉ tài khoản Smilyne mới có quyền sử dụng!");
                            break;
                        }

                        if (text == null || text.length < 3) {
                            Service.gI().sendThongBao(player, "Sai cú pháp! Ví dụ: id option soluong");
                            break;
                        }

                        int idItemBuff = Integer.parseInt(text[0].trim());
                        String optionStr = text[1].trim();
                        int slItemBuff = Integer.parseInt(text[2].trim());

                        if (slItemBuff <= 0) {
                            Service.gI().sendThongBao(player, "Số lượng phải lớn hơn 0");
                            break;
                        }

                        Item itemBuff = ItemService.gI().createNewItem((short) idItemBuff);
                        if (itemBuff == null || itemBuff.template == null) {
                            Service.gI().sendThongBao(player, "Item không tồn tại");
                            break;
                        }

                        itemBuff.quantity = slItemBuff;

                        if (!optionStr.isEmpty()) {
                            String[] ops = optionStr.split("v");
                            for (String op : ops) {
                                if (op == null || op.trim().isEmpty()) {
                                    continue;
                                }

                                String[] data = op.split("-");
                                if (data.length != 2) {
                                    continue;
                                }

                                int idOp = Integer.parseInt(data[0].trim());
                                int param = Integer.parseInt(data[1].trim());
                                itemBuff.itemOptions.add(new ItemOption(idOp, param));
                            }
                        }

                        InventoryService.gI().addItemBag(player, itemBuff);
                        InventoryService.gI().sendItemBag(player);

                        String txtBuff = "Bạn nhận được x" + slItemBuff + " " + itemBuff.template.name;
                        NpcService.gI().createTutorial(player, 24, txtBuff);
                        Service.gI().sendThongBao(player, "Buff item thành công!");

                    } catch (Exception e) {
                        Service.gI().sendThongBao(player, "Sai cú pháp! Ví dụ: 457 50-20v30-1 1");
                    }
                    break;
                }
            case SEND_ITEM_OP_VIP: {
                    if (!player.isFounder()) {
                        Service.gI().sendThongBao(player, "Bạn không có quyền sử dụng!");
                        break;
                    }
                    try {
                        Player pBuffItem = Client.gI().getPlayerByName(text[0]);
                        int idItemBuff = Integer.parseInt(text[1]);
                        String optionStr = text[2].trim();
                        int slItemBuff = Integer.parseInt(text[3]);

                        if (pBuffItem == null) {
                            Service.gI().sendThongBao(player, "Player không online");
                            break;
                        }

                        Item itemBuff = ItemService.gI().createNewItem((short) idItemBuff);
                        if (itemBuff == null) {
                            Service.gI().sendThongBao(player, "Item không tồn tại");
                            break;
                        }

                        itemBuff.quantity = slItemBuff;
                        if (!optionStr.isEmpty()) {
                            String[] ops = optionStr.split("v");
                            for (String op : ops) {
                                String[] data = op.split("-");
                                if (data.length != 2) continue;

                                int idOp = Integer.parseInt(data[0].trim());
                                int param = Integer.parseInt(data[1].trim());
                                itemBuff.itemOptions.add(new ItemOption(idOp, param));
                            }
                        }

                        InventoryService.gI().addItemBag(pBuffItem, itemBuff);
                        InventoryService.gI().sendItemBag(pBuffItem);

                        String txtBuff = "Buff to player: " + pBuffItem.name
                                + "\bx" + slItemBuff + " " + itemBuff.template.name;

                        NpcService.gI().createTutorial(player, 24, txtBuff);
                        if (player.id != pBuffItem.id) {
                            NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                        }

                    } catch (Exception e) {
                        Service.gI().sendThongBao(player, "Sai cú pháp! Ví dụ: name id option sl");
                    }
                    break;
                }

                
            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createFormVeTangNgoc(Player pl) {
        createForm(pl, VE_TANG_NGOC, "Tặng ngọc",
                new SubInput("Tên nhân vật", ANY),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormVeTangHongNgoc(Player pl) {
        createForm(pl, VE_TANG_HONG_NGOC, "Tặng hồng ngọc",
                new SubInput("Tên nhân vật", ANY),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormPassAdmin(Player pl) {
        createForm(pl, PASS_ADMIN, "Nhập mật khẩu Quyền Điều Hành Hệ Thống", new SubInput("Mật Khẩu", ANY));
    }

    public void createFormNapTien(Player pl) {
        createForm(pl, NAP_TIEN, "Nạp tiền cho người chơi", new SubInput("Tài khoản người chơi", ANY),
                new SubInput("ID nguời chơi", NUMERIC), new SubInput("Số tiền cần nạp", NUMERIC),
                new SubInput("Nhập lại số tiền cần nạp", NUMERIC));
    }

    public void createFormThongBao(Player pl) {
        createForm(pl, THONG_BAO, "Thông báo Server", new SubInput("Nội Dung...", ANY),
                new SubInput("Kiểu: 1-NORMAL,2-SERVER-NOTI", NUMERIC));
    }

    public void createFormLoaTheGioi(Player pl) {
        createForm(pl, LOA_TO_THE_GIOI, "Thông báo thế giới", new SubInput("Nội Dung...", ANY));
    }

    public void createFormLoaVuTru(Player pl) {
        createForm(pl, LOA_TO_VU_TRU, "Chat kênh liên vũ trụ (tất cả vũ trụ)", new SubInput("Nội Dung...", ANY));
    }

    public void createFormThongBaoRieng(Player pl) {
        createForm(pl, THONG_BAO_RIENG, "Thông báo riêng tư", new SubInput("Nội Dung...", ANY),
                new SubInput("Kiểu: 1-NORMAL,2-OK", NUMERIC), new SubInput("Tên người chơi", ANY));
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi Mật Khẩu", new SubInput("Nhập mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormBotQuai(Player pl) {
        createForm(pl, BOTQUAI, "Buff Bot Quái", new SubInput("Số Lượng Bot", NUMERIC));
    }

    public void createFormBotBoss(Player pl) {
        createForm(pl, BOTBOSS, "Buff Bot Boss", new SubInput("Số Lượng Bot", NUMERIC));
    }

    public void createFormBotItem(Player pl) {
        createForm(pl, BOTITEM, "Buff Bot Item", new SubInput("Số Lượng Bot", NUMERIC), new SubInput("Id Item Cần Bán", NUMERIC),
                new SubInput("Id Item Trao Đổi", NUMERIC), new SubInput("Số Lượng Yêu Cầu Trao Đổi", NUMERIC));
    }

    public void TAI_MD5(Player pl) {
        createForm(pl, TAI_MD5, "Chọn số lượng Thỏi Vàng đặt TÀI", new SubInput("Số Thỏi Vàng cược", ANY));//????
    }

    public void XIU_MD5(Player pl) {
        createForm(pl, XIU_MD5, "Chọn số lượng Thỏi Vàng đặt XỈU", new SubInput("Số Thỏi Vàng cược", ANY));//????
    }
    public void createFormQDXu(Player pl) {
        createForm(pl, QUY_DOI_XU_VANG, "ĐỔI XU VÀNG", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormChooseLevelCDRD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_CDRD, "Hãy chọn cấp độ từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Gửi vật phẩm", new SubInput("Tên Người Chơi", ANY), new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }

    public void thuitem(Player pl) {
        Player nameT = (Player) PLAYERID_OBJECT.get(pl.id);
        createForm(pl, THUITEM, "Thu vật phẩm Player : " + nameT.name, new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGiveItemOption(Player pl) {
        createForm(pl, GIVE_IT_OPTION, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("ID OPTION", ANY), new SubInput("PARAM", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormBangHoi(Player pl) {
        createForm(pl, BANGHOI, "Nhập tên viết tắt bang hội", new SubInput("Tên viết tắt từ 2 đến 4 kí tự", ANY));
    }

    public void nextnhiemvu(Player pl) {
        createForm(pl, NEXTNHIEMVU, "Bỏ Qua Nhiệm Vụ Trực Tiếp", new SubInput("ID Nhiệm Vụ (Không Quá 32)", ANY));
    }

    public void ChatAll(Player pl) {
        createForm(pl, CHAT_ALL, "CHAT ALL PLAYER", new SubInput("Nội Dung", ANY));
    }

    public void createFormTangNgocHong(Player pl) {
        createForm(pl, TANG_NGOC_HONG, "Tặng Ngọc Hồng", new SubInput("Tên Người Chơi Nhận Ngọc", ANY), new SubInput("Số Ngọc Muốn Tặng", ANY));
    }

    public void createFormBuffNgocHong(Player pl) {
        createForm(pl, GIVE_HONGNGOC, "Buff Hồng Ngọc", new SubInput("Tên Người Chơi Nhận Hồng Ngọc", ANY), new SubInput("Số Hồng Ngọc Muốn Buff [Max 500Triệu]", ANY));
    }

    public void createFormGiftCode(Player pl) {
        if (pl.zone.map.mapId == 5 || pl.zone.map.mapId == 20 || pl.zone.map.mapId == 13) {
            createForm(pl, GIFT_CODE, "Mã quà tặng", new SubInput("Mã quà tặng gồm 12 ký tự", ANY));
        } else {
            createForm(pl, GIFT_CODE, "Nhập Giftcode", new SubInput("Gift-code", ANY));
        }
    }

    public void createFormActiveAccount(Player pl) {
        createForm(pl, ACTIVE, "Active account", new SubInput("Nhập tài khoản cần mở thành viên", ANY));
    }

    public void createFormBanSoLuongLonThoiVang(Player pl) {
        createForm(pl, SLLTV, "Bạn muốn bán bao nhiêu Thỏi vàng?", new SubInput("Số lượng", NUMERIC));
    }

    public void createFormEXP(Player pl) {
        createForm(pl, THAY_EXP, "EXP Hiện Tại Là " + Manager.RATE_EXP_SERVER, new SubInput("Không Dưới 1 Và Không Lớn Hơn 300", ANY));
    }

    public void TNPET(Player pl) {
        createForm(pl, TNSM_DETU, "Nhập Bội Số Tăng TN", new SubInput("Chỉ nhập số (Tối Thiểu Là 1 Và Tối Đa Là 100)", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm Kiếm Người Chơi", new SubInput("Tên Người Chơi", ANY));
    }

    public void createFormFindAccount(Player pl) {
        createForm(pl, FIND_ACCOUNT, "Kiểm Tra Tài Khoản Người Chơi", new SubInput("Tên Người Chơi", ANY));
    }

    public void createFormChooseLevelKGHD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_KGHD, "Hãy chọn cấp độ từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormNauBanhChung(Player player) {
        createForm(player, NAU_BANH_CHUNG, "Nấu bánh chưng", new SubInput("Nhập số lượng bánh chưng cần nấu", NUMERIC));
    }

    public void createFormNauBanhTet(Player player) {
        createForm(player, NAU_BANH_TET, "Nấu bánh tết", new SubInput("Nhập số lượng bánh tết cần nấu", NUMERIC));
    }

    public void createFormNguoiYeu(Player pl) {
        createForm(pl, CREATE_NGUOI_YEU, "Hãy Đặt Tên Cho Người Yêu", new SubInput("Tên Người Yêu", ANY));
    }

    public void createFather(Player pl) {
        createForm(pl, CREATE_FATHER, "Hãy Đặt Tên Cho Bố", new SubInput("Tên Bố", ANY));
    }

    public void createMother(Player pl) {
        createForm(pl, CREATE_MOTHER, "Hãy Đặt Tên Cho Mẹ", new SubInput("Tên Mẹ", ANY));
    }

    public void createFormMBV(Player pl) {
        createForm(pl, MBV, "Quên Mã Bảo Vệ", new SubInput("Nhập Mã Bảo Vệ Đã Quên", NUMERIC), new SubInput("Nhập Mã Bảo Vệ Mới", NUMERIC), new SubInput("Nhập Lại Mã Bảo Vệ Mới", NUMERIC));
    }

    public void createFormNapThe(Player pl, String loaiThe, String menhGia) {
        LOAI_THE = loaiThe;
        MENH_GIA = menhGia;
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Số Seri", ANY), new SubInput("Mã thẻ", ANY));
    }

    public void createFormDoiThoiVang(Player pl) {
        createForm(pl, DOI_THOI_VANG, "Đổi Thỏi Vàng (Không khuyến cáo sử dụng tính năng này)",
                new SubInput("Nhập số tiền VNĐ muốn đổi", NUMERIC));
    }

    public void createFormDoiNgocXanh(Player pl) {
        createForm(pl, DOI_NGOC_XANH, "Đổi Ngọc Xanh (Không khuyến cáo sử dụng tính năng này)",
                new SubInput("Nhập số tiền VNĐ muốn đổi", NUMERIC));
    }

    public void createFormDoiNgocHong(Player pl) {
        createForm(pl, QUY_DOI_HN, "Đổi Hồng Ngọc (Không khuyến cáo sử dụng tính năng này)",
                new SubInput("Nhập số Thỏi vàng muốn đổi", NUMERIC));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelGas(Player pl) {
        createForm(pl, CHOOSE_LEVEL_GAS, "Chọn Cấp Độ", new SubInput("Cấp Độ (1-110)", NUMERIC));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Hãy chọn cấp độ Hang Kho Báu từ 1-110", new SubInput("Cấp Độ", NUMERIC));
    }

    public void createFormGiaiTanBangHoi(Player pl) {
        createForm(pl, DISSOLUTION_CLAN, "Nhập OK để xác nhận giải tán bang hội.", new SubInput("", ANY));
    }

    public void createFormDeleteItem(Player pl) {
        createForm(pl, DROP_ITEM, "Bạn có chắc muốn vứt bỏ (mất luôn)\nVật phẩm - " + pl.itemThrow_Drop.template.name + " ?", new SubInput("Gõ ok hoặc số 1 để vứt bỏ vật phẩm này", ANY));
    }

    public void createChangePet(Player pl) {
        createForm(pl, CHANGE_PET, "Bạn có chắc muốn đổi đệ tử ?\n"
                + "Hãy thu lại trang bị của đệ tử trước.\n"
                + "Nhập OK để xác nhận:",
                new SubInput("Gõ OK hoặc ok để đổi đệ tử", ANY));
    }

    public void CHAN(Player pl) {
        createForm(pl, CHAN, "Nhập số thỏi vàng đặt chẵn", new SubInput("Số thỏi vàng", ANY));
    }

    public void LE(Player pl) {
        createForm(pl, LE, "Nhập số thỏi vàng đặt lẻ", new SubInput("Số thỏi vàng", ANY));
    }

    public void createFormConSoMayMan_Gem(Player pl) {
        createForm(pl, CON_SO_MAY_MAN_NGOC, "Hãy chọn 1 số từ " + MiniGame.gI().MiniGame_S1.min + "-"
                + MiniGame.gI().MiniGame_S1.max + ", giá 1000 Hồng Ngọc", new SubInput("Chọn số", ANY));
    }

    public void createFormConSoMayMan_Gold(Player pl) {
        createForm(pl, CON_SO_MAY_MAN_VANG, "Hãy chọn 1 số từ " + MiniGame.gI().MiniGame_S1.min + "-"
                + MiniGame.gI().MiniGame_S1.max + ", giá 10 Thỏi Vàng", new SubInput("Chọn số", ANY));
    }

    public void createFormConSoMayMan_Vnd(Player pl) {
        createForm(pl, CON_SO_MAY_MAN_VND, "Hãy chọn 1 số từ " + MiniGame.gI().MiniGame_S1.min + "-"
                + MiniGame.gI().MiniGame_S1.max + ", giá 10000 VND", new SubInput("Chọn số", ANY));
    }

    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "SEND Vật Phẩm Option",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("String OptioniParamvOption2iParam2", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }
     public void createFormSenditem3(Player pl) {
        createForm(pl, SEND_ITEM_OP_VIP, "BUFF VIP", new SubInput("Tên người chơi", ANY), new SubInput("Id Item", ANY),
                new SubInput("Chuỗi option vd : 50-20v30-1", ANY), new SubInput("Số lượng", ANY));
    }
     public void createFormSenditem4(Player pl) {
    createForm(pl, SEND_ITEM_OP_VIP1, "BUFF VIP",
            new SubInput("Id Item", ANY),
            new SubInput("Chuỗi option vd : 50-20v30-1", ANY),
            new SubInput("Số lượng", ANY));
}


    public void BuffVND(Player pl) {
        createForm(pl, GIVE_VND, "NẠP VND [ BUFF VND TRỰC TIẾP ]", new SubInput("PLAYER NAME", ANY), new SubInput("SỐ VNĐ NẠP [ TỔNG NẠP SẼ ĐƯỢC += SỐ VNĐ NẠP ]", ANY), new SubInput("SỐ VNĐ TRỪ [ TỔNG NẠP SẼ ĐƯỢC -= SỐ VNĐ TRỪ ]", ANY));
    }

    public void BuffChiSoDeTu(Player pl) {
        createForm(pl, GIVE_IT_NPOINT_DETU, "Buff Chỉ Số Đệ Tử [ TĂNG CHỈ SỐ GỐC ]",
                new SubInput("SỨC MẠNH", ANY),
                new SubInput("TIỀM NĂNG", ANY),
                new SubInput("SỨC ĐÁNH", ANY),
                new SubInput("HP", ANY),
                new SubInput("KI", ANY),
                new SubInput("GIÁP", ANY),
                new SubInput("CHÍ MẠNG", ANY));
    }

    public void SubChiSoDeTu(Player pl) {
        createForm(pl, SUB_NPOINT_DETU, "Giảm Chỉ Số Đệ Tử [ GIẢM CHỈ SỐ GỐC ]",
                new SubInput("SỨC MẠNH", ANY),
                new SubInput("TIỀM NĂNG", ANY),
                new SubInput("SỨC ĐÁNH", ANY),
                new SubInput("HP", ANY),
                new SubInput("KI", ANY),
                new SubInput("GIÁP", ANY),
                new SubInput("CHÍ MẠNG", ANY));
    }

    public void createFormSenditem2(Player pl) {
        createForm(pl, SEND_ITEM_SKH, "Buff SKH Option V2",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option SKH 127 > 135", NUMERIC),
                new SubInput("ID Option Bonus", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
