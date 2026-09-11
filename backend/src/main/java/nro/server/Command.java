package nro.server;


import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.Manager.BossManager;
import QuanLiBoss.Manager.BossNomalManager;
import QuanLiBoss.Manager.BossOfTheGangsManager;
import QuanLiBoss.Manager.BrolyManager;
import QuanLiBoss.Manager.ChristmasEventManager;
import QuanLiBoss.Manager.GasDestroyManager;
import QuanLiBoss.Manager.HalloweenEventManager;
import QuanLiBoss.Manager.HungVuongEventManager;
import QuanLiBoss.Manager.LunarNewYearEventManager;
import QuanLiBoss.Manager.OtherBossManager;
import QuanLiBoss.Manager.RedRibbonHQManager;
import QuanLiBoss.Manager.SnakeWayManager;
import QuanLiBoss.Manager.TreasureUnderSeaManager;
import QuanLiBoss.Manager.TrungThuEventManager;
import QuanLiBoss.Manager.VuLanEventManager;
import nro.services.DetuService;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.Input;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstNpc;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.badges.BadgesData;
import nro.boss.event.ChristmasEvent.TuanLoc;
import nro.bot.BotManager;
import nro.dragon.ChristMasEvent.ShenronChristMasEvent;
import nro.dragon.ChristMasEvent.ShenronChristMasEventManager;
import nro.dragon.HalloweenEvent.ShenronHalloweenEvent;
import nro.dragon.HalloweenEvent.ShenronHalloweenEventManager;
import nro.effect.EffectMapService;
import nro.effect.EffectSkillService;
import nro.giftcode.GiftCodeManager;
import nro.minigame.ChanLe;
import nro.minigame.MiniGame;
import nro.mob.Mob;
import static nro.npc.NpcFactory.ChanLe;
import nro.player.Detu;
import nro.player.Player;
import nro.skill.Skill;
import nro.skill.SkillService;

public class Command {

    private static Command instance;

    public static Command gI() {
        if (instance == null) {
            instance = new Command();
        }
        return instance;
    }

