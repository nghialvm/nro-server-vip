package models.Reward;

import nro.inventory.InventoryService;
import models.Item.Item;
import models.Item.ItemService;
import nro.map.ItemMap;
import nro.mob.Mob;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.Item.ItemOption;


public class RewardService {
    
    //id option set kich hoat (tên set, hiệu ứng set, tỉ lệ, type tỉ lệ)
    private static final int[][][] ACTIVATION_SET = {
        {{129, 141, 1, 1000}, {127, 140, 1, 1000}, {128, 139, 1, 1000}}, //songoku - thien xin hang - kirin
        {{131, 143, 1, 1000}, {132, 144, 1, 1000}, {130, 142, 1, 1000}}, //oc tieu - pikkoro daimao - picolo
        {{135, 138, 1, 1000}, {133, 136, 1, 1000}, {134, 137, 1, 1000}} //kakarot - cadic - nappa
    };
    
    private static RewardService I;

    private RewardService() {

    }

    public static RewardService gI() {
        if (RewardService.I == null) {
            RewardService.I = new RewardService();
        }
        return RewardService.I;
    }    
    
    //trả về list item quái die
    public List<ItemMap> getRewardItems(Player player, Mob mob, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        return list;
    }
     public void initChiSoItem(Item item) {
        initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
    }
    

