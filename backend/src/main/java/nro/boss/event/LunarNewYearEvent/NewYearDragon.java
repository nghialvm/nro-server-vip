package nro.boss.event.LunarNewYearEvent;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.TET_EVENT;
import QuanLiBoss.BossesData;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstTaskBadges;
import event.EventManager;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.skill.SkillService;

public class NewYearDragon extends Boss {

    private long st;

    public NewYearDragon() throws Exception {
        super(TET_EVENT, BossID.NEW_YEAR_DRAGON, false, true, false, false, BossesData.NEW_YEAR_DRAGON_1, BossesData.NEW_YEAR_DRAGON_2, BossesData.NEW_YEAR_DRAGON_3);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        if (EventManager.LUNNAR_NEW_YEAR) {
            for (int i = 0; i < Util.nextInt(1, 20); i++) {
                ItemMap it = new ItemMap(this.zone, 751, 1, this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }
            for (int i = 0; i < Util.nextInt(1, 20); i++) {
                ItemMap it = new ItemMap(this.zone, 1472, 1, this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }
            for (int i = 0; i < Util.nextInt(1, 10); i++) {
                ItemMap it = new ItemMap(this.zone, Util.nextInt(1191, 1193), 1, this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
                it.addOptionParam(30, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }
        }
    }
    
    @Override
    public void chatM() {
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 10000);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 1800000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        short[] List_Item = {1177, 1178, 1179, 1180, 1181, 1473, 1474, 1475, 748, 749, 750};
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
            }
            this.nPoint.subHP(damage);
            if (Util.isTrue(15, 100)) {
                ItemMap it = new ItemMap(this.zone, (List_Item[Util.nextInt(0, List_Item.length - 1)]), 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
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

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.location == null) {
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
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
   
}






