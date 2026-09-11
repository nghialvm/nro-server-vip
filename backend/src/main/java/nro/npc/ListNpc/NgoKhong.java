package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.Player;

public class NgoKhong extends Npc {

    public NgoKhong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 122) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Chu mi nga\n", 
                        "Tặng quả\nHồng Đào", "Tặng quả\nHồng Đào\nChín");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        Item QuaHongDao = InventoryService.gI().findItemBag(player, 541);
        Item TangHongDao = ItemService.gI().createNewItem(Util.isTrue(70, 100) ? (short) 537 : 
                Util.isTrue(80, 100) ? (short) 540 : 
                        Util.isTrue(50, 100) ? (short) 539 : (short) 538, 1);
        //
        Item QuaHongDaoChin = InventoryService.gI().findItemBag(player, 542);
        Item TangHongDaoChin = ItemService.gI().createNewItem((short) Util.nextInt(537, 540), 1);
        if (canOpenNpc(player)) {
            if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                if (this.mapId == 122) {
                    switch (select) {
                        case 0: {
                            if (QuaHongDao == null || QuaHongDao.quantity < 1) {
                                Service.gI().sendThongBao(player, "Bạn không có quả Hồng Đào.");
                                return;
                            }
                            InventoryService.gI().subQuantityItemsBag(player, QuaHongDao, 1);
                            this.npcChat(player, "Xie Xie");
                            Service.gI().dropHongDaoAndPickItem(player, TangHongDao.template.id, 1);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Bạn nhận được " + TangHongDao.Name());
                            break;
                        }
                        case 1: {
                            if (QuaHongDaoChin == null || QuaHongDaoChin.quantity < 1) {
                                Service.gI().sendThongBao(player, "Bạn không có quả Hồng Đào Chín.");
                                return;
                            }
                            InventoryService.gI().subQuantityItemsBag(player, QuaHongDaoChin, 1);
                            this.npcChat(player, "Xie Xie");
                            Service.gI().dropHongDaoAndPickItem(player, TangHongDaoChin.template.id, 1);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Bạn nhận được " + TangHongDaoChin.Name());
                            break;
                        }
                    }
                }
            }
        }
    }
}