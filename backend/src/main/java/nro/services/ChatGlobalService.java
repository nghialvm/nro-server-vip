package nro.services;

import nro.player.Player;
import network.io.Message;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import nro.server.Maintenance;
import nro.server.ServerManager;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ChatGlobalService implements Runnable {

    private static final int COUNT_CHAT = 100;
    private static final int COUNT_WAIT = 100;
    private static ChatGlobalService i;

    private final List<ChatGlobal> listChatting;
    private final List<ChatGlobal> waitingChat;

    private ChatGlobalService() {
        this.listChatting = new ArrayList<>();
        this.waitingChat = new LinkedList<>();
        this.start();
    }

    private void start() {
        Executors.newSingleThreadExecutor().submit(this, "**Chat global");
    }

    public static ChatGlobalService gI() {
        if (i == null) {
            i = new ChatGlobalService();
        }
        return i;
    }

    public void ThongBaoRoiDo(Player player, String text) {
        waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
    }

    public void ThongBaoDapDo(Player player, String text) {
        waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
    }

    public void chatVip(Player player, String text) {
        waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
    }

    public void chatTGbot(Player player, String text) {
        player.iDMark.setLastTimeChatGlobal(System.currentTimeMillis());
        waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
    }

    public void chat(Player player, String text) {
        if (player.baovetaikhoan) {
            Service.gI().sendThongBao(player, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
            return;
        }

        if (waitingChat.size() >= COUNT_WAIT) {
            Service.gI().sendThongBao(player, "Kênh thế giới hiện đang quá tải, không thể chat lúc này");
            return;
        }

        boolean haveInChatting = false;
        for (ChatGlobal chat : listChatting) {
            if (chat.text.equals(text)) {
                haveInChatting = true;
                break;
            }
        }

        if (haveInChatting) {
            return;
        }

        if (player.inventory.ruby >= 500) {
            if (player.isFounder() || player.isQuanTriVien() || Util.canDoWithTime(player.iDMark.getLastTimeChatGlobal(), 30000)) {
                if (player.isFounder() || player.isQuanTriVien() || player.nPoint.power > 2000000000) {
                    player.inventory.subGemAndRuby(500);
                    Service.gI().sendMoney(player);
                    player.iDMark.setLastTimeChatGlobal(System.currentTimeMillis());
                    waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
                } else {
                    Service.gI().sendThongBao(player, "Sức mạnh phải ít nhất 2 tỷ mới có thể chat thế giới");
                }
            } else {
                Service.gI().sendThongBao(player, "Không thể chat thế giới lúc này, vui lòng đợi "
                        + TimeUtil.getTimeLeft(player.iDMark.getLastTimeChatGlobal(), 30));
            }
        } else {
            Service.gI().sendThongBao(player, "Bạn không đủ Hồng Ngọc để chat thế giới");
        }
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                if (!listChatting.isEmpty()) {
                    ChatGlobal chat = listChatting.get(0);
                    if (Util.canDoWithTime(chat.timeSendToPlayer, 1000)) {
                        listChatting.remove(0).dispose();
                    }
                }

                if (!waitingChat.isEmpty()) {
                    ChatGlobal chat = waitingChat.get(0);
                    if (listChatting.size() < COUNT_CHAT) {
                        waitingChat.remove(0);
                        chat.timeSendToPlayer = System.currentTimeMillis();
                        listChatting.add(chat);
                        chatGlobal(chat);
                    }
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                Logger.logException(ChatGlobalService.class, e);
            }
        }
    }

    private void chatGlobal(ChatGlobal chat) {
        Message msg;
        try {
            msg = new Message(92);
            msg.writer().writeUTF(chat.playerName);
            msg.writer().writeUTF("|5|" + chat.text);
            msg.writer().writeInt((int) chat.playerId);
            msg.writer().writeShort(chat.head);
            msg.writer().writeShort(-1);
            msg.writer().writeShort(chat.body);
            msg.writer().writeShort(chat.bag); // bag
            msg.writer().writeShort(chat.leg);
            msg.writer().writeByte(0);
            Service.gI().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private static final String[] BAD_WORDS = {
        "địt", "lồn", "cặc", "buồi", "dái", "đít", "bú", "húp", "cl", "dm", "cm", "cc",
        "clmm", "vcl", "vl", "vđ", "đm", "đmml", "dmm", "clm", "djt", "ccmn", "djtme", "me may",
        "d m", "đ m", "c m", "c l", "đ ỉ", "d . m", "đ.ịt", "đ ị t", "l ồ n", "c ặ c", "b uồi", "bố mày", "mẹ mày",
        "mẹ", "bố", "cha", "má", "ông", "bà", "con đĩ", "thằng chó", "đồ chó", "con chó", "cave", "phò", "đĩ", "nứng",
        "ngu", "óc chó", "óc lợn", "thằng điên", "con điên", "mất dạy", "không có não", "não phẳng",
        "xxx", "sex", "jav", "xnxx", "phim sex", "xvideos", "porn", "loạn luân", "gái gọi", "thổi kèn", "bú cu", "bú bướm",
        "admin", "ad mìn", "mod", "gm", "khải", "khai", "dev", "quản trị",
        ".mobi", ".online", ".info", ".tk", ".ml", ".ga", ".gq", ".io", ".club", ".com", ".net", ".xyz", ".vip", ".top", ".site",
        "nạp", "free", "giftcode", "tool", "hack", "modmenu", "cheat", "acc vip",
        "săn đệ", "tool dame", "bypass", "auto win", "onehit", "mod vip", "crack", "root máy", "game lậu", "server lậu",
        "bug", "auto", "speed", "xuyên tường",
        "keylogger", "aimbot", "script", "trainer", "godmode", "inject", "memory edit",
        "speedhack", "wallhack", "exploit", "bot", "dll", "no recoil", "antiban"
    };

    private void transformText(ChatGlobal chat) {
        String text = chat.text;

        for (String badWord : BAD_WORDS) {
            // (?i) = không phân biệt hoa thường
            String pattern = "(?i)" + Pattern.quote(badWord);
            text = text.replaceAll(pattern, "***");
        }

        chat.text = text;
    }

    private class ChatGlobal {

        public String playerName;
        public int playerId;
        public short head;
        public short body;
        public short leg;
        public short bag;
        public String text;
        public long timeSendToPlayer;

        public ChatGlobal(Player player, String text) {
            if (player.isBot || player.isBot_New || player.isBot_Event || player.isBot_Valentine) {
                this.playerName = player.name + " [" + ServerManager.NAME_SERVER + "]";
            } else if (player.isPl() && !player.isFounder() && !player.isQuanTriVien()) {
                this.playerName = player.name + " [" + ServerManager.NAME_SERVER + "]";
            } else if (player.isFounder()) {
                this.playerName = player.name + " - Founder";
            } else if (player.isQuanTriVien()) {
                this.playerName = player.name + " - Quản Trị Viên";
            }

            this.playerId = (int) player.id;
            this.head = player.getHead();
            this.body = player.getBody();
            this.leg = player.getLeg();
            this.bag = player.getFlagBag();
            this.text = text;
            transformText(this);
        }

        private void dispose() {
            this.playerName = null;
            this.text = null;
        }
    }
}