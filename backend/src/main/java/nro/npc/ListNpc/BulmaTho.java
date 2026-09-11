package nro.npc.ListNpc;

import consts.ConstNpc;
import Utils.Util;
import models.Item.Item;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.Service;
import nro.shop.ShopService;

public class BulmaTho extends Npc {

    public BulmaTho(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {

            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "|0|Em muá»‘n Ä‘Æ°á»£c táº·ng hoa, anh nÃ o táº·ng hoa cho em Ä‘i\n|4|"
                    + "[Hoa há»“ng má»c dáº¡i quanh cÃ¡c lÃ ng, Ä‘áº£o, vÃ¡ch nÃºi, ....]",
                    "Táº·ng hoa",
                    "Tá»‰a hoa",
                    "Phá»¥\nkiá»‡n");

        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> {
                        Item boHoaHong1 = InventoryService.gI().findItemBag(player, 1388);
                        Item boHoaHong2 = InventoryService.gI().findItemBag(player, 1395);

                        int[] idCT = {464, 452};
                        int[] idPet = {892, 893};
                        int[] idDeoLung = {1515, 1520, 1531};
                        int[] idVatPham = {457, 1440, 1229};

                        int[] money = {
                            Util.nextInt(333333, 999999),
                            Util.nextInt(50, 100),
                            Util.nextInt(200, 300)
                        };

                        if (boHoaHong1 == null && boHoaHong2 == null) {
                            Service.gI().sendThongBaoOK(
                                    player,
                                    "Anh kiáº¿m hoa táº·ng em Ä‘i <3 yÃªu yÃªu"
                            );
                            return;
                        }

                        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                            Service.gI().sendThongBaoOK(
                                    player,
                                    "Cáº§n 1 Ã´ hÃ nh trang trá»‘ng trá»Ÿ lÃªn anh yÃªu!!!"
                            );
                            return;
                        }

                        InventoryService.gI().subQuantityItemsBag(
                                player,
                                boHoaHong2 == null ? boHoaHong1 : boHoaHong2,
                                1
                        );

                        Item caiTrang = ItemService.gI().createNewItem(
                                (short) idCT[Util.nextInt(0, idCT.length - 1)]
                        );

                        Item thuCung = ItemService.gI().createNewItem(
                                (short) idPet[Util.nextInt(0, idPet.length - 1)]
                        );

                        Item deoLung = ItemService.gI().createNewItem(
                                (short) idDeoLung[Util.nextInt(0, idDeoLung.length - 1)]
                        );

                        Item vatPham = ItemService.gI().createNewItem(
                                (short) idVatPham[Util.nextInt(0, idVatPham.length - 1)]
                        );

                        if (Util.isTrue(5, 100)) {

                            caiTrang.addOptionParam(50, Util.nextInt(20, 30));
                            caiTrang.addOptionParam(77, Util.nextInt(20, 30));
                            caiTrang.addOptionParam(103, Util.nextInt(20, 30));
                            caiTrang.addOptionParam(
                                    Util.isTrue(5, 100)
                                            ? 117
                                            : Util.isTrue(5, 100) ? 14 : 5,
                                    Util.nextInt(8, 12)
                            );

                            if (Util.isTrue(99, 100)) {
                                caiTrang.addOptionParam(93, Util.nextInt(3, 7));
                            }

                            caiTrang.addOptionParam(30, 1);
                            InventoryService.gI().addItemBag(player, caiTrang);

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + caiTrang.template.name
                            );

                        } else if (Util.isTrue(25, 100)) {

                            thuCung.addOptionParam(50, Util.nextInt(8, 12));
                            thuCung.addOptionParam(77, Util.nextInt(8, 12));
                            thuCung.addOptionParam(103, Util.nextInt(8, 12));
                            thuCung.addOptionParam(
                                    Util.isTrue(5, 100)
                                            ? 117
                                            : Util.isTrue(5, 100) ? 14 : 5,
                                    Util.nextInt(3, 5)
                            );

                            if (Util.isTrue(99, 100)) {
                                thuCung.addOptionParam(93, Util.nextInt(3, 7));
                            }

                            thuCung.addOptionParam(30, 1);
                            InventoryService.gI().addItemBag(player, thuCung);

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + thuCung.template.name
                            );

                        } else if (Util.isTrue(25, 100)) {

                            deoLung.addOptionParam(50, Util.nextInt(8, 12));
                            deoLung.addOptionParam(77, Util.nextInt(8, 12));
                            deoLung.addOptionParam(103, Util.nextInt(8, 12));

                            if (Util.isTrue(99, 100)) {
                                deoLung.addOptionParam(93, Util.nextInt(3, 7));
                            }

                            deoLung.addOptionParam(30, 1);
                            InventoryService.gI().addItemBag(player, deoLung);

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + deoLung.template.name
                            );

                        } else if (Util.isTrue(25, 100)) {

                            vatPham.addOptionParam(30, 1);
                            vatPham.quantity = vatPham.template.id == 457
                                    ? Util.nextInt(1, 3)
                                    : 1;

                            InventoryService.gI().addItemBag(player, vatPham);

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + vatPham.template.name
                            );

                        } else if (Util.isTrue(80, 100)) {

                            player.inventory.gold += money[0];

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + money[0] + " vÃ ng"
                            );

                        } else if (Util.isTrue(10, 100)) {

                            player.inventory.ruby += money[1];

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + money[1] + " ngá»c há»“ng"
                            );

                        } else {

                            player.inventory.gem += money[2];

                            Service.gI().sendThongBao(
                                    player,
                                    "YÃªu anh, táº·ng anh " + money[2] + " ngá»c xanh"
                            );
                        }

                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);
                    }

                    case 1 -> {
                        int[] idBoHoaHong = {1388, 1395};

                        Item keoTiaHoa = InventoryService.gI().findItemBag(player, 1387);
                        Item hoaHong = InventoryService.gI().findItemBag(player, 1530);

                        if (keoTiaHoa == null) {
                            Service.gI().sendThongBao(
                                    player,
                                    "TÃ¬m mua cho em cÃ¡i kÃ©o Ä‘á»ƒ cáº¯t hoa Ä‘i"
                            );
                            return;
                        }

                        if (hoaHong == null) {
                            Service.gI().sendThongBao(
                                    player,
                                    "Anh lÃ m gÃ¬ cÃ³ bÃ´ng há»“ng nÃ o mÃ  Ä‘Æ°a em"
                            );
                            return;
                        }

                        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                            Service.gI().sendThongBaoOK(
                                    player,
                                    "Cáº§n 1 Ã´ hÃ nh trang trá»‘ng trá»Ÿ lÃªn anh yÃªu!!!"
                            );
                            return;
                        }

                        if (hoaHong.quantity > 10) {

                            InventoryService.gI().subQuantityItemsBag(
                                    player,
                                    keoTiaHoa,
                                    1
                            );

                            InventoryService.gI().subQuantityItemsBag(
                                    player,
                                    hoaHong,
                                    Util.nextInt(7, 10)
                            );

                            Item boHoaHong = ItemService.gI().createNewItem(
                                    (short) idBoHoaHong[
                                            Util.nextInt(0, idBoHoaHong.length - 1)
                                    ]
                            );

                            InventoryService.gI().addItemBag(player, boHoaHong);

                            Service.gI().sendThongBao(
                                    player,
                                    boHoaHong.template.name
                                            + " Ä‘áº¹p quÃ¡, anh cÃ³ thá»ƒ táº·ng em Ä‘Æ°á»£c khÃ´ng?"
                            );

                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBag(player);

                        } else {

                            Service.gI().sendThongBao(
                                    player,
                                    "Em cáº§n khoáº£ng 7 Ä‘áº¿n 10 bÃ´ng hoa há»“ng"
                            );
                        }
                    }

                    case 2 -> {
                        ShopService.gI().opendShop(
                                player,
                                "BUNMA",
                                false
                        );
                    }
                }
            }
        }
    }
}

