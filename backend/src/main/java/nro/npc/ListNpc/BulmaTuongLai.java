package nro.npc.ListNpc;


import consts.ConstNpc;
import consts.ConstTask;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.NpcService;
import nro.services.TaskService;
import nro.shop.ShopService;

public class BulmaTuongLai extends Npc {

   public BulmaTuongLai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
       super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
       if (canOpenNpc(player)) {
//            if (this.mapId == 104 || this.mapId == 5) {
//                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
//                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Háº¿ lÃ´ báº¡n nhá»", "Cá»­a hÃ ng", "ÄÃ³ng");
//                }
//            } else if (this.mapId == 102) {
           if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
               if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_29_1) {
                   this.createOtherMenu(player, ConstNpc.BASE_MENU, "CÃ¡m Æ¡n báº¡n Ä‘Ã£ Ä‘áº¿n Ä‘Ã¢y giÃºp chÃºng tÃ´i", "Ká»ƒ chuyá»‡n", "Cá»­a hÃ ng");
               } else {
                   this.createOtherMenu(player, ConstNpc.BASE_MENU, "CÃ¡m Æ¡n báº¡n Ä‘Ã£ Ä‘áº¿n Ä‘Ã¢y giÃºp chÃºng tÃ´i", "Ká»ƒ chuyá»‡n");
               }
           }
//            }
       }
   }

   @Override
   public void confirmMenu(Player player, int select) {
       if (canOpenNpc(player)) {
//            if (this.mapId == 104 || this.mapId == 5) {
//                if (player.iDMark.isBaseMenu()) {
//                    if (select == 0) {
//                        ShopService.gI().opendShop(player, "KARIN", true);
//                    }
//                }
//            } else if (this.mapId == 102) {
           if (player.iDMark.isBaseMenu()) {
               switch (select) {
                   case 0 ->
                           NpcService.gI().createTutorial(player, this.tempId, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                   case 1 -> ShopService.gI().opendShop(player, "bummatuonglai", true);
                   default -> {
                   }
               }
           }
//            }
       }
   }
}

