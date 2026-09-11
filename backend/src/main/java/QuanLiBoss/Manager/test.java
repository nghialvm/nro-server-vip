//public List<Item> getListItemLuckyRound2(Player player, int num, boolean vip) {
//        List<Item> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            Item it = ItemService.gI().createNewItem((short) 189);
//            it.quantity = Util.nextInt(5, 30) * 1000;
//            short[] Param = {3, 5, 7, 15};
//            if (vip) {
//                if (Util.isTrue(50, 100)) {
//                    if (Util.isTrue(10, 100)) {
//                        int itemid = 17;
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        it = nro;
//                    } else if (Util.isTrue(5, 100)) {
//                        int itemid = 1143;
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(30, 0));
//                        it = nro;
//                    } else if (Util.isTrue(5, 100)) {
//                        int itemid = 1173;
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(30, 0));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {1150, 1151, 1152, 1153, 1154};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(86, 0));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {1404, 1405};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {1406, 1407};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {1409, 1410, 1411};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {1412, 1413};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(5, 100)) {
//                        int itemid = 1408;
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(10, 100)) {
//                        int[] itemId = {2062};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(30, 100)) {
//                        int[] itemId = {2069};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    } else if (Util.isTrue(40, 100)) {
//                        int[] itemId = {840, 841, 842, 859, 956};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        nro.itemOptions.add(new Item.ItemOption(30, 0));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {1517, 1518};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    }
//                } else {
//                    if (EventManager.LUNNAR_NEW_YEAR) {
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {1024, 1025, 1026, 1027};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(50, 18));
//                            nro.itemOptions.add(new Item.ItemOption(77, 15));
//                            nro.itemOptions.add(new Item.ItemOption(103, 15));
//                            nro.itemOptions.add(new Item.ItemOption(94, 10));
//                            if (Util.isTrue(90, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {1476, 1483};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 25)));
//                            nro.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 25)));
//                            nro.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 25)));
//                            nro.itemOptions.add(new Item.ItemOption(94, Util.nextInt(10, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(97, Util.nextInt(10, 15)));
//                            if (Util.isTrue(50, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(80, Util.nextInt(10, 15)));
//                            } else {
//                                nro.itemOptions.add(new Item.ItemOption(81, Util.nextInt(10, 15)));
//                            }
//                            nro.itemOptions.add(new Item.ItemOption(101, Util.nextInt(20, 30)));
//                            nro.itemOptions.add(new Item.ItemOption(114, Util.nextInt(10, 20)));
//                            if (Util.isTrue(90, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                    }
//                    if (EventManager.CHRISTMAS) {
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1171);
//                            nro.itemOptions.add(new Item.ItemOption(30, 0));
//                            nro.itemOptions.add(new Item.ItemOption(93, 30));
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {1455, 1465, 1466};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(106, 0));
//                            if (Util.isTrue(99, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 745);
//                            nro.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 18)));
//                            nro.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 18)));
//                            nro.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 18)));
//                            nro.itemOptions.add(new Item.ItemOption(94, Util.nextInt(10, 18)));
//                            nro.itemOptions.add(new Item.ItemOption(97, Util.nextInt(10, 18)));
//                            nro.itemOptions.add(new Item.ItemOption(106, 0));
//                            if (Util.isTrue(199, 200)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                    }
//                    if (EventManager.VU_LAN_FESTIVAL) {
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 898);
//                            nro.itemOptions.add(new Item.ItemOption(50, 24));
//                            nro.itemOptions.add(new Item.ItemOption(14, 4));
//                            nro.itemOptions.add(new Item.ItemOption(77, 22));
//                            nro.itemOptions.add(new Item.ItemOption(103, 21));
//                            nro.itemOptions.add(new Item.ItemOption(5, 16));
//                            nro.itemOptions.add(new Item.ItemOption(80, 11));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 849);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(114, 25));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1273);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(50, 10));
//                            nro.itemOptions.add(new Item.ItemOption(77, 15));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1244);
//                            nro.itemOptions.add(new Item.ItemOption(50, 15));
//                            nro.itemOptions.add(new Item.ItemOption(103, 15));
//                            nro.itemOptions.add(new Item.ItemOption(77, 15));
//                            nro.itemOptions.add(new Item.ItemOption(14, 11));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1243);
//                            nro.itemOptions.add(new Item.ItemOption(50, 15));
//                            nro.itemOptions.add(new Item.ItemOption(77, 15));
//                            nro.itemOptions.add(new Item.ItemOption(103, 15));
//                            nro.itemOptions.add(new Item.ItemOption(94, 11));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1252);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(50, 6));
//                            nro.itemOptions.add(new Item.ItemOption(77, 8));
//                            nro.itemOptions.add(new Item.ItemOption(103, 8));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1253);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(50, 9));
//                            nro.itemOptions.add(new Item.ItemOption(77, 9));
//                            nro.itemOptions.add(new Item.ItemOption(103, 9));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                    }
//                    if (EventManager.HALLOWEEN) {
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {705, 706, 707, 708};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(87, 0));
//                            nro.itemOptions.add(new Item.ItemOption(30, 0));
//                            nro.itemOptions.add(new Item.ItemOption(93, 35));
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 585);
//                            nro.itemOptions.add(new Item.ItemOption(73, 0));
//                            it = nro;
//                        }
//                    }
//                }
//            } else {
//                if (Util.isTrue(50, 100)) {
//                    if (Util.isTrue(20, 100)) {
//                        int[] itemId = {18, 19, 20};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {663, 664, 665, 666, 667};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {381, 382, 383, 384, 385};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(86, 0));
//                        it = nro;
//                    } else if (Util.isTrue(50, 100)) {
//                        int[][] itemData = {
//                            {220, 68},
//                            {221, 70},
//                            {222, 69},
//                            {223, 71},
//                            {224, 67}
//                        };
//                        int index = Util.nextInt(0, itemData.length - 1); // chọn ngẫu nhiên 1 item
//                        int itemId = itemData[index][0];
//                        int optionId = itemData[index][1];
//
//                        Item nro = ItemService.gI().createNewItem((short) itemId);
//                        nro.itemOptions.add(new Item.ItemOption(optionId, 0));
//                        it = nro;
//                    } else if (Util.isTrue(40, 100)) {
//                        int[][] itemData = {
//                            {441, 95},
//                            {442, 96},
//                            {443, 97},
//                            {444, 98},
//                            {445, 99},
//                            {446, 100},
//                            {447, 101}
//                        };
//                        int index = Util.nextInt(0, itemData.length - 1); // chọn ngẫu nhiên 1 item
//                        int itemId = itemData[index][0];
//                        int optionId = itemData[index][1];
//
//                        Item nro = ItemService.gI().createNewItem((short) itemId);
//                        nro.itemOptions.add(new Item.ItemOption(optionId, (optionId == 98 || optionId == 99 ? 3 : 5)));
//                        it = nro;
//                    } else if (Util.isTrue(20, 100)) {
//                        int[] itemId = {2063, 2064, 2065, 2066, 2067, 2068};
//                        int itemid = itemId[Util.nextInt(itemId.length)];
//                        Item nro = ItemService.gI().createNewItem((short) itemid);
//                        nro.itemOptions.add(new Item.ItemOption(87, 0));
//                        it = nro;
//                    }
//                } else {
//                    if (EventManager.LUNNAR_NEW_YEAR) {
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {1759, 1760};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(30, 0));
//                            nro.itemOptions.add(new Item.ItemOption(93, 30));
//                            it = nro;
//                        } 
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {1191, 1192, 1193};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(30, 0));
//                            nro.itemOptions.add(new Item.ItemOption(93, 30));
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {733, 849, 920, 1443, 1468, 1477, 1848};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(50, 10));
//                            nro.itemOptions.add(new Item.ItemOption(77, 10));
//                            nro.itemOptions.add(new Item.ItemOption(103, 10));
//                            nro.itemOptions.add(new Item.ItemOption(97, 10));
//                            if (Util.isTrue(90, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                    }
//                    if (EventManager.CHRISTMAS) {
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {822, 823};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(50, Util.nextInt(13, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(77, Util.nextInt(13, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(103, Util.nextInt(13, 15)));
//                            nro.itemOptions.add(new Item.ItemOption(106, 0));
//                            if (Util.isTrue(99, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1467);
//                            nro.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 16)));
//                            nro.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 16)));
//                            nro.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 16)));
//                            nro.itemOptions.add(new Item.ItemOption(94, Util.nextInt(10, 16)));
//                            nro.itemOptions.add(new Item.ItemOption(97, Util.nextInt(10, 16)));
//                            nro.itemOptions.add(new Item.ItemOption(106, 0));
//                            if (Util.isTrue(199, 200)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 649);
//                            nro.itemOptions.add(new Item.ItemOption(30, 0));
//                            nro.itemOptions.add(new Item.ItemOption(93, 30));
//                            it = nro;
//                        }
//                    }
//                    if (EventManager.VU_LAN_FESTIVAL) {
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 1272);
//                            nro.itemOptions.add(new Item.ItemOption(84, 0));
//                            nro.itemOptions.add(new Item.ItemOption(50, 5));
//                            nro.itemOptions.add(new Item.ItemOption(77, 10));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {994, 995, 996};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(50, 11));
//                            nro.itemOptions.add(new Item.ItemOption(77, 10));
//                            nro.itemOptions.add(new Item.ItemOption(103, 10));
//                            nro.itemOptions.add(new Item.ItemOption(14, 10));
//                            if (Util.isTrue(95, 100)) {
//                                nro.itemOptions.add(new Item.ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
//                            }
//                            it = nro;
//                        }
//                    }
//                    if (EventManager.HALLOWEEN) {
//                        if (Util.isTrue(50, 100)) {
//                            int[] itemId = {705, 706, 707, 708};
//                            int itemid = itemId[Util.nextInt(itemId.length)];
//                            Item nro = ItemService.gI().createNewItem((short) itemid);
//                            nro.itemOptions.add(new Item.ItemOption(87, 0));
//                            nro.itemOptions.add(new Item.ItemOption(30, 0));
//                            nro.itemOptions.add(new Item.ItemOption(93, 35));
//                            it = nro;
//                        }
//                        if (Util.isTrue(50, 100)) {
//                            Item nro = ItemService.gI().createNewItem((short) 585);
//                            nro.itemOptions.add(new Item.ItemOption(73, 0));
//                            it = nro;
//                        }
//                    }
//                }
//            }
//            list.add(it);
//        }
//        return list;
//    }
//    
//    private Item itemRand(Item item, boolean success) {
//        if (!success) {
//            item = ItemService.gI().createNewItem((short) 189, Util.nextInt(5, 50) * 1000);
//        }
//        return item;
//    }
//}
