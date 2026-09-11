package event.BakeACake;

import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Logger;
import consts.ConstDataEvent;
import java.util.HashMap;
import models.Item.Item;
import models.Item.ItemService;
import nro.player.Player;


public class NauBanh_NewYear extends Thread {

    public static HashMap<Long, BanhChungBanhTet> banhChungBanhTetMaps = new HashMap<>();
    public static long timeCBNau = 0;
    public static int item1 = 1606;
    public static int item2 = 1607;
    public static int item3 = 1608;
    public static int item4 = 1609;
    public static int item5 = 1610;
    public static int banhtet = 1510;
    public static int banhchung = 1511;

    public static int binhnuoc = 1918;
    public static int cuilua = 1917;

    public static void subTimeNauBanh(long time) {
        if (ConstDataEvent.thoiGianNauBanh - time <= 0) {
            ConstDataEvent.thoiGianNauBanh = 0;
            return;
        } else {
            ConstDataEvent.thoiGianNauBanh -= time;
        }
    }

    public static int getTotal() {
        int sum = 0;
        for (java.util.Map.Entry<Long, BanhChungBanhTet> entry : banhChungBanhTetMaps.entrySet()) {
            BanhChungBanhTet value = entry.getValue();
            int banhChung = value.slBanhChung;
            int banhTet = value.slBanhTet;
            sum += (banhTet + banhChung);
        }
        return sum;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (ConstDataEvent.thoiGianNauBanh == -999999) {
                    // System.out.println("Chờ nguyên liệu");
                    Logger.error("Chờ Nguyên Liệu\n");
                    timeCBNau = System.currentTimeMillis() + 900000;
                    // timeCBNau = System.currentTimeMillis() + 9000;

                    // Nghỉ tạm 15 phút
                    Logger.error("Nghỉ 15p\n");
                    Sleep(900000);
                    // Sleep(9000);

                    ConstDataEvent.thoiGianNauBanh = 2700000;
                    // ConstDataEvent.thoiGianNauBanh = 27000;
                }

                // Logger.error("update");
                if (ConstDataEvent.thoiGianNauBanh == 0) {
                    // Logger.error("Chờ nhận bánh");
                    if (ConstDataEvent.mucNuocTrongNoi == 0
                            || ConstDataEvent.slBanhTrongNoi == 0
                            || ConstDataEvent.mucNuocTrongNoi < ConstDataEvent.slBanhTrongNoi) {
                        Logger.error("Hủy nhận bánh do thiếu nước\n");
                        ConstDataEvent.slBanhTrongNoi = 0;
                        ConstDataEvent.mucNuocTrongNoi = 0;
                        ConstDataEvent.thoiGianNauBanh = -999999;
                        banhChungBanhTetMaps.clear();
                    } else {
                        // Nghỉ tạm 5 phút cho nó nhận bánh
                        Logger.error("Nghỉ 5 phút\n");
                        Sleep(300000);
                        System.out.println("Đã nghỉ xong\n");
                        ConstDataEvent.slBanhTrongNoi = 0;
                        ConstDataEvent.mucNuocTrongNoi = 0;
                        ConstDataEvent.thoiGianNauBanh = -999999;
                        banhChungBanhTetMaps.clear();
                    }
                }

                Sleep(1000);

                if (ConstDataEvent.thoiGianNauBanh > 0) {
                    // Logger.error("Chờ nấu bánh");
                    if (ConstDataEvent.thoiGianNauBanh - 1000 <= 0) {
                        ConstDataEvent.thoiGianNauBanh = 0;
                    } else {
                        ConstDataEvent.thoiGianNauBanh -= 1000;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Sleep(long j) {
        try {
            Thread.sleep(j);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void nauBanhChung(Player player, String s) {
        try {
            int slBanhChung = Math.abs(Integer.parseInt(s));
            Item laGiong = InventoryService.gI().findItemBag(player, item1);
            Item gaoNep = InventoryService.gI().findItemBag(player, item2);
            Item dauXanh = InventoryService.gI().findItemBag(player, item3);
            Item giongTre = InventoryService.gI().findItemBag(player, item4);
            Item thitLon = InventoryService.gI().findItemBag(player, item5);
            Item nuocNau = InventoryService.gI().findItemBag(player, binhnuoc);

            if (laGiong == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item1).name);
                return;
            }

            if (gaoNep == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item2).name);
                return;
            }

            if (dauXanh == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item3).name);
                return;
            }

            if (giongTre == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item4).name);
                return;
            }

            if (thitLon == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item5).name);
                return;
            }

            if (laGiong.quantity < (10 * slBanhChung)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item1).name);
                return;
            }

            if (gaoNep.quantity < (10 * slBanhChung)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item2).name);
                return;
            }

            if (dauXanh.quantity < (10 * slBanhChung)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item3).name);
                return;
            }

            if (giongTre.quantity < (10 * slBanhChung)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item4).name);
                return;
            }

            if (thitLon.quantity < (10 * slBanhChung)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item5).name);
                return;
            }

            InventoryService.gI().subQuantityItemsBag(player, laGiong, (10 * slBanhChung));
            InventoryService.gI().subQuantityItemsBag(player, gaoNep, (10 * slBanhChung));
            InventoryService.gI().subQuantityItemsBag(player, dauXanh, (10 * slBanhChung));
            InventoryService.gI().subQuantityItemsBag(player, giongTre, (10 * slBanhChung));
            InventoryService.gI().subQuantityItemsBag(player, thitLon, (10 * slBanhChung));

            player.slBanhChung = slBanhChung;
            ConstDataEvent.slBanhTrongNoi += slBanhChung;

            BanhChungBanhTet banhChungBanhTet = new BanhChungBanhTet();
            banhChungBanhTet.slBanhTet = player.slBanhTet;
            banhChungBanhTet.slBanhChung = player.slBanhChung;
            NauBanh_NewYear.banhChungBanhTetMaps.put(player.id, banhChungBanhTet);

