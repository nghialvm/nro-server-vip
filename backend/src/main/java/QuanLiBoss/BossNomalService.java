package QuanLiBoss;

import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import consts.ConstTaskBadges;
import java.util.List;
import models.Item.Item;
import models.Item.ItemMapService;
import nro.badges.BadgesTaskService;
import nro.boss.map.BossNomal.SoiHecQuyn;
import nro.boss.map.BossNomal.XinBaTo;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.player.Player;


public class BossNomalService {

    public static void SoiHecQuyn(Player pl, Item item) {
        if (pl == null || pl.zone == null || item == null || pl.isDie()) {
            return;
        }

        List<Player> bosses = pl.zone.getBosses();
        if (bosses == null || bosses.isEmpty()) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Sói héc quyn");
            return;
        }

        Boss soihecQuyn = null;
        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN_NOMAL) {
                    soihecQuyn = (Boss) bossPlayer;
                    break;
                }
            }
        }

        if (soihecQuyn == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Sói héc quyn");
            return;
        }

        if (Util.getDistance(pl, soihecQuyn) > 200) {
            Service.gI().sendThongBao(pl, "Hãy đến gần Sói!");
            return;
        }

        SoiHecQuyn soi = (SoiHecQuyn) soihecQuyn;

        if (soi.KiemTraNhatXuong()) {
            Service.gI().sendThongBao(pl, "Sói đã no rồi");
            return;
        }

        // Sói ăn xương
        soi.NhatXuong();
        Service.gI().chat(soihecQuyn, "Ê, Cục xương ngon quá");

        // Cập nhật nhiệm vụ
        BadgesTaskService.updateCountBagesTask(pl, ConstTaskBadges.KE_THAO_TUNG_SOI, 1);

        // Rơi item xương ảo
        int x = pl.location.x;
        if (x < 0 || x >= pl.zone.map.mapWidth) {
            return;
        }

        int y = pl.zone.map.yPhysicInTop(x, pl.location.y - 24);

        ItemMap itemMap = new ItemMap(pl.zone, 460, 1, x, y, pl.id);
        itemMap.isPickedUp = true;
        itemMap.createTime -= 23000;
        Service.gI().dropItemMap(pl.zone, itemMap);

        // Trừ item
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);

        // Phần thưởng từ boss
        Service.gI().dropItemBossNomal_PickItem(pl);

        // Sau 5s thì sói rời map
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ItemMapService.gI().removeItemMapAndSendClient(itemMap);
            soi.leaveMapNew();
        }).start();
    }

    public static void XinBaTo(Player pl, Item item) {
        if (pl == null || pl.zone == null || item == null || item.quantity < 99) {
            Service.gI().sendThongBao(pl, "Hãy tìm đủ 99 bình và tìm Xinbatô để cho");
            return;
        }

        List<Player> bosses = pl.zone.getBosses();
        if (bosses == null || bosses.isEmpty()) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Xinbatô");
            return;
        }

        Boss xinbato = null;
        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.XIN_BA_TO_NOMAL && !pl.isDie()) {
                    xinbato = (Boss) bossPlayer;
                    break;
                }
            }
        }

        if (xinbato == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Xinbatô");
            return;
        }

        if (Util.getDistance(pl, xinbato) > 200) {
            Service.gI().sendThongBao(pl, "Hãy đến gần Xinbatô!");
            return;
        }

        XinBaTo xinBaToBoss = (XinBaTo) xinbato;

        if (xinBaToBoss.KiemTraBinhNuoc()) {
            Service.gI().sendThongBao(pl, "Xinbatô đang không cần nước nữa.");
            return;
        }

        // Bắt đầu xử lý cho nước
        xinBaToBoss.BinhNuoc();
        Service.gI().chat(xinbato, "Cảm ơn " + pl.name);

        // Cập nhật nhiệm vụ
        BadgesTaskService.updateCountBagesTask(pl, ConstTaskBadges.NUOC_ANH_BAO, 1);

        // Rơi item ảo ra đất (x99)
        int x = pl.location.x;
        if (x < 0 || x >= pl.zone.map.mapWidth) {
            return;
        }

        int y = pl.zone.map.yPhysicInTop(x, pl.location.y - 24);
        ItemMap itemMap = new ItemMap(pl.zone, 456, 99, x, y, pl.id);
        itemMap.isPickedUp = true;
        itemMap.createTime -= 23000;

        Service.gI().dropItemMap(pl.zone, itemMap);

        // Trừ 99 bình nước
        InventoryService.gI().subQuantityItemsBag(pl, item, 99);
        InventoryService.gI().sendItemBag(pl);

        // Thêm phần thưởng từ boss nếu có
        Service.gI().dropItemBossNomal_PickItem(pl);

        // Tạo thread xử lý xóa itemMap và cho boss rời map sau 5 giây
        ItemMap finalItemMap = itemMap;
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ItemMapService.gI().removeItemMapAndSendClient(finalItemMap);
            xinBaToBoss.leaveMapNew();
        }).start();
    }

    public static void CheckDonateWater_AutoUpdated(Player pl) {
        if (pl == null || pl.zone == null || pl.isDie()) {
            return;
        }

        Zone zone = pl.zone;
        List<Player> bosses = zone.getBosses();
        if (bosses == null || bosses.isEmpty()) {
            return;
        }

        boolean hasXinBaTo = false;
        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.XIN_BA_TO_NOMAL) {
                    hasXinBaTo = true;
                    break;
                }
            }
        }

        // Nếu có boss Xinbatô trong zone và đã qua 5s
        if (hasXinBaTo && Util.canDoWithTime(zone.lastTimeNotifyXinBaTo, 5000)) {
            zone.lastTimeNotifyXinBaTo = System.currentTimeMillis();

            // Gửi thông báo cho tất cả người chơi trong khu vực
            for (Player p : zone.getPlayers()) {
                if (!p.isDie()) {
                    Service.gI().sendThongBao(p, "Hãy mang x99 Bình nước đến cho Xinbatô");
                }
            }
        }
    }
}