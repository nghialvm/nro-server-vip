package nro.boss.map.BossNomal;

import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossType;
import QuanLiBoss.Manager.BossManager;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.Logger;
import Utils.Util;
import consts.ConstPlayer;
import consts.ConstTaskBadges;
import event.EventManager;
import java.util.ArrayList;
import java.util.List;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.skill.Skill;



public class SoiHecQuyn extends Boss {

    private long st;
    private int timeLeave;
    private boolean KiemTraNhatXuong = false;
    private long ThoiGianNhatXuong = 0;

    public SoiHecQuyn() throws Exception {
        super(BossType.NOMAL, BossID.SOI_HEC_QUYN_NOMAL, true, false, false, false, new BossData(
                "Sói héc quyn " + Util.nextInt(1, 49),
                ConstPlayer.TRAI_DAT, // gender
                new short[]{394, 395, 396, -1, -1, -1}, // outfit {head, body, leg, bag, aura, eff}
                100, // dame
                new long[]{500000L}, // hp
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, // map join
                new int[][]{
                    {Skill.DRAGON, 1, 2000},
                },
                new String[]{}, // text chat 1
                new String[]{
                    "|-1|Mi... dám đánh ta à!",
                    "|-1|Grừ... mi dám xem thường ta!",
                    "|-1|Chết nè",
                    "|-1|Còn lâu ngươi mới khuất phục được ta"
                }, // text chat 2
                new String[]{}, // text chat 3
                600000));
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            this.changeStatus(BossStatus.CHAT_S);
            this.wakeupAnotherBossWhenAppear();
            this.ThoiGianNhatXuong = 0;
            this.KiemTraNhatXuong = false;
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
                List<Zone> availableZones = new ArrayList<>();

                for (Zone zone : this.zone.map.zones) {
                    if (zone.getNumOfPlayers() <= 10 && !BossManager.gI().checkBosses(zone, BossID.SOI_HEC_QUYN_NOMAL)) {
                        availableZones.add(zone);
                    }
                }

                if (!availableZones.isEmpty()) {
                    int randomIndex = Util.nextInt(availableZones.size());
                    this.zone = availableZones.get(randomIndex);
                    ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(100, 500),
                            this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24));
                    this.changeStatus(BossStatus.CHAT_S);
                    st = System.currentTimeMillis();
                    timeLeave = Util.nextInt(600_000, 1_200_000);
                } else {
                    this.leaveMapNew();
                    return;
                }
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
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.KE_THAO_TUNG_SOI, 1);

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
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
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
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

    public void NhatXuong() {
        KiemTraNhatXuong = true;
        ThoiGianNhatXuong = System.currentTimeMillis();
    }

    public boolean KiemTraNhatXuong() {
        return KiemTraNhatXuong;
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

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20) && Util.getDistance(this, pl) > 50) {
                        if (Util.isTrue(5, 20)) {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70)
                            );
                        } else {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    pl.location.y
                            );
                        }
                    } else if (Util.getDistance(this, pl) <= 50) {
                    }

                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }

                if (ThoiGianNhatXuong > 0) {
                    if (Util.canDoWithTime(ThoiGianNhatXuong, 5000)) {
                        ThoiGianNhatXuong = 0;
                        KiemTraNhatXuong = false;
                    }
                }
            } catch (Exception ex) {
            }
        }
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

            return damage;
        }

        return 0;
    }
}