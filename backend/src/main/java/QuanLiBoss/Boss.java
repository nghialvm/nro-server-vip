package QuanLiBoss;

import Boss.list.Hanhtinhchet.Oren;
import Boss.list.nro.list.boss.Cumber.Cumber;
import consts.ConstPlayer;
import nro.map.Map;
import nro.map.Zone;
import nro.player.Player;
import nro.skill.Skill;
import nro.server.ServerNotify;
import nro.effect.EffectSkillService;
import nro.mob.Mob;
import nro.player.Detu;
import QuanLiBoss.IBoss.IBoss;
import QuanLiBoss.Manager.BrolyManager;
import QuanLiBoss.Manager.ChristmasEventManager;
import QuanLiBoss.Manager.FinalBossManager;
import QuanLiBoss.Manager.GasDestroyManager;
import QuanLiBoss.Manager.HalloweenEventManager;
import QuanLiBoss.Manager.HungVuongEventManager;
import QuanLiBoss.Manager.LunarNewYearEventManager;
import QuanLiBoss.Manager.OtherBossManager;
import QuanLiBoss.Manager.RedRibbonHQManager;
import QuanLiBoss.Manager.SkillSummonedManager;
import QuanLiBoss.Manager.SnakeWayManager;
import QuanLiBoss.Manager.TreasureUnderSeaManager;
import QuanLiBoss.Manager.TrungThuEventManager;
import QuanLiBoss.Manager.YardartManager;
import QuanLiBoss.Manager.BossManager;
import QuanLiBoss.Manager.BossNomalManager;
import QuanLiBoss.Manager.BossOfTheGangsManager;
import QuanLiBoss.Manager.ValentineEventManager;
import QuanLiBoss.Manager.VuLanEventManager;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.skill.SkillService;
import nro.services.TaskService;
import nro.services.Fun.ChangeMapService;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.Util;
import network.io.Message;
import java.io.IOException;
import java.util.List;
import nro.boss.map.The23rdMartialArtCongress.Locopo;

public class Boss extends Player implements IBoss {
    
    //BROLY
    public Player plAttack;
    protected int targetCountChangePlayerAttack;
    protected int countChangePlayerAttack;

    public int currentLevel = -1;
    public final BossData[] data;
    protected long lastTimeBossSpawn;
    protected long lastTimePlayerAttack;
    protected boolean hasPlayerAttackSinceSpawn;
    public BossStatus bossStatus;

    protected Zone lastZone;

    protected long lastTimeRest;
    protected int secondsRest;

    protected long lastTimeChatS;
    protected int timeChatS;
    protected byte indexChatS;
    
    public int idSkillPlayer = -1;
    public Player playertarget;
    public Mob mobTarget;

    protected long lastTimeChatE;
    protected int timeChatE;
    protected byte indexChatE;

    protected long lastTimeChatM;
    protected int timeChatM;

    protected long lastTimeTargetPlayer;
    protected int timeTargetPlayer;
    public Player playerTarger;

    protected Boss parentBoss;
    public Boss[][] bossAppearTogether;

    public Zone zoneFinal = null;

    public Player playerReward;

    public int lv;

    public int error;

    public boolean prepareBom;

    public boolean isNotifyDisabled;
    public boolean isZoneRandomSpawn;
    public boolean isZone02Spawn;
    public boolean isSpawnPlayer;
    
    public Boss(int id, boolean isNotifyDisabled, boolean isZoneRandomSpawn, boolean isZone02Spawn, boolean isSpawnPlayer, BossData... data) throws Exception {
        this(id, data);
        this.isNotifyDisabled = isNotifyDisabled;
        this.isZoneRandomSpawn = isZoneRandomSpawn;
        this.isZone02Spawn = isZone02Spawn;
        this.isSpawnPlayer = isSpawnPlayer;
    }

    public Boss(BossType bossType, int id, boolean isNotifyDisabled, boolean isZoneRandomSpawn, boolean isZone02Spawn, boolean isSpawnPlayer, BossData... data) throws Exception {
        this(bossType, id, data);
        this.isNotifyDisabled = isNotifyDisabled;
        this.isZoneRandomSpawn = isZoneRandomSpawn;
        this.isZone02Spawn = isZone02Spawn;
        this.isSpawnPlayer = isSpawnPlayer;
    }

