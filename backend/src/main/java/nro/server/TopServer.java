package nro.server;

import Utils.Logger;
import Utils.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jbcd.ConnectDB;
import jbcd.dao.TopDAO;
import models.Item.Item;
import models.Item.ItemService;
import nro.top.TOP;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;



public class TopServer {
    
    public static List<TOP> topVDST;
    public static List<TOP> TOP_WHIS;
    public static List<TOP> TOP_NHIEM_VU;
    public static List<TOP> TOP_VND;
    public static List<TOP> TOP_COIN;
    public static List<TOP> TOP_THOI_VANG;
    public static List<TOP> TOP_HONG_NGOC;
    public static List<TOP> TOP_DA_NAP;
    public static List<TOP> TOP_HP;
    public static List<TOP> TOP_KI;
    public static List<TOP> TOP_SD;
    public static List<TOP> TOP_HP_DE_TU;
    public static List<TOP> TOP_KI_DE_TU;
    public static List<TOP> TOP_SD_DE_TU;
    public static List<TOP> TOP_SUC_MANH;
    public static List<TOP> TOP_SUC_MANH_DE_TU;
    public static List<TOP> TOP_NAMEK_WAR;
    public static List<TOP> TOP_TANG_LIXI;
    public static List<TOP> TOP_MO_LIXI;
    public static List<TOP> TOP_BAN_PHAO_HOA;
    public static List<TOP> TOP_BAN_PHAO_HOA_VIP;
    public static List<TOP> TOP_TRANG_TRI_CAY_NOEL;
    public static List<TOP> CHE_TAO_NGUOI_TUYET;
    public static List<TOP> CHE_TAO_NGUOI_TUYET_BANG_GIA;
    public static List<TOP> TOP_DOT_DIEM;
    public static List<TOP> TOP_PHAO_HOA_VU_LAN;
    public static List<TOP> TOP_HOA_DANG;
    public static List<TOP> TOP_HOA_DANG_LOI_CHUC;
    public static List<TOP> TOP_MO_HOP_QUA_MA_QUY;
    public static List<TOP> TOP_THIEP_HALLOWEEN;
    public static List<TOP> TOP_MO_THIEP_83;
    public static List<TOP> TOP_TANG_BONG_HOA_HONG;
    public static List<TOP> TOP_LAM_BANH_TRUNG_THU;
    public static List<TOP> TOP_MO_BANH_TRUNG_THU_DAC_BIET;
    public static List<TOP> TOP_MO_TRUNG_VANG;
    public static List<TOP> TOP_MO_HOP_QUA_GIO_TO;
    public static List<TOP> TOP_DANG_BANH;
    public static List<TOP> TOP_DOI_DUA_HAU;
    public static List<TOP> TOP_MO_HOP_BLACK_FRIDAY;
    public static List<TOP> TOP_MUA_SAM_BLACK_FRIDAY;
    public static List<TOP> TOP_MAY_DAM_TRAI_DAT;
    public static List<TOP> TOP_MAY_DAM_NAMEC;
    public static List<TOP> TOP_MAY_DAM_XAYDA;
    public static long timeRealTop = 0;
    
