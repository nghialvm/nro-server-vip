package nro.bot.New;

import nro.services.DetuService;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.UseItem;
import nro.services.PlayerService;
import Utils.Util;
import consts.ConstPlayer;
import java.util.Random;
import nro.map.Zone;
import nro.player.Detu;
import nro.player.NPoint;
import nro.skill.Skill;

public class BotDetu_5 {
             
    long lastTimeEatPea;
         
    public Bot_new bot;
    
    private long TimeUseSkillXD;
    
    private long TimeUseSkillNM;
    
    private long TimeUseSkillTD;
    
    private long TimeRunMapxayda;
    private long TimeRunMaptraidat;
    private long TimeRunMapnamec;
    
    boolean isTrue = false;
       
    public BotDetu_5(Bot_new b){
        this.bot = b;
    }
    
    private int[] BuffPea = new int[]{16_000, 32_000, 64_000, 128_000, 256_000};
         
    public void update(){
        Random random = new Random();
        int Opstion = BuffPea[random.nextInt(BuffPea.length)];
        if (this.bot.zone != null) {
            if (this.bot.effectSkill.isStone || this.bot.effectSkin.isDraburaFrost || this.bot.isDie()) {
                return;
            }
            MoveTo();
            FixmoveTo(this.bot.location.y);
        }
        if (!this.bot.isDie() && Util.canDoWithTime(lastTimeEatPea, Util.nextInt(30_000, 120_000))) {
            UseItem.gI().eatPeaBot(this.bot, Opstion);
            lastTimeEatPea = System.currentTimeMillis();
        }
        if (this.bot.Detu == null && this.bot.type == 4) {
            if (Util.isTrue(90, 100)) {
                DetuService.gI().createNormalBot(this.bot, NPoint.MAX_LIMIT);
            } else {
                DetuService.gI().createMabuBot(this.bot, NPoint.MAX_LIMIT);
            }
        }
        if (this.bot.Detu != null) {
            this.bot.Detu.changeStatus(Detu.PROTECT);
        }
        if (!isTrue && this.bot.Detu != null) {
            if (Util.isTrue(50, 100)) {
                this.bot.Detu.IdBot = 1;
            } else if (Util.isTrue(20, 100)) {
                this.bot.Detu.IdBot = 2;
            } else if (Util.isTrue(20, 100)) {
                this.bot.Detu.IdBot = 3;
            } else if (Util.isTrue(20, 100)) {
                this.bot.Detu.IdBot = 4;
            } else {
                this.bot.Detu.IdBot = 5;
            }
            isTrue = true;
        }
        if (this.bot.Detu != null && this.bot.Detu.IdBot == 1) {
            this.bot.Detu.nPoint.tlHutHp = 70;
            this.bot.Detu.nPoint.tlHutMp = 70;
        } else if (this.bot.Detu != null && this.bot.Detu.IdBot == 2) {
            this.bot.Detu.nPoint.tlHutHp = 30;
            this.bot.Detu.nPoint.tlHutMp = 30;
            this.bot.Detu.nPoint.tlHutHpMpXQ = 5;
        } else if (this.bot.Detu != null && this.bot.Detu.IdBot == 3) {
            this.bot.Detu.nPoint.tlHutHp = 20;
            this.bot.Detu.nPoint.tlHutMp = 20;
            this.bot.Detu.nPoint.tlHutHpMpXQ = 4;
        } else if (this.bot.Detu != null && this.bot.Detu.IdBot == 4) {
            this.bot.Detu.nPoint.tlHutHp = 10;
            this.bot.Detu.nPoint.tlHutMp = 10;
            this.bot.Detu.nPoint.tlHutHpMpXQ = 2;
        } else if (this.bot.Detu != null && this.bot.Detu.IdBot == 5) {
            this.bot.Detu.nPoint.dame = this.bot.Detu.nPoint.dameg + (this.bot.Detu.nPoint.dameg * 25 / 100);
            this.bot.Detu.nPoint.tlHutHp = 20;
            this.bot.Detu.nPoint.tlHutMp = 20;
        }
        switch (this.bot.gender) {
            case ConstPlayer.XAYDA:
                if (this.bot.playerSkill.skillSelect != null && !this.bot.isDie() && Util.canDoWithTime(TimeUseSkillXD, Util.nextInt(30_000, 60_000))) {
                    this.bot.useSkill(Skill.TAI_TAO_NANG_LUONG);
                    TimeUseSkillXD = System.currentTimeMillis();
                }
                break;
            case ConstPlayer.TRAI_DAT:
                if (this.bot.playerSkill.skillSelect != null && !this.bot.isDie() && Util.canDoWithTime(TimeUseSkillTD, Util.nextInt(30_000, 60_000))) {
                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                    TimeUseSkillTD = System.currentTimeMillis();
                }
                break;
            case ConstPlayer.NAMEC:
                if (this.bot.playerSkill.skillSelect != null && !this.bot.isDie() && Util.canDoWithTime(TimeUseSkillNM, Util.nextInt(30_000, 60_000))) {
                    this.bot.useSkill(Skill.TRI_THUONG);
                    TimeUseSkillNM = System.currentTimeMillis();
                }
                break;
        }
    }
    
