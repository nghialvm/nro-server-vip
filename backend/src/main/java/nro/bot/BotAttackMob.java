package nro.bot;

import nro.services.Fun.UseItem;
import nro.mob.Mob;
import nro.services.PlayerService;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.Util;
import java.util.Random;
import consts.ConstPlayer;
import nro.effect.EffectMapService;

public class BotAttackMob {
        
    private Mob mAttack;
         
    public long lastTimeChanM;
        
    long lastTimeEatPea;
     
    public Bot bot;
    
    private long TimeUseSkillTraiDat;
    private long TimeUseSkillTraiDat2;
    private long TimeUseSkillTraiDat3;
    private long TimeUseSkillTraiDat4;
    
    private long TimeUseSkillNamec;
    private long TimeUseSkillNamec2;
    private long TimeUseSkillNamec3;
    private long TimeUseSkillNamec4;
    
    private long TimeUseSkillXayda;
    private long TimeUseSkillXayda2;
    private long TimeUseSkillXayda3;
    private long TimeUseSkillXayda4;
   
    public BotAttackMob(Bot b){
        this.bot = b;
    }
     
    private int[] BuffPea = new int[]{16_000, 32_000, 64_000, 128_000, 256_000};
    public void update(){
        AutoUpdate();
        Random random = new Random();
        int Opstion = BuffPea[random.nextInt(BuffPea.length)];
        this.Attack();
        this.chanGeMap();
        if (!this.bot.isDie() && Util.canDoWithTime(lastTimeEatPea, Util.nextInt(30_000, 300_000))) {
            UseItem.gI().eatPeaBot(this.bot, Opstion);
            lastTimeEatPea = System.currentTimeMillis();
        }
    }
     
    public void GetMobAttack(){
        if (this.bot.zone != null && !this.bot.zone.mobs.isEmpty()) {
            if (this.mAttack == null || this.mAttack.isDie()) {
                mAttack = this.bot.zone.mobs.get(new Random().nextInt(this.bot.zone.mobs.size()));
            }
        }
    }
          
    public void Attack() {
        if (!this.bot.isDie()) {
            this.GetMobAttack();
            if (Util.isTrue(90, 100)) {
                this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(0);
            } else {
                if (this.bot.nPoint.power > 200_000) {
                    this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(1);
                }
            }
            if (this.mAttack != null) {
                if (this.bot.UseLastTimeSkill()) {
                    switch (this.bot.gender) {
                        case ConstPlayer.XAYDA:
                            if (this.bot.nPoint.power > 50_000_000) {
                                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(TimeUseSkillXayda, Util.nextInt(180000, 240000))) {
                                    this.bot.useSkill(Skill.BIEN_KHI);
                                    TimeUseSkillXayda = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(TimeUseSkillXayda2, Util.nextInt(240000, 300000))) {
                                    this.bot.useSkill(Skill.BIEN_KHI);
                                    TimeUseSkillXayda2 = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(TimeUseSkillXayda3, Util.nextInt(300000, 360000))) {
                                    this.bot.useSkill(Skill.BIEN_KHI);
                                    TimeUseSkillXayda3 = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(TimeUseSkillXayda4, Util.nextInt(360000, 420000))) {
                                    this.bot.useSkill(Skill.BIEN_KHI);
                                    TimeUseSkillXayda4 = System.currentTimeMillis();
                                }
                            }
                            break;
                        case ConstPlayer.TRAI_DAT:
                            if (this.bot.nPoint.power > 50_000_000) {
                                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(TimeUseSkillTraiDat, Util.nextInt(15000, 20000))) {
                                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                                    TimeUseSkillTraiDat = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(TimeUseSkillTraiDat2, Util.nextInt(20000, 25000))) {
                                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                                    TimeUseSkillTraiDat2 = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(TimeUseSkillTraiDat3, Util.nextInt(25000, 30000))) {
                                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                                    TimeUseSkillTraiDat3 = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(TimeUseSkillTraiDat4, Util.nextInt(30000, 40000))) {
                                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                                    TimeUseSkillTraiDat4 = System.currentTimeMillis();
                                }
                            }
                            break;
                        case ConstPlayer.NAMEC:
                            if (this.bot.nPoint.power > 50_000_000) {
                                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(TimeUseSkillNamec, Util.nextInt(180_000, 200_000))) {
                                    this.bot.useSkill(Skill.DE_TRUNG);
                                    TimeUseSkillNamec = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(TimeUseSkillNamec2, Util.nextInt(250_000, 300_000))) {
                                    this.bot.useSkill(Skill.DE_TRUNG);
                                    TimeUseSkillNamec2 = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(TimeUseSkillNamec3, Util.nextInt(350_000, 400_000))) {
                                    this.bot.useSkill(Skill.DE_TRUNG);
                                    TimeUseSkillNamec3 = System.currentTimeMillis();
                                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(TimeUseSkillNamec4, Util.nextInt(450_000, 500_000))) {
                                    this.bot.useSkill(Skill.DE_TRUNG);
                                    TimeUseSkillNamec4 = System.currentTimeMillis();
                                }
                            }
                            break;
                    }
                }
            }
        }
    }
            
    public void AutoUpdate() {
        if (this.bot.effectSkill.isStone || this.bot.effectSkin.isDraburaFrost || this.bot.isDie() || this.mAttack == null) {
            return;
        }
        
        int distance = Util.getDistance(this.bot, this.mAttack);
        int direction = (this.bot.location.x < this.mAttack.location.x) ? 1 : -1;

        moveToTarget(direction, distance);
        attackIfClose(distance);
        autoHealIfStrong();
    }

    private void moveToTarget(int direction, int distance) {
        int moveStep = (distance > 30) ? 40 : (Util.isTrue(20, 100) ? Util.nextInt(3, 5) : 0);
        
        int newX = this.bot.location.x + (direction == 1 ? moveStep : -moveStep);
        int newY = this.bot.zone.map.yPhysicInTopBot(this.bot.location.x, this.mAttack.location.y);
        this.bot.move(this.bot, newX, newY);
    }

    private void attackIfClose(int distance) {
        if (distance < 40) {
            SkillService.gI().useSkill(this.bot, null, this.mAttack, -1, null);
        }
    }

    private void autoHealIfStrong() {
        if (this.bot.nPoint.power <= 50_000_000) {
            return;
        }
        int healRatioHp = this.bot.OptionHut;
        int healRatioMp = this.bot.OptionHut;
        int hpRestore = (int) (this.bot.nPoint.dame * healRatioHp / 100);
        int mpRestore = (int) (this.bot.nPoint.dame * healRatioMp / 100);

        if (this.bot.zone != null && !this.bot.zone.mobs.isEmpty()
            && hpRestore > 0 && mpRestore > 0 && healRatioHp > 0 && healRatioMp > 0) {
            EffectMapService.gI().sendEffectMapToAllInMap(this.bot, 37, 3, 1,
                mAttack.location.x, mAttack.location.y, -1);
        }
        PlayerService.gI().hoiPhuc(this.bot, hpRestore, mpRestore);
    }
                
    public void chanGeMap(){
        if (this.lastTimeChanM < ((System.currentTimeMillis() - 300_000) -  new Random().nextInt(300_000))) {
            this.bot.joinMap();
        }
    }
}