package models.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import nro.combine.CombineUtil;
import nro.template.ItemTemplate;

public class Item {

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public int quantityGD = 0;

    public List<ItemOption> itemOptions;

    public long createTime;

    public boolean isNotNullItem() {
        return this.template != null;
    }

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public Item(short itemId) {
        this.template = ItemService.gI().getTemplate(itemId);
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String Name() {
        return this.template.name;
    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString();
        }
        return strInfo;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

    public void dispose() {
        this.template = null;
        this.info = null;
        this.content = null;
        if (this.itemOptions != null) {
            for (ItemOption io : this.itemOptions) {
                io.dispose();
            }
            this.itemOptions.clear();
        }
        this.itemOptions = null;
    }

    public boolean isBUg() {
        for (ItemOption itemOption : itemOptions) {
            if ((itemOption.optionTemplate.id != 249 && (itemOption.optionTemplate.id == 50 || itemOption.optionTemplate.id == 77 || itemOption.optionTemplate.id == 103 || itemOption.optionTemplate.id == 5))
                    && itemOption.param > 35) {

                return true;
            }
        }
        return false;
    }

    public boolean isSKH() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135) {
                return true;
            }
        }
        return false;
    }

    public boolean isDTS() {
        return this.template.level == 15;
    }

    public boolean isDTL() {
        return this.template.level == 13;
    }

    public boolean isDHD() {
        return this.template.level == 14;
    }
    public boolean isDHD1() {
        if (this.template.id >= 650 && this.template.id <= 662) {
            return true;
        }
        return false;
    }
      public boolean isDTLNew() {
       if (this.template.id >= 555 && this.template.id <= 567) {
            return true;
             }
        return false;
    }

    public boolean isManhThienSu() {
        return this.template.id >= 1066 && this.template.id <= 1070;
    }

    public boolean isDaMayMan() {
        return this.template.id >= 1079 && this.template.id <= 1083;
    }

    public boolean isDaNangCapTS() {
        return this.template.id >= 1074 && this.template.id <= 1078;
    }

    public boolean isCongThuc() {
        return this.template.id >= 1071 && this.template.id <= 1073;
    }

    public boolean isCongThucVip() {
        return this.template.id >= 1084 && this.template.id <= 1086;
    }

    public boolean isDaNangCap() {
        return this.template.type == 14;
    }

    public String typeName() {
        switch (this.template.type) {
            case 0:
                return "Áo";
            case 1:
                return "Quần";
            case 2:
                return "Găng";
            case 3:
                return "Giày";
            case 4:
                return "Rada";
            default:
                return "";
        }
    }

    public String getGenderName() {
        return template.gender == 0 ? "Trái Đất" : template.gender == 1 ? "Namếc" : "Xay da";
    }

    public byte typeManh() {
        switch (this.template.id) {
            case 1066:
                return 0;
            case 1067:
                return 1;
            case 1070:
                return 2;
            case 1068:
                return 3;
            case 1069:
                return 4;
            default:
                return -1;
        }
    }

    public boolean isSachTuyetKy() {
        return template.id == 1044 || template.id == 1211 || template.id == 1212;
    }

    public boolean isSachTuyetKy2() {
        return template.id >= 1278 && template.id <= 1280;
    }

    public boolean canNangCapWithNDC(Item daNangCap) {
        if (this.template.type == 0 && daNangCap.template.id == 223) {
            return true;
        } else if (this.template.type == 1 && daNangCap.template.id == 222) {
            return true;
        } else if (this.template.type == 2 && daNangCap.template.id == 224) {
            return true;
        } else if (this.template.type == 3 && daNangCap.template.id == 221) {
            return true;
        } else {
            return this.template.type == 4 && daNangCap.template.id == 220;
        }
    }

    public boolean isDaPhaLeEpSao() {
        return template != null && (template.type == 30 || (template.id >= 14 && template.id <= 20));
    }

    public boolean isDaPhaLeC1() {
        return template != null && template.id >= 411 && template.id <= 447;
    }

    public boolean isDaPhaLeC2() {
        return template != null && template.id >= 1416 && template.id <= 1422 || template.id == 964 || template.id == 965;
    }

    public boolean isDaPhaLeMoi() {
        return template != null && template.id >= 1416 && template.id <= 1422 || template.id == 964 || template.id == 965
                || template.id >= 1426 && template.id <= 1434;
    }

    public boolean isDaPhaLeCu() {
        return template != null && template.id >= 441 && template.id <= 447;
    }

    public boolean isTypeBody() {
        return template != null && (0 <= template.type && template.type < 6) || template.type == 32 || template.type == 35 || template.type == 11 || template.type == 23;
    }

    public boolean isHaveOption(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                return true;
            }
        }
        return false;
    }

    public int getPercentOption() {
        int percent = 0;
        switch (this.template.type) {
            case 0: {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 47);
                int param = CombineUtil.reversePoint(getOptionParam(47), getOptionParam(72));
                percent = (param * 100) / paramZin;
                break;
            }
            case 1: {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 6);
                int param = CombineUtil.reversePoint(getOptionParam(6), getOptionParam(72));
                percent = (param * 100) / paramZin;
                break;
            }
            case 2: {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 0);
                int param = CombineUtil.reversePoint(getOptionParam(0), getOptionParam(72));
                percent = (param * 100) / paramZin;
                break;
            }
            case 3: {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 7);
                int param = CombineUtil.reversePoint(getOptionParam(7), getOptionParam(72));
                percent = (param * 100) / paramZin;
                break;
            }
            case 4: {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 14);
                int param = CombineUtil.reversePoint(getOptionParam(14), getOptionParam(72));
                percent = (param * 100) / paramZin;
                break;
            }
        }
        return percent;
    }

    public int getOptionParam(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                return itemOption.param;
            }
        }
        return 0;
    }

    public void addOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param += param;
                return;
            }
        }
        this.itemOptions.add(new ItemOption(id, param));
    }

    public void getOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param = param;
                return;
            }
        }
    }

    public void subOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param -= param;
                return;
            }
        }
    }

    public void subOptionParamAndRemoveIfZero(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param -= param;
                if (param <= 0) {
                    this.itemOptions.remove(i);
                }
                break;
            }
        }
    }

    public void removeOption(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                this.itemOptions.remove(i);
                break;
            }
        }
    }

    public ItemOption getOptionDaPhaLe() {
        switch (template.id) {
            case 20:
                return new ItemOption(77, 5);
            case 19:
                return new ItemOption(103, 5);
            case 18:
                return new ItemOption(80, 5);
            case 17:
                return new ItemOption(81, 5);
            case 16:
                return new ItemOption(50, 3);
            case 15:
                return new ItemOption(94, 2);
            case 14:
                return new ItemOption(108, 2);
            case 441:
                return new ItemOption(95, 5);
            case 442:
                return new ItemOption(96, 5);
            case 443:
                return new ItemOption(97, 5);
            case 444:
                return new ItemOption(98, 5);
            case 445:
                return new ItemOption(99, 5);
            case 446:
                return new ItemOption(100, 5);
            case 447:
                return new ItemOption(101, 5);
            case 1416:
                return new ItemOption(95, 5);
            case 1417:
                return new ItemOption(96, 5);
            case 1418:
                return new ItemOption(97, 5);
            case 1419:
                return new ItemOption(98, 5);
            case 1420:
                return new ItemOption(99, 5);
            case 1421:
                return new ItemOption(100, 5);
            case 1422:
                return new ItemOption(101, 5);
            case 1426:
                return new ItemOption(95, 5);
            case 1427:
                return new ItemOption(96, 5);
            case 1428:
                return new ItemOption(97, 5);
            case 1429:
                return new ItemOption(98, 5);
            case 1430:
                return new ItemOption(99, 5);
            case 1431:
                return new ItemOption(100, 5);
            case 1432:
                return new ItemOption(101, 5);
            case 1433:
                return new ItemOption(153, 5);
            case 1434:
                return new ItemOption(160, 5);
            default:
                return itemOptions.get(0);
        }
    }

    public String getOptionInfo(Item item) {
        boolean haveOption = false;
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        ItemOption iodpl = item.getOptionDaPhaLe();
        for (ItemOption io : itC.itemOptions) {
            if (!haveOption && io.optionTemplate.id == iodpl.optionTemplate.id) {
                io.param += iodpl.param;
                haveOption = true;
            }
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107) {
                optionInfo.add(io.getOptionString());
            }
        }
        if (!haveOption) {
            optionInfo.add(iodpl.getOptionString());
        }
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfoCuongHoa(Item item) {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        ItemOption iodpl = item.getOptionDaPhaLe();
        for (ItemOption io : itC.itemOptions) {
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        optionInfo.add(iodpl.getOptionString());
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfoChuyenHoa(Item item, int level) {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        int percent = item.getPercentOption();
        for (ItemOption io : itC.itemOptions) {
            if (io.isOptionCanUpgrade()) {
                io.param = CombineUtil.pointUp(io.param * percent / 100, level);
            }
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        for (ItemOption io : item.itemOptions) {
            if (!io.isOptionCanUpgrade() && io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfo() {
        StringJoiner optionInfo = new StringJoiner("\n");
        for (ItemOption io : this.itemOptions) {
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public String getOptionInfoUpgrade() {
        StringJoiner optionInfo = new StringJoiner("\n");
        for (ItemOption io : this.itemOptions) {
            if (io.isOptionCanUpgrade() || io.optionTemplate.id == 21 || io.param == 30 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public boolean haveOption(int idOption) {
        if (this != null && this.isNotNullItem()) {
            return this.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == idOption);
        }
        return false;
    }

    private static final Set<Integer> KICH_HOAT_IDS = Set.of(127, 128, 129, 130, 131, 132, 133, 134, 135);

    public boolean haveSetKichHoat() {
        if (this != null && this.isNotNullItem()) {
            return this.itemOptions.stream().anyMatch(op -> op != null && KICH_HOAT_IDS.contains(op.optionTemplate.id)
            );
        }
        return false;
    }

    public boolean isTrangBiPSH() {
        return this.template.type == 21 || this.template.type == 11 || this.template.type == 23;
    }

    public boolean isTrangBiHSD() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id == 93 && itemOption.param >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isTrangBiKhoaGd() {
        return this.template.type == 27;
    }

    public boolean isTrangBiHacHoa() {
        return this.template.type <= 5 || this.template.type == 32 || this.template.type == 21 || this.template.type == 23 || this.template.type == 23 || this.template.type == 11 || this.template.type == 72;
    }

    public String getOptionInfoUpgradeFinal() {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item clone = this.cloneItem();
        for (ItemOption io : clone.itemOptions) {
            if (io.isOptionCanUpgrade()) {
                io.param = CombineUtil.pointUp(io.param, 1);
            }
            if (io.isOptionCanUpgrade() || io.param == 30) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public boolean canPhaLeHoa() {
        return this.template != null && (this.template.type < 5 || this.template.type == 32);
    }

    public boolean isDoKyGui() {
        return this.template != null && (this.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 86) || this.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 87)
                || this.template.type == 14 || this.template.type == 15 || this.template.type == 6 || this.template.id >= 14 && this.template.id <= 20);
    }

    public boolean isItemCollection() {
        return this.template != null && (this.template.type == 5 || this.template.type == 11 || this.template.type == 21 || this.template.type == 23);
    }

    public boolean isItemTemplateToPet() {
        return this.template != null && (this.template.id == 547 || this.template.id == 548 || this.template.id == 932 || this.template.id == 1302 || this.template.id == 1371
                || this.template.id == 1380 || this.template.id == 1381);
    }

    public boolean isItemTemplateToPlayer() {
        return this.template != null && (this.template.id >= 544 && this.template.id <= 546);
    }

    public Item cloneItem() {
        Item item = new Item();
        item.itemOptions = new ArrayList<>();
        item.template = this.template;
        item.info = this.info;
        item.content = this.content;
        item.quantity = this.quantity;
        item.createTime = this.createTime;
        for (ItemOption io : this.itemOptions) {
            item.itemOptions.add(new ItemOption(io));
        }
        return item;
    }

    public void setOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param = param;
                return;
            }
        }
        // nếu chưa có thì thêm mới
        this.itemOptions.add(new ItemOption(id, param));
    }

}
