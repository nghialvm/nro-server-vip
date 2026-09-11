package nro.boss.task.GinyuForceNamek;

import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossesData;
import Utils.Util;
import consts.ConstTaskBadges;
import java.util.List;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.services.Service;

public class So4Namek extends Boss {

    private long st;

    public So4Namek() throws Exception {
        super(BossID.SO_4_NAMEK, false, true, false, false, BossesData.SO_4_NAMEK);
    }

    @Override
    public void moveTo(int x, int y) {
        if (this.currentLevel == 1) {
            return;
        }
        super.moveTo(x, y);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);

        for (int i = 0; i < Util.nextInt(10); i++) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 861, 1,
                    this.location.x + i * Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id));
        }

        short itTemp = 616;
        ItemMap it = new ItemMap(zone, itTemp, 1,
                this.location.x + Util.nextInt(-50, 50),
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                plKill.id);

        // Lấy option mặc định từ shop
        List<ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);

        if (!ops.isEmpty()) {
            it.options = ops;
        }

        // Thêm option 93 với số ngày random 1 - 7
        int soNgay = Util.nextInt(1, 7);
        it.options.add(new ItemOption(93, soNgay));

        // Drop ra map
        Service.gI().dropItemMap(this.zone, it);
    }

    @Override
    protected void notifyJoinMap() {
        if (this.currentLevel == 1) {
            return;
        }
        super.notifyJoinMap();
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public void doneChatE() {
        if (this.parentBoss == null || this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.SO_3_NAMEK && !boss.isDie()) {
                boss.changeStatus(BossStatus.ACTIVE);
                break;
            }
        }
    }
}