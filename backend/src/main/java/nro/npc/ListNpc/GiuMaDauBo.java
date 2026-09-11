package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nro.clan.ClanMember;
import nro.clan.ClanService;
import nro.npc.Npc;
import nro.player.Player;
import nro.server.Client;
import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;

public class GiuMaDauBo extends Npc {

    public GiuMaDauBo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    
    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;
        List<String> menuList = new ArrayList<>();
        if (player.DiemDanhBangHoi > 0) {
            menuList.add("Khiêu chiến\nBoss");
            menuList.add("OK");               
            menuList.add("Đóng");             
        } else {
            menuList.add("Khiêu chiến\nBoss");              
            menuList.add("Điểm danh\n+1 Capsule\nBang");    
            menuList.add("OK");                            
            menuList.add("Đóng");                        
        }
        if (EventManager.TRUNG_THU) {
            menuList.add("Cùng Nhau\nRước Đèn");
        }

        String[] menus = menuList.toArray(String[]::new);

        String extra = "";
        if (player.clan != null) {
            long remaining = player.clan.LasttimeBuffExp + player.clan.TimeStarBuffExp - System.currentTimeMillis();
            if (remaining > 0) {
                extra = "Bang hội của ngươi được tặng x" + player.clan.CongTiemNangSucManhToanBangHoi + " exp\n"
                      + "Thời gian còn lại: " + Util.convertMilliseconds(remaining);
            }
        }

