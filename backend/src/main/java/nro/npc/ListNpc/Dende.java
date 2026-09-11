package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstPlayer;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import models.Reward.RewardService;
import nro.dragon.SummonDragonNamek;
import nro.map.DragonBallNamec.NgocRongNamec;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.BuyBack;
import nro.shop.ShopService;

public class Dende extends Npc {

    public Dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        Item ThiepChucTet1 = InventoryService.gI().findItemBag(player, 1191);
        Item ThiepChucTet2 = InventoryService.gI().findItemBag(player, 1192);
        Item ThiepChucTet3 = InventoryService.gI().findItemBag(player, 1193);
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_3 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN, "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                        "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.canReward_PiLong) {
                    this.createOtherMenu(player, ConstNpc.MENU_PI_LONG,
                        "Ta mải mê ăn tết thế nên bị mấy tên trộm bắt mất PiLong rồi huhuhu...Các cậu giúp tôi tìm về, tôi tặng các cậu cái này.", 
                        "Giao PiLong");
                    return;
                } else if (player.NhanLiXiForNPC_19 == 0) {
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
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) || (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) || (ThiepChucTet3 != null && ThiepChucTet3 .quantity >= 1)) {
                    this.createOtherMenu(player, 1, "Chúc quý khách năm mới an khang, thịnh vượng và luôn gặp nhiều may mắn trong công việc cũng như cuộc sống. "
                            + "Cảm ơn sự tin tưởng và đồng hành của quý khách trong suốt thời gian qua!\n"
                            + "Này " + player.name + ", bạn có quà gì tặng mình không?", 
                        "Tặng Lì Xì", "Bỏ Qua");
                    return;
                }
            }
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (player.idNRNM != -1) {
                    if (player.zone.map.mapId == 7) {
                        this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                    }
                } else {
                    if (player.gender == ConstPlayer.NAMEC) {
                        if (!player.inventory.itemsDaBan.isEmpty()) {
                            this.createOtherMenu(player, ConstNpc.SHOP_DENDE_MUA_LAI,
                            "Anh cần trang bị gì cứ đến chỗ em nhé", 
                            "Cửa\nhàng", "Mua lại\nvật phẩm\nđã bán\n[" + player.inventory.itemsDaBan.size() + "/" + BuyBack.MAX_COUNT_IN_BOX + "]", "Đóng");
                        } else {
                            this.createOtherMenu(player, ConstNpc.SHOP_DENDE_MUA_LAI_2,
                            "Anh cần trang bị gì cứ đến chỗ em nhé", 
                            "Cửa Hàng",  "Đóng");
                        }
                    } else {
                        if (player.Detu == null) {
                            NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc");
                        } else if (player.Detu != null && player.Detu.gender != 1) {
                            NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc");
                        } else if (player.Detu != null && player.Detu.gender == 1) {
                            this.createOtherMenu(player, ConstNpc.SHOP_DENDE_2,
                            "Anh cần trang bị gì cứ đến chỗ em nhé", 
                            "Cửa Hàng Dành Cho Đệ Tử", "Đóng");
                        }
                    }
                }
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
                            player.NhanLiXiForNPC_19++;
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
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
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
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
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
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        } 
                                    } else {
                                        if (ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet1, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        } 
                                        if (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet2, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                        if (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet3, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Dende thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                    }
                                    break;
                                case 1:
                                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        if (player.idNRNM != -1) {
                                            if (player.zone.map.mapId == 7) {
                                                this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                                            }
                                        } else {
                                            if (player.gender == ConstPlayer.NAMEC) {
                                                if (!player.inventory.itemsDaBan.isEmpty()) {
                                                    this.createOtherMenu(player, ConstNpc.SHOP_DENDE_MUA_LAI,
                                                    "Anh cần trang bị gì cứ đến chỗ em nhé", 
                                                    "Cửa\nhàng", "Mua lại\nvật phẩm\nđã bán\n[" + player.inventory.itemsDaBan.size() + "/" + BuyBack.MAX_COUNT_IN_BOX + "]", "Đóng");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.SHOP_DENDE_MUA_LAI_2,
                                                    "Anh cần trang bị gì cứ đến chỗ em nhé", 
                                                    "Cửa Hàng",  "Đóng");
                                                }
                                            } else {
                                                if (player.Detu == null) {
                                                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc");
                                                } else if (player.Detu != null && player.Detu.gender != 1) {
                                                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc");
                                                } else if (player.Detu != null && player.Detu.gender == 1) {
                                                    this.createOtherMenu(player, ConstNpc.SHOP_DENDE_2,
                                                    "Anh cần trang bị gì cứ đến chỗ em nhé", 
                                                    "Cửa Hàng Dành Cho Đệ Tử", "Đóng");
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }
                            return;
                    }
                }
            }
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.SHOP_DENDE:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "DENDE", true);
                            break;
                        case 1:
                            break;
                    }   
                    break;
                case ConstNpc.SHOP_DENDE_2:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "DENDE", true);
                            break;
                    }   
                    break;
                case ConstNpc.SHOP_DENDE_MUA_LAI:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "DENDE", true);
                            break;
                        case 1:
                            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
                            break;
                    }   
                    break;
                case ConstNpc.SHOP_DENDE_MUA_LAI_2:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "DENDE", true);
                            break;
                    }   
                    break;
                case ConstNpc.MENU_PI_LONG:
                    switch (select) {
                        case 0:
                            RewardService.gI().rewardPiLong(player);
                            break;
                    }   
                    break;
                case ConstNpc.NHAN_KEO_HALLOWEEN:
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
                            player.NhanKeoHayBiGheoNpc_3++;
                            break;
                        case 1:
                            player.NhanKeoHayBiGheoNpc_3++;
                            break;
                    }   
                    break;
                case 1:
                    switch (select) {
                    case 0:
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_NRNM);
                        break;
                    case 1: {
                        if (player.zone.map.mapId == 7 && player.idNRNM != -1) {
                            if (player.idNRNM != 353) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Anh phải có viên Ngọc Rồng Namek 1 sao");
                                return;
                            }
                            if (TimeUtil.getCurrHour() > 22 || TimeUtil.getCurrHour() < 8) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi mấy anh, em đang bận buôn bán nên chỉ rảnh gọi Rồng vào khoảng 8h đến 22h");
                                return;
                            }
                            if (!Util.canDoWithTime(player.lastTimePickNRNM, 600000)) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Ngọc bẩn quá, xin chờ em " + TimeUtil.getTimeLeft(player.lastTimePickNRNM, 600) + " nữa để lau bóng ngọc, gọi Rồng mới hiển linh");
                                return;
                            }
                            if (!NgocRongNamec.gI().canCallDragonNamec(player)) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Hãy gom đủ 7 viên Ngọc Rồng tại đây");
                                return;
                            }
                            NgocRongNamec.gI().tOpenNrNamec = System.currentTimeMillis() + 86400000;
                            NgocRongNamec.gI().firstNrNamec = true;
                            NgocRongNamec.gI().timeNrNamec = 0;
                            NgocRongNamec.gI().doneDragonNamec();
                            NgocRongNamec.gI().initNgocRongNamec((byte) 1);
                            NgocRongNamec.gI().reInitNrNamec((long) 86399000);
                            SummonDragonNamek.gI().summonNamec(player);
                        }
                        break;
                    }
                }
            }
        }
    }
}
