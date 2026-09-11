package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Fun.Input;
import nro.services.Service;
import consts.ConstDataEvent;
import consts.ConstNpc;
import event.BakeACake.NauBanh_NewYear;
import java.util.Arrays;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.Player;

public class NoiBanhToanSever extends Npc {

    public NoiBanhToanSever(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    
    private static String getSLBanhChungTet(Player player) {
        if (NauBanh_NewYear.banhChungBanhTetMaps.containsKey(player.id)) {
            int tongSLBanh = NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhChung + NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhTet;
            return String.valueOf(tongSLBanh);
        }
        return "0";
    }

    @Override
    public void openBaseMenu(Player player) {
        ConstDataEvent.slBanhTrongNoi = NauBanh_NewYear.getTotal();
        if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14 || this.mapId == 5) {
            if (ConstDataEvent.thoiGianNauBanh == -999999) {
                this.createOtherMenu(player, ConstNpc.CB_NAU_BANH, "Nồi nấu nguyên liệu toàn server\n Đang trong thời gian lấy bánh chín, và cho nguyên liệu vào để nấu tiếp"
                        + "\nThời gian chuẩn bị để nấu tiếp còn " + ((NauBanh_NewYear.timeCBNau - System.currentTimeMillis()) / 1000)
                        + "\nToan Server đang có " + ConstDataEvent.slBanhTrongNoi + " đang chuẩn bị nấu "
                        + "\nTrong đó mày có " + getSLBanhChungTet(player) + " đang chuẩn bị nấu",
                        //tab menu
                        "Nấu " + ItemService.gI().getTemplate(NauBanh_NewYear.banhchung).name,
                        "Nấu " + ItemService.gI().getTemplate(NauBanh_NewYear.banhtet).name,
                        "Hướng dẫn");

            } else if (ConstDataEvent.thoiGianNauBanh == 0) {
                this.createOtherMenu(player, ConstNpc.BANH_CHIN, "Nguyên liệu đã chín, Mày có 5 phút để lấy", "Lấy ngay", "Hướng dẫn");
            } else {
                this.createOtherMenu(player, ConstNpc.NAU_BANH, "Nồi nấu nguyên liệu toàn server\n Thời gian nấu còn " + ConstDataEvent.thoiGianNauBanh / 1000
                        + "\nSố bánh đang nấu " + ConstDataEvent.slBanhTrongNoi
                        + "\nMức nước trong nồi " + ConstDataEvent.mucNuocTrongNoi + "/" + ConstDataEvent.slBanhTrongNoi + " đang chuẩn bị nấu "
                        + "\nTrong đó có " + getSLBanhChungTet(player) + " Nguyên liệu của bạn đang nấu\n Chơm đủ nước để nồi không bị cháy và nhận đủ số Nguyên liệu đã nấu"
                        + "\nThêm " + ItemService.gI().getTemplate(NauBanh_NewYear.cuilua).name + " để tăng tốc thời gian nấu bánh",
                        //Tab menu
                        "Thêm " + ItemService.gI().getTemplate(NauBanh_NewYear.binhnuoc).name,
                        "Thêm " + ItemService.gI().getTemplate(NauBanh_NewYear.cuilua).name,
                        "Hướng dẫn");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                if (player.iDMark.getIndexMenu() == ConstNpc.CB_NAU_BANH) {
                    switch (select) {
                        case 0:
                            this.createOtherMenu(player, ConstNpc.NAU_BANH_CHUNG,
                                    "\b|4|" + ItemService.gI().getTemplate(NauBanh_NewYear.banhchung).name + ": \n"
                                    + "\n|2|10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item1).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item2).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item3).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item4).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item5).name + " \n"
                                    + "và 03 " + ItemService.gI().getTemplate(NauBanh_NewYear.binhnuoc).name + " để nấu.",
                                    "Nấu", "Đóng");
                            break;
                        case 1:
                            this.createOtherMenu(player, ConstNpc.NAU_BANH_TET,
                                    "\b|3|" + ItemService.gI().getTemplate(NauBanh_NewYear.banhtet).name + ": \n"
                                    + "\n|2|10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item1).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item2).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item3).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item4).name + ", \n"
                                    + "10 " + ItemService.gI().getTemplate(NauBanh_NewYear.item5).name + " \n"
                                    + "và 03 " + ItemService.gI().getTemplate(NauBanh_NewYear.binhnuoc).name + " để nấu.",
                                    "Nấu", "Đóng");
                            break;
                        case 2:
                            if (player.getSession() != null) {
                                this.createOtherMenu(player, 22222, Arrays.toString(ConstDataEvent.Huongdannaubanh), "Đóng");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NAU_BANH_CHUNG) {
                    if (select == 0) {
                        Input.gI().createFormNauBanhChung(player);
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NAU_BANH_TET) {
                    if (select == 0) {
                        Input.gI().createFormNauBanhTet(player);
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.BANH_CHIN) {
                    switch (select) {
                        case 0:
                            if (!NauBanh_NewYear.banhChungBanhTetMaps.containsKey(player.id)) {
                                Service.gI().sendThongBao(player, "Có đâu mà nhận");
                                return;
                            }
                            if (NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhTet == 0 && NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhChung == 0) {
                                Service.gI().sendThongBao(player, "Có đâu mà nhận");
                                return;
                            }
                            if (NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhTet != 0) {
                                Item banhTet = ItemService.gI().createNewItem((short) NauBanh_NewYear.banhtet, NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhTet);
                                InventoryService.gI().addItemBag(player, banhTet);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + banhTet.template.name);
                                player.slBanhTet = 0;
                                NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhTet = 0;
                            }
                            if (NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhChung != 0) {
                                Item banhChung = ItemService.gI().createNewItem((short) NauBanh_NewYear.banhchung, NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhChung);
                                InventoryService.gI().addItemBag(player, banhChung);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + banhChung.template.name);
                                player.slBanhChung = 0;
                                NauBanh_NewYear.banhChungBanhTetMaps.get(player.id).slBanhChung = 0;
                            }
                            break;
                        case 1:
                            if (player.getSession() != null) {
                                this.createOtherMenu(player, 22222, Arrays.toString(ConstDataEvent.Huongdannaubanh), "Đóng");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NAU_BANH) {
                    switch (select) {
                        case 0:
                            if (ConstDataEvent.mucNuocTrongNoi < ConstDataEvent.slBanhTrongNoi) {
                                Item nuocNau = InventoryService.gI().findItemBag(player, NauBanh_NewYear.binhnuoc);
                                if (nuocNau == null) {
                                    Service.gI().sendThongBao(player, "Có " + ItemService.gI().getTemplate(NauBanh_NewYear.binhnuoc).name + " đâu cu");
                                    return;
                                }
                                InventoryService.gI().subQuantityItemsBag(player, nuocNau, 1);
                                InventoryService.gI().sendItemBag(player);
                                ConstDataEvent.mucNuocTrongNoi++;
                                Service.gI().sendThongBao(player, "Thêm " + ItemService.gI().getTemplate(NauBanh_NewYear.binhnuoc).name + " vô nồi thành công");

                            } else {
                                Service.gI().sendThongBao(player, "Đủ " + ItemService.gI().getTemplate(NauBanh_NewYear.binhnuoc).name + " rồi cu");
                            }
                           
                            return;
                        case 1:
                            if (ConstDataEvent.thoiGianNauBanh <= 0) {
                                return;
                            } else {
                                Item cuiLua = InventoryService.gI().findItemBag(player, NauBanh_NewYear.cuilua);
                                if (cuiLua == null) {
                                    Service.gI().sendThongBao(player, "Có " + ItemService.gI().getTemplate(NauBanh_NewYear.cuilua).name + " đâu cu");
                                    return;
                                }
                                InventoryService.gI().subQuantityItemsBag(player, cuiLua, 1);
                                InventoryService.gI().sendItemBag(player);
                                NauBanh_NewYear.subTimeNauBanh(3000);
                                Service.gI().sendThongBao(player, "Đã sử dụng " + ItemService.gI().getTemplate(NauBanh_NewYear.cuilua).name + "\n|5| Giảm 3 giây thời gian nấu nguyên liệu");
                            }
                            return;

                        case 2:
                            if (player.getSession() != null) {
                                this.createOtherMenu(player, 22222, Arrays.toString(ConstDataEvent.Huongdannaubanh), "Đóng");
                            }
                            break;
                    }
                }
            }
        }
    }
}
