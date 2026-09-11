package nro.player;

import Utils.Logger;
import consts.ConstPlayer;
import nro.services.MapService;
import nro.mob.Mob;
import nro.skill.Skill;
import Utils.SkillUtil;
import nro.services.Service;
import Utils.Util;
import models.Item.ItemTimeService;
import nro.services.PlayerService;
import nro.skill.SkillService;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.UseItem;
import Utils.TimeUtil;
import consts.ConstDetu;
import network.io.Message;
import java.util.List;
import lombok.Getter;
import static nro.player.Player.idOutfitHalloween;
import static nro.player.Player.idOutfitMafuba;
import nro.power.CaptionManager;

public class Detu extends Player {
    
    public byte thuctinh;
    public String getNameThuctinh(byte thuctinh) {
        switch (thuctinh) {
            case 1:
                return "[Nâng Cấp +1] ";
            case 2:
                return "[Nâng Cấp +2] ";
            case 3:
                return "[Nâng Cấp +3] ";
            case 4:
                return "[Nâng Cấp +4] ";
            case 5:
                return "[Nâng Cấp +5] ";
            case 6:
                return "[Nâng Cấp +6] ";
            case 7:
                return "[Nâng Cấp +7] ";
            case 8:
                return "[Nâng Cấp +8] ";
            case 9:
                return "[Nâng Cấp +9] ";
            case 10:
                return "[Tiến Hoá] ";
            case 11:
                return "[Tiến Hoá +1] ";
            case 12:
                return "[Tiến Hoá +2] ";
            case 13:
                return "[Tiến Hoá +3] ";
            case 14:
                return "[Tiến Hoá +4] ";
            case 15:
                return "[Tiến Hoá +5] ";
            case 16:
                return "[Tiến Hoá +6] ";
            case 17:
                return "[Tiến Hoá +7] ";
            case 18:
                return "[Tiến Hoá +8] ";
            case 19:
                return "[Tiến Hoá +9] ";
            case 20:
                return "[Thức Tỉnh] ";
            case 21:
                return "[Thức Tỉnh +1] ";
            case 22:
                return "[Thức Tỉnh +2] ";
            case 23:
                return "[Thức Tỉnh +3] ";
            case 24:
                return "[Thức Tỉnh +4] ";
            case 25:
                return "[Thức Tỉnh +5] ";
            case 26:
                return "[Thức Tỉnh +6] ";
            case 27:
                return "[Thức Tỉnh +7] ";
            case 28:
                return "[Thức Tỉnh +8] ";
            case 29:
                return "[Thức Tỉnh +9] ";
            case 30:
                return "[Thần Linh] ";
            default:
                return "[Chưa tiến cấp] ";
        }
    }

    private static final short ARANGE_CAN_ATTACK = 300;
    private static final short ARANGE_ATT_SKILL1 = 50;

    private static final short[][] PET_ID = {{285, 286, 287}, {288, 289, 290}, {282, 283, 284}, {304, 305, 303}};

    public static final byte FOLLOW = 0;
    public static final byte PROTECT = 1;
    public static final byte ATTACK = 2;
    public static final byte GOHOME = 3;
    public static final byte FUSION = 4;
    public static final byte HTVV = 5;

    public Player master;
    @Getter
    public byte status = 0;

    public byte typeDeTu;
    public boolean isTransform;

    public long lastTimeDie;

    private boolean goingHome;

    private Mob mobAttack;
    private Player playerAttack;

    private static final int TIME_WAIT_AFTER_UNFUSION = 5000;
    private long lastTimeUnfusion;

    private int indexChat = 0;
    private long lastTimeChat;
    
    public int IdBot = 0;

    public Detu(Player master) {
        this.master = master;
        this.isDeTu = true;
    }

