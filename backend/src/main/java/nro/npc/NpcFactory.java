package nro.npc;

import Boss.map.Nguhanhson.GiaiPhongAnNgoKhong;
import nro.services.Fun.Input;
import nro.services.Fun.ChangeMapService;
import nro.intrinsic.IntrinsicService;
import nro.services.PlayerService;
import nro.services.NpcService;
import models.Item.ItemService;
import nro.services.Service;
import nro.services.DetuService;
import nro.services.TaskService;
import nro.services.FriendAndEnemyService;
import nro.consignmentstore.ConsignShopService;
import consts.ConstNpc;
import consts.ConstPlayer;
import consts.ConstTask;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import models.Item.Item;
import QuanLiBoss.BossesData;
import QuanLiBoss.Manager.ElecManager;
import QuanLiBoss.Manager.GasManager;
import QuanLiBoss.Manager.MackiManager;
import QuanLiBoss.Manager.OilManager;
import nro.matches.PVPService;
import nro.player.Player;
import nro.shop.ShopService;
import nro.skill.Skill;
import nro.clan.ClanService;
import nro.inventory.InventoryService;
import QuanLiBoss.Manager.BossManager;
import nro.server.Client;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.server.ServerNotify;
import nro.services.Fun.ItemUseHandler;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import java.util.HashMap;
import nro.services.SubMenuService;
import Utils.FormatStyle;
import Utils.Functions;
import java.util.Timer;
import java.util.TimerTask;
import Utils.SkillUtil;
import consts.ConstDetu;
import event.EventManager;
import java.io.IOException;
import jbcd.ConnectDB;
import java.util.logging.Level;
import jbcd.data.GodGK;
import models.Item.ItemOption;
import models.Reward.RewardService;
import network.io.Message;
import nro.boss.event.TrungThuEvent.KhiDot;
import nro.clan.Clan;
import nro.combine.CombineService;
import nro.dragon.ChristMasEvent.ShenronChristMasEventService;
import nro.giftcode.GiftCodeManager;
import nro.dragon.HalloweenEvent.ShenronHalloweenEventService;
import nro.dragon.SummonDragon;
import static nro.dragon.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static nro.dragon.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static nro.dragon.SummonDragon.SHENRON_SAY;
import nro.dragon.SummonDragonNamek;
import nro.effect.EffectMapService;
import nro.inventory.Inventory;
import nro.map.BossOfTheGangs.BossOfTheGangsService;
import nro.map.DragonBallNamec.NgocRongNamec;
import nro.map.RedRibbonHQ.RedRibbonHQ;
import nro.map.RedRibbonHQ.RedRibbonHQService;
import nro.map.SuperDivineWater.SuperDivineWater;
import nro.minigame.ChonAiDay;
import nro.minigame.ConSoMayMan;
import nro.minigame.MiniGame;
import nro.npc.ListNpc.*;
import nro.pariry.PariryServices;


public class NpcFactory {

    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<>();

