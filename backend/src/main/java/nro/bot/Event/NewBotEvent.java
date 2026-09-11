package nro.bot.Event;

import nro.server.Manager;
import Utils.Util;
import java.util.Random;
import nro.bot.BotManager;
import nro.bot.Event.ChristMas.ShopBotChristMas;
import nro.bot.Event.Halloween.ShopBotHalloween;
import nro.bot.Event.HungVuong.ShopBotHungVuong;
import nro.bot.Event.LunaNewYear.ShopBotLunaNewYear;
import nro.bot.Event.VuLan.ShopBotVulan;
import nro.player.NPoint;
import nro.template.ItemTemplate;

public class NewBotEvent {
    public static NewBotEvent i;
    
    public boolean LOAD_PART = true;
    public int MAXPART = 0;
    public static int[][] PARTBOT = new int[Manager.ITEM_TEMPLATES.size()][4];
    
    private final String[] FIRST_NAMES = {"an", "bao", "cuong", "duc", "hai", "linh", "minh", "nam", "quoc", "tuan",
            "ngoc", "hoang", "khanh", "trung", "dat", "duy", "hanh", "hung", "son", "trong",
            "phuc", "quang", "thanh", "thien", "trinh", "viet", "binh", "diep", "han", "hieu",
            "huy", "linh", "nguyen", "phong", "phuoc", "quyen", "tam", "thao", "thi", "thu", "van",
            "phuong", "thuy", "thoai", "thong", "trang", "truc", "trinh", "truong", "tuyen", "uy",
            "y", "yen", "yen", "dung", "duong", "hieu", "huy", "huong", "khoa", "lam", "linh",
            "mai", "nhan", "nhi", "phu", "quyen", "thanh", "truc", "trung", "tuan", "tung",
            "tuyen", "van", "vi", "vu", "an", "bich", "binh", "bong", "chi", "chinh", "cho",
            "chung", "du", "duy", "duyen", "giang", "giau", "hong", "huong", "huyen", "khuong", "lai"};

    private final String[] LAST_NAMES = {"nguyen", "tran", "le", "pham", "hoang", "huynh", "phan", "vo", "dang", "bui",
            "vu", "dinh", "do", "dao", "huu", "trinh", "truong", "ngoc", "hoai", "nhat",
            "phong", "phuc", "quyen", "tam", "thao", "thi", "thu", "van", "dung", "duong",
            "hieu", "huy", "huong", "khoa", "lam", "linh", "mai", "nhan", "nhi", "phu",
            "quyen", "thanh", "truc", "trung", "tuan", "tung", "tuyen", "van", "vi", "vu",
            "anh", "bao", "binh", "chinh", "chung", "du", "duy", "duyen", "giang", "giau",
            "hong", "huong", "huyen", "khuong", "lai", "lan", "luan", "nhon", "phieu", "phuoc",
            "quoc", "sang", "sau", "suong", "tam", "thang", "thao", "thien", "thu", "tinh",
            "tung", "tuyen", "uyen", "vinh", "xuan", "yen", "quynh", "anh", "hoai", "minh"};
    
    
    public static NewBotEvent gI() {
        if (i == null) {
            i = new NewBotEvent();
        }
        return i;
    }
    
    public void LoadPart() {
        if (LOAD_PART) {
            int i = 0;
            for (ItemTemplate it : Manager.ITEM_TEMPLATES) {
                if (it == null) continue;
                if (it.id > 1770) {
                    break;
                }
                if (it.type == 5) {
                    if (it.head != -1 && it.leg != -1 && it.body != -1 && it.leg != 194) {
                        PARTBOT[i][0] = it.head;
                        PARTBOT[i][1] = it.leg;
                        PARTBOT[i][2] = it.body;
                        PARTBOT[i][3] = it.gender;
                        i++;
                        MAXPART++;
                    }
                }
            }
            LOAD_PART = false;
        }
    }
   
    public String Getname(){
        return FIRST_NAMES[new Random().nextInt(FIRST_NAMES.length)] + LAST_NAMES[new Random().nextInt(LAST_NAMES.length)];
    }
   
   
    public int getIndex(int gender){
        int Random = new Random().nextInt(MAXPART);
        int gend = PARTBOT[Random][3];
        if (gend == gender){
            return Random;
        } else {
            return getIndex(gender);
        }
    }
  
   
    public void runBot(int type, ShopBotLunaNewYear shop, ShopBotChristMas shop2, ShopBotVulan shop3, ShopBotHalloween shop4, ShopBotHungVuong shop5, int slot){
         if (!BotManager.ALLOW_CREATE_BOT) {
        return;
    }

        LoadPart();
        for(int i = 0; i < slot ; i++){
            int Gender = new Random().nextInt(3);
            int Random1 = getIndex(Gender);
            int head = PARTBOT[Random1][0];
            int leg = PARTBOT[Random1][1];
            int body = PARTBOT[Random1][2];
            if (shop != null) {
                shop = new ShopBotLunaNewYear(shop);
            }
            if (shop2 != null) {
                shop2 = new ShopBotChristMas(shop2);
            }
            if (shop3 != null) {
                shop3 = new ShopBotVulan(shop3);
            }
            if (shop4 != null) {
                shop4 = new ShopBotHalloween(shop4);
            }
            if (shop5 != null) {
                shop5 = new ShopBotHungVuong(shop5);
            }
            int id = Util.nextInt(1_000_000_000, 2_000_000_000);
            int flag = Util.nextInt(50, 150);
            BotEvent b = new BotEvent(id, (short) head,(short) body,(short) leg, type, Getname(), shop, shop2, shop3, shop4, shop5, (short) flag);
            b.nPoint.limitPower = NPoint.MAX_LIMIT;
            switch (type) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    b.nPoint.power = Util.nextLong(1_000_000_000L, 50_000_000_000L);
                    b.nPoint.tiemNang = Util.nextInt(1, 2);
                    b.nPoint.hpg = Util.nextInt(100_000, 800_000);
                    b.nPoint.hp = b.nPoint.hpg;
                    b.nPoint.hpMax = b.nPoint.hpg;
                    b.nPoint.dameg = Util.nextInt(20_000, 50_000);
                    b.nPoint.dame = b.nPoint.dameg;
                    break;
                default:
                    break;
            }
            b.nPoint.mpg = 2000000;
            b.nPoint.mp = 2000000;
            b.nPoint.defg = Util.nextInt(1, 20);
            b.nPoint.def = Util.nextInt(1, 20);
            b.nPoint.critg = Util.nextInt(1, 40);
            b.nPoint.crit = Util.nextInt(1, 40);
            b.nPoint.maxStamina = 20000;
            b.nPoint.stamina = 20000;
            b.inventory.gold = 200000000;
            b.inventory.gem = 20000;
            b.inventory.ruby = 20000;
            b.gender = (byte) Gender;
            b.joinMap();
            if (shop != null) {
                shop.bot = b;
            }
            if (shop2 != null) {
                shop2.bot = b;
            }
            if (shop3 != null) {
                shop3.bot = b;
            }
            if (shop4 != null) {
                shop4.bot = b;
            }
            if (shop5 != null) {
                shop5.bot = b;
            }
            if (b != null) {
                BotManagerEvent.gI().bot.add(b);
            }
        }
    }
}




