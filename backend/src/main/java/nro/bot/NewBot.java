package nro.bot;

import nro.server.Manager;
import Utils.SkillUtil;
import Utils.Util;
import java.util.Random;
import nro.player.NPoint;
import nro.template.ItemTemplate;

public class NewBot {
    
    public static NewBot i;
    
    private boolean LOAD_PART = true;
    private int MAXPART = 0;
    private static int[][] PARTBOT = new int[Manager.ITEM_TEMPLATES.size()][4];
            
    private final String[] FIRST_NAMES = {"vuong", "lyhh", "truong", "tien", "lan", "minh", "hoang", "hieu", "nguyen", "khai", "dung", "son", "quang", "sang", 
    "duong", "tung", "khang", "bao", "tam", "thao", "quyen", "yen", "nhan", "dai", "bich", "kieu", "hien", "hoai", "phu", 
    "duc", "lam", "quoc", "tai", "long", "giang", "phi", "hieu", "ngoc", "son", "thang", "tuan", "kieu", "sieu", "my", "lanh", 
    "tru", "hoang", "vinh", "cuong", "tinh", "hieu", "anh", "phuc", "truong", "binh", "bao", "tung", "dai", "minh", "dat", 
    "linh", "hoang", "bich", "alex", "john", "mary", "david", "lily", "james", "emma", "olivia", "sophia", "jackson", 
    "harry", "lucas", "mia", "ella", "chloe", "grace", "daniel", "victor", "william", "ella", "rose", "leon", "max", "scarlett", 
    "isabella", "noah", "mason", "charlotte", "benjamin", "lucy", "ethan", "henry", "ella", "zoe", "sarah", "hannah", "lucas", 
    "jack", "aiden", "ryan", "kate", "leo", "sophie", "adam", "rebecca", "susan", "paul", "andrew", "willow", "samantha", 
    "elizabeth", "katherine", "chris", "ryder", "leah", "eva", "matthew", "brian", "joshua", "natalie", "louis", "susan", 
    "victoria", "austin", "alison", "evan", "harper", "peter", "george", "sean", "emily", "caroline", "sydney", "oliver", 
    "eva", "rebecca", "clara", "toby", "grace", "elise", "lillian", "nina", "harrison", "elliot", "julia", "isaac", "ivy", 
    "abigail", "arthur", "caleb", "alexa", "grace", "finn", "lily", "seraphina", "aubrey", "dylan", "anderson", "jackson", 
    "michael", "luke", "eliza", "lucy", "ryan", "amanda", "claire", "cameron", "sophie", "taylor", "stella", "lauren", 
    "madeline", "george", "morgan", "jack", "jason", "jared", "jackson", "susan", "hannah", "willow", "zoe", "noah", "abby", 
    "maria", "hayden", "eliana", "james", "johnny", "daisy", "nathan", "harrison", "olivia", "aiden", "toby", "ashton", 
    "kelsey", "lucas", "julia", "tessa", "grayson", "ethan", "jake", "ryan", "isabel", "maya", "victor", "kyle", "lisa", 
    "courtney", "gabriella", "cameron", "ella", "leon", "maddie", "matilda", "aaron", "serena", "ella", "sydney", "william", 
    "sara", "lucas", "julian", "jennifer", "henry", "peter", "paige", "olga", "emma", "vicky", "ryan", "abby", "isabella", 
    "carter", "michael", "phoebe", "lucy", "mackenzie", "zoey", "jonathan", "jaden", "chloe", "oliver", "bella", "catherine", 
    "evelyn", "maggie", "ivy", "catherine", "logan", "emily", "grace", "leo", "thomas", "olivia", "richard", "aidan", 
    "willow", "jackson", "summer", "jasmine", "sophie", "megan", "kevin", "madeline", "victoria", "charlotte", "alex", 
    "matthew", "olivia", "victoria", "ella", "keira", "cameron", "elizabeth", "laura", "sarah", "camila", "blake", 
    "maya", "jacob", "susan", "michelle", "nicholas", "matthew", "ella", "maria", "lillian", "isaac", "charles", "hailey", 
    "melissa", "robert", "grace", "maddox", "henry", "adam", "chris", "quinn", "natalie", "zoe", "olivia", "aubrey", 
    "joshua", "george", "olga", "bryce", "adam", "noelle", "luke", "zoe", "will", "megan", "paul", "tessa", "lucas",
    "jackson", "samuel", "jacqueline", "lucas", "maddie", "josephine", "thomas", "mariah", "barbara", "elise", "riley", 
    "hailey", "jayden", "derek", "paul", "camden", "everly", "matthew", "ella", "julia", "nora", "mason", "ariel", "carter", 
    "kristen", "lily", "anna", "lucas", "emma", "will", "bailey", "jason", "grayson", "tristan", "zoe", "lindsey", "meredith",
    "colton", "lexi", "william", "blake", "evan", "maggie", "evelyn", "mason", "victoria", "elijah", "madeline", "susan", 
    "olivia", "ava", "lincoln", "noah", "ryan", "sophia", "brianna", "bennett", "katherine", "kate", "joseph", "ella",
    "isla", "chad", "ryan", "willow", "morgan", "nathan", "cora", "miley", "olivia", "lucas", "emily", "gianna", "bailey"};

