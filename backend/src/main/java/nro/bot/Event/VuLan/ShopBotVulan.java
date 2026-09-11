package nro.bot.Event.VuLan;

import nro.player.Player;
import nro.services.ChatGlobalService;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.Trade;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Util;
import java.util.List;
import nro.map.Zone;
import models.Item.Item;
import models.Item.ItemService;
import nro.bot.Event.BotEvent;

public class ShopBotVulan {
    
    public int idItem;
    public int idItTd;
    public int slot;
   
    private long lastimeChattraidat;
    private long lastimeChatnamec;
    private long lastimeChatxayda;
    private long lastimeChatTrain;
    private Trade trade;
    
    private long TimeRunMapxayda;
    private long TimeRunMaptraidat;
    private long TimeRunMapnamec;
   
    public BotEvent bot;
    private Player pl;
   
    public ShopBotVulan(int item , int traodoi , int slot){
        this.idItem = item;
        this.idItTd = traodoi;
        this.slot = slot;
    }
   
    public ShopBotVulan(ShopBotVulan shop){
        this.idItem = shop.idItem;
        this.idItTd = shop.idItTd;
        this.slot = shop.slot;
    }
   
    public void update(){
        if (this.bot.zone != null) {
            if (this.bot.effectSkill.isStone || this.bot.effectSkin.isDraburaFrost || this.bot.isDie()) {
                return;
            }
            MoveTo();
            FixmoveTo();
        }
        if (bot.nPoint.power > 20_000_000_000L) {
            this.mapSieuThi();
        } else {
            switch (bot.gender) {
                case 0:
                    this.mapTraiDat();
                    break;
                case 1:
                    this.mapNamec();
                    break;
                case 2:
                    this.mapXayda();
                    break;
                default:
                    break;
            }
        }
        this.chat();
    }
        
