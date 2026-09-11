package nro.dragon.HalloweenEvent;



import nro.inventory.InventoryService;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.player.Player;

public class ShenronHalloweenEventService {

    private static ShenronHalloweenEventService instance;

    public static final short NGOC_RONG_1_SAO = 702;
    public static final short NGOC_RONG_2_SAO = 703;
    public static final short NGOC_RONG_3_SAO = 704;
    public static final short NGOC_RONG_4_SAO = 705;
    public static final short NGOC_RONG_5_SAO = 706;
    public static final short NGOC_RONG_6_SAO = 707;
    public static final short NGOC_RONG_7_SAO = 708;

    public static ShenronHalloweenEventService gI() {
        if (instance == null) {
            instance = new ShenronHalloweenEventService();
        }
        return instance;
    }

    public void openMenuSummonShenron(Player pl, int type) {
        pl.iDMark.setShenronType(type);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.SUMMON_SHENRON_EVENT_HALLOWEEN, -1, "Bạn có muốn gọi Rồng Xương không ?",
                "Đồng ý", "Từ chối");
    }

    public void summonShenron(Player player) {
        if (MapService.gI().isMapCallDragon(player.zone.map.mapId)) {
            if (checkShenronBall(player)) {
                if (player.isShenronAppear_Halloween || player.shenronEvent_Halloween != null) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }

                if (Util.canDoWithTime(player.lastTimeShenronAppeared_Halloween, ShenronHalloweenEvent.timeResummonShenron)) {
                    for (int i = NGOC_RONG_1_SAO; i <= NGOC_RONG_7_SAO; i++) {
                        try {
                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, i), 1);
                        } catch (Exception ex) {
                        }
                    }
                    InventoryService.gI().sendItemBag(player);
                    ShenronHalloweenEvent shenron = new ShenronHalloweenEvent();
                    shenron.setPlayer(player);
                    ShenronHalloweenEventManager.gI().add(shenron);
                    player.shenronEvent_Halloween = shenron;
                    shenron.setZone(player.zone);
                    shenron.activeShenron(true, ShenronHalloweenEvent.DRAGON_EVENT);
                    shenron.sendWhishesShenron();
                } else {
                    int timeLeft = (int) ((ShenronHalloweenEvent.timeResummonShenron - (System.currentTimeMillis() - player.lastTimeShenronAppeared_Halloween)) / 1000);
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
