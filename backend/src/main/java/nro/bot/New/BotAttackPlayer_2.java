package nro.bot.New;

import nro.services.Fun.ChangeMapService;
import nro.services.Fun.UseItem;
import nro.services.PlayerService;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nro.map.Zone;
import nro.player.Detu;
import nro.player.Player;
import nro.skill.SkillService;

public class BotAttackPlayer_2 {
             
    long lastTimeEatPea;
         
    public Bot_new bot;
    
    public Player playerattack;
    
    protected long lastTimeTargetPlayer;
    protected int timeTargetPlayer;
    protected Player playerTarger;
    protected long lastTimeAttack;
    
    private long lastTimeMove;

    private int timeMove;
    
    private long TimeRunMapxayda;
    private long TimeRunMaptraidat;
    private long TimeRunMapnamec;
    
    boolean StartGame;    
        
    public BotAttackPlayer_2(boolean Start){
        this.StartGame = Start;
    }
   
    public BotAttackPlayer_2(BotAttackPlayer_2 dt){
        this.StartGame = dt.StartGame;
    }    
    
    private int[] BuffPea = new int[]{16_000, 32_000, 64_000, 128_000, 256_000};
    public void update() {
        long power = this.bot.nPoint.power;
        Random random = new Random();
        int Opstion = BuffPea[random.nextInt(BuffPea.length)];
        if (!this.bot.isDie() && !this.bot.effectSkill.isStone) {
            if (power < 40_000_000_000L) {
                this.MapUp_1();
            } else {
                this.MapUp_2();
            }
            if (this.bot.zone != null) {
                if (this.bot.effectSkill.isStone || this.bot.effectSkin.isDraburaFrost || this.bot.isDie()) {
                    return;
                }
                attack();
                MoveTo();
                FixmoveTo(this.bot.location.y);
            }
        }
        if (!this.bot.isDie() && Util.canDoWithTime(lastTimeEatPea, Util.nextInt(30_000, 120_000))) {
            UseItem.gI().eatPeaBot(this.bot, Opstion);
            lastTimeEatPea = System.currentTimeMillis();
        }
    }
    
    public void CheckPower() {
        long power = this.bot.nPoint.power;
        if (power < 40_000_000_000L) {
            this.MapUp_1();
        } else {
            this.MapUp_2();
        }
    }
    
    public void MoveTo() {
        switch (this.bot.gender) {
            case 0:
                if (Util.canDoWithTime(TimeRunMaptraidat, Util.nextInt(1_000, 3_000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + (Util.isTrue(1, 2) ? 40 : -40), this.bot.location.y);
                    TimeRunMaptraidat = System.currentTimeMillis();
                }
                break;
            case 1:
                if (Util.canDoWithTime(TimeRunMapnamec, Util.nextInt(1_000, 3_000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + (Util.isTrue(1, 2) ? 40 : -40), this.bot.location.y);
                    TimeRunMapnamec = System.currentTimeMillis();
                }
                break;
            case 2:
                if (Util.canDoWithTime(TimeRunMapxayda, Util.nextInt(1_000, 3_000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + (Util.isTrue(1, 2) ? 40 : -40), this.bot.location.y);
                    TimeRunMapxayda = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }
    
    public Player getPlayerAttack() {
        List<Player> plNotVoHinh = new ArrayList();
        for (Player pl : this.bot.zone.getNotBosses()) {
            if (pl.cFlag != this.bot.cFlag) {
                plNotVoHinh.add(pl);
            }
        }
        for (Player pl : this.bot.zone.getPlayers()) {
            if (!pl.equals(this.bot)) {
                plNotVoHinh.add(pl);
            }
        }
        if (!plNotVoHinh.isEmpty()) {
            return plNotVoHinh.get(Util.nextInt(0, plNotVoHinh.size() - 1));
        }
        return null;
    }
    
    public void attack() {
        if (this.bot.effectSkill.isCharging) {
            return;
        }
        if (Util.canDoWithTime(this.lastTimeAttack, 1)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    if (Util.canDoWithTime(lastTimeMove, timeMove)) {
                        Player plRand = getAttackPlayer();
                        if (plRand != null) {
                            PlayerService.gI().playerMove(plRand, this.bot.location.x, this.bot.location.y);
                            this.lastTimeMove = System.currentTimeMillis();
                            this.timeMove = Util.nextInt(5000, 30000);
                        }
                    }
                    return;
                }
                this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(Util.nextInt(0, this.bot.playerSkill.skills.size() - 1));
                int dis = Util.getDistance(this.bot, pl);
                if (!this.bot.isDie()) {
                    if (dis > 450) {
                        if (pl.cFlag != 0) {
                            this.bot.move(pl.location.x - 24, pl.location.y);
                        }
                    } else if (dis > 100) {
                        if (pl.cFlag != 0) {
                            int dir = (this.bot.location.x - pl.location.x < 0 ? 1 : -1);
                            int move = Util.nextInt(50, 100);
                            this.bot.move(this.bot.location.x + (dir == 1 ? move : -move), pl.location.y);
                        }
                    } else {
                        if (pl.cFlag != 0) {
                            if (Util.isTrue(30, 100)) {
                                int move = Util.nextInt(50);
                                this.bot.move(pl.location.x + (Util.nextInt(0, 1) == 1 ? move : -move), this.bot.location.y);
                            }
                        }
                        SkillService.gI().useSkill(this.bot, pl, null, -1, null);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
    
    public Player getAttackPlayer() {
        if (this.bot.zone == null) {
            return null;
        }
        if (this.playerTarger != null && (this.playerTarger.isDie() || !this.bot.zone.equals(this.playerTarger.zone))) {
            this.playerTarger = null;
        }
        if (this.playerTarger == null || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
            this.playerTarger = this.bot.zone.getRandomPlayerInMap();
            this.lastTimeTargetPlayer = System.currentTimeMillis();
            this.timeTargetPlayer = Util.nextInt(5000, 7000);
        }
        if (this.playerTarger != null && this.playerTarger.isDeTu && ((Detu) this.playerTarger).master != null && ((Detu) this.playerTarger).master.equals(this.bot)) {
            this.playerTarger = null;
        }
        
        return this.playerTarger;
    }
        
    public void FixmoveTo(int y) {
        if (this.bot.location.x < 50) {
            PlayerService.gI().playerMove(this.bot, Util.nextInt(50, 90), y);
        } else if (this.bot.zone.map.mapId == 7 ? this.bot.location.x > 1200 : this.bot.location.x > 1400) {
            PlayerService.gI().playerMove(this.bot, this.bot.zone.map.mapId == 7 ? Util.nextInt(1150, 1190) : Util.nextInt(1350, 1390), y);
        }
    }    
    
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_1 = new int[]{155 + Util.nextInt(1, 1000)};
    private void MapUp_1(){
        Random random = new Random();
        int location = RandomLocation_x_1[random.nextInt(RandomLocation_x_1.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 7) {
            Zone zone = this.bot.getRandomZone(7);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 432);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_2 = new int[]{1200 + Util.nextInt(-150, 150)};
    private void MapUp_2(){
        Random random = new Random();
        int location = RandomLocation_x_2[random.nextInt(RandomLocation_x_2.length)];
        if (this.bot.zone != null && this.bot.zone.map.mapId != 43) {
            Zone zone = this.bot.getRandomZone(43);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, location, 432);
            }
        }
    }
}