    public Boss(int id, BossData... data) throws Exception {
        this.id = id;
        this.isBoss = true;
        if (data == null || data.length == 0) {
            throw new Exception("Dữ liệu boss không hợp lệ");
        }
        this.data = data;
        this.secondsRest = this.data[0].getSecondsRest();
        this.bossStatus = BossStatus.REST;
        BossManager.gI().addBoss(this);

        this.bossAppearTogether = new Boss[this.data.length][];
        for (int i = 0; i < this.bossAppearTogether.length; i++) {
            if (this.data[i].getBossesAppearTogether() != null) {
                this.bossAppearTogether[i] = new Boss[this.data[i].getBossesAppearTogether().length];
                for (int j = 0; j < this.data[i].getBossesAppearTogether().length; j++) {
                    Boss boss = BossManager.gI().createBoss(this.data[i].getBossesAppearTogether()[j]);
                    if (boss != null) {
                        boss.parentBoss = this;
                        boss.lv = j;
                        this.bossAppearTogether[i][j] = boss;
                    }
                }
            }
        }
    }

    public Boss(BossType bossType, int id, BossData... data) throws Exception {
        this.id = id;
        this.isBoss = true;
        if (data == null || data.length == 0) {
            throw new Exception("Dữ liệu boss không hợp lệ");
        }
        this.data = data;
        this.secondsRest = this.data[0].getSecondsRest();
        this.bossStatus = BossStatus.REST;
        switch (bossType) {
            case YARDART:
                YardartManager.gI().addBoss(this);
                break;
            case FINAL:
                FinalBossManager.gI().addBoss(this);
                break;
            case SKILLSUMMONED:
                SkillSummonedManager.gI().addBoss(this);
                break;
            case BROLY:
                BrolyManager.gI().addBoss(this);
                break;
            case PHOBAN:
                OtherBossManager.gI().addBoss(this);
                break;
            case PHOBANDT:
                RedRibbonHQManager.gI().addBoss(this);
                break;
            case PHOBANBDKB:
                TreasureUnderSeaManager.gI().addBoss(this);
                break;
            case PHOBANCDRD:
                SnakeWayManager.gI().addBoss(this);
                break;
            case PHOBANKGHD:
                GasDestroyManager.gI().addBoss(this);
                break;
            case PHOBANBBH:
                BossOfTheGangsManager.gI().addBoss(this);
                break;
            case TRUNGTHU_EVENT:
                TrungThuEventManager.gI().addBoss(this);
                break;
            case HALLOWEEN_EVENT:
                HalloweenEventManager.gI().addBoss(this);
                break;
            case CHRISTMAS_EVENT:
                ChristmasEventManager.gI().addBoss(this);
                break;
            case HUNGVUONG_EVENT:
                HungVuongEventManager.gI().addBoss(this);
                break;
            case TET_EVENT:
                LunarNewYearEventManager.gI().addBoss(this);
                break;
            case VU_LAN_EVENT:
                VuLanEventManager.gI().addBoss(this);
                break;
            case VALENTINE_EVENT:
                ValentineEventManager.gI().addBoss(this);
                break;
            case NOMAL:
                BossNomalManager.gI().addBoss(this);
                break;
        }

        this.bossAppearTogether = new Boss[this.data.length][];
        for (int i = 0; i < this.bossAppearTogether.length; i++) {
            if (this.data[i].getBossesAppearTogether() != null) {
                this.bossAppearTogether[i] = new Boss[this.data[i].getBossesAppearTogether().length];
                for (int j = 0; j < this.data[i].getBossesAppearTogether().length; j++) {
                    Boss boss = BossManager.gI().createBoss(this.data[i].getBossesAppearTogether()[j]);
                    if (boss != null) {
                        boss.parentBoss = this;
                        this.bossAppearTogether[i][j] = boss;
                    }
                }
            }
        }
    }
        
    @Override
    public void initBase() {
        BossData data = this.data[this.currentLevel];
        this.name = String.format(data.getName(), Util.nextInt(0, 100));
        this.gender = data.getGender();
        this.nPoint.mpg = 31_07_2002;
        this.nPoint.dameg = data.getDame();
        this.nPoint.hpg = data.getHp()[Util.nextInt(0, data.getHp().length - 1)];
        this.nPoint.hp = nPoint.hpg;
        this.nPoint.calPoint();
        this.initSkill();
        this.resetBase();
    }

    protected void initSkill() {
        for (Skill skill : this.playerSkill.skills) {
            skill.dispose();
        }
        this.playerSkill.skills.clear();
        this.playerSkill.skillSelect = null;
        int[][] skillTemps = data[this.currentLevel].getSkillTemp();
        for (int[] skillTemp : skillTemps) {
            Skill skill = SkillUtil.createSkill(skillTemp[0], skillTemp[1]);
            if (skillTemp.length == 3) {
                skill.coolDown = skillTemp[2];
            }
            this.playerSkill.skills.add(skill);
        }
    }
        
