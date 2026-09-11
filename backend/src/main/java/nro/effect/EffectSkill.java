package nro.effect;

import nro.mob.Mob;
import nro.player.Player;
import models.Item.ItemTimeService;
import Utils.Util;
import lombok.Setter;

public class EffectSkill {

    @Setter
    private Player player;
                
    //Hoá Bí Ngô
    public boolean isBiNgo;
    public long LastTimeBiNgo;
    public int TimeBiNgo;
    
    //VIRUS
    public boolean isBongTuyet;
    public long LastTimeBongTuyet;
    public int TimeBongTuyet;
    
    //VIRUS
    public boolean isVirus;
    public long LastTimeVirus;
    public int TimeVirus;
    
    //Mabu Hold
    public boolean isMabuHold;
    
    //PK Sieu Than Thuy
    public boolean isPKSTT;
    public long lastTimePKSTT;
    public int timePKSTT;
    
    //PK Commeson
    public boolean isPKCommeson;
    public long lastTimePKCommeson;
    public int timePKCommeson;
    
    //Bình
    public boolean isBinh;
    public int typeBinh;
    public long lastTimeUpBinh;
    public int timeBinh;
    public Player playerUseMafuba;
    
    //Use Mafuba
    public boolean isUseMafuba;
    public long lastTimeUseMafuba;
    public int timeUseMafuba;
    
    //Intrinsic
    public boolean isIntrinsic;
    public long lastTimeUseSkill;
    public int skillID;
    public int cooldown;
    
    //Intrinsic vip
    public boolean isIntrinsicVip;
    public long lastTimeUseSkillVip;
    public int skillIDVip;
    public int cooldownVip;
        
    //Hóa đá
    public boolean isStone;
    public long lastTimeStone;
    public int timeStone;
    
    //Tàn hình
    public boolean isTanHinh;
    public long lastTimeTanHinh;
    public int timeTanHinh;
    
    //Dame Buff
    public boolean isDameBuff;
    public long lastTimeDameBuff;
    public int timeDameBuff;
    public int tileDameBuff;
    
    //Fide Daika
    public boolean isFideDaiCa;
    public long LastTimeFideDaiCa;
    public int TimeFideDaiCa;
    
    //Fide Xinbato
    public boolean isXinbato;
    public long LastTimeXinbato;
    public int TimeXinbato;
    
    //halloween
    public boolean isHalloween;
    public long lastTimeHalloween;
    public int timeHalloween;
    public int idOutfitHalloween;
    
    //Chibi
    public boolean isChibi;
    public long lastTimeChibi;
    public int timeChibi;

    //thái dương hạ san
    public boolean isStun;
    public long lastTimeStartStun;
    public int timeStun;

    //khiên năng lượng
    public boolean isShielding;
    public long lastTimeShieldUp;
    public int timeShield;

    //biến khỉ
    public boolean isMonkey;
    public byte levelMonkey;
    public long lastTimeUpMonkey;
    public int timeMonkey;
    
    //Use Skill Monkey
    public boolean isUseSkillMonkey;
    public long lastTimeUseSkillMonkey;
    public int timeUseSkillMonkey;

    //tái tạo năng lượng
    public boolean isCharging;
    public int countCharging;

    //huýt sáo
    public int tiLeHPHuytSao;
    public long lastTimeHuytSao;
    
    //thôi miên
    public boolean isThoiMien;
    public long lastTimeThoiMien;
    public int timeThoiMien;

    //trói
    public boolean useTroi;
    public boolean anTroi;
    public long lastTimeTroi;
    public int timeTroi;
    public Player plTroi;
    public Player plAnTroi;
    public Mob mobAnTroi;
    
    //dịch chuyển tức thời
    public boolean isBlindDCTT;
    public long lastTimeBlindDCTT;
    public int timeBlindDCTT;
    
    //Biến Hình Cumber
    public boolean iscumber;
    public long lastTimeUpcumber;
    public int timecumber;
    
    //Biến Hình Cumber2
    public boolean iscumber2;
    public long lastTimeUpcumber2;
    public int timecumber2;
    
    //Biến Hình Pain
    public boolean isPain;
    public long lastTimeUpPain;
    public int timePain;
    
    //Biến Hình Kefla
    public boolean iskefla;
    public long lastTimeUpkefla;
    public int timekefla;
    
    //socola
    public boolean isSocola;
    public long lastTimeSocola;
    public int timeSocola;
    public int countPem1hp;   
        
