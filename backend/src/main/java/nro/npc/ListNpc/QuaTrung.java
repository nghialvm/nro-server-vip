package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstPlayer;
import models.Item.Item;
import nro.npc.Npc;
import nro.player.Player;

public class QuaTrung extends Npc {

    private final int COST_AP_TRUNG_NHANH = 20; // 🔥 Cần 20 Thỏi vàng để ấp nhanh / nở

    public QuaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;

        if (this.mapId == (21 + player.gender)) {
            if (player.mabuEgg == null) {
                Service.gI().sendThongBao(player, "❌ Bạn chưa có trứng Mabư nào để ấp!");
                return;
            }

            player.mabuEgg.sendMabuEgg();

            if (player.mabuEgg.getSecondDone() != 0) {
                // 🥚 Đang ấp
                this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG,
                        " Trứng đang được ấp, còn " + player.mabuEgg.getSecondDone() + " giây nữa sẽ nở.\n"
                        + "Bạn có muốn ấp nhanh bằng " + COST_AP_TRUNG_NHANH + " Thỏi vàng không?",
                        "Hủy bỏ\ntrứng",
                        "Ấp nhanh\n" + COST_AP_TRUNG_NHANH + " Thỏi vàng",
                        "Đóng");
            } else {
                // 🐣 Đã sẵn sàng nở
                this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG,
                        " Trứng đã sẵn sàng nở!\nBạn muốn cho nở bằng " + COST_AP_TRUNG_NHANH + " Thỏi vàng không?",
                        "Nở (" + COST_AP_TRUNG_NHANH + " Thỏi vàng)",
                        "Hủy bỏ\ntrứng",
                        "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        if (this.mapId != (21 + player.gender)) return;
        if (player.mabuEgg == null) {
            Service.gI().sendThongBao(player, " Bạn không có trứng Mabư nào!");
            return;
        }

        switch (player.iDMark.getIndexMenu()) {
            // 🥚 Trứng đang ấp
            case ConstNpc.CAN_NOT_OPEN_EGG: {
                if (select == 0) {
                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                            "⚠️ Bạn có chắc chắn muốn hủy bỏ trứng Mabư?",
                            "Đồng ý", "Từ chối");
                } else if (select == 1) {
                    // Ấp nhanh bằng 20 Thỏi vàng
                    int count = InventoryService.gI().findItemBag(player, 457).quantity;
                    if (count >= COST_AP_TRUNG_NHANH) {
                       InventoryService.gI().subQuantityItemsBag(
                                player,
                                InventoryService.gI().findItemBag(player, 457),
                                COST_AP_TRUNG_NHANH
                        );
                        InventoryService.gI().sendItemBag(player);
                        player.mabuEgg.timeDone = 0;
                        player.mabuEgg.sendMabuEgg();
                        Service.gI().sendThongBao(player, "✅ Trứng đã được ấp nhanh thành công bằng " + COST_AP_TRUNG_NHANH + " Thỏi vàng!");
                    } else {
                        Service.gI().sendThongBao(player, "❌ Bạn cần " + COST_AP_TRUNG_NHANH + " Thỏi vàng để ấp nhanh!");
                    }
                }
                break;
            }

            // 🐣 Trứng đã sẵn sàng nở
            case ConstNpc.CAN_OPEN_EGG: {
                if (select == 0) {
                    int count = InventoryService.gI().findItemBag(player, 457).quantity;
                    if (count >= COST_AP_TRUNG_NHANH) {
                        InventoryService.gI().subQuantityItemsBag(
                                player,
                                InventoryService.gI().findItemBag(player, 457),
                                COST_AP_TRUNG_NHANH
                        );
                        InventoryService.gI().sendItemBag(player);

                        this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                " Trứng sẽ nở ngay bây giờ!\nChọn hành tinh cho đệ Mabư:",
                                "Đệ Mabư\nTrái Đất", "Đệ Mabư\nNamếc", "Đệ Mabư\nXayda", "Từ chối");
                    } else {
                        Service.gI().sendThongBao(player, " Bạn không có đủ " + COST_AP_TRUNG_NHANH + " Thỏi vàng để nở trứng!");
                    }
                } else if (select == 1) {
                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                            "️ Bạn có chắc chắn muốn hủy trứng Mabư?",
                            "Đồng ý\n Trừ 10 HN", "Từ chối");
                }
                break;
            }

            // ✅ Xác nhận chọn loại đệ Mabư
            case ConstNpc.CONFIRM_OPEN_EGG: {
                switch (select) {
                    case 0 -> player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                    case 1 -> player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                    case 2 -> player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                }
                break;
            }

            // 🗑️ Xác nhận hủy trứng
            case ConstNpc.CONFIRM_DESTROY_EGG: {
                if (select == 0) {
                    player.mabuEgg.destroyEgg();
                    player.inventory.subGemAndRuby(10);
                    Service.gI().sendThongBao(player, " Bạn đã hủy bỏ trứng Mabư!");
                }
                break;
            }
        }
    }
}