    //chỉ số cơ bản: hp, ki, hồi phục, sđ, crit
    public void initBaseOptionClothes(int tempId, int type, List<ItemOption> list) {
        int[][] option_param = {{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}};
        switch (type) {
            case 0: //áo
                option_param[0][0] = 47; //giáp
                switch (tempId) {
                    case 0:
                        option_param[0][1] = 2;
                        break;
                    case 33:
                        option_param[0][1] = 4;
                        break;
                    case 3:
                        option_param[0][1] = 8;
                        break;
                    case 34:
                        option_param[0][1] = 16;
                        break;
                    case 136:
                        option_param[0][1] = 24;
                        break;
                    case 137:
                        option_param[0][1] = 40;
                        break;
                    case 138:
                        option_param[0][1] = 60;
                        break;
                    case 139:
                        option_param[0][1] = 90;
                        break;
                    case 230:
                        option_param[0][1] = 200;
                        break;
                    case 231:
                        option_param[0][1] = 250;
                        break;
                    case 232:
                        option_param[0][1] = 300;
                        break;
                    case 233:
                        option_param[0][1] = 400;
                        break;
                    case 1:
                        option_param[0][1] = 2;
                        break;
                    case 41:
                        option_param[0][1] = 4;
                        break;
                    case 4:
                        option_param[0][1] = 8;
                        break;
                    case 42:
                        option_param[0][1] = 16;
                        break;
                    case 152:
                        option_param[0][1] = 24;
                        break;
                    case 153:
                        option_param[0][1] = 40;
                        break;
                    case 154:
                        option_param[0][1] = 60;
                        break;
                    case 155:
                        option_param[0][1] = 90;
                        break;
                    case 234:
                        option_param[0][1] = 200;
                        break;
                    case 235:
                        option_param[0][1] = 250;
                        break;
                    case 236:
                        option_param[0][1] = 300;
                        break;
                    case 237:
                        option_param[0][1] = 400;
                        break;
                    case 2:
                        option_param[0][1] = 3;
                        break;
                    case 49:
                        option_param[0][1] = 5;
                        break;
                    case 5:
                        option_param[0][1] = 10;
                        break;
                    case 50:
                        option_param[0][1] = 20;
                        break;
                    case 168:
                        option_param[0][1] = 30;
                        break;
                    case 169:
                        option_param[0][1] = 50;
                        break;
                    case 170:
                        option_param[0][1] = 70;
                        break;
                    case 171:
                        option_param[0][1] = 100;
                        break;
                    case 238:
                        option_param[0][1] = 230;
                        break;
                    case 239:
                        option_param[0][1] = 280;
                        break;
                    case 240:
                        option_param[0][1] = 330;
                        break;
                    case 241:
                        option_param[0][1] = 450;
                        break;
                    case 555: //áo thần trái đất
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 800;
                        option_param[2][1] = 15;
                        break;
                    case 557: //áo thần namếc
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 800;
                        option_param[2][1] = 15;
                        break;
                    case 559: //áo thần xayda
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 800;
                        option_param[2][1] = 15;
                        break;
                       
                }
                break;
            case 1: //quần
                option_param[0][0] = 6; //hp
                option_param[1][0] = 27; //hp hồi/30s
                switch (tempId) {
                    case 6:
                        option_param[0][1] = 30;
                        break;
                    case 35:
                        option_param[0][1] = 150;
                        option_param[1][1] = 12;
                        break;
                    case 9:
                        option_param[0][1] = 300;
                        option_param[1][1] = 40;
                        break;
                    case 36:
                        option_param[0][1] = 600;
                        option_param[1][1] = 120;
                        break;
                    case 140:
                        option_param[0][1] = 1400;
                        option_param[1][1] = 280;
                        break;
                    case 141:
                        option_param[0][1] = 3000;
                        option_param[1][1] = 600;
                        break;
                    case 142:
                        option_param[0][1] = 6000;
                        option_param[1][1] = 1200;
                        break;
                    case 143:
                        option_param[0][1] = 10000;
                        option_param[1][1] = 2000;
                        break;
                    case 242:
                        option_param[0][1] = 14000;
                        option_param[1][1] = 2500;
                        break;
                    case 243:
                        option_param[0][1] = 18000;
                        option_param[1][1] = 3000;
                        break;
                    case 244:
                        option_param[0][1] = 22000;
                        option_param[1][1] = 3500;
                        break;
                    case 245:
                        option_param[0][1] = 26000;
                        option_param[1][1] = 4000;
                        break;
                    case 7:
                        option_param[0][1] = 20;
                        break;
                    case 43:
                        option_param[0][1] = 25;
                        option_param[1][1] = 10;
                        break;
                    case 10:
                        option_param[0][1] = 120;
                        option_param[1][1] = 28;
                        break;
                    case 44:
                        option_param[0][1] = 250;
                        option_param[1][1] = 100;
                        break;
                    case 156:
                        option_param[0][1] = 600;
                        option_param[1][1] = 240;
                        break;
                    case 157:
                        option_param[0][1] = 1200;
                        option_param[1][1] = 480;
                        break;
                    case 158:
                        option_param[0][1] = 2400;
                        option_param[1][1] = 960;
                        break;
                    case 159:
                        option_param[0][1] = 4800;
                        option_param[1][1] = 1800;
                        break;
                    case 246:
                        option_param[0][1] = 13000;
                        option_param[1][1] = 2200;
                        break;
                    case 247:
                        option_param[0][1] = 17000;
                        option_param[1][1] = 2700;
                        break;
                    case 248:
                        option_param[0][1] = 21000;
                        option_param[1][1] = 3200;
                        break;
                    case 249:
                        option_param[0][1] = 25000;
                        option_param[1][1] = 3700;
                        break;
                    case 8:
                        option_param[0][1] = 20;
                        break;
                    case 51:
                        option_param[0][1] = 20;
                        option_param[1][1] = 8;
                        break;
                    case 11:
                        option_param[0][1] = 100;
                        option_param[1][1] = 20;
                        break;
                    case 52:
                        option_param[0][1] = 200;
                        option_param[1][1] = 80;
                        break;
                    case 172:
                        option_param[0][1] = 500;
                        option_param[1][1] = 200;
                        break;
                    case 173:
                        option_param[0][1] = 1000;
                        option_param[1][1] = 400;
                        break;
                    case 174:
                        option_param[0][1] = 2000;
                        option_param[1][1] = 800;
                        break;
                    case 175:
                        option_param[0][1] = 4000;
                        option_param[1][1] = 1600;
                        break;
                    case 250:
                        option_param[0][1] = 12000;
                        option_param[1][1] = 2100;
                        break;
                    case 251:
                        option_param[0][1] = 16000;
                        option_param[1][1] = 2600;
                        break;
                    case 252:
                        option_param[0][1] = 20000;
                        option_param[1][1] = 3100;
                        break;
                    case 253:
                        option_param[0][1] = 24000;
                        option_param[1][1] = 3600;
                        break;
                    case 556: //quần thần trái đất
                        option_param[0][0] = 22; //hp
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 52;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 15;
                        break;
                    case 558: //quần thần namếc
                        option_param[0][0] = 22; //hp
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 50;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 15;
                        break;
                    case 560: //quần thần xayda
                        option_param[0][0] = 22; //hp
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 48;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 15;
                        break;
                }
                break;
            case 2: //găng
                option_param[0][0] = 0; //sđ
                switch (tempId) {
                    case 21:
                        option_param[0][1] = 4;
                        break;
                    case 24:
                        option_param[0][1] = 7;
                        break;
                    case 37:
                        option_param[0][1] = 14;
                        break;
                    case 38:
                        option_param[0][1] = 28;
                        break;
                    case 144:
                        option_param[0][1] = 55;
                        break;
                    case 145:
                        option_param[0][1] = 110;
                        break;
                    case 146:
                        option_param[0][1] = 220;
                        break;
                    case 147:
                        option_param[0][1] = 530;
                        break;
                    case 254:
                        option_param[0][1] = 680;
                        break;
                    case 255:
                        option_param[0][1] = 1000;
                        break;
                    case 256:
                        option_param[0][1] = 1500;
                        break;
                    case 257:
                        option_param[0][1] = 2200;
                        break;
                    case 22:
                        option_param[0][1] = 3;
                        break;
                    case 46:
                        option_param[0][1] = 6;
                        break;
                    case 25:
                        option_param[0][1] = 12;
                        break;
                    case 45:
                        option_param[0][1] = 24;
                        break;
                    case 160:
                        option_param[0][1] = 50;
                        break;
                    case 161:
                        option_param[0][1] = 100;
                        break;
                    case 162:
                        option_param[0][1] = 200;
                        break;
                    case 163:
                        option_param[0][1] = 500;
                        break;
                    case 258:
                        option_param[0][1] = 630;
                        break;
                    case 259:
                        option_param[0][1] = 950;
                        break;
                    case 260:
                        option_param[0][1] = 1450;
                        break;
                    case 261:
                        option_param[0][1] = 2150;
                        break;
                    case 23:
                        option_param[0][1] = 5;
                        break;
                    case 53:
                        option_param[0][1] = 8;
                        break;
                    case 26:
                        option_param[0][1] = 16;
                        break;
                    case 54:
                        option_param[0][1] = 32;
                        break;
                    case 176:
                        option_param[0][1] = 60;
                        break;
                    case 177:
                        option_param[0][1] = 120;
                        break;
                    case 178:
                        option_param[0][1] = 240;
                        break;
                    case 179:
                        option_param[0][1] = 560;
                        break;
                    case 262:
                        option_param[0][1] = 700;
                        break;
                    case 263:
                        option_param[0][1] = 1050;
                        break;
                    case 264:
                        option_param[0][1] = 1550;
                        break;
                    case 265:
                        option_param[0][1] = 2250;
                        break;
                    case 562: //găng thần trái đất
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 3700;
                        option_param[2][1] = 17;
                        break;
                    case 564: //găng thần namếc
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 3500;
                        option_param[2][1] = 17;
                        break;
                    case 566: //găng thần xayda
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 3800;
                        option_param[2][1] = 17;
                        break;
                }
                break;
            case 3: //giày
                option_param[0][0] = 7; //ki
                option_param[1][0] = 28; //ki hồi /30s
                switch (tempId) {
                    case 27:
                        option_param[0][1] = 10;
                        break;
                    case 30:
                        option_param[0][1] = 25;
                        option_param[1][1] = 5;
                        break;
                    case 39:
                        option_param[0][1] = 120;
                        option_param[1][1] = 24;
                        break;
                    case 40:
                        option_param[0][1] = 250;
                        option_param[1][1] = 50;
                        break;
                    case 148:
                        option_param[0][1] = 500;
                        option_param[1][1] = 100;
                        break;
                    case 149:
                        option_param[0][1] = 1200;
                        option_param[1][1] = 240;
                        break;
                    case 150:
                        option_param[0][1] = 2400;
                        option_param[1][1] = 480;
                        break;
                    case 151:
                        option_param[0][1] = 5000;
                        option_param[1][1] = 1000;
                        break;
                    case 266:
                        option_param[0][1] = 9000;
                        option_param[1][1] = 1500;
                        break;
                    case 267:
                        option_param[0][1] = 14000;
                        option_param[1][1] = 2000;
                        break;
                    case 268:
                        option_param[0][1] = 19000;
                        option_param[1][1] = 2500;
                        break;
                    case 269:
                        option_param[0][1] = 24000;
                        option_param[1][1] = 3000;
                        break;
                    case 28:
                        option_param[0][1] = 15;
                        break;
                    case 47:
                        option_param[0][1] = 30;
                        option_param[1][1] = 6;
                        break;
                    case 31:
                        option_param[0][1] = 150;
                        option_param[1][1] = 30;
                        break;
                    case 48:
                        option_param[0][1] = 300;
                        option_param[1][1] = 60;
                        break;
                    case 164:
                        option_param[0][1] = 600;
                        option_param[1][1] = 120;
                        break;
                    case 165:
                        option_param[0][1] = 1500;
                        option_param[1][1] = 300;
                        break;
                    case 166:
                        option_param[0][1] = 3000;
                        option_param[1][1] = 600;
                        break;
                    case 167:
                        option_param[0][1] = 6000;
                        option_param[1][1] = 1200;
                        break;
                    case 270:
                        option_param[0][1] = 10000;
                        option_param[1][1] = 1700;
                        break;
                    case 271:
                        option_param[0][1] = 15000;
                        option_param[1][1] = 2200;
                        break;
                    case 272:
                        option_param[0][1] = 20000;
                        option_param[1][1] = 2700;
                        break;
                    case 273:
                        option_param[0][1] = 25000;
                        option_param[1][1] = 3200;
                        break;
                    case 29:
                        option_param[0][1] = 10;
                        break;
                    case 55:
                        option_param[0][1] = 20;
                        option_param[1][1] = 4;
                        break;
                    case 32:
                        option_param[0][1] = 100;
                        option_param[1][1] = 20;
                        break;
                    case 56:
                        option_param[0][1] = 200;
                        option_param[1][1] = 40;
                        break;
                    case 180:
                        option_param[0][1] = 400;
                        option_param[1][1] = 80;
                        break;
                    case 181:
                        option_param[0][1] = 1000;
                        option_param[1][1] = 200;
                        break;
                    case 182:
                        option_param[0][1] = 2000;
                        option_param[1][1] = 400;
                        break;
                    case 183:
                        option_param[0][1] = 4000;
                        option_param[1][1] = 800;
                        break;
                    case 274:
                        option_param[0][1] = 8000;
                        option_param[1][1] = 1300;
                        break;
                    case 275:
                        option_param[0][1] = 13000;
                        option_param[1][1] = 1800;
                        break;
                    case 276:
                        option_param[0][1] = 18000;
                        option_param[1][1] = 2300;
                        break;
                    case 277:
                        option_param[0][1] = 23000;
                        option_param[1][1] = 2800;
                        break;
                    case 563: //giày thần trái đất
                        option_param[0][0] = 23;
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 48;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 14;
                        break;
                    case 565: //giày thần namếc
                        option_param[0][0] = 23;
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 48;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 14;
                        break;
                    case 567: //giày thần xayda
                        option_param[0][0] = 23;
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 46;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 14;
                        break;
                }
                break;
            case 4: //rada
                option_param[0][0] = 14; //crit
                switch (tempId) {
                    case 12:
                        option_param[0][1] = 1;
                        break;
                    case 57:
                        option_param[0][1] = 2;
                        break;
                    case 58:
                        option_param[0][1] = 3;
                        break;
                    case 59:
                        option_param[0][1] = 4;
                        break;
                    case 184:
                        option_param[0][1] = 5;
                        break;
                    case 185:
                        option_param[0][1] = 6;
                        break;
                    case 186:
                        option_param[0][1] = 7;
                        break;
                    case 187:
                        option_param[0][1] = 8;
                        break;
                    case 278:
                        option_param[0][1] = 9;
                        break;
                    case 279:
                        option_param[0][1] = 10;
                        break;
                    case 280:
                        option_param[0][1] = 11;
                        break;
                    case 281:
                        option_param[0][1] = 12;
                        break;
                    case 561: //nhẫn thần linh
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 15;
                        option_param[2][1] = 18;
                        break;
                }
                break;
        }

        for (int i = 0; i < option_param.length; i++) {
            if (option_param[i][0] != -1 && option_param[i][1] != -1) {
                list.add(new ItemOption(option_param[i][0],
                        (option_param[i][1] + Util.nextInt(-(option_param[i][1] * 10 / 100),
                                option_param[i][1] * 10 / 100))));
            }
        }
    }

