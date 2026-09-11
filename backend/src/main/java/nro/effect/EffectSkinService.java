package nro.effect;

import nro.services.Service;
import java.io.IOException;
import network.io.Message;
import nro.player.Player;

/**
 * @author MaiTienDung
 */

public class EffectSkinService {

    public static final byte TURN_ON_EFFECT = 1;
    public static final byte TURN_OFF_EFFECT = 0;
    public static final byte TURN_OFF_ALL_EFFECT = 2;
    public static final byte BLIND_EFFECT = 40;
    public static final byte SLEEP_EFFECT = 41;
    public static final byte STONE_EFFECT = 42;
    private static EffectSkinService i;

    public static EffectSkinService gI() {
        if (i == null) {
            i = new EffectSkinService();
        }
        return i;
    }
    
    //----------------------------------SET HOÁ BĂNG----------------------------
    public void setDraburaFrost(Player player, long lastTime, int time) {
        player.effectSkin.lastTimeDraburaFrost = lastTime;
        player.effectSkin.timeDraburaFrost = time;
        player.effectSkin.isDraburaFrost = true;
        EffectMapService.gI().sendCharEffect_HoaBang(player);
        EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.ICE_EFFECT);
        Service.getInstance().point(player);
    }

    public void removeDraburaFrost(Player player) {
        player.effectSkin.isDraburaFrost = false;
        EffectMapService.gI().Remove_sendCharEffect_HoaBang(player);
        sendEffectPlayer(player, player, EffectSkillService.TURN_OFF_EFFECT, EffectSkillService.ICE_EFFECT);
        Service.getInstance().point(player);
        Service.getInstance().Send_Caitrang(player);
    }
    
    //

    public void setSlow(Player player, long lastTimeSlow, int timeSlow) {
        player.effectSkin.lastTimeSlow = lastTimeSlow;
        player.effectSkin.timeSlow = timeSlow;
        player.effectSkin.isSlow = true;
        Service.getInstance().point(player);
    }

    public void removeSlow(Player player) {
        player.effectSkin.isSlow = false;
        Service.getInstance().point(player);
    }

    public void setSocola(Player player, long lastTimeSocola, int timeSocola) {
        player.effectSkin.lastTimeSocola = lastTimeSocola;
        player.effectSkin.timeSocola = timeSocola;
        player.effectSkin.isSocola = true;
        Service.getInstance().point(player);
    }

    public void removeSocola(Player player) {
        player.effectSkin.isSocola = false;
        Service.getInstance().point(player);
        Service.getInstance().Send_Caitrang(player);
    }
    
    //----------------------------------SET THỎ ĐẠI KA--------------------------
    public void setThoDaiKa(Player player, long lastTime, int time) {
        if (player.nPoint != null && player.nPoint.isThoDaiCa) {
            return;
        }
        player.effectSkin.lastTimeThoDaiKa = lastTime;
        player.effectSkin.timeThoDaiKa = time;
        player.effectSkin.isThoDaiKa = true;
        Service.getInstance().point(player);
    }

    public void removeThoDaiKa(Player player) {
        player.effectSkin.isThoDaiKa = false;
        Service.getInstance().point(player);
        Service.getInstance().Send_Caitrang(player);
    }

    public void setCuongNo(Player player, long lastTimeCuongNo, int timeCuongNo) {
        player.effectSkin.isNezuko = true;
        player.effectSkin.lastTimeNezuko = lastTimeCuongNo;
        player.effectSkin.timeNezuko = timeCuongNo;
        Service.getInstance().point(player);
    }

    public void removeCuongNo(Player player) {
        player.effectSkin.isNezuko = false;
        Service.getInstance().point(player);
    }

    public void setInosuke(Player player, long lastTimeTanjiro, int timeTanjiro) {
        player.effectSkin.isInosuke = true;
        player.effectSkin.lastTimeInosuke = lastTimeTanjiro;
        player.effectSkin.timeInosuke = timeTanjiro;
        Service.getInstance().point(player);
    }

    public void removeInosuke(Player player) {
        player.effectSkin.isInosuke = false;
        Service.getInstance().point(player);
    }

    public void setTanjiro(Player player, long lastTimeTanjiro, int timeTanjiro) {
        player.effectSkin.isTanjiro = true;
        player.effectSkin.lastTimeTanjiro = lastTimeTanjiro;
        player.effectSkin.timeTanjiro = timeTanjiro;
        Service.getInstance().point(player);
    }

    public void removeTanjiro(Player player) {
        player.effectSkin.isTanjiro = false;
        Service.getInstance().point(player);
    }

    public void setZenitsu(Player player, long lastTimeZenitsu, int timeZenitsu) {
        player.effectSkin.isZenitsu = true;
        player.effectSkin.lastTimeZenitsu = lastTimeZenitsu;
        player.effectSkin.timeZenitsu = timeZenitsu;
        Service.getInstance().point(player);
    }

    public void removeZenitsu(Player player) {
        player.effectSkin.isZenitsu = false;
        Service.getInstance().point(player);
    }

    public void setInoHashi(Player player, long lastTimeInoHashi, int timeInoHashi) {
        player.effectSkin.isInoHashi = true;
        player.effectSkin.lastTimeInoHashi = lastTimeInoHashi;
        player.effectSkin.timeInoHashi = timeInoHashi;
        Service.getInstance().point(player);
    }

    public void removeInoHashi(Player player) {
        player.effectSkin.isInoHashi = false;
        Service.getInstance().point(player);
    }

    public void sendEffectPlayer(Player plUseSkill, Player plTarget, byte toggle, byte effect) {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(toggle); //0: hủy hiệu ứng, 1: bắt đầu hiệu ứng
            msg.writer().writeByte(0); //0: vào phần phayer, 1: vào phần mob
            if (toggle == TURN_OFF_ALL_EFFECT) {
                msg.writer().writeInt((int) plTarget.id);
            } else {
                msg.writer().writeByte(effect); //loại hiệu ứng
                msg.writer().writeInt((int) plTarget.id); //id player dính effect
                msg.writer().writeInt((int) plUseSkill.id); //id player dùng skill
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
            msg.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
