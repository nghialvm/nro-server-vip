package nro.map.DeathOrAliveArena;



import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossStatus;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstPlayer;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import nro.boss.map.DeathOrAliveArena.BongBang;
import nro.boss.map.DeathOrAliveArena.Dracula;
import nro.boss.map.DeathOrAliveArena.NguoiVoHinh;
import nro.boss.map.DeathOrAliveArena.ThoDauBac;
import nro.boss.map.DeathOrAliveArena.VuaQuySaTang;
import nro.map.Zone;
import nro.matches.Pvp.DHVT;
import nro.npc.Npc;

public class DeathOrAliveArena {

    @Setter
    @Getter
    private Player player;

    private Boss boss;

    @Setter
    private Npc npc;

    @Setter
    private long timeTotal;

    private int time;
    @Setter
    private int round;
    private int timeWait;

    @Setter
    @Getter
    private int cuocBaHatMit;

    @Setter
    @Getter
    private int cuocPlayer;

    @Setter
    @Getter
    private Zone zone;

    private final List<Player> binhChon = new ArrayList<>();

    public boolean endChallenge;

    public void update() {

        if (player.zone == null) {
            this.endChallenge();
            return;
        }

        if (timeWait > 0) {
            switch (timeWait) {
                case 5: {
                    if (round > 1) {
                        npc.npcChat(player, "Khá lắm, chuẩn bị đánh tiếp nào");
                    }
                    break;
                }
                case 3:
                    Service.gI().chat(boss, "Sẵn sàng chưa?");
                    break;
                case 1: {
                    ready();
                    npc.npcChat(player, "Con tắc kè màu xanh màu đỏ...Em bắt về em nấu cà ri...Ồ là la ýe...");
                    break;
                }
            }
            timeWait--;
            return;
        }

        if (time > 0) {
            time--;
            if (player.isDie()) {
                die();
                return;
            }
            if (player.location != null && !player.isDie() && player != null && player.zone != null) {
                if (boss.isDie()) {
                    round++;
                    timeTotal += (180 - time);
                    traThuongHatMit(true);
                    boss.leaveMap();
                    toTheNextRound();
                }
                if (player.location.y > 336 && !(player.location.x > 322 && player.location.x < 614)) {
                    leave();
                    return;
                }
                if (!player.isPKDHVT) {
                    leave();
                }
            } else {
                if (boss != null) {
                    boss.leaveMap();
                }
                DeathOrAliveArenaManager.gI().remove(this);
            }

        } else {
            timeOut();
        }
    }

    public void ready() {
        setTime(181);
        DeathOrAliveArenaService.gI().sendTypePK(player, boss);
        PlayerService.gI().changeAndSendTypePK(this.player, ConstPlayer.PK_PVP);
        boss.changeStatus(BossStatus.ACTIVE);
        new DHVT(player, boss);
    }

    public void toTheNextRound() {
        try {
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            Boss bss;
            switch (round) {
                case 0:
                    bss = new Dracula(player);
                    break;
                case 1:
                    bss = new NguoiVoHinh(player);
                    break;
                case 2:
                    bss = new BongBang(player);
                    break;
                case 3:
                    bss = new VuaQuySaTang(player);
                    break;
                case 4:
                    bss = new ThoDauBac(player);
                    break;
                case 5: {
                    champion();
                    return;
                }
                default: {
                    return;
                }
            }
            Service.gI().setPos(player, 401, 336);
            setTimeWait(5);
            setBoss(bss);
        } catch (Exception e) {
        }
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    private void die() {
        traThuongHatMit(false);
        Service.gI().sendThongBao(player, "Bạn đã thua, hẹn gặp lại ở giải sau");
        npc.npcChat(player.zone, "Người tiếp theo chuẩn bị.");
        if (player.zone != null) {
            endChallenge();
        }
    }

    private void timeOut() {
        if (round < 5) {
            traThuongHatMit(false);
            Service.gI().sendThongBao(player, "Bạn đã thua, hẹn gặp lại ở giải sau");
            npc.npcChat(player.zone, "Người tiếp theo chuẩn bị.");
            endChallenge();
        }
    }

    private void champion() {
        if (player.timePKVDST == 0 || player.timePKVDST > timeTotal) {
            player.timePKVDST = timeTotal;
        }
        endChallenge();
        npc.npcChat(player, "Đây là phần thưởng cho con.");
        reward();
    }

    public void leave() {
        if (round < 5) {
            traThuongHatMit(false);
            Service.gI().sendThongBao(player, "Bạn đã thua, hẹn gặp lại ở giải sau");
            npc.npcChat(player.zone, "Người tiếp theo chuẩn bị.");
            setTime(0);
            endChallenge();
        }
    }

    private void reward() {
        player.haveRewardVDST = true;
    }

    private void traThuongHatMit(boolean playerWin) {
        try {
            long tongCuoc = (cuocBaHatMit + cuocPlayer) * 9_000_000;
            if (cuocBaHatMit >= 0 && !playerWin || cuocPlayer > 0 && playerWin) {
                if (playerWin) {
                    tongCuoc /= cuocPlayer;
                } else {
                    tongCuoc /= cuocBaHatMit;
                }
                for (Player pl : binhChon) {
                    try {
                        if (playerWin) {
                            int cuoc = pl.binhChonPlayer;
                            if (cuoc > 0 && pl.zoneBinhChon.equals(zone)) {
                                long vangNhan = cuoc * tongCuoc;
                                pl.inventory.gold += vangNhan;
                                pl.binhChonPlayer = 0;
                                pl.binhChonHatMit = 0;
                                Service.gI().sendMoney(pl);
                                Service.gI().sendThongBao(pl, "Chúc mừng bạn đã thắng " + cuoc + " bình chọn đúng và được thưởng " + Util.formatNumber(vangNhan, FormatStyle.VIETNAMESE) + " vàng");
                            }
                        } else {
                            int cuoc = pl.binhChonHatMit;
                            if (cuoc > 0 && pl.zoneBinhChon.equals(zone)) {
                                long vangNhan = cuoc * tongCuoc;
                                pl.inventory.gold += vangNhan;
                                pl.binhChonPlayer = 0;
                                pl.binhChonHatMit = 0;
                                Service.gI().sendMoney(pl);
                                Service.gI().sendThongBao(pl, "Chúc mừng bạn đã thắng " + cuoc + " bình chọn đúng và được thưởng " + Util.formatNumber(vangNhan, FormatStyle.VIETNAMESE) + " vàng");
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
            cuocBaHatMit = 0;
            cuocPlayer = 0;
            binhChon.clear();
        } catch (Exception e) {
        }
    }

    public void endChallenge() {
        if (!endChallenge) {
            endChallenge = true;
            Service.gI().sendPlayerVS(player, null, (byte) 0);
            if (player.zone != null) {
                PlayerService.gI().hoiSinh(player);
            }
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            if (player != null && player.zone != null && player.zone.map.mapId == 112) {
                Service.gI().setPos(player, Util.nextInt(100, 200), 408);
            }
            player.isPKDHVT = false;
            if (boss != null) {
                boss.leaveMap();
            }
            zone = null;
            DeathOrAliveArenaManager.gI().remove(this);
        }
    }

    public void addBinhChon(Player pl) {
        if (!binhChon.contains(pl)) {
            binhChon.add(pl);
        }
    }
}
