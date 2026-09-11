package nro.player;

import nro.bot.NewBot;
import nro.map.Hirudegarn.MapHirudegarn;
import nro.card.Card;
import nro.map.MajinBuu12H.MajinBuu12H;
import nro.map.QuaKhu.QuaKhu;
import nro.skill.PlayerSkill;
import java.util.List;
import nro.clan.Clan;
import nro.intrinsic.IntrinsicPlayer;
import models.Item.Item;
import models.Item.ItemTime;
import nro.npc.Special.MagicTree;
import consts.ConstPlayer;
import consts.ConstTask;
import nro.npc.Special.MabuEgg;
import nro.mob.DeTrung;
import Data.DataGame;
import nro.clan.ClanMember;
import nro.map.Zone;
import nro.map.NguHanhSon.NguHanhSon;
import nro.effect.EffectFlagBag;
import nro.effect.EffectSkin;
import nro.effect.EffectSkill;
import nro.effect.EffectSkillService;
import nro.matches.TYPE_LOSE_PVP;
import nro.npc.Special.BillEgg;
import nro.skill.Skill;
import nro.services.Service;
import nro.task.TaskPlayer;
import nro.inventory.Inventory;
import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossNomalService;
import Utils.Functions;
import nro.server.Client;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.services.FriendAndEnemyService;
import nro.services.MapService;
import nro.services.DetuService;
import nro.services.PlayerService;
import nro.services.TaskService;
import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import nro.skill.NewSkill;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstAchievement;
import consts.ConstDailyGift;
import consts.ConstNpc;
import event.EventManager;
import jbcd.ConnectDB;
import java.sql.Timestamp;
import nro.map.ItemMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import jbcd.dao.HistoryTransactionDAO;
import jbcd.dao.PlayerDAO;
import lombok.Getter;
import lombok.Setter;
import nro.card.RadarService;
import models.Item.ItemEvent;
import models.Item.ItemOption;
import models.Item.ItemService;
import models.Item.ItemTimeService;
import models.Reward.RewardBlackBall;
import network.io.Message;
import network.session.MySession;
import nro.achievement.Achievement;
import nro.badges.Badges;
import nro.badges.BadgesData;
import nro.badges.BadgesTask;
import nro.badges.BadgesTaskService;
import nro.boss.map.TrainingBoss.TopKillWhisManager;
import nro.boss.map.TrainingBoss.Traning;
import nro.map.BlackBallWar.BlackBallWarService;
import nro.map.DragonBallNamec.NgocRongNamec;
import nro.map.DragonNamecWar.TranhNgoc;
import nro.map.DragonNamecWar.TranhNgocService;
import nro.npc.NonInteractiveNPC;
import nro.map.MajinBuu14H.MaBuHold;
import nro.map.MajinBuu14H.MajinBuu14H;
import nro.map.SuperDivineWater.SuperDivineWater;
import nro.map.RankSuper.SuperRank;
import nro.map.The23rdMartialArtCongress.The23rdMartialArtCongress;
import nro.map.The23rdMartialArtCongress.The23rdMartialArtCongressManager;
import nro.map.TreasureUnderSea.TrapMap;
import nro.matches.IPVP;
import nro.minigame.ChonAiDay;
import nro.card.RadarCard;
import nro.clan.Buff;
import nro.clan.ClanService;
import nro.combine.Combine;
import nro.dragon.ChristMasEvent.ShenronChristMasEvent;
import nro.dragon.HalloweenEvent.ShenronHalloweenEvent;
import nro.map.MajinBuu12H.FightMabu;
import nro.npc.Special.MelonPlant;
import nro.player.DailyGift.DailyGiftData;
import nro.player.DailyGift.DailyGiftService;
import nro.satellite.Satellite;

public class Player implements Runnable {
    public int hienThiHopThe = 1;
    public long lastTimeEatPea;

    @Setter
    @Getter
    private MySession session;
    @Getter
    @Setter
    private boolean Saving;
    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember;
    public long accountCreatedDays;
    public short head;
    public int deltaTime;
    public byte typePk;
    public byte cFlag;
    public boolean haveTennisSpaceShip;
    private long ResetDateOut;
    public boolean resetdame = false;
    public long lastTimeDame;
    public long dametong = 0;
    public long lastTimeDameMayDam;
    public long TotalDameMayDam = 0;
    public Timestamp lastimelogin;
    public Date firstTimeLogin;
    public Timestamp LastTimeLoginGame;
    public long lastTimeAttack;
    public int tlDameAdd = 0;
    public int tlDameClanAdd = 0;
    public int tlHpClanAdd = 0;
    public int tlMpClanAdd = 0;
    public boolean canGeFirstTimeLogin;
    public int tempItemIndex = -1;
    public Item itemThrow_Drop = null;
    public Item Item_ChangePet = null;
    public transient List<HistoryTransactionDAO.TransactionLog> lichSuGiaoDichDangXem;
    public transient int trangThaiLichSuGd;
    public long TopMayDamTraiDat;
    public long TopMayDamNamec;
    public long TopMayDamXayda;
    public int numUseSkill = 0;
    public long lastTimeHoiskill;
    public int DiemDanhBangHoi;
    public int VeTangNgoc_SoLuongNgoc;
    public Player Player_NhanNgoc;
    public int VeTangHongNgoc_SoLuongHongNgoc;
    public Player Player_NhanHongNgoc;
    public int cuoc;
    public int rubyWin = 0;
    public String nameClan;
    public long lastTimeDropTail;
    public long lastTimeChangeBadges;
    public long MapTransitionTime;
    private static final long TIME_TRUNG_THU = 120000;
    private static long LAST_TIME_UPDATE_LOGIN;
    private int wrongPasswordAttempts = 0;
    public byte RestartNauBanh = 0;
    public boolean khisukien = false;
    public long lastTimeCallKhi = 0;
    public long Lasttimekhisukien;
    private long LastTimeBom;
    public int partDanhHieu;
    public boolean titleitem;
    public byte PointRank;
    public byte typeTabPet = 0;
    public byte typeBox = 0;
    public int typeVip = -1;
    public boolean isPlayer;
    public boolean isDeTu;
    public boolean isBo;
    public boolean isMe;
    public boolean isPetFollow;
    public boolean isDuongTang;
    public boolean isBoss;
    public boolean isNguoiYeu;
    public boolean isConOne;
    public boolean isConTwo;
    public boolean isConThree;
    public boolean isPhanThan;
    public int nhapNgocRongTimes = 1;

    //SỰ KIỆN HALLOWEEN
    public int NhanKeoHayBiGheoNpc_1 = 0;
    public int NhanKeoHayBiGheoNpc_2 = 0;
    public int NhanKeoHayBiGheoNpc_3 = 0;
    public int NhanKeoHayBiGheoNpc_4 = 0;
    public int NhanKeoHayBiGheoNpc_5 = 0;
    public int NhanKeoHayBiGheoNpc_6 = 0;
    public int NhanKeoHayBiGheoNpc_7 = 0;
    public int NhanKeoHayBiGheoNpc_8 = 0;
    public int NhanKeoHayBiGheoNpc_9 = 0;
    public int NhanKeoHayBiGheoNpc_10 = 0;
    public int NhanKeoHayBiGheoNpc_11 = 0;
    public int NhanKeoHayBiGheoNpc_12 = 0;
    public int NhanKeoHayBiGheoNpc_13 = 0;
    public int NhanKeoHayBiGheoNpc_14 = 0;
    public int NhanKeoHayBiGheoNpc_15 = 0;

    //ĐUA TOP SỰ KIỆN
    public long CheckTrongNgay;
    //SK TẾT
    public int DuaTopTangLiXi = 0;
    public int DuaTopMoLiXi = 0;
    public int DuaTopBanPhaoHoa = 0;
    public int DuaTopBanPhaoHoaVIP = 0;

    //SK GIÁNG SINH
    public int DuaTopTrangTriCayNoel = 0;
    public int DuaTopCheTaoNguoiTuyet = 0;
    public int DuaTopCheTaoNguoiTuyetBangGia = 0;
    public int DuaTopDotDiem = 0;

    //SỰ KIỆN VULAN
    public int DuaTopPhaoHoaVuLan;
    public int DuaTopHoaDang;
    public int DuaTopHoaDangCoLoiChuc;

    //SỰ KIỆN HALLOWEEN
    public int DuaTopMoHopMaQuy;
    public int DuaTopThiepHalloween;

    //SỰ KIỆN 8-3
    public int DuaTopMoThiep83;
    public int DuaTopTangBongHoaHong;

    //SỰ KIỆN TRUNG THU
    public int DuaTopLamBanhTrungThu;
    public int DuaTopMoHopTrungThuDacBiet;
    public int UocMienPhi;

    //SỰ KIỆN HÙNG VƯƠNG
    public int NhanQuaHungVuongFree;
    public long lastTimeNauBanhHungVuong;
    public int typeBanhDangNau;
    public int DuaTopMoHopQuaGioTo;
    public int DuaTopDangBanhHungVuong;
    public int DuaTopMoTrungRongVang;
    public int DuaTopDoiDuaHau;

    //SỰ KIỆN BLACK FRIDAY
    public int DuaTopMoHopBlackFriday;
    public int DuaTopMuaSamBlackFriday;

    //SỰ KIỆN 20/10
    public int DuaTopMoHop20_10;
    public int DuaTopMoThiepChuc;
    public int DuaTopTangThiepChuc;

    public boolean HoTongDuongTang;
    public int MapHoTong;
    public long lastTimeDuongTang;

    public int NhanLiXiForNPC_1 = 0;
    public int NhanLiXiForNPC_2 = 0;
    public int NhanLiXiForNPC_3 = 0;
    public int NhanLiXiForNPC_4 = 0;
    public int NhanLiXiForNPC_5 = 0;
    public int NhanLiXiForNPC_6 = 0;
    public int NhanLiXiForNPC_7 = 0;
    public int NhanLiXiForNPC_8 = 0;
    public int NhanLiXiForNPC_9 = 0;
    public int NhanLiXiForNPC_10 = 0;
    public int NhanLiXiForNPC_11 = 0;
    public int NhanLiXiForNPC_12 = 0;
    public int NhanLiXiForNPC_13 = 0;
    public int NhanLiXiForNPC_14 = 0;
    public int NhanLiXiForNPC_15 = 0;
    public int NhanLiXiForNPC_16 = 0;
    public int NhanLiXiForNPC_17 = 0;
    public int NhanLiXiForNPC_18 = 0;
    public int NhanLiXiForNPC_19 = 0;
    public int NhanLiXiForNPC_20 = 0;
    public int NhanLiXiForNPC_21 = 0;
    public int NhanLiXiForNPC_22 = 0;
    public int NhanLiXiForNPC_23 = 0;
    public int NhanLiXiForNPC_24 = 0;
    public int NhanLiXiForNPC_25 = 0;

    //TRANH NGỌC NAMEK
    public boolean isHoldNamecBallTranhDoat;
    public int tempIdNamecBallHoldTranhDoat = -1;
    public long lastTimePickItem;
    public long lastTimeUpdateBallWar;

    //GIẢI ĐẠI HỘI VÕ THUẬT
    public long totalDamageTaken;
    public boolean thongBaoChangeMap;
    public String textThongBaoChangeMap;
    public boolean thongBaoThua;
    public String textThongBaoThua;

    //ĐẠI HỘI VÕ THUẬT 23
    public int levelWoodChest;
    public long goldChallenge;
    public long rubyChallenge;
    public long lastTimeRewardWoodChest;
    public List<Item> itemsWoodChest = new ArrayList<>();
    public int indexWoodChest;
    public long lastTimePKDHVT23;
    public boolean lostByDeath;

    //VÕ ĐÀI SINH TỬ
    public boolean isPKDHVT;
    public long lastTimePKVoDaiSinhTu;
    public boolean haveRewardVDST;
    public int thoiVangVoDaiSinhTu;
    public long timePKVDST;
    public int binhChonHatMit;
    public int binhChonPlayer;
    public Zone zoneBinhChon;

    //MÃ BẢO VỆ
    public int mbv = 0;
    public boolean baovetaikhoan;
    public long mbvtime;

    //CAPCHA
    public String captcha = "";

    //MAP MABU 12H
    public int timeGohome;
    public long lastUpdateGohomeTime;
    public boolean goHome;

    //MAP MABU 14H
    public MajinBuu14H maBu2H;
    public boolean isMabuHold;
    public MaBuHold maBuHold;
    public int precentMabuHold;
    public boolean isPhuHoMapMabu;

    //Nhân Bản
    public boolean isCopy;
    public long lastPkCommesonTime;

    //cừu sát
    public int timesPerDayCuuSat;
    public long lastTimeCuuSat;
    public boolean nhanVangNangVIP;
    public boolean nhanDeTuNangVIP;
    public boolean nhanSKHVIP;

    //TUẦN LỘC
    public boolean canReward_TuanLoc;
    public boolean changeMapVIP_TuanLoc;
    public boolean haveReward_TuanLoc;

    //DẮT LÂN
    public boolean canReward;
    public boolean changeMapVIP;
    public boolean haveReward;

    //DẮT MÈO ĐEN
    public boolean canReward_MeoDen;
    public boolean changeMapVIP_MeoDen;
    public boolean haveReward_MeoDen;

    //DẮT MÈO ĐEN
    public boolean canReward_PiLong;
    public boolean changeMapVIP_PiLong;
    public boolean haveReward_PiLong;

    //DẮT BÉ NA
    public boolean canReward_PeNa;
    public boolean changeMapVIP_PeNa;
    public boolean haveReward_PeNa;

    //MAP DOANH TRẠI
    public long lastTimeJoinDT;

    //MAP CON ĐƯỜNG RẮN ĐỘC
    public boolean joinCDRD;
    public long lastTimeJoinCDRD;
    public boolean talkToThuongDe;
    public boolean talkToThanMeo;
    public long timeChangeMap144;
    public int levelCDRDDone;
    public long timeCDRDDone;
    public long lastTimeUpdateTopCDRD;

