package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.server.ServerManager;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstAttribute;
import consts.ConstNpc;
import event.EventManager;
import java.util.ArrayList;
import java.util.List;
import jbcd.dao.EventDAO;
import models.Item.Item;
import models.Item.ItemService;
import nro.attribute.Attribute;
import nro.npc.Npc;
import nro.player.Player;

public class ChiChi extends Npc {

    public ChiChi(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    
    int TIME_12_HOUS = 43200;
    int TIME_24_HOUS = 86400;
    int TIME_36_HOUS = 129600;
    int TIME_48_HOUS = 172800;
    int TIME_60_HOUS = 216000;
    
    private void applyGlobalExpBuff(Attribute at, int point) {
        int rewardStage = 0;
        int value = 0;
        int time = 0;

        if (point >= 5000) {
            rewardStage = 5;
            value = 400;
            time = TIME_60_HOUS;
        } else if (point >= 1500) {
            rewardStage = 4;
            value = 300;
            time = TIME_48_HOUS;
        } else if (point >= 500) {
            rewardStage = 3;
            value = 200;
            time = TIME_36_HOUS;
        } else if (point >= 200) {
            rewardStage = 2;
            value = 100;
            time = TIME_24_HOUS;
        } else if (point >= 100) {
            rewardStage = 1;
            value = 100;
            time = TIME_12_HOUS;
        }

        // Chỉ kích hoạt nếu vượt mốc mới
        if (rewardStage > EventDAO.getLAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY()) {
            EventDAO.setLAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY(rewardStage);
            at.setValue(value);
            at.setTime(time);
        }
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            Item BongHoaHong = InventoryService.gI().findItemBag(player, 1530);
            List<String> menuList = new ArrayList<>();

            if (EventManager.NEW_SEVER) {
                menuList.add("Nhận Quà\nĐặc Biệt");
            } else {
                menuList.add("Cửa Hàng");
            }

            if (EventManager.INTERNATIONAL_WOMANS_DAY) {
                menuList.add("Tặng 1\bBông Hoa\nĐang có: " + (BongHoaHong != null ? BongHoaHong.quantity : 0));
            }

            menuList.add("Đóng");

            String[] menus = menuList.toArray(String[]::new);
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn muốn hỏi chi ?", menus);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                if (EventManager.NEW_SEVER) {
                    switch (select) {
                        case 0:
                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                Service.gI().sendThongBao(player, "Hành trang của bạn không đủ ô trống.");
                                return;
                            }
                            Item Caitrang = ItemService.gI().createNewItem((short) 1201);
                            Caitrang.addOptionParam(50, 17);
                            Caitrang.addOptionParam(77, 17);
                            Caitrang.addOptionParam(103, 17);
                            Caitrang.addOptionParam(14, 5);
                            Caitrang.addOptionParam(80, 10);
                            Caitrang.addOptionParam(101, 20);
                            Caitrang.addOptionParam(114, 20);
                            InventoryService.gI().addItemBag(player, Caitrang);
                            Service.gI().sendThongBao(player, "Bạn nhận được " + Caitrang.Name());
                            InventoryService.gI().sendItemBag(player);
                            break;
                        case 1:
                            TangHoaHong(player);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (select) {
                        case 0:
                            //Shop
                            break;
                        case 1:
                            TangHoaHong(player);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    public void TangHoaHong(Player player) {
        if (!EventManager.INTERNATIONAL_WOMANS_DAY) {
            return;
        }

        // Kiểm tra hành trang
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
            return;
        }

        // Kiểm tra item
        Item hoaHong = InventoryService.gI().findItemBag(player, 1530);
        if (hoaHong == null || hoaHong.quantity < 1) {
            Service.gI().sendThongBao(player, "Bạn không có Bông hoa hồng.");
            return;
        }

        // Trừ item
        InventoryService.gI().subQuantityItemsBag(player, hoaHong, 1);

        // Cập nhật exp toàn server
        Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
        applyGlobalExpBuff(at, EventDAO.RACE_INTERNATIONAL_WOMENS_DAY_EVENT);

        // Cập nhật top
        player.DuaTopTangBongHoaHong += 1;
        EventDAO.RACE_INTERNATIONAL_WOMENS_DAY_EVENT += 1;

        // Gửi lời chúc
        Service.gI().chat(player, "Chúc bạn 8-3 vui vẻ");

        // Tính phần thưởng
        boolean nhanVang = Util.isTrue(50, 100);
        int soVang = 0;
        int soRuby = 0;

        if (nhanVang) {
            soVang = Util.nextInt(10, 50) * 1_000_000;
            player.inventory.addGold(soVang);
        } else {
            soRuby = Util.nextInt(1, 5);
            player.inventory.addRuby(soRuby);
        }

        // NPC phản hồi
        npcChat(player, "Ồ quý hoá quá, ta tặng lại món quà, hãy nhận lấy.");

        // Thông báo phần thưởng
        int finalSoVang = soVang;
        int finalSoRuby = soRuby;
        boolean finalNhanVang = nhanVang;
        
        player.event.addInternationalWomensDayPoint(1);

        if (finalNhanVang) {
            Service.gI().sendThongBao(player, "Bạn nhận được " + Util.formatNumber(finalSoVang, FormatStyle.VIETNAMESE) + " vàng");
        } else {
            Service.gI().sendThongBao(player, "Bạn nhận được " + finalSoRuby + " hồng ngọc");
        }

        Service.gI().sendMoney(player);
        EventDAO.save();
    }
}
