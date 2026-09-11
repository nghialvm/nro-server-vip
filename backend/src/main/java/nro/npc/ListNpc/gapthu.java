// import Inventory.InventoryService;
// import Services.NpcService;
// import Services.Service;
// import Utils.Util;
// import consts.ConstNpc;
// import models.Item.Item;
// import nro.npc.Npc;
// import nro.player.Player;
// import nro.shop.ShopService;

// public static Npc gapthu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        this.createOtherMenu(player, 1234, "|7|- •⊹٭Ngọc Rồng SenKai٭⊹• -\nMÁY GẮP LINH THÚ\n" + "|3|GẮP THƯỜNG : 5-10% CHỈ SỐ\nGẮP CAO CẤP : 10-20% CHỈ SỐ\nGẮP VIP : 15-25% CHỈ SỐ" + "\nGẮP X1 : GẮP THỦ CÔNG\nGẮP X10 : AUTO X10 LẦN GẮP\nGẮP X100 : AUTO X100 LẦN GẮP\n" + "|7|LƯU Ý : MỌI CHỈ SỐ ĐỀU RANDOM KHÔNG CÓ OPTION NHẤT ĐỊNH\nNẾU MUỐN NGƯNG AUTO GẤP CHỈ CẦN THOÁT GAME VÀ VÀO LẠI!",
//                                "Gắp Thường", "Gắp Cao Cấp", "Gắp VIP", "Xem Top", "Rương Đồ");
//                    }
//                }
//            }

