package nro.boss.map.The23rdMartialArtCongress;



import nro.player.Player;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.PHOBAN;
import QuanLiBoss.BossesData;

public class ChaPa extends The23rdMartialArtCongress {

    public ChaPa(Player player) throws Exception {
        super(PHOBAN, BossID.CHA_PA, BossesData.CHA_PA);
        this.playerAtt = player;
    }
}






