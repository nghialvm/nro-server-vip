package nro.npc.ListNpc;



import Utils.Functions;
import nro.inventory.InventoryService;
import nro.server.ServerManager;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.Util;
import consts.ConstAttribute;
import consts.ConstNpc;
import event.EventManager;
import java.io.IOException;
import jbcd.dao.EventDAO;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.attribute.Attribute;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;

public class CayThongNoel extends Npc {
    
    int TIME_12_HOUS = 43200;
    int TIME_24_HOUS = 86400;
    int TIME_36_HOUS = 129600;
    int TIME_48_HOUS = 172800;
    int TIME_60_HOUS = 216000;

    public CayThongNoel(int mapId, int status, int cx, int cy, int tempId, int avartar) {
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
        if (rewardStage > EventDAO.getLAST_EXP_REWARD_STAGE_CHRISTMAS()) {
            EventDAO.setLAST_EXP_REWARD_STAGE_CHRISTMAS(rewardStage);
            at.setValue(value);
            at.setTime(time);
        }
    }

    @Override
    public void openBaseMenu(Player player) {
        if (EventManager.CHRISTMAS) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Đang có " + EventDAO.RACE_CHRISTMAS_EVENT + " lượt trang trí\n"
                        + "Trang trí 50 lượt sẽ tặng: x2 exp toàn máy chủ " + (TIME_12_HOUS / 3600) + " giờ\n"
                        + "Trang trí 100 lượt sẽ tặng: x2 exp toàn máy chủ " + (TIME_24_HOUS / 3600) + " giờ\n"
                        + "Trang trí 170 lượt sẽ tặng: x3 exp toàn máy chủ " + (TIME_36_HOUS / 3600) + " giờ\n"
                        + "Trang trí 250 lượt sẽ tặng: x4 exp toàn máy chủ " + (TIME_48_HOUS / 3600) + " giờ\n"
                        + "Trang trí 500 lượt sẽ tặng: x5 exp toàn máy chủ " + (TIME_60_HOUS / 3600) + " giờ\n"
                        + ((at != null && at.getValue() != 0 && at.getTime() != 0) 
                                ? "|1|Toàn bộ máy chủ được tăng " + at.getValue() + "% TNSM, thời gian còn lại " + Util.formatTimeHMS(at.getTime()) + "s." : ""),
                        "Trang trí", "Đổi\n100 kẹo\nlấy quà", "Đua Top", "Vùng đất\nbăng giá\nsự kiện\nNoel", "Cửa hàng\nsự kiện", "Đóng");

            } else if (this.mapId == 174) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Tôi sẽ đưa bạn về",
                        "Đồng ý", "Từ chối");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.CHRISTMAS) {
                Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
                Item Chuong = InventoryService.gI().findItemBag(player, 1459);
                Item QuaChau = InventoryService.gI().findItemBag(player, 1460);
                Item Ngoisao = InventoryService.gI().findItemBag(player, 1461);
                Item DayKimTuyen = InventoryService.gI().findItemBag(player, 1462);
                Item MocTreoNoel = InventoryService.gI().findItemBag(player, 1463);
                boolean Ch = false;
                boolean Qc = false;
                boolean Ns = false;
                boolean Dkt = false;
                boolean Mtne = false;
                if (Chuong != null && Chuong.quantity >= 30) {
                    Ch = true;
                }
                if (QuaChau != null && QuaChau.quantity >= 30) {
                    Qc = true;
                }
                if (Ngoisao != null && Ngoisao.quantity >= 30) {
                    Ns = true;
                }
                if (DayKimTuyen != null && DayKimTuyen.quantity >= 2) {
                    Dkt = true;
                }
                if (MocTreoNoel != null && MocTreoNoel.quantity >= 1) {
                    Mtne = true;
                }
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                if (Ch && Qc && Ns && Dkt && Mtne) {
                                    this.createOtherMenu(player, 0,
                                        "|1|Để trang trí cây thông Noel cần\n"
                                        + "|2|Chuông " + Chuong.quantity + "/30\n"
                                        + "|2|Quả Châu " + QuaChau.quantity + "/30\n"
                                        + "|2|Ngôi Sao " + Ngoisao.quantity + "/30\n"
                                        + "|2|Dây Kim Tuyến " + DayKimTuyen.quantity + "/2\n"
                                        + "|2|Móc Treo Noel " + MocTreoNoel.quantity + "/1\n"
                                        + "|2|Giá vàng : 500tr vàng",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Để trang trí cây thông Noel cần\n"
                                        + (Chuong == null ? "|7|" : Chuong.quantity >= 30 ? "|2|" : "|7|") + "Chuông " + (Chuong == null ? "0" : Chuong.quantity) + "/30\n"
                                        + (QuaChau == null ? "|7|" : QuaChau.quantity >= 30 ? "|2|" : "|7|") + "Quả Châu " + (QuaChau == null ? "0" : QuaChau.quantity) + "/30\n"
                                        + (Ngoisao == null ? "|7|" : Ngoisao.quantity >= 30 ? "|2|" : "|7|") + "Ngôi Sao " + (Ngoisao == null ? "0" : Ngoisao.quantity) + "/30\n"
                                        + (DayKimTuyen == null ? "|7|" : DayKimTuyen.quantity >= 2 ? "|2|" : "|7|") + "Dây Kim Tuyến " + (DayKimTuyen == null ? "0" : DayKimTuyen.quantity) + "/2\n"
                                        + (MocTreoNoel == null ? "|7|" : MocTreoNoel.quantity >= 1 ? "|2|" : "|7|") + "Móc Treo Noel " + (MocTreoNoel == null ? "0" : MocTreoNoel.quantity) + "/1\n"
                                        + "|2|Giá vàng : 500tr vàng",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 1: {
                                Item KeoGiangSinh = InventoryService.gI().findItemBag(player, 533);
                                if (KeoGiangSinh != null && KeoGiangSinh.quantity < 100) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ Kẹo giáng sinh");
                                    return;
                                }
                                if (KeoGiangSinh == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có Kẹo giáng sinh");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                InventoryService.gI().subQuantityItemsBag(player, KeoGiangSinh, 100);
                                int gender;
                                switch (player.gender) {
                                    case 0:
                                        gender = 1155;
                                        break;
                                    case 1:
                                        gender = 1157;
                                        break;
                                    default:
                                        gender = 1156;
                                        break;
                                }
                                Item Caitrang = ItemService.gI().createNewItem((short) gender);
                                Caitrang.addOptionParam(50, Util.nextInt(17, 27));
                                Caitrang.addOptionParam(103, Util.nextInt(17, 27));
                                Caitrang.addOptionParam(77, Util.nextInt(17, 27));
                                Caitrang.addOptionParam(80, Util.nextInt(27, 40));
                                Caitrang.addOptionParam(106, 0);
                                InventoryService.gI().addItemBag(player, Caitrang);
                                Service.gI().sendThongBao(player, "Bạn nhận được Cải trang " + Caitrang.Name());
                                InventoryService.gI().sendItemBag(player);
                                break;
                            }
                            case 2: {
                                this.createOtherMenu(player, 1, 
                                    "Bạn cần tôi giúp gì?",
                                    "Top 100\ntrang trí\ncây Noel", "Top 100\nđốt diêm", "Đóng");
                                break;
                            }
                            case 3: {
                                ChangeMapService.gI().changeMap(player, 174, -1, Util.nextInt(200, 250), 408);
                                break;
                            }
                            case 4: {
                                ShopService.gI().opendShop(player, "CHRIST_MAS_EVENT", false);
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
                                if (player.inventory.getGold() < 500_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                short[] List_Item = {381, 382, 383, 384, 441, 442, 443, 444, 445, 446, 447, 1143, 1830, 1832, 746, 936, 1452};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                int Item_Template = itemReceived.template.id;
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(5);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Chuong));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, QuaChau));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Ngoisao));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DayKimTuyen));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, MocTreoNoel));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(12322);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, Chuong, 30);
                                    InventoryService.gI().subQuantityItemsBag(player, QuaChau, 30);
                                    InventoryService.gI().subQuantityItemsBag(player, Ngoisao, 30);
                                    InventoryService.gI().subQuantityItemsBag(player, DayKimTuyen, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, MocTreoNoel, 1);
                                    player.inventory.gold -= 500_000_000;
                                    player.DuaTopTrangTriCayNoel++;
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                //
                                if (Item_Template == 1452) {
                                    itemReceived.addOptionParam(77, Util.nextInt(15, 16));
                                    itemReceived.addOptionParam(103, Util.nextInt(15, 16));
                                    itemReceived.addOptionParam(50, Util.nextInt(15, 16));
                                    itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(80, Util.nextInt(10, 15));
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Util.nextInt(3, 7));
                                    }
                                }
                                if (Item_Template == 936) {
                                    itemReceived.addOptionParam(50, Util.nextInt(13, 15));
                                    itemReceived.addOptionParam(77, Util.nextInt(13, 15));
                                    itemReceived.addOptionParam(103, Util.nextInt(13, 15));
                                    itemReceived.addOptionParam(101, 20);
                                    itemReceived.addOptionParam(95, 20);
                                    itemReceived.addOptionParam(96, 20);
                                    itemReceived.addOptionParam(106, 0);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Util.nextInt(3, 7));
                                    }
                                }
                                if (Item_Template == 746) {
                                    itemReceived.addOptionParam(84, 0);
                                    itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(101, 20);
                                    itemReceived.addOptionParam(95, 20);
                                    itemReceived.addOptionParam(96, 20);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Util.nextInt(3, 7));
                                    }
                                }
                                if (Item_Template == 1832) {
                                    itemReceived.addOptionParam(50, Util.nextInt(13, 17));
                                    itemReceived.addOptionParam(77, Util.nextInt(13, 17));
                                    itemReceived.addOptionParam(103, Util.nextInt(13, 17));
                                    itemReceived.addOptionParam(14, Util.nextInt(10, 17));
                                    itemReceived.addOptionParam(106, 0);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Util.nextInt(3, 7));
                                    }
                                }
                                if (Item_Template == 1830) {
                                    itemReceived.addOptionParam(84, 0);
                                    itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(77, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(103, Util.nextInt(10, 15));
                                    itemReceived.addOptionParam(106, 0);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Util.nextInt(3, 7));
                                    }
                                }
                                if (Item_Template >= 381 && Item_Template <= 384) {
                                    itemReceived.addOptionParam(86, 0);
                                }
                                if (Item_Template == 1143) {
                                    itemReceived.addOptionParam(30, 0);
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
                                applyGlobalExpBuff(at, EventDAO.RACE_CHRISTMAS_EVENT);
                                InventoryService.gI().addItemBag(player, itemReceived);                                
                                EventDAO.RACE_CHRISTMAS_EVENT++;
                                new Thread(() -> {
                                    Functions.sleep(2000);
                                    player.event.addChristMasPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                EventDAO.save();
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                TopService.showListTopTrangTriCayNoel(player);
                                break;
                            }
                            case 1: {
                                TopService.showListTopDotDiem(player);
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                } else if (this.mapId == 174) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                ChangeMapService.gI().changeMap(player, 0, -1, Util.nextInt(900, 1000), 432);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
