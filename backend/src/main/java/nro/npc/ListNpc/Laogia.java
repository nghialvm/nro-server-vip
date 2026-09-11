package nro.npc.ListNpc;

import Utils.Util;
import consts.ConstNpc;
import jbcd.data.DatabaseUpdater;
import models.Item.Item;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;
import nro.server.Manager;
import nro.services.Service;

public class Laogia extends Npc {

    public Laogia(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (mapId) {
                case 1:
                case 8:
                case 15: {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|2|Xin chào!\n"
                            + "Tại đây bạn có thể nạp và quy đổi tiền tệ.\n"
                            + "|0|Số dư hiện tại: " + Util.format(player.getSession().vnd) + " VNĐ",
                            "Nạp Tiền", "Đóng");
                    break;
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
                this.createOtherMenu(player, 0,
                        "|2|Bạn muốn nạp vào Vàng hay Hồng Ngọc?\n"
                        + "|0|Số dư hiện tại: " + Util.format(player.getSession().vnd) + " VNĐ",
                        "Nạp Vàng", "Nạp Hồng Ngọc", "Đóng");
            }
        } else if (player.iDMark.getIndexMenu() == 0) {
            // Chọn nạp Vàng hoặc Ngọc
            switch (select) {
                case 0: { // Menu Nạp Vàng
                    this.createOtherMenu(player, 1,
                            "|2|Bảng Giá Nạp Thỏi Vàng (x" + Manager.TY_LE_NAP_THOI_VANG + ")\n\n"
                            + "5K → " + Util.format(30 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "10K → " + Util.format(70 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "20K → " + Util.format(150 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "50K → " + Util.format(400 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "100K → " + Util.format(850 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "200K → " + Util.format(1800 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "500K → " + Util.format(4700 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "1.000K → " + Util.format(10000 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "2.000K → " + Util.format(22000 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "5.000K → " + Util.format(75000 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n"
                            + "10.000K → " + Util.format(200000 * Manager.TY_LE_NAP_THOI_VANG) + " Thỏi Vàng\n\n"
                            + "|0|Số dư: " + Util.format(player.getSession().vnd) + " VNĐ",
                            "Nạp [5K]", "Nạp [10K]", "Nạp [20K]", "Nạp [50K]",
                            "Nạp [100K]", "Nạp [200K]", "Nạp [500K]", "Nạp [1.000K]",
                            "Nạp [2.000K]", "Nạp [5.000K]", "Nạp [10.000K]", "Đóng");
                    break;
                }
                case 1: { // Menu Nạp Ngọc
                    this.createOtherMenu(player, 2,
                            "|2|Bảng Giá Nạp Hồng Ngọc (x" + Manager.TY_LE_NAP_HONG_NGOC + ")\n\n"
                            + "5K → " + Util.format(160 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "10K → " + Util.format(330 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "20K → " + Util.format(680 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "50K → " + Util.format(1800 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "100K → " + Util.format(3800 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "200K → " + Util.format(7500 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "500K → " + Util.format(20000 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "1.000K → " + Util.format(45000 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "2.000K → " + Util.format(100000 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "5.000K → " + Util.format(300000 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n"
                            + "10.000K → " + Util.format(1000000 * Manager.TY_LE_NAP_HONG_NGOC) + " Hồng Ngọc\n\n"
                            + "|0|Số dư: " + Util.format(player.getSession().vnd) + " VNĐ",
                            "Nạp [5K]", "Nạp [10K]", "Nạp [20K]", "Nạp [50K]",
                            "Nạp [100K]", "Nạp [200K]", "Nạp [500K]", "Nạp [1.000K]",
                            "Nạp [2.000K]", "Nạp [5.000K]", "Nạp [10.000K]", "Đóng");
                    break;
                }
            }
        } else if (player.iDMark.getIndexMenu() == 1) {
            // Menu nạp Vàng
            int[] vndMoc = {5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000};
            int[] vangMoc = {30, 70, 150, 400, 850, 1800, 4700, 10000, 22000, 75000, 200000};
            if (select >= 0 && select < vndMoc.length) {
                processNapTien(player, vndMoc[select], vangMoc[select] * Manager.TY_LE_NAP_THOI_VANG, true);
            }
        } else if (player.iDMark.getIndexMenu() == 2) {
            // Menu nạp Ngọc
            int[] vndMoc = {5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000};
            int[] ngocMoc = {160, 330, 680, 1800, 3800, 7500, 20000, 45000, 100000, 300000, 1000000};
            if (select >= 0 && select < vndMoc.length) {
                processNapTien(player, vndMoc[select], ngocMoc[select] * Manager.TY_LE_NAP_HONG_NGOC, false);
            }
        }
    }

    /**
     * Hàm xử lý nạp tiền chung
     * @param player Người chơi
     * @param vnd Số tiền VNĐ cần trừ
     * @param amount Số lượng thỏi vàng hoặc ngọc cộng
     * @param vang true = vàng, false = ngọc
     */
    private void processNapTien(Player player, int vnd, int amount, boolean vang) {
        if (player.getSession() == null) return;

        if (player.getSession().vnd < vnd) {
            Service.gI().sendThongBao(player, "Số dư không đủ để nạp gói này!");
            return;
        }
        if (vang && InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, cần ít nhất 1 ô trống!");
            return;
        }

        DatabaseUpdater.subVND_byPlayer(player, vnd);
        if (vang) {
            Item thoiVang = ItemService.gI().createNewItem((short) 457, amount);
            InventoryService.gI().addItemBag(player, thoiVang);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được x" + Util.format(amount) + " Thỏi Vàng");
        } else {
            player.inventory.addRuby(amount);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + Util.format(amount) + " Hồng Ngọc");
        }
    }
}