    public static Npc ChanLe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.rubyWin > 0) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|2|[ADMINISTRATOR] Đã Từng Nói:\n"
                                + "|5|'Cờ Bạc Không Ai Là Người Chiến Thắng\n"
                                + "|5|Kẻ Không Cờ Bạc Là Kẻ Chiến Thắng'\n"
                                + "|1|NRO Tuổi Thơ Là Nhà Cái Uy Tín Đến Từ Châu Á!\n"
                                + "|6|Bạn đã thắng được " + Util.formatNumber((player.rubyWin * 1.5), FormatStyle.VIETNAMESE) + " thỏi vàng\n"
                                + "|5|Chú ý : Chỉ cược 1 lần tối đa 10k thỏi vàng, mọi sự mất mát ad không giải quyết!",
                                "Chẵn", "Lẻ", "Xem \nlịch sử\nbản thân", "Xem lịch sử",
                                "Nhận\nphần thưởng");
                    } else {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|2|[ADMINISTRATOR] Đã Từng Nói:\n"
                                + "|5|'Cờ Bạc Không Ai Là Người Chiến Thắng\n"
                                + "|5|Kẻ Không Cờ Bạc Là Kẻ Chiến Thắng'\n"
                                + "|1|NRO Tuổi Thơ Là Nhà Cái Uy Tín Đến Từ Châu Á!\n"
                                + "|6|Chẵn lẻ đê.\n Bạn chưa thắng cược lần nào\n"
                                + "|5|Chú ý : Chỉ cược 1 lần tối đa 10k thỏi vàng, mọi sự mất mát ad không giải quyết!",
                                "Chẵn", "Lẻ", "Xem \nlịch sử\nbản thân", "Xem lịch sử");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 14) {

                        if (!player.getSession().actived) {
                            Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");

                        } else if (player.iDMark.isBaseMenu()) {
                            switch (select) {

                                case 0:
                                    if (!PariryServices.gI().checkHavePariry()) {
                                        Service.gI().sendThongBaoOK(player, "Chưa có phiên chẵn lẻ nào khởi động");
                                        break;
                                    }
                                    Input.gI().CHAN(player);
                                    break;
                                case 1:
                                    if (!PariryServices.gI().checkHavePariry()) {
                                        Service.gI().sendThongBaoOK(player, "Chưa có phiên chẵn lẻ nào khởi động");
                                        break;
                                    }
                                    Input.gI().LE(player);
                                    break;
                                case 2:
                                    Service.gI().sendThongBaoFromAdmin(player,
                                            PariryServices.gI().getHistoryPlayer(player));
                                    break;
                                case 3:
                                    Service.gI().sendThongBaoFromAdmin(player,
                                            PariryServices.gI().getHistory());
                                    break;
                                case 4:
                                    if (player.rubyWin <= 0) {
                                        Service.gI().sendThongBaoOK(player, "Có cái nịt mà nhận");
                                        break;
                                    }
                                    PariryServices.gI().rewardRuby(player);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc miNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.MENU_JOIN_GIAI_CUU_MI_NUONG,
                           "Ta Đang Bị Kẻ Xấu Khống Chế\n"
                           + "Các Chàng Trai Hãy Mau Mau Lên Đường Giải Cứu Ta!",
                           "Tham Gia", "Hướng Dẫn\n Giải Cứu", "Hướng Dẫn Sơn Tinh Thuỷ Tinh", "Tìm Kiếm Sơn Thuỷ Tinh", "Từ Chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                int nPlSameClan = 0;
                for (Player pl : player.zone.getPlayers()) {
                    if (!pl.equals(player) && pl.clan != null && pl.clan.equals(player.clan) && pl.location.x >= 1120 && pl.location.x <= 1500) {
                        nPlSameClan++;
                    }
                }
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_GIAI_CUU_MI_NUONG:
                            if (select == 0) {
                                if (player.clan == null) {
                                    Service.gI().sendThongBao(player, "Yêu Cầu Có Bang Hội Mới Tham Gia Được");
                                    break;
                                }
                                if (player.clan.giaiCuuMiNuong != null) {
                                    ChangeMapService.gI().changeMapInYard(player, 185, player.clan.giaiCuuMiNuong.id, 60);
                                    break; 
                                } else if (player.clan.getMembers().size() < GiaiCuuMiNuong.N_PLAYER_CLAN) {
                                    Service.gI().sendThongBao(player, "Bang Hội Phải Có Đủ 3 Người Mới Được Tham Gia");
                                    break;
                                } else if (nPlSameClan < GiaiCuuMiNuong.N_PLAYER_MAP) {
                                    Service.gI().sendThongBao(player, "Hãy Đứng Cùng 2 Người Trong Bang Để Tham Gia");
                                    break;
                                } else if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                                    Service.gI().sendThongBao(player, "Yêu cầu tham gia bang hội trên 1 ngày");
                                    break;
                                } else if (player.clan.haveGoneGiaiCuuMiNuong) {
                                    Service.gI().sendThongBaoOK(player, "Bang Hội Của Anh Đã Tham Gia Hôm Nay Rồi\n"
                                           + "Hẹn Gặp Anh Vào Ngày Mai ♡");
                                    break;
                                } else {
                                    GiaiCuuMiNuongService.gI().openGiaiCuuMiNuong(player);
                                }
                            } else if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_GIAI_CUU_MI_NUONG);
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_GIAI_CUU_MI_NUONG2);
                            } else if (select == 3) {
                                Boss ST = BossManager.gI().getBossById(BossID.SonTinh);
                                Boss ST2 = BossManager.gI().getBossById(BossID.SonTinh2);
                                if (BossManager.gI().getBossById(BossID.SonTinh) != null || BossManager.gI().getBossById(BossID.ThuyTinh) != null) {
                                    this.npcChat(player, "Sơn Tinh Và Thuỷ Tinh Đang Ở Map: " + ST.zone.map.mapName);
                                } else if (BossManager.gI().getBossById(BossID.SonTinh2) != null || BossManager.gI().getBossById(BossID.ThuyTinh2) != null) {
                                    this.npcChat(player, "Sơn Tinh Và Thuỷ Tinh Đang Ở Map: " + ST2.zone.map.mapName);
                                } else if (BossManager.gI().getBossById(BossID.SonTinh) == null || BossManager.gI().getBossById(BossID.ThuyTinh) == null) {
                                    this.npcChat(player, "Boss Đã Chết");
                                } else if (BossManager.gI().getBossById(BossID.SonTinh2) == null || BossManager.gI().getBossById(BossID.ThuyTinh2) == null) {
                                    this.npcChat(player, "Boss Đã Chết");
                                }
                            }
                            break;
                    }
                }
            }
        };
    }   

    public static Npc monaito(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            public void Npcchat(Player player) {
                String[] chat = {
                    "Cứu ta cứu taa",
                    "Hành Tinh và Người Cereal bị bọn Xayda tàn sát hết rồi...",
                    "Mau đến ngăn cản chúng lại!"
                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 6000, 6000);
            }

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 7) {
                        this.createOtherMenu(player, 0,
                                "|2|Ngươi muốn đến hành tinh Cereal?\n"
                                + "|2|Vùng đất Cereal đầy dãy nguy hiểm, ngươi vẫn muốn đến?", "Đến Cereal", "Đóng");
                    }
                    if (this.mapId == 7) {
                        if (player.gender == ConstPlayer.XAYDA) {
                            this.createOtherMenu(player, 4,
                                    "|7|Ngươi Là Người Xayda?, Xin Ngươi!\n"
                                    + "|7|Đừng Có Đến Hành Tinh Của Ta Để Tàn Phá Nữa!",
                                    "Đến Cereal", "Đóng");
                        }
                    }
                    if (this.mapId == 194) {
                        this.createOtherMenu(player, 1,
                                "Ngươi Muốn Quay Về Sao?", "Về Làng Mori", "Từ chối");
                    }
                    if (this.mapId == 194) {
                        if (player.gender == ConstPlayer.XAYDA) {
                            this.createOtherMenu(player, 5,
                                    "|7|Tên Xayda Chó Nàyy, Ngươi Mau Cút Khỏi Đây!!!\n",
                                    "Về Làng Mori", "Từ chối");
                        }
                    }
                    if (this.mapId == 200) {
                        this.createOtherMenu(player, 2,
                                "|7|Hãy Nghe Ta Kể Chuyện Lịch Sử Về Hành Tinh Này!", "Nghe Kể", "Đéo");
                    }
                    if (this.mapId == 200) {
                        if (player.gender == ConstPlayer.XAYDA) {
                            this.createOtherMenu(player, 6,
                                    "|7|Hãy Nghe Ta Kể Chuyện Lịch Sử Về Hành Tinh Này!", "Nghe Kể", "Đéo");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                Npcchat(player);

                if (canOpenNpc(player)) {
                    if (this.mapId == 7) {
                        if (player.iDMark.getIndexMenu() == 0) {
                            switch (select) {
                                case 0:
                                    if (player.nPoint.power >= 30000000000L) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 194, 0, 192);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn Không Đủ 30 Tỷ Sức Mạnh");
                                    }
                                    break;
                            }
                        }
                    }
                    if (canOpenNpc(player)) {
                        if (this.mapId == 200) {
                            if (player.gender == ConstPlayer.XAYDA) {
                                if (player.iDMark.getIndexMenu() == 6) {
                                    switch (select) {
                                        case 0:
                                            Service.gI().sendThongBao(player, "|7|Nghe Con Mẹ Mày, Chính Mày Và Người Xayda Của Mày Đã Tàn Phá Dân Tộc Tao");
                                            Service.gI().sendThongBao(player, "|7|Mày Mau Cút Khỏi Đây Mau!");
                                            if (player.demthoigian <= System.currentTimeMillis() + 5000);
                                             {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, Util.nextInt(14, 20), 0, Util.nextInt(200, 1000));
                                                Service.gI().sendThongBao(player, "|2|Bạn Đã Bị Người Dân Cereal Đuổi Về Hành Tinh Xayda!");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                        if (canOpenNpc(player)) {
                            if (this.mapId == 7) {
                                if (player.gender == ConstPlayer.XAYDA) {
                                    if (player.iDMark.getIndexMenu() == 4) {
                                        switch (select) {
                                            case 0:
                                                if (player.nPoint.power >= 30000000000L) {
                                                    ChangeMapService.gI().changeMapBySpaceShip(player, 194, 0, 192);
                                                } else {
                                                    Service.gI().sendThongBao(player, "Bạn Không Đủ 30 Tỷ Sức Mạnh");
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                            if (this.mapId == 194) {
                                if (player.iDMark.getIndexMenu() == 1) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 7, 0, 432);
                                            break;

                                    }
                                }
                            }
                            if (canOpenNpc(player)) {
                                if (this.mapId == 194) {
                                    if (player.gender == ConstPlayer.XAYDA) {
                                        if (player.iDMark.getIndexMenu() == 5) {
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMapBySpaceShip(player, 7, 0, 432);
                                                    break;
                                            }
                                        }
                                    }
                                }
                                if (this.mapId == 200) {
                                    if (player.iDMark.getIndexMenu() == 2) {
                                        switch (select) {
                                            case 0:
                                                this.createOtherMenu(player, 3,
                                                        "Vào 500 Năm Về Trước Hành Tinh Này Vô Cùng Yên Ổn\n"
                                                        + "Đến Một Ngày Bỗng Nhiên Từ Đâu Xuất Hiện Bọn Fide, "
                                                        + "Chúng Nó Phá Nát Thành Phố Yên Bình, Đồ Sát Dân Tộc Cereal. "
                                                        + "Chúng Đã Phá Nát 1 Thành Phố. "
                                                        + "Để Không Xảy Ra Chuyện Như Trên, "
                                                        + "Ta Đã Hợp Lực Với Toàn Bộ Những Người Còn Sống Trên Hành Tinh Cereal "
                                                        + "Dùng Hết Tinh Huyết Và Tuổi Thọ Của Mình Để\n"
                                                        + "|2|Tạo 1 Lời Nguyền Lên Tộc Xayda\n"
                                                        + "|2|Chỉ Cần Có Người Xayda Đến Thì Sức Mạnh Sẽ Bị Giảm Đi 50%\n"
                                                        + "|6|Nhưng Từ Khi Tạo Lời Nguyền Đó Bọn Ta Đã Yếu Dần, "
                                                        + "Bọn Xayda Còn Sót Trên Cereal Vẫn Âm Thầm Quấy Phá, "
                                                        + "Ngươi Hãy Giúp Ta Tiêu Diệt Bọn Chúng Bên Thành Phố Đổ Nát Nhé! "
                                                        + "Ta Sẽ Đưa Ngươi Viên Thuốc Độc Quyền Của Dân Tộc Cereal. "
                                                        + "Sau Khi Ngươi Uống Viên Thuốc Đó Vào Thì Bọn Xayda Kia Sẽ Không Phát Hiện Ra Ngươi, "
                                                        + "Nhưng Cẩn Thận Nếu Phát Ra Tiếng Động Là Thuốc Sẽ Mất Tác Dụng!\n",
                                                        "Nhận Thuốc");
                                                break;
                                            case 1:
                                                this.npcChat(player, "|2|Ngươi Sẽ Phải Hối Hận Với Quyết Định Của Mình!");
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == 3) {
                                        switch (select) {
                                            case 0: {
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                                } else if (player.NhanThuocTrongNgay == 1) {
                                                    this.npcChat(player, "Hôm nay ngươi đã nhận Thuốc rồi!");
                                                } else {
                                                    Service.gI().sendThongBao(player, "Bạn Nhận Được 1 Viên Thuốc Tàng Hình");
                                                    Item thuoctanghinh = ItemService.gI().createNewItem((short) 1327);
                                                    InventoryService.gI().addItemBag(player, thuoctanghinh);
                                                    InventoryService.gI().sendItemBag(player);
                                                    player.NhanThuocTrongNgay = 1;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc mabumap(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            public void Npcchat(Player player) {
                String[] chat = {
                    "Mau Đến Đây, Ta Có Thông Tin Quan Trọng"
                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 6000, 6000);
            }

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 202) {
                        this.createOtherMenu(pl, 0,
                                "|8|Ngươi Đang Muốn Tìm 2 Tên Người Lạ Kia Sao?\n"
                                + "|8|Một Người Tên Là Berus\n"
                                + "|8|Một Người Tên Là Whis\n"
                                + "\n"
                                + "|0|Nếu Ngươi Đưa Ta 1 Thỏi Vàng Ta Sẽ Nói Ngươi Biết Hắn Ở Đâu!\n",
                                "Đồng Ý", "Từ Chối");
                    }
                    if (this.mapId == 203) {
                        this.createOtherMenu(pl, 1,
                                "|8|Ngươi Đang Muốn Tìm 2 Tên Người Lạ Kia Sao?\n"
                                + "|8|Một Người Tên Là Champa\n"
                                + "|8|Một Người Tên Là Vados\n"
                                + "\n"
                                + "|0|Nếu Ngươi Đưa Ta 1 Thỏi Vàng Ta Sẽ Nói Ngươi Biết Hắn Ở Đâu!\n",
                                "Đồng Ý", "Từ Chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    Npcchat(player);
                    if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0:
                                Item thoivang = null;
                                try {
                                    thoivang = InventoryService.gI().findItemBag(player, 457);
                                } catch (Exception e) {
                                }
                                if (thoivang == null || thoivang.quantity < 1) {
                                    this.npcChat(player, "Mày Đéo Có Thỏi Vàng");
                                } else {
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 1);
                                    InventoryService.gI().sendItemBag(player);
                                    Boss timkiem202 = BossManager.gI().getBossById(BossID.WHIS);
                                    if (timkiem202 != null) {
                                        this.npcChat(player, "|2|Hai Tên Đó Đang Ở Khu: " + timkiem202.zone.zoneId);
                                        this.npcChat(player, "|2|Hai Tên Đó Đang Ở Khu: " + timkiem202.zone.zoneId);
                                    } else {
                                        this.npcChat(player, "|2|Hai Tên Đó Đã Bị Ai Đó Giết");
                                    }
                                }
                                break;
                            case 1:
                                this.npcChat(player, "Không Có Tiền Thì Cút Ra Chỗ Khác");
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0:
                                Item thoivang = null;
                                try {
                                    thoivang = InventoryService.gI().findItemBag(player, 457);
                                } catch (Exception e) {
                                }
                                if (thoivang == null || thoivang.quantity < 1) {
                                    this.npcChat(player, "Mày Đéo Có Thỏi Vàng");
                                } else {
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 1);
                                    InventoryService.gI().sendItemBag(player);
                                    Boss timkiem202 = BossManager.gI().getBossById(BossID.VADOS);
                                    if (timkiem202 != null) {
                                        this.npcChat(player, "|2|Hai Tên Đó Đang Ở Khu: " + timkiem202.zone.zoneId);
                                        this.npcChat(player, "|2|Hai Tên Đó Đang Ở Khu: " + timkiem202.zone.zoneId);
                                    } else {
                                        this.npcChat(player, "|2|Hai Tên Đó Đã Bị Ai Đó Giết");
                                    }
                                }
                                break;
                            case 1:
                                this.npcChat(player, "Không Có Tiền Thì Cút Ra Chỗ Khác");
                                break;
                        }

                    }
                }
            }
        };
    }

    public static Npc ngokhong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            public void Npcchat(Player player) {
                String[] chat = {
                    "Cứu Taa...",
                    "chu mi naa"
                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 6000, 6000);
            }

            @Override
            public void openBaseMenu(Player player) {
                Npcchat(player);
                if (canOpenNpc(player)) {
                    if (this.mapId == 124) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Hãy giúp ta Giải Phong Ấn\n"
                                + "Ta đang dùng 1 tia thần hồn cuối cùng để cầu xin các ngươi\n"
                                + "|2|Hãy đến Vách Núi cứu ta",
                                "Vào\n Vách Núi", "Quay Về");
                    } else if (this.mapId == 191) {
                        this.createOtherMenu(player, 0, "|7|Hãy giúp ta Giải Phong Ấn\n"
                                + "\n|7|Số Lần Giải Phong Ấn : " + (player.giaiphongan == 1 ? "Đã Hết Cơ Hội Giải Phong Ấn!!!" : "Có Duy Nhất 1 Lần Giải Phong Ấn!"),
                                "Giải\nPhong Ấn\n Ngộ Không", "Ghép Hồn\n Hầu Vương", "Nâng Cấp\n Ngộ Không", "Quay Về");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 124) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 191, 0, 600, 408);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 0, 0, 450, 432);
                                    break;
                            }
                        }
                    } else if (this.mapId == 191) {
                    }
                    if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, 1, "|7|Giải Phong Ấn Ngộ Không\n"
                                        + "\n|2|Ngộ Không hắn có 72 phép thần thông biến hoá liên tục, hãy cân nhắc trước khi Giải Phong Ấn cho nó!!\n"
                                        + "\n|2|Giải Phong Ấn Cần 1 Bùa Giải Phong Ấn"
                                        + "\n"
                                        + "\n|7|Số Lần Giải Phong Ấn : " + (player.giaiphongan == 1 ? "Đã Hết Cơ Hội Giải Phong Ấn!!!" : "Có Duy Nhất 1 Lần Giải Phong Ấn!\n"),
                                        "Giải\n Phong Ấn", "Từ Chối");
                                break;
                            case 1:
                                this.createOtherMenu(player, 2, "|7|Ghép hồn Yêu Hầu\n"
                                        + "\nCần 1 Hồn Yêu Hầu và 1 Viên Đá Cổ Đại để ghép!"
                                        + "\nTỉ lệ thành công: 101%\n"
                                        + "\n"
                                        + "Sau khi ghép sẽ nhận đệ tử Ngộ Không",
                                        "Ghép Hồn", "Quay Lại");
                                break;
                            case 2:
                                this.createOtherMenu(player, 5, "|7|Nâng Cấp Ngộ Không\n"
                                        + "\nCần 1 Hồn Yêu Hầu để Tiến Hoá!"
                                        + "\nTỉ lệ thành công: 100%\n"
                                        + "\n"
                                        + "Tiến Hoá sẽ tăng từng cấp!",
                                        "Tiến Hoá", "Thông Tin");
                                break;
                            case 3:
                                ChangeMapService.gI().changeMap(player, 124, 0, 2840, 312);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, 3, "|7|Bạn có chắc muốn Giải Phong Ấn không?\n"
                                        + "\n|4|Lưu ý: Sau khi giải phong ấn sẽ xuất hiện Ngộ Không, bạn có thể thu phục Ngộ Không bằng cách tiêu diệt nó"
                                        + "\n|4|Lưu ý: Người chơi khác cũng có thể tiêu diệt và thu phục nó!\n"
                                        + "\n|7|Tiêu diệt Ngộ Không rồi mang Hồn Vương Hầu đến gặp Npc Ngộ Không, dùng Hồn Vương Hầu và 1 Viên Đá Cổ Đại để triệu hồi Đệ Tử Ngộ Không!!!"
                                        + "\n"
                                        + "\n|2|Muốn lắm chắc phần thắng yêu cầu HP của Sư Phụ và Đệ Tử > 5.000.000\n",
                                        "Đồng ý\nGiải\nPhong Ấn", "Không\n đồng ý");
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 3) {
                        switch (select) {
                            case 0:
                                Item BuaGiaiPhongAn = null;
                                try {
                                    BuaGiaiPhongAn = InventoryService.gI().findItemBag(player, 1561);
                                } catch (Exception e) {
                                }
                                if (mapId == 191) {
                                    Boss oldBossClone = BossManager.gI().getBossById(BossID.GiaiPhongAnNgoKhong);
                                    if (oldBossClone != null) {
                                        Service.gI().sendThongBao(player, "Ngộ Không đã được ai đó giải phong ấn, hãy chờ thêm...");

                                    } else if (player.giaiphongan == 1) {
                                        Service.gI().sendThongBao(player, "|7|Bạn đã hết lượt Giải Phong Ấn!!");

                                    } else if (BuaGiaiPhongAn == null || BuaGiaiPhongAn.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không có Bùa Giải Phong Ấn!!!");

                                    } else {
                                        try {
                                            Service.gI().sendThongBao(player, "Giải Phong Ấn thành công!");
                                            GiaiPhongAnNgoKhong dt = new GiaiPhongAnNgoKhong(BossID.GiaiPhongAnNgoKhong,
                                                    BossesData.GiaiPhongAnNgoKhong, player.zone, player.location.x - 20,
                                                    player.location.y);
                                            player.GiaiPhongAnNgoKhong = true;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Logger.logException(Manager.class, e, "Lỗi tạo giải phong ấn");
                                        }
                                        // trừ 
                                        InventoryService.gI().subQuantityItemsBag(player, BuaGiaiPhongAn, 1);
                                        InventoryService.gI().sendItemBag(player);
                                        player.giaiphongan = 1;
                                    }
                                }
                                break;
                            case 1:
                                Service.gI().sendThongBao(player, "Hãy cứu ta");
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            case 0:
                                if (mapId == 191) {
//                                CombineService.gI().openTabCombine(player, CombineService.GHEP_HON_YEU_HAU);
                                }
                                break;
                        }
//                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
//                            switch (player.combineNew.typeCombine) {
//                                case CombineService.GHEP_HON_YEU_HAU:
//                                    if (select == 0) {
//                                        CombineService.gI().startCombine(player);
//                                    }
//                                    break;
//                            case 1:
//                                //lỗi quay lại
//                                break;
//                                }
                    } else if (player.iDMark.getIndexMenu() == 5) {
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, 6, "|1|Tiến Hoá Ngộ Không\n"
                                        + "\n|2|Tiến Hoá Cấp 2:"
                                        + "\n|6|[Cần 10 Thỏi Vàng, 1 Hồn Yêu Hầu]\n"
                                        + "\n|2|Tiến Hoá Cấp 3[MAX]:"
                                        + "\n|6|[Cần 50 Thỏi Vàng, 2 Hồn Yêu Hầu]"
                                        + "\n"
                                        + "\n|4|Tiến hoá sẽ giữ nguyên SỨC MẠNH và ĐỒ và SKILL của đệ tử!",
                                        "Tiến Hoá\n Cấp 2", "Tiến Hoá\n Cấp 3", "Đóng");
                                break;
                            case 1:
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.THONG_TIN_DE_TU_NGO_KHONG);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 6) {
                        switch (select) {
                            case 0:

                                break;
                            case 1:

                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    Service.gI().hideWaitDialog(player);
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                if (this.mapId != player.zone.map.mapId) {
                    Service.gI().sendThongBao(player, "Calích đã rời khỏi map!");
                    Service.gI().hideWaitDialog(player);
                    return;
                }

                if (this.mapId == 102) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?",
                            "Kể\nChuyện", "Quay về\nQuá khứ");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào Chú, Cháu có thể giúp gì?",
                            "Kể\nChuyện", "Đi đến\nTương lai", "Du Hành\n Thời Gian", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else if (select == 2) {
                        this.createOtherMenu(player, 0,
                                "|1|Du Hành Thời Gian về 5000 năm trước\n"
                                + "|1|Du Hành Thời Gian cần 1 Vé Du Hành\n\n"
                                + (player.thoigianduhanh > 0 ? "|2|Thời gian Du Hành còn lại: " + Util.formatCountdown(player.thoigianduhanh, true, true, true) : "|2|Bạn không có Thời Gian Du Hành!"),
                                "Du Hành\n Thời Gian", "Đóng");
                    }
                } else if (player.iDMark.getIndexMenu() == 0) {
                    if (select == 0) {
                        if (player.thoigianduhanh > 0) {
                            ChangeMapService.gI().DuHanhThoiGian(player);
                            player.nPoint.power /= 100;
                            Service.gI().point(player);
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không có Thời Gian Du Hành!");
                        }
                    }
                }
            }
        };
    }

    public static Npc calichquakhu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                TaskService.gI().checkDoneTaskTalkNpc(player, this);
                if (this.mapId == 167) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, Cháu là Calich của 5000 năm trước\n"
                            + "Ở Quá Khứ không thể dùng Capsune để đi lại\n"
                            + "Cháu có thể giúp gì cho chú không?",
                            "Quay về\nHiện Tại");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 167) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ChangeMapService.gI().TroVeThoiGian(player);

                            Service.gI().point(player);
                        }
                    }
                }
            }
        };
    }

    public static Npc LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                // --- CHỈ CHẴN / LẺ ---
                long remain = Math.max(0, (nro.minigame.ChanLe.gI().lastTimeEnd - System.currentTimeMillis()) / 1000);
                String time = remain + " giây";

                StringBuilder msg = new StringBuilder();
                msg.append("|7|Con số may mắn – Chẵn/Lẻ\n")
                        .append("|2|Đặt cược bằng Thỏi Vàng, trả thưởng 1.9x\n\n");

                // Thông tin cá nhân
                if (player.goldChan == 0 && player.goldLe == 0) {
                    msg.append("|1|- Bạn chưa tham gia\n");
                } else {
                    if (player.goldChan > 0) {
                        msg.append("|4|Bạn đặt Chẵn: ")
                                .append(Util.format(player.goldChan)).append(" Thỏi Vàng\n");
                    }
                    if (player.goldLe > 0) {
                        msg.append("|4|Bạn đặt Lẻ: ")
                                .append(Util.format(player.goldLe)).append(" Thỏi Vàng\n");
                    }
                }

                // Tổng cược toàn server (đúng field trong ChanLe hiện tại)
                msg.append("|7|Tổng Chẵn: ")
                        .append(Util.format(nro.minigame.ChanLe.gI().goldChan)).append(" Thỏi Vàng\n")
                        .append("|7|Tổng Lẻ: ")
                        .append(Util.format(nro.minigame.ChanLe.gI().goldLe)).append(" Thỏi Vàng\n\n");

                // Lịch sử + kết quả
                msg.append("|2|Lịch sử gần đây:\n")
                        .append(nro.minigame.ChanLe.gI().getHistoryGame())
                        .append("\n|6|Số vừa quay: ").append(nro.minigame.ChanLe.gI().number).append("\n")
                        .append("|3|Kết quả mới có sau ").append(time);

                if (nro.minigame.ChanLe.gI().isAllowBetting()) {
                    createOtherMenu(player, ConstNpc.CHAN_LE,
                            msg.toString(),
                            "Cập nhật", "Bảng xếp hạng", "Mua Chẵn", "Mua Lẻ", "Đóng");
                } else {
                    createOtherMenu(player, ConstNpc.CHAN_LE,
                            msg.toString(),
                            "Cập nhật", "Bảng xếp hạng", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (!canOpenNpc(pl)) {
                    return;
                }

                if (pl.iDMark.getIndexMenu() == ConstNpc.CHAN_LE) {
                    boolean allowBet = nro.minigame.ChanLe.gI().isAllowBetting();

                    switch (select) {
                        case 0: // Cập nhật
                            openBaseMenu(pl);
                            break;

                        case 1: // BXH (chưa có)
                            Service.getInstance().sendThongBao(pl, "Chưa cập nhật");
                            // Service.getInstance().showTopPower(pl, Service.getInstance().TOP_CL);
                            break;

                        case 2: // Mua Chẵn
                            if (allowBet) {
                                nro.services.Fun.Input.gI().CHAN(pl);
                            } else {
                                Service.gI().sendThongBao(pl, "Đang chờ kết quả! Vui lòng đợi ván mới.");
                            }
                            break;

                        case 3: // Mua Lẻ
                            if (allowBet) {
                                nro.services.Fun.Input.gI().LE(pl);
                            } else {
                                Service.gI().sendThongBao(pl, "Đang chờ kết quả! Vui lòng đợi ván mới.");
                            }
                            break;

                        case 4: // Đóng
                            Service.gI().sendThongBao(pl, "Hẹn gặp lại!");
                            break;
                    }
                }
            }
        };
    }

    public static Npc DocNhan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (mapId == 57) {
                    if (!player.clan.doanhTrai.winDT) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar, "Bọn mi đừng hòng thoát khỏi nơi đây");
                    } else {
                        NpcService.gI().createTutorial(player, tempId, this.avartar, "Ta chịu thua, nhưng các ngươi đừng có mong lấy được ngọc của ra\nta đã giấu ngọc 4 sao và 1 đống ngọc 7 sao trong doanh trại này...\nCác ngươi chỉ có 5 phút đi tìm, đố các ngươi tìm ra hahaha");
                        if (!player.clan.doanhTrai.isTimePicking) {
                            Service.gI().sendThongBao(player, "Trại Độc Nhãn đã bị tiêu diệt, bạn có 5 phút để tìm kiếm viên ngọc 4 sao trước khi phi thuyền đến đón");
                            player.clan.doanhTrai.isTimePicking = true;
                            player.clan.doanhTrai.lastTimePick = System.currentTimeMillis();
                            player.clan.doanhTrai.randomNR();
                            player.clan.doanhTrai.sendTextTimePickDoanhTrai();
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {

            }
        };
    }

    public static Npc LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai");
                        return;
                    }
                    if (player.clan.getMembers().size() < RedRibbonHQ.N_PLAYER_CLAN) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar,
                                "Bang hội phải có ít nhất 3 thành viên mới có thể tham gia");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar,
                                "Gia nhập bang hội trên 1 ngày mới được tham gia");
                        return;
                    }
                    if (player.clan.doanhTrai != null) {
                        createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                                "Bang hội của ngươi đang đánh trại độc nhãn\nThời gian còn lại là "
                                + TimeUtil.getTimeLeft(player.clan.doanhTrai.getLastTimeOpen(), RedRibbonHQ.TIME_DOANH_TRAI / 1000)
                                + ". Ngươi có muốn tham gia không?",
                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < RedRibbonHQ.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi phải có ít nhất " + RedRibbonHQ.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể vào\n"
                                + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết. "
                                + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clan.haveGoneDoanhTrai && !Util.isAfterMidnight(player.clan.lastTimeOpenDoanhTrai)) {
                        if (!Util.isAfterMidnight(player.lastTimeJoinDT)) {
                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                    "Hôm nay bạn đã tham gia doanh trại rồi, hẹn gặp bạn vào ngày mai");
                            return;
                        }
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội của ngươi ngày hôm nay đã vào 1 lần rồi (thành viên " + player.clan.playerOpenDoanhTrai.name + ") lúc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm") + "\n"
                                + "Nên ngươi không thể vào được nữa.\n"
                                + "Hãy chờ đến ngày mai để có thể vào miễn phí", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\nkhông?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng.",
                            "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI: {
                            if (select == 0) {
                                RedRibbonHQService.gI().joinDoanhTrai(player);
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        }
                        case ConstNpc.IGNORE_MENU: {
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopService.gI().opendShop(player, "bummatuonglai", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc GokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 80:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta mới hạ Fide, nhưng nó đã kịp đào 1 cái lỗ\nHành tinh này sắp nổ tung rồi\nMau lượn thôi",
                                    "Chuẩn");
                            break;
                        case 131:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đây là đâu? Xong ta rồi...", "Bó tay", "Về chỗ cũ");
                            break;
                        default:
                            super.openBaseMenu(player);
                            break;
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU: {
                            if (this.mapId == 131) {
                                if (select == 1) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                }
                            } else if (this.mapId == 80) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 870);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        };
    }

    ///////////////////////////////////////////NPC Chopper///////////////////////////////////////////
    public static Npc chopper(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Êi êi cậu có muốn cùng Chopper đi đến Đảo Kho Báu không,\nnhóm Hải Tặc Mũ Rơm đang chờ đợi cậu đến đó\n Có rất nhiều phần quà mùa hấp dẫn ở đó.\n Đi thôi nào....",
                                "Đi đến\nĐảo Kho Báu", "Chi tiết", "Từ chối");
                    }
                    if (this.mapId == 170) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn quay về Đảo kame à,\nChopper tôi sẽ đưa cậu đi",
                                "Đi thôi", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 170, -1, 1560);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 170) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    ///////////////////////////////////////////NPC Nami///////////////////////////////////////////
    public static Npc nami(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|1|Oh hoan nghên bạn đến với của hàng của tôi\n bạn có muốn đổi vỏ ốc, cua đỏ\nlấy các món đồ mùa hè không?.",
                                "Cửa hàng\nNami");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ShopService.gI().opendShop(player, "EVENT_MUA_HE", true);
                                break;
                        }
                    }
                }
            }
        };
    }

    ///////////////////////////////////////////NPC Franky///////////////////////////////////////////
    public static Npc franky(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 170) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn đi ra khơi khám phá?\n Nghe nói Luffy và mọi người đang tìm tên\ngấu tướng cướp ở ngoài đó.",
                                "Ra khơi\nthôi nào", "Từ chối");
                    }
                    if (this.mapId == 0) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn quay về Đảo kame à,\nđể Franky tôi đưa cậu đi",
                                "Đi thôi", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 170) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapInYard(player, 171, -1, 48);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc GokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy cố gắng luyện tập\nThu thập 9.999 bí kiếp để đổi trang phục Yardrat nhé!",
                            "Nhận\nthưởng", "OK");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (select == 0) {
                        int soluong = InventoryService.gI().getParam(player, 31, 590);
                        if (soluong >= 9999) {
                            InventoryService.gI().subParamItemsBag(player, 590, 31, 9999);
                            Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                            yardart.itemOptions.add(new ItemOption(47, 400));
                            yardart.itemOptions.add(new ItemOption(97, 10));
                            yardart.itemOptions.add(new ItemOption(14, 15));
                            yardart.itemOptions.add(new ItemOption(147, 30));
                            yardart.itemOptions.add(new ItemOption(108, 10));
                            InventoryService.gI().addItemBag(player, yardart);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Bạn nhận được võ phục của người Yardrat");
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không đủ 9.999 bí kiếp!");
                        }
                    }
                }
            }
        };
    }

    public static Npc granola(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 197) {
                        this.createOtherMenu(player, 0,
                                "|2|Ngươi Cần Gì Ở Ta?",
                                "Cửa Hàng", "Kích Hoạt\nOatmeal", "Kích Hoạt\nCải Trang", "Hướng Dẫn", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 197) {
                        switch (player.iDMark.getIndexMenu()) {
                            case 0:
                                switch (select) {
                                    case 0:
                                        ShopService.gI().opendShop(player, "shopcereal", true);
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, 1,
                                                "|2|Hãy Mang Đá Sức Mạnh Đến Đây\n"
                                                + "|0|Ta Sẽ Giúp Ngươi Kích Hoạt Oatmeal",
                                                "Kích Hoạt\nOatmeal", "Nâng Cấp\nOatmeal", "Đóng");
                                        break;
                                    case 2:
                                        this.createOtherMenu(player, 2,
                                                "|2|Ta Sẽ Giúp Ngươi Mở Chỉ Số Cải Trang\n"
                                                + "|7|Hãy Đến Gặp Elec Ở Thành Phố Suger\n"
                                                + "|7|Hắn Sẽ Giúp Ngươi!\n",
                                                "Mở Chỉ Số", "Từ Chối");
                                        break;
                                    case 3:
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_CEREAL1);
                                        break;
                                }
                                break;
                            case 2:
                                switch (select) {
                                    case 0:
                                        Service.gI().sendThongBao(player, "|0|Gặp Npc ELEC Ở Thành Phố Suger Để Mở");
                                        break;
                                }
                                break;
                            case 1:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, 3,
                                                "|8|Kích Hoạt Oatmeal Cần:\n"
                                                + "|0|5 Viên Đá Sức Mạnh Red\n"
                                                + "|0|5 Viên Đá Sức Mạnh Blue\n"
                                                + "|0|5 Viên Đá Sức Mạnh Purple\n"
                                                + "|0|3 Viên Đá Sức Mạnh Yellow\n",
                                                "Kích Hoạt", "Đóng");
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, 4,
                                                "|8|Nâng Cấp Oatmeal\n"
                                                + "|0|Có 3 Loại Oatmeal Dành Cho Từng Dân Tộc\n"
                                                + "|0|Oatmeal Red Chủ Yếu Dành Cho Người Chơi HP\n"
                                                + "|0|Oatmeal Blue Chủ Yếu Dành Cho Người Chơi KI\n"
                                                + "|0|Oatmeal Green Chủ Yếu Dành Cho Người Chơi SĐ\n",
                                                "Nâng Cấp\n Oatmeal \n Red", "Nâng Cấp\n Oatmeal \n Blue", "Nâng Cấp\n Oatmeal \n Green", "Đóng");
                                        break;
                                }
                                break;
                            case 3:
                                switch (select) {
                                    case 0:
                                        if (mapId == 197) {
//                                            CombineService.gI().openTabCombine(player, CombineService.KICH_HOAT_OATMEAL);
                                        }
                                        break;
                                }
                                break;
                            case 4:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, 5,
                                                "|7|Nâng Cấp Oatmeal Red Cần:\n"
                                                + "|0|1 Oatmeal Đã Mở Chỉ Số Và 1 Viên Ngọc Rồng Torombo 2 Sao\n"
                                                + "|0|Tỉ Lệ Thành Công 999999%\n",
                                                "Nâng Cấp", "Đóng");

                                        break;
                                    case 1:
                                        this.createOtherMenu(player, 6,
                                                "|2|Nâng Cấp Oatmeal Blue Cần:\n"
                                                + "|0|1 Oatmeal Đã Mở Chỉ Số Và 1 Viên Ngọc Rồng Torombo 3 Sao\n"
                                                + "|0|Tỉ Lệ Thành Công 999999%\n",
                                                "Nâng Cấp", "Đóng");

                                        break;
                                    case 2:
                                        this.createOtherMenu(player, 7,
                                                "|1|Nâng Cấp Oatmeal Green Cần:\n"
                                                + "|0|1 Oatmeal Đã Mở Chỉ Số Và 1 Viên Ngọc Rồng Torombo 1 Sao\n"
                                                + "|0|Tỉ Lệ Thành Công 999999%\n",
                                                "Nâng Cấp", "Đóng");

                                        break;
                                }
                                break;
                            case 5:
                                switch (select) {
                                    case 0:
                                        if (mapId == 197) {
//                                            CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_OATMEALXD);
                                        }
                                        break;
                                }
                                break;
                            case 6:
                                switch (select) {
                                    case 0:
                                        if (mapId == 197) {
//                                            CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_OATMEALNM);
                                        }
                                        break;
                                }
                                break;
                            case 7:
                                switch (select) {
                                    case 0:
                                        if (mapId == 197) {
//                                            CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_OATMEALTD);
                                        }
                                        break;
                                }
                                break;