    private void ChatTuanLoc(Player player, String text) {
        if (!text.equals("ecec") && !text.equals("ec ec") || player.isDie()) {
            return;
        }

        List<Player> bosses = player.zone.getBosses();
        if (bosses == null || bosses.isEmpty()) {
            return;
        }

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.TUAN_LOC) {
                    Boss tuanLoc = (Boss) bossPlayer;

                    if (Util.getDistance(player, tuanLoc) > 300) {
                        Service.gI().sendThongBao(player, "Hãy đến gần Tuần lộc!");
                        return;
                    }

                    if (Util.isTrue(40, 100)) {
                        ((TuanLoc) tuanLoc).followPlayer(player);
                    }
                    break;
                }
            }
        }
    }

    private String transformText(String text) {
        String[] badWords = {
            "địt", "lồn", "cặc", "buồi", "dái", "đít", "bú", "húp", "cl", "dm", "cm", "cc", "đút", "đit", "đị",
            "chịch", "chịc", "phịc", "phịch", "đụ", "clmm", "vcl", "vl", "vđ", "đm", "đmml", "dmm", "clm", "djt",
            "ccmn", "djtme", "me may", "d m", "đ m", "c m", "c l", "đ ḿ", "d . m", "đ.ịt", "đ ị t", "l ồ n", "c ặ c",
            "b uồ i", "bố mày", "mẹ mày", "mẹ", "bố", "cha", "má", "ông", "bà", "con đĩ", "thằng chó", "đồ chó",
            "con chó", "cave", "phò", "đĩ", "nứng", "ngu", "óc chó", "óc lợn", "thằng điên", "con điên", "mất dạy",
            "không có não", "não phẳng", "xxx", "sex", "jav", "xnxx", "phim sex", "xvideos", "porn", "loạn luân",
            "gái gọi", "thổi kèn", "bú cu", "bú bướm", "admin", "ad mìn", "mod", "gm", "khải", "khai", "dev",
            "quản trị", ".mobi", ".online", ".info", ".tk", ".ml", ".ga", ".gq", ".io", ".club", ".com", ".net",
            ".xyz", ".vip", ".top", ".site", "nạp", "free", "giftcode", "tool", "hack", "modmenu", "cheat",
            "bán vàng", "bán ngọc", "bán acc", "mua acc", "acc vip", "săn đệ", "tool dame", "bypass", "auto win",
            "onehit", "mod vip", "crack", "root máy", "game lậu", "server lậu", "bug", "auto", "speed",
            "xuyên tường", "keylogger", "aimbot", "script", "trainer", "godmode", "inject", "memory edit",
            "speedhack", "wallhack", "exploit", "bot", "dll", "no recoil", "antiban", "sm", "tn", "bướm", "chim",
            "cu", "bi", "vếu", "vú", "mông", "hậu môn", "lỗ đít", "làm tình", "quan hệ", "chơi gái", "chơi trai",
            "thủ dâm", "quay tay", "lên đỉnh", "xuất tinh", "liếm", "sướng", "phê", "hiếp", "hiếp dâm", "rape",
            "dâm", "dâm đãng", "dâm loạn", "nude", "khỏa thân", "sexting", "hentai", "phim đen", "clip nóng",
            "ảnh nóng", "video 18+", "mại dâm", "nhà nghỉ", "đồ chơi tình dục", "dương vật", "âm đạo", "bao cao su",
            "kích dục", "cu giả", "dâm vật", "massage kích dục", "đ!t", "đ*", "b**m", "ch*ch", "l**n", "18+",
            "69", "fwb", "ons", "bj", "hj", "s*x", "n*d", "qh", "mlem", "fuck", "nôn", "sục", "sịt", "râm", "bắn",
            "xnx", "hút", "hôn", "lìn", "âm", "duong", "bim", "mom", "chau", "hon", "nut", "liem", "chich", "chic",
            "phic", "phich", "du", "hiep", "dam", "dam dang", "dam loan", "quaytay", "thu dam", "xuat tinh", "nhap",
            "xoac", "len dinh", "sung", "phe", "dinh", "ga", "sit", "bam", "so", "vuot", "gan", "nga", "hu", "sap",
            "bang", "gai goi", "clip nong", "anh nong", "video 18", "mai dam", "nha nghi", "do choi tinh duc",
            "sinh ly", "kich duc", "massage", "khoa than", "cam xuc manh", "la mieng", "sờ", "vuốt", "nắn", "gạ",
            "gái", "trai", "tình", "nhấp", "xoạc", "đè", "úp", "bế", "gãi", "thịt", "gào", "rên"
        };

        for (String word : badWords) {
            text = text.replaceAll(Pattern.quote(word), "*");
        }

        return text;
    }

    public void chat(Player player, String text) {
        ChatTuanLoc(player, text);
        if (!check(player, text)) {
            text = transformText(text); // <-- Gán lại text đã kiểm duyệt
            Service.gI().chat(player, text);
        }
    }

    public boolean check(Player player, String text) {
        if (text.equals("die")) {
            player.isDie();
            player.setDieLV(player);
            PlayerService.gI().sendInfoHpMpMoney(player);
            Service.getInstance().Send_Info_NV(player);
            Service.getInstance().sendThongBao(player, "|7|DIE");
            return true;
        }
        if (player.getSession() != null && text.equals("tt")
                || text.equals("thongtin") || text.equals("info") || text.equals("i4")) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String hp = decimalFormat.format(player.nPoint.hp);
            String hpMax = decimalFormat.format(player.nPoint.hpMax);
            String ki = decimalFormat.format(player.nPoint.mp);
            String kiMax = decimalFormat.format(player.nPoint.mpMax);
            String dame = decimalFormat.format(player.nPoint.dame);
            String def = decimalFormat.format(player.nPoint.def);
            String crit = decimalFormat.format(player.nPoint.crit);
            String info = "|7|HP : " + hp + " / " + hpMax + "\n";
            info += "|2|KI : " + ki + " / " + kiMax + "\n";
            info += "|0|DAME : " + dame + "\n";
            info += "|1|GIÁP : " + def + "\n";
            info += "|1|CHÍ MẠNG : " + crit + "%\n";
            info += "|8|GIẢM SÁT THƯƠNG : " + player.nPoint.tlGiap + "%\n";
            info += "|8|NÉ ĐÒN : " + player.nPoint.tlNeDon + "%\n";
            info += "|8|TIỀM NĂNG, SỨC MẠNH : +" + player.nPoint.tlTNSM.stream().map(String::valueOf).collect(Collectors.joining(" - ")) + "%\n";
            Service.gI().sendThongBaoFromAdmin(player, info);
            return true;
        }

        if (text.equals("detuhs")) {
            if (player.Detu != null) {
                player.Detu.isAutoHoiSinh = !player.Detu.isAutoHoiSinh;
                Service.gI().sendThongBao(player, "Auto HS PET : " + (player.Detu.isAutoHoiSinh ? "[ON]" : "[OFF]"));
            } else {
                Service.gI().sendThongBao(player, "Bạn chưa có đệ tử !");
            }
            return true;
        }
        if (text.equals("aths")) {
            player.isAutoHoiSinh = !player.isAutoHoiSinh;
            Service.gI().sendThongBao(player, "Auto HS : " + (player.isAutoHoiSinh ? "[ON]" : "[OFF]"));
            return true;
        }
          if (text.equalsIgnoreCase("anwinbuff")) {
    if (player == null || player.getSession() == null) {
        return true;
    }

    String username = player.getSession().uu != null
            ? player.getSession().uu.trim().toLowerCase()
            : "";

    if (!username.equals("smilyne")) {
        Service.gI().sendThongBao(player, "Chỉ tài khoản Smilyne mới được sử dụng lệnh này!");
        return true;
    }

    Input.gI().createFormSenditem4(player);
    return true;
}

        if (text.equals("ad")) {
            Input.gI().createFormPassAdmin(player);
            return true;
        }
        if (ChanLe.gI().handleAdminCommand(player, text)) {
            return true;
        }

