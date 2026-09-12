package nro.admin;

import QuanLiBoss.BossData;
import QuanLiBoss.BossesData;
import QuanLiBoss.Manager.BossManager;
import Utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import event.EventManager;
import jbcd.ConnectDB;
import network.session.SessionManager;
import nro.giftcode.GiftCodeManager;
import nro.server.Client;
import nro.server.DropManager;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.server.ServerManager;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Small, dependency-free management API for the game server.
 *
 * The API intentionally uses an allowlist of resources and columns. The web
 * client never receives database credentials and can never submit arbitrary
 * SQL or operating-system commands.
 */
public final class AdminHttpServer {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SESSION_COOKIE = "nro_admin_session";
    private static final int MAX_BODY_BYTES = 5 * 1024 * 1024;
    private static final long SESSION_IDLE_MS = TimeUnit.MINUTES.toMillis(30);
    private static final long SESSION_MAX_MS = TimeUnit.HOURS.toMillis(8);
    private static final long LOGIN_WINDOW_MS = TimeUnit.MINUTES.toMillis(15);
    private static final int LOGIN_LIMIT = 5;
    private static final Pattern SAFE_IP = Pattern.compile("^[0-9a-fA-F:.]{2,64}$");
    private static final Pattern SAFE_KEY = Pattern.compile("^[A-Za-z0-9_-]{1,100}$");

    private static final AdminHttpServer INSTANCE = new AdminHttpServer();