    //MAP KHÍ GAS HUỶ DIỆT
    public int levelKhiGasDone;
    public long timeKhiGasDone;
    public long lastTimeUpdateTopKhiGas;

    //MAP BẢN ĐỒ KHO BÁU
    public int timesPerDayBDKB = 0;
    public long lastTimeJoinBDKB;
    public int bdkb_countPerDay;
    public long bdkb_lastTimeJoin;
    public boolean bdkb_isJoinBdkb;
    public int levelBDKBDone;
    public long timeBDKBDone;
    public long lastTimeUpdateTopBDKB;

    //MAP TÂY KARIN
    public boolean callBossPocolo;
    public Zone zoneSieuThanhThuy;
    public boolean winSTT;
    public long lastTimeWinSTT;
    public long lastTimeUpdateSTT;

    public String notify = null;

    //DATA LUYỆN TẬP
    public boolean isOffline = false;
    public int levelLuyenTap;
    public boolean isThachDau;
    public int tnsmLuyenTap;
    public boolean dangKyTapTuDong;
    public long lastTimeOffline;
    public int mapIdDangTapTuDong;
    public int lastMapOffline;
    public int lastZoneOffline;
    public int lastXOffline;
    public String thongBaoTapTuDong;
    public boolean teleTapTuDong;
    public Traning traning;

    //ĐỆ TỦ ATTACK
    public boolean doesNotAttack;
    public long lastTimePlayerNotAttack;
    public int timeNotAttack = 1800000;
    public List<Player> temporaryEnemies = new ArrayList<>();
    public boolean justRevived;
    public long lastTimeRevived;

    //-----------------------------GIA ĐÌNH-------------------------------------
    //BỐ ATTACK
    public boolean doesNotAttackBo;
    public long lastTimePlayerNotAttackBo;
    public int timeNotAttackBo = 1800000;
    public List<Player> temporaryEnemiesBo = new ArrayList<>();
    public boolean justRevivedBo;
    public long lastTimeRevivedBo;
    public String NameFather;
    //MẸ ATTACK
    public boolean doesNotAttackMe;
    public long lastTimePlayerNotAttackMe;
    public int timeNotAttackMe = 1800000;
    public List<Player> temporaryEnemiesMe = new ArrayList<>();
    public boolean justRevivedMe;
    public long lastTimeRevivedMe;
    public String NameMother;
    //NguoiYeu ATTACK
    public boolean doesNotAttackNguoiYeu;
    public long lastTimePlayerNotAttackNguoiYeu;
    public int timeNotAttackNguoiYeu = 1800000;
    public List<Player> temporaryEnemiesNguoiYeu = new ArrayList<>();
    public boolean justRevivedNguoiYeu;
    public long lastTimeRevivedNguoiYeu;
    //CON 1 ATTACK
    public boolean doesNotAttackConone;
    public long lastTimePlayerNotAttackConone;
    public int timeNotAttackConone = 1800000;
    public List<Player> temporaryEnemiesConone = new ArrayList<>();
    public boolean justRevivedConone;
    public long lastTimeRevivedConone;
    //CON 2 ATTACK
    public boolean doesNotAttackContwo;
    public long lastTimePlayerNotAttackContwo;
    public int timeNotAttackContwo = 1800000;
    public List<Player> temporaryEnemiesContwo = new ArrayList<>();
    public boolean justRevivedContwo;
    public long lastTimeRevivedContwo;
    //CON 3 ATTACK
    public boolean doesNotAttackConthree;
    public long lastTimePlayerNotAttackConthree;
    public int timeNotAttackConthree = 1800000;
    public List<Player> temporaryEnemiesConthree = new ArrayList<>();
    public boolean justRevivedConthree;
    public long lastTimeRevivedConthree;
    //--------------------------------------------------------------------------

    //MAP
    public int xSend;
    public int ySend;
    public boolean isFly;

    //SHENRON EVENT
    public long lastTimeShenronAppeared_Halloween;
    public boolean isShenronAppear_Halloween;
    public ShenronHalloweenEvent shenronEvent_Halloween;

    //SHENRON EVENT CHRIST MAS
    public long lastTimeShenronAppeared_Christmas;
    public boolean isShenronAppear_Christmas;
    public ShenronChristMasEvent shenronEvent_Christmas;

    //CHIBI
    public int typeChibi;
    public long lastTimeChibi;
    public long lastTimeUpdateChibi;

    //NRNM
    public short idNRNM = -1;
    public short idGo = -1;
    public long lastTimePickNRNM;

    //BOT
    public boolean isBot;
    private long TimeSpawnBot;
    public boolean isBot_New;
    public boolean isBotLogin;
    public boolean isBot_Event;
    public boolean isBot_Valentine;

    //TX
    public int goldNormar;
    public int goldVIP;

    //AUTO
    public boolean autoHP = false;
    public boolean autoKI = false;
    public boolean autoSD = false;
    public boolean autoGiap = false;

    //card
    public int THE_TUAN;
    public long LASTTIME_THE_TUAN;
    public int THE_THANG;
    public long LASTTIME_THE_THANG;
    public int THE_NAM;
    public long LASTTIME_THE_NAM;
    public int THE_CHI_TON;
    public long LASTTIME_THE_CHI_TON;

    //ĐIỂM DANH
    public int DIEM_DANH;
    public int CHECK_DAY_ON_ONE;
    public int DIEM_DANH_TUAN;
    public int CHECK_DAY_ON_TUAN;
    public int DIEM_DANH_THANG;
    public int CHECK_DAY_ON_THANG;
    public int DIEM_DANH_NAM;
    public int CHECK_DAY_ON_NAM;
    public int DIEM_DANH_CHI_TON;
    public int CHECK_DAY_ON_CHI_TON;

    //DANH HIỆU
    public boolean isUseDanhHieu_ThienTu;
    public long LastTimeDanhHieu_ThienTu;
    //
    public boolean isUseDanhHieu_2;
    public long LastTimeDanhHieu_2;
    //
    public boolean isUseDanhHieu_3;
    public long LastTimeDanhHieu_3;
    //
    public boolean isUseDanhHieu_4;
    public long LastTimeDanhHieu_4;

    //SỰ KIỆN TẾT
    public int slBanhChung;
    public int slBanhTet;

    //TRADE
    public List<Item> itemsTradeWVP = new ArrayList<>();
    public long goldTradeWVP;
    public boolean tradeWVP;
    public long plIdWVP;

    public long lastTimeTranformation;
    public int isbienhinh;
    public byte countBDKB;
    public boolean firstJoinBDKB;
    public long lastimeJoinBDKB;

    public int goldTai;
    public long last_time_dd;
    public int goldXiu;
    public boolean beforeDispose;

    public byte ErrorMap = 0;
    public byte ErrorLocation = 0;
    public byte ErrorPay = 0;

    public long thoigianduhanh;
    public boolean isthoigianduhanh;

    public boolean ThueOil;
    public boolean ThueMacki;
    public boolean ThueGas;
    public boolean ThueElec;
    public boolean GiaiPhongAnNgoKhong;
    public boolean Boss;

    public byte giaiphongan;
    public short solanhotong;

    public byte NhanThuocTrongNgay;
    public long TimeTrongNgay;

    public boolean bosshoimau;

    public byte trbne;
    public byte trbgiap;

    public int actived;
    public int account_id;
    public long demthoigian;

    public long timeres;

    public int timedanhboss;
    public int bossxuathien;
    public IPVP pvp;

    public byte maxTime = 30;
    public byte type = 0;
    public long timeoff = 0;

    @Setter
    @Getter
    private Buff buff;
    public PointFusion pointfusion;
    public List<DailyGiftData> dailyGiftData = new ArrayList<>();
    public PlayerEvent event;
    public SaiBaMen Saibamen;
    public boolean IsSaibamen;
    public Badges badges;
    public int mapIdBeforeLogout;
    public PhanThan PhanThan;
    public List<Zone> mapBlackBall;
    public List<Zone> mapMaBu;
    public List<BadgesData> dataBadges = new ArrayList<>();
    public List<BadgesTask> dataTaskBadges = new ArrayList<>();
    public ItemEvent itemEvent;
    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public Detu Detu;
    public PetFollow PetFollow;
    public DuongTang Duongtang;
    public SuperRank superRank;
    public DeTrung DeTrung;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public BillEgg billEgg;
    public MelonPlant duahau;
    public TaskPlayer playerTask;
    public ItemTime itemTime;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public Combine combine;
    public IDMark iDMark;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;
    public FightMabu fightMabu;
    public NewSkill newSkill;
    public Achievement achievement;
    public Clan clan;
    public ClanMember clanMember;
    public Satellite satellite;
    public List<Friend> friends;
    public List<Enemy> enemies;
    private DropItem dropItem;
    public List<Card> Cards = new ArrayList<>();

    public boolean isAutoHoiSinh = false;
    public int SoNgayTaoAcc;
    public byte QuocTich;

    public long timeChangeZone;
    public long lastUseOptionTime;

    public long lastTimeHoiPhuc;
    public float DucNTdamethanmeo;

    public long TimeChatKhukhu;

    public List<Integer> idEffChar = new ArrayList<>();
    public List<Integer> BoughtSkill = new ArrayList<>();
    public PlayerSkill LearnSkill;
    public int goldChan;
    public int goldLe;
    public boolean receivedGift50k;
    public boolean receivedGift100k;
    public int[] mocnap = new int[]{0, 0, 0, 0, 0};

    public Player() {
        LearnSkill = new PlayerSkill();
        lastUseOptionTime = System.currentTimeMillis();
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory();
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer();
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag();
        fightMabu = new FightMabu(this);
        satellite = new Satellite();
        iDMark = new IDMark();
        combine = new Combine();
        playerTask = new TaskPlayer();
        friends = new ArrayList<>();
        enemies = new ArrayList<>();
        itemTime = new ItemTime(this);
        charms = new Charms();
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
        newSkill = new NewSkill(this);
        achievement = new Achievement(this);
        dropItem = new DropItem(this);
        superRank = new SuperRank(this);
        itemEvent = new ItemEvent(this);
        badges = new Badges();
        event = new PlayerEvent(this);
        traning = new Traning();
        pointfusion = new PointFusion(this);
        buff = Buff.NONE;
    }

