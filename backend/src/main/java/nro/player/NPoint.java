package nro.player;

import nro.effect.EffectSkillService;
import consts.ConstPlayer;
import consts.ConstRatio;
import nro.intrinsic.Intrinsic;
import models.Item.Item;
import models.Item.ItemService;
import nro.skill.Skill;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstAttribute;
import event.EventManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jbcd.dao.EventDAO;
import lombok.Setter;
import models.Item.ItemOption;
import models.Reward.RewardBlackBall;
import nro.attribute.Attribute;
import nro.badges.BagesTemplate;
import nro.boss.map.TrainingBoss.TopKillWhisManager;
import nro.mob.Mob;
import nro.card.Card;
import nro.card.OptionCard;
import nro.clan.Buff;
import nro.inventory.InventoryService;
import nro.power.PowerLimit;
import nro.power.PowerLimitManager;

public class NPoint {

    public static final byte MAX_LIMIT = 13;

    @Setter
    private Player player;

    public NPoint(Player player) {
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.tlTNSM = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
        this.tlSpeed = new ArrayList<>();
    }

    public boolean isCrit;
    public boolean isCrit100;
    public boolean isCritTele;

    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public long dameAfter;
    public PowerLimit powerLimit;
    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public int stamina, maxStamina;

    public byte limitPower;
    public long power;
    public long tiemNang;

    public long hp, hpMax, hpg;
    public long mp, mpMax, mpg;
    public long dame, dameg;
    public int def, defg;
    public int crit, critg;
    public byte speed = 5;

    public boolean teleport;

    public boolean khangTDHS;

    public boolean KhangHoaXuong;

    public boolean isHoaBiNgoXungQuanh;

    public boolean wearingMabu;
    public boolean wearingBuiBui;

    public boolean IsTacDungHopThe;

    public void initPowerLimit() {
        powerLimit = PowerLimitManager.getInstance().get(limitPower);
    }

    public List<Integer> tlSpeed;

    /**
     * Tỉ lệ may mắn
     */
    public int tlMayman;

    /**
     * Chỉ số cộng thêm
     */
    public int hpAdd, mpAdd, dameAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd;

    /**
     * //+#% sức đánh chí mạng
     */
    public List<Integer> tlDameCrit;
    public int tlSDCM;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public long hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public int tlHpHoi, tlMpHoi;

    public int tlHpHoiBanthan_DongMinh;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public int tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public int tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public int tlHutHpMpXQ;

    public int tlFixStun;

    public int tlCuteAddame;

    /**
     * Tỉ lệ phản sát thương
     */
    public int tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;

    /**
     * Tỉ lệ vàng cộng thêm
     */
    public int tlGold;

    /**
     * Tỉ lệ né đòn
     */
    public int tlNeDon;

    public int tlBom;

    public int tlGiap;

    public int tlxgcc;

    public int tlxgc;

    public int tlchinhxac;

    public int tlstc;

    /**
     * Cộng Dồn
     */
    public int tlCongDonSD;

    public int tlSubDamePercenMp20;

    /**
     * Tấn công lên tộc
     */
    public int tlTanCongTocTraiDat;

    public int tlTanCongTocNamec;

    public int tlTanCongTocXayda;

    /**
     * Giảm sát thương lên tộc
     */
    public int tlGiamSatThuongTraiDat;

    public int tlGiamSatThuongNamec;

    public int tlGiamSatThuongXayda;

    /**
     * Tăng chỉ số khi ở gần thành viên bang hội
     */
    public int tlDameClan;

    public int tlHpClan;

    public int tlMpClan;

    /**
     * Tăng tấn công lên Boss
     */
    public int tlDameBoss;

    /**
     * Tăng tấn công lên Mob
     */
    public int tlDameMobRun;

    public int tlDameMobMonkey;

    public int tlDameMobFly;

    //Phân Tâm
    public boolean isXinbato;

    //Biến cà rot
    public boolean isThoDaiCa;

    public int tlTNSMPet;
    public int xChuong;

    public int setTinhAn;
    public int setNhatAn;
    public int setNguyetAn;

    /**
     * Tỉ lệ sức đánh đẹp cộng thêm cho bản thân và người xung quanh
     */
    public int tlSexyDame;

    public int tlCoolDame;

    /**
     * Tỉ lệ giảm sức đánh
     */
    public int tlSubSD;
    public int tlSubHP;
    public int tlSubMP;

    public int voHieuChuong;

    /*------------------------Effect skin-------------------------------------*/
    public Item trainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;
    public boolean isFounder;
    public boolean isTinhAn;
    public boolean isNhatAn;
    public boolean isNguyetAn;
    public boolean isTanHinh;
    public boolean isHoaDa;
    public boolean isDoSPL;
    public boolean isThoBulma;
    public boolean isBunmaTocMau;
    public boolean isTiecBaiBien;
    public int tlHpGiamODo;

    public boolean isGogeta;

    public boolean Cong20ExpKhiAttackMob;

    public int levelBT;

