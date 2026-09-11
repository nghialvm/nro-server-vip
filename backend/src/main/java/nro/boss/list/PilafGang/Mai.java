package nro.boss.list.PilafGang;

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
import nro.effect.EffectSkillService;

public class Mai extends Boss {

    private long st;

    public Mai() throws Exception {
        super(BossID.MAI, false, true, false, false, BossesData.MAI);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);

        // Rơi item phụ 861
        for (int i = 0; i < Util.nextInt(5, 10); i++) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 861, 1,
                    this.location.x + i * Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id));
        }

        // Rơi item chính 636
        short itTemp = 636;
        ItemMap it = new ItemMap(zone, itTemp, 1,
                this.location.x + Util.nextInt(-50, 50),
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                plKill.id);

        List<ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
        if (!ops.isEmpty()) {
            it.options = ops;
        }

        it.options.add(new ItemOption(93, Util.nextInt(1, 7)));
        Service.gI().dropItemMap(this.zone, it);
    }

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.ACTIVE);
    }

    @Override
    public void doneChatE() {
        // Mai chết -> gọi Pilap
        if (parentBoss == null || parentBoss.bossAppearTogether == null
                || parentBoss.bossAppearTogether[parentBoss.currentLevel] == null) {
            return;
        }

        for (Boss boss : parentBoss.bossAppearTogether[parentBoss.currentLevel]) {
            if (boss.id == BossID.PI_LAP && !boss.isDie()) {
                boss.changeStatus(BossStatus.ACTIVE);
            }
        }
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
        if (zone != null && zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(100, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            if (damage > 1) {
                damage = 1;
            }

            this.nPoint.subHP(damage);
            return damage;
        }

        return 0;
    }
}