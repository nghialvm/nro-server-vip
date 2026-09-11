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

public class BotDetu_6 {
             
    long lastTimeEatPea;
         
    public Bot_new bot;
    
    private long TimeUseSkillXD;
    
    private long TimeUseSkillNM;
    
    private long TimeUseSkillTD;
    
    private long TimeRunMapxayda;
    private long TimeRunMaptraidat;
    private long TimeRunMapnamec;
    
    boolean isTrue = false;
       
    public BotDetu_6(Bot_new b){
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
        if (this.bot.Detu == null && this.bot.type == 5) {
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
            } else if (Util.isTrue(40, 100)) {
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
            this.MapUp_28();
        } else if (power >= 23_000_000_000L && power < 26_000_000_000L) {
            this.MapUp_29();
        } else if (power >= 26_000_000_000L && power < 29_000_000_000L) {
            this.MapUp_30();
        } else if (power >= 29_000_000_000L && power < 32_000_000_000L) {
            this.MapUp_31();
        } else if (power >= 32_000_000_000L && power < 35_000_000_000L) {
            this.MapUp_32();
        } else if (power >= 35_000_000_000L && power < 40_000_000_000L) {
            this.MapUp_33();
        }
    }
    
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_12 = new int[]{1380 + Util.nextInt(-20, 20), 1180 + Util.nextInt(-20, 20), 1020 + Util.nextInt(-20, 20), 611 + Util.nextInt(0, 20)};
    public void MapUp_28(){
        Random random = new Random();
        int location = RandomLocation_x_12[random.nextInt(RandomLocation_x_12.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 66) {
            Zone zone = this.bot.getRandomZone(66);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 360);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_13 = new int[]{700 + Util.nextInt(-150, 150), 310 + Util.nextInt(-140, 140)};
    public void MapUp_29(){
        Random random = new Random();
        int location = RandomLocation_x_13[random.nextInt(RandomLocation_x_13.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 67) {
            Zone zone = this.bot.getRandomZone(67);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 720);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_14 = new int[]{250 + Util.nextInt(-20, 20), 490 + Util.nextInt(-20, 20), 750 + Util.nextInt(-20, 20)};
    public void MapUp_30(){
        Random random = new Random();
        int location = RandomLocation_x_14[random.nextInt(RandomLocation_x_14.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 73) {
            Zone zone = this.bot.getRandomZone(73);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 504);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_15 = new int[]{330 + Util.nextInt(-20, 20), 560 + Util.nextInt(-20, 20), 785 + Util.nextInt(-20, 20), 1035 + Util.nextInt(-20, 20), 1265 + Util.nextInt(-20, 20)};
    public void MapUp_31(){
        Random random = new Random();
        int location = RandomLocation_x_15[random.nextInt(RandomLocation_x_15.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 74) {
            Zone zone = this.bot.getRandomZone(74);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 336);
            }
        }
    }
    private int[] RandomLocation_x_16 = new int[]{390 + Util.nextInt(-20, 20), 650 + Util.nextInt(-20, 20), 930 + Util.nextInt(-20, 20), 1200 + Util.nextInt(-20, 20)};
    public void MapUp_32(){
        Random random = new Random();
        int location = RandomLocation_x_16[random.nextInt(RandomLocation_x_16.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 75) {
            Zone zone = this.bot.getRandomZone(75);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 336);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_17 = new int[]{443 + Util.nextInt(0, 20), 637 + Util.nextInt(-20, 0), 1019 + Util.nextInt(0, 20), 1232 + Util.nextInt(-20, 20)};
    public void MapUp_33(){
        Random random = new Random();
        int location = RandomLocation_x_17[random.nextInt(RandomLocation_x_17.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 76) {
            Zone zone = this.bot.getRandomZone(76);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 336);
            }
        }
    }
}




