package nro.boss.map.The23rdMartialArtCongress;



import nro.player.Player;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.PHOBAN;
import QuanLiBoss.BossesData;

public class ODo extends The23rdMartialArtCongress {

    public ODo(Player player) throws Exception {
        super(PHOBAN, BossID.O_DO, BossesData.O_DO);
        this.playerAtt = player;
    }
}