//            if (nuocNau == null) {
//                ConstDataEvent.mucNuocTrongNoi += 0;
//            } else {
//                ConstDataEvent.mucNuocTrongNoi += 3;
//                InventoryServiceNew.gI().subQuantityItemsBag(player, nuocNau, (3 * slBanhChung));
//            }
            Service.gI().sendThongBao(player, "Đã đặt " + slBanhChung + " " + ItemService.gI().getTemplate(banhchung).name + " vào nồi");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Số lượng nhập không hợp lệ");
        }
    }

    public static void nauBanhTet(Player player, String s) {
        try {
            int slBanhTet = Math.abs(Integer.parseInt(s));
            Item laChuoi = InventoryService.gI().findItemBag(player, item1);
            Item gaoNep = InventoryService.gI().findItemBag(player, item2);
            Item dauXanh = InventoryService.gI().findItemBag(player, item3);
            Item giongTre = InventoryService.gI().findItemBag(player, item4);
            Item thitLon = InventoryService.gI().findItemBag(player, item5);
            Item nuocNau = InventoryService.gI().findItemBag(player, binhnuoc);

            if (laChuoi == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item1).name);
                return;
            }

            if (gaoNep == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item2).name);
                return;
            }

            if (dauXanh == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item4).name);
                return;
            }

            if (giongTre == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item4).name);
                return;
            }

            if (thitLon == null) {
                Service.gI().sendThongBao(player, "Thiếu " + ItemService.gI().getTemplate(item5).name);
                return;
            }

            if (laChuoi.quantity < (10 * slBanhTet)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item1).name);
                return;
            }

            if (gaoNep.quantity < (10 * slBanhTet)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item2).name);
                return;
            }

            if (dauXanh.quantity < (10 * slBanhTet)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item3).name);
                return;
            }

            if (giongTre.quantity < (10 * slBanhTet)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item4).name);
                return;
            }

            if (thitLon.quantity < (10 * slBanhTet)) {
                Service.gI().sendThongBao(player, "Không đủ " + ItemService.gI().getTemplate(item5).name);
                return;
            }

            InventoryService.gI().subQuantityItemsBag(player, laChuoi, (10 * slBanhTet));
            InventoryService.gI().subQuantityItemsBag(player, gaoNep, (10 * slBanhTet));
            InventoryService.gI().subQuantityItemsBag(player, dauXanh, (10 * slBanhTet));
            InventoryService.gI().subQuantityItemsBag(player, giongTre, (10 * slBanhTet));
            InventoryService.gI().subQuantityItemsBag(player, thitLon, (10 * slBanhTet));

            player.slBanhTet = slBanhTet;
            ConstDataEvent.slBanhTrongNoi += slBanhTet;

            BanhChungBanhTet banhChungBanhTet = new BanhChungBanhTet();
            banhChungBanhTet.slBanhTet = player.slBanhTet;
            banhChungBanhTet.slBanhChung = player.slBanhChung;
            NauBanh_NewYear.banhChungBanhTetMaps.put(player.id, banhChungBanhTet);

            Service.gI().sendThongBao(player, "Đã đặt " + slBanhTet + " " + ItemService.gI().getTemplate(banhtet).name + " vào nồi");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Số lượng nhập không hợp lệ");
        }
    }
}