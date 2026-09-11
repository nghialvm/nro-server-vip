package nro.bot;

import nro.inventory.Inventory;
import nro.effect.EffectSkillService;
import nro.mob.DeTrung;
import nro.mob.Mob;
import nro.player.Player;
import nro.services.DetuService;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.Service;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.SkillUtil;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nro.map.Map;
import nro.map.Zone;
import consts.ConstPlayer;
import java.io.IOException;
import models.Item.ItemService;
import network.io.Message;

public class Bot extends Player {
    private short head_;
    private short body_;
    private short leg_;
    private short flag_;
    private short mount_;
    public int type;
    private int index_ = 0;
    long LasttimeChat;
    long LasttimeChat_1;
    private boolean IsDetu = true;
    public ShopBot shop;
    public SellBot shopsell;
    public BotAttackBoss boss;
    public BotAttackMob mo1;
    public int OptionHut;
    private Player plAttack;
    long TimeChatKhukhu;
        
    //Map ID
    private int[] MapIDTeen_1_1 = new int[]{0,1};
    private int[] MapIDTeen_1_2 = new int[]{7,8};
    private int[] MapIDTeen_1_3 = new int[]{14,15};
    private int[] MapIDTeen_1 = new int[]{2,9,16};
    private int[] MapIDTeen_2 = new int[]{3,4,11,12,17,18};
    private int[] MapIDTeen_3 = new int[]{27,28,31,32,35,36};
    private int[] MapIDTeen_4 = new int[]{5,29,30,13,33,34,20,37,38};
    private int[] MapIDTeen_5 = new int[]{6,10,19};
    //MapID
    private int[] MapIDNapa_1 = new int[]{68,69,70};
    private int[] MapIDNapa_2 = new int[]{71,72,64,65};
    private int[] MapIDNapa_3 = new int[]{63,66,67};
    private int[] MapIDNapa_4 = new int[]{73,74,75,76};
    private int[] MapIDNapa_5 = new int[]{77,81,82};
    private int[] MapIDNapa_6 = new int[]{83,79,80};
    //MapID
    private int[] MapIDFuture_1 = new int[]{92,93,94};
    private int[] MapIDFuture_2 = new int[]{96,97};
    private int[] MapIDFuture_3 = new int[]{98,99,100};
    //MapID
    private int[] MapIDCold = new int[]{105,106,107,108,109,110};
    //MapID
    private int[] MapIDNguHanhSon = new int[]{122,123,124,192,193};
    //MapID
    private int[] MapIDCereal = new int[]{195,196,197,198,200};
    //MapID
    private int[] MapIDThanhDia_1 = new int[]{156,157};
    private int[] MapIDThanhDia_2 = new int[]{158,159};
    //MapID
    private int[] MapIDNguyenThuy = new int[]{160,161,162,163};
    //MapID
    private int[] MapIDDiaNguc = new int[]{167,168,172,173};
    //MapID
    private int[] MapIDChrisMas = new int[]{174,175,176,177,178,179};
    
   
    public Bot(int id, short head, short body, short leg, int type, String name, ShopBot shop, SellBot shopsell, short flag, byte cflag, boolean isDetu, short mount){
        this.head_ = head;
        this.body_ = body;
        this.leg_ = leg;
        this.shop = shop;
        this.shopsell = shopsell;
        this.name = name;
        this.id = id;
        this.type = type;
        this.isBot = true;
        this.flag_ = flag;
        this.cFlag = cflag;
        this.nPoint.stamina = 1000;
        this.nPoint.maxStamina = 1000;
        this.IsDetu = isDetu;
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
    
    private void UpdateChat() {
        if (isBot && Util.canDoWithTime(TimeChatKhukhu, Util.nextInt(15000, 25000))) {
            Service.getInstance().chat(this, "Khụ khụ...");
            TimeChatKhukhu = System.currentTimeMillis();
        }
    }
           
    public int MapToPow(){
        Random random = new Random();
        long power = this.nPoint.power;
        int mapId = 78;
        switch (type) {
            case 0:
                if (power < 16_000) {
                    if (power < 10_000) {
                        switch (gender) {
                            case 0:
                                mapId = MapIDTeen_1_1[random.nextInt(MapIDTeen_1_1.length)];
                                break;
                            case 1:
                                mapId = MapIDTeen_1_2[random.nextInt(MapIDTeen_1_2.length)];
                                break;
                            case 2:
                                mapId = MapIDTeen_1_3[random.nextInt(MapIDTeen_1_3.length)];
                                break;
                            default:
                                break;
                        }
                    } else {
                        mapId = MapIDTeen_1[random.nextInt(MapIDTeen_1.length)];
                    }
                } else if (power >= 16_000 && power < 100_000) {
                    mapId = MapIDTeen_2[random.nextInt(MapIDTeen_2.length)];
                } else if (power >= 100_000 && power < 500_000) {
                    mapId = MapIDTeen_3[random.nextInt(MapIDTeen_3.length)];
                } else if (power >= 500_000 && power < 1_500_000) {
                    mapId = MapIDTeen_4[random.nextInt(MapIDTeen_4.length)];
                } else if (power >= 1_500_000 && power < 50_000_000) {
                    mapId = MapIDTeen_5[random.nextInt(MapIDTeen_5.length)];
                } else if (power >= 50_000_000 && power < 200_000_000) {
                    mapId = MapIDNapa_1[random.nextInt(MapIDNapa_1.length)];
                } else if (power >= 200_000_000 && power < 500_000_000) {
                    mapId = MapIDNapa_2[random.nextInt(MapIDNapa_2.length)];
                } else if (power >= 500_000_000 && power < 1_000_000_000) {
                    mapId = MapIDNapa_3[random.nextInt(MapIDNapa_3.length)];
                } else if (power >= 1_000_000_000 && power < 5_000_000_000L) {
                    mapId = MapIDNapa_4[random.nextInt(MapIDNapa_4.length)];
                } else if (power >= 5_000_000_000L && power < 10_000_000_000L) {
                    mapId = MapIDNapa_5[random.nextInt(MapIDNapa_5.length)];
                } else if (power >= 10_000_000_000L && power < 25_000_000_000L) {
                    if (Util.isTrue(70, 100)) {
                        mapId = MapIDNapa_6[random.nextInt(MapIDNapa_6.length)];
                    } else {
                        mapId = MapIDNguHanhSon[random.nextInt(MapIDNguHanhSon.length)];
                    }
                } else if (power >= 25_000_000_000L && power < 30_000_000_000L) {
                    if (Util.isTrue(70, 100)) {
                        mapId = MapIDFuture_1[random.nextInt(MapIDFuture_1.length)];
                    } else {
                        mapId = MapIDNguHanhSon[random.nextInt(MapIDNguHanhSon.length)];
                    }
                } else if (power >= 30_000_000_000L && power < 35_000_000_000L) {
                    if (Util.isTrue(70, 100)) {
                        mapId = MapIDFuture_2[random.nextInt(MapIDFuture_2.length)];
                    } else {
                        mapId = MapIDCereal[random.nextInt(MapIDCereal.length)];
                    }
                } else if (power >= 35_000_000_000L && power < 40_000_000_000L) {
                    if (Util.isTrue(70, 100)) {
                        mapId = MapIDFuture_3[random.nextInt(MapIDFuture_3.length)];
                    } else {
                        mapId = MapIDNguHanhSon[random.nextInt(MapIDNguHanhSon.length)];
                    }
                } else if (power >= 40_000_000_000L && power < 50_000_000_000L) {
                    if (Util.isTrue(76, 100)) {
                        mapId = MapIDCold[random.nextInt(MapIDCold.length)];
                    } else {
                        mapId = MapIDCereal[random.nextInt(MapIDCereal.length)];
                    }
                } else if (power >= 50_000_000_000L && power < 70_000_000_000L) {
                    if (Util.isTrue(75, 100)) {
                        mapId = MapIDNguyenThuy[random.nextInt(MapIDNguyenThuy.length)];
                    } else {
                        mapId = 206;
                    }
                } else if (power >= 70_000_000_000L && power < 75_000_000_000L) {
                    if (Util.isTrue(75, 100)) {
                        mapId = MapIDThanhDia_1[random.nextInt(MapIDThanhDia_1.length)];
                    } else {
                        mapId = 155;
                    }
                } else if (power >= 75_000_000_000L && power < 80_000_000_000L) {
                    if (Util.isTrue(75, 100)) {
                        mapId = MapIDThanhDia_2[random.nextInt(MapIDThanhDia_2.length)];
                    } else {
                        mapId = 155;
                    }
                } else if (power > 80_000_000_000L) {
                    if (Util.isTrue(45, 100)) {
                        mapId = MapIDDiaNguc[random.nextInt(MapIDDiaNguc.length)];
                    } else {
                        mapId = MapIDChrisMas[random.nextInt(MapIDChrisMas.length)];
                    }
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
            this.mo1.lastTimeChanM = System.currentTimeMillis();
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
                BotManager.gI().bot.remove(this);
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
        long power = this.nPoint.power;
        switch (this.type){
            case 0:
                this.mo1.update();
                break;
            case 1:
                this.shop.update();
                break;
            case 2:
                this.boss.update();
                break;
            case 3:
                this.shopsell.update();
                break;
            default:
                break;
        }
        if (this.type == 0 && this.isDie()) {
            if (Util.isTrue(1, 200)) {
                Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
            }
        } else if (this.type == 2 && this.gender == 2 && this.isDie()) {
            if (Util.isTrue(1, 300)) {
                Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
            }
        } else if (this.type == 2 && this.gender != 2 && this.isDie()) {
            if (Util.isTrue(1, 100)) {
                Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
            }
        } else if (this.isDie() && (this.type == 1 || this.type == 3)) {
            Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
        if (this != null && power < 500_000) {
            this.flag_ = -1;
            if (type == 0) {
                switch (gender) {
                    case 0:
                        this.body_ = 14;
                        this.leg_ = 15;
                        break;
                    case 1:
                        this.body_ = 10;
                        this.leg_ = 11;
                        break;
                    case 2:
                        this.body_ = 16;
                        this.leg_ = 17;
                        break;
                    default:
                        break;
                }
            }
        }
        if (this != null && this.zone != null && MapService.gI().isMapColdforBot(this.zone.map.mapId)) {
            if (type == 0) {
                if (Util.isTrue(20, 100)) {
                    this.head_ = 709;
                    this.body_ = 710;
                    this.leg_ = 711;
                } else if (Util.isTrue(40, 100)) {
                    this.head_ = 712;
                    this.body_ = 713;
                    this.leg_ = 714;
                } else if (Util.isTrue(60, 100)) {
                    this.head_ = 950;
                    this.body_ = 951;
                    this.leg_ = 952;
                } else if (Util.isTrue(70, 100)) {
                    this.head_ = 1431;
                    this.body_ = 1432;
                    this.leg_ = 1433;
                } else {
                    this.head_ = 499;
                    this.body_ = 500;
                    this.leg_ = 501;
                }
            }
        }
        if (this != null && this.zone != null && (MapService.gI().isMapHanhTinhThucVat(this.zone.map.mapId) || MapService.gI().isMapThanhDia(this.zone.map.mapId))) {
            if (type == 0) {
                switch (gender) {
                    case 0:
                    case 2:
                        this.head_ = 383;
                        this.body_ = 384;
                        this.leg_ = 385;
                        break;
                    case 1:
                        this.head_ = 391;
                        this.body_ = 392;
                        this.leg_ = 393;
                        break;
                    default:
                        break;
                }
            }
        }
        if (this != null) {
            if (type == 2) {
                switch (gender) {
                    case 0:
                        if (power < 80_000_000_000L) {
                            this.head_ = 383;
                            this.body_ = 384;
                            this.leg_ = 385;
                        } else {
                            this.head_ = 870;
                            this.body_ = 871;
                            this.leg_ = 872;
                        }
                        break;
                    case 1:
                        if (power < 80_000_000_000L) {
                            this.head_ = 391;
                            this.body_ = 392;
                            this.leg_ = 393;
                        } else {
                            this.head_ = 873;
                            this.body_ = 874;
                            this.leg_ = 875;
                        }
                        break;
                    case 2:
                        if (power < 80_000_000_000L) {
                            this.head_ = 383;
                            this.body_ = 384;
                            this.leg_ = 385;
                        } else {
                            this.head_ = 867;
                            this.body_ = 868;
                            this.leg_ = 869;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        switch (type) {
            case 0:
                if (this.isDie() && Util.canDoWithTime(LasttimeChat, Util.nextInt(5000, 15000))) {
                    if (Util.isTrue(20, 100)) {
                        Service.getInstance().chat(this, "tha em up cái");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "anh tha cho em đi");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "tha em làm nhiệm vụ");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "tha e lm nv");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "nv anh oi");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "nhiem vu ma anh");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "tha e");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "đừng đánh em");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "anh gì gì dz");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "anh dz tha em");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "anh dz tha e làm nv");
                    } else if (Util.isTrue(15, 100)) {
                        Service.getInstance().chat(this, "tha em đi anh");
                    } else {
                        Service.getInstance().chat(this, "đừng phá em");
                    }
                    LasttimeChat = System.currentTimeMillis();
                }
                break;
            case 2:
                if (Util.canDoWithTime(LasttimeChat_1, Util.nextInt(5000, 15000))) {
                    if (this.isDie()) {
                        if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "trói vào hộ cái");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "boss mạnh thế");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "trâu vcl");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "khỏe vl");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "hoi sinh");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "hồi sinh với");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "trói đi");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "bọn ngu đéo biết trói à dmmm");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "đm bọn ngu này");
                        } else {
                            Service.getInstance().chat(this, "hs");
                        }
                    } else {
                        if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "trói vào hộ cái");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "trói boss");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "tha e làm nv");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "troi boss di may anh");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "troi");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "pem");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "xin boss");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "xin lam nhiem vu");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "xin boss lam nv");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "nhả boss cho t nhé");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "choáng boss đi");
                        } else if (Util.isTrue(15, 100)) {
                            Service.getInstance().chat(this, "thôi miên nó");
                        } else {
                            Service.getInstance().chat(this, "trói nó lại");
                        }
                    }
                    LasttimeChat_1 = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
        if (this.IsDetu == true && this.Detu == null && (this.type == 0 || this.type == 1) && this.zone != null && power > 10_000_000 && !MapService.gI().isMapHanhTinhThucVat(this.zone.map.mapId) 
                && !MapService.gI().isMapThanhDia(this.zone.map.mapId)) {
            DetuService.gI().createNormalPet(this);
        }
        if (this.Detu != null) {
            this.Detu.changeStatus(Detu.ATTACK);
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
        List<Player> bosses = this.zone.getBosses();
            switch (skillId) {
                case Skill.BIEN_KHI:
                    EffectSkillService.gI().startUseSkillMonkey(this);
                    SkillService.gI().affterUseSkill(this, this.playerSkill.skillSelect.template.id);
                    break;
                case Skill.QUA_CAU_KENH_KHI:
                    this.playerSkill.prepareQCKK = !this.playerSkill.prepareQCKK;
                    this.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    sendPlayerPrepareSkill(this, 2000);
                    break;
                case Skill.MAKANKOSAPPO:
                    this.playerSkill.prepareLaze = !this.playerSkill.prepareLaze;
                    this.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    sendPlayerPrepareSkill(this, 3000);
                break;
            case Skill.TROI:
                for (Player m : bosses) {
                    EffectSkillService.gI().sendEffectUseSkill(this, Skill.TROI);
                    int timeHold = SkillUtil.getTimeTroi(this.playerSkill.skillSelect.point);
                    EffectSkillService.gI().setUseTroi(this, System.currentTimeMillis(), timeHold);
                    if (boss != null && (!m.playerSkill.prepareQCKK && !m.playerSkill.prepareLaze && !m.playerSkill.prepareTuSat)) {
                        this.effectSkill.plAnTroi = m;
                        EffectSkillService.gI().sendEffectPlayer(this, m, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HOLD_EFFECT);
                        EffectSkillService.gI().setAnTroi(m, this, System.currentTimeMillis(), timeHold);
                    }
                    SkillService.gI().affterUseSkill(this, this.playerSkill.skillSelect.template.id);
                }
                break;
            case Skill.KHIEN_NANG_LUONG:
                EffectSkillService.gI().setStartShield(this);
                EffectSkillService.gI().sendEffectPlayer(this, this, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SHIELD_EFFECT);
                SkillService.gI().affterUseSkill(this, this.playerSkill.skillSelect.template.id);
                break;
            case Skill.THAI_DUONG_HA_SAN:
                int timeStun = SkillUtil.getTimeStun(this.playerSkill.skillSelect.point);
                    mobs = new ArrayList<>();
                    players = new ArrayList<>();
                    if (!MapService.gI().isMapOffline(this.zone.map.mapId)) {
                        List<Player> playersMap = new ArrayList<>(this.zone.getHumanoids());
                        for (Player pl : playersMap) {
                            if (pl != null && !this.equals(pl)) {
                                if (pl.nPoint != null && !pl.nPoint.khangTDHS) {
                                    int distance = Util.getDistance(this, pl);
                                    int rangeStun = SkillUtil.getRangeStun(this.playerSkill.skillSelect.point);
                                    if (distance <= rangeStun && SkillService.gI().canAttackPlayerForbot(this, pl)) {
                                        EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), timeStun);
                                        int rand = Util.nextInt(1, 3); // 1-3
                                        switch (rand) {
                                            case 1:
                                                Service.getInstance().chat(pl, "Mắt của ta");
                                                break;
                                            case 2:
                                                Service.getInstance().chat(pl, "Chói mắt quá");
                                                break;
                                            case 3:
                                                Service.getInstance().chat(pl, "Mù mắt rồi");
                                                break;
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
            case Skill.DE_TRUNG:
                EffectSkillService.gI().sendEffectUseSkill(this, Skill.DE_TRUNG);
                if (this.DeTrung != null) {
                    this.DeTrung.mobMeDie();
                }
                this.DeTrung = new DeTrung(this);
                SkillService.gI().affterUseSkill(this, this.playerSkill.skillSelect.template.id);
                break;
            }
        }
    }
    
    private void sendPlayerPrepareSkill(Player player, int affterMiliseconds) {
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(4);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
}