    //bien super ssj
    public boolean isTranformation;
    public byte levelTranformation = 0;
    public long lastTimeTranformation;
    public int timeTranformation;

    public boolean isEvolution;
    public byte levelEvolution;
    public long lastTimeEvolution;
    public int timeEvolution;    
    
    //Sức Mạnh Bộc Phá
    public boolean isSUcManhBocPha;
    public long lastTimeSUcManhBocPhaUp;
    public int timeSUcManhBocPha;
    
    public boolean isBodyChangeTechnique;
    
    public EffectSkill(Player player) {
        this.player = player;
    }

    public void removeSkillEffectWhenDie() {
        if (isMonkey) {
            EffectSkillService.gI().monkeyDown(player);
        }
        if (isUseSkillMonkey) {
            EffectSkillService.gI().finishUseMonkey(player);
        }
        if (isBinh) {
            EffectSkillService.gI().BinhDown(player);
        }
        if (isTranformation) {
            EffectSkillService.gI().TranformationDown(player);
        }
        if (isEvolution) {
            EffectSkillService.gI().EvolutionDown(player);
        }
        if (isSUcManhBocPha) {
            EffectSkillService.gI().removeSucManhBocPha(player);
            switch (player.gender) {
                case 0:
                    ItemTimeService.gI().removeItemTime(player, 11997);
                    break;
                case 1:
                    ItemTimeService.gI().removeItemTime(player, 12000);
                    break;
                case 2:
                    ItemTimeService.gI().removeItemTime(player, 12003);
                    break;
                default:
                    break;
            }
        }
        if (isShielding) {
            EffectSkillService.gI().removeShield(player);
            ItemTimeService.gI().removeItemTime(player, 3784);
        }
        if (useTroi) {
            EffectSkillService.gI().removeUseTroi(this.player);
        }
        if (isStun) {
            EffectSkillService.gI().removeStun(this.player);
        }
        if (isThoiMien) {
            EffectSkillService.gI().removeThoiMien(this.player);
        }
        if (isBlindDCTT) {
            EffectSkillService.gI().removeBlindDCTT(this.player);
        }
        if (isDameBuff) {
            EffectSkillService.gI().removeDameBuff(this.player);
        }
        if (isXinbato) {
            EffectSkillService.gI().removeXinbato(this.player);
        }
        if (isFideDaiCa) {
            EffectSkillService.gI().removeFideDaiCa(this.player);
        }
        if (isStone) {
            EffectSkillService.gI().removeStone(this.player);
        }
        if (isTanHinh) {
            EffectSkillService.gI().removeTanHinh(this.player);
        }
        if (isMabuHold) {
            EffectSkillService.gI().removeMabuHold(this.player);
        }
        if (isVirus) {
            EffectSkillService.gI().removeVirus(this.player);
        }
        if (isBongTuyet) {
            EffectSkillService.gI().removeBongTuyet(this.player);
        }
        if (isBiNgo) {
            EffectSkillService.gI().removeBiNgo(this.player);
        }
    }

