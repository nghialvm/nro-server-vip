package nro.npc.ListNpc;



import nro.services.NpcService;
import nro.services.Service;
import nro.consignmentstore.ConsignShopService;
import nro.npc.Npc;
import nro.player.Player;

public class ShopKyGui extends Npc {

    public ShopKyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, 0,
                    "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.",
                    "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (canOpenNpc(pl)) {
            switch (select) {
                case 0:
                    NpcService.gI().createTutorial(pl, tempId, avartar,
                    "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\b"
                            + "Chỉ với 1000 hồng ngọc\bGiá trị ký gửi 1-2Tỷ thỏi vàng hoặc 1-2tỷ hồng ngọc\b"
                            + "Một người bán, vạn người mua, mại dô, mại dô");
                    break;
                case 1: {
                    if (pl.getSession().actived) {
                        ConsignShopService.gI().openShopKyGui(pl);
                        return;
                    }
                    Service.getInstance().sendThongBao(pl, "Bạn chưa kích hoạt tài khoản!");
                    break;
                }
            }
        }
    }
}
