package nro.boss.map.The23rdMartialArtCongress;



import nro.player.Player;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.PHOBAN;
import QuanLiBoss.BossesData;

public class Xinbato extends The23rdMartialArtCongress {

    public Xinbato(Player player) throws Exception {
        super(PHOBAN, BossID.XINBATO, BossesData.XINBATO);
        this.playerAtt = player;
    }
}