    private final Map<String, ResourceDefinition> resources = new LinkedHashMap<>();
    private final ConcurrentMap<String, AdminSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LoginBucket> loginBuckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, runnable -> {
        Thread thread = new Thread(runnable, "Admin-API-Scheduler");
        thread.setDaemon(true);
        return thread;
    });
    private final ScheduledExecutorService antiDdosScheduler = Executors.newScheduledThreadPool(1, runnable -> {
        Thread thread = new Thread(runnable, "Admin-AntiDDoS");
        thread.setDaemon(true);
        return thread;
    });
    private final List<EventDefinition> eventDefinitions = Arrays.asList(
            new EventDefinition(1, "Tết Nguyên Đán"),
            new EventDefinition(2, "Trung Thu"),
            new EventDefinition(3, "Halloween"),
            new EventDefinition(4, "Giáng Sinh"),
            new EventDefinition(5, "Vu Lan Báo Hiếu"),
            new EventDefinition(6, "Quốc tế 8/3"),
            new EventDefinition(7, "Giỗ Tổ Hùng Vương"),
            new EventDefinition(8, "Black Friday"),
            new EventDefinition(9, "Valentine"),
            new EventDefinition(10, "20/10"),
            new EventDefinition(11, "Top Up")
    );
    private final ConcurrentMap<String, String> runtimeSettings = new ConcurrentHashMap<>();

    private volatile HttpServer httpServer;
    private volatile ExecutorService httpExecutor;
    private volatile AdminConfig config = AdminConfig.defaults();
    private volatile IconAssetResolver iconAssets = new IconAssetResolver(config.iconRoot);
    private volatile boolean antiDdosRunning;
    private volatile boolean antiDdosAutoScan;
    private volatile boolean antiDdosLockdown;
    private volatile int antiDdosLimit = 10;
    private volatile int antiDdosScanSeconds = 60;
    private volatile ScheduledFuture<?> antiDdosTask;

    private AdminHttpServer() {
        registerResources();
    }

    public static AdminHttpServer gI() {
        return INSTANCE;
    }

    public synchronized void start() {
        if (httpServer != null) {
            return;
        }

        config = AdminConfig.load();
        iconAssets = new IconAssetResolver(config.iconRoot);
        try {
            ensureAdminTables();
            applyBossOverrides();
        } catch (Exception exception) {
            Logger.warn("ADMIN_API", "Không thể khởi tạo migration/override: " + exception.getMessage());
        }

        try {
            httpServer = HttpServer.create(new InetSocketAddress(config.bind, config.port), 0);
            httpServer.createContext("/api", new ApiHandler());
            httpExecutor = Executors.newFixedThreadPool(config.workerThreads, runnable -> {
                Thread thread = new Thread(runnable, "Admin-API-Worker");
                thread.setDaemon(true);
                return thread;
            });
            httpServer.setExecutor(httpExecutor);
            httpServer.start();
            scheduler.scheduleAtFixedRate(this::cleanExpiredSessions, 5, 5, TimeUnit.MINUTES);
            Logger.success("ADMIN_API", "Admin API đang chạy tại http://" + config.bind + ":" + config.port);
        } catch (Exception exception) {
            httpServer = null;
            if (httpExecutor != null) {
                httpExecutor.shutdownNow();
                httpExecutor = null;
            }
            Logger.err("ADMIN_API", "Không thể khởi động Admin API: " + exception.getMessage());
        }
    }

    public synchronized void stop() {
        stopAntiDdos();
        if (httpServer != null) {
            httpServer.stop(1);
            httpServer = null;
        }
        if (httpExecutor != null) {
            httpExecutor.shutdownNow();
            httpExecutor = null;
        }
        sessions.clear();
    }

    /**
     * Applies the event selection persisted by the web console after the
     * normal game data and event managers have been initialized.
     */
    public void applyStoredEvents() {
        Set<Integer> active = readActiveEventIds();
        if (active.isEmpty()) {
            return;
        }
        try {
            EventManager.gI().setCurrentEvent(active.iterator().next());
        } catch (Exception exception) {
            Logger.warn("ADMIN_EVENT", "Không thể áp dụng event đã lưu: " + exception.getMessage());
        }
    }

    private final class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            String path = normalizePath(exchange.getRequestURI().getPath());
            try {
                if ("/health".equals(path)) {
                    handleHealth(exchange);
                    return;
                }
                if ("/assets/icons".equals(path) || path.startsWith("/assets/icons/")) {
                    handleIcon(exchange, path);
                    return;
                }
                if ("/auth/login".equals(path)) {
                    handleLogin(exchange);
                    return;
                }

                boolean readOnly = isReadOnlyMethod(exchange.getRequestMethod());
                AdminSession session = requireSession(exchange, !readOnly);

                if (path.startsWith("/auth/")) {
                    handleAuth(exchange, path, session);
                } else if ("/dashboard".equals(path)) {
                    requireMethod(exchange, "GET");
                    respondSuccess(exchange, 200, dashboardSnapshot(), null);
                } else if ("/dashboard/stream".equals(path)) {
                    handleDashboardStream(exchange, session);
                } else if (path.startsWith("/lookups/")) {
                    handleLookup(exchange, path);
                } else if ((path.startsWith("/shops/") && path.endsWith("/detail"))
                        || (path.startsWith("/shop-tabs/") && path.endsWith("/detail"))) {
                    handleShopDetail(exchange, path, session);
                } else if ("/giftcodes/detail".equals(path)
                        || (path.startsWith("/giftcodes/") && (path.endsWith("/detail")
                        || path.endsWith("/used-players/reset")))) {
                    handleGiftcodeDetail(exchange, path, session);
                } else if (path.startsWith("/resources/")) {
                    handleResource(exchange, path, session);
                } else if (path.startsWith("/server/")) {
                    handleServer(exchange, path, session);
                } else if (path.startsWith("/events")) {
                    handleEvents(exchange, path, session);
                } else if (path.startsWith("/boss-config")) {
                    handleBossConfig(exchange, path, session);
                } else if ("/bosses/actions".equals(path)) {
                    handleBossAction(exchange, session);
                } else if (path.startsWith("/security")) {
                    handleSecurity(exchange, path, session);
                } else if (path.startsWith("/anti-ddos")) {
                    handleAntiDdos(exchange, path, session);
                } else if (path.startsWith("/players/") && path.split("/").length >= 4) {
                    handlePlayerAction(exchange, path, session);
                } else if (path.startsWith("/accounts") || path.startsWith("/players")
                    || path.startsWith("/giftcodes") || path.startsWith("/shops")
                        || path.startsWith("/shop-tabs") || path.startsWith("/shop-items") || path.startsWith("/shop-options")
                        || path.startsWith("/topup-rewards") || path.startsWith("/badges")
                        || path.startsWith("/maps") || path.startsWith("/items/templates")
                        || path.startsWith("/transactions") || path.startsWith("/radar")
                        || path.startsWith("/parts") || path.startsWith("/head-avatars")
                        || path.startsWith("/head-frames") || path.startsWith("/drops")) {
                    handleResource(exchange, aliasResourcePath(path), session);
                } else {
                    throw ApiException.notFound("ROUTE_NOT_FOUND", "Không tìm thấy endpoint");
                }
            } catch (ApiException exception) {
                respondError(exchange, exception.status, exception.code, exception.getMessage(), null);
            } catch (SQLException exception) {
                Logger.warn("ADMIN_API", "Database error: " + exception.getMessage());
                respondError(exchange, 503, "DATABASE_UNAVAILABLE", "Không thể truy cập database", null);
            } catch (Exception exception) {
                Logger.logException(AdminHttpServer.class, exception);
                respondError(exchange, 500, "INTERNAL_ERROR", "Có lỗi không xác định trên server", null);
            } finally {
                exchange.close();
            }
        }
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET");
        JsonObject data = new JsonObject();
        data.addProperty("status", "ok");
        data.addProperty("api", httpServer != null);
        data.addProperty("gameRunning", ServerManager.isRunning);
        data.addProperty("time", Instant.now().toString());
        respondSuccess(exchange, 200, data, null);
    }

    private void handleLogin(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        String remoteIp = clientIp(exchange);
        LoginBucket bucket = loginBuckets.computeIfAbsent(remoteIp, ignored -> new LoginBucket());
        if (bucket.isLimited()) {
            throw new ApiException(429, "LOGIN_RATE_LIMITED", "Quá nhiều lần đăng nhập thất bại");
        }

        JsonObject body = readJsonObject(exchange);
        String username = requiredText(body, "username", 100);
        String password = requiredText(body, "password", 200);
        AccountPrincipal account = findAdminAccount(username, password);
        if (account == null) {
            bucket.recordFailure();
            auditLoginFailure(username, remoteIp);
            throw new ApiException(401, "INVALID_CREDENTIALS", "Tài khoản hoặc mật khẩu không đúng");
        }
        bucket.clear();

        byte[] sessionBytes = new byte[32];
        byte[] csrfBytes = new byte[32];
        RANDOM.nextBytes(sessionBytes);
        RANDOM.nextBytes(csrfBytes);
        String sessionId = Base64.getUrlEncoder().withoutPadding().encodeToString(sessionBytes);
        String csrf = Base64.getUrlEncoder().withoutPadding().encodeToString(csrfBytes);
        AdminSession session = new AdminSession(sessionId, csrf, account.id, account.username, remoteIp);
        sessions.put(sessionId, session);
        audit(session, "LOGIN", "auth", String.valueOf(account.id), null);

        JsonObject data = new JsonObject();
        data.add("user", account.toJson());
        data.addProperty("csrfToken", csrf);
        data.addProperty("expiresInSeconds", TimeUnit.MILLISECONDS.toSeconds(SESSION_MAX_MS));
        exchange.getResponseHeaders().add("Set-Cookie", buildCookie(sessionId));
        respondSuccess(exchange, 200, data, null);
    }

    private AccountPrincipal findAdminAccount(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, is_admin, ban FROM account WHERE username = ? LIMIT 1";
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                String storedPassword = resultSet.getString("password");
                boolean passwordMatches = storedPassword != null
                        && MessageDigest.isEqual(storedPassword.getBytes(StandardCharsets.UTF_8), password.getBytes(StandardCharsets.UTF_8));
                if (!passwordMatches || resultSet.getInt("is_admin") <= 0 || resultSet.getInt("ban") != 0) {
                    return null;
                }
                return new AccountPrincipal(resultSet.getInt("id"), resultSet.getString("username"));
            }
        }
    }

    private void handleAuth(HttpExchange exchange, String path, AdminSession session) throws IOException {
        if ("/auth/me".equals(path)) {
            requireMethod(exchange, "GET");
            JsonObject data = new JsonObject();
            JsonObject user = new JsonObject();
            user.addProperty("id", session.accountId);
            user.addProperty("username", session.username);
            user.addProperty("isAdmin", true);
            data.add("user", user);
            data.addProperty("csrfToken", session.csrfToken);
            respondSuccess(exchange, 200, data, null);
            return;
        }
        if ("/auth/logout".equals(path)) {
            requireMethod(exchange, "POST");
            audit(session, "LOGOUT", "auth", String.valueOf(session.accountId), null);
            sessions.remove(session.id);
            exchange.getResponseHeaders().add("Set-Cookie", buildExpiredCookie());
            respondSuccess(exchange, 200, messageData("Đã đăng xuất"), null);
            return;
        }
        throw ApiException.notFound("AUTH_ROUTE_NOT_FOUND", "Không tìm thấy endpoint xác thực");
    }

    private void handleShopDetail(HttpExchange exchange, String path, AdminSession session) throws Exception {
        List<String> pathSegments = segments(path);
        if (pathSegments.size() == 3 && "shops".equals(pathSegments.get(0))
                && "detail".equals(pathSegments.get(2))) {
            requireMethod(exchange, "GET");
            int shopId = pathInt(pathSegments.get(1), "shopId");
            JsonObject detail = loadShopDetail(shopId, null, false);
            respondSuccess(exchange, 200, detail, objectOf("resource", "shops", "shopId", shopId));
            return;
        }
        if (pathSegments.size() == 3 && "shop-tabs".equals(pathSegments.get(0))
                && "detail".equals(pathSegments.get(2))) {
            int tabId = pathInt(pathSegments.get(1), "tabId");
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonObject detail = loadShopTabDetail(tabId, false);
                respondSuccess(exchange, 200, detail, objectOf("resource", "shop-tabs", "tabId", tabId));
                return;
            }
            if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonObject envelope = readJsonObject(exchange);
                JsonObject detail = updateShopTabDetail(tabId, envelope, session);
                respondSuccess(exchange, 200, detail, objectOf("resource", "shop-tabs", "tabId", tabId));
                return;
            }
        }
        throw ApiException.methodNotAllowed("METHOD_NOT_ALLOWED", "Phương thức detail shop không được hỗ trợ");
    }

    private JsonObject loadShopDetail(int shopId, Integer onlyTabId, boolean forUpdate) throws SQLException {
        try (Connection connection = ConnectDB.getConnection()) {
            return loadShopDetail(connection, shopId, onlyTabId, forUpdate);
        }
    }

    private JsonObject loadShopTabDetail(int tabId, boolean forUpdate) throws SQLException {
        try (Connection connection = ConnectDB.getConnection()) {
            int shopId;
            String sql = "SELECT shop_id FROM tab_shop WHERE id=?" + (forUpdate ? " FOR UPDATE" : "");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, tabId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw ApiException.notFound("TAB_NOT_FOUND", "Không tìm thấy tab shop");
                    }
                    shopId = resultSet.getInt("shop_id");
                }
            }
            return loadShopDetail(connection, shopId, tabId, forUpdate);
        }
    }

    private JsonObject loadShopDetail(Connection connection, int shopId, Integer onlyTabId,
                                      boolean forUpdate) throws SQLException {
        JsonObject rawShop;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, npc_id, tag_name, type_shop FROM shop WHERE id=?"
                        + (forUpdate ? " FOR UPDATE" : ""))) {
            statement.setInt(1, shopId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw ApiException.notFound("SHOP_NOT_FOUND", "Không tìm thấy shop");
                }
                rawShop = jsonRow(resultSet, "id", "npc_id", "tag_name", "type_shop");
            }
        }

        List<JsonObject> rawTabs = new ArrayList<>();
        String tabSql = "SELECT id, shop_id, NAME FROM tab_shop WHERE shop_id=?"
                + (onlyTabId == null ? "" : " AND id=?") + " ORDER BY id"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement statement = connection.prepareStatement(tabSql)) {
            statement.setInt(1, shopId);
            if (onlyTabId != null) statement.setInt(2, onlyTabId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rawTabs.add(jsonRow(resultSet, "id", "shop_id", "NAME"));
                }
            }
        }
        if (onlyTabId != null && rawTabs.isEmpty()) {
            throw ApiException.notFound("TAB_NOT_FOUND", "Tab không thuộc shop hoặc không tồn tại");
        }

        Map<Integer, JsonArray> rawItemsByTab = new LinkedHashMap<>();
        Map<Integer, JsonObject> rawItemById = new LinkedHashMap<>();
        if (!rawTabs.isEmpty()) {
            String itemSql = "SELECT id, tab_id, temp_id, is_new, is_sell, type_sell, cost, costgold, icon_spec, create_time "
                    + "FROM item_shop WHERE tab_id IN (" + placeholders(rawTabs.size()) + ") ORDER BY tab_id, id"
                    + (forUpdate ? " FOR UPDATE" : "");
            try (PreparedStatement statement = connection.prepareStatement(itemSql)) {
                int index = 1;
                for (JsonObject tab : rawTabs) statement.setInt(index++, tab.get("id").getAsInt());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JsonObject item = jsonRow(resultSet, "id", "tab_id", "temp_id", "is_new", "is_sell",
                                "type_sell", "cost", "costgold", "icon_spec", "create_time");
                        int tabId = item.get("tab_id").getAsInt();
                        int itemId = item.get("id").getAsInt();
                        rawItemsByTab.computeIfAbsent(tabId, ignored -> new JsonArray()).add(item);
                        rawItemById.put(itemId, item);
                    }
                }
            }
        }

        Map<Integer, JsonArray> rawOptionsByItem = new LinkedHashMap<>();
        Map<Integer, JsonObject> rawOptionById = new LinkedHashMap<>();
        if (!rawItemById.isEmpty()) {
            String optionSql = "SELECT id, item_shop_id, option_id, param FROM item_shop_option WHERE item_shop_id IN ("
                    + placeholders(rawItemById.size()) + ") ORDER BY item_shop_id, id"
                    + (forUpdate ? " FOR UPDATE" : "");
            try (PreparedStatement statement = connection.prepareStatement(optionSql)) {
                int index = 1;
                for (Integer itemId : rawItemById.keySet()) statement.setInt(index++, itemId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JsonObject option = jsonRow(resultSet, "id", "item_shop_id", "option_id", "param");
                        int itemId = option.get("item_shop_id").getAsInt();
                        int optionId = option.get("id").getAsInt();
                        rawOptionsByItem.computeIfAbsent(itemId, ignored -> new JsonArray()).add(option);
                        rawOptionById.put(optionId, option);
                    }
                }
            }
        }

        Set<Integer> itemTemplateIds = new LinkedHashSet<>();
        Set<Integer> optionTemplateIds = new LinkedHashSet<>();
        for (JsonObject item : rawItemById.values()) itemTemplateIds.add(item.get("temp_id").getAsInt());
        for (JsonObject option : rawOptionById.values()) optionTemplateIds.add(option.get("option_id").getAsInt());
        Map<Integer, JsonObject> itemTemplates = loadLookupRows(connection, "items", itemTemplateIds);
        Map<Integer, JsonObject> optionTemplates = loadLookupRows(connection, "options", optionTemplateIds);
        Map<Integer, JsonObject> npcTemplates = loadLookupRows(connection,
                "npcs", Set.of(rawShop.get("npc_id").getAsInt()));

        JsonObject shop = rawShop.deepCopy();
        JsonObject npcTemplate = npcTemplates.get(rawShop.get("npc_id").getAsInt());
        shop.add("npc", npcTemplate == null ? JsonNull.INSTANCE : npcTemplate.deepCopy());
        shop.addProperty("version", rowVersion(rawShop));

        JsonArray tabs = new JsonArray();
        for (JsonObject rawTab : rawTabs) {
            int tabId = rawTab.get("id").getAsInt();
            JsonArray items = new JsonArray();
            JsonArray rawItems = rawItemsByTab.getOrDefault(tabId, new JsonArray());
            JsonArray versionItems = new JsonArray();
            for (JsonElement rawItemElement : rawItems) {
                JsonObject rawItem = rawItemElement.getAsJsonObject();
                int itemId = rawItem.get("id").getAsInt();
                JsonArray rawOptions = rawOptionsByItem.getOrDefault(itemId, new JsonArray());
                JsonArray options = new JsonArray();
                for (JsonElement rawOptionElement : rawOptions) {
                    JsonObject rawOption = rawOptionElement.getAsJsonObject();
                    JsonObject option = rawOption.deepCopy();
                    JsonObject optionTemplate = optionTemplates.get(rawOption.get("option_id").getAsInt());
                    option.add("optionTemplate", optionTemplate == null ? JsonNull.INSTANCE : optionTemplate.deepCopy());
                    options.add(option);
                }
                JsonObject item = rawItem.deepCopy();
                JsonObject itemTemplate = itemTemplates.get(rawItem.get("temp_id").getAsInt());
                item.add("itemTemplate", itemTemplate == null ? JsonNull.INSTANCE : itemTemplate.deepCopy());
                item.add("options", options);
                JsonObject versionItem = rawItem.deepCopy();
                versionItem.add("options", rawOptions.deepCopy());
                item.addProperty("version", rowVersion(versionItem));
                items.add(item);
                versionItems.add(versionItem);
            }
            JsonObject tabVersionSource = rawTab.deepCopy();
            tabVersionSource.add("items", versionItems);
            String tabVersion = rowVersion(tabVersionSource);
            JsonObject tab = rawTab.deepCopy();
            tab.add("items", items);
            tab.addProperty("version", tabVersion);
            tabs.add(tab);
        }

        JsonObject result = new JsonObject();
        result.add("shop", shop);
        result.add("tabs", tabs);
        result.addProperty("version", sha256(GSON.toJson(tabs)));
        return result;
    }

    private JsonObject updateShopTabDetail(int tabId, JsonObject envelope, AdminSession session) throws SQLException {
        JsonObject body = requestData(envelope);
        validateAllowedFields(body, Set.of("id", "shop_id", "NAME", "items", "version"), "shop tab");
        if (body.has("id") && requiredJsonInt(body, "id", 1, Integer.MAX_VALUE) != tabId) {
            throw ApiException.badRequest("PRIMARY_KEY_MISMATCH", "ID shop tab không khớp URL");
        }
        String expectedVersion = textValue(envelope.get("version"));
        if (expectedVersion == null || expectedVersion.isBlank()) {
            throw ApiException.badRequest("VERSION_REQUIRED", "Thiếu version của shop tab");
        }
        JsonElement itemsElement = body.get("items");
        if (itemsElement == null || !itemsElement.isJsonArray()) {
            throw ApiException.badRequest("ITEMS_REQUIRED", "Danh sách item shop phải là mảng");
        }
        JsonArray incomingItems = itemsElement.getAsJsonArray();
        if (incomingItems.size() > 200) {
            throw ApiException.badRequest("TOO_MANY_SHOP_ITEMS", "Một tab tối đa 200 item");
        }

        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int shopId;
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT shop_id FROM tab_shop WHERE id=? FOR UPDATE")) {
                    statement.setInt(1, tabId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) throw ApiException.notFound("TAB_NOT_FOUND", "Không tìm thấy tab shop");
                        shopId = resultSet.getInt("shop_id");
                    }
                }
                JsonObject currentDetail = loadShopDetail(connection, shopId, tabId, true);
                JsonArray currentTabs = currentDetail.getAsJsonArray("tabs");
                JsonObject currentTab = currentTabs.get(0).getAsJsonObject();
                String actualVersion = currentTab.get("version").getAsString();
                if (!expectedVersion.equals(actualVersion)) {
                    throw new ApiException(409, "VERSION_CONFLICT", "Shop tab đã được thay đổi, hãy tải lại");
                }

                int newShopId = body.has("shop_id") ? requiredJsonInt(body, "shop_id", 0, Integer.MAX_VALUE) : shopId;
                String tabName = body.has("NAME") ? requiredJsonText(body, "NAME", 50)
                        : currentTab.get("NAME").getAsString();
                ensureIdsExist(connection, "shop", "id", Set.of(newShopId), "SHOP_NOT_FOUND", "Shop không tồn tại");

                Map<Integer, JsonObject> currentItems = new LinkedHashMap<>();
                for (JsonElement element : currentTab.getAsJsonArray("items")) {
                    JsonObject item = element.getAsJsonObject();
                    validateAllowedFields(item, Set.of("id", "temp_id", "is_new", "is_sell", "type_sell",
                            "cost", "costgold", "icon_spec", "options"), "shop item");
                    currentItems.put(item.get("id").getAsInt(), item);
                }
                Set<Integer> seenItems = new LinkedHashSet<>();
                List<JsonObject> normalizedItems = new ArrayList<>();
                List<JsonObject> auditOperations = new ArrayList<>();
                Set<Integer> itemTemplateIds = new LinkedHashSet<>();
                Set<Integer> optionTemplateIds = new LinkedHashSet<>();
                for (int index = 0; index < incomingItems.size(); index++) {
                    JsonElement element = incomingItems.get(index);
                    if (!element.isJsonObject()) {
                        throw ApiException.badRequest("INVALID_SHOP_ITEM", "Item thứ " + (index + 1) + " phải là object");
                    }
                    JsonObject item = element.getAsJsonObject();
                    Integer existingId = optionalJsonInt(item, "id", 1, Integer.MAX_VALUE);
                    if (existingId != null) {
                        if (!currentItems.containsKey(existingId) || !seenItems.add(existingId)) {
                            throw ApiException.badRequest("INVALID_SHOP_ITEM_ID", "Item shop không thuộc tab hoặc bị lặp: " + existingId);
                        }
                    }
                    JsonObject normalized = new JsonObject();
                    if (existingId != null) normalized.addProperty("id", existingId);
                    int tempId = requiredJsonInt(item, "temp_id", 0, IconAssetResolver.MAX_ICON_ID);
                    normalized.addProperty("temp_id", tempId);
                    normalized.addProperty("is_new", requiredJsonBoolean(item, "is_new"));
                    normalized.addProperty("is_sell", requiredJsonBoolean(item, "is_sell"));
                    normalized.addProperty("type_sell", requiredJsonInt(item, "type_sell", 0, 3));
                    normalized.addProperty("cost", requiredJsonInt(item, "cost", 0, Integer.MAX_VALUE));
                    normalized.addProperty("costgold", requiredJsonInt(item, "costgold", 0, Integer.MAX_VALUE));
                    normalized.addProperty("icon_spec", requiredJsonInt(item, "icon_spec", -1, IconAssetResolver.MAX_ICON_ID));
                    JsonElement optionsElement = item.get("options");
                    if (optionsElement == null || !optionsElement.isJsonArray()) {
                        throw ApiException.badRequest("OPTIONS_REQUIRED", "Options của item phải là mảng");
                    }
                    if (optionsElement.getAsJsonArray().size() > 100) {
                        throw ApiException.badRequest("TOO_MANY_SHOP_OPTIONS", "Một item tối đa 100 option");
                    }
                    JsonArray normalizedOptions = new JsonArray();
                    Set<Integer> currentOptionIds = new LinkedHashSet<>();
                    if (existingId != null) {
                        for (JsonElement option : currentItems.get(existingId).getAsJsonArray("options")) {
                            currentOptionIds.add(option.getAsJsonObject().get("id").getAsInt());
                        }
                    }
                    Set<Integer> seenOptions = new LinkedHashSet<>();
                    for (int optionIndex = 0; optionIndex < optionsElement.getAsJsonArray().size(); optionIndex++) {
                        JsonElement optionElement = optionsElement.getAsJsonArray().get(optionIndex);
                        if (!optionElement.isJsonObject()) {
                            throw ApiException.badRequest("INVALID_SHOP_OPTION", "Option thứ " + (optionIndex + 1) + " phải là object");
                        }
                        JsonObject option = optionElement.getAsJsonObject();
                        validateAllowedFields(option, Set.of("id", "option_id", "param"), "shop option");
                        Integer optionRowId = optionalJsonInt(option, "id", 1, Integer.MAX_VALUE);
                        if (optionRowId != null && (!currentOptionIds.contains(optionRowId) || !seenOptions.add(optionRowId))) {
                            throw ApiException.badRequest("INVALID_SHOP_OPTION_ID", "Option shop không thuộc item hoặc bị lặp: " + optionRowId);
                        }
                        JsonObject normalizedOption = new JsonObject();
                        if (optionRowId != null) normalizedOption.addProperty("id", optionRowId);
                        int optionId = requiredJsonInt(option, "option_id", 0, IconAssetResolver.MAX_ICON_ID);
                        normalizedOption.addProperty("option_id", optionId);
                        normalizedOption.addProperty("param", requiredJsonInt(option, "param", Integer.MIN_VALUE, Integer.MAX_VALUE));
                        normalizedOptions.add(normalizedOption);
                        optionTemplateIds.add(optionId);
                    }
                    normalized.add("options", normalizedOptions);
                    normalizedItems.add(normalized);
                    itemTemplateIds.add(tempId);
                }
                ensureIdsExist(connection, "item_template", "id", itemTemplateIds,
                        "ITEM_NOT_FOUND", "Item template không tồn tại");
                ensureIdsExist(connection, "item_option_template", "id", optionTemplateIds,
                        "OPTION_NOT_FOUND", "Option template không tồn tại");

                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE tab_shop SET shop_id=?, NAME=? WHERE id=?")) {
                    statement.setInt(1, newShopId);
                    statement.setString(2, tabName);
                    statement.setInt(3, tabId);
                    statement.executeUpdate();
                }

                for (JsonObject item : normalizedItems) {
                    int itemShopId;
                        if (item.has("id")) {
                        itemShopId = item.get("id").getAsInt();
                        try (PreparedStatement statement = connection.prepareStatement(
                                "UPDATE item_shop SET temp_id=?, is_new=?, is_sell=?, type_sell=?, cost=?, costgold=?, icon_spec=? WHERE id=? AND tab_id=?")) {
                            statement.setInt(1, item.get("temp_id").getAsInt());
                            statement.setBoolean(2, item.get("is_new").getAsBoolean());
                            statement.setBoolean(3, item.get("is_sell").getAsBoolean());
                            statement.setInt(4, item.get("type_sell").getAsInt());
                            statement.setInt(5, item.get("cost").getAsInt());
                            statement.setInt(6, item.get("costgold").getAsInt());
                            statement.setInt(7, item.get("icon_spec").getAsInt());
                            statement.setInt(8, itemShopId);
                            statement.setInt(9, tabId);
                            if (statement.executeUpdate() == 0) {
                                throw ApiException.badRequest("INVALID_SHOP_ITEM_ID", "Item shop không thuộc tab");
                            }
                        }
                        auditOperations.add(objectOf("action", "UPDATE", "resource", "shop-items", "id", itemShopId,
                                "tempId", item.get("temp_id")));
                    } else {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "INSERT INTO item_shop (tab_id, temp_id, is_new, is_sell, type_sell, cost, costgold, icon_spec) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            statement.setInt(1, tabId);
                            statement.setInt(2, item.get("temp_id").getAsInt());
                            statement.setBoolean(3, item.get("is_new").getAsBoolean());
                            statement.setBoolean(4, item.get("is_sell").getAsBoolean());
                            statement.setInt(5, item.get("type_sell").getAsInt());
                            statement.setInt(6, item.get("cost").getAsInt());
                            statement.setInt(7, item.get("costgold").getAsInt());
                            statement.setInt(8, item.get("icon_spec").getAsInt());
                            statement.executeUpdate();
                            try (ResultSet keys = statement.getGeneratedKeys()) {
                                if (!keys.next()) throw new SQLException("Không lấy được id item shop mới");
                                itemShopId = keys.getInt(1);
                            }
                        }
                        auditOperations.add(objectOf("action", "INSERT", "resource", "shop-items", "id", itemShopId,
                                "tempId", item.get("temp_id")));
                    }
                    syncShopOptions(connection, itemShopId, item.getAsJsonArray("options"), auditOperations);
                }

                for (Integer currentItemId : currentItems.keySet()) {
                    if (seenItems.contains(currentItemId)) continue;
                    for (JsonElement oldOption : currentItems.get(currentItemId).getAsJsonArray("options")) {
                        JsonObject option = oldOption.getAsJsonObject();
                        auditOperations.add(objectOf("action", "DELETE", "resource", "shop-options",
                                "id", option.get("id"), "itemShopId", currentItemId));
                    }
                    try (PreparedStatement deleteOptions = connection.prepareStatement(
                            "DELETE FROM item_shop_option WHERE item_shop_id=?")) {
                        deleteOptions.setInt(1, currentItemId);
                        deleteOptions.executeUpdate();
                    }
                    try (PreparedStatement deleteItem = connection.prepareStatement(
                            "DELETE FROM item_shop WHERE id=? AND tab_id=?")) {
                        deleteItem.setInt(1, currentItemId);
                        deleteItem.setInt(2, tabId);
                        deleteItem.executeUpdate();
                    }
                    auditOperations.add(objectOf("action", "DELETE", "resource", "shop-items", "id", currentItemId));
                }
                connection.commit();
                JsonArray auditJson = new JsonArray();
                for (JsonObject operation : auditOperations) auditJson.add(operation);
                audit(session, "UPDATE_DETAIL", "shop-tabs", String.valueOf(tabId),
                        objectOf("items", normalizedItems.size(), "shopId", newShopId, "operations", auditJson));
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
        return loadShopTabDetail(tabId, false);
    }

    private void syncShopOptions(Connection connection, int itemShopId, JsonArray incomingOptions,
                                 List<JsonObject> auditOperations) throws SQLException {
        Set<Integer> existingIds = new LinkedHashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM item_shop_option WHERE item_shop_id=?")) {
            statement.setInt(1, itemShopId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) existingIds.add(resultSet.getInt(1));
            }
        }
        Set<Integer> seenIds = new LinkedHashSet<>();
        for (JsonElement element : incomingOptions) {
            JsonObject option = element.getAsJsonObject();
            if (option.has("id")) {
                int id = option.get("id").getAsInt();
                seenIds.add(id);
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE item_shop_option SET option_id=?, param=? WHERE id=? AND item_shop_id=?")) {
                    statement.setInt(1, option.get("option_id").getAsInt());
                    statement.setInt(2, option.get("param").getAsInt());
                    statement.setInt(3, id);
                    statement.setInt(4, itemShopId);
                    if (statement.executeUpdate() == 0) throw ApiException.badRequest("INVALID_SHOP_OPTION_ID", "Option shop không thuộc item");
                }
                auditOperations.add(objectOf("action", "UPDATE", "resource", "shop-options", "id", id,
                        "itemShopId", itemShopId, "optionId", option.get("option_id")));
            } else {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO item_shop_option (item_shop_id, option_id, param) VALUES (?, ?, ?)")) {
                    statement.setInt(1, itemShopId);
                    statement.setInt(2, option.get("option_id").getAsInt());
                    statement.setInt(3, option.get("param").getAsInt());
                    statement.executeUpdate();
                }
                auditOperations.add(objectOf("action", "INSERT", "resource", "shop-options", "itemShopId", itemShopId,
                        "optionId", option.get("option_id")));
            }
        }
        for (Integer id : existingIds) {
            if (!seenIds.contains(id)) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM item_shop_option WHERE id=? AND item_shop_id=?")) {
                    statement.setInt(1, id);
                    statement.setInt(2, itemShopId);
                    statement.executeUpdate();
                }
                auditOperations.add(objectOf("action", "DELETE", "resource", "shop-options", "id", id,
                        "itemShopId", itemShopId));
            }
        }
    }

    private Map<Integer, JsonObject> loadLookupRows(Connection connection, String kind, Set<Integer> ids) throws SQLException {
        Map<Integer, JsonObject> result = new LinkedHashMap<>();
        if (ids == null || ids.isEmpty()) return result;
        LookupDefinition lookup = lookupDefinition(kind);
        if (lookup == null || !"id".equals(lookup.idColumn)) return result;
        String sql = "SELECT " + String.join(", ", lookup.columns) + " FROM " + lookup.table
                + " WHERE " + lookup.idColumn + " IN (" + placeholders(ids.size()) + ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Integer id : ids) statement.setInt(index++, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JsonObject row = new JsonObject();
                    for (String column : lookup.columns) row.add(column, jsonValue(resultSet.getObject(column)));
                    JsonObject normalized = normalizeLookupRow(kind, lookup, row);
                    result.put(normalized.get("id").getAsInt(), normalized);
                }
            }
        }
        return result;
    }

    private static JsonObject jsonRow(ResultSet resultSet, String... columns) throws SQLException {
        JsonObject row = new JsonObject();
        for (String column : columns) row.add(column, jsonValue(resultSet.getObject(column)));
        return row;
    }

    private static int pathInt(String value, String field) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 0) throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException exception) {
            throw ApiException.badRequest("INVALID_" + field.toUpperCase(Locale.ROOT), field + " không hợp lệ");
        }
    }

    private static String requiredJsonText(JsonObject object, String key, int maxLength) {
        JsonElement value = object.get(key);
        String text = value == null || value.isJsonNull() || !value.isJsonPrimitive() ? "" : value.getAsString().trim();
        if (text.isEmpty() || text.length() > maxLength) {
            throw ApiException.badRequest("INVALID_" + key.toUpperCase(Locale.ROOT), "Trường " + key + " không hợp lệ");
        }
        return text;
    }

    private static int requiredJsonInt(JsonObject object, String key, int min, int max) {
        JsonElement value = object.get(key);
        if (value == null || value.isJsonNull()) {
            throw ApiException.badRequest("INVALID_" + key.toUpperCase(Locale.ROOT), "Thiếu trường " + key);
        }
        return jsonInt(value, key, min, max);
    }

    private static Integer optionalJsonInt(JsonObject object, String key, int min, int max) {
        JsonElement value = object.get(key);
        if (value == null || value.isJsonNull()) return null;
        return jsonInt(value, key, min, max);
    }

    private static int jsonInt(JsonElement value, String key, int min, int max) {
        try {
            if (!value.isJsonPrimitive()) throw new NumberFormatException();
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            String text = primitive.getAsString().trim();
            if (!text.matches("-?\\d+")) throw new NumberFormatException();
            long parsed = Long.parseLong(text);
            if (parsed < min || parsed > max) throw new NumberFormatException();
            return (int) parsed;
        } catch (Exception exception) {
            throw ApiException.badRequest("INVALID_" + key.toUpperCase(Locale.ROOT), "Giá trị " + key + " không hợp lệ");
        }
    }

    private static boolean requiredJsonBoolean(JsonObject object, String key) {
        JsonElement value = object.get(key);
        if (value == null || value.isJsonNull() || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isBoolean()) {
            throw ApiException.badRequest("INVALID_" + key.toUpperCase(Locale.ROOT), "Giá trị " + key + " phải là boolean");
        }
        return value.getAsBoolean();
    }

    private static void ensureIdsExist(Connection connection, String table, String idColumn, Set<Integer> ids,
                                       String code, String message) throws SQLException {
        if (ids == null || ids.isEmpty()) return;
        if (!Set.of("shop", "item_template", "item_option_template").contains(table)
                || !"id".equals(idColumn)) {
            throw new IllegalArgumentException("Lookup table không được phép");
        }
        String sql = "SELECT id FROM " + table + " WHERE " + idColumn + " IN (" + placeholders(ids.size()) + ")";
        Set<Integer> found = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Integer id : ids) statement.setInt(index++, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) found.add(resultSet.getInt(1));
            }
        }
        if (found.size() != ids.size()) {
            Set<Integer> missing = new LinkedHashSet<>(ids);
            missing.removeAll(found);
            throw ApiException.badRequest(code, message + ": " + missing);
        }
    }

    private void handleGiftcodeDetail(HttpExchange exchange, String path, AdminSession session) throws Exception {
        List<String> pathSegments = segments(path);
        if (pathSegments.size() == 2 && "giftcodes".equals(pathSegments.get(0))
                && "detail".equals(pathSegments.get(1))) {
            requireMethod(exchange, "POST");
            JsonObject created = saveGiftcodeDetail(null, readJsonObject(exchange), session);
            respondSuccess(exchange, 201, created, objectOf("resource", "giftcodes"));
            return;
        }
        if (pathSegments.size() == 3 && "giftcodes".equals(pathSegments.get(0))
                && "detail".equals(pathSegments.get(2))) {
            int giftcodeId = pathInt(pathSegments.get(1), "giftcodeId");
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                respondSuccess(exchange, 200, loadGiftcodeDetail(giftcodeId),
                        objectOf("resource", "giftcodes", "giftcodeId", giftcodeId));
                return;
            }
            if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonObject updated = saveGiftcodeDetail(giftcodeId, readJsonObject(exchange), session);
                respondSuccess(exchange, 200, updated,
                        objectOf("resource", "giftcodes", "giftcodeId", giftcodeId));
                return;
            }
        }
        if (pathSegments.size() == 4 && "giftcodes".equals(pathSegments.get(0))
                && "used-players".equals(pathSegments.get(2))
                && "reset".equals(pathSegments.get(3))) {
            requireMethod(exchange, "POST");
            int giftcodeId = pathInt(pathSegments.get(1), "giftcodeId");
            JsonObject result = resetGiftcodeUsedPlayers(giftcodeId, readJsonObject(exchange), session);
            respondSuccess(exchange, 200, result,
                    objectOf("resource", "giftcodes", "giftcodeId", giftcodeId));
            return;
        }
        throw ApiException.methodNotAllowed("METHOD_NOT_ALLOWED", "Phương thức detail giftcode không được hỗ trợ");
    }

    private JsonObject loadGiftcodeDetail(int giftcodeId) throws SQLException {
        try (Connection connection = ConnectDB.getConnection()) {
            return loadGiftcodeDetail(connection, giftcodeId, false);
        }
    }

    private JsonObject loadGiftcodeDetail(Connection connection, int giftcodeId, boolean forUpdate) throws SQLException {
        JsonObject raw;
        String sql = "SELECT id, code, item, `option`, listIdPlayers, datecreate, expired, count_left "
                + "FROM giftcode WHERE id=?" + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, giftcodeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw ApiException.notFound("GIFTCODE_NOT_FOUND", "Không tìm thấy giftcode");
                }
                raw = jsonRow(resultSet, "id", "code", "item", "option", "listIdPlayers", "datecreate", "expired", "count_left");
            }
        }

        JsonObject validation = new JsonObject();
        JsonArray rewards = parseGiftcodeRewards(textValue(raw.get("item")), validation);
        JsonArray options = parseGiftcodeOptions(textValue(raw.get("option")), validation);
        JsonArray usedPlayerIds = parseGiftcodePlayerIds(textValue(raw.get("listIdPlayers")), validation);

        Set<Integer> itemIds = new LinkedHashSet<>();
        for (JsonElement element : rewards) itemIds.add(element.getAsJsonObject().get("id").getAsInt());
        Set<Integer> optionIds = new LinkedHashSet<>();
        for (JsonElement element : options) optionIds.add(element.getAsJsonObject().get("id").getAsInt());
        Set<Integer> playerIds = new LinkedHashSet<>();
        for (JsonElement element : usedPlayerIds) playerIds.add(element.getAsInt());
        Map<Integer, JsonObject> itemTemplates = loadLookupRows(connection, "items", itemIds);
        Map<Integer, JsonObject> optionTemplates = loadLookupRows(connection, "options", optionIds);
        Map<Integer, JsonObject> playerTemplates = loadLookupRows(connection, "players", playerIds);

        JsonArray resolvedRewards = new JsonArray();
        for (JsonElement element : rewards) {
            JsonObject reward = element.getAsJsonObject().deepCopy();
            JsonObject template = itemTemplates.get(reward.get("id").getAsInt());
            reward.add("itemTemplate", template == null ? JsonNull.INSTANCE : template.deepCopy());
            resolvedRewards.add(reward);
        }
        JsonArray resolvedOptions = new JsonArray();
        for (JsonElement element : options) {
            JsonObject option = element.getAsJsonObject().deepCopy();
            JsonObject template = optionTemplates.get(option.get("id").getAsInt());
            option.add("optionTemplate", template == null ? JsonNull.INSTANCE : template.deepCopy());
            resolvedOptions.add(option);
        }
        JsonArray usedPlayers = new JsonArray();
        for (JsonElement element : usedPlayerIds) {
            int playerId = element.getAsInt();
            JsonObject player = playerTemplates.get(playerId);
            if (player == null) {
                player = objectOf("id", playerId, "label", "Không tìm thấy", "meta", new JsonObject());
            }
            usedPlayers.add(player.deepCopy());
        }

        JsonObject data = raw.deepCopy();
        data.remove("item");
        data.remove("option");
        data.remove("listIdPlayers");
        data.add("rewards", resolvedRewards);
        data.add("options", resolvedOptions);
        data.add("usedPlayerIds", usedPlayerIds);
        data.add("usedPlayers", usedPlayers);
        if (validation.size() > 0) data.add("validation", validation);
        data.addProperty("rawItem", textValue(raw.get("item")) == null ? "" : textValue(raw.get("item")));
        data.addProperty("rawOption", textValue(raw.get("option")) == null ? "" : textValue(raw.get("option")));
        data.addProperty("rawUsedPlayers", textValue(raw.get("listIdPlayers")) == null ? "" : textValue(raw.get("listIdPlayers")));
        data.addProperty("version", rowVersion(raw));

        return data;
    }

    private JsonObject saveGiftcodeDetail(Integer giftcodeId, JsonObject envelope, AdminSession session) throws SQLException {
        JsonObject body = requestData(envelope);
        validateAllowedFields(body, Set.of("id", "code", "count_left", "expired", "rewards", "options", "version"), "giftcode");
        if (giftcodeId != null && body.has("id") && requiredJsonInt(body, "id", 1, Integer.MAX_VALUE) != giftcodeId) {
            throw ApiException.badRequest("PRIMARY_KEY_MISMATCH", "ID giftcode không khớp URL");
        }
        String code = requiredJsonText(body, "code", 255);
        int countLeft = requiredJsonInt(body, "count_left", 0, Integer.MAX_VALUE);
        String expired = requiredJsonText(body, "expired", 19);
        validateGiftcodeDate(expired);
        JsonArray rewards = requiredJsonArray(body, "rewards", "REWARDS_REQUIRED");
        JsonArray options = requiredJsonArray(body, "options", "OPTIONS_REQUIRED");
        if (rewards.size() > 200) throw ApiException.badRequest("TOO_MANY_REWARDS", "Giftcode tối đa 200 phần thưởng");
        if (options.size() > 200) throw ApiException.badRequest("TOO_MANY_OPTIONS", "Giftcode tối đa 200 option");

        JsonArray normalizedRewards = new JsonArray();
        Set<Integer> itemIds = new LinkedHashSet<>();
        for (int index = 0; index < rewards.size(); index++) {
            JsonElement element = rewards.get(index);
            if (!element.isJsonObject()) throw ApiException.badRequest("INVALID_REWARD", "Phần thưởng thứ " + (index + 1) + " phải là object");
            JsonObject reward = element.getAsJsonObject();
            validateAllowedFields(reward, Set.of("id", "quantity"), "giftcode reward");
            int itemId = requiredJsonInt(reward, "id", 0, IconAssetResolver.MAX_ICON_ID);
            int quantity = requiredJsonInt(reward, "quantity", 1, 1_000_000_000);
            if (!itemIds.add(itemId)) throw ApiException.badRequest("DUPLICATE_REWARD", "Item phần thưởng bị lặp: " + itemId);
            normalizedRewards.add(objectOf("id", itemId, "quantity", quantity));
        }
        JsonArray normalizedOptions = new JsonArray();
        Set<Integer> optionIds = new LinkedHashSet<>();
        for (int index = 0; index < options.size(); index++) {
            JsonElement element = options.get(index);
            if (!element.isJsonObject()) throw ApiException.badRequest("INVALID_OPTION", "Option thứ " + (index + 1) + " phải là object");
            JsonObject option = element.getAsJsonObject();
            validateAllowedFields(option, Set.of("id", "param"), "giftcode option");
            int optionId = requiredJsonInt(option, "id", 0, IconAssetResolver.MAX_ICON_ID);
            int param = requiredJsonInt(option, "param", Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (!optionIds.add(optionId)) throw ApiException.badRequest("DUPLICATE_OPTION", "Option giftcode bị lặp: " + optionId);
            normalizedOptions.add(objectOf("id", optionId, "param", param));
        }

        boolean creating = giftcodeId == null;
        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ensureIdsExist(connection, "item_template", "id", itemIds,
                        "ITEM_NOT_FOUND", "Item template không tồn tại");
                ensureIdsExist(connection, "item_option_template", "id", optionIds,
                        "OPTION_NOT_FOUND", "Option template không tồn tại");
                if (giftcodeId == null) {
                    try (PreparedStatement duplicate = connection.prepareStatement("SELECT id FROM giftcode WHERE code=? LIMIT 1")) {
                        duplicate.setString(1, code);
                        try (ResultSet resultSet = duplicate.executeQuery()) {
                            if (resultSet.next()) throw ApiException.badRequest("CODE_EXISTS", "Mã giftcode đã tồn tại");
                        }
                    }
                    String datecreate = LocalDateTime.now().format(GIFTCODE_DATE_FORMAT);
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO giftcode (code, item, `option`, listIdPlayers, datecreate, expired, count_left) VALUES (?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                        statement.setString(1, code);
                        statement.setString(2, GSON.toJson(normalizedRewards));
                        statement.setString(3, GSON.toJson(normalizedOptions));
                        statement.setString(4, "[]");
                        statement.setString(5, datecreate);
                        statement.setString(6, expired);
                        statement.setInt(7, countLeft);
                        statement.executeUpdate();
                        try (ResultSet keys = statement.getGeneratedKeys()) {
                            if (!keys.next()) throw new SQLException("Không lấy được id giftcode mới");
                            giftcodeId = keys.getInt(1);
                        }
                    }
                } else {
                    JsonObject current = loadGiftcodeRaw(connection, giftcodeId, true);
                    assertVersion(envelope, objectOf("version", rowVersion(current)));
                    try (PreparedStatement duplicate = connection.prepareStatement("SELECT id FROM giftcode WHERE code=? AND id<>? LIMIT 1")) {
                        duplicate.setString(1, code);
                        duplicate.setInt(2, giftcodeId);
                        try (ResultSet resultSet = duplicate.executeQuery()) {
                            if (resultSet.next()) throw ApiException.badRequest("CODE_EXISTS", "Mã giftcode đã tồn tại");
                        }
                    }
                    try (PreparedStatement statement = connection.prepareStatement(
                            "UPDATE giftcode SET code=?, item=?, `option`=?, expired=?, count_left=? WHERE id=?")) {
                        statement.setString(1, code);
                        statement.setString(2, GSON.toJson(normalizedRewards));
                        statement.setString(3, GSON.toJson(normalizedOptions));
                        statement.setString(4, expired);
                        statement.setInt(5, countLeft);
                        statement.setInt(6, giftcodeId);
                        if (statement.executeUpdate() == 0) throw ApiException.notFound("GIFTCODE_NOT_FOUND", "Không tìm thấy giftcode");
                    }
                }
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
        audit(session, creating ? "CREATE_DETAIL" : "UPDATE_DETAIL", "giftcodes",
                String.valueOf(giftcodeId), objectOf("rewards", normalizedRewards.size(), "options", normalizedOptions.size()));
        return loadGiftcodeDetail(giftcodeId);
    }

    private JsonObject resetGiftcodeUsedPlayers(int giftcodeId, JsonObject envelope, AdminSession session) throws SQLException {
        String expectedVersion = textValue(envelope.get("version"));
        if (expectedVersion == null || expectedVersion.isBlank()) throw ApiException.badRequest("VERSION_REQUIRED", "Thiếu version giftcode");
        JsonObject body = requestData(envelope);
        boolean all = body.has("all") && !body.get("all").isJsonNull() && body.get("all").isJsonPrimitive()
                && body.getAsJsonPrimitive("all").isBoolean() && body.get("all").getAsBoolean();
        JsonArray requested = body.has("playerIds") && body.get("playerIds").isJsonArray()
                ? body.getAsJsonArray("playerIds") : new JsonArray();
        if (!all && requested.size() == 0) throw ApiException.badRequest("PLAYERS_REQUIRED", "Chọn người chơi hoặc reset toàn bộ");
        Set<Integer> removeIds = new LinkedHashSet<>();
        for (JsonElement element : requested) removeIds.add(jsonInt(element, "playerId", 0, Integer.MAX_VALUE));

        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                JsonObject current = loadGiftcodeRaw(connection, giftcodeId, true);
                String actualVersion = rowVersion(current);
                if (!expectedVersion.equals(actualVersion)) throw new ApiException(409, "VERSION_CONFLICT", "Giftcode đã được thay đổi, hãy tải lại");
                JsonObject validation = new JsonObject();
                JsonArray currentIds = parseGiftcodePlayerIds(textValue(current.get("listIdPlayers")), validation);
                if (validation.size() > 0 && !all) throw ApiException.badRequest("INVALID_USED_PLAYERS", "Lịch sử sử dụng giftcode không hợp lệ");
                List<Integer> remaining = new ArrayList<>();
                if (!all) for (JsonElement element : currentIds) {
                    int value = element.getAsInt();
                    if (!removeIds.contains(value)) remaining.add(value);
                }
                try (PreparedStatement statement = connection.prepareStatement("UPDATE giftcode SET listIdPlayers=? WHERE id=?")) {
                    statement.setString(1, GSON.toJson(all ? new JsonArray() : intArray(remaining)));
                    statement.setInt(2, giftcodeId);
                    if (statement.executeUpdate() == 0) throw ApiException.notFound("GIFTCODE_NOT_FOUND", "Không tìm thấy giftcode");
                }
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
        audit(session, "RESET_USED_PLAYERS", "giftcodes", String.valueOf(giftcodeId),
                objectOf("all", all, "playerIds", requested));
        return loadGiftcodeDetail(giftcodeId);
    }

    private JsonObject loadGiftcodeRaw(Connection connection, int giftcodeId, boolean forUpdate) throws SQLException {
        String sql = "SELECT id, code, item, `option`, listIdPlayers, datecreate, expired, count_left FROM giftcode WHERE id=?"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, giftcodeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) throw ApiException.notFound("GIFTCODE_NOT_FOUND", "Không tìm thấy giftcode");
                return jsonRow(resultSet, "id", "code", "item", "option", "listIdPlayers", "datecreate", "expired", "count_left");
            }
        }
    }

    private static final DateTimeFormatter GIFTCODE_DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    private static JsonArray requiredJsonArray(JsonObject object, String key, String errorCode) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonArray()) throw ApiException.badRequest(errorCode, "Trường " + key + " phải là mảng");
        return value.getAsJsonArray();
    }

    private static void validateAllowedFields(JsonObject object, Set<String> allowed, String context) {
        for (String key : object.keySet()) {
            if (!allowed.contains(key)) {
                throw ApiException.badRequest("FIELD_NOT_ALLOWED", "Trường " + key + " không được phép trong " + context);
            }
        }
    }

    private static void validateGiftcodeDate(String value) {
        try {
            LocalDateTime.parse(value, GIFTCODE_DATE_FORMAT);
        } catch (Exception exception) {
            throw ApiException.badRequest("INVALID_EXPIRED", "expired phải có dạng yyyy-MM-dd HH:mm:ss");
        }
    }

    private static JsonArray parseGiftcodeRewards(String raw, JsonObject validation) {
        JsonArray result = new JsonArray();
        JsonArray source = parseJsonArray(raw, "item", validation);
        if (source == null) return result;
        for (JsonElement element : source) {
            if (!element.isJsonObject()) { validation.addProperty("item", "item phải chứa object"); return new JsonArray(); }
            JsonObject value = element.getAsJsonObject();
            Integer id = safeJsonInt(value.get("id"));
            Integer quantity = safeJsonInt(value.get("quantity"));
            if (id == null || id < 0 || quantity == null || quantity <= 0) {
                validation.addProperty("item", "item có id/quantity không hợp lệ");
                return new JsonArray();
            }
            result.add(objectOf("id", id, "quantity", quantity));
        }
        return result;
    }

    private static JsonArray parseGiftcodeOptions(String raw, JsonObject validation) {
        JsonArray result = new JsonArray();
        JsonArray source = parseJsonArray(raw, "option", validation);
        if (source == null) return result;
        for (JsonElement element : source) {
            if (!element.isJsonObject()) { validation.addProperty("option", "option phải chứa object"); return new JsonArray(); }
            JsonObject value = element.getAsJsonObject();
            Integer id = safeJsonInt(value.get("id"));
            Integer param = safeJsonInt(value.get("param"));
            if (id == null || id < 0 || param == null) {
                validation.addProperty("option", "option có id/param không hợp lệ");
                return new JsonArray();
            }
            result.add(objectOf("id", id, "param", param));
        }
        return result;
    }

    private static JsonArray parseGiftcodePlayerIds(String raw, JsonObject validation) {
        JsonArray result = new JsonArray();
        JsonArray source = parseJsonArray(raw, "listIdPlayers", validation);
        if (source == null) return result;
        for (JsonElement element : source) {
            Integer id = safeJsonInt(element);
            if (id == null || id < 0) {
                validation.addProperty("listIdPlayers", "Danh sách người đã dùng không hợp lệ");
                return new JsonArray();
            }
            if (!containsJsonInt(result, id)) result.add(id);
        }
        return result;
    }

    private static JsonArray parseJsonArray(String raw, String field, JsonObject validation) {
        if (raw == null || raw.isBlank()) {
            validation.addProperty(field, "Thiếu JSON");
            return null;
        }
        try {
            JsonElement parsed = JSON_PARSER.parse(raw);
            if (!parsed.isJsonArray()) {
                validation.addProperty(field, "Phải là JSON array");
                return null;
            }
            return parsed.getAsJsonArray();
        } catch (Exception exception) {
            validation.addProperty(field, "JSON malformed");
            return null;
        }
    }

    private static Integer safeJsonInt(JsonElement value) {
        if (value == null || value.isJsonNull() || !value.isJsonPrimitive()) return null;
        try {
            String text = value.getAsString().trim();
            if (!text.matches("-?\\d+")) return null;
            long parsed = Long.parseLong(text);
            return parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE ? null : (int) parsed;
        } catch (Exception exception) {
            return null;
        }
    }

    private static boolean containsJsonInt(JsonArray values, int candidate) {
        for (JsonElement value : values) if (safeJsonInt(value) != null && safeJsonInt(value) == candidate) return true;
        return false;
    }

    private void handleResource(HttpExchange exchange, String path, AdminSession session) throws Exception {
        List<String> segments = segments(path);
        if (segments.size() < 2 || !"resources".equals(segments.get(0))) {
            throw ApiException.notFound("RESOURCE_ROUTE_NOT_FOUND", "Không tìm thấy resource");
        }

        String resourceName = segments.get(1);
        ResourceDefinition definition = definitionFor(resourceName, query(exchange).get("table"));
        if (definition == null) {
            throw ApiException.notFound("RESOURCE_NOT_FOUND", "Resource không được hỗ trợ");
        }
        if ("topup-rewards".equals(resourceName)) {
            ensureTableAvailable(definition.table);
        }

        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
        if (segments.size() == 2 && "GET".equals(method)) {
            JsonObject list = listResource(definition, query(exchange));
            respondSuccess(exchange, 200, list.get("data"), list.getAsJsonObject("meta"));
            return;
        }

        String id = segments.size() >= 3 ? segments.get(2) : null;
        if (id == null && "POST".equals(method)) {
            requireWritable(definition);
            JsonObject body = requestData(readJsonObject(exchange));
            validateData(definition, body);
            JsonObject created = insertResource(definition, body, session);
            respondSuccess(exchange, 201, created, null);
            return;
        }
        if (id == null) {
            throw ApiException.badRequest("RESOURCE_ID_REQUIRED", "Thiếu id của resource");
        }
        if ("GET".equals(method)) {
            JsonObject row = findResource(definition, id);
            if (row == null) {
                throw ApiException.notFound("ROW_NOT_FOUND", "Không tìm thấy dữ liệu");
            }
            respondSuccess(exchange, 200, row, null);
            return;
        }
        if ("PUT".equals(method) || "PATCH".equals(method)) {
            requireWritable(definition);
            JsonObject envelope = readJsonObject(exchange);
            JsonObject body = requestData(envelope);
            if (body.has(definition.idColumn)) {
                String bodyId = textValue(body.get(definition.idColumn));
                if (bodyId != null && !id.equals(bodyId)) {
                    throw ApiException.badRequest("PRIMARY_KEY_MISMATCH", "Khóa chính trong payload không khớp URL");
                }
                body.remove(definition.idColumn);
            }
            validateData(definition, body);
            JsonObject updated = updateResource(definition, id, body, envelope, session);
            respondSuccess(exchange, 200, updated, null);
            return;
        }
        if ("DELETE".equals(method)) {
            requireWritable(definition);
            JsonObject envelope = readJsonObject(exchange);
            deleteResource(definition, id, envelope, session);
            respondSuccess(exchange, 200, messageData("Đã xóa dữ liệu"), null);
            return;
        }
        throw ApiException.methodNotAllowed("METHOD_NOT_ALLOWED", "Phương thức không được hỗ trợ");
    }

    private JsonObject listResource(ResourceDefinition definition, Map<String, String> query) throws SQLException {
        int page = positiveInt(query.get("page"), 1, 1, 1000000);
        int pageSize = positiveInt(query.get("pageSize"), 50, 1, 200);
        String search = query.getOrDefault("search", "").trim();
        String sort = query.getOrDefault("sort", definition.idColumn);
        if (!definition.columns.contains(sort)) {
            sort = definition.idColumn;
        }
        String order = "desc".equalsIgnoreCase(query.get("order")) ? "DESC" : "ASC";

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        if (!search.isEmpty() && !definition.searchColumns.isEmpty()) {
            where.append(" AND (");
            for (int index = 0; index < definition.searchColumns.size(); index++) {
                if (index > 0) {
                    where.append(" OR ");
                }
                where.append("CAST(").append(definition.searchColumns.get(index)).append(" AS CHAR) LIKE ?");
                parameters.add("%" + search + "%");
            }
            where.append(")");
        }

        JsonObject result = new JsonObject();
        JsonArray rows = new JsonArray();
        String select = "SELECT " + String.join(", ", definition.columns) + " FROM " + definition.table
                + where + " ORDER BY " + sort + " " + order + " LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM " + definition.table + where;
        long total = 0;

        try (Connection connection = ConnectDB.getConnection()) {
            try (PreparedStatement countStatement = connection.prepareStatement(count)) {
                bind(countStatement, parameters);
                try (ResultSet countSet = countStatement.executeQuery()) {
                    if (countSet.next()) {
                        total = countSet.getLong(1);
                    }
                }
            }
            try (PreparedStatement selectStatement = connection.prepareStatement(select)) {
                int parameterIndex = bind(selectStatement, parameters);
                selectStatement.setInt(parameterIndex++, pageSize);
                selectStatement.setInt(parameterIndex, (page - 1) * pageSize);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        rows.add(rowFromResultSet(resultSet, definition));
                    }
                }
            }
        }

        result.add("data", rows);
        JsonObject meta = new JsonObject();
        meta.addProperty("page", page);
        meta.addProperty("pageSize", pageSize);
        meta.addProperty("total", total);
        meta.addProperty("resource", definition.name);
        meta.add("columns", stringArray(definition.columns));
        result.add("meta", meta);
        return result;
    }

    private JsonObject findResource(ResourceDefinition definition, String id) throws SQLException {
        String sql = "SELECT " + String.join(", ", definition.columns) + " FROM " + definition.table
                + " WHERE " + definition.idColumn + " = ? LIMIT 1";
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? rowFromResultSet(resultSet, definition) : null;
            }
        }
    }

    private JsonObject insertResource(ResourceDefinition definition, JsonObject body, AdminSession session) throws SQLException {
        if (body.entrySet().isEmpty()) {
            throw ApiException.badRequest("EMPTY_DATA", "Dữ liệu không được rỗng");
        }
        List<String> columns = writableColumns(definition, body);
        String placeholders = String.join(", ", Collections.nCopies(columns.size(), "?"));
        String sql = "INSERT INTO " + definition.table + " (" + String.join(", ", columns) + ") VALUES (" + placeholders + ")";
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindJsonValues(statement, columns, body);
            statement.executeUpdate();
            String id = textValue(body.get(definition.idColumn));
            if (id == null) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        id = String.valueOf(keys.getObject(1));
                    }
                }
            }
            audit(session, "CREATE", definition.name, id, body);
            JsonObject created = id == null ? body.deepCopy() : findResource(definition, id);
            return created == null ? body.deepCopy() : created;
        }
    }

    private JsonObject updateResource(ResourceDefinition definition, String id, JsonObject body,
                                      JsonObject envelope, AdminSession session) throws SQLException {
        List<String> columns = writableColumns(definition, body);
        if (columns.isEmpty()) {
            throw ApiException.badRequest("EMPTY_UPDATE", "Không có trường nào để cập nhật");
        }

        String sql = "UPDATE " + definition.table + " SET "
                + joinAssignments(columns) + " WHERE " + definition.idColumn + " = ?";
        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);
            JsonObject updated;
            try {
                JsonObject current = findResourceForUpdate(connection, definition, id);
                if (current == null) {
                    throw ApiException.notFound("ROW_NOT_FOUND", "Không tìm thấy dữ liệu");
                }
                assertVersion(envelope, current);
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    int index = bindJsonValues(statement, columns, body);
                    statement.setString(index, id);
                    if (statement.executeUpdate() == 0) {
                        throw ApiException.notFound("ROW_NOT_FOUND", "Không tìm thấy dữ liệu");
                    }
                }
                updated = findResource(connection, definition, id);
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
            audit(session, "UPDATE", definition.name, id, safeAuditData(body));
            return updated;
        }
    }

    private void deleteResource(ResourceDefinition definition, String id, JsonObject envelope,
                                 AdminSession session) throws SQLException {
        String sql = "DELETE FROM " + definition.table + " WHERE " + definition.idColumn + " = ?";
        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                JsonObject current = findResourceForUpdate(connection, definition, id);
                if (current == null) {
                    throw ApiException.notFound("ROW_NOT_FOUND", "Không tìm thấy dữ liệu");
                }
                assertVersion(envelope, current);
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, id);
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
            audit(session, "DELETE", definition.name, id, null);
        }
    }

    private JsonObject findResource(Connection connection, ResourceDefinition definition, String id) throws SQLException {
        String sql = "SELECT " + String.join(", ", definition.columns) + " FROM " + definition.table
                + " WHERE " + definition.idColumn + " = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? rowFromResultSet(resultSet, definition) : null;
            }
        }
    }

    private JsonObject findResourceForUpdate(Connection connection, ResourceDefinition definition, String id) throws SQLException {
        String sql = "SELECT " + String.join(", ", definition.columns) + " FROM " + definition.table
                + " WHERE " + definition.idColumn + " = ? LIMIT 1 FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? rowFromResultSet(resultSet, definition) : null;
            }
        }
    }

    private JsonObject rowFromResultSet(ResultSet resultSet, ResourceDefinition definition) throws SQLException {
        JsonObject row = new JsonObject();
        for (String column : definition.columns) {
            Object value = resultSet.getObject(column);
            row.add(column, jsonValue(value));
        }
        row.addProperty("version", rowVersion(row));
        return row;
    }

    private void handleLookup(HttpExchange exchange, String path) throws Exception {
        requireMethod(exchange, "GET");
        String kind = segments(path).size() >= 2 ? segments(path).get(1) : "";
        LookupDefinition lookup = lookupDefinition(kind);
        if (lookup == null) {
            throw ApiException.notFound("LOOKUP_NOT_FOUND", "Lookup không được hỗ trợ");
        }
        Map<String, String> query = query(exchange);
        String search = query.getOrDefault("search", "").trim();
        int limit = positiveInt(query.get("limit"), 50, 1, 200);
        List<String> ids = lookupIds(query.get("ids"));
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(String.join(", ", lookup.columns))
                .append(" FROM ").append(lookup.table);
        List<String> predicates = new ArrayList<>();
        if (!search.isEmpty() && !lookup.searchColumns.isEmpty()) {
            predicates.add(joinLike(lookup.searchColumns));
        }
        if (!ids.isEmpty()) {
            predicates.add(lookup.idColumn + " IN (" + placeholders(ids.size()) + ")");
        }
        if (!predicates.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", predicates));
        }
        sql.append(" ORDER BY ").append(lookup.idColumn).append(" LIMIT ?");
        JsonArray data = new JsonArray();
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (!search.isEmpty() && !lookup.searchColumns.isEmpty()) {
                for (int i = 0; i < lookup.searchColumns.size(); i++) {
                    statement.setString(index++, "%" + search + "%");
                }
            }
            for (String id : ids) {
                statement.setString(index++, id);
            }
            statement.setInt(index, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JsonObject row = new JsonObject();
                    for (String column : lookup.columns) {
                        row.add(column, jsonValue(resultSet.getObject(column)));
                    }
                    data.add(normalizeLookupRow(kind, lookup, row));
                }
            }
        }
        JsonObject meta = new JsonObject();
        meta.addProperty("kind", kind);
        meta.addProperty("limit", limit);
        meta.addProperty("requested", ids.size());
        meta.addProperty("total", data.size());
        respondSuccess(exchange, 200, data, meta);
    }

    private JsonObject normalizeLookupRow(String kind, LookupDefinition lookup, JsonObject row) {
        JsonObject result = new JsonObject();
        JsonElement id = row.get(lookup.idColumn);
        if (id == null || id.isJsonNull()) {
            id = JsonNull.INSTANCE;
        }
        result.add("id", id.deepCopy());

        String rawLabel = lookup.labelColumn == null ? null : textValue(row.get(lookup.labelColumn));
        if ((rawLabel == null || rawLabel.isBlank()) && "clans".equals(kind)) {
            rawLabel = textValue(row.get("NAME_2"));
        }
        String label;
        if (rawLabel == null || rawLabel.isBlank()) {
            label = "#" + textValue(id);
        } else if ("parts".equals(kind)) {
            label = "Part #" + textValue(id) + " · " + partTypeLabel(rawLabel);
        } else if ("shop-items".equals(kind)) {
            label = "Shop item · item #" + rawLabel;
        } else if ("shop-options".equals(kind)) {
            label = "Shop option · option #" + rawLabel;
        } else {
            label = rawLabel;
        }
        result.addProperty("label", label);

        if (lookup.iconColumn != null) {
            Integer iconId = "parts".equals(kind) && "DATA".equals(lookup.iconColumn)
                    ? firstAvailablePartIconId(row.get(lookup.iconColumn), iconAssets) : integerValue(row.get(lookup.iconColumn));
            if (iconId != null && iconId > 0 && iconAssets.isAvailable(iconId)) {
                result.addProperty("iconId", iconId);
            }
        }

        JsonObject meta = row.deepCopy();
        meta.remove(lookup.idColumn);
        if ("parts".equals(kind)) meta.remove("DATA");
        result.add("meta", meta);
        return result;
    }

    private static String partTypeLabel(String rawType) {
        return switch (rawType) {
            case "0" -> "Head";
            case "1" -> "Body";
            case "2" -> "Leg";
            default -> "Type " + rawType;
        };
    }

    static List<Integer> partIconIds(JsonElement value) {
        List<Integer> result = new ArrayList<>();
        if (value == null || value.isJsonNull()) return result;
        try {
            JsonElement parsed = value.isJsonArray() ? value : JSON_PARSER.parse(value.getAsString());
            if (!parsed.isJsonArray()) return result;
            JsonArray layers = parsed.getAsJsonArray();
            for (JsonElement layer : layers) {
                Integer iconId = null;
                if (layer.isJsonArray() && layer.getAsJsonArray().size() > 0) {
                    iconId = integerValue(layer.getAsJsonArray().get(0));
                } else {
                    iconId = integerValue(layer);
                }
                if (iconId != null && iconId > 0 && !result.contains(iconId)) {
                    result.add(iconId);
                }
            }
        } catch (Exception ignored) {
            return result;
        }
        return result;
    }

    static Integer firstAvailablePartIconId(JsonElement value, IconAssetResolver assets) {
        if (assets == null) return null;
        for (Integer iconId : partIconIds(value)) {
            if (assets.isAvailable(iconId)) return iconId;
        }
        return null;
    }

    private static Integer integerValue(JsonElement value) {
        if (value == null || value.isJsonNull()) return null;
        try {
            return value.getAsInt();
        } catch (Exception ignored) {
            return null;
        }
    }

    static List<String> lookupIds(String raw) {
        if (raw == null || raw.isBlank()) return Collections.emptyList();
        String[] values = raw.split(",");
        if (values.length > 200) {
            throw ApiException.badRequest("TOO_MANY_LOOKUP_IDS", "Tối đa 200 ID mỗi lần tra cứu");
        }
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String value : values) {
            String id = value.trim();
            if (!id.matches("-?\\d{1,10}")) {
                throw ApiException.badRequest("INVALID_LOOKUP_ID", "ID tra cứu không hợp lệ");
            }
            try {
                Integer.parseInt(id);
            } catch (NumberFormatException exception) {
                throw ApiException.badRequest("INVALID_LOOKUP_ID", "ID tra cứu không hợp lệ");
            }
            result.add(id);
        }
        return new ArrayList<>(result);
    }

    private static String placeholders(int count) {
        return String.join(", ", Collections.nCopies(count, "?"));
    }

    private void handleServer(HttpExchange exchange, String path, AdminSession session) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
        if ("/server/reload".equals(path)) {
            requireMethod(exchange, "POST");
            JsonObject body = readJsonObject(exchange);
            JsonArray targetArray = body.has("targets") && body.get("targets").isJsonArray()
                    ? body.getAsJsonArray("targets") : new JsonArray();
            JsonArray reloaded = new JsonArray();
            for (JsonElement target : targetArray) {
                String value = target.getAsString().toLowerCase(Locale.ROOT);
                switch (value) {
                    case "giftcode", "giftcodes" -> {
                        GiftCodeManager.gI().listGiftCode.clear();
                        GiftCodeManager.gI().init();
                        reloaded.add("giftcodes");
                    }
                    case "shop", "shops" -> {
                        Manager.gI().updateShop();
                        reloaded.add("shops");
                    }
                    case "map", "maps", "npc", "npcs" -> {
                        Manager.gI().loadMap();
                        reloaded.add("maps");
                    }
                    case "drop", "drops" -> {
                        DropManager.gI().reload();
                        reloaded.add("drops");
                    }
                    case "boss", "bosses" -> {
                        applyBossOverrides();
                        reloaded.add("boss-config");
                    }
                    default -> throw ApiException.badRequest("INVALID_RELOAD_TARGET", "Target reload không hợp lệ: " + value);
                }
            }
            audit(session, "RELOAD", "server", null, body);
            respondSuccess(exchange, 200, objectOf("reloaded", reloaded), null);
            return;
        }
        if ("/server/maintenance".equals(path)) {
            requireMethod(exchange, "POST");
            JsonObject body = readJsonObject(exchange);
            int seconds = positiveInt(body.has("delaySeconds") ? body.get("delaySeconds").getAsString() : null,
                    body.has("minutes") ? positiveInt(body.get("minutes").getAsString(), 2, 0, 1440) * 60 : 120,
                    0, 86400);
            Maintenance.setAutoRestartRequested(booleanValue(body, "autoRestart", false));
            if (seconds == 0) {
                Maintenance.gI().startImmediately();
            } else {
                Maintenance.gI().startSeconds(seconds);
            }
            audit(session, "MAINTENANCE", "server", null, objectOf("delaySeconds", seconds));
            respondSuccess(exchange, 202, objectOf("scheduled", true, "delaySeconds", seconds), null);
            return;
        }
        if ("/server/kick-all".equals(path)) {
            requireMethod(exchange, "POST");
            Client.gI().close();
            audit(session, "KICK_ALL", "server", null, null);
            respondSuccess(exchange, 200, messageData("Đã kick toàn bộ người chơi"), null);
            return;
        }
        if ("/server/config/exp".equals(path)) {
            requireMethod(exchange, "PUT");
            JsonObject body = readJsonObject(exchange);
            int rate = positiveInt(body.has("rate") ? body.get("rate").getAsString() : null, Manager.RATE_EXP_SERVER, 1, 1000);
            Manager.RATE_EXP_SERVER = rate;
            audit(session, "UPDATE", "server.exp", null, objectOf("rate", rate));
            respondSuccess(exchange, 200, objectOf("rate", rate), null);
            return;
        }
        if ("/server/config/data-mode".equals(path)) {
            requireMethod(exchange, "PUT");
            JsonObject body = readJsonObject(exchange);
            String mode = body.has("mode") ? body.get("mode").getAsString().toLowerCase(Locale.ROOT) : "int";
            if (!"int".equals(mode) && !"long".equals(mode)) {
                throw ApiException.badRequest("INVALID_DATA_MODE", "Mode phải là int hoặc long");
            }
            Manager.readInt = "int".equals(mode);
            audit(session, "UPDATE", "server.data-mode", null, objectOf("mode", mode));
            respondSuccess(exchange, 200, objectOf("mode", mode), null);
            return;
        }
        if ("/server/bots".equals(path)) {
            requireMethod(exchange, "PUT");
            JsonObject body = readJsonObject(exchange);
            for (Map.Entry<String, JsonElement> entry : body.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    runtimeSettings.put("bot." + entry.getKey(), entry.getValue().toString());
                }
            }
            audit(session, "UPDATE", "server.bots", null, body);
            respondSuccess(exchange, 200, objectOf("settings", objectFromSettings("bot.")), null);
            return;
        }
        if ("/server/optimize".equals(path)) {
            requireMethod(exchange, "POST");
            JsonObject body = readJsonObject(exchange);
            String kind = body.has("kind") ? body.get("kind").getAsString() : "ram";
            if ("ram".equals(kind)) {
                System.gc();
            }
            audit(session, "OPTIMIZE", "server", null, objectOf("kind", kind));
            respondSuccess(exchange, 200, objectOf("kind", kind, "accepted", true), null);
            return;
        }
        if ("GET".equals(method) && "/server/config".equals(path)) {
            respondSuccess(exchange, 200, serverConfigSnapshot(), null);
            return;
        }
        throw ApiException.notFound("SERVER_ROUTE_NOT_FOUND", "Không tìm thấy thao tác server");
    }

    private void handlePlayerAction(HttpExchange exchange, String path, AdminSession session) throws Exception {
        requireMethod(exchange, "POST");
        List<String> parts = segments(path);
        if (parts.size() < 3 || !"players".equals(parts.get(0))) {
            throw ApiException.notFound("PLAYER_ACTION_NOT_FOUND", "Không tìm thấy player action");
        }
        long playerId;
        try {
            playerId = Long.parseLong(parts.get(1));
        } catch (NumberFormatException exception) {
            throw ApiException.badRequest("INVALID_PLAYER_ID", "Player id không hợp lệ");
        }
        String action = parts.get(2).toLowerCase(Locale.ROOT);
        if ("kick".equals(action)) {
            nro.player.Player player = Client.gI().getPlayerByID(playerId);
            boolean online = player != null && player.getSession() != null;
            if (online) {
                Client.gI().kickSession(player.getSession());
            }
            audit(session, "KICK_PLAYER", "players", String.valueOf(playerId), null);
            respondSuccess(exchange, 200, objectOf("playerId", playerId, "online", online, "kicked", online), null);
            return;
        }
        if (!"buff-item".equals(action) && !"revoke-item".equals(action)) {
            throw ApiException.notFound("PLAYER_ACTION_NOT_FOUND", "Player action không hợp lệ");
        }
        JsonObject body = readJsonObject(exchange);
        nro.player.Player livePlayer = Client.gI().getPlayerByID(playerId);
        boolean online = livePlayer != null && livePlayer.getSession() != null;
        if (online) {
            // The legacy server periodically persists the in-memory player.
            // Save the current state and detach the session before changing
            // items_bag so the next login cannot overwrite the admin change.
            Client.gI().kickSession(livePlayer.getSession());
        }
        JsonObject result = mutatePlayerBag(playerId, "buff-item".equals(action), body);
        result.addProperty("kickedForConsistency", online);
        audit(session, "PLAYER_" + action.toUpperCase(Locale.ROOT), "players", String.valueOf(playerId), body);
        respondSuccess(exchange, 200, result, null);
    }

    private JsonObject mutatePlayerBag(long playerId, boolean add, JsonObject body) throws SQLException {
        int itemId = positiveInt(body.has("itemId") ? body.get("itemId").getAsString() : null, -1, 0, 2_000_000);
        if (itemId < 0) {
            throw ApiException.badRequest("ITEM_ID_REQUIRED", "Thiếu itemId");
        }
        int quantity = positiveInt(body.has("quantity") ? body.get("quantity").getAsString() : null, 1, 1, 1_000_000_000);
        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String rawBag;
                try (PreparedStatement select = connection.prepareStatement("SELECT items_bag FROM player WHERE id=? FOR UPDATE")) {
                    select.setLong(1, playerId);
                    try (ResultSet resultSet = select.executeQuery()) {
                        if (!resultSet.next()) {
                            throw ApiException.notFound("PLAYER_NOT_FOUND", "Không tìm thấy người chơi");
                        }
                        rawBag = resultSet.getString(1);
                    }
                }
                JsonElement parsed = JSON_PARSER.parse(rawBag == null || rawBag.isBlank() ? "[]" : rawBag);
                if (!parsed.isJsonArray()) {
                    throw ApiException.badRequest("INVALID_PLAYER_BAG", "items_bag không phải JSON array");
                }
                JsonArray bag = parsed.getAsJsonArray();
                int changed = 0;
                if (add) {
                    String options = "[]";
                    if (body.has("options") && !body.get("options").isJsonNull()) {
                        options = body.get("options").isJsonPrimitive() ? body.get("options").getAsString() : GSON.toJson(body.get("options"));
                        try {
                            JSON_PARSER.parse(options);
                        } catch (Exception exception) {
                            throw ApiException.badRequest("INVALID_ITEM_OPTIONS", "options không phải JSON hợp lệ");
                        }
                    }
                    JsonArray item = new JsonArray();
                    item.add(itemId);
                    item.add(quantity);
                    item.add(options);
                    item.add(System.currentTimeMillis());
                    bag.add(GSON.toJson(item));
                    changed = 1;
                } else {
                    boolean removeAll = booleanValue(body, "removeAll", false);
                    for (int index = bag.size() - 1; index >= 0; index--) {
                        JsonElement itemElement = bag.get(index);
                        JsonArray item = parseBagItem(itemElement);
                        if (item == null || item.size() == 0 || item.get(0).getAsInt() != itemId) continue;
                        if (removeAll || item.size() < 2 || item.get(1).getAsLong() <= quantity) {
                            bag.remove(index);
                            changed++;
                        } else {
                            item.set(1, new JsonPrimitive(item.get(1).getAsLong() - quantity));
                            bag.set(index, new JsonPrimitive(GSON.toJson(item)));
                            changed++;
                        }
                    }
                }
                if (!add && changed == 0) {
                    connection.rollback();
                    return objectOf("playerId", playerId, "itemId", itemId, "changed", 0, "found", false);
                }
                try (PreparedStatement update = connection.prepareStatement("UPDATE player SET items_bag=? WHERE id=?")) {
                    update.setString(1, GSON.toJson(bag));
                    update.setLong(2, playerId);
                    update.executeUpdate();
                }
                connection.commit();
                return objectOf("playerId", playerId, "itemId", itemId, "quantity", quantity, "changed", changed,
                        "runtimeReloadRequired", true);
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private JsonArray parseBagItem(JsonElement element) {
        try {
            String raw = element.isJsonPrimitive() ? element.getAsString() : GSON.toJson(element);
            JsonElement parsed = JSON_PARSER.parse(raw);
            return parsed.isJsonArray() ? parsed.getAsJsonArray() : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private JsonObject serverConfigSnapshot() {
        JsonObject data = new JsonObject();
        data.addProperty("expRate", Manager.RATE_EXP_SERVER);
        data.addProperty("dataMode", Manager.readInt ? "int" : "long");
        data.addProperty("gamePort", ServerManager.PORT);
        data.addProperty("adminApiPort", config.port);
        data.add("bots", objectFromSettings("bot."));
        return data;
    }

    private void handleEvents(HttpExchange exchange, String path, AdminSession session) throws Exception {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            JsonObject data = new JsonObject();
            JsonArray events = new JsonArray();
            Set<Integer> active = readActiveEventIds();
            for (EventDefinition definition : eventDefinitions) {
                JsonObject event = new JsonObject();
                event.addProperty("id", definition.id);
                event.addProperty("name", definition.name);
                event.addProperty("active", active.contains(definition.id));
                events.add(event);
            }
            data.add("events", events);
            data.add("activeIds", intArray(active));
            data.addProperty("runtimeUsesFirstEventOnly", true);
            respondSuccess(exchange, 200, data, null);
            return;
        }
        requireMethod(exchange, "PUT");
        JsonObject body = readJsonObject(exchange);
        if (!body.has("eventIds") || !body.get("eventIds").isJsonArray()) {
            throw ApiException.badRequest("EVENT_IDS_REQUIRED", "eventIds phải là mảng");
        }
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        for (JsonElement element : body.getAsJsonArray("eventIds")) {
            int id = element.getAsInt();
            if (id < 1 || id > 11) {
                throw ApiException.badRequest("INVALID_EVENT_ID", "Event không hợp lệ: " + id);
            }
            ids.add(id);
        }
        writeActiveEventIds(ids);
        int first = ids.isEmpty() ? 0 : ids.iterator().next();
        EventManager.gI().setCurrentEvent(first);
        JsonObject result = objectOf("activeIds", intArray(ids), "runtimeFirstEvent", first);
        audit(session, "UPDATE", "events", null, result);
        respondSuccess(exchange, 200, result, null);
    }

    private void handleBossConfig(HttpExchange exchange, String path, AdminSession session) throws Exception {
        List<String> segments = segments(path);
        if (segments.size() == 1 && "GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            JsonArray data = listBossConfigs();
            respondSuccess(exchange, 200, data, null);
            return;
        }
        if (segments.size() == 2 && "POST".equalsIgnoreCase(exchange.getRequestMethod())
                && "reload".equals(segments.get(1))) {
            applyBossOverrides();
            audit(session, "RELOAD", "boss-config", null, null);
            respondSuccess(exchange, 200, messageData("Đã áp dụng cấu hình boss"), null);
            return;
        }
        if (segments.size() != 2) {
            throw ApiException.notFound("BOSS_ROUTE_NOT_FOUND", "Không tìm thấy boss config");
        }
        String key = segments.get(1);
        if (!SAFE_KEY.matcher(key).matches()) {
            throw ApiException.badRequest("INVALID_BOSS_KEY", "Boss key không hợp lệ");
        }
        Field field = bossField(key);
        if (field == null) {
            throw ApiException.notFound("BOSS_NOT_FOUND", "Không tìm thấy boss config");
        }
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            JsonObject data = bossConfigRow(key, field);
            respondSuccess(exchange, 200, data, null);
            return;
        }
        requireMethod(exchange, "PUT");
        JsonObject envelope = readJsonObject(exchange);
        JsonObject body = requestData(envelope);
        BossData parsed;
        try {
            parsed = GSON.fromJson(body, BossData.class);
        } catch (Exception exception) {
            throw ApiException.badRequest("INVALID_BOSS_CONFIG", "Boss config JSON không hợp lệ");
        }
        if (!isValidBossConfig(parsed)) {
            throw ApiException.badRequest("INVALID_BOSS_CONFIG", "Boss config thiếu trường bắt buộc hoặc có kiểu dữ liệu không hợp lệ");
        }
        JsonObject current = bossConfigRow(key, field);
        assertVersion(envelope, current);
        String json = GSON.toJson(body);
        String sql = "INSERT INTO admin_boss_config (boss_key, config_json, updated_by, updated_at) VALUES (?, ?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE config_json=VALUES(config_json), updated_by=VALUES(updated_by), updated_at=NOW()";
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            statement.setString(2, json);
            statement.setInt(3, session.accountId);
            statement.executeUpdate();
        }
        applyBossData(field, parsed);
        audit(session, "UPDATE", "boss-config", key, safeAuditData(body));
        respondSuccess(exchange, 200, bossConfigRow(key, field), null);
    }

    private boolean isValidBossConfig(BossData data) {
        return data != null
                && data.getName() != null && !data.getName().trim().isEmpty() && data.getName().length() <= 255
                && data.getOutfit() != null && data.getOutfit().length > 0
                && data.getHp() != null && data.getHp().length > 0
                && data.getMapJoin() != null && data.getMapJoin().length > 0
                && data.getSkillTemp() != null && data.getSkillTemp().length > 0
                && data.getTextS() != null && data.getTextM() != null && data.getTextE() != null
                && data.getTypeAppear() != null;
    }

    private JsonArray listBossConfigs() throws SQLException {
        Map<String, String> overrides = bossOverrides();
        JsonArray result = new JsonArray();
        for (Field field : BossesData.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !BossData.class.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                BossData data = (BossData) field.get(null);
                if (data == null) {
                    continue;
                }
                JsonObject row = new JsonObject();
                row.addProperty("key", field.getName());
                row.add("data", GSON.toJsonTree(data));
                row.addProperty("overridden", overrides.containsKey(field.getName()));
                String json = overrides.get(field.getName());
                row.addProperty("version", sha256(json == null ? GSON.toJson(data) : json));
                result.add(row);
            } catch (IllegalAccessException ignored) {
            }
        }
        return result;
    }

    private JsonObject bossConfigRow(String key, Field field) throws SQLException {
        JsonObject row = new JsonObject();
        try {
            BossData data = (BossData) field.get(null);
            row.addProperty("key", key);
            row.add("data", GSON.toJsonTree(data));
        } catch (IllegalAccessException exception) {
            throw new ApiException(500, "BOSS_REFLECTION_ERROR", "Không thể đọc boss config");
        }
        String override = bossOverrides().get(key);
        row.addProperty("overridden", override != null);
        row.addProperty("version", sha256(override == null ? row.get("data").toString() : override));
        return row;
    }

    private void handleBossAction(HttpExchange exchange, AdminSession session) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readJsonObject(exchange);
        String action = requiredText(body, "action", 50).toLowerCase(Locale.ROOT);
        JsonObject result = new JsonObject();
        switch (action) {
            case "resetall" -> {
                BossManager.gI().resetAllBosses();
                result.addProperty("action", action);
            }
            case "respawnresting" -> result.addProperty("count", BossManager.gI().respawnAllRestingBosses());
            case "summon" -> {
                int bossId = requiredInt(body, "bossId", -2_000_000_000, 2_000_000_000);
                result.addProperty("bossId", bossId);
                result.addProperty("created", BossManager.gI().createBoss(bossId) != null);
            }
            default -> throw ApiException.badRequest("INVALID_BOSS_ACTION", "Boss action không hợp lệ");
        }
        audit(session, "BOSS_ACTION", action, null, body);
        respondSuccess(exchange, 200, result, null);
    }

    private void handleSecurity(HttpExchange exchange, String path, AdminSession session) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
        if ("/security/blocked-ips".equals(path) && "GET".equals(method)) {
            ResourceDefinition definition = resources.get("security");
            JsonObject list = listResource(definition, query(exchange));
            respondSuccess(exchange, 200, list.get("data"), list.getAsJsonObject("meta"));
            return;
        }
        if ("/security/block-ip".equals(path) && "POST".equals(method)) {
            JsonObject body = readJsonObject(exchange);
            String ip = requiredText(body, "ip", 64);
            validateIp(ip);
            String reason = "Admin block";
            if (body.has("reason") && !body.get("reason").isJsonNull()) {
                if (!body.get("reason").isJsonPrimitive() || !body.getAsJsonPrimitive("reason").isString()) {
                    throw ApiException.badRequest("INVALID_REASON", "Lý do phải là chuỗi");
                }
                reason = body.get("reason").getAsString().trim();
                if (reason.isBlank() || reason.length() > 255) {
                    throw ApiException.badRequest("INVALID_REASON", "Lý do phải có từ 1 đến 255 ký tự");
                }
            }
            try (Connection connection = ConnectDB.getConnection()) {
                try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM blockip_list WHERE blocker_ip=? LIMIT 1")) {
                    check.setString(1, ip);
                    try (ResultSet resultSet = check.executeQuery()) {
                        if (!resultSet.next()) {
                            try (PreparedStatement insert = connection.prepareStatement(
                                    "INSERT INTO blockip_list (blocker_ip, block_reason, blocked_at) VALUES (?, ?, NOW())")) {
                                insert.setString(1, ip);
                                insert.setString(2, reason.substring(0, Math.min(255, reason.length())));
                                insert.executeUpdate();
                            }
                        }
                    }
                }
            }
            firewallBlock(ip);
            audit(session, "BLOCK_IP", "security", ip, objectOf("reason", reason));
            respondSuccess(exchange, 200, objectOf("ip", ip, "blocked", true), null);
            return;
        }
        if (path.startsWith("/security/blocked-ips/") && "DELETE".equals(method)) {
            String ip = decode(segments(path).get(2));
            validateIp(ip);
            try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM blockip_list WHERE blocker_ip=?")) {
                statement.setString(1, ip);
                statement.executeUpdate();
            }
            firewallUnblock(ip);
            audit(session, "UNBLOCK_IP", "security", ip, null);
            respondSuccess(exchange, 200, objectOf("ip", ip, "blocked", false), null);
            return;
        }
        if ("/security/unblock-all".equals(path) && "POST".equals(method)) {
            try (Connection connection = ConnectDB.getConnection(); Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM blockip_list");
            }
            firewallClear();
            audit(session, "UNBLOCK_ALL", "security", null, null);
            respondSuccess(exchange, 200, messageData("Đã gỡ toàn bộ IP bị chặn"), null);
            return;
        }
        throw ApiException.notFound("SECURITY_ROUTE_NOT_FOUND", "Không tìm thấy security endpoint");
    }

    private void handleAntiDdos(HttpExchange exchange, String path, AdminSession session) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
        if ("/anti-ddos/status".equals(path) && "GET".equals(method)) {
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        if ("/anti-ddos/settings".equals(path) && "PUT".equals(method)) {
            JsonObject body = readJsonObject(exchange);
            antiDdosLimit = positiveInt(body.has("connLimit") ? body.get("connLimit").getAsString() : null,
                    antiDdosLimit, 1, 100000);
            antiDdosScanSeconds = positiveInt(body.has("scanSeconds") ? body.get("scanSeconds").getAsString() : null,
                    antiDdosScanSeconds, 5, 3600);
            if (body.has("gamePort")) {
                int port = positiveInt(body.get("gamePort").getAsString(), ServerManager.PORT, 1, 65535);
                runtimeSettings.put("antiDdos.gamePort", String.valueOf(port));
            }
            rescheduleAntiDdos();
            audit(session, "UPDATE", "anti-ddos.settings", null, body);
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        if ("/anti-ddos/start".equals(path) && "POST".equals(method)) {
            antiDdosRunning = true;
            antiDdosAutoScan = true;
            rescheduleAntiDdos();
            audit(session, "ANTI_DDOS_START", "anti-ddos", null, null);
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        if ("/anti-ddos/stop".equals(path) && "POST".equals(method)) {
            stopAntiDdos();
            audit(session, "ANTI_DDOS_STOP", "anti-ddos", null, null);
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        if ("/anti-ddos/auto-scan".equals(path) && "POST".equals(method)) {
            antiDdosAutoScan = !antiDdosAutoScan;
            rescheduleAntiDdos();
            audit(session, "ANTI_DDOS_AUTO_SCAN", "anti-ddos", null, objectOf("enabled", antiDdosAutoScan));
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        if ("/anti-ddos/lockdown".equals(path) && "POST".equals(method)) {
            antiDdosLockdown = !antiDdosLockdown;
            int port = antiDdosGamePort();
            if (antiDdosLockdown) {
                firewallPort(port, true);
            } else {
                firewallPort(port, false);
            }
            audit(session, "ANTI_DDOS_LOCKDOWN", "anti-ddos", null, objectOf("enabled", antiDdosLockdown));
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        if ("/anti-ddos/sync".equals(path) && "POST".equals(method)) {
            syncFirewallFromDatabase();
            audit(session, "ANTI_DDOS_SYNC", "anti-ddos", null, null);
            respondSuccess(exchange, 200, antiDdosSnapshot(), null);
            return;
        }
        throw ApiException.notFound("ANTI_DDOS_ROUTE_NOT_FOUND", "Không tìm thấy Anti-DDoS endpoint");
    }

    private JsonObject antiDdosSnapshot() {
        JsonObject data = new JsonObject();
        data.addProperty("running", antiDdosRunning);
        data.addProperty("autoScan", antiDdosAutoScan);
        data.addProperty("lockdown", antiDdosLockdown);
        data.addProperty("gamePort", antiDdosGamePort());
        data.addProperty("connLimit", antiDdosLimit);
        data.addProperty("scanSeconds", antiDdosScanSeconds);
        data.addProperty("mode", "game-connection-map");
        return data;
    }

    private void handleDashboardStream(HttpExchange exchange, AdminSession session) throws IOException {
        requireMethod(exchange, "GET");
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/event-stream; charset=utf-8");
        headers.set("Cache-Control", "no-cache");
        headers.set("Connection", "keep-alive");
        headers.set("X-Accel-Buffering", "no");
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream output = exchange.getResponseBody()) {
            while (ServerManager.isRunning || httpServer != null) {
                JsonObject data = dashboardSnapshot();
                writeSse(output, "metrics", data);
                output.flush();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException ignored) {
            // The browser closing an SSE connection is normal.
        }
    }

    private JsonObject dashboardSnapshot() {
        JsonObject data = new JsonObject();
        data.addProperty("serverRunning", ServerManager.isRunning);
        data.addProperty("serverName", ServerManager.NAME);
        data.addProperty("gamePort", ServerManager.PORT);
        data.addProperty("startTime", ServerManager.timeStart);
        Runtime runtime = Runtime.getRuntime();
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        data.addProperty("heapUsedBytes", heap.getUsed());
        data.addProperty("heapMaxBytes", heap.getMax());
        data.addProperty("heapUsedPercent", heap.getMax() > 0 ? (heap.getUsed() * 100.0 / heap.getMax()) : 0);
        data.addProperty("cpuLoad", cpuLoad());
        data.addProperty("threads", Thread.activeCount());
        data.addProperty("sessions", safeSessionCount());
        data.addProperty("onlinePlayers", safeOnlinePlayers());
        int[] bossStatus = safeBossStatus();
        data.addProperty("bossAlive", bossStatus[0]);
        data.addProperty("bossDead", bossStatus[1]);
        data.addProperty("bossResting", bossStatus[2]);
        data.addProperty("jvmFreeBytes", runtime.freeMemory());
        data.add("events", currentEventFlags());
        data.add("antiDdos", antiDdosSnapshot());
        return data;
    }

    private void handleIcon(HttpExchange exchange, String path) throws IOException {
        requireMethod(exchange, "GET");
        List<String> parts = segments(path);
        if (parts.size() != 3) {
            throw ApiException.badRequest("ICON_ID_REQUIRED", "Thiếu icon id");
        }
        int id;
        try {
            id = Integer.parseInt(parts.get(2));
        } catch (NumberFormatException exception) {
            throw ApiException.badRequest("INVALID_ICON_ID", "Icon id không hợp lệ");
        }
        if (id < 0 || id > IconAssetResolver.MAX_ICON_ID) {
            throw ApiException.badRequest("INVALID_ICON_ID", "Icon id không hợp lệ");
        }
        byte[] bytes = iconAssets.read(id).orElse(null);
        if (bytes == null) {
            throw ApiException.notFound("ICON_NOT_FOUND", "Không tìm thấy icon");
        }
        exchange.getResponseHeaders().set("Content-Type", "image/png");
        exchange.getResponseHeaders().set("Cache-Control", "public, max-age=86400");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private JsonObject dashboardSnapshotForSse() {
        return dashboardSnapshot();
    }

    private void writeSse(OutputStream output, String event, JsonObject data) throws IOException {
        String payload = "event: " + event + "\ndata: " + GSON.toJson(data) + "\n\n";
        output.write(payload.getBytes(StandardCharsets.UTF_8));
    }

    private void addCorsHeaders(HttpExchange exchange) {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (origin != null && (config.corsOrigin.equals("*") || config.corsOrigin.equals(origin))) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
            exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
        }
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-CSRF-Token");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Vary", "Origin");
    }

    private AdminSession requireSession(HttpExchange exchange, boolean checkCsrf) {
        String sessionId = cookie(exchange, SESSION_COOKIE);
        if (sessionId == null) {
            throw new ApiException(401, "UNAUTHENTICATED", "Phiên đăng nhập đã hết hạn");
        }
        AdminSession session = sessions.get(sessionId);
        if (session == null || session.isExpired()) {
            sessions.remove(sessionId);
            throw new ApiException(401, "UNAUTHENTICATED", "Phiên đăng nhập đã hết hạn");
        }
        session.touch();
        if (checkCsrf && !MessageDigest.isEqual(session.csrfToken.getBytes(StandardCharsets.UTF_8),
                String.valueOf(exchange.getRequestHeaders().getFirst("X-CSRF-Token")).getBytes(StandardCharsets.UTF_8))) {
            throw new ApiException(403, "CSRF_FAILED", "CSRF token không hợp lệ");
        }
        return session;
    }

    private void ensureAdminTables() throws SQLException {
        try (Connection connection = ConnectDB.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS admin_audit_log ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "admin_id INT NOT NULL, admin_username VARCHAR(100) NOT NULL,"
                    + "action VARCHAR(80) NOT NULL, resource_name VARCHAR(120) NOT NULL,"
                    + "target_id VARCHAR(120) NULL, details_json LONGTEXT NULL,"
                    + "request_ip VARCHAR(64) NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS admin_boss_config ("
                    + "boss_key VARCHAR(100) NOT NULL PRIMARY KEY, config_json LONGTEXT NOT NULL,"
                    + "updated_by INT NOT NULL, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        }
    }

    private void audit(AdminSession session, String action, String resource, String target, JsonElement details) {
        if (session == null) {
            return;
        }
        String sql = "INSERT INTO admin_audit_log (admin_id, admin_username, action, resource_name, target_id, details_json, request_ip) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, session.accountId);
            statement.setString(2, session.username);
            statement.setString(3, action);
            statement.setString(4, resource);
            statement.setString(5, target);
            statement.setString(6, details == null ? null : GSON.toJson(safeAuditData(details)));
            statement.setString(7, session.requestIp);
            statement.executeUpdate();
        } catch (Exception exception) {
            Logger.warn("ADMIN_AUDIT", "Không ghi được audit: " + exception.getMessage());
        }
    }

    private void auditLoginFailure(String username, String requestIp) {
        String sql = "INSERT INTO admin_audit_log (admin_id, admin_username, action, resource_name, target_id, details_json, request_ip) "
                + "VALUES (0, ?, 'LOGIN_FAILED', 'auth', NULL, ?, ?)";
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.substring(0, Math.min(username.length(), 100)));
            statement.setString(2, "{\"reason\":\"invalid_credentials\"}");
            statement.setString(3, requestIp);
            statement.executeUpdate();
        } catch (Exception exception) {
            Logger.warn("ADMIN_AUDIT", "Không ghi được audit đăng nhập thất bại: " + exception.getMessage());
        }
    }

    private Map<String, String> bossOverrides() throws SQLException {
        Map<String, String> result = new HashMap<>();
        try (Connection connection = ConnectDB.getConnection(); Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT boss_key, config_json FROM admin_boss_config")) {
            while (resultSet.next()) {
                result.put(resultSet.getString(1), resultSet.getString(2));
            }
        }
        return result;
    }

    private void applyBossOverrides() throws SQLException {
        Map<String, String> overrides = bossOverrides();
        for (Map.Entry<String, String> entry : overrides.entrySet()) {
            Field field = bossField(entry.getKey());
            if (field == null) {
                continue;
            }
            try {
                BossData parsed = GSON.fromJson(entry.getValue(), BossData.class);
                if (parsed != null) {
                    Object current = field.get(null);
                    if (current instanceof BossData) {
                        applyBossData(field, parsed);
                    }
                }
            } catch (Exception exception) {
                Logger.warn("ADMIN_BOSS", "Bỏ qua override boss " + entry.getKey() + ": " + exception.getMessage());
            }
        }
    }

    private void applyBossData(Field staticField, BossData source) throws IllegalAccessException {
        BossData target = (BossData) staticField.get(null);
        if (target == null) {
            return;
        }
        for (Field field : BossData.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            field.set(target, field.get(source));
        }
    }

    private Field bossField(String key) {
        try {
            Field field = BossesData.class.getField(key);
            return BossData.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()) ? field : null;
        } catch (NoSuchFieldException exception) {
            return null;
        }
    }

    private void registerResources() {
        resources.put("accounts", resource("accounts", "account", "id", false,
                new String[]{"id", "username", "email", "is_admin", "cash", "active", "ban", "isFounder",
                        "isQuanTriVien", "Vip_Point", "tongnap", "vnd", "coin", "danap", "ruby", "create_time",
                        "last_time_login", "last_time_logout", "ip_address", "mocnap", "tong_nap2", "server_login"},
                new String[]{"id", "username", "email"},
                new String[]{"username", "password", "email", "is_admin", "cash", "active", "ban", "isFounder",
                        "isQuanTriVien", "Vip_Point", "tongnap", "vnd", "coin", "danap", "ruby", "mocnap", "tong_nap2", "server_login", "id"}));
        resources.put("players", resource("players", "player", "id", false,
                new String[]{"id", "account_id", "name", "head", "gender", "clan_id", "data_point", "data_inventory",
                        "items_body", "items_bag", "items_box", "pet", "data_task", "data_side_task", "data_clan_task",
                        "data_kol_task", "dataBadges", "LastTimeLoginGame"},
                new String[]{"id", "account_id", "name"},
                new String[]{"account_id", "name", "head", "gender", "clan_id", "data_point", "data_inventory",
                        "items_body", "items_bag", "items_box", "pet", "data_task", "data_side_task", "data_clan_task",
                        "data_kol_task", "dataBadges", "id"}));
        resources.put("giftcodes", resource("giftcodes", "giftcode", "id", false,
                new String[]{"id", "code", "item", "option", "listIdPlayers", "datecreate", "expired", "count_left"},
                new String[]{"id", "code"},
                new String[]{"code", "item", "option", "listIdPlayers", "datecreate", "expired", "count_left", "id"}));
        resources.put("shops", resource("shops", "shop", "id", false,
                new String[]{"id", "npc_id", "tag_name", "type_shop"}, new String[]{"id", "npc_id", "tag_name"},
                new String[]{"npc_id", "tag_name", "type_shop", "id"}));
        resources.put("shop-tabs", resource("shop-tabs", "tab_shop", "id", false,
                new String[]{"id", "shop_id", "NAME"}, new String[]{"id", "shop_id", "NAME"},
                new String[]{"shop_id", "NAME", "id"}));
        resources.put("shop-items", resource("shop-items", "item_shop", "id", false,
                new String[]{"id", "tab_id", "temp_id", "is_new", "is_sell", "type_sell", "cost", "costgold", "icon_spec", "create_time"},
                new String[]{"id", "tab_id", "temp_id"},
                new String[]{"tab_id", "temp_id", "is_new", "is_sell", "type_sell", "cost", "costgold", "icon_spec", "id"}));
        resources.put("shop-options", resource("shop-options", "item_shop_option", "id", false,
                new String[]{"id", "item_shop_id", "option_id", "param"}, new String[]{"id", "item_shop_id", "option_id"},
                new String[]{"item_shop_id", "option_id", "param", "id"}));
        resources.put("topup-rewards", resource("topup-rewards", "moc_nap", "id", false,
                new String[]{"id", "user_id", "amount", "reward_json", "created_at"}, new String[]{"id", "user_id", "amount"},
                new String[]{"user_id", "amount", "reward_json", "id"}));
        resources.put("badges", resource("badges", "data_badges", "id", false,
                new String[]{"id", "idEffect", "idItem", "NAME", "Options"}, new String[]{"id", "idEffect", "idItem", "NAME"},
                new String[]{"idEffect", "idItem", "NAME", "Options", "id"}));
        resources.put("maps", resource("maps", "map_template", "id", false,
                new String[]{"id", "NAME", "type", "planet_id", "bg_type", "tile_id", "bg_id", "zones", "max_player", "genderType", "waypoints", "mobs", "npcs"},
                new String[]{"id", "NAME", "planet_id"},
                new String[]{"NAME", "type", "planet_id", "bg_type", "tile_id", "bg_id", "zones", "max_player", "genderType", "waypoints", "mobs", "npcs", "id"}));
        resources.put("items", resource("items", "item_template", "id", false,
                new String[]{"id", "TYPE", "gender", "NAME", "description", "level", "icon_id", "part", "is_up_to_up", "power_require", "gold", "gold_sell", "gem", "gem_sell", "ruby", "ruby_sell", "head", "body", "leg", "TypeEvent", "isGender"},
                new String[]{"id", "NAME", "TYPE", "icon_id"},
                new String[]{"TYPE", "gender", "NAME", "description", "level", "icon_id", "part", "is_up_to_up", "power_require", "gold", "gold_sell", "gem", "gem_sell", "ruby", "ruby_sell", "head", "body", "leg", "TypeEvent", "isGender", "id"}));
        resources.put("transactions", resource("transactions", "history_transaction", "id", true,
                new String[]{"id", "player_1", "player_2", "item_player_1", "item_player_2", "bag_1_before_tran", "bag_2_before_tran", "bag_1_after_tran", "bag_2_after_tran", "time_tran"},
                new String[]{"id", "player_1", "player_2"}, new String[]{}));
        resources.put("radar", resource("radar", "radar", "id", false,
                new String[]{"id", "iconId", "rank", "max", "type", "mob_id", "body", "name", "info", "options", "aura_id"},
                new String[]{"id", "name", "mob_id"}, new String[]{"iconId", "rank", "max", "type", "mob_id", "body", "name", "info", "options", "aura_id", "id"}));
        resources.put("parts", resource("parts", "part", "id", false,
                new String[]{"id", "TYPE", "DATA"}, new String[]{"id", "TYPE"}, new String[]{"TYPE", "DATA", "id"}));
        resources.put("head-avatars", resource("head-avatars", "head_avatar", "head_id", false,
                new String[]{"head_id", "avatar_id"}, new String[]{"head_id", "avatar_id"}, new String[]{"head_id", "avatar_id"}));
        resources.put("head-frames", resource("head-frames", "array_head_2_frames", "id", false,
                new String[]{"id", "data"}, new String[]{"id"}, new String[]{"data", "id"}));
        resources.put("drops", resource("drops", "drop_item", "id", false,
                new String[]{"id", "active", "mob_id", "map_id", "item_id", "quantity", "rate_num", "rate_den", "family", "note", "options", "conditions", "created_at", "updated_at"},
                new String[]{"id", "mob_id", "map_id", "item_id", "note"}, new String[]{"active", "mob_id", "map_id", "item_id", "quantity", "rate_num", "rate_den", "family", "note", "options", "conditions", "id"}));
        resources.put("security", resource("security", "blockip_list", "id", false,
                new String[]{"id", "blocker_ip", "block_reason", "blocked_at"}, new String[]{"id", "blocker_ip", "block_reason"},
                new String[]{"blocker_ip", "block_reason", "id"}));
    }

    private ResourceDefinition definitionFor(String name, String tableOverride) {
        if ("topup-rewards".equals(name) && tableOverride != null && !tableOverride.isBlank()) {
            if (!Set.of("moc_nap", "moc_nap_top", "moc_san_boss", "moc_suc_manh", "moc_suc_manh_top").contains(tableOverride)) {
                throw ApiException.badRequest("INVALID_TOPUP_TABLE", "Bảng topup không được allowlist");
            }
            return topupDefinition(tableOverride);
        }
        return resources.get(name);
    }

    private ResourceDefinition topupDefinition(String table) {
        if ("moc_nap".equals(table)) {
            return resources.get("topup-rewards");
        }
        return resource("topup-rewards", table, "id", false,
                new String[]{"id", "user_id", "amount", "reward_json", "created_at"},
                new String[]{"id", "user_id", "amount"}, new String[]{"user_id", "amount", "reward_json", "id"});
    }

    private void ensureTableAvailable(String table) throws SQLException {
        try (Connection connection = ConnectDB.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(null, null, table, new String[]{"TABLE"})) {
                if (!tables.next()) {
                    throw new ApiException(503, "FEATURE_UNAVAILABLE", "Bảng " + table + " chưa tồn tại");
                }
            }
        }
    }

    private String aliasResourcePath(String path) {
        if (path.startsWith("/items/templates")) return "/resources/items" + path.substring("/items/templates".length());
        if (path.startsWith("/accounts")) return "/resources/accounts" + path.substring("/accounts".length());
        if (path.startsWith("/players")) return "/resources/players" + path.substring("/players".length());
        if (path.startsWith("/giftcodes")) return "/resources/giftcodes" + path.substring("/giftcodes".length());
        if (path.startsWith("/shops")) return "/resources/shops" + path.substring("/shops".length());
        if (path.startsWith("/shop-tabs")) return "/resources/shop-tabs" + path.substring("/shop-tabs".length());
        if (path.startsWith("/shop-items")) return "/resources/shop-items" + path.substring("/shop-items".length());
        if (path.startsWith("/shop-options")) return "/resources/shop-options" + path.substring("/shop-options".length());
        if (path.startsWith("/topup-rewards")) return "/resources/topup-rewards" + path.substring("/topup-rewards".length());
        if (path.startsWith("/badges")) return "/resources/badges" + path.substring("/badges".length());
        if (path.startsWith("/maps")) return "/resources/maps" + path.substring("/maps".length());
        if (path.startsWith("/transactions")) return "/resources/transactions" + path.substring("/transactions".length());
        if (path.startsWith("/radar")) return "/resources/radar" + path.substring("/radar".length());
        if (path.startsWith("/parts")) return "/resources/parts" + path.substring("/parts".length());
        if (path.startsWith("/head-avatars")) return "/resources/head-avatars" + path.substring("/head-avatars".length());
        if (path.startsWith("/head-frames")) return "/resources/head-frames" + path.substring("/head-frames".length());
        if (path.startsWith("/drops")) return "/resources/drops" + path.substring("/drops".length());
        return path;
    }

    private LookupDefinition lookupDefinition(String kind) {
        return switch (kind) {
            case "items" -> lookup("item_template", Arrays.asList("id", "NAME", "icon_id", "TYPE", "gender",
                    "description", "level", "part", "is_up_to_up", "power_require", "gold", "gold_sell",
                    "gem", "gem_sell", "ruby", "ruby_sell", "head", "body", "leg", "TypeEvent", "isGender"),
                    Arrays.asList("id", "NAME"), "id", "NAME", "icon_id");
            case "options" -> lookup("item_option_template", Arrays.asList("id", "NAME", "type"), Arrays.asList("id", "NAME"), "id", "NAME", null);
            case "maps" -> lookup("map_template", Arrays.asList("id", "NAME", "planet_id"), Arrays.asList("id", "NAME"), "id", "NAME", null);
            case "mobs" -> lookup("mob_template", Arrays.asList("id", "NAME", "hp"), Arrays.asList("id", "NAME"), "id", "NAME", null);
            case "npcs" -> lookup("npc_template", Arrays.asList("id", "NAME", "avatar"), Arrays.asList("id", "NAME"), "id", "NAME", "avatar");
            case "parts" -> lookup("part", Arrays.asList("id", "TYPE", "DATA"), Arrays.asList("id", "TYPE"), "id", "TYPE", "DATA");
            case "skill", "skills" -> lookup("skill_template", Arrays.asList("nclass_id", "id", "NAME", "icon_id", "slot"), Arrays.asList("id", "NAME"), "id", "NAME", "icon_id");
            case "tasks", "main-tasks" -> lookup("task_main_template", Arrays.asList("id", "NAME", "detail"), Arrays.asList("id", "NAME", "detail"), "id", "NAME", null);
            case "side-tasks" -> lookup("side_task_template", Arrays.asList("id", "NAME", "max_count_lv1", "max_count_lv2", "max_count_lv3", "max_count_lv4", "max_count_lv5"), Arrays.asList("id", "NAME"), "id", "NAME", null);
            case "clan-tasks" -> lookup("clan_task_template", Arrays.asList("id", "NAME", "max_count_lv1", "max_count_lv2", "max_count_lv3", "max_count_lv4", "max_count_lv5"), Arrays.asList("id", "NAME"), "id", "NAME", null);
            case "kol-tasks" -> lookup("task_kol_template", Arrays.asList("id", "info", "max_count"), Arrays.asList("id", "info"), "id", "info", null);
            case "event-tasks" -> lookup("task_event_template", Arrays.asList("id", "NAME", "max_count_lv1", "max_count_lv2", "max_count_lv3", "max_count_lv4", "max_count_lv5"), Arrays.asList("id", "NAME"), "id", "NAME", null);
            case "events" -> lookup("event_server", Arrays.asList("id_event", "name_event", "Point"), Arrays.asList("id_event", "name_event"), "id_event", "name_event", null);
            case "accounts" -> lookup("account", Arrays.asList("id", "username", "is_admin", "active", "ban"), Arrays.asList("id", "username"), "id", "username", null);
            case "players" -> lookup("player", Arrays.asList("id", "name", "account_id", "head"), Arrays.asList("id", "name", "account_id"), "id", "name", null);
            case "clans" -> lookup("clan", Arrays.asList("id", "NAME", "NAME_2", "LEVEL", "power_point", "max_member", "clan_point", "img_id"),
                    Arrays.asList("id", "NAME", "NAME_2"), "id", "NAME", "img_id");
            case "shops" -> lookup("shop", Arrays.asList("id", "npc_id", "tag_name", "type_shop"), Arrays.asList("id", "npc_id", "tag_name"), "id", "tag_name", null);
            case "shop-tabs" -> lookup("tab_shop", Arrays.asList("id", "shop_id", "NAME"), Arrays.asList("id", "shop_id", "NAME"), "id", "NAME", null);
            case "shop-items" -> lookup("item_shop", Arrays.asList("id", "tab_id", "temp_id", "icon_spec", "is_sell"), Arrays.asList("id", "tab_id", "temp_id"), "id", "temp_id", "icon_spec");
            case "shop-options" -> lookup("item_shop_option", Arrays.asList("id", "item_shop_id", "option_id", "param"), Arrays.asList("id", "item_shop_id", "option_id"), "id", "option_id", null);
            default -> null;
        };
    }

    private LookupDefinition lookup(String table, List<String> columns, List<String> searchColumns,
                                    String idColumn, String labelColumn, String iconColumn) {
        return new LookupDefinition(table, columns, searchColumns, idColumn, labelColumn, iconColumn);
    }

    private JsonObject requestData(JsonObject envelope) {
        return envelope.has("data") && envelope.get("data").isJsonObject()
                ? envelope.getAsJsonObject("data") : envelope;
    }

    private void validateData(ResourceDefinition definition, JsonObject body) {
        for (Map.Entry<String, JsonElement> entry : body.entrySet()) {
            String column = entry.getKey();
            if ("version".equals(column)) {
                continue;
            }
            if (!definition.writable.contains(column)) {
                throw ApiException.badRequest("FIELD_NOT_ALLOWED", "Trường không được phép: " + column);
            }
            if (looksLikeJsonColumn(column) && entry.getValue().isJsonPrimitive()
                    && entry.getValue().getAsJsonPrimitive().isString()) {
                String value = entry.getValue().getAsString();
                if (!value.isBlank()) {
                    try {
                        JSON_PARSER.parse(value);
                    } catch (Exception exception) {
                        throw ApiException.badRequest("INVALID_JSON", "JSON không hợp lệ ở trường " + column);
                    }
                }
            }
        }
    }

    private boolean looksLikeJsonColumn(String column) {
        String name = column.toLowerCase(Locale.ROOT);
        return name.contains("data") || name.contains("items") || name.contains("options")
                || name.contains("conditions") || name.contains("reward") || name.contains("waypoint")
                || name.equals("pet") || name.equals("email") || name.equals("body") || name.equals("npcs")
                || name.equals("mobs") || name.equals("item") || name.equals("option")
                || name.equals("listidplayers") || name.equals("point") || name.equals("skills");
    }

    private List<String> writableColumns(ResourceDefinition definition, JsonObject body) {
        requireWritable(definition);
        List<String> columns = new ArrayList<>();
        for (String column : definition.writable) {
            if (body.has(column) && !"version".equals(column)) {
                columns.add(column);
            }
        }
        for (String column : body.keySet()) {
            if (!"version".equals(column) && !definition.writable.contains(column)) {
                throw ApiException.badRequest("FIELD_NOT_ALLOWED", "Trường không được phép: " + column);
            }
        }
        return columns;
    }

    private void requireWritable(ResourceDefinition definition) {
        if (definition == null || definition.readOnly) {
            throw new ApiException(403, "READ_ONLY_RESOURCE", "Resource chỉ đọc");
        }
    }

    private void assertVersion(JsonObject envelope, JsonObject current) {
        if (envelope == null || !envelope.has("version") || envelope.get("version").isJsonNull()
                || envelope.get("version").getAsString().isBlank()) {
            throw new ApiException(400, "VERSION_REQUIRED", "Thiếu version của bản ghi; hãy tải lại dữ liệu");
        }
        String expected = envelope.get("version").getAsString();
        String actual = current.has("version") ? current.get("version").getAsString() : "";
        if (!expected.equals(actual)) {
            throw new ApiException(409, "VERSION_CONFLICT", "Dữ liệu đã được thay đổi, hãy tải lại");
        }
    }

    private int bind(PreparedStatement statement, List<Object> values) throws SQLException {
        int index = 1;
        for (Object value : values) {
            statement.setObject(index++, value);
        }
        return index;
    }

    private int bindJsonValues(PreparedStatement statement, List<String> columns, JsonObject body) throws SQLException {
        int index = 1;
        for (String column : columns) {
            JsonElement value = body.get(column);
            if (value == null || value.isJsonNull()) {
                statement.setObject(index++, null);
            } else if (value.isJsonPrimitive()) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isBoolean()) statement.setBoolean(index++, primitive.getAsBoolean());
                else if (primitive.isNumber()) statement.setBigDecimal(index++, primitive.getAsBigDecimal());
                else statement.setString(index++, primitive.getAsString());
            } else {
                statement.setString(index++, GSON.toJson(value));
            }
        }
        return index;
    }

    private JsonObject currentEventFlags() {
        JsonObject events = new JsonObject();
        events.addProperty("lunarNewYear", EventManager.LUNNAR_NEW_YEAR);
        events.addProperty("christmas", EventManager.CHRISTMAS);
        events.addProperty("vuLan", EventManager.VU_LAN_FESTIVAL);
        events.addProperty("halloween", EventManager.HALLOWEEN);
        events.addProperty("womenDay", EventManager.INTERNATIONAL_WOMANS_DAY);
        events.addProperty("trungThu", EventManager.TRUNG_THU);
        events.addProperty("hungVuong", EventManager.HUNG_VUONG);
        events.addProperty("blackFriday", EventManager.BLACK_FRIDAY);
        events.addProperty("valentine", EventManager.VALENTINE_DAY);
        events.addProperty("day20October", EventManager.DAY_20_10);
        events.addProperty("topUp", EventManager.TOP_UP);
        return events;
    }

    private Set<Integer> readActiveEventIds() {
        Path path = Paths.get("data", "config", "active_event.txt");
        if (!Files.isRegularFile(path)) {
            return new LinkedHashSet<>();
        }
        try {
            Set<Integer> result = new LinkedHashSet<>();
            for (String item : Files.readString(path).trim().split("-")) {
                if (!item.isBlank()) result.add(Integer.parseInt(item));
            }
            return result;
        } catch (Exception exception) {
            return new LinkedHashSet<>();
        }
    }

    private void writeActiveEventIds(Set<Integer> ids) throws IOException {
        Path directory = Paths.get("data", "config");
        Files.createDirectories(directory);
        Path temporary = directory.resolve("active_event.txt.tmp");
        String value = String.join("-", ids.stream().map(String::valueOf).toList());
        Files.writeString(temporary, value, StandardCharsets.UTF_8);
        Files.move(temporary, directory.resolve("active_event.txt"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private void rescheduleAntiDdos() {
        if (antiDdosTask != null) {
            antiDdosTask.cancel(false);
            antiDdosTask = null;
        }
        if (antiDdosRunning && antiDdosAutoScan) {
            antiDdosTask = antiDdosScheduler.scheduleAtFixedRate(this::antiDdosScan, 0, antiDdosScanSeconds, TimeUnit.SECONDS);
        }
    }

    private void stopAntiDdos() {
        antiDdosRunning = false;
        antiDdosAutoScan = false;
        if (antiDdosTask != null) {
            antiDdosTask.cancel(false);
            antiDdosTask = null;
        }
    }

    private void antiDdosScan() {
        if (!antiDdosRunning || !antiDdosAutoScan) return;
        try {
            for (Map.Entry entry : ((Map<?, ?>) ServerManager.CLIENTS).entrySet()) {
                String ip = String.valueOf(entry.getKey());
                int connections = Integer.parseInt(String.valueOf(entry.getValue()));
                if (connections > antiDdosLimit && SAFE_IP.matcher(ip).matches()) {
                    blockIpInternal(ip, "Auto scan: " + connections + " connections");
                }
            }
        } catch (Exception exception) {
            Logger.warn("ADMIN_DDOS", "Scan lỗi: " + exception.getMessage());
        }
    }

    private void blockIpInternal(String ip, String reason) {
        try (Connection connection = ConnectDB.getConnection(); PreparedStatement check = connection.prepareStatement(
                "SELECT 1 FROM blockip_list WHERE blocker_ip=? LIMIT 1")) {
            check.setString(1, ip);
            try (ResultSet resultSet = check.executeQuery()) {
                if (!resultSet.next()) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO blockip_list (blocker_ip, block_reason, blocked_at) VALUES (?, ?, NOW())")) {
                        insert.setString(1, ip);
                        insert.setString(2, reason);
                        insert.executeUpdate();
                    }
                    firewallBlock(ip);
                }
            }
        } catch (Exception exception) {
            Logger.warn("ADMIN_DDOS", "Không block được " + ip + ": " + exception.getMessage());
        }
    }

    private void syncFirewallFromDatabase() throws SQLException {
        if (!config.firewallEnabled) return;
        try (Connection connection = ConnectDB.getConnection(); Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT blocker_ip FROM blockip_list")) {
            while (resultSet.next()) {
                firewallBlock(resultSet.getString(1));
            }
        }
    }

    private void firewallBlock(String ip) {
        if (!config.firewallEnabled) return;
        if (!SAFE_IP.matcher(ip).matches()) return;
        runFirewall(commandForIp(ip, true));
    }

    private void firewallUnblock(String ip) {
        if (!config.firewallEnabled) return;
        if (!SAFE_IP.matcher(ip).matches()) return;
        runFirewall(commandForIp(ip, false));
    }

    private void firewallClear() {
        if (!config.firewallEnabled) return;
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            runFirewall(Arrays.asList("netsh", "advfirewall", "firewall", "delete", "rule", "name=NRO Admin Block"));
        } else {
            // Individual rules are deliberately not mass-deleted by the API.
            Logger.warn("ADMIN_FIREWALL", "Đã xóa IP trong DB; hãy sync firewall theo rule của hệ điều hành");
        }
    }

    private void firewallPort(int port, boolean block) {
        if (!config.firewallEnabled || port < 1 || port > 65535) return;
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            List<String> command = Arrays.asList("netsh", "advfirewall", "firewall", block ? "add" : "delete", "rule",
                    "name=NRO Admin Lockdown " + port, "dir=in", "action=block", "protocol=TCP", "localport=" + port);
            runFirewall(command);
        } else {
            Logger.warn("ADMIN_FIREWALL", "Lockdown firewall Linux cần cấu hình operator riêng cho port " + port);
        }
    }

    private List<String> commandForIp(String ip, boolean block) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return Arrays.asList("netsh", "advfirewall", "firewall", block ? "add" : "delete", "rule",
                    "name=NRO Admin Block", "dir=in", "action=block", "remoteip=" + ip);
        }
        String firewallBinary = ip.indexOf(':') >= 0 ? "ip6tables" : "iptables";
        if (block) {
            return Arrays.asList(firewallBinary, "-I", "INPUT", "-s", ip, "-j", "DROP");
        }
        return Arrays.asList(firewallBinary, "-D", "INPUT", "-s", ip, "-j", "DROP");
    }

    private void runFirewall(List<String> command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly();
            }
        } catch (Exception exception) {
            Logger.warn("ADMIN_FIREWALL", "Firewall command thất bại: " + exception.getMessage());
        }
    }

    private int antiDdosGamePort() {
        String value = runtimeSettings.get("antiDdos.gamePort");
        return value == null ? ServerManager.PORT : positiveInt(value, ServerManager.PORT, 1, 65535);
    }

    private JsonObject objectFromSettings(String prefix) {
        JsonObject result = new JsonObject();
        for (Map.Entry<String, String> entry : runtimeSettings.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                String key = entry.getKey().substring(prefix.length());
                try {
                    result.add(key, JSON_PARSER.parse(entry.getValue()));
                } catch (Exception exception) {
                    result.addProperty(key, entry.getValue());
                }
            }
        }
        return result;
    }

    private int safeOnlinePlayers() {
        try {
            return Client.gI().getPlayersSnapshot().size();
        } catch (Exception exception) {
            return 0;
        }
    }

    private int safeSessionCount() {
        try {
            return SessionManager.gI().getNumSession();
        } catch (Exception exception) {
            return 0;
        }
    }

    private int[] safeBossStatus() {
        try {
            return BossManager.gI().getBossStatusCounts();
        } catch (Exception exception) {
            return new int[]{0, 0, 0};
        }
    }

    private double cpuLoad() {
        try {
            com.sun.management.OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean)
                    ManagementFactory.getOperatingSystemMXBean();
            return Math.max(0, bean.getCpuLoad() * 100.0);
        } catch (Exception exception) {
            return 0;
        }
    }

    private void cleanExpiredSessions() {
        for (Map.Entry<String, AdminSession> entry : sessions.entrySet()) {
            if (entry.getValue().isExpired()) sessions.remove(entry.getKey());
        }
        for (Map.Entry<String, LoginBucket> entry : loginBuckets.entrySet()) {
            entry.getValue().cleanup();
        }
    }

    private String buildCookie(String sessionId) {
        return SESSION_COOKIE + "=" + sessionId + "; Path=/; HttpOnly; SameSite=Lax; Max-Age="
                + TimeUnit.MILLISECONDS.toSeconds(SESSION_MAX_MS) + (config.secureCookie ? "; Secure" : "");
    }

    private String buildExpiredCookie() {
        return SESSION_COOKIE + "=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0" + (config.secureCookie ? "; Secure" : "");
    }

    private String cookie(HttpExchange exchange, String name) {
        String header = exchange.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (String item : header.split(";")) {
            String[] pair = item.trim().split("=", 2);
            if (pair.length == 2 && name.equals(pair[0])) return pair[1];
        }
        return null;
    }

    private String clientIp(HttpExchange exchange) {
        String forwarded = exchange.getRequestHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        InetSocketAddress address = exchange.getRemoteAddress();
        return address == null || address.getAddress() == null ? "unknown" : address.getAddress().getHostAddress();
    }

    private JsonObject readJsonObject(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        if (bytes.length > MAX_BODY_BYTES) {
            throw new ApiException(413, "BODY_TOO_LARGE", "Payload quá lớn");
        }
        if (bytes.length == 0) return new JsonObject();
        try {
            JsonElement element = JSON_PARSER.parse(new String(bytes, StandardCharsets.UTF_8));
            if (!element.isJsonObject()) throw new ApiException(400, "JSON_OBJECT_REQUIRED", "Payload phải là JSON object");
            return element.getAsJsonObject();
        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ApiException(400, "INVALID_JSON", "JSON không hợp lệ");
        }
    }

    private void respondSuccess(HttpExchange exchange, int status, JsonElement data, JsonObject meta) throws IOException {
        JsonObject envelope = new JsonObject();
        envelope.add("data", data == null ? JsonNull.INSTANCE : data);
        if (meta != null) envelope.add("meta", meta);
        respondJson(exchange, status, envelope);
    }

    private void respondError(HttpExchange exchange, int status, String code, String message, JsonObject fields) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        if (fields != null) error.add("fields", fields);
        JsonObject envelope = new JsonObject();
        envelope.add("error", error);
        envelope.addProperty("requestId", UUID.randomUUID().toString());
        respondJson(exchange, status, envelope);
    }

    private void respondJson(HttpExchange exchange, int status, JsonObject payload) throws IOException {
        byte[] bytes = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static void requireMethod(HttpExchange exchange, String expected) {
        if (!expected.equalsIgnoreCase(exchange.getRequestMethod())) {
            throw ApiException.methodNotAllowed("METHOD_NOT_ALLOWED", "Phương thức không được hỗ trợ");
        }
    }

    private static boolean isReadOnlyMethod(String method) {
        return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method);
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) return "/";
        String value = path.startsWith("/api") ? path.substring(4) : path;
        if (value.isEmpty()) value = "/";
        return value.endsWith("/") && value.length() > 1 ? value.substring(0, value.length() - 1) : value;
    }

    private static List<String> segments(String path) {
        List<String> result = new ArrayList<>();
        for (String value : path.split("/")) {
            if (!value.isBlank()) result.add(decode(value));
        }
        return result;
    }

    private static Map<String, String> query(HttpExchange exchange) {
        return query(exchange.getRequestURI());
    }

    private static Map<String, String> query(URI uri) {
        Map<String, String> result = new LinkedHashMap<>();
        String raw = uri.getRawQuery();
        if (raw == null || raw.isBlank()) return result;
        for (String pair : raw.split("&")) {
            String[] values = pair.split("=", 2);
            String key = decode(values[0]);
            String value = values.length == 2 ? decode(values[1]) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            return value;
        }
    }

    private static JsonObject objectOf(Object... values) {
        JsonObject result = new JsonObject();
        for (int index = 0; index + 1 < values.length; index += 2) {
            String key = String.valueOf(values[index]);
            Object value = values[index + 1];
            if (value == null) result.add(key, JsonNull.INSTANCE);
            else if (value instanceof JsonElement element) result.add(key, element);
            else if (value instanceof Boolean bool) result.addProperty(key, bool);
            else if (value instanceof Number number) result.addProperty(key, number);
            else result.addProperty(key, String.valueOf(value));
        }
        return result;
    }

    private static JsonObject messageData(String message) {
        return objectOf("message", message);
    }

    private static JsonArray stringArray(List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) array.add(value);
        return array;
    }

    private static JsonArray intArray(Iterable<Integer> values) {
        JsonArray array = new JsonArray();
        for (Integer value : values) array.add(value);
        return array;
    }

    private static JsonElement jsonValue(Object value) {
        if (value == null) return JsonNull.INSTANCE;
        if (value instanceof Timestamp timestamp) return new JsonPrimitive(timestamp.toString());
        if (value instanceof java.sql.Date date) return new JsonPrimitive(date.toString());
        if (value instanceof java.util.Date date) return new JsonPrimitive(date.toInstant().toString());
        if (value instanceof byte[] bytes) return new JsonPrimitive(Base64.getEncoder().encodeToString(bytes));
        if (value instanceof Boolean bool) return new JsonPrimitive(bool);
        if (value instanceof Number number) return new JsonPrimitive(number);
        return new JsonPrimitive(String.valueOf(value));
    }

    private static String rowVersion(JsonObject row) {
        JsonObject copy = row.deepCopy();
        copy.remove("version");
        return sha256(GSON.toJson(copy));
    }

    private static String sha256(String value) {
        if (value == null) return "";
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte item : digest) result.append(String.format("%02x", item));
            return result.toString();
        } catch (Exception exception) {
            return Integer.toHexString(value.hashCode());
        }
    }

    private static String textValue(JsonElement element) {
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    private static String requiredText(JsonObject body, String key, int maxLength) {
        String value = body.has(key) && !body.get(key).isJsonNull() ? body.get(key).getAsString().trim() : "";
        if (value.isEmpty() || value.length() > maxLength) {
            throw ApiException.badRequest("INVALID_" + key.toUpperCase(Locale.ROOT), "Trường " + key + " không hợp lệ");
        }
        return value;
    }

    private static int requiredInt(JsonObject body, String key, int min, int max) {
        if (!body.has(key)) throw ApiException.badRequest("INVALID_" + key.toUpperCase(Locale.ROOT), "Thiếu " + key);
        return positiveInt(body.get(key).getAsString(), 0, min, max);
    }

    private static int positiveInt(String value, int fallback, int min, int max) {
        if (value == null || value.isBlank()) return fallback;
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < min || parsed > max) throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException exception) {
            throw ApiException.badRequest("INVALID_NUMBER", "Giá trị số không hợp lệ");
        }
    }

    private static boolean booleanValue(JsonObject body, String key, boolean fallback) {
        return body.has(key) && !body.get(key).isJsonNull() ? body.get(key).getAsBoolean() : fallback;
    }

    private static String joinAssignments(List<String> columns) {
        List<String> assignments = new ArrayList<>();
        for (String column : columns) assignments.add(column + " = ?");
        return String.join(", ", assignments);
    }

    private static String joinLike(List<String> columns) {
        List<String> result = new ArrayList<>();
        for (String column : columns) result.add("CAST(" + column + " AS CHAR) LIKE ?");
        return "(" + String.join(" OR ", result) + ")";
    }

    private static boolean looksLikeSensitive(String key) {
        String lower = key.toLowerCase(Locale.ROOT);
        return lower.contains("password") || lower.contains("pass") || lower.contains("token");
    }

    private static JsonElement safeAuditData(JsonElement source) {
        if (source == null || source.isJsonNull()) return JsonNull.INSTANCE;
        if (source.isJsonArray()) {
            JsonArray array = new JsonArray();
            for (JsonElement item : source.getAsJsonArray()) array.add(safeAuditData(item));
            return array;
        }
        if (!source.isJsonObject()) return source.deepCopy();
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : source.getAsJsonObject().entrySet()) {
            result.add(entry.getKey(), looksLikeSensitive(entry.getKey()) ? new JsonPrimitive("[REDACTED]") : safeAuditData(entry.getValue()));
        }
        return result;
    }

    private static void validateIp(String ip) {
        if (!SAFE_IP.matcher(ip).matches()) {
            throw ApiException.badRequest("INVALID_IP", "IP không hợp lệ");
        }
        try {
            if (ip.indexOf(':') >= 0) {
                if (!InetAddress.getByName(ip).getHostAddress().contains(":")) {
                    throw new IllegalArgumentException("not ipv6");
                }
            } else {
                String[] octets = ip.split("\\.", -1);
                if (octets.length != 4) throw new IllegalArgumentException("not ipv4");
                for (String octet : octets) {
                    int value = Integer.parseInt(octet);
                    if (value < 0 || value > 255) throw new IllegalArgumentException("invalid octet");
                }
            }
        } catch (Exception exception) {
            throw ApiException.badRequest("INVALID_IP", "IP không hợp lệ");
        }
    }

    private static final class ResourceDefinition {
        private final String name;
        private final String table;
        private final String idColumn;
        private final boolean readOnly;
        private final List<String> columns;
        private final List<String> searchColumns;
        private final Set<String> writable;

        private ResourceDefinition(String name, String table, String idColumn, boolean readOnly,
                                   List<String> columns, List<String> searchColumns, Set<String> writable) {
            this.name = name;
            this.table = table;
            this.idColumn = idColumn;
            this.readOnly = readOnly;
            this.columns = columns;
            this.searchColumns = searchColumns;
            this.writable = writable;
        }
    }

    private ResourceDefinition resource(String name, String table, String idColumn, boolean readOnly,
                                        String[] columns, String[] searchColumns, String[] writable) {
        return new ResourceDefinition(name, table, idColumn, readOnly,
                List.of(columns), List.of(searchColumns), new LinkedHashSet<>(List.of(writable)));
    }

    private record LookupDefinition(String table, List<String> columns, List<String> searchColumns,
                                    String idColumn, String labelColumn, String iconColumn) {}

    private record EventDefinition(int id, String name) {}

    private record AccountPrincipal(int id, String username) {
        private JsonObject toJson() {
            return objectOf("id", id, "username", username, "isAdmin", true);
        }
    }

    private static final class AdminSession {
        private final String id;
        private final String csrfToken;
        private final int accountId;
        private final String username;
        private final String requestIp;
        private final long createdAt = System.currentTimeMillis();
        private volatile long lastAccess = createdAt;

        private AdminSession(String id, String csrfToken, int accountId, String username, String requestIp) {
            this.id = id;
            this.csrfToken = csrfToken;
            this.accountId = accountId;
            this.username = username;
            this.requestIp = requestIp;
        }

        private void touch() {
            lastAccess = System.currentTimeMillis();
        }

        private boolean isExpired() {
            long now = System.currentTimeMillis();
            return now - createdAt > SESSION_MAX_MS || now - lastAccess > SESSION_IDLE_MS;
        }
    }

    private static final class LoginBucket {
        private final Deque<Long> failures = new ArrayDeque<>();

        private synchronized boolean isLimited() {
            cleanup();
            return failures.size() >= LOGIN_LIMIT;
        }

        private synchronized void recordFailure() {
            cleanup();
            failures.addLast(System.currentTimeMillis());
        }

        private synchronized void clear() {
            failures.clear();
        }

        private synchronized void cleanup() {
            long cutoff = System.currentTimeMillis() - LOGIN_WINDOW_MS;
            while (!failures.isEmpty() && failures.peekFirst() < cutoff) failures.removeFirst();
        }
    }

    private static final class AdminConfig {
        private final String bind;
        private final int port;
        private final int workerThreads;
        private final boolean secureCookie;
        private final String corsOrigin;
        private final boolean firewallEnabled;
        private final Path iconRoot;

        private AdminConfig(String bind, int port, int workerThreads, boolean secureCookie, String corsOrigin,
                            boolean firewallEnabled, Path iconRoot) {
            this.bind = bind;
            this.port = port;
            this.workerThreads = workerThreads;
            this.secureCookie = secureCookie;
            this.corsOrigin = corsOrigin;
            this.firewallEnabled = firewallEnabled;
            this.iconRoot = iconRoot;
        }

        private static AdminConfig defaults() {
            return new AdminConfig("127.0.0.1", 18080, 16, false, "http://localhost:5173", false,
                    Paths.get("data", "icon").toAbsolutePath().normalize());
        }

        private static AdminConfig load() {
            AdminConfig defaults = defaults();
            java.util.Properties properties = new java.util.Properties();
            try (java.io.FileInputStream input = new java.io.FileInputStream("data/config/data_base.properties")) {
                properties.load(input);
            } catch (Exception ignored) {
            }
            String bind = properties.getProperty("admin.api.bind", System.getProperty("admin.api.bind", defaults.bind));
            int port = parseProperty(properties, "admin.api.port", "admin.api.port", defaults.port, 1024, 65535);
            int threads = parseProperty(properties, "admin.api.threads", "admin.api.threads", defaults.workerThreads, 4, 64);
            boolean secure = Boolean.parseBoolean(properties.getProperty("admin.cookie.secure", System.getProperty("admin.cookie.secure", String.valueOf(defaults.secureCookie))));
            String cors = properties.getProperty("admin.api.cors", System.getProperty("admin.api.cors", defaults.corsOrigin));
            boolean firewall = Boolean.parseBoolean(properties.getProperty("admin.firewall.enabled", System.getProperty("admin.firewall.enabled", String.valueOf(defaults.firewallEnabled))));
            String iconDirectory = properties.getProperty("admin.assets.dir",
                    System.getProperty("admin.assets.dir", defaults.iconRoot.toString()));
            Path iconRoot = resolveIconRoot(iconDirectory, defaults.iconRoot);
            return new AdminConfig(bind, port, threads, secure, cors, firewall, iconRoot);
        }

        private static Path resolveIconRoot(String value, Path fallback) {
            if (value == null || value.isBlank()) return fallback;
            try {
                return Paths.get(value.trim()).toAbsolutePath().normalize();
            } catch (RuntimeException exception) {
                return fallback;
            }
        }

        private static int parseProperty(java.util.Properties properties, String key, String systemKey, int fallback, int min, int max) {
            try {
                int value = Integer.parseInt(properties.getProperty(key, System.getProperty(systemKey, String.valueOf(fallback))));
                return value >= min && value <= max ? value : fallback;
            } catch (Exception exception) {
                return fallback;
            }
        }
    }

    private static final class ApiException extends RuntimeException {
        private final int status;
        private final String code;

        private ApiException(int status, String code, String message) {
            super(message);
            this.status = status;
            this.code = code;
        }

        private static ApiException badRequest(String code, String message) { return new ApiException(400, code, message); }
        private static ApiException notFound(String code, String message) { return new ApiException(404, code, message); }
        private static ApiException methodNotAllowed(String code, String message) { return new ApiException(405, code, message); }
    }
}
