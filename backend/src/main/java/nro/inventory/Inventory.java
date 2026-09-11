package nro.inventory;

import java.util.ArrayList;
import java.util.List;
import models.Item.Item;
import models.Item.ItemOption;

public class Inventory {

    public static final long LIMIT_GOLD = 2_000_000_000_000L;
    public static final long LIMIT_GOLD2 = 2_000_000_000L;

    public static final int LIMIT_RUBY = 2_000_000_000;
    
    public static final int LIMIT_GEM = 2_000_000_000;
    
    public static final int LIMIT_EXP_VIP = 2_000_000_000;
    
    public static final int MAX_ITEMS_BAG = 100;
    public static final int MAX_ITEMS_BOX = 100;

    public Item trainArmor;
    public List<String> giftCode;
    public List<Item> itemsBody;
    public List<Item> itemsBag;
    public List<Item> itemsBox;
    public List<Item> itemsBoxCollection;

    public List<Item> itemsBoxCrackBall;
    public List<Item> itemsMailBox;
    public List<Item> itemsDaBan;
    
    public long gold;
    public int gem;
    public int ruby;
    public int Exp_Vip;
        
    public Inventory() {
        itemsBody = new ArrayList<>();
        itemsBag = new ArrayList<>();
        itemsBox = new ArrayList<>();
        itemsBoxCollection = new ArrayList<>();
        itemsBoxCrackBall = new ArrayList<>();
        itemsMailBox = new ArrayList<>();
        giftCode = new ArrayList<>();
        itemsDaBan = new ArrayList<>();
    }

    public int getGemAndRuby() {
        return this.gem + this.ruby;
    }
    
    public int getGem() {
        return this.gem;
    }
    
    public int getRuby() {
        return this.ruby;
    }
    
    public int getExpVip() {
        return this.Exp_Vip;
    }
    
    public long getGold() {
        return this.gold;
    }
    
    public long getGoldLimit() {
        return LIMIT_GOLD;
    }
    
    public int getParam(Item it , int id){
        for(ItemOption op : it.itemOptions){
            if(op!=null&&op.optionTemplate.id ==id){
                return op.param;
            }
        }
        return 0;
    }

    
    public boolean haveOption(List<Item> l , int index , int id){
        Item it = l.get(index);
        if(it != null && it.isNotNullItem()){
            return it.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == id);
        }
        return false;
    }
    
    public void subGem(int num) {
        this.gem -= num;
    }

    public void subGold(int num) {
        this.gold -= num;
    }
    
    public void subGold(long num) {
        this.gold -= num;
    }

    public void subRuby(int num) {
        this.ruby -= num;
    }
    
    public void subExpVip(int num) {
        this.Exp_Vip -= num;
    }
    
    public void subGemAndRuby(int num) {
        this.ruby -= num;
        if (this.ruby < 0) {
            this.gem += this.ruby;
            this.ruby = 0;
        }
    }

    public void addGold(int gold) {
        this.gold += gold;
        if (this.gold > LIMIT_GOLD) {
            this.gold = LIMIT_GOLD;
        }
    }
    
    
    public void addRuby(int ruby) {
        this.ruby += ruby;
        if (this.ruby > LIMIT_RUBY) {
            this.ruby = LIMIT_RUBY;
        }
    }
        
    public void addGem(int gem) {
        this.gem += gem;
        if (this.gem > LIMIT_GEM) {
            this.gem = LIMIT_GEM;
        }
    }
    
    public void addExpVip(int vip) {
        this.Exp_Vip += vip;
        if (this.Exp_Vip > LIMIT_EXP_VIP) {
            this.Exp_Vip = LIMIT_EXP_VIP;
        }
    }

    public void dispose() {
        if (this.trainArmor != null) {
            this.trainArmor.dispose();
        }
        this.trainArmor = null;
        if (this.itemsBody != null) {
            for(Item it : this.itemsBody){
                it.dispose();
            }
            this.itemsBody.clear();
        }
        if (this.itemsBag != null){
            for(Item it : this.itemsBag) {
                it.dispose();
            }
            this.itemsBag.clear();
        }
        if (this.itemsBox != null){
            for(Item it : this.itemsBox) {
                it.dispose();
            }
            this.itemsBox.clear();
        }
        if (this.itemsBoxCollection != null){
            for(Item it : this.itemsBoxCollection) {
                it.dispose();
            }
            this.itemsBoxCollection.clear();
        }
        if (this.itemsBoxCrackBall != null) {
            for(Item it : this.itemsBoxCrackBall){
                it.dispose();
            }
            this.itemsBoxCrackBall.clear();
        }
        if (this.itemsMailBox != null) {
            for (Item it : this.itemsMailBox) {
                it.dispose();
            }
            this.itemsMailBox.clear();
        }
        if (this.itemsDaBan != null) {
            for (Item it : this.itemsDaBan) {
                it.dispose();
            }
            this.itemsDaBan.clear();
        }
        this.itemsBody = null;
        this.itemsBag = null;
        this.itemsBox = null;
        this.itemsBoxCollection = null;
        this.itemsBoxCrackBall = null;
        this.itemsMailBox = null;
        this.itemsDaBan = null;
    }
}





