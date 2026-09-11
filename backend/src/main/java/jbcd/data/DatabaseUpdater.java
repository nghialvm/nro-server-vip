package jbcd.data;

import nro.server.Client;
import Utils.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jbcd.ConnectDB;
import jbcd.CrisResultSet;
import jbcd.dao.PlayerDAO;
import static jbcd.dao.PlayerDAO.insertHistoryGold;
import network.session.MySession;
import nro.player.Player;


public class DatabaseUpdater {
    
    public static boolean addVND_byPlayer(Player player, int num) {
        String updateQuery = "UPDATE account SET vnd = vnd + ? WHERE id = ?";
        try ( Connection con = ConnectDB.getConnection();  PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                player.getSession().vnd += num;
                if (player.getSession().vnd > 2_000_000_000) {
                    player.getSession().vnd = 2_000_000_000;
                    try (PreparedStatement psLimit = con.prepareStatement("UPDATE account SET vnd = ? WHERE id = ?")) {
                        psLimit.setInt(1, 2_000_000_000);
                        psLimit.setInt(2, player.getSession().userId);
                        psLimit.executeUpdate();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating vnd for player " + player.name);
            return false;
        }
    }
    
    public static boolean addVND_byName(String name, int num) {
        String updateQuery = 
            "UPDATE account " +
            "JOIN player ON player.account_id = account.id " +
            "SET account.vnd = account.vnd + ? " +
            "WHERE player.name = ?";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setInt(1, num);
            ps.setString(2, name.trim());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                Player player = Client.gI().getPlayerByName(name);
                if (player != null) {
                    player.getSession().vnd += num;
                    if (player.getSession().vnd > 2_000_000_000) {
                        player.getSession().vnd = 2_000_000_000;

                        try (PreparedStatement psLimit = con.prepareStatement(
                            "UPDATE account " +
                            "JOIN player ON player.account_id = account.id " +
                            "SET account.vnd = ? " +
                            "WHERE player.name = ?")) {

                            psLimit.setInt(1, 2_000_000_000);
                            psLimit.setString(2, name.trim());
                            psLimit.executeUpdate();
                        }
                    }
                }
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating VND by character name: " + name);
            return false;
        }
    }
    
    public static boolean addVND_byIdPlayer(long playerId, int num) {
        String updateQuery = 
            "UPDATE account " +
            "JOIN player ON player.account_id = account.id " +
            "SET account.vnd = account.vnd + ? " +
            "WHERE player.id = ?";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setInt(1, num);
            ps.setLong(2, playerId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                Player player = Client.gI().getPlayerByID(playerId);
                if (player != null) {
                    player.getSession().vnd += num;
                    if (player.getSession().vnd > 2_000_000_000) {
                        player.getSession().vnd = 2_000_000_000;

                        try (PreparedStatement psLimit = con.prepareStatement(
                            "UPDATE account " +
                            "JOIN player ON player.account_id = account.id " +
                            "SET account.vnd = ? " +
                            "WHERE player.id = ?")) {

                            psLimit.setInt(1, 2_000_000_000);
                            psLimit.setLong(2, playerId);
                            psLimit.executeUpdate();
                        }
                    }
                }
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating VND by player ID: " + playerId);
            return false;
        }
    }
    
    public static boolean subVND_byPlayer(Player player, int num) {
        if (player.getSession().vnd < num) {
            return false;
        }

        String updateQuery = "UPDATE account SET vnd = vnd - ? WHERE id = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                player.getSession().vnd -= num;
                return true;
            }
        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating vnd for player " + player.name);
        }
        return false;
    }
        
    public static boolean subVND_byName(String name, int num) {
        String updateQuery = 
            "UPDATE account " +
            "JOIN player ON player.account_id = account.id " +
            "SET account.vnd = account.vnd - ? " +
            "WHERE player.name = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setInt(1, num);
            ps.setString(2, name.trim());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                Player player = Client.gI().getPlayerByName(name);
                if (player != null) {
                    player.getSession().vnd -= num;
                    if (player.getSession().vnd < 0) {
                        player.getSession().vnd = 0;
                        limitVND(con, player.getSession().userId, 0);
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error subtracting VND by character name: " + name);
        }
        return false;
    }
    
    public static boolean subVND_byIdPlayer(long playerId, int num) {
        String updateQuery = 
            "UPDATE account " +
            "JOIN player ON player.account_id = account.id " +
            "SET account.vnd = account.vnd - ? " +
            "WHERE player.id = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setInt(1, num);
            ps.setLong(2, playerId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                Player player = Client.gI().getPlayerByID(playerId);
                if (player != null) {
                    player.getSession().vnd -= num;
                    if (player.getSession().vnd < 0) {
                        player.getSession().vnd = 0;
                        limitVND(con, player.getSession().userId, 0);
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error subtracting VND by player ID: " + playerId);
        }
        return false;
    }
    
    private static void limitVND(Connection con, int accountId, int limit) throws SQLException {
        String limitQuery = "UPDATE account SET vnd = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(limitQuery)) {
            ps.setInt(1, limit);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    public static boolean addvnd(Player player, int num) {
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd + ?), tongnap = (tongnap + ?) where id = ?");
            ps.setInt(1, num);
            ps.setInt(2,num);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd += num;
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lá»—i update VND " + player.name);
            return false;
        } finally {
        }
        if (num > 1000) {
            insertHistoryGold(player, num);
        }
        return true;
    }
    
    public static boolean addDaysToTimeUpSkhByPlayerId(long playerId, int num) {
        String updateQuery = 
            "UPDATE account " +
            "JOIN player ON player.account_id = account.id " +
            "SET account.accountAgeDays = account.accountAgeDays + ? " +
            "WHERE player.id = ?";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setInt(1, num);       // sá»‘ ngÃ y muá»‘n cá»™ng thÃªm
            ps.setLong(2, playerId); // id player

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating accountAgeDays by player ID: " + playerId);
            return false;
        }
    }
    
    public static boolean refreshAccountAgeDays(MySession session) {
        CrisResultSet rs = null;
        try {
            rs = ConnectDB.executeQuery(
                "SELECT accountAgeDays FROM account WHERE username = ? AND password = ?", 
                session.uu, session.pp
            );

            if (rs.next()) {
                session.accountAgeDays = rs.getInt("accountAgeDays");
                return true;
            }
        } catch (Exception e) {
            if (session == null) {
                return false;
            }
            Logger.error(session.uu);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return false;
    }

    public static boolean addVip_byPlayer(Player player, int num) {
        String updateQuery = "UPDATE account SET Vip_Point = Vip_Point + ? WHERE id = ?";
        try ( Connection con = ConnectDB.getConnection();  PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                player.getSession().Vip_Point += num;
                if (player.getSession().Vip_Point > 2_000_000_000) {
                    player.getSession().Vip_Point = 2_000_000_000;
                    try (PreparedStatement psLimit = con.prepareStatement("UPDATE account SET Vip_Point = ? WHERE id = ?")) {
                        psLimit.setInt(1, 2_000_000_000);
                        psLimit.setInt(2, player.getSession().userId);
                        psLimit.executeUpdate();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating Vip_Point for player " + player.name);
            return false;
        }
    }

    
    public static void updateLastTimeUpdateTask(Player player, long num) {
        if (player == null) {
            return;
        }
        String query = "UPDATE player SET lastTimeUpdateTask = ? WHERE id = ?";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, num);
            ps.setLong(2, player.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            Logger.logException(PlayerDAO.class, e, "Error updating lastTimeUpdateTask for player: " + player.name);
        }
    }


}






