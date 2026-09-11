package nro.boss.map.The23rdMartialArtCongress;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.PHOBAN;
import QuanLiBoss.BossesData;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstRatio;
import models.Item.ItemTimeService;
import nro.skill.SkillService;

public class Locopo extends The23rdMartialArtCongress {

    public Locopo(Player player) throws Exception {
        super(PHOBAN, BossID.LOCOPO, BossesData.LOCOPO);
        this.playerAtt = player;
    }

    private long timechoang;
    public boolean transformed;

    @Override
    public void update() {
        super.update();

        if (!isDie()) {
            if (!transformed && nPoint.hp <= nPoint.hpMax / 2) {
                transformed = true;
                EffectSkillService.gI().SendEffectBienHinhorUseItem(this);
                Service.gI().Send_Caitrang(this);
                Service.getInstance().chat(playerAtt, "Thật khủng khiếp!");
            }
        }
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

                    if (Util.isTrue(1, 5) && Util.canDoWithTime(timechoang, 10000)) {
                        int time = Util.nextInt(1, 3);
                        EffectSkillService.gI().startStun(playerAtt, System.currentTimeMillis(), time * 1000);
                        ItemTimeService.gI().sendItemTime(playerAtt, 3779, time);
                        Service.getInstance().chat(this, "Định thân!");
                        timechoang = System.currentTimeMillis();
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