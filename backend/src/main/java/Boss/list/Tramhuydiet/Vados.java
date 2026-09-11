package Boss.list.Tramhuydiet;

import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import nro.map.ItemMap;
import nro.player.Player;
import nro.server.Manager;
import nro.effect.EffectSkillService;
import QuanLiBoss.Manager.BossManager;
import nro.services.Service;
import Utils.Util;
import consts.ConstTaskBadges;
import java.util.List;
import java.util.Random;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.badges.BadgesTaskService;
import nro.services.TaskService;

public class Vados extends Boss {

    public Vados() throws Exception {
        super(BossID.VADOS, BossesData.VADOS);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        int x = this.location.x; 
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(1, 3)) {
            ItemMap it = ItemService.gI().randDoTLBoss(this.zone, 1, x + Util.nextInt(-15, 15), y, plKill.id);
            if (it != null) {
                Service.gI().dropItemMap(zone, it);
            }
        }
        if (Util.isTrue(1, 2)) {
            int group = Util.nextInt(1, 100) <= 70 ? 0 : 1;
            int[][] drops = {
                {230, 231, 232, 234, 235, 236, 238, 239, 240, 242, 243, 244, 246, 247, 248, 250, 251, 252, 266, 267, 268, 270, 271, 272, 274, 275, 276},
                {254, 255, 256, 258, 259, 260, 262, 263, 264, 278, 279, 280} 
            };
            int dropOptional = drops[group][Util.nextInt(0, drops[group].length - 1)];
            ItemMap optionalItemMap = new ItemMap(this.zone, dropOptional, 1, x + Util.nextInt(-15, 15), y, plKill.id);
            List<ItemOption> optionalOps = ItemService.gI().getListOptionItemShop((short) dropOptional);
            optionalOps.forEach(option -> option.param = (int) (option.param * Util.nextInt(100, 115) / 100.0));
            optionalItemMap.options.addAll(optionalOps);
            int value = 0;
            if (Util.isTrue(40, 100)) {
                value = Util.nextInt(1, 3); 
            } else if (Util.isTrue(10, 100)) {
                value = Util.nextInt(4, 5); 
            }
            optionalItemMap.options.add(new ItemOption(107, value));
            Service.gI().dropItemMap(zone, optionalItemMap);
        }
        if (Util.isTrue(1, 2)) {
            int[] dropItems = {15, 16, 17, 18, 19, 20};
            int dropOptional = dropItems[Util.nextInt(0, dropItems.length - 1)];
            ItemMap optionalItemMap = new ItemMap(this.zone, dropOptional, Util.nextInt(1, 2), x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, optionalItemMap);
        }
        ItemMap it = new ItemMap(this.zone, 190, Util.nextInt(28000, 30000), x + Util.nextInt(-15, 15), y, plKill.id);
        Service.gI().dropItemMap(zone, it);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }
    
    @Override
    public void active() {
        if (BossManager.gI().getBossById(BossID.CHAMPA) == null) {
            this.leaveMap();
        }
        this.nPoint.khangTDHS = true;
        super.active();
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (Util.isTrue(50, 100) && plAtt != null) { // tỉ lệ hụt của thiên sứ
            Util.isTrue(this.nPoint.tlNeDon, 1);
            if (Util.isTrue(80, 100)) {
                this.chat("Hãy Để Bản Năng Tự Vận Động");
                this.chat("Tránh Các Động Tác Thừa");
            } else if (Util.isTrue(80, 100)) {
                this.chat("Đây Chính Là Bản Năng Vô Cực");
            }
            damage = 0;

        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1)) {
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
            if (damage >= 1) {
                damage = 1;
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
    public void joinMap() {
        super.joinMap();
    }
    
    @Override
    public void leaveMap() {
        super.leaveMap();
    }
}