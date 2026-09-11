package nro.server;

import Utils.Functions;
import Utils.Logger;
import jbcd.ConnectDB;
import jbcd.dao.PlayerDAO;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import models.Item.Item;
import models.Item.ItemTimeService;
import network.interfaces.ISession;
import network.io.Message;
import network.session.MySession;
import network.session.SessionManager;
import nro.dragon.SummonDragon;
import nro.dragon.SummonDragonNamek;
import nro.inventory.InventoryService;
import nro.map.DragonBallNamec.NgocRongNamec;
import nro.map.DragonNamecWar.TranhNgoc;
import nro.map.ItemMap;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.Fun.TransactionService;
import nro.services.Service;

public class Client implements Runnable {

    private static Client instance;

    private final Map<Long, Player> players_id = new HashMap<>();
    private final Map<Integer, Player> players_userId = new HashMap<>();
    private final Map<String, Player> players_name = new HashMap<>();

    @Getter
    private final List<Player> players = new ArrayList<>();

    private final Object lock = new Object();

    private Client() {
        new Thread(this, "Update Client").start();
    }

    public static Client gI() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    /**
     * Không add trực tiếp nữa.
     * Tất cả đi qua safePut để chống login 2 tab / 1 account.
     */
    public void put(Player player) {
        safePut(player);
    }

    /**
     * Last login wins:
     * - Tab 1 đang online.
     * - Tab 2 đăng nhập cùng account.
     * - Tab 2 được vào.
     * - Tab 1 nhận thông báo rồi bị kick.
     */
    public boolean safePut(Player player) {
        if (player == null || player.getSession() == null) {
            return false;
        }

        MySession newSession = player.getSession();
        MySession oldSessionToKick = null;

        synchronized (lock) {
            int userId = newSession.userId;

            Player oldByUser = players_userId.get(userId);
            if (oldByUser != null && oldByUser != player) {
                MySession oldSession = oldByUser.getSession();

                if (oldSession != null && oldSession != newSession) {
                    oldSessionToKick = oldSession;
                }

                removePlayerFromCollections(oldByUser);
            }

            Player oldById = players_id.get(player.id);
            if (oldById != null && oldById != player) {
                MySession oldSession = oldById.getSession();

                if (oldSessionToKick == null && oldSession != null && oldSession != newSession) {
                    oldSessionToKick = oldSession;
                }

                removePlayerFromCollections(oldById);
            }

            Player oldByName = players_name.get(player.name);
            if (oldByName != null && oldByName != player) {
                MySession oldSession = oldByName.getSession();

                if (oldSessionToKick == null && oldSession != null && oldSession != newSession) {
                    oldSessionToKick = oldSession;
                }

                removePlayerFromCollections(oldByName);
            }

            players_id.put(player.id, player);
            players_userId.put(userId, player);
            players_name.put(player.name, player);

            if (!players.contains(player)) {
                players.add(player);
            }
        }

        if (oldSessionToKick != null && oldSessionToKick != newSession) {
            System.out.println("[LOGIN_DUP] userId=" + newSession.userId
                    + " | kickOldSession=" + oldSessionToKick
                    + " | keepNewSession=" + newSession);

            sendLoginOtherDeviceMessage(oldSessionToKick);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            try {
                kickSession(oldSessionToKick);
            } catch (Exception e) {
                Logger.logException(Client.class, e);
            }
        }

        return true;
    }

