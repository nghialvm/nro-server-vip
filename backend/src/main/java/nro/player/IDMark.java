package nro.player;

import consts.ConstNpc;
import nro.npc.Npc;
import nro.shop.Shop;
import lombok.Data;
import nro.map.Zone;

@Data
public class IDMark {

    private int idItemUpTop;
    private int typeChangeMap; 
    private int indexMenu; // menu npc
    private int typeInput; 
    private byte typeLuckyRound; 

    private long idPlayThachDau;
    private int goldThachDau;
    private long killCharId = -9999;

    private long idEnemy; 

    private Shop shopOpen; 
    private String tagNameShop; 

    private byte idSpaceShip;
    
    private long damePST;

    private long lastTimeBan;
    private boolean isBan;
            
    //giao dịch
    private boolean acpTrade;
    private int playerTradeId = -1;
    private Player playerTrade;
    private long lastTimeTrade;

    private long lastTimeNotifyTimeHoldBlackBall;
    private long lastTimeHoldBlackBall;
    private int tempIdBlackBallHold = -1;
    private boolean holdBlackBall;

    private int tempIdNamecBallHold = -1;
    private boolean holdNamecBall;
    
    private byte isTranhNgoc = -1;

    private boolean loadedAllDataPlayer; 

    private long lastTimeChangeFlag;

    private int mbv;
    
    private int shenronType = -1;
    
    private boolean gotoFuture;
    private long lastTimeGoToFuture;
    
    private Zone zoneKhiGasHuyDiet;
    private int xMapKhiGasHuyDiet;
    private int yMapKhiGasHuyDiet;
    private boolean goToKGHD;
    private long lastTimeGoToKGHD;

    private long lastTimeChangeZone;
    private long lastTimeChatGlobal;
    private long lastTimeChatPrivate;

    private long lastTimePickItem;

    private boolean goToBDKB;
    private long lastTimeGoToBDKB;
    private long lastTimeAnXienTrapBDKB;
        
    private boolean GoToDuHanhThoiGian;
    private long LastTimeDuHanhThoiGian;
    private boolean GoToTroVeThoiGian;
    private long LastTimeTroVeThoiGian;
    private boolean GoToTroVeThoiGian2;
    private long LastTimeTroVeThoiGian2;
       
    private boolean goToHome;
    private long lastTimeGoToHome;
   
    private Npc npcChose; 

    private byte loaiThe; 
    
    private long lastTimeRevenge;
    
    private boolean transactionWP;
    private boolean transactionWVP;
    
    private int ott;
    private int menuType;

    // ✅ thêm field để nhớ NPC nào mở menu
    private byte npcId;  

    public byte getTranhNgoc() {
        return isTranhNgoc;
    }
    
    public void setTranhNgoc(byte tn) {
        this.isTranhNgoc = tn;
    }
    
    public byte getIsTranhNgoc() {
        return isTranhNgoc;
    }

    public void setIsTranhNgoc(byte isTranhNgoc) {
        this.isTranhNgoc = isTranhNgoc;
    }
    
    public boolean isBaseMenu() {
        return this.indexMenu == ConstNpc.BASE_MENU;
    }

    public void dispose() {
        if (this.shopOpen != null) {
            this.shopOpen.dispose();
            this.shopOpen = null;
        }
        this.npcChose = null;
        this.tagNameShop = null;
        this.playerTrade = null;
        this.zoneKhiGasHuyDiet = null;
    }

    // ✅ Getter/Setter cho npcId
    public void setNpcId(byte npcId) {
        this.npcId = npcId;
    }

    public byte getNpcId() {
        return this.npcId;
    }
}
