package nro.bot;

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

public class SellBot {
    
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
   
    public Bot sellbot;
    private Player pl;
   
    public SellBot(int item , int traodoi , int slot){
        this.idItem = item;
        this.idItTd = traodoi;
        this.slot = slot;
    }
   
    public SellBot(SellBot shop){
        this.idItem = shop.idItem;
        this.idItTd = shop.idItTd;
        this.slot = shop.slot;
    }
   
    public void update(){
        MoveTo();
        FixmoveTo();
        switch (sellbot.gender) {
            case 0:
                if (this.sellbot.nPoint.power < 2_000_000_000) {
                    this.mapTraiDat();
                } else {
                    this.mapTraiDatVIP();
                }
                break;
            case 1:
                if (this.sellbot.nPoint.power < 2_000_000_000) {
                    this.mapNamec();
                } else {
                    this.mapNamecVIP();
                }
                break;
            case 2:
                if (this.sellbot.nPoint.power < 2_000_000_000) {
                    this.mapXayda();
                } else {
                    this.mapXaydaVIP();
                }
                break;
            default:
                break;
        }
        this.chat();
    }
        
    public void MoveTo() {
        if (this.sellbot.effectSkill.isStone || this.sellbot.effectSkin.isDraburaFrost || this.sellbot.isDie()) {
            return;
        }
        switch (this.sellbot.gender) {
            case 0:
                if (Util.canDoWithTime(TimeRunMaptraidat, Util.nextInt(2000, 4000))) {
                    PlayerService.gI().playerMove(this.sellbot, this.sellbot.location.x + Util.nextInt(-30, 30), this.sellbot.location.y);
                    TimeRunMaptraidat = System.currentTimeMillis();
                }
                break;
            case 1:
                if (Util.canDoWithTime(TimeRunMapnamec, Util.nextInt(2000, 4000))) {
                    PlayerService.gI().playerMove(this.sellbot, this.sellbot.location.x + Util.nextInt(-30, 30), this.sellbot.location.y);
                    TimeRunMapnamec = System.currentTimeMillis();
                }
                break;
            case 2:
                if (Util.canDoWithTime(TimeRunMapxayda, Util.nextInt(2000, 4000))) {
                    PlayerService.gI().playerMove(this.sellbot, this.sellbot.location.x + Util.nextInt(-30, 30), this.sellbot.location.y);
                    TimeRunMapxayda = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }
    
    public void FixmoveTo() {
        if (this.sellbot.zone != null) {
            if (this.sellbot.effectSkill.isStone || this.sellbot.effectSkin.isDraburaFrost || this.sellbot.isDie()) {
                return;
            }
            switch (this.sellbot.zone.map.mapId) {
                case 0:
                    if (this.sellbot.location.x < 150) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(160, 180), 432);
                    } else if (this.sellbot.location.x > 600) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(550, 580), 432);
                    }   break;
                case 5:
                    if (this.sellbot.location.x < 900) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(910, 940), 408);
                    } else if (this.sellbot.location.x > 1300) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(1250, 1290), 408);
                    }   break;
                case 7:
                    if (this.sellbot.location.x < 200) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(210, 230), 432);
                    } else if (this.sellbot.location.x > 600) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(550, 580), 432);
                    }   break;
                case 13:
                    if (this.sellbot.location.x < 580) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(590, 620), 384);
                    } else if (this.sellbot.location.x > 900) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(850, 890), 384);
                    }   break;
                case 14:
                    if (this.sellbot.location.x < 150) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(160, 180), 408);
                    } else if (this.sellbot.location.x > 500) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(450, 480), 408);
                    }   break;
                case 20:
                    if (this.sellbot.location.x < 800) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(810, 840), 360);
                    } else if (this.sellbot.location.x > 1150) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(1100, 1140), 360);
                    }   break;
                case 124:
                    if (this.sellbot.location.x < 2650) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(2660, 2680), 312);
                    } else if (this.sellbot.location.x > 2900) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(2850, 2890), 312);
                    }   break;
                case 194:
                    if (this.sellbot.location.x < 700) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(710, 740), 144);
                    } else if (this.sellbot.location.x > 1100) {
                        PlayerService.gI().playerMove(this.sellbot, Util.nextInt(1050, 1090), 144);
                    }   break;
                default:
                    break;
            }
        }
    }
   
    public String getChat(){
        Item it = ItemService.gI().createNewItem((short) this.idItem);
        Item it1 = ItemService.gI().createNewItem((short) this.idItTd);
        String text = String.format((Util.isTrue(50, 100) ? "mua" : Util.isTrue(50, 100) ? "cần mua" : "m")
                + ((this.idItTd >= 17 && this.idItTd <= 20) ? (Util.isTrue(50, 100) ? " x99 viên %s " : " 99 viên %s ") : " %s ") +
                (Util.isTrue(50, 100) ? " giá" : Util.isTrue(50, 100) ? " với giá" : " ja")
                + (this.idItTd == 14 ? (Util.isTrue(50, 100) ? " x15 %s/1" : " 15 %s/1 viên") :
                this.idItTd == 15 ? (Util.isTrue(50, 100) ? " x4 %s/1" : " 4 %s/1 viên") :
                this.idItTd == 16 ? (Util.isTrue(50, 100) ? " x6 %s/1" : " 6 %s/1 viên") :
                this.idItTd == 17 ? (Util.isTrue(50, 100) ? " x4 %s" : " 4 %s") :
                this.idItTd == 18 ? (Util.isTrue(50, 100) ? " x3 %s" : " 3 %s") :
                this.idItTd == 19 ? (Util.isTrue(50, 100) ? " x2 %s" : " 2 %s") :
                this.idItTd == 20 ? (Util.isTrue(50, 100) ? " x1 %s" : " 1 %s") 
                : " x %s")
                + (Util.isTrue(40, 100) ? ", cần sll, " :  Util.isTrue(40, 100) ? ", ai có pm, " : Util.isTrue(40, 100) ? ", có tele qua, " : ", đang cần gấp, ")
                + "%s "
                + (Util.isTrue(50, 100) ? "k" : "khu") + "%d",
                it1.template.name, it.template.name , this.sellbot.zone.map.mapName , this.sellbot.zone.zoneId);
        return text;
    }
    
    public void chat(){
        switch (sellbot.gender) {
            case 0:
                if (Util.canDoWithTime(lastimeChattraidat, Util.nextInt(30000, 60000))){
                    if (this.sellbot.zone != null) {
                        ChatGlobalService.gI().chatTGbot(this.sellbot , this.getChat());
                    }
                    lastimeChattraidat = System.currentTimeMillis();
                }
                break;
            case 1:
                if (Util.canDoWithTime(lastimeChatnamec, Util.nextInt(30000, 60000))){
                    if (this.sellbot.zone != null) {
                        ChatGlobalService.gI().chatTGbot(this.sellbot , this.getChat());
                    }
                    lastimeChatnamec = System.currentTimeMillis();
                }
                break;
            default:
                if (Util.canDoWithTime(lastimeChatxayda, Util.nextInt(30000, 60000))){
                    if (this.sellbot.zone != null) {
                        ChatGlobalService.gI().chatTGbot(this.sellbot , this.getChat());
                    }
                    lastimeChatxayda = System.currentTimeMillis();
                }
                break;
        }
        if (Util.canDoWithTime(lastimeChatTrain, Util.nextInt(3000, 5000))) {
            if (this.sellbot.zone != null) {
                Service.gI().chat(this.sellbot , getChat());
            }
            this.lastimeChatTrain = System.currentTimeMillis();
        }
    }
   
   
   public void activeTraDe(Player pl){
      trade = new Trade(pl , sellbot);
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
   
    public void active(int sl){
        int sl1 = (int) Math.round((double) sl / this.slot);
        switch (this.idItTd) {
            case 14:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1 * 15);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
            case 15:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1 * 4);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
            case 16:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1 * 6);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
            case 17:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1 * 4);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
            case 18:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1 * 3);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
            case 19:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1 * 2);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
            case 20:
                {
                    Item it = ItemService.gI().createNewItem((short) this.idItem , sl1);
                    it.addOptionParam(73, 1);
                    this.trade.addItemBot(it);
                    this.trade.lockTran(this.sellbot);
                    this.trade.acceptTrade();
                    break;
                }
        }
    }
    //--------------------------------------------------------------------------
    
    public void mapTraiDat(){
        if (this.sellbot.zone != null && this.sellbot.zone.map.mapId != 0) {
            Zone zone = this.sellbot.getRandomZone(0);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.sellbot, zone);
                this.sellbot.zone.load_Me_To_Another(this.sellbot);
                PlayerService.gI().playerMove(this.sellbot, Util.nextInt(150, 500), 432);
            }
        }
    }
    public void mapTraiDatVIP(){
        if (this.sellbot.zone != null && this.sellbot.zone.map.mapId != 5) {
            Zone zone = this.sellbot.getRandomZone(5);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.sellbot, zone);
                this.sellbot.zone.load_Me_To_Another(this.sellbot);
                PlayerService.gI().playerMove(this.sellbot, Util.nextInt(900, 1300), 408);
            }
        }
    }
    //--------------------------------------------------------------------------
    public void mapNamec(){
        if (this.sellbot.zone != null && this.sellbot.zone.map.mapId != 7) {
            Zone zone = this.sellbot.getRandomZone(7);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.sellbot, zone);
                this.sellbot.zone.load_Me_To_Another(this.sellbot);
                PlayerService.gI().playerMove(this.sellbot, Util.nextInt(200, 600), 432);
            }
        }
    }
    public void mapNamecVIP(){
        if (this.sellbot.zone != null && this.sellbot.zone.map.mapId != 13) {
            Zone zone = this.sellbot.getRandomZone(13);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.sellbot, zone);
                this.sellbot.zone.load_Me_To_Another(this.sellbot);
                PlayerService.gI().playerMove(this.sellbot, Util.nextInt(600, 900), 384);
            }
        }
    }
    //--------------------------------------------------------------------------
    public void mapXayda(){
        if (this.sellbot.zone != null && this.sellbot.zone.map.mapId != 14) {
            Zone zone = this.sellbot.getRandomZone(14);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.sellbot, zone);
                this.sellbot.zone.load_Me_To_Another(this.sellbot);
                PlayerService.gI().playerMove(this.sellbot, Util.nextInt(200, 500), 408);
            }
        }
    }
    public void mapXaydaVIP(){
        if (this.sellbot.zone != null && this.sellbot.zone.map.mapId != 20) {
            Zone zone = this.sellbot.getRandomZone(20);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.sellbot, zone);
                this.sellbot.zone.load_Me_To_Another(this.sellbot);
                PlayerService.gI().playerMove(this.sellbot, Util.nextInt(880, 1100), 360);
            }
        }
    }    
}