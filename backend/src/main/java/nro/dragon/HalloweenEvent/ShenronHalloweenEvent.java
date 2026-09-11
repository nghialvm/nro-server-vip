package nro.dragon.HalloweenEvent;



import Data.DataGame;
import nro.inventory.InventoryService;
import nro.server.Client;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import lombok.Getter;
import lombok.Setter;
import models.Item.Item;
import models.Item.ItemService;
import models.Item.ItemTimeService;
import network.io.Message;
import nro.map.Zone;
import nro.player.Player;

public class ShenronHalloweenEvent {

    @Setter
    @Getter
    private Player player;

    @Setter
    @Getter
    private Zone zone;

    public long playerId;
    public boolean isPlayerDisconnect;
    public byte select;
    public int shenronType;
    public boolean leaveMap;

    public static final byte WISHED = 0;
    public static final byte TIME_UP = 1;

    public static final byte DRAGON_EVENT = 1;

    public long lastTimeShenronWait;
    public static int timeResummonShenron = 300000;
    public static int timeShenronWait = 300000;

    public static final String SHENRON_SAY = "Ta sẽ ban cho người 1 điều ước, ngươi có 5 phút, hãy chọn đi:\n"
            + "1) Đổi cả 3 kĩ năng đầu của đệ tử\n"
            + "(Lưu ý: Kĩ năng mới có cấp 1 và vẫn có thể trùng lại với kỹ năng vốn có).\n"
            + "2) X3 tiềm năng sức mạnh đệ tử 30 phút.\n"
            + "3) Tăng 15% HP, KI, SD trong 30 phút.\n"
            + "4) Tiềm năng sức mạnh X3 cho bản thân 30 phút.\n"
            + "5) Giảm 100tr sức mạnh của bản thân (yêu cầu trên 40 tỷ sức mạnh).\n"
            + "6) Pet chó địa ngục hạn sử dụng 15 đến 90 ngày.\n"
            + "7) Phiếu giảm giá 80% (không có phiếu nào mới ước được).";

    public static final String[] SHENRON_WISHES = new String[]{"Điều\nước 1", "Điều\nước 2", "Điều\nước 3", "Điều\nước 4", "Điều\nước 5", "Điều\nước 6", "Điều\nước 7"};

    public boolean shenronLeave;

