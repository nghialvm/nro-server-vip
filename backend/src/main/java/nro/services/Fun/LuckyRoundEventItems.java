package nro.services.Fun;



import Utils.Util;
import static Utils.Util.nextInt;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;

public class LuckyRoundEventItems {
    
    private static int randomIn(int... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("No values provided for random selection.");
        }
        return values[nextInt(0, values.length - 1)];
    }
    
    private static final short[] PARAM_93_VALUES = {3, 5, 7, 15, 30};

    public static Item getLunarNewYearItem(boolean vip) {
        if (!EventManager.LUNNAR_NEW_YEAR) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(1024, 1025, 1026, 1027), new int[][]{
                    {50, 18}, {77, 15}, {103, 15}, {94, 10}
                });
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(1476, 1483), new int[][]{
                    {50, Util.nextInt(20, 25)},
                    {77, Util.nextInt(20, 25)},
                    {103, Util.nextInt(20, 25)},
                    {94, Util.nextInt(10, 15)},
                    {97, Util.nextInt(10, 15)},
                    {Util.isTrue(50, 100) ? 80 : 81, Util.nextInt(10, 15)},
                    {101, Util.nextInt(20, 30)},
                    {114, Util.nextInt(10, 20)}
                });
                addOption93(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(1759, 1760), new int[][]{
                    {30, 0}, {93, 30}
                });
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(1191, 1192, 1193), new int[][]{
                    {30, 0}, {93, 30}
                });
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(733, 849, 920, 1443, 1468, 1477, 1848), new int[][]{
                    {84, 0}, {50, 10}, {77, 10}, {103, 10}, {97, 10}
                });
                addOption93(it);
            }
        }
        return it;
    }

    public static Item getChristmasItem(boolean vip) {
        if (!EventManager.CHRISTMAS) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1171, new int[][]{{30, 0}, {93, 30}});
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(1455, 1465, 1466), new int[][]{
                    {84, 0}, {50, Util.nextInt(10, 15)}, {77, Util.nextInt(10, 15)}, {103, Util.nextInt(10, 15)}, {106, 0}
                });
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(745, new int[][]{
                    {50, Util.nextInt(10, 18)}, {77, Util.nextInt(10, 18)}, {103, Util.nextInt(10, 18)},
                    {94, Util.nextInt(10, 18)}, {97, Util.nextInt(10, 18)}, {106, 0}
                });
                addOption93(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(822, 823), new int[][]{
                    {50, Util.nextInt(13, 15)}, {77, Util.nextInt(13, 15)}, {103, Util.nextInt(13, 15)}, {106, 0}
                });
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1467, new int[][]{
                    {50, Util.nextInt(10, 16)}, {77, Util.nextInt(10, 16)}, {103, Util.nextInt(10, 16)},
                    {94, Util.nextInt(10, 16)}, {97, Util.nextInt(10, 16)}, {106, 0}
                });
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(649, new int[][]{{30, 0}, {93, 30}});
            }
        }
        return it;
    }

    public static Item getVuLanItem(boolean vip) {
        if (!EventManager.VU_LAN_FESTIVAL) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(898, new int[][]{
                    {50, 24}, {14, 4}, {77, 22}, {103, 21}, {5, 16}, {80, 11}
                });
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(849, new int[][]{{84, 0}, {114, 25}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1273, new int[][]{{84, 0}, {50, 10}, {77, 15}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1244, new int[][]{{50, 15}, {103, 15}, {77, 15}, {14, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1243, new int[][]{{50, 15}, {77, 15}, {103, 15}, {94, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1252, new int[][]{{84, 0}, {50, 6}, {77, 8}, {103, 8}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1253, new int[][]{{84, 0}, {50, 9}, {77, 9}, {103, 9}});
                addOption93(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1272, new int[][]{{84, 0}, {50, 5}, {77, 10}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(994, 995, 996), new int[][]{
                    {50, 11}, {77, 10}, {103, 10}, {14, 10}
                });
                addOption93(it);
            }
        }
        return it;
    }

    public static Item getHalloweenItem(boolean vip) {
        if (!EventManager.HALLOWEEN) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1346, new int[][]{{84, 0}, {50, Util.nextInt(5, 12)}, {77, Util.nextInt(5, 12)}, {103, Util.nextInt(5, 12)}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1785, new int[][]{{84, 0}, {50, Util.nextInt(5, 12)}, {77, Util.nextInt(5, 12)}, {103, Util.nextInt(5, 12)}, {14, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1347, new int[][]{{50, Util.nextInt(5, 12)}, {77, Util.nextInt(5, 12)}, {103, Util.nextInt(5, 12)}, {14, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1105, 
                new int[][]{{50, 24}, {77, 21}, {103, 21}, {14, 18}, {94, 18}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1309, 
                new int[][]{{50, 20}, {77, 30}, {103, 30}, {80, 15}, {94, 15}});
                addOption93(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(randomIn(705, 706, 707, 708), new int[][]{{87, 0}, {30, 0}, {93, 35}});
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(585, new int[][]{{73, 0}});
            }
        }
        return it;
    }
    
    public static Item getInternationalWomensDayItem(boolean vip) {
        if (!EventManager.INTERNATIONAL_WOMANS_DAY) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1783, new int[][]{{50, 24}, {77, 24}, {103, 24}, {210, 4}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(Util.nextInt(1174, 1176), new int[][]{{50, 24}, {77, 24}, {103, 24}, {14, 15}, {114, 20}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(Util.nextInt(1198, 1200), new int[][]{{50, 22}, {77, 26}, {103, 26}, {94, 15}, {114, 20}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1210, new int[][]{{50, 22}, {77, 23}, {103, 23}, {94, 10}, {97, 5}, {114, 20}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1208, new int[][]{{50, 22}, {77, 23}, {103, 23}, {14, 10}, {108, 5}, {114, 20}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1209, new int[][]{{50, 22}, {77, 23}, {103, 23}, {94, 10}, {108, 5}, {114, 20}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(954, new int[][]{{50, 22}, {77, 15}, {103, 15}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(955, new int[][]{{50, 18}, {77, 12}, {103, 12}});
                addOption93_Vip(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(954, new int[][]{{50, 22}, {77, 15}, {103, 15}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(955, new int[][]{{50, 18}, {77, 12}, {103, 12}});
                addOption93_Vip(it);
            }
        }
        return it;
    }
    
    public static Item getTrungThuItem(boolean vip) {
        if (!EventManager.TRUNG_THU) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1024, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, Util.nextInt(10, 15)}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1025, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, Util.nextInt(10, 15)}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1026, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, Util.nextInt(10, 15)}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1027, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, Util.nextInt(10, 15)}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1675, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, 11}, {5, 15}});
                addOption93(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1042, new int[][]{{50, 25}, {77, 21}, {103, 21}, {14, 15}, {114, 20}, {5, 10}, {196, 2}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1043, new int[][]{{50, 21}, {77, 25}, {103, 23}, {94, 20}, {114, 20}, {108, 10}, {196, 2}});
                addOption93(it);
            }
        }
        return it;
    }
    
    public static Item getHungVuongItem(boolean vip) {
        if (!EventManager.HUNG_VUONG) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1557, new int[][]{{50, 25}, {77, 24}, {103, 24}, {80, 15}, {108, 15}, {94, 15}, {114, 50}, {117, 10}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(860, new int[][]{{50, 24}, {77, 24}, {117, 20}, {114, 25}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1230, new int[][]{{50, 18}, {10, 15}, {14, 10}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1205, new int[][]{{50, 25}, {77, 25}, {103, 25}});
                addOption93(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1022, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1013, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {14, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1207, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {94, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1207, new int[][]{{50, Util.nextInt(15, 17)}, {77, Util.nextInt(15, 17)}, {103, Util.nextInt(15, 17)}, {94, 11}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1112, new int[][]{{50, Util.nextInt(16, 17)}, {5, Util.nextInt(16, 17)}, {14, 11}});
                addOption93(it);
            }
        }
        return it;
    }
    
    public static Item getBlackFridayItem(boolean vip) {
        if (!EventManager.BLACK_FRIDAY) return null;
        Item it = null;
        if (vip) {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1344, new int[][]{{77, 18}, {103, 18}, {5, 10}, {117, 5}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(860, new int[][]{{50, 24}, {77, 24}, {117, 20}, {114, 25}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1363, new int[][]{{50, 7}, {77, 7}, {103, 7}, {84, 0}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1309, new int[][]{{50, 20}, {77, 30}, {103, 30}, {80, 15}, {94, 15}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1384, new int[][]{{50, 23}, {77, 25}, {103, 24}, {101, 30}, {95, 15}, {96, 15}, {236, 20}});
                addOption93(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(954, new int[][]{{50, 22}, {77, 15}, {103, 15}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(955, new int[][]{{50, 18}, {77, 12}, {103, 12}});
                addOption93_Vip(it);
            }
        } else {
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(954, new int[][]{{50, 22}, {77, 15}, {103, 15}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(955, new int[][]{{50, 18}, {77, 12}, {103, 12}});
                addOption93_Vip(it);
            }
            if (Util.isTrue(50, 100)) {
                it = createItemWithOptions(1346, new int[][]{{84, 0}, {50, 7}, {77, 7}, {103, 7}});
                addOption93(it);
            }
        }
        return it;
    }

    private static Item createItemWithOptions(int itemId, int[][] options) {
        Item item = ItemService.gI().createNewItem((short) itemId);
        for (int[] opt : options) {
            item.itemOptions.add(new ItemOption(opt[0], opt[1]));
        }
        return item;
    }

    private static void addOption93(Item item) {
        if (item != null && Util.isTrue(90, 100)) {
            int value = PARAM_93_VALUES[Util.nextInt(0, PARAM_93_VALUES.length - 1)];
            item.itemOptions.add(new ItemOption(93, value));
        }
    }
    
    private static void addOption93_Vip(Item item) {
        if (item != null && Util.isTrue(99, 100)) {
            int value = PARAM_93_VALUES[Util.nextInt(0, PARAM_93_VALUES.length - 1)];
            item.itemOptions.add(new ItemOption(93, value));
        }
    }
}