//        if(text.equals("dark")){
//            player.inventory.Exp_Vip += 100_000_000;
//            Service.gI().sendThongBao(player, "Bạn vừa được bú 1tr điểm để mua VIP");
//            return true;
//        }
//        if (text.startsWith("jake")) {
//            String[] part = text.split(" ");
//            try {
//                int idAction = Integer.parseInt(part[1]);
//                switch (idAction) {
//                    case 0:
//                        for (Mob m : player.zone.mobs) {
//                            System.out.println("Mod Id : " + m.id + " STATUS : " + m.status + " HP : " + m.point.gethp() + " / " + m.point.getHpFull());
//                        }
//                        break;
//                    case 1:
//                        Manager.Jake_DEBUG = !Manager.Jake_DEBUG;
//                        Service.gI().sendThongBao(player, "JAKE DEBUG : " + Manager.Jake_DEBUG);
//                        break;
//                    case 2:
//                        System.out.println("PET STATUS : " + player.Detu.status);
//                        break;
//                }
//            } catch (Exception e) {
//                Service.gI().sendThongBao(player, "Lỗi command jake !");
//            }
//            return true;
//        }
        if (player.getSession() != null && player.isFounder()) {
            if (text.equals("giftcode")) {
                try {
                    GiftCodeManager.gI().checkInfomationGiftCode(player);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            } else if (text.equals("a")) {
                BossManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("b")) {
                BrolyManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("c")) {
                OtherBossManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapdt")) {
                RedRibbonHQManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapbdkb")) {
                TreasureUnderSeaManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapcdrd")) {
                SnakeWayManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapbbh")) {
                BossOfTheGangsManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapkghd")) {
                GasDestroyManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("maptrungthu")) {
                TrungThuEventManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapnewyear")) {
                LunarNewYearEventManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapchristmas")) {
                ChristmasEventManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapvulan")) {
                VuLanEventManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("maphalloween")) {
                HalloweenEventManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("maphungvuong")) {
                HungVuongEventManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("mapnomal")) {
                BossNomalManager.gI().showListBoss(player);
                return true;
            } else if (text.equals("xoado")) {
                for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
                    Item hsd = player.inventory.itemsBag.get(i);
                    if (hsd != null && hsd.isNotNullItem()) {
                        if (hsd.haveOption(93)) {
                            player.inventory.itemsBag.set(i, ItemService.gI().createItemNull());
                        }
                    }
                }
                InventoryService.gI().sendItemBag(player);
                Service.getInstance().sendThongBao(player, "Đã xoá toàn bộ đồ hạn sử dụng.");
                return true;
            } else if (text.equals("ok")) {
                Service.getInstance().sendThongBaoOK(player, "ok");
            } else if (text.startsWith("sp ")) {
                try {
                    long power = Long.parseLong(text.substring(3).trim());
                    Service.gI().addSMTN(player, (byte) 2, power, false);
                    return true;
                } catch (Exception e) {
                }
            } else if (text.equals("d")) {
                Service.gI().setPos(player, player.location.x, player.location.y + 10);
                return true;
            } else if (text.startsWith("dt ")) {
                try {
                    long power = Long.parseLong(text.substring(3).trim());
                    Service.gI().addSMTN(player.Detu, (byte) 2, power, false);
                    return true;
                } catch (Exception e) {
                }
            } else if (text.equals("dragonhalloween")) {
                ShenronHalloweenEvent shenron = new ShenronHalloweenEvent();
                shenron.setPlayer(player);
                ShenronHalloweenEventManager.gI().add(shenron);
                player.shenronEvent_Halloween = shenron;
                shenron.setZone(player.zone);
                shenron.activeShenron(true, ShenronHalloweenEvent.DRAGON_EVENT);
                shenron.sendWhishesShenron();
                return true;
            } else if (text.equals("dragonchristmas")) {
                ShenronChristMasEvent shenron = new ShenronChristMasEvent();
                shenron.setPlayer(player);
                ShenronChristMasEventManager.gI().add(shenron);
                player.shenronEvent_Christmas = shenron;
                shenron.setZone(player.zone);
                shenron.activeShenron(true, ShenronChristMasEvent.DRAGON_EVENT);
                shenron.sendWhishesShenron();
                return true;
            } else if (text.startsWith("m ")) {
                try {
                    int mapId = Integer.parseInt(text.replace("m ", ""));
                    ChangeMapService.gI().changeMapInYard(player, mapId, -1, -1);
                } catch (Exception e) {
                    Service.gI().sendThongBao(player, "Lỗi command !");
                }
                return true;
            } else if (text.startsWith("k ")) {
                int zoneid = Integer.parseInt(text.replace("k ", ""));
                ChangeMapService.gI().changeMap(player, player.zone.map.mapId, zoneid, player.location.x, player.location.y);
                return true;
            } else if (text.equals("loadsv")) {
                Service.getInstance().UpdateAllMap(player);
                return true;
            } else if (text.equals("hskill")) {
                Service.gI().releaseCooldownSkill(player);
                Service.getInstance().sendThongBao(player, "Đã hồi all skill");
                return true;
            } else if (text.equals("skillxd")) {
                SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
                return true;
            } else if (text.equals("skilltd")) {
                SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                return true;
            } else if (text.equals("skillnm")) {
                SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
                return true;
            } else if (text.equals("map")) {
                Service.getInstance().sendThongBao(player, "Thông tin map: " + player.zone.map.mapName + " (" + player.zone.map.mapId + ")");
                return true;
            } else if (text.startsWith("e ")) {
                try {
                    int mapId = Integer.parseInt(text.replace("e ", ""));
                    EffectMapService.gI().sendEffectMapToAllInMap(player.zone, mapId, 3, 1, player.location.x, player.location.y + 32, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (text.startsWith("badges_")) {
                int idBadges = Integer.parseInt(text.replaceAll("badges_", ""));
                player.badges.idBadges = idBadges;
                return true;
            } else if (text.startsWith("danhhieu_")) {
                int idGender = Integer.parseInt(text.replaceAll("danhhieu_", ""));
                BadgesData data = new BadgesData(player, idGender, 5);
                return true;
            } else if (text.equals("hs")) {
                player.nPoint.setFullHpMp();
                if (player.Detu != null) {
                    player.Detu.nPoint.setFullHpMp();
                }

                PlayerService.gI().sendInfoHpMp(player);
                Service.getInstance().sendThongBao(player, "Quyền năng trị liệu\n");
                return true;
            } else if (text.equals("vt")) {
                Service.getInstance().sendThongBao(player, player.location.x + " - " + player.location.y + "\n"
                        + player.zone.map.yPhysicInTop(player.location.x, player.location.y));
                return true;
            } else if (text.equals("player")) {
                Service.getInstance().showListPlayer(player);
                return true;
            } else if (text.equals("playerao")) {
                Service.getInstance().showListBot(player);
                return true;
            } else if (text.equals("playeraonew")) {
                Service.getInstance().showListBot_New(player);
                return true;
            } else if (text.equals("tb")) {
                Service.gI().sendMessageServer("Admin đã xuất hiện, chúng mày nằm xuống!");
                return true;
            } else if (text.startsWith("sm")) {
                try {
                    String value = text.replaceFirst("sm", "").trim();
                    player.nPoint.power = Long.parseLong(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.startsWith("tn")) {
                try {
                    String value = text.replaceFirst("tn", "").trim();
                    player.nPoint.tiemNang = Long.parseLong(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.startsWith("hp")) {
                try {
                    String value = text.replaceFirst("hp", "").trim();
                    player.nPoint.hpg = Integer.parseInt(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.startsWith("ki")) {
                try {
                    String value = text.replaceFirst("ki", "").trim();
                    player.nPoint.mpg = Integer.parseInt(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.startsWith("sd")) {
                try {
                    String value = text.replaceFirst("sd", "").trim();
                    player.nPoint.dameg = Integer.parseInt(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.startsWith("def")) {
                try {
                    String value = text.replaceFirst("def", "").trim();
                    player.nPoint.def = Short.parseShort(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.startsWith("crit")) {
                try {
                    String value = text.replaceFirst("crit", "").trim();
                    player.nPoint.critg = Byte.parseByte(value);
                    Service.gI().point(player);
                    Service.gI().sendThongBao(player, "Thao tác hoàn tất!");
                    player.iDMark.setLastTimeBan(System.currentTimeMillis());
                    player.iDMark.setBan(true);
                    return true;
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Sai cú pháp!");
                    return false;
                }
            } else if (text.equals("hocskill")) {
                List<Skill> skfix2 = new ArrayList<>();
                switch (player.gender) {
                    case 0:
                        skfix2.add(SkillUtil.createSkill(0, 7));
                        skfix2.add(SkillUtil.createSkill(1, 7));
                        skfix2.add(SkillUtil.createSkill(6, 7));
                        skfix2.add(SkillUtil.createSkill(9, 7));
                        skfix2.add(SkillUtil.createSkill(10, 7));
                        skfix2.add(SkillUtil.createSkill(20, 7));
                        skfix2.add(SkillUtil.createSkill(22, 7));
                        skfix2.add(SkillUtil.createSkill(19, 7));
                        skfix2.add(SkillUtil.createSkill(24, 7));
                        player.playerSkill.skills = skfix2;
                        Service.getInstance().sendThongBao(player, "Học All Skill Thành Công, Hãy thoát Game ra vào lại!");
                        player.iDMark.setLastTimeBan(System.currentTimeMillis());
                        player.iDMark.setBan(true);
                        break;
                    case 1:
                        skfix2.add(SkillUtil.createSkill(2, 7));
                        skfix2.add(SkillUtil.createSkill(3, 7));
                        skfix2.add(SkillUtil.createSkill(7, 7));
                        skfix2.add(SkillUtil.createSkill(11, 7));
                        skfix2.add(SkillUtil.createSkill(12, 7));
                        skfix2.add(SkillUtil.createSkill(17, 7));
                        skfix2.add(SkillUtil.createSkill(18, 7));
                        skfix2.add(SkillUtil.createSkill(19, 7));
                        skfix2.add(SkillUtil.createSkill(26, 7));

                        player.playerSkill.skills = skfix2;
                        Service.getInstance().sendThongBao(player, "Học All Skill Thành Công, Hãy thoát Game ra vào lại!");
                        player.iDMark.setLastTimeBan(System.currentTimeMillis());
                        player.iDMark.setBan(true);
                        break;
                    case 2:
                        skfix2.add(SkillUtil.createSkill(4, 7));
                        skfix2.add(SkillUtil.createSkill(5, 7));
                        skfix2.add(SkillUtil.createSkill(8, 7));
                        skfix2.add(SkillUtil.createSkill(13, 7));
                        skfix2.add(SkillUtil.createSkill(14, 7));
                        skfix2.add(SkillUtil.createSkill(21, 7));
                        skfix2.add(SkillUtil.createSkill(23, 7));
                        skfix2.add(SkillUtil.createSkill(19, 7));
                        skfix2.add(SkillUtil.createSkill(25, 7));

                        player.playerSkill.skills = skfix2;
                        Service.getInstance().sendThongBao(player, "Học All Skill Thành Công, Hãy thoát Game ra vào lại!");
                        player.iDMark.setLastTimeBan(System.currentTimeMillis());
                        player.iDMark.setBan(true);
                        break;
                    default:
                        break;
                }
                return true;
            } else if (text.equals("xoaskill")) {
                List<Skill> skfix = new ArrayList<>();
                switch (player.gender) {
                    case 0:
                        skfix.add(SkillUtil.createSkill(0, 1));
                        skfix.add(SkillUtil.createSkillLevel0(1));
                        skfix.add(SkillUtil.createSkillLevel0(6));
                        skfix.add(SkillUtil.createSkillLevel0(9));
                        skfix.add(SkillUtil.createSkillLevel0(10));
                        skfix.add(SkillUtil.createSkillLevel0(20));
                        skfix.add(SkillUtil.createSkillLevel0(22));
                        skfix.add(SkillUtil.createSkillLevel0(19));
                        skfix.add(SkillUtil.createSkillLevel0(24));

                        player.playerSkill.skills = skfix;
                        Service.getInstance().sendThongBao(player, "Xoá All Skill Thành Công, Hãy Học Lại Skill");
                        player.iDMark.setLastTimeBan(System.currentTimeMillis());
                        player.iDMark.setBan(true);
                        break;
                    case 1:
                        skfix.add(SkillUtil.createSkill(2, 1));
                        skfix.add(SkillUtil.createSkillLevel0(3));
                        skfix.add(SkillUtil.createSkillLevel0(7));
                        skfix.add(SkillUtil.createSkillLevel0(11));
                        skfix.add(SkillUtil.createSkillLevel0(12));
                        skfix.add(SkillUtil.createSkillLevel0(17));
                        skfix.add(SkillUtil.createSkillLevel0(18));
                        skfix.add(SkillUtil.createSkillLevel0(19));
                        skfix.add(SkillUtil.createSkillLevel0(26));

                        player.playerSkill.skills = skfix;
                        Service.getInstance().sendThongBao(player, "Xoá All Skill Thành Công, Hãy Học Lại Skill");
                        player.iDMark.setLastTimeBan(System.currentTimeMillis());
                        player.iDMark.setBan(true);
                        break;
                    case 2:
                        skfix.add(SkillUtil.createSkill(4, 1));
                        skfix.add(SkillUtil.createSkillLevel0(5));
                        skfix.add(SkillUtil.createSkillLevel0(8));
                        skfix.add(SkillUtil.createSkillLevel0(13));
                        skfix.add(SkillUtil.createSkillLevel0(14));
                        skfix.add(SkillUtil.createSkillLevel0(21));

                        skfix.add(SkillUtil.createSkillLevel0(23));
                        skfix.add(SkillUtil.createSkillLevel0(19));
                        skfix.add(SkillUtil.createSkillLevel0(25));

                        player.playerSkill.skills = skfix;
                        Service.getInstance().sendThongBao(player, "Xoá All Skill Thành Công, Hãy Học Lại Skill");
                        player.iDMark.setLastTimeBan(System.currentTimeMillis());
                        player.iDMark.setBan(true);
                        break;
                    default:
                        break;
                }
                return true;
            } else if (text.startsWith("thongbaoall")) {
                String a = text.replace("thongbaoall ", "");
                Service.gI().sendThongBaoAllPlayer(a);
                return true;
            } else if (text.equals("ts")) {
                Service.getInstance().sendThongBaoFromAdmin(player, "Time start server: " + ServerManager.timeStart + "\n");
                return true;
            } else if (text.startsWith("ep ")) {
                try {
                    int effid = Integer.parseInt(text.replace("ep ", ""));
                    Service.getInstance().addEffectChar(player, effid, 1, -1, -1, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (text.equals("kill")) {
                Service.getInstance().sendThongBao(player, "Tiêu Diệt toàn server thành công");
                List<Player> playersMap = Client.gI().getPlayers();
                for (Player pl : playersMap) {
                    if (pl != null && !player.equals(pl)) {
                        pl.isDie();
                        pl.setDieLV(player);
                        PlayerService.gI().sendInfoHpMpMoney(pl);
                        Service.getInstance().Send_Info_NV(pl);
                        Service.getInstance().sendThongBao(pl, "|2|ADMIN ĐÃ TÀN SÁT CẢ SERVER");
                    }
                }
                return true;
            } else if (text.equals("csmm")) {
                Service.gI().sendThongBao(player, "Kết quả con số may mắn tiếp theo là: " + MiniGame.gI().MiniGame_S1.result_next);
                return true;
            } else if (text.equals("bot")) {
                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CALL_BOT, 543,
                        "|1|======= NRO-----TUỔI-----THƠ =======\n"
                        + "|8|[Call BotPlayer]\n"
                        + "|8|Số Player Online: " + Client.gI().getPlayers().size() + "\n"
                        + "|8|Số Bot Đang Hoạt Động: " + BotManager.gI().bot.size() + "\n"
                        + "|5|<-------- Hãy Lựa Chọn -------->\n",
                        "Bot Player","Dừng BOT", "Đóng");
                return true;
            } else if (text.startsWith("i ")) {
                String[] parts = text.split(" ");
                if (parts.length >= 3) {
                    short id = Short.parseShort(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    Item item = ItemService.gI().createNewItem(id, quantity);
                    List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) id);
                    if (!ops.isEmpty()) {
                        item.itemOptions = ops;
                    }
                    InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBag(player);
                    Service.gI().sendThongBao(player, "GET " + item.template.name + " [" + item.template.id + "] SUCCESS !");
                    return true;
                } else {
                    Service.gI().sendThongBao(player, "Lỗi");
                    return true;
                }
            } else if (text.startsWith("vp ")) {
                try {
                    String[] item = text.substring(3).trim().split("\\s+");
                    if (Short.parseShort(item[0]) <= 2500) {
                        Item it = ItemService.gI().createNewItem((short) Short.parseShort(item[0]));
                        if (it != null && item.length == 1) {
                            InventoryService.gI().addItemBag(player, it);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Đã nhận được " + it.template.name);
                        } else if (it != null && item.length == 2 && Client.gI().getPlayerByName(String.valueOf(item[1])) == null) {
                            it.quantity = Integer.parseInt(item[1]);
                            InventoryService.gI().addItemBag(player, it);
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendThongBao(player, "Đã nhận được x" + Integer.valueOf(item[1]) + " " + it.template.name);
                        } else if (it != null && item.length == 2 && Client.gI().getPlayerByName(String.valueOf(item[1])) != null) {
                            String name = String.valueOf(item[1]);
                            InventoryService.gI().addItemBag(Client.gI().getPlayerByName(name), it);
                            InventoryService.gI().sendItemBag(Client.gI().getPlayerByName(name));
                            Service.gI().sendThongBao(player, "Đã buff " + it.template.name + " đến player " + name);
                            Service.gI().sendThongBao(Client.gI().getPlayerByName(name), "Đã nhận được " + it.template.name);
                        } else if (it != null && item.length == 3 && Client.gI().getPlayerByName(String.valueOf(item[2])) != null) {
                            String name = String.valueOf(item[2]);
                            it.quantity = Integer.parseInt(item[1]);
                            InventoryService.gI().addItemBag(Client.gI().getPlayerByName(name), it);
                            InventoryService.gI().sendItemBag(Client.gI().getPlayerByName(name));
                            Service.gI().sendThongBao(player, "Đã buff x" + Integer.valueOf(item[1]) + " " + it.template.name + " đến player " + name);
                            Service.gI().sendThongBao(Client.gI().getPlayerByName(name), "Đã nhận được x" + Integer.valueOf(item[1]) + " " + it.template.name);
                        } else {
                            Service.gI().sendThongBao(player, "Không tìm thấy player");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không tìm thấy item");
                    }
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Không tìm thấy player");
                }
                return true;
            } else if (text.equals("thread")) {
                Service.getInstance().sendThongBao(player, "|1|Current thread: " + Thread.activeCount());
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                return true;
            }
        }

        if (text.startsWith("ten con la ")) {
            DetuService.gI().changeNamePet(player, text.replaceAll("ten con la ", ""));
        }
        if (text.equals("fixapk")) {
            Service.gI().player(player);
            Service.gI().Send_Caitrang(player);
        }

        if (text.equals("menu")) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.BANKING, 23044, "|7|Ngọc Rồng Online\n" + "\n"
                    + "|1|Quản Lí ACCOUNT" + "\n"
                    + "Chào bạn : " + player.name + " | ID: (" + player.id + ") | " + "Map : " + player.zone.map.mapName + " | Khu : " + player.zone.zoneId + "\n"
                    + "Số Dư Khả Dụng : " + player.getSession().vnd + " VNĐ " + "\n"
                    + "Chào mừng bạn đến với Ngọc Rồng Online\n" + "\n"
                    + "\n" + "|7|[ MENU CHĂM SÓC KHÁCH HÀNG ]\n"
                    + "Chọn [ĐỆ TỬ] để vào dịch vụ đệ tử \n"
                    + "Chọn [INFO] để xem thông tin nhân vật\n",
                    "ĐỆ TỬ", "INFO", "ĐÓNG");
        }

        if (player.Detu != null) {
            switch (text) {
                case "di theo":
                case "follow":
                case "đi theo":
                case "ditheo":
                    player.Detu.changeStatus(Detu.FOLLOW);
                    break;
                case "bao ve":
                case "protect":
                case "bảo vệ":
                case "baove":
                    player.Detu.changeStatus(Detu.PROTECT);
                    break;
                case "tan cong":
                case "attack":
                case "tấn công":
                case "tancong":
                    player.Detu.changeStatus(Detu.ATTACK);
                    break;
                case "ve nha":
                case "gohome":
                case "về nhà":
                case "venha":
                    player.Detu.changeStatus(Detu.GOHOME);
                    break;
                case "bien hinh":
                case "bienhinh":
                case "biến hình":
                    player.Detu.transform();
                    break;
                default:
                    break;
            }
        }

        return false;
    }
}
