package nro.npc.ListNpc;

import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.map.The23rdMartialArtCongress.The23rdMartialArtCongressService;
import nro.map.WorldMartialArtsTournament.WorldMartialArtsTournamentService;
import nro.npc.Npc;
import nro.player.Player;
import nro.top.TopService;

public class GhiDanh extends Npc {

    String[] menuselect = new String[]{};

    private static final short THOI_VANG_ID = 457;
    private static final int SO_THOI_VANG_MOI_LAN = 1;

    public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (canOpenNpc(pl)) {
            if (this.map.mapId == 52) {
                WorldMartialArtsTournamentService.menu(this, pl);

            } else if (this.mapId == 129) {

                // Bỏ reset goldChallenge/rubyChallenge vì không dùng nữa
                if (Util.isAfterMidnight(pl.lastTimePKDHVT23)) {
                    pl.levelWoodChest = 0;
                }

                if (pl.levelWoodChest == 0) {
                    menuselect = new String[]{
                            "Hướng\ndẫn\nthêm",
                            "Thi đấu\n1 Thỏi vàng",
                            "Về\nĐại Hội\nVõ Thuật"
                    };
                } else {
                    menuselect = new String[]{
                            "Hướng\ndẫn\nthêm",
                            "Thi đấu\n1 Thỏi vàng",
                            "Nhận\nthưởng\nRương Cấp\n" + pl.levelWoodChest,
                            "Về\nĐại Hội\nVõ Thuật"
                    };
                }

                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                        "Đại hội võ thuật lần thứ 23\n"
                                + "Diễn ra bất kể ngày đêm, ngày nghỉ, ngày lễ\n"
                                + "Phần thưởng vô cùng quý giá\n"
                                + "Nhanh chóng tham gia nào",
                        menuselect, "Từ chối");

            } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                        "Tính điểm máy đấm nào các thí sinh\n"
                                + "Máy đấm đằng kia không phải tôi.",
                        "Top 100\nTrái đất",
                        "Top 100\nNamec",
                        "Top 100\nXayda",
                        "Xem điểm",
                        "Đóng");
            } else {
                super.openBaseMenu(pl);
            }
        }
    }

    private long TopMayDam(Player player) {
        if (player == null) return 0;

        switch (player.gender) {
            case 0:
                return player.TopMayDamTraiDat;
            case 1:
                return player.TopMayDamNamec;
            case 2:
                return player.TopMayDamXayda;
        }

        return 0;
    }

    private boolean truThoiVang(Player player, int soLuong) {
        Item tv = InventoryService.gI().findItemBag(player, THOI_VANG_ID);

        if (tv != null && tv.isNotNullItem() && tv.quantity >= soLuong) {
            InventoryService.gI().subQuantityItemsBag(player, tv, soLuong);
            InventoryService.gI().sendItemBag(player);
            return true;
        }

        return false;
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        if (this.map.mapId == 52) {
            WorldMartialArtsTournamentService.confirm(this, player, select);
            return;
        }

        if (this.map.mapId == 42 || this.map.mapId == 43 || this.map.mapId == 44) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        TopService.showListTopMayDamTraiDat(player);
                        break;

                    case 1:
                        TopService.showListTopMayDamNamec(player);
                        break;

                    case 2:
                        TopService.showListTopMayDamXayda(player);
                        break;

                    case 3:
                        NpcService.gI().createTutorial(
                                player,
                                tempId,
                                this.avartar,
                                "Điểm máy đấm của bạn là "
                                        + Util.format(TopMayDam(player))
                        );
                        break;
                }
            }

            return;
        }

        if (this.mapId == 129) {
            switch (player.iDMark.getIndexMenu()) {

                case ConstNpc.BASE_MENU: {

                    if (player.levelWoodChest == 0) {

                        switch (select) {

                            case 0: // Hướng dẫn
                                NpcService.gI().createTutorial(
                                        player,
                                        tempId,
                                        this.avartar,
                                        ConstNpc.NPC_DHVT23
                                );
                                break;

                            case 1: // Thi đấu 1 thỏi vàng
                                if (player.levelWoodChest != 12) {

                                    if (InventoryService.gI().finditemWoodChest(player)) {

                                        if (truThoiVang(player, SO_THOI_VANG_MOI_LAN)) {
                                            The23rdMartialArtCongressService.gI().startChallenge(player);
                                        } else {
                                            Service.gI().sendThongBao(
                                                    player,
                                                    "Bạn cần "
                                                            + SO_THOI_VANG_MOI_LAN
                                                            + " Thỏi vàng để thi đấu"
                                            );
                                        }

                                    } else {
                                        Service.gI().sendThongBao(
                                                player,
                                                "Hãy mở rương báu vật trước"
                                        );
                                    }

                                } else {
                                    Service.gI().sendThongBao(
                                            player,
                                            "Bạn đã vô địch giải. Vui lòng chờ đến ngày mai"
                                    );
                                }

                                break;

                            case 2: // Về Đại Hội Võ Thuật
                                ChangeMapService.gI().changeMapNonSpaceship(
                                        player,
                                        52,
                                        player.location.x,
                                        336
                                );
                                break;
                        }

                    } else {

                        switch (select) {

                            case 0: // Hướng dẫn
                                NpcService.gI().createTutorial(
                                        player,
                                        tempId,
                                        this.avartar,
                                        ConstNpc.NPC_DHVT23
                                );
                                break;

                            case 1:
                                if (player.levelWoodChest != 12) {

                                    if (InventoryService.gI().finditemWoodChest(player)) {

                                        if (truThoiVang(player, SO_THOI_VANG_MOI_LAN)) {
                                            The23rdMartialArtCongressService.gI().startChallenge(player);
                                        } else {
                                            Service.gI().sendThongBao(
                                                    player,
                                                    "Bạn cần "
                                                            + SO_THOI_VANG_MOI_LAN
                                                            + " Thỏi vàng để thi đấu"
                                            );
                                        }

                                    } else {
                                        Service.gI().sendThongBao(
                                                player,
                                                "Hãy mở rương báu vật trước"
                                        );
                                    }

                                } else {
                                    Service.gI().sendThongBao(
                                            player,
                                            "Bạn đã vô địch giải. Vui lòng chờ đến ngày mai"
                                    );
                                }

                                break;

                            case 2: // Nhận thưởng
                                this.createOtherMenu(
                                        player,
                                        1,
                                        "Phần thưởng của bạn đang ở cấp "
                                                + player.levelWoodChest
                                                + " / 12\n"
                                                + "Mỗi ngày chỉ được nhận thưởng 1 lần\n"
                                                + "Bạn có chắc sẽ nhận phần thưởng ngay bây giờ?",
                                        "OK",
                                        "Từ chối"
                                );
                                break;

                            case 3:
                                ChangeMapService.gI().changeMapNonSpaceship(
                                        player,
                                        52,
                                        player.location.x,
                                        336
                                );
                                break;
                        }
                    }

                    break;
                }

                case 1: {

                    if (select == 0) {

                        if (InventoryService.gI().finditemWoodChest(player)) {

                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {

                                Item it = ItemService.gI().createNewItem((short) 570);

                                it.addOptionParam(72, player.levelWoodChest);
                                it.addOptionParam(30, 0);
                                it.createTime = System.currentTimeMillis();

                                InventoryService.gI().addItemBag(player, it);
                                InventoryService.gI().sendItemBag(player);

                                player.levelWoodChest = 0;
                                player.lastTimeRewardWoodChest = System.currentTimeMillis();

                                NpcService.gI().createMenuConMeo(
                                        player,
                                        -1,
                                        -1,
                                        "Bạn nhận được\n"
                                                + "|1|Rương Gỗ\n"
                                                + "|2|Giấu bên trong nhiều vật phẩm quý giá",
                                        "OK"
                                );

                            } else {

                                this.npcChat(
                                        player,
                                        "Hành trang đã đầy, cần một ô trống trong hành trang để nhận vật phẩm"
                                );
                            }

                        } else {

                            Service.gI().sendThongBao(
                                    player,
                                    "Hãy mở rương báu vật trước"
                            );
                        }
                    }

                    break;
                }
            }
        }
    }
}
