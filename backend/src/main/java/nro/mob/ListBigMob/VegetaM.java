package nro.mob.ListBigMob;

import nro.services.Service;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import network.io.Message;
import nro.mob.BigBoss;
import nro.mob.Mob;
import nro.player.Player;
import nro.server.Manager;

public class VegetaM extends BigBoss {

    public VegetaM(Mob mob) {
        super(mob);
    }

    @Override
    public void attack() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastBigBossAttackTime, 3000)) {
            if (this.zone.getNotBosses().isEmpty()) {
                return;
            }

            List<Player> players = new ArrayList<>();
            action = Util.nextInt(11, 12); // random attack range

            switch (action) {
                case 11: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 50) {
                            players.add(pl);
                            break;
                        }
                    }
                    break;
                }
                case 12: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 100) {
                            players.add(pl);
                            break;
                        }
                    }
                    break;
                }
            }

            // Nếu không tìm thấy player đủ gần, thì chọn 1 random để di chuyển đến
            if (players.isEmpty()) {
                int index = Util.nextInt(0, this.zone.getNotBosses().size() - 1);
                players.add(this.zone.getNotBosses().get(index));
                action = 10; // chỉ di chuyển
            }

            Message msg = null;
            try {
                msg = new Message(102);
                msg.writer().writeByte(action);
                msg.writer().writeByte(this.id);

                switch (action) {
                    case 10: { // Di chuyển đến gần player
                        for (Player player : players) {
                            moveToward(player);
                        }
                        msg.writer().writeShort(this.location.x);
                        msg.writer().writeShort(this.location.y);

                        // Gửi thông tin di chuyển để client vẽ hoạt ảnh
                        Message moveMsg = new Message(103); // bạn có thể đổi ID nếu client dùng cái khác
                        moveMsg.writer().writeByte(this.id);
                        moveMsg.writer().writeShort(this.location.x);
                        moveMsg.writer().writeShort(this.location.y);
                        moveMsg.writer().writeByte(this.status); // 1: đang di chuyển
                        Service.gI().sendMessAllPlayerInMap(this.zone, moveMsg);
                        break;
                    }

                    case 11:
                    case 12: { // Tấn công
                        msg.writer().writeByte(players.size());
                        int dir = 0;
                        for (Player pl : players) {
                            double dame = pl.injured(null, this.point.getDameAttack(), false, true);
                            msg.writer().writeInt((int) pl.id);
                            msg.writeCris(Util.CrisGH(dame), Manager.readInt);
                            dir = pl.location.x < this.location.x ? -1 : 1;
                        }
                        msg.writer().writeByte(dir);
                        this.status = 3; // status tấn công (client-side cần tương ứng)
                        break;
                    }
                }

                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                lastBigBossAttackTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void moveToward(Player player) {
        if (player == null || player.location == null) return;

        int dx = player.location.x - this.location.x;
        int dy = player.location.y - this.location.y;

        int step = 10; // mỗi lần di chuyển

        boolean moved = false;

        if (Math.abs(dx) > step) {
            this.location.x += dx > 0 ? step : -step;
            moved = true;
        } else {
            this.location.x = player.location.x;
        }

        if (Math.abs(dy) > step) {
            this.location.y += dy > 0 ? step : -step;
            moved = true;
        } else {
            this.location.y = player.location.y;
        }

        if (moved) {
            this.status = 1; 
        } else {
            this.status = 0; 
        }
    }
}
