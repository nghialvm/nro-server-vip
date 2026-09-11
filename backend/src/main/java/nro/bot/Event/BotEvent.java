package nro.bot.Event;

import nro.inventory.Inventory;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.Service;
import Utils.Util;
import consts.ConstPlayer;
import models.Item.ItemService;
import nro.bot.Event.ChristMas.ShopBotChristMas;
import nro.bot.Event.Halloween.ShopBotHalloween;
import nro.bot.Event.HungVuong.ShopBotHungVuong;
import nro.bot.Event.LunaNewYear.ShopBotLunaNewYear;
import nro.bot.Event.VuLan.ShopBotVulan;
import nro.map.Map;
import nro.map.Zone;
import nro.player.Player;

public class BotEvent extends Player {
   private short head_;
   private short body_;
   private short leg_;
   private short flag_;
   public int type;
   private int index_ = 0;
   public ShopBotLunaNewYear shop;
   public ShopBotChristMas shop2;
   public ShopBotVulan shop3;
   public ShopBotHalloween shop4;
   public ShopBotHungVuong shop5;
   
   public BotEvent(int id, short head, short body, short leg, int type, String name, ShopBotLunaNewYear shop, ShopBotChristMas shop2, ShopBotVulan shop3, ShopBotHalloween shop4, 
           ShopBotHungVuong shop5, short flag){
        this.head_ = head;
        this.body_ = body;
        this.leg_ = leg;
        this.shop = shop;
        this.shop2 = shop2;
        this.shop3 = shop3;
        this.shop4 = shop4;
        this.shop5 = shop5;
        this.name = name;
        this.id = id;
        this.type = type;
        this.isBot_Event = true;
        this.flag_ = flag;
        this.inventory = new Inventory();
        this.inventory.gold = 2_000_000_000;
        for (int i = 0; i < 100; i++) {
            this.inventory.itemsBag.add(ItemService.gI().createItemNull());
        }
        for (int i = 0; i < 20; i++) {
            this.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
   }
   
    public int MapToPow(){
        int mapId = 21;
        return mapId;
    }
   
    public void joinMap() {
        Zone zone = getRandomZone(MapToPow());
        if (zone != null){
            ChangeMapService.gI().goToMap(this, zone);
            this.zone.load_Me_To_Another(this);
        }
    }
   
   public Zone getRandomZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                zone = map.zones.stream()
                .filter(z -> z.getNumOfPlayers() == 0)
                .findFirst()
                .orElseGet(() -> {
                    Zone randomZone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
                    return randomZone.isFullPlayer() ? null : randomZone;
                });
            }
        } catch (Exception e) {
        }
        if (zone != null) {
            this.index_ = 0;
            return zone;
        } else {
            this.index_ += 1;
            if (this.index_ >= 20) {
                BotManagerEvent.gI().bot.remove(this);
                ChangeMapService.gI().exitMap(this);
                return null;
            } else {
                return getRandomZone(MapToPow());
            }
        }
    }

   
    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isStone) {
            return 454;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][0];
        }
        if (effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 406;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1413;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 760;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1210;
        } else {
            return this.head_;
        }
    }

    @Override
    public short getBody() {
        if (effectSkill != null && effectSkill.isStone) {
            return 455;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][1];
        }
        if (effectSkill.isMonkey) {
            return 193;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 407;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1414;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 761;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1211;
        } else {
            return this.body_;
        }
    }

    @Override
    public short getLeg() {
        if (effectSkill != null && effectSkill.isStone) {
            return 456;
        }
        if (effectSkill != null && effectSkill.isHalloween) {
            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][2];
        }
        if (effectSkill.isMonkey) {
            return 194;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
            return 408;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1415;
        } else if (effectSkill != null && effectSkill.isBiNgo) {
            return 762;
        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
            return 1212;
        } else {
            return this.leg_;
        }
    }
    
    @Override
    public short getFlagBag() {
        return this.flag_;
    }
        
    @Override
    public void update() {
        super.update();
        this.increasePoint();
        switch (this.type){
            case 0:
               this.shop.update();
               break;
            case 1:
               this.shop2.update();
               break;
            case 2:
               this.shop3.update();
               break;
            case 3:
               this.shop4.update();
               break;
            case 4:
               this.shop5.update();
               break;
        }
        if (this.isDie()) {
            Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
    }
    
    private void increasePoint() {
        long tiemNangUse = 0;
        int point = 0;
        if (this.nPoint != null) {
            if (Util.isTrue(50, 100)) {
                point = 100;
                int pointHp = point * 20;
                tiemNangUse = point * (2 * (this.nPoint.hpg + 1000) + pointHp - 20) / 2;
                if (doUseTiemNang(tiemNangUse)) {
                    this.nPoint.hpMax += point;
                    this.nPoint.hpg += point;
                    Service.gI().point(this);
                }
            } else {
                point = 10;
                tiemNangUse = point * (2 * this.nPoint.dameg + point - 1) / 2 * 100;
                if (doUseTiemNang(tiemNangUse)) {
                    this.nPoint.dameg += point;
                    Service.gI().point(this);
                }
            }
        }
    }

   
   private boolean doUseTiemNang(long tiemNang) {
        if (this.nPoint.tiemNang < tiemNang) {
            return false;
        } else {
            this.nPoint.tiemNang -= tiemNang;
            return true;
        }
    }
}




