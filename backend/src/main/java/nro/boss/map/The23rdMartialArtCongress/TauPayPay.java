package nro.boss.map.The23rdMartialArtCongress;



import nro.player.Player;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.PHOBAN;
import QuanLiBoss.BossesData;

public class TauPayPay extends The23rdMartialArtCongress {

    public TauPayPay(Player player) throws Exception {
        super(PHOBAN, BossID.TAU_PAY_PAY, BossesData.TAU_PAY_PAY);
        this.playerAtt = player;
    }
}






