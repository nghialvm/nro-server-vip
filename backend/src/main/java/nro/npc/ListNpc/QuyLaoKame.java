package nro.npc.ListNpc;


import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.FormatStyle;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.util.ArrayList;
import jbcd.dao.PlayerDAO;
import models.Item.Item;
import models.Item.ItemService;
import models.Reward.RewardService;
import nro.clan.Clan;
import nro.map.TreasureUnderSea.TreasureUnderSea;
import nro.map.TreasureUnderSea.TreasureUnderSeaService;
import nro.npc.Npc;
import static nro.npc.NpcFactory.PLAYERID_OBJECT;
import nro.player.Player;
import nro.server.Manager;
import nro.shop.ShopService;
import nro.skill.Skill;

public class QuyLaoKame extends Npc {

    public QuyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        Service.gI().addBoughtSkillAttack(player);
        Item ruacon = InventoryService.gI().findItemBag(player, 874);
        Item ThiepChucTet1 = InventoryService.gI().findItemBag(player, 1191);
        Item ThiepChucTet2 = InventoryService.gI().findItemBag(player, 1192);
        Item ThiepChucTet3 = InventoryService.gI().findItemBag(player, 1193);
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_13 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN, "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                            "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.canReward_PeNa) {
                    createOtherMenu(player, ConstNpc.MENU_PE_NA,
                            "Ah con đã tìm thấy bé rắn cute thất lạc của ta\n"
                            + "Ta sẽ thưởng cho con 1 viên Capsule Tết 2025.",
                            "Đồng ý");
                    return;
                } else if (player.canReward) {
                    createOtherMenu(player, ConstNpc.MENU_LAN_CON,
                            "Ah con đã tìm thấy lân con thất lạc của ta\n"
                            + "Ta sẽ thưởng cho con 1 viên Capsule Tết 2025.",
                            "Đồng ý");
                    return;
                } else if (player.NhanLiXiForNPC_16 == 0) {
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
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1)
                        || (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1)
                        || (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1)) {
                    createOtherMenu(player, 1, "Chúc con năm mới an khang, thịnh vượng và luôn gặp nhiều may mắn trong công việc cũng như cuộc sống. "
                            + "Cảm ơn sự tin tưởng và đồng hành của con trong suốt thời gian qua!\n"
                            + "Này " + player.name + ", con có quà gì tặng ta không?",
                            "Tặng Lì Xì", "Bỏ Qua");
                    return;
                }
            }
            ArrayList<String> menu = new ArrayList<>();
            menu.add("Nói\nchuyện");
            menu.add("Sự kiện VIP");
            //menu.add("Quy đổi hồng ngọc");
            //menu.add("Sự Kiện Hộp Rocket");
