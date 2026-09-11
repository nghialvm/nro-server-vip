package nro.boss.event.TrungThuEvent;



import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.TRUNGTHU_EVENT;
import QuanLiBoss.BossesData;
import nro.services.Fun.ChangeMapService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import models.Item.Item;
import nro.effect.EffectSkillService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.skill.SkillService;

public class NhatThan extends Boss {

    private long lastTimeMove;

    private int timeMove;

    private boolean isReward;

    private long lastTimeReward;

    public NhatThan() throws Exception {
        super(TRUNGTHU_EVENT, BossID.NHATTHAN, true, true, false, false, BossesData.NHATTTHAN);
    }

    @Override
    public void reward(Player plKill) {
        this.parentBoss.playerReward = plKill;
        this.parentBoss.changeStatus(BossStatus.AFK);
    }

    @Override
    public void afk() {
        if (playerReward.isPl() && !isReward && this.zone != null) {
            for (int i = 0; i < Util.nextInt(1, 5); i++) {
                ItemMap it = new ItemMap(this.zone, 1312, 1,
                        this.location.x + Util.nextInt(-50, 50),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        playerReward.id);

                it.addOptionParam(86, 0);
                it.addOptionParam(30, 0);
                it.addOptionParam(93, 35);
                Service.gI().dropItemMap(this.zone, it);
            }

            if (!InventoryService.gI().findItemKiemGo(playerReward)) {
                return;
            }

            Item KiemGo = InventoryService.gI().findItemBag(playerReward, 1304);

            if (KiemGo != null) {
                ItemMap it = new ItemMap(this.zone, 1276, 1,
                        this.location.x,
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        playerReward.id);

                it.addOptionParam(77, 24);
                it.addOptionParam(103, 24);
                it.addOptionParam(50, 24);
                it.addOptionParam(94, 10);
                it.addOptionParam(14, 10);
                it.addOptionParam(108, 10);
                it.addOptionParam(93, 3);

                Service.gI().dropItemMap(this.zone, it);
                InventoryService.gI().subQuantityItemsBag(playerReward, KiemGo, 1);
                InventoryService.gI().sendItemBag(playerReward);
            }

            isReward = true;
            lastTimeReward = System.currentTimeMillis();
            this.chat("Cảm ơn đã giúp ta tiêu diệt hắn!");
        }

        if (Util.canDoWithTime(lastTimeReward, 3000)) {
            this.leaveMap();
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
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

            if (!piercing && InventoryService.gI().findItemKiemGo(plAtt)) {
                if (plAtt.isPlMan()) {
                    if (damage > 300) {
                        damage = 300;
                    }
                } else {
                    if (damage > 100) {
                        damage = 100;
                    }
                }
            } else {
                return 0;
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
        super.joinMap();
        st = System.currentTimeMillis();
        Service.gI().changeFlag(this, 2);
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

    @Override
    public void active() {
        this.attack();
    }

    @Override
    public Player getPlayerAttack() {
        List<Player> plNotVoHinh = new ArrayList();

        for (Player pl : this.zone.getNotBosses()) {
            if ((pl.effectSkin == null || !pl.effectSkin.isVoHinh) && pl.cFlag != this.cFlag) {
                plNotVoHinh.add(pl);
            }
        }

        for (Player pl : this.zone.getBosses()) {
            if (!pl.equals(this) && pl.cFlag == 1) {
                plNotVoHinh.add(pl);
            }
        }

        if (!plNotVoHinh.isEmpty()) {
            return plNotVoHinh.get(Util.nextInt(0, plNotVoHinh.size() - 1));
        }

        return null;
    }

    @Override
    public void attack() {
        if (this.effectSkill.isCharging) {
            return;
        }

        if (Util.canDoWithTime(this.lastTimeAttack, 500)) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = getPlayerAttack();

                if (pl == null || pl.isDie()) {
                    if (Util.canDoWithTime(lastTimeMove, timeMove)) {
                        Player plRand = super.getPlayerAttack();

                        if (plRand != null) {
                            this.moveToPlayer(plRand);
                            this.lastTimeMove = System.currentTimeMillis();
                            this.timeMove = Util.nextInt(5000, 30000);
                        }
                    }
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                int dis = Util.getDistance(this, pl);

                if (dis > 450) {
                    move(pl.location.x - 24, pl.location.y);
                } else if (dis > 100) {
                    int dir = (this.location.x - pl.location.x < 0 ? 1 : -1);
                    int move = Util.nextInt(50, 100);
                    move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                } else {
                    if (Util.isTrue(30, 100)) {
                        int move = Util.nextInt(50);
                        move(pl.location.x + (Util.nextInt(0, 1) == 1 ? move : -move), this.location.y);
                    }

                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(50, 100);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
    }

    @Override
    public void moveToPlayer(Player pl) {
        if (pl.location != null) {
            moveTo(pl.location.x, pl.location.y);
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.isReward = false;
        this.playerReward = null;
        this.changeStatus(BossStatus.REST);
    }
}