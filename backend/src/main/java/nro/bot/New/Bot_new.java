package nro.bot.New;

import nro.inventory.Inventory;
import nro.effect.EffectSkillService;
import nro.mob.Mob;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstAchievement;
import java.util.ArrayList;
import java.util.List;
import nro.map.Map;
import nro.map.Zone;
import consts.ConstPlayer;
import java.util.Random;
import models.Item.ItemService;
import network.io.Message;
import nro.achievement.AchievementService;

public class Bot_new extends Player {
    private short head_;
    private short body_;
    private short leg_;
    private short flag_;
    private short mount_;
    public int type;
    private int index_ = 0;
    long LasttimeChat;
    long LasttimeChat_2;
    long LasttimeChat_3;
    long LasttimeChat_4;
    public int Style_2 = 0;
    public int IDBot_2 = 0;
    long TimeChatKhuKhu;
    public BotDetu_1 detu;
    public BotDetu_2 detu_2;
    public BotDetu_3 detu_3;
    public BotDetu_4 detu_4;
    public BotDetu_5 detu_5;
    public BotDetu_6 detu_6;
    public BotDetu_7 detu_7;
    public BotDetu_8 detu_8;
    public BotDetu_9 detu_9;
    public BotDetu_10 detu_10;
    public BotDetu_11 detu_11;
    public BotAttackPlayer_1 attackplayer_1;
    public BotAttackPlayer_2 attackplayer_2;
    public BotAttackPlayer_3 attackplayer_3;
    
    private void UpdateChat() {
        if (isBot_New && Util.canDoWithTime(TimeChatKhuKhu, Util.nextInt(15000, 25000))) {
            Service.getInstance().chat(this, "Khụ khụ...");
            TimeChatKhuKhu = System.currentTimeMillis();
        }
    }
                  