        this.createOtherMenu(player, ConstNpc.BASE_MENU,
            "Ngươi đang muốn tìm mảnh vỡ và mảnh hồn bông tai Porata trong truyền thuyết, ta sẽ đưa ngươi đến đó ?\n"
            + extra,
            menus);
    }
    
    private String showInfo(int type) {
        switch (type) {
            case 1:
                return "Miễn phí";
            case 2:
                return "100\nhồng ngọc";
            case 3:
                return "300\nhồng ngọc";
            case 4:
                return "500\nhồng ngọc";
            case 5:
                return "2000\nhồng ngọc";
            default:
                return "Max";
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;
        List<String> menuList = new ArrayList<>();
        if (player.DiemDanhBangHoi > 0) {
            menuList.add("Khiêu chiến\nBoss");
            menuList.add("OK");
            menuList.add("Đóng");
        } else {
            menuList.add("Khiêu chiến\nBoss");
            menuList.add("Điểm danh\n+1 Capsule\nBang");
            menuList.add("OK");
            menuList.add("Đóng");
        }
        if (EventManager.TRUNG_THU) menuList.add("Cùng Nhau\nRước Đèn");

        if (select < 0 || select >= menuList.size()) return;
        String chosen = menuList.get(select);

        switch (chosen) {
            case "Khiêu chiến\nBoss": {
                if (player.clan == null) {
                    ChangeMapService.gI().changeMap(player, 5, -1, Util.nextInt(1050, 1150), 408);
                    player.clan = null;
                    player.clanMember = null;
                    ClanService.gI().sendMyClan(player);
                    return;
                }
                if (player.clan.BossOfTheGang != null) {
                    NpcService.gI().createMenuConMeo(player, ConstNpc.GOTO_CLAN_BOSS, this.avartar, "Thời gian khiêu chiến Boss là 30 phút.\n"
                            + "Top 1 đánh boss +25 Capsule Bang\n"
                            + "Top 3 đánh boss +20 Capsule Bang\n"
                            + "Top 5 đánh boss +15 Capsule Bang\n"
                            + "Top 10 đánh boss +10 Capsule Bang\n"
                            + "Top 11 trở lên +5 Capsule Bang\n"
                            + "Người đánh boss cuối cùng thưởng thêm 10 Capsule Bang\n"
                            + "Mở cửa vào ngày thứ 7 và Chủ Nhật hàng tuần.",
                            "Vào", "Đóng");
                } else {
                    NpcService.gI().createMenuConMeo(player, ConstNpc.CREATE_CLAN_BOSS, this.avartar, "Thời gian khiêu chiến Boss là 30 phút.\n"
                            + "Top 1 đánh boss +25 Capsule Bang\n"
                            + "Top 3 đánh boss +20 Capsule Bang\n"
                            + "Top 5 đánh boss +15 Capsule Bang\n"
                            + "Top 10 đánh boss +10 Capsule Bang\n"
                            + "Top 11 trở lên +5 Capsule Bang\n"
                            + "Người đánh boss cuối cùng thưởng thêm 10 Capsule Bang\n"
                            + "Mở cửa vào ngày thứ 7 và Chủ Nhật hàng tuần.",
                            showInfo(player.clan.boss_clan_round), "Đóng");
                }
                break;
            }
            case "Điểm danh\n+1 Capsule\nBang": {
                if (player.DiemDanhBangHoi > 0) {
                    return;
                }
                if (player.clan == null) {
                    ChangeMapService.gI().changeMap(player, 5, -1, Util.nextInt(1050, 1150), 408);
                    player.clan = null;
                    player.clanMember = null;
                    ClanService.gI().sendMyClan(player);
                    return;
                }
                Service.gI().sendThongBao(player, "Bạn vừa nhận được 1 viên capsule bang.");
                player.DiemDanhBangHoi++;
                player.clanMember.clanPoint++;
                player.clanMember.memberPoint++;
                player.clan.capsuleClan++;
                for (ClanMember cm : player.clan.getMembers()) {
                    Player pls = Client.gI().getPlayerByID(cm.id);
                    if (pls != null) {
                        ClanService.gI().sendMyClan(pls);
                        ClanService.gI().updateClanMembersToDB(pls.clan);
                    }
                }
                player.clan.saveClanPoint();
                break;
            }
            case "OK": {
                if (player.nPoint.power < 20_000_000_000L) {
                    Service.gI().sendThongBao(player, "Sức mạnh phải trên 20 tỷ thì mới có thể qua được.");
                    return;
                }
                player.type = 5;
                player.maxTime = 5;
                Service.gI().Transport(player);
                break;
            }
            case "Đóng": {
                break;
            }
            case "Cùng Nhau\nRước Đèn": {
                if (!EventManager.TRUNG_THU) return;
                if (player.clan == null) {
                    Service.gI().sendThongBao(player, "Bạn không có bang hội");
                    return;
                }
                Calendar last = Calendar.getInstance();
                last.setTimeInMillis(player.clan.LasttimeBuffExp);
                Calendar now = Calendar.getInstance();
                boolean sameDay = (last.get(Calendar.YEAR) == now.get(Calendar.YEAR))
                               && (last.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR));
                if (sameDay) {
                    Service.gI().sendThongBao(player, "Bang hội của ngươi đã cùng nhau rước đèn rồi, hãy chờ đến ngày mai nhé!");
                    return;
                }
                if (!player.clan.isLeader(player)) {
                    Service.gI().sendThongBao(player, "Bạn không phải là bang chủ");
                    return;
                }
                if (player.clan.hasOfflineMember(player)) {
                    Service.gI().sendThongBao(player, "Yêu cầu toàn bộ thành viên phải online!");
                    return;
                }
                if (!player.clan.areAllMembersInSameZone(player)) {
                    Service.gI().sendThongBaoToClan(player.clan, "Yêu cầu toàn bộ thành viên phải đứng cùng trong khu!");
                    return;
                }
                if (!InventoryService.gI().areAllMembersInSameZoneAndHaveLongDenItem(player)) {
                    Service.gI().sendThongBaoToClan(player.clan, "Yêu cầu toàn bộ thành viên phải đeo (trang bị) Lồng đèn bất kỳ!");
                    return;
                }
                Service.gI().sendThongBao(player, "Thưởng x2 kinh nghiệm toàn bang hội trong 1h");
                player.clan.LasttimeBuffExp = System.currentTimeMillis();
                player.clan.TimeStarBuffExp = 3600000;
                player.clan.CongTiemNangSucManhToanBangHoi = 2;
                player.clan.update();
                break;
            }
            default: {
                break;
            }
        }
    }
}