//                            case ConstNpc.MENU_START_COMBINE:
//                                switch (player.combineNew.typeCombine) {
//                                    case CombineService.KICH_HOAT_OATMEAL:
//                                    case CombineService.NANG_CAP_OATMEALTD:
//                                    case CombineService.NANG_CAP_OATMEALXD:
//                                    case CombineService.NANG_CAP_OATMEALNM:
//                                        if (select == 0) {
//                                            CombineService.gI().startCombine(player);
//                                        }
//                                        break;
//                                }   break;
                            default:
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc xatang(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 123) {
                        this.createOtherMenu(player, 0,
                                "|2|Bạn Có Muốn Đánh Nhau Với Ngưu Ma Vương Không?\n"
                                + "|2|Nó Mới Bị Người Yêu Cắm Sừng Nên Có Vẻ Cay Cú",
                                "Cửa Hàng", "Đổi Pet", "Hướng Dẫn", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 123) {
                        if (player.iDMark.getIndexMenu() == 0) {
                            switch (select) {
                                case 0:
                                    ShopService.gI().opendShop(player, "shopxatang", true);
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 1,
                                            "|2|Hãy Mang 1 Chiếc Sừng Phải Và 1 Chiếc Sừng Trái Đến Đây\n"
                                            + "|2|Ta Sẽ Giúp Ngươi Đổi Pet Ngưu Ma Vương!",
                                            "Đổi Pet", "Từ Chối");
                                    break;
                                case 2:
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_NGUUMAVUONG);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 2,
                                            "|2|Lựa Chọn 1: Cần 1 Sừng Trái Và 1 Sừng Phải\n"
                                            + "|6|Sẽ Đổi Được Pet Ngưu Ma Vương SD,HP,KI Ngẫu Nhiên 5-12%\n\n"
                                            + "|2|Lựa Chọn 2: Cần 1 Sừng Trái Và 1 Sừng Phải Và 30 Thỏi Vàng\n"
                                            + "|6|Sẽ Đổi Được Pet Ngưu Ma Vương SD,HP,KI Ngẫu Nhiên 8-15%\n",
                                            "Lựa Chọn 1", "Lựa Chọn 2", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 2) {
                            switch (select) {
                                case 0: {
                                    Item sungtrai = null;
                                    Item sungphai = null;
                                    try {
                                        sungtrai = InventoryService.gI().findItemBag(player, 1573);
                                        sungphai = InventoryService.gI().findItemBag(player, 1572);
                                    } catch (Exception e) {
                                    }
                                    if (sungtrai == null || sungtrai.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không có Sừng Trái");
                                    } else if (sungphai == null || sungphai.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không có Sừng Phải");
                                    } else if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryService.gI().subQuantityItemsBag(player, sungtrai, 1);
                                        InventoryService.gI().subQuantityItemsBag(player, sungtrai, 1);
                                        Item petnguumavuong = ItemService.gI().createNewItem((short) 1202);
                                        petnguumavuong.itemOptions.add(new ItemOption(77, Util.nextInt(5, 12)));
                                        petnguumavuong.itemOptions.add(new ItemOption(50, Util.nextInt(5, 12)));
                                        petnguumavuong.itemOptions.add(new ItemOption(103, Util.nextInt(5, 12)));
                                        InventoryService.gI().addItemBag(player, petnguumavuong);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "|2|Bạn nhận được Pet Ngưu Ma Vương!");
                                    }
                                    break;
                                }
                                case 1: {
                                    Item sungtrai = null;
                                    Item sungphai = null;
                                    Item thoivang = null;
                                    try {
                                        sungtrai = InventoryService.gI().findItemBag(player, 1573);
                                        sungphai = InventoryService.gI().findItemBag(player, 1572);
                                        thoivang = InventoryService.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
                                    }
                                    if (sungtrai == null || sungtrai.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không có Sừng Trái");
                                    } else if (sungphai == null || sungphai.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không có Sừng Phải");
                                    } else if (thoivang == null || thoivang.quantity < 30) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 30 Thỏi Vàng");
                                    } else if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryService.gI().subQuantityItemsBag(player, sungtrai, 1);
                                        InventoryService.gI().subQuantityItemsBag(player, sungtrai, 1);
                                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 30);
                                        Item petnguumavuong = ItemService.gI().createNewItem((short) 1202);
                                        petnguumavuong.itemOptions.add(new ItemOption(77, Util.nextInt(8, 15)));
                                        petnguumavuong.itemOptions.add(new ItemOption(50, Util.nextInt(8, 15)));
                                        petnguumavuong.itemOptions.add(new ItemOption(103, Util.nextInt(8, 15)));
                                        InventoryService.gI().addItemBag(player, petnguumavuong);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "|2|Bạn nhận được Pet Ngưu Ma Vương!");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc elec(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 199) {
                        this.createOtherMenu(player, 0,
                                "|2|Ngươi Muốn Gì Ở Ta?\n",
                                "Kích Hoạt\n Cải Trang", "Gọi Boss", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 199) {
                        if (player.iDMark.getIndexMenu() == 0) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1,
                                            "|0|Bạn Có Muốn Mở Chỉ Số Cải Trang Không?\n"
                                            + "\n|2|Lưu Ý: Ta Chỉ Có Thể Kích Hoạt Chỉ Số Cải Trang Mà Ngươi Mua Ở Tên Granola Đó"
                                            + "\n|2|Hãy Mang Cải Trang Đến Đây Nào!\n",
                                            "Kích Hoạt", "Đóng");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 2,
                                            "|7|Bạn Có Muốn Gọi Boss Không?\n"
                                            + "\n|2|Băng HEETERS Gồm 4 Boss: Oil, Gas, Elec, Macki"
                                            + "\n|2|Tiêu Diệt Boss Để Lấy Đá Sức Mạnh\n"
                                            + "\n|6|Ta Sẽ Triệu Hồi Phân Thân Của Băng HEETERS Ra Cho Ngươi Chiến Đấu"
                                            + "\n|6|Nếu Thắng Ngươi Sẽ Nhận Được Phần Thưởng Tương Ứng"
                                            + "\n|6|Giá Thì Ngươi Yên Tâm, Chỉ Như Bát Phở!\n"
                                            + "\n|2|Phân Thân Elec: 5 Thỏi Vàng"
                                            + "\n|2|Phân Thân Macki: 3 Thỏi Vàng"
                                            + "\n|2|Phân Thân Gas: 3 Thỏi Vàng"
                                            + "\n|2|Phân Thân Oil: 3 Thỏi Vàng"
                                            + "\n|2|Phân Thân 4 Người: 10 Thỏi Vàng",
                                            "Phân Thân\n Elec", "Phân Thân\nMacki", "Phân Thân\nGas", "Phân Thân\nOil", "Phân Thân\n4 Người", "Từ Chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) {
                            switch (select) {
                                case 0:
//                                    CombineService.gI().openTabCombine(player, CombineService.MO_CHI_SO_CEREAL);
                                    break;
                            }
//                       } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
//                            switch (player.combineNew.typeCombine) {
//                                case CombineService.MO_CHI_SO_CEREAL:
//                                    if (select == 0) {
//                                        CombineService.gI().startCombine(player);
//                                    }
//                                    break;
//                            }
                        } else if (player.iDMark.getIndexMenu() == 2) {
                            switch (select) {
                                case 0://elec
                                    Item thoivang = null;
                                    try {
                                        thoivang = InventoryService.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
                                    }
                                    Boss Elec = BossManager.gI().getBossById(BossID.ElecManager);
                                    if (Elec != null) {
                                        Service.gI().sendThongBao(player, "Bạn Hãy Tiêu Diệt Boss Đợt Trước Đã, Boss Ở Khu " + Elec.zone.zoneId);
                                        if (thoivang == null || thoivang.quantity < 5) {
                                            Service.gI().sendThongBao(player, "Ngươi Không Đủ Thỏi Vàng!");
                                        }
                                    } else {
                                        try {
                                            ElecManager elecManager = new ElecManager(player.zone, 2, Util.nextInt(1000, 10000), BossID.ElecManager);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 5);
                                        InventoryService.gI().sendItemBag(player);
                                    }
                                    break;
                                case 1://macki                               
                                    Item thoivang2 = null;
                                    try {
                                        thoivang2 = InventoryService.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
                                    }
                                    Boss macki = BossManager.gI().getBossById(BossID.MackiManager);
                                    if (macki != null) {
                                        Service.gI().sendThongBao(player, "Bạn Hãy Tiêu Diệt Boss Đợt Trước Đã, Boss Ở Khu " + macki.zone.zoneId);
                                        if (thoivang2 == null || thoivang2.quantity < 3) {
                                            Service.gI().sendThongBao(player, "Ngươi Không Đủ Thỏi Vàng!");
                                        }
                                    } else {
                                        try {
                                            MackiManager mackiManager = new MackiManager(player.zone, 2, Util.nextInt(1000, 10000), BossID.MackiManager);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        InventoryService.gI().subQuantityItemsBag(player, thoivang2, 3);
                                        InventoryService.gI().sendItemBag(player);
                                    }
                                    break;
                                case 2://gas
                                    Item thoivang3 = null;
                                    try {
                                        thoivang3 = InventoryService.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
                                    }
                                    Boss gas = BossManager.gI().getBossById(BossID.GasManager);
                                    if (gas != null) {
                                        Service.gI().sendThongBao(player, "Bạn Hãy Tiêu Diệt Boss Đợt Trước Đã, Boss Ở Khu " + gas.zone.zoneId);
                                        if (thoivang3 == null || thoivang3.quantity < 3) {
                                            Service.gI().sendThongBao(player, "Ngươi Không Đủ Thỏi Vàng!");
                                        }
                                    } else {
                                        try {
                                            GasManager gasManager = new GasManager(player.zone, 2, Util.nextInt(1000, 10000), BossID.GasManager);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        InventoryService.gI().subQuantityItemsBag(player, thoivang3, 3);
                                        InventoryService.gI().sendItemBag(player);
                                    }
                                    break;

                                case 3://oil
                                    Item thoivang4 = null;
                                    try {
                                        thoivang4 = InventoryService.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
                                    }
                                    Boss oil = BossManager.gI().getBossById(BossID.OilManager);
                                    if (oil != null) {
                                        Service.gI().sendThongBao(player, "Bạn Hãy Tiêu Diệt Boss Đợt Trước Đã, Boss Ở Khu " + oil.zone.zoneId);
                                        if (thoivang4 == null || thoivang4.quantity < 3) {
                                            Service.gI().sendThongBao(player, "Ngươi Không Đủ Thỏi Vàng!");
                                        }
                                    } else {
                                        try {
                                            OilManager oilManager = new OilManager(player.zone, 2, Util.nextInt(1000, 10000), BossID.OilManager);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        InventoryService.gI().subQuantityItemsBag(player, thoivang4, 3);
                                        InventoryService.gI().sendItemBag(player);
                                    }
                                    break;

                                case 4://cả băng
                                    Service.gI().sendThongBao(player, "Không Thể Gọi ");
                                    break;
                            }
                        }

                    }
                }
            }
        };
    }

    public static Npc vip_truongchimto(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 181) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Thức Tỉnh");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 181) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
//                                    CombineService.gI().openTabCombine(player, CombineService.THUC_TINH_DT);
                                    break;
                            }