    public void MoveTo() {
        switch (this.bot.gender) {
            case 0:
                if (Util.canDoWithTime(TimeRunMaptraidat, Util.nextInt(5_000, 60_000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + (Util.isTrue(1, 2) ? 1 : -1), this.bot.location.y);
                    TimeRunMaptraidat = System.currentTimeMillis();
                }
                break;
            case 1:
                if (Util.canDoWithTime(TimeRunMapnamec, Util.nextInt(5_000, 60_000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + (Util.isTrue(1, 2) ? 1 : -1), this.bot.location.y);
                    TimeRunMapnamec = System.currentTimeMillis();
                }
                break;
            case 2:
                if (Util.canDoWithTime(TimeRunMapxayda, Util.nextInt(5_000, 60_000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + (Util.isTrue(1, 2) ? 1 : -1), this.bot.location.y);
                    TimeRunMapxayda = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }
    
    public void FixmoveTo(int y) {
        if (this.bot.location.x < 200) {
            PlayerService.gI().playerMove(this.bot, Util.nextInt(220, 240), y);
        } else if (this.bot.location.x > 1300) {
            PlayerService.gI().playerMove(this.bot, Util.nextInt(1250, 1290), y);
        }
    }
    
    public void CheckPower() {
        long power = this.bot.nPoint.power;
        if (power >= 20_000_000_000L && power < 23_000_000_000L) {
            this.MapUp_22();
        } else if (power >= 23_000_000_000L && power < 26_000_000_000L) {
            this.MapUp_23();
        } else if (power >= 26_000_000_000L && power < 30_000_000_000L) {
            this.MapUp_24();
        } else if (power >= 30_000_000_000L && power < 35_000_000_000L) {
            this.MapUp_25();
        } else if (power >= 35_000_000_000L && power < 38_000_000_000L) {
            this.MapUp_26();
        } else if (power >= 38_000_000_000L && power < 40_000_000_000L) {
            this.MapUp_27();
        }
    }
    
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_12 = new int[]{790, 370, 580, 960, 450};
    public void MapUp_22(){
        Random random = new Random();
        int location = RandomLocation_x_12[random.nextInt(RandomLocation_x_12.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 70) {
            Zone zone = this.bot.getRandomZone(70);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, (this.bot.location.x == 790 ? 696 : this.bot.location.x == 370 ? 528 : this.bot.location.x == 580 ? 360 
                        : this.bot.location.x == 960 ? 360 : this.bot.location.x == 450 ? 192 : 192));
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_13 = new int[]{539, 350, 220, 395, 685};
    public void MapUp_23(){
        Random random = new Random();
        int location = RandomLocation_x_13[random.nextInt(RandomLocation_x_13.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 71) {
            Zone zone = this.bot.getRandomZone(71);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, (this.bot.location.x == 539 ? 456 : this.bot.location.x == 350 ? 168 : this.bot.location.x == 220 ? 360 
                        : this.bot.location.x == 395 ? 624 : this.bot.location.x == 685 ? 840 : 168));
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_14 = new int[]{420 + Util.nextInt(-20, 20), 740 + Util.nextInt(-20, 20), 1055 + Util.nextInt(-20, 20), 1300 + Util.nextInt(-20, 20)};
    public void MapUp_24(){
        Random random = new Random();
        int location = RandomLocation_x_14[random.nextInt(RandomLocation_x_14.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 72) {
            Zone zone = this.bot.getRandomZone(72);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 312);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_15 = new int[]{390 + Util.nextInt(-20, 20), 590 + Util.nextInt(-20, 20), 815 + Util.nextInt(-20, 20), 1030 + Util.nextInt(-20, 20), 1240 + Util.nextInt(-20, 20)};
    public void MapUp_25(){
        Random random = new Random();
        int location = RandomLocation_x_15[random.nextInt(RandomLocation_x_15.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 64) {
            Zone zone = this.bot.getRandomZone(64);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 312);
            }
        }
    }
    private int[] RandomLocation_x_16 = new int[]{580 + Util.nextInt(-80, 80), 970 + Util.nextInt(-80, 80)};
    public void MapUp_26(){
        Random random = new Random();
        int location = RandomLocation_x_16[random.nextInt(RandomLocation_x_16.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 65) {
            Zone zone = this.bot.getRandomZone(65);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 312);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_17 = new int[]{710, 900, 580, 700, 580};
    public void MapUp_27(){
        Random random = new Random();
        int location = RandomLocation_x_17[random.nextInt(RandomLocation_x_17.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 63) {
            Zone zone = this.bot.getRandomZone(63);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, (this.bot.location.x == 900 ? 144 : this.bot.location.x == 710 ? 144 : this.bot.location.x == 580 ? 288 
                        : this.bot.location.x == 700 ? 432 : this.bot.location.x == 580 ? 600 : 168));
            }
        }
    }
}




