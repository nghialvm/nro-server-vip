package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import QuanLiBoss.BossID;
import nro.services.NpcService;
import nro.services.OpenPowerService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import nro.boss.map.TrainingBoss.TrainningService;
import nro.npc.Npc;
import nro.player.NPoint;
import nro.player.Player;

public class ToSuKaio extends Npc {

    public ToSuKaio(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_12 == 0) {
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
            if (player.nPoint.limitPower > 4) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tập luyện với Tổ sư Kaio sẽ tăng " + Util.formatNumber(TrainningService.gI().getTnsmMoiPhut(player), FormatStyle.VIETNAMESE) + " sức mạnh mỗi phút, có thể tăng giảm tùy vào khả năng đánh quái của con",
                player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", "Đồng ý\nluyện tập", "Không\nđồng ý", "Nâng\nGiới hạn\nSức mạnh");
            } else {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tập luyện với Tổ sư Kaio sẽ tăng " + Util.formatNumber(TrainningService.gI().getTnsmMoiPhut(player), FormatStyle.VIETNAMESE) + " sức mạnh mỗi phút, có thể tăng giảm tùy vào khả năng đánh quái của con",
                player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", "Đồng ý\nluyện tập", "Không\nđồng ý");
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
                            player.NhanLiXiForNPC_12++;
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
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                    if (player.dangKyTapTuDong) {
                        player.dangKyTapTuDong = false;
                        NpcService.gI().createTutorial(player, tempId, avartar, "Con đã hủy thành công đăng ký tập tự động\ntừ giờ con muốn tập Offline hãy tự đến đây trước");
                        return;
                    }
                    this.createOtherMenu(player, 2001, "Đăng ký để mỗi khi Offline quá 30 phút, con sẽ được tự động luyện tập với tốc độ " + TrainningService.gI().getTnsmMoiPhut(player) + " sức mạnh mỗi phút",
                        "Hướng\ndẫn\nthêm", "Đồng ý\n1 ngọc\nmỗi lần", "Không\nđồng ý");
                    break;
                    case 1:
                        TrainningService.gI().callBoss(player, BossID.TO_SU_KAIO, false);
                        break;
                    case 3:
                        if (player.Detu != null) {
                            this.createOtherMenu(player, ConstNpc.GIOI_HAN_POWER,
                                    "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                                    "Bản thân", "Đệ tử", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.GIOI_HAN_POWER,
                                    "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                                    "Bản thân", "Từ chối");
                        }
                    default:
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
            } else if (player.iDMark.getIndexMenu() == ConstNpc.GIOI_HAN_POWER) {
                if (player.Detu != null) {
                    switch (select) {
                        case 0:
                            if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                this.createOtherMenu(player, ConstNpc.NANG_GIOI_HAN_POWER,
                                        "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh \n"
                                        + "của bản thân lên " + Util.formatNumber(player.nPoint.getPowerNextLimit(), FormatStyle.VIETNAMESE) + ".\n"
                                        + "Lưu ý: từ 40 tỷ trở lên sức mạnh của con sẽ tăng chậm đáng kể",
                                        "Nâng\ngiới hạn\nsức mạnh",
                                        "Nâng ngay\n" + 199 + " ngọc", "OK");
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Sức mạnh của con đã đạt tới giới hạn",
                                        "Đóng");
                            }
                            break;
                        case 1:
                            if (player.Detu != null) {
                                if (player.Detu.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.NANG_GIOI_HAN_POWER_PET,
                                            "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh \n"
                                            + "của đệ tử lên " + Util.formatNumber(player.Detu.nPoint.getPowerNextLimit(), FormatStyle.VIETNAMESE) + ".\n"
                                            + "Lưu ý: từ 40 tỷ trở lên sức mạnh của đệ tử sẽ tăng chậm đáng kể",
                                            "Nâng ngay\ncho đệ tử\n" + 199 + " ngọc", "OK");
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
                            if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                this.createOtherMenu(player, ConstNpc.NANG_GIOI_HAN_POWER,
                                        "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh \n"
                                        + "của bản thân lên " + Util.formatNumber(player.nPoint.getPowerNextLimit(), FormatStyle.VIETNAMESE) + ".\n"
                                        + "Lưu ý: từ 40 tỷ trở lên sức mạnh của con sẽ tăng chậm đáng kể",
                                        "Nâng\ngiới hạn\nsức mạnh",
                                        "Nâng ngay\n" + 199 + " ngọc", "OK");
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Sức mạnh của con đã đạt tới giới hạn",
                                        "Đóng");
                            }
                            break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NANG_GIOI_HAN_POWER) {
                if (player.nPoint.limitPower < 5) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                switch (select) {
                    case 0:
                        OpenPowerService.gI().openPowerBasic(player);
                        break;
                    case 1:
                        if (player.inventory.getGemAndRuby() >= 199) {
                            if (OpenPowerService.gI().openPowerSpeed(player)) {
                                player.inventory.subGemAndRuby(199);
                                Service.gI().sendMoney(player);
                            }
                        } else {
                            Service.gI().sendThongBao(player,
                                    "Bạn không đủ ngọc để mở, còn thiếu "
                                    + Util.formatNumber((199 - player.inventory.getGemAndRuby()), FormatStyle.VIETNAMESE) + " ngọc");
                        }
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NANG_GIOI_HAN_POWER_PET) {
                if (player.Detu.nPoint.limitPower < 5) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                if (select == 0) {
                    if (player.inventory.getGemAndRuby() >= 199) {
                        if (OpenPowerService.gI().openPowerSpeed(player.Detu)) {
                            player.inventory.subGemAndRuby(199);
                            Service.gI().sendMoney(player);
                        }
                    } else {
                        Service.gI().sendThongBao(player,
                                "Bạn không đủ ngọc để mở, còn thiếu "
                                + Util.formatNumber((199 - player.inventory.getGemAndRuby()), FormatStyle.VIETNAMESE) + " ngọc");
                    }
                }
            }
        }
    }
}
