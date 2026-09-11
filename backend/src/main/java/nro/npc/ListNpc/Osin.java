package nro.npc.ListNpc;



import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import java.util.ArrayList;
import models.Item.ItemTimeService;
import nro.map.MajinBuu12H.MajinBuu12H;
import nro.map.MajinBuu14H.MajinBuu14HService;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;

public class Osin extends Npc {

    public Osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskTalkNpc(player, this);
            switch (this.mapId) {
                case 50:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                    break;
                case 154:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi\nCon có thể up đá chân mệnh ở 2 map Ngục Tù or Hành tinh chết\nQuy đổi ở NPC map Hành tinh chết  ?",
                            "Đến\nhành tinh\nngục tù", "Đến\nhành tinh\nchết", "Cửa Hàng", "Từ chối");
                    break;
                case 155:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Quay về", "Từ chối");
                    break;
                case 180:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Mau hút năng lượng tà ác trong người cậu ấy (Dùng tự động luyện tập)",
                            "Về nhà", "Từ chối");
                    break;
                case 206:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Quay về", "Từ chối");
                    break;
                case 52:
                    player.fightMabu.clear();
                    if (this.mapId == 52) {
                        if (TimeUtil.isMabu14HOpen()) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Mabư đã thoát khỏi vỏ bọc\nmau đi cùng ta ngăn chặn hắn lại\ntrước khi hắn tàn phá trái đất này",
                                    "OK", "Bình hút\nnăng lượng", "Từ chối");
                        } else if (TimeUtil.isMabuOpen()) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Bây giờ tôi sẽ bí mật...\nđuổi theo 2 tên đồ tể...\nQuý vị nào muốn đi theo thì xin mời !",
                                    "OK", "Bình hút\nnăng lượng", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                    "Vào lúc " + MajinBuu12H.HOUR_OPEN_MAP_MABU + "h tôi sẽ bí mật...\nđuổi theo 2 tên đồ tể...\nQuý vị nào muốn đi theo thì xin mời !", 
                                    "OK", "Bình hút\nnăng lượng", "Từ chối");
                        }
                    }
                    break;
                case 114:
                case 115:
                case 117:
                case 118:
                case 119:
                case 120: {
                    if (player.cFlag != 9) {
                        NpcService.gI().createTutorial(player, tempId, avartar, "Ngươi hãy về phe của mình mà thể hiện");
                        return;
                    }
                    String npcSay = "Đừng vội xem thường Babiđây, ngay đến cha hắn là thần ma đạo sĩ Bibiđây khi còn sống cũng phải sợ hắn đấy!";
                    ArrayList<String> menuAL = new ArrayList<>();
                    menuAL.add("Hướng\ndẫn\nthêm");
                    if (!player.itemTime.isUseGTPT) {
                        menuAL.add("Giải trừ\nphép thuật\n1 ngọc");
                    }
                    if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && this.mapId != 120) {
                        menuAL.add("Xuống\nTầng dưới");
                    }
                    String[] menus = menuAL.toArray(String[]::new);
                    this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, npcSay, menus);
                    break;
                }
                case 127: {
                    String npcSay = player.isPhuHoMapMabu ? "Ta có thể giúp gì cho ngươi ?" : "Ta sẽ phù hộ ngươi bằng\nnguồn sức mạnh của Thần Kaiô\n+1 triệu HP, +1 triệu KI, +10k Sức đánh\nLưu ý: sức mạnh này sẽ biến mất khi ngươi rời khỏi đây";
                    ArrayList<String> menuAL = new ArrayList<>();
                    if (!player.isPhuHoMapMabu) {
                        menuAL.add("Phù hộ\n10 hồng ngọc");
                    }
                    menuAL.add("Từ chối");
                    menuAL.add("Về\nĐại Hội\nVõ Thuật");
                    String[] menus = menuAL.toArray(String[]::new);
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, npcSay, menus);
                    break;
                }
                default:
                    super.openBaseMenu(player);
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 50: {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                break;
                        }
                    }
                    break;
                }
                case 154: {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                break;
                            case 1:
                                //Service.gI().sendThongBao(player, "Chức năng đang bảo trì!");
                                ChangeMapService.gI().changeMap(player, 206, -1, 111, 648);
                                break;
                            case 2:
                                ShopService.gI().opendShop(player, "SHOP_MAPS_154", false);
                                break;
                        }
                    }
                    break;
                }
                case 155: {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMap(player, 154, -1, 780, 312);
                            break;
                        }
                    }
                    break;
                }
                case 180: {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                        }
                    }
                    break;
                }
                case 206: {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMap(player, 154, -1, 780, 312);
                            break;
                        }
                    }
                    break;
                }
                case 52: {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_OPEN_MMB: {
                            if (select == 0) {
                                if (TimeUtil.isMabu14HOpen()) {
                                    MajinBuu14HService.gI().joinMaBu2H(player);
                                } else if (TimeUtil.isMabuOpen()) {
                                    ChangeMapService.gI().changeMap(player, 114, -1, Util.nextInt(100, 500), 336);
                                }
                            }
                            if (select == 1) {
                                this.createOtherMenu(player, 123412,
                                        "Cađíc đã bị phù thuỷ Babidi thôi miên\n"
                                        + "Hãy mang Bình Hút Năng Lượng đến\n"
                                        + "Hút cạn năng lượng tà ác trong cậu ấy", 
                                        "OK", "Từ chối");
                            }
                            break;
                        }
                        case ConstNpc.MENU_NOT_OPEN_MMB: {
                            if (select == 1) {
                                this.createOtherMenu(player, 123412,
                                        "Cađíc đã bị phù thuỷ Babidi thôi miên\n"
                                        + "Hãy mang Bình Hút Năng Lượng đến\n"
                                        + "Hút cạn năng lượng tà ác trong cậu ấy", 
                                        "OK", "Từ chối");
                            }
                            break;
                        }
                        case 123412: {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 180, -1, Util.nextInt(100, 200), 240);
                            }
                            break;
                        }
                    }
                    break;
                }
                case 114:
                case 115:
                case 117:
                case 118:
                case 119:
                case 120: {
                    if (player.cFlag != 9) {
                        return;
                    }
                    switch (select) {
                        case 0:
                            NpcService.gI().createTutorial(player, tempId, 4388, ConstNpc.HUONG_DAN_MAP_MA_BU);
                            break;
                        case 1: {
                            if (!player.itemTime.isUseGTPT) {
                                player.itemTime.lastTimeUseGTPT = System.currentTimeMillis();
                                player.itemTime.isUseGTPT = true;
                                ItemTimeService.gI().sendAllItemTime(player);
                                Service.gI().sendThongBao(player, "Phép thuật đã được giải trừ, sức đánh của bạn đã tăng theo điểm tích lũy");
                            } else if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && this.mapId != 120) {
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            }
                            break;
                        }
                        case 2: {
                            if (!player.itemTime.isUseGTPT && player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && this.mapId != 120) {
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            }
                            break;
                        }
                    }
                    break;
                }
                case 127: {
                    switch (select) {
                        case 0: {
                            if (!player.isPhuHoMapMabu) {
                                if (player.inventory.ruby < 10) {
                                    Service.gI().sendThongBao(player, "Bạn không có đủ hồng ngọc");
                                } else {
                                    player.inventory.subRuby(10);
                                    player.isPhuHoMapMabu = true;
                                    player.nPoint.calPoint();
                                    player.nPoint.setHp(Util.CrisGH(player.nPoint.hpMax));
                                    player.nPoint.setMp(Util.CrisGH(player.nPoint.mpMax));
                                    Service.gI().point(player);
                                    Service.gI().Send_Info_NV(player);
                                    Service.gI().Send_Caitrang(player);
                                }
                            }
                            break;
                        }
                        case 1: {
                            if (player.isPhuHoMapMabu) {
                                ChangeMapService.gI().changeMap(player, 52, -1, Util.nextInt(100, 300), 336);
                            }
                            break;
                        }
                        case 2: {
                            if (!player.isPhuHoMapMabu) {
                                ChangeMapService.gI().changeMap(player, 52, -1, Util.nextInt(100, 300), 336);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
}
