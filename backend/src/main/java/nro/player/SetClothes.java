package nro.player;

import models.Item.Item;
import models.Item.ItemOption;

public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }
    //traidat
    public byte songoku;
    public byte thienXinHang;
    public byte kirin;
    public byte thanVuTruKaio;
    public byte yamcha;
    //namec
    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;
    public byte nail;
    public byte slug;
    //xayda
    public byte kakarot;
    public byte cadic;
    public byte nappa;
    public byte cadicM;
    public byte broly;
    //all gender
    public byte gohan;
    public byte goten;
    public byte frieza;
    public byte cumber;
    public byte thanhuydietchampa;
    
    public byte worldcup;
    public byte setDHD;

    public boolean godClothes;
    public int ctHaiTac = -1;
    public int ctBunmaTocMau = -1;
    public int ctTiecbaiBien = -1;
    
    public int ctNezuko = -1;
    public int ctTanjiro = -1;
    public int ctInoHashi = -1;
    public int ctInosuke = -1;
    public int ctZenitsu = -1;
    
    public int ctFideDaiCa = -1;
    public int ctDanEmFide = -1;

    public void setup() {
        setDefault();
        setupSKT();
        setupSKHNew();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;
                case 1087:
                    this.ctTanjiro = ct.template.id;
                    break;
                case 1088:
                    this.ctInoHashi = ct.template.id;
                    break;
                case 1089:
                    this.ctInosuke = ct.template.id;
                    break;
                case 1090:
                    this.ctZenitsu = ct.template.id;
                    break;
                case 1091:
                    this.ctNezuko = ct.template.id;
                    break;
                case 1208:
                case 1209:
                case 1210:
                    this.ctBunmaTocMau = ct.template.id;
                    break;
                case 405:
                case 406:
                case 407:
                case 629:
                    this.ctFideDaiCa = ct.template.id;
                    break;
                case 429:
                case 430:
                case 431:
                case 432:
                case 433:
                    this.ctDanEmFide = ct.template.id;
                    break;
                case 1234:
                case 1235:
                case 1236:
                    this.ctTiecbaiBien = ct.template.id;
                    break;

            }
        }

    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        //traidat
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 250:
                        case 253:
                            isActSet = true;
                            yamcha++;
                            break;
                        //namec
                        case 131:
//                      case 143:
                        case 254:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 130:
                        case 143:
                            isActSet = true;
                            picolo++;
                            break;
                        case 251:
//                        case 254:
                        case 142:
                            isActSet = true;
                            slug++;
                            break;
                        //xayda
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 252:
                        case 255:
                            isActSet = true;
                            broly++;
                            break;
                        //new
                        case 233:
                        case 234:
                            isActSet = true;
                            gohan++;
                            break;
                        case 263:
                        case 264:
                            isActSet = true;
                            goten++;
                            break;
                        case 265:
                        case 266:
                            isActSet = true;
                            frieza++;
                            break;
                        case 267:
                        case 268:
                            isActSet = true;
                            cumber++;
                            break;
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setupSKHNew() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 245:
                        case 246:
                        case 247:
                        case 248:
                            isActSet = true;
                            thanVuTruKaio++;
                            break;
                        case 237:
                        case 238:
                        case 239:
                        case 240:
                            isActSet = true;
                            nail++;
                            break;
                        case 241:
                        case 242:
                        case 243:
                        case 244:
                            isActSet = true;
                            cadicM++;
                            break;
                        case 269:
                        case 270:
                        case 271:
                        case 272:
                            isActSet = true;
                            thanhuydietchampa++;
                            break;
                    }
                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        //traidat
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.yamcha = 0;
        this.thanVuTruKaio = 0;
        //namec
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.slug = 0;
        this.nail = 0;
        //xayda
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.broly = 0;
        this.cadicM = 0;
        //all gender
        this.gohan = 0;
        this.goten = 0;
        this.frieza = 0;
        this.cumber = 0;
        this.thanhuydietchampa = 0;
        //else
        this.setDHD = 0;
        this.worldcup = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
        this.ctInoHashi = -1;
        this.ctInosuke = -1;
        this.ctNezuko = -1;
        this.ctTanjiro = -1;
        this.ctZenitsu = -1;
        this.ctBunmaTocMau = -1;
        this.ctFideDaiCa = -1;
        this.ctDanEmFide = -1;
        this.ctTiecbaiBien = -1;
    }

    public boolean checkSetGod() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id < 555 || item.template.id > 567) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean checkSetDes() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id < 650 || item.template.id > 662) {

                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void dispose() {
        this.player = null;
    }
}