    //--------------------------------------------------------------------------
    private int getPlayerWhis(List<Player> list, Player player) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == player.id) {
                return i + 1;
            }
        }
        return -1;
    }

    public PointFusion getPointfusion() {
        return this.pointfusion;
    }

    public int getWrongPasswordAttempts() {
        return wrongPasswordAttempts;
    }

    public void increaseWrongPasswordAttempts() {
        wrongPasswordAttempts++;
    }

    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        }
        return true;
    }

    public void setSession(MySession session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public MySession getSession() {
        return this.session;
    }

    public boolean isMaster() {
        return isDeTu || isBo || isMe || isPetFollow || isDuongTang || IsSaibamen || isPhanThan || isNguoiYeu;
    }

    public Player getMaster() {
        if (this.isDeTu) {
            return ((Detu) this).master;
        } else if (this.isPetFollow) {
            return ((PetFollow) this).master;
        } else if (this.isDuongTang) {
            return ((DuongTang) this).master;
        } else if (this.IsSaibamen) {
            return ((SaiBaMen) this).player;
        } else if (this.isPhanThan) {
            return ((PhanThan) this).master;
        } else {
            return this;
        }
    }

    public static long setIdForPet(Player player, long id) {
        if (player.isNguoiYeu) {
            return -3_000_000_000L + id;
        } else if (player.isDeTu) {
            return -id;
        } else if (player.isBo) {
            return -1_000_000_000L + id;
        } else if (player.isMe) {
            return -2_000_000_000L + id;
        } else {
            return id;
        }
    }

    public boolean isPl() {
        return isPlayer && !isDeTu && !isBo && !isMe && !isBoss && !isPetFollow && !isDuongTang && !isPhanThan && !isBot_Valentine && !isBot && !isBot_Event && !isBot_New && !IsSaibamen && !isNguoiYeu && !isConOne && !isConTwo && !isConThree
                && !(this instanceof NonInteractiveNPC);
    }

    public boolean isPlandBot() {
        return (isPlayer || isBot || isBot_New || isBot_Event || isBot_Valentine) && !isDeTu && !isBo && !isMe && !isBoss && !isPetFollow && !isDuongTang && !isPhanThan && !IsSaibamen && !isConOne && !isConTwo && !isConThree && !isNguoiYeu
                && !(this instanceof NonInteractiveNPC);
    }

    public boolean isPlMan() {
        return (isPlayer || isDeTu || isBo || isMe || isNguoiYeu || isConOne || isConTwo || isConThree || isPhanThan) && !isBoss && !isPetFollow && !isDuongTang && !isBot && !isBot_Event && !isBot_New && !isBot_Valentine && !IsSaibamen
                && !(this instanceof NonInteractiveNPC);
    }

    public boolean getBot() {
        return (isBot || isBot_New || isBot_Event || isBot_Valentine);
    }

    public boolean isActive() {
        return (this.isPl() && this.session != null && this.session.actived);
    }

    public boolean isFounder() {
        return this.session.isFounder;
    }

    public boolean isJail() {
        return this.session.isJail;
    }

    @Override
    public void run() {
        Functions.sleep(500);
        while (!Maintenance.isRunning && session != null && session.isConnected() && this.name != null) {
            long st = System.currentTimeMillis();
            update();
            Functions.sleep(Math.max(1000 - (System.currentTimeMillis() - st), 10));
        }
    }

    public void start() {
        new Thread(this, "Update player " + this.name).start();
    }

    public void update() {
        if (!this.beforeDispose) {
            try {
                if (this.zone != null || (!this.isPl() && this.zone == null)) {
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (this.isPl() && this.zone != null && this.zone.map.mapId == this.gender + 21 && (TaskService.gI().getIdTask(this) == ConstTask.TASK_0_0 || TaskService.gI().getIdTask(this) == ConstTask.TASK_0_1)) {
                        this.playerTask.taskMain.index = 2;
                        TaskService.gI().sendTaskMain(this);
                    }
                }
                if (isPl() && MapService.gI().isHome(this.zone.map.mapId) && iDMark != null && iDMark.isBan() && Util.canDoWithTime(iDMark.getLastTimeBan(), 5000)) {
                    Client.gI().kickSession(session);
                    return;
                }
                if ((this.zone != null && !MapService.gI().isHome(this.zone.map.mapId)) || (!this.isPl() && this.zone == null)) {
                    if (isPl() && iDMark != null && iDMark.isBan() && Util.canDoWithTime(iDMark.getLastTimeBan(), 5000)) {
                        Client.gI().kickSession(session);
                        return;
                    }
                    if (PhanThan != null) {
                        PhanThan.update();
                    }
                    if (Saibamen != null) {
                        Saibamen.update();
                    }
                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkill != null) {
                        effectSkill.update();
                    }
                    if (DeTrung != null) {
                        DeTrung.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (Detu != null) {
                        Detu.update();
                    }
                    if (PetFollow != null) {
                        PetFollow.update();
                    }
                    if (Duongtang != null) {
                        Duongtang.update();
                    }
                    if (satellite != null) {
                        satellite.update();
                    }
                    if (dropItem != null) {
                        dropItem.update();
                    }
                    if (this.LastTimeDanhHieu_ThienTu != 0 && Util.canDoWithTime(this.LastTimeDanhHieu_ThienTu, 6000)) {
                        LastTimeDanhHieu_ThienTu = 0;
                        isUseDanhHieu_ThienTu = false;
                    }
                    if (this.LastTimeDanhHieu_2 != 0 && Util.canDoWithTime(this.LastTimeDanhHieu_2, 6000)) {
                        LastTimeDanhHieu_2 = 0;
                        isUseDanhHieu_2 = false;
                    }
                    if (this.LastTimeDanhHieu_3 != 0 && Util.canDoWithTime(this.LastTimeDanhHieu_3, 6000)) {
                        LastTimeDanhHieu_3 = 0;
                        isUseDanhHieu_3 = false;
                    }
                    if (this.LastTimeDanhHieu_4 != 0 && Util.canDoWithTime(this.LastTimeDanhHieu_4, 6000)) {
                        LastTimeDanhHieu_4 = 0;
                        isUseDanhHieu_4 = false;
                    }
                    if (this.zone != null && this.zone.map.mapId == (21 + this.gender)) {
                        if (this.mabuEgg != null) {
                            this.mabuEgg.sendMabuEgg();
                        }
                        if (this.duahau != null) {
                            this.duahau.sendDuaHau();
                        }
                    }
                    if (this.isPl() && this.achievement != null) {
                        this.achievement.done(ConstAchievement.HOAT_DONG_CHAM_CHI, 1000);
                    }

                    if (this.zone != null && this.effectSkin != null && this.effectSkin.xHPKI > 1 && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId)) {
                        this.effectSkin.xHPKI = 1;
                        this.nPoint.calPoint();
                        Service.gI().point(this);
                    }

                    if (this.zone != null && this.effectSkin != null && this.effectSkin.xDame > 1 && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId)) {
                        this.effectSkin.xDame = 1;
                        this.nPoint.calPoint();
                        Service.gI().point(this);
                    }

                    if (this.isPl() && this.zone != null) {
                        fixBlackBallWar();
                    }

                    if (this.isPl() && this.superRank != null) {
                        if (this.PointRank == 0) {
                            if (Util.isAfterMidnight(this.superRank.lastTimeReward)) {
                                this.superRank.reward();
                                this.PointRank = 1;
                            }
                        }
                    }

                    if (this.isAutoHoiSinh && this.nPoint != null && this.isDie()) {
                        Service.gI().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
                    }

                    if (Util.canDoWithTime(this.lastTimeDame, 5000) && this.dametong != 0) {
                        this.dametong = 0;
                        this.resetdame = true;
                    }
                    if (Util.canDoWithTime(this.lastTimeDameMayDam, 2000) && this.TotalDameMayDam > 0) {
                        long score = (long) Math.max(1, Math.sqrt(this.TotalDameMayDam / 10.0) * 2);
                        Service.getInstance().sendThongBao(this, "Điểm máy đấm của bạn là " + Util.format(score));
                        switch (this.gender) {
                            case 0:
                                this.TopMayDamTraiDat += score;
                                break;
                            case 1:
                                this.TopMayDamNamec += score;
                                break;
                            case 2:
                                this.TopMayDamXayda += score;
                                break;
                            default:
                                break;
                        }
                        this.TotalDameMayDam = 0;
                    }
                    if (this.isPl()) {
                        UpdateOptionItem();

                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);

                        // Cho phép vào map 126 trong 2 khung giờ: 15–16h hoặc 22–23h
                        boolean inTime = (hour >= 15 && hour <= 16) || (hour >= 22 && hour <= 23);

                        if (!inTime) {
                            if (this.zone != null && this.zone.map.mapId == 126) {
                                Service.gI().sendThongBao(this,
                                        "Bạn chỉ có thể vào map Hirudegarn trong khoảng 15h–16h hoặc 22h–23h!");
                                ChangeMapService.gI().changeMapNonSpaceship(
                                        this, 19, 1000 + Util.nextInt(-100, 100), 360
                                );
                            }
                        }
                        if (this.itemTime != null && this.itemTime.isUseHoiSieuCap) {
                            PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, this.nPoint.mpMax);
                        }
//                        if (Util.canDoWithTime(TimeSpawnBot, 30000)) {
//                            NewBot.gI().runBot(2, null, null, 1);
//                            TimeSpawnBot = System.currentTimeMillis();
//                        }
                        if (Util.canDoWithTime(this.lastTimeAttack, 7000)) {
                            this.tlDameAdd = 0;
                        }
                        NguHanhSon.gI().update(this);
                        if (Util.isTrue(20, 100)) {
                            updateBossEvent(this);
                        }
                        TaskService.gI().sendUpdateCountSubTask(this);
                        autoSendBadges();
                        BadgesTaskService.updateDoneTask(this);
                        sendTextTimeDaiLyGift();
                        TranhNgoc.gI().update(this);
                        UpdateOptionClan();
                        if (this.clan != null) {
                            ClanService.gI().checkDoneTaskJoinClan(this.clan);
                            if (System.currentTimeMillis() >= this.clan.LasttimeBuffExp + this.clan.TimeStarBuffExp) {
                                this.clan.CongTiemNangSucManhToanBangHoi = 1; // trở lại bình thường
                            }
                        }
//                        if (Util.canDoWithTime(Manager.LAST_TIME_UPDATE_BXH, 60_000)) {
//                            List<Player> playersCopy = new ArrayList<>(Client.gI().getPlayers()); // tạo bản sao an toàn
//                            for (Player player : playersCopy) {
//                                PlayerDAO.updateBangXepHang(player);
//                            }
//                            Manager.LAST_TIME_UPDATE_BXH = System.currentTimeMillis();
//                        }
                        if (Util.canDoWithTime(LAST_TIME_UPDATE_LOGIN, 60_000)) {
                            PlayerService.updatePlayerLastTimeLoginGame(this);
                            LAST_TIME_UPDATE_LOGIN = System.currentTimeMillis();
                        }
                        if (this.isPl()) {
                            if (!InventoryService.gI().findBinhPhep(this) && this.inventory != null) {
                                for (Item item : this.inventory.itemsBag) {
                                    Item HonMa = InventoryService.gI().findItemBag(this, 1258);
                                    if (item.isNotNullItem() && item.template.id == 1258) {
                                        if (Util.isTrue(20, 100)) {
                                            InventoryService.gI().subQuantityItemsBag(this, HonMa, 1);
                                            InventoryService.gI().sendItemBag(this);
                                            Service.gI().sendThongBao(this, "Hồn ma Lích Tên đã thoát ra ngoài, Hãy dùng Bình Phép để ngăn chặn");
                                        }
                                    }
                                }
                            }
                        }
                        BossNomalService.CheckDonateWater_AutoUpdated(this);
                    }
                    SendPetChiBi();
                    SuperDivineWater.gI().update(this);
                    MajinBuu12H.gI().update(this);
                    QuaKhu.gI().update(this);
                    UpdateChat();
                    UpdatePhoBan();
                    SendMabu14H();
                    MajinBuu12H.gI().update(this);
                    UpdateEffectKillWhis();
                    SendDropTail();
                    if (this.isPl() && this.iDMark != null) {
                        if (!isBoss && this.iDMark != null && this.iDMark.isGotoFuture() && Util.canDoWithTime(this.iDMark.getLastTimeGoToFuture(), 60000)) {
                            ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                            this.iDMark.setGotoFuture(false);
                        }
                        if (this.isPl() && location != null && location.lastTimeplayerMove < System.currentTimeMillis() - 5 * 60 * 60 * 1000) {
                            Client.gI().kickSession(session);
                        }
                        if (this.iDMark.isGoToDuHanhThoiGian() && Util.canDoWithTime(this.iDMark.getLastTimeDuHanhThoiGian(), 8000)) {
                            ChangeMapService.gI().changeMapTuQuaKhuVeHienTai(this, 234, -1, 170);
                            this.iDMark.setGoToDuHanhThoiGian(false);
                        }
                        if (this.iDMark.isGoToTroVeThoiGian() && Util.canDoWithTime(this.iDMark.getLastTimeTroVeThoiGian(), 8000)) {
                            ChangeMapService.gI().changeMapTuQuaKhuVeHienTai(this, 27, -1, 170);
                            this.iDMark.setGoToTroVeThoiGian(false);
                        }
                        if (this.iDMark.isGoToTroVeThoiGian2() && Util.canDoWithTime(this.iDMark.getLastTimeTroVeThoiGian2(), 8000)) {
                            ChangeMapService.gI().changeMapTuQuaKhuVeHienTai(this, 27, -1, 170);
                            this.iDMark.setGoToTroVeThoiGian2(false);
                        }

                        if (!isBoss && this.iDMark.isGotoFuture() && Util.canDoWithTime(this.iDMark.getLastTimeGoToFuture(), 6000)) {
                            ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                            this.iDMark.setGotoFuture(false);
                        }
                        if (!isBoss && this.iDMark.isGoToHome() && Util.canDoWithTime(this.iDMark.getLastTimeGoToHome(), 30000)) {
                            ChangeMapService.gI().changeMapBySpaceShip(this, (this.gender + 21), -1, Util.nextInt(60, 200));
                            this.iDMark.setGoToHome(false);
                        }
                    }

                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                    }
                    if (this.isPl() && this.inventory != null && this.inventory.itemsBag != null && this.inventory.itemsBody.get(7) != null) {
                        Item it = this.inventory.itemsBody.get(7);
                        if (it != null && it.isNotNullItem() && this.PetFollow == null) {
                            DetuService.PetFollow(this, it.template.head, it.template.body, it.template.leg);
                            Service.getInstance().point(this);
                        }
                    } else if (this.isPl() && PetFollow != null && !this.inventory.itemsBody.get(7).isNotNullItem()) {
                        PetFollow.dispose();
                        PetFollow = null;
                    }
                }
            } catch (Exception e) {
                Logger.logException(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }

    private void UpdateOptionItem() {
        if (Util.canDoWithTime(this.ResetDateOut, 1000)) {
            for (Item item : this.inventory.itemsBag) {
                if (item.getOptionParam(260) > 0 || item.getOptionParam(261) > 0) {
                    ItemService.gI().loadItemTimeStatus(this, item);
                }
            }
            for (Item item : this.inventory.itemsBag) {
                if ((item.getOptionParam(261) < 1 || item.getOptionParam(260) < 1) && ItemService.gI().isOutOfDateTime(item)) {
                    InventoryService.gI().removeItem(this, item);
                    Service.gI().sendThongBao(this, item.Name() + " đã hết hạn sử dụng");
                }
            }
            this.ResetDateOut = System.currentTimeMillis();
        }
    }

    private void UpdateOptionClan() {
        int nPlSameClanNearby = 0;
        if(this.zone == null){
            return;
        }
        for (Player plclan : this.zone.getPlayers()) {
            if (!plclan.equals(this)
                    && plclan.clan != null
                    && plclan.clan.equals(this.clan)
                    && Util.getDistance(this, plclan) <= 300) {
                nPlSameClanNearby++;
            }
        }
        if (nPlSameClanNearby >= 2) {
            tlHpClanAdd = this.nPoint.tlHpClan;
            tlDameClanAdd = this.nPoint.tlDameClan;
            tlMpClanAdd = this.nPoint.tlMpClan;
            Service.gI().point(this);
        } else {
            tlHpClanAdd = 0;
            tlDameClanAdd = 0;
            tlMpClanAdd = 0;
            Service.gI().point(this);
        }
    }

    private void updateBossEvent(Player player) {
        if (player == null || player.zone == null || player.isDie()) {
            return;
        }

        List<Player> bosses = player.zone.getBosses();
        if (bosses == null || bosses.isEmpty()) {
            return;
        }

        Boss ongGia = null;

        // Tìm boss Ông Già Noel trong zone
        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.ONG_GIA_NOEL) {
                    ongGia = (Boss) bossPlayer;
                    break;
                }
            }
        }

        // Trường hợp không có Ông già Noel trong map
        if (ongGia == null && player.canReward_TuanLoc && !player.haveReward_TuanLoc) {
            Service.gI().sendThongBao(player, "Hãy mang Tuần lộc trả cho Ông già Noel nào!");
            return;
        }

        // Trường hợp có Ông già Noel
        if (ongGia != null && player.canReward_TuanLoc && !player.haveReward_TuanLoc) {
            if (Util.getDistance(player, ongGia) > 200) {
                Service.gI().sendThongBao(player, "Hãy đến gần Ông già Noel hơn!");
                return;
            }

            if (Util.isTrue(50, 100)) {
                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_ONG_GIA_NOEL, 6126,
                        "Đây là bé Tuần lộc kéo xe của ta\n"
                        + "Cảm ơn con đã tìm nó giùm ta.\n"
                        + "Giao nó cho ta nhé", "Đồng ý", "Từ chối");
            }
        }
    }

    public void autoSendBadges() {
        Iterator<BadgesData> iterator = dataBadges.iterator();
        while (iterator.hasNext()) {
            BadgesData data = iterator.next();
            if (System.currentTimeMillis() >= data.timeofUseBadges) {
                iterator.remove();
            } else if (data.isUse) {
                badges.idBadges = data.idBadGes;
            }
        }

        if (badges.idBadges != -1 && Util.canDoWithTime(badges.lastTimeSendBadges, 10000)) {
            Service.gI().sendBadgesPlayer(this, 5, badges.idBadges);
            badges.lastTimeSendBadges = System.currentTimeMillis();
            this.nPoint.update();
            Service.gI().point(this);
        }
    }

