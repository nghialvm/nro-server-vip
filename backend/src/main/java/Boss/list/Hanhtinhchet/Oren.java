package Boss.list.Hanhtinhchet;

import nro.effect.EffectSkillService;
import models.Item.Item;
import nro.player.Player;
import nro.services.PlayerService;
import nro.services.Service;
import consts.ConstPlayer;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import QuanLiBoss.Manager.BossManager;
import nro.map.ItemMap;
import nro.server.ServerNotify;
import nro.services.Fun.ChangeMapService;
import Utils.Util;
import network.io.Message;
import java.io.IOException;
import models.Item.ItemOption;

public class Oren extends Boss {
    private Kami iskami;
    protected boolean isReady;
    public boolean isFusion;
    protected long lastTimeFusion;
    private final int timeToFusion = 5000;
    protected long lastTimecanAttack;
    public boolean canAttack;

    public Oren() throws Exception {
        super(BossID.oren, BossesData.oren);
        this.iskami = null;
        this.isReady = false;
        this.isFusion = false;
    }

    public void createSmallBoss() {
        try {
            this.iskami = new Kami(this, this.zone, (short) this.location.x, (short) this.location.y, BossesData.kami);
        } catch (Exception ex) {

        }
    }

    public void hoptheAdr() {
        if (this.iskami != null && this.iskami.typePk == ConstPlayer.NON_PK && this.iskami.isReady
                && this.typePk == ConstPlayer.NON_PK && this.isReady && !this.isFusion) {
            if (Util.canDoWithTime(lastTimeFusion, this.timeToFusion)) {
                this.isFusion = true;
                setBaseFusion();
                fusion(false);
                ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
            }
        }
    }

    private void setBaseFusion() {
        BossData data = this.data[this.currentLevel];
        this.name = "KamiOren";
        this.nPoint.mpg = 500_000_000;
        this.nPoint.dameg = 200_000;
        this.nPoint.hpg = data.getHp()[Util.nextInt(0, data.getHp().length - 1)] + 1_500_000_000;
        this.nPoint.calPoint();
        super.active(); 
        this.initSkill();
        this.resetBase();
    }

    public void fusion(boolean porata) {
        ChangeMapService.gI().exitMap(this.iskami);
        this.iskami = null;
        this.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
        fusionEffect(this.fusion.typeFusion);
        this.location.y = this.zone.map.yPhysicInTop(this.location.x, this.location.y);
        ChangeMapService.gI().changeMap(this, zone, this.location.x, this.location.y);
        Service.getInstance().Send_Caitrang(this);
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(20, 100)) {
            ItemMap kami = new ItemMap(this.zone, 1623, 1, this.location.x, this.zone.map.yPhysicInTop
                    (this.location.x, this.location.y), plKill.id);
            kami.options.add(new ItemOption(50, 40));
            kami.options.add(new ItemOption(77, 40));
            kami.options.add(new ItemOption(103, 40));
            kami.options.add(new ItemOption(95, 40));
            kami.options.add(new ItemOption(96, 40));
            if (Util.isTrue(80, 100)) {
                kami.options.add(new ItemOption(93, Util.nextInt(3, 7)));
            }
            Service.getInstance().dropItemMap(this.zone, kami);
        } else if (Util.isTrue(20, 100)) {
            ItemMap oren = new ItemMap(this.zone, 1624, 1, this.location.x, this.zone.map.yPhysicInTop
                    (this.location.x, this.location.y), plKill.id);
            oren.options.add(new ItemOption(50, 40));
            oren.options.add(new ItemOption(77, 40));
            oren.options.add(new ItemOption(103, 40));
            oren.options.add(new ItemOption(95, 40));
            oren.options.add(new ItemOption(96, 40));
            if (Util.isTrue(80, 100)) {
                oren.options.add(new ItemOption(93, Util.nextInt(3, 7)));
            }
            Service.getInstance().dropItemMap(this.zone, oren);
        } else {
            ItemMap Dasucmanh = new ItemMap(this.zone, Util.nextInt(1625, 1627), 1, this.location.x, this.zone.map.yPhysicInTop
                    (this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, Dasucmanh);
        }
    }

    private void fusionEffect(int type) {
        Message msg;
        try {
            msg = new Message(125);
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) this.id);
            Service.gI().sendMessAllPlayerInMap(this, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
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
            if (damage >= this.nPoint.hp && !this.isReady && this.iskami != null) {
                this.changeToTypeNonPK();
                this.nPoint.hp = 1;
                this.isReady = true;
                this.effectSkill.removeSkillEffectWhenDie();
                if (this.iskami.isReady) {
                    this.lastTimeFusion = System.currentTimeMillis();
                    this.lastTimecanAttack = System.currentTimeMillis();
                }
                return 0;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void active() {
        hoptheAdr();
        if (this.typePk == ConstPlayer.NON_PK && !isReady) {
            this.changeToTypePK();
            return;
        }
        if (this.iskami == null && Util.canDoWithTime(lastTimecanAttack, timeToFusion * 2) && !this.canAttack) {
            this.changeToTypePK();
            this.canAttack = true;
            return;
        }
        if ((BossManager.gI().getBossById(BossID.oren).typePk == ConstPlayer.NON_PK) && (BossManager.gI().getBossById(BossID.kami).typePk == ConstPlayer.NON_PK)) {
            this.chat("|7|Hợp Thể KamiOren");
            BossManager.gI().getBossById(BossID.kami).chat("|7|Hợp Thể KamiOren");
        }
        this.attack();
    }

    @Override
    public void joinMap() {
        super.joinMap();
        this.createSmallBoss();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        super.dispose();
        BossManager.gI().removeBoss(this);
        BossManager.gI().createBoss(BossID.oren);
    }
}