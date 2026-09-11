package event.HungKingsEvent;

import QuanLiBoss.BossID;
import Utils.Util;
import event.Event;
import jbcd.dao.EventDAO;

/**
 *
 * @author Anwin
 */
public class HungVuong extends Event {

    @Override
    public void init() {
        super.init();
        EventDAO.loadHungVuongEvent();
    }

    @Override
    public void boss() {
        createBoss(BossID.THUY_TINH, 5);
        createBoss(BossID.THUY_TINH_NEW, 5);
        createBoss(BossID.RONG_NHI_1S, 3);
        createBoss(BossID.RONG_NHI_2S, 3);
        createBoss(BossID.RONG_NHI_3S, 3);
        createBoss(BossID.RONG_NHI_4S, 3);
        createBoss(BossID.RONG_NHI_5S, 3);
        createBoss(BossID.RONG_NHI_6S, 3);
        createBoss(BossID.RONG_NHI_7S, 3);
    }

    @Override
    public void npc() {
        createNpc(5, 52, 900, 408);
        createNpc(13, 52, 940, 384);
        createNpc(20, 52, 1130, 360);
        createNpc(21, 51, 160, 336);
        createNpc(22, 51, 450, 336);
        createNpc(23, 51, 160, 336);
        createNpc(2, 52, 1045, 288);
        createNpc(9, 52, 140, 216);
        createNpc(16, 52, 360, 288);
        createNpc(27, 52, 1225, 336);
        createNpc(5, 66, 1040, 408);
        createNpc(13, 66, 410, 384);
        createNpc(20, 66, 160, 288);
        createNpc(0, 52, 540, 432);
        createNpc(7, 52, 560, 432);
        createNpc(14, 52, 950, 408);
    }

    @Override
    public void bigboss() {
        int[] mapIds = {3, 4, 27, 28};
        int[] bossIds = {82, 83, 84};

        for (int i = 0; i < 34; i++) {
            int mapId = mapIds[Util.nextInt(0, mapIds.length - 1)];
            int bossId = bossIds[i % bossIds.length]; // luân phiên 82, 83, 84
            createBigBoss(mapId, bossId, (byte) 1, Util.nextInt(0, 10), 15_000_000L, 10_000, Util.nextInt(100, 900), 300);
        }
    }
}