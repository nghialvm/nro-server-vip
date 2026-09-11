package nro.effect;

import nro.inventory.InventoryService;
import models.Item.Item;
import nro.mob.Mob;
import nro.player.Player;
import models.Item.ItemService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.MapService;
import nro.skill.Skill;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import models.Item.ItemOption;
import models.Item.ItemTimeService;

public class EffectSkin {

    public static final String[] textOdo = new String[]{
        "Hôi quá, tránh xa ta ra",
        "Biến đi",
        "Trời ơi đồ ở dơ",
        "Thúi quá",
        "Mùi gì hôi quá"
    };

    private static final String[] textThoBulma = new String[]{
        "Wow, sexy quá"
    };

    private static final String[] textBuffSD = new String[]{
        "Wow!", "Đẹp quá!", "Xinh quá!"
    };

    private static final String[] textfounder = new String[]{
        "MaiTienDung", "Anh MaiTienDung!", "Idol Vip Quá!", "Idol",
        "Trùm Server Đây Rồi!", "Nhà Sáng Lập MaiTienDung!"
    };

    private static final String[] textXinbato = new String[]{
        "Bực bội quá",
        "Phân tâm quá",
        "Nực quá",
        "Phân tâm quá",
        "Tránh ra đi Xinbatô ơi"
    };

    private static final String[] textBongTuyet = new String[]{
        "Lạnh quá...",
        "Ư... lạnh ghê...",
        "Brrr...",
        "Chết rét mất..."
    };

    private static final String[] textBiNgo = new String[]{
        "Khoan đã!",
        "Khônggg... bí?!",
        "Tôi... tròn rồi!",
        "Cứu tôi với!",
        "Đầu tôi đâu?!"
    };

    private static final String[] textCarot = new String[]{
        "Biến thành cà rốt cho ta!",
        "Cà rốt chi thuật!",
        "Cà rốt hóa thân! Hô biến!",
        "Cà rốt hóa hình! Giờ thì chạy đi!",
        "Biến hình... cà rốt style!",
        "Lệnh Thỏ ban ra, biến thành củ đi!"
    };

    @Setter
    private Player player;

    public EffectSkin(Player player) {
        this.player = player;
        this.xHPKI = 1;
        this.xDame = 1;
    }

    private long lastTimeHoiHpDongMinh10s;

    private long lastTimeBiNgo;

    private long lastTimeVirus;

    private long lastTimeBongTuyet;

    public boolean isNezuko;
    public boolean isTanjiro;
    public boolean isZenitsu;
    public boolean isInosuke;
    public boolean isInoHashi;

    private long lastTimeEffNezuko;
    private long lastTimeEffTanjiro;
    private long lastTimeEffInoHashi;
    private long lastTimeEffZenitsu;
    private long lastTimeEffInosuke;

    public long lastTimeInoHashi;
    public long lastTimeZenitsu;
    public long lastTimeNezuko;
    public long lastTimeInosuke;
    public long lastTimeTanjiro;

    public int timeNezuko;
    public int timeTanjiro;
    public int timeInosuke;
    public int timeZenitsu;
    public int timeInoHashi;

    public boolean isSocola;
    public int timeSocola;
    public long lastTimeSocola;

    public boolean isThoDaiKa;
    public int timeThoDaiKa;
    public long lastTimeThoDaiKa;

    public boolean isDraburaFrost;
    public int timeDraburaFrost;
    public long lastTimeDraburaFrost;

    public int timeSlow;
    public long lastTimeSlow;
    public boolean isSlow;

    public long lastTimeAttack;
    public long lastTimeVoHinh;
    private long lastTimeOdo;

    private long lastTimeFounder;

    private long lastTimeCooldame;

    private long lastTimeCuteDame;

    private long lastTimeThoBulma;
    private long lastTimeBunmaTocMau;
    private long lastTimeTiecbaiBien;
    private long lastTimeMaPhongBa;
    private long lastTimeXenHutHpKi;

    private long lastTimeXinbato;

    private long lastTimeThodaica;

