package nro.boss.Anw.BossOfTheGangs;

import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBANBBH;
import QuanLiBoss.Manager.BossOfTheGangsManager;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstPlayer;
import consts.ConstRatio;
import java.util.List;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.clan.Clan;
import nro.effect.EffectSkillService;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import nro.skill.Skill;
import nro.skill.SkillService;


public class MabuClan extends Boss {

    private Clan clan;
    private long lastTimeSkill = System.currentTimeMillis();

    public MabuClan(Zone zone, Clan clan) throws Exception {
        super(PHOBANBBH, BossID.MABU_CLAN, new BossData(
                "Ma bư",
                ConstPlayer.TRAI_DAT,
                new short[]{297, 298, 299, -1, -1, -1},
                50_000,
                new long[]{250_000_000L},
                new int[]{165},
                generateSkills(Skill.DRAGON, Skill.DEMON, Skill.GALICK, Skill.KAMEJOKO, Skill.MASENKO, Skill.ANTOMIC),
                new String[]{},
                new String[]{
                    "|-1|Em ơi đừng khóc bóng tối trước mắt sẽ bắt em đi",
                    "|-1|Em ơi đừng lo Em ơi đừng cho tương lai vụt tắt",
                    "|-1|Sâu trong màu mắt có chút tiếc nuối phút cuối chỉ vì",
                    "|-1|Em đâu hề sai em đâu thể mãi để trái tim đau",
                    "|-1|Không còn tương lai, em cũng chẳng còn thương ai",
                    "|-1|Sau bao niềm đau em mong rằng con tim em dừng lại",
                    "|-1|Nỗi nhớ này lâu phai, nhốt em trong 1 lâu đài",
                    "|-1|Lâu đài của những cơn đau bất tận",
                    "|-1|Vì sao em phải khóc?",
                    "|-1|Có đáng để buồn đâu, tình yêu như cơn lốc thoáng phút chốc lướt qua thật mau.",
                    "|-1|Vì sao em phải khóc?",
                    "|-1|Có đáng để buồn đâu, rượu kề môi em nốc, thoáng phút chốc đã vơi u sầu"
                },
                new String[]{},
                60
        ));
        this.zone = zone;
        this.clan = clan;
    }

    private static int[][] generateSkills(int... skillIds) {
        int[][] skills = new int[skillIds.length * 7][3];
        int index = 0;
        for (int skillId : skillIds) {
            for (int level = 1; level <= 7; level++) {
                skills[index++] = new int[]{skillId, level, 800};
            }
        }
        return skills;
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(100, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (plAtt != null && plAtt.idNRNM != -1) {
                return 1;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 2);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (plAtt != null && plAtt.clan != null && plAtt.clan.BossOfTheGang != null && plAtt.clanMember != null) {
                plAtt.clanMember.memberDamage += damage;
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
    public void effectCharger() {
        if (Util.isTrue(50, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }

    @Override
    public void reward(Player plKill) {
        int numPlayers = this.zone.getNumOfPlayers();
        for (int i = 0; i < numPlayers; i++) {
            int distance = (i + 1) * Util.nextInt(-60, 60);
            int offsetX = Util.isTrue(50, 100) ? distance : -distance;
            dropCt(offsetX);
        }
        for (int i = 0; i < numPlayers; i++) {
            dropItem();
        }
        if (plKill.clan != null && plKill.clan.BossOfTheGang != null) {
            plKill.clan.BossOfTheGang.BossDead = true;
        }
        if (plKill.clan != null && plKill.clan.BossOfTheGang != null && plKill.clanMember != null) {
            plKill.clan.rewardTopDamagers(plKill);
        }
    }

    private void dropCt(int x) {
        ItemMap it = new ItemMap(zone, 578, 1, this.location.x + x,
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
        List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) it.itemTemplate.id);
        if (!ops.isEmpty()) {
            it.options = ops;
        }
        it.addOptionParam(93, 15);
        Service.gI().dropItemMap(this.zone, it);
    }

    private void dropItem() {
        for (int i = 0; i < Util.nextInt(3, 4); i++) {
            ItemMap it = new ItemMap(zone, 1150, 1, this.location.x + Util.nextInt(-200, 200),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            it.addOptionParam(86, 0);
            it.addOptionParam(30, 0);
            Service.gI().dropItemMap(this.zone, it);
        }
        for (int i = 0; i < Util.nextInt(3, 4); i++) {
            ItemMap it = new ItemMap(zone, 1151, 1, this.location.x + Util.nextInt(-200, 200),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            it.addOptionParam(86, 0);
            it.addOptionParam(30, 0);
            Service.gI().dropItemMap(this.zone, it);
        }
        for (int i = 0; i < Util.nextInt(3, 4); i++) {
            ItemMap it = new ItemMap(zone, 1152, 1, this.location.x + Util.nextInt(-200, 200),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            it.addOptionParam(86, 0);
            it.addOptionParam(30, 0);
            Service.gI().dropItemMap(this.zone, it);
        }
        for (int i = 0; i < Util.nextInt(3, 4); i++) {
            ItemMap it = new ItemMap(zone, 1153, 1, this.location.x + Util.nextInt(-200, 200),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            it.addOptionParam(86, 0);
            it.addOptionParam(30, 0);
            Service.gI().dropItemMap(this.zone, it);
        }
        for (int i = 0; i < Util.nextInt(3, 4); i++) {
            ItemMap it = new ItemMap(zone, 1154, 1, this.location.x + Util.nextInt(-200, 200),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            it.addOptionParam(86, 0);
            it.addOptionParam(30, 0);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 910, 384);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void afk() {
        Player pl = getPlayerAttack();
        if (pl == null || pl.isDie()) {
            return;
        }
        if (Util.getDistance(this, pl) <= 200) {
            this.changeStatus(BossStatus.ACTIVE);
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 500) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                goToPlayer(pl, false);
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (System.currentTimeMillis() - lastTimeSkill >= 5000) {
                        lastTimeSkill = System.currentTimeMillis();
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * 200),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        }
                    } else {
                        this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)), pl.location.y);
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
            }
        }
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
        BossOfTheGangsManager.gI().removeBoss(this);
        this.dispose();
    }
}