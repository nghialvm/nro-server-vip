package nro.services;

import nro.inventory.InventoryService;
import models.Item.Item;
import models.Item.ItemService;
import nro.player.Player;
import jbcd.ConnectDB;

/**
 *
 * @author Administrator
 */
public class NapVangService {

    public static void ChonGiaTien(int chon, Player p) throws Exception {
        switch (chon) {
            case 20: { // 20k
                if (p.getSession().vnd < 20000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 20,000 vnđ");
                    return;
                }

                if (InventoryService.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }

                Item thoivang = ItemService.gI().createNewItem((short) 457, 60);

                if (thoivang != null) {
                    p.getSession().vnd -= 20000;
                    InventoryService.gI().addItemBag(p, thoivang);
                    InventoryService.gI().sendItemBag(p);
                    ConnectDB.executeUpdate("UPDATE account SET vnd = '" + p.getSession().vnd + "' WHERE id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 60 thỏi vàng");
                }
                break;
            }

            case 50: { // 50k
                if (p.getSession().vnd < 50000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 50,000 vnđ");
                    return;
                }

                if (InventoryService.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }

                Item thoivang = ItemService.gI().createNewItem((short) 457, 150);

                if (thoivang != null) {
                    p.getSession().vnd -= 50000;
                    InventoryService.gI().addItemBag(p, thoivang);
                    InventoryService.gI().sendItemBag(p);
                    ConnectDB.executeUpdate("UPDATE account SET vnd = '" + p.getSession().vnd + "' WHERE id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 150 thỏi vàng");
                }
                break;
            }

            case 100: { // 100k
                if (p.getSession().vnd < 100000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 100,000 vnđ");
                    return;
                }

                if (InventoryService.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }

                Item thoivang = ItemService.gI().createNewItem((short) 457, 300);

                if (thoivang != null) {
                    p.getSession().vnd -= 100000;
                    InventoryService.gI().addItemBag(p, thoivang);
                    InventoryService.gI().sendItemBag(p);
                    ConnectDB.executeUpdate("UPDATE account SET vnd = '" + p.getSession().vnd + "' WHERE id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 300 thỏi vàng");
                }
                break;
            }

            case 500: { // 500k
                if (p.getSession().vnd < 500000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 500,000 vnđ");
                    return;
                }

                if (InventoryService.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }

                Item thoivang = ItemService.gI().createNewItem((short) 457, 1500);

                if (thoivang != null) {
                    p.getSession().vnd -= 500000;
                    InventoryService.gI().addItemBag(p, thoivang);
                    InventoryService.gI().sendItemBag(p);
                    ConnectDB.executeUpdate("UPDATE account SET vnd = '" + p.getSession().vnd + "' WHERE id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 1500 thỏi vàng");
                }
                break;
            }
        }
    }
}