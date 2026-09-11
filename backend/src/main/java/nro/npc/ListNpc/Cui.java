package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.Manager.BossManager;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstTask;
import event.EventManager;
import java.util.ArrayList;
import models.Item.Item;
import models.Item.ItemService;
import models.Reward.RewardService;
import nro.map.Zone;
import nro.npc.Npc;
import nro.player.Player;

public class Cui extends Npc {

    private final int COST_FIND_BOSS = 50_000_000;

    public Cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (!canOpenNpc(pl)) {
            return;
        }

        //==================== SỰ KIỆN HALLOWEEN ====================
        if (EventManager.HALLOWEEN) {
            if (pl.NhanKeoHayBiGheoNpc_9 == 0) {
                createOtherMenu(pl, ConstNpc.NHAN_KEO_HALLOWEEN,
                        "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                        "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                return;
            }
        }
        //==================== SỰ KIỆN TẾT ====================
        else if (EventManager.LUNNAR_NEW_YEAR) {
            if (pl.NhanLiXiForNPC_24 == 0) {
                String[] chucTetMessages = {
                    "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                    "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                    "Phát tài phát lộc",
                    "Vạn sự như ý",
                    "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                    "Năm mới phát tài phát lộc, vạn sự như ý nha",
                    "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                    "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                    "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                    "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                    "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                    "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                    "Tết đến cười thật nhiều, sống thật chill, vui hết mình",
                    "Chúc mừng năm mới"
                };
                String message = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                createOtherMenu(pl, ConstNpc.NHAN_LI_XI, message,
                        "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                return;
            }
        }

        //==================== NHIỆM VỤ ====================
        if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
            switch (this.mapId) {
                case 19: {
                    int taskId = TaskService.gI().getIdTask(pl);
                    switch (taskId) {
                        case ConstTask.TASK_21_0:
                            this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                    "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                    "Đến chỗ\nKuku\n(" + Util.formatNumber(COST_FIND_BOSS, FormatStyle.VIETNAMESE) + " vàng)",
                                    "Đến Cold", "Đến\nNappa", "Từ chối");
                            break;
                        case ConstTask.TASK_21_1:
                            this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                    "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                    "Đến chỗ\nMập đầu đinh\n(" + Util.formatNumber(COST_FIND_BOSS, FormatStyle.VIETNAMESE) + " vàng)",
                                    "Đến Cold", "Đến\nNappa", "Từ chối");
                            break;
                        case ConstTask.TASK_21_2:
                            this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                    "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                    "Đến chỗ\nRambo\n(" + Util.formatNumber(COST_FIND_BOSS, FormatStyle.VIETNAMESE) + " vàng)",
                                    "Đến Cold", "Đến\nNappa", "Từ chối");
                            break;
                        default:
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó\n"
                                    + "Hoàn thành nhiệm vụ tiêu diệt để mở Khu vực Thám Hiểm (x2 TN cho đệ tử)\n"
                                    + "Cần 1 bình nước để vào.",
                                    "Đến Cold", "Đến\nNappa", "Đến Khu vực\nThám Hiểm", "Từ chối");
                            break;
                    }
                    break;
                }
                case 68:
                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                            "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                    break;
                default: {
                    ArrayList<String> menu = new ArrayList<>();
                    if (!pl.canReward_MeoDen) {
                        menu.add("Đến\nTrái Đất");
                        menu.add("Đến\nNamếc");
                        menu.add("Đến\nSiêu thị");
                    } else {
                        if (EventManager.LUNNAR_NEW_YEAR) {
                            menu.add("Trả Mèo");
                        }
                    }
                    String[] menus = menu.toArray(String[]::new);
                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                            (!pl.canReward_MeoDen
                                    ? "Tàu vũ trụ Xayda có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được."
                                    : "Ta bị bọn Pilap bắt Mèo rồi huhuhu, Ngươi tìm lại giúp ta đi..."),
                            menus);
                    break;
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (!canOpenNpc(pl)) {
            return;
        }

        //==================== TẾT ====================
        if (EventManager.LUNNAR_NEW_YEAR) {
            if (pl.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI) {
                if (select == 1) {
                    Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                    Lixi.addOptionParam(30, 0);
                    Lixi.addOptionParam(93, 30);
                    InventoryService.gI().addItemBag(pl, Lixi);
                    InventoryService.gI().sendItemBag(pl);
                    pl.NhanLiXiForNPC_24++;
                    Service.gI().sendThongBao(pl, "Bạn nhận được " + Lixi.template.name);
                }
                return;
            }
        }

        //==================== HALLOWEEN ====================
        else if (EventManager.HALLOWEEN) {
            if (pl.iDMark.getIndexMenu() == ConstNpc.NHAN_KEO_HALLOWEEN) {
                switch (select) {
                    case 0: {
                        Item KeoBanTay = ItemService.gI().createNewItem((short) 901, Util.nextInt(1, 3));
                        KeoBanTay.addOptionParam(86, 0);
                        KeoBanTay.addOptionParam(93, 35);
                        InventoryService.gI().addItemBag(pl, KeoBanTay);
                        InventoryService.gI().sendItemBag(pl);
                        Service.gI().chat(pl, "Haha xin được " + KeoBanTay.quantity + " kẹo bàn tay rồi");
                        pl.NhanKeoHayBiGheoNpc_9++;
                        break;
                    }
                    case 1:
                        pl.NhanKeoHayBiGheoNpc_9++;
                        break;
                }
                return;
            }
        }

        //==================== MÈO ĐEN ====================
        if (pl.canReward_MeoDen) {
            RewardService.gI().rewardMeoDen(pl);
            return;
        }

        //==================== LOGIC MAP ====================
        switch (this.mapId) {
            case 26:
                if (pl.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(pl, 24, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(pl, 25, -1, -1);
                            break;
                        case 2:
                            if (pl.nPoint.power < 20_000_000L) {
                                Service.getInstance().sendThongBao(pl, "Yêu cầu sức mạnh lớn hơn 20tr");
                                return;
                            }
                            ChangeMapService.gI().changeMapBySpaceShip(pl, 84, -1, -1);
                            break;
                    }
                }
                break;

            case 19:
                int idTask = pl.playerTask.taskMain.id;
                int subTask = TaskService.gI().getIdTask(pl);
                if (pl.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0: // Cold
                            if (pl.nPoint.power >= 41_000_000_000L && idTask > 26) {
                                ChangeMapService.gI().changeMapBySpaceShip(pl, 109, -1, 295);
                            } else {
                                Service.gI().sendThongBaoOK(pl, "Hoàn thành nhiệm vụ 26 và đạt trên 41 tỷ sức mạnh");
                            }
                            break;
                        case 1: // Nappa
                            if (idTask >= 17 || (subTask >= ConstTask.TASK_21_0 && subTask <= ConstTask.TASK_21_2)) {
                                ChangeMapService.gI().changeMapBySpaceShip(pl, 68, -1, -1);
                            } else {
                                Service.gI().sendThongBaoOK(pl, "Hãy hoàn thành nhiệm vụ 16 đi đã!");
                            }
                            break;
                        case 2: // Thám hiểm
                            if (idTask <= 23) {
                                Service.gI().sendThongBaoOK(pl, "Cần hoàn thành nhiệm vụ tiêu diệt Fide để đến Khu vực Thám Hiểm!");
                                return;
                            }
                            Item binhNuoc = InventoryService.gI().findItemBag(pl, 456);
                            if (binhNuoc == null || binhNuoc.quantity <= 0) {
                                Service.gI().sendThongBaoOK(pl, "Bạn cần có ít nhất 1 Bình nước (ID 456)!");
                                return;
                            }
                            InventoryService.gI().subQuantityItemsBag(pl, binhNuoc, 1);
                            InventoryService.gI().sendItemBag(pl);
                            Service.gI().sendThongBao(pl, "Đã tiêu hao 1 Bình nước để vào Khu vực Thám Hiểm!");
                            ChangeMapService.gI().changeMapBySpaceShip(pl, 179, -1, -1);
                            break;
                    }
                } else {
                    // MENU NHIỆM VỤ KUKU - MAP DẦU ĐINH - RAMBO
                    if (pl.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                        switch (select) {
                            case 0: openBossLocation(pl, BossID.KUKU, "Kuku"); break;
                            case 1:
                                if (pl.nPoint.power >= 41_000_000_000L && idTask > 26)
                                    ChangeMapService.gI().changeMapBySpaceShip(pl, 109, -1, 295);
                                else
                                    Service.gI().sendThongBaoOK(pl, "Hoàn thành nhiệm vụ 26 và đạt trên 41 tỷ sức mạnh!");
                                break;
                            case 2:
                                if (idTask >= 17 || (subTask >= ConstTask.TASK_21_0 && subTask <= ConstTask.TASK_21_2))
                                    ChangeMapService.gI().changeMapBySpaceShip(pl, 68, -1, -1);
                                else
                                    Service.gI().sendThongBaoOK(pl, "Hãy hoàn thành nhiệm vụ 16 đi đã!");
                                break;
                        }
                    } else if (pl.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                        switch (select) {
                            case 0: openBossLocation(pl, BossID.MAP_DAU_DINH, "Mập đầu đinh"); break;
                            case 1:
                                if (pl.nPoint.power >= 41_000_000_000L && idTask > 26)
                                    ChangeMapService.gI().changeMapBySpaceShip(pl, 109, -1, 295);
                                else
                                    Service.gI().sendThongBaoOK(pl, "Hoàn thành nhiệm vụ 26 và đạt trên 41 tỷ sức mạnh!");
                                break;
                            case 2:
                                if (idTask >= 17 || (subTask >= ConstTask.TASK_21_0 && subTask <= ConstTask.TASK_21_2))
                                    ChangeMapService.gI().changeMapBySpaceShip(pl, 68, -1, -1);
                                else
                                    Service.gI().sendThongBaoOK(pl, "Hãy hoàn thành nhiệm vụ 16 đi đã!");
                                break;
                        }
                    } else if (pl.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                        switch (select) {
                            case 0: openBossLocation(pl, BossID.RAMBO, "Rambo"); break;
                            case 1:
                                if (pl.nPoint.power >= 41_000_000_000L && idTask > 26)
                                    ChangeMapService.gI().changeMapBySpaceShip(pl, 109, -1, 295);
                                else
                                    Service.gI().sendThongBaoOK(pl, "Hoàn thành nhiệm vụ 26 và đạt trên 41 tỷ sức mạnh!");
                                break;
                            case 2:
                                if (idTask >= 17 || (subTask >= ConstTask.TASK_21_0 && subTask <= ConstTask.TASK_21_2))
                                    ChangeMapService.gI().changeMapBySpaceShip(pl, 68, -1, -1);
                                else
                                    Service.gI().sendThongBaoOK(pl, "Hãy hoàn thành nhiệm vụ 16 đi đã!");
                                break;
                        }
                    }
                }
                break;

            case 68:
                if (pl.iDMark.isBaseMenu() && select == 0) {
                    ChangeMapService.gI().changeMapBySpaceShip(pl, 19, -1, 1100);
                }
                break;
        }
    }

    private void openBossLocation(Player pl, int bossId, String bossName) {
        Boss boss = BossManager.gI().getBossById(bossId);
        if (boss == null || boss.zone == null) {
            Service.gI().sendThongBao(pl, bossName + " chưa xuất hiện");
            return;
        }
        if (boss.isDie()) {
            Service.gI().sendThongBao(pl, "Boss " + bossName + " đã chết!");
            return;
        }
        if (pl.inventory.gold < COST_FIND_BOSS) {
            Service.gI().sendThongBao(pl, "Không đủ vàng, còn thiếu " + Util.formatNumber(COST_FIND_BOSS - pl.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
            return;
        }
        Zone z = MapService.gI().getMapCanJoin(pl, boss.zone.map.mapId, boss.zone.zoneId);
        if (z == null || z.getNumOfPlayers() >= z.maxPlayer) {
            Service.gI().sendThongBao(pl, "Khu vực đã đầy!");
            return;
        }
        pl.inventory.gold -= COST_FIND_BOSS;
        Service.gI().sendMoney(pl);
        ChangeMapService.gI().changeMap(pl, boss.zone, boss.location.x, boss.location.y);
        Service.gI().sendThongBao(pl, "Đã đến chỗ boss " + bossName + "!");
    }
}