//                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
//                            switch (player.combineNew.typeCombine) {
//                                case CombineService.THUC_TINH_DT:
//                                    switch (select) {
//                                        case 0:
//                                            CombineService.gI().thuctinhDT(player, 1);
//                                            System.out.print("test");
//                                            break;
//                                        case 1:
//                                            CombineService.gI().thuctinhDT(player, 10);
//                                            break;
//                                        case 2:
//                                            CombineService.gI().thuctinhDT(player, 100);
//                                            break;
//                                    }
//                                    break;
//                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.TRANH_NGOC_NAMEC:
                    return new TranhNgocNamek(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CADIC:
                    return new Cadic(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.FIDE:
                    return new Fide(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GHI_DANH:
                    return new GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE:
                    return new Potage(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME:
                    return new QuyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MR_POPO:
                    return new MrPoPo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MEO_DO_DEN:
                    return new ChanLe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return new TruongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return new VuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                    return new OngGohan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_MOORI:
                    return new OngMoori(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_PARAGUS:
                    return new OngParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return new Bulma(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return new Dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return new Appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return new DrDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return new Cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return new Cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return new Santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHAN_LE:
                    return ChanLe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return new Uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return new BaHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NOI_BANH:
                    return new NoiBanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return new RuongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return new DauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return new Calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return new Jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAISHINKAN:
                    return new DaiThienSu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRONG_TAI:
                    return new TrongTai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NGO_KHONG:
                    return new NgoKhong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NULL_03:
                    return elec(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return new ThuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HALLOWEEN_EVENT:
                    return new HalloweenEvent(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO:
                    return new GiuMaDauBo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI:
                    return new ShopKyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VU_LAN_EVENT:
                    return new VuLanEvent(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_VU_TRU:
                    return new ThanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return new Kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return new Osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG:
                    return new LyTieuNuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH:
                    return LinhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DOC_NHAN:
                    return new DocNhan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return new QuaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUA_HAU:
                    return new DuaHau(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return new QuocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return new BulmaTuongLai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.calichquakhu:
                    return calichquakhu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CAY_THONG_NOEL:
                    return new CayThongNoel(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CAY_THONG_NOEL_VERSION:
                    return new CayThongNoel_Version(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA:
                    return new RongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                    return new Rong1Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_2S:
                    return new Rong2Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_3S:
                    return new Rong3Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_4S:
                    return new Rong4Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_5S:
                    return new Rong5Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_6S:
                    return new Rong6Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_7S:
                    return new Rong7Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return new Bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LAO_GIA:
                    return new Laogia(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.WHIS:
                    return new Whis(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return new BoMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_MEO_KARIN:
                    return new ThanMeoKarin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return GokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_2:
                    return GokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG:
                    return new DuongTang(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BABIDAY:
                    return new Babiday(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHOPPER:
                    return chopper(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.FRANKY:
                    return franky(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NAMI:
                    return nami(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TO_SU_KAIO:
                    return new ToSuKaio(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KARIN_KID_LAN:
                    return new EventTetNguyenDan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CAY_NEU_NEWYEAR:
                    return new CayNeu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.INTERNATIONAL_WOMENS_DAY:
                    return new BulmaTho(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HOA_HONG_8_3:
                    return new HoaHongInternationalWomensDayEvent(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUNG_THU:
                    return new TrungThuEvent(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TREO_DEN_TRUNG_THU:
                    return new TreoDenTrungThu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THO_DAI_KA:
                    return new ThoDaiKa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HUNG_VUONG:
                    return new HungVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_BANG_HOI:
                    return new RuongBangHoi(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_SUU_TAP:
                    return new RuongSuuTap(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS:
                    return new Vados(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VALENTINE_EVENT:
                    return new ValentineEvent(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.PANCHY:
                    return new Panchy(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAY_20_10:
                    return new Day20_10(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MINUONG:
                    return miNuong(mapId, status, cx, cy, tempId, avatar);
//                case ConstNpc.GAPTHU1:
//                    return gapthu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TORIBOT:
                    return new ToriBot(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHI_CHI:
                    return new ChiChi(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BLACK_FRIDAY:
                    return new BlackFridayEvent(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MABU_MAP:
                    return mabumap(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TAPION:
                    return new Tapion(mapId, status, cx, cy, tempId, avatar);
                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class,
                    e, "Lỗi load npc");
            return null;
        }
    }

    //girlbeo-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.SHOW_SHENRON_NAMEK_CONFIRM:
                        SummonDragonNamek.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                    case ConstNpc.SHENRON_NAMEK_CONFIRM:
                        if (select == 0) {
                            SummonDragonNamek.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragonNamek.gI().sendWhishesNamec(player);
                        }
                        break;
                    case ConstNpc.SHOW_SHENRON_EVENT_CONFIRM_CHRISTMAS:
                        if (player.shenronEvent_Christmas != null) {
                            player.shenronEvent_Christmas.showConfirmShenron((byte) select);
                        }
                        break;
                    case ConstNpc.SHENRON_EVENT_CONFIRM_CHRISTMAS:
                        if (player.shenronEvent_Christmas != null) {
                            if (select == 0) {
                                player.shenronEvent_Christmas.confirmWish();
                            } else if (select == 1) {
                                player.shenronEvent_Christmas.sendWhishesShenron();
                            }
                        }
                        break;
                    case ConstNpc.SHOW_SHENRON_EVENT_CONFIRM_HALLOWEEN:
                        if (player.shenronEvent_Halloween != null) {
                            player.shenronEvent_Halloween.showConfirmShenron((byte) select);
                        }
                        break;
                    case ConstNpc.SHENRON_EVENT_CONFIRM_HALLOWEEN:
                        if (player.shenronEvent_Halloween != null) {
                            if (select == 0) {
                                player.shenronEvent_Halloween.confirmWish();
                            } else if (select == 1) {
                                player.shenronEvent_Halloween.sendWhishesShenron();
                            }
                        }
                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng_Nomal(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng_Nomal(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.MAKE_MATCH_PVP: {
                        if (Maintenance.isRunning) {
                            break;
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;
                    }
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                try {
                                    FriendAndEnemyService.gI().acceptMakeFriend(player,
                                            Integer.parseInt(String.valueOf(playerId)));
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON_EVENT_CHRISTMAS: {
                        if (select == 0) {
                            ShenronChristMasEventService.gI().summonShenron(player);
                        }
                        break;
                    }
                    case ConstNpc.SUMMON_SHENRON_EVENT_HALLOWEEN: {
                        if (select == 0) {
                            ShenronHalloweenEventService.gI().summonShenron(player);
                        }
                        break;
                    }
                    case ConstNpc.USE_THOI_VANG: {
                        int[] quantities = {1, 5, 10, 20, 50, 100, 200, 500, 1000};
                        long[] prices = {
                            500_000_000L, // 1 thỏi vàng
                            2_500_000_000L, // 5 thỏi vàng
                            5_000_000_000L, // 10
                            10_000_000_000L, // 20
                            25_000_000_000L, // 50
                            50_000_000_000L, // 100
                            100_000_000_000L, // 200
                            250_000_000_000L, // 500
                            500_000_000_000L // 1000
                        };
                        long MAX_GOLD = Inventory.LIMIT_GOLD;

                        if (select < 0 || select >= quantities.length) {
                            Service.getInstance().sendThongBao(player, "Lựa chọn không hợp lệ!");
                            return;
                        }

                        int sltv = quantities[select];
                        long cost = prices[select];

                        Item usethoivang = InventoryService.gI().findItemBag(player, 457);

                        if (usethoivang == null || usethoivang.quantity < sltv) {
                            Service.getInstance().sendThongBao(player, "Bạn không đủ " + sltv + " Thỏi vàng để bán!");
                            return;
                        }

                        if (player.inventory.gold >= MAX_GOLD) {
                            Service.getInstance().sendThongBao(player, "Bạn đã đạt giới hạn vàng, không thể bán thêm!");
                            return;
                        }

                        long newGoldAmount = player.inventory.gold + cost;
                        if (newGoldAmount > MAX_GOLD) {
                            long remainingCapacity = MAX_GOLD - player.inventory.gold;
                            // Tính số lượng thỏi vàng có thể bán tối đa dựa vào số vàng còn lại
                            int maxSellable = (int) (remainingCapacity / (cost / sltv));
                            maxSellable = Math.min(maxSellable, usethoivang.quantity);

                            if (maxSellable < 1) {
                                Service.getInstance().sendThongBao(player, "Vàng sau khi bán sẽ vượt giới hạn. Bạn không thể bán thêm Thỏi vàng nào lúc này!");
                            } else {
                                Service.getInstance().sendThongBao(player, "Vàng sau khi bán vượt giới hạn. Bạn chỉ có thể bán tối đa " + maxSellable + " Thỏi vàng!");
                            }
                            return;
                        }

                        // Trừ thỏi vàng, cộng vàng
                        InventoryService.gI().subQuantityItemsBag(player, usethoivang, sltv);
                        player.inventory.gold += cost;

                        // Gửi cập nhật
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        InventoryService.gI().sendItemBag(player);
                        Service.getInstance().sendMoney(player);
                        Service.getInstance().sendThongBao(player, "Đã bán " + sltv + " Thỏi vàng, thu được " + Util.formatNumber(cost, FormatStyle.VIETNAMESE) + " vàng.");
                    }
                    break;

                    case ConstNpc.MENU_OPTION_USE_ITEM726:
                        if (select == 0) {
                            SuperDivineWater.gI().joinMapThanhThuy(player);
                        }
                        break;
                    case ConstNpc.MENU_SIEU_THAN_THUY:
                        if (select == 0) {
                            ChangeMapService.gI().changeMap(player, 46, -1, Util.nextInt(300, 400), 408);
                        }
                        break;
                    case ConstNpc.MENU_CALL_BOT:
                        switch (select) {
                            case 0:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CALL_BOTVIP, 543,
                                        "|1|======= NRO-----TUỔI----THƠ =======\n"
                                        + "|8|[Bot Player]\n"
                                        + "|5|<-------- Hãy Lựa Chọn -------->\n",
                                        "Bot\nPem Quái", "Bot\nBán Item", "Bot\nSăn Boss", "Đóng");
                                break;
                            default:
                                break;
                        }
                        break;

                    case ConstNpc.MENU_CALL_BOTVIP:
                        switch (select) {
                            case 0:
                                Input.gI().createFormBotQuai(player);
                                break;
                            case 1:
                                Input.gI().createFormBotItem(player);
                                break;
                            case 2:
                                Input.gI().createFormBotBoss(player);
                                break;
                        }
                        break;

                    case ConstNpc.INTRINSIC:
                        switch (select) {
                            case 0:
                                IntrinsicService.gI().showAllIntrinsic(player);
                                break;
                            case 1:
                                IntrinsicService.gI().showConfirmOpen(player);
                                break;
                            case 2:
                                IntrinsicService.gI().showConfirmOpenVip(player);
                                break;
                            default: {
                                break;
                            }
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (player.baovetaikhoan) {
                            Service.gI().sendThongBao(player, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
                            return;
                        }
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (player.baovetaikhoan) {
                            Service.gI().sendThongBao(player, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
                            return;
                        }
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;

                    case ConstNpc.UP_TOP_ITEM:
                        if (select == 0) {
                            if (player.inventory.gold < 500000000) {
                                Service.gI().sendThongBao(player, "Bạn không có đủ vàng!");
                                return;
                            }
                            player.inventory.gold -= 500000000;
                            Service.gI().sendMoney(player);
                            int iditem = player.iDMark.getIdItemUpTop();
                            ConsignShopService.gI().getItemBuy(player, iditem).lasttime = System.currentTimeMillis();
                            Service.gI().sendThongBao(player, "Up top thành công!");
                            ConsignShopService.gI().openShopKyGui(player);
                        }
                        break;

                    case ConstNpc.petservice:
                        switch (select) {
                            case 0:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.muade, 16449,
                                        "|7|Quản Lí Đệ Tử" + "\n" + "|1|" + "\n"
                                        + "Số Dư Khả Dụng : " + player.getSession().vnd + " VNĐ" + "\n"
                                        + "Bạn có chăc muốn đổi đệ không?" + "\n" + "\n"
                                        + "|7|* Chỉ Cần Mua 1 Đệ Tử Bất Kỳ, Hệ Thống Sẽ"
                                        + "\nTự Động Kích Hoạt Tài Khoản Cho Bạn!!"
                                        + "\nCHÚ Ý : Đệ nhận miễn phí không tự động KÍCH HOẠT Account",
                                        "Đệ Kefla\n100.000VNĐ (Bảo Trì)", "Đệ Cumber\n150.000VNĐ (Bảo Trì)", "Đóng");
                                break;
                            case 1:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.doiskill, 16449,
                                        "|7|Quản Lí Đệ Tử" + "\n" + "|1|" + "\n"
                                        + "Số Dư Khả Dụng : " + player.getSession().vnd + " VNĐ" + "\n" + "\n"
                                        + "Phí đổi SKILL 2 là 25.000 VNĐ" + "\n"
                                        + "Phí đổi SKILL 3 là 50.000 VNĐ" + "\n"
                                        + "Phí đổi SKILL 4 là 100.000 VNĐ" + "\n" + "\n"
                                        + "|7|[ ĐỔI SKILL ĐỆ TỬ ]" + "\n", "Đổi\nSkill 2", "Đổi\nSkill 3", "Đổi\nSkill 4", "Đóng");
                                break;
                            case 2:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.doihanhtinh, 16449,
                                        "|7|Quản Lí Đệ Tử" + "\n" + "|1|" + "\n"
                                        + "Số Dư Khả Dụng : " + player.getSession().vnd + " VNĐ" + "\n" + "\n"
                                        + "Phí đổi qua HÀNH TINH TRAIDAT là 20.000 VNĐ" + "\n"
                                        + "Phí đổi qua HÀNH TINH NAMEC là 20.000 VNĐ" + "\n"
                                        + "Phí đổi qua HÀNH TINH XAYDA là 20.000 VNĐ" + "\n" + "\n"
                                        + "|7|[ ĐỔI HÀNH TINH ĐỆ TỬ ]" + "\n", "Đổi HÀNH\nTINH TD", "Đổi HÀNH\nTINH NM", "Đổi HÀNH\nTINH XD", "Đóng");
                                break;
                        }
                        break;

                    case ConstNpc.doihanhtinh:
                        switch (select) {
                            case 0:
                                if (player.getSession().vnd < 20000) {
                                    Service.gI().sendThongBao(player, "Bạn không dủ tiền, hãy nạp tiền để mua");
                                } else {
                                    if (player.Detu != null) {
                                        try {
                                            int sum = player.getSession().vnd - 20000;
                                            ConnectDB.executeUpdate(
                                                    "update account set vnd = ? where id = ?", sum,
                                                    player.account_id);
                                            player.getSession().vnd = sum;

                                        } catch (Exception e) {
                                            this.npcChat(player, "Đã có lỗi xảy ra!" + e);
                                        }
                                        player.Detu.gender = 0;
                                        ChangeMapService.gI().exitMap(player.Detu);
                                        Service.gI().sendThongBao(player, "Đã đổi thành công hành tinh Trái Đất");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn làm gì có đệ");
                                    }
                                }
                                break;
                            case 1:
                                if (player.getSession().vnd < 20000) {
                                    Service.gI().sendThongBao(player, "Bạn không dủ tiền, hãy nạp tiền để mua");
                                } else {
                                    if (player.Detu != null) {
                                        try {
                                            int sum = player.getSession().vnd - 20000;
                                            ConnectDB.executeUpdate(
                                                    "update account set vnd = ? where id = ?", sum,
                                                    player.account_id);
                                            player.getSession().vnd = sum;
                                        } catch (Exception e) {
                                            this.npcChat(player, "Đã có lỗi xảy ra!" + e);
                                        }
                                        player.Detu.gender = 1;
                                        ChangeMapService.gI().exitMap(player.Detu);
                                        Service.gI().sendThongBao(player, "Đã đổi thành công hành tinh Namek");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn làm gì có đệ");
                                    }
                                }
                                break;
                            case 2:
                                if (player.getSession().vnd < 20000) {
                                    Service.gI().sendThongBao(player, "Bạn không dủ tiền, hãy nạp tiền để mua");
                                } else {
                                    if (player.Detu != null) {
                                        try {
                                            int sum = player.getSession().vnd - 20000;
                                            ConnectDB.executeUpdate(
                                                    "update account set vnd = ? where id = ?", sum,
                                                    player.account_id);
                                            player.getSession().vnd = sum;
                                        } catch (Exception e) {
                                            this.npcChat(player, "Đã có lỗi xảy ra!" + e);
                                        }
                                        player.Detu.gender = 2;
                                        ChangeMapService.gI().exitMap(player.Detu);
                                        Service.gI().sendThongBao(player, "Đã đổi thành công đệ hành tinh Xayda");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn làm gì có đệ");
                                    }
                                }
                                break;
                        }
                        break;
                    case ConstNpc.doiskill:
                        switch (select) {
                            case 0:
                                if (player.getSession().vnd < 25000) {
                                    Service.gI().sendThongBao(player, "Cần 25.000 vnđ");
                                } else {
                                    if (player.Detu == null) {
                                        Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                                        break;
                                    }
                                    if (player.Detu.playerSkill.skills.get(1).skillId != -1) {
                                        player.Detu.openSkill2();
                                        try {
                                            int sum = player.getSession().vnd - 25000;
                                            ConnectDB.executeUpdate(
                                                    "update account set vnd = ? where id = ?", sum,
                                                    player.account_id);
                                            player.getSession().vnd = sum;

                                        } catch (Exception e) {
                                            this.npcChat(player, "Đã có lỗi xảy ra!" + e);
                                        }
                                        Service.gI().sendThongBao(player, "Đã đổi thành công chiêu 2 đệ tử");
                                    } else {
                                        Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");
                                    }
                                }
                                break;
                            case 1:
                                if (player.getSession().vnd < 50000) {
                                    Service.gI().sendThongBao(player, "Cần 50.000 vnđ");
                                } else {
                                    if (player.Detu == null) {
                                        Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                                        break;
                                    }
                                    if (player.Detu.playerSkill.skills.get(2).skillId != -1) {
                                        try {
                                            int sum = player.getSession().vnd - 50000;
                                            ConnectDB.executeUpdate(
                                                    "update account set vnd = ? where id = ?", sum,
                                                    player.account_id);
                                            player.getSession().vnd = sum;

                                        } catch (Exception e) {
                                            this.npcChat(player, "Đã có lỗi xảy ra!" + e);
                                        }
                                        player.Detu.openSkill3();
                                        Service.gI().sendThongBao(player, "Đã đổi thành công chiêu 3 đệ tử");
                                    } else {
                                        Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");
                                    }
                                }
                                break;
                            case 2:
                                if (player.getSession().vnd < 100000) {
                                    Service.gI().sendThongBao(player, "Cần 100.000 vnđ");
                                } else {
                                    if (player.Detu == null) {
                                        Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                                        break;
                                    }
                                    if (player.Detu.playerSkill.skills.get(3).skillId != -1) {
                                        try {
                                            int sum = player.getSession().vnd - 100000;
                                            ConnectDB.executeUpdate(
                                                    "update account set vnd = ? where id = ?", sum,
                                                    player.account_id);
                                            player.getSession().vnd = sum;

                                        } catch (Exception e) {
                                            this.npcChat(player, "Đã có lỗi xảy ra!" + e);
                                        }
                                        player.Detu.openSkill4();
                                        Service.gI().sendThongBao(player, "Đã đổi thành công chiêu 4 đệ tử");
                                    } else {
                                        Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 4 chứ!");
                                    }
                                }
                                break;
                        }
                        break;
//----------------------------CHỨC NĂNG ADMIN-----------------------------------
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            if (((Player) PLAYERID_OBJECT.get(player.id)).isFounder()) {
                                Service.gI().sendThongBaoOK((Player) PLAYERID_OBJECT.get(player.id), "|2|Thằng [" + player.name + "] Đang Muốn Khoá Tài Khoản Bạn!");
                                Service.gI().sendThongBao(player, "|7|Không Thể Khoá Tài Khoản ADMINISTRATOR");
                            } else {
                                PlayerService.gI().KhoaTaiKhoan((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Khoá Tài Khoản Người Chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            }
                        }
                        break;
                    case ConstNpc.ACTIVE:
                        if (select == 0) {
                            if (!((Player) PLAYERID_OBJECT.get(player.id)).getSession().actived) {
                                PlayerService.gI().MoThanhVienPlayer((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Mở Thành Viên Cho Người Chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            } else {
                                Service.gI().sendThongBao(player, "|7|Mở Thành Viên Không Thành Công Vì " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Đã Mở Thành Viên Rồi!");
                            }
                        } else if (select == 2) {
                            if (((Player) PLAYERID_OBJECT.get(player.id)).getSession().actived) {
                                PlayerService.gI().XoaThanhVienPlayer((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Xoá Thành Viên Cho Người Chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            } else {
                                Service.gI().sendThongBao(player, "|7|Xoá Thành Viên Không Thành Công Vì " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Không Phải Là Thành Viên!");
                            }
                        }
                        break;
                    case ConstNpc.JAIL:
                        try {
                            if (select == 0) {
                                if (((Player) PLAYERID_OBJECT.get(player.id)).getSession().isJail == false) {
                                    PlayerService.gI().jail((Player) PLAYERID_OBJECT.get(player.id));
                                    ChangeMapService.gI().changeMap(((Player) PLAYERID_OBJECT.get(player.id)), 49, 0, 620, 320);
                                    Service.gI().sendThongBaoOK(player, "Giam người chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                                    Thread.sleep(3000);
                                    Service.gI().sendThongBaoFromAdmin(((Player) PLAYERID_OBJECT.get(player.id)), "Bạn Đã Bị Giam Bởi " + player.name);
                                    ServerNotify.gI().notify("người chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " đã vào tù vì phạm tội");
                                } else {
                                    PlayerService.gI().jail((Player) PLAYERID_OBJECT.get(player.id));
                                    ChangeMapService.gI().changeMap(((Player) PLAYERID_OBJECT.get(player.id)), 5, -1, 1040, 169);
                                    Service.gI().sendThongBaoOK(player, "Mở giam người chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                                    Thread.sleep(3000);
                                    Service.gI().sendThongBaoFromAdmin(((Player) PLAYERID_OBJECT.get(player.id)), "Bạn Đã Được Mở Giam Bởi " + player.name);
                                    ServerNotify.gI().notify("Người chơi : (" + ((Player) PLAYERID_OBJECT.get(player.id)).name + ") đã được mãn hạn tù");
                                }
                            }
                        } catch (InterruptedException e) {
                        }
                        break;
                    case ConstNpc.NEXT_NV:
                        switch (select) {
                            case 0:
                                Input.gI().nextnhiemvu(player);
                                break;
                            case 1:
                                if (((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.id > 33) {
                                    Service.gI().sendThongBao(player, "Nhiệm Vụ Quá Giới Hạn!");
                                    break;
                                }
                                if (((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.id < 33) {
                                    int nvp = ((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.id;
                                    TaskService.gI().sendNextTaskMain(((Player) PLAYERID_OBJECT.get(player.id)));
                                    Service.gI().sendThongBaoOK(((Player) PLAYERID_OBJECT.get(player.id)), "Bạn Vừa Được Bỏ Qua Nhiệm Vụ Bởi ADMIN!\n"
                                            + "Hãy Đăng Nhập Lại Để Kích Hoạt Hoặc Hệ Thống Sẽ Kick Bạn Ra Khỏi Server Sau 10s Nữa!");
                                    ((Player) PLAYERID_OBJECT.get(player.id)).iDMark.setLastTimeBan(System.currentTimeMillis());
                                    ((Player) PLAYERID_OBJECT.get(player.id)).iDMark.setBan(true);
                                    Service.gI().sendThongBaoOK(player, "Bỏ Qua Nhiệm Vụ Cho Người Chơi [" + ((Player) PLAYERID_OBJECT.get(player.id)).name + "] Đến " + nvp + " Thành Công!");
                                } else {
                                    Service.gI().sendThongBao(player, "Người Chơi Không Online!");
                                }
                                break;
                            case 2:
                                if (((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.id > 33) {
                                    Service.gI().sendThongBao(player, "Nhiệm Vụ Quá Giới Hạn!");
                                    break;
                                }
                                if (((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.id < 33) {
                                    String nvp = ((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.subTasks.get(((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.index).name;
                                    TaskService.gI().addDoneSubTask(((Player) PLAYERID_OBJECT.get(player.id)), ((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.subTasks.get(((Player) PLAYERID_OBJECT.get(player.id)).playerTask.taskMain.index).maxCount);
                                    Service.gI().sendThongBaoOK(player, "Bỏ Qua Nhiệm Vụ Cho Người Chơi [" + ((Player) PLAYERID_OBJECT.get(player.id)).name + "] Đến " + nvp + " Thành Công!");
                                    ((Player) PLAYERID_OBJECT.get(player.id)).iDMark.setLastTimeBan(System.currentTimeMillis());
                                    ((Player) PLAYERID_OBJECT.get(player.id)).iDMark.setBan(true);
                                    Service.gI().sendThongBaoOK(((Player) PLAYERID_OBJECT.get(player.id)), "Bạn Vừa Được Bỏ Qua Nhiệm Vụ Bởi ADMIN!\n"
                                            + "Hãy Đăng Nhập Lại Để Kích Hoạt Hoặc Hệ Thống Sẽ Kick Bạn Ra Khỏi Server Sau 10s Nữa!");
                                } else {
                                    Service.gI().sendThongBao(player, "Người Chơi Không Online!");
                                }
                                break;
                        }
                        break;
                    case ConstNpc.BO_NHIEM_UY_NHIEM:
                        if (select == 0) {
                            if (((Player) PLAYERID_OBJECT.get(player.id)).isQuanTriVien()) {
                                Service.gI().sendThongBao(player, "|7|Cấp Quyền Không Thành Công Vì " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Đang Là KEY CONTROLLER");
                            } else {
                                PlayerService.gI().CapQuyenKeyController((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Cấp Quyền KEY CONTROLLER " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            }
                        }
                        if (select == 1) {
                            if (!((Player) PLAYERID_OBJECT.get(player.id)).isQuanTriVien()) {
                                Service.gI().sendThongBao(player, "|7|Huỷ Quyền Không Thành Công Vì " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Không Phải Là KEY CONTROLLER");
                            } else {
                                PlayerService.gI().HuyQuyenKeyController((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Hủy Quyền KEY CONTROLLER " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            }
                        }
                        if (select == 2) {
                            if (((Player) PLAYERID_OBJECT.get(player.id)).isFounder()) {
                                Service.gI().sendThongBao(player, "|7|Cấp Quyền Không Thành Công Vì " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Đang Là ADMINISTRATOR");
                            } else {
                                PlayerService.gI().CapQuyenAdmin((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Cấp Quyền ADMINISTRATOR " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            }
                        }
                        if (select == 3) {
                            if (!((Player) PLAYERID_OBJECT.get(player.id)).isFounder()) {
                                Service.gI().sendThongBao(player, "|7|Huỷ Quyền Không Thành Công Vì " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Không Phải Là ADMINISTRATOR");
                            } else {
                                PlayerService.gI().HuyQuyenAdmin((Player) PLAYERID_OBJECT.get(player.id));
                                Service.gI().sendThongBao(player, "|1|Hủy Quyền ADMINISTRATOR " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " Thành Công!");
                            }
                        }
                        break;
//------------------------------------------------------------------------------
//--------------------------------CHỨC NĂNG ĐỆ TỬ-------------------------------
                    case ConstNpc.MODE_PET:
                        switch (select) {
                            case 0:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.TYPE_PET, 24222, "|7|CHANGE TYPE DISCIPLE" + "\n"
                                        + "|1|[ CHANGE TYPE DISCIPLE FOR PLAYER ]\n"
                                        + "|2|Đổi Kiểu Đệ Tử Giữ Lại Đồ Và Kĩ Năng\n"
                                        + "|2|Nếu Người Chơi Chưa Có Đệ, Chọn Đệ Sẽ Auto Tạo Đệ Thường\n"
                                        + "|7|Chọn Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                        "Đệ Tử Mabu", "Đệ Tử Berus", "Đệ Tử Ngộ Không", "Đóng");
                                break;
                            case 1:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.GENDER_PET, 24222, "|7|CHANGE GENDER DISCIPLE" + "\n"
                                        + "|1|[ CHANGE GENDER DISCIPLE FOR PLAYER ]\n"
                                        + "|2|Đổi Hành Tinh Đệ Tử\n"
                                        + "|7|Chọn Hành Tinh Đệ Tử Muốn Đổi Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                        "Hành Tinh Trái Đất", "Hành Tinh Namec", "Hành Tinh Xayda", "Đóng");
                                break;
                            case 2:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL_PET, 24222, "|7|CHANGE SKILL DISCIPLE" + "\n"
                                        + "|1|[ CHANGE SKILL DISCIPLE FOR PLAYER ]\n"
                                        + "|2|Đổi Kĩ Năng Đệ Tử\n"
                                        + "|7|Chọn Kĩ Năng Đệ Tử Muốn Đổi Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                        "Kĩ Năng 2", "Kĩ Năng 3", "Kĩ Năng 4", "Kĩ Năng 5", "Kĩ Năng 6", "Kĩ Năng 7", "Đóng");
                                break;
                            case 3:
                                Input.gI().BuffChiSoDeTu(player);
                                break;
                            case 4:
                                Input.gI().SubChiSoDeTu(player);
                                break;
                        }
                        break;
                    case ConstNpc.TYPE_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                DetuService.gI().createNormalPet(pl);
                                Service.gI().sendThongBao(player, "Đã Tạo Đệ Thường Cho Người Chơi " + pl.name);
                                break;
                            }
                            DetuService.gI().createMabuPet(pl);
                            pl.Detu.nPoint.setFullHpMp();
                            Service.gI().Send_Caitrang(pl.Detu);
                            ChangeMapService.gI().exitMap(pl.Detu);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                            Service.gI().sendThongBaoFromAdmin(pl, "Bạn Nhận Được Đệ Tử Mabu Từ ADMIN [" + player.name + "]\n"
                                    + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
                            Service.gI().sendThongBao(player, "Đổi Đệ Tử Mabu Cho Người Chơi " + pl.name + " Thành Công!");
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                DetuService.gI().createNormalPet(pl, pl.gender, pl.nPoint.limitPower);
                                Service.gI().sendThongBao(player, "Đã Tạo Đệ Thường Cho Người Chơi " + pl.name);
                                break;
                            }
                            pl.Detu.nPoint.setFullHpMp();
                            Service.gI().Send_Caitrang(pl.Detu);
                            ChangeMapService.gI().exitMap(pl.Detu);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                            Service.gI().sendThongBaoFromAdmin(pl, "Bạn Nhận Được Đệ Tử Berus Từ ADMIN [" + player.name + "]\n"
                                    + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
                            Service.gI().sendThongBao(player, "Đổi Đệ Tử Berus Cho Người Chơi " + pl.name + " Thành Công!");
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                DetuService.gI().createNormalPet(pl, pl.gender, pl.nPoint.limitPower);
                                Service.gI().sendThongBao(player, "Đã Tạo Đệ Thường Cho Người Chơi " + pl.name);
                                break;
                            }
                            pl.Detu.nPoint.setFullHpMp();
                            Service.gI().Send_Caitrang(pl.Detu);
                            ChangeMapService.gI().exitMap(pl.Detu);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                            Service.gI().sendThongBaoFromAdmin(pl, "Bạn Nhận Được Đệ Tử Ngộ Không Từ ADMIN [" + player.name + "]\n"
                                    + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
                            Service.gI().sendThongBao(player, "Đổi Đệ Tử Ngộ Không Cho Người Chơi " + pl.name + " Thành Công!");
                        }
                        break;
                    }
                    case ConstNpc.GENDER_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.gender = 0;
                            ChangeMapService.gI().exitMap(pl.Detu);
                            Service.gI().sendThongBaoOK(pl, "Hành Tinh Đệ Tử Của Bạn Đã Bị Đổi Sang Trái Đất Bởi ADMIN [" + player.name + "]\n"
                                    + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
                            Service.gI().sendThongBaoOK(player, "Đổi Hành Tinh Đệ Tử Qua Trái Đất Cho Người Chơi " + pl.name + " Thành Công!");
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.gender = 1;
                            ChangeMapService.gI().exitMap(pl.Detu);
                            Service.gI().sendThongBaoOK(pl, "Hành Tinh Đệ Tử Của Bạn Đã Bị Đổi Sang Namek Bởi ADMIN [" + player.name + "]\n"
                                    + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
                            Service.gI().sendThongBaoOK(player, "Đổi Hành Tinh Đệ Tử Qua Namek Cho Người Chơi " + pl.name + " Thành Công!");
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.gender = 2;
                            ChangeMapService.gI().exitMap(pl.Detu);
                            Service.gI().sendThongBaoOK(pl, "Hành Tinh Đệ Tử Của Bạn Đã Bị Đổi Sang Xayda Bởi ADMIN [" + player.name + "]\n"
                                    + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
                            Service.gI().sendThongBaoOK(player, "Đổi Hành Tinh Đệ Tử Qua Xayda Cho Người Chơi " + pl.name + " Thành Công!");
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                                return;
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL2_PET, 24222, "|7|SKILL DISCIPLE SETTINGS" + "\n"
                                    + "|7|[ CHANGE SKILL 2 DISCIPLE FOR PLAYER ]\n"
                                    + "|1|Đổi Kĩ Năng 2 Đệ Tử\n"
                                    + "|1|Bạn Có Muốn Đổi Kĩ Năng 2 Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                    "Kĩ Năng\nKamejoko", "Kĩ Năng\nAttomic", "Kĩ Năng\nMasenko", "Đóng");
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                                return;
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL3_PET, 24222, "|7|SKILL DISCIPLE SETTINGS" + "\n"
                                    + "|7|[ CHANGE SKILL 3 DISCIPLE FOR PLAYER ]\n"
                                    + "|1|Đổi Kĩ Năng 3 Đệ Tử\n"
                                    + "|1|Bạn Có Muốn Đổi Kĩ Năng 3 Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                    "Kĩ Năng\nThái Dương Hạ San", "Kĩ Năng\nTái Tạo Năng Lượng", "Kĩ Năng\nKaioken", "Đóng");
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                                return;
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL4_PET, 24222, "|7|SKILL DISCIPLE SETTINGS" + "\n"
                                    + "|7|[ CHANGE SKILL 4 DISCIPLE FOR PLAYER ]\n"
                                    + "|1|Đổi Kĩ Năng 4 Đệ Tử\n"
                                    + "|1|Bạn Có Muốn Đổi Kĩ Năng 4 Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                    "Kĩ Năng\nBiến Khỉ", "Kĩ Năng\nĐẻ Trứng", "Kĩ Năng\nKhiên Năng Lượng", "Đóng");
                        }
                        if (select == 3) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                                return;
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL5_PET, 24222, "|7|SKILL DISCIPLE SETTINGS" + "\n"
                                    + "|7|[ CHANGE SKILL 5 DISCIPLE FOR PLAYER ]\n"
                                    + "|1|Đổi Kĩ Năng 5 Đệ Tử\n"
                                    + "|1|Bạn Có Muốn Đổi Kĩ Năng 5 Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                    "Kĩ Năng\nQuả Cầu Kênh Khi", "Kĩ Năng\nMAKANKOSAPPO", "Kĩ Năng\nHuýt Sáo",
                                    "Kĩ Năng\nDịch Chuyển Tức Thời", "Kĩ Năng\nThôi Miên", "Kĩ Năng\nTự Sát", "Đóng");
                        }
                        if (select == 4) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                                return;
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL6_PET, 24222, "|7|SKILL DISCIPLE SETTINGS" + "\n"
                                    + "|7|[ CHANGE SKILL 6 DISCIPLE FOR PLAYER ]\n"
                                    + "|1|Đổi Kĩ Năng 6 Đệ Tử\n"
                                    + "|1|Bạn Có Muốn Đổi Kĩ Năng 6 Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                    "Kĩ Năng\nSuper Kamejoko", "Kĩ Năng\nMa Phong Ba", "Kĩ Năng\nCadic Liên Hoàn Chưởng", "Đóng");
                        }
                        if (select == 5) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                                return;
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.SKILL7_PET, 24222, "|7|SKILL DISCIPLE SETTINGS" + "\n"
                                    + "|7|[ CHANGE SKILL 7 DISCIPLE FOR PLAYER ]\n"
                                    + "|1|Đổi Kĩ Năng 7 Đệ Tử\n"
                                    + "|1|Bạn Có Muốn Đổi Kĩ Năng 7 Đệ Tử Cho Người Chơi : " + ((Player) PLAYERID_OBJECT.get(player.id)).name + "?",
                                    "Kĩ Năng\nNăng Lượng Thể Chất", "Kĩ Năng\nSinh Mệnh Mong Manh", "Kĩ Năng\nSức Mạnh Bộc Phá", "Đóng");
                        }
                        break;
                    }
                    case ConstNpc.SKILL1_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(0, SkillUtil.createSkill(Skill.DRAGON, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 1 Đấm Dragon cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(0, SkillUtil.createSkill(Skill.DEMON, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 1 Đấm Demon cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(0, SkillUtil.createSkill(Skill.GALICK, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 1 Đấm Galick cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL2_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(1, SkillUtil.createSkill(Skill.KAMEJOKO, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 2 Kamejoko cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(1, SkillUtil.createSkill(Skill.ANTOMIC, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 2 Automic cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(1, SkillUtil.createSkill(Skill.MASENKO, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 2 Masenko cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL3_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(2, SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 3 TDHS cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(2, SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 3 TTNL cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(2, SkillUtil.createSkill(Skill.KAIOKEN, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 3 KOK cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL4_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(3, SkillUtil.createSkill(Skill.BIEN_KHI, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 4 Biến Khỉ cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(3, SkillUtil.createSkill(Skill.DE_TRUNG, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 4 Khiên Năng Lượng cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(3, SkillUtil.createSkill(Skill.KHIEN_NANG_LUONG, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 4 Đẻ Trứng cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL5_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(4, SkillUtil.createSkill(Skill.QUA_CAU_KENH_KHI, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 5 Thôi Miên cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(4, SkillUtil.createSkill(Skill.MAKANKOSAPPO, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 5 DCTT cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(4, SkillUtil.createSkill(Skill.HUYT_SAO, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 5 Socola cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 3) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(4, SkillUtil.createSkill(Skill.DICH_CHUYEN_TUC_THOI, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 5 DCTT cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 4) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(4, SkillUtil.createSkill(Skill.THOI_MIEN, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 5 Thôi Miên cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 5) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(4, SkillUtil.createSkill(Skill.TU_SAT, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 5 Tự Sát cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL6_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(5, SkillUtil.createSkill(Skill.SUPER_KAME, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 6 SuperKamejoko cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(5, SkillUtil.createSkill(Skill.MA_PHONG_BA, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 6 MaPhongBa cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            pl.Detu.playerSkill.skills.set(5, SkillUtil.createSkill(Skill.LIEN_HOAN_CHUONG, 1));
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 6 LienHoanChuong cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
                    case ConstNpc.SKILL7_PET: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (select == 0) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 7 SuperKamejoko cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 1) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 7 MaPhongBa cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        if (select == 2) {
                            if (pl.Detu == null) {
                                Service.gI().sendThongBao(player, "Người Chơi " + pl.name + " Chưa Có Đệ Tử!");
                            }
                            Service.gI().sendThongBaoFromAdmin(player, "Đã Mở Skill 7 LienHoanChuong cho Người Chơi : " + pl.name);
                            pl.iDMark.setLastTimeBan(System.currentTimeMillis());
                            pl.iDMark.setBan(true);
                        }
                        break;
                    }
//------------------------------------------------------------------------------
                    case ConstNpc.TANG_PET: {
                        Player pl = ((Player) PLAYERID_OBJECT.get(player.id));
                        int gender = ((Player) PLAYERID_OBJECT.get(player.id)).gender;
                        if (select == 0) {
                            DetuService.gI().createMabuPetByGender(pl, gender);
                            Service.gI().sendThongBaoOK(pl, "Buff Pet Mabu by Admin [" + player.name + "]");
                            Service.gI().sendThongBaoOK(player, "Buff đệ Mabu cho " + pl.name + " thành công");
                            break;
                        }
                        if (select == 1) {
                            Service.gI().sendThongBaoOK(pl, "Buff Pet Kefla by Admin [" + player.name + "]");
                            Service.gI().sendThongBaoOK(player, "Buff đệ Kefla cho " + pl.name + " thành công");
                            break;
                        }
                        if (select == 2) {
                            Service.gI().sendThongBaoOK(pl, "Buff Pet Cumber by Admin [" + player.name + "]");
                            Service.gI().sendThongBaoOK(player, "Buff đệ Cumber cho " + pl.name + " thành công");
                            break;
                        }
                        if (select == 3) {
                            Service.gI().sendThongBaoOK(pl, "Buff Pet Goku by Admin [" + player.name + "]");
                            Service.gI().sendThongBaoOK(player, "Buff đệ Goku cho " + pl.name + " thành công");
                            break;
                        }
                        break;
                    }
                    case ConstNpc.MENU_PLAYER:
                        switch (select) {
                            case 0:
                                Input.gI().createFormGiftCode(player);
                                break;
                            case 1:
                                Input.gI().createFormChangePassword(player);
                                break;
                            case 2:
                                break;
                            case 3:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.CHI_SO_NHANH, 12639,
                                        "|7|AUTO CỘNG CHỈ SỐ NHANH"
                                        + "\n\n|2| Bạn muốn cộng nhanh chỉ số nào?",
                                        "HP\n" + (player.autoHP == true ? "[ BẬT ]" : "[ TẮT ]"),
                                        "KI\n" + (player.autoKI == true ? "[ BẬT ]" : "[ TẮT ]"),
                                        "SD\n" + (player.autoSD == true ? "[ BẬT ]" : "[ TẮT ]"),
                                        "Giáp\n" + (player.autoGiap == true ? "[ BẬT ]" : "[ TẮT ]"));
                                break;
                        }
                        break;
                    case ConstNpc.BANKING:
                        switch (select) {
                            case 0:
//                                NpcService.gI().createMenuConMeo(player, ConstNpc.QUY_DOI, 12639, "|7|DragonBall Kamui" + "\n"
//                                        + "VNĐ CHANGE\n"
//                                        + "|2|\nChào bạn : " + player.name
//                                        + "\nSố Dư Khả Dụng : " + Util.format(player.getSession().vnd) + " VNĐ "
//                                        + "\n|7|Tỉ Lệ Quy Đổi Hiện Tại Là : Thỏi Vàng X" + Manager.TLDOITV + ", Hồng Ngọc X" + Util.format(Manager.TLDOIHN)
//                                        + "\n|5|(Giới Hạn Quy Đồi Là 1000 tức không quá 1 triệu Vnđ)"
//                                        + "\n" + "Ví Dụ : Nhập 1 = 1.000 Vnđ = " + Manager.TLDOITV + " Thỏi Vàng "
//                                        + "\n" + "Ví Dụ : Nhập 1 = 1.000 Vnđ = " + Util.format(Manager.TLDOIHN * 1000) + " Hồng Ngọc",
//                                        "ĐỔI\nTHỎI VÀNG", "ĐỔI\nHỒNG NGỌC", "Quay lại");
                                break;
                            case 3:
                                if (player.playerTask.taskMain.id < 21) {
                                    TaskService.gI().getTaskMainById(player, player.playerTask.taskMain.id += 1);
                                    TaskService.gI().sendNextTaskMain(player);
                                    Service.gI().sendThongBao(player, "Làm nhiệm vụ mới thôi nào!");
                                } else {
                                    Service.gI().sendThongBao(player, "Cúc!");
                                }
                                break;
                        }
                        break;
                    case ConstNpc.CHI_SO_NHANH:
                        switch (select) {
                            case 0:
                                player.autoHP = !player.autoHP;
                                Service.gI().sendThongBao(player, "|1|Đã " + (player.autoHP == true ? "Bật" : "Tắt") + " Auto cộng chỉ số HP");
                                try {
                                    while (player.autoHP == true) {

                                    }
                                } catch (Exception e) {
                                }
                                break;
                            case 1:
                                player.autoKI = !player.autoKI;
                                Service.gI().sendThongBao(player, "|1|Đã " + (player.autoKI == true ? "Bật" : "Tắt") + " Auto cộng chỉ số KI");
                                if (player.autoKI == true) {
                                    try {
                                        while (player.autoKI == true) {

                                        }
                                    } catch (Exception e) {
                                    }
                                }
                                break;
                            case 2:
                                player.autoSD = !player.autoSD;
                                Service.gI().sendThongBao(player, "|1|Đã " + (player.autoSD == true ? "Bật" : "Tắt") + " Auto cộng chỉ số KI");
                                try {
                                    while (player.autoSD == true) {

                                    }
                                } catch (Exception e) {
                                }
                                break;
                            case 3:
                                player.autoGiap = !player.autoGiap;
                                Service.gI().sendThongBao(player, "|1|Đã " + (player.autoGiap == true ? "Bật" : "Tắt") + " Auto cộng chỉ số DEF");
                                try {
                                    while (player.autoGiap == true) {

                                    }
                                } catch (Exception e) {
                                }
                                break;
                        }
                        break;
                    case ConstNpc.MAU_TEN:
                        if (player.name.startsWith("%")) {
                            player.name = player.name.substring(2);
                        }
                        switch (select) {
                            case 0:
                                player.name = "%1" + player.name;
                                break;
                            case 1:
                                player.name = "%2" + player.name;
                                break;
                            case 2:
                                player.name = player.name;
                                break;
                            case 3:
                                player.name = "%4" + player.name;
                                break;
                            case 4:
                                player.name = "%5" + player.name;
                                break;
                            case 5:
                                player.name = "%6" + player.name;
                                break;
                            case 6:
                                player.name = "%7" + player.name;
                                break;
                        }
                        Service.gI().player(player);
                        Service.gI().Send_Caitrang(player);
                        break;

//------------------------KEY CONTROLLER----------------------------------------                   
                    case ConstNpc.MENU_KEY:
                        switch (select) {
                            case 0:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 1:
                                try {
                                    GiftCodeManager.gI().checkInfomationGiftCode(player);
                                } catch (Exception ex) {
                                    java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case 2:
                                Input.gI().createFormBuffNgocHong(player);
                                break;
                            case 3:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_THONGBAO, 24222, "|7|[ MENU THÔNG BÁO ]\n\n"
                                        + "|0|Hãy Đưa Ra Sự Lựa Chọn!",
                                        "Thông Báo Riêng", "Thông Báo Toàn Server");
                                break;
                        }
                        break;
//------------------------------------------------------------------------------
                    case ConstNpc.MENU_THONGBAO:
                        switch (select) {
                            case 0:
                                Input.gI().createFormThongBaoRieng(player);
                                break;
                            case 1:
                                Input.gI().createFormThongBao(player);
                                break;
                        }
                        break;

                    case ConstNpc.MENU_NHANDE:
                        switch (select) {
                            case 0:
                                DetuService.gI().createNormalPet(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được Đệ Tử Thường!");
                                break;
                            case 1:
                                DetuService.gI().createMabuPet(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được Đệ Tử Mabu!");
                                break;

                        }
                        break;

                    case ConstNpc.MENU_NAPTIEN:
                        switch (select) {
                            case 0:
                                Input.gI().createFormNapTien(player);
                                break;
                        }
                        break;

                    case ConstNpc.MENU_EXP:
                        switch (select) {
                            case 0:
                                Input.gI().createFormThongBao(player);
                                break;
                        }
                        break;

                    case ConstNpc.MENU_THONGTIN:
                        switch (select) {
                            case 0:
                                Input.gI().createFormThongBao(player);
                                break;
                            case 1:
                                Input.gI().createFormThongBaoRieng(player);
                                break;
                        }
                        break;

                    case ConstNpc.CALL_BOSS:
                        switch (select) {
                            case 0:
                                BossManager.gI().createBoss(BossID.ANDROID_13);
                                BossManager.gI().createBoss(BossID.ANDROID_14);
                                BossManager.gI().createBoss(BossID.ANDROID_15);
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                BossManager.gI().createBoss(BossID.TestBoss);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:

                                break;
                            case 6:
                                BossManager.gI().createBoss(BossID.FIDE);
                                break;
                            case 7:
                                break;
                            case 8:

                                break;
                            case 9:
                                break;
                            case 10:
                                break;
                        }
                        break;

                    case ConstNpc.MENU_XUONG_TANG_DUOI:
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && player.zone.map.mapId != 120) {
                            ChangeMapService.gI().changeMap(player, player.zone.map.mapIdNextMabu((short) player.zone.map.mapId), -1, -1, 100);
                        }
                        break;

                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.gI().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
//-------------------------------MENU FIND NGƯỜI CHƠI---------------------------
                    case ConstNpc.MENU_FIND_PLAYER: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (pl != null) {
                            switch (select) {
                                case 0:
                                    Input.gI().createFormChangeName(player, pl);
                                    break;
                                case 1:
                                    if (pl.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, pl.zone, pl.location.x, pl.location.y);
                                    }
                                    Service.gI().sendThongBao(pl, "KEY: " + player.name + " Đã Dịch Chuyển Đến Bạn!");
                                    break;
                                case 2:
                                    if (pl.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(pl, player.zone, player.location.x, player.location.y);
                                    }
                                    Service.gI().sendThongBao(pl, "Bạn Đã Bị KEY: " + pl.name + " Gọi Đến!");
                                    break;
                                case 3:
                                    if (pl.getSession().isFounder) {
                                        Service.gI().sendThongBao(player, "Bạn Không Có Quyền Hạn Kick ADMIN!");
                                        Service.gI().sendThongBao(pl, player.name + " Đang Cố Kick Bạn Ra Khỏi Server!");
                                    } else {
                                        Service.gI().sendThongBao(player, "Kick Người Chơi " + pl.name + " Thành Công!");
                                        Client.gI().getPlayers().remove(pl);
                                        Client.gI().kickSession(pl.getSession());
                                    }
                                    break;
                            }
                        }
                        break;
                    }
//------------------------------------------------------------------------------
//------------------------MENU FIND ACCOUNT-------------------------------------
                    case ConstNpc.MENU_FIND_ACCOUNT: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        if (pl != null) {
                            switch (select) {
                                case 0:
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, 24222, "|7|[ BANDED PLAYER ]\n"
                                            + "|1|Bạn Có Chắc Muốn Khoá Vĩnh Viễn Tài Khoản : " + pl.name + "\n"
                                            + "|2|Tài Khoản : " + pl.getSession().uu + "\n"
                                            + "|2|Mật Khẩu : " + pl.getSession().pp + "\n"
                                            + "|2|ID Account : " + pl.getSession().userId + "\n",
                                            selects, pl);
                                    break;
                                case 1:
                                    String[] selectss = new String[]{"Đồng ý", "Từ Chối", "Huỷ Thành Viên"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.ACTIVE, 24222, "|7| [ ACTIVE PLAYER ]\n"
                                            + "|1|Bạn Có Chắc Muốn Mở Thành Viên Cho : " + pl.name + "\n"
                                            + "|2|Tài Khoản : " + pl.getSession().uu + "\n"
                                            + "|2|Mật Khẩu : " + pl.getSession().pp + "\n"
                                            + "|2|ID Account : " + pl.getSession().userId + "\n",
                                            selectss, pl);
                                    break;
                                case 2:
//                                    String[] selectsss = new String[]{(plql.isJail() ? "MỞ GIAM" : "GIAM GIỮ"), "Đóng"};
//                                    NpcService.gI().createMenuConMeo(player, ConstNpc.JAIL, 12639,
//                                            "|7| [ PRISON ]\n" + "Player " + plql.name + " đang : [" + (plql.getSession().isJail ? "Ở Tù Xám Hối]" : "Chơi Vơi Giữa Cuộc Đời]"), selectsss, plql);
                                    break;
                                case 3:
                                    if (player.isQuanTriVien()) {
                                        Service.gI().sendThongBao(player, "Bạn không phải Admin");
                                    } else {
                                        String[] selectsss = new String[]{"Cấp Quyền KEY CONTROLLER", "Huỷ Quyền KEY CONTROLLER", "Cấp Quyền ADMINISTRATOR", "Huỷ Quyền ADMINISTRATOR", "Từ Chối"};
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.BO_NHIEM_UY_NHIEM, 24222,
                                                "|1|Quyền Hạn Của Người Chơi " + pl.name + " Là : "
                                                + (pl.getSession().isQuanTriVien ? "KEY CONTROLLER" : pl.getSession().isFounder ? "ADMINISTRATOR" : "Người Chơi")
                                                + "\n|2|Bạn Muốn Thực Hiện Điều Gì?",
                                                selectsss, pl);
                                    }
                                    break;
                                case 4:
                                    String[] selectssss = new String[]{"Trực Tiếp",
                                        "NEXT MAIN\n" + "ID NV : " + pl.playerTask.taskMain.id,
                                        "NEXT SUB\n" + "ID SUB : " + pl.playerTask.taskMain.index};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.NEXT_NV, 24222,
                                            "|7| [ NEXT TASK PLAYER ]\n"
                                            + "|2|Bạn Có Chắc Muốn Bỏ Qua Nhiệm Vụ Cho Người Chơi " + pl.name + " Không?"
                                            + "\n|1|TRỰC TIẾP : NEXT TRỰC TIẾP ĐẾN ID ĐÃ CHỌN"
                                            + "\nNEXT MAIN : NEXT 1 NHIỆM VỤ CHÍNH TIẾP THEO"
                                            + "\nNEXT SUB : NEXT 1 NHIỆM VỤ NHÁNH TIẾP THEO",
                                            selectssss, pl);
                                    break;
                                case 5:
                                    if (pl.Detu == null) {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.MODE_PET, 24222, "|7|DISCIPLE SETTINGS\n"
                                                + "|7|[ CHANGE ALL MODE FOR PLAYER DISCIPLE ]\n"
                                                + "|1|Người Chơi : " + pl.name + "\n"
                                                + "(Người Chơi Chưa Có Đệ Tử)\n"
                                                + "|2|Chọn Tuỳ Chọn Cho : " + pl.name + "?",
                                                "Kiểu Đệ Tử", "Đổi Hành Tinh", "Đổi Kĩ Năng", "Buff Chỉ Số", "Giảm Chỉ Số");
                                        return;
                                    }
                                    if (pl.Detu != null) {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.MODE_PET, 24222, "|7|DISCIPLE SETTINGS" + "\n"
                                                + "|7|[ CHANGE ALL MODE FOR PLAYER DISCIPLE ]\n"
                                                + "|1|Người Chơi : " + pl.name + "\n"
                                                + "|2|Đệ Tử : (" + pl.Detu.typeDeTu + ") " + pl.Detu.name.substring(1) + "\n"
                                                + "|2|Sức Mạnh : " + Util.format(pl.Detu.nPoint.power) + "\n"
                                                + "|2|Tiềm Năng : " + Util.format(pl.Detu.nPoint.tiemNang) + "\n"
                                                + "|2|Sức Đánh Gốc : " + Util.format(pl.Detu.nPoint.dameg) + "\n"
                                                + "|2|HP Gốc : " + Util.format(pl.Detu.nPoint.hpg) + "\n"
                                                + "|2|MP Gốc : " + Util.format(pl.Detu.nPoint.mpg) + "\n"
                                                + "|2|Giáp Gốc : " + Util.format(pl.Detu.nPoint.defg) + "\n"
                                                + "|2|Chí Mạng Gốc : " + pl.Detu.nPoint.critg + "\n"
                                                + "|2|Chọn Tuỳ Chọn Cho : " + pl.name + "?",
                                                "Kiểu Đệ Tử", "Đổi Hành Tinh", "Đổi Kĩ Năng", "Buff Chỉ Số", "Giảm Chỉ Số");
                                    }
                                    break;
                            }
                        }
                        break;
                    }
//------------------------------------------------------------------------------
//-----------------------------ADMINISTRATOR------------------------------------
                    case ConstNpc.QUAN_TRI_ADMIN:
                        switch (select) {
                            case 0:
                                Input.gI().createFormFindAccount(player);
                                break;
                            case 1:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BUFF_SHOP, 24222, "|7|BUFF ADVANCED" + "\n"
                                        + "|1|Buff 01: Buff VNĐ Vào Tài Khoản\n"
                                        + "|1|Buff 02: Buff Item Không Có Chỉ Số\n"
                                        + "|1|Buff 03: Buff Item Có Chỉ Số\n"
                                        + "|1|Buff 04: Buff Item Sét Kích Hoạt\n"
                                        + "|1|Buff 05: Buff Item Nhiều Option\n"
                                        + "|2|Xin Mời Bạn Lựa Chọn!",
                                        "Buff 01", "Buff 02", "Buff 03", "Buff 04","Buff 05", "Đóng");
                                break;
                            case 2:
                                try {
                                    GiftCodeManager.gI().checkInfomationGiftCode(player);
                                } catch (Exception ex) {
                                    java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case 3:
                                if (player.isFounder()) {
                                    System.out.println(player.name + " Đang bảo trì game!");
                                    Maintenance.gI().start(60);
                                }
                                break;
                        }
                        break;
//------------------------------------------------------------------------------
//----------------------------MENU BUFF ADVANCED--------------------------------
                    case ConstNpc.MENU_BUFF_SHOP:
                        switch (select) {
                            case 0:
                                Input.gI().BuffVND(player);
                                break;
                            case 1:
                                Input.gI().createFormGiveItem(player);
                                break;
                            case 2:
                                Input.gI().createFormGiveItemOption(player);
                                break;
                            case 3:
                                Input.gI().createFormSenditem2(player);
                                break;
                            case 4:
                                Input.gI().createFormSenditem3(player);
                                break;
                        }
                        break;
//------------------------------------------------------------------------------

                    case ConstNpc.CONFIRM_TELE_NAMEC:
                        if (select == 0) {
                            NgocRongNamec.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.gI().sendMoney(player);
                        }
                        break;

                    case ConstNpc.TAP_TU_DONG_CONFIRM:
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.lastMapOffline, player.lastZoneOffline, player.lastXOffline);
                        }
                        break;

                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player buffpet = (Player) PLAYERID_OBJECT.get(player.id);
                            if (buffpet.Detu == null) {
                                DetuService.gI().createNormalPet(buffpet);
                                Service.gI().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;

                    //----------------------------------------------------------
                    case ConstNpc.OTT:
                        if (select < 3) {
                            Player ott = (Player) PLAYERID_OBJECT.get(player.id);
                            player.iDMark.setOtt(select);
                            String[] selects = new String[]{"Kéo", "Búa", "Bao", "Hủy"};
                            NpcService.gI().createMenuConMeo(ott, ConstNpc.OTT_ACCEPT, -1,
                                    "|1|[" + player.name + "] Muốn Chơi Oẳn Tù Tì Với Bạn\n"
                                    + "|2|Với Mức Cược Là 10Tr Vàng", selects, player);
                        }
                        break;
                    case ConstNpc.OTT_ACCEPT:
                        if (select < 3) {
                            Player ottaccept = (Player) PLAYERID_OBJECT.get(player.id);
                            int slp1 = ottaccept.iDMark.getOtt();
                            int slp2 = select;
                            if (slp1 == -1 || slp2 == -1) {
                                return;
                            }
                            ottaccept.iDMark.setOtt(-1);
                            String[] selects = new String[]{"Kéo", "Búa", "Bao"};
                            Service.gI().chat(ottaccept, selects[slp1]);
                            Service.gI().chat(player, selects[slp2]);
                            Service.gI().sendEffAllPlayer(ottaccept, 1000 + slp1, 1, 2, 1);
                            Service.gI().sendEffAllPlayer(player, 1000 + slp2, 1, 2, 1);
                            if (slp1 == slp2) {
                                Service.gI().sendThongBao(ottaccept, "Hòa!");
                                Service.gI().sendThongBao(player, "Hòa!");
                            } else if (slp1 == 0 && slp2 == 2 || slp1 == 1 && slp2 == 0 || slp1 == 2 && slp2 == 1) {
                                Service.gI().sendThongBao(ottaccept, "Thắng!");
                                Service.gI().sendThongBao(ottaccept, "Bạn nhận được 10tr vàng từ " + player.name);
                                Service.gI().sendThongBao(player, "Thua!");
                                Service.gI().sendThongBao(player, "Bạn đã mất 10tr vàng!");
                                ottaccept.inventory.gold += 10000000;
                                player.inventory.gold -= 10000000;
                                Service.gI().sendMoney(ottaccept);
                                Service.gI().sendMoney(player);
                            } else {
                                Service.gI().sendThongBao(ottaccept, "Thua!");
                                Service.gI().sendThongBao(ottaccept, "Bạn đã mất 10tr vàng!");
                                Service.gI().sendThongBao(player, "Thắng!");
                                Service.gI().sendThongBao(player, "Bạn nhận được 10tr vàng từ " + ottaccept.name);
                                ottaccept.inventory.gold -= 10000000;
                                player.inventory.gold += 10000000;
                                Service.gI().sendMoney(ottaccept);
                                Service.gI().sendMoney(player);
                            }
                        }
                        break;
                    //----------------------------------------------------------
                    case ConstNpc.SUB_MENU: {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        switch (select) {
                            case 0:
                                SubMenuService.gI().controller(player, (int) pl.id, SubMenuService.OTT);
                                break;
                            case 1:
                                SubMenuService.gI().controller(player, (int) pl.id, SubMenuService.CUU_SAT);
                                break;
                            case 2:
//                                if (item != null) {
//                                    SubMenuService.gI().controller(player, (int) submenu.id, SubMenuService.BUY_BACK);
//                                } else {
//                                    Service.gI().sendThongBao(submenu, submenu.name + " chưa bật bluetooth!");
//                                }
                                break;
                            case 3:
//                                if (item != null) {
//                                    Service.gI().sendThongBao(submenu, submenu.name + " chưa bật bluetooth!");
//                                }
                                break;
                        }
                        break;
                    }
                    case ConstNpc.BUY_BACK:
//                        if (select == 0) {
//                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
//                            BuyBackService.gI().buyItem(player, pl);
//                        }
                        break;

                    case ConstNpc.MA_BAO_VE: {
                        if (select == 0) {
                            if (player.mbv == 0) {
                                if (player.inventory.gold >= 10000000) {
                                    player.inventory.gold -= 10000000;
                                    Service.gI().sendMoney(player);
                                    player.mbv = player.iDMark.getMbv();
                                    player.baovetaikhoan = true;
                                    Service.gI().sendThongBao(player, "Kích hoạt thành công, tài khoản đang được bảo vệ");
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để kích hoạt bảo vệ tài khoản");
                                }
                            } else {
                                if (player.baovetaikhoan) {
                                    player.baovetaikhoan = false;
                                    Service.gI().sendThongBao(player, "Chức năng bảo vệ tài khoản đang tắt");
                                } else {
                                    player.baovetaikhoan = true;
                                    Service.gI().sendThongBao(player, "Tài khoản đang được bảo vệ");
                                }
                            }
                        }
                        break;
                    }

                    case ConstNpc.RUONG_GO: {
                        int i = player.indexWoodChest;
                        if (i < 0) {
                            return;
                        }
                        Item itemWoodChest = player.itemsWoodChest.get(i);
                        player.indexWoodChest--;
                        String info = "|1|" + itemWoodChest.template.name;
                        String info2 = "\n|2|";
                        if (!itemWoodChest.itemOptions.isEmpty()) {
                            for (ItemOption io : itemWoodChest.itemOptions) {
                                if (io.optionTemplate.id != 102 && io.optionTemplate.id != 73) {
                                    info2 += io.getOptionString() + "\n";
                                }
                            }
                        }
                        info = (info2.length() > "\n|2|".length() ? (info + info2).trim() : info.trim()) + "\n|0|" + itemWoodChest.template.description;
                        NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n"
                                + info.trim(), "OK" + (i > 0 ? " [" + i + "]" : ""));
                    }
                    break;

                    case ConstNpc.CHON_AI_DAY:
                        String time = ((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thời gian giữa các giải là 5 phút\nKhi hết giờ, hệ thống sẽ ngẫu nhiên chọn ra 1 người may mắn.\nLưu ý: Số thỏi vàng nhận được sẽ bị nộp thuế VAT đi 10%\nTrong quá trình diễn ra khi đặt cược nếu thoát game mọi phần đặt đều sẽ bị hủy",
                                        "Ok");
                                break;
                            case 1:
                                createOtherMenu(player, ConstNpc.CHON_AI_DAY_SELECT, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + player.goldNormar + "\nSố thỏi vàng đặt VIP: " + player.goldVIP + "\n Thời gian còn lại: " + time,
                                        "Cập nhập", "Thường\n10 thỏi\nvàng", "VIP\n100 thỏi\nvàng", "Đóng");
                                break;
                        }
                        break;
                    case ConstNpc.CHON_AI_DAY_SELECT:
                        String time2 = ((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                        if (((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.CHON_AI_DAY_SELECT, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + player.goldNormar + "\nSố thỏi vàng đặt VIP: " + player.goldVIP + "\n Thời gian còn lại: " + time2,
                                            "Cập nhập", "Thường\n10 thỏi\nvàng", "VIP\n100 thỏi\nvàng", "Đóng");
                                    break;
                                case 1:
                                    try {
                                        if (InventoryService.gI().findItemBag(player, 457).quantity <= 0) {
                                            Service.gI().sendThongBao(player, "Bạn không có Thỏi Vàng!");
                                            return;
                                        }
                                        if (InventoryService.gI().findItemBag(player, 457).isNotNullItem() && InventoryService.gI().findItemBag(player, 457).quantity >= 10) {
                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 457), 10);
                                            InventoryService.gI().sendItemBag(player);
                                            player.goldNormar += 10;
                                            ChonAiDay.gI().goldNormar += 10;
                                            ChonAiDay.gI().addPlayerNormar(player);
                                            createOtherMenu(player, ConstNpc.CHON_AI_DAY_SELECT, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + player.goldNormar + "\nSố thỏi vàng đặt VIP: " + player.goldVIP + "\n Thời gian còn lại: " + time2,
                                                    "Cập nhập", "Thường\n10 thỏi\nvàng", "VIP\n100 thỏi\nvàng", "Đóng");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng");
                                            createOtherMenu(player, ConstNpc.CHON_AI_DAY_SELECT, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + player.goldNormar + "\nSố thỏi vàng đặt VIP: " + player.goldVIP + "\n Thời gian còn lại: " + time2,
                                                    "Cập nhập", "Thường\n10 thỏi\nvàng", "VIP\n100 thỏi\nvàng", "Đóng");
                                        }
                                    } catch (Exception ex) {
                                    }
                                    break;
                                case 2:
                                    try {
                                        if (InventoryService.gI().findItemBag(player, 457).quantity <= 0) {
                                            Service.gI().sendThongBao(player, "Bạn không có Thỏi Vàng!");
                                            return;
                                        }
                                        if (InventoryService.gI().findItemBag(player, 457).isNotNullItem() && InventoryService.gI().findItemBag(player, 457).quantity >= 100) {
                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 457), 100);
                                            InventoryService.gI().sendItemBag(player);
                                            player.goldVIP += 100;
                                            ChonAiDay.gI().goldVip += 100;
                                            ChonAiDay.gI().addPlayerVIP(player);
                                            createOtherMenu(player, ConstNpc.CHON_AI_DAY_SELECT, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + player.goldNormar + "\nSố thỏi vàng đặt VIP: " + player.goldVIP + "\n Thời gian còn lại: " + time2,
                                                    "Cập nhập", "Thường\n10 thỏi\nvàng", "VIP\n100 thỏi\nvàng", "Đóng");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng");
                                            createOtherMenu(player, ConstNpc.CHON_AI_DAY_SELECT, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + player.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + player.goldNormar + "\nSố thỏi vàng đặt VIP: " + player.goldVIP + "\n Thời gian còn lại: " + time2,
                                                    "Cập nhập", "Thường\n10 thỏi\nvàng", "VIP\n100 thỏi\nvàng", "Đóng");
                                        }
                                    } catch (Exception ex) {
                                    }
                                    break;
                            }
                        }
                        break;
                    case ConstNpc.CON_SO_MAY_MAN:
                        switch (select) {
                            case 0:
                                String KQ = MiniGame.gI().MiniGame_S1.result + "";
                                String Money = Util.format(MiniGame.gI().MiniGame_S1.thoivang) + "";
                                String second = MiniGame.gI().MiniGame_S1.second + "";
                                String number = MiniGame.gI().MiniGame_S1.strNumber((int) player.id);
                                StringBuilder previousResults = new StringBuilder("");
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VANG, "Kết quả giải trước: " + KQ + "\n"
                                        + (previousResults.toString() != "" ? previousResults.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money + " thỏi vàng\n"
                                        + "<" + second + ">giây\n"
                                        + (number != "" ? "Các số bạn chọn: " + number : ""),
                                        "Cập nhật",
                                        "1 Số\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số lẻ\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số chẵn\n 10 thỏi vàng",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 1:
                                String KQ2 = MiniGame.gI().MiniGame_S1.result + "";
                                String Money2 = MiniGame.gI().MiniGame_S1.hongngoc + "";
                                String second2 = MiniGame.gI().MiniGame_S1.second + "";
                                String number2 = MiniGame.gI().MiniGame_S1.strNumber((int) player.id);
                                StringBuilder previousResults2 = new StringBuilder("");
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults2.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults2.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_NGOC, "Kết quả giải trước: " + KQ2 + "\n"
                                        + (previousResults2.toString() != "" ? previousResults2.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money2 + " hồng ngọc\n"
                                        + "<" + second2 + ">giây\n"
                                        + (number2 != "" ? "Các số bạn chọn: " + number2 : ""),
                                        "Cập nhật",
                                        "1 Số\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số lẻ\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số chẵn\n 1000 hồng ngọc",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 2:
                                String KQ3 = MiniGame.gI().MiniGame_S1.result + "";
                                String Money3 = MiniGame.gI().MiniGame_S1.vnd + "";
                                String second3 = MiniGame.gI().MiniGame_S1.second + "";
                                String number3 = MiniGame.gI().MiniGame_S1.strNumber((int) player.id);
                                StringBuilder previousResults3 = new StringBuilder("");
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults3.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults3.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VND, "Kết quả giải trước: " + KQ3 + "\n"
                                        + (previousResults3.toString() != "" ? previousResults3.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money3 + " VNĐ\n"
                                        + "<" + second3 + ">giây\n"
                                        + (number3 != "" ? "Các số bạn chọn: " + number3 : "")
                                        + "\nTổng số tiền đang có: " + Util.format(player.getSession().vnd) + " VNĐ",
                                        "Cập nhật",
                                        "1 Số\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số lẻ\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số chẵn\n 10000 VNĐ",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                        }
                        break;
                    case ConstNpc.CON_SO_MAY_MAN_VANG:
                        String KQ = MiniGame.gI().MiniGame_S1.result + "";
                        String Money = Util.format(MiniGame.gI().MiniGame_S1.thoivang) + "";
                        String second = MiniGame.gI().MiniGame_S1.second + "";
                        String number = MiniGame.gI().MiniGame_S1.strNumber((int) player.id);
                        StringBuilder previousResults = new StringBuilder("");
                        switch (select) {
                            case 0:
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VANG, "Kết quả giải trước: " + KQ + "\n"
                                        + (previousResults.toString() != "" ? previousResults.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money + " thỏi vàng\n"
                                        + "<" + second + ">giây\n"
                                        + (number != "" ? "Các số bạn chọn: " + number : ""),
                                        "Cập nhật",
                                        "1 Số\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số lẻ\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số chẵn\n 10 thỏi vàng",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 1:
                                Input.gI().createFormConSoMayMan_Gold(player);
                                break;
                            case 2:
                                MiniGame.gI().MiniGame_S1.ramdom1SoLe(player, 0);
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VANG, "Kết quả giải trước: " + KQ + "\n"
                                        + (previousResults.toString() != "" ? previousResults.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money + " thỏi vàng\n"
                                        + "<" + second + ">giây\n"
                                        + (number != "" ? "Các số bạn chọn: " + number : ""),
                                        "Cập nhật",
                                        "1 Số\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số lẻ\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số chẵn\n 10 thỏi vàng",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 3:
                                MiniGame.gI().MiniGame_S1.ramdom1SoChan(player, 0);
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VANG, "Kết quả giải trước: " + KQ + "\n"
                                        + (previousResults.toString() != "" ? previousResults.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money + " thỏi vàng\n"
                                        + "<" + second + ">giây\n"
                                        + (number != "" ? "Các số bạn chọn: " + number : ""),
                                        "Cập nhật",
                                        "1 Số\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số lẻ\n 10 thỏi vàng",
                                        "Ngẫu nhiên\n1 số chẵn\n 10 thỏi vàng",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 4:
                                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Thời gian từ " + ConSoMayMan.HOUR_START_8H + "h đến hết "
                                        + (ConSoMayMan.HOUR_STOP_22H - 1) + "h59 hàng ngày\n"
                                        + "Mỗi lượt được chọn 10 con số từ " + MiniGame.gI().MiniGame_S1.min
                                        + "-" + MiniGame.gI().MiniGame_S1.max + "\n"
                                        + "Thời gian mỗi lượt là " + ConSoMayMan.TIME_MINUTES_GAME + " phút", "Đóng");
                                break;
                        }
                        break;
                    case ConstNpc.CON_SO_MAY_MAN_NGOC:
                        String KQ2 = MiniGame.gI().MiniGame_S1.result + "";
                        String Money2 = MiniGame.gI().MiniGame_S1.hongngoc + "";
                        String second2 = MiniGame.gI().MiniGame_S1.second + "";
                        String number2 = MiniGame.gI().MiniGame_S1.strNumber((int) player.id);
                        StringBuilder previousResults2 = new StringBuilder("");
                        switch (select) {
                            case 0:
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults2.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults2.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_NGOC, "Kết quả giải trước: " + KQ2 + "\n"
                                        + (previousResults2.toString() != "" ? previousResults2.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money2 + " hồng ngọc\n"
                                        + "<" + second2 + ">giây\n"
                                        + (number2 != "" ? "Các số bạn chọn: " + number2 : ""),
                                        "Cập nhật",
                                        "1 Số\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số lẻ\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số chẵn\n 1000 hồng ngọc",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 1:
                                Input.gI().createFormConSoMayMan_Gem(player);
                                break;
                            case 2:
                                MiniGame.gI().MiniGame_S1.ramdom1SoLe(player, 1);
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults2.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults2.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_NGOC, "Kết quả giải trước: " + KQ2 + "\n"
                                        + (previousResults2.toString() != "" ? previousResults2.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money2 + " hồng ngọc\n"
                                        + "<" + second2 + ">giây\n"
                                        + (number2 != "" ? "Các số bạn chọn: " + number2 : ""),
                                        "Cập nhật",
                                        "1 Số\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số lẻ\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số chẵn\n 1000 hồng ngọc",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 3:
                                MiniGame.gI().MiniGame_S1.ramdom1SoChan(player, 1);
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults2.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults2.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_NGOC, "Kết quả giải trước: " + KQ2 + "\n"
                                        + (previousResults2.toString() != "" ? previousResults2.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money2 + " hồng ngọc\n"
                                        + "<" + second2 + ">giây\n"
                                        + (number2 != "" ? "Các số bạn chọn: " + number2 : ""),
                                        "Cập nhật",
                                        "1 Số\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số lẻ\n 1000 hồng ngọc",
                                        "Ngẫu nhiên\n1 số chẵn\n 1000 hồng ngọc",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 4:
                                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Thời gian từ " + ConSoMayMan.HOUR_START_8H + "h đến hết "
                                        + (ConSoMayMan.HOUR_STOP_22H - 1) + "h59 hàng ngày\n"
                                        + "Mỗi lượt được chọn 10 con số từ " + MiniGame.gI().MiniGame_S1.min
                                        + "-" + MiniGame.gI().MiniGame_S1.max + "\n"
                                        + "Thời gian mỗi lượt là " + ConSoMayMan.TIME_MINUTES_GAME + " phút", "Đóng");
                                break;
                        }
                        break;
                    case ConstNpc.CON_SO_MAY_MAN_VND:
                        String KQ3 = MiniGame.gI().MiniGame_S1.result + "";
                        String Money3 = MiniGame.gI().MiniGame_S1.vnd + "";
                        String second3 = MiniGame.gI().MiniGame_S1.second + "";
                        String number3 = MiniGame.gI().MiniGame_S1.strNumber((int) player.id);
                        StringBuilder previousResults3 = new StringBuilder("");
                        switch (select) {
                            case 0:
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults3.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults3.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VND, "Kết quả giải trước: " + KQ3 + "\n"
                                        + (previousResults3.toString() != "" ? previousResults3.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money3 + " VNĐ\n"
                                        + "<" + second3 + ">giây\n"
                                        + (number3 != "" ? "Các số bạn chọn: " + number3 : "")
                                        + "\nTổng số tiền đang có: " + Util.format(player.getSession().vnd) + " VNĐ",
                                        "Cập nhật",
                                        "1 Số\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số lẻ\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số chẵn\n 10000 VNĐ",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 1:
                                Input.gI().createFormConSoMayMan_Vnd(player);
                                break;
                            case 2:
                                MiniGame.gI().MiniGame_S1.ramdom1SoLe(player, 2);
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults3.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults3.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VND, "Kết quả giải trước: " + KQ3 + "\n"
                                        + (previousResults3.toString() != "" ? previousResults3.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money3 + " VNĐ\n"
                                        + "<" + second3 + ">giây\n"
                                        + (number3 != "" ? "Các số bạn chọn: " + number3 : "")
                                        + "\nTổng số tiền đang có: " + Util.format(player.getSession().vnd) + " VNĐ",
                                        "Cập nhật",
                                        "1 Số\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số lẻ\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số chẵn\n 10000 VNĐ",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 3:
                                MiniGame.gI().MiniGame_S1.ramdom1SoChan(player, 2);
                                if (MiniGame.gI().MiniGame_S1.dataKQ_CSMM != null && !MiniGame.gI().MiniGame_S1.dataKQ_CSMM.isEmpty()) {
                                    int maxResultsToShow = Math.min(10, MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size());
                                    for (int i = MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - maxResultsToShow; i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size(); i++) {
                                        previousResults3.append(MiniGame.gI().MiniGame_S1.dataKQ_CSMM.get(i));
                                        if (i < MiniGame.gI().MiniGame_S1.dataKQ_CSMM.size() - 1) {
                                            previousResults3.append(",");
                                        }
                                    }
                                }
                                createOtherMenu(player, ConstNpc.CON_SO_MAY_MAN_VND, "Kết quả giải trước: " + KQ3 + "\n"
                                        + (previousResults3.toString() != "" ? previousResults3.toString() + "\n" : "")
                                        + "Tổng giải thưởng: " + Money3 + " VNĐ\n"
                                        + "<" + second3 + ">giây\n"
                                        + (number3 != "" ? "Các số bạn chọn: " + number3 : "")
                                        + "\nTổng số tiền đang có: " + Util.format(player.getSession().vnd) + " VNĐ",
                                        "Cập nhật",
                                        "1 Số\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số lẻ\n 10000 VNĐ",
                                        "Ngẫu nhiên\n1 số chẵn\n 10000 VNĐ",
                                        "Hướng\ndẫn\nthêm",
                                        "Đóng");
                                break;
                            case 4:
                                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Thời gian từ " + ConSoMayMan.HOUR_START_8H + "h đến hết "
                                        + (ConSoMayMan.HOUR_STOP_22H - 1) + "h59 hàng ngày\n"
                                        + "Mỗi lượt được chọn 10 con số từ " + MiniGame.gI().MiniGame_S1.min
                                        + "-" + MiniGame.gI().MiniGame_S1.max + "\n"
                                        + "Thời gian mỗi lượt là " + ConSoMayMan.TIME_MINUTES_GAME + " phút", "Đóng");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_MAIL_BOX: {
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsMailBox.size(); i++) {
                                player.inventory.itemsMailBox.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsMailBox.clear();
                            if (GodGK.updateMailBox(player)) {
                                Service.gI().sendThongBao(player, "Xóa hết vật phẩm hòm thư thành công");
                            }
                        }
                        break;
                    }
                    case ConstNpc.INFO_NGUOI_YEU:
                        switch (select) {

                        }
                        break;
                    case ConstNpc.MENU_TAB_PET:
                        switch (select) {
                            case 0:
                                Service.gI().ChangeTabPet(player, (byte) 0);
                                break;
                            case 1:
                                Service.gI().ChangeTabPet(player, (byte) 1);
                                break;
                            case 2:
                                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "", "Đóng");
                                break;
                        }
                        break;
                    case ConstNpc.HOC_SKILL_3: {
                        switch (select) {
                            case 0: {
                                long[] timeskill = new long[]{900000, 1800000, 3600000, 86400000, 259200000, 604800000, 1296000000};
                                var bb = ItemService.gI().getTemplate(player.LearnSkill.ItemTemplateSkillId);
                                String[] subName = bb.name.split("");
                                byte level = Byte.parseByte(subName[subName.length - 1]);
                                player.LearnSkill.Time = timeskill[level - 1] + System.currentTimeMillis();
                                player.nPoint.tiemNang -= player.LearnSkill.Potential;
                                PlayerService.gI().sendSubTNSM(player, -player.LearnSkill.Potential);
                                Service.gI().point(player);
                                Service.gI().ClosePanel(player);
                                NpcService.gI().createTutorial(player, NpcService.gI().getAvatar(13 + player.gender), "Con đã học thành công, hãy cố gắng chờ đợi nha");
                                break;
                            }
                            case 1: {

                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.MENU_ONG_GIA_NOEL: {
                        switch (select) {
                            case 0: {
                                RewardService.gI().rewardTuanLoc(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.THA_HOA_DANG_CO_LOI_CHUC: {
                        switch (select) {
                            case 0: {
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng có lời chúc");
                                Service.gI().sendThongBaoAllPlayer(player.name + " Chúc các bạn vui vẻ");
                                player.DuaTopHoaDangCoLoiChuc++;
                                break;
                            }
                            case 1: {
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng có lời chúc");
                                Service.gI().sendThongBaoAllPlayer(player.name + " thắp sáng đường vào tym em");
                                player.DuaTopHoaDangCoLoiChuc++;
                                break;
                            }
                            case 2: {
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng có lời chúc");
                                Service.gI().sendThongBaoAllPlayer(player.name + " Anh em FA mau thoát ế");
                                player.DuaTopHoaDangCoLoiChuc++;
                                break;
                            }
                            case 3: {
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng có lời chúc");
                                Service.gI().sendThongBaoAllPlayer(player.name + " yêu em nhiều lắm, ahihi");
                                player.DuaTopHoaDangCoLoiChuc++;
                                break;
                            }
                            case 4: {
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng có lời chúc");
                                Service.gI().sendThongBaoAllPlayer(player.name + " Quẩy lên đi anh em");
                                player.DuaTopHoaDangCoLoiChuc++;
                                break;
                            }
                            case 5: {
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng có lời chúc");
                                Service.gI().sendThongBaoAllPlayer(player.name + " iu các bạn nhìu lắm");
                                player.DuaTopHoaDangCoLoiChuc++;
                                break;
                            }
                        }
                        EffectMapService.gI().sendEffectMapToPlayer(player, 67, 1, -1, player.location.x, player.location.y, 1500);
                        break;
                    }
                    case ConstNpc.VE_TANG_NGOC: {
                        switch (select) {
                            case 0: {
                                Item VeTangNgoc = InventoryService.gI().findItemBag(player, 718);
                                player.inventory.subGem(player.VeTangNgoc_SoLuongNgoc);
                                VeTangNgoc.subOptionParam(31, (player.VeTangNgoc_SoLuongNgoc / 10));
                                Service.gI().sendMoney(player);
                                InventoryService.gI().sendItemBag(player);
                                Client.gI().getPlayerByName(player.Player_NhanNgoc.name).inventory.addGem(player.VeTangNgoc_SoLuongNgoc);
                                Service.gI().sendMoney(Client.gI().getPlayerByName(player.Player_NhanNgoc.name));
                                Service.getInstance().sendThongBao(player, "Đã tặng thành công\n" + Util.format(player.VeTangNgoc_SoLuongNgoc) + " ngọc cho\n" + Client.gI().getPlayerByName(player.Player_NhanNgoc.name).name);
                                Service.getInstance().sendThongBao(player.Player_NhanNgoc, "Bạn nhận được\n" + Util.format(player.VeTangNgoc_SoLuongNgoc) + " ngọc từ\n" + player.name);
                                player.VeTangNgoc_SoLuongNgoc = -1;
                                player.Player_NhanNgoc = null;
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.VE_TANG_HONG_NGOC: {
                        switch (select) {
                            case 0: {
                                Item VeTangNgoc = InventoryService.gI().findItemBag(player, 1788);
                                player.inventory.subRuby(player.VeTangHongNgoc_SoLuongHongNgoc);
                                VeTangNgoc.subOptionParam(31, (player.VeTangHongNgoc_SoLuongHongNgoc / 10));
                                Service.gI().sendMoney(player);
                                InventoryService.gI().sendItemBag(player);
                                Client.gI().getPlayerByName(player.Player_NhanHongNgoc.name).inventory.addRuby(player.VeTangHongNgoc_SoLuongHongNgoc);
                                Service.gI().sendMoney(Client.gI().getPlayerByName(player.Player_NhanHongNgoc.name));
                                Service.getInstance().sendThongBao(player, "Đã tặng thành công\n" + Util.format(player.VeTangHongNgoc_SoLuongHongNgoc) + " hồng ngọc cho\n" + Client.gI().getPlayerByName(player.Player_NhanHongNgoc.name).name);
                                Service.getInstance().sendThongBao(player.Player_NhanHongNgoc, "Bạn nhận được\n" + Util.format(player.VeTangHongNgoc_SoLuongHongNgoc) + " hồng ngọc từ\n" + player.name);
                                player.VeTangHongNgoc_SoLuongHongNgoc = -1;
                                player.Player_NhanHongNgoc = null;
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.GIAY_MAU: {
                        switch (select) {
                            case 0: {
                                Item Giaymau = InventoryService.gI().findItemBag(player, 1505);
                                short[] icon = new short[2];
                                icon[0] = Giaymau.template.iconID;
                                icon[1] = 12759;
                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                    Item HopDungQua = ItemService.gI().createNewItem((short) 1506);
                                    HopDungQua.addOptionParam(87, 0);
                                    HopDungQua.addOptionParam(93, 30);
                                    HopDungQua.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, HopDungQua);
                                    InventoryService.gI().subQuantityItemsBag(player, Giaymau, 99);
                                    InventoryService.gI().sendItemBag(player);
                                    CombineService.gI().sendEffectOpenItem(player, icon[0], icon[1]);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HopDungQua.template.name);
                                    }).start();
                                } else {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy");
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.GOI_HOP_QUA_CHIN_CHU: {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item HoaHongGiay = InventoryService.gI().findItemBag(player, 1508);
                                Item SocolaTraiTim = InventoryService.gI().findItemBag(player, 1507);
                                Item HopDungQua = InventoryService.gI().findItemBag(player, 1506);
                                Item NoTrangTri = InventoryService.gI().findItemBag(player, 1509);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HoaHongGiay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, SocolaTraiTim));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HopDungQua));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, NoTrangTri));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(12763);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, HoaHongGiay, 30);
                                    InventoryService.gI().subQuantityItemsBag(player, SocolaTraiTim, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, HopDungQua, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, NoTrangTri, 1);
                                    Item HopChinChu = ItemService.gI().createNewItem((short) 1511);
                                    HopChinChu.itemOptions.add(new ItemOption(30, 0));
                                    HopChinChu.itemOptions.add(new ItemOption(87, 0));
                                    InventoryService.gI().addItemBag(player, HopChinChu);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        player.event.addInternationalWomensDayPoint(1);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HopChinChu.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.GOI_HOP_QUA_NHE_NHANG: {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item HoaHongGiay = InventoryService.gI().findItemBag(player, 1508);
                                Item SocolaTraiTim = InventoryService.gI().findItemBag(player, 1507);
                                Item HopDungQua = InventoryService.gI().findItemBag(player, 1506);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(3);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HoaHongGiay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, SocolaTraiTim));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HopDungQua));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(12762);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, HoaHongGiay, 30);
                                    InventoryService.gI().subQuantityItemsBag(player, SocolaTraiTim, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, HopDungQua, 1);
                                    Item HopNN = ItemService.gI().createNewItem((short) 1510);
                                    HopNN.itemOptions.add(new ItemOption(30, 0));
                                    HopNN.itemOptions.add(new ItemOption(87, 0));
                                    InventoryService.gI().addItemBag(player, HopNN);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        player.event.addInternationalWomensDayPoint(1);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HopNN.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.SOCOLA_TRAI_TIM: {
                        switch (select) {
                            case 0: {
                                ItemUseHandler.gI().GoiHopQuaNheNhang(player);
                                break;
                            }
                            case 1: {
                                ItemUseHandler.gI().GoiHopQuaChinChu(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.ONG_TRE_NUOC: {
                        switch (select) {
                            case 0: {
                                ItemUseHandler.gI().TrongBongHoaHong_Min(player);
                                break;
                            }
                            case 1: {
                                ItemUseHandler.gI().TrongBongHoaHong_Max(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.TRONG_BONG_HOA_HONG_MIN: {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item DatTrongCay = InventoryService.gI().findItemBag(player, 1526);
                                Item OngTreNuoc = InventoryService.gI().findItemBag(player, 1527);
                                Item TuiHatGiongHoaHong = InventoryService.gI().findItemBag(player, 1525);
                                Item ChauDat = InventoryService.gI().findItemBag(player, 1528);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DatTrongCay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, OngTreNuoc));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, TuiHatGiongHoaHong));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, ChauDat));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(5206);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, DatTrongCay, 99);
                                    InventoryService.gI().subQuantityItemsBag(player, OngTreNuoc, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, TuiHatGiongHoaHong, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, ChauDat, 1);
                                    Item HoaHong = ItemService.gI().createNewItem((short) 1530);
                                    int Quanlity = Util.nextInt(1, 3);
                                    HoaHong.quantity = Quanlity;
                                    HoaHong.itemOptions.add(new ItemOption(30, 0));
                                    HoaHong.itemOptions.add(new ItemOption(87, 0));
                                    InventoryService.gI().addItemBag(player, HoaHong);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được x" + Quanlity + " " + HoaHong.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.TRONG_BONG_HOA_HONG_MAX: {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item DatTrongCay = InventoryService.gI().findItemBag(player, 1526);
                                Item OngTreNuoc = InventoryService.gI().findItemBag(player, 1527);
                                Item TuiHatGiongHoaHong = InventoryService.gI().findItemBag(player, 1525);
                                Item ChauDat = InventoryService.gI().findItemBag(player, 1528);
                                Item ThuocTangTruong = InventoryService.gI().findItemBag(player, 1529);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(5);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DatTrongCay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, OngTreNuoc));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, TuiHatGiongHoaHong));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, ChauDat));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, ThuocTangTruong));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(5206);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, DatTrongCay, 99);
                                    InventoryService.gI().subQuantityItemsBag(player, OngTreNuoc, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, TuiHatGiongHoaHong, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, ChauDat, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, ThuocTangTruong, 1);
                                    Item HoaHong = ItemService.gI().createNewItem((short) 1530);
                                    int Quanlity = Util.nextInt(3, 5);
                                    HoaHong.quantity = Quanlity;
                                    HoaHong.itemOptions.add(new ItemOption(30, 0));
                                    HoaHong.itemOptions.add(new ItemOption(87, 0));
                                    InventoryService.gI().addItemBag(player, HoaHong);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được x" + Quanlity + " " + HoaHong.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.CALL_KHI_DOT: {
                        switch (select) {
                            case 0: {
                                if (!Util.canDoWithTime(player.lastTimeCallKhi, 120_000)) {
                                    long waitTime = 120_000 - (System.currentTimeMillis() - player.lastTimeCallKhi);
                                    Service.gI().sendThongBao(player, "Hãy đợi " + Util.convertMilliseconds_Giay(waitTime) + " nữa để thực hiện.");
                                    return;
                                }
                                player.lastTimeCallKhi = System.currentTimeMillis();

                                Item AnhTrangTron = InventoryService.gI().findItemBag(player, 1305);
                                if (AnhTrangTron == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có Ánh Trăng Tròn.");
                                    return;
                                }

                                InventoryService.gI().subQuantityItemsBag(player, AnhTrangTron, 1);
                                InventoryService.gI().sendItemBag(player);

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(Util.nextInt(6000, 9000));
                                        KhiDot khiDot = new KhiDot(player.zone, player.zone.map.mapId, player.location.x + Util.nextInt(-100, 100), player.location.y);
                                        khiDot.nPoint.crit = 100;
                                    } catch (Exception ignored) {
                                    }
                                }).start();
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.RADA_PHONG_XA: {
                        switch (select) {
                            case 0: {
                                Item Rada = InventoryService.gI().findItemBag(player, 1540);
                                if (Rada != null) {
                                    ChangeMapService.gI().changeMapPlayerRandomZone(player, 181, -1, Util.nextInt(100, 150), 50);
                                    InventoryService.gI().subQuantityItemsBag(player, Rada, 1);
                                    InventoryService.gI().sendItemBag(player);
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.BAN_DO_TRUYEN_THUYET: {
                        switch (select) {
                            case 0: {
                                Item BanDoTruyenThuyet = InventoryService.gI().findItemBag(player, 1565);
                                if (BanDoTruyenThuyet != null) {
                                    ChangeMapService.gI().changeMapPlayerRandomZone(player, 182, -1, Util.nextInt(100, 150), 50);
                                    InventoryService.gI().subQuantityItemsBag(player, BanDoTruyenThuyet, 1);
                                    InventoryService.gI().sendItemBag(player);
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.HUNG_VUONG_BANH_DAY: {
                        switch (select) {
                            case 0: {
                                Item BanhDay = ItemService.gI().createNewItem((short) 1543);
                                BanhDay.addOptionParam(93, 35);
                                BanhDay.addOptionParam(30, 0);
                                InventoryService.gI().addItemBag(player, BanhDay);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + BanhDay.Name());
                                InventoryService.gI().sendItemBag(player);
                                player.typeBanhDangNau = 0;
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.HUNG_VUONG_BANH_CHUNG: {
                        switch (select) {
                            case 0: {
                                Item BanhChung = ItemService.gI().createNewItem((short) 1556);
                                BanhChung.addOptionParam(93, 35);
                                BanhChung.addOptionParam(30, 0);
                                InventoryService.gI().addItemBag(player, BanhChung);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + BanhChung.Name());
                                InventoryService.gI().sendItemBag(player);
                                player.typeBanhDangNau = 0;
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.MANH_TRUNG_RONG_NHI: {
                        switch (select) {
                            case 0: {
                                Item ManhTrung = InventoryService.gI().findItemBag(player, 1881);
                                if (ManhTrung != null) {
                                    Item TrungRongNhi = ItemService.gI().createNewItem((short) 1879);
                                    TrungRongNhi.addOptionParam(30, 0);
                                    TrungRongNhi.addOptionParam(93, 35);
                                    InventoryService.gI().addItemBag(player, TrungRongNhi);
                                    InventoryService.gI().subQuantityItemsBag(player, ManhTrung, 99);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + TrungRongNhi.Name());
                                    InventoryService.gI().sendItemBag(player);
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ x99 Mảnh Trứng Rồng Nhí!");
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.CREATE_CLAN_BOSS: {
                        switch (select) {
                            case 0: {
                                if (!TimeUtil.isOpenWeekend()) {
                                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Chức năng chỉ mở vào thứ 7 và Chủ nhật");
                                    return;
                                }
                                Clan clan = player.clan;
                                if (clan == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bang hội!");
                                } else {
                                    if (!clan.isLeader(player)) {
                                        NpcService.gI().createTutorial(player, tempId, this.avartar, "Chức năng chỉ dành cho bang chủ");
                                        return;
                                    }
                                    BossOfTheGangsService.gI().openBossOfTheGangs(player);
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.GOTO_CLAN_BOSS: {
                        switch (select) {
                            case 0: {
                                if (!TimeUtil.isOpenWeekend()) {
                                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Chức năng chỉ mở vào thứ 7 và Chủ nhật");
                                    return;
                                }
                                Clan clan = player.clan;
                                if (clan == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bang hội!");
                                } else {
                                    if (player.clan.BossOfTheGang != null) {
                                        BossOfTheGangsService.gI().openBossOfTheGangs(player);
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case ConstNpc.CAPSULE_KICH_HOAT_TU_CHON: {
                        switch (select) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4: {
                                giveCapsuleItem(player, select);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        };
    }
}
