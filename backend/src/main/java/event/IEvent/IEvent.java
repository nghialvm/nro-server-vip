package event.IEvent;



public interface IEvent {

    void init();

    void npc();

    void createNpc(int mapId, int npcId, int x, int y);

    void boss();

    void createBoss(int bossId, int... total);

    void itemMap();

    void itemBoss();
    
    void bigboss();
    
    void createBigBoss(int mapId, int BigbossTempId, byte Level, int zoneId, long hpBigboss, long dameBigboss, int x, int y);
    
    void mob();
    
    void createMob(int mapId, int BigbossTempId, byte Level, int zoneId, long hpBigboss, long dameBigboss, int x, int y);
    
    void runEvent();
}






