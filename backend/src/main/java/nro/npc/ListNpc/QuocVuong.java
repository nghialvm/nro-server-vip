package nro.npc.ListNpc;


import nro.inventory.InventoryService;
import nro.services.OpenPowerService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import nro.npc.Npc;
import nro.player.Player;
import nro.power.PowerLimitManager;

public class QuocVuong extends Npc {

    // ===== Giá mở nhanh bằng vàng =====
    private static final long GOLD_SPEED_OPEN_LIMIT_POWER = 500_000_000L;

    public QuocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (player.Detu != null) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                    "Bản thân", "Đệ tử", "Từ chối");
        } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                    "Bản thân", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                if (player.Detu != null) {
                    switch (select) {
                        case 0:
                            if (PowerLimitManager.getInstance().hasNext(player.nPoint.limitPower)) {
                                this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                        "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh \n"
                                        + "của bản thân lên " + Service.gI().getPowerLimitDisplay(player) + ".\n"
                                        + "Điều kiện: đạt " + Util.formatNumber(player.nPoint.getPowerLimit(), FormatStyle.VIETNAMESE) + " sức mạnh.\n"
                                        + "Lưu ý: tỷ lệ tiền năng giảm dần theo từng mốc giới hạn sức mạnh",
                                        "Nâng\ngiới hạn\nsức mạnh",
                                        "Nâng ngay\n" + Util.formatNumber(GOLD_SPEED_OPEN_LIMIT_POWER, FormatStyle.VIETNAMESE) + " vàng",
                                        "OK");
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Sức mạnh của con đã đạt tới giới hạn",
                                        "Đóng");
                            }
                            break;

                        case 1:
                            if (player.Detu != null) {
                                if (PowerLimitManager.getInstance().hasNext(player.Detu.nPoint.limitPower)) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                            "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh \n"
                                            + "của đệ tử lên " + Service.gI().getPowerLimitDisplay(player.Detu) + ".\n"
                                            + "Điều kiện: đạt " + Util.formatNumber(player.Detu.nPoint.getPowerLimit(), FormatStyle.VIETNAMESE) + " sức mạnh.\n"
                                            + "Lưu ý: tỷ lệ tiền năng giảm dần theo từng mốc giới hạn sức mạnh",
                                            "Nâng ngay\ncho đệ tử\n" + Util.formatNumber(GOLD_SPEED_OPEN_LIMIT_POWER, FormatStyle.VIETNAMESE) + " vàng",
                                            "OK");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của đệ con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Không thể thực hiện");
                            }
                            break;
                    }
                } else {
                    switch (select) {
                        case 0:
                            if (PowerLimitManager.getInstance().hasNext(player.nPoint.limitPower)) {
                                this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                        "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh \n"
                                        + "của bản thân lên " + Service.gI().getPowerLimitDisplay(player) + ".\n"
                                        + "Điều kiện: đạt " + Util.formatNumber(player.nPoint.getPowerLimit(), FormatStyle.VIETNAMESE) + " sức mạnh.\n"
                                        + "Lưu ý: tỷ lệ tiền năng giảm dần theo từng mốc giới hạn sức mạnh",
                                        "Nâng\ngiới hạn\nsức mạnh",
                                        "Nâng ngay\n" + Util.formatNumber(GOLD_SPEED_OPEN_LIMIT_POWER, FormatStyle.VIETNAMESE) + " vàng",
                                        "OK");
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Sức mạnh của con đã đạt tới giới hạn",
                                        "Đóng");
                            }
                            break;
                    }
                }

            } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {

                // ===== BỎ điều kiện cần 1 trang bị thần =====
                // if (player.nPoint.limitPower == 4 && !InventoryService.gI().findItemThanLinh(player)) { ... }

                switch (select) {
                    case 0:
                        OpenPowerService.gI().openPowerBasic(player);
                        break;

                    case 1:
                        // ===== ĐỔI mở nhanh bằng 500.000.000 vàng =====
                        if (player.inventory.gold >= GOLD_SPEED_OPEN_LIMIT_POWER) {
                            if (OpenPowerService.gI().openPowerSpeed(player)) {
                                player.inventory.gold -= GOLD_SPEED_OPEN_LIMIT_POWER;
                                Service.gI().sendMoney(player);
                            }
                        } else {
                            Service.gI().sendThongBao(player,
                                    "Bạn không đủ vàng để mở, còn thiếu "
                                    + Util.formatNumber((GOLD_SPEED_OPEN_LIMIT_POWER - player.inventory.gold), FormatStyle.VIETNAMESE) + " vàng");
                        }
                        break;
                }

            } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {

                if (player.Detu.nPoint.limitPower == 5) {
                    Service.gI().sendThongBao(player, "Hãy tìm đến Tổ Sư Kaio để ông ý có thể giúp con nhé!");
                    return;
                }

                if (select == 0) {
                    // ===== ĐỔI mở nhanh cho đệ tử bằng 500.000.000 vàng =====
                    if (player.inventory.gold >= GOLD_SPEED_OPEN_LIMIT_POWER) {
                        if (OpenPowerService.gI().openPowerSpeed(player.Detu)) {
                            player.inventory.gold -= GOLD_SPEED_OPEN_LIMIT_POWER;
                            Service.gI().sendMoney(player);
                        }
                    } else {
                        Service.gI().sendThongBao(player,
                                "Bạn không đủ vàng để mở, còn thiếu "
                                + Util.formatNumber((GOLD_SPEED_OPEN_LIMIT_POWER - player.inventory.gold), FormatStyle.VIETNAMESE) + " vàng");
                    }
                }
            }
        }
    }
}
