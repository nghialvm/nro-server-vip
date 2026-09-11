package QuanLiBoss.Manager;

import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import nro.player.Player;
import nro.services.Service;
import nro.skill.Skill;
import Utils.Util;
import java.util.Random;
import nro.player.Detu;
import nro.skill.SkillService;
import Utils.SkillUtil;
import consts.ConstPlayer;
import nro.map.Zone;

public class GasManager extends Boss {

    private long lastTimeBlame;

    public GasManager(Zone zone, int dame, int hp, int id) throws Exception {
        super(BossID.GasManager, new BossData(
                "Phân Thân Gas",
                ConstPlayer.TRAI_DAT,
                new short[]{2009, 2010, 2011, -1, -1, -1},
                Util.nextInt(60000, 100000),
                new long[]{Util.nextInt(500000000, 1000000000)},
                new int[]{199},
                new int[][]{
                    {Skill.KAMEJOKO, 7, Util.nextInt(500, 2000)},
                    {Skill.ANTOMIC, 7, Util.nextInt(500, 2000)},
                    {Skill.MASENKO, 7, Util.nextInt(500, 2000)},
                    {Skill.DE_TRUNG, Util.nextInt(1, 7), Util.nextInt(50000, 100000)},
                    {Skill.DICH_CHUYEN_TUC_THOI, Util.nextInt(1, 7), Util.nextInt(150000, 200000)},
                    {Skill.QUA_CAU_KENH_KHI, 7, Util.nextInt(50000, 100000)},
                    {Skill.SOCOLA, Util.nextInt(1, 7), Util.nextInt(50000, 100000)},
                    {Skill.TAI_TAO_NANG_LUONG, Util.nextInt(1, 7), Util.nextInt(600000, 1000000)},
                    {Skill.HUYT_SAO, Util.nextInt(1, 7), Util.nextInt(500000, 1000000)},
                    {Skill.LIEN_HOAN, 7, 1000}
                },
                new String[]{},
                new String[]{},
                new String[]{},
                86400
        ));
        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        int[] itemDos = new int[]{1595, 1596, 1597, 1598};
        int randomDo = new Random().nextInt(itemDos.length);
        if (Util.isTrue(50, 100)) {
            Service.gI().dropItemMap(this.zone,
                    Util.dasucmanh(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
        }
    }

    @Override
    public void active() {
        this.attack();
        super.active();

        if (System.currentTimeMillis() - lastTimeBlame > Util.nextInt(10000, 20000)) {
            this.chat("HaHaHa Ngươi Sẽ Phải Chiến Đấu Với Phân Thân Của Ta");
            this.chat("Mạnh Mẽ lên Nàoo");
            lastTimeBlame = System.currentTimeMillis();
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(5, 100)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);
            this.nPoint.subHP(damage);

            if (isDie()) {
                if (plAtt != null && !plAtt.isDeTu && !plAtt.isPetFollow) {
                    this.setDie(plAtt);
                    die(plAtt);
                } else if (plAtt instanceof Detu) {
                    this.setDie(((Detu) plAtt).master);
                    die(((Detu) plAtt).master);
                }
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

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

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
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}