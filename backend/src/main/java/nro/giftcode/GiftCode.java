package nro.giftcode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import models.Item.ItemOption;



public class GiftCode {
    
    public String code;
    public int id;
    public int countLeft;
    public HashMap<Integer, Integer> detail = new HashMap<>();
    public ArrayList<Integer> listIdPlayer = new ArrayList<>();
    public ArrayList<ItemOption> option = new ArrayList<>();
    public Timestamp datecreate;
    public Timestamp dateexpired;
    
    public boolean isUsedGiftCode(int idPlayer) {
        return listIdPlayer.contains(idPlayer);
    }

    public void addPlayerUsed(int idPlayer) {
        listIdPlayer.add(idPlayer);
    }
    
    public boolean timeCode() {
        if (this.datecreate == null || this.dateexpired == null) {
            return false;
        }
        return this.datecreate.getTime() > this.dateexpired.getTime();
    }
}






