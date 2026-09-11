package nro.npc.ListNpc;



import consts.ConstNpc;
import nro.npc.Npc;
import nro.player.Player;
import Utils.Util;
//
//public class LinhCanh extends Npc {
//
//    public LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        super(mapId, status, cx, cy, tempId, avartar);
//    }
//
//    @Override
//    public void openBaseMenu(Player player) {
//        if (canOpenNpc(player)) {
//            if (player.clan == null) {
//                NpcService.gI().createTutorial(player, tempId, this.avartar,
//                        "Chá»‰ tiáº¿p cÃ¡c bang há»™i, miá»…n tiáº¿p khÃ¡ch vÃ£ng lai");
//                return;
//            }
//            if (player.clan.getMembers().size() < RedRibbonHQ.N_PLAYER_CLAN) {
//                NpcService.gI().createTutorial(player, tempId, this.avartar,
//                        "Bang há»™i pháº£i cÃ³ Ã­t nháº¥t 5 thÃ nh viÃªn má»›i cÃ³ thá»ƒ tham gia");
//                return;
//            }
//            if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
//                NpcService.gI().createTutorial(player, tempId, this.avartar,
//                        "Gia nháº­p bang há»™i trÃªn 2 ngÃ y má»›i Ä‘Æ°á»£c tham gia");
//                return;
//            }
//            if (player.clan.doanhTrai != null) {
//                createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
//                        "Bang há»™i cá»§a ngÆ°Æ¡i Ä‘ang Ä‘Ã¡nh tráº¡i Ä‘á»™c nhÃ£n\nThá»i gian cÃ²n láº¡i lÃ  "
//                        + TimeUtil.getTimeLeft(player.clan.doanhTrai.getLastTimeOpen(), RedRibbonHQ.TIME_DOANH_TRAI / 1000)
//                        + ". NgÆ°Æ¡i cÃ³ muá»‘n tham gia khÃ´ng?",
//                        "Tham gia", "KhÃ´ng", "HÆ°á»›ng\ndáº«n\nthÃªm");
//                return;
//            }
//            int nPlSameClan = 0;
//            for (Player pl : player.zone.getPlayers()) {
//                if (!pl.equals(player) && pl.clan != null
//                        && pl.clan.equals(player.clan) && pl.location.x >= 1285
//                        && pl.location.x <= 1645) {
//                    nPlSameClan++;
//                }
//            }
//            if (nPlSameClan < RedRibbonHQ.N_PLAYER_MAP) {
//                createOtherMenu(player, ConstNpc.IGNORE_MENU,
//                        "NgÆ°Æ¡i pháº£i cÃ³ Ã­t nháº¥t " + RedRibbonHQ.N_PLAYER_MAP + " Ä‘á»“ng Ä‘á»™i cÃ¹ng bang Ä‘á»©ng gáº§n má»›i cÃ³ thá»ƒ vÃ o\n"
//                        + "tuy nhiÃªn ta khuyÃªn ngÆ°Æ¡i nÃªn Ä‘i cÃ¹ng vá»›i 3-4 ngÆ°á»i Ä‘á»ƒ khá»i cháº¿t. "
//                        + "Hahaha.", "OK", "HÆ°á»›ng\ndáº«n\nthÃªm");
//                return;
//            }
//            if (player.clan.haveGoneDoanhTrai && !Util.isAfterMidnight(player.clan.lastTimeOpenDoanhTrai)) {
//                if (!Util.isAfterMidnight(player.lastTimeJoinDT)) {
//                    NpcService.gI().createTutorial(player, tempId, this.avartar,
//                            "HÃ´m nay báº¡n Ä‘Ã£ tham gia doanh tráº¡i rá»“i, háº¹n gáº·p báº¡n vÃ o ngÃ y mai");
//                    return;
//                }
//                createOtherMenu(player, ConstNpc.IGNORE_MENU,
//                        "Bang há»™i cá»§a ngÆ°Æ¡i ngÃ y hÃ´m nay Ä‘Ã£ vÃ o 1 láº§n rá»“i (thÃ nh viÃªn " + player.clan.playerOpenDoanhTrai.name + ") lÃºc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm") + "\n"
//                        + "NÃªn ngÆ°Æ¡i khÃ´ng thá»ƒ vÃ o Ä‘Æ°á»£c ná»¯a.\n"
//                        + "HÃ£y chá» Ä‘áº¿n ngÃ y mai Ä‘á»ƒ cÃ³ thá»ƒ vÃ o miá»…n phÃ­", "OK", "HÆ°á»›ng\ndáº«n\nthÃªm");
//                return;
//            }
//            createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
//                    "HÃ´m nay bang há»™i cá»§a ngÆ°Æ¡i chÆ°a vÃ o tráº¡i láº§n nÃ o. NgÆ°Æ¡i cÃ³ muá»‘n vÃ o\nkhÃ´ng?\nÄá»ƒ vÃ o, ta khuyÃªn ngÆ°Æ¡i nÃªn cÃ³ 3-4 ngÆ°á»i cÃ¹ng bang Ä‘i cÃ¹ng.",
//                    "VÃ o\n(miá»…n phÃ­)", "KhÃ´ng", "HÆ°á»›ng\ndáº«n\nthÃªm");
//        }
//    }
//
//    @Override
//    public void confirmMenu(Player player, int select) {
//        if (canOpenNpc(player)) {
//            switch (player.iDMark.getIndexMenu()) {
//                case ConstNpc.MENU_JOIN_DOANH_TRAI -> {
//                    if (select == 0) {
//                        RedRibbonHQService.gI().joinDoanhTrai(player);
//                    } else if (select == 2) {
//                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
//                    }
//                }
//                case ConstNpc.IGNORE_MENU -> {
//                    if (select == 1) {
//                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
//                    }
//                }
//            }
//        }
//    }
//}

