package nro.boss.map.TrainingBoss;

import QuanLiBoss.BossesData;
import QuanLiBoss.BossID;
import nro.player.Player;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBAN;
import nro.services.Fun.ChangeMapService;
import Utils.Util;

public class ThanVuTru extends TrainningBoss {

    public ThanVuTru(Player player) throws Exception {
        super(PHOBAN, BossID.THAN_VU_TRU, BossesData.THAN_VU_TRU);
        playerAtt = player;
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMap(this, this.zone, 420, 240);
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
    public void afk() {
        if (Util.canDoWithTime(lastTimeMove, 1000)) {
            this.goToXY(playerAtt.location.x, Util.getOne(240, 360));
            this.lastTimeMove = System.currentTimeMillis();
        }
        if (Util.canDoWithTime(lastTimeAFK, 2000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
}





