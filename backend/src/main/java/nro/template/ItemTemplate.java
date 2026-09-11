package nro.template;


public class ItemTemplate {
    
    public short id;

    public byte type;

    public byte gender;

    public String name;

    public String description;

    public byte level = 0;

    public short iconID;

    public short part;

    public boolean isUpToUp;

    public int strRequire;

    public int gold;
    
    public int goldSell;

    public int gem;
    
    public int gemSell;
    
    public int ruby;
    
    public int ruby_sell;

    public int head;

    public int body;

    public int leg;

    public int TypeEvent;
    protected byte LUNNAR_NEW_YEAR = 1;
    protected byte CHRISTMAS = 2;
    protected byte VU_LAN_FESTIVAL = 3;
    protected byte HALLOWEEN = 4;
    protected byte INTERNATIONAL_WOMANS_DAY = 5;
    protected byte TRUNG_THU = 6;
    protected byte HUNG_VUONG = 7;
    protected byte BLACK_FRIDAY = 8;
    protected byte WORLD_CUP = 9;
    protected byte VALENTINE = 10;
    protected byte DAY_20_10 = 11;

    public byte isGender;

    public ItemTemplate() {
    }

    public ItemTemplate(short id, byte type, byte gender, String name, String description, short iconID, short part, boolean isUpToUp, int strRequire, int TypeEvent, byte isGender) {
        this.id = id;
        this.type = type;
        this.gender = gender;
        this.name = name;
        this.description = description;
        this.iconID = iconID;
        this.part = part;
        this.isUpToUp = isUpToUp;
        this.strRequire = strRequire;
        this.TypeEvent = TypeEvent;
        this.isGender = isGender;
    }
}