    public void changeStatus(byte status) {
        if (goingHome || master.fusion.typeFusion != 0 || (this.isDie() && status == FUSION)) {
            Service.gI().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        Service.gI().chatJustForMe(master, this, getTextStatus(status));
        if (status == GOHOME) {
            goHome();
        } else if (status == FUSION) {
            fusion(false);
        }
        this.status = status;
    }
    
    public String getStrLevel() {
        int level = CaptionManager.getInstance().getLevel(this);
        var cap = CaptionManager.getInstance().findLevel(level);
        var capmax = CaptionManager.getInstance().findLevel(level + 1);
        long maxPower = capmax == null ? 0 : capmax.getPower();
        long clevel = 0;
        if (maxPower != 0) {
            clevel = (this.nPoint.power - cap.getPower()) * 10000 / maxPower;
        }
        String text = cap.getCaption(gender) + " " + clevel / 100 + "%";
        return text;
    }

    public void joinMapMaster() {
        if (status != GOHOME && status != FUSION && !isDie()) {
            this.location.x = master.location.x + Util.nextInt(-10, 10);
            this.location.y = master.location.y;
            if (MapService.gI().isMapNotCanJoinPet(this.master.zone.map.mapId)) {
                ChangeMapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, master.gender + 21, -1));
                return;
            }
            ChangeMapService.gI().goToMap(this, master.zone);
            this.zone.load_Me_To_Another(this);
        }
    }

    public void goHome() {
        if (this.status == GOHOME) {
            return;
        }
        goingHome = true;
        new Thread(() -> {
            try {
                Detu.this.status = Detu.FOLLOW;
                Thread.sleep(2000);
            } catch (Exception e) {
                Logger.logException(Detu.class, e);
            }
            if (master != null) {
                try {
                    ChangeMapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, master.gender + 21, -1));
                } catch (Exception e) {
                }
                this.zone.load_Me_To_Another(this);
                Detu.this.status = Detu.GOHOME;
                goingHome = false;
            }
        }).start();
    }

    private String getTextStatus(byte status) {
        switch (status) {
            case FOLLOW:
                return "Ok con theo sư phụ";
            case PROTECT:
                return "Ok con sẽ bảo vệ sư phụ";
            case ATTACK:
                return "Ok sư phụ để con lo cho";
            case GOHOME:
                return "OK con về, bibi sư phụ";
            case HTVV:
                return "Mong kiếp sau, chúng ta vẫn là thầy trò!";
            default:
                return "Sư phụ ơi con lên cấp rồi";
        }
    }

    public void fusion(boolean porata) {
        if (this.isDie()) {
            Service.gI().sendThongBao(master, "Yêu cầu phải có đệ tử và đệ tử còn sống");
            return;
        }
        if (Util.canDoWithTime(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION)) {
            if (porata) {
                master.fusion.typeFusion = ConstPlayer.HOP_THE_PORATA;
            } else {
                master.fusion.lastTimeFusion = System.currentTimeMillis();
                master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
                ItemTimeService.gI().sendItemTime(master, master.gender == ConstPlayer.NAMEC ? 3901 : 3790, Fusion.TIME_FUSION / 1000);
            }
            this.status = FUSION;
            ChangeMapService.gI().exitMap(this);
            fusionEffect(master.fusion.typeFusion);
            Service.gI().Send_Caitrang(master);
            master.nPoint.calPoint();
            master.nPoint.setFullHpMp();
            Service.gI().point(master);
        } else {
            Service.gI().sendThongBao(this.master, "Vui lòng đợi "
                    + TimeUtil.getTimeLeft(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION / 1000) + " nữa");
        }
    }

    public void fusion2(boolean porata2) {
        if (this.isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        if (Util.canDoWithTime(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION)) {
            if (porata2) {
                master.fusion.typeFusion = ConstPlayer.HOP_THE_PORATA2;
            } else {
                master.fusion.lastTimeFusion = System.currentTimeMillis();
                master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
                ItemTimeService.gI().sendItemTime(master, master.gender == ConstPlayer.NAMEC ? 3901 : 3790, Fusion.TIME_FUSION / 1000);
            }
            this.status = FUSION;
            ChangeMapService.gI().exitMap(this);
            fusionEffect(master.fusion.typeFusion);
            Service.getInstance().Send_Caitrang(master);
            master.nPoint.calPoint();
            master.nPoint.setFullHpMp();
            Service.getInstance().point(master);
        } else {
            Service.getInstance().sendThongBao(this.master, "Vui lòng đợi "
                    + TimeUtil.getTimeLeft(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION / 1000) + " nữa");
        }
    }

    public void fusion3(boolean porata3) {
        if (this.isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        if (Util.canDoWithTime(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION)) {
            if (porata3) {
                master.fusion.typeFusion = ConstPlayer.HOP_THE_PORATA3;
            } else {
                master.fusion.lastTimeFusion = System.currentTimeMillis();
                master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
                ItemTimeService.gI().sendItemTime(master, master.gender == ConstPlayer.NAMEC ? 3901 : 3790, Fusion.TIME_FUSION / 1000);
            }
            this.status = FUSION;
            ChangeMapService.gI().exitMap(this);
            fusionEffect(master.fusion.typeFusion);
            Service.getInstance().Send_Caitrang(master);
            master.nPoint.calPoint();
            master.nPoint.setFullHpMp();
            Service.getInstance().point(master);
        } else {
            Service.getInstance().sendThongBao(this.master, "Vui lòng đợi "
                    + TimeUtil.getTimeLeft(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION / 1000) + " nữa");
        }
    }

    public void fusion4(boolean porata4) {
        if (this.isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        if (Util.canDoWithTime(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION)) {
            if (porata4) {
                master.fusion.typeFusion = ConstPlayer.HOP_THE_PORATA4;
            } else {
                master.fusion.lastTimeFusion = System.currentTimeMillis();
                master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
                ItemTimeService.gI().sendItemTime(master, master.gender == ConstPlayer.NAMEC ? 3901 : 3790, Fusion.TIME_FUSION / 1000);
            }
            this.status = FUSION;
            ChangeMapService.gI().exitMap(this);
            fusionEffect(master.fusion.typeFusion);
            Service.getInstance().Send_Caitrang(master);
            master.nPoint.calPoint();
            master.nPoint.setFullHpMp();
            Service.getInstance().point(master);
        } else {
            Service.getInstance().sendThongBao(this.master, "Vui lòng đợi "
                    + TimeUtil.getTimeLeft(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION / 1000) + " nữa");
        }
    }
    
    public void fusion5(boolean porata5) {
        if (this.isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        if (Util.canDoWithTime(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION)) {
            if (porata5) {
                master.fusion.typeFusion = ConstPlayer.HOP_THE_PORATA5;
            } else {
                master.fusion.lastTimeFusion = System.currentTimeMillis();
                master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
                ItemTimeService.gI().sendItemTime(master, master.gender == ConstPlayer.NAMEC ? 3901 : 3790, Fusion.TIME_FUSION / 1000);
            }
            this.status = FUSION;
            ChangeMapService.gI().exitMap(this);
            fusionEffect(master.fusion.typeFusion);
            Service.getInstance().Send_Caitrang(master);
            master.nPoint.calPoint();
            master.nPoint.setFullHpMp();
            Service.getInstance().point(master);
        } else {
            Service.getInstance().sendThongBao(this.master, "Vui lòng đợi "
                    + TimeUtil.getTimeLeft(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION / 1000) + " nữa");
        }
    }

    public void unFusion() {
        master.fusion.typeFusion = 0;
        this.status = PROTECT;
        Service.gI().point(master);
        joinMapMaster();
        fusionEffect(master.fusion.typeFusion);
        Service.gI().Send_Caitrang(master);
        Service.gI().point(master);
        this.lastTimeUnfusion = System.currentTimeMillis();
    }

    private void fusionEffect(int type) {
        Message msg;
        try {
            msg = new Message(125);
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) master.id);
            Service.gI().sendMessAllPlayerInMap(master, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Detu.class, e);
        }
    }

    public long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (status == GOHOME || status == FUSION || status == HTVV) {
            return;
        }
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.location.x - master.location.x <= 0 ? -1 : 1;
            PlayerService.gI().playerMove(this, master.location.x
                    + (dir == -1 ? 50 : -50), master.location.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
            idle = false;
        }
    }

    private void masterDoesNotAttack() {
        if (Util.canDoWithTime(master.lastTimePlayerNotAttack, master.timeNotAttack)) {
            if (!MapService.gI().isMapOffline(master.zone.map.mapId)) {
                master.doesNotAttack = true;
            }
            master.lastTimePlayerNotAttack = System.currentTimeMillis();
            master.timeNotAttack = Util.nextInt(1800000, 3600000); // random 30p - 1h
        }
    }
    
    private long lastTimeMoveAtHome;
    private byte directAtHome = -1;
    
    @Override
    public void update() {
        try {
            if (this.master != null && this.master.zone != null) {
                super.update();
                increasePoint(); //cộng chỉ số
                updatePower();
                if (this.isDie()) {
                    if (System.currentTimeMillis() - lastTimeDie > Util.nextInt(40000, 50000)) {
                        Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
                    } else {
                        return;
                    }
                }                
                if (this.newSkill != null && this.newSkill.isStartSkillSpecial) {
                    SkillService.gI().newSkillNotFocus(this, 20);
                    return;
                }
                if (justRevived && this.zone == master.zone) {
                    Service.gI().chatJustForMe(master, this, "Sư phụ ơi con đây nè");
                    justRevived = false;
                }

                if (this.zone == null || this.zone != master.zone) {
                    joinMapMaster();
                }
                if (master.isDie() || this.isDie() || effectSkill.isHaveEffectSkill()) {
                    return;
                }
                masterDoesNotAttack();
                moveIdle();
                switch (status) {
                    case FOLLOW:
                        followMaster(60);
                        break;
                    case PROTECT:
                        if (useSkill3() || useSkill4() || useSkill5()) {
                            break;
                        }
                        playerAttack = findPlayerAttack();
                        if (playerAttack != null) {
                            petSay(playerAttack);
                            int disToPlayer = Util.getDistance(this, playerAttack);
                            if (disToPlayer <= ARANGE_ATT_SKILL1) {
                                //đấm
                                this.playerSkill.skillSelect = getSkill(1);
                                if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                    if (SkillService.gI().canUseSkillWithMana(this)) {
                                        PlayerService.gI().playerMove(this, playerAttack.location.x + Util.nextInt(-60, 60), playerAttack.location.y);
                                        SkillService.gI().useSkill(this, playerAttack, null, -1, null);
                                    } else {
                                        askPea();
                                    }
                                }
                            } else {
                                //chưởng
                                this.playerSkill.skillSelect = getSkill(2);
                                if (this.playerSkill.skillSelect.skillId != -1) {
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            SkillService.gI().useSkill(this, playerAttack, null, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                } else {
                                    this.playerSkill.skillSelect = getSkill(1);
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            PlayerService.gI().playerMove(this, playerAttack.location.x + Util.nextInt(-60, 60), playerAttack.location.y);
                                            SkillService.gI().useSkill(this, playerAttack, null, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                }
                            }
                            return;
                        }

                        mobAttack = findMobAttack();
                        if (mobAttack != null) {
                            int disToMob = Util.getDistance(this, mobAttack);
                            if (disToMob <= ARANGE_ATT_SKILL1) {
                                //đấm
                                this.playerSkill.skillSelect = getSkill(1);
                                if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                    if (SkillService.gI().canUseSkillWithMana(this)) {
                                        PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-60, 60), mobAttack.location.y);
                                        SkillService.gI().useSkill(this, null, mobAttack, -1, null);
                                    } else {
                                        askPea();
                                    }
                                }
                            } else {
                                //chưởng
                                this.playerSkill.skillSelect = getSkill(2);
                                if (this.playerSkill.skillSelect.skillId != -1) {
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            SkillService.gI().useSkill(this, null, mobAttack, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                } else {
                                    this.playerSkill.skillSelect = getSkill(1);
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-60, 60), mobAttack.location.y);
                                            SkillService.gI().useSkill(this, null, mobAttack, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                }
                            }

                        } else {
                            idle = true;
                        }

                        break;
                    case ATTACK:
                        if (useSkill3() || useSkill4() || useSkill5()) {
                            break;
                        }
                        playerAttack = findPlayerAttack();
                        if (playerAttack != null) {
                            petSay(playerAttack);
                            int disToPlayer = Util.getDistance(this, playerAttack);
                            if (disToPlayer <= ARANGE_ATT_SKILL1) {
                                //đấm
                                this.playerSkill.skillSelect = getSkill(1);
                                if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                    if (SkillService.gI().canUseSkillWithMana(this)) {
                                        PlayerService.gI().playerMove(this, playerAttack.location.x + Util.nextInt(-60, 60), playerAttack.location.y);
                                        SkillService.gI().useSkill(this, playerAttack, null, -1, null);
                                    } else {
                                        askPea();
                                    }
                                }
                            } else {
                                //chưởng
                                this.playerSkill.skillSelect = getSkill(2);
                                if (this.playerSkill.skillSelect.skillId != -1) {
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            SkillService.gI().useSkill(this, playerAttack, null, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                } else {
                                    this.playerSkill.skillSelect = getSkill(1);
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            PlayerService.gI().playerMove(this, playerAttack.location.x + Util.nextInt(-60, 60), playerAttack.location.y);
                                            SkillService.gI().useSkill(this, playerAttack, null, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        mobAttack = findMobAttack();
                        if (mobAttack != null) {
                            int disToMob = Util.getDistance(this, mobAttack);
                            if (disToMob <= ARANGE_ATT_SKILL1) {
                                this.playerSkill.skillSelect = getSkill(1);
                                if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                    if (SkillService.gI().canUseSkillWithMana(this)) {
                                        PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                                        SkillService.gI().useSkill(this, playerAttack, mobAttack, -1, null);
                                    } else {
                                        askPea();
                                    }
                                }
                            } else {
                                this.playerSkill.skillSelect = getSkill(2);
                                if (this.playerSkill.skillSelect.skillId != -1) {
                                    if (SkillService.gI().canUseSkillWithMana(this)) {
                                        PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                                        SkillService.gI().useSkill(this, playerAttack, mobAttack, -1, null);
                                    }
                                } else {
                                    this.playerSkill.skillSelect = getSkill(1);
                                    if (SkillService.gI().canUseSkillWithCooldown(this) && canAttack()) {
                                        if (SkillService.gI().canUseSkillWithMana(this)) {
                                            PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                                            SkillService.gI().useSkill(this, playerAttack, mobAttack, -1, null);
                                        } else {
                                            askPea();
                                        }
                                    }
                                }
                            }

                        } else {
                            idle = true;
                        }
                        break;

                    case GOHOME:
                        if (this.zone != null) {
                            int mapId = this.zone.map.mapId;

                            if (mapId == 21 || mapId == 22 || mapId == 23) {
                                if (System.currentTimeMillis() - lastTimeMoveAtHome <= 5000) {
                                    return;
                                }

                                int x = 0;
                                int y = 336;

                                switch (mapId) {
                                    case 21:
                                        x = (directAtHome == -1) ? 250 : 200;
                                        directAtHome *= -1;
                                        break;
                                    case 22:
                                        x = (directAtHome == -1) ? 500 : 452;
                                        directAtHome *= -1;
                                        break;
                                    case 23:
                                        x = (directAtHome == -1) ? 250 : 200;
                                        directAtHome *= -1;
                                        break;
                                }

                                PlayerService.gI().playerMove(this, x, y);
                                Service.gI().chatJustForMe(master, this, "Là do bạn không chơi đồ đấy bạn ạ!");
                                lastTimeMoveAtHome = System.currentTimeMillis();
                            }
                        }
                        break;
                    case HTVV:
                        if (master.baovetaikhoan) {
                            Service.gI().sendThongBao(master, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
                            master.Detu.changeStatus(Detu.FOLLOW);
                            return;
                        }
                        if (master.gender == 1) {
                            fusionEffect(ConstPlayer.LUONG_LONG_NHAT_THE);
                            ChangeMapService.gI().exitMap(this);
                            Service.gI().addSMTN(master, (byte) 1, this.nPoint.power, true);
                            master.Detu = null;
                            Service.gI().sendHavePet(master);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Logger.logException(Detu.class, e);
        }
    }

    private long lastTimeAskPea;

    public void askPea() {
        if (Util.canDoWithTime(lastTimeAskPea, 10000)) {
            if (this.master.isDeTu) {
                if (this != null && !this.isDie()) {
                    int statima = 100 * 10;
                    long hpKiHoiPhuc = 100000;
                    this.nPoint.stamina += statima;
                    if (this.nPoint.stamina > this.nPoint.maxStamina) {
                        this.nPoint.stamina = this.nPoint.maxStamina;
                    }
                    this.nPoint.setHp(Util.CrisGH(this.nPoint.hp + hpKiHoiPhuc));
                    this.nPoint.setMp(Util.CrisGH(this.nPoint.mp + hpKiHoiPhuc));
                    Service.gI().sendInfoPlayerEatPea(this);
                }
                lastTimeAskPea = System.currentTimeMillis();
                return;
            }
            Service.gI().chatJustForMe(master, this, "Sư phụ ơi cho con đậu thần");
            UseItem.gI().eatPea(master);
            lastTimeAskPea = System.currentTimeMillis();
        }
    }

    private int countTTNL;
    
    private boolean useSkill3() {
        try {
            playerSkill.skillSelect = getSkill(3);
            if (playerSkill.skillSelect.skillId == -1) {
                return false;
            }
            switch (this.playerSkill.skillSelect.template.id) {
                case Skill.THAI_DUONG_HA_SAN:
                    if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                        SkillService.gI().useSkill(this, null, null, -1, null);
                        Service.gI().chatJustForMe(master, this, "Thái dương hạ san!");
                        return true;
                    }
                    return false;
                case Skill.TAI_TAO_NANG_LUONG:
                    if (this.effectSkill.isCharging && this.countTTNL < Util.nextInt(3, 5)) {
                        this.countTTNL++;
                        return true;
                    }
                    if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)
                            && (this.nPoint.getCurrPercentHP() <= 20 || this.nPoint.getCurrPercentMP() <= 20)) {
                        SkillService.gI().useSkill(this, null, null, -1, null);
                        this.countTTNL = 0;
                        return true;
                    }
                    return false;
                case Skill.KAIOKEN:
                    if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {

                        mobAttack = this.findMobAttack();
                        playerAttack = this.findPlayerAttack();
                        if (playerAttack != null) {
                            mobAttack = null;
                            int dis = Util.getDistance(this, playerAttack);
                            if (dis > ARANGE_ATT_SKILL1) {
                                PlayerService.gI().playerMove(this, playerAttack.location.x, playerAttack.location.y);
                            } else {
                                if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                                    PlayerService.gI().playerMove(this, playerAttack.location.x + Util.nextInt(-20, 20), playerAttack.location.y);
                                }
                            }
                        } else if (mobAttack == null) {
                            return false;
                        }
                        if (mobAttack != null) {
                            int dis = Util.getDistance(this, mobAttack);
                            if (dis > ARANGE_ATT_SKILL1) {
                                PlayerService.gI().playerMove(this, mobAttack.location.x, mobAttack.location.y);
                            } else {
                                if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                                    PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                                }
                            }
                        }

                        SkillService.gI().useSkill(this, playerAttack, mobAttack, -1, null);
                        getSkill(1).lastTimeUseThisSkill = System.currentTimeMillis();
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean useSkill4() {
        try {
            this.playerSkill.skillSelect = getSkill(4);
            if (this.playerSkill.skillSelect.skillId == -1) {
                return false;
            }
            switch (this.playerSkill.skillSelect.template.id) {
                case Skill.BIEN_KHI:
                    if (!this.effectSkill.isMonkey && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                        SkillService.gI().useSkill(this, null, null, -1, null);
                        return true;
                    }
                    return false;
                case Skill.KHIEN_NANG_LUONG:
                    if (!this.effectSkill.isShielding && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                        SkillService.gI().useSkill(this, null, null, -1, null);
                        return true;
                    }
                    return false;
                case Skill.DE_TRUNG:
                    if (this.DeTrung == null && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                        SkillService.gI().useSkill(this, null, null, -1, null);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean useSkill5() {
        try {
            this.playerSkill.skillSelect = getSkill(5);
            if (this.playerSkill.skillSelect.skillId == -1) {
                return false;
            }
            if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                Player plAtt = findPlayerAttack();
                Mob mobAtt = findMobAttack();
                if (plAtt != null) {
                    mobAtt = null;
                } else if (mobAtt == null) {
                    return false;
                }
                SkillService.gI().useSkill(this, plAtt, mobAtt, -1, null);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private long lastTimeIncreasePoint;
    // cộng chỉ số đệ tử

//    private void increasePoint() {
//        if (this.nPoint != null && Util.canDoWithTime(lastTimeIncreasePoint, 100)) {
//            if (status != FUSION) {
//                int tn = 2;
//                if (Util.isTrue(70, 100)) {
//                    this.nPoint.increasePoint((byte) Util.nextInt(3, 4), (short) Util.nextInt(1, tn), false);
//                } else {
//                    this.nPoint.increasePoint((byte) Util.nextInt(0, 2), (short) Util.nextInt(1, tn), false);
//                }
//                lastTimeIncreasePoint = System.currentTimeMillis();
//            }
//        }
//    }
 private void increasePoint() {
    if (this.nPoint != null && Util.canDoWithTime(lastTimeIncreasePoint, 0)) {
        for (int i = 0; i < 20; i++) {
            this.nPoint.increasePoint(
                (byte) Util.nextInt(0, 4),
                (short) 1,                
                false
            );
        }
        lastTimeIncreasePoint = System.currentTimeMillis();
    }
}
    public void followMaster() {
        if (this.isDie() || effectSkill.isHaveEffectSkill()) {
            return;
        }
        switch (this.status) {
            case ATTACK:
                if ((mobAttack != null && Util.getDistance(this, master) <= 1500)) {
                    break;
                }
            case FOLLOW:
            case PROTECT:
                followMaster(40);
                break;
        }
    }

    private void followMaster(int dis) {
        int mX = master.location.x;
        int mY = master.location.y;
        int disX = this.location.x - mX;
        if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= dis) {
            if (disX < 0) {
                this.location.x = mX - Util.nextInt(0, dis);
            } else {
                this.location.x = mX + Util.nextInt(0, dis);
            }
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }

    public short getAvatar() {
        switch (this.typeDeTu) {
            case ConstDetu.MABU:
                return 297;
            case ConstDetu.U_BU:
                return 946;
            case ConstDetu.KID_JIREN:
                return 876;
            case ConstDetu.KID_BEER:
                return 1422;
            case ConstDetu.BLACK:
                return 1914;
            default:
                return PET_ID[3][this.gender];
        }
    }

    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][0];
        }
        if (effectSkill != null && effectSkill.isStone) {
            return 454;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][0];
        }
        if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 406;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 760;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1210;
        } else if (this.typeDeTu == ConstDetu.MABU && !this.isTransform) {
            return 297;
        } else if (this.typeDeTu == ConstDetu.U_BU && !this.isTransform) {
            return 946;
        } else if (this.typeDeTu == ConstDetu.KID_JIREN && !this.isTransform) {
            return 876;
        } else if (this.typeDeTu == ConstDetu.KID_BEER && !this.isTransform) {
            return 1422;
        } else if (this.typeDeTu == ConstDetu.BLACK && !this.isTransform) {
            return 1914;
        } else if (inventory.itemsBody.get(5).isNotNullItem()) {
            int part = inventory.itemsBody.get(5).template.head;
            if (part != -1) {
                return (short) part;
            }
        }
        if (this.IdBot == 1 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 353;
        } else if (this.IdBot == 2 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 264;
        } else if (this.IdBot == 3 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 234;
        } else if (this.IdBot == 4 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 736;
        } else if (this.IdBot == 5 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 252;
        } else if (this.IdBot == 6 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 499;
        } else if (this.IdBot == 7 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 709;
        } else if (this.IdBot == 8 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 712;
        } else if (this.IdBot == 9 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 950;
        }
        if (this.nPoint.power < 1500000) {
            return PET_ID[this.gender][0];
        } else {
            return PET_ID[3][this.gender];
        }
    }

    @Override
    public short getBody() {
        if (effectSkill.isMonkey) {
            return 193;
        } 
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][1];
        }
        if (effectSkill != null && effectSkill.isStone) {
            return 455;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][1];
        }
        if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 407;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 761;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1211;
        } else if (this.typeDeTu == ConstDetu.MABU && !this.isTransform) {
            return 298;
        } else if (this.typeDeTu == ConstDetu.U_BU && !this.isTransform) {
            return 947;
        } else if (this.typeDeTu == ConstDetu.KID_JIREN && !this.isTransform) {
            return 877;
        } else if (this.typeDeTu == ConstDetu.KID_BEER && !this.isTransform) {
            return 1423;
         } else if (this.typeDeTu == ConstDetu.BLACK && !this.isTransform) {
            return 1915;
        } else if (inventory.itemsBody.get(5).isNotNullItem()) {
            int body = inventory.itemsBody.get(5).template.body;
            if (body != -1) {
                return (short) body;
            }
        }
        if (inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        if (this.IdBot == 1 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 354;
        } else if (this.IdBot == 2 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 265;
        } else if (this.IdBot == 3 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 235;
        } else if (this.IdBot == 4 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 737;
        } else if (this.IdBot == 5 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 253;
        } else if (this.IdBot == 6 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 500;
        } else if (this.IdBot == 7 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 710;
        } else if (this.IdBot == 8 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 713;
        } else if (this.IdBot == 9 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 951;
        }
        if (this.nPoint.power < 1500000) {
            return PET_ID[this.gender][1];
        } else {
            return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
        }
    }

    @Override
    public short getLeg() {
        if (effectSkill.isMonkey) {
            return 194;
        }
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][2];
        }
        if (effectSkill != null && effectSkill.isStone) {
            return 456;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][2];
        }
        if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 408;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 762;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1212;
        } else if (this.typeDeTu == ConstDetu.MABU && !this.isTransform) {
            return 299;
        } else if (this.typeDeTu == ConstDetu.U_BU && !this.isTransform) {
            return 948;
        } else if (this.typeDeTu == ConstDetu.KID_JIREN && !this.isTransform) {
            return 878;
        } else if (this.typeDeTu == ConstDetu.KID_BEER && !this.isTransform) {
            return 1424;
        } else if (this.typeDeTu == ConstDetu.BLACK && !this.isTransform) {
            return 1916;
        } else if (inventory.itemsBody.get(5).isNotNullItem()) {
            int leg = inventory.itemsBody.get(5).template.leg;
            if (leg != -1) {
                return (short) leg;
            }
        }
        if (inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        if (this.IdBot == 1 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 355;
        } else if (this.IdBot == 2 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 266;
        } else if (this.IdBot == 3 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 236;
        } else if (this.IdBot == 4 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 738;
        } else if (this.IdBot == 5 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 254;
        } else if (this.IdBot == 6 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 501;
        } else if (this.IdBot == 7 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 711;
        } else if (this.IdBot == 8 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 714;
        } else if (this.IdBot == 9 && this.nPoint.power > 1_500_000 && this.master.isBot_New) {
            return 952;
        }
        if (this.nPoint.power < 1500000) {
            return PET_ID[this.gender][2];
        } else {
            return (short) (gender == ConstPlayer.NAMEC ? 60 : 58);
        }
    }
    
    //--------------------------------------------------------------------------
    
    private Player findPlayerAttack() {
        List<Player> playersMap = zone.getHumanoids();
        int dis = ARANGE_CAN_ATTACK;
        Player plAtt = null;

        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            if (!cantAttack(pl)) {
                int d = Util.getDistance(this, pl);
                if (d <= dis) {
                    dis = d;
                    plAtt = pl;
                }
            }
        }

        return plAtt;
    }

    private boolean cantAttack(Player player) {
        return player == null || player.location == null || player.isDie() || Util.getDistance(this, player) > 500 || this.equals(player) || 
        player.equals(master) || (!temporaryEnemies.contains(player) && !master.temporaryEnemies.contains(player)) || (!SkillService.gI().canAttackPlayer(this, player));
    }

    private Mob findMobAttack() {
        int dis = ARANGE_CAN_ATTACK;
        Mob mobAtt = null;
        for (Mob mob : zone.mobs) {
            if (mob.isDie()) {
                continue;
            }
            int d = Util.getDistance(this, mob);
            if (d <= dis) {
                dis = d;
                mobAtt = mob;
            }
        }
        return mobAtt;
    }
    
    private void updatePower() {
        if (this.playerSkill != null) {
            switch (this.playerSkill.getSizeSkill()) {
                case 1: {
                    if (this.nPoint.power >= 150_000_000) {
                        openSkill2();
                    }
                    break;
                }
                case 2: {
                    if (this.nPoint.power >= 1_500_000_000) {
                        openSkill3();
                    }
                    break;
                }
                case 3: {
                    if (this.nPoint.power >= 20_000_000_000L) {
                        openSkill4();
                    }
                    break;
                }
                case 4: {
                    if (this.nPoint.power >= 60_000_000_000L && (this.typeDeTu == ConstDetu.U_BU || this.typeDeTu == ConstDetu.KID_JIREN || this.typeDeTu == ConstDetu.KID_BEER || this.typeDeTu == ConstDetu.BLACK)) {
                        openSkill5();
                    }
                    break;
                }
            }
        }
    }
    
    public void openSkill1() {
        Skill skill = null;
        int tiLeDragon = 30;
        int tiLeGalick = 30;
        int tiLeDemon = 40;
        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeDragon) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.DRAGON, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.DRAGON, 1);
            }
        } else if (rd <= tiLeDragon + tiLeGalick) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.GALICK, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.GALICK, 1);
            }
        } else if (rd <= tiLeDragon + tiLeGalick + tiLeDemon) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.DEMON, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.DEMON, 1);
            }
        }
        this.playerSkill.skills.set(0, skill);
    }

    public void openSkill2() {
        Skill skill = null;
        int tiLeKame = 30;
        int tiLeMasenko = 40;
        int tiLeAntomic = 30;
        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeKame) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.KAMEJOKO, (this.master.isBot_New ? 7 : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.KAMEJOKO, 1);
            }
        } else if (rd <= tiLeKame + tiLeMasenko) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.MASENKO, (this.master.isBot_New ? 7 : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.MASENKO, 1);
            }
        } else if (rd <= tiLeKame + tiLeMasenko + tiLeAntomic) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.ANTOMIC, (this.master.isBot_New ? 7 : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.ANTOMIC, 1);
            }
        }
        skill.coolDown = 1000;
        this.playerSkill.skills.set(1, skill);
    }

    public void openSkill3() {
        Skill skill = null;
        int tiLeTDHS = 30;
        int tiLeTTNL = 30;
        int tiLeKOK = 40;
        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeTDHS) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1);
            }
        } else if (rd <= tiLeTDHS + tiLeTTNL) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1);
            }
        } else if (rd <= tiLeTDHS + tiLeTTNL + tiLeKOK) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.KAIOKEN, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.KAIOKEN, 1);
            }
        }
        this.playerSkill.skills.set(2, skill);
    }

    public void openSkill4() {
        Skill skill = null;
        int tiLeBienKhi = 30;
        int tiLeDeTrung = 30;
        int tiLeKNL = 40;
        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeBienKhi) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.BIEN_KHI, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.BIEN_KHI, 1);
            }
        } else if (rd <= tiLeBienKhi + tiLeDeTrung) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.DE_TRUNG, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.DE_TRUNG, 1);
            }
        } else if (rd <= tiLeBienKhi + tiLeDeTrung + tiLeKNL) {
            if (this.master.isBot_New == true) {
                skill = SkillUtil.createSkill(Skill.KHIEN_NANG_LUONG, (this.master.isBot_New ? Util.nextInt(1, 7) : 1));
            } else {
                skill = SkillUtil.createSkill(Skill.KHIEN_NANG_LUONG, 1);
            }
        }
        this.playerSkill.skills.set(3, skill);
    }
    
    public void openSkill5() {
        int idSkill[] = {Skill.SUPER_KAME, Skill.MA_PHONG_BA, Skill.LIEN_HOAN_CHUONG};
        Skill skill = SkillUtil.createSkill(idSkill[Util.nextInt(idSkill.length)], 1);
        this.playerSkill.skills.set(4, skill);
    }

    private Skill getSkill(int indexSkill) {
        return this.playerSkill.skills.get(indexSkill - 1);
    }

    public void transform() {
        if (this.typeDeTu == ConstDetu.MABU) {
            this.isTransform = !this.isTransform;
            Service.gI().Send_Caitrang(this);
            Service.gI().chat(this, "Ai Am Bư !! Bư..Bư..Bư..Ma..Nhân..Bư....");
        }
        if (this.typeDeTu == ConstDetu.U_BU) {
            this.isTransform = !this.isTransform;
            Service.gI().Send_Caitrang(this);
        }
        if (this.typeDeTu == ConstDetu.KID_JIREN) {
            this.isTransform = !this.isTransform;
            Service.gI().Send_Caitrang(this);
        }
        if (this.typeDeTu == ConstDetu.KID_BEER) {
            this.isTransform = !this.isTransform;
            Service.gI().Send_Caitrang(this);
        }
    }
    
    public long lastTimeAskAttack;

    public boolean canAttack() {
        if (this.master.isPl() && this.master.doesNotAttack && this.master.charms.tdDeTu < System.currentTimeMillis()) {
            if (Util.canDoWithTime(lastTimeAskAttack, 10000)) {
                Service.gI().chatJustForMe(master, this, "Sao sư phụ không đánh đi?");
                lastTimeAskAttack = System.currentTimeMillis();
            }
            return false;
        }
        return true;
    }
    
    public void petSay(Player player) {
        if (Util.canDoWithTime(lastTimeChat, indexChat == 0 ? 15000 : 1500)) {
            String[] chat = {"Mi làm ta nổi giận rồi " + player.name.replace("$", "")};
            Service.gI().chat(this, chat[indexChat]);
            indexChat = (indexChat + 1) % chat.length;
            lastTimeChat = System.currentTimeMillis();
        }
    }

    @Override
    public void dispose() {
        if (zone != null) {
            ChangeMapService.gI().exitMap(master);
        }
        this.mobAttack = null;
        this.master = null;
        super.dispose();
    }
}
