package nro.boss.map.BossNomal;

import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossType;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Logger;
import Utils.Util;
import consts.ConstPlayer;
import event.EventManager;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.player.Player;
import nro.skill.Skill;
import nro.skill.SkillService;

public class AnTrom extends Boss {

    private long goldAnTrom;
    private long lastTimeAnTrom;

    public AnTrom() throws Exception {
        super(BossType.NOMAL, BossID.AN_TROM_NOMAL, true, false, false, false, new BossData(
                "Ăn Trộm " + Util.nextInt(1, 49),
                ConstPlayer.TRAI_DAT,
                new short[]{201, 202, 203, -1, -1, -1},
                1,
                new long[]{100},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84},
                new int[][]{
                    {Skill.THAI_DUONG_HA_SAN, 3, 50000}
                },
                new String[]{
                    "|-1|Tới giờ làm việc, lụm lụm",
                    "|-1|Cảm giác mình vào phải khu người nghèo"
                },
                new String[]{
                    "|-1|Ái chà vàng vàng",
                    "|-1|Không làm vẫn có ăn",
                    "|-2|Dám ăn trộm giữa ban ngày thế à",
                    "|-2|Cút ngay không là ăn đòn"
                },
                new String[]{
                    "|-1|Híc lần sau ta sẽ cho ngươi phá sản",
                    "|-2|Chừa thói ăn trộm nghe chưa"
                },
                600));
    }

    @Override
    public Zone getMapJoin() {
        int mapId = this.data[this.currentLevel].getMapJoin()[Util.nextInt(0, this.data[this.currentLevel].getMapJoin().length - 1)];
        return MapService.gI().getMapById(mapId).zones.get(0);
    }

    @Override
    public Player getPlayerAttack() {
        return super.getPlayerAttack();
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = 1;
            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            if (Util.isTrue(20, 100)) {
                ItemMap it = new ItemMap(this.zone, 189, Util.nextInt(20_000, 25_000),
                        this.location.x + Util.nextInt(-20, 20),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        -1);
                Service.gI().dropItemMap(this.zone, it);
            }

            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
            SkillService.gI().useSkill(this, plAtt, null, -1, null);
            return damage;
        }

        return 0;
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

                if (Util.getDistance(this, pl) <= 40) {
                    if (!Util.canDoWithTime(this.lastTimeAnTrom, 500) || goldAnTrom > 10_000_000_000L) {
                        return;
                    }

                    int gold = 0;

                    if (pl.isPl()) {
                        if (pl.inventory.gold >= 100000000) {
                            gold = Util.nextInt(20000, 30000);
                        } else if (pl.inventory.gold >= 10000000) {
                            gold = Util.nextInt(4000, 5000);
                        } else if (pl.inventory.gold >= 1000000) {
                            gold = Util.nextInt(1000, 2000);
                        }

                        this.chat("Đã trộm được " + Util.formatNumber(gold, FormatStyle.VIETNAMESE) + " vàng rồi");

                        if (gold > 0) {
                            pl.inventory.gold -= gold;
                            goldAnTrom += gold;
                            Service.gI().stealMoney(pl, -gold);

                            ItemMap itemMap = new ItemMap(this.zone, 190, gold,
                                    (this.location.x + pl.location.x) / 2,
                                    this.location.y,
                                    this.id);

                            Service.gI().dropItemMap(this.zone, itemMap);
                            Service.gI().sendToAntherMePickItem(this, itemMap.itemMapId);
                            this.zone.removeItemMap(itemMap);
                            this.lastTimeAnTrom = System.currentTimeMillis();
                        }
                    }
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

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(30, 40);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
    }

    @Override
    public void die(Player plKill) {
        this.reward(plKill);
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void reward(Player plKill) {
        if (goldAnTrom != 0) {
            goldAnTrom = goldAnTrom * 8 / 10;

            for (byte i = 0; i < 5; i++) {
                ItemMap it = new ItemMap(this.zone, 190, (int) (goldAnTrom / 5),
                        this.location.x + Util.nextInt(-50, 50),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        plKill.id);
                Service.gI().dropItemMap(this.zone, it);
            }
        }

        if (EventManager.LUNNAR_NEW_YEAR) {
            for (int i = 0; i < Util.nextInt(1, 5); i++) {
                ItemMap it = new ItemMap(this.zone, 751, 1,
                        this.location.x + Util.nextInt(-15, 15),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        plKill.id);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }

            for (int i = 0; i < Util.nextInt(1, 5); i++) {
                ItemMap it = new ItemMap(this.zone, 1472, 1,
                        this.location.x + Util.nextInt(-15, 15),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        plKill.id);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }
        }
    }

    private long st;

    @Override
    public void active() {
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
        this.nPoint.hpMax = 100;
        this.nPoint.hp = this.nPoint.hpMax;
        this.nPoint.dameg = this.nPoint.hpMax / 10;
        goldAnTrom = 0;
        st = System.currentTimeMillis();

        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
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

                // Check trong khu lớn hơn 10 người chuyển sang khu n + 1
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
                Logger.error(this.data[0].getName() + ": Lỗi đang tiến hành REST\n");
                this.changeStatus(BossStatus.REST);
            }
        } else {
            Logger.error(this.data[0].getName() + ": Lỗi map đang tiến hành RESPAWN\n");
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