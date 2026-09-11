package nro.dragon.ChristMasEvent;

import nro.inventory.InventoryService;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.player.Player;

public class ShenronChristMasEventService {

    private static ShenronChristMasEventService instance;

    public static final short NGOC_RONG_1_SAO = 925;
    public static final short NGOC_RONG_2_SAO = 926;
    public static final short NGOC_RONG_3_SAO = 927;
    public static final short NGOC_RONG_4_SAO = 928;
    public static final short NGOC_RONG_5_SAO = 929;
    public static final short NGOC_RONG_6_SAO = 930;
    public static final short NGOC_RONG_7_SAO = 931;

    public static ShenronChristMasEventService gI() {
        if (instance == null) {
            instance = new ShenronChristMasEventService();
        }
        return instance;
    }

    public void openMenuSummonShenron(Player pl, int type) {
        pl.iDMark.setShenronType(type);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.SUMMON_SHENRON_EVENT_CHRISTMAS, -1, "Bạn có muốn gọi Rồng Băng không ?",
                "Đồng ý", "Từ chối");
    }

    public void summonShenron(Player player) {
        if (MapService.gI().isMapCallDragon(player.zone.map.mapId)) {
            if (checkShenronBall(player)) {
                if (player.isShenronAppear_Christmas || player.shenronEvent_Christmas != null) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }

                if (Util.canDoWithTime(player.lastTimeShenronAppeared_Christmas, ShenronChristMasEvent.timeResummonShenron)) {
                    for (int i = NGOC_RONG_1_SAO; i <= NGOC_RONG_7_SAO; i++) {
                        try {
                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, i), 1);
                        } catch (Exception ex) {
                        }
                    }
                    InventoryService.gI().sendItemBag(player);
                    ShenronChristMasEvent shenron = new ShenronChristMasEvent();
                    shenron.setPlayer(player);
                    ShenronChristMasEventManager.gI().add(shenron);
                    player.shenronEvent_Christmas = shenron;
                    shenron.setZone(player.zone);
                    shenron.activeShenron(true, ShenronChristMasEvent.DRAGON_EVENT);
                    shenron.sendWhishesShenron();
                } else {
                    int timeLeft = (int) ((ShenronChristMasEvent.timeResummonShenron - (System.currentTimeMillis() - player.lastTimeShenronAppeared_Christmas)) / 1000);
                    Service.gI().sendThongBao(player, "Vui lòng đợi " + (timeLeft < 7200 ? timeLeft + " giây" : timeLeft / 60 + " phút") + " nữa");
                }
            }
        } else {
            Service.gI().sendThongBao(player, "Không thể gọi rồng ở đây");
        }
    }

    private boolean checkShenronBall(Player pl) {
        for (int i = NGOC_RONG_1_SAO; i <= NGOC_RONG_7_SAO; i++) {
            if (!InventoryService.gI().isExistItemBag(pl, i)) {
                Item it = ItemService.gI().createNewItem((short) i);
                Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên " + it.template.name);
                return false;
            }
        }
        return true;
    }
}
