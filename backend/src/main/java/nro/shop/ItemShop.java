package nro.shop;

import java.util.ArrayList;
import java.util.List;
import models.Item.ItemOption;
import nro.shop.TabShops.TabShop;
import nro.template.ItemTemplate;


public class ItemShop {
    
    public TabShop tabShop;

    public int id;
    
    public ItemTemplate temp;
    
    public boolean isNew;
    
    public List<ItemOption> options;
    
    public byte typeSell;
    
    public int iconSpec;
    
    public int cost;
    
    public int cost_2;

    public ItemShop() {
        this.options = new ArrayList<>();
    }
    
    public ItemShop(ItemShop itemShop){
        this.options = new ArrayList<>();
        this.tabShop = itemShop.tabShop;
        this.id = itemShop.id;
        this.temp = itemShop.temp;
        this.isNew = itemShop.isNew;
        this.typeSell = itemShop.typeSell;
        this.iconSpec = itemShop.iconSpec;
        this.cost = itemShop.cost;
        this.cost_2 = itemShop.cost_2;
        for(ItemOption io : itemShop.options){
            this.options.add(new ItemOption(io));
        }
    }
    
    public void dispose(){
        this.tabShop = null;
        this.temp = null;
        if(this.options != null){
            for(ItemOption io : this.options){
                io.dispose();
            }
            this.options.clear();
        }
        this.options = null;
    }
    
    public byte getLevelSkill() {
        String[] subName = temp.name.split("");
        byte level = Byte.parseByte(subName[subName.length - 1]);
        return level;
    }
    
}





