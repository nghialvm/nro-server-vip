package QuanLiBoss;

public class SmallBoss extends Boss {

    protected Boss bigBoss;

    public SmallBoss(int idgroup, BossData... data) throws Exception {
        super(idgroup, data);
    }

    public SmallBoss(int idgroup, Boss bigBoss, BossData... data) throws Exception {
        super(idgroup, data);
        this.bigBoss = bigBoss;
    }    
}




