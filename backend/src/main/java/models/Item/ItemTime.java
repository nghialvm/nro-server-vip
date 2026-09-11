package models.Item;

import nro.effect.EffectSkillService;
import nro.player.NPoint;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;

public class ItemTime {

    //id item text
    public static final byte DOANH_TRAI = 0;
    public static final byte BAN_DO_KHO_BAU = 1;
    public static final byte KHI_GAS_HUY_DIET = 2;
    public static final byte CON_DUONG_RAN_DOC = 3;
    public static final byte GIAI_CUU_MI_NUONG = 4;
    public static final byte TEXT_NHAN_BUA_MIEN_PHI = 5;
    public static final byte TEXT_DIEM_DANH_HANG_NGAY = 6;
    public static final byte MAP_BOSS_BANG_HOI = 7;

    public static final int TIME_ITEM_3S = 3000;
    public static final int TIME_ITEM_5S = 5000;
    public static final int TIME_ITEM_10S = 10000;
    public static final int TIME_ITEM_15S = 15000;
    public static final int TIME_ITEM_20S = 20000;
    public static final int TIME_ITEM_25S = 25000;
    public static final int TIME_ITEM_30S = 30000;
    public static final int TIME_ITEM_35S = 35000;
    public static final int TIME_ITEM_40S = 40000;
    public static final int TIME_ITEM_45S = 45000;
    public static final int TIME_ITEM_50S = 50000;
    public static final int TIME_ITEM_55S = 55000;
    public static final int TIME_ITEM_1M = 60000;
    public static final int TIME_ITEM_1M30S = 90000;
    public static final int TIME_ITEM_2M = 120000;
    public static final int TIME_ITEM_3M = 180000;
    public static final int TIME_ITEM_5M = 300000;
    public static final int TIME_ITEM_10M = 600000;
    public static final int TIME_ITEM_15M = 900000;
    public static final int TIME_ITEM_20M = 1200000;
    public static final int TIME_ITEM_30M = 1800000;
    public static final int TIME_ITEM_40M = 2400000;
    public static final int TIME_ITEM_50M = 3000000;
    public static final int TIME_ITEM_60M = 3600000;
    public static final int TIME_ITEM_70M = 4200000;
    public static final int TIME_ITEM_80M = 4800000;
    public static final int TIME_ITEM_90M = 5400000;
    public static final int TIME_ITEM_100M = 6000000;
    public static final int TIME_ITEM_110M = 6600000;
    public static final int TIME_ITEM_120M = 7200000;
    public static final int TIME_ITEM_130M = 7800000;
    public static final int TIME_ITEM_140M = 8400000;
    public static final int TIME_ITEM_150M = 9000000;
    public static final int TIME_ITEM_160M = 9600000;
    public static final int TIME_ITEM_170M = 10200000;
    public static final int TIME_ITEM_180M = 10800000;
    public static final int TIME_ITEM_190M = 11400000;
    public static final int TIME_ITEM_200M = 12000000;
    public static final int TIME_ITEM_210M = 12600000;
    public static final int TIME_ITEM_220M = 13200000;
    public static final int TIME_ITEM_230M = 13800000;
    public static final int TIME_ITEM_240M = 14400000;
    public static final int TIME_ITEM_250M = 15000000;
    public static final int TIME_ITEM_260M = 15600000;
    public static final int TIME_ITEM_270M = 16200000;
    public static final int TIME_ITEM_280M = 16800000;
    public static final int TIME_ITEM_290M = 17400000;
    public static final int TIME_ITEM_300M = 18000000;
    public static final int TIME_OPEN_POWER = 86400000;
    public static final int TIME_MAY_DO = 1800000;
    public static final int TIME_EAT_MEAL = 600000;
    public static final int TIME_DUOI_KHI = 600000;
    public static final int TIME_SKILL = 300000;
    public static final int TIME_BROLY = 300000;
    private Player player;

    public boolean isUseBoHuyet;
    public boolean isUseBoKhi;
    public boolean isUseGiapXen;
    public boolean isUseCuongNo;
    public boolean isUseAnDanh;
    public boolean isUseBoHuyet2;
    public boolean isUseBoKhi2;
    public boolean isUseGiapXen2;
    public boolean isUseCuongNo2;
    public boolean isUseAnDanh2;
    public boolean isUse;
    
