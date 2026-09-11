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
import nro.combine.CombineService;

public class PhapSuHoa {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combine.itemsCombine.size() == 2) {
                        Item daHacHoa = null;
                        Item itemHacHoa = null;
                        for (Item item_ : player.combine.itemsCombine) {
                            if (item_.template.id == 1978) {
                                daHacHoa = item_;
                            } else if (item_.isTrangBiPSH()) {
                                itemHacHoa = item_;
                            }
                        }
                        if (daHacHoa == null) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu đá pháp sư", "Đóng");
                            return;
                        }
                        if (itemHacHoa == null) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu trang bị", "Đóng");
                            return;
                        }
                        if (itemHacHoa != null) {
                            for (ItemOption itopt : itemHacHoa.itemOptions) {
                                if (itopt.optionTemplate.id == 72) {
                                    if (itopt.param >= CombineService.MAX_LEVEL_ITEM) {
                                        Service.gI().sendThongBao(player, "Trang bị đã đạt tới giới hạn pháp sư");
                                        return;
                                    }
                                }
                            }
                        }
                        String npcSay = "|2|Hiện tại " + itemHacHoa.template.name + "\n|0|";
                        for (ItemOption io : itemHacHoa.itemOptions) {
                            if (io.optionTemplate.id != 72) {
                                npcSay += io.getOptionString() + "\n";
                            }
                        }
                        player.combine.ratioCombine = 100;
                        npcSay += "|2|Sau khi nâng cấp sẽ cộng 1 chỉ số pháp sư ngẫu nhiên \n|7|"
                                + "\n|7|Tỉ lệ thành công: " + player.combine.ratioCombine + "%\n"
                                + "Cần " + Util.formatNumber(2000000000, FormatStyle.VIETNAMESE) + " vàng";

                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                npcSay, "Nâng cấp\n" + Util.formatNumber(2000000000, FormatStyle.VIETNAMESE) + " vàng", "Từ chối");
                    } else {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có trang bị có thể pháp sư và đá pháp sư", "Đóng");
                    }
                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }

    }

    public static void startCombine(Player player) {
         if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiPSH()).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu trang bị pháp sư");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1978).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu đá pháp sư");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gold < 2000000000) {
                Service.gI().sendThongBao(player, "Con cần 2 tỉ vàng để đổi...");
                return;
            }
            player.inventory.gold -= 2000000000;
            Item daHacHoa = player.combine.itemsCombine.stream().filter(item -> item.template.id == 1978).findFirst().get();
            Item trangBiHacHoa = player.combine.itemsCombine.stream().filter(Item::isTrangBiPSH).findFirst().get();
            if (daHacHoa == null) {
                Service.gI().sendThongBao(player, "Thiếu đá pháp sư");
                return;
            }
            if (trangBiHacHoa == null) {
                Service.gI().sendThongBao(player, "Thiếu trang bị pháp sư");
                return;
            }
            for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                if (itopt.optionTemplate.id == 72) {
                    if (itopt.param >= CombineService.MAX_LEVEL_ITEM) {

                        Service.gI().sendThongBao(player, "Trang bị đã đạt tới giới hạn pháp sư");
                        return;
                    }
                }
            }

            if (Util.isTrue(player.combine.ratioCombine, 30)) {
                CombineService.gI().sendEffectSuccessCombine(player);
                List<Integer> idOptionHacHoa = Arrays.asList(0, 6, 7, 47);
                int randomOption = idOptionHacHoa.get(Util.nextInt(0, 3));
                if (!trangBiHacHoa.haveOption(72)) {
                    trangBiHacHoa.itemOptions.add(new ItemOption(72, 1));
                } else {
                    for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                        if (itopt.optionTemplate.id == 72) {
                            itopt.param += 1;
                            break;
                        }
                    }
                }
                if (!trangBiHacHoa.haveOption(randomOption)) {
                    trangBiHacHoa.itemOptions.add(new ItemOption(randomOption, 500));
                } else {
                    for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                        if (itopt.optionTemplate.id == randomOption) {
                            itopt.param += Util.nextInt(100, 500);
                            break;
                        }
                    }
                }
                player.combine.ratioCombine = 0;
                Service.gI().sendThongBao(player, "Bạn đã nâng cấp thành công");
            } else {

                CombineService.gI().sendEffectFailCombine(player);

            }
            InventoryService.gI().subQuantityItemsBag(player, daHacHoa, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
             CombineService.gI().reOpenItemCombine(player);
             player.combine.itemsCombine.clear();
           
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }
    
}
