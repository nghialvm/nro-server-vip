package nro.npc.ListNpc;


import Utils.Functions;
import nro.inventory.InventoryService;
import nro.server.ServerManager;
import nro.services.Service;
import Utils.Util;
import consts.ConstAttribute;
import consts.ConstNpc;
import event.EventManager;
import jbcd.dao.EventDAO;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.attribute.Attribute;
import nro.npc.Npc;
import nro.player.Player;


public class CayNeu extends Npc {
    
    int TIME_12_HOUS = 43200;
    int TIME_24_HOUS = 86400;
    int TIME_36_HOUS = 129600;
    int TIME_48_HOUS = 172800;
    int TIME_60_HOUS = 216000;

    public CayNeu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    
    private void applyGlobalExpBuff(Attribute at, int point) {
        int rewardStage = 0;
        int value = 0;
        int time = 0;

        if (point >= 500) {
            rewardStage = 5;
            value = 400;
            time = TIME_60_HOUS;
        } else if (point >= 250) {
            rewardStage = 4;
            value = 300;
            time = TIME_48_HOUS;
        } else if (point >= 170) {
            rewardStage = 3;
            value = 200;
            time = TIME_36_HOUS;
        } else if (point >= 100) {
            rewardStage = 2;
            value = 100;
            time = TIME_24_HOUS;
        } else if (point >= 50) {
            rewardStage = 1;
            value = 100;
            time = TIME_12_HOUS;
        }

        // Chỉ kích hoạt nếu vượt mốc mới
        if (rewardStage > EventDAO.getLAST_EXP_REWARD_STAGE_LUNA_NEW_YEAR()) {
            EventDAO.setLAST_EXP_REWARD_STAGE_LUNA_NEW_YEAR(rewardStage);
            at.setValue(value);
            at.setTime(time);
        }
    }