//            menu.add("Meta Game");
            if (ruacon != null && ruacon.quantity >= 1) {
                menu.add("Giao\nRùa con");
            }
            String[] menus = menu.toArray(new String[0]);
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn hỏi gì nào?", menus);
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        Item ThiepChucTet1 = InventoryService.gI().findItemBag(player, 1191);
        Item ThiepChucTet2 = InventoryService.gI().findItemBag(player, 1192);
        Item ThiepChucTet3 = InventoryService.gI().findItemBag(player, 1193);
        int HongNgoc;
        Item ThoiVang = ItemService.gI().createNewItem((short) 457);
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
                            player.NhanLiXiForNPC_16++;
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
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) || (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) || (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case 1:
                            switch (select) {
                                case 0:
                                    player.DuaTopTangLiXi++;
                                    if (Util.isTrue(50, 90)) {
                                        if (ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet1, 1);
                                            int QuantityThoiVang = Util.nextInt(1, 5);
                                            int QuantityHongNgoc = Util.nextInt(1, 20);
                                            HongNgoc = QuantityHongNgoc;
                                            player.inventory.addRuby(HongNgoc);
                                            ThoiVang.quantity = QuantityThoiVang;
                                            ThoiVang.addOptionParam(30, 0);
                                            InventoryService.gI().addItemBag(player, ThoiVang);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        }
                                        if (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet3, 1);
                                            int QuantityThoiVang = Util.nextInt(1, 5);
                                            int QuantityHongNgoc = Util.nextInt(1, 20);
                                            HongNgoc = QuantityHongNgoc;
                                            player.inventory.addRuby(HongNgoc);
                                            ThoiVang.quantity = QuantityThoiVang;
                                            ThoiVang.addOptionParam(30, 0);
                                            InventoryService.gI().addItemBag(player, ThoiVang);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        }
                                        if (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet2, 1);
                                            int QuantityThoiVang = Util.nextInt(1, 5);
                                            int QuantityHongNgoc = Util.nextInt(1, 20);
                                            HongNgoc = QuantityHongNgoc;
                                            player.inventory.addRuby(HongNgoc);
                                            ThoiVang.quantity = QuantityThoiVang;
                                            ThoiVang.addOptionParam(30, 0);
                                            InventoryService.gI().addItemBag(player, ThoiVang);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        }
                                    } else {
                                        if (ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet1, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                        if (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet2, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                        if (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet3, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Quy Lão Kame thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                    }
                                    break;
                                case 1:
                                    Item ruacon = InventoryService.gI().findItemBag(player, 874);
                                    ArrayList<String> menu = new ArrayList<>();
                                    menu.add("Nói\nchuyện");
                                    menu.add("Sự kiện");
                                    if (ruacon != null && ruacon.quantity >= 1) {
                                        menu.add("Giao\nRùa con");
                                    }
                                    String[] menus = menu.toArray(String[]::new);
                                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn hỏi gì nào?", menus);
                                    }
                                    break;
                            }
                            return;
                    }
                }
            }
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU: {
                    switch (select) {
                        case 0: {
                            ArrayList<String> menu = new ArrayList<>();
                            menu.add("Nhiệm vụ");
                            menu.add("Học\nKỹ năng");
                            Clan clan = player.clan;
                            if (clan != null) {
                                menu.add("Về khu\nvực bang");
                                if (clan.isLeader(player)) {
                                    menu.add("Giải tán\nBang hội");
                                }
                            }
                            menu.add("Kho báu\ndưới biển");
                            String[] menus = menu.toArray(String[]::new);

                            this.createOtherMenu(player, 0,
                                    "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào ?", menus);
                            break;
                        }
//                        case 2: {
//                            Item ruacon = InventoryService.gI().findItemBag(player, 874);
//                            if (ruacon != null && ruacon.quantity >= 1) {
//                                this.createOtherMenu(player, ConstNpc.MENU_GIAO_RUA_CON,
//                                        "Cảm ơn cậu đã cứu con rùa của ta\nĐể cảm ơn ta sẽ tặng cậu món quà.",
//                                        "Nhận quà", "Đóng");
//                                break;
//                            }
//                            break;
//                        }
//                        case 2:
//
//                            this.createOtherMenu(player, ConstNpc.QUY_DOI_HN,
//                                    "|7|QUY ĐỔI HỒNG NGỌC"
//                                    + "\n|6|Quy dổi Hồng ngọc từ thỏi vàng tại đây"
//                                    + "\n|1|1 Thỏi vàng = 100 Hồng ngọc"
//                                    + "\n\n|5|Nhập 1 được 100 Hồng Ngọc",
//                                    "Đồng ý", "Từ chối");
//
//                            break;
                        case 1:
                            long tongNap2 = player.getSession().coin;
                            long soHopCoTheNhan = tongNap2 / 20000; // 20k = 1 hộp

                            this.createOtherMenu(player, ConstNpc.QUY_DOI_XU,
                                    "|7|Sự kiện Quy Đổi Cải VIP"
                                    + "\n|6|Các chiến binh sẽ không mất số dư tài khoản khi quy đổi"
                                    + "\n|6|Thời gian: 19/10 - 23/10"
                                    + "\n|6|Mỗi 20.000 VND nạp = 1 Hộp Cải Trang VIP"
                                    + "\n|1|Bạn hiện có tổng : " + Util.format(tongNap2) + " VND"
                                    + "\n|1|=> Có thể nhận: " + Util.format(soHopCoTheNhan) + " hộp"
                                    + "\n\n|5|Chọn hộp bạn muốn nhận:",
                                    "SDCM","HP,KI", "Từ chối");
                            break;
//                        case 2:
//                                            this.createOtherMenu(player, 3345,
//                                                    "|7|Meta mùa 1 từ năm này sang năm sau"
//                                                    + "\n|5|Ma phong ba gây dame theo phần trăm Hp được chuyển sáng thanh phần trăm Ki"
//                                                    + "\n|6|-Trái đất :Giảm 20% đòn chưởng Kamejoko, dịch chuyển thức thời, Tăng 20%Kaioken và Tăng thêm 150% quả cầu khênh khi"
//                                                    + "\n|6|-Xayda : Tăng 20% đòn đánh Galick,Tăng 50% Atomic và Tăng sát thương bom + 200%"
//                                                    + "\n|6|-Namek :Tăng 10% đòn đánh Liên Hoàn ,Tăng sát thương laze thêm 200% ",
//                                                    //               + "\nCHÚC AE " + Manager.SERVER_NAME + " NĂM MỚI VUI VẺ...",
//                                                    "Ok");
//                                            break;    
                    }
                    break;
                }
                case 0: {
                    switch (select) {
                        case 0:
                            NpcService.gI().createTutorial(player, tempId, avartar, player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
                            break;
                        case 1:
                            if (player.LearnSkill.Time != -1 && player.LearnSkill.Time <= System.currentTimeMillis()) {
                                player.LearnSkill.Time = -1;
                                try {
                                    var curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(player.LearnSkill.ItemTemplateSkillId),
                                            SkillUtil.getSkillByItemID(player, player.LearnSkill.ItemTemplateSkillId).point);
                                    player.BoughtSkill.add((int) player.LearnSkill.ItemTemplateSkillId);
                                    SkillUtil.setSkill(player, curSkill);
                                    var msg = Service.gI().messageSubCommand((byte) 62);
                                    msg.writer().writeShort(curSkill.skillId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                } catch (Exception e) {
                                    Logger.log(e.toString());
                                }
                                return;
                            }
                            if (player.LearnSkill.Time != -1) {
                                int ngoc = 5;
                                long time = player.LearnSkill.Time - System.currentTimeMillis();
                                if (time / 600_000 >= 2) {
                                    ngoc += time / 600_000;
                                }
                                String[] subName = ItemService.gI().getTemplate(player.LearnSkill.ItemTemplateSkillId).name.split("");
                                byte level = Byte.parseByte(subName[subName.length - 1]);
                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_1,
                                        "Con đang học kỹ năng\n" + SkillUtil.findSkillTemplate(SkillUtil.getTempSkillSkillByItemID(player.LearnSkill.ItemTemplateSkillId)).name + " cấp " + level + "\nThời gian còn lại " + TimeUtil.getTime(time), "Học\nCấp tốc\n" + ngoc + " ngọc",
                                        "Huỷ", "Bỏ qua");
                            } else {
                                ShopService.gI().opendShop(player, "SHOP_LEARN_SKILL", false);
                            }
                            break;
                        case 2: {
                            Clan clan = player.clan;
                            if (clan != null && select == 2) {
                                ChangeMapService.gI().changeMapNonSpaceship(player, 153, Util.nextInt(100, 200), 432);
                            } else {
                                if (player.clan != null && player.clan.BanDoKhoBau != null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                            "Bang hội con đang ở hang kho báu cấp "
                                            + player.clan.BanDoKhoBau.level + "\ncon có muốn đi cùng họ không?",
                                            "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\nỞ đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                            "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                                }
                            }
                            break;
                        }
                        case 3: {
                            boolean clanCheck = true;
                            Clan clan = player.clan;
                            if (clan != null) {
                                clanCheck = false;
                                if (clan.isLeader(player)) {
                                    createOtherMenu(player, 3, "Con có chắc muốn giải tán bang hội không?", "Đồng ý", "Từ chối");
                                } else {
                                    clanCheck = true;
                                }
                            }
                            if (clanCheck) {
                                if (player.clan != null && player.clan.BanDoKhoBau != null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                            "Bang hội con đang ở hang kho báu cấp "
                                            + player.clan.BanDoKhoBau.level + "\ncon có muốn đi cùng họ không?",
                                            "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\nỞ đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                            "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                                }
                            }
                            break;
                        }
                        case 4: {
                            if (player.clan != null && player.clan.BanDoKhoBau != null) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                        "Bang hội con đang ở hang kho báu cấp "
                                        + player.clan.BanDoKhoBau.level + "\ncon có muốn đi cùng họ không?",
                                        "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                        "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\nỞ đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                        "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                            }
                            break;
                        }

                    }
                    break;
                }
                case 3:
                    Clan clan = player.clan;
                    if (clan != null) {
                        if (clan.isLeader(player)) {
                            if (select == 0) {
                                Input.gI().createFormGiaiTanBangHoi(player);
                            }
                        }
                    }
                    break;
               
                case ConstNpc.QUY_DOI_XU: {
                    switch (select) {
                        case 0: 
                            long coinCanTru = 20000; // Mỗi hộp 20.000 coin
                            long coinHienTai = player.getSession().coin;

                            if (coinHienTai < coinCanTru) {
                                Service.gI().sendThongBao(player, "|7|Không đủ 20.000 Coin để quy đổi!");
                                return;
                            }
                            PlayerDAO.subTongNap2(player, coinCanTru);
                            Item hopBill = ItemService.gI().createNewItem((short) 1922);
                            InventoryService.gI().addItemBag(player, hopBill);
                            InventoryService.gI().sendItemBag(player);

                            Service.gI().sendThongBao(player,
                                    "|7|Bạn đã quy đổi 20.000 Coin để nhận 1 Hộp Cải Trang VIP!"
                                    + "\n|1|Coin còn lại: " + Util.format(player.getSession().tongnap2));
                            break;
                        case 1: // Đồng ý – nhận 1 Hộp Bill
                            long coinCanTru1 = 20000; // Mỗi hộp 20.000 coin
                            long coinHienTai1 = player.getSession().coin;

                            if (coinHienTai1 < coinCanTru1) {
                                Service.gI().sendThongBao(player, "|7|Không đủ 20.000 Coin để quy đổi!");
                                return;
                            }
                            PlayerDAO.subTongNap2(player, coinCanTru1);
                            Item hopBill1 = ItemService.gI().createNewItem((short) 1923);
                            InventoryService.gI().addItemBag(player, hopBill1);
                            InventoryService.gI().sendItemBag(player);

                            Service.gI().sendThongBao(player,
                                    "|7|Bạn đã quy đổi 20.000 Coin để nhận 1 Hộp Cải Trang VIP"
                                    + "\n|1|Coin còn lại: " + Util.format(player.getSession().tongnap2));
                            break;    

                        case 2: 
                            Service.gI().sendThongBao(player, "Hẹn gặp lại con!");
                            break;
                    }
                    break;
                }

                case ConstNpc.MENU_OPENED_DBKB: {
                    switch (select) {
                        case 0:// Top bang hội
                            Service.gI().showTopClanBDKB(player);
                            break;
                        case 1:
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Bạn không có bang hội!");
                                return;
                            }
                            Service.getInstance().showMyTopClanBDKB(player);
                            break;
                        case 2: {
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Hãy vào bang hội trước");
                                return;
                            }
                            if (player.isFounder() || player.nPoint.power >= TreasureUnderSea.POWER_CAN_GO_TO_DBKB) {
                                ChangeMapService.gI().goToDBKB(player);
                            } else {
                                this.npcChat(player, "Yêu cầu sức mạnh lớn hơn "
                                        + Util.formatNumber(TreasureUnderSea.POWER_CAN_GO_TO_DBKB, FormatStyle.VIETNAMESE));
                            }
                            break;
                        }
                    }
                    break;
                }
                case ConstNpc.MENU_OPEN_DBKB: {
                    switch (select) {
                        case 0:// Top bang hội
                            Service.gI().showTopClanBDKB(player);
                            break;
                        case 1:
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Bạn không có bang hội!");
                                return;
                            }
                            Service.getInstance().showMyTopClanBDKB(player);
                            break;
                        case 2: {
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Hãy vào bang hội trước");
                                return;
                            }
                            if (player.isFounder() || player.nPoint.power >= TreasureUnderSea.POWER_CAN_GO_TO_DBKB) {
                                Input.gI().createFormChooseLevelBDKB(player);
                            } else {
                                this.npcChat(player, "Yêu cầu sức mạnh lớn hơn "
                                        + Util.formatNumber(TreasureUnderSea.POWER_CAN_GO_TO_DBKB, FormatStyle.VIETNAMESE));
                            }
                            break;
                        }
                    }
                    break;
                }
                case ConstNpc.MENU_ACCEPT_GO_TO_BDKB: {
                    switch (select) {
                        case 0:
                            TreasureUnderSeaService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                            break;
                    }
                    break;
                }
                case ConstNpc.MENU_GIAO_RUA_CON: {
                    if (select == 0) {
                        Item ruacon = InventoryService.gI().findItemBag(player, 874);
                        if (ruacon != null && ruacon.quantity >= 1) {
                            InventoryService.gI().subQuantityItemsBag(player, ruacon, 1);

                            // Danh sách các vật phẩm có thể nhận được
                            short[] itemIds = {
                                1589, 1590, 1591, 1592, 1593, 1594, 1611, 1621, 1612,
                                611, 1622, 1620, 1641, 1642, 2048, 2049, 1643, 457
                            };

                            // Xác suất nhận từng vật phẩm
                            int chance = 5; // Xác suất 5%
                            Item itemReceived;

                            if (Util.isTrue(chance, 100)) {
                                itemReceived = ItemService.gI().createNewItem(itemIds[Util.nextInt(0, itemIds.length - 1)]);
                            } else {
                                itemReceived = ItemService.gI().createNewItem((short) 457); // Mặc định nếu không trúng vật phẩm hiếm
                            }

                            InventoryService.gI().addItemBag(player, itemReceived);
                            InventoryService.gI().sendItemBag(player);
                            Service.getInstance().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không có Rùa Con");
                        }
                    }
                    break;
                }
                case ConstNpc.MENU_PE_NA: {
                    switch (select) {
                        case 0:
                            RewardService.gI().rewardBeNa(player);
                            break;
                    }
                    break;
                }
                case ConstNpc.MENU_LAN_CON: {
                    switch (select) {
                        case 0:
                            RewardService.gI().rewardLancon(player);
                            break;
                    }
                    break;
                }
                case ConstNpc.HOC_SKILL_1: {
                    if (select == 1) {
                        this.createOtherMenu(player, ConstNpc.HOC_SKILL_2, "Con có muốn huỷ học kỹ năng này và nhận lại 50% số tiềm năng không ?", "Ok", "Đóng");
                    } else if (select == 0) {
                        long time = player.LearnSkill.Time - System.currentTimeMillis();
                        int ngoc = 5;
                        if (time / 600_000 >= 2) {
                            ngoc += time / 600_000;
                        }
                        if (player.inventory.gem < ngoc) {
                            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
                            return;
                        }
                        player.inventory.subGem(ngoc);
                        player.LearnSkill.Time = -1;
                        try {
                            String[] subName = ItemService.gI().getTemplate(player.LearnSkill.ItemTemplateSkillId).name.split("");
                            byte level = Byte.parseByte(subName[subName.length - 1]);
                            Skill curSkill = SkillUtil.getSkillByItemID(player, player.LearnSkill.ItemTemplateSkillId);
                            if (curSkill.point == 0) {
                                player.BoughtSkill.add((int) player.LearnSkill.ItemTemplateSkillId);
                                curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(player.LearnSkill.ItemTemplateSkillId), level);
                                SkillUtil.setSkill(player, curSkill);
                                var msg = Service.getInstance().messageSubCommand((byte) 23);
                                msg.writer().writeShort(curSkill.skillId);
                                player.sendMessage(msg);
                                msg.cleanup();
                            } else {
                                player.BoughtSkill.add((int) player.LearnSkill.ItemTemplateSkillId);
                                curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(player.LearnSkill.ItemTemplateSkillId), level);
                                SkillUtil.setSkill(player, curSkill);
                                var msg = Service.getInstance().messageSubCommand((byte) 62);
                                msg.writer().writeShort(curSkill.skillId);
                                player.sendMessage(msg);
                                msg.cleanup();
                            }
                            PlayerService.gI().sendInfoHpMpMoney(player);
                        } catch (Exception e) {
                            Logger.log(e.toString());
                        }
                    }
                    break;
                }
                case ConstNpc.HOC_SKILL_2: {
                    if (select == 0) {
                        player.nPoint.tiemNang += player.LearnSkill.Potential / 2;
                        PlayerService.gI().sendTNSM(player, (byte) 1, player.LearnSkill.Potential / 2);
                        player.LearnSkill.Time = -1;
                        Service.gI().point(player);
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        Service.gI().ClosePanel(player);
                        NpcService.gI().createTutorial(player, NpcService.gI().getAvatar(13 + player.gender), "Con đã huỷ học kĩ năng thành công, ta sẽ trả lại con 50% tiềm năng đã học");
                    }
                    break;
                }
                case ConstNpc.NHAN_KEO_HALLOWEEN: {
                    switch (select) {
                        case 0:
                            Item KeoBanTay = ItemService.gI().createNewItem((short) 901, 1);
                            KeoBanTay.addOptionParam(86, 0);
                            KeoBanTay.addOptionParam(93, 35);
                            int quality = Util.nextInt(1, 3);
                            KeoBanTay.quantity = quality;
                            InventoryService.gI().addItemBag(player, KeoBanTay);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().chat(player, "Haha xin được " + quality + " kẹo bàn tay rồi");
                            player.NhanKeoHayBiGheoNpc_13++;
                            break;
                        case 1:
                            player.NhanKeoHayBiGheoNpc_13++;
                            break;
                    }
                    break;
                }

            }
        }
    }
}
