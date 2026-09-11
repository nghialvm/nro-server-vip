package nro.services;

import consts.ConstNpc;
import nro.player.Player;
import nro.server.Client;
import Utils.FormatStyle;
import Utils.Util;
import network.io.Message;
import java.util.ArrayList;

public class SubMenuService {

    public static final int BAN = 500;
    public static final int BUFF_PET = 501;
    public static final int OTT = 502;
    public static final int CUU_SAT = 503;
    public static final int MENU = 504;
    public static final int BUY_BACK = 505;
    public static final int IS_LOVE = 506;

    private static SubMenuService i;

    private SubMenuService() {
    }

    public static SubMenuService gI() {
        if (i == null) {
            i = new SubMenuService();
        }
        return i;
    }

    public void controller(Player player, int playerTarget, int menuId) {
        Player plTarget = Client.gI().getPlayerByID(playerTarget);

        switch (menuId) {
            case MENU:
                if (plTarget != null) {
                    String[] selects = new String[]{"Chơi Oẳn Tù Tì", "Cừu sát", "Bắn Bluetooth", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.SUB_MENU, -1,
                            "|0|Thông Tin Đối Phương\n"
                            + "|1|Tên Nhân Vật : " + plTarget.name + "\n"
                            + "|0|Sức Mạnh : " + Util.formatNumber(plTarget.nPoint.power, FormatStyle.VIETNAMESE) + "\n"
                            + "|0|Hành Tinh : " + (plTarget.gender == 0 ? "Trái Đất" : plTarget.gender == 1 ? "Namếc" : "Xayda") + "\n",
                            selects, plTarget);
                }
                break;

            case BAN:
                if (plTarget != null) {
                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                            "Bạn có chắc chắn muốn ban " + plTarget.name, selects, plTarget);
                }
                break;

            case BUFF_PET:
                if (plTarget != null) {
                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.BUFF_PET, -1,
                            "Bạn có chắc chắn muốn phát đệ tử cho " + plTarget.name, selects, plTarget);
                }
                break;

            case OTT:
                if (plTarget != null) {
                    if (plTarget.isBoss) {
                        String[] selects = new String[]{"Kéo", "Búa", "Bao", "Hủy"};
                        NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1,
                                "|1|Bạn Có Chắc Muốn Chơi Oẳn Tù Tì Với [" + plTarget.name + "]\n"
                                + "|2|Mức Cược Là 10Tr Vàng?", selects);
                        return;
                    }

                    if (plTarget.inventory.gold < 10000000) {
                        Service.gI().sendThongBao(player, plTarget.name + " không có đủ 10tr vàng");
                    } else if (player.inventory.gold < 10000000) {
                        Service.gI().sendThongBao(player, "Bạn không có đủ 10tr vàng");
                    } else {
                        String[] selects = new String[]{"Kéo", "Búa", "Bao", "Hủy"};
                        NpcService.gI().createMenuConMeo(player, ConstNpc.OTT, -1,
                                "|1|Bạn Có Chắc Muốn Chơi Oẳn Tù Tì Với [" + plTarget.name + "]\n"
                                + "|2|Mức Cược Là 10Tr Vàng?", selects, plTarget);
                    }
                }
                break;

            case CUU_SAT:
                if (Util.isAfterMidnight(player.lastTimeCuuSat)) {
                    switch (player.getSession().Vip_Point) {
                        case 0:
                            player.timesPerDayCuuSat = 1;
                            break;
                        case 1:
                            player.timesPerDayCuuSat = 15;
                            break;
                        case 2:
                            player.timesPerDayCuuSat = 30;
                            break;
                    }
                    player.lastTimeCuuSat = System.currentTimeMillis();
                }

                if (!player.isFounder() && !(player.getSession().Vip_Point > 0 && player.timesPerDayCuuSat > 0)) {
                    Service.gI().hideWaitDialog(player);
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }

                if (plTarget != null) {
                    if (player.pvp != null || plTarget.pvp != null) {
                        Service.gI().hideWaitDialog(player);
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                        return;
                    }
                    player.timesPerDayCuuSat--;
                }
                break;

            case BUY_BACK:
                // ItemShop item;
                // if ((item = BuyBack.gI().getItemBuyBack(plTarget)) != null) {
                //     NpcService.gI().createMenuConMeo(player, ConstNpc.BUY_BACK, -1,
                //             "Bạn có muốn mua " + item.temp.name + " từ " + plTarget.name + " không?\n"
                //             + "Giá cửa hàng " + item.cost + " ĐSK\n"
                //             + "Giá tại đây " + (int) (item.cost * 0.95) + " ĐSK\n"
                //             + "Bạn cũng có thể nhận được " + (int) (item.cost * 0.2) + " ĐSK\n"
                //             + "khi người khác mua từ bạn (Cải trang của bạn sẽ không mất)",
                //             "Đồng ý", "Từ chối");
                // }
                break;

            case IS_LOVE:
                if (plTarget != null) {
                    String[] selects = new String[]{"Chơi Oẳn Tù Tì", "Cừu sát", "Bắn Bluetooth", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.SUB_MENU, -1,
                            "|0|Thông Tin Đối Phương\n"
                            + "|1|Tên Nhân Vật : " + plTarget.name + "\n"
                            + "|0|Sức Mạnh : " + Util.formatNumber(plTarget.nPoint.power, FormatStyle.VIETNAMESE) + "\n"
                            + "|0|Hành Tinh : " + (plTarget.gender == 0 ? "Trái Đất" : plTarget.gender == 1 ? "Namếc" : "Xayda") + "\n",
                            selects, plTarget);
                }
                break;
        }

        Service.gI().hideWaitDialog(player);
    }

    public void showMenu(Player player) {
        ArrayList<SubMenu> subMenusList = new ArrayList<>();
        subMenusList.add(new SubMenu(MENU, "Chức năng khác", "Oẳn tù tì, cừu sát, mua skin,..."));

        showSubMenu(player, subMenusList.toArray(SubMenu[]::new));
    }

    public void showMenuValentine(Player player) {
        ArrayList<SubMenu> subMenusList = new ArrayList<>();
        subMenusList.add(new SubMenu(IS_LOVE, "Tỏ Tình", ""));

        showSubMenu(player, subMenusList.toArray(SubMenu[]::new));
    }

    public void showMenuForAdmin(Player player) {
        showSubMenu(player, new SubMenu(BAN, "Ban người chơi dddd", ""));
    }

    public void showSubMenu(Player player, SubMenu... subMenus) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 63);
            msg.writer().writeByte(subMenus.length);

            for (SubMenu subMenu : subMenus) {
                msg.writer().writeUTF(subMenu.caption1);
                msg.writer().writeUTF(subMenu.caption2);
                msg.writer().writeShort((short) subMenu.id);
            }

            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Utils.Logger.logException(SubMenuService.class, e);
        }
    }

    public static class SubMenu {

        private int id;
        private String caption1;
        private String caption2;

        public SubMenu(int id, String caption1, String caption2) {
            this.id = id;
            this.caption1 = caption1;
            this.caption2 = caption2;
        }
    }
}