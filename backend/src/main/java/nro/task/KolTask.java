package nro.task;

import Utils.Util;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.services.Service;
import nro.services.TaskService;


public class KolTask {

    public KolTaskTemplate template;
    public int count;

    public void next() {
        if (template != null) {
            template = TaskService.gI().getKolTaskTemplateById(++template.id);
        }
        count = 0;
    }

    public void addCount() {
        if (template != null) {
            count++;
        }
    }

    public boolean isDone() {
        try {
            return count >= template.max_count;
        } catch (Exception e) {
            return false;
        }
    }

    public void receive(Player player) {
        if (template != null) {
            if (!isDone()) {
                Service.gI().sendThongBao(player, "Bạn chưa hoàn thành nhiệm vụ");
                return;
            }

            if (InventoryService.gI().getCountEmptyBag(player) < (template.rewards.size() - 1)) {
                Service.gI().sendThongBao(player, "Bạn cần trống " + (template.rewards.size() - 1) + " ô hành trang!");
                return;
            }

            for (Item item : template.rewards) {
                InventoryService.gI().addItemBag(player, ItemService.gI().copyItem(item));
                Service.gI().sendThongBao(player, "Bạn nhận được " + item.quantity + " " + item.template.name);
            }

            InventoryService.gI().sendItemBag(player);
            next();
        }
    }

    public void checkDonePickItem(ItemMap item) {
        if (template != null) {
            switch (template.id) {
                case 0: {
                    if (item.itemTemplate.id == 1836) {
                        count++;
                    }
                    break;
                }
                case 1: {
                    if (item.itemTemplate.id == 1837) {
                        count++;
                    }
                    break;
                }
            }
        }
    }

    public String getTaskInfo() {
        if (template != null) {
            return template.info.replaceAll("%1", String.valueOf(template.max_count));
        }
        return "Đã hoàn thành hết nhiệm vụ.";
    }

    public String getRewardsInfo() {
        StringBuilder text = new StringBuilder("Thưởng ");

        if (template == null || template.rewards == null) {
            return text.toString();
        }

        for (int i = 0; i < template.rewards.size(); i++) {
            Item item = template.rewards.get(i);

            if (item != null) {
                text.append(Util.format(item.quantity)).append(" ").append(item.template.name);

                ItemOption io = item.itemOptions.stream()
                        .filter(ItemOption::haveExpiryDate)
                        .findAny()
                        .orElse(null);

                if (io != null) {
                    text.append(" (").append(io.param).append(" ngày)");
                }

                if (i < template.rewards.size() - 1) {
                    text.append(", ");
                }
            }
        }

        return text.toString();
    }

    public int getPercentProcess() {
        if (template == null || template.max_count <= 0) {
            return 0;
        }

        if (count >= template.max_count) {
            return 100;
        }

        return count * 100 / template.max_count;
    }

    public void dispose() {
        template = null;
    }
}