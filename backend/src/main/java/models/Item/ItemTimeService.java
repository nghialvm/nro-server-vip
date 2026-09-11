package models.Item;

import consts.ConstPlayer;
import static models.Item.ItemTime.*;
import nro.player.Fusion;
import nro.player.Player;
import network.io.Message;
import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Logger;
import nro.map.BossOfTheGangs.BossOfTheGangs;
import nro.map.DestronGas.DestronGas;
import nro.map.RedRibbonHQ.RedRibbonHQ;
import nro.map.SnakeWay.SnakeWay;
import nro.map.TreasureUnderSea.TreasureUnderSea;

public class ItemTimeService {

    private static ItemTimeService i;

    public static ItemTimeService gI() {
        if (i == null) {
            i = new ItemTimeService();
        }
        return i;
    }

    //gửi cho client
    public void sendAllItemTime(Player player) {
        ItemTimeService.gI().sendTextBanDoKhoBau(player);
        ItemTimeService.gI().sendTextDoanhTrai(player);
        ItemTimeService.gI().sendTextConDuongRanDoc(player);
        ItemTimeService.gI().sendTextKhiGasHuyDiet(player);
        ItemTimeService.gI().sendTextTimePickDoanhTrai(player);
        if (player.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
            sendItemTime(player, player.gender == ConstPlayer.NAMEC ? 3901 : 3790,
                    (int) ((Fusion.TIME_FUSION - (System.currentTimeMillis() - player.fusion.lastTimeFusion)) / 1000));
        }
        if (player.itemTime.isUseDuoiKhiTNSM) {
            sendItemTime(player, 5072, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseDuoiKhiTNSM)) / 1000));
        }
        if (player.itemTime.isUseHBTrungThu) {
            sendItemTime(player, 4126, (int) ((TIME_ITEM_150M - (System.currentTimeMillis() - player.itemTime.lastTimeUseHBTrungThu)) / 1000));
        }
        if (player.itemTime.isUseTrungThuDB) {
            sendItemTime(player, 4125, (int) ((TIME_ITEM_120M - (System.currentTimeMillis() - player.itemTime.lastTimeUseTrungThuDB)) / 1000));
        }
        if (player.itemTime.isUseTrungThu2Trung) {
            sendItemTime(player, 4043, (int) ((TIME_ITEM_90M - (System.currentTimeMillis() - player.itemTime.lastTimeUseTrungThu2Trung)) / 1000));
        }
        if (player.itemTime.isUseTrungThu1Trung) {
            sendItemTime(player, 4042, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseTrungThu1Trung)) / 1000));
        }
        if (player.itemTime.isUseBanhDeoC3) {
            sendItemTime(player, 11677, (int) ((TIME_ITEM_20M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhDeoC3)) / 1000));
        }
        if (player.itemTime.isUseBanhDeoC2) {
            sendItemTime(player, 11676, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhDeoC2)) / 1000));
        }
        if (player.itemTime.isUseBanhDeoC1) {
            sendItemTime(player, 11675, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhDeoC1)) / 1000));
        }
        //
        if (player.itemTime.isRongXuong) {
            sendItemTime(player, 6579, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeRongXuong)) / 1000));
        }
        if (player.itemTime.isRongXuong_2) {
            sendItemTime(player, 6580, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeRongXuong_2)) / 1000));
        }
        if (player.itemTime.isRongXuong_3) {
            sendItemTime(player, 6581, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeRongXuong_3)) / 1000));
        }
        if (player.itemTime.isUseMayDoNgocBi) {
            sendItemTime(player, 434, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDoNgocBi)) / 1000));
        }
        if (player.itemTime.isUseMayDoLinhHon) {
            sendItemTime(player, 11493, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDoLinhHon)) / 1000));
        }
        if (player.itemTime.isUseMayDoSieuHoa) {
            sendItemTime(player, 27021, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDoSieuHoa)) / 1000));
        }
        if (player.itemTime.isUseBuaTNSMDetu) {
            sendItemTime(player, 13540, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBuaTNSMDetu)) / 1000));
        }
        if (player.itemTime.isUseCoBonLa) {
            sendItemTime(player, 13618, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseCoBonLa)) / 1000));
        }
        if (player.itemTime.isUseSauRieng) {
            sendItemTime(player, 13747, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseSauRieng)) / 1000));
        }
        if (player.itemTime.isUseSatThuongChuan) {
            sendItemTime(player, 12766, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseSatThuongChuan)) / 1000));
        }
        if (player.itemTime.isUseSatThuongChuan2) {
            sendItemTime(player, 12767, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseSatThuongChuan2)) / 1000));
        }
        if (player.itemTime.isUseHoiSieuCap) {
            sendItemTime(player, 27019, (int) ((TIME_ITEM_3S - (System.currentTimeMillis() - player.itemTime.lastTimeUseHoiSieuCap)) / 1000));
        }
        if (player.itemTime.isUseRocket1h) {
            sendItemTime(player, 27009, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseRocket1h)) / 1000));
        }
        if (player.itemTime.isUseKamejoko2) {
            sendItemTime(player, 30867, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseKamejoko2)) / 1000));
        }
        if (player.itemTime.isUseKamejoko) {
            sendItemTime(player, 30867, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseKamejoko)) / 1000));
        }
        if (player.itemTime.isUsePhanSatThuong3) {
            sendItemTime(player, 30866, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUsePhanSatThuong3)) / 1000));
        }
        if (player.itemTime.isUsePhanSatThuong2) {
            sendItemTime(player, 30866, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUsePhanSatThuong2)) / 1000));
        }
        if (player.itemTime.isUsePhanSatThuong) {
            sendItemTime(player, 30866, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUsePhanSatThuong)) / 1000));
        }
        if (player.itemTime.isUseNedon2) {
            sendItemTime(player, 32752, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseNedon2)) / 1000));
        }
        if (player.itemTime.isUseNedon) {
            sendItemTime(player, 32752, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseNedon)) / 1000));
        }
        if (player.itemTime.isUseChiMang3) {
            sendItemTime(player, 32751, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseChiMang3)) / 1000));
        }
        if (player.itemTime.isUseChiMang2) {
            sendItemTime(player, 32751, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseChiMang2)) / 1000));
        }
        if (player.itemTime.isUseChuotMap) {
            sendItemTime(player, 12831, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseChuotMap)) / 1000));
        }
        if (player.itemTime.isUseComGaQuay) {
            sendItemTime(player, 12396, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseComGaQuay)) / 1000));
        }
        if (player.itemTime.isUseMiThangLong) {
            sendItemTime(player, 12395, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMiThangLong)) / 1000));
        }
        if (player.itemTime.isUseFoodMeoDen1) {
            sendItemTime(player, 10890, (int) ((TIME_ITEM_3M - (System.currentTimeMillis() - player.itemTime.lastTimeUseFoodMeoDen1)) / 1000));
        }
        if (player.itemTime.isUseFoodMeoDen2) {
            sendItemTime(player, 10891, (int) ((TIME_ITEM_15M - (System.currentTimeMillis() - player.itemTime.lastTimeUseFoodMeoDen2)) / 1000));
        }
        if (player.itemTime.isUseBanhTet) {
            sendItemTime(player, 7079, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhTet)) / 1000));
        }
        if (player.itemTime.isUseBanhTrung) {
            sendItemTime(player, 7080, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhTrung)) / 1000));
        }
        if (player.itemTime.isUseNCD) {
            sendItemTime(player, 11173, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseNCD)) / 1000));
        }
        if (player.itemTime.isUseGTPT) {
            sendItemTime(player, 3778, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseGTPT)) / 1000));
        }
        if (player.itemTime.IsBocPha) {
            sendItemTime(player, 10713, (int) (( TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.TimeBocPha)) / 1000));
        }
        if (player.itemTime.IsDuoiKhi) {
            sendItemTime(player, 5072, (int) (( TIME_ITEM_2M - (System.currentTimeMillis() - player.itemTime.TimeDuoiKhi)) / 1000));
        }
        if (player.itemTime.iscuarangme) {
            sendItemTime(player, 8060, (int) (( TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lasttimecuarangme)) / 1000));
        }
        if (player.itemTime.isbachtuocnuong) {
            sendItemTime(player, 8061, (int) (( TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lasttimebachtuocnuong)) / 1000));
        }
        if (player.itemTime.istomtambot) {
            sendItemTime(player, 8062, (int) (( TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lasttimetomtambot)) / 1000));
        }
        if (player.itemTime.Isthuocmothuong) {
            sendItemTime(player, 9068, (int) (( TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.LastTimethuocmothuong)) / 1000));
        }
        if (player.itemTime.Isthuocmodacbiet) {
            sendItemTime(player, 9068, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimethuocmodacbiet)) / 1000));
        }
        if (player.itemTime.Ishamburgersau) {
            sendItemTime(player, 8247, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimehamburgersau)) / 1000));
        }
        if (player.itemTime.Isbanhgatonhen) {
            sendItemTime(player, 8246, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimebanhgatonhen)) / 1000));
        }
        if (player.itemTime.IsSupbihacam) {
            sendItemTime(player, 8244, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeSupbihacam)) / 1000));
        }
        if (player.itemTime.IsKeoMotMat) {
            sendItemTime(player, 8243, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeKeoMotMat)) / 1000));
        }
        if (player.itemTime.IsKhauTrang) {
            sendItemTime(player, 7149, (int) (( TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastKhauTrang)) / 1000));
        }
        if (player.itemTime.iscommenson) {
            sendItemTime(player, 5829, (int) (( TIME_ITEM_50M - (System.currentTimeMillis() - player.itemTime.lastcommenson)) / 1000));
        }
        if (player.itemTime.istrbsd) {
            sendItemTime(player, 24360, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimetrbsd)) / 1000));
        }
        if (player.itemTime.istrbhp) {
            sendItemTime(player, 24361, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimetrbhp)) / 1000));
        }
        if (player.itemTime.istrbki) {
            sendItemTime(player, 24362, (int) ((TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimetrbki)) / 1000));
        }
        if (player.itemTime.istrbsdxd) {
            sendItemTime(player, 24360, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimetrbsdxd)) / 1000));
        }
        if (player.itemTime.istrbhpxd) {
            sendItemTime(player, 24361, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimetrbhpxd)) / 1000));
        }
        if (player.itemTime.istrbkixd) {
            sendItemTime(player, 24362, (int) ((TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimetrbkixd)) / 1000));
        }

        if (player.itemTime.isUseTHUOCTANGHINH) {
            sendItemTime(player, 24252, (int) ((TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeTHUOCTANGHINH)) / 1000));
        }
        if (player.itemTime.isUseTHUOCTANGHINH10) {
            sendItemTime(player, 24252, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeTHUOCTANGHINH10)) / 1000));
        }
        if (player.itemTime.isUseHongDao0) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_10S - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao0)) / 1000));
        }
        if (player.itemTime.isUseHongDao) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao)) / 1000));
        }
        if (player.itemTime.isUseHongDao1) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao1)) / 1000));
        }
        if (player.itemTime.isUseHongDao3) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao3)) / 1000));
        }
        if (player.itemTime.isUseHongDao5) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao5)) / 1000));
        }
        if (player.itemTime.isUseHongDao10) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao10)) / 1000));
        }
        if (player.itemTime.isUseHongDao25) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao25)) / 1000));
        }
        if (player.itemTime.isUseHongDao50) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao50)) / 1000));
        }
        if (player.itemTime.isUseHongDao99) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao99)) / 1000));
        }
        if (player.itemTime.isUseHongDao999) {
            sendItemTime(player, 4547, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao999)) / 1000));
        }
        if (player.itemTime.isUseBoHuyet) {
            sendItemTime(player, 2755, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet)) / 1000));
        }
        if (player.itemTime.isUseBoKhi) {
            sendItemTime(player, 2756, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi)) / 1000));
        }
        if (player.itemTime.isUseGiapXen) {
            sendItemTime(player, 2757, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen)) / 1000));
        }
        if (player.itemTime.isUseCuongNo) {
            sendItemTime(player, 2754, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo)) / 1000));
        }

        if (player.itemTime.isUseAnDanh) {
            sendItemTime(player, 2760, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh)) / 1000));
        }
        if (player.itemTime.isUseBoHuyet2) {
            sendItemTime(player, 10714, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet2)) / 1000));
        }
        if (player.itemTime.isUseBoKhi2) {
            sendItemTime(player, 10715, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi2)) / 1000));
        }
        if (player.itemTime.isUseGiapXen2) {
            sendItemTime(player, 10712, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen2)) / 1000));
        }
        if (player.itemTime.isUseCuongNo2) {
            sendItemTime(player, 10716, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo2)) / 1000));
        }

        if (player.itemTime.isUseAnDanh2) {
            sendItemTime(player, 10717, (int) ((TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh2)) / 1000));
        }
        if (player.itemTime.isOpenPower) {
            sendItemTime(player, 3783, (int) ((TIME_OPEN_POWER - (System.currentTimeMillis() - player.itemTime.lastTimeOpenPower)) / 1000));
        }
        if (player.itemTime.isUseMayDo) {
            sendItemTime(player, 2758, (int) ((TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDo)) / 1000));
        }
        if (player.effectSkill.iscumber && player.effectSkill.lastTimeUpcumber >= 0) {
               sendItemTime(player, 30907, (int) ((180000 - (System.currentTimeMillis() - player.effectSkill.lastTimeUpcumber)) / 1000));
        }
        if (player.effectSkill.iscumber2 && player.effectSkill.lastTimeUpcumber2 >= 0) {
               sendItemTime(player, 20126, (int) ((180000 - (System.currentTimeMillis() - player.effectSkill.lastTimeUpcumber2)) / 1000));
        }
        if (player.effectSkill.isPain && player.effectSkill.lastTimeUpPain >= 0) {
               sendItemTime(player, 25265, (int) ((180000 - (System.currentTimeMillis() - player.effectSkill.lastTimeUpPain)) / 1000));
        }
        if (player.effectSkill.iskefla && player.effectSkill.lastTimeUpkefla >= 0) {
               sendItemTime(player, 13769, (int) ((300000 - (System.currentTimeMillis() - player.effectSkill.lastTimeUpkefla)) / 1000));
        }
        if (player.itemTime.isEatMeal) {
            sendItemTime(player, player.itemTime.iconMeal, (int) ((TIME_EAT_MEAL - (System.currentTimeMillis() - player.itemTime.lastTimeEatMeal)) / 1000));
        }
        if (player.itemTime.isUseTDLT) {
            sendItemTime(player, 4387, player.itemTime.timeTDLT / 1000);
        }
        
    }
    
    public void clearAllItemTime(Player player) {
        try {
            player.itemTime.isUseHBTrungThu = false;
            removeItemTime(player, 4126);

            player.itemTime.isUseTrungThuDB = false;
            removeItemTime(player, 4125);

            player.itemTime.isUseTrungThu2Trung = false;
            removeItemTime(player, 4043);

            player.itemTime.isUseTrungThu1Trung = false;
            removeItemTime(player, 4042);

            player.itemTime.isUseBanhDeoC3 = false;
            removeItemTime(player, 11677);

            player.itemTime.isUseBanhDeoC2 = false;
            removeItemTime(player, 11676);

            player.itemTime.isUseBanhDeoC1 = false;
            removeItemTime(player, 11675);

            player.itemTime.isRongXuong = false;
            removeItemTime(player, 6579);

            player.itemTime.isRongXuong_2 = false;
            removeItemTime(player, 6580);

            player.itemTime.isRongXuong_3 = false;
            removeItemTime(player, 6581);

            player.itemTime.isUseMayDoNgocBi = false;
            removeItemTime(player, 434);

            player.itemTime.isUseMayDoLinhHon = false;
            removeItemTime(player, 11493);
           
            player.itemTime.isUseMayDoSieuHoa = false;
            removeItemTime(player, 27021);

            player.itemTime.isUseBuaTNSMDetu = false;
            removeItemTime(player, 13540);

            player.itemTime.isUseCoBonLa = false;
            removeItemTime(player, 13618);

            player.itemTime.isUseSauRieng = false;
            removeItemTime(player, 13747);

            player.itemTime.isUseSatThuongChuan = false;
            removeItemTime(player, 12766);

            player.itemTime.isUseSatThuongChuan2 = false;
            removeItemTime(player, 12767);

            player.itemTime.isUseHoiSieuCap = false;
            removeItemTime(player, 27019);

            player.itemTime.isUseRocket1h = false;
            removeItemTime(player, 27009);

            player.itemTime.isUseKamejoko2 = false;
            removeItemTime(player, 30867);

            player.itemTime.isUseKamejoko = false;
            removeItemTime(player, 30867);

            player.itemTime.isUsePhanSatThuong3 = false;
            removeItemTime(player, 30866);

            player.itemTime.isUsePhanSatThuong2 = false;
            removeItemTime(player, 30866);

            player.itemTime.isUsePhanSatThuong = false;
            removeItemTime(player, 30866);

            player.itemTime.isUseNedon2 = false;
            removeItemTime(player, 32752);

            player.itemTime.isUseNedon = false;
            removeItemTime(player, 32752);

            player.itemTime.isUseChiMang3 = false;
            removeItemTime(player, 32751);

            player.itemTime.isUseChiMang2 = false;
            removeItemTime(player, 32751);

            player.itemTime.isUseChuotMap = false;
            removeItemTime(player, 12831);

            player.itemTime.isUseComGaQuay = false;
            removeItemTime(player, 12396);

            player.itemTime.isUseMiThangLong = false;
            removeItemTime(player, 12395);

            player.itemTime.isUseFoodMeoDen1 = false;
            removeItemTime(player, 10890);

            player.itemTime.isUseFoodMeoDen2 = false;
            removeItemTime(player, 10891);

            player.itemTime.isUseBanhTet = false;
            removeItemTime(player, 7079);

            player.itemTime.isUseBanhTrung = false;
            removeItemTime(player, 7080);

            player.itemTime.isUseNCD = false;
            removeItemTime(player, 11173);

            player.itemTime.isUseGTPT = false;
            removeItemTime(player, 3778);

            player.itemTime.IsBocPha = false;
            removeItemTime(player, 10713);

            player.itemTime.IsDuoiKhi = false;
            removeItemTime(player, 5072);

            player.itemTime.iscuarangme = false;
            removeItemTime(player, 8060);

            player.itemTime.isbachtuocnuong = false;
            removeItemTime(player, 8061);

            player.itemTime.istomtambot = false;
            removeItemTime(player, 8062);

            player.itemTime.Isthuocmothuong = false;
            removeItemTime(player, 9068);

            player.itemTime.Isthuocmodacbiet = false;
            removeItemTime(player, 9068);

            player.itemTime.Ishamburgersau = false;
            removeItemTime(player, 8247);

            player.itemTime.Isbanhgatonhen = false;
            removeItemTime(player, 8246);

            player.itemTime.IsSupbihacam = false;
            removeItemTime(player, 8244);

            player.itemTime.IsKeoMotMat = false;
            removeItemTime(player, 8243);

            player.itemTime.IsKhauTrang = false;
            removeItemTime(player, 7149);

            player.itemTime.iscommenson = false;
            removeItemTime(player, 5829);

            player.itemTime.istrbsd = false;
            removeItemTime(player, 24360);

            player.itemTime.istrbhp = false;
            removeItemTime(player, 24361);

            player.itemTime.istrbki = false;
            removeItemTime(player, 24362);

            player.itemTime.istrbsdxd = false;
            removeItemTime(player, 24360);

            player.itemTime.istrbhpxd = false;
            removeItemTime(player, 24361);

            player.itemTime.istrbkixd = false;
            removeItemTime(player, 24362);

            player.itemTime.isUseTHUOCTANGHINH = false;
            removeItemTime(player, 24252);

            player.itemTime.isUseTHUOCTANGHINH10 = false;
            removeItemTime(player, 24252);

            player.itemTime.isUseHongDao0 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao1 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao3 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao5 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao10 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao25 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao50 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao99 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseHongDao999 = false;
            removeItemTime(player, 4547);

            player.itemTime.isUseBoHuyet = false;
            removeItemTime(player, 2755);

            player.itemTime.isUseBoKhi = false;
            removeItemTime(player, 2756);

            player.itemTime.isUseGiapXen = false;
            removeItemTime(player, 2757);

            player.itemTime.isUseCuongNo = false;
            removeItemTime(player, 2754);

            player.itemTime.isUseAnDanh = false;
            removeItemTime(player, 2760);

            player.itemTime.isUseBoHuyet2 = false;
            removeItemTime(player, 10714);

            player.itemTime.isUseBoKhi2 = false;
            removeItemTime(player, 10715);

            player.itemTime.isUseGiapXen2 = false;
            removeItemTime(player, 10712);

            player.itemTime.isUseCuongNo2 = false;
            removeItemTime(player, 10716);

            player.itemTime.isUseAnDanh2 = false;
            removeItemTime(player, 10717);

            player.itemTime.isUseTDLT = false;
            removeItemTime(player, 4387);

            player.itemTime.isUseMayDo = false;
            removeItemTime(player, 2758);

            player.itemTime.isOpenPower = false;
            removeItemTime(player, 3783);
 
            Service.gI().point(player);
        } catch (Exception e) {
            Logger.logException(ItemTimeService.class, e);
        }
    }

    //bật tđlt
    public void turnOnTDLT(Player player, Item item) {
        int min = 0;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 1) {
                min = io.param;
                io.param = 0;
                break;
            }
        }
        player.itemTime.isUseTDLT = true;
        player.itemTime.timeTDLT = min * 60 * 1000;
        player.itemTime.lastTimeUseTDLT = System.currentTimeMillis();
        sendCanAutoPlay(player);
        sendItemTime(player, 4387, player.itemTime.timeTDLT / 1000);
        InventoryService.gI().sendItemBag(player);
    }

    //tắt tđlt
    public void turnOffTDLT(Player player, Item item) {
        player.itemTime.isUseTDLT = false;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 1) {
                io.param += (short) ((player.itemTime.timeTDLT - (System.currentTimeMillis() - player.itemTime.lastTimeUseTDLT)) / 60 / 1000);
                break;
            }
        }
        sendCanAutoPlay(player);
        removeItemTime(player, 4387);
        InventoryService.gI().sendItemBag(player);
    }

    public void sendCanAutoPlay(Player player) {
        Message msg;
        try {
            msg = new Message(-116);
            msg.writer().writeByte(player.itemTime.isUseTDLT ? 1 : 0);
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(ItemTimeService.class, e);
        }
    }

    public void sendTextDoanhTrai(Player player) {
        if (player.clan != null && !player.clan.haveGoneDoanhTrai
                && player.clan.lastTimeOpenDoanhTrai != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.lastTimeOpenDoanhTrai) / 1000);
            int secondsLeft = (RedRibbonHQ.TIME_DOANH_TRAI / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, DOANH_TRAI, "Trại độc nhãn:", secondsLeft);
        }
    }

    public void sendTextTimePickDoanhTrai(Player player) {
        if (player.clan != null && player.clan.doanhTrai != null && player.clan.doanhTrai.isTimePicking) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.doanhTrai.lastTimePick) / 1000);
            int secondsLeft = (RedRibbonHQ.TIME_PICK_DOANH_TRAI / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, DOANH_TRAI, "Trại độc nhãn:", secondsLeft);
        }
    }

    public void sendTextBanDoKhoBau(Player player) {
        if (player.clan != null
                && player.clan.lastTimeOpenBanDoKhoBau != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.lastTimeOpenBanDoKhoBau) / 1000);
            int secondsLeft = (TreasureUnderSea.TIME_BAN_DO_KHO_BAU / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, BAN_DO_KHO_BAU, "Hang kho báu:", secondsLeft);
        }
    }

    public void sendTextXinbato(Player player) {
        sendTextTime(player, BAN_DO_KHO_BAU, "Tìm nước cho Xinbatô ở đảo Kame hoặc đảo Guru", 30);
    }

    public void sendTextConDuongRanDoc(Player player) {
        if (player.clan != null
                && player.clan.lastTimeOpenConDuongRanDoc != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.lastTimeOpenConDuongRanDoc) / 1000);
            int secondsLeft = (SnakeWay.TIME_CON_DUONG_RAN_DOC / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, CON_DUONG_RAN_DOC, "Con đường rắn độc:", secondsLeft);
        }
    }

    public void sendTextKhiGasHuyDiet(Player player) {
        if (player.clan != null
                && player.clan.lastTimeOpenKhiGasHuyDiet != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.lastTimeOpenKhiGasHuyDiet) / 1000);
            int secondsLeft = (DestronGas.TIME_KHI_GAS_HUY_DIET / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, KHI_GAS_HUY_DIET, "Khí gas hủy diệt:", secondsLeft);
        }
    }
    
    public void sendTextMapBossBangHoi(Player player) {
        if (player.clan != null && player.clan.lastTimeOpenBossOfTheGangs != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.lastTimeOpenBossOfTheGangs) / 1000);
            int secondsLeft = (BossOfTheGangs.TIME_MAP_BOSS_BANG_HOI / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, MAP_BOSS_BANG_HOI, "Hạ Boss Bang Hội [lần thứ " + (player.clan.boss_clan_round - 1) + "] thời gian: ", secondsLeft);
        }
    }

    public void removeTextDoanhTrai(Player player) {
        removeTextTime(player, DOANH_TRAI);
    }

    public void removeTextBanDoKhoBau(Player player) {
        removeTextTime(player, BAN_DO_KHO_BAU);
    }

    public void removeTextConDuongRanDoc(Player player) {
        removeTextTime(player, CON_DUONG_RAN_DOC);
    }

    public void removeTextKhiGasHuyDiet(Player player) {
        removeTextTime(player, KHI_GAS_HUY_DIET);
    }
    
    public void removeTextMapBossBangHoi(Player player) {
        removeTextTime(player, MAP_BOSS_BANG_HOI);
    }
    
    public void removeTextTime(Player player, byte id) {
        sendTextTime(player, id, "", 0);
    }
    
    public void sendTextTime(Player player, byte id, String text, int seconds) {
        Message msg;
        try {
            msg = new Message(65);
            msg.writer().writeByte(id);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(seconds);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(ItemTimeService.class, e);
        }
    }

    public void sendItemTime(Player player, int itemId, int time) {
        Message msg;
        try {
            msg = new Message(-106);
            msg.writer().writeShort(itemId);
            msg.writer().writeShort(time);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(ItemTimeService.class, e);
        }
    }

    public void removeItemTime(Player player, int itemTime) {
        sendItemTime(player, itemTime, 0);
    }

}
