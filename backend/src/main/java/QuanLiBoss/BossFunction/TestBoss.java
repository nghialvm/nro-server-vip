package QuanLiBoss.BossFunction;

import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import nro.player.Player;
import nro.server.Manager;
import nro.skill.Skill;
import nro.effect.EffectSkillService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static models.Item.ItemTime.TIME_ITEM_10S;
import models.Item.ItemTimeService;
import nro.services.Service;
import nro.skill.SkillService;
import consts.ConstPlayer;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstTaskBadges;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;

public class TestBoss extends Boss {
        
    public TestBoss() throws Exception {
        super(BossID.TestBoss, BossesData.TestBoss);
    }
    
    @Override
    public void reward(Player plKill) {
        byte randomDo = (byte) new Random().nextInt(Manager.itemDC12.length);
        if (Util.isTrue(30, 100)) {
            Service.gI().dropItemMap(this.zone, Util.RaitiDoc12(zone, Manager.itemDC12[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else if (Util.isTrue(30, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(15, 17), 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 190, 31000, this.location.x, this.location.y, plKill.id));
        }
    }
        
    
    
    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (plAtt.isPlayer) {
                damage = 100_000;
            } else {
                damage = 3_000_000;
            }
            this.nPoint.subHP(damage);
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
    public void active() {
        super.active();
    }
    
}




