package nro.achievement;



import nro.inventory.InventoryService;
import nro.player.Player;
import nro.server.Manager;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import network.io.Message;
import consts.ConstAchievement;
import java.io.IOException;
import models.Item.Item;
import models.Item.ItemService;
import nro.mob.Mob;
import nro.skill.Skill;
import nro.template.AchievementTemplate;

public class AchievementService {

    private static AchievementService instance;

    public static AchievementService gI() {
        if (instance == null) {
            instance = new AchievementService();
        }
        return instance;
    }

    public void openAchievementUI(Player player) {
        Message msg = null;
        try {
            msg = new Message(-76);
            msg.writer().writeByte(0);
            msg.writer().writeByte(Manager.ACHIEVEMENT_TEMPLATE.size());
            for (int i = 0; i < Manager.ACHIEVEMENT_TEMPLATE.size(); i++) {
                AchievementTemplate at = Manager.ACHIEVEMENT_TEMPLATE.get(i);
                msg.writer().writeUTF(at.info1); // info 1
                msg.writer().writeUTF(regex(player, at.info2) + " (" + numberToString(player.achievement.getCompleted(i)) + "/" + numberToString(at.maxCount) + ")"); // info 2
                msg.writer().writeShort(at.money); // money
                msg.writer().writeBoolean(player.achievement.isFinish(i, at.maxCount));// isFinish
                msg.writer().writeBoolean(player.achievement.isRecieve(i)); // isRecieve
            }
            player.sendMessage(msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void confirmAchievement(Player player, byte select) {
        if (player.achievement == null) {
            return;
        }
        if (player.baovetaikhoan) {
            Service.gI().sendThongBao(player, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
            return;
        }
        if (!player.achievement.canReward(select)) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            int money = Manager.ACHIEVEMENT_TEMPLATE.get(select).money;
            player.achievement.reward(select);
            player.inventory.ruby += money;
            Item item = ItemService.gI().createNewItem((short) 457, money);
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + money + " hồng ngọc");
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + money + " thỏi vàng");
        } else {
            Service.gI().sendThongBao(player, "Cần tối thiểu 1 ô trống hành trang để nhận thưởng");
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-76);
            msg.writer().writeByte(1);
            msg.writer().writeByte(select);
            player.sendMessage(msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public String numberToString(long num) {
        return num <= 10000 ? num + "" : Util.formatNumber(num, FormatStyle.VIETNAMESE);
    }

    public String regex(Player player, String text) {
        int gen = player.gender;
        return text.replaceAll("%1", gen == 0 ? "Siêu nhân" : gen == 1 ? "Siêu Namếc" : "Siêu Xayda").replaceAll("%2", gen == 0 ? "Bunma" : gen == 1 ? "Dende" : "Appule");
    }

    public void checkDoneTask(Player player, int aId) {
        if (player.isPl() && player.achievement != null) {
            switch (aId) {
                case ConstAchievement.LAN_DAU_NAP_NGOC:
                    player.achievement.doneNotAdd(aId, player.getSession().danap);
                    break;
                default:
                    player.achievement.done(aId, 1);
                    break;
            }
        }
    }

    public void checkDoneTaskKillMob(Player player, Mob mob) {
        try {
            if (mob.lvMob > 0) {
                checkDoneTask(player, ConstAchievement.DANH_BAI_SIEU_QUAI);
            }
            if (mob.type == 4) {
                checkDoneTask(player, ConstAchievement.THO_SAN_THIEN_XA);
            }
            if (mob.tempId == 0) {
                checkDoneTask(player, ConstAchievement.TAP_LUYEN_BAI_BAN);
            }
        } catch (Exception e) {
        }
    }

    public void checkDoneTaskUseSkill(Player player) {
        if (player.isPl()) {
            switch (player.playerSkill.skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC: {
                    checkDoneTask(player, ConstAchievement.NOI_CONG_CAO_CUONG);
                    break;
                }
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALICK:
                case Skill.LIEN_HOAN:
                case Skill.KAIOKEN:
                case Skill.DICH_CHUYEN_TUC_THOI: {
                    break;
                }
                default:
                    checkDoneTask(player, ConstAchievement.KY_NANG_THANH_THAO);
                    break;
            }
        }
    }

    public void checkDoneTaskFly(Player player, int length) {
        if (player.isPl() && player.achievement != null) {
            length = Math.abs(length / 10);
            if (length < 10) {
                player.achievement.done(ConstAchievement.KHINH_CONG_THANH_THAO, length);
            }
        }
    }

}
