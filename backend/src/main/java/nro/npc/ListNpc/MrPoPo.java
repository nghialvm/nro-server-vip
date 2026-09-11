package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.Service;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import nro.clan.Clan;
import nro.clan.ClanMember;
import nro.map.DestronGas.DestronGas;
import nro.map.DestronGas.DestronGasService;
import nro.npc.Npc;
import static nro.npc.NpcFactory.PLAYERID_OBJECT;
import nro.player.Player;

public class MrPoPo extends Npc {

    public MrPoPo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_5 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN, "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                        "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_4 == 0) {
                    String[] chucTetMessages = {
                        "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                        "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                        "Phát tài phát lộc",
                        "Vạn sự như ý",
                        "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                        "Năm mới phát tài phát lộc, vạn sự như ý nha",
                        "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                        "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                        "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                        "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                        "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                        "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                        "Tết đến cười thật nhiều, sống thật chill, vui hết mình",
                        "Chúc mừng năm mới"
                    };
                    
                    String message = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                    createOtherMenu(player, ConstNpc.NHAN_LI_XI, message, "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                    return;
                }
            }
            if (this.mapId == 0) {
                if (player.clan != null) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Thượng Đế vừa phát hiện ra 1 loại khí đang âm thầm\nhủy diệt mọi mầm sống trên Trái Đất,\nnó được gọi là Destron Gas.\nTa sẽ đưa các cậu đến nơi ấy, các cậu đã sẵn sàng chưa?",
                            "Thông tin\nChi tiết", "Top 100\nBang hội", "Thành tích\nBang", "OK", "Từ chối");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Thượng Đế vừa phát hiện ra 1 loại khí đang âm thầm\nhủy diệt mọi mầm sống trên Trái Đất,\nnó được gọi là Destron Gas.\nTa sẽ đưa các cậu đến nơi ấy, các cậu đã sẵn sàng chưa?",
                            "Thông tin\nChi tiết", "Top 100\nBang hội", "OK", "Từ chối");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI) {
                    switch (select) {
                        case 1:
                            Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                            String[] chucTetMessages = {
                                "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                                "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                                "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                                "Năm mới phát tài phát lộc, vạn sự như ý nha",
                                "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                                "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                                "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                                "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                                "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                                "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                                "Tết đến cười thật nhiều, sống thật chill, vui hết mình"
                            };

                            String NpcChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            String PlayerChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            this.npcChat(player, NpcChat);
                            Service.gI().chat(player, PlayerChat);
                            player.NhanLiXiForNPC_4++;
                            if (Util.isTrue(60, 100)) {
                                Lixi.addOptionParam(30, 0);
                                Lixi.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, Lixi);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + Lixi.template.name);
                            } else {
                                Service.gI().sendThongBao(player, "(>_<)");
                            }
                            break;
                    }
                    return;
                }
            }
            if (this.mapId == 0) {
                if (player.iDMark.isBaseMenu()) {
                    if (player.clan != null) {
                        switch (select) {
                            case 0:
                                NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_KHI_GAS_HUY_DIET);
                                break;
                            case 1:// Top 100 bang hội
                                Service.gI().showTopClanKhiGas(player);
                                break;
                            case 2:// Thành tích Bang
                                Service.gI().showMyTopClanKhiGas(player);
                                break;
                            case 3: {
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                    "Gia nhập bang hội trên 1 ngày mới được tham gia");
                                            return;
                                        }
                                        if (player.clan.KhiGasHuyDiet != null) {
                                            createOtherMenu(player, 2,
                                                    "Bang hội của cậu đang tham gia Destron Gas cấp độ " + player.clan.KhiGasHuyDiet.level + "\n"
                                                    + "cậu có muốn đi cùng họ không ? ("
                                                    + TimeUtil.convertTimeNow(player.clan.KhiGasHuyDiet.getLastTimeOpen())
                                                    + " trước)", "Đồng ý", "Từ chối");
                                            return;
                                        }
                                        if (!clan.isLeader(player)) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar, "Chức năng chỉ dành cho bang chủ");
                                            return;
                                        }
                                        if (clan.members.size() < DestronGas.N_PLAYER_CLAN) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                    "Bang hội phải có ít nhất 2 thành viên mới có thể tham gia");
                                            return;
                                        }
                                        if (player.nPoint.power < DestronGas.POWER_CAN_GO_TO_KHI_GAS_HUY_DIET) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                    "Yêu cầu sức mạnh lớn hơn 2 tỷ để có thể tham gia");
                                            return;
                                        }
                                        Input.gI().createFormChooseLevelKGHD(player);
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        switch (select) {
                            case 0:
                                NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_KHI_GAS_HUY_DIET);
                                break;
                            case 1:// Top 100 bang hội
                                Service.gI().showTopClanKhiGas(player);
                                break;
                            case 2: {
                                Clan clan = player.clan;
                                if (clan == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bang hội!");
                                } else {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                    "Gia nhập bang hội trên 1 ngày mới được tham gia");
                                            return;
                                        }
                                        if (player.clan.KhiGasHuyDiet != null) {
                                            createOtherMenu(player, 2,
                                                    "Bang hội của cậu đang tham gia Destron Gas cấp độ " + player.clan.KhiGasHuyDiet.level + "\n"
                                                    + "cậu có muốn đi cùng họ không ? ("
                                                    + TimeUtil.convertTimeNow(player.clan.KhiGasHuyDiet.getLastTimeOpen())
                                                    + " trước)", "Đồng ý", "Từ chối");
                                            return;
                                        }
                                        if (!clan.isLeader(player)) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar, "Chức năng chỉ dành cho bang chủ");
                                            return;
                                        }
                                        if (clan.members.size() < DestronGas.N_PLAYER_CLAN) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                    "Bang hội phải có ít nhất 2 thành viên mới có thể tham gia");
                                            return;
                                        }
                                        if (player.nPoint.power < DestronGas.POWER_CAN_GO_TO_KHI_GAS_HUY_DIET) {
                                            NpcService.gI().createTutorial(player, tempId, this.avartar,
                                                    "Yêu cầu sức mạnh lớn hơn 2 tỷ để có thể tham gia");
                                            return;
                                        }
                                        Input.gI().createFormChooseLevelKGHD(player);
                                    }
                                }
                                break;
                            }
                        }
                    }
                } else if (player.iDMark.getIndexMenu() == 2) {
                    if (select == 0) {
                        if (player.clan.KhiGasHuyDiet == null) {
                            DestronGasService.gI().openKhiGasHuyDiet(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                        } else {
                            DestronGasService.gI().openKhiGasHuyDiet(player, (byte) 0);
                        }
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_KEO_HALLOWEEN) {
                    switch (select) {
                        case 0:
                            Item KeoBanTay = ItemService.gI().createNewItem((short) 901, 1);
                            KeoBanTay.addOptionParam(86, 0);
                            KeoBanTay.addOptionParam(93, 35);
                            int quality = Util.nextInt(1, 3);
                            KeoBanTay.quantity = quality;
                            InventoryService.gI().addItemBag(player, KeoBanTay);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().chat(player, "Haha xin được " + quality + " kẹo bàn tay rồi");
                            player.NhanKeoHayBiGheoNpc_5++;
                            break;
                        case 1:
                            player.NhanKeoHayBiGheoNpc_5++;
                            break;
                    }   
                }
            }
        }
    }
}
