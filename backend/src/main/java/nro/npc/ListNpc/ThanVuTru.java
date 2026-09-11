package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import QuanLiBoss.BossID;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.Service;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import nro.boss.map.TrainingBoss.TrainningService;
import nro.map.SnakeWay.SnakeWayService;
import nro.npc.Npc;
import static nro.npc.NpcFactory.PLAYERID_OBJECT;
import nro.player.Player;

public class ThanVuTru extends Npc {

    public ThanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_9 == 0) {
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
                    createOtherMenu(player, ConstNpc.NHAN_LI_XI, message, "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                    return;
                }
            }
            if (this.mapId == 48) {
                switch (player.levelLuyenTap) {
                    case 4:
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Thượng đế đưa ngươi đến đây, chắc muốn ta dạy võ chứ gì\nBắt được con khỉ Bubbles rồi hãy tính",
                                player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", "Tập luyện\nvới\nBubbles", "Thách đấu\nBubbles", "Di chuyển");
                        break;
                    case 5:
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ta là Thần Vũ Trụ Phương Bắc cai quản khu vực bắc vũ trụ\nnếu thắng được ta, ngươi sẽ được đến\nLãnh Địa Kaio, nơi ở của Thần Linh",
                                player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", "Tập luyện\nvới\nThần Vũ Trụ", "Thách đấu\nThần Vũ Trụ", "Di chuyển");
                        break;
                    default:
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con mạnh nhất phía bắc vũ trụ này rồi đấy\nnhưng ngoài vũ trụ bao la kia vẫn có những kẻ mạnh hơn nhìu\ncon cần phải tập luyện để mạnh hơn nữa",
                                player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", "Tập luyện\nvới\nBubbles", "Tập luyện\nvới\nThần Vũ Trụ", "Di chuyển");
                        break;
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI) {
                    switch (select) {
                        case 1:
                            Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                            String[] chucTetMessages = {
                                "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                                "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                                "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                                "Năm mới phát tài phát lộc, vạn sự như ý nha",
                                "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                                "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                                "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                                "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                                "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                                "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                                "Tết đến cười thật nhiều, sống thật chill, vui hết mình"
                            };

                            String NpcChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            String PlayerChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            this.npcChat(player, NpcChat);
                            Service.gI().chat(player, PlayerChat);
                            player.NhanLiXiForNPC_9++;
                            if (Util.isTrue(60, 100)) {
                                Lixi.addOptionParam(30, 0);
                                Lixi.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, Lixi);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + Lixi.template.name);
                            } else {
                                Service.gI().sendThongBao(player, "(>_<)");
                            }
                            break;
                    }
                    return;
                }
            }
            if (this.mapId == 48) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            if (player.dangKyTapTuDong) {
                                player.dangKyTapTuDong = false;
                                NpcService.gI().createTutorial(player, tempId, avartar, "Con đã hủy thành công đăng ký tập tự động\ntừ giờ con muốn tập Offline hãy tự đến đây trước");
                                return;
                            }
                            this.createOtherMenu(player, 2001, "Đăng ký để mỗi khi Offline quá 30 phút, con sẽ được tự động luyện tập với tốc độ 1280 sức mạnh mỗi phút",
                                    "Hướng\ndẫn\nthêm", "Đồng ý\n1 ngọc\nmỗi lần", "Không\nđồng ý");
                            break;
                        case 1:
                            switch (player.levelLuyenTap) {
                                case 5:
                                    this.createOtherMenu(player, 2002, "Con có chắc muốn tập luyện ?\nTập luyện với ta sẽ tăng 640 sức mạnh mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                                default:
                                    this.createOtherMenu(player, 2002, "Con có chắc muốn tập luyện ?\nTập luyện với Khỉ Bubbles sẽ tăng 320 sức mạnh mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                            }
                            break;
                        case 2:
                            switch (player.levelLuyenTap) {
                                case 4:
                                    this.createOtherMenu(player, 2003, "Con có chắc muốn thách đấu ?\nNếu thắng Khỉ Bubbles sẽ được tập với ta, tăng 640 sức mạnh mỗi phút",
                                            "Đồng ý\ngiao đấu", "Không\nđồng ý");
                                    break;
                                case 5:
                                    this.createOtherMenu(player, 2003, "Con có chắc muốn thách đấu ?\nNếu thắng được ta, con sẽ được học võ với người mạnh hơn ta để tăng đến 1280 sức mạnh mỗi phút",
                                            "Đồng ý\ngiao đấu", "Không\nđồng ý");
                                    break;
                                default:
                                    this.createOtherMenu(player, 2003, "Con có chắc muốn tập luyện ?\nTập luyện với ta sẽ tăng 640 sức mạnh mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                            }
                            break;
                        case 3:
                            this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                    "Ta sẽ đưa con đi",
                                    "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 2001) {
                    switch (select) {
                        case 0:
                            NpcService.gI().createTutorial(player, tempId, avartar, ConstNpc.TAP_TU_DONG);
                            break;
                        case 1:
                            player.mapIdDangTapTuDong = mapId;
                            player.dangKyTapTuDong = true;
                            NpcService.gI().createTutorial(player, tempId, avartar, "Từ giờ, quá 30 phút Offline con sẽ được tự động luyện tập");
                            break;
                    }

                } else if (player.iDMark.getIndexMenu() == 2002) {
                    switch (select) {
                        case 0:
                            switch (player.levelLuyenTap) {
                                case 5:
                                    TrainningService.gI().callBoss(player, BossID.THAN_VU_TRU, false);
                                    break;
                                default:
                                    TrainningService.gI().callBoss(player, BossID.KHI_BUBBLES, false);
                                    break;
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 2003) {
                    switch (select) {
                        case 0:
                            switch (player.levelLuyenTap) {
                                case 4:
                                    TrainningService.gI().callBoss(player, BossID.KHI_BUBBLES, true);
                                    break;
                                case 5:
                                    TrainningService.gI().callBoss(player, BossID.THAN_VU_TRU, true);
                                    break;
                                default:
                                    TrainningService.gI().callBoss(player, BossID.THAN_VU_TRU, false);
                                    break;
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                            break;
                        case 2:
                            if (player.clan != null) {
                                if (player.clan.ConDuongRanDoc != null) {
                                    this.createOtherMenu(player, 2,
                                            "Bang hội con đang ở con đường rắn độc cấp độ "
                                            + player.clan.ConDuongRanDoc.level + "\ncon có muốn đi cùng họ không? ("
                                            + TimeUtil.convertTimeNow(player.clan.ConDuongRanDoc.getLastTimeOpen())
                                            + " trước)",
                                            "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, 2,
                                            "Hãy mau trở về bằng con đường rắn độc\nbọn Xayda đã đến Trái Đất",
                                            "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                                }
                            } else {
                                NpcService.gI().createTutorial(player, tempId, this.avartar,
                                        "Hãy vào bang hội trước");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 2) {
                    switch (select) {
                        case 0:// Top bang hội
                            Service.gI().showTopClanCDRD(player);
                            break;
                        case 1:
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Bạn không có bang hội!");
                                return;
                            }
                            Service.getInstance().showMyTopClanCDRD(player);
                            break;
                        case 2:
                            if (player.clan == null) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar,
                                        "Hãy gia nhập bang hội!");
                                return;
                            }
                            if (player.clanMember.getNumDateFromJoinTimeToToday() < 3) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar,
                                        "Gia nhập bang hội trên 3 ngày mới được tham gia");
                                return;
                            }
                            if (player.clan.ConDuongRanDoc == null) {
                                Input.gI().createFormChooseLevelCDRD(player);
                            } else {
                                SnakeWayService.gI().openConDuongRanDoc(player, (byte) 0);
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 3) {
                    if (select == 0) {
                        if (player.clan.ConDuongRanDoc != null) {
                            SnakeWayService.gI().openConDuongRanDoc(player, (byte) 0);
                        } else {
                            SnakeWayService.gI().openConDuongRanDoc(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                        }
                    }
                }
            }
        }
    }

}
