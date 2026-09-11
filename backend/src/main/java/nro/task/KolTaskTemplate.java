package nro.task;

import java.util.ArrayList;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;



public class KolTaskTemplate {

    public int id;
    public String info;
    public int max_count;
    public ArrayList<Item> rewards;

    public KolTaskTemplate(int id, String info, int max_count) {
        this.id = id;
        this.info = info;
        this.max_count = max_count;
        this.rewards = getRewardItem();
    }

    private ArrayList<Item> getRewardItem() {
        rewards = new ArrayList<>();
        switch (id) {
            case 0: {
                rewards.add(ItemService.gI().createNewItemLock(1820, 3));
                break;
            }
            case 1: {
                rewards.add(ItemService.gI().createNewItemLock(1592, 5));
                rewards.add(ItemService.gI().createNewItemLock(1757, 5));
                break;
            }
            case 2: {
                rewards.add(ItemService.gI().createNewItemLock(1360, 1));
                break;
            }
            case 3: {
                Item cerBerus = ItemService.gI().createNewItemLock(1654, 1);
                cerBerus.itemOptions.add(new ItemOption(93, 120));
                rewards.add(cerBerus);
                break;
            }
            case 4: {
                rewards.add(ItemService.gI().createNewItemLock(1808, 1));
                break;
            }
            case 5: {
                Item caiTrang = ItemService.gI().createNewItemLock(1829, 1);
                caiTrang.itemOptions.add(new ItemOption(50, 25));
                caiTrang.itemOptions.add(new ItemOption(103, 30));
                caiTrang.itemOptions.add(new ItemOption(93, 180));
                rewards.add(caiTrang);
                rewards.add(ItemService.gI().createNewItemLock(1592, 5));
                rewards.add(ItemService.gI().createNewItemLock(1757, 5));
                break;
            }
            case 6: {
                rewards.add(ItemService.gI().createNewItemLock(1592, 10));
                rewards.add(ItemService.gI().createNewItemLock(1757, 10));
                break;
            }
        }
        return rewards;
    }
}






