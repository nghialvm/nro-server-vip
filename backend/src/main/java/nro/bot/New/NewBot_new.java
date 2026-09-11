package nro.bot.New;

import nro.server.Manager;
import Utils.SkillUtil;
import Utils.Util;
import java.util.Random;
import nro.player.NPoint;
import nro.template.ItemTemplate;

public class NewBot_new {
    
    public static NewBot_new i;
    
    private int Style = 0;
    
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
    
    
    public static NewBot_new gI(){
        if (i == null) {
            i = new NewBot_new();
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
                        PARTBOT[i][1] = it.body;
                        PARTBOT[i][2] = it.leg;
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
        if(gend == gender){
            return Random;
        } else {
            return getIndex(gender);
        }
    }
                    
    public void runBot_new(int type, BotAttackPlayer_1 attack_1, BotAttackPlayer_2 attack_2, BotAttackPlayer_3 attack_3, int slot){
        LoadPart();
        for (int i = 0; i < slot ; i++) {
            if (Util.isTrue(50, 100)) {
                this.Style = 1;
            } else if (Util.isTrue(15, 100)) {
                this.Style = 2;
            } else if (Util.isTrue(15, 100)) {
                this.Style = 3;
            } else if (Util.isTrue(15, 100)) {
                this.Style = 4;
            } else if (Util.isTrue(20, 100)) {
                this.Style = 5;
            } else if (Util.isTrue(30, 100)) {
                this.Style = 6;
            } else if (Util.isTrue(40, 100)) {
                this.Style = 7;
            } else if (Util.isTrue(50, 100)) {
                this.Style = 8;
            } else if (Util.isTrue(50, 100)) {
                this.Style = 9;
            } else if (Util.isTrue(50, 100)) {
                this.Style = 10;
            } else if (Util.isTrue(50, 100)) {
                this.Style = 11;
            } else if (Util.isTrue(60, 100)) {
                this.Style = 12;
            } else {
                this.Style = 13;
            }
            int Gender = new Random().nextInt(3);
            int Random1 = getIndex(Gender);
            int head = ((type >= 0 && type <= 10) ? (Style == 1 ? 377 : Style == 2 ? 234 : Style == 3 ? 409 : Style == 4 ? 554 : Style == 5 ? 356 : Style == 6 ? 92 : Style == 7 ? 189 : Style == 8 ? 180 
                    : Style == 9 ? 347 : Style == 10 ? 264 : Style == 11 ? 231 : Style == 12 ? 403 : Style == 13 ? 306 : PARTBOT[Random1][0]) : PARTBOT[Random1][0]);
            int body = ((type >= 0 && type <= 10) ? (Style == 1 ? 378 : Style == 2 ? 235 : Style == 3 ? 410 : Style == 4 ? 555 : Style == 5 ? 357 : Style == 6 ? 93 : Style == 7 ? 190 : Style == 8 ? 181 
                    : Style == 9 ? 348 : Style == 10 ? 265 : Style == 11 ? 232 : Style == 12 ? 404 : Style == 13 ? 307 : PARTBOT[Random1][1]) : PARTBOT[Random1][1]);
            int leg = ((type >= 0 && type <= 10) ? (Style == 1 ? 379 : Style == 2 ? 236 : Style == 3 ? 411 : Style == 4 ? 556 : Style == 5 ? 358 : Style == 6 ? 94 : Style == 7 ? 191 : Style == 8 ? 182 
                    : Style == 9 ? 349 : Style == 10 ? 266 : Style == 11 ? 233 : Style == 12 ? 405 : Style == 13 ? 308 : PARTBOT[Random1][2]) : PARTBOT[Random1][2]);
            if (attack_1 != null){
                attack_1 = new BotAttackPlayer_1(attack_1);
            }
            if (attack_2 != null){
                attack_2 = new BotAttackPlayer_2(attack_2);
            }
            if (attack_3 != null){
                attack_3 = new BotAttackPlayer_3(attack_3);
            }
            int mount = (Util.isTrue(80, 100) ? Util.nextInt(15, 25) : -1);
            int cflag = ((type >= 0 && type <= 10) ? (Util.isTrue(70, 100) ? 8 : 0) : (type >= 11 && type <= 13) ? Util.nextInt(0, 7) : 0);
            int flag = ((type >= 0 && type <= 10) ? (Util.isTrue(40, 100) ? 20 : Util.isTrue(40, 100) ? 29 : Util.isTrue(60, 100) ? Util.nextInt(37, 117) : -1) : Util.nextInt(37, 117));
            Bot_new b = new Bot_new(Util.nextInt(1_900_000_000, 2_000_000_000), (short) head, (short) body, (short) leg, type, Getname(), attack_1, attack_2, attack_3, (short) flag, (byte) cflag, (short) mount);
            BotDetu_1 detu = new BotDetu_1(b);
            BotDetu_2 detu_2 = new BotDetu_2(b);
            BotDetu_3 detu_3 = new BotDetu_3(b);
            BotDetu_4 detu_4 = new BotDetu_4(b);
            BotDetu_5 detu_5 = new BotDetu_5(b);
            BotDetu_6 detu_6 = new BotDetu_6(b);
            BotDetu_7 detu_7 = new BotDetu_7(b);
            BotDetu_8 detu_8 = new BotDetu_8(b);
            BotDetu_9 detu_9 = new BotDetu_9(b);
            BotDetu_10 detu_10 = new BotDetu_10(b);
            BotDetu_11 detu_11 = new BotDetu_11(b);
            b.detu = detu;
            b.detu_2 = detu_2;
            b.detu_3 = detu_3;
            b.detu_4 = detu_4;
            b.detu_5 = detu_5;
            b.detu_6 = detu_6;
            b.detu_7 = detu_7;
            b.detu_8 = detu_8;
            b.detu_9 = detu_9;
            b.detu_10 = detu_10;
            b.detu_11 = detu_11;
            b.nPoint.limitPower = NPoint.MAX_LIMIT;
            switch (type) {
                case 0:
                case 1:
                case 2:
                    if (Util.isTrue(50, 100)) {
                        b.nPoint.power = Util.nextLong(200_000_000L, 10_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(200_000_000L, 10_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(10_000_000_000L, 25_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(10_000_000_000L, 25_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
                    break;
                case 3:
                    if (Util.isTrue(40, 100)) {
                        b.nPoint.power = Util.nextLong(5_000_000_000L, 10_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(5_000_000_000L, 20_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(10_000_000_000L, 20_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(5_000_000_000L, 20_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
                    break;
                case 4:
                case 5:
                    if (Util.isTrue(50, 100)) {
                        b.nPoint.power = Util.nextLong(20_000_000_000L, 30_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(20_000_000_000L, 30_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(30_000_000_000L, 40_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(30_000_000_000L, 40_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
                    break;
                case 6:
                case 7:
                    if (Util.isTrue(50, 100)) {
                        b.nPoint.power = Util.nextLong(30_000_000_000L, 40_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(20_000_000_000L, 30_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(40_000_000_000L, 55_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(30_000_000_000L, 40_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
                    break;
                case 8:
                    if (Util.isTrue(50, 100)) {
                        b.nPoint.power = Util.nextLong(40_000_000_000L, 50_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(40_000_000_000L, 50_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(50_000_000_000L, 65_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(50_000_000_000L, 60_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
                case 9:
                case 10:
                    if (Util.isTrue(50, 100)) {
                        b.nPoint.power = Util.nextLong(40_000_000_000L, 50_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(40_000_000_000L, 50_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(50_000_000_000L, 60_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(50_000_000_000L, 60_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90000, b.nPoint.power / 60000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 2_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
                    break;
                case 11:
                case 12:
                case 13:
                    if (Util.isTrue(50, 100)) {
                        b.nPoint.power = Util.nextLong(1_000_000_000L, 40_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(1_000_000_000L, 40_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90_000, b.nPoint.power / 60_000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 3_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    } else {
                        b.nPoint.power = Util.nextLong(40_000_000_000L, 80_000_000_000L);
                        b.nPoint.tiemNang = Util.nextLong(40_000_000_000L, 80_000_000_000L);
                        b.nPoint.hpg = Util.nextLong(b.nPoint.power / 90_000, b.nPoint.power / 60_000);
                        b.nPoint.hp = b.nPoint.hpg;
                        b.nPoint.hpMax = b.nPoint.hpg;
                        b.nPoint.dameg = Util.nextLong(b.nPoint.power / 3_000_000, b.nPoint.power / 1_000_000);
                        b.nPoint.dame = b.nPoint.dameg;
                    }
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
            b.Style_2 = this.Style;
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
                    b.playerSkill.skills.add(SkillUtil.createSkill(21, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(23, point));
                    b.playerSkill.skills.add(SkillUtil.createSkill(19, point));
                default:
                    break;
            }
            b.joinMap();
            if (attack_1 != null) {
                attack_1.bot = b;
            }
            if (attack_2 != null) {
                attack_2.bot = b;
            }
            if (attack_3 != null) {
                attack_3.bot = b;
            }
            if (b != null) {
                BotManager_new.gI().botnew.add(b);
            }
        }
    }
}




