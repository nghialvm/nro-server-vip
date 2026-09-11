package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.MapService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import java.util.List;
import models.Item.Item;
import models.Item.ItemService;
import nro.map.Map;
import nro.npc.Npc;
import nro.player.Player;

public class HoaHongInternationalWomensDayEvent extends Npc {

    private final byte COUNT_CHANGE = 1;
    private int count;

    public HoaHongInternationalWomensDayEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private void haiHoaHong(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Bạn không đủ hành trang để hái bông hoa hồng.");
            return;
        }
        Item hoaHong = ItemService.gI().createNewItem((short) 1530);
        hoaHong.addOptionParam(30, 0);
        hoaHong.addOptionParam(87, 0);
        int quality = Util.nextInt(1, 3);
        hoaHong.quantity = quality;
        InventoryService.gI().addItemBag(player, hoaHong);
        Service.gI().sendThongBao(player, "Bạn đã hái bông hoa hồng, nhận được x" + quality + " Bông hoa hồng");
        InventoryService.gI().sendItemBag(player);
        count++;
        if (this.count >= COUNT_CHANGE) {
            List<Player> playersMap;
            playersMap = player.zone.getPlayers();
            count = 0;
            this.map.npcs.remove(this);
            Map mapHoahong = MapService.gI().getMapForHoaHong();
            this.mapId = mapHoahong.mapId;
            this.cx = Util.nextInt(100, mapHoahong.mapWidth - 100);
            this.cy = mapHoahong.yPhysicInTop(this.cx, 0);
            this.map = mapHoahong;
            this.map.npcs.add(this);
            for (Player pl : playersMap) {
                pl.zone.mapInfo(pl);
            }
            System.out.println("hoa hồng mọc tại map " + mapHoahong.mapName);
        }

    }

    @Override
    public void openBaseMenu(Player player) {
        player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
        if (this.mapId != player.zone.map.mapId) {
            Service.gI().sendThongBao(player, "Hoa hồng không thể hái được nữa, vui lòng đi qua chỗ khác để hái.");
            Service.gI().hideWaitDialog(player);
            return;
        }
        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                "Hoa hồng", 
                "Hái", "Bỏ qua");

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0: {
                    haiHoaHong(player);
                    break;
                }
            }
        }
    }
}
