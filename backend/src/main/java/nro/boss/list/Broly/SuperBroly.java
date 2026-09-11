package nro.boss.list.Broly;

import QuanLiBoss.BossData;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.BROLY;
import QuanLiBoss.BossesData;
import nro.effect.EffectSkillService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nro.map.Zone;
import consts.ConstPlayer;
import consts.ConstRatio;
import nro.player.Player;
import QuanLiBoss.Manager.BossManager;
import nro.services.DetuService;
import nro.services.Service;
import nro.services.Fun.ChangeMapService;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.Util;
import nro.map.ItemMap;
import static nro.server.Manager.player;

public class SuperBroly extends Boss {

    private final List<Player> playersAttack;
    private final Map angryPlayers;
    private static final int DIS_ANGRY = 100;

    public SuperBroly() throws Exception {
        super(BROLY, BossID.SUPER_BROLY, false, true, false, false, BossesData.SUPPER_BROLY);
        this.playersAttack = new LinkedList<>();
        this.angryPlayers = new HashMap();
    }

    public SuperBroly(Zone zone, int x, int y) throws Exception {
        super(BROLY, BossID.SUPER_BROLY, false, true, false, false, new BossData(
                "Super Broly", // name
                ConstPlayer.XAYDA, // gender
                new short[]{294, 295, 296, -1, -1, -1}, // outfit {head, body, leg, bag, aura, eff}
                100, // dame
                new long[]{500}, // hp
                new int[]{4, 5, 10, 12, 13, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, // map join
                new int[][]{
                    {Skill.TAI_TAO_NANG_LUONG, 1, 25000},
                    {Skill.DEMON, 1, 1000},
                    {Skill.KAMEJOKO, Util.nextInt(1, 7), 4000},
                    {Skill.MASENKO, Util.nextInt(1, 7), 2000},
                    {Skill.ANTOMIC, Util.nextInt(1, 7), 300},
                }, // skill
                new String[]{}, // text chat 1
                new String[]{
                    "|-1|Tránh xa ta ra, đừng để ta nổi giận",
                }, // text chat 2
                new String[]{
                    "|-1|Các ngươi giỏi lắm. Ta sẽ quay lại"
                }, // text chat 3
                600 // type appear
        ));
        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    @Override
    public Player getPlayerAttack() {
        try {
            if (countChangePlayerAttack < targetCountChangePlayerAttack
                    && plAttack != null
                    && plAttack.zone.equals(this.zone)
                    && !plAttack.effectSkin.isVoHinh) {
                if (!plAttack.isDie()) {
                    this.countChangePlayerAttack++;
                    return plAttack;
                }
            }
        } catch (Exception e) {
            this.playersAttack.remove(plAttack);
        }

        if (!playersAttack.isEmpty()) {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            Player plAtt = playersAttack.get(Util.nextInt(0, playersAttack.size() - 1));
            return (this.plAttack = plAtt);
        }

        return null;
    }

    @Override
    public void reward(Player plKill) {
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

        if (Util.isTrue(30, 100)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 568, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        this.name = "Super Broly " + Util.nextInt(1, 99);
        this.nPoint.hpMax = Util.nextInt(1_000_000, 16_070_777);
        this.nPoint.hp = this.nPoint.hpMax;
        this.nPoint.dame = Util.nextInt(20000, 100000);
        this.joinMap2();
        DetuService.gI().createNormalPet(this);
    }

    public void joinMap2() {
        if (Util.isTrue(80, 100)) {
            if (this.zone != null) {
                ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            }
            this.changeStatus(BossStatus.CHAT_S);
            this.notifyJoinMap();
            return;
        }

        this.zone = getMapJoin();

        if (this.zone != null) {
            try {
                int zoneid = Util.nextInt(1, this.zone.map.zones.size() - 1);

                while (zoneid < this.zone.map.zones.size()
                        && this.zone.map.zones.get(zoneid).getBosses().size() > 0) {
                    zoneid++;
                }

                if (zoneid < this.zone.map.zones.size()) {
                    this.zone = this.zone.map.zones.get(zoneid);
                } else {
                    this.leaveMap();
                }

                if (this.zone.zoneId < 1) {
                    this.leaveMap();
                }

                ChangeMapService.gI().changeMap(this, this.zone, -1, -1);
                this.changeStatus(BossStatus.CHAT_S);
                this.notifyJoinMap();
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
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (Util.isTrue(1, 100)) {
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, 3));
                this.tangChiSo();
                SkillService.gI().useSkill(this, null, null, -1, null);
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing) {
                if ((plAtt.playerSkill.skillSelect.template.id == Skill.ANTOMIC
                        || plAtt.playerSkill.skillSelect.template.id == Skill.KAMEJOKO
                        || plAtt.playerSkill.skillSelect.template.id == Skill.MASENKO
                        || plAtt.playerSkill.skillSelect.template.id == Skill.LIEN_HOAN)) {
                    this.chat("Xí hụt");
                    damage = 0;
                }

                if (!(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT
                        || plAtt.playerSkill.skillSelect.template.id == Skill.MAKANKOSAPPO
                        || plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI)) {
                    if (damage >= this.nPoint.hpMax / 100) {
                        damage = this.nPoint.hpMax / 100;
                    }
                }
            }

            addPlayerAttack(plAtt);
            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return (int) damage;
        }

        return 0;
    }

    private long lastTimeAttack;

    private void addPlayerAttack(Player plAtt) {
        boolean haveInList = false;

        for (Player pl : playersAttack) {
            if (pl.equals(pl)) {
                haveInList = true;
                break;
            }

            if (pl.equals(plAtt)) {
                haveInList = true;
                break;
            }
        }

        if (!haveInList) {
            playersAttack.add(plAtt);
            this.chat("Mi làm ta nổi giận rồi " + plAtt.name.replaceAll("$", "").replaceAll("#", ""));
        }
    }

    protected boolean charge() {
        if (this.effectSkill.isCharging && Util.isTrue(15, 100)) {
            this.effectSkill.isCharging = false;
            return false;
        }

        if (Util.isTrue(1, 20)) {
            for (Skill skill : this.playerSkill.skills) {
                if (skill.template.id == Skill.TAI_TAO_NANG_LUONG) {
                    this.playerSkill.skillSelect = skill;

                    if (this.nPoint.getCurrPercentHP() < Util.nextInt(0, 100)
                            && SkillService.gI().canUseSkillWithCooldown(this)
                            && SkillService.gI().useSkill(this, null, null, -1, null)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void goToXY(int x, int y, boolean isTeleport) {
        EffectSkillService.gI().stopCharge(this);
        super.goToXY(x, y, isTeleport);
    }

    @Override
    public void effectCharger() {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }

    private void angry() {
        if (this.playersAttack.size() < 5 && Util.isTrue(7, ConstRatio.PER100)) {
            Iterator i = (Iterator) this.zone.getPlayers();

            while (i.hasNext()) {
                Player pl = (Player) i.next();

                if (pl == null) {
                    continue;
                }

                if (pl != null
                        && !pl.equals(this)
                        && Util.getDistance(this, pl) <= DIS_ANGRY
                        && !pl.isBoss
                        && !pl.isDie()
                        && !isInListPlayersAttack(pl)) {
                    try {
                        int count = (int) angryPlayers.get(pl);

                        if (++count > 2) {
                            addPlayerAttack(pl);
                        } else {
                            this.chat("Tránh xa ta ra, đừng để ta nổi giận");
                            effectCharger();
                        }

                        angryPlayers.put(pl, count);
                        break;
                    } catch (Exception e) {
                        this.chat("Tránh xa ta ra, đừng để ta nổi giận");
                        effectCharger();
                        angryPlayers.put(pl, 1);
                        break;
                    }
                }
            }
        }
    }

    private boolean isInListPlayersAttack(Player player) {
        for (Player pl : playersAttack) {
            if (player.equals(pl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void attack() {
        try {
            if (!charge()) {
                angry();
                Player pl = getPlayerAttack();

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(Util.nextInt(1, 4), this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(
                                pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0
                                        ? pl.location.y
                                        : pl.location.y - Util.nextInt(0, 50),
                                false
                        );
                    }

                    this.effectCharger();

                    if (Util.isTrue(1, 100) && this.nPoint.hp <= this.nPoint.hpMax / 2) {
                        this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, 3));
                        this.tangChiSo();
                    }

                    int dis = Util.getDistance(this, pl);

                    if (!this.isDie() && dis <= 100) {
                        this.location.x = pl.location.x;
                        this.location.y = pl.location.y;
                        effectCharger();
                    }

                    if (!this.isDie() && dis >= 1) {
                        this.location.x = pl.location.x;
                        this.location.y = pl.location.y;
                        this.playerSkill.skillSelect = this.getSkillById(Skill.KAMEJOKO);
                        this.playerSkill.skillSelect = this.getSkillById(Skill.ANTOMIC);
                        this.playerSkill.skillSelect = this.getSkillById(Skill.MASENKO);
                    }

                    try {
                        SkillService.gI().useSkill(this, pl, null, -1, null);
                    } catch (Exception e) {
                        Logger.logException(SuperBroly.class, e);
                    }

                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + plAttack.name + " động vào ta chỉ có chết.");
            this.angryPlayers.put(pl, 0);
            this.playersAttack.remove(pl);
            this.plAttack = null;
        }
    }

    @Override
    public void respawn() {
        super.respawn();
        this.plAttack = null;

        if (this.playersAttack != null) {
            this.playersAttack.clear();
        }

        if (this.angryPlayers != null) {
            this.angryPlayers.clear();
        }
    }

    protected boolean useSpecialSkill() {
        return false;
    }

    private void tangChiSo() {
        long hpMax = this.nPoint.hpMax;
        int rand = Util.nextInt(80, 100);
        hpMax = hpMax + hpMax / rand < 16_070_777 ? hpMax + hpMax / rand : 16_070_777;
        this.nPoint.hpMax = hpMax;
        this.nPoint.dame = hpMax / 100;
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        ChangeMapService.gI().exitMap(this.Detu);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        BossManager.gI().removeBoss(this);
    }
}