    /*-------------------------------------------------------------------------*/
    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        if (this.player.Detu != null) {
            this.player.Detu.nPoint.setPointWhenWearClothes();
        }
        this.setPointWhenWearClothes();
    }

    private void setPointWhenWearClothes() {
        resetPoint();
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            tlHutHp += RewardBlackBall.R3S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R4S_2;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            tlDameCrit.add(RewardBlackBall.R5S_1);
            tlSDCM += RewardBlackBall.R5S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[6] > System.currentTimeMillis()) {
            tlNeDon += RewardBlackBall.R7S_1;
        }

        Card card = player.Cards.stream().filter(r -> r != null && r.Used == 1).findFirst().orElse(null);
        if (card != null) {
            for (OptionCard io : card.Options) {
                if (io.active == card.Level || (card.Level == -1 && io.active == 0)) {
                    switch (io.id) {
                        case 0: //Tấn công +#
                            this.dameAdd += io.param;
                            break;
                        case 2: //HP, KI+#000
                            this.hpAdd += io.param * 1000;
                            this.mpAdd += io.param * 1000;
                            break;
                        case 3:// vô hiệu chưởng
                            this.voHieuChuong += io.param;
                            break;
                        case 5: //+#% sức đánh chí mạng
                            this.tlDameCrit.add(io.param);
                            this.tlSDCM += io.param;
                            break;
                        case 6: //HP+#
                            this.hpAdd += io.param;
                            break;
                        case 7: //KI+#
                            this.mpAdd += io.param;
                            break;
                        case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                            this.tlHutHpMpXQ += io.param;
                            break;
                        case 10:
                            this.tlstc += io.param;
                            break;
                        case 14: //Chí mạng+#%
                        case 192:
                            this.critAdd += io.param;
                            break;
                        case 16: // Speed
                        case 114:
                        case 148:
                            this.tlSpeed.add(io.param);
                            break;
                        case 18: //Chinh xac
                            this.tlchinhxac += io.param;
                            break;
                        case 19: //Tấn công+#% khi đánh quái
                            this.tlDameAttMob.add(io.param);
                            break;
                        case 22: //HP+#K
                            this.hpAdd += io.param * 1000;
                            break;
                        case 23: //MP+#K
                            this.mpAdd += io.param * 1000;
                            break;
                        case 24:
                            this.wearingBuiBui = true;
                            break;
                        case 27: //+# HP/30s
                            this.hpHoiAdd += io.param;
                            break;
                        case 28: //+# KI/30s
                            this.mpHoiAdd += io.param;
                            break;
                        case 29:
                            this.wearingMabu = true;
                            break;
                        case 33: //dịch chuyển tức thời
                            this.teleport = true;
                            break;
                        case 34:
                            this.setTinhAn += 1;
                            break;
                        case 35:
                            this.setNguyetAn += 1;
                            break;
                        case 36:
                            this.setNhatAn += 1;
                            break;
                        case 47: //Giáp+#
                            this.defAdd += io.param;
                            break;
                        case 48: //HP/KI+#
                            this.hpAdd += io.param;
                            this.mpAdd += io.param;
                            break;
                        case 49: //Tấn công+#%
                        case 50: //Sức đánh+#%
                            this.tlDame.add(io.param);
                            break;
                        case 77: //HP+#%
                            this.tlHp.add(io.param);
                            break;
                        case 80: //HP+#%/30s
                            this.tlHpHoi += io.param;
                            break;
                        case 81: //MP+#%/30s
                            this.tlMpHoi += io.param;
                            break;
                        case 83:
                            this.Cong20ExpKhiAttackMob = true;
                            break;
                        case 88: //Cộng #% exp khi đánh quái
                            this.tlTNSM.add(io.param);
                            break;
                        case 94: //Giáp #%
                            this.tlGiap += io.param;
                            break;
                        case 95: //Biến #% tấn công thành HP
                            this.tlHutHp += io.param;
                            break;
                        case 96: //Biến #% tấn công thành MP
                            this.tlHutMp += io.param;
                            break;
                        case 97: //Phản #% sát thương
                            this.tlPST += io.param;
                            break;
                        case 98: //Xuyen giap chuong
                            this.tlxgc += io.param;
                            break;
                        case 99: //Xuyen giap can chien
                            this.tlxgcc += io.param;
                            break;
                        case 100: //+#% vàng từ quái
                            this.tlGold += io.param;
                            break;
                        case 101: //+#% TN,SM
                            this.tlTNSM.add(io.param);
                            break;
                        case 103: //KI +#%
                            this.tlMp.add(io.param);
                            break;
                        case 104: //Biến #% tấn công quái thành HP
                            this.tlHutHpMob += io.param;
                            break;
                        case 105: //Vô hình khi không đánh quái và boss
                            this.wearingVoHinh = true;
                            break;
                        case 106: //Không ảnh hưởng bởi cái lạnh
                            this.isKhongLanh = true;
                            break;
                        case 108: //#% Né đòn
                            this.tlNeDon += io.param;
                            break;
                        case 109: //Hôi, giảm #% HP
                            this.tlHpGiamODo += io.param;
                            break;
                        case 116: //Kháng thái dương hạ san
                            this.khangTDHS = true;
                            break;
                        case 153:
                            this.tlBom += io.param;
                            break;
                        case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                            if (io.param > this.tlSexyDame) {
                                this.tlSexyDame = io.param;
                            }
                            break;
                        case 147: //+#% sức đánh
                            this.tlDame.add(io.param);
                            break;
                        case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                            this.tlSubSD += 50;
                            this.tlSubHP += 50;
                            this.tlSubMP += 50;
                            this.tlTNSM.add(io.param);
                            this.tlGold += io.param;
                            break;
                        case 156:
                            this.tlCongDonSD += io.param;
                            break;
                        case 157:
                            this.tlSubDamePercenMp20 += io.param;
                            break;
                        case 162: //Cute hồi #% KI/s bản thân và xung quanh
                            this.mpHoiCute += io.param;
                            break;
                        case 163:
                            this.isHoaBiNgoXungQuanh = true;
                            break;
                        case 173: //Phục hồi #% HP và KI cho đồng đội
                            this.tlHpHoiBanThanVaDongDoi += io.param;
                            this.tlMpHoiBanThanVaDongDoi += io.param;
                            break;
                        case 236: //may mắn
                            this.tlMayman += io.param;
                            break;
                        case 258:
                            if (io.param > this.tlCoolDame) {
                                this.tlCoolDame = io.param;
                            }
                            break;
                        case 259: //HP+#%/10s
                            this.tlHpHoiBanthan_DongMinh += io.param;
                            break;
                        case 226:
                            if (io.param > this.tlCuteAddame) {
                                this.tlCuteAddame = io.param;
                            }
                            break;
                        case 227:
                            this.tlFixStun += io.param;
                            break;
                    }
                }
            }
        }

        // Bông tai cấp 2
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.player.inventory.itemsBag.stream().filter(it -> it.isNotNullItem() && it.template.id == 921).findFirst().ifPresent(btc2 -> {
                for (ItemOption io : btc2.itemOptions) {
                    addOption(io);
                    if (io.optionTemplate.id == 72) {
                        this.levelBT = io.param;
                    }
                }
            });
        }
        // Bông tai cấp 3
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.player.inventory.itemsBag.stream().filter(it -> it.isNotNullItem() && it.template.id == 1943).findFirst().ifPresent(btc3 -> {
                for (ItemOption io : btc3.itemOptions) {
                    addOption(io);
                    if (io.optionTemplate.id == 72) {
                        this.levelBT = io.param;
                    }
                }
            });
        }

        if (BagesTemplate.sendListItemOption(player) != null) {
            for (ItemOption io : BagesTemplate.sendListItemOption(player)) {
                addOption(io);
            }
        }

        this.player.setClothes.worldcup = 0;
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 966:
                    case 982:
                    case 983:
                    case 883:
                    case 904:
                        player.setClothes.worldcup++;
                }
                if (item.template.id >= 592 && item.template.id <= 594) {
                    teleport = true;
                }
                for (ItemOption io : item.itemOptions) {
                    addOption(io);
                }
            }
        }
        setDameTrainArmor();
        setBasePoint();
        setOutfitFusion();
    }

    private void addOption(ItemOption io) {
        switch (io.optionTemplate.id) {
            case 0: //Tấn công +#
                this.dameAdd += io.param;
                break;
            case 2: //HP, KI+#000
                this.hpAdd += io.param * 1000;
                this.mpAdd += io.param * 1000;
                break;
            case 3:// vô hiệu chưởng
                this.voHieuChuong += io.param;
                break;
            case 5: //+#% sức đánh chí mạng
                this.tlDameCrit.add(io.param);
                this.tlSDCM += io.param;
                break;
            case 6: //HP+#
                this.hpAdd += io.param;
                break;
            case 7: //KI+#
                this.mpAdd += io.param;
                break;
            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                this.tlHutHpMpXQ += io.param;
                break;
            case 10:
                this.tlstc += io.param;
                break;
            case 14: //Chí mạng+#%
            case 192:
                this.critAdd += io.param;
                break;
            case 16: // Speed
            case 114:
            case 148:
                this.tlSpeed.add(io.param);
                break;
            case 18: //Chinh xac
                this.tlchinhxac += io.param;
                break;
            case 19: //Tấn công+#% khi đánh quái
                this.tlDameAttMob.add(io.param);
                break;
            case 22: //HP+#K
                this.hpAdd += io.param * 1000;
                break;
            case 23: //MP+#K
                this.mpAdd += io.param * 1000;
                break;
            case 24: //Làm chậm
                this.wearingBuiBui = true;
                break;
            case 25: //Tàn hình
                this.isTanHinh = true;
                break;
            case 26: //Hóa đá
                this.isHoaDa = true;
                break;
            case 27: //+# HP/30s
                this.hpHoiAdd += io.param;
                break;
            case 28: //+# KI/30s
                this.mpHoiAdd += io.param;
                break;
            case 29:
                this.wearingMabu = true;
                break;
            case 32:
                this.KhangHoaXuong = true;
                break;
            case 33: //dịch chuyển tức thời
                this.teleport = true;
                break;
            case 34:
                this.setTinhAn += 1;
                break;
            case 35:
                this.setNguyetAn += 1;
                break;
            case 36:
                this.setNhatAn += 1;
                break;
            case 38:
                this.IsTacDungHopThe = true;
                break;
            case 42:
                this.tlDameMobFly += io.param;
                break;
            case 43:
                this.tlDameMobMonkey += io.param;
                break;
            case 44:
                this.tlDameMobRun += io.param;
                break;
            case 45:
                this.tlTanCongTocNamec += io.param;
                break;
            case 46:
                this.tlTanCongTocTraiDat += io.param;
                break;
            case 47: //Giáp+#
                this.defAdd += io.param;
                break;
            case 48: //HP/KI+#
                this.hpAdd += io.param;
                this.mpAdd += io.param;
                break;
            case 49: //Tấn công+#%
            case 50: //Sức đánh+#%
                this.tlDame.add(io.param);
                break;
            case 77: //HP+#%
                this.tlHp.add(io.param);
                break;
            case 80: //HP+#%/30s
                this.tlHpHoi += io.param;
                break;
            case 81: //MP+#%/30s
                this.tlMpHoi += io.param;
                break;
            case 83:
                this.Cong20ExpKhiAttackMob = true;
                break;
            case 88: //Cộng #% exp khi đánh quái
                this.tlTNSM.add(io.param);
                break;
            case 94: //Giáp #%
                this.tlGiap += io.param;
                break;
            case 95: //Biến #% tấn công thành HP
                this.tlHutHp += io.param;
                break;
            case 96: //Biến #% tấn công thành MP
                this.tlHutMp += io.param;
                break;
            case 97: //Phản #% sát thương
                this.tlPST += io.param;
                break;
            case 98: //Xuyen giap chuong
                this.tlxgc += io.param;
                break;
            case 99: //Xuyen giap can chien
                this.tlxgcc += io.param;
                break;
            case 100: //+#% vàng từ quái
                this.tlGold += io.param;
                break;
            case 101: //+#% TN,SM
                this.tlTNSM.add(io.param);
                break;
            case 103: //KI +#%
                this.tlMp.add(io.param);
                break;
            case 104: //Biến #% tấn công quái thành HP
                this.tlHutHpMob += io.param;
                break;
            case 105: //Vô hình khi không đánh quái và boss
                this.wearingVoHinh = true;
                break;
            case 106: //Không ảnh hưởng bởi cái lạnh
                this.isKhongLanh = true;
                break;
            case 108: //#% Né đòn
                this.tlNeDon += io.param;
                break;
            case 109: //Hôi, giảm #% HP
                this.tlHpGiamODo += io.param;
                break;
            case 110: //Do spl
                this.isDoSPL = true;
                break;
            case 111: //phan tâm
                this.isXinbato = true;
                break;
            case 115: //biến cà rot
                this.isThoDaiCa = true;
                break;
            case 116: //Kháng thái dương hạ san
                this.khangTDHS = true;
                break;

            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                if (io.param > this.tlSexyDame) {
                    this.tlSexyDame = io.param;
                }
                break;
            case 147: //+#% sức đánh
                this.tlDame.add(io.param);
                break;
            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                this.tlSubSD += 50;
                this.tlSubHP += 50;
                this.tlSubMP += 50;
                this.tlTNSM.add(io.param);
                this.tlGold += io.param;
                break;
            case 156:
                this.tlCongDonSD += io.param;
                break;
            case 157:
                this.tlSubDamePercenMp20 += io.param;
                break;
            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                this.mpHoiCute += io.param;
                break;
            case 159: // x chưởng
                this.xChuong = io.param;
                break;
            case 160: // TNSM PET;
                this.tlTNSMPet += io.param;
                break;
            case 173: //Phục hồi #% HP và KI cho đồng đội
                this.tlHpHoiBanThanVaDongDoi += io.param;
                this.tlMpHoiBanThanVaDongDoi += io.param;
                break;
            case 176: //
                setInfoOption176();
                break;
            case 197:
                this.tlTanCongTocXayda += io.param;
                break;
            case 198:
                this.tlGiamSatThuongTraiDat += io.param;
                break;
            case 199:
                this.tlGiamSatThuongNamec += io.param;
                break;
            case 200:
                this.tlGiamSatThuongXayda += io.param;
                break;
            case 201:
                this.tlDameClan += io.param;
                break;
            case 202:
                this.tlHpClan += io.param;
                break;
            case 203:
                this.tlMpClan += io.param;
                break;
            case 204:
                this.tlDameBoss += io.param;
                break;
            case 153: //% phát nổ sau khi chết
                this.tlBom += io.param;
                break;
            case 163:
                this.isHoaBiNgoXungQuanh = true;
                break;
            case 256: //founder
                this.isFounder = true;
                break;
            case 236: //may mắn
                this.tlMayman += io.param;
                break;
            case 258:
                if (io.param > this.tlCoolDame) {
                    this.tlCoolDame = io.param;
                }
                break;
            case 259: //HP+#%/10s
                this.tlHpHoiBanthan_DongMinh += io.param;
                break;
            case 226:
                if (io.param > this.tlCuteAddame) {
                    this.tlCuteAddame = io.param;
                }
                break;
            case 227:
                this.tlFixStun += io.param;
                break;
        }
    }

    private void setSpeed() {
        for (Integer tl : this.tlSpeed) {
            this.speed += calPercent(this.speed, tl);
        }
        if (this.player.effectSkin.isSlow) {
            this.speed = 1;
        }
    }

    private void setInfoOption176() {
        if (player.isPl()) {
            this.tlDame.add(10);
            speed = (byte) (5 + 3 * (50 / 100));
        }
    }

    private void setOutfitFusion() {
        if (this.player.inventory.itemsBody.size() < 6 || this.player.Detu == null || this.player.Detu.inventory.itemsBody.size() < 6) {
            return;
        }
        Item skin = this.player.inventory.itemsBody.get(5);
        Item pskin = this.player.Detu.inventory.itemsBody.get(5);
        if (skin.isNotNullItem() && pskin.isNotNullItem()) {
            this.isGogeta = skin.template.id == 1693 && pskin.template.id == 1553 || skin.template.id == 1553 && pskin.template.id == 1693;
        } else {
            this.isGogeta = false;
        }
    }

    private void setDameTrainArmor() {
        if (player.isPl()) {
            if (this.player.inventory.itemsBody.size() < 7) {
                return;
            }
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl != null && gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.player.inventory.trainArmor == null) {
                        gtl = this.player.inventory.itemsBag.stream()
                                .filter(item -> item != null && item.isNotNullItem()
                                && item.template != null && item.template.type == 32
                                && item.itemOptions != null
                                && item.itemOptions.stream()
                                        .anyMatch(io -> io != null && io.optionTemplate.id == 9 && io.param > 0))
                                .findFirst().orElse(null);
                        if (gtl == null) {
                            return;
                        }
                        this.player.inventory.trainArmor = gtl;
                    }
                    this.wearingTrainArmor = false;
                    if (this.player.inventory.trainArmor != null
                            && this.player.inventory.trainArmor.itemOptions != null) {
                        for (ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io != null && io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame.add(ItemService.gI()
                                        .getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error("Lỗi get giáp tập luyện " + this.player.name + "\n" + e + "\n");
            }
        }
    }

    public void setBasePoint() {
        setHpMax();
        setHp();
        setMpMax();
        setMp();
        setDame();
        setDef();
        setCrit();
        setCritDame();
        setHpHoi();
        setMpHoi();
        setHutHp();
        setHutMp();
        setThoBulma();
        setTiecbaiBien();
        setBunmaTocMau();
        setTinhNhatNguyetAn();
        setSpeed();
        setOptions();
    }

    private void setThoBulma() {
        this.isThoBulma = (this.player.inventory != null && this.player.inventory.itemsBody != null && this.player.inventory.itemsBody.size() >= 5
                && this.player.inventory.itemsBody.get(5).isNotNullItem() && this.player.inventory.itemsBody.get(5).template.id == 584);
    }

    private void setBunmaTocMau() {
        this.isBunmaTocMau = (this.player.inventory != null && this.player.inventory.itemsBody != null && this.player.inventory.itemsBody.size() >= 5
                && this.player.inventory.itemsBody.get(5).isNotNullItem() && this.player.inventory.itemsBody.get(5).template.id >= 1208
                && this.player.inventory.itemsBody.get(5).template.id <= 1210);
    }

    private void setTiecbaiBien() {
        this.isTiecBaiBien = (this.player.inventory != null && this.player.inventory.itemsBody != null && this.player.inventory.itemsBody.size() >= 5
                && this.player.inventory.itemsBody.get(5).isNotNullItem() && this.player.inventory.itemsBody.get(5).template.id >= 1234
                && this.player.inventory.itemsBody.get(5).template.id <= 1236);
    }

    private void setTinhNhatNguyetAn() {
        this.isTinhAn = this.setTinhAn >= 5;
        this.isNhatAn = this.setNhatAn >= 5;
        this.isNguyetAn = this.setNguyetAn >= 5;
    }

    public int getPlayerRank(List<Player> list, Player player) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == player.id) {
                return i + 1;
            }
        }
        return -1;
    }

    private void setHpHoi() {
        this.hpHoi = this.hpMax / 100;
        this.hpHoi += this.hpHoiAdd;

        // Kiểm tra giá trị tlHpHoi không vượt quá giới hạn
        if (this.tlHpHoi > 100) {
            this.tlHpHoi = 100;
        } else if (this.tlHpHoi < 0) {
            this.tlHpHoi = 0;
        }

        this.hpHoi += ((long) this.hpMax * this.tlHpHoi / 100);

        // Kiểm tra giá trị tlHpHoiBanThanVaDongDoi không vượt quá giới hạn
        if (this.tlHpHoiBanThanVaDongDoi > 100) {
            this.tlHpHoiBanThanVaDongDoi = 100;
        } else if (this.tlHpHoiBanThanVaDongDoi < 0) {
            this.tlHpHoiBanThanVaDongDoi = 0;
        }

        this.hpHoi += ((long) this.hpMax * this.tlHpHoiBanThanVaDongDoi / 100);

        if (this.player.itemTime != null && this.player.itemTime.Isthuocmothuong) {
            this.hpHoi += calPercent(this.hpMax, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.Isthuocmodacbiet) {
            this.hpHoi += calPercent(this.hpMax, 10);
        }
        if (this.player.setClothes != null && this.player.setClothes.ctNezuko != -1) {
            this.hpHoi += calPercent(this.hpMax, 3);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHoiSieuCap) {
            this.hpHoi += calPercent(this.hpMax, 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC3) {
            this.hpHoi += calPercent(this.hpMax, 10);
        }
    }

    private void setMpHoi() {
        this.mpHoi = this.mpMax / 100;
        this.mpHoi += this.mpHoiAdd;

        // Kiểm tra giá trị tlMpHoi không vượt quá giới hạn
        if (this.tlMpHoi > 100) {
            this.tlMpHoi = 100;
        } else if (this.tlMpHoi < 0) {
            this.tlMpHoi = 0;
        }

        this.mpHoi += ((long) this.mpMax * this.tlMpHoi / 100);

        // Kiểm tra giá trị tlMpHoiBanThanVaDongDoi không vượt quá giới hạn
        if (this.tlMpHoiBanThanVaDongDoi > 100) {
            this.tlMpHoiBanThanVaDongDoi = 100;
        } else if (this.tlMpHoiBanThanVaDongDoi < 0) {
            this.tlMpHoiBanThanVaDongDoi = 0;
        }

        this.mpHoi += ((long) this.mpMax * this.tlMpHoiBanThanVaDongDoi / 100);

        if (this.player.itemTime != null && this.player.itemTime.Isthuocmothuong) {
            this.mpHoi += calPercent(this.mpMax, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.Isthuocmodacbiet) {
            this.mpHoi += calPercent(this.mpMax, 10);
        }
        if (this.player.setClothes != null && this.player.setClothes.ctNezuko != -1) {
            this.mpHoi += calPercent(this.mpMax, 3);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHoiSieuCap) {
            this.mpHoi += calPercent(this.mpMax, 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC3) {
            this.mpHoi += calPercent(this.mpMax, 10);
        }
    }

    private void setHpMax() {
        // Tính toán giới hạn hpMax
        long hpMax = this.hpg + this.hpAdd;
//
//        for (int tl : new ArrayList<>(this.tlHp)) {
//            hpMax += (hpMax * tl / 100L);
//        }
        for (Integer tl : this.tlHp) {
            if (tl != null) {
                hpMax += (hpMax * tl / 100L);
            }
        }
         // Tinh ấn
        if (hasFull5TinhAn()) {
            hpMax += calPercent(hpMax, 15);
        }

        if (this.player.isPl()) {
            if (InventoryService.gI().findItemRongNhi(this.player)) {
                hpMax += calPercent(hpMax, 1);
            }
            hpMax += calPercent(hpMax, InventoryService.gI().HpItemsInBoxCollection(this.player));
        }

        if (this.player.tlHpClanAdd > 0) {
            hpMax += calPercent(hpMax, this.player.tlHpClanAdd);
        }
        if (this.player.isPhanThan) {
            hpMax = calPercent(((PhanThan) this.player).master.nPoint.hpMax, SkillUtil.getPercentPhanThan(player));
        }
        if (this.player.THE_TUAN == 1 && this.player.LASTTIME_THE_TUAN > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 3);
        }
        if (this.player.THE_TUAN == 2 && this.player.LASTTIME_THE_TUAN > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 5);
        }
        if (this.player.THE_THANG == 1 && this.player.LASTTIME_THE_THANG > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 7);
        }
        if (this.player.THE_THANG == 2 && this.player.LASTTIME_THE_THANG > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 10);
        }
        if (this.player.THE_NAM == 1 && this.player.LASTTIME_THE_NAM > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 15);
        }
        if (this.player.THE_NAM == 2 && this.player.LASTTIME_THE_NAM > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 18);
        }
        if (this.player.THE_CHI_TON == 1 && this.player.LASTTIME_THE_CHI_TON > System.currentTimeMillis()) {
            hpMax += calPercent(hpMax, 20);
        }
        if (this.player.isPl() && this.player.isUseDanhHieu_ThienTu == true && this.player.LastTimeDanhHieu_ThienTu > 0) {
            hpMax += calPercent(hpMax, 5);
        }

        if (EventManager.INTERNATIONAL_WOMANS_DAY) {
            hpMax += calPercent(hpMax, EventDAO.getRemainingTimeToIncreaseHP());
        }

        // Xử lý set nappa
        if (this.player.setClothes.nappa == 5) {
            hpMax += calPercent(hpMax, 100);
        }

        if (this.player.setClothes.cadicM >= 2) {
            hpMax += calPercent(hpMax, 20);
        }

        if (this.player.effectSkill != null && this.player.effectSkill.isVirus) {
            hpMax -= calPercent(hpMax, 10);
        }

        if (this.player.effectSkill != null && this.player.effectSkill.isBongTuyet) {
            hpMax -= calPercent(hpMax, 20);
        }

        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            hpMax += calPercent(hpMax, 10);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseRocket1h) {
            hpMax += calPercent(hpMax, 20);
        }

        if (this.player.itemTime != null && this.player.itemTime.isRongXuong_2) {
            hpMax += calPercent(hpMax, 15);
        }

        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isDeTu || (this.player.isDeTu && ((Detu) this.player).status != Detu.FUSION)) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                hpMax += (hpMax * percent / 100);
            }
        }

        if (this.player.isPlMan()) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.HP);
            if (at != null && !at.isExpired()) {
                hpMax += calPercent(hpMax, at.getValue());
            }
        }

        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            hpMax *= this.player.effectSkin.xHPKI;
        }

        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu == 1) {
                hpMax += this.player.Detu.nPoint.hpMax *  20 / 100L;
            }
        }
        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu == 5) {
                hpMax += this.player.Detu.nPoint.hpMax * 40/ 100L;
            }
        }

        //+hp đệ
        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            hpMax += this.player.Detu.nPoint.hpMax;
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            hpMax += calPercent(hpMax, 5);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            hpMax += calPercent(hpMax, 10);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            hpMax += calPercent(hpMax, 15);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
            hpMax += calPercent(hpMax, 20);
        }
        //huýt sáo
        if (!this.player.isDeTu || (this.player.isDeTu && ((Detu) this.player).status != Detu.FUSION)) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                hpMax += (hpMax * this.player.effectSkill.tiLeHPHuytSao / 100L);
            }
        }
        //bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            hpMax *= 2;
        }

        // Xử lý chibi
        if (this.player.effectSkill != null && this.player.effectSkill.isChibi && this.player.typeChibi == 3) {
            hpMax *= 2;
        }
        if (player.getBuff() == Buff.BUFF_HP) {
            hpMax += calPercent(hpMax, 20);
        }
        //TOP WHIS
        List<Player> list = TopKillWhisManager.getInstance().getList();
        if (!list.isEmpty() && list.size() > 2 && this.player.isPl()) {
            if (list.size() >= 5 && this.player.isPl()) {
                int playerRank = getPlayerRank(list, this.player);
                if (playerRank == 1) {
                    hpMax += calPercent(hpMax, 30);
                } else if (playerRank == 2) {
                    hpMax += calPercent(hpMax, 20);
                } else if (playerRank == 3) {
                    hpMax += calPercent(hpMax, 10);
                } else if (playerRank >= 4 && playerRank <= 5) {
                    hpMax += calPercent(hpMax, 5);
                } else if (playerRank >= 6 && playerRank <= 10) {
                    hpMax += calPercent(hpMax, 3);
                }
            }
        }

        // Phù map mabu
        if (this.player.isPhuHoMapMabu) {
            hpMax += 1_000_000;
        }
        //giảm hp
        hpMax -= (hpMax * tlSubHP / 100);
        //hồng đào
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao0) {
            hpMax -= calPercent(hpMax, 99);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao) {
            hpMax += calPercent(hpMax, 1);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao1) {
            hpMax += calPercent(hpMax, 2);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao3) {
            hpMax += calPercent(hpMax, 3);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao5) {
            hpMax += calPercent(hpMax, 5);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao10) {
            hpMax += calPercent(hpMax, 8);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao25) {
            hpMax += calPercent(hpMax, 12);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao50) {
            hpMax += calPercent(hpMax, 15);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao99) {
            hpMax += calPercent(hpMax, 20);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao999) {
            hpMax += calPercent(hpMax, 100);
        }

        if (this.player.itemTime != null && this.player.itemTime.istrbhp) {
            hpMax += calPercent(hpMax, 30);
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbhpxd) {
            hpMax += calPercent(hpMax, 15);
        }

        // Xử lý ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            hpMax += (hpMax * RewardBlackBall.R2S_1 / 100);
        }

        // item sieu cawsp
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet2) {
            hpMax *= 2.2;
        }
        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map) && !this.isKhongLanh) {
            hpMax /= 2;
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapChristMasEvent(this.player.zone.map.mapId) && !this.isKhongLanh) {
            hpMax /= 2;
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMap5000NamTruoc(this.player.zone.map.mapId)) {
            hpMax -= calPercent(hpMax, 90);
        }

        if (player.gender == ConstPlayer.XAYDA) {
            if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapCereal(this.player.zone.map)) {
                hpMax /= 2;
            }
        }

        if (this.player.itemTime != null && this.player.itemTime.IsSupbihacam) {
            hpMax += calPercent(hpMax, 10);
        }

        if (this.player.itemTime != null && this.player.itemTime.istomtambot) {
            hpMax += calPercent(hpMax, 5);
        }
        this.hpMax = hpMax;
    }

    // (hp sư phụ + hp đệ tử ) + 15%
    // (hp sư phụ + 15% +hp đệ tử)
    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        // Tính toán giới hạn mpMax
        long mpMax = this.mpg + this.mpAdd;

        // Áp dụng các yếu tố ảnh hưởng đến mpMax
