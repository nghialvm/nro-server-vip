package models.Item;



import nro.player.Player;
import Utils.Util;

public class ItemEvent {

    public Player player;

    public long lastTVGSTime;

    public int remainingTVGSCount;
    
    public long lastKeoGiangSinhTime;

    public int remainingKeoGiangSinhCount;

    public long lastHHTime;

    public int remainingHHCount;

    public long lastBNTime;

    public int remainingBNCount;

    public ItemEvent(Player player) {
        this.player = player;
    }

    public boolean canDropTatVoGiangSinh(int maxCount) {
        if (Util.isAfterMidnight(lastTVGSTime)) {
            remainingTVGSCount = maxCount;
            lastTVGSTime = System.currentTimeMillis();
            return true;
        } else if (remainingTVGSCount > 0) {
            remainingTVGSCount--;
            return true;
        }
        return false;
    }
    
    public boolean canDropKeoGiangSinh(int maxCount) {
        if (Util.isAfterMidnight(lastKeoGiangSinhTime)) {
            remainingKeoGiangSinhCount = maxCount;
            lastKeoGiangSinhTime = System.currentTimeMillis();
            return true;
        } else if (remainingKeoGiangSinhCount > 0) {
            remainingKeoGiangSinhCount--;
            return true;
        }
        return false;
    }

    public boolean canDropHoaHong(int maxCount) {
        if (Util.isAfterMidnight(lastHHTime)) {
            remainingHHCount = maxCount;
            lastHHTime = System.currentTimeMillis();
            return true;
        } else if (remainingHHCount > 0) {
            remainingHHCount--;
            return true;
        }
        return false;
    }

    public boolean canDropBinhNuoc(int maxCount) {
        if (Util.isAfterMidnight(lastBNTime)) {
            remainingBNCount = maxCount;
            lastBNTime = System.currentTimeMillis();
            return true;
        } else if (remainingBNCount > 0) {
            remainingBNCount--;
            return true;
        }
        return false;
    }
}






