package nro.boss.map.TrainingBoss;

import QuanLiBoss.BossesData;
import QuanLiBoss.BossID;
import nro.player.Player;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBAN;
import nro.services.Fun.ChangeMapService;
import Utils.Util;

public class ThanMeoKarin extends TrainningBoss {

    private long lastTimeBay;
    private long lastTimeBay2;

    public ThanMeoKarin(Player player) throws Exception {
        super(PHOBAN, BossID.THAN_MEO_KARIN, BossesData.THAN_MEO_KARIN);
        this.playerAtt = player;
    }
    
    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMap(this, this.zone, 420, 408);
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public boolean chatS() {
        if (Util.canDoWithTime(lastTimeChatS, timeChatS)) {
            if (this.doneChatS) {
                return true;
            }
            String textChat = this.data[this.currentLevel].getTextS()[playerAtt.isThachDau ? 1 : 0];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.moveToPlayer(playerAtt);
            this.lastTimeChatS = System.currentTimeMillis();
            this.timeChatS = 2000;
            doneChatS = true;
        }
        return false;
    }

    @Override
    public void bayLungTung() {
        if (Util.canDoWithTime(lastTimeBay, 5000)) {
            goToXY(playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)), this.location.y - 100, false);
            lastTimeBay = System.currentTimeMillis();
        }
        if (Util.canDoWithTime(lastTimeBay2, 5500)) {
            goToXY(playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)), this.location.y - 100, false);
            lastTimeBay2 = System.currentTimeMillis();
        }
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeMove, 1500)) {
            this.goToXY(playerAtt.location.x, playerAtt.location.y);
            this.lastTimeMove = System.currentTimeMillis();
        }
        if (Util.canDoWithTime(lastTimeAFK, 2000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
}





