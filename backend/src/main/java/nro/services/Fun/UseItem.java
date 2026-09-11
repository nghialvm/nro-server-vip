package nro.services.Fun;

import Boss.map.Nguhanhson.NguuMaVuong;
import nro.effect.EffectSkillService;
import nro.services.PlayerService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.DetuService;
import models.Item.ItemTimeService;
import nro.services.TaskService;
import models.Item.ItemService;
import nro.services.MapService;
import nro.card.Card;
import nro.card.RadarCard;
import nro.card.RadarService;
import consts.ConstMap;
import models.Item.Item;
import consts.ConstNpc;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import nro.map.Zone;
import static nro.inventory.Inventory.LIMIT_GOLD;
import nro.player.Player;
import nro.skill.Skill;
import network.io.Message;
import nro.inventory.Inventory;
import nro.inventory.InventoryService;
import QuanLiBoss.BossNomalService;
import nro.server.Manager;
import Utils.SkillUtil;
import Utils.TimeUtil;
import Utils.Util;
import Utils.Logger;
import java.util.Random;
import QuanLiBoss.Manager.BossManager;
import consts.ConstPlayer;
import consts.ConstTask;
import java.io.IOException;
import jbcd.dao.PlayerDAO;
import models.Item.ItemOption;
import models.Reward.RewardService;
import network.session.MySession;
import nro.combine.CombineService;
import nro.dragon.ChristMasEvent.ShenronChristMasEventService;
import nro.dragon.HalloweenEvent.ShenronHalloweenEventService;
import nro.dragon.SummonDragon;
import nro.map.DragonBallNamec.NgocRongNamec;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static int HP_BUFF = 100000;
    private static int MP_BUFF = 100000;
    private static int SD_BUFF = 5000;

    private static UseItem instance;
    public static final int[][][] LIST_ITEM_CLOTHES = {
        // áo , quần , găng ,giày,rada
        //td -> nm -> xd
        {{0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233, 555}, {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245, 556}, {21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257, 562}, {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269, 563}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{1, 41, 4, 42, 152, 153, 154, 155, 234, 235, 236, 237, 557}, {7, 43, 10, 44, 156, 157, 158, 159, 246, 247, 248, 249, 558}, {22, 46, 25, 45, 160, 161, 162, 163, 258, 259, 260, 261, 564}, {28, 47, 31, 48, 164, 165, 166, 167, 270, 271, 272, 273, 565}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{2, 49, 5, 50, 168, 169, 170, 171, 238, 239, 240, 241, 559}, {8, 51, 11, 52, 172, 173, 174, 175, 250, 251, 252, 253, 560}, {23, 53, 26, 54, 176, 177, 178, 179, 262, 263, 264, 265, 566}, {29, 55, 32, 56, 180, 181, 182, 183, 274, 275, 276, 277, 567}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}}
    };

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;
        if (player == null) {
            return;
        }
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    switch (player.typeBox) {
                        case 0:
                            InventoryService.gI().itemBoxToBodyOrBag(player, index);
                            break;
                        case 1:
                            InventoryService.gI().itemBoxClanToBodyOrBag(player, index);
                            break;
                        case 2:
                            InventoryService.gI().itemBoxCollectionToBodyOrBag(player, index);
                            break;
                    }
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    switch (player.typeBox) {
                        case 0:
                            InventoryService.gI().itemBagToBox(player, index);
                            break;
                        case 1:
                            InventoryService.gI().itemBagToBoxClan(player, index);
                            break;
                        case 2:
                            InventoryService.gI().itemBagToBoxCollection(player, index);
                            break;
                    }
                    break;
                case ITEM_BODY_TO_BOX:
                    switch (player.typeBox) {
                        case 0:
                            InventoryService.gI().itemBodyToBox(player, index);
                            break;
                        case 1:
                            InventoryService.gI().itemBodyToBoxClan(player, index);
                            break;
                        case 2:
                            InventoryService.gI().itemBodyToBoxCollection(player, index);
                            break;
                    }
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    switch (player.typeTabPet) {
                        case 0:
                            InventoryService.gI().itemBagToPetBody(player, index);
                            break;
                        case 1:
                            break;
                    }
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            player.setClothes.setup();
            if (player.Detu != null) {
                player.Detu.setClothes.setup();
            }

            player.setClanMember();
            Service.gI().sendFlagBag(player);
            Service.gI().point(player);
            Service.gI().sendSpeedPlayer(player, -1);
        } catch (IOException e) {
            Logger.logException(UseItem.class, e);
        }
    }

    public Item finditem(Player player, int iditem) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == iditem) {
                return item;
            }
        }
        return null;
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            if (index < 0) {
                                return;
                            }
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học " + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else if (item.template.id == 570) {
                                    if (!Util.isAfterMidnight(player.lastTimeRewardWoodChest)) {
                                        Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn mở\n" + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else if (item.template.type == 22) {
                                    if (player.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).count() > 2) {
                                        Service.gI().sendThongBaoOK(player, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn dùng\n" + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            int iditem = _msg.reader().readShort();
                            Item item = finditem(player, iditem);
                            UseItem.gI().useItem(player, item, index);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item;
                        if (index < 0) {
                            return;
                        }
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }
                        if (item.isNotNullItem() && item.template.id == 570) {
                            Service.gI().sendThongBao(player, "Không thể bỏ vật phẩm này.");
                            return;
                        }
                        if (!item.isNotNullItem()) {
                            return;
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn có chắc muốn huỷ bỏ (mất luôn)\n" + (item.quantity > 1 ? item.quantity + "x " : "") + item.template.name + " ?");
                        player.sendMessage(msg);
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryService.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryService.gI().sendItemBag(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (IOException e) {
            Logger.logException(UseItem.class, e);
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (pl.baovetaikhoan) {
            Service.gI().sendThongBao(pl, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
            return;
        }
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570) {
                if (!Util.isAfterMidnight(pl.lastTimeRewardWoodChest)) {
                    Service.gI().sendThongBao(pl, "Hãy chờ đến ngày mai");
                } else {
                    openRuongGo(pl);
                }
                return;
            }
            if (item.template.strRequire <= pl.nPoint.power) {
                switch (item.template.type) {
                    case 21:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        DetuService.PetFollow(pl, pl.getHeadThuCung(), pl.getBodyThuCung(), pl.getLegThuCung());
                        Service.gI().point(pl);
                        break;
                    case 7:
                        learnSkill(pl, item);
                        break;
                    case 33:
                        UseCard(pl, item);
                        break;
                    case 6:
                        this.eatPea(pl);
                        break;

                    case 77:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        break;
                    case 12:
                        controllerCallRongThan(pl, item);
                        break;
                    case 23:
                    case 24:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        break;
                    case 11:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendFlagBag(pl);
                        useItemChangeFlagBag(pl, item);
                        break;
                    case 18:
                        ItemUseHandler.gI().upSkillDetu(pl, item);
                        break;
                    case 19:
                        ItemUseHandler.gI().usePorata(pl, item);
                        break;
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendLinhThu(pl, (short) (item.template.iconID - 1));
                        break;
                    case 39:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.getInstance().sendChanMenh(pl, item.template.id);
                        break;
                    default:
                        switch (item.template.id) {
                            case 992:
                                if (TaskService.gI().getIdTask(pl) == ConstTask.TASK_31_1) {
                                    TaskService.gI().doneTask(pl, ConstTask.TASK_31_1);
                                }
                                ChangeMapService.gI().changeMapBySpaceShip(pl, 160, -1, -1);
                                break;
                            case 1935:
                                ItemUseHandler.gI().useVePotara(pl);
                                break;
                            case 361:
                                pl.idGo = (short) Util.nextInt(0, 6);
                                NgocRongNamec.gI().menuCheckTeleNamekBall(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                InventoryService.gI().sendItemBag(pl);
                                break;
                            case 211: //nho tím
                            case 212: //nho xanh
                                eatGrapes(pl, item);
                                break;
                            case 1105://hop qua skh, item 2002 xd
                                UseItem.gI().Hopts(pl, item);
                                break;
                            case 13141:
                                if (pl.thoigianduhanh == 0) {
                                    pl.thoigianduhanh += System.currentTimeMillis() + (1000 * 60 * 60 * 2);
                                } else {
                                    pl.thoigianduhanh += (1000 * 60 * 60 * 2);
                                }
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                break;
                            case 1911:
                                ItemUseHandler.gI().useBinhHutNangLuong(pl, item);
                                break;
                            case 1949:
                                ItemUseHandler.gI().doiDeTuMoi(pl, item);
                                break;
                            case 1997://hop qua skh, item 2002 xd
                                Openhopct(pl, item);
                                break;

                            case 1994:// Hộp Quà Top 1
                                Openhoptop1(pl, item);
                                break;
                            case 1995:// Hộp Quà Top 2
                                Openhoptop2(pl, item);
                                break;
                            case 1996:// Hộp Quà Top 3
                                Openhoptop3(pl, item);
                                break;
                            case 1998://hop qua skh, item 2002 xd
                                Openhopflagbag(pl, item);
                                break;
                            case 1999://hop qua skh, item 2002 xd
                                Openhoppet(pl, item);
                                break;
                            case 380: //cskb
                                openCSKB(pl, item);
                                break;
                            case 1365:
                                openHopTangNgoc(pl, item);
                                break;
                            case 460:
                                BossNomalService.SoiHecQuyn(pl, item);
                                break;

                            case 342:
                            case 343:
                            case 344:
                            case 345:
                                if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).count() < 3) {
                                    Service.gI().dropSatellite(pl, item, pl.zone, pl.location.x, pl.location.y);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBaoOK(pl, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                }
                                break;
                            case 381: //cuồng nộ
                            case 382: //bổ huyết
                            case 383: //bổ khí
                            case 384: //giáp xên
                            case 385: //ẩn danh
                            case 541://hd
                            case 542://hd
                            case 1327://thuocth
                            case 1498://thuocth
                            case 379: //máy dò capsule
                            case 2037: //Quả trng ha
                            case 638://cms
                            case 764://khảu trang
                            case 899://   
                            case 900:// 
                            case 902://
                            case 903://
                            case 880://crm
                            case 881://bachtuocnuong
                            case 882://tomtambot
                            case 1164://bocpha
                            case 663: //bánh pudding
                            case 664: //xúc xíc
                            case 665: //kem dâu
                            case 666: //mì ly
                            case 667: //sushi
                            case 1150:
                            case 1151:
                            case 1152:
                            case 1153:
                            case 1154:
                            case 1233://nồi cơm điện
                            case 752:
                            case 753:
                            case 1189:
                            case 1190:
                            case 1480:
                            case 1481:
                            case 1852:
                            case 1404:
                            case 1405:
                            case 1406:
                            case 1407:
                            case 1409:
                            case 1410:
                            case 1411:
                            case 1412:
                            case 1413:
                            case 2062:
                            case 2069:
                            case 1517:
                            case 1518:
                            case 1628:
                            case 1672:
                            case 1635:
                            case 1264:
                            case 1986:
                            case 1115:
                            case 1306:
                            case 1307:
                            case 1308:
                            case 465:
                            case 466:
                            case 472:
                            case 473:
                            case 1016:
                            case 1017:
                            case 1045:
                            case 1730:
                                ItemUseHandler.gI().useItemTime(pl, item);
                                break;
                            case 1036:
                                ItemUseHandler.gI().UsePhaoHoa(pl, item);
                                break;
                            case 1559:
                                ItemUseHandler.gI().useCapsuleKichHoat(pl, item);
                                break;
                            case 1538:
                                ItemUseHandler.gI().useHopQuaKichHoat5Sao(pl, item);
                                break;
                            case 1537:
                                ItemUseHandler.gI().useHopQuaKichHoat3Sao(pl, item);
                                break;
                            case 1536:
                                ItemUseHandler.gI().useHopQuaKichHoat(pl, item);
                                break;
                            case 1690:
                                ItemUseHandler.gI().usePhieuDoiCapsule(pl, item);
                                break;
                            case 1655:
                                ItemUseHandler.gI().useCapsuleTuChon(pl);
                                break;

                            case 521: //tdlt
                                useTDLT(pl, item);
                                break;
                            case 718: //Vé tặng ngọc
                                Input.gI().createFormVeTangNgoc(pl);
                                break;
                            case 1788: //Vé tặng hồng ngọc
                                Input.gI().createFormVeTangHongNgoc(pl);
                                break;
                            case 9999:
                                UseItem.gI().usehpbuff(pl);
                                break;
                            case 9997:
                                UseItem.gI().usesdbuff(pl);
                                break;
                            case 9998:
                                UseItem.gI().usekibuff(pl);
                                break;
                            case 193: //gói 10 viên capsule
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            case 194: //capsule đặc biệt
                                openCapsuleUI(pl);
                                break;
                            case 401: //đổi đệ tử
                                changePet(pl, item);
                                break;
                            case 1800:
                            case 1801:
                                ItemUseHandler.gI().useHopQua20_10(pl, item);
                                break;
                            case 1799:
                                ItemUseHandler.gI().useThiepChuc(pl, item);
                                break;
                            case 457:
                                NpcService.gI().createMenuConMeo(pl, ConstNpc.USE_THOI_VANG, 4028, "|8|Bạn Muốn Bán Bao Nhiêu Thỏi Vàng?",
                                        "1 Thỏi Vàng", "5 Thỏi Vàng", "10 Thỏi Vàng", "20 Thỏi Vàng", "50 Thỏi Vàng",
                                        "100 Thỏi Vàng", "200 Thỏi Vàng", "500 Thỏi Vàng", "1000 Thỏi Vàng", "Đóng");
                                break;
                            case 1124:
                                NpcService.gI().createMenuConMeo(pl, ConstNpc.CAN_CUOC_CONG_DAN, pl.head == 31 ? 518 : pl.head == 64 ? 516 : pl.head == 30 ? 519
                                        : pl.head == 32 ? 525 : pl.head == 29 ? 523 : pl.head == 9 ? 524
                                                                : pl.head == 28 ? 522 : pl.head == 6 ? 520 : pl.head == 27 ? 521 : -1, "|7|CĂN CƯỚC CÔNG DÂN\n\n"
                                        + "|2|Số / No : " + pl.id + "\n"
                                        + "|2|Họ Và Tên / Full Name : " + pl.name + "\n"
                                        + "|2|Tuổi (Số Ngày Tạo Account) / Age : " + pl.SoNgayTaoAcc + " Tuổi\n"
                                        + "|2|Hành Tinh / Gender : " + (pl.gender == 0 ? "Trái Đất" : pl.gender == 1 ? "Namếc" : "Xayda") + "\n"
                                        + "|2|Quê Quán / Place Of Origin : " + (pl.gender == 0 ? "Đảo Kamê" : pl.gender == 1 ? "Đảo Guru" : "Vách Núi Đen, Thành Phố Vegeta") + "\n"
                                        + "|2|Nơi Thường Trú / Place Of Residence : " + (pl.gender == 0 ? "Nhà Gôhan" : pl.gender == 1 ? "Nhà Moori" : "Nhà Paragus") + "\n"
                                        + "|2|Quốc Tịch / Nationality : " + (pl.QuocTich == 0 ? "Việt Nam" : pl.QuocTich == 1 ? "Trung Quốc" : pl.QuocTich == 2 ? "Nhật Bản"
                                                                : pl.QuocTich == 3 ? "Hàn Quốc" : pl.QuocTich == 4 ? "Liên Bang Nga" : pl.QuocTich == 5 ? "Đức" : pl.QuocTich == 6 ? "Pháp"
                                                                                                : pl.QuocTich == 7 ? "Brasil" : pl.QuocTich == 8 ? "Canada" : pl.QuocTich == 9 ? "Hoa Kỳ"
                                                                                                                        : pl.QuocTich == 10 ? "Úc" : pl.QuocTich == 11 ? "Ấn Độ" : pl.QuocTich == 12 ? "Argentina"
                                                                                                                                                : pl.QuocTich == 13 ? "Ả Rập Xê Út" : pl.QuocTich == 14 ? "Indonesia" : pl.QuocTich == 15 ? "Iran"
                                                                                                                                                                        : pl.QuocTich == 16 ? "Mông Cổ" : pl.QuocTich == 17 ? "Angola" : pl.QuocTich == 18 ? "Thổ Nhĩ Kỳ"
                                                                                                                                                                                                : pl.QuocTich == 19 ? "Thái Lan" : pl.QuocTich == 20 ? "Tây Ban Nha" : pl.QuocTich == 21 ? "Triều Tiên" : "Không Có (Dân Tị Nạn)") + "\n\n"
                                        + "|3|Thẻ Có Tác Dụng Trong 90 Ngày",
                                        "Đóng");
                                break;
                            case 726:
                                UseItem.gI().ItemManhGiay(pl);
                                break;
                            case 2000://hop qua skh, item 2000 td
                            case 2001://hop qua skh, item 2001 nm
                            case 2002://hop qua skh, item 2002 xd
                                UseItem.gI().ItemSKH(pl, item);
                                break;
                            case 2003://hop qua skh, item 2003 td
                            case 2004://hop qua skh, item 2004 nm
                            case 2005://hop qua skh, item 2005 xd
                                UseItem.gI().ItemDHD(pl, item);
                                break;
                            case 736:
                                ItemUseHandler.gI().hopQuaTanThu(pl, item);
                                break;
                            case 568: {
                                Item trung568 = InventoryService.gI().findItemBag(pl, 568);
                                if (trung568 != null && trung568.quantity > 0) {
                                    int genderRand = pl.gender;
                                    DetuService.gI().changeMabuPet(pl, genderRand);
                                    InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, trung568, 1);
                                    InventoryService.gI().sendItemBag(pl);

                                    Service.gI().sendThongBao(pl, "Bạn đã nở thành công đệ Mabư ("
                                            + (genderRand == ConstPlayer.TRAI_DAT ? "Trái Đất"
                                                    : genderRand == ConstPlayer.NAMEC ? "Namec" : "Xayda") + ")!");
                                } else {
                                    Service.gI().sendThongBao(pl, "Bạn không có trứng Mabư (568) trong túi!");
                                }
                                break;
                            }

                             case 1975: {
                                Item trung1975 = InventoryService.gI().findItemBag(pl, 1975);
                                if (trung1975 != null && trung1975.quantity > 0) {
                                    // Random hành tinh 0=Trái Đất, 1=Namec, 2=Xayda
                                    int[] hanhTinh = {ConstPlayer.TRAI_DAT, ConstPlayer.NAMEC, ConstPlayer.XAYDA};
                                    int genderRand = hanhTinh[Util.nextInt(0, hanhTinh.length - 1)];

                                    // Đổi đệ tử thành Mabư ở hành tinh random
                                    DetuService.gI().changeUbuPet(pl);

                                    // Trừ item 568
                                    InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, trung1975, 1);
                                    InventoryService.gI().sendItemBag(pl);

                                    Service.gI().sendThongBao(pl, "Bạn đã nở thành công đệ Mabư ("
                                            + (genderRand == ConstPlayer.TRAI_DAT ? "Trái Đất"
                                                    : genderRand == ConstPlayer.NAMEC ? "Namec" : "Xayda") + ")!");
                                } else {
                                    Service.gI().sendThongBao(pl, "Bạn không có trứng UUB trong túi!");
                                }
                                break;
                            }

                            case 987:
                                Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); //đá bảo vệ
                                break;
                            case 1120:
                                useItemHopQuaTanThu(pl, item);
                                break;
                            case 725:
                                sieuthanthuy1(pl, item);
                                break;
                            case 727:
                                sieuthanthuy2(pl, item);
                                break;
                            case 728:
                                sieuthanthuy3(pl, item);
                                break;
                            case 1884:
                                ItemUseHandler.gI().useVeRiengTu(pl);
                                break;
                            case 1922:
                                ItemUseHandler.gI().OpenPoke(pl, item);
                                break;
                            case 1960:
                                ItemUseHandler.gI().OpenVanbay(pl, item);
                                break;
                            case 1923:
                                ItemUseHandler.gI().OpenHaitac(pl, item);
                                break;
                            case 1652:
                                ItemUseHandler.gI().useLoaTheGioi(pl);
                                break;
                            case 1653:
                                ItemUseHandler.gI().useLoaVuTru(pl);
                                break;

                            case 2006:
                                Input.gI().createFormChangeNameByItem(pl);
                                break;
                            case 1171:
                                ItemUseHandler.gI().openTui7ChuLun(pl, item);
                                break;
                            case 758:
                                ItemUseHandler.gI().openCapsuneTet(pl, item);
                                break;
                            case 1184:
                                ItemUseHandler.gI().openHopQuaDacBiet(pl, item);
                                break;
                            case 1187:
                                ItemUseHandler.gI().openHopQuaTet(pl, item);
                                break;
                            case 1828:
                                ItemUseHandler.gI().useHopQuaBlackFriday(pl, item);
                                break;
                            case 1173:
                                ItemUseHandler.gI().openManhThienSu(pl, item);
                                break;
                            case 1440:
                                ItemUseHandler.gI().openRuongSaoPhaLe(pl, item);
                                break;
                            case 1964:
                                ItemUseHandler.gI().openRuongSaoPhaLethuong(pl, item);
                                break;
                            case 1493:
                                ItemUseHandler.gI().openPhongBiTet(pl, item);
                                break;
                            case 1496:
                                ItemUseHandler.gI().openThiepChucMung(pl, item);
                                break;
                            case 1760:
                                ItemUseHandler.gI().openLiXiVang(pl, item);
                                break;
                            case 1759:
                                ItemUseHandler.gI().openLiXiXanh(pl, item);
                                break;
                            case 1758:
                                ItemUseHandler.gI().openLiXiVIP(pl, item);
                                break;
                            case 1575:
                                ItemUseHandler.gI().openPhaoHoaThuong(pl, item);
                                break;
                            case 1576:
                                ItemUseHandler.gI().openPhaoHoaThuongVIP(pl, item);
                                break;
                            case 1989:
                                ItemUseHandler.gI().showBossMember(pl, item);
                                break;
                            case 1591:
                            case 1594:
                                ItemUseHandler.gI().openHopQuaGokuDay(pl, item);
                                break;
                            case 1592:
                                ItemUseHandler.gI().openHopQuaGokuDayVIP(pl, item);
                                break;
                            case 1836:
                            case 1837:
                                ItemUseHandler.gI().openHopQuaCadic(pl, item);
                                break;
                            case 1838:
                                ItemUseHandler.gI().openHopQuaCadicVip(pl, item);
                                break;
                            case 1408:
                                ItemUseHandler.gI().UseHoiSkills(pl, item);
                                break;
                            case 2063:
                            case 2064:
                            case 2065:
                            case 2066:
                            case 2067:
                            case 2068:
                                ItemUseHandler.gI().UseItemThuoc(pl, item);
                                break;
                            case 1448:
                                ItemUseHandler.gI().openNguoiTuyet(pl, item);
                                break;
                            case 1449:
                                ItemUseHandler.gI().openNguoiTuyetBangGia(pl, item);
                                break;
                            case 648:
                                ItemUseHandler.gI().openHopQuaGiangSinh(pl, item);
                                break;
                            case 1840:
                                ItemUseHandler.gI().UseQueDiem(pl, item);
                                break;
                            case 1847:
                                ItemUseHandler.gI().UseQueDiemBungChay(pl, item);
                                break;
                            case 456:
                                BossNomalService.XinBaTo(pl, item);
                                break;
                            case 1038:
                                ItemUseHandler.gI().UseHoaDangCoLoiChuc(pl, item);
                                break;
                            case 1037:
                                ItemUseHandler.gI().UseHoaDang(pl, item);
                                break;
                            case 1262:
                                ItemUseHandler.gI().HoiSinhLichTen(pl, item);
                                break;
                            case 1263:
                                ItemUseHandler.gI().HoiSinhSieuLichTen(pl, item);
                                break;
                            case 1356:
                                ItemUseHandler.gI().useHopKeoMaQuy(pl, item);
                                break;
                            case 1357:
                                ItemUseHandler.gI().useKeoHalloween(pl, item);
                                break;
                            case 1117:
                                ItemUseHandler.gI().useThiepHalloween(pl, item);
                                break;
                            case 1352:
                                ItemUseHandler.gI().useBoKeoKinhDi(pl, item);
                                break;
                            case 818:
                                ItemUseHandler.gI().useCaplsuneHalloween(pl, item);
                                break;
                            case 1116:
                                ItemUseHandler.gI().useHomHalloween(pl, item);
                                break;
                            case 915:
                                ItemUseHandler.gI().useCapsuneSquidGame(pl, item);
                                break;
                            case 1809:
                                ItemUseHandler.gI().useTuiMuHalloween(pl, item);
                                break;
                            case 722:
                                ItemUseHandler.gI().useCapsuleHong(pl, item);
                                break;
                            case 1769:
                                ItemUseHandler.gI().useHopBabyThreeThuong(pl, item);
                                break;
                            case 1770:
                                ItemUseHandler.gI().useHopBabyThreeVIP(pl, item);
                                break;
                            case 1521:
                                ItemUseHandler.gI().useThiepMung8_3(pl, item);
                                break;
                            case 1505:
                                ItemUseHandler.gI().useGiaymau(pl);
                                break;
                            case 1509:
                                ItemUseHandler.gI().GoiHopQuaChinChu(pl);
                                break;
                            case 1506:
                            case 1507:
                            case 1508:
                                ItemUseHandler.gI().SocolaTraiTim(pl);
                                break;
                            case 1527:
                            case 1525:
                            case 1528:
                            case 1526:
                                ItemUseHandler.gI().OngTreNuoc(pl);
                                break;
                            case 1529:
                                ItemUseHandler.gI().TrongBongHoaHong_Max(pl);
                                break;
                            case 2013:
                                ItemUseHandler.gI().UseMilk(pl, item);
                                break;
                            case 737:
                                ItemUseHandler.gI().useCapsuleTrungThu(pl, item);
                                break;
                            case 579:
                                ItemUseHandler.gI().UseDuoiKhi(pl, item);
                                break;
                            case 1695:
                                ItemUseHandler.gI().useHopQuaThang9(pl, item);
                                break;
                            case 1696:
                                ItemUseHandler.gI().useHopQuaThang9VIP(pl, item);
                                break;
                            case 1701:
                                ItemUseHandler.gI().useHopBanhTrungThuDacBiet(pl, item);
                                break;
                            case 1305:
                                ItemUseHandler.gI().useAnhTrangTron(pl, item);
                                break;
                            case 1310:
                                ItemUseHandler.gI().useThoNgoc(pl, item);
                                break;
                            case 1540:
                                ItemUseHandler.gI().useRadaPhongXa(pl);
                                break;
                            case 963:
                                ItemUseHandler.gI().hopCaiTrang(pl, item);
                                break;
                            case 1881:

                                ItemUseHandler.gI().useManhTrungRongNhi(pl);
                                break;
                            case 1565:
                                ItemUseHandler.gI().useBanDoTruyenThuyet(pl);
                                break;
                            case 1771:
                                ItemUseHandler.gI().useHatDuaHau(pl, item);
                                break;
                            case 1227:
                                ItemUseHandler.gI().useHopQuaGioToThuong(pl, item);
                                break;
                            case 1228:
                                ItemUseHandler.gI().useHopQuaGioToXin(pl, item);
                                break;
                            case 1879:
                                ItemUseHandler.gI().openTrungRongNhi(pl, item);
                                break;
                            case 1880:
                                ItemUseHandler.gI().openTrungRongNhiVIP(pl, item);
                                break;
                            case 1882:
                                ItemUseHandler.gI().useHopQuaHungVuong(pl, item);
                                break;
                            case 1883:
                                ItemUseHandler.gI().useHopQuaHungVuongVIP(pl, item);
                                break;
                            case 1990:
                                ItemUseHandler.gI().useHopQuaThanLinh(pl, item);
                                break;
                            case 1985:
                                ItemUseHandler.gI().UseTuiSieuAn(pl, item);
                                break;
                             case 2417:
                                ItemUseHandler.gI().openTuiVang(pl, item);
                                break;
                            case 1131:
                                if (pl.Detu == null) {
                                    Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                    break;
                                }

                                if (pl.Detu.playerSkill.skills.get(1).skillId != -1 && pl.Detu.playerSkill.skills.get(2).skillId != -1) {
                                    pl.Detu.openSkill2();
                                    pl.Detu.openSkill3();
                                    InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                    InventoryService.gI().sendItemBag(pl);
                                    Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 2 3 đệ tử");
                                } else {
                                    Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");
                                }
                                break;
                            case 2431: // Đổi chiêu 2 đệ tử
                                if (pl.Detu == null) {
                                    Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                    break;
                                }
                                if (pl.Detu.playerSkill.skills.get(1).skillId != -1) {
                                    pl.Detu.openSkill2();
                                    InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                    InventoryService.gI().sendItemBag(pl);
                                    Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 2 đệ tử");
                                } else {
                                    Service.gI().sendThongBao(pl, "Đệ tử của ngươi chưa có chiêu 2!");
                                }
                                break;

                            case 2432: // Đổi chiêu 3 đệ tử
                                if (pl.Detu == null) {
                                    Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                    break;
                                }
                                if (pl.Detu.playerSkill.skills.get(2).skillId != -1) {
                                    pl.Detu.openSkill3();
                                    InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                    InventoryService.gI().sendItemBag(pl);
                                    Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 3 đệ tử");
                                } else {
                                    Service.gI().sendThongBao(pl, "Đệ tử của ngươi chưa có chiêu 3!");
                                }
                                break;

                            case 2433: // Đổi chiêu 4 đệ tử
                                if (pl.Detu == null) {
                                    Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                    break;
                                }
                                if (pl.Detu.playerSkill.skills.get(3).skillId != -1) {
                                    pl.Detu.openSkill4();
                                    InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                    InventoryService.gI().sendItemBag(pl);
                                    Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 4 đệ tử");
                                } else {
                                    Service.gI().sendThongBao(pl, "Đệ tử của ngươi chưa có chiêu 4!");
                                }
                                break;

                            case 2027:
                            case 2028: {
                                if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
                                    Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống");
                                } else {
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                    Item linhThu = ItemService.gI().createNewItem((short) Util.nextInt(2019, 2026));
                                    linhThu.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
                                    linhThu.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
                                    linhThu.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
                                    InventoryService.gI().addItemBag(pl, linhThu);
                                    InventoryService.gI().sendItemBag(pl);
                                    Service.gI().sendThongBao(pl, "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                                }
                                break;
                            }
                            case 1568:
                                if (pl.zone != null && MapService.gI().isMapNguHanhSon(pl.zone.map.mapId)) {
                                    Item cuonkinh = InventoryService.gI().findItemBag(pl, 1568);
                                    int randomsoluong = Util.nextInt(1, 10);
                                    Item kinh = ItemService.gI().createNewItem((short) 1570, randomsoluong);
                                    kinh.itemOptions.add(new ItemOption(86, 1));
                                    int time = 20000;
                                    if (pl != null) {
                                        EffectSkillService.gI().setBlindDCTT(pl, System.currentTimeMillis(), time);
                                        EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.DOKINH_EFFECT);
                                        ItemTimeService.gI().sendItemTime(pl, 24254, time / 1000);
                                        Service.gI().sendThongBao(pl, "Đang Dò Kinh, Vui Lòng Đợi " + "20" + " Giây Nữa!");
                                        try {
                                            Thread.sleep(time);
                                        } catch (Exception e) {
                                        }
                                        InventoryService.gI().addItemBag(pl, kinh);
                                        InventoryService.gI().sendItemBag(pl);
                                        InventoryService.gI().subQuantityItemsBag(pl, cuonkinh, 1);
                                        Service.gI().sendThongBao(pl, "Dò Thành Công, Bạn Nhận Được " + randomsoluong + " Kinh Thư!");
                                    }
                                } else {
                                    Service.gI().sendThongBao(pl, "|2|Chỉ Dò Được Kinh Khi Bạn Ở Map Ngũ Hành Sơn!");
                                }
                                break;
                            case 1569:
                                if (pl.zone != null && MapService.gI().isMapNguHanhSon(pl.zone.map.mapId)) {
                                    Item cuonkinhcaocap = InventoryService.gI().findItemBag(pl, 1569);
                                    int randomsoluong2 = Util.nextInt(5, 20);
                                    Item kinh2 = ItemService.gI().createNewItem((short) 1570, randomsoluong2);
                                    kinh2.itemOptions.add(new ItemOption(86, 1));
                                    int time2 = 10000;
                                    if (pl != null) {
                                        EffectSkillService.gI().setBlindDCTT(pl, System.currentTimeMillis(), time2);
                                        EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.DOKINH_EFFECT);
                                        ItemTimeService.gI().sendItemTime(pl, 24255, time2 / 1000);
                                        Service.gI().sendThongBao(pl, "Đang Dò Kinh, Vui Lòng Đợi " + "10" + " Giây Nữa!");
                                        try {
                                            Thread.sleep(time2);
                                        } catch (Exception e) {
                                        }
                                        InventoryService.gI().addItemBag(pl, kinh2);
                                        InventoryService.gI().sendItemBag(pl);
                                        InventoryService.gI().subQuantityItemsBag(pl, cuonkinhcaocap, 1);
                                        Service.gI().sendThongBao(pl, "Dò Thành Công, Bạn Nhận Được " + randomsoluong2 + " Kinh Thư!");
                                    }
                                } else {
                                    Service.gI().sendThongBao(pl, "|2|Chỉ Dò Được Kinh Khi Bạn Ở Map Ngũ Hành Sơn!");
                                }
                                break;
                            case 1571:
                                if (pl.zone != null && MapService.gI().isMapNguHanhSon(pl.zone.map.mapId)) {
                                    trieuhoinguumavuong(pl, item);
                                } else {
                                    Service.gI().sendThongBao(pl, "Hãy Đến Ngũ Hành Sơn Để Sử Dụng!");
                                }
                                break;
                            case 2290:
                                // tìm item đang dùng
                                Item it = InventoryService.gI().findItemBag(pl, 2290);
                                if (it != null && it.quantity > 0) {
                                    // trừ 1 item
                                    InventoryService.gI().subQuantityItemsBag(pl, it, 1);
                                    InventoryService.gI().sendItemBag(pl);

                                    // reset bùa
                                    PlayerDAO.resetCharms(pl);
                                } else {
                                    Service.gI().sendThongBao(pl, "Bạn không có vật phẩm để sử dụng!");
                                }
                                break;

                        }
                        break;
                }
                InventoryService.gI().sendItemBag(pl);
            } else {
                Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
            }
        }
    }

    public void openRuongGo(Player player) {
        Item ruongGo = InventoryService.gI().findItemBag(player, 570);
        if (ruongGo == null) {
            Service.gI().sendThongBao(player, "Không có Rương gỗ trong hành trang!");
            return;
        }

        int level = InventoryService.gI().getParam(player, 72, 570);
        if (level <= 0) {
            level = 1;
        }
        if (level > 12) {
            level = 12; // Giới hạn 12 cấp
        }
        if (InventoryService.gI().getCountEmptyBag(player) < 5) {
            Service.gI().sendThongBao(player, "Cần ít nhất 5 ô trống trong hành trang để mở rương!");
            return;
        }

        // Trừ rương
        InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
        InventoryService.gI().sendItemBag(player);

        // Danh sách đồ shop theo thứ tự cấp
        int[] aoIds = {0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233};
        int[] quanIds = {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245};
        int[] gangIds = {21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257};
        int[] giayIds = {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269};
        int[] radaIds = {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281};

        int[][] all = {aoIds, quanIds, gangIds, giayIds, radaIds};

        // Random ra 1 trong 5 loại
        int type = Util.nextInt(0, 4);
        int index = Math.min(level - 1, all[type].length - 1);
        int tempId = all[type][index];

        Item item = ItemService.gI().createNewItem((short) tempId);
        RewardService.gI().initBaseOptionClothes(tempId, type, item.itemOptions);
        item.itemOptions.add(new ItemOption(30, 0));
        InventoryService.gI().addItemBag(player, item);

        // 💰 Thưởng vàng & ruby theo cấp
        long vangThuong = level * 1_000_000L;
        int rubyThuong = level;

        player.inventory.gold += vangThuong;
        player.inventory.ruby += rubyThuong;
        Service.gI().sendMoney(player);

        // 🎁 Phụ kiện (sao pha lê / đá quý)
        int[] saoIds = {441, 442, 443, 444, 445, 446, 447};
        int[] daIds = {220, 221, 222, 223, 224};
        int soPhuKien = Util.nextInt(0, 2);
        for (int i = 0; i < soPhuKien; i++) {
            boolean laSao = Util.isTrue(50, 100);
            int tempIdBonus = laSao ? saoIds[Util.nextInt(saoIds.length)] : daIds[Util.nextInt(daIds.length)];
            Item bonus = ItemService.gI().createNewItem((short) tempIdBonus);
            bonus.itemOptions.add(new ItemOption(laSao ? 30 : 14, 3));
            bonus.quantity = 1;
            InventoryService.gI().addItemBag(player, bonus);
        }

        // 💎 Ngọc Rồng (16–20)
        if (Util.isTrue(50, 100)) {
            int saoId = Util.nextInt(16, 21);
            Item ngoc = ItemService.gI().createNewItem((short) saoId);
            InventoryService.gI().addItemBag(player, ngoc);
        }

        InventoryService.gI().sendItemBag(player);

        // Thông báo
        String thongBao = "|2|Rương gỗ cấp " + level
                + " mở ra: " + item.template.name
                + "\n+ " + Util.format(vangThuong) + " vàng"
                + "\n+ " + rubyThuong + " hồng ngọc";

        Service.gI().sendThongBao(player, thongBao);
    }

    private void Openhopct(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 5) {
            Item aotl = ItemService.gI().createNewItem((short) 557);
            Item quantl = ItemService.gI().createNewItem((short) 558);
            Item gangtl = ItemService.gI().createNewItem((short) 564);
            Item giaytl = ItemService.gI().createNewItem((short) 565);
            Item nhantl = ItemService.gI().createNewItem((short) 561);

            aotl.itemOptions.add(new ItemOption(47, 1091));
            aotl.itemOptions.add(new ItemOption(72, 3));
            aotl.itemOptions.add(new ItemOption(107, 8));

            quantl.itemOptions.add(new ItemOption(6, 70000));
            quantl.itemOptions.add(new ItemOption(27, 6868));
            quantl.itemOptions.add(new ItemOption(72, 2));
            quantl.itemOptions.add(new ItemOption(107, 8));

            gangtl.itemOptions.add(new ItemOption(0, 5514));
            gangtl.itemOptions.add(new ItemOption(72, 2));
            gangtl.itemOptions.add(new ItemOption(107, 8));

            giaytl.itemOptions.add(new ItemOption(7, 62591));
            giaytl.itemOptions.add(new ItemOption(28, 4719));
            giaytl.itemOptions.add(new ItemOption(72, 2));
            giaytl.itemOptions.add(new ItemOption(107, 8));

            nhantl.itemOptions.add(new ItemOption(14, 19));
            nhantl.itemOptions.add(new ItemOption(72, 4));
            nhantl.itemOptions.add(new ItemOption(107, 8));

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, aotl);
            InventoryService.gI().addItemBag(pl, quantl);
            InventoryService.gI().addItemBag(pl, gangtl);
            InventoryService.gI().addItemBag(pl, giaytl);
            InventoryService.gI().addItemBag(pl, nhantl);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được quà bù");
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 5 ô trống trong hành trang.");
        }
    }

    private void Openhoptop1(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 2) {
            Item danhhieu = ItemService.gI().createNewItem((short) 1350);
            Item daphale = ItemService.gI().createNewItem((short) 1399, 3);

            danhhieu.itemOptions.add(new ItemOption(50, 15));
            danhhieu.itemOptions.add(new ItemOption(77, 15));
            danhhieu.itemOptions.add(new ItemOption(103, 15));

            daphale.itemOptions.add(new ItemOption(50, 5));

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, danhhieu);
            InventoryService.gI().addItemBag(pl, daphale);

            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được quà Top 1");
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống trong hành trang.");
        }
    }

    private void Openhoptop2(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 2) {
            Item danhhieu = ItemService.gI().createNewItem((short) 1351);
            Item daphale = ItemService.gI().createNewItem((short) 1399, 2);

            danhhieu.itemOptions.add(new ItemOption(50, 10));
            danhhieu.itemOptions.add(new ItemOption(77, 10));
            danhhieu.itemOptions.add(new ItemOption(103, 10));

            daphale.itemOptions.add(new ItemOption(50, 5));

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, danhhieu);
            InventoryService.gI().addItemBag(pl, daphale);

            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được quà Top 2");
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống trong hành trang.");
        }
    }

    private void Openhoptop3(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 2) {
            Item danhhieu = ItemService.gI().createNewItem((short) 1352);
            Item daphale = ItemService.gI().createNewItem((short) 1399, 1);

            danhhieu.itemOptions.add(new ItemOption(50, 5));
            danhhieu.itemOptions.add(new ItemOption(77, 5));
            danhhieu.itemOptions.add(new ItemOption(103, 5));

            daphale.itemOptions.add(new ItemOption(50, 5));

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, danhhieu);
            InventoryService.gI().addItemBag(pl, daphale);

            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được quà Top 3");
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống trong hành trang.");
        }
    }

    private void Openhopflagbag(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int id = Util.nextInt(0, 100);
            int[] rdfl = new int[]{1157, 1203, 1204, 1205, 1206, 1207, 954, 955, 1211, 1212, 1213,
                1214, 1215, 1216, 1217, 1218, 1219, 1220, 1221, 966, 1222, 1223, 1224, 1225, 1226, 1228,
                1229, 467, 468, 469, 470, 982, 471, 983, 994, 995, 740, 996, 741, 997, 998, 999, 1000, 745,
                1001, 1007, 2035, 1013, 1021, 766, 1022, 767, 1023};
            int[] rdop = new int[]{50, 77, 103};
            int randomfl = new Random().nextInt(rdfl.length);
            int randomop = new Random().nextInt(rdop.length);
            Item fl = ItemService.gI().createNewItem((short) rdfl[randomfl]);
            Item vt = ItemService.gI().createNewItem((short) Util.nextInt(342, 345));
            if (id <= 90) {
                fl.itemOptions.add(new ItemOption(rdop[randomop], Util.nextInt(5, 10)));
                fl.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
            } else {
                fl.itemOptions.add(new ItemOption(rdop[randomop], Util.nextInt(5, 10)));
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, fl);
            InventoryService.gI().addItemBag(pl, vt);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + fl.template.name + " và " + vt.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    private void trieuhoinguumavuong(Player pl, Item item) {
        Item vetrieuhoinguumavuong = null;
        try {
            vetrieuhoinguumavuong = InventoryService.gI().findItemBag(pl, 1571);
        } catch (Exception e) {
        }
        Boss oldBossClone = BossManager.gI().getBossById(BossID.NguuMaVuong);
        if (vetrieuhoinguumavuong == null || vetrieuhoinguumavuong.quantity < 1) {
            Service.gI().sendThongBao(pl, "Bạn không có Vé Triệu Hồi Ngưu Ma Vương");
        } else if (oldBossClone != null) {
            Service.gI().sendThongBao(pl, "Hãy Tìm Và Tiêu Diệt Ngưu Ma Vương Cũ Đi Đã!");
        } else {
            try {
                Service.gI().sendThongBao(pl, "|2|Triệu Hồi Thành Công!");
                NguuMaVuong dt = new NguuMaVuong(BossID.NguuMaVuong,
                        BossesData.NguuMaVuong, pl.zone, pl.location.x + 20,
                        pl.location.y);
                dt.playerReward = pl;
                pl.Boss = true;
            } catch (Exception e) {
                e.printStackTrace();
                Logger.logException(Manager.class, e, "Lỗi");
            }
            InventoryService.gI().subQuantityItemsBag(pl, vetrieuhoinguumavuong, 1);
        }
    }

    private void Openhoppet(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int id = Util.nextInt(0, 100);
            int[] rdpet = new int[]{1311, 1312, 1313};
            int[] rdop = new int[]{50, 77, 103};
            int randompet = new Random().nextInt(rdpet.length);
            int randomop = new Random().nextInt(rdop.length);
            Item pet = ItemService.gI().createNewItem((short) rdpet[randompet]);
            Item vt = ItemService.gI().createNewItem((short) Util.nextInt(342, 345));
            if (id <= 90) {
                pet.itemOptions.add(new ItemOption(50, 13));
                pet.itemOptions.add(new ItemOption(77, 12));
                pet.itemOptions.add(new ItemOption(103, 14));
                pet.itemOptions.add(new ItemOption(93, Util.nextInt(3, 15)));
            } else {
                pet.itemOptions.add(new ItemOption(50, 13));
                pet.itemOptions.add(new ItemOption(77, 12));
                pet.itemOptions.add(new ItemOption(103, 14));

            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, pet);
            InventoryService.gI().addItemBag(pl, vt);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + pet.template.name + " và " + vt.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    public void UseCard(Player pl, Item item) {
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                .filter(c -> c.Id == item.template.id)
                .findFirst()
                .orElse(null);
        if (radarTemplate == null) {
            return;
        }

        if (radarTemplate.Require != -1) {
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                    .filter(r -> r.Id == radarTemplate.Require)
                    .findFirst()
                    .orElse(null);
            if (radarRequireTemplate == null) {
                return;
            }
            Card cardRequire = pl.Cards.stream()
                    .filter(r -> r.Id == radarRequireTemplate.Id)
                    .findFirst()
                    .orElse(null);
            if (cardRequire == null || cardRequire.Level < radarTemplate.RequireLevel) {
                Service.gI().sendThongBao(pl,
                        "Bạn cần sưu tầm " + radarRequireTemplate.Name
                        + " ở cấp độ " + radarTemplate.RequireLevel
                        + " mới có thể sử dụng thẻ này");
                return;
            }
        }

        Card card = pl.Cards.stream()
                .filter(r -> r.Id == item.template.id)
                .findFirst()
                .orElse(null);

        if (card == null) {
            Card newCard = new Card(item.template.id, (byte) 1, radarTemplate.Max, (byte) -1, radarTemplate.Options);
            if (pl.Cards.add(newCard)) {
                RadarService.gI().RadarSetAmount(pl, newCard.Id, newCard.Amount, newCard.MaxAmount);
                RadarService.gI().RadarSetLevel(pl, newCard.Id, newCard.Level);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().sendItemBag(pl);

                // 🔥 cập nhật aura
                RadarService.gI().updateAura(pl);
            }
        } else {
            if (card.Level >= 2) {
                Service.gI().sendThongBao(pl, "Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount) {
                card.Amount = 0;
                if (card.Level == -1) {
                    card.Level = 1;
                } else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl, card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, card.Id, card.Level);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);

            // 🔥 cập nhật aura
            RadarService.gI().updateAura(pl);
        }
    }

    private void useItemChangeFlagBag(Player player, Item item) {
        switch (item.template.id) {
            case 994: //vỏ ốc
                break;
            case 995: //cây kem
                break;
            case 996: //cá heo
                break;
            case 997: //con diều
                break;
            case 998: //diều rồng
                break;
            case 999: //mèo mun
                if (!player.effectFlagBag.useMeoMun) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1000: //xiên cá
                if (!player.effectFlagBag.useXienCa) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1001: //phóng heo
                if (!player.effectFlagBag.usePhongHeo) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
        }
        Service.gI().point(player);
        Service.gI().sendFlagBag(player);
    }

    private void changePet(Player player, Item item) {
        player.Item_ChangePet = item;
        Input.gI().createChangePet(player);
    }

    private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
            ct.itemOptions.add(new ItemOption(147, 3));
            ct.itemOptions.add(new ItemOption(77, 3));
            ct.itemOptions.add(new ItemOption(103, 3));
            ct.itemOptions.add(new ItemOption(149, 0));
            if (item.template.id == 2006) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            } else if (item.template.id == 2007) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
            }
            InventoryService.gI().addItemBag(pl, ct);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void sieuthanthuy1(Player pl, Item item) {
        Item sieuthanthuy = null;
        try {
            sieuthanthuy = InventoryService.gI().findItemBag(pl, 725);
        } catch (Exception e) {
        }
        if (Util.isTrue(60, 100)) {
            for (int stt = 0; stt < 10; stt++) {
                Service.gI().congTiemNang(pl, (byte) 2, 1000000);
            }
            InventoryService.gI().subQuantityItemsBag(pl, sieuthanthuy, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Bạn nhận được 10 Triệu tiềm năng và sức mạnh");
        } else {
            pl.setDieLV(pl);
            InventoryService.gI().subQuantityItemsBag(pl, sieuthanthuy, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực Siêu Thần Thuỷ");
        }
    }

    private void sieuthanthuy2(Player pl, Item item) {
        Item sieuthanthuy = null;
        try {
            sieuthanthuy = InventoryService.gI().findItemBag(pl, 727);
        } catch (Exception e) {
        }
        if (Util.isTrue(50, 100)) {
            for (int stt = 0; stt < 10; stt++) {
                Service.gI().congTiemNang(pl, (byte) 2, 10000000);
            }
            InventoryService.gI().subQuantityItemsBag(pl, sieuthanthuy, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Bạn nhận được 100 Triệu tiềm năng và sức mạnh");
        } else {
            pl.setDieLV(pl);
            InventoryService.gI().subQuantityItemsBag(pl, sieuthanthuy, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực Siêu Thần Thuỷ");
        }
    }

    private void sieuthanthuy3(Player pl, Item item) {
        Item sieuthanthuy = null;
        try {
            sieuthanthuy = InventoryService.gI().findItemBag(pl, 728);
        } catch (Exception e) {
        }
        if (Util.isTrue(40, 100)) {
            for (int stt = 0; stt < 10; stt++) {
                Service.gI().congTiemNang(pl, (byte) 2, 50000000);
            }
            InventoryService.gI().subQuantityItemsBag(pl, sieuthanthuy, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Bạn nhận được 500 Triệu tiềm năng và sức mạnh");
        } else {
            pl.setDieLV(pl);
            InventoryService.gI().subQuantityItemsBag(pl, sieuthanthuy, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực Siêu Thần Thuỷ");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {76, 188, 189, 190, 381, 382, 383, 384, 385};
            int[][] gold = {{5000, 20000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);

            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openHopTangNgoc(Player pl, Item item) {
        Input.gI().createFormTangNgocHong(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void useItemHopQuaTanThu(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {14, 16, 17, 18, 19, 20, 21, 22};
            int[][] gold = {{100000000, 200000000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);

            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        } else if (tempId >= ShenronHalloweenEventService.NGOC_RONG_1_SAO && tempId <= ShenronHalloweenEventService.NGOC_RONG_7_SAO) {
            ShenronHalloweenEventService.gI().openMenuSummonShenron(pl, 0);
        } else if (tempId >= ShenronChristMasEventService.NGOC_RONG_1_SAO && tempId <= ShenronChristMasEventService.NGOC_RONG_7_SAO) {
            ShenronChristMasEventService.gI().openMenuSummonShenron(pl, 0);
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {//Hoc skill moi
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else { // neu chua hoc ma hoc lv cao
                            Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.gI().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            pl.BoughtSkill.add((int) item.template.id);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryService.gI().sendItemBag(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void ItemManhGiay(Player pl) {
        if (pl.winSTT && !Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            Service.gI().sendThongBao(pl, "Hãy gặp thần mèo Karin để sử dụng");
            return;
        } else if (pl.winSTT && Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            pl.winSTT = false;
            pl.callBossPocolo = false;
            pl.zoneSieuThanhThuy = null;
        }
        NpcService.gI().createMenuConMeo(pl, ConstNpc.MENU_OPTION_USE_ITEM726, 564, "Đây chính là dấu hiệu riêng của...\nĐại Ma Vương Pôcôlô\nĐó là một tên quỷ dữ đội lốt người, một kẻ đại gian ác\ncó sức mạnh vô địch và lòng tham không đáy...\nĐối phó với hắn không phải dễ\nCon có chắc chắn muốn tìm hắn không?", "Đồng ý", "Từ chối");
    }

    private void usehpbuff(Player pl) {
        Item hpbuff = null;
        for (Item item : pl.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 1153) {
                hpbuff = item;
                break;
            }
        }
        if (hpbuff != null) {
            pl.nPoint.hpg += HP_BUFF;
            InventoryService.gI().subQuantityItemsBag(pl, hpbuff, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBaoOK(pl, "HP Gốc của bạn đã tăng  " + HP_BUFF);
            Service.getInstance().point(pl);
        }
    }

    private void usesdbuff(Player pl) {
        Item sdbuff = null;
        for (Item item : pl.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 1152) {
                sdbuff = item;
                break;
            }
        }
        if (sdbuff != null) {
            pl.nPoint.dameg += SD_BUFF;
            InventoryService.gI().subQuantityItemsBag(pl, sdbuff, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBaoOK(pl, "SD Gốc của bạn đã tăng  " + SD_BUFF);
            Service.getInstance().point(pl);
        }
    }

    private void usekibuff(Player pl) {
        Item kibuff = null;
        for (Item item : pl.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 1154) {
                kibuff = item;
                break;
            }
        }
        if (kibuff != null) {
            pl.nPoint.mpg += MP_BUFF;
            InventoryService.gI().subQuantityItemsBag(pl, kibuff, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBaoOK(pl, "KI Gốc của bạn đã tăng  " + MP_BUFF);
            Service.getInstance().point(pl);
        }
    }

    public void usethoivang(Player player) {
        Item tv = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 457) {
                tv = item;
                break;
            }
        }
        if (tv != null) {
            if (player.inventory.gold <= LIMIT_GOLD) {
                InventoryService.gI().subQuantityItemsBag(player, tv, 1);
                player.inventory.gold += 500000000;
                PlayerService.gI().sendInfoHpMpMoney(player);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.getInstance().sendThongBao(player, "Đã đạt giới hạn vàng");
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {
        if (pl.idNRNM != -1) {
            Service.gI().sendThongBao(pl, "Không thể mang ngọc rồng này lên Phi thuyền");
            Service.gI().hideWaitDialog(pl);
            return;
        }

        int zoneId = -1;
        if (index > pl.mapCapsule.size() - 1 || index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            Service.gI().hideWaitDialog(pl);
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        //Kiểm tra số lượng người trong khu

        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu12H(zoneChose.map.mapId)
                || MapService.gI().isMapBanDoKhoBau(zoneChose.map.mapId)) {
            Service.gI().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        pl.changeMapVIP = true;
        pl.changeMapVIP_TuanLoc = true;
        pl.changeMapVIP_MeoDen = true;
        pl.changeMapVIP_PiLong = true;
        pl.changeMapVIP_PeNa = true;
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    public void eatPea(Player player) {
        if (!Util.canDoWithTime(player.lastTimeEatPea, 10_000)) {
            Service.getInstance().sendThongBao(player, "Vui lòng đợi " + TimeUtil.getTimeLeft(player.lastTimeEatPea, 10) + " nữa!");
            return;
        }
        player.lastTimeEatPea = System.currentTimeMillis();
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            long hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(Util.CrisGH(player.nPoint.hp + hpKiHoiPhuc));
            player.nPoint.setMp(Util.CrisGH(player.nPoint.mp + hpKiHoiPhuc));
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.Detu != null && player.zone.equals(player.Detu.zone) && !player.Detu.isDie()) {
                int statima = 100 * lvPea;
                player.Detu.nPoint.stamina += statima;
                if (player.Detu.nPoint.stamina > player.Detu.nPoint.maxStamina) {
                    player.Detu.nPoint.stamina = player.Detu.nPoint.maxStamina;
                }
                player.Detu.nPoint.setHp(Util.CrisGH(player.Detu.nPoint.hp + hpKiHoiPhuc));
                player.Detu.nPoint.setMp(Util.CrisGH(player.Detu.nPoint.mp + hpKiHoiPhuc));
                Service.gI().sendInfoPlayerEatPea(player.Detu);
                Service.gI().chatJustForMe(player, player.Detu, "Cám ơn sư phụ");
            }

            InventoryService.gI().subQuantityItemsBag(player, pea, 1);
            InventoryService.gI().sendItemBag(player);
        }
    }

    public void eatPeaBot(Player player, int HpMp) {
        player.nPoint.setHp(Util.CrisGH(player.nPoint.hp + HpMp));
        player.nPoint.setMp(Util.CrisGH(player.nPoint.mp + HpMp));
        PlayerService.gI().sendInfoHpMp(player);
        Service.gI().sendInfoPlayerEatPea(player);
        if (player.Detu != null && player.zone.equals(player.Detu.zone) && !player.Detu.isDie()) {
            int statima = 100 * HpMp;
            player.Detu.nPoint.stamina += statima;
            if (player.Detu.nPoint.stamina > player.Detu.nPoint.maxStamina) {
                player.Detu.nPoint.stamina = player.Detu.nPoint.maxStamina;
            }
            player.Detu.nPoint.setHp(Util.CrisGH(player.Detu.nPoint.hp + HpMp));
            player.Detu.nPoint.setMp(Util.CrisGH(player.Detu.nPoint.mp + HpMp));
            Service.gI().sendInfoPlayerEatPea(player.Detu);
            Service.gI().chatJustForMe(player, player.Detu, "Cám ơn sư phụ");
        }
    }

    private void ItemSKH(Player pl, Item item) {//hop qua skh
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    private void ItemDHD(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    private void Hopts(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất", "Set namec", "Set xayda", "Từ chổi");
    }

}
