package nro.inventory;

import Utils.Logger;
import Utils.TimeUtil;
import nro.server.Client;
import models.Item.Item;
import models.Item.ItemMapService;
import models.Item.ItemService;
import nro.npc.Special.BillEgg;
import nro.npc.Special.MabuEgg;
import nro.player.Detu;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.Input;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nro.services.PlayerService;
import nro.services.Service;
import consts.ConstPlayer;
import jbcd.ConnectDB;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import network.io.Message;
import nro.giftcode.GiftCode;
import nro.giftcode.GiftCodeManager;
import nro.map.BlackBallWar.BlackBallWarService;
import nro.map.DragonBallNamec.NgocRongNamec;
import models.Item.ItemOption;
import nro.clan.Clan;
import nro.clan.ClanMember;
import nro.map.Zone;
import jbcd.CrisResultSet;
import jbcd.data.DatabaseUpdater;
import static nro.server.Manager.player;

public class InventoryService {

    private static InventoryService I;

    public static InventoryService gI() {
        if (InventoryService.I == null) {
            InventoryService.I = new InventoryService();
        }
        return InventoryService.I;
    }

    public boolean findItemBinhHutNangLuong(Player player) {
        if (player.isPl()) {
            for (Item item : player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1911) {
                    return true;
                }
            }
        }
        return false;
    }

    //for giftcode
    public void addItemGiftCodeToPlayer(Player p, GiftCode giftcode, String code) throws Exception {
        Set<Integer> keySet = giftcode.detail.keySet();
        String textGift = "|7|Giftcode: " + code + "\n" + "|6|Phần Thưởng Của Bạn Là :\b";
        CrisResultSet rs = ConnectDB.executeQuery("SELECT * FROM `giftcode` WHERE `code` = '" + code + "';");
        if (rs != null && rs.first()) {
            for (Integer key : keySet) {
                int idItem = key;
                int quantity = giftcode.detail.get(key);
                switch (idItem) {
                    case -1:
                        p.inventory.gold = Math.min(p.inventory.gold + (long) quantity, 2000000000L);
                        textGift += quantity + " vàng\b";
                        break;
                    case -2:
                        p.inventory.gem = Math.min(p.inventory.gem + quantity, 200000000);
                        textGift += quantity + " ngọc\b";
                        break;
                    case -3:
                        p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 200000000);
                        textGift += quantity + " ngọc khóa\b";
                        break;
                    case -10:
                        p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 200000000);
                        textGift += quantity + " ngọc khóa\b";
                        break;
                    case -6:
//                        p.titleitem = true;
//                        p.dhtang1 = true;
//                        p.dhtang2 = true;
//                        Service.gI().point(p);
//                        Service.gI().sendTitle(p, "VECHAI");
//                        Service.gI().sendTitle(p, "FANCUNG");
//                        textGift += "x1 Danh Hiệu Ve Chai\nx1 Danh Hiệu Fan Cứng\b";
                        break;
                    case -7:
//                        p.titlett = true;
//                        p.usedh1 = true;
//                        p.usedh2 = true;
//                        p.usedh3 = true;
//                        p.timedh1 = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 1);
//                        p.timedh2 = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 1);
//                        p.timedh3 = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 1);
//                        Service.gI().point(p);
//                        Service.gI().sendTitle(p, "DH1");
//                        Service.gI().sendTitle(p, "DH2");
//                        Service.gI().sendTitle(p, "DH3");
//                        textGift += "x1 Danh Hiệu Bất Bại\nx1 Danh Hiệu Đại Thần\nx1 Danh Hiệu Trùm Cuối\b";
                        break;
                    default:
                        Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);
                        if (itemGiftTemplate != null) {
                            Item itemGift = new Item((short) idItem);
                            if (itemGift.template.type == 12 || itemGift.template.type >= 0 && itemGift.template.type <= 5) {
                                if (itemGift.template.id == 457) {
                                    itemGift.itemOptions.add(new ItemOption(30, 0));
                                    addItemBag(p, itemGift);
                                    sendItemBag(p);
                                } else {
                                    itemGift.itemOptions = giftcode.option;
                                    itemGift.quantity = quantity;
                                    addItemBag(p, itemGift);
                                    sendItemBag(p);
                                }
                            } else {
                                itemGift.itemOptions = giftcode.option;
                                itemGift.quantity = quantity;
                                addItemBag(p, itemGift);
                                sendItemBag(p);
                            }
                            textGift += "x" + quantity + " " + itemGift.template.name + "\b";
                            GiftCodeManager.gI().checkUseGiftCode((int) p.id, code);
                            int trucount = rs.getInt("count_left") - 1;
                            ConnectDB.executeUpdate("UPDATE `giftcode` SET `count_left` = '" + trucount + "' WHERE `code` = '" + code + "' LIMIT 1;");
                        }
                        break;
                }
            }
            String xuatsql = "(" + p.getSession().userId + ", '" + p.id + "', '" + code + "', '" + Util.toDateString(Date.from(Instant.now())) + "');";
            ConnectDB.executeUpdate("INSERT INTO `giftcode_save` (`id`,`player_id`,`code_da_nhap`,`tgian_nhap`) VALUES " + xuatsql);
            Service.gI().sendThongBaoFromAdmin(p, textGift);
        }
    }

    private void __________________Tìm_kiếm_item_____________________________() {
        //**********************************************************************
    }

    public Item findItem(List<Item> list, int tempId) {
        try {
            for (Item item : list) {
                if (item.isNotNullItem() && item.template.id == tempId) {
                    return item;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Item findItemEvent(List<Item> list, int TypeEvent) {
        try {
            for (Item item : list) {
                if (item.isNotNullItem() && item.template.TypeEvent == TypeEvent) {
                    return item;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean haveOption(Item it, int idOption) {
        if (it != null && it.isNotNullItem()) {
            return it.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == idOption);
        }
        return false;
    }

    public Item findItemByOption(Player player, int idOption) {
        if (player == null || player.inventory == null || player.inventory.itemsBag == null) {
            return null;
        }
        for (Item it : player.inventory.itemsBag) {
            if (it != null && haveOption(it, idOption)) {
                return it;
            }
        }
        return null;
    }

    public void removeItem(Player player, Item item) {
        if (player == null || item == null) {
            return;
        }
        int index = player.inventory.itemsBag.indexOf(item);
        if (index != -1) {
            player.inventory.itemsBag.set(index, ItemService.gI().createItemNull());
            sendItemBag(player);
        }
    }

    public Item findItemBody(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBody, tempId);
    }

    public Item findItemBodyEvent(Player player, int TypeEvent) {
        return this.findItemEvent(player.inventory.itemsBody, TypeEvent);
    }

    public Item findItemBag(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBag, tempId);
    }

    public Item findItemBagEvent(Player player, int TypeEvent) {
        return this.findItemEvent(player.inventory.itemsBag, TypeEvent);
    }

    public Item findItemBox(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBox, tempId);
    }

    public Item findItemBoxEvent(Player player, int TypeEvent) {
        return this.findItemEvent(player.inventory.itemsBox, TypeEvent);
    }

    public Item findItemBoxCollection(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBoxCollection, tempId);
    }

    public Item findItemBoxCollectionEvent(Player player, int TypeEvent) {
        return this.findItemEvent(player.inventory.itemsBoxCollection, TypeEvent);
    }

    public Item findItemBoxClan(Player player, int tempId) {
        return this.findItem(player.clan.itemsBoxClan, tempId);
    }

    public boolean isExistItem(List<Item> list, int tempId) {
        try {
            return this.findItem(list, tempId) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExistItemBody(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBody, tempId);
    }

    public boolean isExistItemBag(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBag, tempId);
    }

    public boolean isExistItemBox(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBox, tempId);
    }

    public boolean isExistItemBoxCollection(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBoxCollection, tempId);
    }

    public boolean isExistItemBoxClan(Player player, int tempId) {
        return this.isExistItem(player.clan.itemsBoxClan, tempId);
    }

    public boolean findItem(Player player, int id) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == id) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == id) {
                return true;
            }
        }
        return false;
    }

    private void __________________Sao_chép_danh_sách_item__________________() {
        //**********************************************************************
    }

    public List<Item> copyList(List<Item> items) {
        List<Item> list = new ArrayList<>();
        for (Item item : items) {
            list.add(ItemService.gI().copyItem(item));
        }
        return list;
    }

    public List<Item> copyItemsBody(Player player) {
        return copyList(player.inventory.itemsBody);
    }

    public List<Item> copyItemsBag(Player player) {
        return copyList(player.inventory.itemsBag);
    }

    public List<Item> copyItemsBox(Player player) {
        return copyList(player.inventory.itemsBox);
    }

    public List<Item> copyItemsBoxCollection(Player player) {
        return copyList(player.inventory.itemsBoxCollection);
    }

    public List<Item> copyItemsBoxClan(Player player) {
        return copyList(player.clan.itemsBoxClan);
    }

    private void __________________Vứt_bỏ_item______________________________() {
        //**********************************************************************
    }

    public void throwItem(Player player, int where, int index) {
        Item itemThrow = null;
        if (where == 0) {
            itemThrow = player.inventory.itemsBody.get(index);
            if (itemThrow != null) {
                if ((!itemThrow.haveOption(93) && itemThrow.template.type == 5)
                        || itemThrow.haveSetKichHoat()
                        || itemThrow.haveOption(107)) {
                    player.tempItemIndex = index;
                    player.itemThrow_Drop = itemThrow;
                    // Input.gI().createFormDeleteItem(player); // ❌ bỏ confirm, chỉ giữ tham chiếu nếu cần
                    removeItemBody(player, index);
                    sendItemBody(player);
                    Service.gI().Send_Caitrang(player);
                    Service.gI().sendThongBao(player, "Đã vứt bỏ " + itemThrow.template.name);
                    return;
                }
                removeItemBody(player, index);
                sendItemBody(player);
                Service.gI().Send_Caitrang(player);
                Service.gI().sendThongBao(player, "Đã vứt bỏ " + itemThrow.template.name);
            }
        } else if (where == 1) {
            itemThrow = player.inventory.itemsBag.get(index);
            if (itemThrow != null) {
                if (itemThrow.template.id == 570 || itemThrow.template.id == 457) {
                    Service.gI().sendThongBao(player, "Không thể vứt vật phẩm này.");
                    return;
                }
                if ((!itemThrow.haveOption(93) && itemThrow.template.type == 5)
                        || itemThrow.haveSetKichHoat()
                        || itemThrow.haveOption(107)) {
                    player.tempItemIndex = index;
                    player.itemThrow_Drop = itemThrow;
                    // Input.gI().createFormDeleteItem(player); // ❌ comment confirm
                    removeItemBag(player, index);
                    sortItems(player.inventory.itemsBag);
                    sendItemBag(player);
                    Service.gI().sendThongBao(player, "Đã vứt bỏ " + itemThrow.template.name);
                    return;
                }
                removeItemBag(player, index);
                sortItems(player.inventory.itemsBag);
                sendItemBag(player);
                Service.gI().sendThongBao(player, "Đã vứt bỏ " + itemThrow.template.name);
            }
        }
        if (itemThrow == null) {
            return;
        }
    }

    private void __________________Xoá_bỏ_item______________________________() {
        //**********************************************************************
    }

    public void removeItem(List<Item> items, int index) {
        Item item = ItemService.gI().createItemNull();
        items.set(index, item);
    }

    public void removeItem(List<Item> items, Item item) {
        if (item == null) {
            return;
        }
        Item it = ItemService.gI().createItemNull();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                items.set(i, it);
                item.dispose();
                break;
            }
        }
    }

    public void removeItemBag(Player player, int index) {
        this.removeItem(player.inventory.itemsBag, index);
    }

    public void removeItemBag(Player player, Item item) {
        this.removeItem(player.inventory.itemsBag, item);
    }

    public void removeItemBody(Player player, int index) {
        this.removeItem(player.inventory.itemsBody, index);
    }

    public void removeItemPetBody(Player player, int index) {
        this.removeItemBody(player.Detu, index);
    }

    public void removeItemBox(Player player, int index) {
        this.removeItem(player.inventory.itemsBox, index);
    }

    public void removeItemBoxCollection(Player player, int index) {
        this.removeItem(player.inventory.itemsBoxCollection, index);
    }

    public void removeItemBoxClan(Player player, int index) {
        this.removeItem(player.clan.itemsBoxClan, index);
    }

    private void __________________Giảm_số_lượng_item_______________________() {
        //**********************************************************************
    }

    public void subQuantityItemsBag(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBag, item, quantity);
    }

    public void subQuantityItemsBody(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBody, item, quantity);
    }

    public void subQuantityItemsBox(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBox, item, quantity);
    }

    public void subQuantityItemsBoxCollection(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBoxCollection, item, quantity);
    }

    public void subQuantityItemsBoxClan(Player player, Item item, int quantity) {
        subQuantityItem(player.clan.itemsBoxClan, item, quantity);
    }

    public void subQuantityItem(List<Item> items, Item item, int quantity) {
        if (item != null) {
            for (Item it : items) {
                if (item.equals(it)) {
                    it.quantity -= quantity;
                    if (it.quantity <= 0) {
                        this.removeItem(items, item);
                    }
                    break;
                }
            }
        }
    }

    private void __________________Sắp_xếp_danh_sách_item___________________() {
        //**********************************************************************
    }

    public void sortItems(List<Item> list) {
        int first = -1;
        int last = -1;
        Item tempFirst = null;
        Item tempLast = null;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isNotNullItem()) {
                first = i;
                tempFirst = list.get(i);
                break;
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isNotNullItem()) {
                last = i;
                tempLast = list.get(i);
                break;
            }
        }
        if (first != -1 && last != -1 && first < last) {
            list.set(first, tempLast);
            list.set(last, tempFirst);
            sortItems(list);
        }
    }

    public void sortItemv2(List<Item> items) {
        int index = 0;
        for (Item item : items) {
            if (item != null && item.quantity > 0) {
                items.set(index, item);
                index++;
            }
        }
        for (int i = index; i < items.size(); i++) {
            items.set(i, null);
        }
    }

    private void __________________Thao_tác_tháo_mặc_item___________________() {
        //**********************************************************************
    }

    private Item putItemBag(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
            if (!player.inventory.itemsBag.get(i).isNotNullItem()) {
                player.inventory.itemsBag.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBox(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBox.size(); i++) {
            if (!player.inventory.itemsBox.get(i).isNotNullItem()) {
                player.inventory.itemsBox.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBoxCollection(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBoxCollection.size(); i++) {
            if (!player.inventory.itemsBoxCollection.get(i).isNotNullItem()) {
                player.inventory.itemsBoxCollection.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBoxClan(Player player, Item item) {
        for (int i = 0; i < player.clan.itemsBoxClan.size(); i++) {
            if (!player.clan.itemsBoxClan.get(i).isNotNullItem()) {
                player.clan.itemsBoxClan.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBody(Player player, Item item) {
        Item sItem = item;
        if (!item.isNotNullItem()) {
            return sItem;
        }
        switch (item.template.type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 32:
            case 23:
            case 24:
            case 11:
            case 21:
            case 35:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 39:
                break;
            default:
                Service.gI().sendThongBaoOK(player.isDeTu ? ((Detu) player).master : player, "Trang bị không phù hợp!");
                return sItem;
        }
        if (item.template.gender < 3 && item.template.gender != player.gender) {
            Service.gI().sendThongBaoOK(player.isDeTu ? ((Detu) player).master : player, "Trang bị không phù hợp!");
            return sItem;
        }
        long powerRequire = item.template.strRequire;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 21) {
                powerRequire = io.param * 1000000000L;
                break;
            }
        }
        if (player.nPoint.power < powerRequire) {
            Service.gI().sendThongBaoOK(player.isDeTu ? ((Detu) player).master : player, "Sức mạnh không đủ yêu cầu!");
            return sItem;
        }
        if (item.isItemTemplateToPlayer() && player.isDeTu) {
            Service.gI().sendThongBao(player.isDeTu ? ((Detu) player).master : player, "Không thể cho đệ tử mặc Cải Trang này");
            return sItem;
        }
        if (item.haveOption(38) && player.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            Service.gI().sendThongBao(player.isDeTu ? ((Detu) player).master : player, (player.isDeTu ? "Không thể cho đệ tử sử dụng" : "Chỉ khi hợp thể mới sử dụng được"));
            return sItem;
        }
        if (item.isItemTemplateToPet() && !player.isDeTu) {
            Service.gI().sendThongBao(player, "Chỉ sử dụng được cho đệ tử");
            return sItem;
        }
        int index = -1;
        switch (item.template.type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                index = item.template.type;
                break;
            case 32:
                index = 6;
                break;
            case 21:
                index = 7;
                break;
            case 11:
                index = 8;
                break;
            case 23:
            case 24:
                index = 9;
                break;
            case 35:
                index = 10;
                break;
//            case 70:
//            case 71:
//            case 72:
//            case 73:
//            case 74:
//            case 75:
//                index = 11;
//                break;
            case 39:
                index = 11;
                break;
//            case 99:
//                index = 11;
//                break;
//            case 98:
//                index = 12;
//                break;                
        }
        if (index >= player.inventory.itemsBody.size()) {
            if (player.isDeTu) {
                Service.gI().sendThongBao(((Detu) player).master, "Đệ tử không thể sử dụng vật phẩm này !");
            } else {
                Service.gI().sendThongBao(((Detu) player).master, "Không thể sử dụng vật phẩm này !");
            }
            return sItem;
        }
        sItem = player.inventory.itemsBody.get(index);
        if (index == 8) {
            if (sItem.isNotNullItem()) {
                Service.gI().removeEffPlayer(player, sItem.template.part);
            }
        }
        if (item.haveOption(210)) {
            removeAndAddOptionTemplate(item);
        }
        if (item.haveOption(231)) {
            removeAndAddOptionTemplateHSD(item);
        }
        player.inventory.itemsBody.set(index, item);
        return sItem;
    }

    public void itemBagToBody(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBag.set(index, putItemBody(player, item));
            if (item.template.type == 39 && item.template.id >= 2002 && item.template.id <= 2010) {
                Service.getInstance().removeDanhHieu(player);
                Service.getInstance().sendChanMenh(player, item.template.id);
            }
            updateSet(player, 127, 139);
            updateSet(player, 128, 140);
            updateSet(player, 129, 141);
            updateSet(player, 130, 142);
            updateSet(player, 131, 143);
            updateSet(player, 132, 144);
            updateSet(player, 133, 136);
            updateSet(player, 134, 137);
            updateSet(player, 135, 138);
            updateSet(player, 233, 234);
            updateSet(player, 250, 253);
            updateSet(player, 251, 254);
            updateSet(player, 252, 255);
            updateSet(player, 263, 264);
            updateSet(player, 265, 266);
            updateSet(player, 267, 268);
            updateSetNew(player, 241, 244, new int[]{242, 243, 244});
            updateSetNew(player, 237, 240, new int[]{238, 239, 240});
            updateSetNew(player, 245, 248, new int[]{246, 247, 248});
            sendItemBag(player);
            sendItemBody(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
            Service.getInstance().sendFlagBag(player);
        }
    }

    public void itemBodyToBag(Player player, int index) {
        Item item = player.inventory.itemsBody.get(index);
        if (getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
            return;
        }
        if (item.isNotNullItem()) {
//            if (index == 11) {
//                Service.gI().sendLinhThu(player, (short) 0);
//            }
            if (index == 7 && !player.isDeTu) {
                if (player.PetFollow != null) {
                    ChangeMapService.gI().exitMap(player.PetFollow);
                    player.PetFollow.dispose();
                    player.PetFollow = null;
                }
            }
            if (item.template.type == 39) {
                Service.getInstance().removeDanhHieu(player);
            }
            player.inventory.itemsBody.set(index, putItemBag(player, item));
            resetSet(player, player, player, 127, 139);
            resetSet(player, player, player, 128, 140);
            resetSet(player, player, player, 129, 141);
            resetSet(player, player, player, 130, 142);
            resetSet(player, player, player, 131, 143);
            resetSet(player, player, player, 132, 144);
            resetSet(player, player, player, 133, 136);
            resetSet(player, player, player, 134, 137);
            resetSet(player, player, player, 135, 138);
            resetSet(player, player, player, 233, 234);
            resetSet(player, player, player, 250, 253);
            resetSet(player, player, player, 251, 254);
            resetSet(player, player, player, 252, 255);
            resetSet(player, player, player, 263, 264);
            resetSet(player, player, player, 265, 266);
            resetSet(player, player, player, 267, 268);
            resetSetNew(player, player, player, 241, 244, new int[]{242, 243, 244});
            resetSetNew(player, player, player, 237, 240, new int[]{238, 239, 240});
            resetSetNew(player, player, player, 245, 248, new int[]{246, 247, 248});
            sendItemBag(player);
            sendItemBody(player);
            if (index == 8 || index == 12) {
                if (item.isNotNullItem()) {
                    Service.gI().removeEffPlayer(player, item.template.part);
                }
            }
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
            Service.getInstance().sendFlagBag(player);
        }
    }

    public void itemBagToPetBody(Player player, int index) {
        try {
            if (player.Detu != null && player.Detu.nPoint.power >= 1500000) {
                Item item = player.inventory.itemsBag.get(index);
                if (item.isNotNullItem()) {
                    Item itemSwap = putItemBody(player.Detu, item);
                    player.inventory.itemsBag.set(index, itemSwap);
                    updateSet(player.Detu, 127, 139);
                    updateSet(player.Detu, 128, 140);
                    updateSet(player.Detu, 129, 141);
                    updateSet(player.Detu, 130, 142);
                    updateSet(player.Detu, 131, 143);
                    updateSet(player.Detu, 132, 144);
                    updateSet(player.Detu, 133, 136);
                    updateSet(player.Detu, 134, 137);
                    updateSet(player.Detu, 135, 138);
                    updateSet(player.Detu, 233, 234);
                    updateSet(player.Detu, 250, 253);
                    updateSet(player.Detu, 251, 254);
                    updateSet(player.Detu, 252, 255);
                    updateSet(player.Detu, 263, 264);
                    updateSet(player.Detu, 265, 266);
                    updateSet(player.Detu, 267, 268);
                    updateSetNew(player.Detu, 241, 244, new int[]{242, 243, 244});
                    updateSetNew(player.Detu, 237, 240, new int[]{238, 239, 240});
                    updateSetNew(player.Detu, 245, 248, new int[]{246, 247, 248});
                    sendItemBag(player);
                    sendItemBody(player);
                    if (!itemSwap.equals(item)) {
                        Service.gI().point(player);
                        player.typeTabPet = 0;
                        Service.gI().showInfoPet(player);
                    }
                    Service.gI().Send_Caitrang(player.Detu);
                    Service.gI().Send_Caitrang(player);
                }
            } else {
                Service.gI().sendThongBao(player, "Đệ tử phải đạt 1tr5 sức mạnh mới có thể mặc");
            }
        } catch (Exception E) {
            Logger.logException(InventoryService.class, E);
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    public void itemPetBodyToBag(Player player, int index) {
        if (getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
            return;
        }
        Player petPl;
        Item item;
        switch (player.typeTabPet) {
            case 0:
                petPl = player.Detu;
                break;
            default:
                petPl = player.Detu;
                break;
        }
        item = petPl.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            petPl.inventory.itemsBody.set(index, putItemBag(player, item));
            resetSet(petPl, player, player, 127, 139);
            resetSet(petPl, player, player, 128, 140);
            resetSet(petPl, player, player, 129, 141);
            resetSet(petPl, player, player, 130, 142);
            resetSet(petPl, player, player, 131, 143);
            resetSet(petPl, player, player, 132, 144);
            resetSet(petPl, player, player, 133, 136);
            resetSet(petPl, player, player, 134, 137);
            resetSet(petPl, player, player, 135, 138);
            resetSet(petPl, player, player, 233, 234);
            resetSet(petPl, player, player, 250, 253);
            resetSet(petPl, player, player, 251, 254);
            resetSet(petPl, player, player, 252, 255);
            resetSet(petPl, player, player, 263, 264);
            resetSet(petPl, player, player, 265, 266);
            resetSet(petPl, player, player, 267, 268);
            resetSetNew(petPl, player, player, 241, 244, new int[]{242, 243, 244});
            resetSetNew(petPl, player, player, 237, 240, new int[]{238, 239, 240});
            resetSetNew(petPl, player, player, 245, 248, new int[]{246, 247, 248});
            sendItemBag(player);
            sendItemBody(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(petPl);
            Service.gI().Send_Caitrang(player);
            switch (player.typeTabPet) {
                case 0:
                    Service.getInstance().showInfoPet(player);
                    break;

                default:
                    break;
            }
        }
    }

    public void itemBoxToBodyOrBag(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
            return;
        }
        Item item = player.inventory.itemsBox.get(index);
        if (item.isNotNullItem()) {
            boolean done = false;
            if (item.template.type >= 0 && item.template.type <= 5 || item.template.type == 32) {
                Item itemBody = player.inventory.itemsBody.get(item.template.type == 32 ? 6 : item.template.type);
                if (!itemBody.isNotNullItem()) {
                    if (item.template.gender == player.gender || item.template.gender == 3) {
                        long powerRequire = item.template.strRequire;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 21) {
                                powerRequire = io.param * 1000000000L;
                                break;
                            }
                        }
                        if (powerRequire <= player.nPoint.power) {
                            player.inventory.itemsBody.set(item.template.type == 32 ? 6 : item.template.type, item);
                            player.inventory.itemsBox.set(index, itemBody);
                            updateSet(player, 127, 139);
                            updateSet(player, 128, 140);
                            updateSet(player, 129, 141);
                            updateSet(player, 130, 142);
                            updateSet(player, 131, 143);
                            updateSet(player, 132, 144);
                            updateSet(player, 133, 136);
                            updateSet(player, 134, 137);
                            updateSet(player, 135, 138);
                            updateSet(player, 233, 234);
                            updateSet(player, 250, 253);
                            updateSet(player, 251, 254);
                            updateSet(player, 252, 255);
                            updateSet(player, 263, 264);
                            updateSet(player, 265, 266);
                            updateSet(player, 267, 268);
                            updateSetNew(player, 241, 244, new int[]{242, 243, 244});
                            updateSetNew(player, 237, 240, new int[]{238, 239, 240});
                            updateSetNew(player, 245, 248, new int[]{246, 247, 248});
                            done = true;
                            sendItemBody(player);
                            Service.gI().point(player);
                            Service.gI().Send_Caitrang(player);
                        }
                    }
                }
            }
            if (!done) {
                if (addItemBag(player, item)) {
                    if (item.quantity == 0) {
                        Item sItem = ItemService.gI().createItemNull();
                        player.inventory.itemsBox.set(index, sItem);
                    }
                    sendItemBag(player);
                }
            }
            sendItemBox(player);
        }
    }

    public void itemBoxCollectionToBodyOrBag(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
            return;
        }
        Item item = player.inventory.itemsBoxCollection.get(index);
        if (item.isNotNullItem()) {
            boolean done = false;
            if (item.template.type >= 0 && item.template.type <= 5 || item.template.type == 32) {
                Item itemBody = player.inventory.itemsBody.get(item.template.type == 32 ? 6 : item.template.type);
                if (!itemBody.isNotNullItem()) {
                    if (item.template.gender == player.gender || item.template.gender == 3) {
                        long powerRequire = item.template.strRequire;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 21) {
                                powerRequire = io.param * 1000000000L;
                                break;
                            }
                        }
                        if (powerRequire <= player.nPoint.power) {
                            player.inventory.itemsBody.set(item.template.type == 32 ? 6 : item.template.type, item);
                            player.inventory.itemsBoxCollection.set(index, itemBody);
                            done = true;
                            sendItemBody(player);
                            Service.gI().point(player);
                            Service.gI().Send_Caitrang(player);
                        }
                    }
                }
            }
            if (!done) {
                if (addItemBag(player, item)) {
                    if (item.quantity == 0) {
                        Item sItem = ItemService.gI().createItemNull();
                        player.inventory.itemsBoxCollection.set(index, sItem);
                    }
                    sendItemBag(player);
                }
            }
            sendItemBoxCollection(player);
        }
    }

    public void itemBagToBox(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570 || item.template.id == 543) {
                Service.gI().sendThongBao(player, "Không thể cất vật phẩm này vào rương");
                return;
            }
            if (addItemBox(player, item)) {
                if (item.quantity == 0) {
                    Item sItem = ItemService.gI().createItemNull();
                    player.inventory.itemsBag.set(index, sItem);
                }
                sortItems(player.inventory.itemsBag);
                resetSet(player, player, player, 127, 139);
                resetSet(player, player, player, 128, 140);
                resetSet(player, player, player, 129, 141);
                resetSet(player, player, player, 130, 142);
                resetSet(player, player, player, 131, 143);
                resetSet(player, player, player, 132, 144);
                resetSet(player, player, player, 133, 136);
                resetSet(player, player, player, 134, 137);
                resetSet(player, player, player, 135, 138);
                resetSet(player, player, player, 233, 234);
                resetSet(player, player, player, 250, 253);
                resetSet(player, player, player, 251, 254);
                resetSet(player, player, player, 252, 255);
                resetSet(player, player, player, 263, 264);
                resetSet(player, player, player, 265, 266);
                resetSet(player, player, player, 267, 268);
                resetSetNew(player, player, player, 241, 244, new int[]{242, 243, 244});
                resetSetNew(player, player, player, 237, 240, new int[]{238, 239, 240});
                resetSetNew(player, player, player, 245, 248, new int[]{246, 247, 248});
                sendItemBag(player);
                sendItemBox(player);
            }
        }
    }

    public void itemBagToBoxCollection(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item != null && item.isNotNullItem()) {
            if (!item.isItemCollection()) {
                Service.gI().sendThongBao(player, "Không thể cất vật phẩm này vào rương");
                return;
            }
            if (item.isTrangBiHSD()) {
                Service.gI().sendThongBao(player, "Không thể cất vật phẩm có hạn sử dụng");
                return;
            }
            if (addItemBoxCollection(player, item)) {
                if (item.quantity == 0) {
                    Item sItem = ItemService.gI().createItemNull();
                    player.inventory.itemsBag.set(index, sItem);
                }
                sortItems(player.inventory.itemsBag);
                sendItemBag(player);
                sendItemBoxCollection(player);
            }
        }
    }

    public void itemBodyToBox(Player player, int index) {
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBody.set(index, putItemBox(player, item));
            sortItems(player.inventory.itemsBag);
            resetSet(player, player, player, 127, 139);
            resetSet(player, player, player, 128, 140);
            resetSet(player, player, player, 129, 141);
            resetSet(player, player, player, 130, 142);
            resetSet(player, player, player, 131, 143);
            resetSet(player, player, player, 132, 144);
            resetSet(player, player, player, 133, 136);
            resetSet(player, player, player, 134, 137);
            resetSet(player, player, player, 135, 138);
            resetSet(player, player, player, 233, 234);
            resetSet(player, player, player, 250, 253);
            resetSet(player, player, player, 251, 254);
            resetSet(player, player, player, 252, 255);
            resetSet(player, player, player, 263, 264);
            resetSet(player, player, player, 265, 266);
            resetSet(player, player, player, 267, 268);
            resetSetNew(player, player, player, 241, 244, new int[]{242, 243, 244});
            resetSetNew(player, player, player, 237, 240, new int[]{238, 239, 240});
            resetSetNew(player, player, player, 245, 248, new int[]{246, 247, 248});
            sendItemBody(player);
            sendItemBox(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
        }
    }

    public void itemBodyToBoxCollection(Player player, int index) {
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            if (!item.isItemCollection()) {
                Service.gI().sendThongBao(player, "Không thể cất vật phẩm này vào rương");
                return;
            }
            if (item.isTrangBiHSD()) {
                Service.gI().sendThongBao(player, "Không thể cất vật phẩm có hạn sử dụng");
                return;
            }
            player.inventory.itemsBody.set(index, putItemBoxCollection(player, item));
            sortItems(player.inventory.itemsBag);
            sendItemBody(player);
            sendItemBoxCollection(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
        }
    }

    //clan
    public void sendItemBoxClanToAll(Clan clan) {
        for (ClanMember cm : clan.members) {
            Player pl = Client.gI().getPlayerByID(cm.id);
            if (pl != null) {
                sendItemClan(pl);
            }
        }
    }

    public void itemBoxClanToBodyOrBag(Player player, int index) {
        if (player == null || player.clan == null) {
            return;
        }
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
            return;
        }

        Item item = player.clan.itemsBoxClan.get(index);
        if (item != null && item.isNotNullItem()) {
            boolean done = false;
            if ((item.template.type >= 0 && item.template.type <= 5) || item.template.type == 32) {
                Item itemBody = player.inventory.itemsBody.get(item.template.type == 32 ? 6 : item.template.type);
                if (!itemBody.isNotNullItem()) {
                    if (item.template.gender == player.gender || item.template.gender == 3) {
                        long powerRequire = item.template.strRequire;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 21) {
                                powerRequire = io.param * 1000000000L;
                                break;
                            }
                        }
                        if (powerRequire <= player.nPoint.power) {
                            player.inventory.itemsBody.set(item.template.type == 32 ? 6 : item.template.type, item);
                            player.clan.itemsBoxClan.set(index, itemBody);
                            done = true;

                            sendItemBody(player);
                            Service.gI().point(player);
                            Service.gI().Send_Caitrang(player);
                        }
                    }
                }
            }

            if (!done) {
                if (addItemBag(player, item)) {
                    if (item.quantity == 0) {
                        Item sItem = ItemService.gI().createItemNull();
                        player.clan.itemsBoxClan.set(index, sItem);
                    }
                    sendItemBag(player);
                }
            }
        }
        player.clan.updateItemsBoxClanToSQL(player.clan);
        sendItemBoxClan(player);
        sendItemBoxClanToAll(player.clan);
    }

    public void itemBagToBoxClan(Player player, int index) {
        if (player == null || player.clan == null) {
            return;
        }
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570 || item.template.id == 543) {
                Service.gI().sendThongBao(player, "Không thể cất vật phẩm này vào rương");
                return;
            }
            if (item.itemOptions != null) {
                for (ItemOption io : item.itemOptions) {
                    if (io != null && io.optionTemplate.id == 30) {
                        Service.gI().sendThongBao(player, "Không thể cất vật phẩm này vào rương");
                        return;
                    }
                }
            }
            if (addItemBoxClan(player, item)) {
                if (item.quantity == 0) {
                    Item sItem = ItemService.gI().createItemNull();
                    player.inventory.itemsBag.set(index, sItem);
                }
                sortItems(player.inventory.itemsBag);
                sendItemBag(player);
                sendItemBoxClan(player);
            }
        }
        player.clan.updateItemsBoxClanToSQL(player.clan);
        sendItemBoxClanToAll(player.clan);
    }

    public void itemBodyToBoxClan(Player player, int index) {
        if (player == null || player.clan == null) {
            return;
        }
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBody.set(index, putItemBoxClan(player, item));
            sortItems(player.inventory.itemsBag);
            sendItemBody(player);
            sendItemBoxClan(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
        }
        player.clan.updateItemsBoxClanToSQL(player.clan);
        sendItemBoxClanToAll(player.clan);
    }

    private void __________________Gửi_danh_sách_item_cho_người_chơi________() {
        //**********************************************************************
    }

    public void sendItemBag(Player player) {
        sortItems(player.inventory.itemsBag);
        Message msg;
        try {
            msg = new Message(-36);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.inventory.itemsBag.size());
            for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
                Item item = player.inventory.itemsBag.get(i);
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                    continue;
                }
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeUTF(item.getInfo());
                msg.writer().writeUTF(item.getContent());
                msg.writer().writeByte(item.itemOptions.size());
                for (int j = 0; j < item.itemOptions.size(); j++) {
                    msg.writer().writeInt(item.itemOptions.get(j).optionTemplate.id);
                    msg.writer().writeInt(item.itemOptions.get(j).param);
                }
            }

            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
    }

    public void sendItemBody(Player player) {
        try {
            Message msg = new Message(-37);
            msg.writer().writeByte(0);
            msg.writer().writeShort(player.getHead());
            msg.writer().writeByte(player.inventory.itemsBody.size());

            for (Item item : player.inventory.itemsBody) {
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption io : itemOptions) {
                        msg.writer().writeInt(io.optionTemplate.id);
                        msg.writer().writeInt(io.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }

        // ✅ Gửi hiệu ứng đặc biệt ngay khi login
        Item chanMenh = player.inventory.itemsBody.get(11);
        if (chanMenh != null && chanMenh.isNotNullItem()
                && chanMenh.template.type == 39) {
            Service.gI().sendChanMenh(player, chanMenh.template.id);
        }

        // Sau đó mới gửi trang phục và cờ
        Service.gI().Send_Caitrang(player);
        Service.gI().sendFlagBag(player);
    }

    public void sendItemBox(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.inventory.itemsBox.size());
            for (Item it : player.inventory.itemsBox) {
                msg.writer().writeShort(it.isNotNullItem() ? it.template.id : -1);
                if (it.isNotNullItem()) {
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeUTF(it.getInfo());
                    msg.writer().writeUTF(it.getContent());
                    msg.writer().writeByte(it.itemOptions.size());
                    for (ItemOption io : it.itemOptions) {
                        msg.writer().writeInt(io.optionTemplate.id);
                        msg.writer().writeInt(io.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
        this.openBox(player);
    }

    public void openBox(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
    }

    public void sendItemBoxCollection(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.inventory.itemsBoxCollection.size());
            for (Item it : player.inventory.itemsBoxCollection) {
                msg.writer().writeShort(it.isNotNullItem() ? it.template.id : -1);
                if (it.isNotNullItem()) {
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeUTF(it.getInfo());
                    msg.writer().writeUTF(it.getContent());
                    msg.writer().writeByte(it.itemOptions.size());
                    for (ItemOption io : it.itemOptions) {
                        msg.writer().writeInt(io.optionTemplate.id);
                        msg.writer().writeInt(io.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
        this.openBox(player);
    }

    public void openBoxCollection(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
    }

    public void sendItemBoxClan(Player player) {
        if (player == null || player.clan == null) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.clan.itemsBoxClan.size());
            for (Item it : player.clan.itemsBoxClan) {
                msg.writer().writeShort(it.isNotNullItem() ? it.template.id : -1);
                if (it.isNotNullItem()) {
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeUTF(it.getInfo());
                    msg.writer().writeUTF(it.getContent());
                    msg.writer().writeByte(it.itemOptions.size());
                    for (ItemOption io : it.itemOptions) {
                        msg.writer().writeInt(io.optionTemplate.id);
                        msg.writer().writeInt(io.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
        this.openBoxClan(player);
    }

    public void sendItemClan(Player player) {
        if (player == null || player.clan == null) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.clan.itemsBoxClan.size());
            for (Item it : player.clan.itemsBoxClan) {
                msg.writer().writeShort(it.isNotNullItem() ? it.template.id : -1);
                if (it.isNotNullItem()) {
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeUTF(it.getInfo());
                    msg.writer().writeUTF(it.getContent());
                    msg.writer().writeByte(it.itemOptions.size());
                    for (ItemOption io : it.itemOptions) {
                        msg.writer().writeInt(io.optionTemplate.id);
                        msg.writer().writeInt(io.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
    }

    public void openBoxClan(Player player) {
        if (player == null || player.clan == null) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(InventoryService.class, e);
        }
    }

    private void __________________Thêm_vật_phẩm_vào_danh_sách______________() {
        //**********************************************************************
    }

    private boolean addItemSpecial(Player player, Item item) {
        if (item.template.type == 13) {
            int min = 0;
            try {
                String tagShopBua = player.iDMark.getShopOpen().tagName;
                switch (tagShopBua) {
                    case "BUA_1H":
                        min = 60;
                        break;
                    case "BUA_DETU":
                        min = 60;
                        break;
                    case "BUA_8H":
                        min = 60 * 8;
                        break;
                    case "BUA_1M":
                        min = 60 * 24 * 30;
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Logger.logException(InventoryService.class, e);
            }
            player.charms.addTimeCharms(item.template.id, min);
            return true;
        }
        switch (item.template.id) {
//            case 568: //quả trứng
//                if (player.mabuEgg == null) {
//                    MabuEgg.createMabuEgg(player);
//                }
//                return true;
            case 2500: //quả trứng
                if (player.billEgg == null) {
                    BillEgg.createBillEgg(player);
                }
                return true;
            case 453: //tàu tennis
                player.haveTennisSpaceShip = true;
                return true;
            case 74: //đùi gà nướng
                player.nPoint.setFullHpMp();
                PlayerService.gI().sendInfoHpMp(player);
                return true;
            case 191: //cà chua
                player.nPoint.setFullHp();
                PlayerService.gI().sendInfoHpMp(player);
                return true;
            case 192: //cà rốt
                player.nPoint.setFullMp();
                PlayerService.gI().sendInfoHpMp(player);
                return true;
            case 516: //socola
                player.nPoint.setFullHpMp();
                Service.gI().congTiemNang(player, (byte) 2, Util.nextInt(500000, 1000000));
                PlayerService.gI().sendInfoHpMp(player);
                return true;
            case 670:
                player.nPoint.setFullHpMp();
                Service.gI().congTiemNang(player, (byte) 2, Util.nextInt(500000, 1000000));
                PlayerService.gI().sendInfoHpMp(player);
                return true;
            case 1784:
                DatabaseUpdater.addDaysToTimeUpSkhByPlayerId(player.id, 7);
                if (player.getSession() != null) {
                    DatabaseUpdater.refreshAccountAgeDays(player.getSession());
                    Service.gI().sendThongBao(player, "Thời gian tìm set kích hoạt đến " + TimeUtil.getDeadline(player.getSession().timeCreateAcount, player.getSession().accountAgeDays));
                }
                return true;
            case 1798:
                DatabaseUpdater.addDaysToTimeUpSkhByPlayerId(player.id, 15);
                if (player.getSession() != null) {
                    DatabaseUpdater.refreshAccountAgeDays(player.getSession());
                    Service.gI().sendThongBao(player, "Thời gian tìm set kích hoạt đến " + TimeUtil.getDeadline(player.getSession().timeCreateAcount, player.getSession().accountAgeDays));
                }
                return true;
            case 1706:
                item = ItemService.gI().createNewItem((short) 220);
                item.addOptionParam(71, 0);
                item.quantity = 99;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
            case 1707:
                item = ItemService.gI().createNewItem((short) 221);
                item.addOptionParam(70, 0);
                item.quantity = 99;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
            case 1708:
                item = ItemService.gI().createNewItem((short) 222);
                item.addOptionParam(69, 0);
                item.quantity = 99;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
            case 1709:
                item = ItemService.gI().createNewItem((short) 223);
                item.addOptionParam(68, 0);
                item.quantity = 99;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
            case 1710:
                item = ItemService.gI().createNewItem((short) 224);
                item.addOptionParam(67, 0);
                item.quantity = 99;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
            case 1703:
                item = ItemService.gI().createNewItem((short) 595);
                item.addOptionParam(2, 512);
                item.quantity = 30;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
            case 1704:
                item = ItemService.gI().createNewItem((short) 1796);
                item.addOptionParam(2, 1024);
                item.quantity = 30;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                return true;
        }
        return false;
    }

    public boolean addItemBox(Player player, Item item) {
        return addItemList(player.inventory.itemsBox, item);
    }

    public boolean addItemBoxCollection(Player player, Item item) {
        return addItemList(player.inventory.itemsBoxCollection, item);
    }

    public boolean addItemBoxClan(Player player, Item item) {
        return addItemList(player.clan.itemsBoxClan, item);
    }

    public boolean addItemList(List<Item> items, Item itemAdd) {
        // --- Nếu item không có option, thêm option rỗng để tránh lỗi
        if (itemAdd.itemOptions.isEmpty()) {
            itemAdd.itemOptions.add(new ItemOption(73, 0));
        }

        // --- Item cộng thêm chỉ số param: tự động luyện tập - bí kiếp
        int[] idParam = isItemIncrementalOption(itemAdd);
        if (idParam[0] != -1) {
            for (Item it : items) {
                if (it.isNotNullItem() && it.template.id == itemAdd.template.id) {
                    for (ItemOption io : it.itemOptions) {
                        if (io.optionTemplate.id == idParam[0]) {
                            io.param += idParam[1];
                        }
                    }
                    itemAdd.quantity = 0;
                    return true;
                }
            }
        }

        // ✅ Xử lý riêng cho Thỏi vàng (ID 457)
        if (itemAdd.template.id == 457) {
            final int MAX_STACK_TV = 100000; // Giới hạn mỗi ô: 30.000
            boolean isLocked = hasOptionTemplateId(itemAdd, 30);

            // --- Gộp vào slot cùng loại và cùng trạng thái khóa
            for (Item it : items) {
                if (it != null && it.isNotNullItem() && it.template.id == 457) {
                    boolean otherLocked = hasOptionTemplateId(it, 30);
                    if (isLocked == otherLocked) {
                        int space = MAX_STACK_TV - it.quantity;
                        if (space > 0) {
                            int add = Math.min(space, itemAdd.quantity);
                            it.quantity += add;
                            itemAdd.quantity -= add;
                            if (itemAdd.quantity <= 0) {
                                return true;
                            }
                        }
                    }
                }
            }

            // --- Nếu còn dư, tìm ô trống để set trực tiếp (không add slot mới)
            if (itemAdd.quantity > 0) {
                for (int i = 0; i < items.size(); i++) {
                    Item slot = items.get(i);
                    if (slot == null || !slot.isNotNullItem()) {
                        Item newItem = ItemService.gI().copyItem(itemAdd);
                        newItem.quantity = Math.min(itemAdd.quantity, MAX_STACK_TV);

                        // Nếu là thỏi vàng khóa, thêm option 30 nếu chưa có
                        if (isLocked && !hasOptionTemplateId(newItem, 30)) {
                            newItem.itemOptions.add(new ItemOption(30, 0));
                        }

                        items.set(i, newItem);
                        itemAdd.quantity -= newItem.quantity;
                        if (itemAdd.quantity <= 0) {
                            return true;
                        }
                    }
                }
            }

            // --- Túi đầy
            Service.gI().sendThongBao(player, "Hành trang đã đầy, không thể nhận thêm Thỏi vàng");
            return false;
        }

        // --- Item cộng dồn thông thường
        if (itemAdd.template.isUpToUp) {
            for (Item it : items) {
                if (!it.isNotNullItem() || it.template.id != itemAdd.template.id || it.quantity >= 100_000_000) {
                    continue;
                }

                if (itemAdd.template.id == 1705) { // Không giới hạn số lượng
                    it.quantity += itemAdd.quantity;
                    itemAdd.quantity = 0;
                    return true;
                }

                // Giới hạn 9999
                if (it.quantity < 9999) {
                    int add = 9999 - it.quantity;
                    if (itemAdd.quantity <= add) {
                        it.quantity += itemAdd.quantity;
                        itemAdd.quantity = 0;
                        return true;
                    } else {
                        it.quantity = 9999;
                        itemAdd.quantity -= add;
                    }
                }
            }
        }

        // --- Nếu còn dư, tìm ô trống để đặt vào
        if (itemAdd.quantity > 0) {
            for (int i = 0; i < items.size(); i++) {
                Item slot = items.get(i);
                if (slot == null || !slot.isNotNullItem()) {
                    items.set(i, ItemService.gI().copyItem(itemAdd));
                    itemAdd.quantity = 0;
                    return true;
                }
            }
        }

        // --- Túi đầy
        return false;
    }

    /**
     * Kiểm tra item có option khóa (ID 30) hay không
     */
    public static boolean checkListsEqual(List<ItemOption> list1, List<ItemOption> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).optionTemplate.id != list2.get(i).optionTemplate.id || list1.get(i).param != list2.get(i).param) {
                return false;
            }
        }

        return true;
    }

    public boolean addItemBag(Player player, Item item) {
        // Ngọc Rồng Đen
        if (ItemMapService.gI().isBlackBall(item.template.id)) {
            return BlackBallWarService.gI().pickBlackBall(player, item);
        }
        // Ngọc Rồng Namek
        if (ItemMapService.gI().isNamecBall(item.template.id) || ItemMapService.gI().isNamecBallStone(item.template.id)) {
            return NgocRongNamec.gI().pickNamekBall(player, item);
        }
        if (addItemSpecial(player, item)) {
            return true;
        }

        // Gold / Gem / Ruby
        switch (item.template.type) {
            case 9:
                if (player.inventory.gold + item.quantity <= player.inventory.getGoldLimit()) {
                    player.inventory.gold += item.quantity;
                    Service.gI().sendMoney(player);
                    return true;
                } else {
                    Service.gI().sendThongBao(player, "Vàng sau khi nhặt quá giới hạn cho phép");
                    return false;
                }
            case 10:
                long gem = (long) player.inventory.gem + (long) item.quantity;
                if (gem > Integer.MAX_VALUE) {
                    gem = Integer.MAX_VALUE;
                }
                player.inventory.gem = (int) gem;
                Service.gI().sendMoney(player);
                return true;
            case 34:
                long ruby = (long) player.inventory.ruby + (long) item.quantity;
                if (ruby > Integer.MAX_VALUE) {
                    ruby = Integer.MAX_VALUE;
                }
                player.inventory.ruby = (int) ruby;
                Service.gI().sendMoney(player);
                return true;
        }

        // Mở rộng hành trang / rương
        switch (item.template.id) {
            case 1627:
            case 517:
                player.inventory.itemsBag.add(ItemService.gI().createItemNull());
                Service.gI().sendThongBaoOK(player, "Hành trang của bạn đã được mở rộng thêm 1 ô");
                return true;
            case 518:
                player.inventory.itemsBox.add(ItemService.gI().createItemNull());
                Service.gI().sendThongBaoOK(player, "Rương đồ của bạn đã được mở rộng thêm 1 ô");
                return true;
            case 988:
                if (player.inventory.getGoldLimit() < 2_000_000_000_000L) {
                    Service.getInstance().sendThongBao(player, "Giới hạn vàng của bạn đã tăng lên 100Tr");
                    return true;
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Giới hạn vàng của bạn đã đạt tối đa");
                    return false;
                }
        }

        // Vé tặng ngọc
        if (item.template.id == 718) {
            if (!item.haveOption(31)) {
                item.addOptionParam(31, 0);
            }
            for (Item it : player.inventory.itemsBag) {
                if (it != null && it.isNotNullItem() && it.template.id == 718) {
                    for (ItemOption io : it.itemOptions) {
                        if (io.optionTemplate.id == 31) {
                            int total = io.param + item.quantity;
                            io.param = Math.min(total, 30_000);
                            item.quantity = 0;
                            return true;
                        }
                    }
                }
            }
        }

        // Vé tặng hồng ngọc
        if (item.template.id == 1788) {
            if (!item.haveOption(31)) {
                item.addOptionParam(31, 0);
            }
            for (Item it : player.inventory.itemsBag) {
                if (it != null && it.isNotNullItem() && it.template.id == 1788) {
                    for (ItemOption io : it.itemOptions) {
                        if (io.optionTemplate.id == 31) {
                            int total = io.param + item.quantity;
                            io.param = Math.min(total, 30_000);
                            item.quantity = 0;
                            return true;
                        }
                    }
                }
            }
        }

        return addItemList(player.inventory.itemsBag, item);
    }

    /**
     * Kiểm tra có option 30 (Khóa)
     */
    private boolean hasOptionTemplateId(Item item, int optionTemplateId) {
        if (item == null || item.itemOptions == null) {
            return false;
        }
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == optionTemplateId) {
                return true;
            }
        }
        return false;
    }

    private void __________________Kiểm_tra_điều_kiện_vật_phẩm______________() {
        //**********************************************************************
    }

    /**
     * Kiểm tra vật phẩm có phải là vật phẩm tăng chỉ số option hay không
     *
     * @param item
     * @return id option tăng chỉ số - param
     */
    private int[] isItemIncrementalOption(Item item) {
        for (ItemOption io : item.itemOptions) {
            switch (io.optionTemplate.id) {
                case 1:
                    return new int[]{io.optionTemplate.id, io.param};
                case 31:
                    return new int[]{io.optionTemplate.id, io.param};
            }
        }
        return new int[]{-1, -1};
    }

    private void removeAndAddOptionTemplate(Item item) {
        int[] randomIds = {50, 77, 103, 14, 5, 94, 97, 108, 95, 96, 101, 204, 80, 81, 45, 46, 197}; // Danh sách ID có thể thay thế
        int count = 1;
        ItemOption option210 = item.itemOptions.stream()
                .filter(option -> option.optionTemplate.id == 210)
                .findFirst()
                .orElse(null);
        if (option210 != null) {
            count = option210.param;
            item.itemOptions.remove(option210);
        }
        List<Integer> availableIds = new ArrayList<>();
        for (int id : randomIds) {
            availableIds.add(id);
        }
        Collections.shuffle(availableIds);

        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int newId;
            if (i < availableIds.size()) {
                newId = availableIds.get(i);
            } else {
                newId = randomIds[rand.nextInt(randomIds.length)];
            }
            int newParam;
            switch (newId) {
                case 50:
                case 77:
                case 103:
                case 108:
                case 45:
                case 46:
                case 197:
                    newParam = Util.nextInt(5, 10);
                    break;
                case 94:
                case 97:
                case 204:
                case 14:
                    newParam = Util.nextInt(7, 18);
                    break;
                case 5:
                case 80:
                case 81:
                case 95:
                case 96:
                    newParam = Util.nextInt(10, 20);
                    break;
                default:
                    newParam = Util.nextInt(15, 30);
                    break;
            }
            item.itemOptions.add(new ItemOption(newId, newParam));
        }
    }

    private void removeAndAddOptionTemplateHSD(Item item) {
        int[] randomIds = {1, 3, 5, 7, 14, 30};
        ItemOption option231 = item.itemOptions.stream()
                .filter(option -> option.optionTemplate.id == 231)
                .findFirst()
                .orElse(null);
        if (option231 != null) {
            item.itemOptions.remove(option231);
        }
        if (Util.isTrue(90, 100)) {
            Random rand = new Random();
            int newId = 93;
            int newParam = randomIds[rand.nextInt(randomIds.length)];
            item.itemOptions.add(new ItemOption(newId, newParam));
        }
    }

    private void __________________Kiểm_tra_danh_sách_còn_chỗ_trống_________() {
        //**********************************************************************
    }

    public byte getCountEmptyBag(Player player) {
        return getCountEmptyListItem(player.inventory.itemsBag);
    }

    public byte getCountEmptyBody(Player player) {
        return getCountEmptyListItem(player.inventory.itemsBody);
    }

    public byte getCountEmptyListItem(List<Item> list) {
        byte count = 0;
        for (Item item : list) {
            if (!item.isNotNullItem()) {
                count++;
            }
        }
        return count;
    }

    public byte getIndexBag(Player pl, Item it) {
        for (byte i = 0; i < pl.inventory.itemsBag.size(); ++i) {
            Item item = pl.inventory.itemsBag.get(i);
            if (item != null && it.equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public boolean finditemWoodChest(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 570) {
                return false;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == 570) {
                return false;
            }
        }
        return true;
    }

    public int getParam(Player player, int idoption, int itemID) {
        for (Item it : player.inventory.itemsBag) {
            if (it != null && it.itemOptions != null && it.isNotNullItem() && it.template.id == itemID) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == idoption) {
                        return iop.param;
                    }
                }
            }
        }
        return 0;
    }

    public void subParamItemsBag(Player player, int itemID, int idoption, int param) {
        Item itemRemove = null;
        for (Item it : player.inventory.itemsBag) {
            if (it != null && it.template.id == itemID) {
                for (ItemOption op : it.itemOptions) {
                    if (op != null && op.optionTemplate.id == idoption) {
                        op.param -= param;
                        if (op.param <= 0) {
                            itemRemove = it;
                        }
                        break;
                    }
                }
                break;
            }
        }
        if (itemRemove != null) {
            removeItem(player.inventory.itemsBag, itemRemove);
        }
    }

    public boolean findItemTheVoCuc(Player player) {
        if (player.isPl()) {
            for (Item item : player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1185) {
                    return true;
                }
            }
            for (Item item : player.inventory.itemsBox) {
                if (item.isNotNullItem() && item.template.id == 1185) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findItemPhieuGiamGiaThuong(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 459) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemPetCeberus(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && item.template.id == 1654) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemPhieuGiamGiaVip(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 721) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemBongTaiCap2(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 921) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == 921) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemSkinQuyLaoKame(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && item.template.id == 710) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 710) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == 710) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemSkinDraburaFrost(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && item.template.id == 1205) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemRongNhi(Player player) {
        Set<Short> idsCanCo = Set.of((short) 1872, (short) 1873, (short) 1874,
                (short) 1875, (short) 1876, (short) 1877, (short) 1878);
        Set<Short> idsTimThay = new HashSet<>();

        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && idsCanCo.contains(item.template.id)) {
                idsTimThay.add(item.template.id);
            }
        }
        return idsTimThay.containsAll(idsCanCo);
    }

    public boolean findItemGender(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item != null && item.isNotNullItem() && item.template.isGender == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemNTK(Player player) {
        if (player.isPl()) {
            for (Item item : player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 992) {
                    return true;
                }
            }
            for (Item item : player.inventory.itemsBox) {
                if (item.isNotNullItem() && item.template.id == 992) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findItemTVC(Player player) {
        if (player.isPl()) {
            for (Item item : player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2077) {
                    return true;
                }
            }
            for (Item item : player.inventory.itemsBox) {
                if (item.isNotNullItem() && item.template.id == 2077) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findItemTatVoGiangSinh(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 649) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemVongKimCo(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 543) {
                return true;
            }
        }
        return false;
    }

    public boolean fullSetThan(Player player) {
        for (int i = 0; i < 5; i++) {
            Item item = player.inventory.itemsBody.get(i);
            if (item == null || item.template == null || item.template.level != 13) {
                return false;
            }
        }
        return true;
    }

    public boolean findItemThanLinh(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && (item.template.id == 555 || item.template.id == 556 || item.template.id == 557 || item.template.id == 558 || item.template.id == 559
                    || item.template.id == 560 || item.template.id == 561 || item.template.id == 562 || item.template.id == 563 || item.template.id == 564 || item.template.id == 565
                    || item.template.id == 566 || item.template.id == 567)) {
                return true;
            }
        }
        return false;
    }

    public boolean findAvatarKhauTrang(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && (item.template.id == 761 || item.template.id == 762 || item.template.id == 763)) {
                return true;
            }
        }
        return false;
    }

    public boolean findNonNoel(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && (item.template.id >= 386 && item.template.id <= 394)) {
                return true;
            }
        }
        return false;
    }

    public boolean findBongTuyet(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && item.template.id == 745) {
                return true;
            }
        }
        return false;
    }

    public boolean findBinhPhep(Player player) {
        if (player == null || player.inventory == null) {
            return false;
        }
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 1259) {
                return true;
            }
        }
        return false;
    }

    public boolean findGioDungNgocBi(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 1348) {
                return true;
            }
        }
        return false;
    }

    public boolean areAllMembersInSameZoneAndHaveLongDenItem(Player player) {
        if (player == null || player.zone == null || player.clan == null) {
            return false;
        }
        Zone currentZone = player.zone;
        Clan clan = player.clan;
        List<ClanMember> clanMembers = clan.getMembers();
        if (clanMembers == null || clanMembers.isEmpty()) {
            return false;
        }
        int[] longDenIds = new int[]{
            467, 468, 469, 470, 471,
            800, 801, 802, 803, 804,
            1047, 1303, 1675
        };
        for (ClanMember cm : clanMembers) {
            Player member = Client.gI().getPlayerByID(cm.id);
            if (member == null || member.zone == null || !member.zone.equals(currentZone)) {
                return false;
            }
            boolean hasLongDen = false;
            for (Item item : member.inventory.itemsBody) {
                if (item.isNotNullItem()) {
                    for (int id : longDenIds) {
                        if (item.template.id == id) {
                            hasLongDen = true;
                            break;
                        }
                    }
                    if (hasLongDen) {
                        break;
                    }
                }
            }
            if (!hasLongDen) {
                return false;
            }
        }
        return true;
    }

    public boolean findItemKiemGo(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 1304) {
                return true;
            }
        }
        return false;
    }

    public boolean x99ThucAn(Player player) {
        Item doAn = player.inventory.itemsBag.stream().filter(it -> it != null && it.template != null && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 || it.template.id == 667) && it.quantity >= 99).findFirst().orElse(null);
        return doAn != null;
    }

    public boolean canOpenBillShop(Player player) {
        return fullSetThan(player) && x99ThucAn(player);
    }

    public boolean optionCanUpgrade(int id) {
        return id == 0 || id == 22 || id == 23 || id == 14 || id == 27 || id == 28 || id == 47;
    }

    public int getIndexItem(Player player, List<Item> items, Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexItemBag(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBag, item);
    }

    public int getIndexItemBody(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBody, item);
    }

    public int getIndexItemBox(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBox, item);
    }

    public int getIndexItemBoxCollection(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBoxCollection, item);
    }

    public int getIndexItemBoxClan(Player player, Item item) {
        return getIndexItem(player, player.clan.itemsBoxClan, item);
    }

    public void updateSet(Player player, int setOptionId, int applyId) {
        int count = 0;
        for (Item item : player.inventory.itemsBody) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == setOptionId) {
                    count++;
                    break;
                }
            }
        }
        for (Item item : player.inventory.itemsBody) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (count == 5) {
                item.getOptionParam(applyId, 1);
            } else {
                item.getOptionParam(applyId, 0);
            }
        }
    }

    public void resetSet(Player player, Player pl, Player plbox, int setOptionId, int applyId) {
        int count = 0;
        for (Item item : player.inventory.itemsBody) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == setOptionId) {
                    count++;
                    break;
                }
            }
        }
        for (Item item : player.inventory.itemsBody) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (count < 5) {
                item.getOptionParam(applyId, 0);
            }
        }
        for (Item item : player.inventory.itemsBag) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            item.getOptionParam(applyId, 0);
        }
        if (pl != null) {
            for (Item item : pl.inventory.itemsBag) {
                if (item == null || !item.isNotNullItem()) {
                    continue;
                }
                item.getOptionParam(applyId, 0);
            }
        }
        if (plbox != null) {
            for (Item item : plbox.inventory.itemsBox) {
                if (item == null || !item.isNotNullItem()) {
                    continue;
                }
                item.getOptionParam(applyId, 0);
            }
        }
    }

    public void updateSetNew(Player player, int startId, int endId, int[] applyIds) {
        int count = 0;
        for (Item items : player.inventory.itemsBody) {
            if (items == null || !items.isNotNullItem()) {
                continue;
            }
            for (ItemOption io : items.itemOptions) {
                if (io.optionTemplate.id >= startId && io.optionTemplate.id <= endId) {
                    count++;
                    break;
                }
            }
        }
        for (Item item : player.inventory.itemsBody) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (count >= 2) {
                item.getOptionParam(applyIds[0], 1);
            } else {
                item.getOptionParam(applyIds[0], 0);
            }
            if (count >= 4) {
                item.getOptionParam(applyIds[1], 1);
            } else {
                item.getOptionParam(applyIds[1], 0);
            }
            if (count >= 5) {
                item.getOptionParam(applyIds[2], 1);
            } else {
                item.getOptionParam(applyIds[2], 0);
            }
        }
    }

    public void resetSetNew(Player player, Player pl, Player plbox, int startId, int endId, int[] applyIds) {
        int count = 0;
        for (Item items : player.inventory.itemsBody) {
            if (items == null || !items.isNotNullItem()) {
                continue;
            }
            for (ItemOption io : items.itemOptions) {
                if (io.optionTemplate.id >= startId && io.optionTemplate.id <= endId) {
                    count++;
                    break;
                }
            }
        }
        for (Item item : player.inventory.itemsBody) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (count < 2) {
                item.getOptionParam(applyIds[0], 0);
            }
            if (count < 4) {
                item.getOptionParam(applyIds[1], 0);
            }
            if (count < 5) {
                item.getOptionParam(applyIds[2], 0);
            }
        }
        for (Item item : player.inventory.itemsBag) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            for (int id : applyIds) {
                item.getOptionParam(id, 0);
            }
        }
        if (pl != null) {
            for (Item item : pl.inventory.itemsBag) {
                if (item == null || !item.isNotNullItem()) {
                    continue;
                }
                for (int id : applyIds) {
                    item.getOptionParam(id, 0);
                }
            }
        }
        if (plbox != null) {
            for (Item item : plbox.inventory.itemsBox) {
                if (item == null || !item.isNotNullItem()) {
                    continue;
                }
                for (int id : applyIds) {
                    item.getOptionParam(id, 0);
                }
            }
        }
    }

    public void countItemsInBoxCollection(Player player) {
        if (player == null || player.inventory == null || player.inventory.itemsBoxCollection == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        int count = 0;
        for (Item item : player.inventory.itemsBoxCollection) {
            if (item != null && item.isNotNullItem()) {
                count++;
            }
        }
        Service.gI().sendThongBao(player, "Trong rương sưu tầm hiện có " + count + " vật phẩm");
    }

    public int HpItemsInBoxCollection(Player player) {
        if (player == null || player.inventory == null || player.inventory.itemsBoxCollection == null) {
            return 0;
        }
        int count = 0;
        for (Item item : player.inventory.itemsBoxCollection) {
            if (item != null && item.isNotNullItem()) {
                count++;
            }
        }
        if (count >= 120) {
            return 30;
        } else if (count >= 100) {
            return 20;
        } else if (count >= 80) {
            return 15;
        } else if (count >= 60) {
            return 10;
        } else if (count >= 40) {
            return 5;
        } else if (count >= 30) {
            return 4;
        } else if (count >= 20) {
            return 2;
        } else if (count >= 10) {
            return 1;
        }
        return 0;
    }

    public int MpItemsInBoxCollection(Player player) {
        if (player == null || player.inventory == null || player.inventory.itemsBoxCollection == null) {
            return 0;
        }
        int count = 0;
        for (Item item : player.inventory.itemsBoxCollection) {
            if (item != null && item.isNotNullItem()) {
                count++;
            }
        }
        if (count >= 120) {
            return 35;
        } else if (count >= 100) {
            return 30;
        } else if (count >= 80) {
            return 24;
        } else if (count >= 60) {
            return 18;
        } else if (count >= 50) {
            return 12;
        } else if (count >= 35) {
            return 7;
        } else if (count >= 20) {
            return 4;
        } else if (count >= 10) {
            return 2;
        } else if (count >= 5) {
            return 1;
        }
        return 0;
    }

    public int DamageItemsInBoxCollection(Player player) {
        if (player == null || player.inventory == null || player.inventory.itemsBoxCollection == null) {
            return 0;
        }
        int count = 0;
        for (Item item : player.inventory.itemsBoxCollection) {
            if (item != null && item.isNotNullItem()) {
                count++;
            }
        }
        if (count >= 120) {
            return 25;
        } else if (count >= 100) {
            return 18;
        } else if (count >= 90) {
            return 15;
        } else if (count >= 70) {
            return 7;
        } else if (count >= 50) {
            return 5;
        } else if (count >= 30) {
            return 2;
        } else if (count >= 15) {
            return 1;
        }
        return 0;
    }

    public int CritItemsInBoxCollection(Player player) {
        if (player == null || player.inventory == null || player.inventory.itemsBoxCollection == null) {
            return 0;
        }
        int count = 0;
        for (Item item : player.inventory.itemsBoxCollection) {
            if (item != null && item.isNotNullItem()) {
                count++;
            }
        }
        if (count >= 120) {
            return 5;
        } else if (count >= 100) {
            return 4;
        } else if (count >= 70) {
            return 3;
        } else if (count >= 40) {
            return 2;
        } else if (count >= 20) {
            return 1;
        }
        return 0;
    }

}
