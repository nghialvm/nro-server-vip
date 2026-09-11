package nro.dragon.ChristMasEvent;

import Data.DataGame;
import nro.inventory.InventoryService;
import nro.server.Client;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstPlayer;
import lombok.Getter;
import lombok.Setter;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import network.io.Message;
import nro.intrinsic.IntrinsicService;
import nro.map.Zone;
import nro.player.Player;

public class ShenronChristMasEvent {

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
    public static int timeResummonShenron = 60000;
    public static int timeShenronWait = 60000;

    public static final String SHENRONEVENT_SAY
            = "Ta sẽ ban cho người 1 điều ước, ngươi có 5 phút, hãy chọn đi:\n"
            + "1) Đổi kỹ năng 3 và 4 của đệ tử\n"
            + "(Lưu ý: kỹ năng mới có cấp 1 và vẫn có thể trùng lại với kỹ năng vốn có).\n"
            + "2) Thay đổi nội tại.\n"
            + "3) Cải trang siêu thần hạn dùng 90 ngày.\n"
            + "4) Cải trang Black Gohan Rose HSD 90 ngày.\n"
            + "5) Đeo lưng kẹo Noel hạn sử dụng hoặc vĩnh viễn.";

    public static final String[] SHENRON_WISHES
            = new String[]{"Điều ước 1", "Điều ước 2", "Điều ước 3", "Điều ước 4", "Điều ước 5"};

    public boolean shenronLeave;

    public void update() {
        try {
            if (!shenronLeave) {
                if (isPlayerDisconnect) {
                    Player pl = Client.gI().getPlayerByID(playerId);
                    if (pl != null) {
                        player = pl;
                        if (MapService.gI().isMapCallDragon(player.zone.map.mapId)) {
                            player.shenronEvent_Christmas = this;
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
        NpcService.gI().createMenuRongThieng_Event(player, ConstNpc.SHOW_SHENRON_EVENT_CONFIRM_CHRISTMAS, SHENRONEVENT_SAY, SHENRON_WISHES);
    }

    public void showConfirmShenron(byte select) {
        this.select = select;
        String wish = null;
        switch (player.iDMark.getShenronType()) {
            case 0:
                wish = SHENRON_WISHES[select];
                break;
        }
        NpcService.gI().createMenuRongThieng_Event(player, ConstNpc.SHENRON_EVENT_CONFIRM_CHRISTMAS, "Ngươi có chắc muốn ước?", wish, "Từ chối");
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
                DataGame.sendEffectTemplate(player.getSession(), 25, 59);
                playerId = player.id;
                shenronType = player.iDMark.getShenronType();
                zone.shenronType = shenronType;
                lastTimeShenronWait = System.currentTimeMillis();
                player.isShenronAppear_Christmas = true;
            }
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
        }
    }

    public void confirmWish() {
        switch (player.iDMark.getShenronType()) {
            case 0:
                switch (this.select) {
                     case 0: //thay chiêu 3-4 đệ tử
                        if (player.Detu != null) {
                            if (player.Detu.playerSkill.skills.get(2).skillId != -1) {
                                player.Detu.openSkill3();
                                if (player.Detu.playerSkill.skills.get(3).skillId != -1) {
                                    player.Detu.openSkill4();
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");
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
                        if (player.getSession().player.nPoint.power >= 10_000_000_000L) {
                            IntrinsicService.gI().DoiNoiTai(player);
                        } else {
                            Service.gI().sendThongBao(player, "10Tỷ Sức Mạnh?");
                            sendWhishesShenron();
                            return;
                        }
                        break;
                    case 2:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            byte gender = this.player.gender;
                            Item avtVip = ItemService.gI().createNewItem((short) (gender == ConstPlayer.TRAI_DAT ? 905
                                    : gender == ConstPlayer.NAMEC ? 907 : 911));
                            avtVip.itemOptions.add(new ItemOption(50, 22));
                            avtVip.itemOptions.add(new ItemOption(47, 400));
                            avtVip.itemOptions.add(new ItemOption(108, 30));
                            avtVip.itemOptions.add(new ItemOption(33, 1));
                            avtVip.itemOptions.add(new ItemOption(93, 90));
                            InventoryService.gI().addItemBag(player, avtVip);
                            InventoryService.gI().sendItemBag(player);
                        } else {
                            Service.gI().sendThongBao(player, "Hành trang đã đầy");
                            reSummonShenron();
                            return;
                        }
                        break;
                    case 3:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            byte gender = this.player.gender;
                            Item avtVip = ItemService.gI().createNewItem((short) (gender == ConstPlayer.TRAI_DAT ? 883
                                    : gender == ConstPlayer.NAMEC ? 883 : 883));
                            avtVip.itemOptions.add(new ItemOption(50, 24));
                            avtVip.itemOptions.add(new ItemOption(14, 3));
                            avtVip.itemOptions.add(new ItemOption(103, 19));
                            avtVip.itemOptions.add(new ItemOption(80, 10));
                            avtVip.itemOptions.add(new ItemOption(93, 90));
                            InventoryService.gI().addItemBag(player, avtVip);
                            InventoryService.gI().sendItemBag(player);
                        } else {
                            Service.gI().sendThongBao(player, "Hành trang đã đầy");
                            reSummonShenron();
                            return;
                        }
                        break;
                    case 4:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            Item keonoel = ItemService.gI().createNewItem((short) 1841);
                            keonoel.itemOptions.add(new ItemOption(50, Util.nextInt(15, 17)));
                            keonoel.itemOptions.add(new ItemOption(77, Util.nextInt(15, 17)));
                            keonoel.itemOptions.add(new ItemOption(103, Util.nextInt(15, 17)));
                            keonoel.itemOptions.add(new ItemOption(14, 15));
                            keonoel.itemOptions.add(new ItemOption(106, 0));
                            if (Util.isTrue(80, 100)) {
                                keonoel.itemOptions.add(new ItemOption(93, 90));
                            }
                            InventoryService.gI().addItemBag(player, keonoel);
                            InventoryService.gI().sendItemBag(player);
                        } else {
                            Service.gI().sendThongBao(player, "Hành trang đã đầy");
                            reSummonShenron();
                            return;
                        }
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
                player.shenronEvent_Christmas = null;
                if (!leaveMap) {
                    NpcService.gI().createTutorial(player, -1, "Điều ước của ngươi đã được thực hiện...tạm biệt");
                }
                activeShenron(false, DRAGON_EVENT);
                player.isShenronAppear_Christmas = false;
                select = -1;
            }
            zone.shenronType = -1;
            player.lastTimeShenronAppeared_Christmas = System.currentTimeMillis();
            ShenronChristMasEventManager.gI().remove(this);
        }
    }
}