    @Override
    public void openBaseMenu(Player player) {
        if (EventManager.LUNNAR_NEW_YEAR) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Toàn máy chủ đang có " + EventDAO.RACE_EVENT_LUNA_NEW_YEAR + " lượt trang trí\n"
                        + "Trang trí 50 lượt sẽ tặng: x2 exp toàn máy chủ " + (TIME_12_HOUS / 3600) + " giờ\n"
                        + "Trang trí 100 lượt sẽ tặng: x2 exp toàn máy chủ " + (TIME_24_HOUS / 3600) + " giờ\n"
                        + "Trang trí 170 lượt sẽ tặng: x3 exp toàn máy chủ " + (TIME_36_HOUS / 3600) + " giờ\n"
                        + "Trang trí 250 lượt sẽ tặng: x4 exp toàn máy chủ " + (TIME_48_HOUS / 3600) + " giờ\n"
                        + "Trang trí 500 lượt sẽ tặng: x5 exp toàn máy chủ " + (TIME_60_HOUS / 3600) + " giờ\n"
                        + ((at != null && at.getValue() != 0 && at.getTime() != 0) 
                                ? "|1|Toàn bộ máy chủ được tăng " + at.getValue() + "% TNSM, thời gian còn lại " + Util.formatTimeHMS(at.getTime()) + "s." : ""),
                        "Trang trí", "Trang trí\nVIP", "Đóng");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
                Item DayPhao = InventoryService.gI().findItemBag(player, 1472);
                Item CauDoi = InventoryService.gI().findItemBag(player, 1473);
                Item DayTreoBanh = InventoryService.gI().findItemBag(player, 1474);
                Item LongDenTreoCay = InventoryService.gI().findItemBag(player, 1475);
                boolean DP = false;
                boolean CD = false;
                boolean DTB = false;
                boolean LDTC = false;
                if (DayPhao != null && DayPhao.quantity >= 2) {
                    DP = true;
                }
                if (CauDoi != null && CauDoi.quantity >= 4) {
                    CD = true;
                }
                if (DayTreoBanh != null && DayTreoBanh.quantity >= 20) {
                    DTB = true;
                }
                if (LongDenTreoCay != null && LongDenTreoCay.quantity >= 1) {
                    LDTC = true;
                }
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                if (DP && CD && DTB && LDTC) {
                                    this.createOtherMenu(player, 0,
                                        "|1|Để trang trí cần\n"
                                        + "|2|Dây pháo " + DayPhao.quantity + "/2\n"
                                        + "|2|Câu đối " + CauDoi.quantity + "/4\n"
                                        + "|2|Dây treo bánh " + DayTreoBanh.quantity + "/20\n"
                                        + "|2|Lồng đèn treo cây " + LongDenTreoCay.quantity + "/1\n"
                                        + "|2|Giá vàng : 1 tỷ vàng",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Để trang trí cần\n"
                                        + (DayPhao == null ? "|7|" : DayPhao.quantity >= 2 ? "|2|" : "|7|") + "Dây pháo " + (DayPhao == null ? "0" : DayPhao.quantity) + "/2\n"
                                        + (CauDoi == null ? "|7|" : CauDoi.quantity >= 2 ? "|2|" : "|7|") + "Câu đối " + (CauDoi == null ? "0" : CauDoi.quantity) + "/4\n"
                                        + (DayTreoBanh == null ? "|7|" : DayTreoBanh.quantity >= 20 ? "|2|" : "|7|") + "Dây treo bánh " + (DayTreoBanh == null ? "0" : DayTreoBanh.quantity) + "/20\n"
                                        + (LongDenTreoCay == null ? "|7|" : LongDenTreoCay.quantity >= 1 ? "|2|" : "|7|") + "Lồng đèn treo cây " + (LongDenTreoCay == null ? "0" : LongDenTreoCay.quantity) + "/1\n"
                                        + "|2|Giá vàng : 1 tỷ vàng",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 1: {
                                if (DP && CD && DTB && LDTC) {
                                    this.createOtherMenu(player, 1,
                                        "|1|Để trang trí cần\n"
                                        + "|2|Dây pháo " + DayPhao.quantity + "/2\n"
                                        + "|2|Câu đối " + CauDoi.quantity + "/4\n"
                                        + "|2|Dây treo bánh " + DayTreoBanh.quantity + "/20\n"
                                        + "|2|Lồng đèn treo cây " + LongDenTreoCay.quantity + "/1\n"
                                        + "|2|Giá vàng : 1 tỷ vàng\n"
                                        + "|2|Giá hồng ngọc : 10 hồng ngọc",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Để trang trí cần\n"
                                        + (DayPhao == null ? "|7|" : DayPhao.quantity >= 2 ? "|2|" : "|7|") + "Dây pháo " + (DayPhao == null ? "0" : DayPhao.quantity) + "/2\n"
                                        + (CauDoi == null ? "|7|" : CauDoi.quantity >= 2 ? "|2|" : "|7|") + "Câu đối " + (CauDoi == null ? "0" : CauDoi.quantity) + "/4\n"
                                        + (DayTreoBanh == null ? "|7|" : DayTreoBanh.quantity >= 20 ? "|2|" : "|7|") + "Dây treo bánh " + (DayTreoBanh == null ? "0" : DayTreoBanh.quantity) + "/20\n"
                                        + (LongDenTreoCay == null ? "|7|" : LongDenTreoCay.quantity >= 1 ? "|2|" : "|7|") + "Lồng đèn treo cây " + (LongDenTreoCay == null ? "0" : LongDenTreoCay.quantity) + "/1\n"
                                        + "|2|Giá vàng : 1 tỷ vàng\n"
                                        + "|2|Giá hồng ngọc : 10 hồng ngọc",
                                        "Từ chối");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getGold() < 1_000_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                short[] Param = {3, 5, 7, 15};
                                short[] List_Item = {381, 382, 383, 384, 441, 442, 443, 444, 445, 446, 447, 220, 221, 222, 223, 224, 1174, 1175, 1176, 1188, 1197};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                int Item_Template = itemReceived.template.id;
                                try {
                                    Message msg = new Message(-81);
                                    msg.writer().writeByte(0);
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeShort(tempId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(1);
                                    msg.writer().writeByte(4);
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, DayPhao));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, CauDoi));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, DayTreoBanh));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, LongDenTreoCay));
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(7);
                                    msg.writer().writeShort(12329);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                } catch (Exception e) {
                                    System.out.println("ERROR 404");
                                }
                                InventoryService.gI().subQuantityItemsBag(player, DayPhao, 2);
                                InventoryService.gI().subQuantityItemsBag(player, CauDoi, 4);
                                InventoryService.gI().subQuantityItemsBag(player, DayTreoBanh, 20);
                                InventoryService.gI().subQuantityItemsBag(player, LongDenTreoCay, 1);
                                //
                                if (Item_Template >= 381 && Item_Template <= 384) {
                                    itemReceived.addOptionParam(86, 0);
                                }
                                if (Item_Template >= 1174 && Item_Template <= 1176) {
                                    itemReceived.addOptionParam(50, Util.nextInt(20, 24));
                                    itemReceived.addOptionParam(77, Util.nextInt(20, 24));
                                    itemReceived.addOptionParam(103, Util.nextInt(20, 24));
                                    itemReceived.addOptionParam(94, Util.nextInt(15, 19));
                                    itemReceived.addOptionParam(97, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(114, 20);
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 1188) {
                                    itemReceived.addOptionParam(50, 18);
                                    itemReceived.addOptionParam(77, 18);
                                    itemReceived.addOptionParam(103, 18);
                                    itemReceived.addOptionParam(80, 20);
                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                }
                                if (Item_Template == 1197) {
                                    itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(77, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(103, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                }
                                if (Item_Template == 441) {
                                    itemReceived.addOptionParam(95, 5);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 442) {
                                    itemReceived.addOptionParam(96, 5);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 443) {
                                    itemReceived.addOptionParam(97, 5);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 444) {
                                    itemReceived.addOptionParam(98, 3);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 445) {
                                    itemReceived.addOptionParam(99, 3);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 446) {
                                    itemReceived.addOptionParam(100, 5);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 447) {
                                    itemReceived.addOptionParam(101, 5);
                                    itemReceived.quantity = Util.nextInt(1, 5);
                                }
                                if (Item_Template == 220) {
                                    itemReceived.addOptionParam(71, 0);
                                    itemReceived.quantity = Util.nextInt(1, 10);
                                }
                                if (Item_Template == 221) {
                                    itemReceived.addOptionParam(70, 0);
                                    itemReceived.quantity = Util.nextInt(1, 10);
                                }
                                if (Item_Template == 222) {
                                    itemReceived.addOptionParam(69, 0);
                                    itemReceived.quantity = Util.nextInt(1, 10);
                                }
                                if (Item_Template == 223) {
                                    itemReceived.addOptionParam(68, 0);
                                    itemReceived.quantity = Util.nextInt(1, 10);
                                }
                                if (Item_Template == 224) {
                                    itemReceived.addOptionParam(67, 0);
                                    itemReceived.quantity = Util.nextInt(1, 10);
                                }
                                applyGlobalExpBuff(at, EventDAO.RACE_EVENT_LUNA_NEW_YEAR);
                                player.inventory.gold -= 1_000_000_000;
                                InventoryService.gI().addItemBag(player, itemReceived);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                EventDAO.RACE_EVENT_LUNA_NEW_YEAR++;
                                new Thread(() -> {
                                    Functions.sleep(3000);
                                    player.event.addLunaNewYearPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                EventDAO.save();
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getGold() < 1_000_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                if (player.inventory.getRuby() < 10) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                                    return;
                                }
                                short[] Param = {3, 5, 7, 15, 30};
                                short[] List_Item = {1150, 1151, 1152, 1153, 1174, 1175, 1176, 1202, 1201, 1143, 1469, 1470, 1471, 1253, 1478, 1476, 1440};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                int Item_Template = itemReceived.template.id;
                                try {
                                    Message msg = new Message(-81);
                                    msg.writer().writeByte(0);
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeShort(tempId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(1);
                                    msg.writer().writeByte(4);
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, DayPhao));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, CauDoi));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, DayTreoBanh));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, LongDenTreoCay));
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(7);
                                    msg.writer().writeShort(12329);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                } catch (Exception e) {
                                    System.out.println("ERROR 404");
                                }
                                InventoryService.gI().subQuantityItemsBag(player, DayPhao, 2);
                                InventoryService.gI().subQuantityItemsBag(player, CauDoi, 4);
                                InventoryService.gI().subQuantityItemsBag(player, DayTreoBanh, 20);
                                InventoryService.gI().subQuantityItemsBag(player, LongDenTreoCay, 1);
                                //
                                if (Item_Template == 1440) {
                                    itemReceived.addOptionParam(30, 0);
                                    itemReceived.addOptionParam(87, 0);
                                }
                                if (Item_Template == 1476) {
                                    itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                                    itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                                    itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                                    itemReceived.addOptionParam(94, Util.nextInt(10, 20));
                                    itemReceived.addOptionParam(14, Util.nextInt(5, 10));
                                    itemReceived.addOptionParam(114, 50);
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 1478) {
                                    itemReceived.addOptionParam(50, Util.nextInt(12, 20));
                                    itemReceived.addOptionParam(77, Util.nextInt(12, 20));
                                    itemReceived.addOptionParam(103, Util.nextInt(12, 20));
                                    itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 1253) {
                                    itemReceived.addOptionParam(50, Util.nextInt(5, 15));
                                    itemReceived.addOptionParam(77, Util.nextInt(5, 15));
                                    itemReceived.addOptionParam(103, Util.nextInt(5, 15));
                                    itemReceived.addOptionParam(84, 0);
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template >= 1469 && Item_Template <= 1471) {
                                    itemReceived.addOptionParam(77, Util.nextInt(24, 27));
                                    itemReceived.addOptionParam(103, Util.nextInt(24, 27));
                                    itemReceived.addOptionParam(50, Util.nextInt(24, 26));
                                    itemReceived.addOptionParam(101, Util.nextInt(20, 40));
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 1143) {
                                    itemReceived.addOptionParam(30, 0);
                                }
                                if (Item_Template >= 1150 && Item_Template <= 1153) {
                                    itemReceived.addOptionParam(86, 0);
                                }
                                if (Item_Template >= 1174 && Item_Template <= 1176) {
                                    itemReceived.addOptionParam(50, Util.nextInt(20, 24));
                                    itemReceived.addOptionParam(77, Util.nextInt(20, 24));
                                    itemReceived.addOptionParam(103, Util.nextInt(20, 24));
                                    itemReceived.addOptionParam(94, Util.nextInt(15, 19));
                                    itemReceived.addOptionParam(97, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(114, 20);
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 1202) {
                                    itemReceived.addOptionParam(50, 18);
                                    itemReceived.addOptionParam(77, 18);
                                    itemReceived.addOptionParam(103, 18);
                                    itemReceived.addOptionParam(80, 20);
                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                }
                                if (Item_Template == 1201) {
                                    itemReceived.addOptionParam(50, 24);
                                    itemReceived.addOptionParam(77, 22);
                                    itemReceived.addOptionParam(103, 22);
                                    itemReceived.addOptionParam(94, 20);
                                    itemReceived.addOptionParam(80, 20);
                                    itemReceived.addOptionParam(114, 20);
                                    if (Util.isTrue(90, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                applyGlobalExpBuff(at, EventDAO.RACE_EVENT_LUNA_NEW_YEAR);
                                player.inventory.gold -= 1_000_000_000;
                                player.inventory.ruby -= 10;
                                InventoryService.gI().addItemBag(player, itemReceived);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                EventDAO.RACE_EVENT_LUNA_NEW_YEAR++;
                                new Thread(() -> {
                                    Functions.sleep(3000);
                                    player.event.addLunaNewYearPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                EventDAO.save();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
