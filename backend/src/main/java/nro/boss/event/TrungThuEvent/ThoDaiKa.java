package nro.boss.event.TrungThuEvent;

import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.TRUNGTHU_EVENT;
import QuanLiBoss.BossesData;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstPlayer;
import models.Item.ItemTimeService;
import nro.effect.EffectSkinService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.skill.SkillService;


public class ThoDaiKa extends Boss {

    private static final String[] textCarot = new String[]{
        "Biến thành cà rốt cho ta!",
        "Cà rốt chi thuật!",
        "Cà rốt hóa thân! Hô biến!",
        "Cà rốt hóa hình! Giờ thì chạy đi!",
        "Biến hình... cà rốt style!",
        "Lệnh Thỏ ban ra, biến thành củ đi!"
    };

    public ThoDaiKa() throws Exception {
        super(TRUNGTHU_EVENT, BossID.THO_DAI_KA, true, true, false, false, BossesData.THO_DAI_KA);
    }

    @Override
    public void reward(Player plKill) {
        for (int i = 0; i < Util.nextInt(5, 10); i++) {
            ItemMap it = new ItemMap(this.zone, 462, 1,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id);
            it.addOptionParam(30, 0);
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
                if (damage > 5000) {
                    damage = 5000;
                }
            }
            if (Util.isTrue(40, 100)) {
                if (!plAtt.effectSkin.isThoDaiKa) {
                    EffectSkinService.gI().setThoDaiKa(plAtt, System.currentTimeMillis(), 300_000);
                    Service.getInstance().Send_Caitrang(plAtt);
                    Service.gI().chat(this, textCarot[Util.nextInt(0, textCarot.length - 1)]);
                    ItemTimeService.gI().sendItemTime(plAtt, 4082, 300_000 / 1000);
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
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    if (Util.isTrue(50, 100)) {
                        if (!pl.effectSkin.isThoDaiKa) {
                            EffectSkinService.gI().setThoDaiKa(pl, System.currentTimeMillis(), 300_000);
                            Service.getInstance().Send_Caitrang(pl);
                            Service.gI().chat(this, textCarot[Util.nextInt(0, textCarot.length - 1)]);
                            ItemTimeService.gI().sendItemTime(pl, 4082, 300_000 / 1000);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void joinMap() {
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            this.changeStatus(BossStatus.CHAT_S);
        } else {
            super.joinMap();
        }
        this.nPoint.khangTDHS = true;
        this.nPoint.isThoDaiCa = true;
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 3_800_000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }
}