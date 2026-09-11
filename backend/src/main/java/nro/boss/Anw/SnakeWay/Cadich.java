package nro.boss.Anw.SnakeWay;



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
import nro.services.Service;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.SkillUtil;
import Utils.Util;
import nro.clan.Clan;
import consts.ConstPlayer;
import models.Item.ItemOption;
import nro.map.ItemMap;
import nro.map.Zone;



public class Cadich extends Boss {

    private Clan clan;

    private long lastTimeSkill;
    private long lastTimeSkillHD;
    private boolean gongBienKhi;
    private boolean bienKhi;
    private byte cadic = 0;

    public Cadich(Zone zone, Clan clan, long dame, long hp) throws Exception {
        super(PHOBANCDRD, BossID.CADICH, new BossData(
                "Cađích",
                ConstPlayer.XAYDA,
                new short[]{645, 646, 647, -1, -1, -1},
                (dame),
                new long[]{(hp)},
                new int[]{144},
                new int[][]{
                    {Skill.GALICK, 7, 1000},
                    {Skill.MASENKO, 1, 200},
                    {Skill.KAMEJOKO, 4, 1000},
                    {Skill.BIEN_KHI, 1, 1000}},//skill
                new String[]{"|-1|Vĩnh biệt chú mày nhé, Na đíc"},
                new String[]{},
                new String[]{"|-1|Tốt lắm phi thuyền đã đến đón ta"},
                60
        ));
        this.zone = zone;
        this.clan = clan;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill.clan.ConDuongRanDoc.level <= 10) {
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap tat = new ItemMap(this.zone, 224, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                tat.options.add(new ItemOption(67, 1));
                Service.getInstance().dropItemMap(this.zone, tat);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap ruby = new ItemMap(this.zone, 222, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                ruby.options.add(new ItemOption(69, 1));
                Service.getInstance().dropItemMap(this.zone, ruby);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap lb = new ItemMap(this.zone, 220, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                lb.options.add(new ItemOption(71, 1));
                Service.getInstance().dropItemMap(this.zone, lb);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap titan = new ItemMap(this.zone, 223, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                titan.options.add(new ItemOption(68, 1));
                Service.getInstance().dropItemMap(this.zone, titan);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap saphia = new ItemMap(this.zone, 221, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                saphia.options.add(new ItemOption(70, 1));
                Service.getInstance().dropItemMap(this.zone, saphia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 441, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(95, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 442, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(96, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 443, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(97, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 444, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(98, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 445, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(99, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 446, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(100, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 30)) {
                ItemMap spl = new ItemMap(this.zone, 447, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(101, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 100)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
        } else if (plKill.clan.ConDuongRanDoc.level >= 11 && plKill.clan.ConDuongRanDoc.level <= 25) {
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap tat = new ItemMap(this.zone, 224, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                tat.options.add(new ItemOption(67, 1));
                Service.getInstance().dropItemMap(this.zone, tat);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap ruby = new ItemMap(this.zone, 222, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                ruby.options.add(new ItemOption(69, 1));
                Service.getInstance().dropItemMap(this.zone, ruby);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap lb = new ItemMap(this.zone, 220, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                lb.options.add(new ItemOption(71, 1));
                Service.getInstance().dropItemMap(this.zone, lb);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap titan = new ItemMap(this.zone, 223, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                titan.options.add(new ItemOption(68, 1));
                Service.getInstance().dropItemMap(this.zone, titan);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap saphia = new ItemMap(this.zone, 221, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                saphia.options.add(new ItemOption(70, 1));
                Service.getInstance().dropItemMap(this.zone, saphia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 441, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(95, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 442, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(96, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 443, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(97, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 444, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(98, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 445, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(99, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 446, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(100, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 50)) {
                ItemMap spl = new ItemMap(this.zone, 447, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(101, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 100)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 100)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
        } else if (plKill.clan.ConDuongRanDoc.level >= 26 && plKill.clan.ConDuongRanDoc.level <= 50) {
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap tat = new ItemMap(this.zone, 224, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                tat.options.add(new ItemOption(67, 1));
                Service.getInstance().dropItemMap(this.zone, tat);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap ruby = new ItemMap(this.zone, 222, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                ruby.options.add(new ItemOption(69, 1));
                Service.getInstance().dropItemMap(this.zone, ruby);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap lb = new ItemMap(this.zone, 220, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                lb.options.add(new ItemOption(71, 1));
                Service.getInstance().dropItemMap(this.zone, lb);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap titan = new ItemMap(this.zone, 223, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                titan.options.add(new ItemOption(68, 1));
                Service.getInstance().dropItemMap(this.zone, titan);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap saphia = new ItemMap(this.zone, 221, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                saphia.options.add(new ItemOption(70, 1));
                Service.getInstance().dropItemMap(this.zone, saphia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 441, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(95, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 442, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(96, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 443, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(97, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 444, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(98, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 445, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(99, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 446, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(100, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 65)) {
                ItemMap spl = new ItemMap(this.zone, 447, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(101, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 80)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 100)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 100)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 100)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
        } else if (plKill.clan.ConDuongRanDoc.level >= 51 && plKill.clan.ConDuongRanDoc.level <= 75) {
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap tat = new ItemMap(this.zone, 224, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                tat.options.add(new ItemOption(67, 1));
                Service.getInstance().dropItemMap(this.zone, tat);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap ruby = new ItemMap(this.zone, 222, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                ruby.options.add(new ItemOption(69, 1));
                Service.getInstance().dropItemMap(this.zone, ruby);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap lb = new ItemMap(this.zone, 220, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                lb.options.add(new ItemOption(71, 1));
                Service.getInstance().dropItemMap(this.zone, lb);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap titan = new ItemMap(this.zone, 223, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                titan.options.add(new ItemOption(68, 1));
                Service.getInstance().dropItemMap(this.zone, titan);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap saphia = new ItemMap(this.zone, 221, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                saphia.options.add(new ItemOption(70, 1));
                Service.getInstance().dropItemMap(this.zone, saphia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 441, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(95, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 442, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(96, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 443, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(97, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 444, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(98, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 445, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(99, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 446, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(100, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 105)) {
                ItemMap spl = new ItemMap(this.zone, 447, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(101, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
        } else if (plKill.clan.ConDuongRanDoc.level >= 101 && plKill.clan.ConDuongRanDoc.level <= 110) {
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap ngocrong = new ItemMap(this.zone, Util.nextInt(16, 20), 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                Service.getInstance().dropItemMap(this.zone, ngocrong);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap tat = new ItemMap(this.zone, 224, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                tat.options.add(new ItemOption(67, 1));
                Service.getInstance().dropItemMap(this.zone, tat);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap ruby = new ItemMap(this.zone, 222, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                ruby.options.add(new ItemOption(69, 1));
                Service.getInstance().dropItemMap(this.zone, ruby);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap lb = new ItemMap(this.zone, 220, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                lb.options.add(new ItemOption(71, 1));
                Service.getInstance().dropItemMap(this.zone, lb);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap titan = new ItemMap(this.zone, 223, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                titan.options.add(new ItemOption(68, 1));
                Service.getInstance().dropItemMap(this.zone, titan);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap saphia = new ItemMap(this.zone, 221, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                saphia.options.add(new ItemOption(70, 1));
                Service.getInstance().dropItemMap(this.zone, saphia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 441, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(95, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 442, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(96, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 443, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(97, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 444, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(98, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 445, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(99, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 446, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(100, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 110)) {
                ItemMap spl = new ItemMap(this.zone, 447, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                spl.options.add(new ItemOption(101, 5));
                Service.getInstance().dropItemMap(this.zone, spl);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), -1);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 111)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 459, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y), plKill.id);
                phieugiamgia.options.add(new ItemOption(112, 80));
                phieugiamgia.options.add(new ItemOption(93, 90));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), -1);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
            if (Util.isTrue(plKill.clan.ConDuongRanDoc.level, 120)) {
                ItemMap phieugiamgia = new ItemMap(this.zone, 721, 1, this.location.x + Util.nextInt(-80, 80), this.zone.map.yPhysicInTop
                (this.location.x, this.location.y - 20), plKill.id);
                phieugiamgia.options.add(new ItemOption(93, 30));
                Service.getInstance().dropItemMap(this.zone, phieugiamgia);
            }
        }
    }

    @Override
    public void afk() {
        if (this.clan == null || this.clan.ConDuongRanDoc == null) {
            this.leaveMap();
            return;
        }
        if (this.clan.ConDuongRanDoc.getNumBossAlive() < 2) {
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 490, 312);
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
        ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.TENNIS_SPACE_SHIP);
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        SnakeWayManager.gI().removeBoss(this);
        if (this.clan.ConDuongRanDoc != null) {
            this.clan.ConDuongRanDoc.endCDRD = true;
        }
        this.dispose();
        this.clan = null;
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (gongBienKhi) {
                return 0;
            }
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 4;
            }
            if (damage >= this.nPoint.hp) {
                this.effectSkill.removeSkillEffectWhenDie();
                this.changeToTypeNonPK();
                die(plAtt);
                return 0;
            }
            this.nPoint.subHP(damage);
            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void attack() {
        if (!gongBienKhi && !this.effectSkill.isCharging && Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                if (this.nPoint.hp < this.nPoint.hpMax / 2 && !bienKhi) {
                    this.chat("Ha ha ha, ha ha ha");
                    this.bienKhi = true;
                    this.gongBienKhi = true;
                    EffectSkillService.gI().sendEffectMonkey(this);
                    Functions.sleep(2000);
                    this.chat("Thế nào " + pl.name + "? Mi đã thấy phép biến hình của người Xayda rồi chứ?");
                    this.gongBienKhi = false;
                    int timeMonkey = 100000;
                    this.effectSkill.isMonkey = true;
                    this.effectSkill.timeMonkey = timeMonkey;
                    this.effectSkill.lastTimeUpMonkey = System.currentTimeMillis();
                    this.effectSkill.levelMonkey = 1;
                    long hpmax = (long) this.nPoint.hpMax * 2L;
                    this.nPoint.hpMax = (int) Math.min(hpmax, 2_000_000_000);
                    this.nPoint.setHp(((long) this.nPoint.hpMax));
                    EffectSkillService.gI().sendEffectMonkey(this);
                    Service.gI().Send_Caitrang(this);
                    Service.gI().point(this);
                    Service.gI().Send_Info_NV(this);
                    Service.gI().sendInfoPlayerEatPea(this);
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(0);
                if (Util.canDoWithTime(this.lastTimeSkillHD, 3000)) {
                    this.playerSkill.skillSelect = this.playerSkill.skills.get(2);
                    this.lastTimeSkillHD = System.currentTimeMillis() + 99999999;
                }
                if (Util.isTrue(1, 20) && Util.canDoWithTime(lastTimeSkill, Util.nextInt(10000, 15000))) {
                    EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), 5000);
                    this.chat("Tuyệt chiêu hủy diệt của môn phái Xayda");
                    this.cadic = 1;
                    this.lastTimeSkill = System.currentTimeMillis();
                    this.lastTimeSkillHD = System.currentTimeMillis();
                    return;
                }
                if (this.cadic == 1) {
                    this.playerSkill.skillSelect = this.getSkillById(Skill.MASENKO);
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                }
                if (!pl.effectSkill.isStun) {
                    this.cadic = 0;
                }
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
