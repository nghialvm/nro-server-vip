package nro.shop;

import models.Item.Item;
import models.Item.ItemService;
import nro.player.Player;
import network.io.Message;
import nro.inventory.Inventory;
import nro.inventory.InventoryService;
import nro.server.Manager;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstAchievement;
import consts.ConstNpc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Item.ItemOption;
import nro.achievement.AchievementService;
import nro.badges.BadgesData;
import nro.badges.BadgesService;
import nro.badges.BadgesTask;
import nro.badges.BadgesTaskTemplate;
import nro.badges.BagesTemplate;
import nro.clan.ClanMember;
import nro.clan.ClanService;
import nro.npc.Special.MagicTree;
import nro.server.Client;
import nro.shop.TabShops.TabShop;

public class ShopService {

    private static final byte COST_GOLD = 0;
    private static final byte COST_GEM = 1;
    private static final byte COST_RUBY = 3;
    private static final byte COST_EVENT = 4;

    private static final byte NORMAL_SHOP = 0;
    private static final byte LEARN_SKILL = 1;
    private static final byte POINT_SHOP = 2;
    private static final byte SPEC_SHOP = 3;
    
    private static ShopService I;

    public static ShopService gI() {
        if (ShopService.I == null) {
            ShopService.I = new ShopService();
        }
        return ShopService.I;
    }

    public void opendShop(Player player, String tagName, boolean allGender) {
        switch (tagName) {
            case "ITEMS_LUCKY_ROUND":
                openShopType4(player, tagName, player.inventory.itemsBoxCrackBall);
                return;
            case "ITEMS_DABAN":
                openShopType8(player, tagName, player.inventory.itemsDaBan);
                return;
            case "ITEMS_MAIL_BOX":
                openShopType4(player, tagName, player.inventory.itemsMailBox);
                return;
            default:
                break;
        }
        try {
            Shop shop = this.getShop(tagName);
            for (TabShop tabShop : shop.tabShops) {
                for (ItemShop item : tabShop.itemShops) {
                    switch (item.temp.id) {
                        case 1627:// hành trang
                            if (player.inventory.itemsBag.size() >= 35) {
                                item.cost = ((player.inventory.itemsBag.size() - 35) + 1) * 2;
                            } else {
                                item.cost = 1;
                            }
                            break;
                    }
                }
                for (ItemShop item : tabShop.itemShops) {
                    if (item.temp.id == 517) {
                        int basePrice = 50;
                        int bagSize = player.inventory.itemsBag.size();
                        if (bagSize >= 21) {
                            item.cost = basePrice + (bagSize - 20) * 50;
                        }
                    }
                    if (item.temp.id == 518) {
                        int basePrice = 50;
                        int BoxSize = player.inventory.itemsBox.size();
                        if (BoxSize >= 21) {
                            item.cost = basePrice + (BoxSize - 20) * 50;
                        }
                    }
                    if (item.temp.id == 457) {
                        int basePrice = 1;
                        item.cost = basePrice + Util.nextInt(10, 30);
                    }
                }
                if (player.haveTennisSpaceShip) {
                    tabShop.itemShops.removeIf(item -> item.temp.id == 453);
                }
//                if (InventoryService.gI().findItem(player, 454)) {
//                    tabShop.itemShops.removeIf(item -> item.temp.id == 454);
//                }
            }
            shop = this.resolveShop(player, shop, allGender);
            switch (shop.typeShop) {
                case NORMAL_SHOP:
                    openShopType0(player, shop);
                    break;
                case LEARN_SKILL:
                    openShopType1(player, shop);
                    break;
                case POINT_SHOP:
                    openShopType2(player, shop);
                    break;
                case SPEC_SHOP:
                    openShopType3(player, shop);
                    break;
            }
        } catch (Exception e) {
            Logger.logException(ShopService.class, e);
        }
    }

    private Shop getShop(String tagName) throws Exception {
        for (Shop s : Manager.SHOPS) {
            if (s.tagName != null && s.tagName.equals(tagName)) {
                return s;
            }
        }
        throw new Exception("Shop " + tagName + " không tồn tại!");
    }

    private Shop resolveShop(Player player, Shop shop, boolean allGender) {
        if (shop.tagName != null && (shop.tagName.equals("BUA_1H") 
                || shop.tagName.equals("BUA_8H") 
                || shop.tagName.equals("BUA_1M")
                || shop.tagName.equals("BUA_DETU")
                || shop.tagName.equals("SHOP_BUA_BANG"))) {
            return this.resolveShopBua(player, new Shop(shop));
        }
        return allGender ? new Shop(shop) : new Shop(shop, player);
    }
    