//    private void SendDropTail() {
//        if (gender == 2) {
//            if (cFlag == 8 && khisukien && Util.canDoWithTime(lastTimeDropTail, TIME_TRUNG_THU)) {
//                if (!this.effectSkill.isMonkey) {
//                    EffectSkillService.gI().sendEffectMonkey(this);
//                    EffectSkillService.gI().setIsMonkeyTrungThu(this);
//                    EffectSkillService.gI().sendEffectMonkey(this);
//
//                    Service.gI().sendSpeedPlayer(this, 0);
//                    Service.gI().Send_Caitrang(this);
//                    Service.gI().sendSpeedPlayer(this, -1);
//                    if (this.isPl()) {
//                        PlayerService.gI().sendInfoHpMp(this);
//                    }
//                    Service.gI().point(this);
//                    Service.gI().Send_Info_NV(this);
//                    Service.gI().sendInfoPlayerEatPea(this);
//                }
//                khisukien = false;
//            }
//        }
//    }
 private void SendDropTail() {
    if (gender != 2) {
        return;
    }

    if (this.cFlag != 0) {
        return;
    }

    if (khisukien && Util.canDoWithTime(lastTimeDropTail, TIME_TRUNG_THU)) {
        if (!this.effectSkill.isMonkey) {
            EffectSkillService.gI().sendEffectMonkey(this);
            EffectSkillService.gI().setIsMonkeyTrungThu(this);
            EffectSkillService.gI().sendEffectMonkey(this);

            Service.gI().sendSpeedPlayer(this, 0);
            Service.gI().Send_Caitrang(this);
            Service.gI().sendSpeedPlayer(this, -1);

            if (this.isPl()) {
                PlayerService.gI().sendInfoHpMp(this);
            }

            Service.gI().point(this);
            Service.gI().Send_Info_NV(this);
            Service.gI().sendInfoPlayerEatPea(this);
        }
        khisukien = false;
    }
}

    private void SendMabu14H() {
        if (this.isPl() && this.effectSkill != null && this.effectSkill.isMabuHold) {
            this.nPoint.subHP(this.nPoint.hpMax / 500);
            if (Util.isTrue(1, 10)) {
                Service.gI().chat(this, "Cứu tôi với");
            }
            PlayerService.gI().sendInfoHp(this);
            if (this.precentMabuHold > 15) {
                EffectSkillService.gI().removeMabuHold(this);
            }
            if (this.nPoint.hp <= 0) {
                EffectSkillService.gI().removeMabuHold(this);
                setDie(this);
            }
        }
        if (this.isPhuHoMapMabu && this.zone != null && !MapService.gI().isMapMabu14H(this.zone.map.mapId)) {
            this.isPhuHoMapMabu = false;
            this.nPoint.calPoint();
            Service.gI().point(this);
            Service.gI().Send_Info_NV(this);
            Service.gI().Send_Caitrang(this);
        }
    }

    private void SendPetChiBi() {
        if (this.zone == null) {
            return;
        }
        if (this.isPlandBot() && this.effectSkill.isChibi && (MapService.gI().isMapWar(this.zone.map.mapId) || MapService.gI().isMapBlackBallWar(this.zone.map.mapId) || MapService.gI().isMapOffline(this.zone.map.mapId))) {
            EffectSkillService.gI().removeChibi(this);
        }
        if (this.isPlandBot() && !this.isDie() && this.effectSkill != null && !this.effectSkill.isChibi && Util.canDoWithTime(lastTimeChibi, 1_500_000)) {
            if (this.nPoint.power <= 1_500_000) {
                if ((isPlayer ? Util.isTrue(1, 100) : Util.isTrue(1, 2_000)) && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId) && !MapService.gI().isMapWar(this.zone.map.mapId) && !MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    EffectSkillService.gI().setChibi(this, 600000);
                }
            } else if (this.nPoint.power > 1500000 && this.nPoint.power <= 150000000) {
                if ((isPlayer ? Util.isTrue(1, 140) : Util.isTrue(1, 4_000)) && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId) && !MapService.gI().isMapWar(this.zone.map.mapId) && !MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    EffectSkillService.gI().setChibi(this, 600000);
                }
            } else if (this.nPoint.power > 150000000 && this.nPoint.power <= 10000000000L) {
                if ((isPlayer ? Util.isTrue(1, 160) : Util.isTrue(1, 5_000)) && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId) && !MapService.gI().isMapWar(this.zone.map.mapId) && !MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    EffectSkillService.gI().setChibi(this, 600000);
                }
            } else if (this.nPoint.power > 10000000000L && this.nPoint.power <= 40000000000L) {
                if ((isPlayer ? Util.isTrue(1, 180) : Util.isTrue(1, 8_000)) && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId) && !MapService.gI().isMapWar(this.zone.map.mapId) && !MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    EffectSkillService.gI().setChibi(this, 600000);
                }
            } else if (this.nPoint.power > 40000000000L) {
                if ((isPlayer ? Util.isTrue(1, 200) : Util.isTrue(1, 10_000)) && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId) && !MapService.gI().isMapWar(this.zone.map.mapId) && !MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    EffectSkillService.gI().setChibi(this, 600000);
                }
            }
        }
        if (this.isPlandBot() && !this.isDie() && this.effectSkill != null && this.effectSkill.isChibi && Util.canDoWithTime(lastTimeUpdateChibi, 1000)) {
            if (this.typeChibi == 1) {
                if (this.nPoint.mp < this.nPoint.mpMax) {
                    if (this.nPoint.mpMax - this.nPoint.mp < this.nPoint.mpMax / 10) {
                        this.nPoint.mp = this.nPoint.mpMax;
                    } else {
                        this.nPoint.mp += this.nPoint.mpMax / 10;
                    }
                }
                PlayerService.gI().sendInfoMp(this);
            } else if (this.typeChibi == 3) {
                if (this.nPoint.hp < this.nPoint.hpMax) {
                    if (this.nPoint.hpMax - this.nPoint.hp < this.nPoint.hpMax / 10) {
                        this.nPoint.hp = this.nPoint.hpMax;
                    } else {
                        this.nPoint.hp += this.nPoint.hpMax / 10;
                    }
                }
                PlayerService.gI().sendInfoHp(this);
            }
            lastTimeUpdateChibi = System.currentTimeMillis();
        }
    }

    private void UpdateCaiTrangBienHinh() {
        if (this.isPl() && this.effectSkill.iscumber2 == true && (this.inventory.itemsBody.get(5).template.id != 1479)) {
            EffectSkillService.gI().cumberdown2(this);
        } else if (this.isPl() && this.effectSkill.iscumber == true && (this.inventory.itemsBody.get(5).template.id != 2013)) {
            EffectSkillService.gI().cumberdown(this);
        } else if (this.isPl() && this.effectSkill.isPain == true && (this.inventory.itemsBody.get(5).template.id != 1393)) {
            EffectSkillService.gI().paindown(this);
        } else if (this.isPl() && this.effectSkill.iskefla == true && (this.inventory.itemsBody.get(5).template.id != 1093)) {
            EffectSkillService.gI().kefladown(this);
        }
    }

    private void UpdatePhoBan() {
        if (this.isPl() && this.clan != null && this.clan.ConDuongRanDoc != null && this.joinCDRD && this.clan.ConDuongRanDoc.allMobsDead && this.talkToThanMeo
                && this.zone.map.mapId == 47 && Util.canDoWithTime(timeChangeMap144, 5000)) {
            ChangeMapService.gI().changeMapYardrat(this, this.clan.ConDuongRanDoc.getMapById(144), 300 + Util.nextInt(-100, 100), 312);
            this.timeChangeMap144 = System.currentTimeMillis();
        }
    }

    private void UpdateChat() {
        if (isPl() && Util.canDoWithTime(TimeChatKhukhu, Util.nextInt(15000, 25000))) {
            Service.getInstance().chat(this, "Khụ khụ...");
            TimeChatKhukhu = System.currentTimeMillis();
        }
    }

    private void UpdateEffectKillWhis() {
        List<Player> list = TopKillWhisManager.getInstance().getList();
        if (!list.isEmpty() && list.size() > 2 && this.isPl()) {
            if (list.size() >= 5 && this.isPl()) {
                int playerRank = getPlayerWhis(list, this);
                switch (playerRank) {
                    case 1:
                        Service.getInstance().addEffectChar(this, 58, 1, -1, -1, 1);
                        break;
                    case 2:
                        Service.getInstance().addEffectChar(this, 57, 1, -1, -1, 1);
                        break;
                    case 3:
                        Service.getInstance().addEffectChar(this, 56, 1, -1, -1, 1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void UpdateSkillBienHinhAndAura() {
        //aura trái đất
        if (isPl() && this.gender == 0 && isbienhinh == 1) {
//            RadarService.gI().setIDAuraEff(this, 111);
        } else if (isPl() && this.gender == 0 && isbienhinh == 2) {
//            RadarService.gI().setIDAuraEff(this, 63);
        } else if (isPl() && this.gender == 0 && isbienhinh == 3) {
//            RadarService.gI().setIDAuraEff(this, 71);
        } else if (isPl() && this.gender == 0 && isbienhinh == 4) {
//            RadarService.gI().setIDAuraEff(this, 72);
        } else if (isPl() && this.gender == 0 && isbienhinh == 5) {
//            RadarService.gI().setIDAuraEff(this, 73);    
        }
        //aura namek
        if (isPl() && this.gender == 1 && isbienhinh == 1) {
//            RadarService.gI().setIDAuraEff(this, 112);
        } else if (isPl() && this.gender == 1 && isbienhinh == 2) {
//            RadarService.gI().setIDAuraEff(this, 70);
        } else if (isPl() && this.gender == 1 && isbienhinh == 3) {
//            RadarService.gI().setIDAuraEff(this, 64);
        } else if (isPl() && this.gender == 1 && isbienhinh == 4) {
//            RadarService.gI().setIDAuraEff(this, 66);
        } else if (isPl() && this.gender == 1 && isbienhinh == 5) {
//            RadarService.gI().setIDAuraEff(this, 74);    
        }
        //aura xayda
        if (isPl() && this.gender == 2 && isbienhinh == 1) {
//            RadarService.gI().setIDAuraEff(this, 111);
        } else if (isPl() && this.gender == 2 && isbienhinh == 2) {
//            RadarService.gI().setIDAuraEff(this, 63);
        } else if (isPl() && this.gender == 2 && isbienhinh == 3) {
//            RadarService.gI().setIDAuraEff(this, 71);
        } else if (isPl() && this.gender == 2 && isbienhinh == 4) {
//            RadarService.gI().setIDAuraEff(this, 72);
        } else if (isPl() && this.gender == 2 && isbienhinh == 5) {
//            RadarService.gI().setIDAuraEff(this, 69);   
        }
    }

    public boolean isTargerDe(Player plAtt) {
        return plAtt.isPetFollow || plAtt.isDuongTang || plAtt.isBoss || plAtt.isPl();
    }

    public byte getEffFront() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 20) {
            return -1;
        }
        int levelAo = 0;
        ItemOption optionLevelAo = null;
        int levelQuan = 0;
        ItemOption optionLevelQuan = null;
        int levelGang = 0;
        ItemOption optionLevelGang = null;
        int levelGiay = 0;
        ItemOption optionLevelGiay = null;
        int levelNhan = 0;
        ItemOption optionLevelNhan = null;
        Item itemAo = this.inventory.itemsBody.get(0);
        Item itemQuan = this.inventory.itemsBody.get(1);
        Item itemGang = this.inventory.itemsBody.get(2);
        Item itemGiay = this.inventory.itemsBody.get(3);
        Item itemNhan = this.inventory.itemsBody.get(4);
        for (ItemOption io : itemAo.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelAo = io.param;
                optionLevelAo = io;
                break;
            }
        }
        for (ItemOption io : itemQuan.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelQuan = io.param;
                optionLevelQuan = io;
                break;
            }
        }
        for (ItemOption io : itemGang.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelGang = io.param;
                optionLevelGang = io;
                break;
            }
        }
        for (ItemOption io : itemGiay.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelGiay = io.param;
                optionLevelGiay = io;
                break;
            }
        }
        for (ItemOption io : itemNhan.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelNhan = io.param;
                optionLevelNhan = io;
                break;
            }
        }
        if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 8 && levelQuan >= 8 && levelGang >= 8 && levelGiay >= 8 && levelNhan >= 8) {
            return 8;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 7 && levelQuan >= 7 && levelGang >= 7 && levelGiay >= 7 && levelNhan >= 7) {
            return 7;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 6 && levelQuan >= 6 && levelGang >= 6 && levelGiay >= 6 && levelNhan >= 6) {
            return 6;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 5 && levelQuan >= 5 && levelGang >= 5 && levelGiay >= 5 && levelNhan >= 5) {
            return 5;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 4 && levelQuan >= 4 && levelGang >= 4 && levelGiay >= 4 && levelNhan >= 4) {
            return 4;
        } else {
            return -1;
        }
    }

    //--------------------------------------------------------------------------
    /*
     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
     * {383, 384, 385}: ht porata xayda trái đất
     * {391, 392, 393}: ht namếc
     * {870, 871, 872}: ht c2 trái đất
     * {873, 874, 875}: ht c2 namếc
     * {867, 878, 869}: ht c2 xayda
     * {2033,2034,2035}: ht c3 td
     * {2030,2031,2032}: ht c3 nm   
     * {2027,2028,2029}: ht c3 xd*/
    private static final short[][] idOutfitFusion = {
        {380, 381, 382}, {383, 384, 385}, {391, 392, 393},
        {870, 871, 872}, {873, 874, 875}, {867, 868, 869},
//        {1871, 1874, 1875}, {1831, 1834, 1835}, {1826, 1829, 1830},
        {1939, 1942, 1943}, {1831, 1834, 1835}, {1826, 1829, 1830},
        {1765, 1766, 1767}, {1771, 1772, 1773}, {1768, 1769, 1770},
        {1774, 1775, 1776}, {1780, 1781, 1782}, {1777, 1778, 1779},};

    private static final short[][] idOutfitCaiTrangFusion = {
        {566, 567, 568}, {570, 571, 572}, {563, 564, 565},
        {630, 631, 632}, {633, 634, 635}, {627, 628, 629},};

    public static final short[][][] idOutfitHalloween = {
        {{654, 655, 656}, {654, 655, 656}, {654, 655, 656}},
        {{651, 652, 653}, {651, 652, 653}, {651, 652, 653}},
        {{545, 548, 549}, {547, 548, 549}, {546, 548, 549}},
        {{760, 761, 762}, {760, 761, 762}, {760, 761, 762}},};

    public static final short[][] idOutfitMafuba = {
        {1686, 1687, 1688}, {-1, -1, -1}, {1218, 1219, 1220}};

    public static final short[][] idOutfitGod = {
        {-1, 472, 473}, {-1, 476, 477}, {-1, 474, 475}};

    private static final short[][] idOutFitSuperEarth = {
        {1436, 1437, 1438}, // level 1
        {1436, 1437, 1438}, // level 2
        {1442, 1437, 1438}, // level 3
        {1440, 1437, 1438}, // level 4
        {1439, 1437, 1438}, // level 5
        {1441, 1437, 1438}, // level 6 
    };

    private static final short[][] idOutFitSuperNamec = {
        {1430, 1431, 1432}, // level 1
        {1443, 1431, 1432}, // level 2
        {1444, 1431, 1432}, // level 3
        {1445, 1431, 1432}, // level 4
        {1446, 1431, 1432}, // level 5
        {1447, 1431, 1432}, // level 6 
    };

    private static final short[][] idOutFitSuperSaiyan = {
        {1433, 1434, 1435}, // level 1
        {1433, 1434, 1435}, // level 2
        {1448, 1434, 1435}, // level 3
        {1449, 1434, 1435}, // level 4
        {1450, 1434, 1435}, // level 5
        {1451, 1434, 1435}, // level 6 
    };

    private static final byte[][] idAuraSuper = {
        {20, 21, 22, 23, 24, 25},// Trái đất
        {26, 27, 28, 29, 30, 31},// namec
        {32, 33, 34, 35, 36, 37},// xayda
    };

    public short getHeadThuCung() {
        if (this.isPl() && this.inventory != null && this.inventory.itemsBody.size() > 7 && this.inventory.itemsBody.get(7).isNotNullItem()) {
            return (short) (this.inventory.itemsBody.get(7).template.head);
        }
        return -1;
    }

    public short getBodyThuCung() {
        if (this.isPl() && this.inventory != null && this.inventory.itemsBody.size() > 7 && this.inventory.itemsBody.get(7).isNotNullItem()) {
            return (short) (this.inventory.itemsBody.get(7).template.body);
        }
        return -1;
    }

    public short getLegThuCung() {
        if (this.isPl() && this.inventory != null && this.inventory.itemsBody.size() > 7 && this.inventory.itemsBody.get(7).isNotNullItem()) {
            return (short) (this.inventory.itemsBody.get(7).template.leg);
        }
        return -1;
    }

                    public short getHead() {
                        if (effectSkill != null && effectSkill.isBinh) {
                            return idOutfitMafuba[effectSkill.typeBinh][0];
                        }
                        if (effectSkill != null && effectSkill.isStone) {
                            return 454;
                        }
                        if (effectSkill != null && effectSkill.isHalloween) {
                            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][0];
                        }
                        if (effectSkill != null && effectSkill.isMonkey) {
                            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
                        } else if (itemTime != null && itemTime.IsDuoiKhi) {
                            return (short) ConstPlayer.DUOIHEADMONKEY[Util.nextInt(0, 6)];
                        }
                        if (effectSkill != null && effectSkill.iscumber) {
                            return 1903;
                        } else if (effectSkill != null && effectSkill.iscumber2) {
                            return 1288;
                        } else if (effectSkill != null && effectSkill.isPain) {
                            return 1450;
                        } else if (effectSkill != null && effectSkill.iskefla) {
                            return 1554;
                        } else if (effectSkill != null && effectSkill.isSocola) {
                            return 412;
                        } else if (effectSkin != null && effectSkin.isSocola) {
                            return 412;
                        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
                            return 406;
                        } else if (effectSkill != null && effectSkill.isBiNgo) {
                            return 760;
                        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
                            return 1210;
                        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION && this.hienThiHopThe == 1) {
                            if ((inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 2014)
                                    && this.Detu != null && this.Detu.inventory.itemsBody.get(5).isNotNullItem() && this.Detu.inventory.itemsBody.get(5).template.id == 2015) {
                                return 1912;
                            } else if ((inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 2015)
                                    && this.Detu != null && this.Detu.inventory.itemsBody.get(5).isNotNullItem() && this.Detu.inventory.itemsBody.get(5).template.id == 2014) {
                                return 1912;
                            }
                            if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 601) {
                                return idOutfitCaiTrangFusion[0][0];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 602) {
                                return idOutfitCaiTrangFusion[1][0];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 603) {
                                return idOutfitCaiTrangFusion[2][0];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 640) {
                                return idOutfitCaiTrangFusion[3][0];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 641) {
                                return idOutfitCaiTrangFusion[4][0];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 639) {
                                return idOutfitCaiTrangFusion[5][0];
                            }
                            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                                return idOutfitFusion[3 + this.gender][0];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                                return idOutfitFusion[6 + this.gender][0];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                                return idOutfitFusion[9 + this.gender][0];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
                                return idOutfitFusion[12 + this.gender][0];
                            }
                        } else if (inventory != null && this.inventory.itemsBody != null && this.inventory.itemsBody.size() > 1 && inventory.itemsBody.get(5).isNotNullItem()) {
                            int headId = inventory.itemsBody.get(5).template.head;
                            if (headId != -1) {
                                return (short) headId;
                            }
                        }
                        return this.head;
                    }

                    public short getBody() {
                        if (effectSkill != null && effectSkill.isBinh) {
                            return idOutfitMafuba[effectSkill.typeBinh][1];
                        }
                        if (effectSkill != null && effectSkill.isStone) {
                            return 455;
                        }
                        if (effectSkill != null && effectSkill.isHalloween) {
                            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][1];
                        }
                        if (effectSkill != null && effectSkill.isMonkey) {
                            return 193;
                        } else if (itemTime != null && itemTime.IsDuoiKhi) {
                            return 193;
                        }

                        if (effectSkill != null && effectSkill.iscumber) {
                            return 1904;
                        } else if (effectSkill != null && effectSkill.iscumber2) {
                            return 1289;
                        } else if (effectSkill != null && effectSkill.isPain) {
                            return 1451;
                        } else if (effectSkill != null && effectSkill.iskefla) {
                            return 1555;
                        } else if (effectSkill != null && effectSkill.isSocola) {
                            return 413;
                        } else if (effectSkin != null && effectSkin.isSocola) {
                            return 413;
                        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
                            return 407;
                        } else if (effectSkill != null && effectSkill.isBiNgo) {
                            return 761;
                        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
                            return 1211;
                        }

                        // GIỮ NGUYÊN logic map Mabu như code gốc của bạn
                        if (isPhuHoMapMabu) {
                            return idOutfitGod[this.gender][1];
                        }

                        // CHỈ hiển thị đồ hợp thể khi bật hienThiHopThe
                        if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION && this.hienThiHopThe == 1) {

                            // Special case 2014 + 2015
                            if ((inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 2014)
                                    && this.Detu != null && this.Detu.inventory.itemsBody.get(5).isNotNullItem()
                                    && this.Detu.inventory.itemsBody.get(5).template.id == 2015) {
                                return 1913;
                            } else if ((inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 2015)
                                    && this.Detu != null && this.Detu.inventory.itemsBody.get(5).isNotNullItem()
                                    && this.Detu.inventory.itemsBody.get(5).template.id == 2014) {
                                return 1913;
                            }

                            // Cải trang fusion
                            if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 601) {
                                return idOutfitCaiTrangFusion[0][1];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 602) {
                                return idOutfitCaiTrangFusion[1][1];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 603) {
                                return idOutfitCaiTrangFusion[2][1];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 640) {
                                return idOutfitCaiTrangFusion[3][1];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 641) {
                                return idOutfitCaiTrangFusion[4][1];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()
                                    && inventory.itemsBody.get(5).template.id == 639) {
                                return idOutfitCaiTrangFusion[5][1];
                            }

                            // Outfit fusion theo typeFusion
                            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                                return idOutfitFusion[3 + this.gender][1];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                                return idOutfitFusion[6 + this.gender][1];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                                return idOutfitFusion[9 + this.gender][1];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
                                return idOutfitFusion[12 + this.gender][1];
                            }
                        }

                        // KHÔNG hợp thể (hoặc tắt hiển thị hợp thể) -> lấy body từ cải trang slot 5
                        if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
                            int body = inventory.itemsBody.get(5).template.body;
                            if (body != -1) {
                                return (short) body;
                            }
                        }

                        // fallback body theo áo slot 0
                        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
                            return inventory.itemsBody.get(0).template.part;
                        }

                        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
                    }

                    public short getLeg() {
                        if (effectSkill != null && effectSkill.isBinh) {
                            return idOutfitMafuba[effectSkill.typeBinh][2];
                        }
                        if (effectSkill != null && effectSkill.isStone) {
                            return 456;
                        }
                        if (effectSkill != null && effectSkill.isHalloween) {
                            return idOutfitHalloween[effectSkill.idOutfitHalloween][this.gender][2];
                        }
                        if (effectSkill != null && effectSkill.isMonkey) {
                            return 194;
                        } else if (itemTime != null && itemTime.IsDuoiKhi) {
                            return 194;
                        }
                        if (effectSkill != null && effectSkill.iscumber) {
                            return 1902;
                        } else if (effectSkill != null && effectSkill.iscumber2) {
                            return 1290;
                        } else if (effectSkill != null && effectSkill.isPain) {
                            return 1452;
                        } else if (effectSkill != null && effectSkill.iskefla) {
                            return 1556;
                        } else if (effectSkill != null && effectSkill.isSocola) {
                            return 414;
                        } else if (effectSkin != null && effectSkin.isSocola) {
                            return 414;
                        } else if (effectSkin != null && effectSkin.isThoDaiKa) {
                            return 408;
                        } else if (effectSkill != null && effectSkill.isBiNgo) {
                            return 762;
                        } else if (effectSkin != null && effectSkin.isDraburaFrost) {
                            return 1212;
                        } else if (isPhuHoMapMabu && fusion != null && fusion.typeFusion == ConstPlayer.NON_FUSION) {
                            return idOutfitGod[this.gender][2];
                        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION && this.hienThiHopThe == 1) {
                            if ((inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 2014)
                                    && this.Detu != null && this.Detu.inventory.itemsBody.get(5).isNotNullItem() && this.Detu.inventory.itemsBody.get(5).template.id == 2015) {
                                return 1914;
                            } else if ((inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 2015)
                                    && this.Detu != null && this.Detu.inventory.itemsBody.get(5).isNotNullItem() && this.Detu.inventory.itemsBody.get(5).template.id == 2014) {
                                return 1914;
                            }
                            if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 601) {
                                return idOutfitCaiTrangFusion[0][2];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 602) {
                                return idOutfitCaiTrangFusion[1][2];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 603) {
                                return idOutfitCaiTrangFusion[2][2];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 640) {
                                return idOutfitCaiTrangFusion[3][2];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 641) {
                                return idOutfitCaiTrangFusion[4][2];
                            } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && inventory.itemsBody.get(5).template.id == 639) {
                                return idOutfitCaiTrangFusion[5][2];
                            }
                            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                                return idOutfitFusion[3 + this.gender][2];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                                return idOutfitFusion[6 + this.gender][2];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                                return idOutfitFusion[9 + this.gender][2];
                            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
                                return idOutfitFusion[12 + this.gender][2];
                            }
                        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
                            int leg = inventory.itemsBody.get(5).template.leg;
                            if (leg != -1) {
                                return (short) leg;
                            }
                        }
                        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
                            return inventory.itemsBody.get(1).template.part;
                        }
                        return (short) (gender == 1 ? 60 : 58);
                    }

