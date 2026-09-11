package nro.boss.task.Napa;


import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossesData;
import nro.services.Service;
import Utils.Util;
import consts.ConstTaskBadges;
import event.EventManager;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.services.TaskService;

public class MapDauDinh extends Boss {

    private long st;

    public MapDauDinh() throws Exception {
        super(BossID.MAP_DAU_DINH, true, true, false, false, BossesData.MAP_DAU_DINH);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        if (EventManager.LUNNAR_NEW_YEAR) {
            for (int i = 0; i < Util.nextInt(1, 5); i++) {
                ItemMap it = new ItemMap(this.zone, 751, 1, this.location.x + Util.nextInt(-15, 15), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }
        }
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
}






