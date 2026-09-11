package nro.skill;

import nro.inventory.InventoryService;
import QuanLiBoss.Manager.BossManager;
import nro.server.Manager;
import nro.boss.list.Broly.Broly;
import nro.boss.list.Broly.BrolyZone0;
import nro.boss.list.Broly.SuperBroly;
import nro.boss.list.Broly.SuperBrolyZone0;
import nro.effect.EffectSkillService;
import consts.ConstPlayer;
import nro.intrinsic.Intrinsic;
import nro.mob.Mob;
import nro.mob.DeTrung;
import nro.player.Detu;
import nro.player.Player;
import models.Item.ItemTimeService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import network.io.Message;
import nro.effect.EffectMapService;
import nro.player.PhanThan;
import nro.player.TestDame;
import nro.server.ServerNotify;
import Utils.FormatStyle;
import java.io.IOException;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstAchievement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jbcd.dao.PlayerDAO;
import nro.achievement.AchievementService;
import nro.boss.map.SuperRank.Rival;
import nro.boss.map.Yardart.Yardart;
import nro.npc.NonInteractiveNPC;


public class SkillService {

    private static SkillService instance;

    public static SkillService gI() {
        if (instance == null) {
            instance = new SkillService();
        }
        return instance;
    }

    public boolean useSkill(Player player, Player plTarget, Mob mobTarget, int status, Message msg) {
        long tStart = System.currentTimeMillis();
        long tCheckClan = 0, tSkillData = 0, tEffectCheck = 0, tSkillUse = 0;

        String playerName = player != null ? player.name : "null";
        int skillIdSelected = (player != null && player.playerSkill != null && player.playerSkill.skillSelect != null)
                ? player.playerSkill.skillSelect.template.id : -1;

        try {
            // Check bang hội
            long t0 = System.currentTimeMillis();
            if (plTarget != null && player.clan != null && plTarget.clan != null && player.clan == plTarget.clan
                    && MapService.gI().isMapBlackBallWar(plTarget.zone.map.mapId)) {
                Service.gI().chatJustForMe(player, plTarget, "Ê cùng bang mà");
                return false;
            }
            if (plTarget != null && (player.idNRNM != -1 || plTarget.idNRNM != -1)
                    && player.clan != null && plTarget.clan != null && player.clan == plTarget.clan) {
                Service.gI().chatJustForMe(player, plTarget, "Ê cùng bang mà");
                return false;
            }
            tCheckClan = System.currentTimeMillis() - t0;

            // Đọc skill data
            long t1 = System.currentTimeMillis();
            byte skillId = -1;
            Short dx = -1, dy = -1, x = -1, y = -1;
            byte dir = -1;
            if (status == 20) {
                try {
                    skillId = msg.reader().readByte();
                    dx = msg.reader().readShort();
                    dy = msg.reader().readShort();
                    dir = msg.reader().readByte();
                    x = msg.reader().readShort();
                    y = msg.reader().readShort();
                } catch (IOException e) {
                }
            }
            tSkillData = System.currentTimeMillis() - t1;

            // Check hiệu ứng
            long t2 = System.currentTimeMillis();
            if (player.effectSkill != null && player.effectSkill.isHaveEffectSkill()) {
                return false;
            }
            if (player.playerSkill == null || player.playerSkill.skillSelect == null) {
                return false;
            }
            if (player.playerSkill.skillSelect.template.type == 2 && canUseSkillWithMana(player) && canUseSkillWithCooldown(player)) {
                useSkillBuffToPlayer(player, plTarget);
                return true;
            }
            tEffectCheck = System.currentTimeMillis() - t2;

            // Gọi skill chính
            long t3 = System.currentTimeMillis();
            if (player.PhanThan != null) {
                useSkill(player.PhanThan, plTarget, mobTarget, -1, null);
            }
            if ((player.effectSkill != null && player.effectSkill.isHaveEffectSkill()
                    && (player.playerSkill.skillSelect.template.id != Skill.TU_SAT
                    && player.playerSkill.skillSelect.template.id != Skill.QUA_CAU_KENH_KHI
                    && player.playerSkill.skillSelect.template.id != Skill.MAKANKOSAPPO))
                    || (plTarget != null && !canAttackPlayer(player, plTarget))
                    || (mobTarget != null && mobTarget.isDie())
                    || !canUseSkillWithMana(player) || !canUseSkillWithCooldown(player)) {
                return false;
            }

            if (player.effectSkill != null && player.effectSkill.useTroi) {
                EffectSkillService.gI().removeUseTroi(player);
            }
            if (player.effectSkill != null && player.effectSkill.isCharging) {
                EffectSkillService.gI().stopCharge(player);
            }

            if (status == 20 && skillId != -1 && player.playerSkill.skillSelect.template.id != skillId) {
                selectSkill(player, skillId);
                return false;
            } else {
                switch (player.playerSkill.skillSelect.template.type) {
                    case 1:
                        useSkillAttack(player, plTarget, mobTarget);
                        break;
                    case 3:
                        useSkillAlone(player);
                        break;
                    case 4:
                        useNewSkillNotFocus(player, plTarget, mobTarget, status, skillId, dx, dy, dir, x, y);
                        break;
                    default:
                        return false;
                }
            }
            tSkillUse = System.currentTimeMillis() - t3;
        } catch (Exception e) {
            System.out.println("[ERROR] useSkill exception for player " + playerName + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            long tTotal = System.currentTimeMillis() - tStart;
//            if (tTotal > 1000) { // chỉ log khi vượt quá 1 giây
//                System.out.println("[DELAY] useSkill() - Player: " + playerName + " | SkillID: " + skillIdSelected + " | Total: " + tTotal + "ms"
//                        + " [ClanCheck: " + tCheckClan + "ms | SkillData: " + tSkillData + "ms | EffectCheck: " + tEffectCheck + "ms | SkillUse: " + tSkillUse + "ms]");
//            }
        }

        return true;
    }

    public void sendPhanThan(Player player, Player plTarget, Mob mobTarget) {
        if (BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)) != null) {
            BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)).idSkillPlayer = player.playerSkill.skillSelect.skillId;
            if (plTarget != null) {
                BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)).playertarget = plTarget;
            }
            if (mobTarget != null) {
                BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)).mobTarget = mobTarget;
            }
        }
    }

    public boolean isUseSkill9(int idSkill) {
        return idSkill == Skill.SUPER_KAME || idSkill == Skill.LIEN_HOAN_CHUONG || idSkill == Skill.MA_PHONG_BA;
    }

    private void useNewSkillNotFocus(Player player, Player plTarget, Mob mobTarget, int status, byte skillId, Short dx, Short dy, byte dir, Short x, Short y) {
        try {
            if (skillId == -1 && (plTarget != null || mobTarget != null)) {
                skillId = player.playerSkill.skillSelect.template.id;
                dx = (short) player.location.x;
                dy = (short) player.location.y;
                if (plTarget != null) {
                    x = (short) plTarget.location.x;
                    y = (short) plTarget.location.y;
                } else {
                    x = (short) mobTarget.location.x;
                    y = (short) mobTarget.location.y;
                }
                dir = (byte) (dx > x ? -1 : 1);
            }
            switch (skillId) {
                case Skill.SUPER_KAME:
                case Skill.LIEN_HOAN_CHUONG:
                case Skill.MA_PHONG_BA: {
                    player.newSkill.setSkillSpecial(dir, dx, dy, x, y);
                    newSkillNotFocus(player, status);
                    AchievementService.gI().checkDoneTask(player, ConstAchievement.TUYET_KY_THANH_THAO);
                    break;
                }
            }
            affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        } catch (Exception e) {
        }
    }

    public void updateSkillSpecial(Player player) {
        try {
            if (player.newSkill == null || player.zone == null) {
                return;
            }
            if (player.isDie() || player.effectSkill.isHaveEffectSkill()) {
                player.newSkill.closeSkillSpecial();
                return;
            }
            if (player.newSkill.skillSelect.template.id == Skill.MA_PHONG_BA) {
                if (Util.canDoWithTime(player.newSkill.lastTimeSkillSpecial, NewSkill.TIME_GONG)) {
                    if (Util.isTrue(1, 50) && player.isPl()) {
                        Service.gI().sendThongBao(player, "Bạn đã kiệt sức vì dùng ma phong ba quá nhiều!");
                        player.setDie();
                    }
                    player.newSkill.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.newSkill.closeSkillSpecial();
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }

                    for (Player playerMap : playersMap) {
                        if (playerMap == null || playerMap.id == player.id) {
                            continue;
                        }
                        if (player.newSkill.dir == -1 && !playerMap.isDie() && Util.getDistance(player, playerMap) <= 500 && this.canAttackPlayer(player, playerMap)) {
                            player.newSkill.playersTaget.add(playerMap);

                        } else if (player.newSkill.dir == 1 && !playerMap.isDie() && Util.getDistance(player, playerMap) <= 500 && this.canAttackPlayer(player, playerMap)) {
                            player.newSkill.playersTaget.add(playerMap);
                        }
                    }

                    if (!player.isBoss) {
                        for (Mob mobMap : player.zone.mobs) {
                            if (mobMap == null) {
                                continue;
                            }
                            if (player.newSkill.dir == -1 && !mobMap.isDie() && Util.getDistance(player, mobMap) <= 500) {
                                player.newSkill.mobsTaget.add(mobMap);
                                mobMap.addTemporaryEnemies(player);
                            } else if (player.newSkill.dir == 1 && !mobMap.isDie() && Util.getDistance(player, mobMap) <= 500) {
                                player.newSkill.mobsTaget.add(mobMap);
                                mobMap.addTemporaryEnemies(player);
                            }
                        }
                    }
                    newSkillNotFocus(player, 21);
                    EffectSkillService.gI().startUseMafuba(player, 4000);
                }
            } else {
                if (player.newSkill.stepSkillSpecial == 0 && Util.canDoWithTime(player.newSkill.lastTimeSkillSpecial, NewSkill.TIME_GONG)) {
                    player.newSkill.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.newSkill.stepSkillSpecial = 1;
                    newSkillNotFocus(player, 21);
                } else if (player.newSkill.stepSkillSpecial == 1 && !Util.canDoWithTime(player.newSkill.lastTimeSkillSpecial, NewSkill.TIME_GONG)) {
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }

                    for (Player playerMap : playersMap) {
                        if (playerMap == null || playerMap.id == player.id) {
                            continue;
                        }
                        if (player.newSkill.dir == -1 && player.location.x > playerMap.location.x && !playerMap.isDie()
                                && Math.abs(playerMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                && Math.abs(playerMap.location.y - player.newSkill._yPlayer) <= player.newSkill._yObjTaget
                                && this.canAttackPlayer(player, playerMap)) {
                            this.playerAttackPlayer(player, playerMap, false);
                        }
                        if (player.newSkill.dir == 1 && player.location.x < playerMap.location.x && !playerMap.isDie()
                                && Math.abs(playerMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                && Math.abs(playerMap.location.y - player.newSkill._yPlayer) <= player.newSkill._yObjTaget
                                && this.canAttackPlayer(player, playerMap)) {
                            this.playerAttackPlayer(player, playerMap, false);
                        }
                    }
                    if (!player.isBoss) {
                        for (Mob mobMap : player.zone.mobs) {
                            if (mobMap == null) {
                                continue;
                            }
                            if (player.newSkill.dir == -1 && player.location.x > mobMap.location.x && !mobMap.isDie()
                                    && Math.abs(mobMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                    && Math.abs(mobMap.location.y - player.newSkill._yPlayer) <= player.newSkill._yObjTaget) {
                                this.playerAttackMob(player, mobMap, false, false);
                            }
                            if (player.newSkill.dir == 1 && player.location.x < mobMap.location.x && !mobMap.isDie()
                                    && Math.abs(mobMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                    && Math.abs(mobMap.location.y - player.newSkill._yPlayer) <= player.newSkill._yObjTaget) {
                                this.playerAttackMob(player, mobMap, false, false);
                            }
                        }
                    }
                } else if (player.newSkill.stepSkillSpecial == 1) {
                    player.newSkill.closeSkillSpecial();
                }
            }
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        }
    }

    public void sendCurrLevelSpecial(Player player, Skill skill) {
        Message message = null;
        try {
            message = Service.gI().messageSubCommand((byte) 62);
            message.writer().writeShort(skill.skillId);
            message.writer().writeByte(0);
            message.writer().writeShort(skill.currLevel);
            player.sendMessage(message);
        } catch (final IOException e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (message != null) {
                message.cleanup();
            }
        }
    }

    //____________________________NEW_SKILL_NOT_FOCUS___________________________
    public void newSkillNotFocus(Player player, int status) {
        Message msg = null;
        try {
            NewSkill newSkill = player.newSkill;
            msg = new Message(-45);
            msg.writer().writeByte(status);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(newSkill.skillSelect.template.id);
            if (status == 20) {
                byte typeFrame = 4;
                switch (newSkill.skillSelect.template.id) {
                    case Skill.SUPER_KAME:
                        if (player.isPlayer && player.inventory.itemsBody.get(10).isNotNullItem()) {
                            typeFrame = 1;
                            switch (player.inventory.itemsBody.get(10).template.id) {
                                case 1044:
                                    newSkill.typePaint = 2;
                                    break;
                                case 1278:
                                    newSkill.typePaint = 3;
                                    break;
                                default:
                                    newSkill.typePaint = 0;
                                    break;
                            }
                        } else {
                            newSkill.typePaint = 0;
                            typeFrame = 1;
                        }
                        break;
                    case Skill.LIEN_HOAN_CHUONG:
                        if (player.isPlayer && player.inventory.itemsBody.get(10).isNotNullItem()) {
                            typeFrame = 2;
                            switch (player.inventory.itemsBody.get(10).template.id) {
                                case 1212:
                                    newSkill.typePaint = 2;
                                    break;
                                case 1280:
                                    newSkill.typePaint = 3;
                                    break;
                                default:
                                    newSkill.typePaint = 0;
                                    break;
                            }
                        } else {
                            newSkill.typePaint = 0;
                            typeFrame = 2;
                        }
                        break;
                    case Skill.MA_PHONG_BA:
                        if (player.isPlayer && player.inventory.itemsBody.get(10).isNotNullItem()) {
                            typeFrame = 3;
                            switch (player.inventory.itemsBody.get(10).template.id) {
                                case 1211:
                                    newSkill.typePaint = 3;
                                    break;
                                case 1279:
                                    newSkill.typePaint = 3;
                                    newSkill.typeItem = 2;
                                    break;
                                default:
                                    newSkill.typePaint = 0;
                                    break;
                            }
                        } else {
                            newSkill.typePaint = 0;
                            typeFrame = 3;
                        }
                        break;
                }
                byte dir = newSkill.dir;
                short timeGong = NewSkill.TIME_GONG;
                boolean isFly = false;
                byte typePaint = newSkill.typePaint;
                byte typeItem = newSkill.typeItem;
                msg.writer().writeByte(typeFrame);
                msg.writer().writeByte(dir);
                msg.writer().writeShort(timeGong);
                msg.writer().writeByte((byte) (isFly ? 1 : 0));
                msg.writer().writeByte(typePaint);
                msg.writer().writeByte(typeItem);
            } else if (status == 21) {
                short pointX = (short) (newSkill._xPlayer + ((newSkill.dir == -1) ? (-newSkill._xObjTaget) : newSkill._xObjTaget));
                short pointY = (short) newSkill._yPlayer;
                short timeDame = NewSkill.TIME_GONG;
                short rangeDame = newSkill._yObjTaget;
                byte typePaint = newSkill.typePaint;
                byte typeItem = newSkill.typeItem;
                byte num = (byte) (player.newSkill.playersTaget.size() + player.newSkill.mobsTaget.size());
                msg.writer().writeShort(pointX);
                msg.writer().writeShort(pointY);
                msg.writer().writeShort(timeDame);
                msg.writer().writeShort(rangeDame);
                msg.writer().writeByte(typePaint);
                msg.writer().writeByte(num);
                if (num > 0) {
                    for (Player playerMap : player.newSkill.playersTaget) {
                        msg.writer().writeByte(1);
                        msg.writer().writeInt((int) playerMap.id);
                    }
                    for (Mob mobMap : player.newSkill.mobsTaget) {
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(mobMap.id);
                    }
                }
                msg.writer().writeByte(typeItem);
            }
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void learSkillSpecial(Player player, byte skillID) {
        Message message = null;
        try {
            Skill curSkill = SkillUtil.createSkill(skillID, 1);
            SkillUtil.setSkill(player, curSkill);
            message = Service.gI().messageSubCommand((byte) 23);
            message.writer().writeShort(curSkill.skillId);
            player.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }

        }
    }

    //--------------------------------------------------------------------------
    public void useSkillAttack(Player player, Player plTarget, Mob mobTarget) {
        long time = System.currentTimeMillis();

        long t1 = System.currentTimeMillis();
        if (player.effectSkill != null && player.effectSkill.useTroi) {
            EffectSkillService.gI().removeUseTroi(player);
        }
        long t2 = System.currentTimeMillis();
        sendPhanThan(player, plTarget, mobTarget);
        long t3 = System.currentTimeMillis();
        if (!player.isBoss) {
            if (player.isDeTu) {
                if (player.isPhanThan) {
                    if (player.nPoint.stamina > 0) {
                        player.nPoint.numAttack++;
                        boolean haveCharmPet = ((Detu) player).master.charms != null && ((Detu) player).master.charms.tdDeTu > System.currentTimeMillis();
                        if (haveCharmPet ? player.nPoint.numAttack >= 5 : player.nPoint.numAttack >= 2) {
                            player.nPoint.numAttack = 0;
                            player.nPoint.stamina--;
                        }
                    } else {
                        ((Detu) player).askPea();
                        return;
                    }
                }
            } else {
                if (player.nPoint.stamina > 0) {
                    if (player.charms.tdDeoDai < System.currentTimeMillis()) {
                        player.nPoint.numAttack++;
                        if (player.nPoint.numAttack == 500) {
                            player.nPoint.numAttack = 0;
                            player.nPoint.stamina--;
                            PlayerService.gI().sendCurrentStamina(player);
                        }
                    }
                } else {
                    Service.gI().sendThongBao(player, "Thể lực đã cạn kiệt, hãy nghỉ ngơi để lấy lại sức");
                    return;
                }
            }
        }
        long t4 = System.currentTimeMillis();
        List<Mob> mobs;
        boolean miss = false;
        long tStart = System.currentTimeMillis();
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.KAIOKEN: //kaioken
                long startKaioken = System.currentTimeMillis();
                if (player.nPoint.hp < player.nPoint.hpMax / 10) {
                    Service.gI().sendThongBao(player, "Không thể dùng chiêu khi HP dưới 10%");
                }
                long hpUse = Util.CrisGH(player.nPoint.hpMax / 100 * 10);
                if (player.setClothes.thanVuTruKaio == 4) {
                    hpUse = Util.CrisGH(player.nPoint.hpMax / 100 * 5); // Nếu trang phục là 4, dùng 5% HP
                } else if (player.setClothes.thanVuTruKaio == 5) {
                    hpUse = Util.CrisGH(player.nPoint.hpMax / 100 * 3); // Nếu trang phục là 5, dùng 3% HP
                }
                if (Util.CrisGH(player.nPoint.hp) <= hpUse) {
                    break;
                } else {
                    Service.gI().sendEffAllPlayer(player, 1031, 1, 10, 10);
                    player.nPoint.setHp(Util.CrisGH(player.nPoint.hp - hpUse));
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    Service.gI().Send_Info_NV(player);
                }
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALICK:
            case Skill.LIEN_HOAN:
                long startDam = System.currentTimeMillis();
                //Siêu hạng 113
                if (player.zone != null && player.zone.map.mapId != 113 && plTarget != null && Util.getDistance(player, plTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
                if (mobTarget != null && Util.getDistance(player, mobTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
                long tnow = System.currentTimeMillis();
                if (tnow - startDam >= 1000) {
                    System.out.println("TIME END SKILL : " + (tnow - startDam) + " ms");
                }
            case Skill.KAMEJOKO:
            case Skill.MASENKO:
            case Skill.ANTOMIC:
                long startChuong = System.currentTimeMillis();
                if (plTarget != null) {
                    playerAttackPlayer(player, plTarget, miss);
                }
                if (mobTarget != null) {
                    playerAttackMob(player, mobTarget, miss, false);
                }
                if (player.DeTrung != null) {
                    player.DeTrung.attack(plTarget, mobTarget, miss);
                }
                if (player.playerSkill != null) {
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            //******************************************************************
            case Skill.QUA_CAU_KENH_KHI:
                long startQCKK = System.currentTimeMillis();
                if (!player.playerSkill.prepareQCKK) {
                    //bắt đầu tụ quả cầu
                    player.playerSkill.prepareQCKK = true;
                    player.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 4000);
                } else {
                    //ném cầu
                    player.playerSkill.prepareQCKK = false;
                    mobs = new ArrayList<>();
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false);
                        if (!player.isBoss) {
                            for (Mob mob : player.zone.mobs) {
                                if (!mob.isDie()
                                        && Util.getDistance(plTarget, mob) <= SkillUtil.getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                    mobs.add(mob);
                                }
                            }
                        }
                    }
                    if (mobTarget != null) {
                        if (!player.isBoss) {
                            playerAttackMob(player, mobTarget, false, true);
                            for (Mob mob : player.zone.mobs) {
                                if (!mob.equals(mobTarget) && !mob.isDie()
                                        && Util.getDistance(mob, mobTarget) <= SkillUtil.getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                    mobs.add(mob);
                                }
                            }
                        }
                    }
                    for (Mob mob : mobs) {
                        mob.injured(player, Util.CrisGH(player.nPoint.getDameAttack(true)), true);
                    }
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            case Skill.MAKANKOSAPPO:
                long startMakenko = System.currentTimeMillis();
                if (!player.playerSkill.prepareLaze) {
                    //bắt đầu nạp laze
                    player.playerSkill.prepareLaze = true;
                    player.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 3000);
                } else {
                    //bắn laze
                    player.playerSkill.prepareLaze = false;
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false);
                    }
                    if (mobTarget != null) {
                        playerAttackMob(player, mobTarget, false, true);
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                PlayerService.gI().sendInfoHpMpMoney(player);
                break;
            case Skill.SOCOLA:
                long startSicula = System.currentTimeMillis();
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.SOCOLA);
                int timeSocola = SkillUtil.getTimeSocola();
                if (plTarget != null) {
                    EffectSkillService.gI().setSocola(plTarget, System.currentTimeMillis(), timeSocola);
                    Service.gI().Send_Caitrang(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 4133, timeSocola / 1000);
                }
                if (mobTarget != null) {
                    EffectSkillService.gI().sendMobToSocola(player, mobTarget, timeSocola);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;

            case Skill.DICH_CHUYEN_TUC_THOI:
                long startDCTT = System.currentTimeMillis();
                int timeChoangDCTT = SkillUtil.getTimeDCTT(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    if (player.isBoss) {
                        Service.gI().chat(player, "Dịch chuyển tức thời");
                    }
                    Service.gI().setPos(player, plTarget.location.x, plTarget.location.y);
                    playerAttackPlayer(player, plTarget, miss);
                    if (plTarget.nPoint != null && plTarget.nPoint.tlFixStun > 0 && plTarget.effectSkill != null && plTarget.effectSkill.isShielding) {
                        int fix = Math.min(plTarget.nPoint.tlFixStun, 100);
                        int reducedTime = timeChoangDCTT * (100 - fix) / 100;
                        int minTime = timeChoangDCTT / 10;
                        if (reducedTime < minTime) {
                            reducedTime = minTime;
                        }
                        timeChoangDCTT = reducedTime;
                    }
                    EffectSkillService.gI().setBlindDCTT(plTarget, System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.BLIND_EFFECT);
                    PlayerService.gI().sendInfoHpMpMoney(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 3779, timeChoangDCTT / 1000);
                }
                if (mobTarget != null) {
                    Service.gI().setPos(player, mobTarget.location.x, mobTarget.location.y);
                    playerAttackMob(player, mobTarget, false, false);
                    mobTarget.effectSkill.setStartBlindDCTT(System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.BLIND_EFFECT);
                }
                player.nPoint.isCrit100 = true;
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.THOI_MIEN:
                long startTM = System.currentTimeMillis();
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.THOI_MIEN);
                int timeSleep = SkillUtil.getTimeThoiMien(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    if (plTarget.nPoint != null && plTarget.nPoint.tlFixStun > 0 && plTarget.effectSkill != null && plTarget.effectSkill.isShielding) {
                        int fix = Math.min(plTarget.nPoint.tlFixStun, 100);
                        int reducedTime = timeSleep * (100 - fix) / 100;
                        int minTime = timeSleep / 10;
                        if (reducedTime < minTime) {
                            reducedTime = minTime;
                        }
                        timeSleep = reducedTime;
                    }
                    EffectSkillService.gI().setThoiMien(plTarget, System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SLEEP_EFFECT);
                    ItemTimeService.gI().sendItemTime(plTarget, 3782, timeSleep / 1000);
                }
                if (mobTarget != null) {
                    mobTarget.effectSkill.setThoiMien(System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SLEEP_EFFECT);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TROI:
                long startTroi = System.currentTimeMillis();
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.TROI);
                int timeHold = SkillUtil.getTimeTroi(player.playerSkill.skillSelect.point);
                EffectSkillService.gI().setUseTroi(player, System.currentTimeMillis(), timeHold);
                if (plTarget != null && (!plTarget.playerSkill.prepareQCKK && !plTarget.playerSkill.prepareLaze && !plTarget.playerSkill.prepareTuSat)) {
                    player.effectSkill.plAnTroi = plTarget;
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HOLD_EFFECT);
                    EffectSkillService.gI().setAnTroi(plTarget, player, System.currentTimeMillis(), timeHold);
                }
                if (mobTarget != null) {
                    player.effectSkill.mobAnTroi = mobTarget;
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HOLD_EFFECT);
                    mobTarget.effectSkill.setTroi(System.currentTimeMillis(), timeHold);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
        }

        long t5 = System.currentTimeMillis();
        if (!player.isBoss) {
            switch (Objects.requireNonNull(player.playerSkill).skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALICK:
                case Skill.LIEN_HOAN:
                case Skill.KAIOKEN:
                case Skill.QUA_CAU_KENH_KHI:
                case Skill.MAKANKOSAPPO:
                case Skill.DICH_CHUYEN_TUC_THOI:
                    player.effectSkin.lastTimeAttack = System.currentTimeMillis();
                    break;
            }
            AchievementService.gI().checkDoneTaskUseSkill(player);
            player.doesNotAttack = false;
            player.lastTimePlayerNotAttack = System.currentTimeMillis();
            player.doesNotAttackBo = false;
            player.lastTimePlayerNotAttackBo = System.currentTimeMillis();
            player.doesNotAttackMe = false;
            player.lastTimePlayerNotAttackMe = System.currentTimeMillis();
            player.doesNotAttackNguoiYeu = false;
            player.lastTimePlayerNotAttackNguoiYeu = System.currentTimeMillis();
            player.doesNotAttackConone = false;
            player.lastTimePlayerNotAttackConone = System.currentTimeMillis();
            player.doesNotAttackContwo = false;
            player.lastTimePlayerNotAttackContwo = System.currentTimeMillis();
            player.doesNotAttackConthree = false;
            player.lastTimePlayerNotAttackConthree = System.currentTimeMillis();
        }
        long t6 = System.currentTimeMillis();
        if (t6 - time >= 1000) {
            System.out.println("[DEBUG] useSkillAttack() - Player: " + player.name
                    + " | SkillID: " + player.playerSkill.skillSelect.template.id
                    + " | Time: Total=" + (t6 - time) + "ms"
                    + " [Stamina=" + (t2 - t1) + "ms | SkillCore=" + (t3 - t2)
                    + "ms | SpecialSkill=" + (t4 - t3) + "ms | Effects=" + (t5 - t4) + "ms | Post=" + (t6 - t5) + "ms]");
        }

    }

    private void useSkillAlone(Player player) {
        List<Mob> mobs;
        List<Player> players;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.THAI_DUONG_HA_SAN:
                int timeStun = SkillUtil.getTimeStun(player.playerSkill.skillSelect.point);
                if (player.setClothes.thienXinHang == 5) {
                    timeStun *= 2;
                }
                mobs = new ArrayList<>();
                players = new ArrayList<>();
                if (!MapService.gI().isHome(player.zone.map.mapId)) {
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }
                    for (Player pl : playersMap) {
                        if (pl != null && !player.equals(pl) && pl.nPoint != null && !pl.nPoint.khangTDHS) {
                            if (Util.getDistance(player, pl) <= SkillUtil.getRangeStun(player.playerSkill.skillSelect.point)
                                    && canAttackPlayer(player, pl)) {
                                if (player.isDeTu && ((Detu) player).master.equals(pl)) {
                                    continue;
                                }
                                String[] text = {"Mắt của ta", "Chói mắt quá", "Đui mắt rồi", "Mù mắt rồi"};
                                Service.gI().chat(pl, text[Util.nextInt(text.length)]);
                                if (pl.idNRNM >= 353 && pl.idNRNM <= 359) {
                                    timeStun /= 2;
                                }
                                if (pl.nPoint != null && pl.nPoint.tlFixStun > 0 && pl.effectSkill != null && pl.effectSkill.isShielding) {
                                    int fix = Math.min(pl.nPoint.tlFixStun, 100);
                                    int reducedTime = timeStun * (100 - fix) / 100;
                                    int minTime = timeStun / 10;
                                    if (reducedTime < minTime) {
                                        reducedTime = minTime;
                                    }
                                    timeStun = reducedTime;
                                }
                                EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), timeStun);
                                players.add(pl);
                            }
                        }
                    }
                }
                if (!player.isBoss) {
                    for (Mob mob : player.zone.mobs) {
                        if (Util.getDistance(player, mob) <= SkillUtil.getRangeStun(player.playerSkill.skillSelect.point)) {
                            mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                            mobs.add(mob);
                        }
                    }
                }
                EffectSkillService.gI().sendEffectBlindThaiDuongHaSan(player, players, mobs, timeStun);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DE_TRUNG:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.DE_TRUNG);
                if (player.DeTrung != null) {
                    player.DeTrung.mobMeDie();
                    player.DeTrung.dispose();
                    player.DeTrung = null;
                }
                player.DeTrung = new DeTrung(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.BIEN_KHI:
                EffectSkillService.gI().startUseSkillMonkey(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.KHIEN_NANG_LUONG:
                EffectSkillService.gI().setStartShield(player);
                EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SHIELD_EFFECT);
                ItemTimeService.gI().sendItemTime(player, 3784, player.effectSkill.timeShield / 1000);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.HUYT_SAO:
                int tileHP = SkillUtil.getPercentHPHuytSao(player.playerSkill.skillSelect.point);
                if (player.zone != null) {
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        if (!player.isBoss) {
                            List<Player> playersMap = player.zone.getHumanoids();
                            for (Player pl : playersMap) {
                                if (pl.effectSkill != null && pl.effectSkill.useTroi) {
                                    EffectSkillService.gI().removeUseTroi(pl);
                                }
                                if (!pl.isBoss && pl.gender != ConstPlayer.NAMEC
                                        && player.cFlag == pl.cFlag) {
                                    EffectSkillService.gI().setStartHuytSao(pl, tileHP);
                                    EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HUYT_SAO_EFFECT);
                                    pl.nPoint.calPoint();
                                    pl.nPoint.setHp(Util.CrisGH(pl.nPoint.hp + pl.nPoint.hp * tileHP / 100));
                                    Service.gI().point(pl);
                                    Service.gI().Send_Info_NV(pl);
                                    ItemTimeService.gI().sendItemTime(pl, 3781, 30);
                                    PlayerService.gI().sendInfoHpMp(pl);
                                } else if (!pl.isBoss && pl.gender == ConstPlayer.NAMEC && player.cFlag == pl.cFlag) {
                                    pl.nPoint.setHp(Util.CrisGH(pl.nPoint.hp - (pl.nPoint.hpMax * 10 / 100) < pl.nPoint.hp ? (pl.nPoint.hpMax * 10 / 100) : 0));
                                    Service.gI().point(pl);
                                    Service.gI().Send_Info_NV(pl);
                                }
                            }
                        } else {
                            List<Player> playersMap = player.zone.getBosses();
                            for (Player pl : playersMap) {
                                if (pl.effectSkill.useTroi) {
                                    EffectSkillService.gI().removeUseTroi(pl);
                                }
                                EffectSkillService.gI().setStartHuytSao(pl, tileHP);
                                EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HUYT_SAO_EFFECT);
                                pl.nPoint.calPoint();
                                pl.nPoint.setHp(Util.CrisGH(pl.nPoint.hp + pl.nPoint.hp * tileHP / 100));
                                Service.gI().point(pl);
                                Service.gI().Send_Info_NV(pl);
                                ItemTimeService.gI().sendItemTime(pl, 3781, 30);
                                PlayerService.gI().sendInfoHpMp(pl);
                            }
                        }
                    } else {
                        EffectSkillService.gI().setStartHuytSao(player, tileHP);
                        EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HUYT_SAO_EFFECT);
                        player.nPoint.calPoint();
                        player.nPoint.setHp(Util.CrisGH(player.nPoint.hp + player.nPoint.hp * tileHP / 100));
                        Service.gI().point(player);
                        Service.gI().Send_Info_NV(player);
                        ItemTimeService.gI().sendItemTime(player, 3781, 30);
                        PlayerService.gI().sendInfoHpMp(player);
                    }
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TAI_TAO_NANG_LUONG:
                EffectSkillService.gI().startCharge(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TU_SAT:
                if (!player.playerSkill.prepareTuSat) {
                    //gồng tự sát
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    player.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
                    sendPlayerPrepareBom(player, 2000);
                } else {
                    if (!player.isBoss && !player.isDeTu && !player.isBo && !player.isMe && !Util.canDoWithTime(player.playerSkill.lastTimePrepareTuSat, 1500)) {
                        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
                        player.playerSkill.prepareTuSat = false;
                        return;
                    }
                    if (player.isBoss || player.isDeTu || player.isBo || player.isMe) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ex) {
                        }
                    }
                    //nổ
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    int rangeBom = SkillUtil.getRangeBom(player.playerSkill.skillSelect.point);
                    if (player.setClothes.cadicM == 2) {
                        rangeBom = SkillUtil.getRangeBom(player.playerSkill.skillSelect.point) + 200;
                    }
                    double dame = Util.CrisGH(player.nPoint.hpMax);
                    if (player.setClothes.cadicM == 4) {
                        dame += player.nPoint.hpMax * 20 / 100;
                    } else if (player.setClothes.cadicM == 5) {
                        dame += player.nPoint.hpMax * 50 / 100;
                    }
                    if (!player.isBoss) {
                        for (Mob mob : player.zone.mobs) {
                            if (Util.getDistance(player, mob) <= rangeBom) { //khoảng cách có tác dụng bom
                                mob.injured(player, dame, true);
                            }
                        }
                    }
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        for (Player pl : playersMap) {
                            if (!player.equals(pl) && canAttackPlayer(player, pl) && Util.getDistance(player, pl) <= rangeBom) {
                                dame = pl.isBoss ? player.effectSkill.isMonkey ? dame / 3 : dame / 2 : dame;
                                pl.injured(player, dame, MapService.gI().isMapYardart(player.zone.map.mapId), false);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.gI().Send_Info_NV(pl);
                            }
                        }
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                    if (!player.isBoss && !player.isDeTu && !player.isBo && !player.isMe) {
                        player.setDie();
                    }
                    if (player.effectSkill.tiLeHPHuytSao != 0) {
                        player.effectSkill.tiLeHPHuytSao = 0;
                        EffectSkillService.gI().removeHuytSao(player);
                    }
                }
                break;
        }
    }

    private void useSkillBuffToPlayer(Player player, Player plTarget) {
        Message msg = null;
        if (player.playerSkill.skillSelect.template.id == Skill.TRI_THUONG) {
            List<Player> players = new ArrayList<>();
            int percentTriThuong = SkillUtil.getPercentTriThuong(player.playerSkill.skillSelect.point);
            int point = player.playerSkill.skillSelect.point;
            if (canHsPlayer(player, plTarget)) {
                players.add(plTarget);
                List<Player> playersMap = player.zone.getNotBosses();
                for (Player pl : playersMap) {
                    if (!pl.equals(plTarget) && point > 1) {
                        if (canHsPlayer(player, plTarget) && Util.getDistance(player, pl) <= 300) {
                            players.add(pl);
                        }
                    }
                }
                for (Player pl : players) {
                    try {
                        msg = new Message(-60);
                        msg.writer().writeInt((int) player.id); //id pem
                        msg.writer().writeByte(player.playerSkill.skillSelect.skillId); //skill pem
                        msg.writer().writeByte(1); //số người pem
                        msg.writer().writeInt((int) pl.id); //id ăn pem
                        msg.writer().writeByte(0); //read continue
                        Service.gI().sendMessAllPlayerInMap(pl, msg);
                        boolean isDie = pl.isDie();
                        player.nPoint.setHP(Util.CrisGH(player.nPoint.getHP() + ((long) player.nPoint.hpMax * percentTriThuong / 100)));
                        pl.nPoint.setHP(Util.CrisGH(pl.nPoint.getHP() + ((long) pl.nPoint.hpMax * percentTriThuong / 100)));
                        pl.nPoint.setMP(Util.CrisGH(pl.nPoint.getMP() + ((long) pl.nPoint.mpMax * percentTriThuong / 100)));
                        if (isDie) {
                            AchievementService.gI().checkDoneTask(pl, ConstAchievement.CHAM_SOC_DAC_BIET);
                            Service.gI().chat(pl, "Cảm ơn " + player.name + " đã hồi sinh mình");
                            Service.gI().Send_Info_NV(player);
                            Service.gI().hsChar(pl, pl.nPoint.getHP(), pl.nPoint.getMP());
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            PlayerService.gI().sendInfoHpMp(player);
                        } else {
                            Service.gI().chat(pl, "Cảm ơn " + player.name + " đã cứu mình");
                            Service.gI().Send_Info_NV(player);
                            PlayerService.gI().sendInfoHpMp(pl);
                            PlayerService.gI().sendInfoHpMp(player);
                        }
                        Service.gI().Send_Info_NV(pl);
                    } catch (Exception e) {
                        Logger.logException(SkillService.class, e);
                    } finally {
                        if (msg != null) {
                            msg.cleanup();
                        }
                    }
                }
            }
            affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        }
    }

    private void PhanSatThuong(Player plAtt, Player plTarget, double dame) {
        if (plAtt != null) {
            int percentPST = plTarget.nPoint.tlPST;
            if (percentPST != 0) {
                double damePST = Util.CrisGH(dame * percentPST / 100L);
                Message msg = null;
                try {
                    msg = new Message(56);
                    msg.writer().writeInt((int) plAtt.id);
                    if (damePST >= plAtt.nPoint.hp) {
                        damePST = plAtt.nPoint.hp - 1;
                    }
                    if (plAtt.isBoss && !(plAtt instanceof Broly || plAtt instanceof BrolyZone0 || plAtt instanceof SuperBrolyZone0 || plAtt instanceof SuperBroly)) {
                        if (damePST > plAtt.nPoint.hpMax / 100) {
                            int giamdame = 0;
                            if (plAtt.nPoint.hpMax / 200 > 1) {
                                giamdame = Util.nextInt((int) plAtt.nPoint.hpMax / 200);
                            }
                            damePST = plAtt.nPoint.hpMax / 100 - giamdame;
                        }
                    }
                    damePST = plAtt.injured(plAtt, damePST, true, false);
                    msg.writeCris(Util.CrisGH(plAtt.nPoint.hp), Manager.readInt);
                    msg.writeCris(Util.CrisGH(damePST), Manager.readInt);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeByte(36);
                    Service.gI().sendMessAllPlayerInMap(plAtt, msg);
                } catch (Exception e) {
                    Logger.logException(SkillService.class, e);
                } finally {
                    if (msg != null) {
                        msg.cleanup();
                    }
                }
            }
        }
    }

    private void hutHPMP(Player player, double dame, Player pl, Mob mob) {
        int tiLeHutHp = player.nPoint.getTileHutHp(mob != null);
        int tiLeHutMp = player.nPoint.getTiLeHutMp();
        long hpHoi = Util.CrisGH(dame * tiLeHutHp / 100);
        long mpHoi = Util.CrisGH(dame * tiLeHutMp / 100);
        if (hpHoi > 0 || mpHoi > 0) {
            int x = -1;
            int y = -1;
            if (pl != null) {
                x = pl.location.x;
                y = pl.location.y;
            } else if (mob != null) {
                x = mob.location.x;
                y = mob.location.y;
            }
            EffectMapService.gI().sendEffectMapToAllInMap(player, 37, 3, 1, x, y, -1);
            PlayerService.gI().hoiPhuc(player, Util.CrisGH(hpHoi), Util.CrisGH(mpHoi));
        }
    }

    private void playerAttackPlayer(Player plAtt, Player plInjure, boolean miss) {
        if (plInjure.effectSkill.anTroi) {
            plAtt.nPoint.isCrit100 = true;
        }

        // ============================================================
        // 🔹 1. TÍNH SÁT THƯƠNG THEO CONFIG SKILL
        // ============================================================
        double baseDame = plAtt.nPoint.getDameAttack(false);
        double multiplier = 1.0;

        if (plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
            try {
                multiplier = nro.config.SkillDamageConfig.getMultiplier(plAtt.playerSkill.skillSelect.template.id);
            } catch (Exception e) {
                multiplier = 1.0; // fallback nếu lỗi
            }
        }

        double finalDame = baseDame * multiplier;

        // ============================================================
        // 🔹 2. HIỆU ỨNG ĐẶC BIỆT & BONUS KHÁC
        // ============================================================
        if (plAtt.isPl() && plAtt.effectSkin != null && plAtt.effectSkin.isXDame) {
            plAtt.effectSkin.isXDame = false;
            if (plInjure.isBoss) {
                finalDame /= 3; // giảm dame khi đánh boss nếu có skin
            }
        }

        if (plAtt.isPlMan()) {
            int tlDameBoss = plAtt.nPoint.tlDameBoss;
            if (tlDameBoss > 0 && plInjure.isBoss) {
                finalDame += Util.CrisGH((finalDame / 100) * tlDameBoss);
            }
        }

        // ============================================================
        // 🔹 3. TÍNH TOÁN DAME GÂY RA
        // ============================================================
        double dameHit = plInjure.injured(plAtt, miss ? 0 : finalDame, false, false);
        if (plAtt.playerSkill == null) {
            return;
        }

        Skill skillSelect = plAtt.playerSkill.skillSelect;

        // Hiển thị thông báo khi sát thương quá lớn
        if (plAtt.isPl() && dameHit >= 150_000_000) {
            ServerNotify.gI().notify(
                    plAtt.name + ": đã đánh 1 chiêu " + skillSelect.template.name
                    + " với sát thương là " + Util.formatNumber(Util.CrisGH(dameHit), FormatStyle.VIETNAMESE)
            );
        }

        // ============================================================
        // 🔹 4. PHẢN SÁT THƯƠNG & HÚT HP/MP
        // ============================================================
        double damePST = (plInjure.effectSkill != null && plInjure.effectSkill.isShielding && plInjure.iDMark != null)
                ? plInjure.iDMark.getDamePST() : dameHit;

        PhanSatThuong(plAtt, plInjure, miss ? 0 : damePST);
        hutHPMP(plAtt, dameHit, plInjure, null);

        // ============================================================
        // 🔹 5. FIX MÁU TRẮNG YARDART
        // ============================================================
        if (plInjure instanceof Yardart) {
            if (plInjure.nPoint.hp < dameHit) {
                dameHit = plInjure.nPoint.hp - 1;
                if (dameHit == 0) {
                    return;
                }
            } else if (plInjure.nPoint.hp <= plInjure.nPoint.hpMax / 10) {
                return;
            }
        }

        // ============================================================
        // 🔹 6. GỬI GÓI TIN SÁT THƯƠNG
        // ============================================================
        Message msg = null;
        try {
            msg = new Message(-60);
            msg.writer().writeInt((int) plAtt.id); // id người đánh
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId); // skill id
            msg.writer().writeByte(1); // số mục tiêu
            msg.writer().writeInt((int) plInjure.id); // id bị đánh
            msg.writer().writeByte(1); // continue
            msg.writer().writeByte(0); // type skill

            if (dameHit > Integer.MAX_VALUE && Manager.readInt) {
                if (dameHit > Integer.MAX_VALUE - 10) {
                    Service.getInstance().sendThongBao(plAtt,
                            "|2|Sát Thương  ️" + " \b|0|" + "-" + Util.formatNumber(dameHit, FormatStyle.VIETNAMESE));
                }
                dameHit = Integer.MAX_VALUE - 9;
                msg.writeCris(Util.CrisGH(dameHit), Manager.readInt);
            } else {
                msg.writeCris(Util.CrisGH(dameHit), Manager.readInt);
            }

            msg.writer().writeBoolean(plInjure.isDie());
            msg.writer().writeBoolean(plAtt.nPoint.isCrit);
            Service.gI().sendMessAllPlayerInMap(plAtt, msg);
            Service.gI().reload_HP_NV(plInjure);

            // ========================================================
            // 🔹 7. PVP THƯỞNG SỨC MẠNH TIỀM NĂNG
            // ========================================================
            if (plAtt.isPl() && plInjure.isPl()
                    && plAtt.typePk == ConstPlayer.PK_PVP_2
                    && plInjure.typePk == ConstPlayer.PK_PVP_2) {
                long tnsm = plAtt.nPoint.calSucManhTiemNang(Util.CrisGH(dameHit / 10))
                        / (Math.abs(Service.gI().getCurrLevel(plAtt) - Service.gI().getCurrLevel(plInjure)) + 1);
                Service.gI().addSMTN(plInjure, (byte) 2, tnsm, false);
            }

            // ========================================================
            // 🔹 8. SỰ KIỆN MABU 12H
            // ========================================================
            if (plInjure.isDie() && !plAtt.isBoss && !plInjure.isBoss
                    && MapService.gI().isMapMaBu12H(plInjure.zone.map.mapId)) {
                plAtt.fightMabu.changePoint((byte) 5);
            }

            // ========================================================
            // 🔹 9. HIỂN THỊ HP LỚN HƠN GIỚI HẠN
            // ========================================================
            if (!plInjure.isDie() && plInjure.nPoint.hp > Integer.MAX_VALUE && Manager.readInt) {
                try {
                    msg = new Message(44);
                    msg.writer().writeInt((int) plInjure.id);
                    msg.writer().writeUTF("|2|HP " + "\b|7| " + Util.format(plInjure.nPoint.hp));
                    Service.getInstance().sendMessAllPlayerInMap(plInjure, msg);
                    msg.cleanup();
                } catch (IOException e) {
                    Logger.logException(Service.class, e);
                }
            }

            // ========================================================
            // 🔹 10. TEST DAME MODE
            // ========================================================
            if (dameHit < Integer.MAX_VALUE && plInjure instanceof TestDame) {
                plAtt.dametong += dameHit;
                Service.getInstance().sendThongBao(plAtt,
                        "|0|Dame Thật: \b|5|" + Util.format(dameHit)
                        + "\n\n|1| Tổng DAME Trong 5 Giây: \b|7|"
                        + Util.formatNumber(plAtt.dametong, FormatStyle.VIETNAMESE));

                if (plAtt.resetdame) {
                    plAtt.lastTimeDame = System.currentTimeMillis();
                    plAtt.resetdame = false;
                }
            }

        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void playerAttackMob(Player plAtt, Mob mob, boolean miss, boolean dieWhenHpFull) {
        if (mob == null || mob.isDie() || plAtt == null || plAtt.nPoint == null || plAtt.playerSkill == null) {
            return;
        }

        // 1. Tính dame cơ bản
        double dameHit = plAtt.nPoint.getDameAttack(true);

        // 2. Kiểm tra hiệu ứng da, hiệu ứng bất tử
        if (plAtt.isPl() && plAtt.effectSkin != null && plAtt.effectSkin.isXDame) {
            plAtt.effectSkin.isXDame = false;
        }

        if ((plAtt.charms != null && plAtt.charms.tdBatTu > System.currentTimeMillis()
                || (plAtt.effectSkill != null && plAtt.effectSkill.isHalloween)) && plAtt.nPoint.hp <= 1) {
            if (plAtt.nPoint.hp < 1) {
                plAtt.nPoint.hp = 1;
            }
            if (!plAtt.isDeTu) {
                dameHit = 0;
                Service.gI().sendThongBao(plAtt, "Bạn đang được bùa bất tử bảo vệ không thể tấn công!");
            }
        }

        // 3. Kiểm tra bùa mạnh mẽ tăng dame
        if (plAtt.charms != null && plAtt.charms.tdManhMe > System.currentTimeMillis()) {
            dameHit += (dameHit * 150 / 100);
        }

        // 4. Kiểm tra buff clan
        if (plAtt.clan != null && plAtt.clan.BuaManhMe > System.currentTimeMillis()) {
            int clanLevel = plAtt.clan.level;
            int bonusPercent = clanLevel * 10;
            if (bonusPercent > 100) {
                bonusPercent = 100;
            }
            dameHit += (dameHit * bonusPercent / 100);
        }
        // 5. Kiểm tra Detu master buff
        if (plAtt.isDeTu) {
            if (((Detu) plAtt).master != null && ((Detu) plAtt).master.charms != null
                    && ((Detu) plAtt).master.charms.tdDeTu > System.currentTimeMillis()) {
                dameHit *= 2;
            }
        }

        // 6. Kiểm tra miss
        if (miss) {
            dameHit = 0;
        }

        // 7. Giới hạn dame đối với siêu quái
        if (mob.isSieuQuai()) {
            if (dameHit > mob.point.maxHp / 10) {
                dameHit = mob.point.maxHp / 10;
            }
        }

        if (dameHit > 2_000_000_000) {
            dameHit = 2_000_000_000;
        }
        hutHPMP(plAtt, dameHit, null, mob);
        sendPlayerAttackMob(plAtt, mob);
        mob.injured(plAtt, dameHit, dieWhenHpFull);
    }

    private void sendPlayerPrepareSkill(Player player, int affterMiliseconds) {
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(4);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void sendPlayerPrepareBom(Player player, int affterMiliseconds) {
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(7);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public boolean canUseSkillWithMana(Player player) {
        if (player.playerSkill.skillSelect != null) {
            if (player.playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
                long hpUse = Util.CrisGH(player.nPoint.hpMax / 100 * 10);
                if (player.isBoss && player instanceof Rival) {
                    hpUse = 0;
                }
                if (Util.CrisGH(player.nPoint.hp) <= hpUse) {
                    return false;
                }
            }
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0: {
                    return Util.CrisGH(player.nPoint.mp) >= player.playerSkill.skillSelect.manaUse;
                }
                case 1: {
                    long mpUse = Util.CrisGH((player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100));
                    return Util.CrisGH(player.nPoint.mp) >= mpUse;
                }
                case 2: {
                    return Util.CrisGH(player.nPoint.mp) > 0;
                }
                default: {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public boolean canUseSkillWithCooldown(Player player) {
        return Util.canDoWithTime(player.playerSkill.skillSelect.lastTimeUseThisSkill, player.playerSkill.skillSelect.coolDown - 50);
    }

    public void affterUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        switch (skillId) {
            case Skill.DICH_CHUYEN_TUC_THOI: {
                if (intrinsic.id == 6) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            }
            case Skill.THOI_MIEN: {
                if (intrinsic.id == 7) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            }
            case Skill.SOCOLA: {
                if (intrinsic.id == 14) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            }
            case Skill.TROI: {
                if (intrinsic.id == 22) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            }
        }
        setMpAffterUseSkill(player);
        setLastTimeUseSkill(player, skillId);
    }

    private void setMpAffterUseSkill(Player player) {
        if (player.isNguoiYeu || player.isConOne || player.isConTwo || player.isConThree) {
            return;
        }
        if (player.playerSkill.skillSelect != null) {
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0: {
                    if (Util.CrisGH(player.nPoint.mp) >= player.playerSkill.skillSelect.manaUse) {
                        player.nPoint.setMp(Util.CrisGH(player.nPoint.mp - player.playerSkill.skillSelect.manaUse));
                    }
                    break;
                }
                case 1: {
                    long mpUse = Util.CrisGH(player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100);
                    if (Util.CrisGH(player.nPoint.mp) >= mpUse) {
                        player.nPoint.setMp(Util.CrisGH(player.nPoint.mp - mpUse));
                    }
                    break;
                }
                case 2:
                    player.nPoint.setMp(Util.CrisGH(0));
                    break;
            }
            PlayerService.gI().sendInfoHpMpMoney(player);
        }
    }

    private void setLastTimeUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        int subTimeParam = 0;
        int subTimeParamVip = 0;
        switch (skillId) {
            case Skill.TRI_THUONG:
                if (intrinsic.id == 10) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.THAI_DUONG_HA_SAN:
                if (intrinsic.id == 3) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.QUA_CAU_KENH_KHI:
                if (intrinsic.id == 4) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.KHIEN_NANG_LUONG:
                if (intrinsic.id == 5 || intrinsic.id == 15 || intrinsic.id == 20) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.MAKANKOSAPPO:
                if (intrinsic.id == 11) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.DE_TRUNG:
                if (intrinsic.id == 12) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.TU_SAT:
                if (intrinsic.id == 19) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.HUYT_SAO:
                if (intrinsic.id == 21) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.KAMEJOKO:
                if (player.setClothes.frieza == 5) {
                    subTimeParam = 80;
                }
                break;
            case Skill.MASENKO:
                if (player.setClothes.frieza == 5) {
                    subTimeParam = 80;
                }
                if (player.setClothes.nail == 4) {
                    subTimeParam = 20;
                } else if (player.setClothes.nail == 5) {
                    subTimeParam = 50;
                }
                break;
            case Skill.ANTOMIC:
                if (player.setClothes.frieza == 5) {
                    subTimeParam = 80;
                }
                break;

        }
        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis() - 1;
        int coolDown = player.playerSkill.skillSelect.coolDown;
        long lastTimeUseSkill = System.currentTimeMillis() - (coolDown * (subTimeParam + subTimeParamVip) / 100);
        if (subTimeParam != 0) {
            EffectSkillService.gI().setIntrinsic(player, skillId, coolDown, lastTimeUseSkill);
        }
        if (subTimeParamVip != 0) {
            EffectSkillService.gI().setIntrinsicVip(player, skillId, coolDown, lastTimeUseSkill);
        }
    }

    private boolean canHsPlayer(Player player, Player plTarget) {
        if (plTarget == null) {
            return false;
        }
        if (plTarget.isBoss) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_ALL) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_PVP) {
            return false;
        }
        if (player.cFlag != 0) {
            if (plTarget.cFlag != 0 && plTarget.cFlag != player.cFlag) {
                return false;
            }
        } else {
            return plTarget.cFlag == 0;
        }
        return true;
    }

    public boolean canAttackPlayer(Player p1, Player p2) {
        if (p1.isDie() || p2.isDie()) {
            return false;
        }
        return canAttackPlayer2(p1, p2);
    }

    public boolean canAttackPlayer2(Player p1, Player p2) {
        if (p1.isPetFollow || p2.isPetFollow || p1.isDuongTang || p2.isDuongTang || (p1 instanceof NonInteractiveNPC) || (p2 instanceof NonInteractiveNPC)) {
            return false;
        }
        if (p1.typePk == ConstPlayer.PK_ALL || p2.typePk == ConstPlayer.PK_ALL) {
            return true;
        }
        if (p1.isPl() && p2.isPl() && (p1.iDMark != null && p1.iDMark.getKillCharId() == p2.id
                || p2.iDMark != null && p2.iDMark.getKillCharId() == p1.id)) {
            return true;
        }
        if ((p1.cFlag != 0 && p2.cFlag != 0)
                && (p1.cFlag == 8 || p2.cFlag == 8 || p1.cFlag != p2.cFlag)) {
            return true;
        }
        if (p1.pvp == null || p2.pvp == null) {
            return false;
        }
        return p1.pvp.isInPVP(p2) || p2.pvp.isInPVP(p1);
    }

    public boolean canAttackPlayerForbot(Player p1, Player p2) {
        if (p1.isDie() || p2.isDie()) {
            return false;
        }
        if (p1.zone.map.mapId == 129 && p1.typePk > 0 && p2.typePk > 0) {
            return true;
        }

        if (p1.typePk == ConstPlayer.PK_ALL || p2.typePk == ConstPlayer.PK_ALL) {
            return true;
        }
        if ((p1.cFlag != 0 && p2.cFlag != 0)
                && (p1.cFlag == 8 || p2.cFlag == 8 || p1.cFlag != p2.cFlag)) {
            return true;
        }
        if (p1.pvp == null || p2.pvp == null) {
            return false;
        }
        if (p1.pvp.isInPVP(p2) || p2.pvp.isInPVP(p1)) {
            return true;
        }
        return false;
    }

    private void sendPlayerAttackMob(Player plAtt, Mob mob) {
        Message msg = null;
        try {
            msg = new Message(54);
            msg.writer().writeInt((int) plAtt.id);
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mob.id);
            Service.gI().sendMessAllPlayerInMap(plAtt, msg);
        } catch (IOException e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    //--------------------------------------------------------------------------

    //phan thaan
    public void useskillPhanThan(Player player) {
        if (!canUseSkillWithCooldown(player)) {
            return;
        }
        if (player.PhanThan != null) {
            player.PhanThan.dispose();
        }
        player.PhanThan = new PhanThan(player);
        affterUseSkill(player, player.playerSkill.skillSelect.template.id);
    }

    //skill bieens hinh
    public void useSkillTranformation(Player player) {
        if (!canUseSkillWithCooldown(player)) {
            return;
        }
        EffectSkillService.gI().sendEffectTranformation(player);
        EffectSkillService.gI().setIsTranformation(player);
        player.effectSkill.lastTimeTranformation = System.currentTimeMillis();
        affterUseSkill(player, player.playerSkill.skillSelect.template.id);
    }

    public long timeselect;

    public void setSkillEvolution(Player player) {
        if ((player.effectSkill.isTranformation || player.effectSkill.isEvolution)) {

        } else {
            Service.getInstance().sendThongBao(player, "Bạn chưa biến hình");
        }
    }

    public void useSkillEvolution(Player player) {
        switch (player.effectSkill.levelTranformation) {
            case 1:
                if (player.isbienhinh == 0) {
                    Service.getInstance().sendThongBao(player, "Skill Biến Hình Phải từ cấp 2 mới có thể tiến hóa");
                }
                break;
            case 2:
                if (player.isbienhinh < 1) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã đạt cấp tối đa");
                }
                break;
            case 3:
                if (player.isbienhinh < 2) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã đạt cấp tối đa");
                }
                break;
            case 4:
                if (player.isbienhinh < 3) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã đạt cấp tối đa");
                }
                break;
            case 5:
                if (player.isbienhinh < 4) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã đạt cấp tối đa");
                }
                break;
            case 6:
                if (player.isbienhinh < 5) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã đạt cấp tối đa");
                }
                break;
            default:
                break;
        }
    }

    public void UseSkillSucManhBocPha(Player player) {
        if (!canUseSkillWithCooldown(player)) {
            return;
        }
        EffectSkillService.gI().setStartSucManhBocPha(player);
        EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SUC_MANH_BOC_PHA_EFFECT);
        switch (player.gender) {
            case 0:
                ItemTimeService.gI().sendItemTime(player, 11997, player.effectSkill.timeSUcManhBocPha / 1000);
                break;
            case 1:
                ItemTimeService.gI().sendItemTime(player, 12000, player.effectSkill.timeSUcManhBocPha / 1000);
                break;
            case 2:
                ItemTimeService.gI().sendItemTime(player, 12003, player.effectSkill.timeSUcManhBocPha / 1000);
                break;
            default:
                break;
        }
        affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        Service.getInstance().point(player);
        Service.getInstance().Send_Info_NV(player);
        PlayerService.gI().sendInfoHpMp(player);
    }

    private boolean canUseSkillWithCooldown(Player player, int skillId) {
        long cooldownTime = 300000;
        long cooldownTime1 = 3000;
        long currentTime = System.currentTimeMillis();

        return false;
    }

    public void selectSkill(Player player, int skillId) {
        for (Skill skill : player.playerSkill.skills) {
            if (skill.skillId != -1 && skill.template.id == skillId) {
                player.playerSkill.skillSelect = skill;
                break;
            }
        }
        if (player.PhanThan != null) {
            selectSkill(player.PhanThan, skillId);
        }
    }
}