    public void MoveTo() {
        switch (this.bot.gender) {
            case 0:
                if (Util.canDoWithTime(TimeRunMaptraidat, Util.nextInt(2000, 4000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + Util.nextInt(-60, 60), this.bot.location.y);
                    TimeRunMaptraidat = System.currentTimeMillis();
                }
                break;
            case 1:
                if (Util.canDoWithTime(TimeRunMapnamec, Util.nextInt(2000, 4000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + Util.nextInt(-60, 60), this.bot.location.y);
                    TimeRunMapnamec = System.currentTimeMillis();
                }
                break;
            case 2:
                if (Util.canDoWithTime(TimeRunMapxayda, Util.nextInt(2000, 4000))) {
                    PlayerService.gI().playerMove(this.bot, this.bot.location.x + Util.nextInt(-60, 60), this.bot.location.y);
                    TimeRunMapxayda = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }
    
    public void FixmoveTo() {
        if (this.bot.zone != null) {
            switch (this.bot.zone.map.mapId) {
                case 42:
                    if (this.bot.location.x < 203) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(210, 250), this.bot.location.y);
                    } else if (this.bot.location.x > 1350) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(1300, 1340), this.bot.location.y);
                    }   break;
                case 43:
                    if (this.bot.location.x < 197) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(200, 250), this.bot.location.y);
                    } else if (this.bot.location.x > 1350) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(1300, 1340), this.bot.location.y);
                    }   break;
                case 44:
                    if (this.bot.location.x < 131) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(135, 165), this.bot.location.y);
                    } else if (this.bot.location.x > 1350) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(1300, 1340), this.bot.location.y);
                    }   break;
                case 84:
                    if (this.bot.location.x < 380) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(380, 420), this.bot.location.y);
                    } else if (this.bot.location.x > 800) {
                        PlayerService.gI().playerMove(this.bot, Util.nextInt(750, 790), this.bot.location.y);
                    }   break;
                default:
                    break;
            }
        }
    }
   
    public String getChat() {
        Item it = ItemService.gI().createNewItem((short) this.idItem);
        Item it1 = ItemService.gI().createNewItem((short) this.idItTd);

        // Từ khóa mở đầu: bán, p, b
        String prefix = Util.isTrue(50, 100) ? "bán" : (Util.isTrue(50, 100) ? "p" : "b");

        // Nội dung mô tả item chính
        String itemDesc = String.format(" %s", it.template.name);

        // Cụm "giá", "ja", hoặc rỗng
        String priceText = Util.isTrue(40, 100) ? " giá" : (Util.isTrue(50, 100) ? " ja" : "");

        // Xác định định lượng item (x99, x3, x1, 1)
        String quantityText = Util.isTrue(50, 100) ? "/x99" : "/99";

        // Thêm đoạn mô tả "số lượng còn lại"
        String remainText;
        if (Util.isTrue(40, 100)) {
            remainText = ", còn x" + Util.nextInt(500, 9999) + ", ";
        } else if (Util.isTrue(40, 100)) {
            remainText = ", còn ít mau nhanh tay, ";
        } else if (Util.isTrue(40, 100)) {
            remainText = ", số lượng có hạn, ";
        } else {
            remainText = " sll ";
        }

        // Địa điểm bán
        String mapText = Util.isTrue(50, 100) ? "mau qua" : "đến";

        // Cụm "k", "khu"
        String endText = Util.isTrue(50, 100) ? " k" : " khu";

        // Format kết quả cuối cùng
        return String.format("%s%s%s x%d %s%s%s %s %s%d",
                prefix,
                itemDesc,
                priceText,
                this.slot,
                it1.template.name,
                quantityText,
                remainText,
                mapText,
                this.bot.zone.map.mapName + endText,
                this.bot.zone.zoneId);
    }
    
    public void chat(){
        switch (bot.gender) {
            case 0:
                if (Util.canDoWithTime(lastimeChattraidat, Util.nextInt(50000, 100000))){
                    if (this.bot.zone != null) {
                        ChatGlobalService.gI().chatTGbot(this.bot , this.getChat());
                    }
                    lastimeChattraidat = System.currentTimeMillis();
                }
                break;
            case 1:
                if (Util.canDoWithTime(lastimeChatnamec, Util.nextInt(50000, 100000))){
                    if (this.bot.zone != null) {
                        ChatGlobalService.gI().chatTGbot(this.bot , this.getChat());
                    }
                    lastimeChatnamec = System.currentTimeMillis();
                }
                break;
            default:
                if (Util.canDoWithTime(lastimeChatxayda, Util.nextInt(50000, 100000))){
                    if (this.bot.zone != null) {
                        ChatGlobalService.gI().chatTGbot(this.bot , this.getChat());
                    }
                    lastimeChatxayda = System.currentTimeMillis();
                }
                break;
        }
        if (Util.canDoWithTime(lastimeChatTrain, Util.nextInt(3000, 5000))){
            if (this.bot.zone != null) {
                Service.gI().chat(this.bot , getChat());
            }
            this.lastimeChatTrain = System.currentTimeMillis();
        }
    }
   
   
   public void activeTraDe(Player pl){
      trade = new Trade(pl , bot);
      this.pl = pl;
      this.trade.openTabTrade();
   }
  
    public void CheckTraDe(List<Item> item){
        int slot1 = item.stream()
        .filter(it -> it.template.id == this.idItTd && it.quantity >= this.slot)
        .mapToInt(it -> it.quantity)
        .findFirst()
        .orElse(0);
        boolean check = slot1 > 0;
        if (check){
            active(slot1);
        } else {
            this.trade.cancelTrade();
        }
    }
   
    public void active(int sl) {
        int sl1 = (int) Math.round((double) sl / this.slot);
        int quantity = sl1;

        quantity *= 99;

        Item it = ItemService.gI().createNewItem((short) this.idItem, quantity);
        
        it.addOptionParam(86, 0);
        it.addOptionParam(93, 30);
        
        this.trade.addItemBot(it);
        this.trade.lockTran(this.bot);
        this.trade.acceptTrade();
    }
   
    public void mapSieuThi(){
        if (this.bot.zone != null && this.bot.zone.map.mapId != 84) {
            Zone zone = this.bot.getRandomZone(84);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, Util.nextInt(500, 800), 336);
            }
        }
    }
    public void mapTraiDat(){
        if (this.bot.zone != null && this.bot.zone.map.mapId != 42) {
            Zone zone = this.bot.getRandomZone(42);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, Util.isTrue(50, 100) ? Util.nextInt(203, 493) : Util.nextInt(683, 925), 432);
            }
        }
    }
    public void mapNamec(){
        if (this.bot.zone != null && this.bot.zone.map.mapId != 43) {
            Zone zone = this.bot.getRandomZone(43);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, Util.isTrue(50, 100) ? Util.nextInt(300, 500) : Util.nextInt(1000, 1300), 432);
            }
        }
    }
    public void mapXayda(){
        if (this.bot.zone != null && this.bot.zone.map.mapId != 44) {
            Zone zone = this.bot.getRandomZone(44);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                PlayerService.gI().playerMove(this.bot, Util.isTrue(50, 100) ? Util.nextInt(131, 637) : Util.nextInt(900, 1200), 432);
            }
        }
    }
}