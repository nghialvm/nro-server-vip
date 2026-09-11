package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstPlayer;
import event.EventManager;
import java.util.ArrayList;
import models.Item.Item;
import models.Item.ItemService;
import models.Reward.RewardService;
import nro.clan.Clan;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.skill.Skill;

public class VuaVegeta extends Npc {

    public VuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        Service.gI().addBoughtSkillAttack(player);
        Item ThiepChucTet1 = InventoryService.gI().findItemBag(player, 1191);
        Item ThiepChucTet2 = InventoryService.gI().findItemBag(player, 1192);
        Item ThiepChucTet3 = InventoryService.gI().findItemBag(player, 1193);
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_12 == 0) {
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
                } else if (player.NhanLiXiForNPC_18 == 0) {
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
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) || 
                           (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) || 
                           (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1)) {
                    createOtherMenu(player, 1, "Chúc con năm mới an khang, thịnh vượng và luôn gặp nhiều may mắn trong công việc cũng như cuộc sống. "
                            + "Cảm ơn sự tin tưởng và đồng hành của con trong suốt thời gian qua!\n"
                            + "Này " + player.name + ", con có quà gì tặng ta không?", 
                        "Tặng Lì Xì", "Bỏ Qua");
                    return;
                }
            }
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (player.gender != ConstPlayer.XAYDA) {
                    NpcService.gI().createTutorial(player, tempId, avartar, "Con hãy về hành tinh của mình mà thể hiện");
                    return;
                }
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

                String[] menus = menu.toArray(String[]::new);
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Chào con, ta rất vui khi gặp được con\nCon muốn làm gì nào ?", menus);
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
                            player.NhanLiXiForNPC_18++;
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
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) || (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) || (ThiepChucTet3 != null && ThiepChucTet3 .quantity >= 1)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case 1:
                            switch (select) {
                                case 0:
                                    player.DuaTopTangLiXi++;
                                    if (Util.isTrue(50, 90)) {
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
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
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
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        }
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
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        }
                                    } else {
                                        if (ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet1, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        } 
                                        if (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet2, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                        if (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet3, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Vua Vegeta thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                    }
                                    break;
                                case 1:
                                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        if (player.gender != ConstPlayer.XAYDA) {
                                            NpcService.gI().createTutorial(player, tempId, avartar, "Con hãy về hành tinh của mình mà thể hiện");
                                            return;
                                        }
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

                                        String[] menus = menu.toArray(String[]::new);
                                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Chào con, ta rất vui khi gặp được con\nCon muốn làm gì nào ?", menus);
                                    }
                                    break;
                        } 
                        return;
                    }
                }
            }
            if (player.iDMark.isBaseMenu()) {
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
                            var ngoc = 5;
                            var time = player.LearnSkill.Time - System.currentTimeMillis();
                            if (time / 600_000 >= 2) ngoc += time / 600_000;
                            String[] subName = ItemService.gI().getTemplate(player.LearnSkill.ItemTemplateSkillId).name.split("");
                            byte level = Byte.parseByte(subName[subName.length - 1]);
                            createOtherMenu(player, ConstNpc.HOC_SKILL_1,
                                    "Con đang học kỹ năng\n" + SkillUtil.findSkillTemplate(SkillUtil.getTempSkillSkillByItemID(player.LearnSkill.ItemTemplateSkillId)).name
                                            + " cấp " + level + "\nThời gian còn lại " + TimeUtil.getTime(time),
                                    "Học Cấp tốc " + ngoc + " ngọc", "Huỷ", "Bỏ qua");
                        } else {
                            ShopService.gI().opendShop(player, "SHOP_LEARN_SKILL", false);
                        }
                        break;
                    case 2: {
                        Clan clan = player.clan;
                        if (clan != null) {
                            ChangeMapService.gI().changeMapNonSpaceship(player, 153, Util.nextInt(100, 200), 432);
                        }
                        break;
                    }
                    case 3: {
                        Clan clan = player.clan;
                        if (clan != null) {
                            if (clan.isLeader(player)) {
                                createOtherMenu(player, 3, "Con có chắc muốn giải tán bang hội không?", "Đồng ý", "Từ chối");
                            }
                        }
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == 3) {
                Clan clan = player.clan;
                if (clan != null) {
                    if (clan.isLeader(player)) {
                        if (select == 0) {
                            Input.gI().createFormGiaiTanBangHoi(player);
                        }
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PE_NA) {
                if (select == 0) {
                    RewardService.gI().rewardBeNa(player);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_LAN_CON) {
                if (select == 0) {
                    RewardService.gI().rewardLancon(player);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.HOC_SKILL_1) {
                switch (select) {
                    case 0: {
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
                        break;
                    }
                    case 1: {
                        createOtherMenu(player, ConstNpc.HOC_SKILL_2, "Con có muốn huỷ học kỹ năng này và nhận lại 50% số tiềm năng không ?", "Ok", "Đóng");
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.HOC_SKILL_2) {
                switch (select) {
                    case 0: {
                        player.nPoint.tiemNang += player.LearnSkill.Potential / 2;
                        PlayerService.gI().sendTNSM(player, (byte)1, player.LearnSkill.Potential / 2);
                        player.LearnSkill.Time = -1;
                        Service.gI().point(player);
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        Service.gI().ClosePanel(player);
                        NpcService.gI().createTutorial(player, NpcService.gI().getAvatar(13 + player.gender), "Con đã huỷ học kĩ năng thành công, ta sẽ trả lại con 50% tiềm năng đã học");
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_KEO_HALLOWEEN) {
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
                        player.NhanKeoHayBiGheoNpc_12++;
                        break;
                    case 1:
                        player.NhanKeoHayBiGheoNpc_12++;
                        break;
                }   
            }
        }
    }
}