    private static List<TOP> realTop(String query, Connection con) {
        int i = 0;
        List<TOP> tops = new ArrayList<>();
        JSONArray dataArray;
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                short head = Util.getHead((byte) rs.getInt("gender"));
                short body = (short) (rs.getInt("gender") == 1 ? 59 : 57);
                short leg = (short) (rs.getInt("gender") == 1 ? 60 : 58);
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(0).toString());
                if (dataItem != null && dataItem.get(0) != null) {
                    Item item;
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        body = (short) item.template.part;
                    }
                }
                dataItem = (JSONArray) JSONValue.parse(dataArray.get(1).toString());
                if (dataItem != null && dataItem.get(0) != null) {
                    Item item;
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        leg = (short) item.template.part;
                    }
                }
                dataItem = (JSONArray) JSONValue.parse(dataArray.get(5).toString());
                if (dataItem != null && dataItem.get(0) != null) {
                    Item item;
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        if (item.template.head != -1) {
                            head = (short) item.template.head;
                        }
                        if (item.template.body != -1) {
                            body = (short) item.template.body;
                        }
                        if (item.template.leg != -1) {
                            leg = (short) item.template.leg;
                        }
                    }
                }
                dataArray.clear();
                TOP top = TOP.builder().name(rs.getString("name")).gender(rs.getByte("gender")).head(head).body(body).leg(leg).build();
                switch (query) {
                    case TopDAO.TOP_NV: {
                        top.setNv(rs.getByte("nv"));
                        top.setSubnv(rs.getByte("subnv"));
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        break;
                    }
                    case TopDAO.TOP_VDST: {
//                        top.setFirstTimeLogin(rs.getLong("firstTimeLogin"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_WHIS: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setTop(rs.getInt("top"));
                        top.setTime(rs.getInt("time"));
                        switch (i) {
                            case 0:
//                                top1Whis = rs.getLong("id");
                            case 1:
//                                top2Whis = rs.getLong("id");
                            case 2:
//                                top3Whis = rs.getLong("id");
                        }
                        i++;
                        break;
                    }
                    case TopDAO.TOP_VND: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setVnd(rs.getInt("vnd"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_COIN: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setCoin(rs.getInt("coin"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_DA_NAP: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDanap(rs.getInt("danap"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_THOI_VANG: {
                        top.setThoivang(rs.getInt("thoi_vang"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_HONG_NGOC: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setHongngoc(rs.getInt("hongngoc"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_SM: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setPower(rs.getLong("sm"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_HP: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setHp(rs.getLong("hp"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_KI: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setKi(rs.getLong("ki"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_SD: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setSd(rs.getLong("sd"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_SM_PET: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setPower_pet(rs.getLong("smpet"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_HP_PET: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setHp_pet(rs.getLong("hppet"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_KI_PET: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setKi_pet(rs.getLong("kipet"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_SD_PET: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setSd_pet(rs.getLong("damepet"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_NAMEK_WAR: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setPointNamekWar(rs.getInt("diem_chien_truong_namek"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_TANG_LIXI: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setTangLixi(rs.getInt("tang_li_xi_nam_moi"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_LIXI: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setMoLixi(rs.getInt("mo_li_xi"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_BAN_PHAO_HOA: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setBanPhaoHoa(rs.getInt("top_phao_hoa"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_BAN_PHAO_HOA_VIP: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setBanPhaoHoaVIP(rs.getInt("top_phao_hoa_vip"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_TRANG_TRI_CAY_NOEL: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong); 
                        top.setTrangtricayNoel(rs.getInt("top_trang_tri_cay_noel"));
                        i++;
                        break;
                    }
                    case TopDAO.CHE_TAO_NGUOI_TUYET: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setChetaonguoituyet(rs.getInt("che_tao_nguoi_tuyet"));
                        i++;
                        break;
                    }
                    case TopDAO.CHE_TAO_NGUOI_TUYET_BANG_GIA: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setChetaonguoituyetbanggia(rs.getInt("che_tao_nguoi_tuyet_bang_gia"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_DOT_DIEM: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDotdiem(rs.getInt("dot_diem"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_DOT_PHAO_VU_LAN: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setBanPhaoHoaVuLan(rs.getInt("dot_phao"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_HOA_DANG: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setHoaDang(rs.getInt("hoa_dang"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_HOA_DANG_LOI_CHUC: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setHoaDangLoiChuc(rs.getInt("hoa_dang_loi_chuc"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_HOP_QUA_MA_QUY: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setMoHopMaQuy(rs.getInt("ma_quy"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_THIEP_HALLOWEEN: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setThiepHalloween(rs.getInt("thiep_halloween"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_THIEP_83: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopMoThiep_83(rs.getInt("thiep_83"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_TANG_BONG_HOA_HONG: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopTangBongHoaHong(rs.getInt("tanghoahong"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_LAM_BANH_TRUNG_THU: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopLamBanhTrungThu(rs.getInt("lambanhtrungthu"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_BANH_TRUNG_THU_DAC_BIET: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopMoBanhTrungThuDacBiet(rs.getInt("mobanhdacbiet"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_TRUNG_VANG: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopMoTrungRongVang(rs.getInt("motrungvang"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_HOP_QUA_GIO_TO: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopMoHopQuaGioTo(rs.getInt("mohopqua"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_DANG_BANH: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopDangBanhHungVuong(rs.getInt("dangbanh"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_DOI_DUA_HAU: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopDoiDuaHau(rs.getInt("doiduahau"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MO_HOP_BLACK_FRIDAY: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopHopQuaBlackFriday(rs.getInt("black_friday"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MUA_SAM_BLACK_FRIDAY: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setDuaTopMuaSam(rs.getInt("mua_sam"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MAY_DAM_TRAI_DAT: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setTopMayDamTraiDat(rs.getLong("traidat"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MAY_DAM_NAMEC: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setTopMayDamNamec(rs.getLong("namec"));
                        i++;
                        break;
                    }
                    case TopDAO.TOP_MAY_DAM_XAYDA: {
                        Timestamp setLastTimeLogin = rs.getTimestamp("LastTimeLoginGame");
                        long firstTimeLoginLong = setLastTimeLogin != null ? setLastTimeLogin.getTime() : 0;
                        top.setLastTimeLogin(firstTimeLoginLong);
                        top.setTopMayDamXayda(rs.getLong("xayda"));
                        i++;
                        break;
                    }
                }
                tops.add(top);
            }
        } catch (NumberFormatException | SQLException e) {
            e.toString();
        }
        return tops;
    }
    
    public static void LoadingTop() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = ConnectDB.getConnection();) {
            TOP_NHIEM_VU = realTop(TopDAO.TOP_NV, con);
            topVDST = realTop(TopDAO.TOP_VDST, con);
            TOP_WHIS = realTop(TopDAO.TOP_WHIS, con);
            TOP_VND = realTop(TopDAO.TOP_VND, con);
            TOP_COIN = realTop(TopDAO.TOP_COIN, con);
            TOP_THOI_VANG = realTop(TopDAO.TOP_THOI_VANG, con);
            TOP_HONG_NGOC = realTop(TopDAO.TOP_HONG_NGOC, con);
            TOP_DA_NAP = realTop(TopDAO.TOP_DA_NAP, con);
            TOP_SUC_MANH = realTop(TopDAO.TOP_SM, con);
            TOP_HP = realTop(TopDAO.TOP_HP, con);
            TOP_KI = realTop(TopDAO.TOP_KI, con);
            TOP_SD = realTop(TopDAO.TOP_SD, con);
            TOP_SUC_MANH_DE_TU = realTop(TopDAO.TOP_SM_PET, con);
            TOP_HP_DE_TU = realTop(TopDAO.TOP_HP_PET, con);
            TOP_KI_DE_TU = realTop(TopDAO.TOP_KI_PET, con);
            TOP_SD_DE_TU = realTop(TopDAO.TOP_SD_PET, con);
            TOP_NAMEK_WAR = realTop(TopDAO.TOP_NAMEK_WAR, con);
            TOP_TANG_LIXI = realTop(TopDAO.TOP_TANG_LIXI, con);
            TOP_MO_LIXI = realTop(TopDAO.TOP_MO_LIXI, con);
            TOP_BAN_PHAO_HOA = realTop(TopDAO.TOP_BAN_PHAO_HOA, con);
            TOP_BAN_PHAO_HOA_VIP = realTop(TopDAO.TOP_BAN_PHAO_HOA_VIP, con);
            TOP_TRANG_TRI_CAY_NOEL = realTop(TopDAO.TOP_TRANG_TRI_CAY_NOEL, con);
            CHE_TAO_NGUOI_TUYET = realTop(TopDAO.CHE_TAO_NGUOI_TUYET, con);
            CHE_TAO_NGUOI_TUYET_BANG_GIA = realTop(TopDAO.CHE_TAO_NGUOI_TUYET_BANG_GIA, con);
            TOP_DOT_DIEM = realTop(TopDAO.TOP_DOT_DIEM, con);
            TOP_PHAO_HOA_VU_LAN = realTop(TopDAO.TOP_DOT_PHAO_VU_LAN, con);
            TOP_HOA_DANG = realTop(TopDAO.TOP_HOA_DANG, con);
            TOP_HOA_DANG_LOI_CHUC = realTop(TopDAO.TOP_HOA_DANG_LOI_CHUC, con);
            TOP_MO_HOP_QUA_MA_QUY = realTop(TopDAO.TOP_MO_HOP_QUA_MA_QUY, con);
            TOP_THIEP_HALLOWEEN = realTop(TopDAO.TOP_THIEP_HALLOWEEN, con);
            TOP_MO_THIEP_83 = realTop(TopDAO.TOP_MO_THIEP_83, con);
            TOP_TANG_BONG_HOA_HONG = realTop(TopDAO.TOP_TANG_BONG_HOA_HONG, con);
            TOP_LAM_BANH_TRUNG_THU = realTop(TopDAO.TOP_LAM_BANH_TRUNG_THU, con);
            TOP_MO_BANH_TRUNG_THU_DAC_BIET = realTop(TopDAO.TOP_MO_BANH_TRUNG_THU_DAC_BIET, con);
            TOP_MO_TRUNG_VANG = realTop(TopDAO.TOP_MO_TRUNG_VANG, con);
            TOP_MO_HOP_QUA_GIO_TO = realTop(TopDAO.TOP_MO_HOP_QUA_GIO_TO, con);
            TOP_DANG_BANH = realTop(TopDAO.TOP_DANG_BANH, con);
            TOP_DOI_DUA_HAU = realTop(TopDAO.TOP_DOI_DUA_HAU, con);
            TOP_MO_HOP_BLACK_FRIDAY = realTop(TopDAO.TOP_MO_HOP_BLACK_FRIDAY, con);
            TOP_MUA_SAM_BLACK_FRIDAY = realTop(TopDAO.TOP_MUA_SAM_BLACK_FRIDAY, con);
            TOP_MAY_DAM_TRAI_DAT = realTop(TopDAO.TOP_MAY_DAM_TRAI_DAT, con);
            TOP_MAY_DAM_NAMEC = realTop(TopDAO.TOP_MAY_DAM_NAMEC, con);
            TOP_MAY_DAM_XAYDA = realTop(TopDAO.TOP_MAY_DAM_XAYDA, con);
            timeRealTop = System.currentTimeMillis();
        } catch (Exception e) {
            Logger.logException(Manager.class, e, "Lá»—i Loadding Top");
            System.exit(0);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
    
    public static void Topserver_data(Connection con) {
        TOP_NHIEM_VU = realTop(TopDAO.TOP_NV, con);
        topVDST = realTop(TopDAO.TOP_VDST, con);
        TOP_WHIS = realTop(TopDAO.TOP_WHIS, con);
        TOP_VND = realTop(TopDAO.TOP_VND, con);
        TOP_COIN = realTop(TopDAO.TOP_COIN, con);
        TOP_THOI_VANG = realTop(TopDAO.TOP_THOI_VANG, con);
        TOP_HONG_NGOC = realTop(TopDAO.TOP_HONG_NGOC, con);
        TOP_DA_NAP = realTop(TopDAO.TOP_DA_NAP, con);
        TOP_SUC_MANH = realTop(TopDAO.TOP_SM, con);
        TOP_HP = realTop(TopDAO.TOP_HP, con);
        TOP_KI = realTop(TopDAO.TOP_KI, con);
        TOP_SD = realTop(TopDAO.TOP_SD, con);
        TOP_SUC_MANH_DE_TU = realTop(TopDAO.TOP_SM_PET, con);
        TOP_HP_DE_TU = realTop(TopDAO.TOP_HP_PET, con);
        TOP_KI_DE_TU = realTop(TopDAO.TOP_KI_PET, con);
        TOP_SD_DE_TU = realTop(TopDAO.TOP_SD_PET, con);
        TOP_NAMEK_WAR = realTop(TopDAO.TOP_NAMEK_WAR, con);
        TOP_TANG_LIXI = realTop(TopDAO.TOP_TANG_LIXI, con);
        TOP_MO_LIXI = realTop(TopDAO.TOP_MO_LIXI, con);
        TOP_BAN_PHAO_HOA = realTop(TopDAO.TOP_BAN_PHAO_HOA, con);
        TOP_BAN_PHAO_HOA_VIP = realTop(TopDAO.TOP_BAN_PHAO_HOA_VIP, con);
        TOP_TRANG_TRI_CAY_NOEL = realTop(TopDAO.TOP_TRANG_TRI_CAY_NOEL, con);
        CHE_TAO_NGUOI_TUYET = realTop(TopDAO.CHE_TAO_NGUOI_TUYET, con);
        CHE_TAO_NGUOI_TUYET_BANG_GIA = realTop(TopDAO.CHE_TAO_NGUOI_TUYET_BANG_GIA, con);
        TOP_DOT_DIEM = realTop(TopDAO.TOP_DOT_DIEM, con);
        TOP_PHAO_HOA_VU_LAN = realTop(TopDAO.TOP_DOT_PHAO_VU_LAN, con);
        TOP_HOA_DANG = realTop(TopDAO.TOP_HOA_DANG, con);
        TOP_HOA_DANG_LOI_CHUC = realTop(TopDAO.TOP_HOA_DANG_LOI_CHUC, con);
        TOP_MO_HOP_QUA_MA_QUY = realTop(TopDAO.TOP_MO_HOP_QUA_MA_QUY, con);
        TOP_THIEP_HALLOWEEN = realTop(TopDAO.TOP_THIEP_HALLOWEEN, con);
        TOP_MO_THIEP_83 = realTop(TopDAO.TOP_MO_THIEP_83, con);
        TOP_TANG_BONG_HOA_HONG = realTop(TopDAO.TOP_TANG_BONG_HOA_HONG, con);
        TOP_LAM_BANH_TRUNG_THU = realTop(TopDAO.TOP_LAM_BANH_TRUNG_THU, con);
        TOP_MO_BANH_TRUNG_THU_DAC_BIET = realTop(TopDAO.TOP_MO_BANH_TRUNG_THU_DAC_BIET, con);
        TOP_MO_TRUNG_VANG = realTop(TopDAO.TOP_MO_TRUNG_VANG, con);
        TOP_MO_HOP_QUA_GIO_TO = realTop(TopDAO.TOP_MO_HOP_QUA_GIO_TO, con);
        TOP_DANG_BANH = realTop(TopDAO.TOP_DANG_BANH, con);
        TOP_DOI_DUA_HAU = realTop(TopDAO.TOP_DOI_DUA_HAU, con);
        TOP_MO_HOP_BLACK_FRIDAY = realTop(TopDAO.TOP_MO_HOP_BLACK_FRIDAY, con);
        TOP_MUA_SAM_BLACK_FRIDAY = realTop(TopDAO.TOP_MUA_SAM_BLACK_FRIDAY, con);
        TOP_MAY_DAM_TRAI_DAT = realTop(TopDAO.TOP_MAY_DAM_TRAI_DAT, con);
        TOP_MAY_DAM_NAMEC = realTop(TopDAO.TOP_MAY_DAM_NAMEC, con);
        TOP_MAY_DAM_XAYDA = realTop(TopDAO.TOP_MAY_DAM_XAYDA, con);
        timeRealTop = System.currentTimeMillis();
//        Logger.log(Logger.YELLOW, " _                   _  _  _  _            _           _  _  _  _        _  _  _  _        _  _  _        _           _        _  _  _                       _  _  _  _  _       _  _  _  _      _  _  _  _                        _  _  _  _      _            _       _  _  _           _  _  _        _  _  _  _  _       _  _  _  _        _  _  _  _\n");
//        Logger.log(Logger.RED, "(_)                _(_)(_)(_)(_)_        _(_)_        (_)(_)(_)(_)      (_)(_)(_)(_)      (_)(_)(_)      (_) _       (_)    _ (_)(_)(_) _                   (_)(_)(_)(_)(_)    _(_)(_)(_)(_)_   (_)(_)(_)(_)_                    _(_)(_)(_)(_)_   (_)          (_)   _ (_)(_)(_) _     _ (_)(_)(_) _    (_)(_)(_)(_)(_)    _(_)(_)(_)(_)_    _(_)(_)(_)(_)_\n");
//        Logger.log(Logger.YELLOW, "(_)               (_)          (_)     _(_) (_)_       (_)      (_)_     (_)      (_)_       (_)         (_)(_)_     (_)   (_)         (_)                        (_)         (_)          (_)  (_)        (_)                  (_)          (_)  (_)          (_)  (_)         (_)   (_)         (_)   (_)               (_)          (_)  (_)          (_)\n");
//        Logger.log(Logger.RED, "(_)               (_)          (_)   _(_)     (_)_     (_)        (_)    (_)        (_)      (_)         (_)  (_)_   (_)   (_)    _  _  _                         (_)         (_)          (_)  (_) _  _  _(_)                  (_)_  _  _  _     (_)          (_)  (_)               (_)               (_) _  _          (_)_  _  _  _     (_)_  _  _  _\n");
//        Logger.log(Logger.YELLOW, "(_)               (_)          (_)  (_) _  _  _ (_)    (_)        (_)    (_)        (_)      (_)         (_)    (_)_ (_)   (_)   (_)(_)(_)                        (_)         (_)          (_)  (_)(_)(_)(_)                      (_)(_)(_)(_)_   (_)          (_)  (_)               (_)               (_)(_)(_)           (_)(_)(_)(_)_     (_)(_)(_)(_)_\n");
//        Logger.log(Logger.RED, "(_)               (_)          (_)  (_)(_)(_)(_)(_)    (_)       _(_)    (_)       _(_)      (_)         (_)      (_)(_)   (_)         (_)                        (_)         (_)          (_)  (_)                              _           (_)  (_)          (_)  (_)          _    (_)          _    (_)                _           (_)   _           (_)\n");
//        Logger.log(Logger.YELLOW, "(_) _  _  _  _    (_)_  _  _  _(_)  (_)         (_)    (_)_  _  (_)      (_)_  _  (_)      _ (_) _       (_)         (_)   (_) _  _  _ (_)                        (_)         (_)_  _  _  _(_)  (_)                             (_)_  _  _  _(_)  (_)_  _  _  _(_)  (_) _  _  _ (_)   (_) _  _  _ (_)   (_) _  _  _  _    (_)_  _  _  _(_)  (_)_  _  _  _(_)\n");
//        Logger.log(Logger.RED, "(_)(_)(_)(_)(_)     (_)(_)(_)(_)    (_)         (_)   (_)(_)(_)(_)      (_)(_)(_)(_)      (_)(_)(_)      (_)         (_)      (_)(_)(_)(_)                        (_)           (_)(_)(_)(_)    (_)                               (_)(_)(_)(_)      (_)(_)(_)(_)       (_)(_)(_)         (_)(_)(_)      (_)(_)(_)(_)(_)     (_)(_)(_)(_)      (_)(_)(_)(_)\n");
    }
    
}






