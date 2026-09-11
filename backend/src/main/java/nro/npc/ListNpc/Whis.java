package nro.npc.ListNpc;


import nro.inventory.InventoryService;
import QuanLiBoss.BossID;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.io.IOException;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.boss.map.TrainingBoss.TrainningService;
import nro.combine.CombineService;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.skill.Skill;
import nro.skill.SkillService;
import nro.top.TopService;

public class Whis extends Npc {

    public Whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_10 == 0) {
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
            switch (this.mapId) {
                case 48:
                    this.createOtherMenu(player, ConstNpc.WHIS_KAIO, "Đói bụng quá, ngươi hãy mang 99 thức ăn đến cho ta\n"
                            + "Nếu ta ăn ngon miệng ta sẽ tặng ngươi 1 món thần linh!\n"
                            + "Ngươi đang có " + player.event.getHakaiPoint() + " điểm huỷ diệt.",
                            "Tặng\n Thức Ăn", "Cửa Hàng", "Từ Chối");
                    break;
                case 155:
                    this.createOtherMenu(player, ConstNpc.WHIS_NGUC_TU, "Ngươi cần ta giúp gì?",
                            "Cửa Hàng", "Đổi\nChân\nThiên Tử", "Đổi\nDanh Hiệu\nThiên Tử", "Nâng Cấp\nVòng Chân\nThiên Tử", "Đóng");
                    break;
                case 154:
                    createOtherMenu(player, ConstNpc.WHIS_BILL,
                            "Thử đánh với ta xem nào.\nNgươi còn 1 lượt nữa cơ mà.",
                            "Nói chuyện", "Học\ntuyệt kỹ", "Top 100", "[LV:" + (player.traning.getTop() + 1) + "]", "Cửa hàng", "Đóng");
                    break;
                default:
                    break;
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
                            player.NhanLiXiForNPC_10++;
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
            if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_BILL) {
                Item BiKiepTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 1229);
                switch (select) {
                    case 0: {
                        if (this.mapId == 154) {
                            if (!player.setClothes.checkSetDes()) {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Ngươi hãy trang bị đủ 5 món trang bị Hủy Diệt rồi ta nói chuyện tiếp.",
                                        "OK");
                                return;
                            }
                            this.createOtherMenu(player, ConstNpc.WHIS_THIEN_SU,
                                    "Ta sẽ giúp ngươi chế tạo trang bị thiên sứ",
                                    "Chế tạo",
                                    "Từ chối");
                        }
                        break;
                    }
                    case 1: {
                        if (BiKiepTuyetKy != null) {
                            int idskill = Skill.MA_PHONG_BA;
                            if (player.gender == 0) {
                                idskill = Skill.SUPER_KAME;
                            } else if (player.gender == 2) {
                                idskill = Skill.LIEN_HOAN_CHUONG;
                            }
                            Skill curSkill = SkillUtil.getSkillbyId(player, idskill);
                            boolean checkskill = false;
                            if (curSkill == null) {
                                checkskill = true;

                            } else if (curSkill.point == 0) {
                                checkskill = true;
                            }
                            boolean duSachTuyetKy = false;
                            boolean duVang = false;
                            boolean duNgoc = false;
                            String nameSkill = player.gender == 0 ? "Super kamejoko" : player.gender == 1 ? "Ma phong ba" : "Ca đíc liên hoàn chưởng";
                            if (BiKiepTuyetKy.quantity >= 9999) {
                                duSachTuyetKy = true;
                            }
                            if (player.inventory.gold >= 2_000_000_000) {
                                duVang = true;
                            }
                            if (player.inventory.ruby >= 99) {
                                duNgoc = true;
                            }
                            if (checkskill) {
                                if (duSachTuyetKy && duVang && duNgoc) {
                                    this.createOtherMenu(player, ConstNpc.WHIS_SKILL,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " 1"
                                            + "\n|2|Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999\n"
                                            + "|2|Giá vàng: 2.000.000.000\n"
                                            + "|2|Giá hồng ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " 1"
                                            + "\n" + (duSachTuyetKy ? "|2|" : "|7|") + "Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999"
                                            + "\n" + (duVang ? "|2|" : "|7|") + "Giá vàng: 2.000.000.000"
                                            + "\n" + (duNgoc ? "|2|" : "|7|") + "Giá hồng ngọc: 99", "Từ chối");
                                }
                            } else {
                                if (duSachTuyetKy && duVang && duNgoc) {
                                    this.createOtherMenu(player, ConstNpc.WHIS_SKILL,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " " + (curSkill.point + 1)
                                            + "\n|2|Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999\n"
                                            + "|2|Giá vàng: 2.000.000.000\n"
                                            + "|2|Giá hồng ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " " + (curSkill.point + 1)
                                            + "\n" + (duSachTuyetKy ? "|2|" : "|7|") + "Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999"
                                            + "\n" + (duVang ? "|2|" : "|7|") + "Giá vàng: 2.000.000.000"
                                            + "\n" + (duNgoc ? "|2|" : "|7|") + "Giá hồng ngọc: 99", "Từ chối");
                                }
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không có bí kiếp tuyệt kĩ!");
                        }
                        break;
                    }
                    case 2:
                        TopService.showListTopWhis(player);
                        break;
                    case 3:
                        TrainningService.gI().callBoss(player, BossID.WHIS, false);
                        break;
                    case 4:
                        ShopService.gI().opendShop(player, "TORI_BOT", true);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_KAIO && this.mapId == 48) {
                if (select == 0) {
                    Item doAn = player.inventory.itemsBag.stream()
                            .filter(it -> it != null && it.template != null
                            && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665
                            || it.template.id == 666 || it.template.id == 667)
                            && it.quantity >= 1)
                            .findFirst().orElse(null);
                    if (doAn != null) {
                        InventoryService.gI().subQuantityItemsBag(player, doAn, 99);
                        InventoryService.gI().sendItemBag(player);

                        short[] itemIds = {15, 16, 17, 18, 19, 20, 457, 861};
                        Item itemReceived = null;
                        int random = Util.nextInt(1, 2);                    
                        
                        if (Util.isTrue(40, 100)) {
                            itemReceived = ItemService.gI().createNewItem(itemIds[Util.nextInt(0, itemIds.length - 1)]);
                        } else if (Util.isTrue(5, 100)) {
                            itemReceived = ItemService.gI().createNewItem((short) 1143);
                            itemReceived.addOptionParam(30, 0);
                        } else if (Util.isTrue(20, 100)) {
                            itemReceived = ItemService.gI().createNewItem((short) 2000);
                            itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                            itemReceived.addOptionParam(77, Util.nextInt(10, 15));
                            itemReceived.addOptionParam(103, Util.nextInt(10, 15));
                            itemReceived.addOptionParam(93, 1);
                            itemReceived.addOptionParam(210, Util.nextInt(1, 10));
                        } else if (Util.isTrue(20, 100)) {
                            itemReceived = ItemService.gI().createNewItem((short) 2001);
                            itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                            itemReceived.addOptionParam(77, Util.nextInt(10, 15));
                            itemReceived.addOptionParam(103, Util.nextInt(10, 15));
                            itemReceived.addOptionParam(93, 1);
                            itemReceived.addOptionParam(210, Util.nextInt(1, 10));
                        } else if (Util.isTrue(5, 100)) {
                            short[] itemAo = {555, 557, 559};
                            itemReceived = ItemService.gI().createNewItem(itemAo[Util.nextInt(0, itemAo.length - 1)]);
                            itemReceived.addOptionParam(47, Util.nextInt(950, 1100));
                            itemReceived.addOptionParam(21, 18);
                        } else if (Util.isTrue(5, 100)) {
                            short[] itemQuan = {556, 558, 560};
                            itemReceived = ItemService.gI().createNewItem(itemQuan[Util.nextInt(0, itemQuan.length - 1)]);
                            itemReceived.addOptionParam(22, Util.nextInt(45, 55));
                            itemReceived.addOptionParam(27, Util.nextInt(6500, 8200));
                            itemReceived.addOptionParam(21, 18);
                        } else if (Util.isTrue(5, 100)) {
                            short[] itemGang = {562, 564, 566};
                            itemReceived = ItemService.gI().createNewItem(itemGang[Util.nextInt(0, itemGang.length - 1)]);
                            itemReceived.addOptionParam(0, Util.nextInt(4000, 5000));
                            itemReceived.addOptionParam(21, 18);
                        } else if (Util.isTrue(5, 100)) {
                            short[] itemGiay = {563, 565, 567};
                            itemReceived = ItemService.gI().createNewItem(itemGiay[Util.nextInt(0, itemGiay.length - 1)]);
                            itemReceived.addOptionParam(23, Util.nextInt(45, 55));
                            itemReceived.addOptionParam(28, Util.nextInt(6500, 8200));
                            itemReceived.addOptionParam(21, 18);
                        } else if (Util.isTrue(5, 100)) {
                            itemReceived = ItemService.gI().createNewItem((short) 561);
                            itemReceived.addOptionParam(14, Util.nextInt(16, 17));
                            itemReceived.addOptionParam(21, 18);
                        } else {
                            if (Util.isTrue(20, 100)) {
                                player.event.addHakaiPoint(3);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 3 điểm huỷ diệt");
                            } else if (Util.isTrue(40, 100)) {
                                player.event.addHakaiPoint(2);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 2 điểm huỷ diệt");
                            } else {
                                player.event.addHakaiPoint(1);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 1 điểm huỷ diệt");
                            }
                        }

                        if (itemReceived != null) {
                            InventoryService.gI().addItemBag(player, itemReceived);
                            InventoryService.gI().sendItemBag(player);
                            Service.getInstance().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                        }

                    } else {
                        Service.gI().sendThongBao(player, "Bạn không có thức ăn!");
                        return;
                    }
                }
                if (select == 1) {
                    ShopService.gI().opendShop(player, "WHIS_KAIO", true);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_NGUC_TU && this.mapId == 155) {
                if (select == 0) {
                    ShopService.gI().opendShop(player, "WHIS_CHAN_MENH", true);
                }
                if (select == 1) {
                    this.createOtherMenu(player, ConstNpc.WHIS_DOI_CHAN_THIEN_TU,
                            "Ngươi đã sưu tầm được đủ nguyên liệu nào rồi?",
                            "Ma Quái", "Tinh Thể");
                }
                if (select == 2) {
                    this.createOtherMenu(player, ConstNpc.WHIS_DANH_HIEU,
                            "Danh hiệu Thiên Tử giúp tăng 5% HP, KI, SD, ngoài ra còn giúp tăng 3 lần tỷ lệ rơi Ma quái khi tiêu diệt quái.\n"
                            + "Ngươi có muốn nhận danh hiệu trong 60 phút không?\n"
                            + (player.LastTimeDanhHieu_ThienTu > 0 ? "Danh hiệu Thiên Tử : " + Util.formatCountdown(player.LastTimeDanhHieu_ThienTu, true, true, true) : ""),
                            "Nhận Ngay\n(500Tr vàng)", "Từ chối");
                }
                if (select == 3) {
                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_CHAN_MENH);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_DOI_CHAN_THIEN_TU && this.mapId == 155) {
                if (select == 0) {
                    this.createOtherMenu(player, ConstNpc.WHIS_MA_QUAI,
                            "Ngươi cần tìm đủ số lượng Ma Quái để có thể đổi lấy trang sức Chân Thiên Tử Tân Thủ với công thức tương ứng sau:\n"
                            + "Cần số lượng 99 Ma Quái để đổi trang sức 30 ngày\n"
                            + "Cần số lượng 999 Ma Quái để đổi trang sức Vĩnh Viễn\n"
                            + "Ngươi muốn đổi loại trang sức nào?",
                            "30 Ngày\n(-5 tỷ vàng)", "Vĩnh viễn\n(-9 tỷ vàng)", "Đóng");
                }
                if (select == 1) {
                    this.createOtherMenu(player, ConstNpc.WHIS_TINH_THE,
                            "Ngươi cần tìm đủ số lượng Tinh Thể để có thể đổi lấy trang sức Chân Thiên Tử Tân Thủ với công thức tương ứng sau:\n"
                            + "Cần số lượng 9 Tinh Thể để đổi trang sức 30 ngày\n"
                            + "Cần số lượng 99 Tinh Thể để đổi trang sức Vĩnh Viễn\n"
                            + "Ngươi muốn đổi loại trang sức nào?",
                            "30 Ngày\n(-5 tỷ vàng)", "Vĩnh viễn\n(-9 tỷ vàng)", "Đóng");
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_MA_QUAI && this.mapId == 155) {
                Item MaQuai = InventoryService.gI().findItemBag(player, 2012);
                Item VongChan = ItemService.gI().createNewItem((short) 2002);
                int[] List_Option = {0, 6, 7};
                int Random_Option = List_Option[Util.nextInt(0, List_Option.length - 1)];
                if (select == 0) {
                    if (player.inventory.gold < 5_000_000_000L) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng!");
                        return;
                    }
                    if (MaQuai != null && MaQuai.quantity < 99) {
                        Service.gI().sendThongBao(player, "Bạn không đủ ma quái!");
                        return;
                    }
                    if (MaQuai == null || MaQuai.quantity < 1) {
                        Service.gI().sendThongBao(player, "Bạn không có ma quái!");
                        return;
                    }
                    VongChan.addOptionParam(Random_Option, (Random_Option == 0 ? Util.nextInt(100, 2000) : Util.nextInt(2000, 30000)));
                    VongChan.addOptionParam(50, Util.nextInt(1, 5));
                    VongChan.addOptionParam(77, Util.nextInt(1, 5));
                    VongChan.addOptionParam(103, Util.nextInt(1, 5));
                    VongChan.addOptionParam(93, 30);
                    InventoryService.gI().addItemBag(player, VongChan);
                    InventoryService.gI().subQuantityItemsBag(player, MaQuai, 99);
                    Service.gI().sendThongBao(player, "Bạn nhận được Chân thiên tử tân thủ");
                    player.inventory.subGold(5_000_000_000L);
                    Service.gI().sendMoney(player);
                    InventoryService.gI().sendItemBag(player);
                }
                if (select == 1) {
                    if (player.inventory.gold < 9_000_000_000L) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng!");
                        return;
                    }
                    if (MaQuai != null && MaQuai.quantity < 999) {
                        Service.gI().sendThongBao(player, "Bạn không đủ ma quái!");
                        return;
                    }
                    if (MaQuai == null || MaQuai.quantity < 1) {
                        Service.gI().sendThongBao(player, "Bạn không có ma quái!");
                        return;
                    }
                    VongChan.addOptionParam(Random_Option, (Random_Option == 0 ? Util.nextInt(100, 2000) : Util.nextInt(2000, 30000)));
                    VongChan.addOptionParam(50, Util.nextInt(1, 5));
                    VongChan.addOptionParam(77, Util.nextInt(1, 5));
                    VongChan.addOptionParam(103, Util.nextInt(1, 5));
                    InventoryService.gI().addItemBag(player, VongChan);
                    InventoryService.gI().subQuantityItemsBag(player, MaQuai, 999);
                    Service.gI().sendThongBao(player, "Bạn nhận được Chân thiên tử tân thủ");
                    player.inventory.subGold(9_000_000_000L);
                    Service.gI().sendMoney(player);
                    InventoryService.gI().sendItemBag(player);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_TINH_THE && this.mapId == 155) {
                Item TinhThe = InventoryService.gI().findItemBag(player, 2011);
                Item VongChan = ItemService.gI().createNewItem((short) 2002);
                int[] List_Option = {0, 6, 7};
                int Random_Option = List_Option[Util.nextInt(0, List_Option.length - 1)];
                if (select == 0) {
                    if (player.inventory.gold < 5_000_000_000L) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng!");
                        return;
                    }
                    if (TinhThe != null && TinhThe.quantity < 9) {
                        Service.gI().sendThongBao(player, "Bạn không đủ tinh thể!");
                        return;
                    }
                    if (TinhThe == null || TinhThe.quantity < 1) {
                        Service.gI().sendThongBao(player, "Bạn không có tinh thể!");
                        return;
                    }
                    VongChan.addOptionParam(Random_Option, (Random_Option == 0 ? Util.nextInt(100, 2000) : Util.nextInt(2000, 30000)));
                    VongChan.addOptionParam(50, Util.nextInt(1, 5));
                    VongChan.addOptionParam(77, Util.nextInt(1, 5));
                    VongChan.addOptionParam(103, Util.nextInt(1, 5));
                    VongChan.addOptionParam(93, 30);
                    InventoryService.gI().addItemBag(player, VongChan);
                    InventoryService.gI().subQuantityItemsBag(player, TinhThe, 9);
                    Service.gI().sendThongBao(player, "Bạn nhận được Chân thiên tử tân thủ");
                    player.inventory.subGold(5_000_000_000L);
                    Service.gI().sendMoney(player);
                    InventoryService.gI().sendItemBag(player);
                }
                if (select == 1) {
                    if (player.inventory.gold < 9_000_000_000L) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng!");
                        return;
                    }
                    if (TinhThe != null && TinhThe.quantity < 99) {
                        Service.gI().sendThongBao(player, "Bạn không đủ tinh thể!");
                        return;
                    }
                    if (TinhThe == null || TinhThe.quantity < 1) {
                        Service.gI().sendThongBao(player, "Bạn không có tinh thể!");
                        return;
                    }
                    VongChan.addOptionParam(Random_Option, (Random_Option == 0 ? Util.nextInt(100, 2000) : Util.nextInt(2000, 30000)));
                    VongChan.addOptionParam(50, Util.nextInt(1, 5));
                    VongChan.addOptionParam(77, Util.nextInt(1, 5));
                    VongChan.addOptionParam(103, Util.nextInt(1, 5));
                    InventoryService.gI().addItemBag(player, VongChan);
                    InventoryService.gI().subQuantityItemsBag(player, TinhThe, 99);
                    Service.gI().sendThongBao(player, "Bạn nhận được Chân thiên tử tân thủ");
                    player.inventory.subGold(9_000_000_000L);
                    Service.gI().sendMoney(player);
                    InventoryService.gI().sendItemBag(player);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_DANH_HIEU && this.mapId == 155) {
                if (select == 0) {
                    if (player.inventory.gold < 500_000_000) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng!");
                        return;
                    }
                    if (player.LastTimeDanhHieu_ThienTu < System.currentTimeMillis()) {
                        player.LastTimeDanhHieu_ThienTu = System.currentTimeMillis() + (1000 * 60 * 60); // 60 phút
                    } else {
                        Service.gI().sendThongBao(player, "Bạn đang sử dụng danh hiệu này rồi!");
                        return;
                    }
                    player.isUseDanhHieu_ThienTu = true;
                    Service.gI().sendDanhHieu(player, 0);
                    player.inventory.subGold(500_000_000);
                    Service.gI().sendMoney(player);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Bạn nhận được 60 phút danh hiệu Thiên Tử.");
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_THIEN_SU) {
                switch (select) {
                    case 0:
                        CombineService.gI().openTabCombine(player, CombineService.CHE_TAO_TRANG_BI_THIEN_SU);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                switch (player.combine.typeCombine) {
                    case CombineService.CHE_TAO_TRANG_BI_THIEN_SU: {
                        CombineService.gI().startCombine(player);
                        break;
                    }
                    case CombineService.NANG_CAP_CHAN_MENH: {
                        switch (select) {
                            case 0:
                                CombineService.gI().startCombine(player);
                                break;
                            case 1:
                                CombineService.gI().startCombine(player, 10);
                                break;
                            case 2:
                                CombineService.gI().startCombine(player, 30);
                                break;
                            case 3:
                                CombineService.gI().startCombine(player, 70);
                                break;
                        }
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.WHIS_SKILL) {
                switch (select) {
                    case 0: {
                        Item BiKiepTuyetKy = InventoryService.gI().findItemBag(player, 1229);
                        if (BiKiepTuyetKy != null && player.inventory.gold >= 2_000_000_000 && player.inventory.ruby > 99 && player.nPoint.power >= 60000000000L) {
                            int idskill = Skill.MA_PHONG_BA;
                            int iconSkill = 11194;
                            if (player.gender == 0) {
                                idskill = Skill.SUPER_KAME;
                                iconSkill = 11162;
                            } else if (player.gender == 2) {
                                idskill = Skill.LIEN_HOAN_CHUONG;
                                iconSkill = 11193;
                            }
                            Skill curSkill = SkillUtil.getSkillbyId(player, idskill);
                            boolean checkskill = false;
                            if (curSkill == null) {
                                checkskill = true;
                            } else if (curSkill.point == 0) {
                                checkskill = true;
                            }
                            if (checkskill) {
                                if (BiKiepTuyetKy.quantity >= 9999) {
                                    try {
                                        int trubk;
                                        String msg;
                                        String msg2;
                                        trubk = 9999;
                                        msg = "Học skill thành công!";
                                        msg2 = "Chúc mừng con nhé!";
                                        switch (player.gender) {
                                            case 0:
                                                SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                                                break;
                                            case 2:
                                                SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
                                                break;
                                            default:
                                                SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
                                                break;
                                        }
                                        Message msgg;
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(0);
                                        msgg.writer().writeUTF("Skill 9");
                                        msgg.writer().writeUTF("MaiTienDung");
                                        msgg.writer().writeShort(tempId);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(1);
                                        msgg.writer().writeByte(1);
                                        msgg.writer().writeByte(InventoryService.gI().getIndexItemBag(player, BiKiepTuyetKy));
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(7);
                                        msgg.writer().writeShort(iconSkill);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        this.npcChat(player, msg2);
                                        Service.gI().sendThongBao(player, msg);
                                        InventoryService.gI().subQuantityItemsBag(player, BiKiepTuyetKy, trubk);
                                        player.inventory.gold -= 2_000_000_000;
                                        player.inventory.ruby -= 99;
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, msg);
                                    } catch (IOException e) {
                                    }
                                } else {
                                    int sosach = 9999 - BiKiepTuyetKy.quantity;
                                    Service.gI().sendThongBao(player, "Ngươi còn thiếu " + sosach + " bí kíp nữa.\nHãy tìm đủ rồi đến gặp ta.");
                                }
                            } else {
                                if (BiKiepTuyetKy.quantity >= 999) {
                                    if (curSkill.currLevel < 1000) {
                                        npcChat(player, "Ngươi chưa luyện skill đến mức thành thạo. Luyện thêm đi.");
                                    } else if (curSkill.point >= 9) {
                                        npcChat(player, "Skill của ngươi đã đến cấp độ tối đa");
                                    } else {
                                        try {
                                            int trubk;
                                            String msg;
                                            String msg2;
                                            trubk = 999;
                                            msg = "Nâng skill thành công!";
                                            msg2 = "Chúc mừng con nhé!";
                                            curSkill.point++;
                                            curSkill.currLevel = 0;
                                            SkillService.gI().sendCurrLevelSpecial(player, curSkill);
                                            Message msgg;
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(0);
                                            msgg.writer().writeUTF("Skill 9");
                                            msgg.writer().writeUTF("MaiTienDung");
                                            msgg.writer().writeShort(tempId);
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(1);
                                            msgg.writer().writeByte(1);
                                            msgg.writer().writeByte(InventoryService.gI().getIndexItemBag(player, BiKiepTuyetKy));
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(7);
                                            msgg.writer().writeShort(iconSkill);
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            this.npcChat(player, msg2);
                                            Service.gI().sendThongBao(player, msg);
                                            InventoryService.gI().subQuantityItemsBag(player, BiKiepTuyetKy, trubk);
                                            player.inventory.gold -= 2_000_000_000;
                                            player.inventory.ruby -= 99;
                                            Service.gI().sendMoney(player);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, msg);
                                        } catch (IOException e) {
                                        }
                                    }
                                } else {
                                    int sosach = 999 - BiKiepTuyetKy.quantity;
                                    Service.gI().sendThongBao(player, "Ngươi còn thiếu " + sosach + " bí kíp nữa.\nHãy tìm đủ rồi đến gặp ta.");
                                }
                            }
                        } else if (player.nPoint.power < 60000000000L) {
                            Service.gI().sendThongBao(player, "Ngươi không đủ sức mạnh để học tuyệt kỹ");
                        } else if (player.inventory.gold <= 2_000_000_000) {
                            Service.gI().sendThongBao(player, "Hãy có đủ vàng thì quay lại gặp ta.");
                        } else if (player.inventory.ruby <= 99) {
                            Service.gI().sendThongBao(player, "Hãy có đủ ngọc xanh thì quay lại gặp ta.");
                        }
                        break;
                    }
                }
            }
        }
    }
   
}
