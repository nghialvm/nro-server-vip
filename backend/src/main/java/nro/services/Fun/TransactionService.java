package nro.services.Fun;

import Utils.Functions;
import nro.server.Client;
import nro.bot.Bot;
import nro.player.Player;
import nro.server.Maintenance;
import nro.server.ServerManager;
import network.io.Message;
import nro.services.Service;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import jbcd.ConnectDB;
import jbcd.dao.PlayerDAO;
import nro.bot.Event.BotEvent;

public class TransactionService implements Runnable {

    private static final int TIME_DELAY_TRADE = 30000;

    static final Map<Player, Trade> PLAYER_TRADE = new HashMap<>();

    private static final byte SEND_INVITE_TRADE = 0;
    private static final byte ACCEPT_TRADE = 1;
    private static final byte ADD_ITEM_TRADE = 2;
    private static final byte CANCEL_TRADE = 3;
    private static final byte LOCK_TRADE = 5;
    private static final byte ACCEPT = 7;

    private static TransactionService i;

    private TransactionService() {
    }

    public static TransactionService gI() {
        if (i == null) {
            i = new TransactionService();
            Executors.newSingleThreadExecutor().submit(i);
        }
        return i;
    }

    public void controller(Player pl, Message msg) {
        try {
            byte action = msg.reader().readByte();
            int playerId = -1;
            Player plMap = null;
            Trade trade = PLAYER_TRADE.get(pl);

            if (pl.baovetaikhoan) {
                Service.gI().sendThongBao(pl, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
                return;
            }

            if (action == SEND_INVITE_TRADE) {
                pl.iDMark.setTransactionWP(false);
                pl.iDMark.setTransactionWVP(false);
            }

            switch (action) {
                case SEND_INVITE_TRADE:
                case ACCEPT_TRADE:
                    if (pl.isPl() && !pl.getSession().actived) {
                        Service.gI().sendThongBao(pl,
                                "Truy Cập: " + ServerManager.DOMAIN + "\nĐể Mở Thành Viên");
                        return;
                    }

                    playerId = msg.reader().readInt();
                    plMap = pl.zone.getPlayerInMap(playerId);

                    if (plMap != null && plMap.isPlandBot()) {
                        if (plMap.tradeWVP) {
                            return;
                        }

                        trade = PLAYER_TRADE.get(pl);

                        if (trade == null) {
                            trade = PLAYER_TRADE.get(plMap);
                        }

                        if (trade == null) {
                            if (action == SEND_INVITE_TRADE) {
                                if (Util.canDoWithTime(pl.iDMark.getLastTimeTrade(), TIME_DELAY_TRADE)
                                        && Util.canDoWithTime(plMap.iDMark.getLastTimeTrade(), TIME_DELAY_TRADE)) {

                                    boolean checkLogout1 = false;
                                    boolean checkLogout2 = false;

                                    try (Connection con = ConnectDB.getConnection()) {
                                        checkLogout1 = PlayerDAO.checkLogout(con, pl);
                                        checkLogout2 = PlayerDAO.checkLogout(con, plMap);
                                    } catch (Exception e) {
                                    }

                                    if (checkLogout1) {
                                        Client.gI().kickSession(pl.getSession());
                                        break;
                                    }

                                    if (checkLogout2) {
                                        Client.gI().kickSession(plMap.getSession());
                                        break;
                                    }

                                    pl.iDMark.setLastTimeTrade(System.currentTimeMillis());
                                    pl.iDMark.setPlayerTradeId((int) plMap.id);
                                    sendInviteTrade(pl, plMap);
                                } else {
                                    Service.gI().sendThongBao(pl, "Thử lại sau "
                                            + TimeUtil.getTimeLeft(
                                                    Math.max(pl.iDMark.getLastTimeTrade(), plMap.iDMark.getLastTimeTrade()),
                                                    TIME_DELAY_TRADE / 1000
                                            ));
                                }
                            } else {
                                if (plMap.iDMark.getPlayerTradeId() == pl.id) {
                                    trade = new Trade(pl, plMap);
                                    trade.openTabTrade();
                                }
                            }
                        } else {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                        }
                    }
                    break;

                case ADD_ITEM_TRADE:
                    if (trade != null) {
                        byte index = msg.reader().readByte();
                        int quantity = msg.reader().readInt();

                        if (quantity < 0) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            trade.cancelTrade();
                            break;
                        }

                        if (quantity == 0) {
                            quantity = 1;
                        }

                        if (index != -1 && quantity > Trade.QUANLITY_MAX) {
                            Service.gI().sendThongBao(pl, "Đã quá giới hạn giao dịch...");
                            trade.cancelTrade();
                            break;
                        }

                        trade.addItemTrade(pl, index, quantity);
                    }
                    break;

                case CANCEL_TRADE:
                    if (trade != null) {
                        trade.cancelTrade();
                    }
                    break;

                case LOCK_TRADE:
                    if (Maintenance.isRunning) {
                        trade.cancelTrade();
                        break;
                    }

                    if (trade != null) {
                        trade.lockTran(pl);
                    }
                    break;

                case ACCEPT:
                    if (Maintenance.isRunning) {
                        trade.cancelTrade();
                        break;
                    }

                    if (trade != null) {
                        trade.acceptTrade();

                        if (trade.accept == 1) {
                            Service.gI().sendThongBao(pl, "Xin chờ đối phương đồng ý");
                        } else if (trade.accept == 2) {
                            trade.dispose();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logException(this.getClass(), e);
        }
    }

    /**
     * Mời giao dịch
     */
    private void sendInviteTrade(Player plInvite, Player plReceive) {
        if (plReceive.isBot) {
            if (((Bot) plReceive).type == 1) {
                ((Bot) plReceive).shop.activeTraDe(plInvite);
            } else if (((Bot) plReceive).type == 3) {
                ((Bot) plReceive).shopsell.activeTraDe(plInvite);
            }
        }

        if (plReceive.isBot_Event) {
            if (((BotEvent) plReceive).type == 0) {
                ((BotEvent) plReceive).shop.activeTraDe(plInvite);
            }

            if (((BotEvent) plReceive).type == 1) {
                ((BotEvent) plReceive).shop2.activeTraDe(plInvite);
            }

            if (((BotEvent) plReceive).type == 2) {
                ((BotEvent) plReceive).shop3.activeTraDe(plInvite);
            }

            if (((BotEvent) plReceive).type == 3) {
                ((BotEvent) plReceive).shop4.activeTraDe(plInvite);
            }

            if (((BotEvent) plReceive).type == 4) {
                ((BotEvent) plReceive).shop5.activeTraDe(plInvite);
            }
        }

        Message msg;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plInvite.id);
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hủy giao dịch
     *
     * @param player
     */
    public void cancelTrade(Player player) {
        Trade trade = PLAYER_TRADE.get(player);

        if (trade != null) {
            trade.cancelTrade();
        }
    }

    public boolean check(Player player) {
        return PLAYER_TRADE.get(player) != null;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long st = System.currentTimeMillis();
                Set<Map.Entry<Player, Trade>> entrySet = PLAYER_TRADE.entrySet();

                for (Map.Entry entry : entrySet) {
                    ((Trade) entry.getValue()).update();
                }

                Functions.sleep(Math.max(300 - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
            }
        }
    }
}