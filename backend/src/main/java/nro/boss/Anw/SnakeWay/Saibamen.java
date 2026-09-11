package nro.boss.Anw.SnakeWay;

/*
 * @Author: MaiTienDung
 */

import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBANCDRD;
import QuanLiBoss.Manager.SnakeWayManager;
import Utils.Functions;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.skill.Skill;
import Utils.Util;
import nro.clan.Clan;
import network.io.Message;
import java.util.List;
import consts.ConstPlayer;
import nro.map.ItemMap;
import nro.map.Zone;

public class Saibamen extends Boss {

    private Clan clan;
    private int idboss;

    public Saibamen(Zone zone, Clan clan, long dame, long hp) throws Exception {
        super(PHOBANCDRD, BossID.SAIBAMEN, new BossData(
                "Số 1",
                ConstPlayer.XAYDA,
                new short[]{642, 643, 644, -1, -1, -1},
                (dame),
                new long[]{(hp)},
                new int[]{144},
                new int[][]{
                    {Skill.GALICK, 7, 1000},},
                new String[]{},
                new String[]{},
                new String[]{},
                60
        ));
        this.zone = zone;
        this.clan = clan;
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(this.zone, Util.nextInt(18, 20), 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void afk() {
        if (this.clan == null || this.clan.ConDuongRanDoc == null) {
            this.leaveMap();
            return;
        }
        Player pl = getPlayerAttack();
        if (pl == null || pl.isDie()) {
            return;
        }
        this.changeToTypePK();
        Functions.sleep(1500);
        this.changeStatus(BossStatus.ACTIVE);
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 435, 342);
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        SnakeWayManager.gI().removeBoss(this);
        this.dispose();
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                setBom(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void setBom(Player plAtt) {
        if (!this.playerSkill.prepareTuSat) {
            for (Player pl : this.zone.getPlayers()) {
                Service.gI().sendThongBao(pl, pl.name + " coi chừng đấy!");
            }
            Service.gI().chat(plAtt, "Trời ơi muộn mất rồi");
            EffectSkillService.gI().startStun(plAtt, System.currentTimeMillis(), 3500);
            //gồng tự sát
            this.playerSkill.prepareTuSat = true;
            this.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
            Message msg;
            try {
                msg = new Message(-45);
                msg.writer().writeByte(7);
                msg.writer().writeInt((int) this.id);
                msg.writer().writeShort(104);
                msg.writer().writeShort(2000);
                Service.gI().sendMessAllPlayerInMap(this, msg);
                msg.cleanup();
            } catch (Exception e) {
            }
            this.nPoint.hp = 0;
        }
        Service.gI().chat(this, "He he he");
        while (this.playerSkill.prepareTuSat) {
            if (Util.canDoWithTime(this.playerSkill.lastTimePrepareTuSat, 2500)) {
                this.playerSkill.prepareTuSat = false;
                setDie(this);
                die(plAtt);
                double dame = this.nPoint.hpMax * 100;
                List<Player> playersMap = null;
                playersMap = this.zone.getNotBosses();
                if (!MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    for (Player pl : playersMap) {
                        if (!this.equals(pl)) {
                            pl.injured(this, dame, false, false);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                        }
                    }
                }
            }
        }
    }
}
