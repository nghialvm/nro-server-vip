package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import QuanLiBoss.BossID;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import nro.boss.map.TrainingBoss.TrainningService;
import nro.npc.Npc;
import nro.player.Player;

public class ThanMeoKarin extends Npc {

    public ThanMeoKarin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_7 == 0) {
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
            if (TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                return;
            }
            if (this.mapId == 46) {
                if (player.winSTT && !Util.isAfterMidnight(player.lastTimeWinSTT)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy bình tĩnh..nghe ta nói đã\nMi chưa đủ sức hạ hắn đâu!\nThôi được rồi...chờ tí\nTa sẽ cho mi uống thuốc.\nThuốc 'Tăng lực siêu thần thủy'", "Đồng ý");
                    return;
                }
                if (player.clan != null && player.clan.ConDuongRanDoc != null && player.joinCDRD && player.clan.ConDuongRanDoc.allMobsDead && player.talkToThanMeo) {
                    Service.gI().sendThongBao(player, "Hãy mau bay xuống chân tháp Karin");
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy mau bay xuống chân tháp Karin", "OK");
                    return;
                }
                if (player.clan != null && player.clan.ConDuongRanDoc != null && player.joinCDRD && player.clan.ConDuongRanDoc.allMobsDead) {
                    player.talkToThuongDe = true;
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cầm lấy hai hạt đậu cuối cùng của ta đây\nCố giữ mình nhé " + player.name + "!", "Cám ơn\nsư phụ");
                    return;
                }
                switch (player.levelLuyenTap) {
                    case 0:
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Muốn chiến thắng Tàu Pảy Pảy phải đánh bại được ta đã", player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", 
                                "Nhiệm vụ", "Tập luyện\nvới\nThần Mèo", "Thách đấu\nThần Mèo");
                        break;
                    case 1:
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Từ giờ Yajirô sẽ luyện tập cùng ngươi. Yajirô đã từng lên đây tập luyện và bây giờ hắn mạnh hơn ta đấy", player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", 
                                "Tập luyện\nvới\nYajirô", "Thách đấu\nYajirô");
                        break;
                    default:
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con hãy bay theo cây Gậy Như Ý trên đỉnh tháp để đến Thần Điện gặp Thượng đế\nCon rất xứng đáng để làm đệ tử ông ấy.", player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", 
                                "Tập luyện\nvới\nThần Mèo", "Tập luyện\nvới\nYajirô");
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
                            player.NhanLiXiForNPC_7++;
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
            if (this.mapId == 46) {
                if (player.winSTT && !Util.isAfterMidnight(player.lastTimeWinSTT)) {
                    int itemId = player.nPoint.power < 500000000 ? 727 : 728;
                    Item item = ItemService.gI().createNewItem(((short) itemId));
                    item.addOptionParam(30, 0);
                    item.addOptionParam(93, 1);
                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                        player.callBossPocolo = false;
                        player.winSTT = false;
                        player.zoneSieuThanhThuy = null;
                        InventoryService.gI().addItemBag(player, item);
                        InventoryService.gI().sendItemBag(player);
                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
                    }
                    return;
                }
                if (player.clan != null && player.clan.ConDuongRanDoc != null && player.joinCDRD && player.clan.ConDuongRanDoc.allMobsDead) {
                    Service.gI().sendThongBao(player, "Hãy mau bay xuống chân tháp Karin");
                    if (!player.talkToThanMeo) {
                        player.nPoint.hp = player.nPoint.hpMax;
                        player.nPoint.mp = player.nPoint.mpMax;
                        Service.gI().sendInfoPlayerEatPea(player);
                    }
                    player.talkToThanMeo = true;
                    return;
                }

                //========================LUYEN TAP========================
                if (player.iDMark.isBaseMenu()) {
                    switch (player.levelLuyenTap) {
                        case 0:
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
                                    NpcService.gI().createTutorial(player, tempId, avartar, player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 2002, "Con có chắc muốn tập luyện ?\nTập luyện với ta sẽ tăng 20 sức mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                                case 3:
                                    this.createOtherMenu(player, 2003, "Con có chắc muốn thách đấu ?\nNếu thắng ta sẽ được tập luyện với Yajirô, tăng 40 sức mạnh mỗi phút",
                                            "Đồng ý\ngiao đấu", "Không\nđồng ý");
                                    break;
                            }
                            break;
                        case 1:
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
                                    this.createOtherMenu(player, 2002, "Con có chắc muốn tập luyện ?\nTập luyện với Yajirô sẽ tăng 40 sức mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 2003, "Con có chắc muốn thách đấu ?\nNếu thắng được Yajirô, con sẽ được học võ với người mạnh hơn để tăng đến 80 sức mạnh mỗi phút",
                                            "Đồng ý\ngiao đấu", "Không\nđồng ý");
                                    break;
                            }
                            break;
                        default: 
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
                                    this.createOtherMenu(player, 2002, "Con có chắc muốn tập luyện ?\nTập luyện với ta sẽ tăng 20 sức mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 2003, "Con có chắc muốn tập luyện ?\nTập luyện với Yajirô sẽ tăng 40 sức mỗi phút",
                                            "Đồng ý\nluyện tập", "Không\nđồng ý");
                                    break;
                            }
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
                    if (select == 0) {
                        switch (player.levelLuyenTap) {
                            case 0:
                                TrainningService.gI().callBoss(player, BossID.THAN_MEO_KARIN, false);
                                break;
                            case 1:
                                TrainningService.gI().callBoss(player, BossID.YAJIRO, false);
                                break;
                            default:
                                TrainningService.gI().callBoss(player, BossID.THAN_MEO_KARIN, false);
                                break;
                        }
                    }
                } else if (player.iDMark.getIndexMenu() == 2003) {
                    if (select == 0) {
                        switch (player.levelLuyenTap) {
                            case 0:
                                TrainningService.gI().callBoss(player, BossID.THAN_MEO_KARIN, true);
                                break;
                            case 1:
                                TrainningService.gI().callBoss(player, BossID.YAJIRO, true);
                                break;
                            default:
                                TrainningService.gI().callBoss(player, BossID.YAJIRO, false);
                                break;
                        }
                    }
                }
            }
        }
    }
}