    private final String[] LAST_NAMES = {"vuong", "lyhh", "truong", "tien", "lan", "minh", "hoang", "hieu", "nguyen", "khai", "dung", "son", "quang", "sang", 
    "duong", "tung", "khang", "bao", "tam", "thao", "quyen", "yen", "nhan", "dai", "bich", "kieu", "hien", "hoai", "phu", 
    "duc", "lam", "quoc", "tai", "long", "giang", "phi", "hieu", "ngoc", "son", "thang", "tuan", "kieu", "sieu", "my", "lanh", 
    "tru", "hoang", "vinh", "cuong", "tinh", "hieu", "anh", "phuc", "truong", "binh", "bao", "tung", "dai", "minh", "dat", 
    "linh", "hoang", "bich", "alex", "john", "mary", "david", "lily", "james", "emma", "olivia", "sophia", "jackson", 
    "harry", "lucas", "mia", "ella", "chloe", "grace", "daniel", "victor", "william", "ella", "rose", "leon", "max", "scarlett", 
    "isabella", "noah", "mason", "charlotte", "benjamin", "lucy", "ethan", "henry", "ella", "zoe", "sarah", "hannah", "lucas", 
    "jack", "aiden", "ryan", "kate", "leo", "sophie", "adam", "rebecca", "susan", "paul", "andrew", "willow", "samantha", 
    "elizabeth", "katherine", "chris", "ryder", "leah", "eva", "matthew", "brian", "joshua", "natalie", "louis", "susan", 
    "victoria", "austin", "alison", "evan", "harper", "peter", "george", "sean", "emily", "caroline", "sydney", "oliver", 
    "eva", "rebecca", "clara", "toby", "grace", "elise", "lillian", "nina", "harrison", "elliot", "julia", "isaac", "ivy", 
    "abigail", "arthur", "caleb", "alexa", "grace", "finn", "lily", "seraphina", "aubrey", "dylan", "anderson", "jackson", 
    "michael", "luke", "eliza", "lucy", "ryan", "amanda", "claire", "cameron", "sophie", "taylor", "stella", "lauren", 
    "madeline", "george", "morgan", "jack", "jason", "jared", "jackson", "susan", "hannah", "willow", "zoe", "noah", "abby", 
    "maria", "hayden", "eliana", "james", "johnny", "daisy", "nathan", "harrison", "olivia", "aiden", "toby", "ashton", 
    "kelsey", "lucas", "julia", "tessa", "grayson", "ethan", "jake", "ryan", "isabel", "maya", "victor", "kyle", "lisa", 
    "courtney", "gabriella", "cameron", "ella", "leon", "maddie", "matilda", "aaron", "serena", "ella", "sydney", "william", 
    "sara", "lucas", "julian", "jennifer", "henry", "peter", "paige", "olga", "emma", "vicky", "ryan", "abby", "isabella", 
    "carter", "michael", "phoebe", "lucy", "mackenzie", "zoey", "jonathan", "jaden", "chloe", "oliver", "bella", "catherine", 
    "evelyn", "maggie", "ivy", "catherine", "logan", "emily", "grace", "leo", "thomas", "olivia", "richard", "aidan", 
    "willow", "jackson", "summer", "jasmine", "sophie", "megan", "kevin", "madeline", "victoria", "charlotte", "alex", 
    "matthew", "olivia", "victoria", "ella", "keira", "cameron", "elizabeth", "laura", "sarah", "camila", "blake", 
    "maya", "jacob", "susan", "michelle", "nicholas", "matthew", "ella", "maria", "lillian", "isaac", "charles", "hailey", 
    "melissa", "robert", "grace", "maddox", "henry", "adam", "chris", "quinn", "natalie", "zoe", "olivia", "aubrey", 
    "joshua", "george", "olga", "bryce", "adam", "noelle", "luke", "zoe", "will", "megan", "paul", "tessa", "lucas",
    "jackson", "samuel", "jacqueline", "lucas", "maddie", "josephine", "thomas", "mariah", "barbara", "elise", "riley", 
    "hailey", "jayden", "derek", "paul", "camden", "everly", "matthew", "ella", "julia", "nora", "mason", "ariel", "carter", 
    "kristen", "lily", "anna", "lucas", "emma", "will", "bailey", "jason", "grayson", "tristan", "zoe", "lindsey", "meredith",
    "colton", "lexi", "william", "blake", "evan", "maggie", "evelyn", "mason", "victoria", "elijah", "madeline", "susan", 
    "olivia", "ava", "lincoln", "noah", "ryan", "sophia", "brianna", "bennett", "katherine", "kate", "joseph", "ella",
    "isla", "chad", "ryan", "willow", "morgan", "nathan", "cora", "miley", "olivia", "lucas", "emily", "gianna", "bailey",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", 
    "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty"};
    
    
    public static NewBot gI(){
        if (i == null) {
            i = new NewBot();
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
   
    public String GetnameRandom(){
        return FIRST_NAMES[new Random().nextInt(FIRST_NAMES.length)] + LAST_NAMES[new Random().nextInt(LAST_NAMES.length)];
    }
    
    public String Getname() {
        String[] names = {"minh", "huyen", "nam", "lan", "hieu", "tuan", "long", "phuc", "trang", "thao",
            "hung", "linh", "hai", "ngoc", "hoang", "vy", "son", "tram", "tung", "ha",
            "khang", "an", "bao", "phuong", "quan", "yen", "nhung", "thang", "diem", "dat",
            "hanh", "trung", "nga", "quynh", "phu", "van", "khanh", "tuyet", "tai", "loan",
            "kien", "huong", "phong", "thuy", "tri", "oanh", "duc", "my", "anh", "nhi",
            "hao", "ngan", "bich", "tam", "phat", "uyen", "nhat", "ly", "vu", "giang",
            "thien", "thanh", "khoi", "le", "duy", "nha", "hong", "thinh", "phuoc", "toan",
            "huy", "tin", "sonvip", "minhpro", "hieudz", "tuanvip", "longvip", "phucvip",
            "khangpro", "baodep", "tungvip", "trivip", "datvip", "nhatvip", "phongvip",
            "duyvip", "khoipro", "quanvip", "trungvip", "thanhvip", "phatvip", "khanhvip",
            "linhvip", "ngocvip", "sonpro", "minhvip", "tuanpro", "longpro", "hieupro",
            "anvip", "baovip", "giangvip", "huyvip", "nhivip", "myvip", "tramvip",
            "tungpro", "thangvip", "datpro", "phongpro", "nhatpro", "duypro",
            "minh123", "hieu123", "tuan123", "long123", "nam123", "hung123", "hai123",
            "minh999", "hieu999", "tuan999", "long999", "nam999", "hung999", "hai999"};
        return names[Util.nextInt(names.length)] + Util.nextInt(10, 999);
    }
   
    public int getIndex(int gender){
        int Random = new Random().nextInt(MAXPART);
        int gend = PARTBOT[Random][3];
        if(gend == gender){
            return Random;
        } else {
            return getIndex(gender);
        }
    }
                
    public void runBot(int type , ShopBot shop, SellBot shopsell, int slot){
        if (!BotManager.ALLOW_CREATE_BOT) {
        return;
    }
        LoadPart();
        for (int i = 0; i < slot ; i++) {
            int Gender = new Random().nextInt(3);
            int Random1 = getIndex(Gender);
            int head = (type == 0 ? (Gender == 0 ? (Util.isTrue(40, 100) ? 64 : (Util.isTrue(40, 100) ? 30 : 31)) : 
                    (Gender == 1 ? (Util.isTrue(40, 100) ? 29 : (Util.isTrue(40, 100) ? 9 : 32)) : 
                    (Util.isTrue(40, 100) ? 6 : (Util.isTrue(40, 100) ? 27 : 28)))) :
                    (type == 1 ? PARTBOT[Random1][0] : PARTBOT[Random1][0]));
            int leg = (type == 0 ? (Gender == 0 ? (Util.isTrue(40, 100) ? 2 : (Util.isTrue(12, 100) ? 15 : (Util.isTrue(33, 100) ? 66
                    : (Util.isTrue(33, 100) ? 72 : (Util.isTrue(33, 100) ? 156 : 158))))) : 
                    (Gender == 1 ? (Util.isTrue(12, 100) ? 11 : (Util.isTrue(50, 100) ? 13 : (Util.isTrue(33, 100) ? 68
                    : (Util.isTrue(33, 100) ? 76 : (Util.isTrue(33, 100) ? 150 : 154))))) : 
                    (Util.isTrue(40, 100) ? 8 : (Util.isTrue(12, 100) ? 17 : (Util.isTrue(33, 100) ? 70 : (Util.isTrue(33, 100) ? 74
                    : (Util.isTrue(33, 100) ? 148 : 152))))))) : 
                    (type == 1 ? PARTBOT[Random1][1] : PARTBOT[Random1][1]));
            int body = (type == 0 ? (Gender == 0 ? (Util.isTrue(40, 100) ? 1 : (Util.isTrue(12, 100) ? 14 : (Util.isTrue(33, 100) ? 65
                    : (Util.isTrue(33, 100) ? 71 : (Util.isTrue(33, 100) ? 155 : 157))))) : 
                    (Gender == 1 ? (Util.isTrue(12, 100) ? 10 : (Util.isTrue(50, 100) ? 12 : (Util.isTrue(33, 100) ? 67
                    : (Util.isTrue(33, 100) ? 75 : (Util.isTrue(33, 100) ? 149 : 153))))) : 
                    (Util.isTrue(40, 100) ? 7 : (Util.isTrue(12, 100) ? 16 : (Util.isTrue(33, 100) ? 69 : (Util.isTrue(33, 100) ? 73
                    : (Util.isTrue(33, 100) ? 147 : 151))))))) : 
                    (type == 1 ? PARTBOT[Random1][2] : PARTBOT[Random1][2]));
            if (shop != null){
                shop = new ShopBot(shop);
            }
            if (shopsell != null){
                shopsell = new SellBot(shopsell);
            }
            int mount = ((type == 0 || type == 2) ? (Util.isTrue(50, 100) ? Util.nextInt(15, 25) : -1) : -1);
            int cflag = (type == 0 ? (Util.isTrue(5, 100) ? Util.nextInt(1, 7) : Util.isTrue(8, 100) ? 8 : 0) : 0);
            int flag = (type == 0 ? (Util.isTrue(30, 100) ? -1 : Util.isTrue(12, 100) ? 19 : Util.isTrue(14, 100) ? 20 : Util.isTrue(16, 100) ? 21 : 
                    Util.isTrue(18, 100) ? 22 : Util.isTrue(20, 100) ? 23 : Util.isTrue(22, 100) ? 24 : Util.isTrue(24, 100) ? 25 : 
                    Util.isTrue(26, 100) ? 26 : Util.isTrue(28, 100) ? 27 : Util.isTrue(30, 100) ? 29 : -1) :
                    (type == 1 || type == 3) ? (Util.isTrue(25, 100) ? -1 : Util.isTrue(80, 100) ? Util.nextInt(50, 117) : Util.isTrue(99, 100) ? Util.nextInt(126, 143) : -1) :
                    type == 2 ? (Util.isTrue(70, 100) ? Util.nextInt(50, 117) : Util.isTrue(90, 100) ? Util.nextInt(126, 143) : -1) :
                    type == 4 ? (Util.isTrue(50, 100) ? Util.nextInt(0, 27) : Util.isTrue(60, 100) ? 20 : Util.isTrue(80, 100) ? 29 : -1) : -1);
            boolean detu = (Util.isTrue(20, 100) ? true : false);
            Bot b = new Bot(Util.nextInt(1_900_000_000, 2_000_000_000), 
                    (short) head, (short) body, (short) leg, 
                    type, (Util.isTrue(50, 100) ? Getname() : GetnameRandom()), 
                    shop, shopsell, (short) flag, 
                    (byte) cflag, detu, (short) mount);
            BotAttackBoss boss = new BotAttackBoss(b);
            BotAttackMob mob = new BotAttackMob(b);
            b.mo1 = mob;
            b.boss = boss;
            b.nPoint.limitPower = NPoint.MAX_LIMIT;
            switch (type) {
                case 0:
                    int randomCase = Util.nextInt(0, 21);
                    switch (randomCase) {
                        case 0:
                        case 1:
                        case 2:
                            b.nPoint.power = Util.nextInt(1200, 16000);
                            b.nPoint.tiemNang = Util.nextInt(1200, 16000);
                            b.nPoint.hpg = Util.nextInt(100, 400);
                            b.nPoint.dameg = Util.nextInt(10, 20);
                            break;
                        case 3:
                        case 4:
                        case 5:
                            b.nPoint.power = Util.nextInt(16000, 100000);
                            b.nPoint.tiemNang = Util.nextInt(16000, 100000);
                            b.nPoint.hpg = Util.nextInt(300, 800);
                            b.nPoint.dameg = Util.nextInt(20, 50);
                            break;
                        case 6:
                            b.nPoint.power = Util.nextInt(100000, 500000);
                            b.nPoint.tiemNang = Util.nextInt(100000, 500000);
                            b.nPoint.hpg = Util.nextInt(500, 1500);
                            b.nPoint.dameg = Util.nextInt(70, 200);
                            break;
                        case 7:
                            b.nPoint.power = Util.nextInt(500_000, 1_500_000);
                            b.nPoint.tiemNang = Util.nextInt(500_000, 1_500_000);
                            b.nPoint.hpg = Util.nextInt(1200, 2500);
                            b.nPoint.dameg = Util.nextInt(100, 500);
                            break;
                        case 8:
                            b.nPoint.power = Util.nextInt(1_500_000, 50_000_000);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000, 50_000_000);
                            b.nPoint.hpg = Util.nextInt(5000, 10000);
                            b.nPoint.dameg = Util.nextInt(500, 1200);
                            break;
                        case 9:
                            b.nPoint.power = Util.nextInt(50_000_000, 200_000_000);
                            b.nPoint.tiemNang = Util.nextInt(50_000_000, 100_000_000);
                            b.nPoint.hpg = Util.nextInt(15000, 30000);
                            b.nPoint.dameg = Util.nextInt(2000, 4000);
                            break;
                        case 10:
                            b.nPoint.power = Util.nextInt(200_000_000, 500_000_000);
                            b.nPoint.tiemNang = Util.nextInt(100_000_000, 300_000_000);
                            b.nPoint.hpg = Util.nextInt(25000, 40000);
                            b.nPoint.dameg = Util.nextInt(4000, 7000);
                            break;
                        case 11:
                            b.nPoint.power = Util.nextInt(500_000_000, 1_000_000_000);
                            b.nPoint.tiemNang = Util.nextInt(300_000_000, 600_000_000);
                            b.nPoint.hpg = Util.nextInt(40000, 60000);
                            b.nPoint.dameg = Util.nextInt(7000, 10000);
                            break;
                        case 12:
                            b.nPoint.power = Util.nextLong(1_000_000_000L, 5_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(70000, 120000);
                            b.nPoint.dameg = Util.nextInt(9000, 12000);
                            break;
                        case 13:
                            b.nPoint.power = Util.nextLong(5_000_000_000L, 10_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(120000, 180000);
                            b.nPoint.dameg = Util.nextInt(12000, 15000);
                            break;
                        case 14:
                            b.nPoint.power = Util.nextLong(10_000_000_000L, 25_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(180000, 250000);
                            b.nPoint.dameg = Util.nextInt(15000, 22000);
                            break;
                        case 15:
                            b.nPoint.power = Util.nextLong(25_000_000_000L, 30_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(250000, 350000);
                            b.nPoint.dameg = Util.nextInt(22000, 26000);
                            break;
                        case 16:
                            b.nPoint.power = Util.nextLong(30_000_000_000L, 35_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(300000, 450000);
                            b.nPoint.dameg = Util.nextInt(26000, 32000);
                            break;
                        case 17:
                            b.nPoint.power = Util.nextLong(35_000_000_000L, 40_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(400000, 550000);
                            b.nPoint.dameg = Util.nextInt(32000, 50000);
                            break;
                        case 18:
                            b.nPoint.power = Util.nextLong(40_000_000_000L, 50_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.isKhongLanh = true;
                            b.nPoint.hpg = Util.nextInt(600000, 800000);
                            b.nPoint.dameg = Util.nextInt(80000, 160000);
                            break;
                        case 19:
                            b.nPoint.power = Util.nextLong(50_000_000_000L, 70_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.hpg = Util.nextInt(1_000_000, 2_000_000);
                            b.nPoint.dameg = Util.nextInt(200000, 500000);
                            break;
                        case 20:
                            b.nPoint.power = Util.nextLong(70_000_000_000L, 80_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.isKhongLanh = true;
                            b.nPoint.hpg = Util.nextInt(2_000_000, 5_000_000);
                            b.nPoint.dameg = Util.nextInt(500000, 1_000_000);
                            break;
                        case 21:
                            b.nPoint.power = Util.nextLong(80_000_000_000L, 100_000_000_000L);
                            b.nPoint.tiemNang = Util.nextInt(1_500_000_000, 2_000_000_000);
                            b.nPoint.isKhongLanh = true;
                            b.nPoint.hpg = Util.nextInt(2_000_000, 5_000_000);
                            b.nPoint.dameg = Util.nextInt(500000, 1_000_000);
                            break;
                    }
                    // Gán các giá trị còn lại
                    b.nPoint.hp = b.nPoint.hpg;
                    b.nPoint.hpMax = b.nPoint.hpg;
                    b.nPoint.dame = b.nPoint.dameg;
                    break;
                case 1:
                    b.nPoint.power = Util.nextLong(1_000_000_000L, 50_000_000_000L);
                    b.nPoint.tiemNang = Util.nextInt(1, 2);
                    b.nPoint.hpg = Util.nextInt(100_000, 800_000);
                    b.nPoint.hp = b.nPoint.hpg;
                    b.nPoint.hpMax = b.nPoint.hpg;
                    b.nPoint.dameg = Util.nextInt(20_000, 50_000);
                    b.nPoint.dame = b.nPoint.dameg;
                    break;
                case 2:
                    b.nPoint.power = Util.nextLong(10_000_000_000L, 100_000_000_000L);
                    b.nPoint.tiemNang = Util.nextInt(1, 2);
                    b.nPoint.hpg = b.nPoint.power / 20000;
                    b.nPoint.hp = b.nPoint.hpg;
                    b.nPoint.hpMax = b.nPoint.hpg;
                    b.nPoint.dameg = b.nPoint.power / 200000;
                    b.nPoint.dame = b.nPoint.dameg;
                    break;
                case 3:
                    b.nPoint.power = Util.nextLong(2_000_000L, 10_000_000_000L);
                    b.nPoint.tiemNang = Util.nextInt(1, 2);
                    b.nPoint.hpg = Util.nextInt(50_000, 400_000);
                    b.nPoint.hp = b.nPoint.hpg;
                    b.nPoint.hpMax = b.nPoint.hpg;
                    b.nPoint.dameg = Util.nextInt(10_000, 30_000);
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
            if (b.nPoint.power >= 50_000_000 && b.nPoint.power < 500_000_000) {
                if (Util.isTrue(15, 100)) {
                    b.OptionHut = 5;
                    b.OptionHut = 5;
                }
            } else if (b.nPoint.power >= 500_000_000 && b.nPoint.power < 2_500_000_000L) {
                if (Util.isTrue(14, 100)) {
                    b.OptionHut = 10;
                    b.OptionHut = 10;
                }
            } else if (b.nPoint.power >= 2_500_000_000L && b.nPoint.power < 10_000_000_000L) {
                if (Util.isTrue(12, 100)) {
                    b.OptionHut = 15;
                    b.OptionHut = 15;
                }
            } else if (b.nPoint.power >= 10_000_000_000L && b.nPoint.power < 40_000_000_000L) {
                if (Util.isTrue(10, 100)) {
                    b.OptionHut = 20;
                    b.OptionHut = 20;
                }
            } else if (b.nPoint.power >= 40_000_000_000L && b.nPoint.power < 60_000_000_000L) {
                if (Util.isTrue(8, 100)) {
                    b.OptionHut = 25;
                    b.OptionHut = 25;
                }
            } else if (b.nPoint.power >= 60_000_000_000L) {
                if (Util.isTrue(6, 100)) {
                    b.OptionHut = 30;
                    b.OptionHut = 30;
                }
            }
            int point = 1;
            if (b.nPoint.power < 10_000_000) {
                point = 1;
            } else if (b.nPoint.power >= 10_000_000 && b.nPoint.power < 50_000_000) {
                point = 2;
            } else if (b.nPoint.power >= 50_000_000 && b.nPoint.power < 150_000_000) {
                point = 3;
            } else if (b.nPoint.power >= 150_000_000 && b.nPoint.power < 500_000_000) {
                point = 4;
            } else if (b.nPoint.power >= 500_000_000 && b.nPoint.power < 1_500_000_000) {
                point = 5;
            } else if (b.nPoint.power >= 1_500_000_000 && b.nPoint.power < 20_000_000_000L) {
                point = 6;
            } else if (b.nPoint.power >= 20_000_000_000L) {
                point = 7;
            }
            b.gender = (byte) Gender;
            switch (b.gender) {
                case 0:
                    b.playerSkill.skills.add(SkillUtil.createSkill(0, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(1, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(6, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(9, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(10, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(20, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(22, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(19, point));
                    break;
                case 1:
                    if (b.nPoint.power > 150_000_000) {
                        b.playerSkill.skills.add(SkillUtil.createSkill(17, point));
                    } else {
                        b.playerSkill.skills.add(SkillUtil.createSkill(2, point));
                    }
                    b.playerSkill.skills.add(SkillUtil.createSkill(3, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(7, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(11, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(12, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(18, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(19, point));
                    break;
                case 2:
                    b.playerSkill.skills.add(SkillUtil.createSkill(4, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(5, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(8, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(13, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(14, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(21, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(23, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(19, point));
                default:
                    break;
            }
            b.joinMap();
            if (shop != null) {
                shop.bot = b;
            }
            if (shopsell != null) {
                shopsell.sellbot = b;
            }
            if (b != null) {
                BotManager.gI().bot.add(b);
            }
        }
    }
}