package nro.boss.event.TrungThuEvent;

import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.TRUNGTHU_EVENT;
import QuanLiBoss.Manager.TrungThuEventManager;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.Util;
import consts.ConstPlayer;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.player.Player;
import nro.skill.Skill;


public class KhiDot extends Boss {

    public KhiDot(Zone zone, int Mapid, int x, int y) throws Exception {
        super(TRUNGTHU_EVENT, BossID.KHIDOT, true, false, false, false, new BossData(
                "Khỉ Đột",
                ConstPlayer.XAYDA,
                new short[]{198, 193, 194, -1, -1, -1},
                50_000,
                new long[]{2_000_000L},
                new int[]{Mapid},
                new int[][]{
                    {Skill.GALICK, 7, 1000}
                },
                new String[]{},
                new String[]{},
                new String[]{},
                60
        ));

        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
    }

    @Override
    public void reward(Player plKill) {
        for (int i = 0; i < Util.nextInt(1, 3); i++) {
            ItemMap it = new ItemMap(this.zone, 1045, 1,
                    this.location.x + Util.nextInt(-20, 20),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id);
            it.addOptionParam(93, 45);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing) {
                if (!(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT
                        || plAtt.playerSkill.skillSelect.template.id == Skill.MAKANKOSAPPO
                        || plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI)) {
                    if (damage > 30_000) {
                        damage = Util.nextInt(20_000, 30_000);
                    }
                }
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return damage;
        }

        return 0;
    }

    @Override
    public void joinMap() {
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            this.changeStatus(BossStatus.CHAT_S);
        } else {
            super.joinMap();
        }

        this.nPoint.crit = 100;
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900_000)) {
            this.leaveMapNew();
        }

        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        TrungThuEventManager.gI().removeBoss(this);
        this.dispose();
    }
}