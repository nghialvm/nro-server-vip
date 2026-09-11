package nro.tambao;

import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jbcd.data.GodGK;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import network.io.Message;
import nro.player.Player;

public class TamBaoService {

    public static TamBaoService instance;

    public static TamBaoService gI() {
        if (instance == null) {
            instance = new TamBaoService();
        }
        return instance;
    }

    public static List<Item> listItem = new ArrayList<>();
    public static List<Item> listItemVip = new ArrayList<>();
    public static int VAN_MAY = 100;

    public static void loadItem() {
        int[] itemthg = {
            16, 17, 18, 19, 20, // type 12
            16, 17, 18, 19, 20, // type 12
            77, 76, 77, 77, 76,
            1601, 1602, 1460, 1465, 1499, // type 29
            1417, 1418, 1419, 1420, 1421, // type 21
            1479, 1484, 1485, 1489, 1492 // type 5
        };

        int[] itemvip = {
            14, 15, 16, 17, 18, // type 12
            16, 17, 18, 19, 20, // type 12
            77, 76, 77, 1603, 861,
            1601, 1602, 1500, 1502, 1401, // type 29
            1417, 1481, 1419, 1420, 1421, // type 21
            1504, 1406, 1485, 1491, 1402 // type 5
        };

        // Trộn mảng item thường
        shuffleArray(itemthg);

        // Trộn mảng item VIP
        shuffleArray(itemvip);

        for (int i = 0; i < 30; i++) {
            Item item = ItemService.gI().createNewItem((short) itemthg[i]);
            listItem.add(item);

            Item itemVip = ItemService.gI().createNewItem((short) itemvip[i]);
            listItemVip.add(itemVip);
        }
    }

    public static void shuffleArray(int[] array) {
        Random rand = new Random();

        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);

            // Hoán đổi phần tử array[i] và array[index]
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void sendDataQuay(Player player, byte type) {
        Message msg = null;

        try {
            msg = new Message(70); // Tạo message với ID 70
            msg.writer().writeByte(type); // Ghi loại quay (0: quay thường, 1: quay VIP)

            int size = listItem.size(); // Lấy số lượng item trong danh sách quay thường
            int size2 = listItemVip.size(); // Lấy số lượng item trong danh sách quay VIP

            // Kiểm tra loại quay: quay thường hoặc quay VIP
            if (type == 0) {
                msg.writer().writeInt(size); // Ghi số lượng vật phẩm quay thường

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        msg.writer().writeInt(listItem.get(i).template != null ? listItem.get(i).template.id : -1); // Ghi ID vật phẩm quay thường
                        msg.writer().writeInt(Util.nextInt(7)); // Ghi một giá trị ngẫu nhiên, có thể là chỉ số item
                    }
                }
            } else {
                msg.writer().writeInt(size2); // Ghi số lượng vật phẩm quay VIP

                if (size2 > 0) {
                    for (int i = 0; i < size2; i++) {
                        msg.writer().writeInt(listItemVip.get(i).template != null ? listItemVip.get(i).template.id : -1); // Ghi ID vật phẩm quay VIP
                        msg.writer().writeInt(Util.nextInt(7)); // Ghi một giá trị ngẫu nhiên, chỉ số item
                    }
                }
            }

            msg.writer().writeInt(20); // Ghi giá trị "Vận may"
            msg.writer().writeInt(31523); // Ghi icon chìa khóa 1
            msg.writer().writeInt(31521); // Ghi icon chìa khóa 2
            player.sendMessage(msg); // Gửi message tới người chơi
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendListReceive(List<Item> list, Player player) {
        try {
            Message msg = new Message(70); // Tạo message với ID 70
            msg.writer().writeByte(1); // Ghi giá trị 1 để hiển thị danh sách nhận thưởng

            int size = list.size(); // Lấy số lượng vật phẩm trong danh sách nhận
            msg.writer().writeInt(size); // Ghi số lượng vật phẩm

            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    msg.writer().writeInt(list.get(i).template.id); // Ghi ID vật phẩm nhận
                    msg.writer().writeInt(6); // Ghi giá trị mặc định là 6, có thể là chỉ số vật phẩm hoặc loại vật phẩm
                }
            }