    protected void resetBase() {
        this.lastTimeChatS = 0;
        this.lastTimeChatE = 0;
        this.timeChatS = 0;
        this.timeChatE = 0;
        this.indexChatS = 0;
        this.indexChatE = 0;
    }

    //.outfit.
    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][0];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }
        if (this.id == BossID.oren) {
            if (((Oren) this).isFusion) {
                return 1249;
            }
        }
        
        if (this.id == BossID.LOCOPO) {
            if (((Locopo) this).transformed == true) {
                return 1273;
            }
        }
        return this.data[this.currentLevel].getOutfit()[0];
    }
    
    @Override
    public short getBody() {
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][1];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        }
        if (this.id == BossID.oren) {
            if (((Oren) this).isFusion) {
                return 1250;
            }
        }
        
        if (this.id == BossID.LOCOPO) {
            if (((Locopo) this).transformed == true) {
                return 1274;
            }
        }
        return this.data[this.currentLevel].getOutfit()[1];
    }

    @Override
    public short getLeg() {
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][2];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        }
        if (this.id == BossID.oren) {
            if (((Oren) this).isFusion) {
                return 1251;
            }
        }
        
        if (this.id == BossID.LOCOPO) {
            if (((Locopo) this).transformed == true) {
                return 1275;
            }
        }
        return this.data[this.currentLevel].getOutfit()[2];
    }
    
    @Override
    public short getFlagBag() {
        return this.data[this.currentLevel].getOutfit()[3];
    }

    @Override
    public byte getAura() {
        return (byte) this.data[this.currentLevel].getOutfit()[4];
    }

    @Override
    public byte getEffFront() {
        return (byte) this.data[this.currentLevel].getOutfit()[5];
    }
    
    public Zone getMapJoin() {
        int mapId = this.data[this.currentLevel].getMapJoin()[Util.nextInt(0, this.data[this.currentLevel].getMapJoin().length - 1)];
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        return map;
    }

    @Override
    public void changeStatus(BossStatus status) {
        this.bossStatus = status;
    }

    @Override
    public Player getPlayerAttack() {
        if (this.zone == null) {
            return null;
        }
        if (this.playerTarger != null && (this.playerTarger.isDie() || !this.zone.equals(this.playerTarger.zone))) {
            this.playerTarger = null;
        }
        if (this.playerTarger == null || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
            this.playerTarger = this.zone.getRandomPlayerInMap();
            this.lastTimeTargetPlayer = System.currentTimeMillis();
            this.timeTargetPlayer = Util.nextInt(5000, 7000);
        }
        if (this.playerTarger != null && this.playerTarger.isDeTu && ((Detu) this.playerTarger).master != null && ((Detu) this.playerTarger).master.equals(this)) {
            this.playerTarger = null;
        }
        
        return this.playerTarger;
    }
    
    @Override
    public void changeToTypePK() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
    }

    @Override
    public void changeToTypeNonPK() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
    }

    @Override
    public void updateInfo() {
        super.update();
    }

    @Override
    public void update() {
        if (prepareBom) {
            return;
        }
        super.update();
        this.nPoint.mp = this.nPoint.mpg;
        if (this.effectSkill == null || this.effectSkill.isHaveEffectSkill() || (this.newSkill != null && this.newSkill.isStartSkillSpecial)) {
            return;
        }
        if (this.newSkill != null && this.newSkill.isStartSkillSpecial) {
            SkillService.gI().newSkillNotFocus(this, 20);
            return;
        }
        switch (this.bossStatus) {
            case CHAT_S:
            case AFK:
            case ACTIVE:
                this.autoLeaveMap();
                break;
        }
        switch (this.bossStatus) {
            case REST:
                this.rest();
                break;
            case RESPAWN: {
                this.respawn();
                this.changeStatus(BossStatus.JOIN_MAP);
                break;
            }
            case JOIN_MAP:
                this.joinMap();
                break;
            case CHAT_S: {
                this.checkAutoResetBySecondsRest();
                if (chatS()) {
                    this.doneChatS();
                    this.lastTimeChatM = System.currentTimeMillis();
                    this.timeChatM = 5000;
                    if (this.bossStatus != BossStatus.AFK) {
                        this.changeStatus(BossStatus.ACTIVE);
                    }
                }
                break;
            }
            case AFK:
                this.afk();
                break;
            case ACTIVE: {
                if (this.zone == null || this.isDie()) {
                    return;
                }

                this.chatM();
                this.checkAutoResetBySecondsRest();

                if ((this.effectSkill.isCharging && !Util.isTrue(1, 20)) || this.effectSkill.useTroi) {
                    return;
                }
                this.active();
                break;
            }
            case DIE:
                this.changeStatus(BossStatus.CHAT_E);
                break;
            case CHAT_E: {
                if (chatE()) {
                    this.doneChatE();
                    this.changeStatus(BossStatus.LEAVE_MAP);
                }
                break;
            }
            case LEAVE_MAP:
                this.leaveMap();
                break;
        }
    }

    @Override
    public void rest() {
        int nextLevel = this.currentLevel + 1;
        if (nextLevel >= this.data.length) {
            nextLevel = 0;
        }
        if (this.data[nextLevel].getTypeAppear() == TypeAppear.DEFAULT_APPEAR
                && Util.canDoWithTime(lastTimeRest, secondsRest * 1000)) {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void afk() {

    }

   @Override
public void respawn() {
    this.currentLevel++;
    if (this.currentLevel >= this.data.length) {
        this.currentLevel = 0;
    }
    this.secondsRest = this.data[this.currentLevel].getSecondsRest();

    this.initBase();
    this.changeToTypeNonPK();

    this.lastTimeBossSpawn = System.currentTimeMillis();
    this.lastTimePlayerAttack = 0;
    this.hasPlayerAttackSinceSpawn = false;
}

//    @Override
//    public void joinMap() {
//        if (zoneFinal != null) {
//            joinMapByZone(zoneFinal);
//            this.notifyJoinMap();
//            this.changeStatus(BossStatus.CHAT_S);
//            this.wakeupAnotherBossWhenAppear();
//            return;
//        }
//        if (this.zone == null) {
//            if (this.parentBoss != null) {
//                this.zone = parentBoss.zone;
//            } else if (this.lastZone == null) {
//                this.zone = getMapJoin();
//            } else {
//                this.zone = this.lastZone;
//            }
//        }
//        if (this.zone == null) {
//            this.zone = getMapJoin();
//        }
//        if (this.zone != null) {
//            try {
//                if (this.currentLevel == 0) {
//                    if (this.parentBoss == null) {
//                        int zoneid = 0;
//                        //this.zone.map.mapId == 80 || this.zone.map.mapId == 103 || this.zone.map.mapId == 97 || this.zone.map.mapId == 102
//                        // Chỉ cho boss xuất hiện ở khu 2
//                        if (this.isZone02Spawn) {
//                            zoneid = 2;
//                            while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
//                                zoneid++;
//                            }
//
//                            if (zoneid < this.zone.map.zones.size()) {
//                                this.zone = this.zone.map.zones.get(zoneid);
//                            } else {
//                                this.changeStatus(BossStatus.REST);
//                                this.zone = null;
//                                this.lastZone = null;
//                                return;
//                            }
//                            // Chỉ cho boss xuất hiện từ khu 0 trở lên ở map thường
//                        } else if (this.isSpawnPlayer) {
//                            
//                            // Chỉ cho boss xuất hiện theo player
//                        } else if (this.isZoneRandomSpawn && this.zone.map.zones.size() > 1) {
//                            zoneid = Util.nextInt(0, this.zone.map.zones.size() - 1);
//                            while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
//                                zoneid++;
//                            }
//
//                            if (zoneid < this.zone.map.zones.size()) {
//                                this.zone = this.zone.map.zones.get(zoneid);
//                            } else {
//                                this.changeStatus(BossStatus.REST);
//                                this.zone = null;
//                                this.lastZone = null;
//                                return;
//                            }
//                        } else {
//                            // Check trong khu lớn hơn 10 người chuyển sang khu n + 1
//                            while (zoneid < this.zone.map.zones.size() && this.zone.map.zones.get(zoneid).getNumOfPlayers() > 10) {
//                                zoneid++;
//                            }
//                            // Check trong khu có boss sẽ chuyển sang khu n + 1
//                            while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
//                                zoneid++;
//                            }
//                            if (zoneid < this.zone.map.zones.size()) {
//                                this.zone = this.zone.map.zones.get(zoneid);
//                            } else {
//                                this.zone = this.zone.map.zones.get(0);
//                            }
//                        }
//                        int x = this.zone.map.mapWidth > 100 ? Util.nextInt(100, this.zone.map.mapWidth - 100) : Util.nextInt(100);
//                        int y = this.zone.map.yPhysicInTop(x, 100);
//                        ChangeMapService.gI().changeMap(this, this.zone, x, y);
//                    } else {
//                        int x = this.parentBoss.location.x - (this.lv + 1) * 30;
//                        int y = this.zone.map.yPhysicInTop(x, 100);
//                        ChangeMapService.gI().changeMap(this, this.zone, x, y);
//                    }
//                    this.wakeupAnotherBossWhenAppear();
//                } else {
//                    ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
//                }
//                Service.gI().sendFlagBag(this);
//                this.notifyJoinMap();
//                this.changeStatus(BossStatus.CHAT_S);
//            } catch (Exception e) {
//                this.changeStatus(BossStatus.REST);
//                if (error < 5) {
//                    Logger.error("Lỗi : " + e + "\n");
//                    error++;
//                }
//            }
//        } else {
//            this.changeStatus(BossStatus.RESPAWN);
//        }
//    }
    @Override
public void joinMap() {
    if (zoneFinal != null) {
        joinMapByZone(zoneFinal);
        this.notifyJoinMap();
        this.changeStatus(BossStatus.CHAT_S);
        this.lastTimeBossSpawn = System.currentTimeMillis();
        this.lastTimePlayerAttack = 0;
        this.hasPlayerAttackSinceSpawn = false;
        this.wakeupAnotherBossWhenAppear();
        return;
    }
    if (this.zone == null) {
        if (this.parentBoss != null) {
            this.zone = parentBoss.zone;
        } else if (this.lastZone == null) {
            this.zone = getMapJoin();
        } else {
            this.zone = this.lastZone;
        }
    }
    if (this.zone == null) {
        this.zone = getMapJoin();
    }
    if (this.zone != null) {
        try {
            if (this.currentLevel == 0) {
                if (this.parentBoss == null) {

                    // Không cho boss xuất hiện ở khu 0
                    int zoneid = 1;

                    // Chỉ cho boss xuất hiện ở khu 2
                    if (this.isZone02Spawn) {
                        zoneid = 2;
                        while (zoneid < this.zone.map.zones.size()
                                && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                            zoneid++;
                        }

                        if (zoneid < this.zone.map.zones.size()) {
                            this.zone = this.zone.map.zones.get(zoneid);
                        } else {
                            this.changeStatus(BossStatus.REST);
                            this.zone = null;
                            this.lastZone = null;
                            return;
                        }

                    } else if (this.isSpawnPlayer) {

                        // Chỉ cho boss xuất hiện theo player

                    } else if (this.isZoneRandomSpawn && this.zone.map.zones.size() > 1) {

                        // Random từ khu 1 trở đi, không lấy khu 0
                        if (this.zone.map.zones.size() <= 1) {
                            this.changeStatus(BossStatus.REST);
                            this.zone = null;
                            this.lastZone = null;
                            return;
                        }

                        zoneid = Util.nextInt(1, this.zone.map.zones.size() - 1);

                        while (zoneid < this.zone.map.zones.size()
                                && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                            zoneid++;
                        }

                        if (zoneid < this.zone.map.zones.size()) {
                            this.zone = this.zone.map.zones.get(zoneid);
                        } else {
                            // nếu random ra khu đã có boss và các khu sau đều full boss
                            // thì quét lại từ khu 1
                            zoneid = 1;
                            while (zoneid < this.zone.map.zones.size()
                                    && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                                zoneid++;
                            }

                            if (zoneid < this.zone.map.zones.size()) {
                                this.zone = this.zone.map.zones.get(zoneid);
                            } else {
                                this.changeStatus(BossStatus.REST);
                                this.zone = null;
                                this.lastZone = null;
                                return;
                            }
                        }

                    } else {
                        // Check trong khu lớn hơn 10 người chuyển sang khu n + 1
                        while (zoneid < this.zone.map.zones.size()
                                && this.zone.map.zones.get(zoneid).getNumOfPlayers() > 10) {
                            zoneid++;
                        }

                        // Check trong khu có boss sẽ chuyển sang khu n + 1
                        while (zoneid < this.zone.map.zones.size()
                                && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                            zoneid++;
                        }

                        if (zoneid < this.zone.map.zones.size()) {
                            this.zone = this.zone.map.zones.get(zoneid);
                        } else {
                            // nếu từ khu 1 trở đi không còn khu hợp lệ thì nghỉ spawn
                            this.changeStatus(BossStatus.REST);
                            this.zone = null;
                            this.lastZone = null;
                            return;
                        }
                    }

                    int x = this.zone.map.mapWidth > 100
                            ? Util.nextInt(100, this.zone.map.mapWidth - 100)
                            : Util.nextInt(100);
                    int y = this.zone.map.yPhysicInTop(x, 100);
                    ChangeMapService.gI().changeMap(this, this.zone, x, y);

                } else {
                    int x = this.parentBoss.location.x - (this.lv + 1) * 30;
                    int y = this.zone.map.yPhysicInTop(x, 100);
                    ChangeMapService.gI().changeMap(this, this.zone, x, y);
                }
                this.wakeupAnotherBossWhenAppear();
            } else {
                ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            }
            Service.gI().sendFlagBag(this);
            this.notifyJoinMap();
            this.changeStatus(BossStatus.CHAT_S);
        } catch (Exception e) {
            this.changeStatus(BossStatus.REST);
            if (error < 5) {
                Logger.error("Lỗi : " + e + "\n");
                error++;
            }
        }
    } else {
        this.changeStatus(BossStatus.RESPAWN);
    }
}

    public void joinMapByZone(Zone zone) {
        if (zone != null) {
            this.zone = zone;
            int x = this.zone.map.mapWidth > 100 ? Util.nextInt(100, this.zone.map.mapWidth - 100) : Util.nextInt(100);
            int y = this.zone.map.yPhysicInTop(x, 100);
            ChangeMapService.gI().changeMap(this, this.zone, x, y);
        }
    }

    protected void notifyJoinMap() {
        if (canSendNotify()) {
            ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
        }
    }

    private boolean canSendNotify() {
        return !(this.zone == null || this.isNotifyDisabled || MapService.gI().isMapNoNottify(this.zone.map.mapId));
    }

    @Override
    public boolean chatS() {
        if (Util.canDoWithTime(lastTimeChatS, timeChatS)) {
            if (this.indexChatS == this.data[this.currentLevel].getTextS().length) {
                return true;
            }
            String textChat = this.data[this.currentLevel].getTextS()[this.indexChatS];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.lastTimeChatS = System.currentTimeMillis();
            this.timeChatS = textChat.length() * 100;
            if (this.timeChatS > 2000) {
                this.timeChatS = 2000;
            }
            this.indexChatS++;
        }
        return false;
    }

    @Override
    public void doneChatS() {

    }

    @Override
    public void chatM() {
        if (this.typePk == ConstPlayer.NON_PK) {
            return;
        }
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
    }

    protected long lastTimeAttack;

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void checkPlayerDie(Player player) {
        if (player.isDie()) {

        }
    }

    protected int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else if (skillId == Skill.DRAGON || skillId == Skill.DEMON || skillId == Skill.GALICK || skillId == Skill.LIEN_HOAN || skillId == Skill.KAIOKEN) {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
        return 500;
    }

   @Override
public void die(Player plKill) {
    Player killer = null;

    if (plKill != null) {
        killer = plKill.getMaster() != null ? plKill.getMaster() : plKill;
        this.playerReward = killer;
    }
    this.lastTimeRest = System.currentTimeMillis();

    if (killer != null) {
        reward(killer);
        ServerNotify.gI().notify(killer.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
    }
    sendBossDieNotifyWithRest(killer);

    this.changeStatus(BossStatus.DIE);
}

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        plKill.event.addEventPointBHM(1);
        Service.gI().sendThongBao(plKill, "Bạn đã Đã tiêu diệt được " + this.name + " và nhận 1 điểm Bà Hạt Mít");
    }

    @Override
    public boolean chatE() {
        if (Util.canDoWithTime(lastTimeChatE, timeChatE)) {
            if (this.indexChatE == this.data[this.currentLevel].getTextE().length) {
                return true;
            }
            String textChat = this.data[this.currentLevel].getTextE()[this.indexChatE];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.lastTimeChatE = System.currentTimeMillis();
            this.timeChatE = textChat.length() * 100;
            if (this.timeChatE > 2000) {
                this.timeChatE = 2000;
            }
            this.indexChatE++;
        }
        return false;
    }

    @Override
    public void doneChatE() {

    }

    @Override
    public void leaveMap() {
        if (this.currentLevel < this.data.length - 1) {
            this.lastZone = this.zone;
            this.changeStatus(BossStatus.RESPAWN);
        } else {
            ChangeMapService.gI().exitMap(this);
            this.lastZone = null;
            // DON'T reset lastTimeRest here - it was set at death time and should be preserved for respawn countdown
            this.changeStatus(BossStatus.REST);
        }
        this.wakeupAnotherBossWhenDisappear();
    }

   @Override
public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
    if (!this.isDie()) {
        if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
            this.chat("Xí hụt");
            return 0;
        }

        if (plAtt != null && !plAtt.isBoss) {
            this.lastTimePlayerAttack = System.currentTimeMillis();
            this.hasPlayerAttackSinceSpawn = true;
        }

        if (plAtt != null && plAtt.idNRNM != -1) {
            return 1;
        }

        damage = this.nPoint.subDameInjureWithDeff(damage);

        if (!piercing && effectSkill.isShielding) {
            if (damage > nPoint.hpMax) {
                EffectSkillService.gI().breakShield(this);
            }
            damage = 1;
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
protected void autoResetBossBecauseNoHunter() {
    try {
        if (this.zone != null && canSendNotify()) {
            ServerNotify.gI().notify("BOSS " + this.name + " vừa  " + this.zone.map.mapName);
        }

        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        
        this.lastTimeBossSpawn = 0;
        this.lastTimePlayerAttack = 0;
        this.hasPlayerAttackSinceSpawn = false;
        this.changeStatus(BossStatus.REST);
    } catch (Exception e) {
        this.lastTimeRest = System.currentTimeMillis();
        this.lastTimeBossSpawn = 0;
        this.lastTimePlayerAttack = 0;
        this.hasPlayerAttackSinceSpawn = false;
        this.changeStatus(BossStatus.REST);
    }
}

    @Override
    public void moveToPlayer(Player pl) {
        if (pl.location != null) {
            moveTo(pl.location.x, pl.location.y);
        }
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(40, 60);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y + (Util.isTrue(3, 10) ? -50 : 0));
    }

    public void chat(String text) {
        Service.gI().chat(this, text);
    }

    protected boolean chat(int prefix, String textChat) {
        if (prefix == -1) {
            this.chat(textChat);
        } else if (prefix == -2) {
            if (this.zone != null) {
                Player plMap = this.zone.getRandomPlayerInMap();
                if (plMap != null && !plMap.isDie() && Util.getDistance(this, plMap) <= 600) {
                    Service.gI().chat(plMap, textChat);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (prefix == -3) {
            if (this.parentBoss != null && !this.parentBoss.isDie()) {
                this.parentBoss.chat(textChat);
            }
        } else if (prefix >= 0) {
            if (this.bossAppearTogether != null && this.bossAppearTogether[this.currentLevel] != null) {
                Boss boss = this.bossAppearTogether[this.currentLevel][prefix];
                if (!boss.isDie()) {
                    boss.chat(textChat);
                }
            } else if (this.parentBoss != null && this.parentBoss.bossAppearTogether != null
                    && this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] != null) {
                Boss boss = this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel][prefix];
                if (!boss.isDie()) {
                    boss.chat(textChat);
                }
            }
        }
        return true;
    }
    protected void checkAutoResetBySecondsRest() {
    if (this.zone == null || this.isDie()) {
        return;
    }

    if (this.secondsRest <= 0) {
        return;
    }

    long now = System.currentTimeMillis();
    long resetMillis = this.secondsRest * 1000L;

    if (!this.hasPlayerAttackSinceSpawn) {
        if (now - this.lastTimeBossSpawn >= resetMillis) {
            this.autoResetBossBecauseNoHunter();
        }
        return;
    }

    if (this.lastTimePlayerAttack > 0 && now - this.lastTimePlayerAttack >= resetMillis) {
        this.autoResetBossBecauseNoHunter();
    }
}

    @Override
    public void wakeupAnotherBossWhenAppear() {
        if (!MapService.gI().isMapNoNottify(this.zone.map.mapId)) {
//            System.out.println("BOSS " + this.name + " : " + this.zone.map.mapName + " khu vực " + this.zone.zoneId + "(" + this.zone.map.mapId + ")");
        }
        if (this.bossAppearTogether == null || this.bossAppearTogether[this.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
            int nextLevelBoss = boss.currentLevel + 1;
            if (nextLevelBoss >= boss.data.length) {
                nextLevelBoss = 0;
            }
            if (boss.data[nextLevelBoss].getTypeAppear() == TypeAppear.CALL_BY_ANOTHER) {
                if (boss.zone != null) {
                    boss.leaveMap();
                }
            }
            if (boss.data[nextLevelBoss].getTypeAppear() == TypeAppear.APPEAR_WITH_ANOTHER) {
                if (boss.zone != null) {
                    boss.leaveMap();
                }
                boss.changeStatus(BossStatus.RESPAWN);
            }
        }
    }

    @Override
    public void wakeupAnotherBossWhenDisappear() {
    }

    @Override
    public void autoLeaveMap() {

    }

    public void leaveMapNew() {
        if (this.data != null) {
            this.currentLevel = this.data.length;
        }
        this.changeStatus(BossStatus.LEAVE_MAP);
    }

    @Override
    public void setBom(Player plAtt) {
        try {
            if (!prepareBom) {
                prepareBom = true;
                this.nPoint.hp = 1;
                long lastTime = System.currentTimeMillis();
                //gồng tự sát
                Service.gI().chat(Boss.this, "Rồi, rồi, mày xong rồi!");
                Message msg;
                try {
                    msg = new Message(-45);
                    msg.writer().writeByte(7);
                    msg.writer().writeInt((int) Boss.this.id);
                    msg.writer().writeShort(104);
                    msg.writer().writeShort(2000);
                    Service.gI().sendMessAllPlayerInMap(Boss.this, msg);
                    msg.cleanup();
                } catch (IOException e) {
                }
                while (prepareBom) {
                    if (Util.canDoWithTime(lastTime, 2500)) {
                        setDie(this);
                        die(plAtt);
                        double dame = Util.CrisGH(Boss.this.nPoint.hpMax);
                        for (Mob mob : Boss.this.zone.mobs) {
                            mob.injured(Boss.this, dame, true);
                        }
                        List<Player> playersMap = Boss.this.zone.getNotBosses();
                        if (!MapService.gI().isMapOffline(Boss.this.zone.map.mapId)) {
                            //Sử dụng vòng for lặp ngược để hạn chế lỗi đồng bộ
                            for (int i = playersMap.size() - 1; i >= 0; i--) {
                                Player pl = playersMap.get(i);
                                if (!Boss.this.equals(pl)) {
                                    pl.injured(Boss.this, dame, false, false);
                                    PlayerService.gI().sendInfoHpMpMoney(pl);
                                    Service.gI().Send_Info_NV(pl);
                                }
                            }
                        }
                        prepareBom = false;
                    }
                }
            }
        } catch (Exception e) {
            if (prepareBom) {
                prepareBom = false;
            }
            setDie(this);
            die(plAtt);
        }
    }
    
    //--------------------------------------------------------------------------
    @Override
    public void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }
    
    @Override
    public void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }
    
    @Override
    public Player getAttackPlayer() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack && plAttack != null && plAttack.zone != null && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isPetFollow && !plAttack.isDuongTang) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            try {
                if (plAttack != null && !plAttack.isDie() && plAttack.effectSkin.isVoHinh) {
                    plAttack = null;
                }
                this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
                this.countChangePlayerAttack = 0;
                if (this.zone != null) {
                    plAttack = this.zone.getRandomPlayerInMap();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error Boss : " + this.name);
            }
        }
        return plAttack;
    }
    
    public void setJustRest() {
        this.lastTimeRest = System.currentTimeMillis();
    }
    
    public void die() {
        setJustRest();
        changeStatus(BossStatus.DIE);
    }
    
    public Zone getRandomZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                if (map.type != 0) {
                    zone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
                } else {
                    zone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
                }
            }
        } catch (Exception e) {
        }
        return zone;
    }
    
    
    public void joinMapByZone(Player player) {
        if (player.zone != null) {
            this.zone = player.zone;
            ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, -1);
        }
    }
    
    public void joinMapByZoneWithXY(Zone zone, short x, short y) {
        if (zone != null) {
            this.zone = zone;
            ChangeMapService.gI().changeMap(this, zone, x, y);
        }
    }
        
    public void effectCharger() {
        if (Util.isTrue(100, 100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }
    private void sendBossDieNotifyWithRest(Player plKill) {
    try {
        String bossName = this.name;
        if ((bossName == null || bossName.isEmpty()) && this.data != null && this.data.length > 0) {
            bossName = this.data[0].getName();
        }

        String mapName = "Không rõ";
        if (this.zone != null && this.zone.map != null) {
            mapName = this.zone.map.mapName;
        } else if (this.data != null && this.data.length > 0
                && this.data[0].getMapJoin() != null
                && this.data[0].getMapJoin().length > 0) {
            mapName = "Map " + this.data[0].getMapJoin()[0];
        }

        String killerName = "Chưa có thông tin";
        if (plKill != null && plKill.name != null && !plKill.name.isEmpty()) {
            killerName = plKill.name;
        }

        long dieTimeMs = this.lastTimeRest > 0 ? this.lastTimeRest : System.currentTimeMillis();
        int rest = this.secondsRest;

        String displayText = "Boss " + bossName + " - " + mapName + " - " + killerName;

        Service.gI().sendBossChatVipWithRest(displayText, dieTimeMs, rest);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    

    public long getLastTimeBossSpawn() {
        return this.lastTimeBossSpawn;
    }

    public long getLastTimeRest() {
        return this.lastTimeRest;
    }

    public int getSecondsRest() {
        return this.secondsRest;
    }

    public Player getPlayerReward() {
        return this.playerReward;
    }

    protected Skill getSkillById(int skillId) {
        return SkillUtil.getSkillbyId(this, skillId);
    }

}
