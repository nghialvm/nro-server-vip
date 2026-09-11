package Boss.map.Nguhanhson;

import QuanLiBoss.BossID;
import QuanLiBoss.Boss;
import QuanLiBoss.BossesData;
import nro.map.ItemMap;
import nro.player.Player;
import nro.effect.EffectSkillService;
import nro.services.Service;
import Utils.Util;

public class NgoKhong extends Boss {

    protected Player playerAtt;
    private boolean trieuhoicon;
    long lastTimeBlame;

    public NgoKhong() throws Exception {
        super(BossID.NgoKhong, BossesData.NgoKhong);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(this.zone, 1563, 1, this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void active() {
        super.active();
        if (System.currentTimeMillis() - lastTimeBlame > Util.nextInt(10000, 20000)) {
            this.chat("500 Năm Trước Ta Đại Náo Thiên Cung Ta Còn Không Sợ");
            this.chat("Ngươi Nghĩ Ta Sợ Ngươi Sao!");
            lastTimeBlame = System.currentTimeMillis();
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(50, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage / 2);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            this.nPoint.subHP(damage);

            if (this.nPoint.hp <= 700000000 && !this.trieuhoicon) {
                try {
                    this.chat("|7|Xuất Hiện Đi Các Con Của Đại Vương");
                    NgoKhongClone ngoKhongClone = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone1);
                    NgoKhongClone ngoKhongClone1 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone2);
                    NgoKhongClone ngoKhongClone2 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone3);
                    NgoKhongClone ngoKhongClone3 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone4);
                    NgoKhongClone ngoKhongClone4 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone5);
                    NgoKhongClone ngoKhongClone5 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone6);
                    NgoKhongClone ngoKhongClone6 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone7);
                    NgoKhongClone ngoKhongClone7 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone8);
                    NgoKhongClone ngoKhongClone8 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone9);
                    NgoKhongClone ngoKhongClone9 = new NgoKhongClone(this.zone, 10000, 50000000, BossID.ngokhongclone10);
                    this.trieuhoicon = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (isDie()) {
                this.setDie(plAtt);
                this.trieuhoicon = false;
                die(plAtt);
            }

            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
    }
}