    private long lasttimeDraburaFrost;

    private long LastTimeFideDaiCa;

    public long lastTimeAddTimeTrainArmor;
    public long lastTimeSubTimeTrainArmor;

    public boolean isVoHinh;

    public long lastTimeXHPKI;
    public int xHPKI;

    public long lastTimeXDame;
    public int xDame;

    public long lastTimeUpdateCTHT;

    public long lastTimeMabu;
    public long lastTimeBuiBui;

    private long lastTimeTanHinh;
    private long lastTimeHoaDa;
    private long lastTimeHalloween;
    public long lastTimeXChuong;
    public boolean isXChuong;
    public boolean isXDame;

    public void update() {
        updateVoHinh();
        if (!this.player.isDie() && this.player.zone != null && !MapService.gI().isMapOffline(this.player.zone.map.mapId)) {
            updateOdo();
            updateFounder();
            updateXinBato();
            updateBienCaRot();
            updateThoBulma();
            updateMaPhongBa();
            updateXenHutXungQuanh();
            updateTanHinh();
            updateHoaDa();
            updateHoaSocola();
            updateLamCham();
            updateXChuong();
            updateHalloween();
            updateBunmaTocMau();
            updateTiecbaiBien();
            updateInoHashi();
            updateInosuke();
            updateNezuko();
            updateTanjiro();
            updateZenitsu();
            updateFideDaiCa();
            updateNhiemVirus();
            updateBongTuyet();
            updateBiNgo();
            updateDraburaFrost();
            updateCoolDame();
            updateHoiHp10s();
            updateCuteDame();
        }
        if (!this.player.isBoss && !this.player.isPetFollow && !player.isPetFollow && !this.player.isDuongTang && !player.isDuongTang && !this.player.isNguoiYeu) {
            updateTrainArmor();
        }
        if (xHPKI != 1 && Util.canDoWithTime(lastTimeXHPKI, 1800000)) {
            xHPKI = 1;
            Service.gI().point(player);
        }
        if (xDame != 1 && Util.canDoWithTime(lastTimeXDame, 1800000)) {
            xDame = 1;
            Service.gI().point(player);
        }
        if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola))) {
            EffectSkinService.gI().removeSocola(this.player);
        }
        if (isThoDaiKa && (Util.canDoWithTime(lastTimeThoDaiKa, timeThoDaiKa))) {
            EffectSkinService.gI().removeThoDaiKa(this.player);
        }
        if (isDraburaFrost && (Util.canDoWithTime(lastTimeDraburaFrost, timeDraburaFrost))) {
            EffectSkinService.gI().removeDraburaFrost(this.player);
        }
        if (isSlow && (Util.canDoWithTime(lastTimeSlow, timeSlow))) {
            EffectSkinService.gI().removeSlow(this.player);
        }
        if (isNezuko && (Util.canDoWithTime(lastTimeEffNezuko, timeNezuko))) {
            EffectSkinService.gI().removeCuongNo(this.player);
        }
        if (isTanjiro && (Util.canDoWithTime(lastTimeEffTanjiro, timeTanjiro))) {
            EffectSkinService.gI().removeTanjiro(this.player);
        }
        if (isInosuke && (Util.canDoWithTime(lastTimeEffInosuke, timeInosuke))) {
            EffectSkinService.gI().removeInosuke(this.player);
        }
        if (isInoHashi && (Util.canDoWithTime(lastTimeEffInoHashi, timeInoHashi))) {
            EffectSkinService.gI().removeInoHashi(this.player);
        }
        updateCTHaiTac();
    }
    //1488	1489	1490

    private void updateHoiHp10s() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.tlHpHoiBanthan_DongMinh > 0) {
                if (Util.canDoWithTime(lastTimeHoiHpDongMinh10s, 10_000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 200) {
                            PlayerService.gI().hoiPhuc(pl, this.player.nPoint.tlHpHoiBanthan_DongMinh, 0);
                        }
                    }
                    this.lastTimeHoiHpDongMinh10s = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDraburaFrost() {
        try {
            if (InventoryService.gI().findItemSkinDraburaFrost(this.player)) {
                if (Util.canDoWithTime(lasttimeDraburaFrost, 30_000)) {
                    List<Player> players = new ArrayList<>();
                    List<Player> playersMap = this.player.zone.getNotBosses();

                    for (Player pl : playersMap) {
                        if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 300) {
                            players.add(pl);
                        }
                    }

                    for (Player pl : players) {
                        if (Util.nextInt(100) < 70) {
                            int time = 5000;
                            EffectSkinService.gI().setDraburaFrost(pl, System.currentTimeMillis(), time);
                            Service.getInstance().Send_Caitrang(pl);
                            Service.gI().chat(this.player, "Phụt!");
                            Service.gI().chat(pl, "Hự...");
                            ItemTimeService.gI().sendItemTime(pl, 11085, time / 1000);
                        }
                    }

                    this.lasttimeDraburaFrost = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBiNgo() {
        try {
            if (this.player.nPoint.isHoaBiNgoXungQuanh) {
                if (Util.canDoWithTime(lastTimeBiNgo, 10_000)) {
                    Service.gI().chat(player, "Hóa bí đi!");
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 200) {
                            ItemTimeService.gI().sendItemTime(pl, 7057, 5);
                            Service.gI().chat(pl, textBiNgo[Util.nextInt(0, textBiNgo.length - 1)]);
                            EffectSkillService.gI().setBiNgo(pl, 5000);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                            Service.gI().point(pl);
                        }
                    }
                    this.lastTimeBiNgo = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBongTuyet() {
        try {
            if (InventoryService.gI().findBongTuyet(player)) {
                if (Util.canDoWithTime(lastTimeBongTuyet, 7000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 200) {
                            ItemTimeService.gI().sendItemTime(pl, 7066, 5);
                            EffectSkillService.gI().setBongTuyet(pl, 5000);
                            Service.gI().chat(pl, textBongTuyet[Util.nextInt(0, textBongTuyet.length - 1)]);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                            Service.gI().point(pl);
                        }
                    }
                    this.lastTimeBongTuyet = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNhiemVirus() {
        try {
            if (this.player.effectSkill.isVirus) {
                if (Util.canDoWithTime(lastTimeVirus, 5000)) {
                    if (Util.isTrue(50, 100) && !this.player.isDie()) {
                        String[] virusTalk = {
                            "Khặc khặc...",
                            "Nóng quá...",
                            "Choáng váng...",
                            "Khè... khụ!",
                            "Không ổn...",
                            "Đầu... đau quá...",
                            "Chóng mặt..."
                        };
                        Service.getInstance().chat(this.player, virusTalk[Util.nextInt(virusTalk.length)]);
                    }

                    if (Util.isTrue(10, 100)) {

                        List<Player> players = new ArrayList<>();

                        List<Player> playersMap = this.player.zone.getNotBosses();
                        for (Player pl : playersMap) {
                            if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 200) {
                                players.add(pl);
                            }
                        }
                        for (Player pl : players) {
                            if (pl.itemTime.IsKhauTrang || InventoryService.gI().findAvatarKhauTrang(pl)) {
                                return;
                            }
                            if (!pl.effectSkill.isVirus) {
                                ItemTimeService.gI().sendItemTime(pl, 7143, 300);
                                EffectSkillService.gI().setVirus(pl, 300_000);
                                Service.gI().point(pl);
                                Service.getInstance().sendThongBao(pl, "Bạn bị lây virus từ " + this.player.name);
                            }
                        }
                        this.lastTimeVirus = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCTHaiTac() {
        if (this.player.setClothes != null && this.player.setClothes.ctHaiTac != -1
                && this.player.zone != null
                && Util.canDoWithTime(lastTimeUpdateCTHT, 5000)) {
            int count = 0;
            int[] cts = new int[9];
            cts[this.player.setClothes.ctHaiTac - 618] = this.player.setClothes.ctHaiTac;
            List<Player> players = new ArrayList<>();
            players.add(player);
            try {
                for (Player pl : player.zone.getNotBosses()) {
                    if (!player.equals(pl) && pl.setClothes.ctHaiTac != -1 && Util.getDistance(player, pl) <= 300) {
                        cts[pl.setClothes.ctHaiTac - 618] = pl.setClothes.ctHaiTac;
                        players.add(pl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < cts.length; i++) {
                if (cts[i] != 0) {
                    count++;
                }
            }
            for (Player pl : players) {
                Item ct = pl.inventory.itemsBody.get(5);
                if (ct.isNotNullItem() && ct.template.id >= 618 && ct.template.id <= 626) {
                    for (ItemOption io : ct.itemOptions) {
                        if (io.optionTemplate.id == 147
                                || io.optionTemplate.id == 77
                                || io.optionTemplate.id == 103) {
                            io.param = count * 3;
                        }
                    }
                }
                if (pl.isPl() && Util.canDoWithTime(lastTimeUpdateCTHT, 5000)) {
                    InventoryService.gI().sendItemBody(pl);
                }
                pl.effectSkin.lastTimeUpdateCTHT = System.currentTimeMillis();
            }
        }
    }

    private void updateNezuko() {
        try {
            if (this.player.setClothes != null && this.player.setClothes.ctNezuko != -1) {
                if (Util.canDoWithTime(lastTimeEffNezuko, 120000)) {
                    Service.getInstance().chat(this.player, "Grừuuuuuu............");
                    EffectSkinService.gI().setCuongNo(this.player, System.currentTimeMillis(), 10000);
                    this.lastTimeEffNezuko = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTanjiro() {
        try {
            if (this.player.setClothes != null && this.player.setClothes.ctTanjiro != -1) {
                if (Util.canDoWithTime(lastTimeEffTanjiro, 120000)) {
                    Service.getInstance().chat(this.player, "Hơi Thở của Nước......");
                    EffectSkinService.gI().setTanjiro(this.player, System.currentTimeMillis(), 15000);
                    this.lastTimeEffTanjiro = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateInoHashi() {
        try {
            if (this.player.setClothes != null && this.player.setClothes.ctInoHashi != -1) {
                if (Util.canDoWithTime(lastTimeEffInoHashi, 120000)) {
                    Service.getInstance().chat(this.player, "Hơi thở của dã thú....");
                    EffectSkinService.gI().setInoHashi(this.player, System.currentTimeMillis(), 20000);
                    this.lastTimeEffInoHashi = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateInosuke() {
        try {
            if (this.player.setClothes != null && this.player.setClothes.ctInosuke != -1) {
                if (Util.canDoWithTime(lastTimeEffInosuke, 120000)) {
                    Service.getInstance().chat(this.player, "Hơi thở của dã thú....");
                    EffectSkinService.gI().setInosuke(this.player, System.currentTimeMillis(), 20000);

                    this.lastTimeEffInosuke = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateZenitsu() {
        try {
            if (this.player.setClothes != null && this.player.setClothes.ctZenitsu != -1) {
                if (Util.canDoWithTime(lastTimeEffZenitsu, 2000)) {
                    Service.getInstance().chat(this.player, "Khò khò.......");
                    List<Player> players = new ArrayList<>();

                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (Player pl : playersMap) {
                        if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 200) {
                            players.add(pl);
                        }

                    }
                    for (Player pl : players) {
                        Service.getInstance().chat(pl, "Hoang mang quá!");
                    }
                    this.lastTimeEffZenitsu = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateXenHutXungQuanh() {
        try {
            if (this.player.nPoint != null && (this.player.nPoint.hp < this.player.nPoint.hpMax || this.player.nPoint.mp < this.player.nPoint.mpMax)) {
                int param = this.player.nPoint.tlHutHpMpXQ;
                if (param > 0) {
                    if (!this.player.isDie() && Util.canDoWithTime(lastTimeXenHutHpKi, 5000)) {
                        long hpHut = 0;
                        long mpHut = 0;
                        List<Player> players = new ArrayList<>();
                        List<Player> playersMap = this.player.zone.getNotBosses();
                        for (Player pl : playersMap) {
                            if(pl == null) continue;
                            if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                    && Util.getDistance(this.player, pl) <= 200) {
                                players.add(pl);
                            }

                        }
                        for (Mob mob : this.player.zone.mobs) {
                            if (mob.point.gethp() > 1) {
                                if (Util.getDistance(this.player, mob) <= 200) {
                                    long subHp = Util.CrisGH(mob.point.getHpFull() * param / 100);
                                    if (subHp >= mob.point.gethp()) {
                                        subHp = mob.point.gethp() - 1;
                                    }
                                    hpHut += subHp;
                                    mob.injured(null, subHp, false);
                                }
                            }
                        }
                        for (Player pl : players) {
                            long subHp = Util.CrisGH((long) pl.nPoint.hpMax * param / 100);
                            long subMp = Util.CrisGH((long) pl.nPoint.mpMax * param / 100);
                            if (subHp >= pl.nPoint.hp) {
                                subHp = pl.nPoint.hp - 1;
                            }
                            if (subMp >= pl.nPoint.mp) {
                                subMp = pl.nPoint.mp - 1;
                            }
                            hpHut += subHp;
                            mpHut += subMp;
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                            pl.injured(null, subHp, true, false);
                        }
                        this.player.nPoint.addHp(hpHut);
                        this.player.nPoint.addMp(mpHut);
                        PlayerService.gI().sendInfoHpMpMoney(this.player);
                        Service.gI().Send_Info_NV(this.player);
                        this.lastTimeXenHutHpKi = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOdo() {
        try {
            if (this.player.nPoint != null) {
                int param = this.player.nPoint.tlHpGiamODo;
                if (param > 0) {
                    if (Util.canDoWithTime(lastTimeOdo, 10000)) {
                        List<Player> playersMap = this.player.zone.getNotBosses();
                        for (int i = playersMap.size() - 1; i >= 0; i--) {
                            Player pl = playersMap.get(i);
                            if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                    && Util.getDistance(this.player, pl) <= 200) {
                                long subHp = Util.CrisGH(pl.nPoint.hpMax * param / 100);
                                if (subHp >= pl.nPoint.hp) {
                                    subHp = pl.nPoint.hp - 1;
                                }
                                Service.gI().chat(pl, textOdo[Util.nextInt(0, textOdo.length - 1)]);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.gI().Send_Info_NV(pl);
                                pl.injured(null, subHp, true, false);
                            }

                        }
                        this.lastTimeOdo = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFounder() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.isFounder) {
                if (Util.canDoWithTime(lastTimeFounder, 5000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 500) {
                            Service.gI().chat(pl, textfounder[Util.nextInt(0, textfounder.length - 1)]);
                        }

                    }
                    this.lastTimeFounder = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateXinBato() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.isXinbato) {
                if (Util.canDoWithTime(lastTimeXinbato, 5000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 300) {
                            Service.gI().chat(pl, textXinbato[Util.nextInt(0, textXinbato.length - 1)]);
                            EffectSkillService.gI().setXinbato(pl, 5000);
                        }
                    }
                    this.lastTimeXinbato = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateThoBulma() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.tlSexyDame > 0) {
                if (Util.canDoWithTime(lastTimeThoBulma, 10000)) {

                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 120) {
                            if (this.player.nPoint.isThoBulma) {
                                Service.gI().chat(pl, textThoBulma[Util.nextInt(0, textThoBulma.length - 1)]);
                            } else {
                                Service.gI().chat(pl, textBuffSD[Util.nextInt(0, textBuffSD.length - 1)]);
                            }
                            EffectSkillService.gI().setDameBuff(player, 11000, this.player.nPoint.tlSexyDame);
                        }
                    }
                    this.lastTimeThoBulma = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCoolDame() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.tlCoolDame > 0) {
                if (Util.canDoWithTime(lastTimeCooldame, 10000)) {

                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 200) {
                            EffectSkillService.gI().setDameBuff(player, 11000, this.player.nPoint.tlCoolDame);
                        }
                    }
                    this.lastTimeCooldame = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCuteDame() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.tlCuteAddame > 0) {
                if (Util.canDoWithTime(lastTimeCuteDame, 10000)) {

                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 200) {
                            EffectSkillService.gI().setDameBuff(player, 11000, this.player.nPoint.tlCuteAddame);
                        }
                    }
                    this.lastTimeCuteDame = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFideDaiCa() {
        try {
            if (this.player.setClothes != null && this.player.setClothes.ctFideDaiCa != -1 && this.player.zone != null) {
                if (Util.canDoWithTime(LastTimeFideDaiCa, 10_000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this.player, pl) <= 500) {
                            if (pl.setClothes != null && pl.setClothes.ctDanEmFide != -1 && pl.zone != null) {
                                Service.getInstance().chat(pl, "Fide đại ca");
                                EffectSkillService.gI().setFideDaiCa(pl, 10_000);
                            }
                        }

                    }
                    this.LastTimeFideDaiCa = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBunmaTocMau() {
        if (this.player.setClothes != null && this.player.setClothes.ctBunmaTocMau != -1
                && this.player.zone != null
                && Util.canDoWithTime(lastTimeBunmaTocMau, 5000)) {
            int count = 0;
            int[] cts = new int[3];
            cts[this.player.setClothes.ctBunmaTocMau - 1208] = this.player.setClothes.ctBunmaTocMau;
            List<Player> players = new ArrayList<>();
            players.add(player);
            try {
                for (Player pl : player.zone.getNotBosses()) {
                    if (!player.equals(pl) && pl.setClothes.ctBunmaTocMau != -1 && Util.getDistance(player, pl) <= 300) {
                        cts[pl.setClothes.ctBunmaTocMau - 1208] = pl.setClothes.ctBunmaTocMau;
                        players.add(pl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < cts.length; i++) {
                if (cts[i] != 0) {
                    count++;
                }
            }
            for (Player pl : players) {
                Item ct = pl.inventory.itemsBody.get(5);
                if (ct.isNotNullItem() && ct.template.id >= 1208 && ct.template.id <= 1210) {
                    for (ItemOption io : ct.itemOptions) {
                        if (io.optionTemplate.id == 147) {
                            io.param = Math.min(count * 3, 10);
                        }
                    }
                }
                if (pl.isPl() && Util.canDoWithTime(lastTimeBunmaTocMau, 5000)) {
                    InventoryService.gI().sendItemBody(pl);
                }
                pl.effectSkin.lastTimeBunmaTocMau = System.currentTimeMillis();
            }
        }
    }

    private void updateTiecbaiBien() {
        if (this.player.setClothes != null && this.player.setClothes.ctTiecbaiBien != -1
                && this.player.zone != null
                && Util.canDoWithTime(lastTimeTiecbaiBien, 5000)) {

            int count = 0;
            int[] cts = new int[3];
            cts[this.player.setClothes.ctTiecbaiBien - 1234] = this.player.setClothes.ctTiecbaiBien;

            List<Player> players = new ArrayList<>();
            players.add(player);

            for (int i = 0; i < cts.length; i++) {
                if (cts[i] != 0) {
                    count++;
                }
            }

            try {
                for (Player pl : player.zone.getNotBosses()) {
                    // Kiểm tra nếu người chơi không phải là chính mình và cách xa trong phạm vi 300
                    if (!player.equals(pl) && pl.setClothes.ctTiecbaiBien != -1 && Util.getDistance(player, pl) <= 300) {

                        // Loại trừ những người mặc cải trang từ 1208 đến 1210
                        if (pl.setClothes.ctTiecbaiBien >= 1234 && pl.setClothes.ctTiecbaiBien <= 1236) {
                            continue;
                        }

                        cts[pl.setClothes.ctTiecbaiBien - 1234] = pl.setClothes.ctTiecbaiBien;
                        players.add(pl);

                        Item ct = pl.inventory.itemsBody.get(5);

                        // Áp dụng giảm SĐ nếu người chơi có item hợp lệ và option đúng
                        if (ct.isNotNullItem() && ct.template.id >= 1234 && ct.template.id <= 1236) {
                            for (ItemOption io : ct.itemOptions) {
                                if (io.optionTemplate.id == 147) {
                                    io.param = io.param - Math.min(count * 5, 15); // giảm 5% SĐ cho mỗi người, tối đa 10 người
                                }
                            }
                        }

                        // Cập nhật hiệu ứng và gửi lại trạng thái item nếu đủ điều kiện
                        if (pl.isPl() && Util.canDoWithTime(lastTimeTiecbaiBien, 5000)) {
                            InventoryService.gI().sendItemBody(pl);
                        }
                        pl.effectSkin.lastTimeTiecbaiBien = System.currentTimeMillis();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTanHinh() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.isTanHinh) {
                if (Util.canDoWithTime(lastTimeTanHinh, 5000)) {
                    EffectSkillService.gI().setIsTanHinh(player, 1500);
                    this.lastTimeTanHinh = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHoaDa() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.isHoaDa) {
                if (Util.canDoWithTime(lastTimeHoaDa, 30_000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null
                                && !this.player.equals(pl)
                                && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 200) {

                            if (Util.nextInt(100) < 80) {
                                EffectSkillService.gI().setIsStone(pl, 6000);
                                Service.getInstance().chat(this.player, "Phẹt.....");
                                Service.getInstance().chat(pl, "hự.....");
                            }
                        }
                    }
                    this.lastTimeHoaDa = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLamCham() {
        try {
            if (this.player.nPoint.wearingBuiBui) {
                if (Util.canDoWithTime(lastTimeBuiBui, 3000)) {
                    List<Player> players = new ArrayList<>();
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (Player pl : playersMap) {
                        if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 200) {
                            players.add(pl);
                        }
                    }
                    for (Player pl : players) {
                        Service.getInstance().chat(pl, "Nặng quá!");
                        EffectSkinService.gI().setSlow(pl, System.currentTimeMillis(), 3000);
                    }
                    this.lastTimeBuiBui = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHoaSocola() {
        try {
            if (this.player.nPoint.wearingMabu) {
                if (Util.canDoWithTime(lastTimeMabu, 30000)) {
                    List<Player> players = new ArrayList<>();
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (Player pl : playersMap) {
                        if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 200) {
                            players.add(pl);
                        }
                    }
                    for (Player pl : players) {
                        Service.getInstance().chat(this.player, "Phẹt.....");
                        Service.getInstance().chat(pl, "hự.....");
                        EffectSkinService.gI().setSocola(pl, System.currentTimeMillis(), 5000);
                        Service.getInstance().Send_Caitrang(pl);
                        ItemTimeService.gI().sendItemTime(pl, 3780, 5000 / 1000);
                    }
                    this.lastTimeMabu = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBienCaRot() {
        try {
            if (this.player.nPoint.isThoDaiCa) {
                if (Util.canDoWithTime(lastTimeThodaica, 330_000)) {
                    List<Player> players = new ArrayList<>();
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (Player pl : playersMap) {
                        if (!this.player.equals(pl) && !pl.isBoss && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 200) {
                            players.add(pl);
                        }
                    }
                    for (Player pl : players) {
                        EffectSkinService.gI().setThoDaiKa(pl, System.currentTimeMillis(), 300_000);
                        Service.getInstance().Send_Caitrang(pl);
                        Service.gI().chat(pl, textCarot[Util.nextInt(0, textCarot.length - 1)]);
                        ItemTimeService.gI().sendItemTime(pl, 4082, 300_000 / 1000);
                    }
                    this.lastTimeThodaica = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateXChuong() {
        try {
            if (this.player.nPoint != null && this.player.nPoint.xChuong != 0) {
                if (Util.canDoWithTime(lastTimeXChuong, 60000)) {
                    this.isXChuong = true;
                    this.lastTimeXChuong = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMaPhongBa() {
        try {
            if (this.player.effectSkill != null && this.player.effectSkill.isBinh && this.player.effectSkill.playerUseMafuba != null) {
                if (Util.canDoWithTime(lastTimeMaPhongBa, 500) && this.player.effectSkill.playerUseMafuba.playerSkill != null) {
                    double param = this.player.effectSkill.playerUseMafuba.playerSkill.getSkillbyId(Skill.MA_PHONG_BA).point * (this.player.effectSkill.typeBinh == 0 ? 1 : 2);
                    long subHp = Util.CrisGH((long) (this.player.effectSkill.playerUseMafuba.nPoint.hpMax * param / 100));
                    if (subHp >= this.player.nPoint.hp) {
                        subHp = Math.abs(this.player.nPoint.hp - 100);
                    }
                    PlayerService.gI().sendInfoHpMpMoney(this.player);
                    Service.gI().Send_Info_NV(this.player);
                    this.player.injured(this.player.effectSkill.playerUseMafuba, subHp, true, false);
                    this.lastTimeMaPhongBa = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHalloween() {
        try {
            if (this.player.effectSkill != null && this.player.effectSkill.isHalloween) {
                if (Util.canDoWithTime(lastTimeHalloween, 10000)) {
                    List<Player> playersMap = this.player.zone.getNotBosses();
                    for (int i = playersMap.size() - 1; i >= 0; i--) {
                        Player pl = playersMap.get(i);
                        if (pl != null && pl.nPoint != null && !this.player.equals(pl) && pl.effectSkill != null && !pl.effectSkill.isHalloween && !pl.isDeTu && !pl.isBo && !pl.isMe && !pl.isDie()
                                && Util.getDistance(this.player, pl) <= 200) {
                            EffectSkillService.gI().setIsHalloween(pl, -1, 1800000);
                        }

                    }
                    this.lastTimeHalloween = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // giáp tập luyện
    private void updateTrainArmor() {
        // tăng thời gian giáp tập luyện đang mặc
        if (Util.canDoWithTime(lastTimeAddTimeTrainArmor, 60000) && !Util.canDoWithTime(lastTimeAttack, 30000)) {
            if (this.player.nPoint.wearingTrainArmor && this.player.inventory.trainArmor != null && this.player.inventory.trainArmor.itemOptions != null) {
                for (ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                    if (io != null && io.optionTemplate.id == 9) {
                        if (io.param < 1000) {
                            io.param++;
                            InventoryService.gI().sendItemBody(player);
                        }
                        break;
                    }
                }
            }
            this.lastTimeAddTimeTrainArmor = System.currentTimeMillis();
        }

        // giảm thời gian giáp tập luyện trong túi và rương
        if (Util.canDoWithTime(lastTimeSubTimeTrainArmor, 60000)) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item != null && item.isNotNullItem() && ItemService.gI().isTrainArmor(item) && item.itemOptions != null) {
                    for (ItemOption io : item.itemOptions) {
                        if (io != null && io.optionTemplate.id == 9 && io.param > 0) {
                            io.param--;
                        }
                    }
                }
            }
            for (Item item : this.player.inventory.itemsBox) {
                if (item != null && item.isNotNullItem() && ItemService.gI().isTrainArmor(item) && item.itemOptions != null) {
                    for (ItemOption io : item.itemOptions) {
                        if (io != null && io.optionTemplate.id == 9 && io.param > 0) {
                            io.param--;
                        }
                    }
                }
            }
            this.lastTimeSubTimeTrainArmor = System.currentTimeMillis();
            InventoryService.gI().sendItemBag(player);
            Service.gI().point(this.player);
        }
    }

    private void updateVoHinh() {
        if (this.player.nPoint != null && this.player.nPoint.wearingVoHinh) {
            if (Util.canDoWithTime(lastTimeAttack, 5000)) {
                isVoHinh = Util.canDoWithTime(lastTimeVoHinh, 5000);
            } else {
                isVoHinh = false;
            }
        } else {
            isVoHinh = false;
        }
    }

    public void dispose() {
        this.player = null;
    }
}
