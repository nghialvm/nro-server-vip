package models.Item;

import Utils.Util;
import nro.template.ItemOptionTemplate;



public class ItemOption {

    public int param;

    public ItemOptionTemplate optionTemplate;

    public ItemOption() {
    }

    public ItemOption(ItemOption io) {
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }

    public ItemOption(int tempId, int param) {
        this.optionTemplate = ItemService.gI().getItemOptionTemplate(tempId);
        this.param = param;
    }

    public ItemOption(ItemOptionTemplate temp, int param) {
        this.optionTemplate = temp;
        this.param = param;
    }

    public String getOptionString() {
        return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
    }

    public boolean isOptionCanUpgrade() {
        int opId = this.optionTemplate.id;
        return opId == 0 || opId == 6 || opId == 7 || opId == 14 || opId == 22 || opId == 23 || opId == 27 || opId == 28 || opId == 47;
    }
    
    public boolean haveExpiryDate() {
        return optionTemplate.id == 93 || optionTemplate.id == 260 || optionTemplate.id == 261;
    }

    public void dispose() {
        this.optionTemplate = null;
    }

    @Override
    public String toString() {
        final String n = "\"";
        return "{"
                + n + "id" + n + ":" + n + optionTemplate.id + n + ","
                + n + "param" + n + ":" + n + param + n
                + "}";
    }
}






