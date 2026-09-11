package QuanLiBoss.IBoss;




import nro.player.Player;
import QuanLiBoss.BossStatus;

public interface IBoss {

    void update();

    void updateInfo();

    void initBase();

    void changeStatus(BossStatus status);

    Player getPlayerAttack();
    
    Player getAttackPlayer() throws Exception;

    void changeToTypePK();

    void changeToTypeNonPK();

    void moveToPlayer(Player player);

    void moveTo(int x, int y);

    void checkPlayerDie(Player player);

    void wakeupAnotherBossWhenAppear();

    void wakeupAnotherBossWhenDisappear();

    void reward(Player plKill);

    void attack();

    void rest();

    void respawn();

    void joinMap();

    boolean chatS();

    void doneChatS();

    void active();

    void chatM();

    void die(Player plKill);

    boolean chatE();

    void doneChatE();

    void leaveMap();

    void autoLeaveMap();

    void afk();
    
    void goToXY(int x, int y, boolean isTeleport);
    
    void goToPlayer(Player pl, boolean isTeleport);

    short getHead();

    short getBody();

    short getLeg();

    short getFlagBag();

    byte getAura();

    byte getEffFront();
}