    public void update() {
        try {
            if (!shenronLeave) {
                if (isPlayerDisconnect) {
                    Player pl = Client.gI().getPlayerByID(playerId);
                    if (pl != null) {
                        player = pl;
                        if (MapService.gI().isMapCallDragon(player.zone.map.mapId)) {
                            player.shenronEvent_Halloween = this;
                            zone = player.zone;
                            player.iDMark.setShenronType(shenronType);
                            isPlayerDisconnect = false;
                            reSummonShenron();
                        }
                    }
                }
                if (Util.canDoWithTime(lastTimeShenronWait, timeShenronWait)) {
                    leaveMap = true;
                    NpcService.gI().createMenuRongThieng_Event(player, ConstNpc.IGNORE_MENU, 
                            "Ngươi lề mề quá, ta còn phải đi đáp nguyện mong muốn của các cư dân khác nữa, tạm biệt...!", "Đóng");
                    shenronLeave();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reSummonShenron() {
        activeShenron(true, DRAGON_EVENT);
        sendWhishesShenron();
    }

    public void sendWhishesShenron() {
        NpcService.gI().createMenuRongThieng_Event(player, ConstNpc.SHOW_SHENRON_EVENT_CONFIRM_HALLOWEEN, SHENRON_SAY, SHENRON_WISHES);
    }

    public void showConfirmShenron(byte select) {
        this.select = select;
        String wish = null;
        switch (player.iDMark.getShenronType()) {
            case 0:
                wish = SHENRON_WISHES[select];
                break;
        }
        NpcService.gI().createMenuRongThieng_Event(player, ConstNpc.SHENRON_EVENT_CONFIRM_HALLOWEEN, "Ngươi có chắc muốn ước?", wish, "Từ chối");
    }

    public void activeShenron(boolean appear, byte type) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(appear ? 0 : (byte) 1);
            if (appear) {
                msg.writer().writeShort(player.zone.map.mapId);
                msg.writer().writeShort(player.zone.map.bgId);
                msg.writer().writeByte(player.zone.zoneId);
                msg.writer().writeInt((int) player.id);
                msg.writer().writeUTF("MaiTienDung");
                msg.writer().writeShort(player.location.x);
                msg.writer().writeShort(player.location.y);
                msg.writer().writeByte(type);
                DataGame.sendEffectTemplate(player.getSession(), 25, 51);
                playerId = player.id;
                shenronType = player.iDMark.getShenronType();
                zone.shenronType = shenronType;
                lastTimeShenronWait = System.currentTimeMillis();
                player.isShenronAppear_Halloween = true;
            }
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
        }
    }

    public void confirmWish() {
        switch (player.iDMark.getShenronType()) {
            case 0:
                switch (this.select) {
                    case 0: //thay chiêu 1-3 đệ tử
                        if (player.Detu != null) {
                            if (player.Detu.playerSkill.skills.get(0).skillId != -1) {
                                player.Detu.openSkill1();
                            }
                            if (player.Detu.playerSkill.skills.get(1).skillId != -1) {
                                player.Detu.openSkill2();
                                if (player.Detu.playerSkill.skills.get(2).skillId != -1) {
                                    player.Detu.openSkill3();
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");
                                sendWhishesShenron();
                                return;
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                            sendWhishesShenron();
                            return;
                        }
                        break;
                    case 1:
                        if (player.Detu != null) {
                            if (player.itemTime.LastTimeRongXuong > System.currentTimeMillis()) {
                                Service.gI().sendThongBao(player, "Ngươi có hiệu ứng này rồi mà!");
                                sendWhishesShenron();
                                return;
                            }
                            player.itemTime.LastTimeRongXuong = System.currentTimeMillis();
                            player.itemTime.isRongXuong = true;
                            ItemTimeService.gI().sendAllItemTime(player);
                            Service.gI().point(player);
                        } else {
                            Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                            sendWhishesShenron();
                            return;
                        }
                        break;
                    case 2:// Tăng hp, ki, sd
                        if (player.itemTime.LastTimeRongXuong_2 > System.currentTimeMillis()) {
                            Service.gI().sendThongBao(player, "Ngươi có hiệu ứng này rồi mà!");
                            sendWhishesShenron();
                            return;
                        }
                        player.itemTime.LastTimeRongXuong_2 = System.currentTimeMillis();
                        player.itemTime.isRongXuong_2 = true;
                        ItemTimeService.gI().sendAllItemTime(player);
                        Service.gI().point(player);                            
                        break;
                    case 3:
                        if (player.itemTime.LastTimeRongXuong_3 > System.currentTimeMillis()) {
                            Service.gI().sendThongBao(player, "Ngươi có hiệu ứng này rồi mà!");
                            sendWhishesShenron();
                            return;
                        }
                        player.itemTime.LastTimeRongXuong_3 = System.currentTimeMillis();
                        player.itemTime.isRongXuong_3 = true;
                        ItemTimeService.gI().sendAllItemTime(player);
                        Service.gI().point(player);                            
                        break;
                    case 4:
                        if (player.nPoint.power < 40_000_000_000L) {
                            Service.gI().sendThongBao(player, "Ngươi không đủ sức mạnh để thực hiện!");
                            sendWhishesShenron();
                            return;
                        }
                        int SubPower = 100_000_000;
                        player.nPoint.subSucManh(SubPower);
                        PlayerService.gI().sendSubTNSM(player, -SubPower);
                        Service.gI().point(player);
                        break;
                    case 5:
                        Item Petceberus = ItemService.gI().createNewItem((short) 1654, 1);
                        Petceberus.addOptionParam(50, Util.nextInt(15, 17));
                        Petceberus.addOptionParam(77, Util.nextInt(15, 17));
                        Petceberus.addOptionParam(103, Util.nextInt(15, 17));
                        Petceberus.addOptionParam(14, 11);
                        Petceberus.addOptionParam(106, 0);
                        Petceberus.addOptionParam(93, Util.nextInt(15, 90));
                        InventoryService.gI().addItemBag(player, Petceberus);
                        InventoryService.gI().sendItemBag(player);
                        break;
                    case 6:
                        if (InventoryService.gI().findItemPhieuGiamGiaThuong(player)) {
                            Service.gI().sendThongBao(player, "Ngươi có phiếu giảm giá rồi mà!");
                            sendWhishesShenron();
                            return;
                        }
                        Item Pgg = ItemService.gI().createNewItem((short) 459, 1);
                        Pgg.addOptionParam(112, 80);
                        Pgg.addOptionParam(93, 90);
                        Pgg.addOptionParam(30, 0);
                        InventoryService.gI().addItemBag(player, Pgg);
                        InventoryService.gI().sendItemBag(player);
                        break;
                }
                break;
        }
        shenronLeave();
    }

    public void shenronLeave() {
        if (!shenronLeave) {
            shenronLeave = true;
            if (player != null && player.zone != null) {
                player.shenronEvent_Halloween = null;
                if (!leaveMap) {
                    NpcService.gI().createTutorial(player, 0, "Điều ước của ngươi đã được thực hiện...tạm biệt");
                }
                activeShenron(false, DRAGON_EVENT);
                player.isShenronAppear_Halloween = false;
                select = -1;
            }
            zone.shenronType = -1;
            player.lastTimeShenronAppeared_Halloween = System.currentTimeMillis();
            ShenronHalloweenEventManager.gI().remove(this);
        }
    }
}