    //sao pha lê
    public void initStarOption(ItemMap item, RatioStar[] ratioStars) {
        RatioStar ratioStar = ratioStars[Util.nextInt(0, ratioStars.length - 1)];
        if (Util.isTrue(ratioStar.ratio, ratioStar.typeRatio)) {
            item.options.add(new ItemOption(107, ratioStar.numStar));
        }
    }


    //sao pha lê
    public void initStarOption(Item item, RatioStar[] ratioStars) {
        RatioStar ratioStar = ratioStars[Util.nextInt(0, ratioStars.length - 1)];
        if (Util.isTrue(ratioStar.ratio, ratioStar.typeRatio)) {
            item.itemOptions.add(new ItemOption(107, ratioStar.numStar));
        }
    }

   public void initBaseOptionSaoPhaLe(Item itemReward) {
        int optionId = -1;
        int param = 5;
        switch (itemReward.template.id) {
            case 441: //hút máu
                optionId = 95;
                param = 5;
                break;
            case 442: //hút ki
                optionId = 96;
                param = 5;
                break;
            case 443: //phản sát thương
                optionId = 97;
                param = 5;
                break;
            case 444:
                optionId = 98;
                param = 3;
                break;
            case 445:
                optionId = 99;
                param = 3;
                break;
            case 446: //vàng
                optionId = 100;
                param = 5;
                break;
            case 447: //tnsm
                optionId = 101;
                param = 5;
                break;
            case 220:
                optionId = 71;
                param = 0;
                break;
            case 221:
                optionId = 70;
                param = 0;
                break;
            case 222:
                optionId = 69;
                param = 0;
                break;
            case 223:
                optionId = 68;
                param = 0;
                break;
            case 224:
                optionId = 67;
                param = 0;
                break;
            case 225:
                optionId = 73;
                param = 0;
                break;
        }
        if (optionId != -1) {
            itemReward.itemOptions.add(new ItemOption(optionId, param));
        } else {
            itemReward.itemOptions.add(new ItemOption(optionId, 5));
        }
    }

