package nro.boss.AnwRedRibbonHQ;



import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBANDT;
import QuanLiBoss.Manager.RedRibbonHQManager;
import nro.services.Fun.ChangeMapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstPlayer;
import consts.ConstRatio;
import nro.map.ItemMap;
import nro.map.Zone;

public class TrungUyThep extends Boss {

    private long lastTimeMove;

    public TrungUyThep(Zone zone, long dame, long hp) throws Exception {
        super(PHOBANDT, BossID.TRUNG_UY_THEP, new BossData(
                "Trung úy Thép",
                ConstPlayer.NAMEC,
                new short[]{129, 130, 131, -1, -1, -1},
                dame,
                new long[]{hp},
                new int[]{55},
                new int[][]{
                    {Skill.MASENKO, 7, Util.nextInt(800, 1100)}
                },
                new String[]{},
                new String[]{
                    "|-1|Nếu bọn mi muốn lên tiếp tầng lầu trên",
                    "|-1|Phải bước qua xác chết của ta đã"
                },
                new String[]{},
                60
        ));
        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill != null && Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    Util.nextInt(14, 16),
                    10,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);

            ItemMap vang = new ItemMap(
                    this.zone,
                    190,
                    31000,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, vang);

            ItemMap vang2 = new ItemMap(
                    this.zone,
                    190,
                    31000,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, vang2);

            if (Util.isTrue(70, 100)) {
                ItemMap bdkb = new ItemMap(
                        this.zone,
                        611,
                        10,
                        this.location.x,
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                        plKill.id
                );
                Service.gI().dropItemMap(this.zone, bdkb);
            }
        }
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (plAtt != null && !SkillUtil.isUseSkillDam(plAtt)) {
            return super.injured(plAtt, damage, piercing, isMobAttack);
        }

        damage = damage / 100;

        if (damage <= 0) {
            damage = 1;
        }

        if (Util.isTrue(40, 110)) {
            ItemMap vang = new ItemMap(
                    this.zone,
                    Util.nextInt(188, 190),
                    Util.nextInt(20000, 31000),
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    -1
            );
            Service.getInstance().dropItemMap(this.zone, vang);
        }

        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 884, 312);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        Service.gI().setPos(this, 884, 312);
    }

    @Override
    public void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            int x2 = this.location.x + (dir == 1 ? move : -move);

            x2 = x2 < 640 ? 640 : x2;
            x2 = x2 > 980 ? 980 : x2;
            x2 = x < 220 ? x : x2;

            PlayerService.gI().playerMove(this, x2, getY(x));
        } else {
            Service.gI().setPos(this, x, y);
        }
    }

    @Override
    public void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    @Override
    public void attack() {
        try {
            Player playerAtt = getPlayerAttack();

            if (playerAtt == null || playerAtt.isDie() || playerAtt.location.x < 640 || playerAtt.location.x > 980) {
                if (Util.canDoWithTime(lastTimeMove, 1500)) {
                    lastTimeMove = System.currentTimeMillis();
                    goToXY(884, 312, false);
                }
                return;
            }

            if (playerAtt.location != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone)) {
                if (this.isDie()) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, playerAtt) <= this.getRangeCanAttackWithSkillSelect()) {
                    int x = playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80));

                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(x, getY(x), false);
                    }

                    if (playerAtt.location.y < 220) {
                        return;
                    }

                    SkillService.gI().useSkill(this, playerAtt, null, -1, null);
                    checkPlayerDie(playerAtt);
                } else {
                    goToPlayer(playerAtt, false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getY(int x) {
        if (x < 638 || x > 966) {
            return 240;
        } else if (x < 707) {
            return 264;
        } else if (x > 949) {
            return 288;
        }
        return 312;
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
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }
}