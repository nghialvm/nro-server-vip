package event.LunarNewYear;


import QuanLiBoss.BossID;
import event.Event;
import jbcd.dao.EventDAO;

public class LunarNewYear extends Event {
    
    @Override
    public void init() {
        super.init();
        EventDAO.loadLunaNewYearEvent();
    }

    @Override
    public void npc() {
        createNpc(5, 79, 900, 408);
        createNpc(13, 79, 950, 384);
        createNpc(20, 79, 1100, 360);
        createNpc(0, 66, 550, 432);
        createNpc(7, 66, 570, 432);
        createNpc(14, 66, 290, 408);
        //2024
        createNpc(0, 80, 720, 432);
        createNpc(7, 80, 210, 432);
        createNpc(14, 80, 755, 408);
        //2025
        createNpc(0, 49, 840, 432);
    }

    @Override
    public void boss() {
        createBoss(BossID.NEW_YEAR_DRAGON, 3);
        createBoss(BossID.THAN_TAI, 3);
        createBoss(BossID.LAN_CON, 2);
        createBoss(BossID.PI_LONG, 2);
        createBoss(BossID.BE_NA, 2);
        createBoss(BossID.LAN_CON, 2);
        createBoss(BossID.MEO_DEN, 2);
        createBoss(BossID.PI_LONG, 2);
        createBoss(BossID.BE_NA, 2);
        createBoss(BossID.LAN_CON, 2);
        createBoss(BossID.MEO_DEN, 2);
        createBoss(BossID.PI_LONG, 2);
        createBoss(BossID.BE_NA, 2);
        createBoss(BossID.MEO_DEN, 2);
    }
}






