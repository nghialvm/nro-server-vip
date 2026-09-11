package nro.boss.map.TrainingBoss;

import QuanLiBoss.BossesData;
import QuanLiBoss.BossID;
import nro.player.Player;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBAN;
import Utils.Util;

public class KhiBubbles extends TrainningBoss {

    public KhiBubbles(Player player) throws Exception {
        super(PHOBAN, BossID.KHI_BUBBLES, BossesData.KHI_BUBBLES);
        playerAtt = player;
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeAFK, 2000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
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
            this.timeChatS = 100;
            doneChatS = true;
        }
        return false;
    }

}





