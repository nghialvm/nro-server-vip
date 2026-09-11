package nro.npc.ListNpc;



import nro.services.Fun.Input;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import nro.minigame.TaiXiu;
import nro.npc.Npc;
import nro.player.Player;

public class ChanLe extends Npc {

    public ChanLe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }    

    @Override
    public void openBaseMenu(Player player) {
        createOtherMenu(player, 0, "\b|8|TÀI XỈU MD5\n\n"
                + "|6|Thử Vận May Thôi Nào, Một Ăn Cả, Ngã Về 0\n\n"
                + "|7|(Điều Kiện Tham Gia : Mở Thành Viên)\n\n"
                + "|2|Đặt Tối Thiểu: 10 Thỏi Vàng\n"
                + "Tối Đa: 1.000.000 Thỏi Vàng\n\n"
                + "|7|Lưu ý : Thoát game khi chốt Kết quả sẽ MẤT Tiền cược và Tiền thưởng!", 
                "Thể lệ", "Tham gia", "Soi Cầu");
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (canOpenNpc(pl)) {
            String time = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
            if (pl.iDMark.getIndexMenu() == 0) {
                switch (select) {
                    case 0:
                        createOtherMenu(pl, ConstNpc.IGNORE_MENU, "|5|Có 2 bên Tài và Xỉu, Bạn chỉ được chọn 1 bên để tham gia\n\n"
                                + "|2|Sau Khi Kết Thúc Thời Gian Đặt Cược. Hệ Thống Sẽ Tung Xí Ngầu Để Biết Kết Quả Tài Hay Xỉu\n\n"
                                + "|1|Nếu Tổng số 3 con xí ngầu <= 10 : XỈU\n"
                                + "|1|Nếu Tổng số 3 con xí ngầu > 10 : TÀI\n"
                                + "|2|Nếu 3 Xí ngầu cùng 1 số : TAM HOA (Nhà cái lụm hết)\n\n"
                                + "|7|Lưu ý: Số Thỏi Vàng nhận được sẽ bị nhà cái lụm đi 10%\n"
                                + "Trong quá trình diễn ra khi đặt cược nếu thoát game trong lúc phát thưởng phần quà sẽ bị HỦY", "Ok");
                        break;
                    case 1:
                        String thongTin = "\n|6|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z + 
                                (TaiXiu.gI().x == TaiXiu.gI().y && TaiXiu.gI().y == TaiXiu.gI().z ? " - TAM HOA" :
                                TaiXiu.gI().x + TaiXiu.gI().y + TaiXiu.gI().z <= 10 ? " - XỈU" : " - TÀI")
                                + "\n\n|1|Tổng Cược TÀI : " + Util.format(TaiXiu.gI().goldTai) + " Thỏi Vàng"
                                + "\n|1|Tổng Cược XỈU : " + Util.format(TaiXiu.gI().goldXiu) + " Thỏi Vàng"
                                + "\n\n|5|Thời gian còn lại: " + time;

                        String tieuDe = (TaiXiu.gI().baotri ? "---HỆ THỐNG SẮP BẢO TRÌ---" : "---ĐỠ THẾ LỒN NÀO ĐƯỢC CÁC ÔNG À---");

                        String cuocThem = "";
                        if (pl.goldTai > 0) {
                            cuocThem = "\n\n|7|Bạn đã cược Tài : " + Util.format(pl.goldTai) + " Thỏi Vàng - Tài";
                        } else if (pl.goldXiu > 0) {
                            cuocThem = "\n\n|7|Bạn đã cược Xỉu : " + Util.format(pl.goldXiu) + " Thỏi Vàng - Xỉu";
                        }

                        String fullNoiDung = "\n|7|" + tieuDe + "\n" + thongTin + cuocThem;

                        if (TaiXiu.gI().baotri && pl.goldTai == 0 && pl.goldXiu == 0) {
                            createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Đóng");
                        } else {
                            createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Theo TÀI", "Theo XỈU", "Soi Cầu", "Đóng");
                        }
                        break;
                    case 2:
                        Service.gI().sendThongBaoFromAdmin(pl,TaiXiu.gI().getHistoryGame());
                        break;
                    default:
                        break;
                }
            } else if (pl.iDMark.getIndexMenu() == 1) {
                long timeLeft = (TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000;
                boolean isActive = timeLeft > 0;

                if (isActive) {
                    String thongTin = "\n|6|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z + 
                                (TaiXiu.gI().x == TaiXiu.gI().y && TaiXiu.gI().y == TaiXiu.gI().z ? " - TAM HOA" :
                                TaiXiu.gI().x + TaiXiu.gI().y + TaiXiu.gI().z <= 10 ? " - XỈU" : " - TÀI")
                                + "\n\n|1|Tổng Cược TÀI : " + Util.format(TaiXiu.gI().goldTai) + " Thỏi Vàng"
                                + "\n|1|Tổng Cược XỈU : " + Util.format(TaiXiu.gI().goldXiu) + " Thỏi Vàng"
                                + "\n\n|5|Thời gian còn lại: " + time;

                    String tieuDe = (TaiXiu.gI().baotri ? "---HỆ THỐNG SẮP BẢO TRÌ---" : "---ĐỠ THẾ LỒN NÀO ĐƯỢC CÁC ÔNG À---");

                    String cuocThem = "";
                    if (pl.goldTai > 0) {
                        cuocThem = "\n\n|7|Bạn đã cược Tài : " + Util.format(pl.goldTai) + " Thỏi Vàng - Tài";
                    } else if (pl.goldXiu > 0) {
                        cuocThem = "\n\n|7|Bạn đã cược Xỉu : " + Util.format(pl.goldXiu) + " Thỏi Vàng - Xỉu";
                    }

                    String fullNoiDung = "\n|7|" + tieuDe + "\n" + thongTin + cuocThem;
                        
                    switch (select) {
                        case 0:
                            createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Theo TÀI", "Theo XỈU", "Soi Cầu", "Đóng");
                            break;
                        case 1:
                            if ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) <= 10_000) {
                                Service.getInstance().sendThongBao(pl, "Đã quá thời gian đặt cược, không để thực hiện!");
                                return;
                            }
                            if (pl.goldTai > 0) {
                                createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Theo TÀI", "Theo XỈU", "Soi Cầu", "Đóng");
                                return;
                            } else if (pl.goldXiu > 0) {
                                createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Theo TÀI", "Theo XỈU", "Soi Cầu", "Đóng");
                                return;
                            }
                            if (!pl.getSession().actived) {
                                Service.gI().sendThongBao(pl, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
                            } else {
                                Input.gI().TAI_MD5(pl);
                            }
                            break;
                        case 2:
                            if ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) <= 10_000) {
                                Service.getInstance().sendThongBao(pl, "Đã quá thời gian đặt cược, không để thực hiện!");
                                return;
                            }
                            if (pl.goldTai > 0) {
                                createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Theo TÀI", "Theo XỈU", "Soi Cầu", "Đóng");
                                return;
                            } else if (pl.goldXiu > 0) {
                                createOtherMenu(pl, 1, fullNoiDung, "Cập nhập", "Theo TÀI", "Theo XỈU", "Soi Cầu", "Đóng");
                                return;
                            }
                            if (!pl.getSession().actived) {
                                Service.gI().sendThongBao(pl, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
                            } else {
                                Input.gI().XIU_MD5(pl);
                            }
                            break;
                        case 3:
                            Service.gI().sendThongBaoFromAdmin(pl, TaiXiu.gI().getHistoryGame());
                            break;
                    }
                }
            }
        }
    }
}
