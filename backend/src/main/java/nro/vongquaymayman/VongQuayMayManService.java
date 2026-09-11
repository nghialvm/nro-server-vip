package nro.vongquaymayman;

import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import network.io.Message;
import nro.player.Player;

public class VongQuayMayManService {
    
    private static volatile VongQuayMayManService instance;
    private final List<Item> listItem;
    private final List<Integer> listItemProbabilities;
    private final List<Integer> listItemQuantities;
    private static final int JACKPOT_TRIGGER_COUNT = 100;
    private static final int ITEM_KEY_ID = 457;
    private static final int SPECIAL_ITEM_ID = 1001;

    private final ConcurrentMap<Integer, Integer> jackpotBox;
    private final Set<Player> playersParticipated;

    private VongQuayMayManService() {
        this.listItem = new ArrayList<>();
        this.listItemProbabilities = new ArrayList<>();
        this.listItemQuantities = new ArrayList<>();
        this.jackpotBox = new ConcurrentHashMap<>();
        this.playersParticipated = Collections.newSetFromMap(new ConcurrentHashMap<>());
        loadItem();
    }

    public static VongQuayMayManService gI() {
        if (instance == null) {
            synchronized (VongQuayMayManService.class) {
                if (instance == null) {
                    instance = new VongQuayMayManService();
                }
            }
        }
        return instance;
    }

    public void loadItem() {
        int[] idItemT = {14, 16, 15, 17, 18, 19, 20, 634, 1001, 220, 222, 224};
        int[] probabilities = {5, 10, 15, 20, 10, 10, 5, 5, 5, 5, 5, 5};
        int[] quantities = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        for (int i = 0; i < idItemT.length; i++) {
            Item item = ItemService.gI().createNewItem((short) idItemT[i]);
            listItem.add(item);
            listItemProbabilities.add(probabilities[i]);
            listItemQuantities.add(quantities[i]);
        }
    }

    private Item getRandomItem() {
        int totalProbability = listItemProbabilities.stream().mapToInt(Integer::intValue).sum();
        int randomValue = Util.nextInt(totalProbability);
        int cumulativeProbability = 0;

        for (int i = 0; i < listItem.size(); i++) {
            cumulativeProbability += listItemProbabilities.get(i);
            if (randomValue < cumulativeProbability) {
                return createNewItemWithQuantity(listItem.get(i), listItemQuantities.get(i));
            }
        }
        
        return createNewItemWithQuantity(
            listItem.get(listItem.size() - 1), 
            listItemQuantities.get(listItemQuantities.size() - 1)
        );
    }

    private Item createNewItemWithQuantity(Item template, int quantity) {
        Item newItem = ItemService.gI().createNewItem((short) template.template.id);
        newItem.quantity = quantity;
        
        if (newItem.template.id == SPECIAL_ITEM_ID || newItem.template.id == 634) {
            addSpecialItemOptions(newItem);
        } 
        
        return newItem;
    }

    private void addSpecialItemOptions(Item item) {
        item.itemOptions.add(new ItemOption(50, 15));
        item.itemOptions.add(new ItemOption(77, 15));
        item.itemOptions.add(new ItemOption(103, 15));
    }