    private Shop resolveShopBua(Player player, Shop s) {
        for (TabShop tabShop : s.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                long min = 0;
                switch (item.temp.id) {
                    case 213:
                        long timeTriTue = player.charms.tdTriTue;
                        long current = System.currentTimeMillis();
                        min = (timeTriTue - current) / 60000;
                        break;
                    case 214:
                        min = (player.charms.tdManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 215:
                        min = (player.charms.tdDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                    case 216:
                        min = (player.charms.tdOaiHung - System.currentTimeMillis()) / 60000;
                        break;
                    case 217:
                        min = (player.charms.tdBatTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 218:
                        min = (player.charms.tdDeoDai - System.currentTimeMillis()) / 60000;
                        break;
                    case 219:
                        min = (player.charms.tdThuHut - System.currentTimeMillis()) / 60000;
                        break;
                    case 522:
                        min = (player.charms.tdDeTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 671:
                        min = (player.charms.tdTriTue3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 672:
                        min = (player.charms.tdTriTue4 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1734:
                        min = (player.charms.tdDeTu2 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1735:
                        min = (player.charms.tdDeTu3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1736:
                        min = (player.charms.tdDeTu4 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1737:
                        min = (player.charms.tdDeTu5 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1738:
                        min = (player.charms.tdDeTu7 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1739:
                        min = (player.charms.tdDeTu10 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1740:
                        min = (player.charms.tdDeTu20 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1741:
                        min = (player.charms.tdTriTue5 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1742:
                        min = (player.charms.tdTriTue7 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1743:
                        min = (player.charms.tdTriTue10 - System.currentTimeMillis()) / 60000;
                        break;
                    case 1744:
                        min = (player.charms.tdTriTue20 - System.currentTimeMillis()) / 60000;
                        break;
                    case 797:
                        min = (player.clan.BuaTriTue - System.currentTimeMillis()) / 60000;
                        break;
                    case 798:
                        min = (player.clan.BuaManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 799:
                        min = (player.clan.BuaDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                }
                if (min > 0) {
                    item.options.clear();
                    if (min >= 1440) {
                        item.options.add(new ItemOption(63, (int) min / 1440));
                    } else if (min >= 60) {
                        item.options.add(new ItemOption(64, (int) min / 60));
                    } else {
                        item.options.add(new ItemOption(65, (int) min));
                    }
                }
            }
        }
        return s;
    }

    private void openShopType0(Player player, Shop shop) {
        if (shop != null) {
            player.iDMark.setShopOpen(shop);
            player.iDMark.setTagNameShop(shop.tagName);
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(NORMAL_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        if (itemShop.typeSell == COST_GOLD) {
                            msg.writer().writeInt(itemShop.cost);
                            msg.writer().writeInt(0);
                        } else if (itemShop.typeSell == COST_GEM) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        } else if (itemShop.typeSell == COST_RUBY) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        } else if (itemShop.typeSell == COST_EVENT) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(0);
                        }
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeInt(option.optionTemplate.id);
                            msg.writer().writeInt(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (IOException e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }
    
    private void openShopType1(Player player, Shop shop) {
        if (shop != null) {
            player.iDMark.setShopOpen(shop);
            player.iDMark.setTagNameShop(shop.tagName);
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(LEARN_SKILL);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        String[] subName = itemShop.temp.name.split("");
                        byte level = Byte.parseByte(subName[subName.length - 1]);

                        var skillTemplateId = SkillUtil.getTempSkillSkillByItemID(itemShop.temp.id);
                        var costPotential = SkillUtil.findSkillTemplate(skillTemplateId).skillss.stream().filter(s -> s.point == level).findFirst().map(s -> (long) s.powRequire).orElse(0L);
                        msg.writer().writeLong(costPotential);

                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeInt(option.optionTemplate.id);
                            msg.writer().writeInt(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);

                        msg.writer().writeByte(0);

                    }
                }
                player.sendMessage(msg);
            } catch (IOException | NumberFormatException e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }
    
    private void openShopType2(Player player, Shop shop) {
        player.iDMark.setShopOpen(shop);
        player.iDMark.setTagNameShop(shop.tagName);
        if (shop != null) {
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(POINT_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(1); // max page
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(itemShop.id);
                        msg.writer().writeInt(0); // gold price
                        msg.writer().writeInt(itemShop.cost);
                        msg.writer().writeByte(itemShop.typeSell);
                        msg.writer().writeInt(1); // quantity
                        msg.writer().writeByte(1); // isMe
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeInt(option.optionTemplate.id);
                            msg.writer().writeInt(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void openShopType3(Player player, Shop shop) {
        player.iDMark.setShopOpen(shop);
        player.iDMark.setTagNameShop(shop.tagName);
        if (shop != null) {
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(SPEC_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(itemShop.iconSpec);
                        msg.writer().writeInt(itemShop.cost);
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeInt(option.optionTemplate.id);
                            msg.writer().writeInt(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void openShopType4(Player player, String tagName, List<Item> items) {
        if (items == null) {
            return;
        }
        player.iDMark.setTagNameShop(tagName);
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Phần\nthưởng");
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|2|LUCKY DRAGON BALL");
                msg.writer().writeByte(item.itemOptions.size() + 1);
                for (ItemOption io : item.itemOptions) {
                    msg.writer().writeInt(io.optionTemplate.id);
                    msg.writer().writeInt(io.param);
                }
                //số lượng
                msg.writer().writeInt(31);
                msg.writer().writeInt(item.quantity);
                //
                msg.writer().writeByte(1);
                if (item.template.type == 5) {
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(item.template.head);
                    msg.writer().writeShort(item.template.body);
                    msg.writer().writeShort(item.template.leg);
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeByte(0);
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(ShopService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void openShopType8(Player player, String tagName, List<Item> items) {
        if (items == null) {
            return;
        }
        player.iDMark.setTagNameShop(tagName);
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(8);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Mua lại\n[" + items.size() + "/" + BuyBack.MAX_COUNT_IN_BOX + "]");
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                int giamualaingoc = item.template.gemSell / 2;
                int giamualaivang = giamualaingoc == 0 ? (int) item.template.goldSell / 2 > 0 ? (int) item.template.goldSell / 2 : item.quantity * 100 : 0;
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(giamualaivang);
                msg.writer().writeInt(giamualaingoc);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeByte(item.itemOptions.size());
                for (ItemOption io : item.itemOptions) {
                    msg.writer().writeInt(io.optionTemplate.id);
                    msg.writer().writeInt(io.param);
                }
                msg.writer().writeByte(0);
                if (item.template.type == 5) {
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(item.template.head);
                    msg.writer().writeShort(item.template.body);
                    msg.writer().writeShort(item.template.leg);
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeByte(0);
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(ShopService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void takeItem(Player player, byte type, int tempId) {
        String tagName = player.iDMark.getTagNameShop();
        if (tagName == null || tagName.length() <= 0) {
            return;
        }
        switch (tagName) {
            case "ITEMS_LUCKY_ROUND":
                getItemSideBoxLuckyRound(player, player.inventory.itemsBoxCrackBall, type, tempId);
                return;
            case "ITEMS_REWARD":
                return;
            case "ITEMS_DABAN":
                buyItemDaBan(player, player.inventory.itemsDaBan, tempId);
                return;
            case "BILL":
                buyItemHuyDiet(player, tempId);
                return;
            case "ITEMS_MAIL_BOX":
                getItemSideMailsBox(player, player.inventory.itemsMailBox, type, tempId);
                return;
            default:
                break;
        }
        if (player.iDMark.getShopOpen() == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        switch (tagName) {
            case "BUA_1H":
            case "BUA_8H":
            case "BUA_1M":
            case "BUA_DETU":
                buyItemBua(player, tempId);
                break;
            case "SHOP_BUA_BANG":
                buyItemBuaClan(player, tempId);
                break;
            case "SANTA_HEAD":
                Item itS = ItemService.gI().createNewItem((short) tempId);
                player.head = (short) itS.template.head;
                Service.getInstance().Send_Caitrang(player);
                Service.getInstance().sendThongBao(player, "Đổi kiểu tóc thành công");
                break;
            case "WHIS_KAIO":
                buyItemShopWhis(player, tempId);
                break;
            case "SHOP_NAMEK_WAR":
                buyItemChienTruongNamec(player, tempId);
                break;
            case "LUNAR_NEW_YEAR_EVENT":
                buyItemLunaNewYearEvent(player, tempId);
                break;
            case "LUNAR_NEW_YEAR":
                buyItemVongKimCo(player, tempId);
                break;
            case "CHRIST_MAS_EVENT_2":
                buyItemChristMasEvent(player, tempId);
                break;
            case "HALLOWEEN_EVENT_2":
                buyItemHalloweenEvent(player, tempId);
                break;
            case "INTERNATIONAL_WOMANS_DAY2":
                buyItem8_3Event(player, tempId);
                break;
            case "TRUNG_THU_EVENT_2":
                buyItemTrungThuEvent(player, tempId);
                break;
            case "TRUNG_THU_EVENT_DUOIKHI":
                buyItemTrungThuEvent_DuoiKhi(player, tempId);
                break;
            case "TRUNG_THU_EVENT_CAROT":
                buyItemTrungThuEvent_Carot(player, tempId);
                break;
            case "HUNG_VUONG_EVENT2":
                buyItemHungVuongEvent(player, tempId);
                break;
            case "BLACK_FRIDAY_EVENT":
                buyItemFlashSaleEventBlackFriday(player, tempId);
                break;
            case "BLACK_FRIDAY_EVENT2":
                buyItemBlackFridayEvent(player, tempId);
                break;
            case "BLACK_FRIDAY_SALE":
                buyItemBlackFridayToPoint(player, tempId);
                break;
            case "SHOP_BIEU_TUONG_BANG":
                buyItemBieuTuongBang(player, tempId);
                break;
            case "20_10_EVENT_2":
                buyItem20_10Event(player, tempId);
                break;
            default:
                buyItem(player, tempId);
                break;
        }
        Service.gI().sendMoney(player);
    }

    private boolean subMoneyByItemShop(Player player, ItemShop is) {
        int gold = 0;
        int gem = 0;
        int ruby = 0;
        int even = 0;
        switch (is.typeSell) {
            case COST_GOLD:
                gold = is.cost;
                break;
            case COST_GEM:
                gem = is.cost;
                break;
            case COST_RUBY:
                ruby = is.cost;
                break;
            case COST_EVENT:
                even = is.cost;
                break;
        }
        if (player.inventory.gold < gold) {
            Service.gI().sendThongBao(player, "Bạn không có đủ vàng");
            return false;
        } else if (player.inventory.gem < gem) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
            return false;
        } else if (player.inventory.ruby < ruby) {
            Service.gI().sendThongBao(player, "Bạn không có đủ hồng ngọc");
            return false;
        }
        player.inventory.gold -= gold;
        player.inventory.gem -= gem;
        player.inventory.ruby -= ruby;
        return true;
    }
    
    private boolean subIemByItemShopByUpdate(Player pl, ItemShop itemShop) {
        boolean isBuy = false;
        short itSpec = ItemService.gI().getItemIdByIcon((short) itemShop.iconSpec);
        int buySpec = itemShop.cost;
        Item itS = ItemService.gI().createNewItem(itSpec);
        switch (itS.template.id) {
            case 76:
            case 188:
            case 189:
            case 190:
                if (pl.inventory.gold >= buySpec) {
                    pl.inventory.gold -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không đủ vàng, còn thiếu " + Util.formatNumber(buySpec - pl.inventory.gold, FormatStyle.VIETNAMESE) + " vàng nữa");
                    isBuy = false;
                }
                break;
            case 77:
                if (pl.inventory.gem >= buySpec) {
                    pl.inventory.gem -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không đủ ngọc, còn thiếu " + Util.formatNumber(buySpec - pl.inventory.gem, FormatStyle.VIETNAMESE) + " ngọc nữa");
                    isBuy = false;
                }
                break;
            case 861:
                if (pl.inventory.ruby >= buySpec) {
                    pl.inventory.ruby -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không đủ hồng ngọc, còn thiếu " + Util.formatNumber(buySpec - pl.inventory.ruby, FormatStyle.VIETNAMESE) + " hồng ngọc nữa");
                    isBuy = false;
                }
                break;
            default:
                if (InventoryService.gI().findItemBag(pl, itSpec) == null || !InventoryService.gI().findItemBag(pl, itSpec).isNotNullItem()) {
                    Service.gI().sendThongBao(pl, "Không tìm thấy " + itS.template.name);
                    isBuy = false;
                } else if (InventoryService.gI().findItemBag(pl, itSpec).quantity < buySpec) {
                    Service.gI().sendThongBao(pl, "Bạn không có đủ " + buySpec + " " + itS.template.name);
                    isBuy = false;
                } else {
                    InventoryService.gI().subQuantityItemsBag(pl, InventoryService.gI().findItemBag(pl, itSpec), buySpec);
                    isBuy = true;
                }
                break;
        }
        return isBuy;
    }

    private boolean subMoneyByItemShopByUpdate(Player player, ItemShop is) {
        int gold = 0;
        int gem = 0;
        int ruby = 0;
        int even = 0;
        switch (is.typeSell) {
            case COST_GOLD:
                gold = is.cost;
                break;
            case COST_GEM:
                gem = is.cost;
                break;
            case COST_RUBY:
                ruby = is.cost;
                break;
            case COST_EVENT:
                even = is.cost;
                break;
        }
        if (player.inventory.gold < gold) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ vàng, còn thiếu " + Util.formatNumber(gold - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng nữa");
            return false;
        } else if (player.inventory.gem < gem) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ ngọc, còn thiếu " + Util.formatNumber(gem - player.inventory.gem, FormatStyle.VIETNAMESE) + " ngọc nữa");
            return false;
        } else if (player.inventory.ruby < ruby) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ hồng ngọc, còn thiếu " + Util.formatNumber(ruby - player.inventory.ruby, FormatStyle.VIETNAMESE) + " hồng ngọc nữa");
            return false;
        }
        player.inventory.gold -= gold;
        player.inventory.gem -= gem;
        player.inventory.ruby -= ruby;
        Service.gI().sendMoney(player);
        return true;
    }

    /**
     * Mua bùa
     *
     * @param player người chơi
     * @param itemTempId id template vật phẩm
     */
    private void buyItemBua(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (!subMoneyByItemShop(player, is)) {
            return;
        }
        InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
        InventoryService.gI().sendItemBag(player);
        opendShop(player, shop.tagName, true);
    }
    
    private long addCharmTime(long currentExpire, long addMillis) {
        long now = System.currentTimeMillis();
        if (currentExpire > now) {
            return currentExpire + addMillis;
        } else {
            return now + addMillis;
        }
    }
    
    private void buyItemBuaClan(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (player.clan == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (player.clan.capsuleClan < is.cost) {
            Service.gI().sendThongBao(player, "Bang hội không đủ capsule bang, còn thiếu " + Util.format(is.cost - player.clan.capsuleClan) + " capsule bang nữa.");
            return;
        }
        if (itemTempId == 797) {
            player.clan.BuaTriTue = addCharmTime(player.clan.BuaTriTue, 3600000L);
        }
        if (itemTempId == 798) {
            player.clan.BuaManhMe = addCharmTime(player.clan.BuaManhMe, 3600000L);
        }
        if (itemTempId == 799) {
            player.clan.BuaDaTrau = addCharmTime(player.clan.BuaDaTrau, 3600000L);
        }
        player.clan.capsuleClan -= is.cost;
        for (ClanMember cm : player.clan.getMembers()) {
            Player pls = Client.gI().getPlayerByID(cm.id);
            if (pls != null) {
                ClanService.gI().sendMyClan(pls);
                ClanService.gI().updateClanMembersToDB(pls.clan);
            }
        }
        player.clan.updateClanBasicInfo();
        player.clan.updateClanDataCharms(player.clan.id, player.clan.BuaTriTue, player.clan.BuaManhMe, player.clan.BuaDaTrau);
        InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn đã mua thành công " + is.temp.name);
        opendShop(player, shop.tagName, true);
    }

    /**
     * Mua vật phẩm trong cửa hàng
     *
     * @param player người chơi
     * @param itemTempId id template vật phẩm
     */
    public void buyItem(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int[][] listDauThan = {{13, 293}, {60, 294}, {61, 295}, {62, 296}, {63, 297}, {64, 298}, {65, 299}, {352, 596}, {523, 597}};
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }

        if (itemTempId == 711 && !InventoryService.gI().findItemSkinQuyLaoKame(player)) {
            Service.gI().sendThongBao(player, "Bạn phải có cải trang thành Quy Lão Kame mới có thể đổi.");
            return;
        }
        
        if (buyMoRongHanhTrang(player, is)) {
            return;
        }
        
        if (shop.typeShop == ShopService.LEARN_SKILL) {
            learnKyNang(player, is);
            return;
        }
        
        if (is.tabShop.id == 24) {
            buyDanhHieu(player, is);
            return;
        }
        if (is.tabShop.id == 25) {
            changeDanhHieu(player, is);
            return;
        }

        if (shop.typeShop == ShopService.NORMAL_SHOP) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        } else if (shop.typeShop == ShopService.SPEC_SHOP) {
            if (!this.subIemByItemShop(player, is)) {
                return;
            }
        } else if (shop.typeShop == ShopService.POINT_SHOP) {
            if (!this.subIemByItemShop(player, is)) {
                return;
            }
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        item = buyMagicPean(player, listDauThan, item);
        if (item.template.id == 1523 || item.template.id == 1524) {
            item = ItemService.gI().createNewItem((short) 521);
            item.itemOptions.addAll(is.options);
        }
        if (item.template.id == 193) {
            item = ItemService.gI().createNewItem((short) 193);
            item.quantity = 10;
        }
        if (item.template.id == 361) {
            item = ItemService.gI().createNewItem((short) 361);
            item.quantity = 10;
        }
        if (item.template.id == 1784 || item.template.id == 1798) {
            if (!player.isNewMember) {
                Service.gI().sendThongBao(player, "Bạn đã hết thời gian có thể gia hạn mầm!");
                return;
            }
        }
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
    }

    private boolean subIemByItemShop(Player pl, ItemShop itemShop) {
        boolean isBuy = false;
        short itSpec = ItemService.gI().getItemIdByIcon((short) itemShop.iconSpec);
        int buySpec = itemShop.cost;
        Item itS = ItemService.gI().createNewItem(itSpec);
        switch (itS.template.id) {
            case 76:
            case 188:
            case 189:
            case 190:
                if (pl.inventory.gold >= buySpec) {
                    pl.inventory.gold -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không đủ vàng, còn thiếu " + Util.formatNumber(buySpec - pl.inventory.gold, FormatStyle.VIETNAMESE) + " vàng nữa");
                    isBuy = false;
                }
                break;
            case 77:
                if (pl.inventory.gem >= buySpec) {
                    pl.inventory.gem -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không đủ ngọc, còn thiếu " + Util.formatNumber(buySpec - pl.inventory.gem, FormatStyle.VIETNAMESE) + " ngọc nữa");
                    isBuy = false;
                }
                break;
            case 861:
                if (pl.inventory.ruby >= buySpec) {
                    pl.inventory.ruby -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không đủ hồng ngọc, còn thiếu " + Util.formatNumber(buySpec - pl.inventory.ruby, FormatStyle.VIETNAMESE) + " hồng ngọc nữa");
                    isBuy = false;
                }
                break;
            default:
                if (InventoryService.gI().findItemBag(pl, itSpec) == null || !InventoryService.gI().findItemBag(pl, itSpec).isNotNullItem()) {
                    Service.gI().sendThongBao(pl, "Không tìm thấy " + itS.template.name);
                    isBuy = false;
                } else if (InventoryService.gI().findItemBag(pl, itSpec).quantity < buySpec) {
                    Service.gI().sendThongBao(pl, "Bạn không có đủ " + buySpec + " " + itS.template.name);
                    isBuy = false;
                } else {
                    InventoryService.gI().subQuantityItemsBag(pl, InventoryService.gI().findItemBag(pl, itSpec), buySpec);
                    isBuy = true;
                }
                break;
        }
        return isBuy;
    }

   public void showConfirmSellItem(Player pl, int where, int index) {
    Item item;
    if (where == 0) {
        if (index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        item = pl.inventory.itemsBody.get(index);
    } else {
        if (pl.getSession().version < 220) {
            index -= (pl.inventory.itemsBody.size() - 7);
        }
        item = pl.inventory.itemsBag.get(index);
    }

    if (item != null && item.isNotNullItem()) {
        if (item.template.id == 570 || item.template.id == 457) {
            Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm này");
            return;
        }

        int quantity = item.quantity;
        int cost = item.template.goldSell;

        cost /= 4;

        if (cost == 0) {
            cost = 1;
        }
        cost *= quantity;

        String text = "Bạn có muốn bán\nx" + quantity
                + " " + item.template.name + "\nvới giá là "
                + Util.formatNumber(cost, FormatStyle.VIETNAMESE) + " vàng?";

        Message msg = null;
        try {
            msg = new Message(7);
            msg.writer().writeByte(where);
            msg.writer().writeShort(index);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
        } catch (IOException e) {
            Logger.logException(ShopService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
}

   public void sellItem(Player pl, int where, int index) {
    if (pl.iDMark.getShopOpen() == null || pl.iDMark.getTagNameShop() == null) {
        Service.gI().sendThongBao(pl, "Không thể thực hiện");
        return;
    }

    if (index < 0) {
        Service.gI().sendThongBao(pl, "Không thể thực hiện");
        return;
    }

    Item item;
    if (where == 0) {
        item = pl.inventory.itemsBody.get(index);
    } else {
        item = pl.inventory.itemsBag.get(index);
    }

    if (item != null && item.isNotNullItem()) {
        if (item.template.id == 570 || item.template.id == 457) {
            Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm này");
            return;
        }

        if (InventoryService.gI().getParam(pl, 93, item.template.id) > 0) {
            Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm có hạn sử dụng");
            return;
        }

        int quantity = item.quantity;
        int cost = item.template.goldSell;

        cost /= 4;

        if (cost == 0) {
            cost = 1;
        }
        cost *= quantity;

        if (pl.inventory.gold + cost > Inventory.LIMIT_GOLD) {
            Service.gI().sendThongBao(pl, "Vàng sau khi bán vượt quá giới hạn");
            return;
        }

        pl.inventory.gold += cost;
        Service.gI().sendMoney(pl);
        Service.gI().sendThongBao(pl, "Đã bán " + item.template.name
                + " thu được " + Util.formatNumber(cost, FormatStyle.VIETNAMESE) + " vàng");

        BuyBack.gI().addItem(pl, item);

        if (where == 0) {
            InventoryService.gI().subQuantityItemsBody(pl, item, quantity);
            InventoryService.gI().sendItemBody(pl);
            Service.gI().Send_Caitrang(pl);
        } else {
            InventoryService.gI().subQuantityItemsBag(pl, item, quantity);
            InventoryService.gI().sendItemBag(pl);
        }

        if ("BUNMA".equals(pl.iDMark.getTagNameShop())
                || "DENDE".equals(pl.iDMark.getTagNameShop())
                || "APPULE".equals(pl.iDMark.getTagNameShop())) {
            AchievementService.gI().checkDoneTask(pl, ConstAchievement.TRUM_NHAT_VE_CHAI);
        }
    } else {
        Service.gI().sendThongBao(pl, "Không thể thực hiện");
    }
}

    private void getItemSideBoxLuckyRound(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index < 0 || index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item);
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.formatNumber(item.quantity, FormatStyle.VIETNAMESE) + " vàng" : item.template.name));
                        InventoryService.gI().sendItemBag(player);
                        items.remove(index);
                    } else {
                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                    }
                } else {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                items.remove(index);
                Service.gI().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = items.size() - 1; i >= 0; i--) {
                    item = items.get(i);
                    if (InventoryService.gI().addItemBag(player, item)) {
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.formatNumber(item.quantity, FormatStyle.VIETNAMESE) + " vàng" : item.template.name));
                        items.remove(i);
                    }
                }
                InventoryService.gI().sendItemBag(player);
                break;
        }
        openShopType4(player, player.iDMark.getTagNameShop(), items);
    }

    private void buyItemDaBan(Player player, List<Item> items, int index) {
        if (items == null) {
            return;
        }
        if (index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        int giamualaingoc = item.template.gemSell / 2;
        int giamualaivang = giamualaingoc == 0 ? (int) item.template.goldSell / 2 > 0 ? (int) item.template.goldSell / 2 : item.quantity * 100 : 0;
        if (giamualaivang > 0 && player.inventory.gold < giamualaivang) {
            Service.gI().sendThongBao(player, "Bạn không có đủ vàng!");
            return;
        }
        if (giamualaingoc > 0 && player.inventory.gem < giamualaingoc) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc xanh!");
            return;
        }
        player.inventory.gem -= giamualaingoc;
        player.inventory.gold -= giamualaivang;
        Service.gI().sendMoney(player);
        if (item.isNotNullItem()) {
            if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                InventoryService.gI().addItemBag(player, item);
                Service.gI().sendThongBao(player, "Bạn nhận được " + (item.template.id == 189 ? Util.formatNumber(item.quantity, FormatStyle.VIETNAMESE) + " vàng" : item.template.name));
                InventoryService.gI().sendItemBag(player);
                items.remove(index);
            } else {
                Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            }
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
        openShopType8(player, player.iDMark.getTagNameShop(), items);
    }

    private void buyItemHuyDiet(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (!subIemByItemShopByUpdate(player, is)) {
            return;
        }
        if (item.template.level == 14) {
            Item doAn = player.inventory.itemsBag.stream().filter(it -> it != null && it.template != null && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 || it.template.id == 667) && it.quantity >= 99).findFirst().orElse(null);
            if (doAn != null) {
                InventoryService.gI().subQuantityItemsBag(player, doAn, 99);
            } else {
                Service.gI().sendThongBao(player, "Không có đủ thức ăn");
                return;
            }
        }
        if (player.inventory.itemsBody.get(0) != null || player.inventory.itemsBody.get(1) != null || player.inventory.itemsBody.get(2) != null || player.inventory.itemsBody.get(3) != null || player.inventory.itemsBody.get(4) != null || player.inventory.itemsBody.get(5) != null) {
            Item dothan = player.inventory.itemsBody.stream().filter(it -> it != null && it.template != null && it.template.level == 13).findFirst().orElse(null);
            if (dothan == null) {
                Service.gI().sendThongBao(player, "Không có đủ set thần");
                return;
            }
        }
        int param = 0;
        if (item.template.level == 14) {
            if (Util.isTrue(15, 100)) {
                param = Util.nextInt(11, 15);
            } else if (Util.isTrue(35, 75)) {
                param = Util.nextInt(5, 10);
            } else {
                param = Util.nextInt(0, 4);
            }
        }
        List<ItemOption> itemoptions = new ArrayList<>();
        if (!item.itemOptions.isEmpty()) {
            for (ItemOption ios : item.itemOptions) {
                if (item.template.level == 14 && InventoryService.gI().optionCanUpgrade(ios.optionTemplate.id) && param > 0) {
                    int id = ios.optionTemplate.id;
                    int param1 = ios.param + (ios.param * param) / 100;
                    itemoptions.add(new ItemOption(id, param1));
                } else if (ios.optionTemplate.id != 164) {
                    itemoptions.add(new ItemOption(ios.optionTemplate.id, ios.param));
                }
            }
        } else {
            itemoptions.add(new ItemOption(73, (short) 0));
        }
        item.itemOptions.clear();
        item.itemOptions.addAll(itemoptions);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
    }
        
    private void buyDanhHieu(Player pl, ItemShop is) {
        int idBadgesCanBuy = BagesTemplate.fineIdEffectbyIdItem(is.temp.id);
        for (BadgesData bg : pl.dataBadges) {
            if (bg.idBadGes == idBadgesCanBuy) {
                Service.gI().sendThongBao(pl, "Bạn đã sở hữu danh hiệu này rồi.");
                return;
            }
        }
        BadgesTask task = null;
        for (BadgesTask data : pl.dataTaskBadges) {
            if (data.idBadgesReward == idBadgesCanBuy) {
                task = data;
                break;
            }
        }
        if (task != null) {
            if (task.isDone()) {
                BadgesData newBadge = new BadgesData(pl, idBadgesCanBuy, 30);
                pl.dataBadges.add(newBadge);
                Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được danh hiệu: " + is.temp.name);
            } else {
                String taskName = "N/A";
                for (BadgesTaskTemplate btt : Manager.TASKS_BADGES_TEMPLATE) {
                    if (btt.idbadgesReward == idBadgesCanBuy) {
                        taskName = btt.name;
                        break;
                    }
                }

                String thongBao = "Bạn chưa hoàn thành yêu cầu để nhận danh hiệu này.\n";
                thongBao += "Nhiệm vụ: " + taskName + "\b";
                thongBao += "Tiến độ: " + task.count + " / " + task.countMax;
                NpcService.gI().createTutorial(pl, 2993, thongBao);
            }
        }
    }

    private void changeDanhHieu(Player pl, ItemShop is) {
        if (pl.lastTimeChangeBadges - System.currentTimeMillis() > 0) {
            Service.gI().sendThongBao(pl, "Vui lòng đợi " + (pl.lastTimeChangeBadges - System.currentTimeMillis()) / 1000 + " giây nữa");
            return;
        }
        if (pl.badges.idBadges == BagesTemplate.fineIdEffectbyIdItem(is.temp.id)) {
            Service.gI().sendThongBao(pl, "Danh hiệu đang được sữ dụng, hãy chọn danh hiệu khác");
            pl.lastTimeChangeBadges = System.currentTimeMillis() + 30000;
            return;
        }
        BadgesService.turnOnBadges(pl, BagesTemplate.fineIdEffectbyIdItem(is.temp.id));
        Service.gI().sendThongBao(pl, "Đã đổi danh hiệu sang " + is.temp.name);
        pl.lastTimeChangeBadges = System.currentTimeMillis() + 30000;
    }
    
    private boolean buyMoRongHanhTrang(Player player, ItemShop itemShop) {
        boolean isBuy = false;
        if (itemShop.temp.id == 518 || itemShop.temp.id == 517 || itemShop.temp.id == 988 || itemShop.temp.id == 1627 || itemShop.temp.id == 457) {
            if (itemShop.temp.id == 1627 && player.inventory.itemsBag.size() >= 150) {
                Service.gI().sendThongBao(player, "Đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (itemShop.temp.id == 517 && player.inventory.itemsBag.size() >= 100) {
                Service.gI().sendThongBao(player, "Đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (itemShop.temp.id == 518 && player.inventory.itemsBox.size() >= 100) {
                Service.gI().sendThongBao(player, "Đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (itemShop.temp.id == 988 && player.inventory.getGoldLimit() >= 200_000_000_000L) {
                Service.gI().sendThongBao(player, "Số vàng trong hành trang đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (subMoneyByItemShop(player, itemShop)) {
                Item item = ItemService.gI().createItemFromItemShop(itemShop);
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                opendShop(player, itemShop.tabShop.shop.tagName, true);
                Service.gI().sendThongBao(player, "Bạn đã mua thành công");
            }
            isBuy = true;
        }
        return isBuy;
    }
    
    private Item buyMagicPean(Player player, int[][] listDauThan, Item item) {
        for (int[] listDauThan1 : listDauThan) {
            if (item.template.id == listDauThan1[1]) {
                item = ItemService.gI().createNewItem((short) listDauThan1[0]);
                item.itemOptions.add(new ItemOption(player.magicTree.level - 1 > 1 ? 2 : 48, MagicTree.PEA_PARAM[player.magicTree.level - 1]));
                item.quantity = 30;
                return item;
            }
        }
        return item;
    }
    
    private void getItemSideMailsBox(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index < 0 || index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item);
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.formatNumber(item.quantity, FormatStyle.VIETNAMESE) + " vàng" : item.template.name));
                        InventoryService.gI().sendItemBag(player);
                        items.remove(index);
                    } else {
                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                    }
                } else {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                items.remove(index);
                Service.gI().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = items.size() - 1; i >= 0; i--) {
                    item = items.get(i);
                    if (InventoryService.gI().addItemBag(player, item)) {
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.formatNumber(item.quantity, FormatStyle.VIETNAMESE) + " vàng" : item.template.name));
                        items.remove(i);
                    }
                }
                InventoryService.gI().sendItemBag(player);
                break;
        }
        openShopType4(player, player.iDMark.getTagNameShop(), items);
    }
    
    private void buyItemShopWhis(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getHakaiPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 76) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subHakaiPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm huỷ diệt");
            }
        }
    }
    
    private void buyItemChienTruongNamec(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getNamekWarPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 76) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subNamekWarPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm chiến trường namek");
            }
        }
    }
    
    private void buyItemLunaNewYearEvent(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getLunaNewYearPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subLunaNewYearPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện tết.");
            }
        }
    }
    
    private void buyItemVongKimCo(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        Item VongKimCo = InventoryService.gI().findItemBag(player, 543);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (!subIemByItemShopByUpdate(player, is)) {
            return;
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        if (item.template.id == 543) {
            if (!InventoryService.gI().findItemVongKimCo(player)) {
                item = ItemService.gI().createNewItem((short) 543);
                item.itemOptions.addAll(is.options);
            } else {
                if (VongKimCo != null) {
                    VongKimCo.addOptionParam(12, 1);
                } else {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                }
            }
        }
        if (VongKimCo == null || item.template.id != 543) {
            InventoryService.gI().addItemBag(player, item);
        }
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
    }
    
    /**
     * Mua vật phẩm trong cửa hàng
     *
     * @param player người chơi
     * @param itemTempId id template vật phẩm
     */
    private void learnKyNang(Player pl, ItemShop is) {
        if (pl.nPoint.power < is.temp.strRequire) {
            Service.gI().sendThongBao(pl, "Sức mạnh của bạn không đủ");
            return;
        }
        if (pl.nPoint.tiemNang < is.cost) {
            Service.gI().sendThongBao(pl, "Bạn không đủ tiềm năng để học chiêu thức này");
            return;
        }
        var skillPlayer = pl.playerSkill.getSkillbyId(SkillUtil.getSkillByItemID(pl, is.temp.id).template.id);
        String[] subName = is.temp.name.split("");
        byte level = Byte.parseByte(subName[subName.length - 1]);
        if (skillPlayer != null) {

            if (skillPlayer.point >= level) {
                Service.gI().sendThongBao(pl, "Bạn đã học kỹ năng này rồi");
                return;

            }
            if (level - skillPlayer.point != 1) {
                Service.gI().sendThongBao(pl, "Bạn chưa thể học kỹ năng này");
                return;
            }
        }
        if (pl.BoughtSkill.contains(is.temp.id)) {
            Service.gI().sendThongBao(pl, "Bạn đã học kỹ năng này rồi");
            return;
        }
        ArrayList<String> menu = new ArrayList<>();
        menu.add("Yes");
        menu.add("No");
        String[] menus = menu.toArray(String[]::new);
        long[] time = new long[]{900000, 1800000, 3600000, 86400000, 259200000, 604800000, 1296000000};
        var timeStudy = "";
        var timeLong = time[level - 1];
        switch (level) {
            case 0:
            case 1:
            case 2:
                timeStudy = TimeUtil.convertMillisecondToMinute(timeLong);
                break;
            case 3:
                timeStudy = TimeUtil.convertMillisecondToHour(timeLong);
                break;
            default:
                timeStudy = TimeUtil.convertMillisecondToDay(timeLong);
                break;
        }
        var skillTemplateId = SkillUtil.getTempSkillSkillByItemID(is.temp.id);

        var potential = SkillUtil.findSkillTemplate(skillTemplateId).skillss.stream()
                .filter(s -> s.point == level)
                .findFirst()
                .map(s -> (long) s.powRequire) // Ép kiểu Long -> int
                .orElse(0L); // Giá trị mặc định là int
        String text = "Con có muốn học kỹ năng " + SkillUtil.findSkillTemplate(SkillUtil.getTempSkillSkillByItemID(is.temp.id)).name + " cấp " + level + "\nCần " + Util.formatNumber(potential, FormatStyle.VIETNAMESE) + " điểm tiềm năng và thời gian học là " + timeStudy;
        pl.LearnSkill.ItemTemplateSkillId = is.temp.id;
        pl.LearnSkill.Time = -1;
        pl.LearnSkill.Potential = potential;

        NpcService.gI().createMenuConMeo(pl, ConstNpc.HOC_SKILL_3, NpcService.gI().getAvatar(13 + pl.gender), text, menus);
    }
    
    private void buyItemChristMasEvent(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getChristMasPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subChristMasPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện noel.");
            }
        }
    }
    
    private void buyItemHalloweenEvent(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getHalloweenPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subHalloweenPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện halloween.");
            }
        }
    }
    
    private void buyItem8_3Event(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getInternationalWomensDayPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subInternationalWomensDayPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện 8-3.");
            }
        }
    }
    
    private void buyItemTrungThuEvent(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getTrungThuPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subTrungThuPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện trung thu.");
            }
        }
    }
    
    private void buyItemTrungThuEvent_DuoiKhi(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        Item DuoiKhi = InventoryService.gI().findItemBag(player, 1045);
        short[] Param = {3, 7, 15, 30, 45};
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (DuoiKhi != null) {
            if (DuoiKhi.quantity > is.cost) {
                switch (itemTempId) {
                    case 1687:
                        is.options.add(new ItemOption(77, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(103, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(94, 11));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1551:
                        is.options.add(new ItemOption(50, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(77, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(103, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(94, 11));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1311:
                        is.options.add(new ItemOption(30, 0));
                        is.options.add(new ItemOption(93, 35));
                        break;
                    case 528:
                        is.options.add(new ItemOption(8, 3));
                        is.options.add(new ItemOption(50, 18));
                        is.options.add(new ItemOption(77, 15));
                        is.options.add(new ItemOption(103, 15));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1213:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(108, 15));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1223:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(94, 11));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1301:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(14, 11));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 920:
                        is.options.add(new ItemOption(84, 0));
                        is.options.add(new ItemOption(77, 10));
                        is.options.add(new ItemOption(103, 10));
                        is.options.add(new ItemOption(114, 25));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1144:
                        is.options.add(new ItemOption(84, 0));
                        is.options.add(new ItemOption(14, 5));
                        is.options.add(new ItemOption(114, 25));
                        is.options.add(new ItemOption(106, 0));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1046:
                        is.options.add(new ItemOption(50, 16));
                        is.options.add(new ItemOption(77, 16));
                        is.options.add(new ItemOption(103, 16));
                        is.options.add(new ItemOption(94, 11));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1207:
                        is.options.add(new ItemOption(50, 16));
                        is.options.add(new ItemOption(77, 15));
                        is.options.add(new ItemOption(103, 15));
                        is.options.add(new ItemOption(5, 18));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    default:
                        break;
                }
                InventoryService.gI().subQuantityItemsBag(player, DuoiKhi, is.cost);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                player.event.addTrungThuPoint(1);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (is.cost - DuoiKhi.quantity) + " Đuôi khỉ.");
            }
        } else {
            Service.gI().sendThongBao(player, "Bạn không có Đuôi khỉ.");
        }
    }
    
    private void buyItemTrungThuEvent_Carot(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        Item Carot = InventoryService.gI().findItemBag(player, 462);
        short[] Param = {3, 7, 15, 30, 45};
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (Carot != null) {
            if (Carot.quantity > is.cost) {
                switch (itemTempId) {
                    case 1686:
                        is.options.add(new ItemOption(50, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(77, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(103, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(236, Util.nextInt(15, 17)));
                        is.options.add(new ItemOption(14, 10));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1311:
                        is.options.add(new ItemOption(30, 0));
                        is.options.add(new ItemOption(93, 35));
                        break;
                    case 464:
                        is.options.add(new ItemOption(50, 15));
                        is.options.add(new ItemOption(117, 10));
                        is.options.add(new ItemOption(114, 25));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 463:
                        is.options.add(new ItemOption(101, 20));
                        is.options.add(new ItemOption(114, 25));
                        is.options.add(new ItemOption(115, 0));
                        is.options.add(new ItemOption(116, 0));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1309:
                        is.options.add(new ItemOption(50, 24));
                        is.options.add(new ItemOption(77, 21));
                        is.options.add(new ItemOption(103, 21));
                        is.options.add(new ItemOption(94, 13));
                        is.options.add(new ItemOption(80, 15));
                        is.options.add(new ItemOption(236, 18));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 908:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(14, 11));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 909:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(94, 13));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1039:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(101, 20));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    case 1040:
                        is.options.add(new ItemOption(50, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(77, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(103, Util.nextInt(14, 16)));
                        is.options.add(new ItemOption(236, 20));
                        if (Util.isTrue(95, 100)) {
                            is.options.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                        }
                        break;
                    default:
                        break;
                }
                InventoryService.gI().subQuantityItemsBag(player, Carot, is.cost);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                player.event.addTrungThuPoint(1);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (is.cost - Carot.quantity) + " Củ cà rốt.");
            }
        } else {
            Service.gI().sendThongBao(player, "Bạn không có Củ cà rốt.");
        }
    }
    
    private void buyItemHungVuongEvent(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getHungVuongPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subHungVuongPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện giỗ tổ hùng vương.");
            }
        }
    }
    
    private void buyItemFlashSaleEventBlackFriday(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (!subIemByItemShopByUpdate(player, is)) {
            return;
        }
        if (itemTempId == 711) {
            if (!InventoryService.gI().findItemSkinQuyLaoKame(player)) {
                Service.gI().sendThongBao(player, "Bạn phải mua cải trang Quy lão trước đã");
                return;
            }
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn đã mua thành công " + item.template.name);
    }
    
    private void buyItemBlackFridayEvent(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getBlackFridayPoint();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subBlackFridayPoint(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện black friday.");
            }
        }
    }
    
    private void buyItemBlackFridayToPoint(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (!subIemByItemShopByUpdate(player, is)) {
            return;
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        player.DuaTopMuaSamBlackFriday += is.cost;
        Service.gI().sendThongBao(player, "Bạn đã mua thành công " + item.template.name);
    }
    
    private void buyItemBieuTuongBang(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (player.clan == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (player.clan.capsuleClan < is.cost) {
            Service.gI().sendThongBao(player, "Bang hội không đủ capsule bang, còn thiếu " + Util.format(is.cost - player.clan.capsuleClan) + " capsule bang nữa.");
            return;
        }
        player.clan.capsuleClan -= is.cost;
        player.clan.imgId = is.temp.part;
        for (ClanMember cm : player.clan.getMembers()) {
            Player pls = Client.gI().getPlayerByID(cm.id);
            if (pls != null) {
                ClanService.gI().sendMyClan(pls);
                ClanService.gI().updateClanMembersToDB(pls.clan);
            }
        }
        player.clan.updateClanBasicInfo();
        Service.gI().sendThongBao(player, "Bạn đã mua thành công " + is.temp.name);
    }
    
    private void buyItem20_10Event(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.get20_10Point();
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 164) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.sub20_10Point(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm sự kiện 20/10.");
            }
        }
    }

}
