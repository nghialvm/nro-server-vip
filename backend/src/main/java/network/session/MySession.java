
package network.session;

import java.net.Socket;
import nro.player.Player;
import nro.server.Controller;
import Data.DataGame;
import jbcd.data.GodGK;
import models.Item.Item;
import nro.server.Client;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.services.Service;
import Utils.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nro.server.AntiLogin;
import Utils.TimeUtil;
import Utils.Util;
import java.io.IOException;
import jbcd.data.DatabaseUpdater;
import network.io.Message;

public class MySession extends Session {

    private static final Map<String, AntiLogin> ANTILOGIN = new HashMap<>();
    private static final Map<String, Object> LOGIN_LOCKS = new HashMap<>();

    public Player player;

    public byte timeWait = 100;

    public boolean connected;
    public boolean sentKey;

    public static final byte[] KEYS = {0};
    public byte curR, curW;

    public String ipAddress;
    public boolean isQuanTriVien;
    public boolean isFounder;
    public boolean isJail;
    public int userId;
    public String uu;
    public String pp;
    public int goldBar;
    public int ruby;

    public int Vip_Point;
    // vé tháng vé tuần
    public int vethang;
    public int vetuan;
    public long vethangExpire;
    public long vetuanExpire;
    public int typeClient;
    public byte zoomLevel;

    public long lastTimeLogout;
    public boolean joinedGame;

    public long lastTimeReadMessage;

    public boolean actived;

    public List<Item> itemsReward;
    public String dataReward;
    public boolean is_gift_box;
    public double bdPlayer;

    public int version;
    public int vnd;
    public int coin;
    public int Bar;
    public long timeout;
    public int tongnap;

    public int accountAgeDays;
    public long timeCreateAcount;

    public int getIdTask;

    public int danap;

    public boolean finishUpdate;
    public int[] mocnap = new int[]{0, 0, 0, 0, 0};
    public int tongnap2;

    public MySession(Socket socket) {
        super(socket);
        ipAddress = socket.getInetAddress().getHostAddress();
    }

    private static Object getLoginLock(String username) {
        if (username == null) {
            username = "";
        }
        username = username.trim().toLowerCase();
        synchronized (LOGIN_LOCKS) {
            Object lock = LOGIN_LOCKS.get(username);
            if (lock == null) {
                lock = new Object();
                LOGIN_LOCKS.put(username, lock);
            }
            return lock;
        }
    }

    @Override
    public void sendKey() throws Exception {
        super.sendKey();
        this.startSend();
    }

    public void sendSessionKey() {
        Message msg = new Message(-27);
        try {
            msg.writer().writeByte(KEYS.length);
            msg.writer().writeByte(KEYS[0]);
            for (int i = 1; i < KEYS.length; i++) {
                msg.writer().writeByte(KEYS[i] ^ KEYS[i - 1]);
            }
            this.sendMessage(msg);
            msg.cleanup();
            sentKey = true;
        } catch (IOException e) {
        }
    }

    public void login(String username, String password) {
        if (username == null || password == null) {
            Service.gI().sendThongBaoOK(this, "Thông tin tài khoản hoặc mật khẩu không chính xác");
            Service.gI().sendLoginFail(this, false);
            return;
        }

        username = username.trim();
        password = password.trim();

        AntiLogin al = ANTILOGIN.get(this.ipAddress);
        if (al == null) {
            al = new AntiLogin();
            ANTILOGIN.put(this.ipAddress, al);
        }
        if (!al.canLogin()) {
            Service.gI().sendThongBaoOK(this, al.getNotifyCannotLogin());
            return;
        }

//        if(!"admin".equals(username)){
//            Service.gI().sendThongBaoOK(this, "Hiện tại server đang được sửa lỗi, hẹn bạn trong vài phút!");
//            return;
//        }

        if (Manager.LOCAL) {
            Service.gI().sendThongBaoOK(this, "Server này chỉ để lưu dữ liệu\nVui lòng qua server khác");
            return;
        }
        if (Maintenance.isRunning) {
            Service.gI().sendThongBaoOK(this, "Server đang trong thời gian bảo trì, vui lòng quay lại sau");
            return;
        }
        if (!this.isFounder && Client.gI().getPlayers().size() >= Manager.MAX_PLAYER) {
            Service.gI().sendThongBaoOK(this, "Máy chủ hiện đang quá tải, cư dân vui lòng di chuyển sang máy chủ khác.");
            return;
        }
        if (this.player != null) {
            Service.gI().sendThongBaoOK(this, "Phiên đăng nhập này đã vào game rồi");
            return;
        }

        synchronized (getLoginLock(username)) {
            if (this.player != null) {
                Service.gI().sendThongBaoOK(this, "Phiên đăng nhập này đã vào game rồi");
                return;
            }

            Player pl = null;
            try {
                long st = System.currentTimeMillis();
                this.uu = username;
                this.pp = password;

                pl = GodGK.login(this, al);
                if (pl == null) {
                    return;
                }

                pl.setSession(this);
                this.player = pl;

                // Thread-safe check and add player to Client
                if (!Client.gI().safePut(pl)) {
                    Service.gI().sendThongBaoOK(this, "Tài khoản này đang đăng nhập ở nơi khác");
                    Service.gI().sendLoginFail(this, true);
                    try {
                        pl.dispose();
                    } catch (Exception e) {
                    }
                    this.player = null;
                    return;
                }

                DataGame.sendSmallVersion(this);
                DataGame.sendBgItemVersion(this);

                this.timeWait = 0;
                this.joinedGame = true;

                pl.nPoint.calPoint();
                pl.nPoint.setHp(Util.CrisGH(pl.nPoint.hp));
                pl.nPoint.setMp(Util.CrisGH(pl.nPoint.mp));

                if (pl.Detu != null) {
                    pl.Detu.nPoint.calPoint();
                    pl.Detu.nPoint.setHp(Util.CrisGH(pl.Detu.nPoint.hp));
                    pl.Detu.nPoint.setMp(Util.CrisGH(pl.Detu.nPoint.mp));
                }

                pl.zone.addPlayer(pl);

                DataGame.sendVersionGame(this);
                DataGame.sendDataItemBG(this);

                Logger.primary(TimeUtil.getCurrHour() + "h" + TimeUtil.getCurrMin()
                        + "m: Login Succesfully Player : " + this.player.name
                        + " {" + (System.currentTimeMillis() - st) + " ms}\n");

                if (this.player.notify != null
                        && !this.player.notify.equals("null")
                        && !this.player.notify.isEmpty()
                        && this.player.notify.length() > 0) {
                    Service.gI().sendThongBao(this.player, this.player.notify);
                    this.player.notify = null;
                }

                if (this.player.isNewMember) {
                    DatabaseUpdater.refreshAccountAgeDays(this.player.getSession());
                    Service.gI().sendThongBao(this.player,
                            "Thời gian tìm set kích hoạt đến "
                            + TimeUtil.getDeadline(this.player.getSession().timeCreateAcount,
                                    this.player.getSession().accountAgeDays));
                }
            } catch (Exception e) {
                Logger.logException(MySession.class, e, "Lỗi login");
                if (pl != null) {
                    try {
                        pl.dispose();
                    } catch (Exception ex) {
                    }
                }
                this.player = null;
            }
        }
    }
}
