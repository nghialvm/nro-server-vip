package nro.boss.AnwDestronGas;

import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBANKGHD;
import QuanLiBoss.Manager.GasDestroyManager;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.SkillUtil;
import nro.skill.Skill;
import Utils.Util;
import nro.clan.Clan;
import consts.ConstPlayer;
import consts.ConstRatio;
import models.Item.ItemOption;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.skill.SkillService;


public class Hatchiyack extends Boss {

    private final int level;
    private Clan clan;

    public Hatchiyack(Zone zone, Clan clan, int level, long dame, long hp) throws Exception {
        super(PHOBANKGHD, BossID.HATCHIYACK, new BossData(
                "Hatchiyack",
                ConstPlayer.TRAI_DAT,
                new short[]{639, 640, 641, -1, -1, -1},
                dame,
                new long[]{hp},
                new int[]{148},
                new int[][]{
                    {Skill.DRAGON, 1, 1000},
                    {Skill.TAI_TAO_NANG_LUONG, 1, 3600000}
                },
                new String[]{
                    "|-1|Các ngươi dám hạ sư phụ ta",
                    "|-1|Ta sẽ tiêu diệt hết các ngươi"
                },
                new String[]{
                    "|-1|Đại bác báo thù...",
                    "|-1|Heyyyyyyyy Yaaaaa"
                },
                new String[]{
                    "|-1|Các ngươi khó mà rời khỏi nơi đây"
                },
                60
        ));
        this.zone = zone;
        this.level = level;
        this.clan = clan;
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.level + 10, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (plAtt != null && plAtt.idNRNM != -1) {
                return 1;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage + Util.nextInt(-200 * this.level, 0));
            damage -= damage / 100 * (this.level / 5);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
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
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = getPlayerAttack();

                if (pl == null || pl.isDie()) {
                    return;
                }

                this.effectCharger();
                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0
                                            ? pl.location.y
                                            : pl.location.y - Util.nextInt(0, 70)
                            );
                        } else {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0
                                            ? pl.location.y
                                            : pl.location.y - Util.nextInt(0, 50)
                            );
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

    @Override
    public void effectCharger() {
        if (Util.isTrue(50, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }

    @Override
    public void reward(Player plKill) {
        dropCt(0);

        for (int i = 0; i < this.zone.getNumOfPlayers(); i++) {
            int x = (i + 1) * 40;
            dropCt(x);
            dropCt(-x);
        }
    }

    private void dropCt(int x) {
        try {
            ItemMap it = new ItemMap(
                    zone,
                    729,
                    1,
                    this.location.x + x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    -1
            );

            it.options.clear();

            int ParamMax = 10 + (level / 3) - (level > 55 ? Util.nextInt(level / 10) : 0);

            if (ParamMax < 3) {
                ParamMax = 3;
            }

            int ParamMin = ParamMax - 3;

            if (ParamMin < 3) {
                ParamMin = 3;
            }

            int hsd = Util.nextInt(ParamMin, ParamMax);

            it.options.add(new ItemOption(50, Util.nextInt(ParamMin, ParamMax)));
            it.options.add(new ItemOption(77, Util.nextInt(ParamMin, ParamMax)));
            it.options.add(new ItemOption(103, Util.nextInt(ParamMin, ParamMax)));
            it.options.add(new ItemOption(5, Util.nextInt(ParamMin, ParamMax)));
            it.options.add(new ItemOption(93, hsd > 21 ? 21 : hsd));
            it.options.add(new ItemOption(30, 0));

            Service.gI().dropItemMap(this.zone, it);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 480, 295);
        this.moveTo(480, 480);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }

        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        if (clan != null && clan.KhiGasHuyDiet != null) {
            clan.KhiGasHuyDiet.hatchiyatchDead = true;
        }

        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        GasDestroyManager.gI().removeBoss(this);
        this.dispose();
    }
}