package nro.services;

import jbcd.dao.PlayerDAO;
import nro.player.Player;
import Utils.Util;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NapThe {

    private static NapThe I;

    public static NapThe gI() {
        if (NapThe.I == null) {
            NapThe.I = new NapThe();
        }
        return NapThe.I;
    }

    public void napThe(Player pl, String maThe, String seri) {
        System.out.println(maThe);
        System.out.println(seri);
    }

    public static final void SendCard(Player p, String loaiThe, String menhGia, String soSeri, String maPin) {
        String partnerId = "50675543528";
        String partnerKey = "51a572a9b1bb9e746a7b4a22db2c8dbd";

        String api = MD5Hash(partnerKey + maPin + soSeri);
        int requestID = Util.nextInt(100000000, 999999999);
        String t = String.valueOf(requestID);

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            MediaType mediaType = MediaType.parse("application/json");

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("telco", loaiThe)
                    .addFormDataPart("code", maPin)
                    .addFormDataPart("serial", soSeri)
                    .addFormDataPart("amount", menhGia)
                    .addFormDataPart("request_id", t)
                    .addFormDataPart("partner_id", partnerId)
                    .addFormDataPart("sign", api)
                    .addFormDataPart("command", "charging")
                    .build();

            Request request = new Request.Builder()
                    .url("https://thesieure.com/chargingws/v2")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

            String jsonString = response.body().string();

            Object obj = JSONValue.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;

            long status = (long) jsonObject.get("status");

            if (status == 99) {
                PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, t);

                Service.gI().sendThongBaoOK(p, "Gửi thẻ thành công\n"
                        + "Seri: " + soSeri + "\n"
                        + "Mã thẻ: " + maPin + "\n"
                        + "Mệnh giá: " + menhGia + "\n"
                        + "Thời gian: " + java.time.LocalDate.now() + " " + java.time.LocalTime.now() + "\n"
                        + "Vui lòng thoát game để cập nhật lại số tiền");
            }

            if (status == 1) {
                PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, t);

                Service.gI().sendThongBaoOK(p, "Gửi thẻ thành công\n"
                        + "Seri: " + soSeri + "\n"
                        + "Mã thẻ: " + maPin + "\n"
                        + "Mệnh giá: " + menhGia + "\n"
                        + "Thời gian: " + java.time.LocalDate.now() + " " + java.time.LocalTime.now() + "\n"
                        + "Vui lòng thoát game để cập nhật lại số tiền");
            } else if (status == 2) {
                Service.gI().sendThongBao(p, "Nạp thành công nhưng sai mệnh giá. Tài khoản sẽ không được cộng tiền.");
            } else if (status == 3) {
                Service.gI().sendThongBao(p, "Bạn đã nhập sai giá trị, vui lòng kiểm tra lại.");
            } else if (status == 4) {
                Service.gI().sendThongBao(p, "Hệ thống nạp thẻ đang bảo trì, vui lòng thử lại sau.");
            } else if (status == 100) {
                Service.gI().sendThongBao(p, "Sai seri hoặc mã pin, vui lòng kiểm tra lại.");
            }

            System.out.println(status + "\n" + menhGia + soSeri + "\n" + maPin);

        } catch (Exception e) {
            e.printStackTrace();
            // Logger.error("Lỗi ở nạp thẻ");
        }
    }

    public static String MD5Hash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}