//        for (Integer tl : this.tlMp) {
//            mpMax += (mpMax * tl / 100L);
//        }
for (Integer tl : this.tlMp) {
    if (tl != null) {
        mpMax += (mpMax * tl / 100L);
    }
}
// nhật ấn
if (hasFull5NhatAn()) {
    mpMax += calPercent(mpMax, 15);
}



        // Xử lý set picolo
//        if (this.player.setClothes.ocTieu == 5) {
//            mpMax *= 2;
//        }
        if (this.player.setClothes.picolo == 5) {
            mpMax *= 2;
        }

        if (this.player.isPl()) {
            if (InventoryService.gI().findItemRongNhi(this.player)) {
                mpMax += calPercent(mpMax, 1);
            }
            mpMax += calPercent(mpMax, InventoryService.gI().MpItemsInBoxCollection(this.player));
        }

        if (this.player.tlMpClanAdd > 0) {
            mpMax += calPercent(mpMax, this.player.tlMpClanAdd);
        }
        if (this.player.isPhanThan) {
            mpMax = calPercent(((PhanThan) this.player).master.nPoint.mpMax, SkillUtil.getPercentPhanThan(player));
        }
        if (this.player.THE_TUAN == 1 && this.player.LASTTIME_THE_TUAN > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 3);
        }
        if (this.player.THE_TUAN == 2 && this.player.LASTTIME_THE_TUAN > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 5);
        }
        if (this.player.THE_THANG == 1 && this.player.LASTTIME_THE_THANG > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 7);
        }
        if (this.player.THE_THANG == 2 && this.player.LASTTIME_THE_THANG > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 10);
        }
        if (this.player.THE_NAM == 1 && this.player.LASTTIME_THE_NAM > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 15);
        }
        if (this.player.THE_NAM == 2 && this.player.LASTTIME_THE_NAM > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 18);
        }
        if (this.player.THE_CHI_TON == 1 && this.player.LASTTIME_THE_CHI_TON > System.currentTimeMillis()) {
            mpMax += calPercent(mpMax, 20);
        }
        if (this.player.isPl() && this.player.isUseDanhHieu_ThienTu == true && this.player.LastTimeDanhHieu_ThienTu > 0) {
            mpMax += calPercent(mpMax, 5);
        }
        if (player.getBuff() == Buff.BUFF_KI) {
            mpMax += calPercent(mpMax, 20);
        }

        if (EventManager.INTERNATIONAL_WOMANS_DAY) {
            mpMax += calPercent(mpMax, EventDAO.getRemainingTimeToIncreaseMP());
        }

        if (this.player.itemTime != null && this.player.itemTime.isRongXuong_2) {
            mpMax += calPercent(mpMax, 15);
        }

        if (this.player.effectSkill != null && this.player.effectSkill.isBongTuyet) {
            mpMax -= calPercent(mpMax, 20);
        }

        // Phù map mabu
        if (this.player.isPhuHoMapMabu) {
            mpMax += 1_000_000;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseRocket1h) {
            mpMax += calPercent(mpMax, 20);
        }

        // Xử lý ngọc rồng đen 6 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            mpMax += (mpMax * RewardBlackBall.R6S_1 / 100);
            mpMax += (mpMax * RewardBlackBall.R6S_1 / 100L);
        }

        if (this.player.effectSkill != null && this.player.effectSkill.isVirus) {
            mpMax -= calPercent(mpMax, 10);
        }

        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            mpMax += calPercent(mpMax, 10);
        }

         if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu == 1) {
                mpMax += this.player.Detu.nPoint.mpMax *  20 / 100L;
            }
        }
        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu == 5) {
                mpMax += this.player.Detu.nPoint.mpMax * 40/ 100L;
            }
        }

        //hợp thể
        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            mpMax += this.player.Detu.nPoint.mpMax;
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            mpMax += calPercent(mpMax, 5);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            mpMax += calPercent(mpMax, 10);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            mpMax += calPercent(mpMax, 15);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
            mpMax += calPercent(mpMax, 20);
        }
        //bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            mpMax *= 2;
        }
        if (this.player.isPlMan()) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.KI);
            if (at != null && !at.isExpired()) {
                mpMax += calPercent(mpMax, at.getValue());
            }
        }
        //WHIS
        List<Player> list = TopKillWhisManager.getInstance().getList();
        if (!list.isEmpty() && list.size() > 2 && this.player.isPl()) {
            if (list.size() >= 5 && this.player.isPl()) {
                int playerRank = getPlayerRank(list, this.player);
                if (playerRank == 1) {
                    mpMax += calPercent(mpMax, 30);
                } else if (playerRank == 2) {
                    mpMax += calPercent(mpMax, 20);
                } else if (playerRank == 3) {
                    this.hpMax += calPercent(mpMax, 10);
                } else if (playerRank >= 4 && playerRank <= 5) {
                    mpMax += calPercent(mpMax, 5);
                } else if (playerRank >= 6 && playerRank <= 10) {
                    mpMax += calPercent(mpMax, 3);
                }
            }
        }

        //hồng đào
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao0) {
            mpMax -= calPercent(mpMax, 99);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao) {
            mpMax += calPercent(mpMax, 1);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao1) {
            mpMax += calPercent(mpMax, 2);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao3) {
            mpMax += calPercent(mpMax, 3);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao5) {
            mpMax += calPercent(mpMax, 5);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao10) {
            mpMax += calPercent(mpMax, 8);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao25) {
            mpMax += calPercent(mpMax, 12);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao50) {
            mpMax += calPercent(mpMax, 15);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao99) {
            mpMax += calPercent(mpMax, 20);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao999) {
            mpMax += calPercent(mpMax, 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi2) {
            mpMax *= 2.2;
        }
        //giảm mp
        mpMax -= (mpMax * tlSubMP / 100);

        if (this.player.itemTime != null && this.player.itemTime.istrbki) {
            mpMax += calPercent(mpMax, 30);
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbkixd) {
            mpMax += calPercent(mpMax, 15);
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map) && !this.isKhongLanh) {
            mpMax /= 2;
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapChristMasEvent(this.player.zone.map.mapId) && !this.isKhongLanh) {
            mpMax /= 2;
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMap5000NamTruoc(this.player.zone.map.mapId)) {
            mpMax -= calPercent(mpMax, 90);
        }

        if (player.gender == ConstPlayer.XAYDA) {
            if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapCereal(this.player.zone.map)) {
                mpMax /= 2;
            }
        }

        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            mpMax *= this.player.effectSkin.xHPKI;
        }
        //xiên cá
        if (this.player.effectFlagBag.useXienCa) {
            mpMax += calPercent(mpMax, 15);
        }
        this.mpMax = mpMax;
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    public long getHP() {
        return this.hp <= this.hpMax ? this.hp : this.hpMax;
    }

    public void setHP(long hp) {
        if (hp > 0) {
            this.hp = (hp <= this.hpMax ? hp : this.hpMax);
        } else {
            player.setDie();
        }
    }

    public long getMP() {
        return this.mp <= this.mpMax ? this.mp : this.mpMax;
    }

    public void setMP(long mp) {
        if (mp > 0) {
            this.mp = (mp <= this.mpMax ? mp : this.mpMax);
        } else {
            this.mp = 0;
        }
    }

    private void setDame() {
        long dame = this.dameg + this.dameAdd;

//        for (Integer tl : this.tlDame) {
//            dame += (dame * tl / 100L);
//        }
        for (Integer tl : this.tlDame) {
            if (tl != null) {
                dame += (dame * tl / 100L);
            }
        }
        // Nguyệt Ấn: đủ 5 món +15% Sức đánh
        if (hasFull5NguyetAn()) {
            dame += calPercent(dame, 15);
        }

        if (this.player.isPl()) {
            if (InventoryService.gI().findItemRongNhi(this.player)) {
                dame += calPercent(dame, 1);
            }
            dame += calPercent(dame, InventoryService.gI().DamageItemsInBoxCollection(this.player));
        }

        if (this.player.setClothes.cumber == 5) {
            dame += calPercent(dame, 20);
        }

        if (this.player.isPhanThan) {
            dame = calPercent(((PhanThan) this.player).master.nPoint.dame, SkillUtil.getPercentPhanThan(player));
        }
        if (this.player.THE_TUAN == 1 && this.player.LASTTIME_THE_TUAN > System.currentTimeMillis()) {
            dame += calPercent(dame, 3);
        }
        if (this.player.THE_TUAN == 2 && this.player.LASTTIME_THE_TUAN > System.currentTimeMillis()) {
            dame += calPercent(dame, 5);
        }
        if (this.player.THE_THANG == 1 && this.player.LASTTIME_THE_THANG > System.currentTimeMillis()) {
            dame += calPercent(dame, 7);
        }
        if (this.player.THE_THANG == 2 && this.player.LASTTIME_THE_THANG > System.currentTimeMillis()) {
            dame += calPercent(dame, 10);
        }
        if (this.player.THE_NAM == 1 && this.player.LASTTIME_THE_NAM > System.currentTimeMillis()) {
            dame += calPercent(dame, 15);
        }
        if (this.player.THE_NAM == 2 && this.player.LASTTIME_THE_NAM > System.currentTimeMillis()) {
            dame += calPercent(dame, 18);
        }
        if (this.player.THE_CHI_TON == 1 && this.player.LASTTIME_THE_CHI_TON > System.currentTimeMillis()) {
            dame += calPercent(dame, 20);
        }

        if (this.player.tlDameClanAdd > 0) {
            dame += calPercent(dame, this.player.tlDameClanAdd);
        }
        if (EventManager.INTERNATIONAL_WOMANS_DAY) {
            dame += calPercent(dame, EventDAO.getRemainingTimeToIncreaseDame());
        }

        if (this.player.effectSkill != null && this.player.effectSkill.isVirus) {
            dame -= calPercent(dame, 10);
        }

        dame += (dame * tlSexyDame / 100);

        dame += (dame * tlCoolDame / 100);

        dame += (dame * tlCuteAddame / 100);

        if (this.player.isPl() && this.player.isUseDanhHieu_ThienTu == true && this.player.LastTimeDanhHieu_ThienTu > 0) {
            dame += calPercent(dame, 5);
        }
        if (this.player.itemTime != null && this.player.itemTime.IsDuoiKhi) {
            dame += calPercent(dame, 10);
        }
        if (this.player.getBuff() == Buff.BUFF_ATK) {
            dame += calPercent(dame, 20);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC1) {
            dame += calPercent(dame, 5);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC2) {
            dame += calPercent(dame, 10);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC3) {
            dame += calPercent(dame, 15);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseTrungThu1Trung) {
            dame += calPercent(dame, 10);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseTrungThu2Trung) {
            dame += calPercent(dame, 15);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseTrungThuDB) {
            dame += calPercent(dame, 20);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseHBTrungThu) {
            dame += calPercent(dame, 25);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseRocket1h) {
            dame += calPercent(dame, 20);
        }

        if (this.player.effectSkill != null && this.player.effectSkill.isBongTuyet) {
            dame -= calPercent(dame, 20);
        }

        if (this.player.itemTime != null && this.player.itemTime.isRongXuong_2) {
            dame += calPercent(dame, 15);
        }

        if (this.player.effectSkin != null && this.player.effectSkin.isThoDaiKa) {
            dame -= calPercent(dame, 15);
        }

        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            dame += calPercent(dame, 10);
        }

        //thức ăn
        if (!this.player.isDeTu && this.player.itemTime.isEatMeal || this.player.isDeTu && ((Detu) this.player).master.itemTime.isEatMeal) {
            dame += calPercent(dame, 10);
        }

        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu >= 5) {
                dame += this.player.Detu.nPoint.dame * this.player.getPointfusion().getDameFusion() / 100L;
            }
        }

        //hợp thể
         if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu == 1) {
                dame += this.player.Detu.nPoint.dame *  20 / 100L;
            }
        }
        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (this.player.Detu.typeDeTu == 5) {
                dame += this.player.Detu.nPoint.dame * 40/ 100L;
            }
        }
        if (this.player.Detu != null && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            dame += this.player.Detu.nPoint.dame;
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            dame += calPercent(dame, 5);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            dame += calPercent(dame, 10);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            dame += calPercent(dame, 15);
        }
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
            dame += calPercent(dame, 20);
        }
        //cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            dame *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao) {
            dame += calPercent(dame, 1);
        }
        // hồng đào
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao0) {
            dame -= calPercent(dame, 99);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao1) {
            dame += calPercent(dame, 2);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao3) {
            dame += calPercent(dame, 3);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao5) {
            dame += calPercent(dame, 5);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao10) {
            dame += calPercent(dame, 8);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao25) {
            dame += calPercent(dame, 12);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao50) {
            dame += calPercent(dame, 15);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao99) {
            dame += calPercent(dame, 20);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseHongDao999) {
            dame += calPercent(dame, 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo2) {
            dame *= 2.2;
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbsd) {
            dame += calPercent(dame, 30);
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbsdxd) {
            dame += calPercent(dame, 15);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseBanhTet) {
            dame += calPercent(dame, 15);
        }

        if (this.player.itemTime != null && this.player.itemTime.isUseBanhTrung) {
            dame += calPercent(dame, 25);
        }

        // Phù map mabu
        if (this.player.isPhuHoMapMabu) {
            dame += 10_000;
        }
        if (this.player.isPlMan()) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.SUC_DANH);
            if (at != null && !at.isExpired()) {
                dame += calPercent(dame, at.getValue());
            }
        }
        //WHIS
        List<Player> list = TopKillWhisManager.getInstance().getList();
        if (!list.isEmpty() && list.size() > 2 && this.player.isPl()) {
            if (list.size() >= 5 && this.player.isPl()) {
                int playerRank = getPlayerRank(list, this.player);
                if (playerRank == 1) {
                    dame += calPercent(dame, 30);
                } else if (playerRank == 2) {
                    dame += calPercent(dame, 20);
                } else if (playerRank == 3) {
                    dame += calPercent(dame, 10);
                } else if (playerRank >= 4 && playerRank <= 5) {
                    dame += calPercent(dame, 5);
                } else if (playerRank >= 6 && playerRank <= 10) {
                    dame += calPercent(dame, 3);
                }
            }
        }

        //SucManhBocPha
        if (player.isPl() && player.playerSkill.skillSelect != null) {
            int tiLeDameSucManhBocPha = SkillUtil.getPercentDameSucManhBocPha(player.playerSkill.skillSelect.point);
            if (this.player.effectSkill.isSUcManhBocPha) {
                dame += (dame * tiLeDameSucManhBocPha / 100);
            }
        }
        //giảm dame
        dame -= (dame * tlSubSD / 100);

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map) && !this.isKhongLanh) {
            dame /= 2;
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapChristMasEvent(this.player.zone.map.mapId) && !this.isKhongLanh) {
            dame /= 2;
        }

        if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMap5000NamTruoc(this.player.zone.map.mapId)) {
            dame -= calPercent(dame, 90);
        }

        if (player.gender == ConstPlayer.XAYDA) {
            if (!this.player.isBoss && !this.player.getBot() && this.player.zone != null && MapService.gI().isMapCereal(this.player.zone.map)) {
                dame /= 2;
            }
        }

        // Xử lý ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            dame += (dame * RewardBlackBall.R1S_2 / 100);
        }

        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isDeTu || (this.player.isDeTu && ((Detu) this.player).status != Detu.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey(player.effectSkill.levelMonkey);
                dame += (dame * percent / 100);
            }
        }

        // Xử lý phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            dame *= this.player.effectSkin.xDame;
        }

        if (this.player.itemTime != null && this.player.itemTime.Ishamburgersau) {
            dame += calPercent(dame, 10);
        }

        if (this.player.itemTime != null && this.player.itemTime.Isthuocmothuong) {
            dame += calPercent(dame, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.Isthuocmodacbiet) {
            dame += calPercent(dame, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.iscuarangme) {
            dame += calPercent(dame, 5);
        }
        this.dame = dame;
    }

    private void setOptions() {
        if (this.player.setClothes.gohan == 5) {
            this.tlMayman += 150;
            this.tlGold += 80;
        }
        if (this.player.isPl() && this.intrinsic != null && this.intrinsic.id == 23) {
            this.tlGold += intrinsic.param1;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseNedon) {
            this.tlNeDon += 10;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseNedon2) {
            this.tlNeDon += 20;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUsePhanSatThuong) {
            this.tlPST += 3;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUsePhanSatThuong2) {
            this.tlPST += 6;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUsePhanSatThuong3) {
            this.tlPST += 12;
        }
        if (this.player.itemTime != null && this.player.itemTime.IsKhauTrang) {
            this.tlDameAttMob.add(10);
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseSatThuongChuan) {
            this.tlstc += 10;
        }
        if (this.player.itemTime != null && (this.player.itemTime.isUseSatThuongChuan2 || this.player.itemTime.isUseSauRieng)) {
            this.tlstc += 15;
        }
        //hieuunng
        if (this.player.itemTime != null && this.player.itemTime.isUseTHUOCTANGHINH) {
            this.wearingVoHinh = true;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseTHUOCTANGHINH10) {
            this.wearingVoHinh = true;
        }

        if (player.gender == ConstPlayer.XAYDA) {
            if (this.player.zone != null && MapService.gI().isMapVohinh(this.player.zone.map)) {
                this.wearingVoHinh = true;
            }
        }
        //trb
        if (this.player.trbne == 1) {
            this.tlNeDon += (this.tlNeDon + 1);
        }
        if (this.player.trbne == 2) {
            this.tlNeDon += (this.tlNeDon + 2);
        }
        if (this.player.trbne == 3) {
            this.tlNeDon += (this.tlNeDon + 3);
        }
        if (this.player.trbne == 4) {
            this.tlNeDon += (this.tlNeDon + 4);
        }
        if (this.player.trbne == 5) {
            this.tlNeDon += (this.tlNeDon + 5);
        }
        if (this.player.trbne == 6) {
            this.tlNeDon += (this.tlNeDon + 6);
        }
        if (this.player.trbne == 7) {
            this.tlNeDon += (this.tlNeDon + 7);
        }
        if (this.player.trbne == 8) {
            this.tlNeDon += (this.tlNeDon + 8);
        }
        if (this.player.trbne == 9) {
            this.tlNeDon += (this.tlNeDon + 9);
        }
        if (this.player.trbne == 10) {
            this.tlNeDon += (this.tlNeDon + 10);
        }
    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        //đồ
        for (Integer tl : this.tlDef) {
            this.def += ((long) this.def * tl / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.Isbanhgatonhen) {
            this.def += ((long) this.def * 10 / 100);
        }
        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.def += ((long) this.def * RewardBlackBall.R2S_2 / 100);
        }
        switch (player.isbienhinh) {
            case 1:
                this.def += 5000;
                break;
            case 2:
                this.def += 10000;
                break;
            case 3:
                this.def += 15000;
                break;
            case 4:
                this.def += 20000;
                break;
            case 5:
                this.def += 25000;
                break;
            default:
                break;
        }
        if (this.player.setClothes != null && this.player.setClothes.ctInosuke != -1) {
            this.def += calPercent(this.def, 50);
        }
        if (this.player.setClothes != null && this.player.setClothes.ctInoHashi != -1) {
            this.def += calPercent(this.def, 60);
        }
    }

    private void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        if (this.player.isPl()) {
            this.crit += InventoryService.gI().CritItemsInBoxCollection(this.player);
        }
        if (this.player.setClothes.cumber == 5) {
            this.crit += 100;
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhDeoC2) {
                this.crit += 10;
            }
            if (this.player.itemTime.isUseTrungThu1Trung) {
                this.crit += 10;
            }
            if (this.player.itemTime.isUseTrungThu2Trung) {
                this.crit += 15;
            }
            if (this.player.itemTime.isUseTrungThuDB) {
                this.crit += 20;
            }
            if (this.player.itemTime.isUseHBTrungThu) {
                this.crit += 25;
            }
            if (this.player.itemTime.isUseRocket1h) {
                this.crit += 20;
            }
            if (this.player.itemTime.isUseChiMang2) {
                this.crit += 10;
            }
            if (this.player.itemTime.isUseChiMang3) {
                this.crit += 15;
            }
            if (this.player.itemTime.IsBocPha) {
                this.crit += 5;
            }
            if (this.player.itemTime.IsKeoMotMat) {
                this.crit += 5;
            }
            if (this.player.itemTime.isUseBanhTet) {
                this.crit += 15;
            }
            if (this.player.itemTime.isUseBanhTrung) {
                this.crit += 25;
            }
        }
        if (this.player.setClothes.thanVuTruKaio >= 2) {
            this.crit += 20;
        }
        if (this.player.effectSkill != null) {
            if (this.player.effectSkill.isMonkey) {
                this.crit = 100;
            }
            if (this.player.effectSkill.iscumber) {
                this.crit += 20;
            }
            if (this.player.effectSkill.iscumber2) {
                this.crit += 15;
            }
            if (this.player.effectSkill.iskefla) {
                this.crit = 100;
            }
        }
        if (this.player.itemTime != null && this.player.itemTime.IsDuoiKhi) {
            this.crit = 100;
        }
        if (this.player.getBuff() == Buff.BUFF_CRIT) {
            this.crit += 10;
        }
        switch (this.player.isbienhinh) {
            case 1:
                this.crit += 10;
                break;
            case 2:
                this.crit += 20;
                break;
            case 3:
                this.crit += 30;
                break;
            case 4:
                this.crit += 40;
                break;
            case 5:
                this.crit += 50;
                break;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.crit += RewardBlackBall.R3S_2;
        }
        if (this.crit > 100) {
            this.crit = 100;
        }
    }

    private void setCritDame() {
        if (this.player.setClothes != null && this.player.setClothes.ctTanjiro != -1) {
            this.tlSDCM += 30;
            this.tlDameCrit.add(30);
        }
        if (this.player.itemTime != null && this.player.itemTime.isbachtuocnuong) {
            this.tlSDCM += 5;
            this.tlDameCrit.add(5);
        }
        if (this.player.itemTime != null && this.player.itemTime.IsBocPha) {
            this.tlSDCM += 5;
            this.tlDameCrit.add(5);
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.tlSDCM += 20;
            this.tlDameCrit.add(20);
        }
        // Xử lý set nail
        if (this.player.setClothes.nail >= 2) {
            this.tlSDCM += 20;
            this.tlDameCrit.add(20);
        }
        if (this.player.setClothes.cumber == 5) {
            this.tlSDCM += 100;
            this.tlDameCrit.add(100);
        }
    }

    private void setHutHp() {
        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC1) {
            this.tlHutHp += 30;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC2) {
            this.tlHutHp += 50;
        }
    }

    private void setHutMp() {
        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC1) {
            this.tlHutMp += 30;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBanhDeoC2) {
            this.tlHutMp += 50;
        }
    }

    public void addHp(long hp) {
        if (hp > 0) {
            long potentialHp = this.hp + hp;
            if (potentialHp > this.hpMax) {
                this.hp = this.hpMax;
            } else {
                this.hp = potentialHp;
            }
        }
    }

    public void addMp(long mp) {
        long potentialMp = this.mp + mp;
        if (potentialMp > this.mpMax) {
            this.mp = this.mpMax;
        } else if (potentialMp < 0) {
            this.mp = 0;
        } else {
            this.mp = potentialMp;
        }
    }

    public void setHp(long hp) {
        if (hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public void setMp(long mp) {
        if (mp < 0) {
            this.mp = 0;
        } else {
            this.mp = mp;
        }
    }

    private void resetPoint() {
        this.voHieuChuong = 0;
        this.hpAdd = 0;
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameCrit.clear();
        this.tlDameAttMob.clear();
        this.tlSDCM = 0;
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlHpHoiBanthan_DongMinh = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.tlPST = 0;
        this.tlDameMobFly = 0;
        this.tlDameMobMonkey = 0;
        this.tlDameMobRun = 0;
        this.tlTNSM.clear();
        this.tlDameAttMob.clear();
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.tlMayman = 0;
        this.tlGiamSatThuongNamec = 0;
        this.tlGiamSatThuongTraiDat = 0;
        this.tlGiamSatThuongXayda = 0;
        this.tlTanCongTocNamec = 0;
        this.tlTanCongTocTraiDat = 0;
        this.tlTanCongTocXayda = 0;
        this.tlDameClan = 0;
        this.tlHpClan = 0;
        this.tlMpClan = 0;
        this.tlDameBoss = 0;
        this.tlBom = 0;
        this.tlGiap = 0;
        this.tlxgcc = 0;
        this.tlxgc = 0;
        this.tlstc = 0;
        this.tlchinhxac = 0;
        this.tlSubDamePercenMp20 = 0;
        this.tlCongDonSD = 0;
        this.tlTNSMPet = 0;
        this.xChuong = 0;
        this.isFounder = false;
        this.setTinhAn = 0;
        this.setNhatAn = 0;
        this.setNguyetAn = 0;
        this.tlSexyDame = 0;
        this.tlCuteAddame = 0;
        this.tlCoolDame = 0;
        this.tlSubSD = 0;
        this.tlSubHP = 0;
        this.tlSubMP = 0;
        this.tlHpGiamODo = 0;
        this.tlFixStun = 0;
        this.tlSpeed.clear();
        this.speed = 5;
        this.KhangHoaXuong = false;
        this.teleport = false;
        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.isHoaBiNgoXungQuanh = false;
        this.khangTDHS = false;
        this.isTanHinh = false;
        this.isHoaDa = false;
        this.wearingBuiBui = false;
        this.wearingMabu = false;
        this.isDoSPL = false;
        this.isXinbato = false;
        this.isThoDaiCa = false;
        this.isThoBulma = false;
        this.isBunmaTocMau = false;
        this.isTiecBaiBien = false;
        this.Cong20ExpKhiAttackMob = false;
    }

    public void settlGold() {
        if (intrinsic != null && intrinsic.id == 23) {
            this.tlGold += intrinsic.param1;
        }
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25
                && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    public double getDameAttack(boolean isAttackMob) {
        setIsCrit();
        long dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        byte percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        if (skillSelect.template.id != Skill.DICH_CHUYEN_TUC_THOI && isCritTele) {
            isCrit = true;
            isCritTele = false;
        }
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAMEJOKO:
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.GALICK:
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.ANTOMIC:
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.DEMON:
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.MASENKO:
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                if (this.player.setClothes.nail == 5) {
                    percentXDame = 50;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.LIEN_HOAN:
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
//                percentDameSkill = skillSelect.damage;
//                if (this.player.setClothes.slug == 5) {
//                    percentXDame = 100;
//                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.ocTieu == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.KAIOKEN:
                if (intrinsic.id == 26) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.yamcha == 5) {
                    percentXDame = 100;
                } else if (player.setClothes.thanVuTruKaio == 5) {
                    percentXDame = 30;
                }
                break;
            case Skill.TU_SAT:
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.cadicM == 4) {
                    percentXDame = 20;
                } else if (this.player.setClothes.cadicM == 5) {
                    percentXDame = 40;
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                isCrit = true;
                isCritTele = true;
                dameAttack = Util.nextLong(Util.CrisGH((dameAttack - (dameAttack / 100 * 5))), Util.CrisGH((dameAttack + (dameAttack / 100 * 5))));
                break;
            case Skill.MAKANKOSAPPO:
                percentDameSkill = skillSelect.damage;
                long dameSkill = Util.CrisGH((long) this.mpMax * percentDameSkill / 100);
//                if (this.player.setClothes.picolo == 5) {
//                    dameSkill += dameSkill * 50 / 100;
//                }
                return dameSkill;
            case Skill.QUA_CAU_KENH_KHI:
                long hpmob = 0;
                long hppl = 0;

                for (Mob mob : this.player.zone.mobs) {
                    if (!mob.isDie() && Util.getDistance(this.player, mob) <= SkillUtil.getRangeQCKK(this.player.playerSkill.skillSelect.point)) {
                        hpmob += mob.point.hp;
                    }
                }

                for (Player pl : this.player.zone.getHumanoids()) {
                    if (!pl.isDie() && this.player.id != pl.id && Util.getDistance(this.player, pl) <= SkillUtil.getRangeQCKK(this.player.playerSkill.skillSelect.point)) {
                        hppl += pl.nPoint.hp;
                    }
                }
                long dameqckk = (hpmob * 10 / 100) + (hppl * 10 / 100) + this.dame * 10;

                if (this.player.setClothes.kirin == 5) {
                    dameqckk *= 2;
                }

                dameqckk = dameqckk + (Util.nextInt(-5, 5) * dameqckk / 100);

                return dameqckk;
            case Skill.DE_TRUNG:
                if (player.setClothes.pikkoroDaimao == 5) {
                    dameAttack *= 2;
                }
                return dameAttack;
        }
        if (percentDameSkill != 0) {
            dameAttack = dameAttack * percentDameSkill / 100;
        }

        dameAttack += (dameAttack * percentDameIntrinsic / 100);
        dameAttack += (dameAttack * dameAfter / 100);

        if (this.player.effectSkill != null && this.player.effectSkill.isDameBuff && (tlSexyDame == 0 || tlCoolDame == 0 || tlCuteAddame == 0)) {
            int tiLeDame = this.player.effectSkill.tileDameBuff;
            dameAttack += (dameAttack * tiLeDame / 100L);
        }

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += (dameAttack * tl / 100);
            }
            if (this.player.isDeTu && ((Detu) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                dameAttack *= 2;
            }
        }

        dameAfter = 0;

        if (isCrit) {
            dameAttack *= 2;
            dameAttack += (dameAttack * tlSDCM / 100);
        }

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += (dameAttack * tl / 100);
            }
        }

        dameAttack += dameAttack * percentXDame / 100;

        long tempDameAttack = (long) (dameAttack / 100L * 5L);
        if (tempDameAttack <= 0) {
            tempDameAttack = 1;
        }
        dameAttack += (long) (Util.getOne(-1, 1) * Util.Crisnext(tempDameAttack) + 1);

        if (player.effectSkin != null && player.effectSkin.isXChuong && (player.playerSkill.skillSelect.template.id == Skill.KAMEJOKO || player.playerSkill.skillSelect.template.id == Skill.ANTOMIC || player.playerSkill.skillSelect.template.id == Skill.MASENKO)) {
            dameAttack *= xChuong;
            player.effectSkin.isXDame = true;
            player.effectSkin.isXChuong = false;
            player.effectSkin.lastTimeXChuong = System.currentTimeMillis();
        }
        return dameAttack;
    }

    public int getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return (int) ((long) this.hp * 100 / this.hpMax);
    }

    public int getCurrPercentMP() {
        return (int) ((long) this.mp * 100 / this.mpMax);
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;
    }

    public void subHP(double sub) {
        this.hp -= sub;
        if (this.hp <= 0) {
            this.hp = 0;
            this.setHp(0);
        }
    }

    public void subMP(long sub) {
        this.mp -= sub;
        if (this.mp <= 0) {
            this.mp = 0;
        }
    }

    public void setFullHp() {
        this.hp = this.hpMax;
    }

    public void setFullMp() {
        this.mp = this.mpMax;
    }

    public long calPercent(long param, long percent) {
        return param * percent / 100;
    }

    public void subSucManh(long point) {
        this.power -= point;
        if (this.power <= 0) {
            this.power = 0;
        }
    }

    public void subTiemNang(long point) {
        this.tiemNang -= point;
        if (this.tiemNang <= 0) {
            this.tiemNang = 0;
        }
    }

    public void subSucManhTiemNang(long point) {
        this.power -= point;
        this.tiemNang -= point;
        if (this.power <= 0) {
            this.power = 0;
        }
        if (this.tiemNang <= 0) {
            this.tiemNang = 0;
        }
    }

    public long calSucManhTiemNang(long tiemNang) {
        if (player == null || player.zone == null) {
            return 0;
        }
        if (player.zone.map.type == 3) {
            return 0;
        }

        if (power >= getPowerLimit()) {
            return 10;
        }

        long now = System.currentTimeMillis();
        long originalTiemNang = tiemNang;

        // Buff % từ tlTNSM
        if (this.tlTNSM != null) {
            for (Integer tl : this.tlTNSM) {
                if (tl != null) {
                    tiemNang += calPercent(tiemNang, tl);
                }
            }
        }

        // Cờ
        if (this.player.cFlag != 0) {
            int percent = this.player.cFlag == 8 ? 10 : 5;
            tiemNang += calPercent(tiemNang, percent);
        }

        // Attribute server
        if (this.player.isPl()) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
            if (at != null && !at.isExpired()) {
                tiemNang += calPercent(tiemNang, at.getValue());
            }
        }

        // Thẻ tuần / tháng / năm / chí tôn
        if (this.player.THE_TUAN != 0 && this.player.LASTTIME_THE_TUAN > now) {
            tiemNang += calPercent(tiemNang, this.player.THE_TUAN == 1 ? 20 : 50);
        }
        if (this.player.THE_THANG != 0 && this.player.LASTTIME_THE_THANG > now) {
            tiemNang += calPercent(tiemNang, this.player.THE_THANG == 1 ? 100 : 150);
        }
        if (this.player.THE_NAM != 0 && this.player.LASTTIME_THE_NAM > now) {
            tiemNang += calPercent(tiemNang, this.player.THE_NAM == 1 ? 200 : 300);
        }
        if (this.player.THE_CHI_TON != 0 && this.player.LASTTIME_THE_CHI_TON > now) {
            tiemNang += calPercent(tiemNang, 500);
        }

        // Khẩu trang
        if (this.player.itemTime != null && this.player.itemTime.IsKhauTrang) {
            tiemNang += calPercent(tiemNang, 20);
        }

        // Charm trí tuệ
        long charmBonusMultiplier = 0;
        if (this.player.charms.tdTriTue > now) {
            charmBonusMultiplier += 1;
        }
        if (this.player.charms.tdTriTue3 > now) {
            charmBonusMultiplier += 2;
        }
        if (this.player.charms.tdTriTue4 > now) {
            charmBonusMultiplier += 3;
        }
        if (this.player.charms.tdTriTue5 > now) {
            charmBonusMultiplier += 4;
        }
        if (this.player.charms.tdTriTue7 > now) {
            charmBonusMultiplier += 6;
        }
        if (this.player.charms.tdTriTue10 > now) {
            charmBonusMultiplier += 9;
        }
        if (this.player.charms.tdTriTue20 > now) {
            charmBonusMultiplier += 19;
        }
        tiemNang += originalTiemNang * charmBonusMultiplier;

        // Nội tại
        if (this.intrinsic != null && this.intrinsic.id == 24) {
            tiemNang += calPercent(tiemNang, this.intrinsic.param1);
        }

        // Chibi
        if (this.player.effectSkill.isChibi && this.player.typeChibi == 2) {
            tiemNang += originalTiemNang * 2;
        }

        // Clan buff
        if (this.player.clan != null) {
            if (now <= this.player.clan.LasttimeBuffExp + this.player.clan.TimeStarBuffExp) {
                tiemNang += originalTiemNang * this.player.clan.CongTiemNangSucManhToanBangHoi;
            }
            if (now <= this.player.clan.BuaTriTue) {
                int bonusPercent = Math.min(this.player.clan.level * 20, 200);
                tiemNang += calPercent(originalTiemNang, bonusPercent);
            }
        }

        // Vệ tinh
        if (this.player.satellite != null && this.player.satellite.isIntelligent) {
            tiemNang += calPercent(originalTiemNang, 20);
        }

        // VIP
        if (this.player.getSession() != null && this.player.getSession().Vip_Point > 0) {
            tiemNang += originalTiemNang * 2;
        }

        // Item time
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isRongXuong_3) {
                tiemNang += originalTiemNang * 3;
            }
            if (this.player.itemTime.isUseDuoiKhiTNSM) {
                tiemNang += originalTiemNang * 10;
            }
        }

        // Cộng exp khi đánh mob
        if (this.player.nPoint.Cong20ExpKhiAttackMob) {
            tiemNang += calPercent(tiemNang, 20);
        }

        // Set goten
        if (this.player.setClothes.goten == 5) {
            tiemNang += originalTiemNang * 5;
        }

        // Nếu là đệ tử
        if (this.player.isDeTu) {
            Player master = ((Detu) this.player).master;
            if (master != null) {
                if (master.itemTime.isUseBuaTNSMDetu) {
                    tiemNang += originalTiemNang * 2;
                }
                 if (master.itemTime.isUseBuaTNSMDetu) {
                tiemNang += originalTiemNang * 10;
            }

                long masterNow = now;
                if (master.charms.tdDeTu > masterNow) {
                    tiemNang += originalTiemNang * 2;
                }
                if (master.charms.tdDeTu2 > masterNow) {
                    tiemNang += originalTiemNang * 3;
                }
                if (master.charms.tdDeTu3 > masterNow) {
                    tiemNang += originalTiemNang * 4;
                }
                if (master.charms.tdDeTu4 > masterNow) {
                    tiemNang += originalTiemNang * 5;
                }
                if (master.charms.tdDeTu5 > masterNow) {
                    tiemNang += originalTiemNang * 6;
                }
                if (master.charms.tdDeTu7 > masterNow) {
                    tiemNang += originalTiemNang * 8;
                }
                if (master.charms.tdDeTu10 > masterNow) {
                    tiemNang += originalTiemNang * 10;
                }
                if (master.charms.tdDeTu20 > masterNow) {
                    tiemNang += originalTiemNang * 20;
                }

                if (master.itemTime != null && master.itemTime.isRongXuong) {
                    tiemNang += originalTiemNang * 3;
                }

                if (master.THE_TUAN != 0 && master.LASTTIME_THE_TUAN > masterNow) {
                    tiemNang += calPercent(tiemNang, master.THE_TUAN == 1 ? 20 : 50);
                }
                if (master.THE_THANG != 0 && master.LASTTIME_THE_THANG > masterNow) {
                    tiemNang += calPercent(tiemNang, master.THE_THANG == 1 ? 100 : 150);
                }
                if (master.THE_NAM != 0 && master.LASTTIME_THE_NAM > masterNow) {
                    tiemNang += calPercent(tiemNang, master.THE_NAM == 1 ? 200 : 300);
                }
                if (master.THE_CHI_TON != 0 && master.LASTTIME_THE_CHI_TON > masterNow) {
                    tiemNang += calPercent(tiemNang, 500);
                }

                if (master.nPoint != null) {
                    tiemNang += originalTiemNang / 100 * (master.nPoint.tlTNSMPet + 100);
                }
                if (MapService.gI().isMapKVTH(this.player.zone.map.mapId)) {
                    tiemNang *= 2;
                }
            }
        }

        // Bo/Me
        if (this.player.isBo) {
            tiemNang += originalTiemNang * 2;
        }
        if (this.player.isMe) {
            tiemNang += originalTiemNang * 2;
        }

        // Map đặc biệt
        if (this.player.zone != null) {
            if (MapService.gI().isMapBanDoKhoBau(this.player.zone.map.mapId)) {
                tiemNang *= 6;
            }
            if (MapService.gI().isMapDoanhTrai(this.player.zone.map.mapId)) {
                tiemNang *= 3;
            }
           
            if (MapService.gI().isMapBinhHutNangLuong(this.player.zone.map.mapId)
                    || MapService.gI().isMapDiaNguc(this.player.zone.map.mapId)
                    || MapService.gI().isMapHirudegarn(this.player.zone.map.mapId)
                    || MapService.gI().isMapPotara(this.player.zone.map.mapId)
                    || MapService.gI().isMapThanhDia(this.player.zone.map.mapId)
                    || MapService.gI().isMapHanhTinhThucVat(this.player.zone.map.mapId)) {
                tiemNang = tiemNang / 10;
            }

        }
        if (this.player.isDeTu) {
            Detu pet = (Detu) this.player;
            int type = pet.typeDeTu;

            double factor = 1.0; // mặc định: đệ thường (type 0) = 1x

            // Nhóm sau tăng chậm gấp đôi mỗi bậc
            switch (type) {
                case 0 ->
                    factor = 1.0;       // Đệ thường
                case 1 ->
                    factor = 1.0;       // MaBư
                case 2, 3,4 ->
                    factor = 2.0;    // Sơn Tinh, Thủy Tinh
                case 5->
                    factor = 4.0; // Bư Nhí, Xên Nhí, Fide Đại Ka
                
            }

            tiemNang = (long) (tiemNang / factor);
        }

        // Nhân hệ số server
        tiemNang *= Manager.RATE_EXP_SERVER;

        // Giảm EXP và giới hạn
        tiemNang = calSubTNSM(tiemNang);

        if (tiemNang <= 0) {
            tiemNang = 1;
        }

        return tiemNang;
    }
      private int countItemsHaveAn(int optionId) {
    if (this.player == null
            || this.player.inventory == null
            || this.player.inventory.itemsBody == null) {
        return 0;
    }

    int count = 0;

    for (Item item : this.player.inventory.itemsBody) {
        if (item == null || !item.isNotNullItem() || item.itemOptions == null) {
            continue;
        }

        boolean hasAn = false;
        for (ItemOption io : item.itemOptions) {
            if (io != null && io.optionTemplate != null && io.optionTemplate.id == optionId) {
                hasAn = true;
                break;
            }
        }

        if (hasAn) {
            count++;
        }
    }

    return count;
}
// =========================
// TINH ẤN TRANG BỊ
// =========================
private boolean hasFull5TinhAn() {
    return this.setTinhAn >= 5;
}

