package event;


import event.BlackFriday.BlackFriday;
import nro.services.Service;
import event.Christmas.Christmas;
import event.Halloween.Halloween;
import event.HungKingsEvent.HungVuong;
import event.List.Nomal;
import event.List.InternationalWomensDay.InternationalWomensDay;
import event.List.TopUp;
import event.List.WomensDay.WomensDay;
import event.LunarNewYear.LunarNewYear;
import event.MidAutumnFestival.TrungThu;
import event.ValentineDay.ValentineDay;
import event.VuLanFestival.VuLanFestival;
import nro.player.Player;
import nro.server.Client;

public class EventManager {

    private static EventManager instance;

    public static boolean NEW_SEVER = false;

    public static boolean LUNNAR_NEW_YEAR = false;

    public static boolean CHRISTMAS = false;

    public static boolean VU_LAN_FESTIVAL = false;

    public static boolean HALLOWEEN = false;

    public static boolean INTERNATIONAL_WOMANS_DAY = false;

    public static boolean TRUNG_THU = false;

    public static boolean HUNG_VUONG = false;

    public static boolean BLACK_FRIDAY = false;

    public static boolean VALENTINE_DAY = false; // Chưa xong

    public static boolean DAY_20_10 = false;

    public static boolean TOP_UP = false;

    public static EventManager gI() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void init() {
        new Nomal().init();

        if (LUNNAR_NEW_YEAR) {
            new LunarNewYear().init();
        }
        if (CHRISTMAS) {
            new Christmas().init();
        }
        if (VU_LAN_FESTIVAL) {
            new VuLanFestival().init();
        }
        if (HALLOWEEN) {
            new Halloween().init();
        }
        if (INTERNATIONAL_WOMANS_DAY) {
            new InternationalWomensDay().init();
        }
        if (HUNG_VUONG) {
            new HungVuong().init();
        }
        if (TRUNG_THU) {
            new TrungThu().init();
        }
        if (BLACK_FRIDAY) {
            new BlackFriday().init();
        }
        if (VALENTINE_DAY) {
            new ValentineDay().init();
        }
        if (DAY_20_10) {
            new WomensDay().init();
        }
        if (TOP_UP) {
            new TopUp().init();
        }
    }

    public void setCurrentEvent(int eventId) {
        LUNNAR_NEW_YEAR = false;
        CHRISTMAS = false;
        VU_LAN_FESTIVAL = false;
        HALLOWEEN = false;
        INTERNATIONAL_WOMANS_DAY = false;
        TRUNG_THU = false;
        HUNG_VUONG = false;
        BLACK_FRIDAY = false;
        VALENTINE_DAY = false;
        DAY_20_10 = false;
        TOP_UP = false;

        switch (eventId) {
            case 1:
                LUNNAR_NEW_YEAR = true;
                Utils.Logger.success("Sự kiện Tết Nguyên Đán đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Tết Nguyên Đán đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 2:
                TRUNG_THU = true;
                Utils.Logger.success("Sự kiện Trung Thu đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Trung Thu đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 3:
                HALLOWEEN = true;
                Utils.Logger.success("Sự kiện Halloween đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Halloween đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 4:
                CHRISTMAS = true;
                Utils.Logger.success("Sự kiện Giáng Sinh đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Giáng Sinh đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 5:
                VU_LAN_FESTIVAL = true;
                Utils.Logger.success("Sự kiện Vu Lan Báo Hiếu đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Vu Lan đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 6:
                INTERNATIONAL_WOMANS_DAY = true;
                Utils.Logger.success("Sự kiện Quốc tế 8-3 đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Quốc tế 8-3 đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 7:
                HUNG_VUONG = true;
                Utils.Logger.success("Sự kiện Hùng Vương đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Hùng Vương đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 8:
                BLACK_FRIDAY = true;
                Utils.Logger.success("Sự kiện BlackFriday đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện BlackFriday đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 9:
                VALENTINE_DAY = true;
                Utils.Logger.success("Sự kiện Valentine đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện Valentine đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 10:
                DAY_20_10 = true;
                Utils.Logger.success("Sự kiện 20-10 đã khởi động!\n");
                for (Player p : Client.gI().getPlayers()) {
                    Service.gI().sendThongBao(p, "|7|Sự kiện 20/10 đang diễn ra, nhanh tay cày quốc thôi nào!");
                }
                break;

            case 11:
                TOP_UP = true;
                Utils.Logger.success("Sự kiện Top Up đã khởi động!\n");
                break;

            default:
                break;
        }

        init();
    }
}