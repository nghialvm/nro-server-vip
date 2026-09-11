package Utils;

import nro.inventory.InventoryService;
import java.util.ArrayList;
import java.util.List;
import models.Item.Item;
import nro.player.Player;


public class ItemCheckUtil {

    private final Player player;
    private final List<String> statusLines = new ArrayList<>();
    private boolean allEnough = true;

    public ItemCheckUtil(Player player) {
        this.player = player;
    }

    public ItemCheckUtil check(int itemId, int requiredQty, String itemName) {
        Item item = InventoryService.gI().findItemBag(player, itemId);
        int have = (item == null ? 0 : item.quantity);
        boolean enough = have >= requiredQty;

        allEnough &= enough;

        String colorCode = enough ? "|2|" : "|7|";
        statusLines.add(colorCode + itemName + " " + have + "/" + requiredQty);

        return this;
    }

    public boolean isAllEnough() {
        return allEnough;
    }

    public String getStatusText(String title, boolean costGold, boolean costRuby, long costGoldValue, int costRubyValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("|1|").append(title).append("\n");

        for (String line : statusLines) {
            sb.append(line).append("\n");
        }

        if (costGold) {
            boolean enoughGold = player.inventory.getGold() >= costGoldValue;
            sb.append(enoughGold ? "|2|" : "|7|")
                    .append("Giá Vàng: ")
                    .append(Util.format(costGoldValue))
                    .append("\n");

            allEnough &= enoughGold;
        }

        if (costRuby) {
            boolean enoughRuby = player.inventory.getRuby() >= costRubyValue;
            sb.append(enoughRuby ? "|2|" : "|7|")
                    .append("Giá Hồng Ngọc: ")
                    .append(Util.format(costRubyValue))
                    .append("\n");

            allEnough &= enoughRuby;
        }

        return sb.toString();
    }
}