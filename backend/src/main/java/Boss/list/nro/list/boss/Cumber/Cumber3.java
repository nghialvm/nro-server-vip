package Boss.list.nro.list.boss.Cumber;

import nro.player.Player;
import nro.services.Service;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import nro.map.ItemMap;
import Utils.Util;
import nro.npc.Special.MabuEgg;

public class Cumber3 extends Boss {

    private long st;

    public Cumber3() throws Exception {
        super(BossID.DRABULA3, false, true, false, false, BossesData.Darbula3);
    }

    @Override
    public void reward(Player plKill) {
        // Xác định vị trí rơi vật phẩm theo vị trí hiện tại của boss
        int dropX = this.location.x + Util.nextInt(-10, 10);
        int dropY = this.location.y; // hoặc this.location.yEnd nếu server có thuộc tính đó

        // ID vật phẩm rơi (tùy bạn muốn)
        int itemId = 457; // hoặc đổi sang vật phẩm khác, ví dụ 1500 để tránh trùng

        if (Util.isTrue(1, 2)) { // 50% tỉ lệ rơi
            ItemMap it = new ItemMap(this.zone, itemId, 1, dropX, dropY, plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        } else {
            if (plKill.mabuEgg == null) {
                MabuEgg.createMabuEgg(plKill);
                Service.gI().sendThongBao(plKill, "Bạn vừa nhận được Thỏi vàng!");
            }
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (Util.isTrue(10, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
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
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 3600000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }
}