package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstPlayer;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import models.Reward.RewardService;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.BuyBack;
import nro.shop.ShopService;

public class Bulma extends Npc {

    public Bulma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        Item ThiepChucTet1 = InventoryService.gI().findItemBag(player, 1191);
        Item ThiepChucTet2 = InventoryService.gI().findItemBag(player, 1192);
        Item ThiepChucTet3 = InventoryService.gI().findItemBag(player, 1193);

        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_2 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN,
                            "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                            "Cho kẹo\nhay\nbị ghẹo?",
                            "Từ chối\nnhận kẹo",
                            "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.canReward_PiLong) {
                    this.createOtherMenu(player, ConstNpc.MENU_PI_LONG,
                            "Ta mải mê ăn tết thế nên bị mấy tên trộm bắt mất PiLong rồi huhuhu..."
                            + "Các cậu giúp tôi tìm về, tôi tặng các cậu cái này.",
                            "Giao PiLong");
                    return;
                } else if (player.NhanLiXiForNPC_2 == 0) {
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
                    createOtherMenu(player, ConstNpc.NHAN_LI_XI, message,
                            "Ok",
                            "Chúc Mừng\nNăm Mới",
                            "Đóng");
                    return;
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1)
                        || (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1)
                        || (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1)) {
                    this.createOtherMenu(player, 1,
                            "Chúc quý khách năm mới an khang, thịnh vượng và luôn gặp nhiều may mắn trong công việc cũng như cuộc sống. "
                            + "Cảm ơn sự tin tưởng và đồng hành của quý khách trong suốt thời gian qua!\n"
                            + "Này " + player.name + ", bạn có quà gì tặng mình không?",
                            "Tặng Lì Xì",
                            "Bỏ Qua");
                    return;
                }
            }

            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (player.gender == ConstPlayer.TRAI_DAT) {
                    if (!player.inventory.itemsDaBan.isEmpty()) {
                        this.createOtherMenu(player, ConstNpc.SHOP_BUMMA_MUA_LAI,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé",
                                "Cửa\nhàng",
                                "Mua lại\nvật phẩm\nđã bán\n[" + player.inventory.itemsDaBan.size() + "/" + BuyBack.MAX_COUNT_IN_BOX + "]",
                                "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.SHOP_BUMMA_MUA_LAI_2,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé",
                                "Cửa Hàng",
                                "Đóng");
                    }
                } else {
                    if (player.Detu == null) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar,
                                "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất");
                    } else if (player.Detu != null && player.Detu.gender != 0) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar,
                                "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất");
                    } else if (player.Detu != null && player.Detu.gender == 0) {
                        this.createOtherMenu(player, ConstNpc.SHOP_BUMMA_2,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé",
                                "Cửa Hàng Dành Cho Đệ Tử",
                                "Đóng");
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

                            player.NhanLiXiForNPC_2++;

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
                } else if ((ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1)
                        || (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1)
                        || (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1)) {
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

                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
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

                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
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

                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công, Bạn nhận được " + HongNgoc + " hồng ngọc");
                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công, Bạn nhận được " + QuantityThoiVang + " thỏi vàng");
                                            return;
                                        }
                                    } else {
                                        if (ThiepChucTet1 != null && ThiepChucTet1.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet1, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }

                                        if (ThiepChucTet2 != null && ThiepChucTet2.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet2, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }

                                        if (ThiepChucTet3 != null && ThiepChucTet3.quantity >= 1) {
                                            InventoryService.gI().subQuantityItemsBag(player, ThiepChucTet3, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, "Chúc Bulma thành công");
                                            Service.gI().sendThongBao(player, "(!__!)");
                                            return;
                                        }
                                    }
                                    break;

                                case 1:
                                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        if (player.gender == ConstPlayer.TRAI_DAT) {
                                            if (!player.inventory.itemsDaBan.isEmpty()) {
                                                this.createOtherMenu(player, ConstNpc.SHOP_BUMMA_MUA_LAI,
                                                        "Cậu cần trang bị gì cứ đến chỗ tôi nhé",
                                                        "Cửa\nhàng",
                                                        "Mua lại\nvật phẩm\nđã bán\n[" + player.inventory.itemsDaBan.size() + "/" + BuyBack.MAX_COUNT_IN_BOX + "]",
                                                        "Đóng");
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.SHOP_BUMMA_MUA_LAI_2,
                                                        "Cậu cần trang bị gì cứ đến chỗ tôi nhé",
                                                        "Cửa Hàng",
                                                        "Đóng");
                                            }
                                        } else {
                                            if (player.Detu == null) {
                                                NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                        "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất");
                                            } else if (player.Detu != null && player.Detu.gender != 0) {
                                                NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                        "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất");
                                            } else if (player.Detu != null && player.Detu.gender == 0) {
                                                this.createOtherMenu(player, ConstNpc.SHOP_BUMMA_2,
                                                        "Cậu cần trang bị gì cứ đến chỗ tôi nhé",
                                                        "Cửa Hàng Dành Cho Đệ Tử",
                                                        "Đóng");
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
                case ConstNpc.SHOP_BUMMA:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "BUNMA", true);
                            break;
                        case 1:
                            break;
                    }
                    break;

                case ConstNpc.SHOP_BUMMA_2:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "BUNMA", true);
                            break;
                    }
                    break;

                case ConstNpc.SHOP_BUMMA_MUA_LAI:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "BUNMA", true);
                            break;

                        case 1:
                            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
                            break;
                    }
                    break;

                case ConstNpc.SHOP_BUMMA_MUA_LAI_2:
                    switch (select) {
                        case 0:
                            ShopService.gI().opendShop(player, "BUNMA", true);
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
                            player.NhanKeoHayBiGheoNpc_2++;
                            break;

                        case 1:
                            player.NhanKeoHayBiGheoNpc_2++;
                            break;
                    }
                    break;

                default:
                    break;
            }
        }
    }
}