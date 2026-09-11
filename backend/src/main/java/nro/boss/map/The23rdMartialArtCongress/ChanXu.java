package nro.boss.map.The23rdMartialArtCongress;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.PHOBAN;
import QuanLiBoss.BossesData;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstRatio;
import models.Item.ItemTimeService;
import nro.skill.Skill;
import nro.skill.SkillService;

public class ChanXu extends The23rdMartialArtCongress {

    private long timeChoang;

    public ChanXu(Player player) throws Exception {
        super(PHOBAN, BossID.CHAN_XU, BossesData.CHAN_XU);
        this.playerAtt = player;
    }

    @Override
    public void attack() {
        try {
            if (Util.canDoWithTime(timeJoinMap, 10000)) {
                if (playerAtt != null
                        && playerAtt.location != null
                        && playerAtt.zone != null
                        && this.zone != null
                        && this.zone.equals(playerAtt.zone)) {

                    if (this.isDie()) {
                        return;
                    }

                    if (Util.isTrue(1, 5) && Util.canDoWithTime(timeChoang, 10000)) {
                        int time = Util.nextInt(1, 5);
                        EffectSkillService.gI().startStun(playerAtt, System.currentTimeMillis(), time * 1000);
                        ItemTimeService.gI().sendItemTime(playerAtt, 3779, time);

                        String[] text = {
                            "Đứng hình",
                            "Nhất dương chỉ"
                        };
                        this.chat(text[Util.nextInt(2)]);

                        timeChoang = System.currentTimeMillis();
                    }

                    if (playerAtt.effectSkill.isStun) {
                        this.playerSkill.skillSelect = this.getSkillById(Skill.MASENKO);
                        SkillService.gI().useSkill(this, playerAtt, null, -1, null);
                    } else {
                        this.playerSkill.skillSelect = this.getSkillById(Skill.DRAGON);
                        SkillService.gI().useSkill(this, playerAtt, null, -1, null);
                    }

                    if (this.nPoint != null && playerAtt.effectSkill.isStun) {
                        this.nPoint.crit = 100;
                    } else {
                        this.nPoint.crit = 0;
                    }

                    this.playerSkill.skillSelect = this.playerSkill.skills.get(
                            Util.nextInt(0, this.playerSkill.skills.size() - 1)
                    );

                    if (Util.getDistance(this, playerAtt) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(
                                    playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0
                                            ? playerAtt.location.y
                                            : playerAtt.location.y - Util.nextInt(0, 50),
                                    false
                            );
                        }

                        this.playerSkill.skillSelect = this.getSkillById(Skill.DRAGON);
                        SkillService.gI().useSkill(this, playerAtt, null, -1, null);
                        checkPlayerDie(playerAtt);
                    } else {
                        goToPlayer(playerAtt, false);
                    }
                } else {
                    this.leaveMap();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}