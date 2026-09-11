package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import java.util.Arrays;
import java.util.List;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.combine.CombineService;

public class SieuHoaCaiTrang {

    private static int getDaNangcapSieuHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 12;
            case 2:
                return 14;
            case 3:
                return 16;
            case 4:
                return 18;
            case 5:
                return 20;
            case 6:
                return 22;
            case 7:
                return 24;
        }
        return 0;
    }

    private static float getTiLeNangcapSieuHoa(int star) {
        switch (star) {
            case 0:
                return 100f;
            case 1:
                return 50f;
            case 2:
                return 30f;
            case 3:
                return 20f;
            case 4:
                return 10f;
            case 5:
                return 5f;
            case 6:
                return 3f;
            case 7:
                return 2f;
        }
        return 0;
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() == 2) {
            Item caiTrang = null;
            Item manhVo = null;
            int star = 0;

            for (Item item : player.combine.itemsCombine) {
                if (item.template.type == 5) {
                    caiTrang = item;
                } else if (item.template.id == 1979) {
                    manhVo = item;
                }
            }

            if (caiTrang != null) {
                for (ItemOption io2 : caiTrang.itemOptions) {
                    if (io2.optionTemplate.id == 72) {
                        if (io2.param >= CombineService.MAX_LEVEL_ITEM) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Cải Trang đã siêu hóa cấp tối đa", "Đóng");
                            return;
                        }
                        star = io2.param;
                        break;
                    }
                }
            }

            player.combine.DaNangcap = getDaNangcapSieuHoa(star);
            player.combine.TileNangcap = getTiLeNangcapSieuHoa(star);

            if (caiTrang != null && manhVo != null) {
                String npcSay = caiTrang.template.name + "\n|2|";
                for (ItemOption io : caiTrang.itemOptions) {
                    npcSay += io.getOptionString() + "\n";
                }

                npcSay += "|7|Tỉ lệ thành công: " + getTiLeNangcapSieuHoa(star) + "%\n";
                npcSay += "|7|Cần: " + Util.formatNumber(10_000_000_000L, FormatStyle.VIETNAMESE)
                        + " vàng\n";
                npcSay += "|7|Cần " + Util.formatNumber(getDaNangcapSieuHoa(star), FormatStyle.VIETNAMESE)
                        + " Đá Siêu Hóa";

                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                        "Nâng cấp\ncần " + Util.formatNumber(getDaNangcapSieuHoa(star), FormatStyle.VIETNAMESE)
                        + " Đá Siêu Hóa");
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Cần 1 Cải Trang và Đá Siêu Hóa", "Đóng");
            }
        } else {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần 1 Cải Trang và Đá Siêu Hóa", "Đóng");
        }
    }

    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() == 2) {
            float tiLe = player.combine.TileNangcap;
            long gold = 10_000_000_000L;

            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ 10 tỷ vàng để thực hiện");
                return;
            }

            Item caiTrang = null;
            Item manhCaiTrang = null;
            for (Item item : player.combine.itemsCombine) {
                if (item.template.type == 5) {
                    caiTrang = item;
                } else if (item.template.id == 1979) {
                    manhCaiTrang = item;
                }
            }

            int star = 0;
            if (caiTrang != null) {
                for (ItemOption io : caiTrang.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        star = Math.max(star, io.param);
                    }
                }
            }

            if (star >= 10) {
                Service.gI().sendThongBao(player, "Đã max cấp");
                return;
            }

            if (caiTrang != null && manhCaiTrang != null
                    && manhCaiTrang.quantity >= player.combine.DaNangcap
                    && star < CombineService.MAX_LEVEL_ITEM) {

                player.inventory.gold -= gold;

                InventoryService.gI().subQuantityItemsBag(player, manhCaiTrang, player.combine.DaNangcap);

                if (Util.isTrue(tiLe, 100)) {
                    Item newCaiTrang = ItemService.gI().createNewItem((short) (caiTrang.template.id));
                    for (ItemOption io : caiTrang.itemOptions) {
                        newCaiTrang.itemOptions.add(new ItemOption(io.optionTemplate.id, io.param));
                    }

                    List<Integer> optionIds = Arrays.asList(0, 6, 7, 47);
                    int randomOptionId = optionIds.get(Util.nextInt(0, optionIds.size()));
                    boolean hasOption = false;

                    for (ItemOption io : newCaiTrang.itemOptions) {
                        if (io.optionTemplate.id == randomOptionId) {
                            io.param += Util.nextInt(500, 3000);
                            hasOption = true;
                            break;
                        }
                    }

                    boolean foundOption72 = false;
                    for (ItemOption io : newCaiTrang.itemOptions) {
                        if (io.optionTemplate.id == 72) {
                            io.param = Math.min(io.param + 1, CombineService.MAX_LEVEL_ITEM);
                            foundOption72 = true;
                            break;
                        }
                    }

                    if (!foundOption72) {
                        newCaiTrang.itemOptions.add(new ItemOption(72, 1));
                    }

                    if (!hasOption) {
                        newCaiTrang.itemOptions.add(new ItemOption(randomOptionId, Util.nextInt(500, 3000)));
                    }

                    CombineService.gI().sendEffectSuccessCombine(player);

                    InventoryService.gI().subQuantityItemsBag(player, caiTrang, 1);
                    InventoryService.gI().addItemBag(player, newCaiTrang);

                } else {
                    CombineService.gI().sendEffectFailCombine(player);
                }

                InventoryService.gI().sendItemBag(player);
                Service.gI().sendMoney(player);
                CombineService.gI().reOpenItemCombine(player);

            } else {
                Service.gI().sendThongBao(player, "Thiếu vật phầm để nâng");
            }
        }
    }
}