    public boolean isUseHongDao0;
    public boolean isUseHongDao;
    public boolean isUseHongDao1;
    public boolean isUseHongDao3;
    public boolean isUseHongDao5;
    public boolean isUseHongDao10;
    public boolean isUseHongDao25;
    public boolean isUseHongDao50;
    public boolean isUseHongDao99;
    public boolean isUseHongDao999;
    
    public boolean isUseTHUOCTANGHINH;
    public boolean isUseTHUOCTANGHINH10;
    
    public long lastTimeBoHuyet;
    public long lastTimeBoKhi;
    public long lastTimeGiapXen;
    public long lastTimeCuongNo;
    public long lastTimeAnDanh;

    public long lastTimeHongDao0;
    public long lastTimeHongDao;
    public long lastTimeHongDao1;
    public long lastTimeHongDao3;
    public long lastTimeHongDao5;
    public long lastTimeHongDao10;
    public long lastTimeHongDao25;
    public long lastTimeHongDao50;
    public long lastTimeHongDao99;
    public long lastTimeHongDao999;
    
    public long lastTimeTHUOCTANGHINH;
    public long lastTimeTHUOCTANGHINH10;
  
    public long lastTimeBoHuyet2;
    public long lastTimeBoKhi2;
    public long lastTimeGiapXen2;
    public long lastTimeCuongNo2;
    public long lastTimeAnDanh2;

    public boolean isUseMayDo;
    public long lastTimeUseMayDo;//lastime de chung 1 cai neu time = nhau
    
    public boolean isOpenPower;
    public long lastTimeOpenPower;

    public boolean isUseTDLT;
    public long lastTimeUseTDLT;
    public int timeTDLT;

    public boolean isEatMeal;
    public long lastTimeEatMeal;
    public int iconMeal;
        
    public boolean istrbsd;
    public boolean istrbhp;
    public boolean istrbki;
    public boolean istrbsdxd;
    public boolean istrbhpxd;
    public boolean istrbkixd;

    public long lastTimetrbsd;
    public long lastTimetrbhp;
    public long lastTimetrbki;
    public long lastTimetrbsdxd;
    public long lastTimetrbhpxd;
    public long lastTimetrbkixd;
    
    public boolean iscommenson;

    public long lastcommenson;
   
    public boolean IsKhauTrang;
    public long LastKhauTrang;
    
    public boolean IsKeoMotMat;
    public long LastTimeKeoMotMat;
    public boolean IsSupbihacam;
    public long LastTimeSupbihacam;
    public boolean Isbanhgatonhen;
    public long LastTimebanhgatonhen;
    public boolean Ishamburgersau;
    public long LastTimehamburgersau;
    
    public boolean Isthuocmothuong;
    public long LastTimethuocmothuong;
    public boolean Isthuocmodacbiet;
    public long LastTimethuocmodacbiet;
    
    public boolean iscuarangme;
    public long lasttimecuarangme;
    public boolean isbachtuocnuong;
    public long lasttimebachtuocnuong;
    public boolean istomtambot;
    public long lasttimetomtambot;
    
    public long TimeDuoiKhi;
    public boolean IsDuoiKhi;
    
    public long TimeBocPha;
    public boolean IsBocPha;
    
    public boolean isUseGTPT;
    public long lastTimeUseGTPT;
        
    public boolean isUseNCD;
    public long lastTimeUseNCD;
    //--------------------------------------------------------------------------
    //EVENT TẾT
    public boolean isUseBanhTet;
    public long lastTimeUseBanhTet;
    //
    public boolean isUseBanhTrung;
    public long lastTimeUseBanhTrung;
    //
    public boolean isUseFoodMeoDen1;
    public long lastTimeUseFoodMeoDen1;
    //
    public boolean isUseFoodMeoDen2;
    public long lastTimeUseFoodMeoDen2;
    //
    public boolean isUseMiThangLong;
    public long lastTimeUseMiThangLong;
    //
    public boolean isUseComGaQuay;
    public long lastTimeUseComGaQuay;
    
    public boolean isUseChuotMap;
    public long lastTimeUseChuotMap;
    //--------------------------------------------------------------------------
    
