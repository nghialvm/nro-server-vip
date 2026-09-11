package nro.bot;

import QuanLiBoss.Boss;
import QuanLiBoss.Manager.BossManager;
import nro.services.Fun.ChangeMapService;
import nro.services.PlayerService;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.Util;
import java.util.Random;
import consts.ConstPlayer;
import nro.effect.EffectMapService;
import nro.mob.Mob;

public class BotAttackBoss {
    
    public Bot bot;
     
    public Boss bossAttack;
     
    public long lastTimeSkill1;
    
    private long TimeUseSkillBienKhi;
    private long TimeUseSkillBienKhi2;
    private long TimeUseSkillBienKhi3;
    private long TimeUseSkillBienKhi4;
    
    private long TimeUseSkillTroi;
    private long TimeUseSkillTroi2;
    private long TimeUseSkillTroi3;
    private long TimeUseSkillTroi4;
    
    private long TimeUseSkillThaiDuongHaSan;
    private long TimeUseSkillThaiDuongHaSan2;
    private long TimeUseSkillThaiDuongHaSan3;
    private long TimeUseSkillThaiDuongHaSan4;
    
    private long LastTimeUseSkillKhienForEarth;
    private long LastTimeUseSkillKhienForEarth2;
    private long LastTimeUseSkillKhienForEarth3;
    private long LastTimeUseSkillKhienForEarth4;
    
    private long LastTimeUseSkillKhienForNamec;
    private long LastTimeUseSkillKhienForNamec2;
    private long LastTimeUseSkillKhienForNamec3;
    private long LastTimeUseSkillKhienForNamec4;
    
    private long LastTimeUseSkillDETRUNG;
    private long LastTimeUseSkillDETRUNG2;
    private long LastTimeUseSkillDETRUNG3;
    private long LastTimeUseSkillDETRUNG4;
    
    private long LastTimeUseSkillKillBot;
    private long LastTimeUseSkillKillBot2;
    private long LastTimeUseSkillKillBot3;
     
    public BotAttackBoss(Bot b){
        this.bot = b;
    }
     
    public void update() {
        this.SanBot();
        this.CheckLocation();
    }
    
    public void CheckLocation() {
        if (this.bot.zone != null) {
            if (this.bot.isBot && (this.bot.location.x > this.bot.zone.map.mapWidth || this.bot.location.x < 100 || this.bot.location.y > this.bot.zone.map.mapHeight || this.bot.location.y < 100)) {
                PlayerService.gI().playerMove(this.bot, Util.nextInt(100, 800), Util.nextInt(100, 200));
            }
        }
    }
     
    public boolean isMap(int mapId){
        return (mapId == 6 || mapId == 80 || mapId == 68 || mapId == 69 || mapId == 70 || mapId == 71 || mapId == 63 || mapId == 64 || mapId == 65 || mapId == 66 || mapId == 67 || mapId == 72 ||
                mapId == 74 || mapId == 75 || mapId == 76 || mapId == 77 || mapId == 92 || mapId == 93 || mapId == 100 || mapId == 103 || mapId == 98 || mapId == 97 || mapId == 99 ||
                mapId == 104 || mapId == 108 || mapId == 109 || mapId == 110 || mapId == 206 || mapId == 155);
    }
    
    public void GetBoss(int status){
        if (this.bossAttack == null || this.bossAttack.isDie()){
            this.bossAttack = BossManager.gI().getBosses().get(new Random().nextInt(BossManager.gI().getBosses().size()));
            if (this.bossAttack.zone != null) {
            boolean bosAction = (!this.bossAttack.isDie() && this.isMap(this.bossAttack.zone.map.mapId) && !this.bossAttack.zone.isFullPlayer() && this.bossAttack.zone.mobs.size() >= 1);
            if (bosAction){
                ChangeMapService.gI().goToMap(this.bot, this.bossAttack.zone);
                this.bot.zone.load_Me_To_Another(this.bot);
            }
            if (!bosAction && status < 10){
                this.bossAttack = null;
                this.GetBoss(status + 1);
            } else if (!bosAction) {
                BotManager.gI().bot.remove(this.bot);
                ChangeMapService.gI().exitMap(this.bot);
                this.bossAttack = null;
            }
            }
        }
    }
     