//    public byte getAura() {
//        byte aura = -1;
//
//        if (isPl() && this.inventory != null && this.inventory.itemsBody.size() > 5) {
//            Item item = this.inventory.itemsBody.get(5);
//            if (item != null && item.isNotNullItem()) {
//                switch (item.template.id) {
//                    case 2013:
//                        aura = 10;
//                        break;
//                    case 2014:
//                        aura = 14;
//                        break;
//                    case 2015:
//                        aura = 15;
//                        break;
//                    case 2059:
//                        aura = 11;
//                        break;
//                   
//                }
//            }
//        }
//
//        if (aura == -1 && this.Cards != null) {
//            for (Card card : this.Cards) {
//                if (card != null) {
//                    RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
//                            .filter(r -> r.Id == card.Id)
//                            .findFirst()
//                            .orElse(null);
//                    if (radarTemplate != null && radarTemplate.AuraId > 0) {
//                        aura = (byte) radarTemplate.AuraId;
//
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (aura == -1) {
//            aura = auraPower();
//        }
//
//        return aura;
//    }
public byte getAura() {
    byte aura = -1;

    if (isPl() && this.inventory != null && this.inventory.itemsBody.size() > 5) {
        Item item = this.inventory.itemsBody.get(5);
        if (item != null && item.isNotNullItem()) {
            switch (item.template.id) {
                case 2013:
                    aura = 10;
                    break;
                case 2014:
                    aura = 14;
                    break;
                case 2015:
                    aura = 15;
                    break;
                case 2059:
                    aura = 11;
                    break;
            }
        }
    }

    if (aura == -1 && this.Cards != null) {
        for (Card card : this.Cards) {
            if (card != null && card.Used == 1) {
                RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                        .filter(r -> r.Id == card.Id)
                        .findFirst()
                        .orElse(null);
                if (radarTemplate != null && radarTemplate.AuraId > 0) {
                    aura = (byte) radarTemplate.AuraId;
                    break;
                }
            }
        }
    }

    if (aura == -1) {
        aura = auraPower();
    }

    return aura;
}
    public byte auraPower() {
        if (this.nPoint == null) {
            return 0;
        }
        if (this.nPoint.power >= 180_000_000_000L) {
            return 84;
        } else if (this.nPoint.power >= 120_000_000_000L) {
            return 83;
        } else if (this.nPoint.power >= 110_000_000_000L) {
            return 82;
        } else if (this.nPoint.power >= 100_000_000_000L) {
            return 6;
        } else if (this.nPoint.power >= 80_000_000_000L) {
            return 6;
        }
        return -1; // không có aura
    }

    public short getFlagBag() {
        if (this.iDMark != null && this.iDMark.isHoldBlackBall()) {
            return 31;
        } else if (this.idNRNM >= 353 && this.idNRNM <= 359) {
            return 30;
        } else if (this.isHoldNamecBallTranhDoat) {
            return 30;
        }
        if (this.inventory != null && this.inventory.itemsBody.size() >= 10) {
            if (this.inventory.itemsBody.get(8).isNotNullItem()) {
                return this.inventory.itemsBody.get(8).template.part;
            }
        }
        if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        }
        if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(9);
        if (!item.isNotNullItem()) {
            return -1;
        }
        if (item.template.type == 24) {
            if (item.template.gender == 3 || item.template.gender == this.gender) {
                return item.template.id;
            } else {
                return -1;
            }
        } else {
            if (item.template.id < 500) {
                return item.template.id;
            } else {
                return (short) DataGame.MAP_MOUNT_NUM.get(item.template.id);
            }
        }
    }

    public int getHat() {
        return -1;
    }

    public short getHeadSuper() {
        switch (gender) {
            case 0:
                return idOutFitSuperEarth[(playerSkill.getSkillbyId(27).point - 1) - numUseSkill][0];
            case 1:
                return idOutFitSuperNamec[(playerSkill.getSkillbyId(28).point - 1) - numUseSkill][0];
            case 2:
                return idOutFitSuperSaiyan[(playerSkill.getSkillbyId(29).point - 1) - numUseSkill][0];
        }
        return -1;
    }

    public short getBodySuper() {
        switch (gender) {
            case 0:
                return idOutFitSuperEarth[(playerSkill.getSkillbyId(27).point - 1) - numUseSkill][1];
            case 1:
                return idOutFitSuperNamec[(playerSkill.getSkillbyId(28).point - 1) - numUseSkill][1];
            case 2:
                return idOutFitSuperSaiyan[(playerSkill.getSkillbyId(29).point - 1) - numUseSkill][1];
        }
        return -1;
    }

    public short getLegSuper() {
        switch (gender) {
            case 0:
                return idOutFitSuperEarth[(playerSkill.getSkillbyId(27).point - 1) - numUseSkill][2];
            case 1:
                return idOutFitSuperNamec[(playerSkill.getSkillbyId(28).point - 1) - numUseSkill][2];
            case 2:
                return idOutFitSuperSaiyan[(playerSkill.getSkillbyId(29).point - 1) - numUseSkill][2];
        }
        return -1;
    }

    public Timer timer;
    public TimerTask task;

    public long lastTimeSendTextTime;
    public long lastTimeSendTextTime_2;

    public void sendTextTimeDaiLyGift() {
        if (Util.canDoWithTime(lastTimeSendTextTime, 300000)) {
            if (DailyGiftService.checkDailyGift(this, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                ItemTimeService.gI().sendTextTime(this, ItemTime.TEXT_NHAN_BUA_MIEN_PHI, "Nhận ngẫu nhiên bùa 1h mỗi ngày tại Bà Hạt Mít ở vách núi", 30);
            }
            lastTimeSendTextTime = System.currentTimeMillis();
        }
        if (Util.canDoWithTime(lastTimeSendTextTime_2, 60000)) {
            if (DailyGiftService.checkDailyGift(this, ConstDailyGift.DIEM_DANH_HANG_NGAY)) {
                switch (this.gender) {
                    case 0:
                        ItemTimeService.gI().sendTextTime(this, ItemTime.TEXT_DIEM_DANH_HANG_NGAY, "Điểm danh mỗi ngày tại Ông Gôhan ở Nhà Gôhan", 30);
                        break;
                    case 1:
                        ItemTimeService.gI().sendTextTime(this, ItemTime.TEXT_DIEM_DANH_HANG_NGAY, "Điểm danh mỗi ngày tại Ông Moori ở Nhà Moori", 30);
                        break;
                    case 2:
                        ItemTimeService.gI().sendTextTime(this, ItemTime.TEXT_DIEM_DANH_HANG_NGAY, "Điểm danh mỗi ngày tại Ông Paragus ở Nhà Broly", 30);
                        break;
                    default:
                        break;
                }
            }
            lastTimeSendTextTime_2 = System.currentTimeMillis();
        }
    }

    public void sendInfoHPMP() {
        if (!isPl()) {
            return;
        }
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte) 5);
            msg.writer().writeLong(this.nPoint.hp);
            this.sendMessage(msg);
            msg.cleanup();
            msg = Service.gI().messageSubCommand((byte) 6);
            msg.writer().writeLong(this.nPoint.mp);
            this.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(Player.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    //--------------------------------------------------------------------------
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (plAtt != null && !plAtt.equals(this)) {
                setTemporaryEnemies(plAtt);
            }
            if (EventManager.HALLOWEEN) {
                if (plAtt != null && this.isDeTu && ((Detu) this).master.id == plAtt.id) {
                    if (this.effectSkill != null && !this.effectSkill.isHalloween && !this.nPoint.KhangHoaXuong) {
                        EffectSkillService.gI().setIsHalloween(this, Util.nextInt(0, 2), 1800000);
                    }
                }
            }
            if (plAtt != null && plAtt.playerSkill.skillSelect != null && plAtt.playerSkill.skillSelect.template != null && !plAtt.isBoss && MapService.gI().isMapMaBu12H(this.zone.map.mapId)) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                        damage = Util.CrisGH(damage > this.nPoint.hpMax / 20 ? this.nPoint.hpMax / 20 : damage);
                }
            }
            if (this.setClothes.broly == 5) {
                damage -= Util.CrisGH((damage / 100) * 80);
            }
            if (plAtt != null && plAtt.effectSkill != null && plAtt.effectSkill.isBinh
                    && !Util.canDoWithTime(plAtt.effectSkill.lastTimeUpBinh, 3000)) {
                return 0;
            }
            if (plAtt != null && plAtt.isBoss) {
                this.effectSkin.isVoHinh = false;
                this.effectSkin.lastTimeVoHinh = System.currentTimeMillis();
            }

            if (plAtt != null) {
                int tlDameEath = plAtt.nPoint.tlTanCongTocTraiDat;
                int tlDameNamek = plAtt.nPoint.tlTanCongTocNamec;
                int tlDameSaiyan = plAtt.nPoint.tlTanCongTocXayda;

                if (tlDameEath > 0 && this.gender == ConstPlayer.TRAI_DAT) {
                    damage += Util.CrisGH((damage / 100) * tlDameEath);
                }
                if (tlDameNamek > 0 && this.gender == ConstPlayer.NAMEC) {
                    damage += Util.CrisGH((damage / 100) * tlDameNamek);
                }
                if (tlDameSaiyan > 0 && this.gender == ConstPlayer.XAYDA) {
                    damage += Util.CrisGH((damage / 100) * tlDameSaiyan);
                }
            }
            if (plAtt != null && plAtt.playerSkill.skillSelect != null && plAtt.playerSkill.skillSelect.template != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (this.nPoint.voHieuChuong > 0) {
                            nro.services.PlayerService.gI().hoiPhuc(this, 0, Util.CrisGH(damage * this.nPoint.voHieuChuong / 100));
                            return 0;
                        }
                }
            }
            int tlGiap = this.nPoint.tlGiap;
            int tlNeDon = this.nPoint.tlNeDon;
            long now = System.currentTimeMillis();
            if (plAtt != null && plAtt.playerSkill.skillSelect != null && plAtt.playerSkill.skillSelect.template != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                        if (plAtt.tlDameAdd < plAtt.nPoint.tlCongDonSD) {
                            plAtt.tlDameAdd++;
                        }
                        if (plAtt.nPoint.tlCongDonSD > 0) {
                            plAtt.lastTimeAttack = now;
                        }
                        damage += Util.CrisGH((damage / 100) * plAtt.tlDameAdd);
                        break;
                }
            }
            if (plAtt != null && !isMobAttack && plAtt.playerSkill.skillSelect != null && plAtt.playerSkill.skillSelect.template != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                    case Skill.DICH_CHUYEN_TUC_THOI:
                        tlNeDon -= plAtt.nPoint.tlchinhxac;
                        break;
                    default:
                        tlNeDon = 0;
                        break;
                }
            }

            if (piercing) {
                tlGiap = 0;
            }
            if (tlNeDon > 90) {
                tlNeDon = 90;
            }
            if (tlGiap > 86) {
                tlGiap = 86;
            }
            if (Util.isTrue(tlNeDon, 100)) {
                return 0;
            }
            int tileXuyenGiap = 0;
            int tileSatthuongChuan = 0;
            if (plAtt != null) {
                tileSatthuongChuan = plAtt.nPoint.tlstc;
                if (plAtt.playerSkill.skillSelect != null && plAtt.playerSkill.skillSelect.template != null && SkillUtil.isUseSkillChuong(plAtt)) {
                    tileXuyenGiap = plAtt.nPoint.tlxgc;
                } else {
                    tileXuyenGiap = plAtt.nPoint.tlxgcc;
                }
            }
            damage = calculateFinalDamage(Util.CrisGH(damage), tlGiap, tileXuyenGiap, tileSatthuongChuan);
            //
            int tlSubDame20Mp = this.nPoint.tlSubDamePercenMp20;

            if (tlSubDame20Mp > 0 && this.nPoint.mp < this.nPoint.mpMax * 0.2) {
                damage -= Util.CrisGH((damage / 100) * tlSubDame20Mp);
            }

            int tlSubEath = this.nPoint.tlGiamSatThuongTraiDat;
            int tlSubNamek = this.nPoint.tlGiamSatThuongNamec;
            int tlSubSaiyan = this.nPoint.tlGiamSatThuongXayda;

            if (tlSubEath > 99) {
                tlSubEath = 99;
            }
            if (tlSubNamek > 99) {
                tlSubNamek = 99;
            }
            if (tlSubSaiyan > 99) {
                tlSubSaiyan = 99;
            }
            if (tlSubEath > 0 && plAtt != null && plAtt.gender == ConstPlayer.TRAI_DAT) {
                damage -= Util.CrisGH((damage / 100) * tlSubEath);
            }
            if (tlSubNamek > 0 && plAtt != null && plAtt.gender == ConstPlayer.NAMEC) {
                damage -= Util.CrisGH((damage / 100) * tlSubNamek);
            }
            if (tlSubSaiyan > 0 && plAtt != null && plAtt.gender == ConstPlayer.XAYDA) {
                damage -= Util.CrisGH((damage / 100) * tlSubSaiyan);
            }

            if (plAtt != null && plAtt.isPl() && this.maBuHold != null && this.zone != null && this.zone.map.mapId == 128) {
                this.precentMabuHold++;
                damage = 1;
            }
            if (plAtt != null && plAtt.idNRNM != -1 && (this.isBoss || this.isPetFollow || this.isDuongTang)) {
                return 1;
            }
            if (plAtt != null && (plAtt.idNRNM != -1 || this.idNRNM != -1) && plAtt.clan != null && this.clan != null && plAtt.clan == this.clan) {
                Service.gI().chatJustForMe(plAtt, this, "Ê cùng bang mà");
                return 0;
            }
            if (plAtt != null && plAtt.effectSkill != null && plAtt.effectSkill.isXinbato && Util.isTrue(20, 100)) {
                return 0;
            }

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            long[] reviveTimes = {
                this.lastTimeRevived,
                this.lastTimeRevivedBo,
                this.lastTimeRevivedMe,
                this.lastTimeRevivedNguoiYeu,
                this.lastTimeRevivedConone,
                this.lastTimeRevivedContwo,
                this.lastTimeRevivedConthree
            };

            for (long time : reviveTimes) {
                if (!Util.canDoWithTime(time, 1500)) {
                    return 0;
                }
            }

            if (!piercing && plAtt == null && isMobAttack && (this.charms.tdBatTu > System.currentTimeMillis() || this.effectSkill != null && this.effectSkill.isHalloween)
                    && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            boolean isUseGX = false;
            if (!piercing && plAtt != null && plAtt.playerSkill.skillSelect != null && plAtt.playerSkill.skillSelect.template != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                    case Skill.DICH_CHUYEN_TUC_THOI:
                        isUseGX = true;
                        break;
                }
            }
            if ((isUseGX || isMobAttack) && this.itemTime != null) {
                if (this.itemTime.isUseGiapXen && !this.itemTime.isUseGiapXen2) {
                    damage /= 2;
                }
                if (this.itemTime.isUseGiapXen2) {
                    damage = damage / 100 * 40;
                }
            }

            if (!piercing && effectSkill.isShielding && !isMobAttack) {
                if (this.iDMark != null) {
                    this.iDMark.setDamePST(Util.CrisGH(damage));
                }
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
                if (MapService.gI().isMapPhoBan(this.zone.map.mapId)) {
                    damage = 10;
                }
            }
            if (plAtt != null && this.nPoint.isFounder) {
                Service.gI().sendThongBao(plAtt, "Không thể tấn công! Vì người này là nhà sáng lập game!");
                return 0;
            }
            if (this.zone.map.mapId == 51) {
                this.totalDamageTaken += damage;
            }
            if (this.zone.map.mapId == 129) {
                if (damage >= this.nPoint.hp) {
                    this.lostByDeath = true;
                    The23rdMartialArtCongress mc = The23rdMartialArtCongressManager.gI().getMC(zone);
                    if (mc != null) {
                        mc.die();
                    }
                    return 0;
                }
            }

            if (this.setClothes != null && this.setClothes.ctFideDaiCa != -1 && this.zone != null) {
                if (plAtt != null && plAtt.setClothes != null && plAtt.setClothes.ctDanEmFide != -1 && plAtt.zone != null) {
                    if (plAtt.effectSkill != null && plAtt.effectSkill.isFideDaiCa) {
                        damage = 1;
                    }
                }
            }

            this.nPoint.subHP(Util.CrisGH(damage));
            if ((plAtt != null || isMobAttack) && isDie() && !isBoss && !isPetFollow && !isDuongTang) {
                if (this.isPl() && this != null && plAtt != null && plAtt.gender == 0 && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null && (plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI) && this.playerTask != null && this.playerTask.taskMain != null && this.playerTask.taskMain.id >= 19 && effectSkill.isMonkey && khisukien == false && Util.canDoWithTime(lastTimeDropTail, TIME_TRUNG_THU)) {
                    try {
                        ItemMap duoikhi = new ItemMap(zone, 579, 1, location.x, location.y, plAtt.id);
                        Service.gI().dropItemMap(zone, duoikhi);
                        lastTimeDropTail = System.currentTimeMillis();
                        ConnectDB.executeUpdate("update player set lastTimeDropTail = ? where name = ?", System.currentTimeMillis(), name);
                        Service.gI().sendThongBao(plAtt, "Rớt đuôi khỉ kìa");
                        Service.gI().sendThongBao(this, "Bạn đã bị hạ và rớt đuôi khỉ, trong vòng 1 giờ sẽ không rớt đuôi khỉ nữa");
                    } catch (Exception e) {
                        Logger.logException(Player.class, e);
                    }
                }
                if (!MapService.gI().isHome(this.zone.map.mapId) && !MapService.gI().isMapLuyenTap(this.zone.map.mapId) && !MapService.gI().isMapWar(this.zone.map.mapId)) {
                    if (plAtt != null) {
                        if (this.effectSkill.isSocola) {
                            ItemMap socola = new ItemMap(zone, 516, 1, location.x, location.y, plAtt.id);
                            Service.gI().dropItemMap(zone, socola);
                        }
                        if (this.effectSkin.isThoDaiKa) {
                            ItemMap socola = new ItemMap(zone, 670, 1, location.x, location.y, plAtt.id);
                            Service.gI().dropItemMap(zone, socola);
                        }
                    }
                }
                if (Util.isTrue(this.nPoint.tlBom, 100)) {
                    setBom(plAtt);
                } else {
                    setDie(plAtt);
                }
            }
            return damage;
        } else {
            return 0;
        }
    }

    private double calculateFinalDamage(double baseDamage, int tlGiap, int tileXuyenGiap, int tileSatThuongChuan) {
        int effectiveArmor = tlGiap - tileXuyenGiap;
        if (effectiveArmor < 0) {
            int bonus = Math.min((-effectiveArmor) / 2, 20);
            effectiveArmor = 0;
            baseDamage += Util.CrisGH(baseDamage) * bonus / 100;
        }
        double finalDamage = Util.CrisGH(baseDamage) * (100 - effectiveArmor) / 100;
        if (tileSatThuongChuan > 0) {
            finalDamage += Util.CrisGH(baseDamage) * tileSatThuongChuan / 100;
        }
        return Math.max(1, finalDamage);
    }

    public void setTemporaryEnemies(Player pl) {
        List<List<Player>> lists = Arrays.asList(
                temporaryEnemies,
                temporaryEnemiesBo,
                temporaryEnemiesMe,
                temporaryEnemiesNguoiYeu,
                temporaryEnemiesConone,
                temporaryEnemiesContwo,
                temporaryEnemiesConthree
        );

        for (List<Player> list : lists) {
            if (!list.contains(pl)) {
                list.add(pl);
            }
        }
    }

    private void fixBlackBallWar() {
        int x = this.location.x;
        int y = this.location.y;
        switch (this.zone.map.mapId) {
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91: {
                if (this.isPl()) {
                    if (x < 24 || x > this.zone.map.mapWidth - 24 || y < 0 || y > this.zone.map.mapHeight - 24) {
                        if (MapService.gI().getWaypointPlayerIn(this) == null) {
                            Service.gI().resetPoint(this, x, this.zone.map.yPhysicInTop(this.location.x, 100));
                            this.nPoint.hp -= this.nPoint.hpMax / 10;
                            PlayerService.gI().sendInfoHp(this);
                            return;
                        }
                    }
                    int yTop = this.zone.map.yPhysicInTop(this.location.x, this.location.y);
                    if (yTop >= this.zone.map.mapHeight - 24) {
                        Service.gI().resetPoint(this, x, this.zone.map.yPhysicInTop(this.location.x, 100));
                        this.nPoint.hp -= this.nPoint.hpMax / 10;
                        PlayerService.gI().sendInfoHp(this);
                    }
                }
                break;
            }
        }
    }

    public void move(Player player, int _toX, int _toY) {
        if (_toX != player.location.x) {
            player.location.x = _toX;
        }
        if (_toY != player.location.y) {
            player.location.y = _toY;
        }
        MapService.gI().sendPlayerMove(player);
    }

    public void move(int _toX, int _toY) {
        if (_toX != this.location.x) {
            this.location.x = _toX;
        }
        if (_toY != this.location.y) {
            this.location.y = _toY;
        }
        MapService.gI().sendPlayerMove(this);
    }

    //----------------CHECK ERROR IN BAND ACCOUNT-------------------------------
    public void Check_Error_Pay(Player player) {
        ChangeMapService.gI().changeMap(player, player.gender + 21, -1, Util.nextInt(200, 600), 300);
        Service.getInstance().sendBigMessage(player, 1139, "|1|Hệ Thống Phát Hiện\n"
                + "|0|Tài Khoản Của Bạn Có Những Hành Vi Bất Thường Ảnh Hưởng Đến Lợi Ích Của Game\n"
                + "|0|Chúng Tôi Tiến Hành Phạt Và Cảnh Cáo Bạn!\n"
                + "|7|Nếu Vi Phạm Quá 3 Lần Bạn Sẽ Bị Khoá Tài Khoản Vĩnh Viễn!\n"
                + "|7|Số Lần Vi Phạm " + this.ErrorPay + " / 3\n\n"
                + "|2|Bị Phạt 2 Tỷ Vàng + 500 Hồng Ngọc!");
        if (player.inventory.gold < 2_000_000_000) {
            player.inventory.gold = 0;
        } else {
            player.inventory.subGold(2_000_000_000);
        }
        if (player.inventory.ruby < 500) {
            player.inventory.ruby = 0;
        } else {
            player.inventory.subRuby(500);
        }
        Service.gI().sendMoney(player);
    }

    public void UpdateBandAcc() {
        if (this.ErrorMap == 5) {
            Service.getInstance().sendThongBao(this, "Tài Khoản Của Bạn Sẽ Bị Khoá Vĩnh Viễn Sau 5 Giây Nữa!");
            PlayerService.gI().KhoaTaiKhoan(this);
        }
    }

    private void checkPlayerInMap() {
        if (this.isPl() && this.getSession().isFounder) {
            return;
        }
        if (this != null && this.zone != null) {
            if (this.isPl() && MapService.gI().isMapNguHanhSon(this.zone.map.mapId)) {
                if (this.nPoint.power < 40_000_000_000L) {
                    ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                    if (this.inventory.gold >= 2000000000) {
                        this.inventory.subGold(2000000000);
                    } else {
                        this.inventory.gold = 0;
                    }
                    this.ErrorMap += 1;
                    PlayerService.gI().sendInfoHpMpMoney(this);
                    Service.getInstance().sendBigMessage(this, 1139, "|1|Hệ Thống Band 4.0 Phát Hiện\n "
                            + "Do Bạn Có Hành Sai Trái Trong Game, Nên Chúng Tôi\n"
                            + "Đưa Bạn Về Nhà Và Phạt 2 Tỷ Vàng Để Cảnh Cáo!\n"
                            + "|7|Nếu Còn Tái Phạm Quá 5 Lần Thì Bạn Sẽ Bị Khoá Tài Khoản Account Vĩnh Viễn\n"
                            + "Số Lần Đã Tái Phạm " + this.ErrorMap + " / 5");
                }
            } else if (this.isPl() && MapService.gI().isMapCereal(this.zone.map)) {
                if (this.nPoint.power < 30_000_000_000L) {
                    ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                    if (this.inventory.gold >= 2000000000) {
                        this.inventory.subGold(2000000000);
                    } else {
                        this.inventory.gold = 0;
                    }
                    this.ErrorMap += 1;
                    PlayerService.gI().sendInfoHpMpMoney(this);
                    Service.getInstance().sendBigMessage(this, 1139, "|1|Hệ Thống Band 4.0 Phát Hiện\n "
                            + "Do Bạn Có Hành Sai Trái Trong Game, Nên Chúng Tôi\n"
                            + "Đưa Bạn Về Nhà Và Phạt 2 Tỷ Vàng Để Cảnh Cáo!\n"
                            + "|7|Nếu Còn Tái Phạm Quá 5 Lần Thì Bạn Sẽ Bị Khoá Tài Khoản Account Vĩnh Viễn\n"
                            + "Số Lần Đã Tái Phạm " + this.ErrorMap + " / 5");
                }
            } else if (this.isPl() && MapService.gI().isMapHanhTinhNgucTu(this.zone.map.mapId)) {
                if (this.nPoint.power < 60_000_000_000L) {
                    ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                    if (this.inventory.gold >= 2000000000) {
                        this.inventory.subGold(2000000000);
                    } else {
                        this.inventory.gold = 0;
                    }
                    this.ErrorMap += 1;
                    PlayerService.gI().sendInfoHpMpMoney(this);
                    Service.getInstance().sendBigMessage(this, 1139, "|1|Hệ Thống Band 4.0 Phát Hiện\n "
                            + "Do Bạn Có Hành Sai Trái Trong Game, Nên Chúng Tôi\n"
                            + "Đưa Bạn Về Nhà Và Phạt 2 Tỷ Vàng Để Cảnh Cáo!\n"
                            + "|7|Nếu Còn Tái Phạm Quá 5 Lần Thì Bạn Sẽ Bị Khoá Tài Khoản Account Vĩnh Viễn\n"
                            + "Số Lần Đã Tái Phạm " + this.ErrorMap + " / 5");
                }
            } else if (this.isPl() && MapService.gI().islanhdiabanghoi(this.zone.map.mapId)) {
                if (this.nPoint.power < 40_000_000_000L) {
                    ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                    if (this.inventory.gold >= 2000000000) {
                        this.inventory.subGold(2000000000);
                    } else {
                        this.inventory.gold = 0;
                    }
                    this.ErrorMap += 1;
                    PlayerService.gI().sendInfoHpMpMoney(this);
                    Service.getInstance().sendBigMessage(this, 1139, "|1|Hệ Thống Band 4.0 Phát Hiện\n "
                            + "Do Bạn Có Hành Sai Trái Trong Game, Nên Chúng Tôi\n"
                            + "Đưa Bạn Về Nhà Và Phạt 2 Tỷ Vàng Để Cảnh Cáo!\n"
                            + "|7|Nếu Còn Tái Phạm Quá 5 Lần Thì Bạn Sẽ Bị Khoá Tài Khoản Account Vĩnh Viễn\n"
                            + "Số Lần Đã Tái Phạm " + this.ErrorMap + " / 5");
                }
            } else if (this.isPl() && MapService.gI().isMapTuongLai(this.zone.map.mapId)) {
                if (TaskService.gI().getIdTask(this) < ConstTask.TASK_22_0) {
                    ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                    if (this.inventory.gold >= 2000000000) {
                        this.inventory.subGold(2000000000);
                    } else {
                        this.inventory.gold = 0;
                    }
                    this.ErrorMap += 1;
                    PlayerService.gI().sendInfoHpMpMoney(this);
                    Service.getInstance().sendBigMessage(this, 1139, "|1|Hệ Thống Band 4.0 Phát Hiện\n "
                            + "Do Bạn Có Hành Sai Trái Trong Game, Nên Chúng Tôi\n"
                            + "Đưa Bạn Về Nhà Và Phạt 2 Tỷ Vàng Để Cảnh Cáo!\n"
                            + "|7|Nếu Còn Tái Phạm Quá 5 Lần Thì Bạn Sẽ Bị Khoá Tài Khoản Account Vĩnh Viễn\n"
                            + "Số Lần Đã Tái Phạm " + this.ErrorMap + " / 5");
                }
            } else if (this.isPl() && MapService.gI().isMapCold(this.zone.map)) {
                if (TaskService.gI().getIdTask(this) < ConstTask.TASK_25_0) {
                    ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                    if (this.inventory.gold >= 2000000000) {
                        this.inventory.subGold(2000000000);
                    } else {
                        this.inventory.gold = 0;
                    }
                    this.ErrorMap += 1;
                    PlayerService.gI().sendInfoHpMpMoney(this);
                    Service.getInstance().sendBigMessage(this, 1139, "|1|Hệ Thống Band 4.0 Phát Hiện\n "
                            + "Do Bạn Có Hành Sai Trái Trong Game, Nên Chúng Tôi\n"
                            + "Đưa Bạn Về Nhà Và Phạt 2 Tỷ Vàng Để Cảnh Cáo!\n"
                            + "|7|Nếu Còn Tái Phạm Quá 5 Lần Thì Bạn Sẽ Bị Khoá Tài Khoản Account Vĩnh Viễn\n"
                            + "Số Lần Đã Tái Phạm " + this.ErrorMap + " / 5");
                }
            }
        }
    }

    private void checkPlayerLocation() {
        if (this.isPl() && this.zone != null && (this.location.x > this.zone.map.mapWidth || this.location.x < 0 || this.location.y > this.zone.map.mapHeight || this.location.y < 0)
                && !MapService.gI().isMapKhiGasHuyDiet(this.zone.map.mapId)) {
            if (this.inventory.gold >= 500000000) {
                this.inventory.subGold(500000000);
            } else {
                this.inventory.gold = 0;
            }
            this.ErrorLocation += 1;
            PlayerService.gI().sendInfoHpMpMoney(this);
            ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
            Service.getInstance().sendBigMessage(this, 1139, "|1|Do Phát Hiện Có Hành Vi Bất Thường Nên\n "
                    + "Chúng Tôi Đã Đưa Bạn Về Nhà Và Xử Phạt 500Tr Vàng\n"
                    + "|7|Nếu Bạn Còn Tái Phạm Quá 10 Lần\n"
                    + "Thì Bạn Sẽ Bị Khoá Tài Khoản Account 3 Ngày\n"
                    + "Số Lần Đã Tái Phạm " + this.ErrorLocation + " / 10");
        }
    }

    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isQuanTriVien() {
        return this.session.isQuanTriVien;
    }

    public void setJustRevivaled() {
        if (this.isDeTu) {
            this.justRevived = true;
            this.lastTimeRevived = System.currentTimeMillis();
        } else if (this.isBo) {
            this.justRevivedBo = true;
            this.lastTimeRevivedBo = System.currentTimeMillis();
        } else if (this.isMe) {
            this.justRevivedMe = true;
            this.lastTimeRevivedMe = System.currentTimeMillis();
        } else if (this.isNguoiYeu) {
            this.justRevivedNguoiYeu = true;
            this.lastTimeRevivedNguoiYeu = System.currentTimeMillis();
        } else if (this.isConOne) {
            this.justRevivedConone = true;
            this.lastTimeRevivedConone = System.currentTimeMillis();
        } else if (this.isConTwo) {
            this.justRevivedContwo = true;
            this.lastTimeRevivedContwo = System.currentTimeMillis();
        } else if (this.isConThree) {
            this.justRevivedConthree = true;
            this.lastTimeRevivedConthree = System.currentTimeMillis();
        }
    }

    protected void setBom(Player plAtt) {
        setDie(plAtt);
        if (Util.canDoWithTime(LastTimeBom, 5_000)) {
            Service.getInstance().CallSaiBaMen(this);
            LastTimeBom = System.currentTimeMillis();
        }
    }

    public void setDie() {
        this.setDie(null);
    }

    protected void setDie(Player plAtt) {
        if (this.isPl() && !MapService.gI().isMapWar(this.zone.map.mapId)) {
            long vangtru = this.nPoint.power / 1000000;
            if (vangtru > 32000) {
                vangtru = 32000;
            }
            int vang = (int) vangtru - Util.nextInt(10, 100);

            if (this.inventory.gold >= vang && vang >= 1) {
                this.inventory.gold -= vang;
                Service.gI().sendMoney(this);
                vang = vang * 95 / 100;
                if (vang < 10000) {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 189, vang, this.location.x, this.location.y, this.id));
                } else if (vang < 20000) {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 188, vang, this.location.x, this.location.y, this.id));
                } else {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 190, vang, this.location.x, this.location.y, this.id));
                }
            }
        }

        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.gI().point(this);
        }
        if (this.effectSkin.xDame > 1) {
            this.effectSkin.xDame = 1;
            Service.gI().point(this);
        }
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        this.effectSkill.removeSkillEffectWhenDie();
        nPoint.setHp(Util.CrisGH(0));
        nPoint.setMp(Util.CrisGH(0));
        if (this.DeTrung != null) {
            this.DeTrung.mobMeDie();
            this.DeTrung.dispose();
            this.DeTrung = null;
        }
        Service.gI().charDie(this);
        if (!this.isDeTu && !this.isBo && !this.isMe && !this.isPetFollow && !this.isDuongTang && !this.isPhanThan && !this.isNguoiYeu && !this.isConOne && !this.isConTwo && !this.isConThree && !this.isBoss
                && plAtt != null && !plAtt.isDeTu && !plAtt.isBo && !plAtt.isMe && !plAtt.isPetFollow && !plAtt.isDuongTang && !plAtt.isBoss && !plAtt.isPhanThan && !plAtt.isNguoiYeu && !plAtt.isConOne && !plAtt.isConTwo && !plAtt.isConThree) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
        }

        this.typePk = 0;
        if (this.pvp != null && this.zone.map.mapId != 140) {
            this.pvp.lose(this, TYPE_LOSE_PVP.DEAD);
        }
        if (this.PhanThan != null) {
            this.PhanThan.setDie(plAtt);
        }
        BlackBallWarService.gI().dropBlackBall(this);
        NgocRongNamec.gI().dropNamekBall(this);
        if (isHoldNamecBallTranhDoat) {
            TranhNgocService.getInstance().dropBall(this, (byte) -1);
            TranhNgocService.getInstance().sendUpdateLift(this);
        }
    }

    public void setDieLV(Player plAtt) {
        if (this.isPl() && !MapService.gI().isMapWar(this.zone.map.mapId)) {
            long vangtru = this.nPoint.power / 1000000;
            if (vangtru > 32000) {
                vangtru = 32000;
            }
            int vang = (int) vangtru - Util.nextInt(10, 100);

            if (this.inventory.gold >= vang && vang >= 1) {
                this.inventory.gold -= vang;
                Service.gI().sendMoney(this);
                vang = vang * 95 / 100;
                if (vang < 10000) {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 189, vang, this.location.x, this.location.y, this.id));
                } else if (vang < 20000) {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 188, vang, this.location.x, this.location.y, this.id));
                } else {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 190, vang, this.location.x, this.location.y, this.id));
                }
            }
        }

        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.gI().point(this);
        }
        if (this.effectSkin.xDame > 1) {
            this.effectSkin.xDame = 1;
            Service.gI().point(this);
        }
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        this.effectSkill.removeSkillEffectWhenDie();
        nPoint.setHp(Util.CrisGH(0));
        nPoint.setMp(Util.CrisGH(0));
        if (this.DeTrung != null) {
            this.DeTrung.mobMeDie();
            this.DeTrung.dispose();
            this.DeTrung = null;
        }
        Service.gI().charDie(this);
        if (!this.isDeTu && !this.isBo && !this.isMe && !this.isPetFollow && !this.isDuongTang && !this.isPhanThan && !this.isNguoiYeu && !this.isConOne && !this.isConTwo && !this.isConThree && !this.isBoss
                && plAtt != null && !plAtt.isDeTu && !plAtt.isBo && !plAtt.isMe && !plAtt.isPetFollow && !plAtt.isDuongTang && !plAtt.isBoss && !plAtt.isPhanThan && !plAtt.isNguoiYeu && !plAtt.isConOne && !plAtt.isConTwo && !plAtt.isConThree) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
        }

        this.typePk = 0;
        if (this.pvp != null && this.zone.map.mapId != 140) {
            this.pvp.lose(this, TYPE_LOSE_PVP.DEAD);
        }
        if (this.PhanThan != null) {
            this.PhanThan.setDie(plAtt);
        }
        BlackBallWarService.gI().dropBlackBall(this);
        NgocRongNamec.gI().dropNamekBall(this);
        if (isHoldNamecBallTranhDoat) {
            TranhNgocService.getInstance().dropBall(this, (byte) -1);
            TranhNgocService.getInstance().sendUpdateLift(this);
        }
    }

    public void dispose() {
        if (itemsTradeWVP != null) {
            if (!itemsTradeWVP.isEmpty()) {
                for (Item item : itemsTradeWVP) {
                    InventoryService.gI().addItemBag(this, item);
                }
            }
            itemsTradeWVP.clear();
            itemsTradeWVP = null;
        }
        if (Detu != null) {
            Detu.dispose();
            Detu = null;
        }
        if (PetFollow != null) {
            PetFollow.dispose();
            PetFollow = null;
        }
        if (Duongtang != null) {
            Duongtang.dispose();
            Duongtang = null;
        }
        if (PhanThan != null) {
            PhanThan.dispose();
            PhanThan = null;
        }
        if (Saibamen != null) {
            Saibamen.dispose();
            Saibamen = null;
        }
        if (mapBlackBall != null) {
            mapBlackBall.clear();
            mapBlackBall = null;
        }
        if (mapMaBu != null) {
            mapMaBu.clear();
            mapMaBu = null;
        }
        if (mapCapsule != null) {
            mapCapsule.clear();
            mapCapsule = null;
        }
        if (DeTrung != null) {
            DeTrung.dispose();
            DeTrung = null;
        }
        if (setClothes != null) {
            setClothes.dispose();
            setClothes = null;
        }
        if (effectSkill != null) {
            effectSkill.dispose();
            effectSkill = null;
        }
        if (mabuEgg != null) {
            mabuEgg.dispose();
            mabuEgg = null;
        }
        if (duahau != null) {
            duahau.dispose();
            duahau = null;
        }
        if (billEgg != null) {
            billEgg.dispose();
            billEgg = null;
        }
        if (playerTask != null) {
            playerTask.dispose();
            playerTask = null;
        }
        if (itemTime != null) {
            itemTime.dispose();
            itemTime = null;
        }
        if (fusion != null) {
            fusion.dispose();
            fusion = null;
        }
        if (magicTree != null) {
            magicTree.dispose();
            magicTree = null;
        }
        if (playerIntrinsic != null) {
            playerIntrinsic.dispose();
            playerIntrinsic = null;
        }
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (playerSkill != null) {
            playerSkill.dispose();
            playerSkill = null;
        }
        if (combine != null) {
            combine.dispose();
            combine = null;
        }
        if (iDMark != null) {
            iDMark.dispose();
            iDMark = null;
        }
        if (charms != null) {
            charms.dispose();
            charms = null;
        }
        if (effectSkin != null) {
            effectSkin.dispose();
            effectSkin = null;
        }
        if (gift != null) {
            gift.dispose();
            gift = null;
        }
        if (nPoint != null) {
            nPoint.dispose();
            nPoint = null;
        }
        if (effectFlagBag != null) {
            effectFlagBag.dispose();
            effectFlagBag = null;
        }
        if (itemsWoodChest != null) {
            itemsWoodChest.clear();
            itemsWoodChest = null;
        }
        if (pvp != null) {
            pvp.dispose();
            pvp = null;
        }
        if (achievement != null) {
            achievement.dispose();
            achievement = null;
        }
        if (temporaryEnemies != null) {
            temporaryEnemies.clear();
            temporaryEnemies = null;
        }
        if (temporaryEnemiesBo != null) {
            temporaryEnemiesBo.clear();
            temporaryEnemiesBo = null;
        }
        if (temporaryEnemiesMe != null) {
            temporaryEnemiesMe.clear();
            temporaryEnemiesMe = null;
        }
        if (temporaryEnemiesNguoiYeu != null) {
            temporaryEnemiesNguoiYeu.clear();
            temporaryEnemiesNguoiYeu = null;
        }
        if (temporaryEnemiesConone != null) {
            temporaryEnemiesConone.clear();
            temporaryEnemiesConone = null;
        }
        if (temporaryEnemiesContwo != null) {
            temporaryEnemiesContwo.clear();
            temporaryEnemiesContwo = null;
        }
        if (temporaryEnemiesConthree != null) {
            temporaryEnemiesConthree.clear();
            temporaryEnemiesConthree = null;
        }
        if (rewardBlackBall != null) {
            rewardBlackBall.dispose();
            rewardBlackBall = null;
        }
        if (superRank != null) {
            superRank.dispose();
            superRank = null;
        }
        if (friends != null) {
            friends.clear();
            friends = null;
        }
        if (enemies != null) {
            enemies.clear();
            enemies = null;
        }
        if (Cards != null) {
            Cards.clear();
            Cards = null;
        }
        if (dropItem != null) {
            dropItem.dispose();
            dropItem = null;
        }
        if (satellite != null) {
            satellite = null;
        }
        if (traning != null) {
            traning = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        location = null;
        maBu2H = null;
        maBuHold = null;
        effectFlagBag = null;
        clan = null;
        clanMember = null;
        session = null;
        friends = null;
        enemies = null;
        name = null;
        thongBaoTapTuDong = null;
        notify = null;
        newSkill = null;
        zoneSieuThanhThuy = null;
        itemsWoodChest = null;
        textThongBaoChangeMap = null;
        textThongBaoThua = null;
        itemEvent = null;
        Cards = null;
    }

    public String percentGold(int type) {
        try {
            if (type == 0) {
                double percent = ((double) this.goldNormar / ChonAiDay.gI().goldNormar) * 100;
                return String.valueOf(Math.ceil(percent));
            } else if (type == 1) {
                double percent = ((double) this.goldVIP / ChonAiDay.gI().goldVip) * 100;
                return String.valueOf(Math.ceil(percent));
            }
        } catch (ArithmeticException e) {
            return "0";
        }
        return "0";
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------    

}
