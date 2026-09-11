package nro.card;

import nro.player.Player;
import nro.services.Service;
import network.io.Message;

import java.util.ArrayList;
import java.util.List;

public class RadarService {

    public List<RadarCard> RADAR_TEMPLATE = new ArrayList<>();

    private static RadarService instance;

    public static RadarService gI() {
        if (instance == null) {
            instance = new RadarService();
        }
        return instance;
    }

    // ========== GỬI TOÀN BỘ DANH SÁCH THẺ ==========
    public void sendRadar(Player pl, List<Card> cards) {
        try {
            Message m = new Message(127);
            m.writer().writeByte(0);
            m.writer().writeShort(RADAR_TEMPLATE.size());
            for (RadarCard radar : RADAR_TEMPLATE) {
                Card card = cards.stream().filter(c -> c.Id == radar.Id).findFirst().orElse(null);
                if (card == null) {
                    card = new Card(radar.Max, radar.Options);
                }
                m.writer().writeShort(radar.Id);
                m.writer().writeShort(radar.IconId);
                m.writer().writeByte(radar.Rank);
                m.writer().writeByte(card.Amount);
                m.writer().writeByte(card.MaxAmount);
                m.writer().writeByte(radar.Type);
                switch (radar.Type) {
                    case 0:
                        m.writer().writeShort(radar.Template);
                        break;
                    case 1:
                        m.writer().writeShort(radar.Head);
                        m.writer().writeShort(radar.Body);
                        m.writer().writeShort(radar.Leg);
                        m.writer().writeShort(radar.Bag);
                        break;
                }
                m.writer().writeUTF(radar.Name);
                m.writer().writeUTF(radar.Info);
                m.writer().writeByte(card.Level);
                m.writer().writeByte(card.Used);
                m.writer().writeByte(radar.Options.size());
                for (OptionCard option : radar.Options) {
                    m.writer().writeByte(option.id);
                    m.writer().writeShort(option.param);
                    m.writer().writeByte(option.active);
                }
            }
            m.writer().flush();
            pl.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== DÙNG / THÁO CARD ==========
    public void Radar1(Player pl, short id, int use) {
        try {
            if (pl == null || pl.Cards == null) {
                return;
            }

            if (use == 1) {
                // Tắt con cũ
                for (Card card : pl.Cards) {
                    if (card != null) {
                        card.Used = 0;
                    }
                }

                // Bật con mới
                for (Card card : pl.Cards) {
                    if (card != null && card.Id == id) {
                        card.Used = 1;
                        break;
                    }
                }
            } else {
                // Tắt đúng card đang chọn
                for (Card card : pl.Cards) {
                    if (card != null && card.Id == id) {
                        card.Used = 0;
                        break;
                    }
                }
            }

            Message message = new Message(127);
            message.writer().writeByte(1);
            message.writer().writeShort(id);
            message.writer().writeByte(use);
            message.writer().flush();
            pl.sendMessage(message);
            message.cleanup();

            // Xóa aura cũ trước
            sendAura(pl, -1, 0);

            // Cập nhật aura mới
            updateAura(pl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== ĐỔI LEVEL CARD ==========
    public void RadarSetLevel(Player pl, int id, int level) {
        try {
            Message message = new Message(127);
            message.writer().writeByte(2);
            message.writer().writeShort(id);
            message.writer().writeByte(level);
            message.writer().flush();
            pl.sendMessage(message);
            message.cleanup();

            updateAura(pl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== ĐỔI SỐ LƯỢNG CARD ==========
    public void RadarSetAmount(Player pl, int id, int amount, int max_amount) {
        try {
            Message message = new Message(127);
            message.writer().writeByte(3);
            message.writer().writeShort(id);
            message.writer().writeByte(amount);
            message.writer().writeByte(max_amount);
            message.writer().flush();
            pl.sendMessage(message);
            message.cleanup();

            updateAura(pl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== GỬI AURA ==========
    public void sendAura(Player pl, int id_Aura, int id_Eff_Set_Item) {
        try {
            Message message = new Message(127);
            message.writer().writeByte(4);
            message.writer().writeInt((int) pl.id);
            message.writer().writeShort(id_Aura);
            message.writer().writeByte(id_Eff_Set_Item);
            Service.gI().sendMessAllPlayerInMap(pl, message);
            message.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== HÀM TIỆN ÍCH UPDATE AURA ==========
   public void updateAura(Player pl) {
    try {
        byte newAura = pl.getAura();

        // tắt aura cũ trước
        sendAura(pl, -1, 0);

        // bật aura mới
        if (newAura != -1) {
            sendAura(pl, newAura, 0);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}