    public void GetSkil() {
        if (!this.bot.isDie()) {
        if (Util.isTrue(80 , 100)) {
            this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(0);
        } else {
            this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(1);
        }
        switch (this.bot.gender) {
            case ConstPlayer.XAYDA:
                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(TimeUseSkillBienKhi, Util.nextInt(180000, 240000))) {
                    this.bot.useSkill(Skill.BIEN_KHI);
                    TimeUseSkillBienKhi = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(TimeUseSkillBienKhi2, Util.nextInt(240000, 300000))) {
                    this.bot.useSkill(Skill.BIEN_KHI);
                    TimeUseSkillBienKhi2 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(TimeUseSkillBienKhi3, Util.nextInt(300000, 360000))) {
                    this.bot.useSkill(Skill.BIEN_KHI);
                    TimeUseSkillBienKhi3 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(TimeUseSkillBienKhi4, Util.nextInt(360000, 420000))) {
                    this.bot.useSkill(Skill.BIEN_KHI);
                    TimeUseSkillBienKhi4 = System.currentTimeMillis();
                }
                if (this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(TimeUseSkillTroi, Util.nextInt(20000, 25000))) {
                    if (this.bossAttack.typePk == ConstPlayer.PK_ALL && this.bossAttack.nPoint.hp > 1) {
                        this.bot.useSkill(Skill.TROI);
                        TimeUseSkillTroi = System.currentTimeMillis();
                    }
                } else if (this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(TimeUseSkillTroi2, Util.nextInt(30000, 35000))) {
                    if (this.bossAttack.typePk == ConstPlayer.PK_ALL && this.bossAttack.nPoint.hp > 1) {
                        this.bot.useSkill(Skill.TROI);
                        TimeUseSkillTroi2 = System.currentTimeMillis();
                    }
                } else if (this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(TimeUseSkillTroi3, Util.nextInt(40000, 45000))) {
                    if (this.bossAttack.typePk == ConstPlayer.PK_ALL && this.bossAttack.nPoint.hp > 1) {
                        this.bot.useSkill(Skill.TROI);
                        TimeUseSkillTroi3 = System.currentTimeMillis();
                    }
                } else if (this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(TimeUseSkillTroi4, Util.nextInt(50000, 55000))) {
                    if (this.bossAttack.typePk == ConstPlayer.PK_ALL && this.bossAttack.nPoint.hp > 1) {
                        this.bot.useSkill(Skill.TROI);
                        TimeUseSkillTroi4 = System.currentTimeMillis();
                    }
                }
                break;
            case ConstPlayer.TRAI_DAT:
                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(LastTimeUseSkillKhienForEarth, Util.nextInt(60000, 90000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForEarth = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(LastTimeUseSkillKhienForEarth2, Util.nextInt(80000, 120000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForEarth2 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(LastTimeUseSkillKhienForEarth3, Util.nextInt(100000, 150000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForEarth3 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(LastTimeUseSkillKhienForEarth4, Util.nextInt(120000, 180000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForEarth4 = System.currentTimeMillis();
                }
                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(TimeUseSkillThaiDuongHaSan, Util.nextInt(30000, 40000))) {
                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                    TimeUseSkillThaiDuongHaSan = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(TimeUseSkillThaiDuongHaSan2, Util.nextInt(40000, 50000))) {
                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                    TimeUseSkillThaiDuongHaSan2 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(TimeUseSkillThaiDuongHaSan3, Util.nextInt(50000, 60000))) {
                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                    TimeUseSkillThaiDuongHaSan3 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(TimeUseSkillThaiDuongHaSan4, Util.nextInt(60000, 80000))) {
                    this.bot.useSkill(Skill.THAI_DUONG_HA_SAN);
                    TimeUseSkillThaiDuongHaSan4 = System.currentTimeMillis();
                }
                break;
            case ConstPlayer.NAMEC:
                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(LastTimeUseSkillKhienForNamec, Util.nextInt(60000, 90000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForNamec = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(LastTimeUseSkillKhienForNamec2, Util.nextInt(80000, 120000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForNamec2 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(LastTimeUseSkillKhienForNamec3, Util.nextInt(100000, 150000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForNamec3 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(LastTimeUseSkillKhienForNamec4, Util.nextInt(120000, 180000))) {
                    this.bot.useSkill(Skill.KHIEN_NANG_LUONG);
                    LastTimeUseSkillKhienForNamec4 = System.currentTimeMillis();
                }
                if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point <= 2 && Util.canDoWithTime(LastTimeUseSkillDETRUNG, Util.nextInt(300000, 360000))) {
                    this.bot.useSkill(Skill.DE_TRUNG);
                    LastTimeUseSkillDETRUNG = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 3 && this.bot.playerSkill.skillSelect.point <= 4) && Util.canDoWithTime(LastTimeUseSkillDETRUNG2, Util.nextInt(360000, 420000))) {
                    this.bot.useSkill(Skill.DE_TRUNG);
                    LastTimeUseSkillDETRUNG2 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && (this.bot.playerSkill.skillSelect.point >= 5 && this.bot.playerSkill.skillSelect.point <= 6) && Util.canDoWithTime(LastTimeUseSkillDETRUNG3, Util.nextInt(420000, 480000))) {
                    this.bot.useSkill(Skill.DE_TRUNG);
                    LastTimeUseSkillDETRUNG3 = System.currentTimeMillis();
                } else if (!this.bot.isDie() && this.bot.playerSkill.skillSelect.point == 7 && Util.canDoWithTime(LastTimeUseSkillDETRUNG4, Util.nextInt(480000, 540000))) {
                    this.bot.useSkill(Skill.DE_TRUNG);
                    LastTimeUseSkillDETRUNG4 = System.currentTimeMillis();
                }
                break;
        }
        this.lastTimeSkill1 = System.currentTimeMillis() - new Random().nextInt(300000);
    }
    }
          
    public void SanBot() {
        this.GetBoss(0);
        this.GetSkil();
        if (this.bossAttack != null && !this.bossAttack.isDie()) {
            if (this.bot.UseLastTimeSkill()) {
                int y = 0;
                int x = 0;
                for (Mob m : this.bot.zone.mobs) {
                    y = m.location.y;
                    x = m.location.x;
                }
                if (this.bossAttack == null ||this.bossAttack.zone == null) {
                    BotManager.gI().bot.remove(this.bot);
                    ChangeMapService.gI().exitMap(this.bot);
                    this.bossAttack = null;
                }
                if (this.bot.zone != null) {
                    if (this.bot.effectSkill.isStone || this.bot.effectSkin.isDraburaFrost || this.bot.isDie()) {
                        return;
                    }
                    int dis = Util.getDistance(this.bot, this.bossAttack);
                    int dir = (this.bot.location.x - this.bossAttack.location.x < 0 ? 1 : -1);
                    int move = (dis > 60 ? Util.nextInt(80, 120) : Util.nextInt(10, 40));
                    this.bot.move(this.bot.location.x + (dir == 1 ? move : -move), this.bossAttack.location.y);
                    if (dis < 60) {
                        SkillService.gI().useSkill(this.bot, this.bossAttack, null, -1, null);
                    }
                    if (this.bot.nPoint.power > 50_000_000) {
                        int tiLeHutHp = this.bot.OptionHut;
                        int tiLeHutMp = this.bot.OptionHut;
                        int hpHoi = (int) (this.bot.nPoint.dame * tiLeHutHp / 100);
                        int mpHoi = (int) (this.bot.nPoint.dame * tiLeHutMp / 100);
                        if (this.bot.zone != null && bossAttack != null && hpHoi != 0 && mpHoi != 0 && tiLeHutHp != 0 && tiLeHutMp != 0) {
                            EffectMapService.gI().sendEffectMapToAllInMap(this.bot, 37, 3, 1, bossAttack.location.x, bossAttack.location.y, -1);
                        }
                        PlayerService.gI().hoiPhuc(this.bot, hpHoi, mpHoi);
                    }
                }
            }
        }
    }    
}