private boolean hasFull5NguyetAn() {
    return this.setNguyetAn >= 5;
}

private boolean hasFull5NhatAn() {
    return this.setNhatAn >= 5;
}

// Giảm exp theo mốc + giới hạn 20tr
    public long calSubTNSM(long tiemNang) {
        if (power >= getPowerLimit()) {
            return 0;
        }

        if (this.power >= 120_000_000_000L) {
            tiemNang = calPercent(tiemNang, 1);   // giảm 99.9%
        } else if (this.power >= 100_000_000_000L) {
            tiemNang = calPercent(tiemNang, 1);   // giảm 99%
        } else if (this.power >= 50_000_000_000L) {
            tiemNang = calPercent(tiemNang, 20);  // giảm 80%
        } else if (this.power >= 40_000_000_000L) {
            tiemNang = calPercent(tiemNang, 50);  // giảm 50%
        }

        // Giới hạn tối đa 20 triệu
        if (tiemNang > 20_000_000L) {
            tiemNang = 20_000_000L;
        }
        

        return tiemNang;
    }

    public int getTileHutHp(boolean isMob) {
        if (isMob) {
            return (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public int getTiLeHutMp() {
        return this.tlHutMp;
    }

    public double subDameInjureWithDeff(double dame) {
        long def = this.def;
        dame -= def;
        if (dame < 0) {
            dame = 1;
        }
        return dame;
    }

    public int getTlGold() {
        return this.tlGold;
    }

    /*------------------------------------------------------------------------*/
    public boolean canOpenPower() {
        return this.power >= getPowerLimit();
    }

    public long getPowerLimit() {
        if (powerLimit != null) {
            return powerLimit.getPower();
        }
        return 0;
    }

    public long getPowerNextLimit() {
        PowerLimit powerLimit = PowerLimitManager.getInstance().get(limitPower + 1);
        if (powerLimit != null) {
            return powerLimit.getPower();
        }
        return 0;
    }

    //**************************************************************************
    //POWER - TIEM NANG
    public void powerUp(long power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, this.power);
    }

    public void tiemNangUp(long tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void increasePoint(byte type, short point, boolean manualForPet) {
        if (player.baovetaikhoan) {
            Service.gI().sendThongBao(player, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
            return;
        }
        if (powerLimit == null) {
            return;
        }
        if (point <= 0) {
            return;
        }
        boolean updatePoint = false;
        long tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (2 * (this.hpg + 1000) + pointHp - 20) / 2;
            if ((this.hpg + pointHp) <= powerLimit.getHp()) {
                if (doUseTiemNang(tiemNangUse)) {
                    hpg += pointHp;
                    updatePoint = true;
                }
            } else {
                Service.gI().sendThongBao(player, "HP của bạn đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (2 * (this.mpg + 1000) + pointMp - 20) / 2;
            if ((this.mpg + pointMp) <= powerLimit.getMp()) {
                if (doUseTiemNang(tiemNangUse)) {
                    mpg += pointMp;
                    updatePoint = true;
                }
            } else {
                Service.gI().sendThongBao(player, "KI của bạn đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = point * (2 * this.dameg + point - 1) / 2 * 100;
            if ((this.dameg + point) <= powerLimit.getDamage()) {
                if (doUseTiemNang(tiemNangUse)) {
                    dameg += point;
                    updatePoint = true;
                }
            } else {
                Service.gI().sendThongBao(player, "Sức đánh của bạn đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = 2 * (this.defg + 5) / 2 * 100000;
            if ((this.defg + point) <= powerLimit.getDefense()) {
                if (doUseTiemNang(tiemNangUse)) {
                    defg += point;
                    updatePoint = true;
                }
            } else {
                Service.gI().sendThongBao(player, "Giáp của bạn đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critg; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critg + point) <= powerLimit.getCritical()) {
                if (doUseTiemNang(tiemNangUse)) {
                    critg += point;
                    updatePoint = true;
                }
            } else {
                Service.gI().sendThongBao(player, "Chí mạng của bạn đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return;
            }
        }
        if (updatePoint) {
            Service.gI().point(player);
        }
        if (manualForPet) {
            if (player.Detu != null) {
                Service.gI().InfoPetGoc(player);
                Service.gI().showInfoPet(player);
                Service.gI().point(player);
            }
        }
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang >= tiemNang && this.tiemNang - tiemNang >= 0) {
            this.tiemNang -= tiemNang;
            TaskService.gI().checkDoneTaskUseTiemNang(player);
            return true;
        }
        return false;
    }

    //--------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiPhuc10s;
    private long lastTimeHoiStamina;

    public void update() {
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                int tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (player.effectSkill.isCharging && !player.isDie() && !player.effectSkill.isHaveEffectSkill() && (hp < hpMax || mp < mpMax)) {
                    long hpRecovered = hpMax / 100 * tiLeHoiPhuc;
                    long mpRecovered = mpMax / 100 * tiLeHoiPhuc;

                    PlayerService.gI().hoiPhuc(player, Util.CrisGH(hpRecovered), Util.CrisGH(mpRecovered));

                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.gI().chat(player, "Phục hồi năng lượng " + getCurrPercentHP() + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }

            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
                PlayerService.gI().hoiPhuc(this.player, Util.CrisGH(hpHoi), Util.CrisGH(mpHoi));
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc10s, 10000)) {
                PlayerService.gI().hoiPhuc(this.player, tlHpHoiBanthan_DongMinh, 0);
                this.lastTimeHoiPhuc10s = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                this.stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();

                if (!this.player.isBoss && !this.player.isDeTu && !this.player.isBo && !this.player.isMe && !this.player.isNguoiYeu && !this.player.isConOne && !this.player.isConTwo && !this.player.isConThree) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
        }
        //hồi phục 30s
        //hồi phục thể lực
    }

    public long getFullTN() {
        long tnhp = 0, tnki = 0, tnsd = 0, tng = 0, tncm = 0;

        if (hpg > 0) {
            tnhp = (((hpg / 20L) * (50L + (50L + (hpg / 20L) - 1L)) / 2L) * 20L);
        }
        if (mpg > 0) {
            tnki = (((mpg / 20L) * (50L + (50L + (mpg / 20L) - 1L)) / 2L) * 20L);
        }
        if (dameg > 0) {
            tnsd = ((dameg * (dameg - 1L) * 100L) / 2L);
        }
        if (defg > 0) {
            tng = ((defg * (500000L + (500000L + (defg - 1L) * 100000L))) / 2L);
        }
        if (critg > 0) {
            tncm = ((50L * (((long) Math.pow(5L, critg) - 1L)) / (5L - 1L) * 1000000L));
        }
        return tnhp + tnki + tnsd + tng + tncm;
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.tlTNSM = null;
        this.tlSpeed = null;
    }
}
