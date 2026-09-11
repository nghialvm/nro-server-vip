package nro.boss.map.TaoPaiPai;

import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import nro.player.Player;
import nro.effect.EffectSkillService;
import static QuanLiBoss.BossType.FINAL;
import nro.services.Service;
import QuanLiBoss.BossesData;
import Utils.Util;

public class TaoPaiPai extends Boss {
    
    protected Player playerAtt;

    public TaoPaiPai() throws Exception {
        super(FINAL, BossID.TAU_PAY_PAY_DONG_NAM_KARIN, BossesData.TAU_PAY_PAY_DONG_NAM_KARIN);
    }
    
    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        this.playerAtt = plAtt;
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1)) {
                this.chat("XÃ­ há»¥t");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (damage >= 100) {
                damage = 100;
            }
            this.nPoint.dame = (int) (damage / Util.nextInt(500, 1000));
            this.nPoint.subHP(damage);
            if (plAtt != null && plAtt.nPoint.power <= 1_500_000L) {
                long tnSm = (int) (damage * Util.nextInt(70, 190));
                Service.gI().addSMTN(plAtt, (byte) 2, tnSm, true);
            } else if (plAtt != null && plAtt.nPoint.power > 1_500_000L && plAtt.nPoint.power <= 15_000_000L) {
                long tnSm = (int) (damage * Util.nextInt(270, 490));
                Service.gI().addSMTN(plAtt, (byte) 2, tnSm, true);
            } else if (plAtt != null && plAtt.nPoint.power > 15_000_000L && plAtt.nPoint.power <= 150_000_000L) {
                long tnSm = (int) (damage * Util.nextInt(600, 2000));
                Service.gI().addSMTN(plAtt, (byte) 2, tnSm, true);
            } else if (plAtt != null && plAtt.nPoint.power > 150_000_000L && plAtt.nPoint.power <= 1_500_000_000L) {
                long tnSm = (int) (damage * Util.nextInt(2000, 8000));
                Service.gI().addSMTN(plAtt, (byte) 2, tnSm, true);
            } else if (plAtt != null && plAtt.nPoint.power > 1_500_000_000L && plAtt.nPoint.power <= 39_999_000_000L) {
                long tnSm = (int) (damage * Util.nextInt(8000, 15000));
                Service.gI().addSMTN(plAtt, (byte) 2, tnSm, true);
            } else if (plAtt != null && plAtt.nPoint.power > 39_999_000_000L) {
                long tnSm = Util.nextInt(1, 1000);
                Service.gI().addSMTN(plAtt, (byte) 2, tnSm, true);
            }
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
}