    private void sendLoginOtherDeviceMessage(MySession session) {
        if (session == null) {
            return;
        }

        Message msg = null;
        try {
            msg = new Message(-70);
            msg.writer().writeUTF("Tài khoản của bạn đã được đăng nhập ở nơi khác.");
            session.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(Client.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void remove(MySession session) {
        if (session == null) {
            return;
        }

        Player playerToDispose = null;

        synchronized (lock) {
            if (session.player != null) {
                playerToDispose = session.player;
                session.player = null;
                removePlayerFromCollections(playerToDispose);
            }

            session.connected = false;
        }

        if (playerToDispose != null) {
            disposePlayerData(playerToDispose);

            try {
                playerToDispose.dispose();
            } catch (Exception e) {
                Logger.logException(Client.class, e);
            }
        }

        if (session.joinedGame) {
            session.joinedGame = false;

            try {
                ConnectDB.executeUpdate(
                        "update account set last_time_logout = ? where id = ?",
                        new Timestamp(System.currentTimeMillis()),
                        session.userId
                );
            } catch (Exception e) {
                Logger.logException(Client.class, e);
            }
        }

        ServerManager.gI().disconnect(session);
    }

    private void removePlayerFromCollections(Player player) {
        if (player == null) {
            return;
        }

        players_id.remove(player.id);

        Player byName = players_name.get(player.name);
        if (byName == player) {
            players_name.remove(player.name);
        }

        MySession s = player.getSession();
        if (s != null) {
            Player byUser = players_userId.get(s.userId);
            if (byUser == player) {
                players_userId.remove(s.userId);
            }
        }

        players.remove(player);
    }

    private void disposePlayerData(Player player) {
        if (player == null) {
            return;
        }

        try {
            if (!player.beforeDispose) {
                player.beforeDispose = true;

                try {
                    TranhNgoc.gI().removePlayersCadic(player);
                    TranhNgoc.gI().removePlayersFide(player);
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }

                if (player.zone != null && player.zone.map != null) {
                    player.mapIdBeforeLogout = player.zone.map.mapId;
                }

                if (player.idNRNM != -1 && player.zone != null) {
                    try {
                        ItemMap itemMap = new ItemMap(
                                player.zone,
                                player.idNRNM,
                                1,
                                player.location.x,
                                player.location.y,
                                -1
                        );

                        Service.gI().dropItemMap(player.zone, itemMap);
                        NgocRongNamec.gI().pNrNamec[player.idNRNM - 353] = "";
                        NgocRongNamec.gI().idpNrNamec[player.idNRNM - 353] = -1;
                        player.idNRNM = -1;
                    } catch (Exception e) {
                        Logger.logException(Client.class, e);
                    }
                }

                try {
                    ChangeMapService.gI().exitMap(player);
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }

                try {
                    TransactionService.gI().cancelTrade(player);
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }

                try {
                    if (player.clan != null) {
                        player.clan.removeMemberOnline(null, player);
                    }
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }

                try {
                    if (player.itemTime != null && player.itemTime.isUseTDLT) {
                        Item tdlt = null;

                        try {
                            tdlt = InventoryService.gI().findItemBag(player, 521);
                        } catch (Exception e) {
                            Logger.logException(Client.class, e);
                        }

                        if (tdlt != null) {
                            ItemTimeService.gI().turnOffTDLT(player, tdlt);
                        }
                    }
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }

                try {
                    if (SummonDragon.gI().playerSummonShenron != null
                            && SummonDragon.gI().playerSummonShenron.id == player.id) {
                        SummonDragon.gI().isPlayerDisconnect = true;
                    }

                    if (SummonDragonNamek.gI().playerSummonShenron != null
                            && SummonDragonNamek.gI().playerSummonShenron.id == player.id) {
                        SummonDragonNamek.gI().isPlayerDisconnect = true;
                    }

                    if (player.shenronEvent_Christmas != null) {
                        player.shenronEvent_Christmas.isPlayerDisconnect = true;
                    }

                    if (player.shenronEvent_Halloween != null) {
                        player.shenronEvent_Halloween.isPlayerDisconnect = true;
                    }
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }

                try {
                    if (player.DeTrung != null) {
                        player.DeTrung.mobMeDie();
                    }

                    if (player.Detu != null) {
                        if (player.Detu.DeTrung != null) {
                            player.Detu.DeTrung.mobMeDie();
                        }

                        try {
                            ChangeMapService.gI().exitMap(player.Detu);
                        } catch (Exception e) {
                            Logger.logException(Client.class, e);
                        }
                    }
                } catch (Exception e) {
                    Logger.logException(Client.class, e);
                }
            }
        } catch (Exception e) {
            Logger.logException(Client.class, e);
        } finally {
            try {
                PlayerDAO.updatePlayer(player);
            } catch (Exception e) {
                Logger.logException(Client.class, e);
            }
        }
    }

    public void kickSession(MySession session) {
        if (session == null) {
            return;
        }

        try {
            remove(session);
        } catch (Exception e) {
            Logger.logException(Client.class, e);
        }

        try {
            session.disconnect();
        } catch (Exception e) {
            Logger.logException(Client.class, e);
        }
    }

    public Player getPlayerByID(long playerId) {
        synchronized (lock) {
            return players_id.get(playerId);
        }
    }

    public Player getPlayerByUser(int userId) {
        synchronized (lock) {
            return players_userId.get(userId);
        }
    }

    public Player getPlayerByName(String name) {
        synchronized (lock) {
            return players_name.get(name);
        }
    }

    public List<Player> getPlayersSnapshot() {
        synchronized (lock) {
            return new ArrayList<>(players);
        }
    }

    public void close() {
        List<Player> snapshot;

        synchronized (lock) {
            snapshot = new ArrayList<>(players);
        }

        Logger.log(Logger.YELLOW, "BEGIN KICK OUT SESSION " + snapshot.size() + "\n");

        for (Player pl : snapshot) {
            try {
                if (pl != null && pl.getSession() != null) {
                    this.kickSession(pl.getSession());
                }
            } catch (Exception e) {
                Logger.logException(Client.class, e);
            }
        }

        Logger.success("SUCCESSFUL\n");
    }

    private void update() {
        List<ISession> sessions = SessionManager.gI().getSessions();

        for (int i = sessions.size() - 1; i >= 0; i--) {
            ISession s = sessions.get(i);
            MySession session = (MySession) s;

            if (session == null) {
                sessions.remove(i);
                continue;
            }

            if (session.timeWait > 0) {
                session.timeWait--;

                if (session.timeWait == 0) {
                    kickSession(session);
                }
            }
        }
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            long st = System.currentTimeMillis();

            try {
                update();
            } catch (Exception e) {
                Logger.logException(Client.class, e);
            }

            Functions.sleep(Math.max(1000 - (System.currentTimeMillis() - st), 10));
        }
    }
}