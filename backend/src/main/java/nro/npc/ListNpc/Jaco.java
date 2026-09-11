package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.Player;

public class Jaco extends Npc {

    public Jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_25 == 0) {
                    String[] chucTetMessages = {
                        "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                        "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                        "Phát tài phát lộc",
                        "Vạn sự như ý",
                        "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                        "Năm mới phát tài phát lộc, vạn sự như ý nha",
                        "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                        "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                        "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                        "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                        "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                        "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                        "Tết đến cười thật nhiều, sống thật chill, vui hết mình",
                        "Chúc mừng năm mới"
                    };
                    
                    String message = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                    createOtherMenu(player, ConstNpc.NHAN_LI_XI, message, "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                    return;
                }
            }
            switch (this.mapId) {
                case 24:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Gô Tên, Calích và Monaka đang gặp chuyện ở hành tinh\nPotaufeu\nHãy đến đó ngay", "Đến\nPotaufeu", "Từ chối");
                    break;
                case 139:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây.\nCậu muốn đi đâu?", "Đến\nTrái Đất", "Đến\nNamếc", "Đến\nXayda", "Từ chối");
                    break;
                default:
                break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI) {
                    switch (select) {
                        case 1:
                            Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                            String[] chucTetMessages = {
                                "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                                "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                                "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                                "Năm mới phát tài phát lộc, vạn sự như ý nha",
                                "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                                "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                                "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                                "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                                "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                                "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                                "Tết đến cười thật nhiều, sống thật chill, vui hết mình"
                            };

                            String NpcChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            String PlayerChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            this.npcChat(player, NpcChat);
                            Service.gI().chat(player, PlayerChat);
                            player.NhanLiXiForNPC_25++;
                            if (Util.isTrue(60, 100)) {
                                Lixi.addOptionParam(30, 0);
                                Lixi.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, Lixi);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + Lixi.template.name);
                            } else {
                                Service.gI().sendThongBao(player, "(>_<)");
                            }
                            break;
                    }
                    return;
                }
            }
            if (player.iDMark.isBaseMenu()) {
                switch (this.mapId) {
                    case 24:
                        if (player.nPoint.power < 200_000_000L) {
                            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 200 triệu!");
                            return;
                        }
                        if (select == 0) {
                            ChangeMapService.gI().goToPotaufeu(player);
                        }
                        break;
                    case 139:
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                        }
                        break;
                }
            }
        }
    }
}