    public void readData(Message msg, Player player) {
        try {
            int type = msg.reader().readByte();
            
            if (type == 0) {
                sendMessage(player, (byte) type, listItem);
                return;
            }
            
            if (type == 1 || type == 4) {
                handleSpinRequest(msg, player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSpinRequest(Message msg, Player player) throws IOException {
        int spinCount = msg.reader().readInt();
        int costPerSpin = 1;
        int totalCost = spinCount * costPerSpin;
        
        if (!validateSpinRequest(player, spinCount, totalCost)) {
            return;
        }

        int keysUsed = consumeKeys(player, totalCost);
        updateJackpotAndParticipants(player, keysUsed);
        
        List<Item> receivedItems = performSpins(player, spinCount);
        if (!receivedItems.isEmpty()) {
            addItemsToInventory(player, receivedItems);
            sendListReceive(player, receivedItems);
        }
    }

    private boolean validateSpinRequest(Player player, int spinCount, int totalCost) {
        int availableKeys = countKeyInBag(player, ITEM_KEY_ID);
        if (availableKeys < totalCost) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ TV để quay");
            return false;
        }

        int emptySlots = (int) player.inventory.itemsBag.stream()
            .filter(it -> it == null || it.template == null)
            .count();
            
        if (emptySlots < spinCount) {
            Service.getInstance().sendThongBao(player, "Hành trang không đủ chỗ trống");
            return false;
        }

        return true;
    }

    private int consumeKeys(Player player, int totalCost) {
        int remainingCost = totalCost;
        int keysUsed = 0;

        Iterator<Item> iterator = player.inventory.itemsBag.iterator();
        while (iterator.hasNext() && remainingCost > 0) {
            Item item = iterator.next();
            if (item != null && item.template != null && item.template.id == ITEM_KEY_ID) {
                int useAmount = Math.min(remainingCost, item.quantity);
                item.quantity -= useAmount;
                keysUsed += useAmount;
                remainingCost -= useAmount;

                if (item.quantity <= 0) {
                    iterator.remove();
                }
            }
        }

        return keysUsed;
    }

    private void updateJackpotAndParticipants(Player player, int keysUsed) {
        jackpotBox.merge(ITEM_KEY_ID, keysUsed, Integer::sum);
        playersParticipated.add(player);
    }

    private List<Item> performSpins(Player player, int spinCount) {
        List<Item> receivedItems = new ArrayList<>();
        int totalItemsAdded = 0;

        for (int i = 0; i < spinCount; i++) {
            Item randomItem = getRandomItem();
            receivedItems.add(randomItem);
            sendItemImage(player, randomItem);
            
            totalItemsAdded += randomItem.quantity;
            jackpotBox.merge(randomItem.template.id & 0xFFFF, randomItem.quantity, Integer::sum);

            if (totalItemsAdded >= JACKPOT_TRIGGER_COUNT) {
                processJackpot();
                break;
            }
        }

        return receivedItems;
    }

    private void addItemsToInventory(Player player, List<Item> items) {
        for (Item item : items) {
            Item bagItem = ItemService.gI().createNewItem((short) item.template.id);
            bagItem.quantity = item.quantity;
            bagItem.itemOptions = new ArrayList<>(item.itemOptions);
            InventoryService.gI().addItemBag(player, bagItem);
        }
        InventoryService.gI().sendItemBag(player);
    }

    private void processJackpot() {
        List<Player> playersList = new ArrayList<>(playersParticipated);
        if (playersList.isEmpty()) {
            return;
        }

        Player winner = playersList.get(new Random().nextInt(playersList.size()));
        Service.getInstance().sendThongBao(winner, 
            "Chúc mừng! " + winner.name + " đã trúng hũ và nhận được phần thưởng lớn!");

        distributeJackpotPrizes(winner);
        resetJackpot();
    }

    private void distributeJackpotPrizes(Player winner) {
        for (Map.Entry<Integer, Integer> entry : jackpotBox.entrySet()) {
            Item item = ItemService.gI().createNewItem((short) entry.getKey().shortValue());
            item.quantity = entry.getValue();
            InventoryService.gI().addItemBag(winner, item);
        }
    }

    private void resetJackpot() {
        jackpotBox.clear();
        playersParticipated.clear();
    }

    private int countKeyInBag(Player player, int itemId) {
        return player.inventory.itemsBag.stream()
                .filter(it -> it != null && it.template != null && it.template.id == itemId)
                .mapToInt(it -> it.quantity)
                .sum();
    }

    private void sendMessage(Player player, byte type, List<Item> items) {
        try {
            Message msg = new Message(69);
            msg.writer().writeByte(type);
            msg.writer().writeInt(items.size());
            
            for (Item item : items) {
                msg.writer().writeInt(item.template.id);
                msg.writer().writeInt(0); // Color
            }
            
            msg.writer().writeInt(jackpotBox.getOrDefault(ITEM_KEY_ID, 0));
            msg.writer().writeInt(4028);
            msg.writer().writeInt(4028);
            
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendListReceive(Player player, List<Item> items) {
        try {
            Message msg = new Message(69);
            msg.writer().writeByte(1);
            msg.writer().writeInt(items.size());
            
            for (Item item : items) {
                msg.writer().writeInt(item.template.id);
                msg.writer().writeInt(0);
            }
            
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendItemImage(Player player, Item item) {
        try {
            Message msg = new Message(70);
            msg.writer().writeInt(item.template.id);
            msg.writer().writeInt(0);
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}