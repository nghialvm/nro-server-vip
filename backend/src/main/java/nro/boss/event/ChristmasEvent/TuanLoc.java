package nro.boss.event.ChristmasEvent;



import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossType;
import QuanLiBoss.BossesData;
import QuanLiBoss.Manager.BossManager;
import nro.server.Client;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.Logger;
import Utils.Util;
import consts.ConstPlayer;
import nro.effect.EffectSkillService;
import nro.player.Player;

public class TuanLoc extends Boss {

    private long st;
    private int timeLeave;
    public long playerId;
    private boolean afk;

    public TuanLoc() throws Exception {
        super(BossType.CHRISTMAS_EVENT, BossID.TUAN_LOC, BossesData.TUAN_LOC);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            this.notifyJoinMap();
            this.changeStatus(BossStatus.CHAT_S);
            this.wakeupAnotherBossWhenAppear();
            return;
        }

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

                // Check trong khu có boss sẽ chuyển sang khu n + 1
                while (zoneid < this.zone.map.zones.size()
                        && BossManager.gI().checkBosses(this.zone.map.zones.get(zoneid), BossID.ONG_GIA_NOEL)) {
                    zoneid++;
                }

                if (zoneid < this.zone.map.zones.size()) {
                    this.zone = this.zone.map.zones.get(zoneid);
                } else {
                    this.leaveMapNew();
                    return;
                }

                ChangeMapService.gI().changeMap(
                        this,
                        this.zone,
                        Util.nextInt(100, 500),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24)
                );

                this.changeStatus(BossStatus.CHAT_S);
                st = System.currentTimeMillis();
                timeLeave = Util.nextInt(600_000, 1_200_000);
            } catch (Exception e) {
                Logger.error(this.data[0].getName() + ": Lỗi đang tiến hành REST\n");
                this.changeStatus(BossStatus.REST);
            }
        } else {
            Logger.error(this.data[0].getName() + ": Lỗi map đang tiến hành RESPAWN\n");
            this.changeStatus(BossStatus.RESPAWN);
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

        String textChat = this.data[this.currentLevel].getTextM()[
                Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)
        ];

        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);

        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 10000);
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, timeLeave)) {
            this.leaveMapNew();
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.playerId = -1;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 250)) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = getPlayerAttack();

                if (pl == null || pl.location == null || pl.isDie()) {
                    return;
                }

                int dis = Util.getDistance(this, pl);

                if (dis > 450) {
                    move(pl.location.x - 24, pl.location.y);
                } else if (dis > 100) {
                    int dir = this.location.x - pl.location.x < 0 ? 1 : -1;
                    int move = Util.nextInt(50, 100);
                    move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(this.lastTimeAttack, 500)) {
            this.lastTimeAttack = System.currentTimeMillis();

            Player pl = Client.gI().getPlayerByID(playerId);

            if (pl == null || pl.zone == null) {
                return;
            }

            if (pl.haveReward_TuanLoc) {
                pl.haveReward_TuanLoc = false;
                this.leaveMap();
                return;
            }

            if (this.zone.equals(pl.zone)) {
                int dis = Util.getDistance(this, pl);

                if (dis <= 300) {
                    if (dis > 50) {
                        int dir = this.location.x - pl.location.x < 0 ? 1 : -1;
                        int move = Util.nextInt(50, 100);
                        move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                        st = System.currentTimeMillis();
                    }

                    afk = false;
                    pl.canReward_TuanLoc = true;
                } else {
                    afk = true;
                    pl.canReward_TuanLoc = false;
                }
            } else if (!afk) {
                if (pl.changeMapVIP_TuanLoc) {
                    pl.changeMapVIP_TuanLoc = false;
                    pl.canReward_TuanLoc = false;
                    afk = true;
                    return;
                }

                ChangeMapService.gI().changeMap(
                        this,
                        pl.zone,
                        pl.location.x + Util.nextInt(-10, 10),
                        pl.location.y
                );
            }
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

            if (damage > 500_000) {
                damage = 500_000;
            }

            this.nPoint.subHP(damage);
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void active() {
        this.attack();

        if (this.typePk == ConstPlayer.PK_ALL) {
            this.changeToTypeNonPK();
        }
    }

    public void followPlayer(Player pl) {
        // 1. Nếu có người cũ được đi theo → xóa quyền thưởng
        if (this.playerId != -1 && this.playerId != pl.id) {
            Player oldPlayer = Client.gI().getPlayerByID(this.playerId);

            if (oldPlayer != null) {
                oldPlayer.canReward_TuanLoc = false;
                oldPlayer.haveReward_TuanLoc = false;
                Service.gI().sendThongBao(oldPlayer, "Tuần Lộc đã bị người khác cướp mất!");
            }
        }

        // 2. Gán playerId mới
        this.playerId = Math.abs(pl.id);
        this.nPoint.hp = this.nPoint.hpMax;
        this.changeStatus(BossStatus.AFK);

        // 3. Cho người mới được nhận thưởng sau khi đến gần
        pl.canReward_TuanLoc = false;
        pl.haveReward_TuanLoc = false;

        Service.gI().chat(pl, "Ha ha, Tuần Lộc đã chịu theo ta");
    }
}