    public boolean isUseChiMang2;
    public long lastTimeUseChiMang2;
    public boolean isUseChiMang3;
    public long lastTimeUseChiMang3;
    public boolean isUseNedon;
    public long lastTimeUseNedon;
    public boolean isUseNedon2;
    public long lastTimeUseNedon2;
    public boolean isUsePhanSatThuong;
    public long lastTimeUsePhanSatThuong;
    public boolean isUsePhanSatThuong2;
    public long lastTimeUsePhanSatThuong2;
    public boolean isUsePhanSatThuong3;
    public long lastTimeUsePhanSatThuong3;
    public boolean isUseKamejoko;
    public long lastTimeUseKamejoko;
    public boolean isUseKamejoko2;
    public long lastTimeUseKamejoko2;
    
    //
    public boolean isUseRocket1h;
    public long lastTimeUseRocket1h;
    public boolean isUseHoiSieuCap;
    public long lastTimeUseHoiSieuCap;
    
    //
    public boolean isUseSatThuongChuan;
    public long lastTimeUseSatThuongChuan;
    public boolean isUseSatThuongChuan2;
    public long lastTimeUseSatThuongChuan2;
    
    //
    public boolean isUseBuaTNSMDetu;
    public long lastTimeUseBuaTNSMDetu;
    //
    public boolean isUseCoBonLa;
    public long lastTimeUseCoBonLa;
    //
    public boolean isUseSauRieng;
    public long lastTimeUseSauRieng;
    
    public boolean isUseMayDoLinhHon;
    public long lastTimeUseMayDoLinhHon;
    
    public boolean isUseMayDoSieuHoa;
    public long lastTimeUseMayDoSieuHoa;
    
    public boolean isUseMayDoNgocBi;
    public long lastTimeUseMayDoNgocBi;
    
    //Rồng Xương
    public boolean isRongXuong;
    public long LastTimeRongXuong;
    public boolean isRongXuong_2;
    public long LastTimeRongXuong_2;
    public boolean isRongXuong_3;
    public long LastTimeRongXuong_3;
    
    //
    public boolean isUseBanhDeoC1;
    public long lastTimeUseBanhDeoC1;
    public boolean isUseBanhDeoC2;
    public long lastTimeUseBanhDeoC2;
    public boolean isUseBanhDeoC3;
    public long lastTimeUseBanhDeoC3;
    public boolean isUseTrungThu1Trung;
    public long lastTimeUseTrungThu1Trung;
    public boolean isUseTrungThu2Trung;
    public long lastTimeUseTrungThu2Trung;
    public boolean isUseTrungThuDB;
    public long lastTimeUseTrungThuDB;
    public boolean isUseHBTrungThu;
    public long lastTimeUseHBTrungThu;
    
    public boolean isUseDuoiKhiTNSM;
    public long lastTimeUseDuoiKhiTNSM;
        
    public ItemTime(Player player) {
        this.player = player;
    }

