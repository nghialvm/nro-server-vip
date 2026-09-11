package nro.giftcode;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import jbcd.ConnectDB;
import java.sql.Timestamp;
import java.util.ArrayList;
import models.Item.Item;
import jbcd.CrisResultSet;



public class GiftCodeService {

    private static GiftCodeService i;
    
    private GiftCodeService(){
        
    }
    public String code;
    public int idGiftcode;
    public int gold;
    public int gem;
    public int dayexits;
    public Timestamp timecreate;
    public ArrayList<Item> listItem = new ArrayList<>();
    public static ArrayList<GiftCodeService> gifts = new ArrayList<>();
    public static GiftCodeService gI(){
        if(i == null){
            i = new GiftCodeService();
        }
        return i;
    }
   
    public void giftCode(Player player, String code) throws Exception {
        GiftCode giftcode = GiftCodeManager.gI().CheckCode((int) player.id, code);
        CrisResultSet rs = ConnectDB.executeQuery(
                "SELECT * FROM giftcode_save WHERE `player_id` = " + player.id + " AND `code_da_nhap` = '"+ code + "';");
        if (rs != null && rs.first()) {
            Service.gI().sendThongBaoFromAdmin(player,"|7|- THÔNG BÁO -\n"
                    + "|0|Giftcode : " + code + "\n"
                    + "|6|Bạn đã nhập Giftcode này vào lúc : " + rs.getTimestamp("tgian_nhap"));
            return;
        } else {
            rs.dispose();
            rs = ConnectDB.executeQuery("SELECT * FROM `giftcode` WHERE `code` = '"+ code + "';");
            if (rs != null && rs.first()) {
                int count = rs.getInt("count_left");
            if (count < 1) {
                Service.gI().sendThongBaoFromAdmin(player, "|7|- THÔNG BÁO -\n"
                        + "|0|Giftcode : " + code + "\n"
                        + "|6|Đã hết lượt nhập, vui lòng quay lại sau!");
                return;
            }}}
            if (giftcode == null) {
                Service.gI().sendThongBaoFromAdmin(player,"|7|- THÔNG BÁO -\n"
                        + "|6|Giftcode vừa nhập không tồn tại trong hệ thống!");
            } else if (giftcode.timeCode()) {
                Service.gI().sendThongBaoFromAdmin(player,"|7|- THÔNG BÁO -\n" 
                        + "|0|Giftcode : " + code + "\n"
                        + "|6|Giftcode này đã hết hạn!");
            } else if (InventoryService.gI().getCountEmptyBag(player) < giftcode.detail.size()) {
                Service.gI().sendThongBaoFromAdmin(player, "|7|- THÔNG BÁO -\n" 
                        + "|0|Giftcode : " + code + "\n"
                        + "|6|Cần trống " + giftcode.detail.size() + " ô hành trang để nhập!");
            } else {
                InventoryService.gI().addItemGiftCodeToPlayer(player,giftcode,code);
        } 
    }
}
