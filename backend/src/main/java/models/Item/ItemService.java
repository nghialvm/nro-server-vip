package models.Item;

import nro.inventory.InventoryService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.services.Service;
import nro.shop.ItemShop;
import nro.server.Manager;
import Utils.TimeUtil;
import Utils.Util;
import java.util.*;
import java.util.stream.Collectors;
import nro.map.Zone;
import nro.shop.Shop;
import nro.shop.TabShops.TabShop;
import nro.template.ItemOptionTemplate;
import nro.template.ItemTemplate;

public class ItemService {

    private static ItemService i;

    public static ItemService gI() {
        if (i == null) {
            i = new ItemService();
        }
        return i;
    }

    public ItemMap randDoTLBoss(Zone zone, int quantity, int x, int y, long id) {
        short idTempTL;
        short[] ao = {555, 557, 559};
        short[] quan = {556, 558, 560};
        short[] gang = {562, 564, 566};
        short[] giay = {563, 565, 567};
        short[] nhan = {561};
        short[] options = {86, 87};
        if (Util.isTrue(10, 100)) {  // Nhẫn (10%)
            idTempTL = nhan[0];
        } else if (Util.isTrue(25, 100)) {  // Găng tay (15%)
            idTempTL = gang[Util.nextInt(3)];
        } else if (Util.isTrue(45, 100)) {  // Quần (20%)
            idTempTL = quan[Util.nextInt(3)];
        } else if (Util.isTrue(75, 100)) {  // Áo (30%)
            idTempTL = ao[Util.nextInt(3)];
        } else {  // Giày (25%)
            idTempTL = giay[Util.nextInt(3)];
        }

        int tiLe = Util.nextInt(100, 115);
        List<ItemOption> itemoptions = new ArrayList<>();

        switch (idTempTL) {
            case 555: // Áo Thần Linh TD
                itemoptions.add(new ItemOption(47, 800 * tiLe / 100));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100)); // Vật phẩm hiếm rơi từ quái
                }
                break;
            case 557: // Áo Thần Linh NM
                itemoptions.add(new ItemOption(47, 850 * tiLe / 100));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 559: // Áo Thần Linh XD
                itemoptions.add(new ItemOption(47, 900 * tiLe / 100));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 556: // Quần Thần Linh TD
                int chiso = 52000 * tiLe / 100;
                itemoptions.add(new ItemOption(22, chiso / 1000));
                itemoptions.add(new ItemOption(27, chiso / 20));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 558: // Quần Thần Linh NM
                chiso = 50000 * tiLe / 100;
                itemoptions.add(new ItemOption(22, chiso / 1000));
                itemoptions.add(new ItemOption(27, chiso / 20));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 560: // Quần Thần Linh XD
                chiso = 48000 * tiLe / 100;
                itemoptions.add(new ItemOption(22, chiso / 1000));
                itemoptions.add(new ItemOption(27, chiso / 20));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 562: // Găng tay Thần Linh TD
                itemoptions.add(new ItemOption(0, 4400 * tiLe / 100));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 564: // Găng tay Thần Linh NM
                itemoptions.add(new ItemOption(0, 4300 * tiLe / 100));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 566: // Găng tay Thần Linh
                itemoptions.add(new ItemOption(0, 4500 * tiLe / 100));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 563: // Giày Thần Linh TD
                chiso = 48000 * tiLe / 100;
                itemoptions.add(new ItemOption(23, chiso / 1000));
                itemoptions.add(new ItemOption(28, chiso / 20));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 565: // Giày Thần Linh NM
                chiso = 50000 * tiLe / 100;
                itemoptions.add(new ItemOption(23, chiso / 1000));
                itemoptions.add(new ItemOption(28, chiso / 20));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 567: // Giày Thần Linh XD
                chiso = 46000 * tiLe / 100;
                itemoptions.add(new ItemOption(23, chiso / 1000));
                itemoptions.add(new ItemOption(28, chiso * 150 / 1000));
                if (tiLe > 100) {
                    itemoptions.add(new ItemOption(207, tiLe - 100));
                }
                break;
            case 561: // Nhẫn Thần Linh
                itemoptions.add(new ItemOption(14, 14 * tiLe / 100));
                break;
            default:
                break;
        }

        if (Util.isTrue(30, 100)) {
            itemoptions.add(new ItemOption(options[Util.nextInt(options.length)], 0));
        }

        itemoptions.add(new ItemOption(21, Util.nextInt(15, 17)));

        ItemMap it = new ItemMap(zone, idTempTL, quantity, x, y, id);
        it.options.clear();
        it.options.addAll(itemoptions);
        return it;
    }

    public short getItemIdByIcon(short IconID) {
        for (int j = 0; j < Manager.ITEM_TEMPLATES.size(); j++) {
            if (Manager.ITEM_TEMPLATES.get(j).iconID == IconID) {
                return Manager.ITEM_TEMPLATES.get(j).id;
            }
        }
        return -1;
    }

    public Item createItemNull() {
        Item item = new Item();
        return item;
    }

    private void removeAndAddOptionTemplateHSD(List<ItemOption> itemOptions, int removeId) {
        int id = 0;
        int param = 0;
        int[] random = new int[]{1, 3, 5, 7, 14, 30};
        int ChiSo = new Random().nextInt(random.length);
        boolean shouldExecute = false;
        switch (removeId) {
            case 231:
                id = 93;
                param = random[ChiSo];
                shouldExecute = true;
                break;
            default:
                break;
        }
        if (shouldExecute && itemOptions.stream().anyMatch(io -> io.optionTemplate.id == removeId)) {
            itemOptions.removeIf(io -> io.optionTemplate.id == removeId);
            if (Util.isTrue(Util.nextInt(86, 90), Util.nextInt(96, 100))) {
                itemOptions.add(new ItemOption(new ItemOption(id, param)));
            }
        }
    }

    private void removeOptionTemplate(List<ItemOption> itemOptions, int removeId) {
        boolean shouldExecute = false;
        switch (removeId) {
            case 76:
                shouldExecute = true;
                break;
            case 164:
                shouldExecute = true;
                break;
            case 249:
                shouldExecute = true;
                break;
            default:
                break;
        }
        if (shouldExecute && itemOptions.stream().anyMatch(io -> io.optionTemplate.id == removeId)) {
            itemOptions.removeIf(io -> io.optionTemplate.id == removeId);
        }
    }

    private void removeAndAddOptionTemplate(List<ItemOption> itemOptions, int removeId) {
        if (removeId != 210) {
            return;
        }
        Optional<ItemOption> option210 = itemOptions.stream()
                .filter(io -> io.optionTemplate.id == 210)
                .findFirst();
        if (!option210.isPresent()) {
            return;
        }
        int paramCount = option210.get().param;
        int[] randomValues = {50, 77, 103, 14, 5, 94, 97, 108, 95, 96, 101, 204, 80, 81, 45, 46, 197};
        List<Integer> availableIds = new ArrayList<>();
        for (int value : randomValues) {
            availableIds.add(value);
        }
        Collections.shuffle(availableIds);
        List<ItemOption> newOptions = new ArrayList<>();
        Random random = new Random();
        for (int j = 0; j < paramCount; j++) {
            int id;
            if (j < availableIds.size()) {
                id = availableIds.get(j);
            } else {
                id = randomValues[random.nextInt(randomValues.length)];
            }
            int param;
            switch (id) {
                case 50:
                case 77:
                case 103:
                case 108:
                case 45:
                case 46:
                case 197:
                    param = Util.nextInt(5, 10);
                    break;
                case 94:
                case 97:
                case 204:
                case 14:
                    param = Util.nextInt(7, 18);
                    break;
                case 5:
                case 80:
                case 81:
                case 95:
                case 96:
                    param = Util.nextInt(10, 20);
                    break;
                default:
                    param = Util.nextInt(15, 30);
                    break;
            }
            newOptions.add(new ItemOption(id, param));
        }
        itemOptions.removeIf(io -> io.optionTemplate.id == removeId);
        itemOptions.addAll(newOptions);
    }

    public Item createItemFromItemShop(ItemShop itemShop) {
        Item item = new Item();
        item.template = itemShop.temp;
        item.quantity = 1;
        item.content = item.getContent();
        item.info = item.getInfo();
        for (ItemOption io : itemShop.options) {
            item.itemOptions.add(new ItemOption(io));
            removeAndAddOptionTemplate(item.itemOptions, new ItemOption(io).optionTemplate.id);
            removeAndAddOptionTemplateHSD(item.itemOptions, new ItemOption(io).optionTemplate.id);
            removeOptionTemplate(item.itemOptions, new ItemOption(io).optionTemplate.id);
        }
        return item;
    }

    public Item copyItem(Item item) {
        Item it = new Item();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (ItemOption io : item.itemOptions) {
            it.itemOptions.add(new ItemOption(io));
        }
        return it;
    }

    public Item createNewItem(short tempId) {
        return createNewItem(tempId, 1);
    }

    public Item otpts(short tempId) {
        return otpts(tempId, 1);
    }

    public Item createNewItem(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createNewItemLock(int tempId) {
        return createNewItemLock(tempId, 1);
    }

    public Item createNewItemLock(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        item.itemOptions.add(new ItemOption(30, 1));
        return item;
    }

    public Item otpts(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(18000, 20000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150, 200)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemSetKichHoat(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemDoHuyDiet(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemFromItemMap(ItemMap itemMap) {
        Item item = createNewItem(itemMap.itemTemplate.id, itemMap.quantity);
        item.itemOptions = itemMap.options;
        return item;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return Manager.ITEM_OPTION_TEMPLATES.get(id);
    }

    public ItemTemplate getTemplate(int id) {
        return Manager.ITEM_TEMPLATES.get(id);
    }

    public boolean isItemActivation(Item item) {
        return false;
    }

    public int getPercentTrainArmor(Item item) {
        if (item == null) {
            return 0;
        }
        switch (item.template.id) {
            case 529: // giáp tập luyện cấp 1
            case 534:
                return 10;
            case 530: // cấp 2
            case 535:
                return 20;
            case 531: // cấp 3
            case 536:
                return 30;
            case 1797: // giáp đặc biệt
                return 40;
            default:
                return 0;
        }
    }

    public boolean isTrainArmor(Item item) {
        if (item == null) {
            return false;
        }
        switch (item.template.id) {
            case 529:
            case 534:
            case 530:
            case 535:
            case 531:
            case 536:
            case 1797:
                return true;
            default:
                return false;
        }
    }

    /**
     * Kiểm tra item hết hạn và cập nhật thời gian.
     *
     * @param item Item cần kiểm tra
     * @return true nếu bất kỳ option nào hết hạn, false nếu còn hạn
     */
    public boolean isOutOfDateTime(Item item) {
        if (item == null) {
            return false;
        }
        boolean expired = false;
        for (ItemOption io : item.itemOptions) {
            long now = System.currentTimeMillis();
            switch (io.optionTemplate.id) {
                case 93:
                    int dayPassed = (int) TimeUtil.diffDate(new Date(now), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPassed > 0) {
                        io.param -= dayPassed;
                        if (io.param <= 0) {
                            expired = true;
                        } else {
                            item.createTime = now;
                        }
                    }
                    break;
                case 260:
                    int hoursPassed = (int) TimeUtil.diffDate(new Date(now), new Date(item.createTime), TimeUtil.HOUR);
                    if (hoursPassed > 0) {
                        io.param -= hoursPassed;
                        if (io.param <= 0) {
                            expired = true;
                        } else {
                            item.createTime = now;
                        }
                    }
                    break;
                case 261:
                    int minutesPassed = (int) TimeUtil.diffDate(new Date(now), new Date(item.createTime), TimeUtil.MINUTE);
                    if (minutesPassed > 0) {
                        io.param -= minutesPassed;
                        if (io.param <= 0) {
                            expired = true;
                        } else {
                            item.createTime = now;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return expired;
    }

    public void loadItemTimeStatus(Player player, Item item) {
        if (item == null) {
            return;
        }
        long now = System.currentTimeMillis();
        for (ItemOption io : item.itemOptions) {
            switch (io.optionTemplate.id) {
                case 93:
                    int dayPassed = (int) TimeUtil.diffDate(new Date(now), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPassed > 0) {
                        io.param -= dayPassed;
                        if (io.param < 0) {
                            io.param = 0;
                        }
                        item.createTime = now;
                    }
                    break;
                case 260:
                    int hoursPassed = (int) TimeUtil.diffDate(new Date(now), new Date(item.createTime), TimeUtil.HOUR);
                    if (hoursPassed > 0) {
                        io.param -= hoursPassed;
                        if (io.param < 0) {
                            io.param = 0;
                        }
                        item.createTime = now;
                    }
                    break;
                case 261:
                    int minutesPassed = (int) TimeUtil.diffDate(new Date(now), new Date(item.createTime), TimeUtil.MINUTE);
                    if (minutesPassed > 0) {
                        io.param -= minutesPassed;
                        if (io.param < 0) {
                            io.param = 0;
                        }
                        item.createTime = now;
                    }
                    break;
                default:
                    break;
            }
            if (ItemService.gI().isOutOfDateTime(item)) {
                item = ItemService.gI().createItemNull();
            }
        }
        InventoryService.gI().sendItemBag(player);
    }

    // Random ID của set kích hoạt theo giới tính
// gender: 0 = Trái Đất, 1 = Namếc, 2 = Xayda, 3 = dùng chung cho Xayda
    public int randomSKHId(byte gender) {
        if (gender == 3) {
            gender = 2; // Nếu là giới tính 3 thì quy về Xayda
        }

        // Mảng 3 chiều chứa id option set cho từng chủng tộc
        // options[gender][0] = Set loại 1
        // options[gender][1] = Set loại 2
        // options[gender][2] = Set loại 3
        int[][] options = {
            {128, 129, 127}, // Trái Đất
            {131, 132, 130}, // Namếc
            {133, 135, 134} // Xayda
        };

        // Tỷ lệ rơi của từng loại set
        int skhv1 = 25; // 25% cho set loại 1
        int skhv2 = 35; // 35% cho set loại 2
        int skhc = 40; // 40% cho set loại 3

        int skhId = -1;
        int rd = Util.nextInt(1, 100); // random từ 1 -> 100

        // Xác định set dựa theo random
        if (rd <= skhv1) {              // 25%
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) { // tiếp theo 35%
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) { // còn lại 40%
            skhId = 2;
        }

        // Trường hợp đặc biệt: Trái Đất và Set đầu tiên có thêm 50% cơ hội thành 214
        if (gender == 0 && skhId == 0 && Util.isTrue(50, 100)) {
            return 214;
        }

        // Trả về ID option set theo giới tính + loại set đã random
        return options[gender][skhId];
    }

    // Tạo ra 1 món đồ kích hoạt (SKH) dựa vào item gốc và skhId random
    public Item itemSKH(int itemId, int skhId) {
        // Tạo 1 item trống từ template
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            // Thêm các option cơ bản giống trong Shop
            item.itemOptions.addAll(ItemService.gI().getListOptionItemShop((short) itemId));

            // Thêm option set kích hoạt (ví dụ 127, 128, 129...)
            item.itemOptions.add(new ItemOption(skhId, 1));

            // Thêm option bonus tương ứng với set đó
            // Ví dụ set 127 (Taiyoken) thì bonus 139 (x2 thời gian chói mắt)
            item.itemOptions.add(new ItemOption(optionIdSKH(skhId), 1));

            // Thêm option khóa (id = 30, param = 1)
            item.itemOptions.add(new ItemOption(30, 1));
        }
        return item;
    }

    // Map từ option set (127–135, 250–255) sang option bonus đi kèm
// Mỗi set có hiệu ứng riêng biệt
    public int optionIdSKH(int skhId) {
        switch (skhId) {
            case 127:
                return 139; // Set Taiyoken -> x2 thời gian chói mắt
            case 128:
                return 140; // Set Genki -> +100% sát thương Quả Cầu Kênh Khi
            case 129:
                return 141; // Set Kamejoko -> +100% sát thương Kamejoko
            case 130:
                return 143; // Set Dame -> +150% KI
//                return 142; // Set KI -> +50% sát thương Masenko
            case 131:
//                return 143; // Set Dame -> +150% KI
                return 254; // Set Dame -> +100% sát thương liên hoàn
            case 132:
                return 144; // Set Summon -> +100% sát thương + bất tử đệ tử
            case 133:
                return 136; // Set Galick -> +100% sát thương đấm Galick
            case 134:
                return 137; // Set Monkey -> x5 thời gian hóa khỉ
            case 135:
                return 138; // Set HP -> +80% HP
            case 250:
                return 253; // Set Kaioken -> +100% sát thương Kaioken
            case 251:
                return 254; // Set Liên Hoàn -> +100% sát thương Liên Hoàn
            case 255:
                return 256; // Set Giảm Sát Thương -> +80% giảm sát thương
        }
        return 0; // fallback
    }

    public Item itemDHD(int itemId, int dhdId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.add(new ItemOption(dhdId, 1));
            item.itemOptions.add(new ItemOption(optionIdDHD(dhdId), 1));
            item.itemOptions.add(new ItemOption(30, 1));
        }
        return item;
    }

    public int optionIdDHD(int skhId) {
        switch (skhId) {
            case 127: //Set Taiyoken
                return 139;
            case 128: //Set Genki
                return 140;
            case 129: //Set Kamejoko
                return 141;
            case 130: //Set KI
                return 143;
            case 131: //Set Dame
                return 254;
            case 132: //Set Summon
                return 144;
            case 133: //Set Galick
                return 136;
            case 134: //Set Monkey
                return 137;
            case 135: //Set HP
                return 138;
            case 250: //Set Kaioken
                return 253;
            case 251: //Set Lien Hoàn
                return 254;
            case 255: //Set Giảm Sát Thương
                return 256;

        }
        return 0;
    }

    public Item randomCS_DHD(int itemId, int gender) {
        Item it = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(650, 652, 654);
        List<Integer> quan = Arrays.asList(651, 653, 655);
        List<Integer> gang = Arrays.asList(657, 659, 661);
        List<Integer> giay = Arrays.asList(658, 660, 662);
        int nhd = 656;
        if (ao.contains(itemId)) {
            it.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(1001) + 1800))); // áo từ 1800-2800 giáp
        }
        if (quan.contains(itemId)) {
            it.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(16) + 85))); // hp 85-100k
        }
        if (gang.contains(itemId)) {
            it.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(150) + 8500))); // 8500-10000
        }
        if (giay.contains(itemId)) {
            it.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(11) + 80))); // ki 80-90k
        }
        if (nhd == itemId) {
            it.itemOptions.add(new ItemOption(14, new Random().nextInt(3) + 17)); //chí mạng 17-19%
        }
        it.itemOptions.add(new ItemOption(21, 80));// yêu cầu sm 80 tỉ
        it.itemOptions.add(new ItemOption(30, 1));// ko the gd
        return it;
    }

    public void openDTS(Player player) {
        //check sl đồ tl, đồ hd
        if (player.combine.itemsCombine.stream().filter(item -> item.template.id >= 555 && item.template.id <= 567).count() < 1) {
            Service.gI().sendThongBao(player, "Thiếu đồ thần linh");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.template.id >= 650 && item.template.id <= 662).count() < 2) {
            Service.gI().sendThongBao(player, "Thiếu đồ hủy diệt");
            return;
        }
        if (player.combine.itemsCombine.size() != 3) {
            Service.gI().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item itemTL = player.combine.itemsCombine.stream().filter(item -> item.template.id >= 555 && item.template.id <= 567).findFirst().get();
            List<Item> itemHDs = player.combine.itemsCombine.stream().filter(item -> item.template.id >= 650 && item.template.id <= 662).collect(Collectors.toList());
            short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061}, {1050, 1053, 1056, 1059, 1062}}; // thứ tự td - 0,nm - 1, xd - 2

            Item itemTS = DoThienSu(itemIds[player.gender][itemTL.template.type], player.gender);
            InventoryService.gI().addItemBag(player, itemTS);

            InventoryService.gI().subQuantityItemsBag(player, itemTL, 1);
            itemHDs.forEach(item -> InventoryService.gI().subQuantityItemsBag(player, item, 1));

            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + itemTS.template.name);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public Item DoThienSu(int itemId, int gender) {
        Item dots = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        //áo
        if (ao.contains(itemId)) {
            dots.addOptionParam(47, Util.highlightsItem(gender == 2, new Random().nextInt(1201) + 2800)); // áo từ 2800-4000 giáp
        }
        //quần
        if (Util.isTrue(80, 100)) {
            if (quan.contains(itemId)) {
                dots.addOptionParam(22, Util.highlightsItem(gender == 0, new Random().nextInt(11) + 120)); // hp 120k-130k
            }
        } else {
            if (quan.contains(itemId)) {
                dots.addOptionParam(22, Util.highlightsItem(gender == 0, new Random().nextInt(21) + 130)); // hp 130-150k 15%
            }
        }
        //găng
        if (Util.isTrue(80, 100)) {
            if (gang.contains(itemId)) {
                dots.addOptionParam(0, Util.highlightsItem(gender == 2, new Random().nextInt(651) + 9350)); // 9350-10000
            }
        } else {
            if (gang.contains(itemId)) {
                dots.addOptionParam(0, Util.highlightsItem(gender == 2, new Random().nextInt(1001) + 10000)); // gang 15% 10-11k -xayda 12k1
            }
        }
        //giày
        if (Util.isTrue(80, 100)) {
            if (giay.contains(itemId)) {
                dots.addOptionParam(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 90)); // ki 90-110k
            }
        } else {
            if (giay.contains(itemId)) {
                dots.addOptionParam(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 110)); // ki 110-130k
            }
        }

        if (nhan.contains(itemId)) {
            dots.addOptionParam(14, Util.highlightsItem(gender == 1, new Random().nextInt(3) + 18)); // nhẫn 18-20%
        }
        dots.addOptionParam(21, 120);
        dots.addOptionParam(30, 1);
        return dots;
    }

    public List<ItemOption> getListOptionItemShop(int level, int gender, int type) {
        List<ItemOption> list = new ArrayList<>();
        Manager.SHOPS.forEach(shop -> shop.tabShops.forEach(tabShop -> tabShop.itemShops.forEach(itemShop -> {
            if (itemShop.temp.level == level && itemShop.temp.gender == gender && itemShop.temp.type == type && list.isEmpty()) {
                for (ItemOption io : itemShop.options) {
                    list.add(new ItemOption(io.optionTemplate.id, io.param));
                }
            }
        })));
        return list;
    }

    public int randTempItemKichHoat(int gender) {
        int[][][] items = {
            {{0, 0}, {1, 1}, {2, 2}, {0, 33}, {1, 41}, {2, 49}, {0, 3}, {1, 4}, {2, 5}},
            {{0, 6}, {1, 7}, {2, 8}, {0, 35}, {1, 43}, {2, 51}, {0, 9}, {1, 10}, {2, 11}},
            {{0, 21}, {1, 22}, {2, 23}, {0, 24}, {1, 46}, {2, 53}, {0, 37}, {1, 25}, {2, 26}},
            {{0, 27}, {1, 28}, {2, 29}, {0, 30}, {1, 47}, {2, 55}, {0, 39}, {1, 31}, {2, 32}},
            {{3, 12}, {3, 57}, {3, 58}, {3, 59}}
        };
        for (int j = 0; j < 1; j++) {
            int type = Util.nextInt(items.length);
            List<Integer> candidates = new ArrayList<>();
            for (int[] pair : items[type]) {
                if (pair[0] == gender || pair[0] == 3) {
                    candidates.add(pair[1]);
                }
            }
            if (!candidates.isEmpty()) {
                return candidates.get(Util.nextInt(candidates.size()));
            }
        }
        return 0;
    }

    public int[] randOptionItemKichHoat(int gender) {
        int[][] options;
        switch (gender) {
            case 0:
                options = new int[][]{
                    {128, 140},
                    {127, 139},
                    {129, 141},
                    {233, 234},
                    {250, 253},
                    {263, 264},
                    {265, 266},
                    {267, 268}
                };
                break;
            case 1:
                options = new int[][]{
                    {130, 143},
                    {131, 254},
                    {132, 144},
                    {233, 234},
                    {251, 142},
                    {263, 264},
                    {265, 266},
                    {267, 268}
                };
                break;
            default:
                options = new int[][]{
                    {134, 137},
                    {135, 138},
                    {133, 136},
                    {233, 234},
                    {252, 255},
                    {263, 264},
                    {265, 266},
                    {267, 268}
                };
                break;
        }
        return options[Util.nextInt(options.length)];
    }

    public int[] randOptionItemKichHoatNew(int gender) {
        int op1;
        int op2;
        int op3;
        int op4;
        switch (gender) {
            case 0: {
                op1 = 245;
                op2 = 246;
                op3 = 247;
                op4 = 248;
                break;
            }
            case 1: {
                op1 = 237;
                op2 = 238;
                op3 = 239;
                op4 = 240;
                break;
            }
            case 2: {
                op1 = 241;
                op2 = 242;
                op3 = 243;
                op4 = 244;
                break;
            }
            default: {
                op1 = 269;
                op2 = 270;
                op3 = 271;
                op4 = 272;
                break;
            }
        }
        int[] options = {op1, op2, op3, op4};
        return options;
    }

    public int randTempItemKichHoat_VIP(int gender) {
        int[][][] items = {
            {{0, 0}, {1, 1}, {2, 2}, {0, 33}, {1, 41}, {2, 49}, {0, 3}, {1, 4}, {2, 5}, {0, 34}, {0, 136}, {0, 137}, {0, 138}, {0, 139}, {0, 230}, {0, 231}, {0, 232}, {0, 233},
            {1, 42}, {1, 152}, {1, 153}, {1, 154}, {1, 155}, {1, 234}, {1, 235}, {1, 236}, {1, 237}, {2, 50}, {2, 168}, {2, 169}, {2, 170}, {2, 171}, {2, 238}, {2, 239},
            {2, 240}, {2, 241}},
            {{0, 6}, {1, 7}, {2, 8}, {0, 35}, {1, 43}, {2, 51}, {0, 9}, {1, 10}, {2, 11}, {0, 36}, {0, 140}, {0, 141}, {0, 142}, {0, 143}, {0, 242}, {0, 243}, {0, 244}, {0, 245},
            {1, 44}, {1, 156}, {1, 157}, {1, 158}, {1, 159}, {1, 246}, {1, 247}, {1, 248}, {1, 249}, {2, 52}, {2, 172}, {2, 173}, {2, 174}, {2, 175}, {2, 250}, {2, 251}, {2, 252},
            {2, 253}},
            {{0, 21}, {1, 22}, {2, 23}, {0, 24}, {1, 46}, {2, 53}, {0, 37}, {1, 25}, {2, 26}, {0, 38}, {0, 144}, {0, 145}, {0, 146}, {0, 147}, {0, 254}, {0, 255}, {0, 256}, {0, 257},
            {1, 45}, {1, 160}, {1, 161}, {1, 162}, {1, 163}, {1, 258}, {1, 259}, {1, 260}, {1, 261}, {2, 54}, {2, 176}, {2, 177}, {2, 178}, {2, 179}, {2, 262}, {2, 263}, {2, 264},
            {2, 265}},
            {{0, 27}, {1, 28}, {2, 29}, {0, 30}, {1, 47}, {2, 55}, {0, 39}, {1, 31}, {2, 32}, {0, 40}, {0, 148}, {0, 149}, {0, 150}, {0, 151}, {0, 266}, {0, 267}, {0, 268}, {0, 269},
            {1, 48}, {1, 164}, {1, 165}, {1, 166}, {1, 167}, {1, 270}, {1, 271}, {1, 272}, {1, 273}, {2, 56}, {2, 180}, {2, 181}, {2, 182}, {2, 183}, {2, 274}, {2, 275}, {2, 276},
            {2, 277}},
            {{3, 12}, {3, 57}, {3, 58}, {3, 59}, {3, 184}, {3, 185}, {3, 186}, {3, 187}, {3, 278}, {3, 279}, {3, 280}, {3, 281}}
        };
        for (int j = 0; j < 1; j++) {
            int type = Util.nextInt(items.length);
            List<Integer> candidates = new ArrayList<>();
            for (int[] pair : items[type]) {
                if (pair[0] == gender || pair[0] == 3) {
                    candidates.add(pair[1]);
                }
            }
            if (!candidates.isEmpty()) {
                return candidates.get(Util.nextInt(candidates.size()));
            }
        }
        return 0;
    }

    public ItemMap randDoTL(Zone zone, int quantity, int x, int y, long id) {
        short idTempTL, type;
        short[] ao = {555, 557, 559};
        short[] quan = {556, 558, 560};
        short[] gang = {562, 564, 566};
        short[] giay = {563, 565, 567};
        short[] nhan = {561};
        short[] options = {30, 34, 35, 36, 86, 87, 208};
        if (Util.isTrue(10, 100)) {
            idTempTL = nhan[0];
            type = 4; // rada
        } else if (Util.isTrue(30, 100)) {
            idTempTL = gang[Util.nextInt(3)];
            type = 2; // gang
        } else if (Util.isTrue(50, 100)) {
            idTempTL = quan[Util.nextInt(3)];
            type = 1; // quan
        } else if (Util.isTrue(70, 100)) {
            idTempTL = ao[Util.nextInt(3)];
            type = 0; // ao
        } else {
            idTempTL = giay[Util.nextInt(3)];
            type = 3; // giay
        }
        int tiLe = Util.nextInt(100, 115);
        List<ItemOption> itemoptions = new ArrayList<>();
        switch (type) {
            case 0:
                itemoptions.add(new ItemOption(47, Util.nextInt(800, 900) * tiLe / 100));
                break;
            case 1: {
                int chiso = Util.nextInt(46000, 49000) * tiLe / 100;
                itemoptions.add(new ItemOption(22, chiso / 1000));
                itemoptions.add(new ItemOption(27, chiso * 125 / 1000));
                break;
            }
            case 2:
                itemoptions.add(new ItemOption(0, Util.nextInt(4300, 4500) * tiLe / 100));
                break;
            case 3: {
                int chiso = Util.nextInt(46000, 49000) * tiLe / 100;
                itemoptions.add(new ItemOption(23, chiso / 1000));
                itemoptions.add(new ItemOption(28, chiso * 125 / 1000));
                break;
            }
            case 4:
                itemoptions.add(new ItemOption(14, Util.nextInt(14, 17) * tiLe / 100));
                break;
        }
        if (Util.isTrue(90, 100)) {
            itemoptions.add(new ItemOption(options[Util.nextInt(options.length)], 0));
        }
        itemoptions.add(new ItemOption(21, Util.nextInt(15, 20)));
        ItemMap it = new ItemMap(zone, idTempTL, quantity, x, y, id);
        it.options.clear();
        it.options.addAll(itemoptions);
        return it;
    }

    public int getOptionParamItemShop(short id, int optionId) {
        for (Shop shop : Manager.SHOPS) {
            for (TabShop tabShop : shop.tabShops) {
                for (ItemShop itemShop : tabShop.itemShops) {
                    if (itemShop.temp.id != id) {
                        continue;
                    }
                    for (ItemOption itemOption : itemShop.options) {
                        if (itemOption.optionTemplate.id == optionId) {
                            return itemOption.param;
                        }
                    }
                }
            }
        }
        return -1;
    }

    public List<ItemOption> getListOptionItemShop(short id) {
        List<ItemOption> list = new ArrayList<>();
        Manager.SHOPS.forEach(shop -> shop.tabShops.forEach(tabShop -> tabShop.itemShops.forEach(itemShop -> {
            if (itemShop.temp.id == id && list.isEmpty()) {
                list.addAll(itemShop.options);
            }
        })));
        return list;
    }

    public Item getAngelItem(int gender, int type) {
        int tempId = 1048 + type * 3 + gender;
        Item angelItem = createNewItem((short) tempId);
        for (ItemOption io : getListOptionItemShop(14, type == 4 ? 3 : gender, type)) {
            if (io.isOptionCanUpgrade()) {
                int param = (int) (io.param * 1.2);
                angelItem.itemOptions.add(new ItemOption(io.optionTemplate.id, param));
            }
        }
        int param;
        switch (type) {
            case 0:
                param = 62;
                break;
            case 1:
                param = 66;
                break;
            case 2:
                param = 70;
                break;
            case 3:
                param = 64;
                break;
            default:
                param = 68;
                break;
        }
        angelItem.itemOptions.add(new ItemOption(21, param));
        angelItem.itemOptions.add(new ItemOption(30, 0));
        return angelItem;
    }

    public Item otptl(short tempId) {
        return otptl(tempId, 1);
    }

    public Item otphd(short tempId) {
        return otphd(tempId, 1);
    }

    public Item otptl(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 19));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(800, 1200)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 19));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(24, 28)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 19));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(5500, 7800)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 19));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(23, 29)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 19));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(1, 14)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item otphd(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(1200, 2100)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(60, 80)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(8500, 11000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(59, 82)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(5, 18)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public void settlkaio(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{555, 556, 562, 563, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);

            ao.addOptionParam(127, 0);

            ao.addOptionParam(139, 0);

            ao.addOptionParam(30, 0);

            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);

                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được 1 món Thần Linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }

        }
    }

    public void settlgenki(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{555, 556, 562, 563, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(128, 0);

            ao.addOptionParam(140, 0);

            ao.addOptionParam(30, 0);

            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);

                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Thần Linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }

    }

    public void settlson(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{555, 556, 562, 563, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(129, 0);

            ao.addOptionParam(141, 0);

            ao.addOptionParam(30, 0);

            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);

                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Thần Linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void settlpico(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);

            int[] dotl = new int[]{557, 558, 564, 565, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(130, 0);

            ao.addOptionParam(142, 0);

            ao.addOptionParam(30, 0);

            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);

                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Thần Linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void settloctieu(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{557, 558, 564, 565, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(131, 0);
            ao.addOptionParam(143, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 4) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Thần Linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void settlpiko(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{557, 558, 564, 565, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(132, 0);
            ao.addOptionParam(144, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Thần Linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void settlgalick(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{559, 560, 566, 567, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(133, 0);
            ao.addOptionParam(136, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món thần linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void settlcadick(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{559, 560, 566, 567, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(134, 0);
            ao.addOptionParam(137, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Thàn linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void settlnappa(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1703 + i);
            int[] dotl = new int[]{559, 560, 566, 567, 561};

            int ramdom = new Random().nextInt(dotl.length);

            Item ao = ItemService.gI().otptl((short) dotl[ramdom]);
            ao.addOptionParam(135, 0);
            ao.addOptionParam(138, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món thần linh ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdkaio(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{650, 651, 657, 658, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.addOptionParam(127, 0);
            ao.addOptionParam(139, 0);

            ao.addOptionParam(30, 0);

            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);

                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdgenki(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{650, 651, 657, 658, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.addOptionParam(128, 0);
            ao.addOptionParam(140, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdson(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{650, 651, 657, 658, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.addOptionParam(129, 0);
            ao.addOptionParam(141, 0);
            ao.addOptionParam(30, 0);
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdpico(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{652, 653, 659, 660, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.itemOptions.add(new ItemOption(130, 0));
            ao.itemOptions.add(new ItemOption(142, 0));
            ao.itemOptions.add(new ItemOption(30, 0));
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdoctieu(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{652, 653, 659, 660, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.itemOptions.add(new ItemOption(131, 0));
            ao.itemOptions.add(new ItemOption(143, 0));
            ao.itemOptions.add(new ItemOption(30, 0));
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdpiko(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{652, 653, 659, 660, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.itemOptions.add(new ItemOption(132, 0));
            ao.itemOptions.add(new ItemOption(144, 0));
            ao.itemOptions.add(new ItemOption(30, 0));
            if (InventoryService.gI().getCountEmptyBag(player) > 4) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy DIệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdcadick(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{654, 655, 661, 662, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.itemOptions.add(new ItemOption(133, 0));
            ao.itemOptions.add(new ItemOption(136, 0));
            ao.itemOptions.add(new ItemOption(30, 0));
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdcadic(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{654, 655, 661, 662, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.itemOptions.add(new ItemOption(134, 0));
            ao.itemOptions.add(new ItemOption(137, 0));
            ao.itemOptions.add(new ItemOption(30, 0));
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }

    public void sethdnappa(Player player) {
        for (int i = 0; i < 1; i++) {
            Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1704 + i);
            int[] dohd = new int[]{654, 655, 661, 662, 656};

            int ramdom = new Random().nextInt(dohd.length);

            Item ao = ItemService.gI().otphd((short) dohd[ramdom]);
            ao.itemOptions.add(new ItemOption(135, 0));
            ao.itemOptions.add(new ItemOption(138, 0));
            ao.itemOptions.add(new ItemOption(30, 0));
            if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                InventoryService.gI().addItemBag(player, ao);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "Bạn đã nhận được món Hủy Diệt ");
                InventoryService.gI().subQuantityItemsBag(player, hq, 1);
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            }
        }
    }
    // ========== Default option cho đồ Thần Linh ==========

    public List<ItemOption> getDefaultOptionTL(int itemId) {
        List<ItemOption> options = new ArrayList<>();
        int tiLe = Util.nextInt(100, 115);

        switch (itemId) {
            // Áo
            case 555:
                options.add(new ItemOption(47, 800 * tiLe / 100));
                break; // TD
            case 557:
                options.add(new ItemOption(47, 850 * tiLe / 100));
                break; // NM
            case 559:
                options.add(new ItemOption(47, 900 * tiLe / 100));
                break; // XD

            // Quần
            case 556: {
                int cs = 52000 * tiLe / 100;
                options.add(new ItemOption(22, cs / 1000));
                options.add(new ItemOption(27, cs / 20));
                break;
            }
            case 558: {
                int cs = 50000 * tiLe / 100;
                options.add(new ItemOption(22, cs / 1000));
                options.add(new ItemOption(27, cs / 20));
                break;
            }
            case 560: {
                int cs = 48000 * tiLe / 100;
                options.add(new ItemOption(22, cs / 1000));
                options.add(new ItemOption(27, cs / 20));
                break;
            }

            // Găng
            case 562:
                options.add(new ItemOption(0, 4400 * tiLe / 100));
                break;
            case 564:
                options.add(new ItemOption(0, 4300 * tiLe / 100));
                break;
            case 566:
                options.add(new ItemOption(0, 4500 * tiLe / 100));
                break;

            // Giày
            case 563: {
                int cs = 48000 * tiLe / 100;
                options.add(new ItemOption(23, cs / 1000));
                options.add(new ItemOption(28, cs / 20));
                break;
            }
            case 565: {
                int cs = 50000 * tiLe / 100;
                options.add(new ItemOption(23, cs / 1000));
                options.add(new ItemOption(28, cs / 20));
                break;
            }
            case 567: {
                int cs = 46000 * tiLe / 100;
                options.add(new ItemOption(23, cs / 1000));
                options.add(new ItemOption(28, cs * 150 / 1000));
                break;
            }

            // Nhẫn
            case 561:
                options.add(new ItemOption(14, 14 * tiLe / 100));
                break;
        }

        // Random option phụ
        if (Util.isTrue(30, 100)) {
            options.add(new ItemOption(Util.nextInt(86, 87), 0));
        }

        // Yêu cầu sức mạnh
        options.add(new ItemOption(21, Util.nextInt(15, 40)));

        return options;
    }

}