            player.sendMessage(msg); // Gửi message tới người chơi
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readData(Message msg, Player player) {
        try {
            int type = msg.reader().readByte();

            if (type == 1 || type == 4) {
                int luotQuay = msg.reader().readInt();
                List<Item> receive = new ArrayList<>();

                for (int i = 0; i < luotQuay; i++) {
                    receive.add(type == 1
                            ? listItem.get(Util.nextInt(listItem.size() - 1))
                            : listItemVip.get(Util.nextInt(listItemVip.size() - 1)));
                }

                int dem = 0;
                int slKey = 0;

                for (Item it : player.inventory.itemsBag) {
                    if (it == null || it.template == null) {
                        dem++;
                        continue;
                    }

                    if ((type == 1 && it.template.id == 1594) || (type == 4 && it.template.id == 1595)) {
                        slKey += it.quantity;
                    }
                }

                if (slKey < luotQuay) {
                    Service.gI().sendThongBao(player, "Bạn không đủ key để quay");
                    return;
                }

                if (dem < luotQuay) {
                    Service.gI().sendThongBao(player, "Hành trang không đủ chỗ trống");
                    return;
                }

                for (Item i : receive) {
                    if (i.template == null) {
                        return;
                    }

                    if (i.template.type == 5) {
                        // Cập nhật itemOptions cho type 5
                        i.itemOptions.add(new ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(20, 45) : Util.nextInt(20, 35)));
                        i.itemOptions.add(new ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(20, 45) : Util.nextInt(20, 35)));
                        i.itemOptions.add(new ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(20, 45) : Util.nextInt(20, 35)));

                        if (Util.isTrue(99, 100)) {
                            i.itemOptions.add(new ItemOption(Util.nextInt(93, 101), Util.isTrue(10, 100) ? Util.nextInt(5, 20) : Util.nextInt(1, 10)));
                        }

                        if (Util.isTrue(30, 100)) {
                            i.itemOptions.add(new ItemOption(5, Util.isTrue(10, 100) ? Util.nextInt(20, 25) : Util.nextInt(12, 22)));
                        }

                        if (Util.isTrue(30, 100)) {
                            i.itemOptions.add(new ItemOption(14, Util.isTrue(10, 100) ? Util.nextInt(5, 8) : Util.nextInt(1, 10)));
                        }
                    }

                    if (i.template.type == 21) {
                        // Cập nhật itemOptions cho type 21
                        i.itemOptions.add(new ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(12, 15) : Util.nextInt(10, 14)));
                        i.itemOptions.add(new ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(12, 15) : Util.nextInt(10, 14)));
                        i.itemOptions.add(new ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(12, 15) : Util.nextInt(10, 14)));

                        if (Util.isTrue(99, 100)) {
                            i.itemOptions.add(new ItemOption(Util.nextInt(93, 101), Util.isTrue(10, 100) ? Util.nextInt(5, 20) : Util.nextInt(1, 10)));
                        }

                        if (Util.isTrue(30, 100)) {
                            i.itemOptions.add(new ItemOption(5, Util.isTrue(10, 100) ? Util.nextInt(5, 12) : Util.nextInt(10, 15)));
                        }

                        if (Util.isTrue(30, 100)) {
                            i.itemOptions.add(new ItemOption(14, Util.isTrue(10, 100) ? Util.nextInt(5, 20) : Util.nextInt(1, 10)));
                        }
                    }

                    if (i.template.type == 11) {
                        // Cập nhật itemOptions cho type 11
                        i.itemOptions.add(new ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(8, 15) : Util.nextInt(5, 12)));
                        i.itemOptions.add(new ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(8, 15) : Util.nextInt(5, 12)));
                        i.itemOptions.add(new ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(8, 15) : Util.nextInt(5, 12)));

                        if (Util.isTrue(99, 100)) {
                            i.itemOptions.add(new ItemOption(Util.nextInt(93, 101), Util.isTrue(10, 100) ? Util.nextInt(5, 20) : Util.nextInt(1, 10)));
                        }

                        if (Util.isTrue(30, 100)) {
                            i.itemOptions.add(new ItemOption(5, Util.isTrue(10, 100) ? Util.nextInt(10, 18) : Util.nextInt(5, 12)));
                        }

                        if (Util.isTrue(30, 100)) {
                            i.itemOptions.add(new ItemOption(14, Util.isTrue(10, 100) ? Util.nextInt(5, 12) : Util.nextInt(1, 10)));
                        }
                    }

                    if (i.template.type == 29 || i.template.type == 12 || i.template.type == 27 || i.template.type == 93) {
                        // Cập nhật itemOptions cho các loại khác
                        if (Util.isTrue(99, 100)) {
                            i.itemOptions.add(new ItemOption(30, 1));
                        }

                        if (Util.isTrue(99, 100)) {
                            i.itemOptions.add(new ItemOption(93, 10));
                        }
                    }

                    if (InventoryService.gI().getCountEmptyBag(player) >= luotQuay) {
                        InventoryService.gI().addItemBag(player, i);
                    } else {
                        player.inventory.itemsMailBox.add(i);
                        GodGK.updateMailBox(player);
                        Service.gI().sendThongBao(player, "Hành trang đầy, vui lòng kiểm tra hòm thư");
                    }
                }

                // Xử lý giảm số lượng key
                for (Item it : player.inventory.itemsBag) {
                    if (luotQuay == 0) {
                        break;
                    }

                    if (it == null || it.template == null) {
                        continue;
                    }

                    if ((type == 1 && it.template.id == 1594) || (type == 4 && it.template.id == 1595)) {
                        int min = Math.min(luotQuay, it.quantity);
                        luotQuay -= min;
                        it.quantity -= min;
                    }
                }

                InventoryService.gI().sendItemBag(player);
                sendListReceive(receive, player);
            } else if (type == 0 || type == 3) {
                sendDataQuay(player, (byte) type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}