    //set kích hoạt
    public void initActivationOption(int gender, int type, List<ItemOption> list) {
        if (type <= 4) {
            int[] idOption = ACTIVATION_SET[gender][Util.nextInt(0, 2)];
            list.add(new ItemOption(idOption[0], 1)); //tên set
            list.add(new ItemOption(idOption[1], 1)); //hiệu ứng set
            list.add(new ItemOption(30, 7)); //không thể giao dịch
        }
    }

    public static class RatioStar {

        public byte numStar;
        public int ratio;
        public int typeRatio;

        public RatioStar(byte numStar, int ratio, int typeRatio) {
            this.numStar = numStar;
            this.ratio = ratio;
            this.typeRatio = typeRatio;
        }
    }
    
    public void rewardTuanLoc(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward_TuanLoc = false;
            player.haveReward_TuanLoc = true;
            short[] Param = {7, 15, 30};
            short[] List_Item = {1654, 1748, 1829, 1841, 1467, 822, 823, 388, 391, 394, 925, 926, 927};
            Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
            int Item_Template = itemReceived.template.id;
            if (Item_Template == 1654) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1748) {
                itemReceived.addOptionParam(103, Util.nextInt(15, 25));
                itemReceived.addOptionParam(81, Util.nextInt(5, 15));
                itemReceived.addOptionParam(236, Util.nextInt(20, 25));
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1829) {
                itemReceived.addOptionParam(77, Util.nextInt(15, 20));
                itemReceived.addOptionParam(80, Util.nextInt(10, 25));
                itemReceived.addOptionParam(236, Util.nextInt(20, 25));
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1841) {
                itemReceived.addOptionParam(77, 23);
                itemReceived.addOptionParam(80, Util.nextInt(10, 20));
                itemReceived.addOptionParam(94, Util.nextInt(10, 20));
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1467) {
                itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                itemReceived.addOptionParam(50, Util.nextInt(10, 20));
                itemReceived.addOptionParam(103, Util.nextInt(10, 20));
                itemReceived.addOptionParam(101, Util.nextInt(20, 50));
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 822 || Item_Template == 823) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 20));
                itemReceived.addOptionParam(77, Util.nextInt(15, 20));
                itemReceived.addOptionParam(103, Util.nextInt(15, 20));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 388 || Item_Template == 391 || Item_Template == 394) {
                itemReceived.addOptionParam(77, 30);
                itemReceived.addOptionParam(106, 0);
                itemReceived.addOptionParam(80, 50);
                itemReceived.addOptionParam(50, 20);
            }
            if (Item_Template == 925 || Item_Template == 926 || Item_Template == 927) {
                itemReceived.addOptionParam(87, 0);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(93, 35);
            }
            InventoryService.gI().addItemBag(player, itemReceived);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + itemReceived.template.name);
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }
    
    public void rewardBeNa(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward_PeNa = false;
            player.haveReward_PeNa = true;
            int Quantity = Util.nextInt(1, 5);
            Item item = ItemService.gI().createNewItem((short) 758);
            item.itemOptions.add(new ItemOption(86, 0));
            item.itemOptions.add(new ItemOption(93, 30));
            item.quantity = Quantity;
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + Quantity + " " + item.template.name);
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }    
    
    public void rewardPiLong(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward_PiLong = false;
            player.haveReward_PiLong = true;
            int Quantity = Util.nextInt(1, 5);
            Item item = ItemService.gI().createNewItem((short) 1187);
            item.itemOptions.add(new ItemOption(86, 0));
            item.itemOptions.add(new ItemOption(93, 30));
            item.quantity = Quantity;
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + Quantity + " " + item.template.name);
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }
    
    public void rewardMeoDen(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward_MeoDen = false;
            player.haveReward_MeoDen = true;
            int Quantity = Util.nextInt(1, 3);
            Item item = ItemService.gI().createNewItem((short) 1187);
            item.itemOptions.add(new ItemOption(86, 0));
            item.itemOptions.add(new ItemOption(93, 30));
            item.quantity = Quantity;
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + Quantity + " " + item.template.name);
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }
    
    public void rewardLancon(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward = false;
            player.haveReward = true;
            int Quantity = Util.nextInt(1, 3);
            Item item = ItemService.gI().createNewItem((short) 758);
            item.itemOptions.add(new ItemOption(86, 0));
            item.itemOptions.add(new ItemOption(93, 30));
            item.quantity = Quantity;
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + Quantity + " " + item.template.name);
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }
    
    public void rewardFirstTimeLoginPerDay(Player player) {
        if (Util.isAfterDay(Date.from(Instant.now()), player.firstTimeLogin)) {
            Item item = ItemService.gI().createNewItem((short) 649);
            item.quantity = 1;
            item.itemOptions.add(new ItemOption(74, 0));
            item.itemOptions.add(new ItemOption(30, 0));
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBaoFromAdmin(player, "Quà đăng nhập hàng ngày: \nBạn nhận được " + item.template.name + " số lượng : " + item.quantity);
            player.firstTimeLogin = Date.from(Instant.now());
        }
    }
}