    public void update() {
        if (isMonkey && (Util.canDoWithTime(lastTimeUpMonkey, timeMonkey))) {
            EffectSkillService.gI().monkeyDown(player);
        }
        if (isUseSkillMonkey && Util.canDoWithTime(lastTimeUseSkillMonkey, timeUseSkillMonkey)) {
            EffectSkillService.gI().finishUseMonkey(player);
        }
        if (isPKSTT && Util.canDoWithTime(lastTimePKSTT, timePKSTT)) {
            EffectSkillService.gI().removePKSTT(this.player);
        }
        if (isPKCommeson && Util.canDoWithTime(lastTimePKCommeson, timePKCommeson)) {
            EffectSkillService.gI().removePKCommeson(this.player);
        }
        if (isBinh && (Util.canDoWithTime(lastTimeUpBinh, timeBinh))) {
            EffectSkillService.gI().BinhDown(player);
        }
        if (isUseMafuba && Util.canDoWithTime(lastTimeUseMafuba, timeUseMafuba)) {
            EffectSkillService.gI().finishUseMafuba(player);
        }
        if (isDameBuff && Util.canDoWithTime(lastTimeDameBuff, timeDameBuff)) {
            EffectSkillService.gI().removeDameBuff(this.player);
        }
        if (isXinbato && Util.canDoWithTime(LastTimeXinbato, TimeXinbato)) {
            EffectSkillService.gI().removeXinbato(this.player);
        }
        if (isFideDaiCa && Util.canDoWithTime(LastTimeFideDaiCa, TimeFideDaiCa)) {
            EffectSkillService.gI().removeFideDaiCa(this.player);
        }
        if (isHalloween && Util.canDoWithTime(lastTimeHalloween, timeHalloween)) {
            EffectSkillService.gI().removeHalloween(this.player);
        }
        if (isChibi && Util.canDoWithTime(lastTimeChibi, timeChibi)) {
            EffectSkillService.gI().removeChibi(this.player);
        }
        if (iscumber && (Util.canDoWithTime(lastTimeUpcumber, timecumber))) {
            EffectSkillService.gI().cumberdown(player);
        }
        if (iscumber2 && (Util.canDoWithTime(lastTimeUpcumber2, timecumber2))) {
            EffectSkillService.gI().cumberdown2(player);
        }
        if (isPain && (Util.canDoWithTime(lastTimeUpPain, timePain))) {
            EffectSkillService.gI().paindown(player);
        }
        if (iskefla && (Util.canDoWithTime(lastTimeUpkefla, timekefla))) {
            EffectSkillService.gI().kefladown(player);
        }
        if (isShielding && (Util.canDoWithTime(lastTimeShieldUp, timeShield))) {
            EffectSkillService.gI().removeShield(player);
        }
        if (isTranformation && (Util.canDoWithTime(lastTimeTranformation, 300000))) {
            EffectSkillService.gI().TranformationDown(player);
        }
        if (isEvolution && (Util.canDoWithTime(lastTimeEvolution, timeEvolution))) {
            EffectSkillService.gI().EvolutionDown(player);
        }
        if (isSUcManhBocPha && (Util.canDoWithTime(lastTimeSUcManhBocPhaUp, timeSUcManhBocPha))) {
            EffectSkillService.gI().removeSucManhBocPha(player);
        }
        if (useTroi && Util.canDoWithTime(lastTimeTroi, timeTroi) || plAnTroi != null && plAnTroi.isDie() || useTroi && isHaveEffectSkill()) {
            EffectSkillService.gI().removeUseTroi(this.player);
        }
        if (isStun && Util.canDoWithTime(lastTimeStartStun, timeStun)) {
            EffectSkillService.gI().removeStun(this.player);
        }
        if (isThoiMien && (Util.canDoWithTime(lastTimeThoiMien, timeThoiMien))) {
            EffectSkillService.gI().removeThoiMien(this.player);
        }
        if (isBlindDCTT && (Util.canDoWithTime(lastTimeBlindDCTT, timeBlindDCTT))) {
            EffectSkillService.gI().removeBlindDCTT(this.player);
        }
        if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola))) {
            EffectSkillService.gI().removeSocola(this.player);
        }
        if (tiLeHPHuytSao != 0 && Util.canDoWithTime(lastTimeHuytSao, 30000)) {
            EffectSkillService.gI().removeHuytSao(this.player);
        }
        if (isStone && Util.canDoWithTime(lastTimeStone, timeStone)) {
            EffectSkillService.gI().removeStone(this.player);
        }
        if (isTanHinh && Util.canDoWithTime(lastTimeTanHinh, timeTanHinh)) {
            EffectSkillService.gI().removeTanHinh(this.player);
        }
        if (isIntrinsic && Util.canDoWithTime(lastTimeUseSkill, cooldown)) {
            EffectSkillService.gI().releaseCooldownSkillByIntrinsic(player);
        }
        if (isIntrinsicVip && Util.canDoWithTime(lastTimeUseSkillVip, cooldownVip)) {
            EffectSkillService.gI().releaseCooldownSkillByIntrinsicVip(player);
        }
        if (isVirus && Util.canDoWithTime(LastTimeVirus, TimeVirus)) {
            EffectSkillService.gI().removeVirus(player);
        }
        if (isBongTuyet && Util.canDoWithTime(LastTimeBongTuyet, TimeBongTuyet)) {
            EffectSkillService.gI().removeBongTuyet(player);
        }
        if (isBiNgo && Util.canDoWithTime(LastTimeBiNgo, TimeBiNgo)) {
            EffectSkillService.gI().removeBiNgo(player);
        }
    }
    
    public boolean isHaveEffectSkill() {
        return (isStun || isBlindDCTT || anTroi || isThoiMien || isStone || isMabuHold || isUseSkillMonkey) && !player.isDie();
    }

    public void dispose() {
        this.player = null;
        this.plAnTroi = null;
        this.plTroi = null;
        this.mobAnTroi = null;
        this.playerUseMafuba = null;
    }
}
