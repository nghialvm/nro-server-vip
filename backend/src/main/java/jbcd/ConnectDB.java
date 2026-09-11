package jbcd;

import Utils.Logger;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.io.FileInputStream;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.Connection;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;
import java.io.IOException;

public class ConnectDB {

    private static String DRIVER;
    private static String DB_HOST;
    private static String DB_PORT;
    public static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static int MIN_CONN;
    private static int MAX_CONN;
    private static long MAX_LIFE_TIME;
    public static boolean LOG_QUERY;
    private static HikariConfig CONFIG;
    private static HikariDataSource DS;

    static {
        loadProperties();
        CONFIG = createConfig("User Management", DB_NAME);
        DS = new HikariDataSource(CONFIG);
    }

    public static Connection getConnection() throws SQLException {
        return ConnectDB.DS.getConnection();
    }

    public static void close() {
        ConnectDB.DS.close();
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("data/config/data_base.properties"));
            Object value;

            if ((value = properties.get("database.driver")) != null) {
                ConnectDB.DRIVER = String.valueOf(value);
            }
            if ((value = properties.get("database.host")) != null) {
                ConnectDB.DB_HOST = String.valueOf(value);
            }
            if ((value = properties.get("database.port")) != null) {
                ConnectDB.DB_PORT = String.valueOf(value);
            }
            if ((value = properties.get("database.name")) != null) {
                ConnectDB.DB_NAME = String.valueOf(value);
            }
            if ((value = properties.get("database.user")) != null) {
                ConnectDB.DB_USER = String.valueOf(value);
            }
            if ((value = properties.get("database.pass")) != null) {
                ConnectDB.DB_PASSWORD = String.valueOf(value);
            }
            if ((value = properties.get("database.min")) != null) {
                ConnectDB.MIN_CONN = Integer.parseInt(String.valueOf(value));
            }
            if ((value = properties.get("database.max")) != null) {
                ConnectDB.MAX_CONN = Integer.parseInt(String.valueOf(value));
            }
            if ((value = properties.get("database.lifetime")) != null) {
                ConnectDB.MAX_LIFE_TIME = Integer.parseInt(String.valueOf(value));
            }
            if ((value = properties.get("database.log")) != null) {
                ConnectDB.LOG_QUERY = Boolean.parseBoolean(String.valueOf(value));
            }

//            Logger.log(Logger.YELLOW," _   __   ___    _  __        __ __   __ __   ___    ____        ___    ___   ____\n");
//            Logger.log(Logger.PURPLE,"| | / /  / _ |  / |/ /       / //_/  / // /  / _ |  /  _/       / _ \\  / _ \\ / __ \\\n");
//            Logger.log(Logger.BLUE,"| |/ /  / __ | /    /       / ,<    / _  /  / __ | _/ /        / ___/ / , _// /_/ /\n");
//            Logger.log(Logger.RED,"|___/  /_/ |_|/_/|_/       /_/|_|  /_//_/  /_/ |_|/___/       /_/    /_/|_| \\____/\n");
            Logger.log(Logger.GREEN, "Chạy thành công tệp properties!\n");
        } catch (final IOException | NumberFormatException ex) {
            Logger.log(Logger.RED, "Không thể load file properties!\n");
        } finally {
            properties.clear();
        }
    }

    public static CrisResultSet executeQuery(final String query) throws Exception {
        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (ConnectDB.LOG_QUERY) {
                        Logger.log(Logger.GREEN, "Thực thi thành công câu lệnh: " + ps.toString() + "\n");
                    }
                    return new ConnectResultSet(rs);
                }
            } finally {
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception ex) {
            Logger.log(Logger.RED, "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw ex;
        }
    }

    public static CrisResultSet executeQuery(final String query, final Object... objs) throws Exception {
        try (final Connection con = getConnection(); final PreparedStatement ps = con.prepareStatement(query)) {
            for (int i = 0; i < objs.length; ++i) {
                ps.setObject(i + 1, objs[i]);
            }

            if (ConnectDB.LOG_QUERY) {
                Logger.log(Logger.GREEN, "Thực thi thành công câu lệnh: " + ps.toString() + "\n");
            }

            return new ConnectResultSet(ps.executeQuery());
        } catch (final Exception ex) {
            Logger.log(Logger.RED, "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw ex;
        }
    }

    public static int executeUpdate(final String query) throws Exception {
        int rowUpdated = -1;

        try (final Connection con = getConnection(); final PreparedStatement ps = con.prepareStatement(query)) {
            if (ConnectDB.LOG_QUERY) {
                Logger.log(Logger.GREEN, "Thực thi thành công câu lệnh: " + ps.toString() + "\n");
            }
            rowUpdated = ps.executeUpdate();
        } catch (final Exception e) {
            Logger.log(Logger.RED, "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }

        return rowUpdated;
    }

    public static int executeUpdate(String query, final Object... objs) throws Exception {
        if (query.indexOf("insert") == 0 && query.lastIndexOf("()") == query.length() - 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("(");

            for (int i = 0; i < objs.length; ++i) {
                sb.append("?");
                if (i < objs.length - 1) {
                    sb.append(",");
                } else {
                    sb.append(")");
                }
            }

            query = query.replace("()", sb.toString());
        }

        try (final Connection con = getConnection(); final PreparedStatement ps = con.prepareStatement(query)) {
            for (int j = 0; j < objs.length; ++j) {
                ps.setObject(j + 1, objs[j]);
            }

            if (ConnectDB.LOG_QUERY) {
                Logger.log(Logger.GREEN, "Thực thi thành công câu lệnh: " + ps.toString() + "\n");
            }

            return ps.executeUpdate();
        } catch (final Exception ex) {
            Logger.log(Logger.RED, "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw ex;
        }
    }

    private static HikariConfig createConfig(String poolName, String databaseName) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(DRIVER);
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useUnicode=yes&characterEncoding=UTF-8",
                DB_HOST, DB_PORT, databaseName));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMinimumIdle(MIN_CONN);
        config.setMaximumPoolSize(MAX_CONN);
        config.setMaxLifetime(MAX_LIFE_TIME);
        config.setPoolName(poolName);
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000);

        config.setMaxLifetime(25 * 60 * 1000);  // 25 phút
        config.setIdleTimeout(10 * 60 * 1000);  // 10 phút
        config.setKeepaliveTime(5 * 60 * 1000); // 5 phút

        config.addDataSourceProperty("cachePrepStmts", "false");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "true");

        return config;
    }
}