    public void update() {
        if (isUseDuoiKhiTNSM) {
            if (Util.canDoWithTime(lastTimeUseDuoiKhiTNSM, TIME_ITEM_30M)) {
                isUseDuoiKhiTNSM = false;
                Service.gI().point(player);
            }
        }
        if (isUseHBTrungThu) {
            if (Util.canDoWithTime(lastTimeUseHBTrungThu, TIME_ITEM_150M)) {
                isUseHBTrungThu = false;
                Service.gI().point(player);
            }
        }
        if (isUseTrungThuDB) {
            if (Util.canDoWithTime(lastTimeUseTrungThuDB, TIME_ITEM_120M)) {
                isUseTrungThuDB = false;
                Service.gI().point(player);
            }
        }
        if (isUseTrungThu2Trung) {
            if (Util.canDoWithTime(lastTimeUseTrungThu2Trung, TIME_ITEM_90M)) {
                isUseTrungThu2Trung = false;
                Service.gI().point(player);
            }
        }
        if (isUseTrungThu1Trung) {
            if (Util.canDoWithTime(lastTimeUseTrungThu1Trung, TIME_ITEM_60M)) {
                isUseTrungThu1Trung = false;
                Service.gI().point(player);
            }
        }
        if (isUseBanhDeoC3) {
            if (Util.canDoWithTime(lastTimeUseBanhDeoC3, TIME_ITEM_20M)) {
                isUseBanhDeoC3 = false;
                Service.gI().point(player);
            }
        }
        if (isUseBanhDeoC2) {
            if (Util.canDoWithTime(lastTimeUseBanhDeoC2, TIME_ITEM_10M)) {
                isUseBanhDeoC2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseBanhDeoC1) {
            if (Util.canDoWithTime(lastTimeUseBanhDeoC1, TIME_ITEM_10M)) {
                isUseBanhDeoC1 = false;
                Service.gI().point(player);
            }
        }
        if (isRongXuong_3) {
            if (Util.canDoWithTime(LastTimeRongXuong_3, TIME_ITEM_30M)) {
                isRongXuong_3 = false;
                Service.gI().point(player);
            }
        }
        if (isRongXuong_2) {
            if (Util.canDoWithTime(LastTimeRongXuong_2, TIME_ITEM_30M)) {
                isRongXuong_2 = false;
                Service.gI().point(player);
            }
        }
        if (isRongXuong) {
            if (Util.canDoWithTime(LastTimeRongXuong, TIME_ITEM_30M)) {
                isRongXuong = false;
                Service.gI().point(player);
            }
        }
        if (isUseMayDoNgocBi) {
            if (Util.canDoWithTime(lastTimeUseMayDoNgocBi, TIME_ITEM_60M)) {
                isUseMayDoNgocBi = false;
                Service.gI().point(player);
            }
        }
        if (isUseMayDoLinhHon) {
            if (Util.canDoWithTime(lastTimeUseMayDoLinhHon, TIME_ITEM_60M)) {
                isUseMayDoLinhHon = false;
                Service.gI().point(player);
            }
        }
        if (isUseMayDoSieuHoa) {
            if (Util.canDoWithTime(lastTimeUseMayDoSieuHoa, TIME_ITEM_60M)) {
                isUseMayDoSieuHoa = false;
                Service.gI().point(player);
            }
        }
        if (isUseBuaTNSMDetu) {
            if (Util.canDoWithTime(lastTimeUseBuaTNSMDetu, TIME_ITEM_30M)) {
                isUseBuaTNSMDetu = false;
                Service.gI().point(player);
            }
        }
        if (isUseCoBonLa) {
            if (Util.canDoWithTime(lastTimeUseCoBonLa, TIME_ITEM_30M)) {
                isUseCoBonLa = false;
                Service.gI().point(player);
            }
        }
        if (isUseSauRieng) {
            if (Util.canDoWithTime(lastTimeUseSauRieng, TIME_ITEM_10M)) {
                isUseSauRieng = false;
                Service.gI().point(player);
            }
        }
        if (isUseSatThuongChuan) {
            if (Util.canDoWithTime(lastTimeUseSatThuongChuan, TIME_ITEM_10M)) {
                isUseSatThuongChuan = false;
                Service.gI().point(player);
            }
        }
        if (isUseSatThuongChuan2) {
            if (Util.canDoWithTime(lastTimeUseSatThuongChuan2, TIME_ITEM_10M)) {
                isUseSatThuongChuan2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseRocket1h) {
            if (Util.canDoWithTime(lastTimeUseRocket1h, TIME_ITEM_60M)) {
                isUseRocket1h = false;
                Service.gI().point(player);
            }
        }
        if (isUseHoiSieuCap) {
            if (Util.canDoWithTime(lastTimeUseHoiSieuCap, TIME_ITEM_3S)) {
                isUseHoiSieuCap = false;
                Service.gI().point(player);
            }
        }
        if (isUseKamejoko2) {
            if (Util.canDoWithTime(lastTimeUseKamejoko2, TIME_ITEM_30S)) {
                isUseKamejoko2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseKamejoko) {
            if (Util.canDoWithTime(lastTimeUseKamejoko, TIME_ITEM_30S)) {
                isUseKamejoko = false;
                Service.gI().point(player);
            }
        }
        if (isUsePhanSatThuong3) {
            if (Util.canDoWithTime(lastTimeUsePhanSatThuong3, TIME_ITEM_30S)) {
                isUsePhanSatThuong3 = false;
                Service.gI().point(player);
            }
        }
        if (isUsePhanSatThuong2) {
            if (Util.canDoWithTime(lastTimeUsePhanSatThuong2, TIME_ITEM_30S)) {
                isUsePhanSatThuong2 = false;
                Service.gI().point(player);
            }
        }
        if (isUsePhanSatThuong) {
            if (Util.canDoWithTime(lastTimeUsePhanSatThuong, TIME_ITEM_30S)) {
                isUsePhanSatThuong = false;
                Service.gI().point(player);
            }
        }
        if (isUseNedon2) {
            if (Util.canDoWithTime(lastTimeUseNedon2, TIME_ITEM_30S)) {
                isUseNedon2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseNedon) {
            if (Util.canDoWithTime(lastTimeUseNedon, TIME_ITEM_30S)) {
                isUseNedon = false;
                Service.gI().point(player);
            }
        }
        if (isUseChiMang3) {
            if (Util.canDoWithTime(lastTimeUseChiMang3, TIME_ITEM_10M)) {
                isUseChiMang3 = false;
                Service.gI().point(player);
            }
        }
        if (isUseChiMang2) {
            if (Util.canDoWithTime(lastTimeUseChiMang2, TIME_ITEM_10M)) {
                isUseChiMang2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseChuotMap) {
            if (Util.canDoWithTime(lastTimeUseChuotMap, TIME_ITEM_60M)) {
                isUseChuotMap = false;
                Service.gI().point(player);
            }
        }
        if (isUseComGaQuay) {
            if (Util.canDoWithTime(lastTimeUseComGaQuay, TIME_ITEM_10M)) {
                isUseComGaQuay = false;
                Service.gI().point(player);
            }
        }
        if (isUseMiThangLong) {
            if (Util.canDoWithTime(lastTimeUseMiThangLong, TIME_ITEM_10M)) {
                isUseMiThangLong = false;
                Service.gI().point(player);
            }
        }
        if (isUseFoodMeoDen1) {
            if (Util.canDoWithTime(lastTimeUseFoodMeoDen1, TIME_ITEM_3M)) {
                isUseFoodMeoDen1 = false;
                Service.gI().point(player);
            }
        }
        if (isUseFoodMeoDen2) {
            if (Util.canDoWithTime(lastTimeUseFoodMeoDen2, TIME_ITEM_15M)) {
                isUseFoodMeoDen2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseBanhTet) {
            if (Util.canDoWithTime(lastTimeUseBanhTet, TIME_ITEM_60M)) {
                isUseBanhTet = false;
                Service.gI().point(player);
            }
        }
        if (isUseBanhTrung) {
            if (Util.canDoWithTime(lastTimeUseBanhTrung, TIME_ITEM_60M)) {
                isUseBanhTrung = false;
                Service.gI().point(player);
            }
        }
        //----------------------------------------------------------------------
        if (isUseNCD) {
            if (Util.canDoWithTime(lastTimeUseNCD, TIME_ITEM_30M)) {
                isUseNCD = false;
                Service.gI().point(player);
            }
        }
        if (IsBocPha) {
            if (Util.canDoWithTime(TimeBocPha, TIME_ITEM_10M)) {
                IsBocPha = false;
                Service.gI().point(player);
            }
        }
        if (IsDuoiKhi) {
            if (Util.canDoWithTime(TimeDuoiKhi, TIME_ITEM_2M)) {
                IsDuoiKhi = false;
                EffectSkillService.gI().DuoiKhiDown(player);
                Service.gI().point(player);
            }
        }
        if (istomtambot) {
            if (Util.canDoWithTime(lasttimetomtambot, TIME_ITEM_10M)) {
                istomtambot = false;
                Service.gI().point(player);
            }
        }
        if (isbachtuocnuong) {
            if (Util.canDoWithTime(lasttimebachtuocnuong, TIME_ITEM_10M)) {
                isbachtuocnuong = false;
                Service.gI().point(player);
            }
        }
        if (iscuarangme) {
            if (Util.canDoWithTime(lasttimecuarangme, TIME_ITEM_10M)) {
                iscuarangme = false;
                Service.gI().point(player);
            }
        }
        if (Isthuocmothuong) {
            if (Util.canDoWithTime(LastTimethuocmothuong, TIME_ITEM_10M)) {
                Isthuocmothuong = false;
                Service.gI().point(player);
            }
        }
        if (Isthuocmodacbiet) {
            if (Util.canDoWithTime(LastTimethuocmodacbiet, TIME_ITEM_30M)) {
                Isthuocmodacbiet = false;
                Service.gI().point(player);
            }
        }
        if (isUseGTPT) {
            if (Util.canDoWithTime(lastTimeUseGTPT, TIME_ITEM_10M)) {
                isUseGTPT = false;
            }
        }
        if (IsKhauTrang) {
            if (Util.canDoWithTime(LastKhauTrang, TIME_ITEM_30M)) {
                IsKhauTrang = false;
                Service.gI().point(player);
            }
        }
        if (Ishamburgersau) {
            if (Util.canDoWithTime(LastTimehamburgersau, TIME_ITEM_30M)) {
                Ishamburgersau = false;
                Service.gI().point(player);
            }
        }
        if (Isbanhgatonhen) {
            if (Util.canDoWithTime(LastTimebanhgatonhen, TIME_ITEM_30M)) {
                Isbanhgatonhen = false;
                Service.gI().point(player);
            }
        }
        if (IsSupbihacam) {
            if (Util.canDoWithTime(LastTimeSupbihacam, TIME_ITEM_30M)) {
                IsSupbihacam = false;
                Service.gI().point(player);
            }
        }
        if (IsKeoMotMat) {
            if (Util.canDoWithTime(LastTimeKeoMotMat, TIME_ITEM_30M)) {
                IsKeoMotMat = false;
                Service.gI().point(player);
            }
        }
        if (iscommenson) {
            if (Util.canDoWithTime(lastcommenson, TIME_ITEM_50M)) {
                iscommenson = false;
                Service.gI().point(player);
            }
        }
        
        if (istrbsd) {
            if (Util.canDoWithTime(lastTimetrbsd, TIME_ITEM_60M)) {
                istrbsd = false;
                Service.gI().point(player);
            }
        }
         
        if (istrbhp) {
            if (Util.canDoWithTime(lastTimetrbhp, TIME_ITEM_60M)) {
                istrbhp = false;
                Service.gI().point(player);
            }
        }
        if (istrbki) {
            if (Util.canDoWithTime(lastTimetrbki, TIME_ITEM_60M)) {
                istrbki = false;
                Service.gI().point(player);
            }
        }
        if (istrbsdxd) {
            if (Util.canDoWithTime(lastTimetrbsdxd, TIME_ITEM_30M)) {
                istrbsdxd = false;
                Service.gI().point(player);
            }
        }
         
        if (istrbhpxd) {
            if (Util.canDoWithTime(lastTimetrbhpxd, TIME_ITEM_30M)) {
                istrbhpxd = false;
                Service.gI().point(player);
            }
        }
        if (istrbkixd) {
            if (Util.canDoWithTime(lastTimetrbkixd, TIME_ITEM_30M)) {
                istrbkixd = false;
                Service.gI().point(player);
            }
        }
        if (isUseTHUOCTANGHINH) {
            if (Util.canDoWithTime(lastTimeTHUOCTANGHINH,TIME_ITEM_1M)) {
                isUseTHUOCTANGHINH = false;
                Service.gI().point(player);
                Service.gI().sendThongBao(player, "Thuốc Tàng Hình Thường Đã Hết Tác Dụng!");
            }
        }  
        if (isUseTHUOCTANGHINH10) {
            if (Util.canDoWithTime(lastTimeTHUOCTANGHINH10,TIME_ITEM_10M)) {
                isUseTHUOCTANGHINH10 = false;
                Service.gI().point(player);
                Service.gI().sendThongBao(player, "Thuốc Tàng Hình Cải Tiến Đã Hết Tác Dụng!");
            }
        }  
        if (isUseHongDao0) {
            if (Util.canDoWithTime(lastTimeHongDao0,TIME_ITEM_10S)) {
                isUseHongDao0 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao) {
            if (Util.canDoWithTime(lastTimeHongDao,TIME_ITEM_30S)) {
                isUseHongDao = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao1) {
            if (Util.canDoWithTime(lastTimeHongDao1,TIME_ITEM_30S)) {
                isUseHongDao1 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao3) {
            if (Util.canDoWithTime(lastTimeHongDao3,TIME_ITEM_30S)) {
                isUseHongDao3 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao5) {
            if (Util.canDoWithTime(lastTimeHongDao5,TIME_ITEM_1M)) {
                isUseHongDao5 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao10) {
            if (Util.canDoWithTime(lastTimeHongDao10,TIME_ITEM_1M)) {
                isUseHongDao10 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao25) {
            if (Util.canDoWithTime(lastTimeHongDao25,TIME_ITEM_1M)) {
                isUseHongDao25 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao50) {
            if (Util.canDoWithTime(lastTimeHongDao50,TIME_ITEM_10M)) {
                isUseHongDao50 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao99) {
            if (Util.canDoWithTime(lastTimeHongDao99,TIME_ITEM_10M)) {
                isUseHongDao99 = false;
                Service.gI().point(player);
            }
        }
        if (isUseHongDao999) {
            if (Util.canDoWithTime(lastTimeHongDao999,TIME_ITEM_10M)) {
                isUseHongDao999 = false;
                Service.gI().point(player);
            }
        }
        if (isEatMeal) {
            if (Util.canDoWithTime(lastTimeEatMeal, TIME_EAT_MEAL)) {
                isEatMeal = false;
                Service.gI().point(player);
            }
        }
        if (isUseBoHuyet) {
            if (Util.canDoWithTime(lastTimeBoHuyet, TIME_ITEM_10M)) {
                isUseBoHuyet = false;
                Service.gI().point(player);
            }
        }

        if (isUseBoKhi) {
            if (Util.canDoWithTime(lastTimeBoKhi, TIME_ITEM_10M)) {
                isUseBoKhi = false;
                Service.gI().point(player);
            }
        }

        if (isUseGiapXen) {
            if (Util.canDoWithTime(lastTimeGiapXen, TIME_ITEM_10M)) {
                isUseGiapXen = false;
            }
        }
        if (isUseCuongNo) {
            if (Util.canDoWithTime(lastTimeCuongNo, TIME_ITEM_10M)) {
                isUseCuongNo = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh) {
            if (Util.canDoWithTime(lastTimeAnDanh, TIME_ITEM_10M)) {
                isUseAnDanh = false;
            }
        }

        if (isUseBoHuyet2) {
            if (Util.canDoWithTime(lastTimeBoHuyet2, TIME_ITEM_10M)) {
                isUseBoHuyet2 = false;
                Service.gI().point(player);
            }
        }

        if (isUseBoKhi2) {
            if (Util.canDoWithTime(lastTimeBoKhi2, TIME_ITEM_10M)) {
                isUseBoKhi2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseGiapXen2) {
            if (Util.canDoWithTime(lastTimeGiapXen2, TIME_ITEM_10M)) {
                isUseGiapXen2 = false;
            }
        }
        if (isUseCuongNo2) {
            if (Util.canDoWithTime(lastTimeCuongNo2, TIME_ITEM_10M)) {
                isUseCuongNo2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh2) {
            if (Util.canDoWithTime(lastTimeAnDanh2, TIME_ITEM_10M)) {
                isUseAnDanh2 = false;
            }
        }
        if (isOpenPower) {
            if (Util.canDoWithTime(lastTimeOpenPower, TIME_OPEN_POWER)) {
                player.nPoint.limitPower++;
                if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                    player.nPoint.limitPower = NPoint.MAX_LIMIT;
                }
                player.nPoint.initPowerLimit();
                Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
                isOpenPower = false;
            }
        }
        if (isUseMayDo) {
            if (Util.canDoWithTime(lastTimeUseMayDo, TIME_MAY_DO)) {
                isUseMayDo = false;
            }
        }
        if (isUseTDLT) {
            if (Util.canDoWithTime(lastTimeUseTDLT, timeTDLT)) {
                this.isUseTDLT = false;
                ItemTimeService.gI().sendCanAutoPlay(this.player);
            }
        }
    }

    public void dispose() {
        this.player = null;
    }
}