//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        if (player.iDMark.getIndexMenu() == 1234) {
//                            switch (select) {
//                                case 0:
//                                    this.createOtherMenu(player, 12345, "|6|Gắp Thú Thường" + "\n|7|Tiến Hành Gắp",
//                                            "Gắp x1", "Gắp x10", "Gắp x100", "Rương Đồ");
//                                    break;
//                                case 1:
//                                    this.createOtherMenu(player, 12346, "|6|Gắp Thú Cao Cấp" + "\n|7|Tiến Hành Gắp",
//                                            "Gắp x1", "Gắp x10", "Gắp x100", "Rương Đồ");
//                                    break;
//                                case 2:
//                                    this.createOtherMenu(player, 12347, "|6|Gắp Thú VIP" + "\n|7|Tiến Hành Gắp",
//                                            "Gắp x1", "Gắp x10", "Gắp x100", "Rương Đồ");
//                                    break;
//                                case 3:
// //                                    Service.gI().ShowListTopNomal(player, Manager.TopGapThu);
//                                    break;
//                                case 4:
//                                    this.createOtherMenu(player, ConstNpc.RUONG_PHU,
//                                            "|1|Tình yêu như một dây đàn\n"
//                                            + "Tình vừa được thì đàn đứt dây\n"
//                                            + "Đứt dây này anh thay dây khác\n"
//                                            + "Mất em rồi anh biết thay ai?",
//                                            "Rương Phụ\n(" + (player.inventory.itemsBoxCrackBall.size()
//                                            - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
//                                            + " món)",
//                                            "Xóa Hết\nRương Phụ", "Đóng");
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 12345) {
//                            switch (select) {
//                                case 0:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1394) == null) {
//                                        this.createOtherMenu(player, 12345, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x1 lần");
//                                        int timex1 = 1;
//                                        int count = 0;
//                                        while (timex1 > 0) {
//                                            timex1--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1394), 1);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1394) == null) {
//                                                this.createOtherMenu(player, 12345, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12345, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {2019, 2020, 2021};
//                                            Item gapx1 = Util.petrandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx1);
//                                                    this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx1.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx1);
//                                                    this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx1.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 1:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1394) == null) {
//                                        this.createOtherMenu(player, 12345, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x10 lần");
//                                        int timex10 = 10;
//                                        int count = 0;
//                                        while (timex10 > 0) {
//                                            timex10--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1394), 1);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1394) == null) {
//                                                this.createOtherMenu(player, 12345, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12345, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {2019, 2020, 2021};
//                                            Item gapx10 = Util.petrandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx10);
//                                                    this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx10.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx10);
//                                                    this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx10.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 2:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1394) == null) {
//                                        this.createOtherMenu(player, 12345, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x100 lần");
//                                        int timex100 = 100;
//                                        int count = 0;
//                                        while (timex100 > 0) {
//                                            timex100--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1394), 1);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1394) == null) {
//                                                this.createOtherMenu(player, 12345, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12345, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {2019, 2020, 2021};
//                                            Item gapx100 = Util.petrandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx100);
//                                                    this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx100.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx100);
//                                                    this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx100.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1394).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 3:
//                                    this.createOtherMenu(player, ConstNpc.RUONG_PHU,
//                                            "|1|Tình yêu như một dây đàn\n"
//                                            + "Tình vừa được thì đàn đứt dây\n"
//                                            + "Đứt dây này anh thay dây khác\n"
//                                            + "Mất em rồi anh biết thay ai?",
//                                            "Rương Phụ\n(" + (player.inventory.itemsBoxCrackBall.size()
//                                            - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
//                                            + " món)",
//                                            "Xóa Hết\nRương Phụ", "Đóng");
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 12346) {
//                            switch (select) {
//                                case 0:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1395) == null) {
//                                        this.createOtherMenu(player, 12346, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x1 lần");
//                                        int timex1 = 1;
//                                        int count = 0;
//                                        while (timex1 > 0) {
//                                            timex1--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1395), 2);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1395) == null) {
//                                                this.createOtherMenu(player, 12346, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12346, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {2022, 2023, 2024};
//                                            Item gapx1 = Util.petccrandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx1);
//                                                    this.createOtherMenu(player, 12346, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx1.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12346, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx1);
//                                                    this.createOtherMenu(player, 12346, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx1.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12346, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 1:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1395) == null) {
//                                        this.createOtherMenu(player, 12346, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x10 lần");
//                                        int timex10 = 10;
//                                        int count = 0;
//                                        while (timex10 > 0) {
//                                            timex10--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1395), 2);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1395) == null) {
//                                                this.createOtherMenu(player, 12346, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12346, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {2022, 2023, 2024};
//                                            Item gapx10 = Util.petccrandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx10);
//                                                    this.createOtherMenu(player, 12346, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx10.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12346, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx10);
//                                                    this.createOtherMenu(player, 12346, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx10.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12346, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 2:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1395) == null) {
//                                        this.createOtherMenu(player, 12346, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x100 lần");
//                                        int timex100 = 100;
//                                        int count = 0;
//                                        while (timex100 > 0) {
//                                            timex100--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1395), 2);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1395) == null) {
//                                                this.createOtherMenu(player, 12346, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12346, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {2022, 2023, 2024};
//                                            Item gapx100 = Util.petccrandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx100);
//                                                    this.createOtherMenu(player, 12346, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx100.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12346, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx100);
//                                                    this.createOtherMenu(player, 12346, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx100.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12346, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1395).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 3:
//                                    this.createOtherMenu(player, ConstNpc.RUONG_PHU,
//                                            "|1|Tình yêu như một dây đàn\n"
//                                            + "Tình vừa được thì đàn đứt dây\n"
//                                            + "Đứt dây này anh thay dây khác\n"
//                                            + "Mất em rồi anh biết thay ai?",
//                                            "Rương Phụ\n(" + (player.inventory.itemsBoxCrackBall.size()
//                                            - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
//                                            + " món)",
//                                            "Xóa Hết\nRương Phụ", "Đóng");
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 12347) {
//                            switch (select) {
//                                case 0:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1396) == null) {
//                                        this.createOtherMenu(player, 12347, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x1 lần");
//                                        int timex1 = 1;
//                                        int count = 0;
//                                        while (timex1 > 0) {
//                                            timex1--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1396), 3);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1396) == null) {
//                                                this.createOtherMenu(player, 12347, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12347, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {1397, 1398, 1399, 1400, 1401, 1402, 1377};
//                                            Item gapx1 = Util.petviprandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx1);
//                                                    this.createOtherMenu(player, 12347, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx1.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12347, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx1);
//                                                    this.createOtherMenu(player, 12347, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx1.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12347, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex1 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 1:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1396) == null) {
//                                        this.createOtherMenu(player, 12347, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x10 lần");
//                                        int timex10 = 10;
//                                        int count = 0;
//                                        while (timex10 > 0) {
//                                            timex10--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1396), 3);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1396) == null) {
//                                                this.createOtherMenu(player, 12347, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12347, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {1397, 1398, 1399, 1400, 1401, 1402, 1377};
//                                            Item gapx10 = Util.petviprandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx10);
//                                                    this.createOtherMenu(player, 12347, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx10.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12347, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx10);
//                                                    this.createOtherMenu(player, 12347, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx10.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12347, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 2:
//                                    if (InventoryService.gI().findItem(player.inventory.itemsBag, 1396) == null) {
//                                        this.createOtherMenu(player, 12347, "|7|HẾT XU!",
//                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                        break;
//                                    }
//                                    try {
//                                        Service.gI().sendThongBao(player, "Tiến hành auto gắp x100 lần");
//                                        int timex100 = 100;
//                                        int count = 0;
//                                        while (timex100 > 0) {
//                                            timex100--;
//                                            count++;
//                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 1396), 3);
//                                            InventoryService.gI().sendItemBag(player);
//                                            Thread.sleep(100);
//                                            if (InventoryService.gI().findItem(player.inventory.itemsBag, 1396) == null) {
//                                                this.createOtherMenu(player, 12347, "|7|HẾT XU!\nSỐ LƯỢT ĐÃ GẮP : " + count,
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            if (1 + player.inventory.itemsBoxCrackBall.size() > 200) {
//                                                this.createOtherMenu(player, 12347, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + count + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
//                                                        "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                break;
//                                            }
//                                            player.point_gapthu += 1;
//                                            short[] bkt = {1397, 1398, 1399, 1400, 1401, 1402, 1377};
//                                            Item gapx100 = Util.petviprandom(bkt[Util.nextInt(bkt.length)]);
//                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    InventoryService.gI().addItemBag(player, gapx100);
//                                                    this.createOtherMenu(player, 12347, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx100.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12347, "|7|ĐANG TIẾN HÀNH GẮP AUTO X1\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
//                                                if (Util.isTrue(10, 100)) {
//                                                    player.inventory.itemsBoxCrackBall.add(gapx100);
//                                                    this.createOtherMenu(player, 12347, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + gapx100.template.name + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                } else {
//                                                    this.createOtherMenu(player, 12347, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu còn : " + InventoryService.gI().findItem(player.inventory.itemsBag, 1396).quantity + "\n|7|TỔNG ĐIỂM : " + player.point_gapthu,
//                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                    }
//                                    break;
//                                case 3:
//                                    this.createOtherMenu(player, ConstNpc.RUONG_PHU,
//                                            "|1|Tình yêu như một dây đàn\n"
//                                            + "Tình vừa được thì đàn đứt dây\n"
//                                            + "Đứt dây này anh thay dây khác\n"
//                                            + "Mất em rồi anh biết thay ai?",
//                                            "Rương Phụ\n(" + (player.inventory.itemsBoxCrackBall.size()
//                                            - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
//                                            + " món)",
//                                            "Xóa Hết\nRương Phụ", "Đóng");
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == ConstNpc.RUONG_PHU) {
//                            switch (select) {
//                                case 0:
//                                    ShopService.gI().opendShop(player, "RUONG_PHU", true);
//                                    break;
//                                case 1:
//                                    NpcService.gI().createMenuConMeo(player,
//                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
//                                            "|3|Bạn chắc muốn xóa hết vật phẩm trong rương phụ?\n"
//                                            + "|7|Sau khi xóa sẽ không thể khôi phục!",
//                                            "Đồng ý", "Hủy bỏ");
//                                    break;
//                            }
//                        }
//                    }
//                }
//            }
//        };
//    }



