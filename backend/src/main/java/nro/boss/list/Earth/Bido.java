package nro.boss.list.Earth;

import nro.player.Player;
import nro.services.Service;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossesData;
import nro.map.ItemMap;
import Utils.Util;
import consts.ConstTaskBadges;
import java.util.List;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.badges.BadgesTaskService;

public class Bido extends Boss {

    private long st;

    public Bido() throws Exception {
        super(BossID.BIDO, false, true, false, false, BossesData.BIDO);
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
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 861, 1, this.location.x + i * Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
//        for (int i = 0; i < Util.nextInt(3, 15); i++) {
//            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 77, Util.nextInt(1_000_000, 20_000_000), this.location.x + i * 10, this.zone.map.yPhysicInTop(this.location.x,
//                    this.location.y - 24), plKill.id));
//        }
//        for (int i = 1; i < Util.nextInt(3, 15) + 1; i++) {
//            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 77, Util.nextInt(1_000_000, 20_000_000), this.location.x - i * 10, this.zone.map.yPhysicInTop(this.location.x,
//                    this.location.y - 24), plKill.id));
//        }
        short itTemp = 426;
        ItemMap it = new ItemMap(zone, itTemp, 1, this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
        List<ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
        if (!ops.isEmpty()) {
            it.options = ops;
        }
        int soNgay = Util.nextInt(1, 7); 
        it.options.add(new ItemOption(93, soNgay));
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
            if ((boss.id == BossID.BUJIN || boss.id == BossID.KOGU || boss.id == BossID.ZANGYA) && !boss.isDie()) {
                return;
            }
        }
        this.parentBoss.changeStatus(BossStatus.ACTIVE);
    }
}





