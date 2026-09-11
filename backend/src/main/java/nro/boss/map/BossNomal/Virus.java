package nro.boss.map.BossNomal;

import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossType;
import nro.server.Client;
import nro.services.Fun.ChangeMapService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Util;
import consts.ConstPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import models.Item.ItemTimeService;
import nro.effect.EffectSkillService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.skill.Skill;
import nro.skill.SkillService;

public class Virus extends Boss {

    private final Map<Long, Long> globalEffectTimers = new ConcurrentHashMap<>();

    public Virus() throws Exception {
        super(BossType.NOMAL, BossID.VIRUS_NOMAL, true, false, false, false, new BossData(
                "Virus",
                ConstPlayer.TRAI_DAT,
                new short[]{778, 779, 780, -1, -1, -1},
                1000,
                new long[]{2_000},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42, 43, 44},
                new int[][]{
                    {Skill.DRAGON, 7, 5000}
                },
                new String[]{},
                new String[]{},
                new String[]{},
                600000));
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (damage > 20) {
                damage = 20;
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

    private void applyEffect(Player player) {
        if (player.itemTime.IsKhauTrang || InventoryService.gI().findAvatarKhauTrang(player)) {
            return;
        }

        if (!player.effectSkill.isVirus) {
            long effectEndTime = System.currentTimeMillis() + 300_000;

            ItemTimeService.gI().sendItemTime(player, 7143, 300);
            EffectSkillService.gI().setVirus(player, 300_000);
            Service.gI().sendThongBao(player, "Bạn đã bị nhiễm virus");
            this.chat("Khè khè, " + player.name + " đã bị nhiễm");

            globalEffectTimers.put(player.id, effectEndTime);
            Service.gI().point(player);
        }
    }

    private void updateVirus() {
        try {
            if (Util.isTrue(30, 100)) {
                List<Player> playersMap = this.zone.getNotBosses();
                List<Player> eligiblePlayers = new ArrayList<>();

                for (Player pl : playersMap) {
                    if (pl != null && pl.nPoint != null && !this.equals(pl) && !pl.isBoss && !pl.isDie()
                            && Util.getDistance(this, pl) <= 200) {
                        eligiblePlayers.add(pl);
                    }
                }

                if (!eligiblePlayers.isEmpty()) {
                    Player target = eligiblePlayers.get(Util.nextInt(eligiblePlayers.size()));
                    applyEffect(target);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkGlobalEffects() {
        long currentTime = System.currentTimeMillis();

        globalEffectTimers.forEach((playerId, effectEndTime) -> {
            if (currentTime >= effectEndTime) {
                Player player = Client.gI().getPlayerByID(playerId);

                if (player != null) {
                    if (!player.isDie()) {
                        if (Util.isTrue(50, 100)) {
                            player.injured(null, Util.CrisGH(player.nPoint.hp), true, false);
                            Service.gI().sendThongBao(player, "Bạn đã chết do nhiễm virus");
                        }
                    }
                }

                globalEffectTimers.remove(playerId);
            }
        });
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = this.getPlayerAttack();

                if (pl == null || pl.isDie()) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, pl) <= 40) {
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);

                    if (!globalEffectTimers.containsKey(pl.id)
                            || System.currentTimeMillis() >= globalEffectTimers.get(pl.id)) {
                        updateVirus();
                    }
                } else {
                    this.moveToPlayer(pl);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(30, 40);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(20, 100)) {
            ItemMap it = new ItemMap(this.zone, 459, 1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id);
            it.addOptionParam(112, 80);
            it.addOptionParam(93, 90);
            it.addOptionParam(20, Util.nextInt(10000));
            Service.gI().dropItemMap(this.zone, it);
        }

        for (int i = 0; i < Util.nextInt(5, 15); i++) {
            ItemMap it = new ItemMap(this.zone, 861, 1,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }

        for (int i = 0; i < Util.nextInt(5, 10); i++) {
            ItemMap it = new ItemMap(this.zone, 861, 1,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    -1);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    private long st;

    @Override
    public void active() {
        this.checkGlobalEffects();

        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }

        this.attack();

        if (Util.canDoWithTime(st, 1_800_000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        this.joinMap2();
        st = System.currentTimeMillis();
    }

    public void joinMap2() {
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }

        if (this.zone != null) {
            try {
                int zoneid = 0;

                while (zoneid < this.zone.map.zones.size() && this.zone.map.zones.get(zoneid).getNumOfPlayers() < 3) {
                    zoneid++;
                }

                // Check trong khu có boss sẽ chuyển sang khu n + 1
                while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                    zoneid++;
                }

                if (zoneid < this.zone.map.zones.size()) {
                    this.zone = this.zone.map.zones.get(zoneid);
                } else {
                    this.leaveMapNew();
                    return;
                }

                ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(100, 500),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24));

                this.changeStatus(BossStatus.CHAT_S);
            } catch (Exception e) {
                this.changeStatus(BossStatus.REST);
            }
        } else {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }
}