    public Bot_new(int id, short head, short body, short leg, int type, String name, BotAttackPlayer_1 attack_1, BotAttackPlayer_2 attack_2, BotAttackPlayer_3 attack_3, 
            short flag, byte cflag, short mount){
        this.head_ = head;
        this.body_ = body;
        this.leg_ = leg;
        this.attackplayer_1 = attack_1;
        this.attackplayer_2 = attack_2;
        this.attackplayer_3 = attack_3;
        this.name = name;
        this.id = id;
        this.type = type;
        this.isBot_New = true;
        this.flag_ = flag;
        this.cFlag = cflag;
        this.nPoint.stamina = 1000;
        this.nPoint.maxStamina = 1000;
        this.mount_ = mount;
        this.inventory = new Inventory();
        this.inventory.gold = 2_000_000_000;
        for (int i = 0; i < 100; i++) {
            this.inventory.itemsBag.add(ItemService.gI().createItemNull());
        }
        for (int i = 0; i < 20; i++) {
            this.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
    }
           
    public int MapToPow() {
        int mapId = 101;
        switch (type) {
            case 0:
                detu.CheckPower();
                break;
            case 1:
                detu_2.CheckPower();
                break;
            case 2:
                detu_3.CheckPower();
                break;
            case 3:
                detu_4.CheckPower();
                break;
            case 4:
                detu_5.CheckPower();
                break;
            case 5:
                detu_6.CheckPower();
                break;
            case 6:
                detu_7.CheckPower();
                break;
            case 7:
                detu_8.CheckPower();
                break;
            case 8:
                detu_9.CheckPower();
                break;
            case 9:
                detu_10.CheckPower();
                break;
            case 10:
                detu_11.CheckPower();
                break;
            case 11:
                if (this.nPoint.power < 40_000_000_000L) {
                    this.MapUp_1();
                } else {
                    this.MapUp_2();
                }
                break;
            case 12:
                if (this.nPoint.power < 40_000_000_000L) {
                    this.MapUp_3();
                } else {
                    this.MapUp_4();
                }
                break;
            case 13:
                if (this.nPoint.power < 40_000_000_000L) {
                    this.MapUp_5();
                } else {
                    this.MapUp_6();
                }
                break;
            default:
                break;
        }
        return mapId;
    }
   
    public void joinMap() {
        Zone zone = getRandomZone(MapToPow());
        if (zone != null){
            ChangeMapService.gI().goToMap(this, zone);
            this.zone.load_Me_To_Another(this);
        }
    }
   
    public Zone getRandomZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                zone = map.zones.stream()
                .filter(z -> z.getNumOfPlayers() == 0)
                .findFirst()
                .orElseGet(() -> { Zone randomZone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
                return randomZone.isFullPlayer() ? null : randomZone; 
                });
            }
        } catch (Exception e) {
        }
        if (zone != null) {
            this.index_ = 0;
            return zone;
        } else {
            this.index_ += 1;
            if (this.index_ >= 99) {
                BotManager_new.gI().botnew.remove(this);
                ChangeMapService.gI().exitMap(this);
                return null;
            } else {
                return getRandomZone(MapToPow());
            }
        }
    }

   
    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isStone) {
            return 454;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][0];
        }
        if (effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 406;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1413;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 760;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1210;
        } else {
            return this.head_;
        }
    }

    @Override
    public short getBody() {
        if (effectSkill != null && effectSkill.isStone) {
            return 455;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][1];
        }
        if (effectSkill.isMonkey) {
            return 193;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 407;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1414;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 761;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1211;
        } else {
            return this.body_;
        }
    }

    @Override
    public short getLeg() {
        if (effectSkill != null && effectSkill.isStone) {
            return 456;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][2];
        }
        if (effectSkill.isMonkey) {
            return 194;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 408;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1415;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 762;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1212;
        } else {
            return this.leg_;
        }
    }
    
    @Override
    public short getFlagBag() {
        return this.flag_;
    }
    
    @Override
    public short getMount() {
        return this.mount_;
    }
    
    @Override
    public void update() {
        super.update();
        this.increasePoint();
        UpdateChat();
        if (this.zone != null) {
            switch (this.type) {
                case 0:
                    this.detu.update();
                    this.detu.CheckPower();
                    break;
                case 1:
                    this.detu_2.update();
                    this.detu_2.CheckPower();
                    break;
                case 2:
                    this.detu_3.update();
                    this.detu_3.CheckPower();
                    break;
                case 3:
                    this.detu_4.update();
                    this.detu_4.CheckPower();
                    break;
                case 4:
                    this.detu_5.update();
                    this.detu_5.CheckPower();
                    break;
                case 5:
                    this.detu_6.update();
                    this.detu_6.CheckPower();
                    break;
                case 6:
                    this.detu_7.update();
                    this.detu_7.CheckPower();
                    break;
                case 7:
                    this.detu_8.update();
                    this.detu_8.CheckPower();
                    break;
                case 8:
                    this.detu_9.update();
                    this.detu_9.CheckPower();
                    break;
                case 9:
                    this.detu_10.update();
                    this.detu_10.CheckPower();
                    break;
                case 10:
                    this.detu_11.update();
                    this.detu_11.CheckPower();
                    break;
                case 11:
                    if (this.nPoint.power < 40_000_000_000L) {
                        this.MapUp_1();
                    } else {
                        this.MapUp_2();
                    }
                    this.attackplayer_1.update();
                    this.attackplayer_1.CheckPower();
                    break;
                case 12:
                    if (this.nPoint.power < 40_000_000_000L) {
                        this.MapUp_3();
                    } else {
                        this.MapUp_4();
                    }
                    this.attackplayer_2.update();
                    this.attackplayer_2.CheckPower();
                    break;
                case 13:
                    if (this.nPoint.power < 40_000_000_000L) {
                        this.MapUp_5();
                    } else {
                        this.MapUp_6();
                    }
                    this.attackplayer_3.update();
                    this.attackplayer_3.CheckPower();
                    break;
                default:
                    break;
            }
        }
        if (this.isDie() && (this.type >= 0 && this.type <= 10)) {
            if (Util.isTrue(1, 1000)) {
                Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
            }
        }
        if (this.isDie() && (this.type >= 11 && this.type <= 13)) {
            if (Util.isTrue(1, 100)) {
                Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
            }
        }
        if (this.Detu != null) {
            this.Detu.cFlag = this.cFlag;
            this.IDBot_2 = this.Detu.IdBot;
        }
        if (this.Style_2 == 1) {
            this.nPoint.wearingVoHinh = true;
        }
        if (this.IDBot_2 >= 6) {
            this.Detu.nPoint.isKhongLanh = true;
        }
        if ((this.type >= 0 && this.type <= 10) && this.isDie()) {
            if (Util.canDoWithTime(LasttimeChat, Util.nextInt(5_000, 8_000))) {
                if (Util.isTrue(20, 100)) {
                    Service.getInstance().chat(this, "dcm thang suc vat nay");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "dm may");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "pha cc");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "djt me may");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "thang lol pha cai cc");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "phá cái đầu lolll");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "phá con cek");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "dm thang tre trau");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "djt mẹ cả lò nhà mày");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "lũ súc vật phá bố");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "để yên bố up đệ");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "chửi cả nhà m");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đjt con me nha mày");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cút ra chỗ khác cho bố m up đệ cái");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đm biến mẹ m đi");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "phá con căck");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cút không bố diết may giờ");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "djt con ba may");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "thang re rach");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "suc vat");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "re rach");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "sv vai lon");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cut ra cho bo up de");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "dmmmmmmm");
                } else {
                    Service.getInstance().chat(this, "cút");
                }
                LasttimeChat = System.currentTimeMillis();
            }
        }
        if ((this.type >= 0 && this.type <= 10) && !this.isDie()) {
            if (Util.canDoWithTime(LasttimeChat_2, Util.nextInt(8_000, 15_000))) {
                if (Util.isTrue(20, 100)) {
                    Service.getInstance().chat(this, "tha em up đệ");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tha e");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tha e cho e up đệ");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cho ké với auto hs cho");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "ké với");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "ké dt nha");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cho up chung vs nha");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "up detu chung voi nha");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tha tui up detu");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cac a tha e");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cho em up đệ tử với em auto hs cho");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "cắm vệ tinh lên bro");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tha mạng mấy bro ơi");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tha tui up de caii");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đừng bật cờ");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đổi cờ đi");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "ai cắm vệ tinh đi");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tháo cờ đi");
                } else {
                    Service.getInstance().chat(this, "tha em");
                }
                LasttimeChat_2 = System.currentTimeMillis();
            }
        }
        if ((this.type >= 11 && this.type <= 13) && this.isDie()) {
            if (Util.canDoWithTime(LasttimeChat_3, Util.nextInt(5_000, 8_000))) {
                if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "mày mạnh đấy");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đm vip đấy");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đm mạnh thế");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đợi t cầm acc chính");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "doi t mang acc chinh qua");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "hs");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đợi t hs");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "kinh vl");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "mạnh ác");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "thôi tha t đi");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "mày nạp bao nhiêu rồi mà mạnh tke");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "ê tha tao");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "tháo cờ đi mày");
                } else {
                    Service.getInstance().chat(this, "tha tao");
                }
                LasttimeChat_3 = System.currentTimeMillis();
            }
        }
        if ((this.type >= 11 && this.type <= 13) && !this.isDie()) {
            if (Util.canDoWithTime(LasttimeChat_4, Util.nextInt(8_000, 15_000))) {
                if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "ae pem no");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "đấm nó");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "bật cờ lên các e");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "xin đi t tha");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "bố m cân tất");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "bố cân cả khu");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "giỏi vào mà solo với t");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "giỏi vào đây");
                } else if (Util.isTrue(15, 100)) {
                    Service.getInstance().chat(this, "bọn m tuổi gì");
                } else {
                    Service.getInstance().chat(this, "tuổi gì giám bật cờ");
                }
                LasttimeChat_4 = System.currentTimeMillis();
            }
        }
    }
           
    public boolean UseLastTimeSkill(){
        if (this.playerSkill.skillSelect != null && this.playerSkill.skillSelect.lastTimeUseThisSkillbot < (System.currentTimeMillis() - this.playerSkill.skillSelect.coolDown)) {
            this.playerSkill.skillSelect.lastTimeUseThisSkillbot = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
    
    private void increasePoint() {
        long tiemNangUse = 0;
        int point = 0;
        if (this.nPoint != null) {
            if (Util.isTrue(50, 100)) {
                point = 100;
                int pointHp = point * 20;
                tiemNangUse = point * (2 * (this.nPoint.hpg + 1000) + pointHp - 20) / 2;
                if (doUseTiemNang(tiemNangUse)){
                    this.nPoint.hpMax += point;
                    this.nPoint.hpg += point;
                    Service.gI().point(this);
                }
            } else {
                point = 10;
                tiemNangUse = point * (2 * this.nPoint.dameg + point - 1) / 2 * 100;
                if (doUseTiemNang(tiemNangUse)) {
                    this.nPoint.dameg += point;
                    Service.gI().point(this);
                }
            }
        }
    }
   
    private boolean doUseTiemNang(long tiemNang) {
        if (this.nPoint.tiemNang < tiemNang) {
            return false;
        } else {
            this.nPoint.tiemNang -= tiemNang;
            return true;
        }
    }
    
    List<Mob> mobs;
    List<Player> players;
    public void useSkill(int skillId){
        if (this.zone != null) {
            switch (skillId) {
                case Skill.THAI_DUONG_HA_SAN:
                int timeStun = SkillUtil.getTimeStun(this.playerSkill.skillSelect.point);
                mobs = new ArrayList<>();
                players = new ArrayList<>();
                if (!MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    List<Player> playersMap = this.zone.getHumanoids();
                        for (Player pl : playersMap) {
                            if (pl != null && !this.equals(pl)) {
                                if (!pl.nPoint.khangTDHS) {
                                    int distance = Util.getDistance(this, pl);
                                    int rangeStun = SkillUtil.getRangeStun(this.playerSkill.skillSelect.point);
                                    if (distance <= rangeStun && SkillService.gI().canAttackPlayerForbot(this, pl)) {
                                        EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), timeStun);
                                        if (Util.isTrue(50, 100)) {
                                            Service.getInstance().chat(pl,"Mắt của ta");
                                        } else if (Util.isTrue(50, 100)) {
                                            Service.getInstance().chat(pl,"Chói mắt quá");
                                        } else {
                                            Service.getInstance().chat(pl,"Mù mắt rồi");
                                        }
                                        if (pl.typePk != ConstPlayer.NON_PK) {
                                            players.add(pl);
                                        }
                                    }
                                } else {
                                    Service.getInstance().chat(pl, "Vô dụng thôi, hahaha");
                                }
                            }
                        }
                    }
                    for (Mob mob : this.zone.mobs) {
                    if (Util.getDistance(this, mob) <= SkillUtil.getRangeStun(this.playerSkill.skillSelect.point)) {
                        mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                        mobs.add(mob);
                    }
                }
                EffectSkillService.gI().sendEffectBlindThaiDuongHaSan(this, players, mobs, timeStun);
                SkillService.gI().affterUseSkill(this, this.playerSkill.skillSelect.template.id);
                break;
            case Skill.TAI_TAO_NANG_LUONG:
                EffectSkillService.gI().startCharge(this);
                SkillService.gI().affterUseSkill(this, this.playerSkill.skillSelect.template.id);
                break;
            case Skill.TRI_THUONG:
                List<Player> playersMap = this.zone.getHumanoids();
                for (Player pl : playersMap) {
                    if (pl != null && !this.equals(pl)) {
                        useSkillBuffToBot(this, pl);
                    }
                }
                break;
            }
        }
    }
    
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_1 = new int[]{200 + Util.nextInt(-100, 900)};
    private void MapUp_1(){
        Random random = new Random();
        int location = RandomLocation_x_1[random.nextInt(RandomLocation_x_1.length)];
        if (this.zone != null && this.zone.map.mapId != 0) {
            Zone zone = this.getRandomZone(0);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this, zone);
                this.zone.load_Me_To_Another(this);
                PlayerService.gI().playerMove(this, location, 432);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_2 = new int[]{1250 + Util.nextInt(-100, 140)};
    private void MapUp_2(){
        Random random = new Random();
        int location = RandomLocation_x_2[random.nextInt(RandomLocation_x_2.length)];
        if (this.zone != null && this.zone.map.mapId != 42) {
            Zone zone = this.getRandomZone(42);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this, zone);
                this.zone.load_Me_To_Another(this);
                PlayerService.gI().playerMove(this, location, 432);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_3 = new int[]{155 + Util.nextInt(1, 1000)};
    private void MapUp_3(){
        Random random = new Random();
        int location = RandomLocation_x_3[random.nextInt(RandomLocation_x_3.length)];
        if (this.zone != null && this.zone.map.mapId != 7) {
            Zone zone = this.getRandomZone(7);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this, zone);
                this.zone.load_Me_To_Another(this);
                PlayerService.gI().playerMove(this, location, 432);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_4 = new int[]{1200 + Util.nextInt(-150, 150)};
    private void MapUp_4(){
        Random random = new Random();
        int location = RandomLocation_x_4[random.nextInt(RandomLocation_x_4.length)];
        if (this.zone != null && this.zone.map.mapId != 43) {
            Zone zone = this.getRandomZone(43);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this, zone);
                this.zone.load_Me_To_Another(this);
                PlayerService.gI().playerMove(this, location, 432);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_5 = new int[]{200 + Util.nextInt(-100, 900)};
    private void MapUp_5(){
        Random random = new Random();
        int location = RandomLocation_x_5[random.nextInt(RandomLocation_x_5.length)];
        if (this.zone != null && this.zone.map.mapId != 14) {
            Zone zone = this.getRandomZone(14);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this, zone);
                this.zone.load_Me_To_Another(this);
                PlayerService.gI().playerMove(this, location, 408);
            }
        }
    }
    //--------------------------------------------------------------------------
    private int[] RandomLocation_x_6 = new int[]{1050 + Util.nextInt(-150, 150)};
    private void MapUp_6(){
        Random random = new Random();
        int location = RandomLocation_x_6[random.nextInt(RandomLocation_x_6.length)];
        if (this.zone != null && this.zone.map.mapId != 44) {
            Zone zone = this.getRandomZone(44);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this, zone);
                this.zone.load_Me_To_Another(this);
                PlayerService.gI().playerMove(this, location, 432);
            }
        }
    }
    
    private void useSkillBuffToBot(Player player, Player plTarget) {
        Message msg = null;
        if (player.playerSkill.skillSelect.template.id == Skill.TRI_THUONG) {
            List<Player> players = new ArrayList<>();
            int percentTriThuong = SkillUtil.getPercentTriThuong(player.playerSkill.skillSelect.point);
            int point = player.playerSkill.skillSelect.point;
            if (canHsPlayer(player, plTarget)) {
                players.add(plTarget);
                List<Player> playersMap = player.zone.getNotBosses();
                for (Player pl : playersMap) {
                    if (!pl.equals(plTarget) && point > 1) {
                        if (canHsPlayer(player, plTarget) && Util.getDistance(player, pl) <= 300) {
                            players.add(pl);
                        }
                    }
                }
                for (Player pl : players) {
                    try {
                        msg = new Message(-60);
                        msg.writer().writeInt((int) player.id); //id pem
                        msg.writer().writeByte(player.playerSkill.skillSelect.skillId); //skill pem
                        msg.writer().writeByte(1); //số người pem
                        msg.writer().writeInt((int) pl.id); //id ăn pem
                        msg.writer().writeByte(0); //read continue
                        Service.gI().sendMessAllPlayerInMap(pl, msg);
                        boolean isDie = pl.isDie();
                        player.nPoint.setHP(player.nPoint.getHP() + ((long) player.nPoint.hpMax * percentTriThuong / 100));
                        pl.nPoint.setHP(pl.nPoint.getHP() + ((long) pl.nPoint.hpMax * percentTriThuong / 100));
                        pl.nPoint.setMP(pl.nPoint.getMP() + ((long) pl.nPoint.mpMax * percentTriThuong / 100));
                        if (isDie) {
                            AchievementService.gI().checkDoneTask(pl, ConstAchievement.CHAM_SOC_DAC_BIET);
                            Service.gI().chat(pl, "Cảm ơn " + player.name + " đã hồi sinh mình");
                            Service.gI().Send_Info_NV(player);
                            Service.gI().hsChar(pl, pl.nPoint.getHP(), pl.nPoint.getMP());
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            PlayerService.gI().sendInfoHpMp(player);
                        } else {
                            Service.gI().chat(pl, "Cảm ơn " + player.name + " đã cứu mình");
                            Service.gI().Send_Info_NV(player);
                            PlayerService.gI().sendInfoHpMp(pl);
                            PlayerService.gI().sendInfoHpMp(player);
                        }
                        Service.gI().Send_Info_NV(pl);
                    } catch (Exception e) {
                    } finally {
                        if (msg != null) {
                            msg.cleanup();
                        }
                    }
                }
            }
            SkillService.gI().affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        }
    }
    
    private boolean canHsPlayer(Player player, Player plTarget) {
        if (plTarget == null) {
            return false;
        }
        if (plTarget.isBoss) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_ALL) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_PVP) {
            return false;
        }
        if (player.cFlag != 0) {
            if (plTarget.cFlag != 0 && plTarget.cFlag != player.cFlag) {
                return false;
            }
        } else return plTarget.cFlag == 0;
        return true;
    }
}