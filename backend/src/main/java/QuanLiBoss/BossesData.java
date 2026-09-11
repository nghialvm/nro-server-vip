package QuanLiBoss;

import consts.ConstPlayer;
import nro.skill.Skill;
import Utils.Util;

public class BossesData {

    private static final int[][] FULL_DRAGON = new int[][]{{Skill.DRAGON, 1}, {Skill.DRAGON, 2}, {Skill.DRAGON, 3}, {Skill.DRAGON, 4}, {Skill.DRAGON, 5}, {Skill.DRAGON, 6}, {Skill.DRAGON, 7}};
    private static final int[][] FULL_DEMON = new int[][]{{Skill.DEMON, 1}, {Skill.DEMON, 2}, {Skill.DEMON, 3}, {Skill.DEMON, 4}, {Skill.DEMON, 5}, {Skill.DEMON, 6}, {Skill.DEMON, 7}};
    private static final int[][] FULL_GALICK = new int[][]{{Skill.GALICK, 1}, {Skill.GALICK, 2}, {Skill.GALICK, 3}, {Skill.GALICK, 4}, {Skill.GALICK, 5}, {Skill.GALICK, 6}, {Skill.GALICK, 7}};
    private static final int[][] FULL_KAMEJOKO = new int[][]{{Skill.KAMEJOKO, 1}, {Skill.KAMEJOKO, 2}, {Skill.KAMEJOKO, 3}, {Skill.KAMEJOKO, 4}, {Skill.KAMEJOKO, 5}, {Skill.KAMEJOKO, 6}, {Skill.KAMEJOKO, 7}};
    private static final int[][] FULL_MASENKO = new int[][]{{Skill.MASENKO, 1}, {Skill.MASENKO, 2}, {Skill.MASENKO, 3}, {Skill.MASENKO, 4}, {Skill.MASENKO, 5}, {Skill.MASENKO, 6}, {Skill.MASENKO, 7}};
    private static final int[][] FULL_ANTOMIC = new int[][]{{Skill.ANTOMIC, 1}, {Skill.ANTOMIC, 2}, {Skill.ANTOMIC, 3}, {Skill.ANTOMIC, 4}, {Skill.ANTOMIC, 5}, {Skill.ANTOMIC, 6}, {Skill.ANTOMIC, 7}};
    private static final int[][] FULL_LIENHOAN = new int[][]{{Skill.LIEN_HOAN, 1}, {Skill.LIEN_HOAN, 2}, {Skill.LIEN_HOAN, 3}, {Skill.LIEN_HOAN, 4}, {Skill.LIEN_HOAN, 5}, {Skill.LIEN_HOAN, 6}, {Skill.LIEN_HOAN, 7}};
    private static final int[][] FULL_TDHS = new int[][]{{Skill.THAI_DUONG_HA_SAN, 1}, {Skill.THAI_DUONG_HA_SAN, 2}, {Skill.THAI_DUONG_HA_SAN, 3}, {Skill.THAI_DUONG_HA_SAN, 4}, {Skill.THAI_DUONG_HA_SAN, 5}, {Skill.THAI_DUONG_HA_SAN, 6}, {Skill.THAI_DUONG_HA_SAN, 7}};

    private static final int REST_1_S = 1;
    private static final int REST_2_S = 2;
    private static final int REST_5_S = 5;
    private static final int REST_10_S = 10;
    private static final int REST_20_S = 20;
    private static final int REST_30_S = 30;
    private static final int REST_1_M = 60;
    private static final int REST_2_M = 120;
    private static final int REST_5_M = 300;
    private static final int REST_10_M = 600;
    private static final int REST_15_M = 900;
    private static final int REST_20_M = 1200;
    private static final int REST_30_M = 1800;
    private static final int REST_1_H = 3600;
    private static final int REST_2_H = 7200;
    private static final int REST_24_H = 86400000;

    public static final BossData CUMBER = new BossData(
            "Cumber", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1896, 1897, 1898, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{1000000000L}, //hp
            new int[]{155}, //map join
            new int[][]{
                {Skill.GALICK, 7, 500},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.KHIEN_NANG_LUONG, 1, 240000}}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Ta muốn tìm một đối thủ xứng tầm",
                "|-1|Đi chết đi!",
                "|-1|Các ngươi không phải đối thủ của ta đâu"
            }, //text chat 2
            new String[]{"|-2|Hắn ta mạnh thật!"}, //text chat 3
            REST_10_M //type appear
    );

    //**************************************************************************
    //?
    public static final BossData TestBoss = new BossData(
            "TestBosss", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{29, 67, 13, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500_000, //dame
            new long[]{60000000L}, //hp
            new int[]{201}, //map join
            new int[][]{
                {Skill.DRAGON, 7, 1000}, {Skill.KAMEJOKO, 7, 10000},},//skill
            new String[]{}, //text chat 1
            new String[]{},
            new String[]{}, //text chat 3
            REST_10_M //type appear
    );
    public static final BossData  Darbula2 = new BossData(
            "Drabula[Địa Ngục]", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1207, 1208, 1209, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000000, //dame
            new long[]{20000000000L}, //hp
            new int[]{167,168,172,173}, //map join
            new int[][]{
                {Skill.DRAGON, 7, 1000}, {Skill.KAMEJOKO, 7, 10000},{Skill.KHIEN_NANG_LUONG, 1, 240000},{Skill.TAI_TAO_NANG_LUONG, 7, 240000}},//skill
            new String[]{}, //text chat 1
            new String[]{},
            new String[]{}, //text chat 3
            REST_10_M //type appear
    );
    public static final BossData  Darbula3 = new BossData(
            "Pikachu", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1844, 1845, 1846, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000000, //dame
            new long[]{100_000_000L}, //hp
            new int[]{4, 5, 10, 12, 13, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 60000},
                {Skill.DEMON, 1, 1000},
                {Skill.KAMEJOKO, Util.nextInt(1, 7), 4000},
                {Skill.MASENKO, Util.nextInt(1, 7), 2000},
                {Skill.ANTOMIC, Util.nextInt(1, 7), 300},}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Tránh xa ta ra, Pika Pí Ka",}, //text chat 2
            new String[]{"|-1|Các ngươi Thật là Pí Ka . Ta sẽ quay lại"}, //text chat 3
            REST_15_M //type appear
    );
    //**************************************************************************

    //**************************************************************************
    public static final BossData NguoiXayda = new BossData(
            "Người Xayda",
            ConstPlayer.XAYDA,
            new short[]{28, 147, 148, -1, -1, -1},
            Util.nextInt(50000, 80000),
            new long[]{Util.nextInt(800000000, 1000000000)},
            new int[]{201},
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(500, 1000)},
                {Skill.ANTOMIC, 7, Util.nextInt(1000, 2000)},
                {Skill.BIEN_KHI, Util.nextInt(3, 6), Util.nextInt(60000, 100000)},},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            REST_30_M
    );
    //**************************************************************************
    public static final BossData KhiCon = new BossData(
            "Khỉ Con",
            ConstPlayer.TRAI_DAT,
            new short[]{462, 463, 464, -1, -1, -1},
            50000,
            new long[]{500000000},
            new int[]{201},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.TROI, 1, 50000}},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            REST_20_M
    );
    public static final BossData KhiCon2 = new BossData(
            "Khỉ Con",
            ConstPlayer.TRAI_DAT,
            new short[]{462, 463, 464, -1, -1, -1},
            50000,
            new long[]{500000000},
            new int[]{201},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.TROI, 1, 50000}},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            REST_20_M
    );
    public static final BossData KhiCon3 = new BossData(
            "Khỉ Con",
            ConstPlayer.TRAI_DAT,
            new short[]{462, 463, 464, -1, -1, -1},
            50000,
            new long[]{500000000},
            new int[]{201},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.TROI, 1, 50000}},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            REST_20_M
    );
    public static final BossData KhiCon4 = new BossData(
            "Khỉ Con",
            ConstPlayer.TRAI_DAT,
            new short[]{462, 463, 464, -1, -1, -1},
            50000,
            new long[]{500000000},
            new int[]{201},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.TROI, 1, 50000}},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            REST_20_M
    );
    public static final BossData KhiCon5 = new BossData(
            "Khỉ Con",
            ConstPlayer.TRAI_DAT,
            new short[]{462, 463, 464, -1, -1, -1},
            50000,
            new long[]{500000000},
            new int[]{201},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.TROI, 1, 50000}},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            REST_20_M
    );
    //***************************************************************************

    public static final BossData GranolaManager = new BossData(
            "Phân Thân Granola",
            ConstPlayer.NAMEC,
            new short[]{2018, 2019, 2020, -1, -1, -1},
            1,
            new long[]{2000000000},
            new int[]{194},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 50000}
            },
            new String[]{},
            new String[]{},
            new String[]{}, //text chat 3
            REST_1_S// type appear
    );
    //**************************************************************************
    public static final BossData DrLyche = new BossData(
            "Dr Lyche", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{742, 743, 744, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new long[]{500}, //hp
            new int[]{148}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.LIEN_HOAN, 6, 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tao hơn hẳn mày, mày nên cầu cho may mắn ở phía mày đi",
                "|-1|Ha ha ha! Mắt mày mù à? Nhìn máy đo chỉ số đi!!",
                "|-1|Định chạy trốn hả, hử",
                "|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Hahaha mày đây rồi",
                "|-1|Tao đã có lệnh từ đại ca rồi"
            }, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S
    );

    public static final BossData HaChiJack = new BossData(
            "HaChiJack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{639, 640, 641, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new long[]{500}, //hp
            new int[]{148}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.LIEN_HOAN, 6, 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tao hơn hẳn mày, mày nên cầu cho may mắn ở phía mày đi",
                "|-1|Ha ha ha! Mắt mày mù à? Nhìn máy đo chỉ số đi!!",
                "|-1|Định chạy trốn hả, hử",
                "|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Hahaha mày đây rồi",
                "|-1|Tao đã có lệnh từ đại ca rồi"
            }, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.ANOTHER_LEVEL
    );
    //*************************************************************************
    public static final BossData NguuMaVuong = new BossData(
            "Ngưu Ma Vương", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{813, 814, 815, 72, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new long[]{500000000}, //hp
            new int[]{123}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 50000}
            },
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            86400
    );
    public static final BossData NgoKhong = new BossData(
            "Tôn Ngộ Không", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1357, 1358, 1359, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{2000000000L}, //hp
            new int[]{123, 124, 192, 193}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 1, 500},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.BIEN_KHI, 3, Util.nextInt(500000, 1000000)}, {Skill.BIEN_KHI, 5, Util.nextInt(500000, 1000000)}, {Skill.BIEN_KHI, 4, Util.nextInt(500000, 1000000)},
                {Skill.BIEN_KHI, 1, Util.nextInt(500000, 1000000)}, {Skill.BIEN_KHI, 6, Util.nextInt(500000, 1000000)}, {Skill.BIEN_KHI, 2, Util.nextInt(500000, 1000000)},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, Util.nextInt(10000, 20000)}
            },
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M// type appear
    );
    //***************************************************************************
    public static final BossData GiaiPhongAnNgoKhong = new BossData(
            "Ngộ Không Được Giải Phong Ấn",
            (byte) 0,
            new short[]{462, 463, 464, -1, -1, -1},
            50000,
            new long[]{100},
            new int[]{0},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 2000},
                {Skill.THAI_DUONG_HA_SAN, 7, 40000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 50000},
                {Skill.BIEN_KHI, 1, 20000}, {Skill.BIEN_KHI, 2, 25000}, {Skill.BIEN_KHI, 3, 30000},
                {Skill.BIEN_KHI, 4, 35000}, {Skill.BIEN_KHI, 5, 40000},
                {Skill.BIEN_KHI, 6, 45000}, {Skill.BIEN_KHI, 7, 50000}},
            new String[]{}, // text chat 1
            new String[]{}, // text chat 2
            new String[]{}, // text chat 3
            86400
    );
    //****************************************************************************
    public static final BossData HoTongDuongTang = new BossData(
            "Đường Tăng",
            (byte) 0,
            new short[]{467, 468, 469, -1, -1, -1},
            100000,
            new long[]{100},
            new int[]{0},
            new int[][]{
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 15000}},
            new String[]{}, // text chat 1
            new String[]{"|-1|Đi Chậm Thôi",
                "|-1|Cứu Ta ",
                "|-1|Ui da Đau Quá",
                "|-1|Đi Nhanh Thế? "
            }, // text chat 2
            new String[]{}, // text chat 3
            86400
    );

    //**************************************************************************
    public static final BossData oren = new BossData(
            "Oren", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1246, 1247, 1248, -1, 15, -1}, //outfit {head, body, leg, bag, aura, eff}
            60000, //dame
            new long[]{1500000000L}, //hp
            new int[]{206}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 700},
                {Skill.KAMEJOKO, 7, 1500},
                {Skill.SOCOLA, 7, 80000},
                {Skill.QUA_CAU_KENH_KHI, 7, 120000}}, //skill
            new String[]{"|-1|Gaaaaaa !!!!!!!!",
                "|-2|Các ngươi là ai vậy",
                "|-1|Sức mạnh huỷ diệt!"
            }, //text chat 1
            new String[]{
                "|-1|Các ngươi không phải đối thủ của ta đâu"
            }, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M //second rest
    );
    public static final BossData kami = new BossData(
            "Kami", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1243, 1244, 1245, -1, 14, -1}, //outfit {head, body, leg, bag, aura, eff}
            80000, //dame
            new long[]{1500000000L}, //hp
            new int[]{206}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.MASENKO, 7, 2000},
                {Skill.MAKANKOSAPPO, 7, 120000}}, //skill
            new String[]{"|-1|Hahaha cuối cùng cũng tìm được ngươi!",
                "|-2|Các ngươi là ai vậy",
                "|-1|Vua saiyan!"
            }, //text chat 1
            new String[]{"|-1|Tên này gà nhỉ, Oren",
                "|-1|Hắn Ta cũng khá, phải hạ gục nhanh nhất!",
                "|-1|Các ngươi không phải đối thủ của ta đâu"
            }, //text chat 2
            new String[]{}, //text chat 3
            -1
    );
    //____________________________________________________________________________

    //**************************************************************************
    public static final BossData BLACKGOKU = new BossData(
            "Black Goku", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{550, 880, 881, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            70000, //dame
            new long[]{1000000000L}, //hp
            new int[]{97, 98, 99}, //map join
            new int[][]{
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},
                {Skill.KHIEN_NANG_LUONG, 1, 60000},
                {Skill.KAMEJOKO, 7, 1500},
                {Skill.DRAGON, 7, 700}},
            new String[]{"|-1|Ta là Sôn Gô Ku",
                "|-1|Cơ thể này,sức mạnh này",
                "|-1|Ta khá thích việc loại bỏ các ngươi",
                "|-1|Mau chấp nhận số phận đi lũ sâu bọ"
            }, //text chat 1
            new String[]{"|-1|Các ngươi chỉ có vậy thôi sao?",
                "|-1|Đúng là loài người thấp kém",
                "|-2|Ngươi nói như thể ngươi không phải con người vậy?",
                "|-2|Chiếc nhẫn kia lẽ nào ngươi là một Kaioshin?!",
                "|-1|Các ngươi không nên biết quá nhiều",
                "|-2|Xem đòn đánh của ta đây !",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!"
            }, //text chat 2
            new String[]{"|-1|Biến hình! Super Sayan Rose"}, //text chat 3
            REST_15_M //second rest
    );

        public static final BossData ZAMAS = new BossData(
            "Kaioshin Zamas", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{433, 904, 905, -1, -1, -1}, //outfit
            120000, //dame
            new long[]{2000000000L}, //hp
            new int[]{97}, //map
            new int[][]{{Skill.DICH_CHUYEN_TUC_THOI, 7, 40000}, {Skill.KHIEN_NANG_LUONG, 1, 90000}, {Skill.KAMEJOKO, 7, 2000}, {Skill.LIEN_HOAN, 7, 1000}}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Kia là một con người sao?", "|-3|Ủa tên kia là ai vậy?", "|-2|Lẽ nào đúng như chúng ta đã nghĩ", "|-1|Lũ con người không đủ tư cách để nói chuyện với ta", "|-2|Zamas! Tại sao chứ !", "|-1|Ta sẽ cho người biết sức mạnh của một vị thần là như thế nào !", "|-1|Ta là kaioshin của vũ trụ thứ 10", "|-1|Tên của ta là Zamas, ta sẽ thay đổi thế giới này", "|-1|Lũ con người các ngươi là những thứ ta cần loại bỏ đầu tiên", "|-2|Tại sao các ngươi lại nhắm tới con người bọn ta chứ?", "|-1|Bởi vì ta muốn thực hiện kế hoạch đưa con người về số 0 !", "|-1|Lần này ta không nương tay đâu!", "|-2|Ngươi thực sự rất mạnh. Nhưng chưa đủ thực lực đâu!!", "|-1|Cái gì!? Đó là điều ngu ngốc nhất ta từng nghe! Mau biến đi", "|-1|Hắn thực sự rất mạnh, đúng là cuộc chiến hay", "|-3|Không lí nào ta lại run sợ bọn con người sao"}, //text chat 2
            new String[]{}, //text chat 3
            REST_15_M
    );

        public static final BossData THANZM2 = new BossData(
            "Thần Zamas Tối Thượng", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{903, 904, 905, -1, -1, -1}, //outfit
            150000, //dame
            new long[]{3000000000L}, //hp
            new int[]{98}, //map
            new int[][]{{Skill.DICH_CHUYEN_TUC_THOI, 7, 40000}, {Skill.KHIEN_NANG_LUONG, 1, 90000}, {Skill.KAMEJOKO, 7, 2000}, {Skill.LIEN_HOAN, 7, 1000}}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Ta chính là thế giới", "|-1|Ta chính là công lí", "|-1|Hãy chiêm ngưỡng vẻ đẹp của ta !Hỡi con người", "|-1|Sức mạnh to lớn nằm trong cơ thể bất tử", "|-1|Ta sẽ đem công lí tới toàn bộ vũ trụ này", "|-2|Ngươi cứ lải nhải hoài 2 chữ công lí vậy?", "|-1|Lũ các ngươi làm ta thấy đau rồi ấy haha"}, //text chat 2
            new String[]{}, //text chat 3
            REST_15_M
    );

    //************************************************************************** Boss hủy diệt
            public static final BossData WHIS_TWO = new BossData(
            "Thiên sứ Whis", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{838, 839, 840, -1, -1, -1}, //outfit
            999999999, //dame
            new long[]{100L}, //hp
            new int[]{202}, //map
            new int[][]{{Skill.THOI_MIEN, 7, 147804}}, //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M
    );
                public static final BossData BERUS = new BossData(
            "Thần Hủy Diệt Berrus", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{508, 509, 510, -1, -1, -1}, //outfit
            500000, //dame
            new long[]{2000000000L}, //hp
            new int[]{202}, //map
            new int[][]{{Skill.MASENKO, 1, 604}, {Skill.DRAGON, 7, 558}}, //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
           REST_30_M
    );
    //*****************************************************************************
        public static final BossData VADOS = new BossData(
            "Thiên sứ Vados", //name
            ConstPlayer.NAMEC, //gender
            new short[]{530, 531, 532, -1, -1, -1}, //outfit
            999999999, //dame
            new long[]{100L}, //hp
            new int[]{203}, //map
            new int[][]{{Skill.THOI_MIEN, 4, 176879}}, //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            1200
    );
        public static final BossData CHAMPA = new BossData(
            "Thần Hủy Diệt Champa", //name
            ConstPlayer.NAMEC, //gender
            new short[]{511, 512, 513, -1, -1, 77}, //outfit
            500000, //dame
            new long[]{2000000000L}, //hp
            new int[]{203}, //map
            new int[][]{{Skill.MASENKO, 1, 576}, {Skill.DEMON, 7, 947}}, //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M
    );

//*******************************************************************************
//===============================================================================
//===============================================================================
//---------------------BOSS MAI SU  PILAF -------------------------------    
    public static final BossData KAMIRIN = new BossData(
            "Thần Tài 1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{356, 357, 358, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            5205500, //dame
            new long[]{100000000L}, //hp
            new int[]{43}, //map join

            (int[][]) Util.addArray(FULL_GALICK, FULL_KAMEJOKO, FULL_LIENHOAN, FULL_ANTOMIC, FULL_DEMON, FULL_MASENKO, FULL_DRAGON), //skill
            new String[]{"|-1|Sôn..gôku",
                "|-2|Lại là Soome à.. rốt cuộc ông ta đã tạo ra bao nhiêu Anhrin nhân tạo thế nhỉ?",
                "|-1|Bọn ta là rôbốt sát thủ, sinh ra từ máy tính ngài Soome,..",
                "|-1|..cho một mục tiêu duy nhất là giết Sôngôku!",
                "|-2|Máy tính? Để giết Gôku sao?",
                "|-1|Mong muốn trả thù Gôku của ngài Soome đã được lưu hết vào máy tính..",
                "|-1|.., Bọn ta sinh ra từ lòng căm thù ngày một tăng bên trong chiếc máy tính có chứa mong muốn trả thù",
                "|-1|Mục tiêu của bọn ta chỉ là Gôku, nhưng mà.. nếu ngươi mà cản đường thì là chuyện khác!",}, //text chat 1
            new String[]{"|-1|Sao thế hả? Ta mới chỉ khởi động thôi mà!",
                "|-2|Ngươi đánh giá thấp bọn ta quá đấy!",
                "|-2|Đừng có tưởng bở, lũ sâu bọ member!",
                "|-1|Nếu có ý định gây trở ngại cho cuộc chiến giữa ta và Sôngôku, thì ta cũng sẽ giết ngươi ngay lập tức",
                "|-2|Ngươi tưởng ta để cho ngươi giết được ta ngay à?",
                "|-2|Đúng là mạnh mồm thật đấy!",
                "|-2|Đỡ này",}, //text chat 2
            new String[]{"|-1|Sô..Sông...gôku....."}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
//-------------------------------BOSS DATA--------------------------------------
    public static final BossData POLICE = new BossData(
            "Police",
            ConstPlayer.NAMEC,
            new short[]{1614, 631, 632, -1, -1, -1},
            100000,
            new long[]{10000000},
            new int[]{5},
            new int[][]{
                {Skill.MASENKO, 1, 4000}},
            new String[]{},
            new String[]{},
            new String[]{"|-1|Đây là kết cục của những kẻ phản kháng",
                "|-1|Mau đưa hắn về Trại Giam",
                "|-2|Đừng mà..."},
            REST_30_S
    );
    public static final BossData POLICE_2 = new BossData(
            "Police",
            ConstPlayer.NAMEC,
            new short[]{1614, 631, 632, -1, -1, -1},
            100000,
            new long[]{10000000},
            new int[]{5},
            new int[][]{
                {Skill.MASENKO, 1, 4000}},
            new String[]{},
            new String[]{},
            new String[]{"|-1|Đây là kết cục của những kẻ phản kháng",
                "|-1|Mau đưa hắn về Trại Giam",
                "|-2|Đừng mà..."},
            REST_30_S
    );
    //-------------------------------BOSS DATA--------------------------------------
    public static final BossData POLICE_CHARACTERISTIC = new BossData(
            "Police",
            ConstPlayer.NAMEC,
            new short[]{1614, 631, 632, -1, -1, -1},
            2000000,
            new long[]{20000000},
            new int[]{5},
            new int[][]{
                {Skill.MASENKO, 1, 4000}},
            new String[]{},
            new String[]{},
            new String[]{"|-1|Đây là kết cục của những kẻ phản kháng",
                "|-1|Mau đưa hắn về Trại Giam",
                "|-2|Đừng mà..."},
            REST_1_S
    );
    public static final BossData POLICE_CHARACTERISTIC_2 = new BossData(
            "Police",
            ConstPlayer.NAMEC,
            new short[]{1614, 631, 632, -1, -1, -1},
            20000000,
            new long[]{20000000},
            new int[]{5},
            new int[][]{
                {Skill.MASENKO, 1, 4000}},
            new String[]{},
            new String[]{},
            new String[]{"|-1|Đây là kết cục của những kẻ phản kháng",
                "|-1|Mau đưa hắn về Trại Giam",
                "|-2|Đừng mà..."},
            REST_1_S
    );
    public static final BossData POLICE_CHARACTERISTIC_3 = new BossData(
            "Police",
            ConstPlayer.NAMEC,
            new short[]{1614, 631, 632, -1, -1, -1},
            20000000,
            new long[]{20000000},
            new int[]{5},
            new int[][]{
                {Skill.MASENKO, 1, 4000}},
            new String[]{},
            new String[]{},
            new String[]{"|-1|Đây là kết cục của những kẻ phản kháng",
                "|-1|Mau đưa hắn về Trại Giam",
                "|-2|Đừng mà..."},
            REST_1_S
    );
    public static final BossData GOHAN_NHAT_NGUYET = new BossData(
            "Gohan Nhật Nguyệt", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1381, 1382, 1383, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new long[]{300000L}, //hp
            new int[]{250}, //map join
            new int[][]{
                {Skill.MASENKO, 3, 1000},
                {Skill.LIEN_HOAN, 7, 1000}},
            new String[]{"|-1|Hế lô em,anh đứng đây từ chiều",
                "|-1|Mày hiểu thế là sao chứ? Cuối cùng tao đã có thể giết mày!",
                "|-2|Tao lại sợ mày quá cơ,cho bố cái địa chỉ!",
                "|-1|Mày làm tao phấn khích rồi đấy hahaha.."
            }, //text chat 1
            new String[]{"|-1|Tao hơn hẳn mày, mày nên cầu cho may mắn ở phía mày đi",
                "|-1|Ha ha ha! Mắt mày mù à? Nhìn máy đo chỉ số đi!!",
                "|-1|Định chạy trốn hả, hử",
                "|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Hahaha mày đây rồi",
                "|-1|Tao đã có lệnh từ đại ca rồi"
            }, //text chat 2
            new String[]{"|-2|Đẹp trai nó phải thế"}, //text chat 3
            5 //second rest
    );
    //////
    public static final BossData BILL = new BossData(
            "Berrus", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1381, 1382, 1383, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new long[]{300000L}, //hp
            new int[]{250}, //map join
            new int[][]{
                {Skill.MASENKO, 3, 1000},
                {Skill.LIEN_HOAN, 7, 1000}},
            new String[]{"|-1|Hế lô em,anh đứng đây từ chiều",
                "|-1|Mày hiểu thế là sao chứ? Cuối cùng tao đã có thể giết mày!",
                "|-2|Tao lại sợ mày quá cơ,cho bố cái địa chỉ!",
                "|-1|Mày làm tao phấn khích rồi đấy hahaha.."
            }, //text chat 1
            new String[]{"|-1|Tao hơn hẳn mày, mày nên cầu cho may mắn ở phía mày đi",
                "|-1|Ha ha ha! Mắt mày mù à? Nhìn máy đo chỉ số đi!!",
                "|-1|Định chạy trốn hả, hử",
                "|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Hahaha mày đây rồi",
                "|-1|Tao đã có lệnh từ đại ca rồi"
            }, //text chat 2
            new String[]{"|-2|Đẹp trai nó phải thế"}, //text chat 3
            5 //second rest
    );
//--------------------------BOSS FROST------------------------------------------
    public static final BossData FROST = new BossData(
            "Frost 1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{493, 494, 495, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            80000, //dame
            new long[]{1000000000}, //hp
            new int[]{108, 109}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.MASENKO, 7, 3000},
                {Skill.DRAGON, 7, 800},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Các ngươi thật là yếu ớt",
                "|-1|Chán quá!",
                "|-1|Không có ai đủ mạnh để đấu với ta sao?",}, //text chat 2
            new String[]{"|-1|Biến hình !"}, //text chat 3
            REST_15_M
    );
    public static final BossData FROST_2 = new BossData(
            "Frost 2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{496, 497, 498, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{1500000000}, //hp
            new int[]{108, 109}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.MASENKO, 7, 3000},
                {Skill.DRAGON, 7, 800},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Các ngươi thật là yếu ớt",
                "|-1|Chán quá!",
                "|-1|Không có ai đủ mạnh để đấu với ta sao?",}, //text chat 2
            new String[]{"|-1|Biến hình !"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    public static final BossData FROST_3 = new BossData(
            "Frost 3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{499, 500, 501, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            120000, //dame
            new long[]{2000000000}, //hp
            new int[]{108, 109}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.MASENKO, 7, 3000},
                {Skill.DRAGON, 7, 800},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Các ngươi thật là yếu ớt",
                "|-1|Chán quá!",
                "|-1|Không có ai đủ mạnh để đấu với ta sao?",
                "|-1|Ta nghèo lắm! Đừng săn ta nữa",}, //text chat 2
            new String[]{"|-1|Lần sau hãy tha cho ta..."}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
//--------------------------BOSS COOLER-----------------------------------------
    public static final BossData COOLER = new BossData(
            "Cooler", //name
            ConstPlayer.XAYDA, //gender
            new short[]{317, 318, 319, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            32000, //dame
            new long[]{1000000000}, //hp
            new int[]{110}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{"|-1|Ta sẽ cho chúng bây biết sức mạnh thực sự của dân tộc Frost Demons"
            }, //text chat 1
            new String[]{"|-1|Tụi mày có giỏi thì xông vào cứu hắn đi",
                "|-1|Trận địa pháo mini",
                "|-1|Nên giải quyết con sâu nào trước đây",
                "|-1|HA HA HA",
                "|-1|Ta chính là Vũ Trụ Đệ Nhất Cao Thủ",
                "|-1|Xí hụt ha ha",
                "|-1|Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha",
                "|-1|Chúng mày nghĩ kiến lại thắng nổi khủng long sao",
                "|-1|Hô hô hô",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!"
            }, //text chat 2
            new String[]{"|-1|Nãy giờ ta chưa thèm tung hết sức đâu",
                "|-1|Biến hình, hây aaaa..."}, //text chat 3
            REST_30_M //second rest
    );

    public static final BossData COOLER_2 = new BossData(
            "Cooler 2", //name
            ConstPlayer.XAYDA, //gender
            new short[]{320, 321, 322, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new long[]{2000000000}, //hp
            new int[]{110}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{"|-1|Ta sẽ cho chúng bây biết sức mạnh thực sự của dân tộc Frost Demons"
            }, //text chat 1
            new String[]{"|-1|Tụi mày có giỏi thì xông vào cứu hắn đi",
                "|-1|Trận địa pháo mini",
                "|-1|Nên giải quyết con sâu nào trước đây",
                "|-1|HA HA HA",
                "|-1|Ta chính là Vũ Trụ Đệ Nhất Cao Thủ",
                "|-1|Xí hụt ha ha",
                "|-1|Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha",
                "|-1|Chúng mày nghĩ kiến lại thắng nổi khủng long sao",
                "|-1|Hô hô hô",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!",}, //text chat 2
            new String[]{"|-1|Mọi chuyện chưa kết thúc đâu",
                "|-1|Mọi chuyện chưa kết thúc đâu"},
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    public static final BossData Chilled = new BossData(
            "Chilled", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1024, 1025, 1026, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            32000, //dame
            new long[]{1000000000}, //hp
            new int[]{160, 161, 162, 163}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Nãy giờ ta chưa thèm tung hết sức đâu",
                "|-1|Biến hình, hây aaaa..."}, //text chat 3
            REST_30_M //second rest
    );

    public static final BossData Chilled_2 = new BossData(
            "Chilled 2", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1021, 1022, 1023, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new long[]{2000000000}, //hp
            new int[]{160, 161, 162, 163}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Mọi chuyện chưa kết thúc đâu",
                "|-1|Mọi chuyện chưa kết thúc đâu"},
            TypeAppear.ANOTHER_LEVEL //type appear
    );
//--------------------------BOSS TASK FUTURE------------------------------------
    public static final BossData BLACK_GOKU = new BossData(
            "Black Goku", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{550, 551, 552, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new long[]{1_000_000_000}, //hp
            new int[]{102, 92, 93, 94, 96, 97, 98, 99, 100}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 1000000},
                {Skill.GALICK, 7, 1000},},
            new String[]{"|-1|Ta là Sôn Gô Ku",
                "|-1|Cơ thể này,sức mạnh này",
                "|-1|Ta khá thích việc loại bỏ các ngươi",
                "|-1|Mau chấp nhận số phận đi lũ sâu bọ"
            }, //text chat 1
            new String[]{"|-1|Các ngươi chỉ có vậy thôi sao?",
                "|-1|Đúng là loài người thấp kém",
                "|-2|Ngươi nói như thể ngươi không phải con người vậy?",
                "|-2|Chiếc nhẫn kia lẽ nào ngươi là một Kaioshin?!",
                "|-1|Các ngươi không nên biết quá nhiều",
                "|-2|Xem đòn đánh của ta đây !",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!"
            }, //text chat 2
            new String[]{"|-1|Biến hình! Super Saiyan Rose"}, //text chat 3
            REST_15_M //second rest
    );

    public static final BossData SUPER_BLACK_GOKU = new BossData(
            "Super Black Goku", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{553, 551, 552, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{2_000_000_000}, //hp
            new int[]{102, 92, 93, 94, 96, 97, 98, 99, 100}, //map join
            new int[][]{
                {Skill.THAI_DUONG_HA_SAN, 1, 30000},
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.GALICK, 7, 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Ta chính là người mang thân thể của Songoku",
                "|-1|Sức mạnh của ta là không có giới hạn",
                "|-1|Ta sẽ thống trị vũ trụ",
                "|-1|Để ta nói cho nghe,người Sayan sau khi hồi phục sức mạnh sẽ tăng lên rất nhiều",
                "|-2|Tại sao ngươi lại lấy thân thể của songoku chứ?"
            }, //text chat 2

            new String[]{"|-1|Chúng ta sẽ gặp lại nhau sớm thôi",
                "|-2|Ngươi nói gì chứ?"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    //--------------------------BOSS GOMAH -----------------------------------
    public static final BossData GOMAH= new BossData(
            "Gomah Tà Ám", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1907, 1908, 1909, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new long[]{2_000_000_000}, //hp
            new int[]{194, 195}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.THAI_DUONG_HA_SAN, 3, 30000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 30000},
                {Skill.GALICK, 7, 1000},},
            new String[]{"|-1|Ta là Sôn Gô Ku",
                "|-1|Cơ thể này,sức mạnh này",
                "|-1|Ta khá thích việc loại bỏ các ngươi",
                "|-1|Mau chấp nhận số phận đi lũ sâu bọ"
            }, //text chat 1
            new String[]{"|-1|Các ngươi chỉ có vậy thôi sao?",
                "|-1|Đúng là loài người thấp kém",
                "|-2|Ngươi nói như thể ngươi không phải con người vậy?",
                "|-2|Chiếc nhẫn kia lẽ nào ngươi là một Kaioshin?!",
                "|-1|Các ngươi không nên biết quá nhiều",
                "|-2|Xem đòn đánh của ta đây !",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!"
            }, //text chat 2
            new String[]{"|-1|Biến hình! Super Saiyan Rose"}, //text chat 3
            REST_15_M //second rest
    );
    public static final BossData GOMAH1 = new BossData(
            "Gomah Hắc Ám", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1910, 1912, 1913, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{2_000_000_000}, //hp
            new int[]{194, 195}, //map join
            new int[][]{
                {Skill.THAI_DUONG_HA_SAN, 3, 30000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 30000},
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.GALICK, 7, 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Ta chính là người mang thân thể của Songoku",
                "|-1|Sức mạnh của ta là không có giới hạn",
                "|-1|Ta sẽ thống trị vũ trụ",
                "|-1|Để ta nói cho nghe,người Sayan sau khi hồi phục sức mạnh sẽ tăng lên rất nhiều",
                "|-2|Tại sao ngươi lại lấy thân thể của songoku chứ?"
            }, //text chat 2

            new String[]{"|-1|Chúng ta sẽ gặp lại nhau sớm thôi",
                "|-2|Ngươi nói gì chứ?"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
//--------------------------BOSS VALENTINE EVENT--------------------------------
    public static final BossData THO_BUNMA = new BossData(
            "Thỏ Bunma", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{409, 410, 411, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1, //dame
            new long[]{500_000}, //hp
            new int[]{0}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 60_000}},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_1_M //second rest
    );
//--------------------------BOSS TRUNG THU EVENT--------------------------------
    public static final BossData THO_DAI_KA = new BossData(
            "Thỏ Đại Ka", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{403, 404, 405, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{1_000_000}, //hp
            new int[]{5, 13, 20, 84}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 5_000},
                {Skill.THAI_DUONG_HA_SAN, 1, 60_000}},
            new String[]{},
            new String[]{"|-1|Ta thách ngươi đấy nhóc con",
                "|-1|Muốn biến thành củ cà rốt thì cứ nhào vô",
                "|-1|Bắt tay cái nào",
                "|-1|Mi đã chạm vào tay ta rồi con ạ"},
            new String[]{"|-1|Ối, xin ông đừng nấu cà rốt!"},
            REST_10_M //second rest
    );
    public static final BossData NGUYETTHAN = new BossData(
            "Nguyệt thần", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1282, 1283, 1284, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{25_000}, //hp
            new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Util.isTrue(50, 100) ? Skill.ANTOMIC : Util.isTrue(50, 100) ? Skill.MASENKO : Skill.KAMEJOKO, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Các ngươi chờ đấy, ta sẽ quay lại sau"}, //text chat 3
            REST_15_M,//second rest
            new int[]{BossID.NHATTHAN}
    );
    public static final BossData NHATTTHAN = new BossData(
            "Nhật thần", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1293, 1294, 1295, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{25_000}, //hp
            new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Util.isTrue(50, 100) ? Skill.ANTOMIC : Util.isTrue(50, 100) ? Skill.MASENKO : Skill.KAMEJOKO, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Các ngươi chờ đấy, ta sẽ quay lại sau"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    //
    public static final BossData GOGETA = new BossData(
            "Gogeta", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1578, 1581, 1582, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{25_000}, //hp
            new int[]{1, 2, 3, 4, 6, 8, 9, 10, 11, 12, 15, 16, 17, 18, 19}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Util.isTrue(50, 100) ? Skill.ANTOMIC : Util.isTrue(50, 100) ? Skill.MASENKO : Skill.KAMEJOKO, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Các ngươi chờ đấy, ta sẽ quay lại sau"}, //text chat 3
            REST_15_M,//second rest
            new int[]{BossID.OMEGA}
    );
    public static final BossData OMEGA = new BossData(
            "Omega", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1569, 1570, 1571, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{25_000}, //hp
            new int[]{1, 2, 3, 4, 6, 8, 9, 10, 11, 12, 15, 16, 17, 18, 19}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Util.isTrue(50, 100) ? Skill.ANTOMIC : Util.isTrue(50, 100) ? Skill.MASENKO : Skill.KAMEJOKO, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Các ngươi chờ đấy, ta sẽ quay lại sau"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
//--------------------------BOSS HALLOWEEN EVENT--------------------------------
    public static final BossData MA_TROI = new BossData(
            "Ma trơi", //name
            ConstPlayer.XAYDA, //gender
            new short[]{651, 652, 653, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{500000}, //hp
            new int[]{3, 4, 5, 6, 10, 11, 12, 13, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khà khà"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M
    );

    public static final BossData DOI = new BossData(
            "Dơi", //name
            ConstPlayer.XAYDA, //gender
            new short[]{654, 655, 656, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{500000}, //hp
            new int[]{3, 4, 5, 6, 10, 11, 12, 13, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khà khà"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M
    );

    public static final BossData XUONG_KHO = new BossData(
            "Xương khô", //name
            ConstPlayer.XAYDA, //gender
            new short[]{545, 548, 549, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{500000}, //hp
            new int[]{3, 4, 5, 6, 10, 11, 12, 13, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khà khà"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M
    );

    public static final BossData BI_NGO = new BossData(
            "Vua Bí", //name
            ConstPlayer.XAYDA, //gender
            new short[]{754, 755, 756, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100_000, //dame
            new long[]{2_000_000_000}, //hp
            new int[]{3, 4}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, Util.nextInt(3000, 5000)},
                {Skill.THAI_DUONG_HA_SAN, 1, Util.nextInt(15000, 30000)}},
            new String[]{
                "|-1|Ngươi dám bước vào Lâu Đài Bí Ngô sao?",
                "|-1|Thời gian của ta sắp cạn... lời nguyền đang lớn dần.",
                "|-1|Nếu ngươi thật sự có hy vọng, hãy cứu lấy ta trước khi quá muộn!"
            },
            new String[]{
                "|-1|Đừng lại gần ta! Ta không kiểm soát được nữa!",
                "|-1|Sức mạnh của Bí Ngô Bóng Tối... ngươi không thể hiểu!",
                "|-1|Ta sẽ nghiền nát ngươi như một quả bí chín rụng!"
            },
            new String[]{
                "|-1|Cuối cùng... ta cũng được tự do...",
                "|-1|Lời nguyền... đã kết thúc...",
                "|-1|Hạt giống hy vọng... đã nảy mầm rồi..."
            },
            REST_15_M
    );
//--------------------------BOSS VULAN EVENT------------------------------------
    public static final BossData PIKKON = new BossData(
            "Pikkon", //name
            ConstPlayer.NAMEC, //gender
            new short[]{1555, 1556, 1557, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100_000, //dame
            new long[]{2_000_000_000L}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 1, Util.nextInt(500, 1000)}},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_15_M //second rest
    );
//--------------------------BOSS CHRIST MAS EVENT-------------------------------
    public static final BossData TUAN_LOC = new BossData(
            "Tuần lộc", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{718, 719, 720, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{5000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42, 43, 44}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|éc éc"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );

    public static final BossData GOKU_GOD_NOEL = new BossData(
            "Super Xayda God", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1362, 1363, 1364, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            70_000, //dame
            new long[]{25000L}, //hp
            new int[]{177, 178, 179}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000}},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M,
            new int[]{BossID.BROLY_NOEL}
    );

    public static final BossData BROLY_NOEL = new BossData(
            "Broly", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1359, 1360, 1361, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            70_000, //dame
            new long[]{25000L}, //hp
            new int[]{177, 178, 179}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000}},
            new String[]{},
            new String[]{},
            new String[]{},
            TypeAppear.APPEAR_WITH_ANOTHER
    );

    public static final BossData COLD_NOEL = new BossData(
            "Golden Cooler", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{709, 710, 711, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100_000, //dame
            new long[]{2_000_000_000L}, //hp
            new int[]{174, 175, 176, 177, 178, 179}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)},
                {Skill.MASENKO, 1, Util.nextInt(2000, 3000)}},
            new String[]{},
            new String[]{"|-1|Trận địa pháo mini"},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData GOKU_NOEL = new BossData(
            "Gôku Santa", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1344, 1345, 1346, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            70_000, //dame
            new long[]{2_000_000_000L}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)},
                {Skill.KAMEJOKO, Util.nextInt(5, 6), Util.nextInt(2000, 3000)}},
            new String[]{"|-1|Ngọn lửa Thiêng ở Cây Thông Vĩnh Cửu, nơi giữ ấm cho cả thế giới trong mùa đông, bất ngờ bị đánh cắp!",
                "|-2|Ông già Noel, quẫn trí vì không thể phát quà trong cái lạnh buốt giá, đã triệu hồi một người hùng có trái tim nóng nhất vũ trụ… Songoku."},
            new String[]{"|-1|Ngươi mạnh đấy… Nhưng ta còn mạnh hơn khi nghĩ về những người mình yêu quý!",
                "|-1|Đây là cú đấm vì Gohan!",
                "|-1|Cú đá này… vì ChiChi bắt tôi dọn nhà trước khi đi!",
                "|-1|Và chiêu cuối này — là để mọi người có một Giáng Sinh trọn vẹn!"},
            new String[]{"|-2|Cảm ơn nhé! Giờ thì… ông già Noel có thể phát quà được rồi!"},
            REST_15_M //second rest
    );
    public static final BossData CHICHI_NOEL = new BossData(
            "Chi Chi Tuần Lộc", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1350, 1351, 1352, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50_000, //dame
            new long[]{2_000_000_000L}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)},
                {Skill.KAMEJOKO, Util.nextInt(5, 6), Util.nextInt(3000, 4000)}},
            new String[]{"|-2|Tương truyền, có một tuần lộc nữ canh giữ thời gian Giáng Sinh – tên nàng là ChiChi.",
                "|-2|Khi ông già Noel luôn bị trễ vì đám tuần lộc ham chơi và Goku lại... ngủ quên, ChiChi đã biến hình thành Tuần Lộc Chiến Binh, kéo xe thay đàn em và “quản lý lịch phát quà” bằng nắm đấm thép và trái tim nóng.",
                "|-1|Đêm Giáng Sinh là để yêu thương…",
                "|-1|Nhưng nếu ai đó dám quên quà, quên lời hứa, hay dám lười như Goku…",
                "|-1|Thì xin lỗi, tôi không phát quà đâu — tôi phát đấm."},
            new String[]{"|-1|Chạy đi, kẻ nào không biết trân trọng Giáng Sinh.",
                "|-1|Vì khi chuông ngân, ta sẽ không tha thứ – ta sẽ xuất chiêu!"},
            new String[]{"|-1|Khônggg…! Goku mà biết tôi thua thì… lại trốn làm việc nhà nữa mất…",
                "|-1|Trận này thua… nhưng ông già Noel vẫn phải phát quà đúng giờ, nghe rõ chưa?!",
                "|-1|Ta mà hồi sinh lại… đứa nào quên tặng quà nữa là ta bẻ gãy gậy kẹo luôn!"},
            REST_15_M //second rest
    );
//--------------------------BOSS ENVENT LUNAR NEW YEAR--------------------------
    //NEW YEAR DRAGON
    public static final BossData NEW_YEAR_DRAGON_1 = new BossData(
            "Gôku dragon", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1404, 1405, 1406, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100_000, //dame
            new long[]{1000000000L}, //hp
            new int[]{5, 13, 20}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tết đến rồi, tết đến rồi...",
                "|-1|Thằng nào có lì xì, đưa hết cho tao mau",
                "|-1|Đưa tiền đây!",
                "|-1|hehe, có lì xì rồi, có lì xì rồi...",
                "|-1|ha ha ha"
            }, //text chat 2
            new String[]{"|-1|Thôi tao trả tụi bay lì xì đó!"}, //text chat 3
            REST_15_M //second rest
    );
    public static final BossData NEW_YEAR_DRAGON_2 = new BossData(
            "Pôcôlô dragon", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1410, 1411, 1412, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150_000, //dame
            new long[]{1500000000L}, //hp
            new int[]{5, 13, 20}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tết đến rồi, tết đến rồi...",
                "|-1|Thằng nào có lì xì, đưa hết cho tao mau",
                "|-1|Đưa tiền đây!",
                "|-1|hehe, có lì xì rồi, có lì xì rồi...",
                "|-1|ha ha ha"
            }, //text chat 2
            new String[]{"|-1|Thôi tao trả tụi bay lì xì đó!"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    public static final BossData NEW_YEAR_DRAGON_3 = new BossData(
            "Cađíc dragon", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1407, 1408, 1409, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            200_000, //dame
            new long[]{2000000000L}, //hp
            new int[]{5, 13, 20}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tết đến rồi, tết đến rồi...",
                "|-1|Thằng nào có lì xì, đưa hết cho tao mau",
                "|-1|Đưa tiền đây!",
                "|-1|hehe, có lì xì rồi, có lì xì rồi...",
                "|-1|ha ha ha"
            }, //text chat 2
            new String[]{"|-1|Thôi tao trả tụi bay lì xì đó!"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    //THẦN TÀI
    public static final BossData THAN_TAI_1 = new BossData(
            "Thần tài 1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1386, 1387, 1388, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100_000, //dame
            new long[]{2000000000L}, //hp
            new int[]{0, 7, 14}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tết đến rồi, tết đến rồi...",
                "|-1|Thằng nào có lì xì, đưa hết cho tao mau",
                "|-1|Đưa tiền đây!",
                "|-1|hehe, có lì xì rồi, có lì xì rồi...",
                "|-1|ha ha ha"
            }, //text chat 2
            new String[]{"|-1|Thôi tao trả tụi bay lì xì đó!"}, //text chat 3
            REST_15_M //second rest
    );
    public static final BossData THAN_TAI_2 = new BossData(
            "Thần tài 2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1392, 1393, 1394, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150_000, //dame
            new long[]{2000000000L}, //hp
            new int[]{0, 7, 14}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tết đến rồi, tết đến rồi...",
                "|-1|Thằng nào có lì xì, đưa hết cho tao mau",
                "|-1|Đưa tiền đây!",
                "|-1|hehe, có lì xì rồi, có lì xì rồi...",
                "|-1|ha ha ha"
            }, //text chat 2
            new String[]{"|-1|Thôi tao trả tụi bay lì xì đó!"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    public static final BossData THAN_TAI_3 = new BossData(
            "Thần tài 3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1389, 1390, 1391, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            200_000, //dame
            new long[]{2000000000L}, //hp
            new int[]{0, 7, 14}, //map join
            new int[][]{
                {Skill.DRAGON, 1, Util.nextInt(500, 1000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tết đến rồi, tết đến rồi...",
                "|-1|Thằng nào có lì xì, đưa hết cho tao mau",
                "|-1|Đưa tiền đây!",
                "|-1|hehe, có lì xì rồi, có lì xì rồi...",
                "|-1|ha ha ha"
            }, //text chat 2
            new String[]{"|-1|Thôi tao trả tụi bay lì xì đó!"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    //
    public static final BossData LAN_CON = new BossData(
            "Lân con", //name
            ConstPlayer.XAYDA, //gender
            new short[]{763, 764, 765, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{5000000}, //hp
            new int[]{0, 1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 10, 15, 16, 17, 18, 19, 14, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42, 43, 44}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tùng tùng xèng xèng"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );
    public static final BossData BE_NA = new BossData(
            "", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1595, 1596, 1597, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{5_000_000}, //hp
            new int[]{0, 1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 10, 15, 16, 17, 18, 19, 14, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42, 43, 44}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|le le le",
                "|-1|hi hi hi"
            }, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );
    public static final BossData PI_LONG = new BossData(
            "PiLong", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1383, 1384, 1385, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{5_000_000}, //hp
            new int[]{1, 2, 3, 4, 5, 6, 8, 9, 11, 12, 13, 10, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42, 43, 44}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Happy new year"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );
    public static final BossData MEO_DEN = new BossData(
            "Mèo đen", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1183, 1184, 1185, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{5_000_000}, //hp
            new int[]{0, 1, 3, 4, 5, 6, 7, 8, 11, 12, 13, 10, 14, 15, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42, 43, 44}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Happy new year"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );
//--------------------------BOSS TIỂU ĐỘI PILAP---------------------------------
    public static final BossData MAI = new BossData(
            "Mai", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{615, 616, 617, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10, //dame
            new long[]{100}, //hp
            new int[]{0}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 500}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );

    public static final BossData SHU = new BossData(
            "Su", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{618, 619, 620, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10, //dame
            new long[]{100}, //hp
            new int[]{0}, //map join
            new int[][]{
                {Skill.DEMON, 1, 500}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat
            TypeAppear.APPEAR_WITH_ANOTHER
    );

    public static final BossData PI_LAP = new BossData(
            "PiLap", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{612, 613, 614, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10, //dame
            new long[]{100}, //hp
            new int[]{0}, //map join
            new int[][]{
                {Skill.GALICK, 1, 500}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M,
            new int[]{BossID.MAI, BossID.SHU}
    );
//--------------------------BOSS SUPER BROLY------------------------------------
    public static final BossData SUPPER_BROLY = new BossData(
            "Super Broly", //name
            ConstPlayer.XAYDA, //gender
            new short[]{294, 295, 296, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{500}, //hp
            new int[]{4, 5, 10, 12, 13, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 25000},
                {Skill.DEMON, 1, 1000},
                {Skill.KAMEJOKO, Util.nextInt(1, 7), 4000},
                {Skill.MASENKO, Util.nextInt(1, 7), 2000},
                {Skill.ANTOMIC, Util.nextInt(1, 7), 300},}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Tránh xa ta ra, đừng để ta nổi giận",}, //text chat 2
            new String[]{"|-1|Các ngươi giỏi lắm. Ta sẽ quay lại"}, //text chat 3
            REST_2_H//type appear
    );
    public static final BossData SUPPER_BROLY_ZONE0 = new BossData(
            "Super Broly", //name
            ConstPlayer.XAYDA, //gender
            new short[]{294, 295, 296, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{500}, //hp
            new int[]{4, 5, 10, 12, 13, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 25000},
                {Skill.DEMON, 1, 1000},
                {Skill.KAMEJOKO, Util.nextInt(1, 7), 4000},
                {Skill.MASENKO, Util.nextInt(1, 7), 2000},
                {Skill.ANTOMIC, Util.nextInt(1, 7), 300},}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Tránh xa ta ra, đừng để ta nổi giận",}, //text chat 2
            new String[]{"|-1|Các ngươi giỏi lắm. Ta sẽ quay lại"}, //text chat 3
            REST_2_H//type appear
    );
    public static final BossData SUPPER_BROLY_NEW = new BossData(
            "Super Broly", //name
            ConstPlayer.XAYDA, //gender
            new short[]{294, 295, 296, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{100000000}, //hp
            new int[]{4, 5, 10, 12, 13, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 4, 25000},
                {Skill.DEMON, 1, 1000},
                {Skill.KAMEJOKO, Util.nextInt(1, 7), 4000},
                {Skill.MASENKO, Util.nextInt(1, 7), 2000},
                {Skill.ANTOMIC, Util.nextInt(1, 7), 300},}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Tránh xa ta ra, đừng để ta nổi giận",}, //text chat 2
            new String[]{"|-1|Các ngươi giỏi lắm. Ta sẽ quay lại"}, //text chat 3
            REST_5_M//type appear
    );
//---------------------BOSS TIỂU ĐỘI SÁT THỦ NAMEK------------------------------
    public static final BossData SO_4_NAMEK = new BossData(
            "Số 4 - Namek", //name
            ConstPlayer.XAYDA, //gender
            new short[]{168, 169, 170, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            170000, //dame
            new long[]{15000000}, //hp
            new int[]{7, 8, 9, 10, 11, 12, 13, 25, 34, 33, 43}, //map join
            //           new int[]{86}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.MASENKO, 7, 1000},
                {Skill.THOI_MIEN, 7, 100000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Các ngươi không nhúc nhích được sao?",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!",
                "|-1|Ta mà lại thua được sao?",
                "|-1|Hãy trả thù cho ta!"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    public static final BossData SO_3_NAMEK = new BossData(
            "Số 3 - Namek", //name
            ConstPlayer.XAYDA, //gender
            new short[]{174, 175, 176, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            180000, //dame
            new long[]{16000000}, //hp
            new int[]{7, 8, 9, 10, 11, 12, 13, 25, 34, 33, 43}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.ANTOMIC, 4, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!",
                "|-1|Ta mà lại thua được sao?",
                "|-1|Hãy trả thù cho ta!"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );
    public static final BossData SO_2_NAMEK = new BossData(
            "Số 2 - Namek", //name
            ConstPlayer.XAYDA, //gender
            new short[]{171, 172, 173, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            207200, //dame
            new long[]{17000000}, //hp
            new int[]{7, 8, 9, 10, 11, 12, 13, 25, 34, 33, 43}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.ANTOMIC, 3, 3000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!",
                "|-1|Ta mà lại thua được sao?",
                "|-1|Hãy trả thù cho ta!"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );
    public static final BossData SO_1_NAMEK = new BossData(
            "Số 1 - Namek", //name
            ConstPlayer.XAYDA, //gender
            new short[]{177, 178, 179, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            250200, //dame
            new long[]{20000000}, //hp
            new int[]{7, 8, 9, 10, 11, 12, 13, 25, 34, 33, 43}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.KAMEJOKO, 4, 10000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!",
                "|-1|Ta mà lại thua được sao?",
                "|-1|Hãy trả thù cho ta!"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );
    public static final BossData TIEU_DOI_TRUONG_NAMEK = new BossData(
            "Tiểu đội trưởng - Namek", //name
            ConstPlayer.XAYDA, //gender
            new short[]{180, 181, 182, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            300000, //dame
            new long[]{22000000}, //hp
            new int[]{7, 8, 9, 10, 11, 12, 13, 25, 34, 33, 43}, //map join
            new int[][]{
                {Skill.SOCOLA, 7, 1000},
                {Skill.MASENKO, 7, 1000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!"
            }, //text chat 3
            REST_10_M,
            new int[]{BossID.SO_4_NAMEK, BossID.SO_3_NAMEK, BossID.SO_2_NAMEK, BossID.SO_1_NAMEK,} //type appear
    );
//---------------------BOSS TASK------------------------------------------------
    //XEN BO HUNG
    public static final BossData XEN_BO_HUNG_1 = new BossData(
            "Xên bọ hung",
            ConstPlayer.XAYDA,
            new short[]{228, 229, 230, -1, -1, -1},
            100000,
            new long[]{100000000},
            new int[]{100},
            new int[][]{
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.LIEN_HOAN, 7, 10000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 10000}},//skill
            new String[]{"|-2|Cái gì kia vậy!? Đó là loài gì thế!!?",
                "|-1|Hôm nay sẽ là ngày đáng nhớ đây!",
                "|-1|Ta sẽ hấp thụ số 17 và 18 để đạt được dạng hoàn hảo!",
                "|-1|Sao vậy, Picôlô? Nếu ngươi muốn ngăn ta lại thì xong lên đi chứ!?"
            }, //text chat 1
            new String[]{"|-2|Hắn làm ta bất ngờ đấy! Khốn kiếp!",
                "|-2|Tên đó muốn hấp thụ số 17 và 18 sao?",
                "|-1|Đến đây nào! Khi kết hợp với ta, ngươi sẽ trở nên bất bại, trở thành một thể sống hoàn mỹ!",
                "|-2|Mơ đi, sao ta phải để ngươi hấp thu hả!?",
                "|-2|Dù muốn hay không, ngươi cũng sẽ bị ta hấp thu thôi..",
                "|-2|Chúng ta không thể để hắn đạt đến dạng hoàn hảo được!",
                "|-2|Mục tiêu của hắn không phải là Sôngôku.., mà là phá hủy cả vũ trụ này!",
                "|-1|Làm đứt đuôi ta ư? Đừng quên ta có tế bào của Picôlô!!",
                "|-1|Ta có thể tái tạo.. mọi bộ phận cơ thể!",
                "|-2|Vậy thì để ngăn cản ngươi đạt đến dạng hoàn hảo, ta phải tiêu diệt ngươi!",
                "|-2|Hắn quá mạnh! Mình có thể làm được gì đây!?",
                "|-1|Có vẻ như ta đã trở nên quá mạnh, chắc là ta đã giết nhiều người hơn dự tính!!",
                "|-1|Ngươi không thể thắng nổi ta! Từ bỏ đi!!",
                "|-1|Đến lúc ta hấp thu ngươi rồi",
                "|-2|Đồ quái vật chết tiệt...",
                "|-1|Hê hê hê, rồi ngươi sẽ trở thành một phần của con quái vật này thôi!",
                "|-1|Lại thêm một tên ngốc nữa chán sống!"
            }, //text chat 2
            new String[]{"|-2|Khốn kiếp, Pic.. hắn bị Cell hấp thu rồi!!"}, //text chat 3
            REST_10_M
    );
    public static final BossData XEN_BO_HUNG_2 = new BossData(
            "Xên bọ hung 2",
            ConstPlayer.XAYDA,
            new short[]{231, 232, 233, -1, -1, -1},
            100000,
            new long[]{200000000},
            new int[]{100},
            new int[][]{
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 1000},
                {Skill.LIEN_HOAN, 7, 10000}}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-2|Nguy rồi... thực sự nguy to rồi!",
                "|-1|Các ngươi nghĩ có thể chạy được sao!?",
                "|-1|Muốn chạy khỏi ta thì đừng hòng!!",
                "|-1|Ta cũng ngạc nhiên với tốc độ của mình! Đó tất nhiên là do ta hấp thụ được số 17!",
                "|-2|Hắn nhanh quá!!",
                "|-1|Ta muốn thử xem sức mạnh này đến đâu...",
                "|-1|Hmm.. có vẻ như sức mạnh này đã tăng lên gấp bội!",
                "|-1|Đã đến lúc ta đạt đến trạng thái hoàn hảo.!",
                "|-1|Có vẻ như ngươi muốn bị ép hơn là tự nguyện!!",
                "|-2|Bây giờ ta chưa thể thắng được ngươi!! Nhưng ngươi đừng hòng huyênh hoang!!!",
                "|-1|Muốn chạy à!!? Ta sẽ không để ngươi thoát đâu!!",}, //text chat 2
            new String[]{"|-1|Đến lúc rồi!"}, //text chat 3
            TypeAppear.ANOTHER_LEVEL
    );
    public static final BossData XEN_BO_HUNG_3 = new BossData(
            "Xên hoàn thiện",
            ConstPlayer.XAYDA,
            new short[]{234, 235, 236, -1, -1, -1},
            100000,
            new long[]{300000000},
            new int[]{100},
            new int[][]{
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 10000},
                {Skill.KHIEN_NANG_LUONG, 7, 100000},
                {Skill.TAI_TAO_NANG_LUONG, 5, 10000},
                {Skill.LIEN_HOAN, 7, 10000},
                {Skill.THOI_MIEN, 7, 100000}},
            //skill

            new String[]{"|-2|Cuối cùng hắn cũng đã biến đổi",
                "|-2|Khốn kiếp! Phải kết liễu hắn ngay lúc này!"
            }, //text chat 1
            new String[]{"|-2|Cell đã đạt đến dạng hoàn hảo rồi!",
                "|-2|Đồ khốn, sao ngươi dám làm vậy với số 18!!",
                "|-2|Không ấn tượng lắm với dạng hoàn hảo của ngươi..",
                "|-2|Sao hắn không hề hấn gì?",
                "|-1|Xin lỗi.. Ngươi có thể giúp ta làm nóng cơ thể lên không !?",
                "|-2|Tình hình nguy cấp rồi!",
                "|-2|Khốn kiếp! Ngươi không chú tâm vào trận đấu!",
                "|-1|Thì ta đã bảo đây chỉ là làm nóng cơ thể mà!!",
                "|-1|Giờ ngươi chỉ là rác rưởi mà thôi!",
                "|-2|Không thể nào! Ngươi dù sao cũng chỉ là đồ sâu bọ!",}, //text chat 2
            new String[]{"|-1|Oái.. không...",
                "|-1|Cơ thể hoàn hảo của ta!!",
                "|-1|Ta không tin chuyện này sẽ xảy ra!!",
                "|-1|Đồ khốn kiếp!! Rồi ngươi sẽ phải trả giá"
            }, //text chat 3
            TypeAppear.ANOTHER_LEVEL
    );
    public static final BossData SIEU_BO_HUNG_1 = new BossData(
            "Xên Hoàn Thiện",
            ConstPlayer.XAYDA,
            new short[]{234, 235, 236, -1, -1, -1},
            100000,
            new long[]{100000000},
            new int[]{103},
            new int[][]{
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 20000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 120000},
                {Skill.GALICK, 7, 1000},
                {Skill.THAI_DUONG_HA_SAN, 7, 50000}
            },
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M,
            new int[]{BossID.XEN_CON_1, BossID.XEN_CON_2, BossID.XEN_CON_3, BossID.XEN_CON_4, BossID.XEN_CON_5, BossID.XEN_CON_6, BossID.XEN_CON_7}
    );
    public static final BossData SIEU_BO_HUNG_2 = new BossData(
            "Siêu Bọ Hung",
            ConstPlayer.XAYDA,
            new short[]{234, 235, 236, -1, -1, -1},
            300000,
            new long[]{500000000},
            new int[]{103},
            new int[][]{
                {Skill.DEMON, 3, 1}, {Skill.DEMON, 6, 2}, {Skill.DRAGON, 7, 3}, {Skill.DRAGON, 1, 4}, {Skill.GALICK, 5, 5},
                {Skill.KAMEJOKO, 7, 6}, {Skill.KAMEJOKO, 6, 7}, {Skill.KAMEJOKO, 5, 8}, {Skill.KAMEJOKO, 4, 9}, {Skill.KAMEJOKO, 3, 10}, {Skill.KAMEJOKO, 2, 11}, {Skill.KAMEJOKO, 1, 12},
                {Skill.ANTOMIC, 1, 13}, {Skill.ANTOMIC, 2, 14}, {Skill.ANTOMIC, 3, 15}, {Skill.ANTOMIC, 4, 16}, {Skill.ANTOMIC, 5, 17}, {Skill.ANTOMIC, 6, 19}, {Skill.ANTOMIC, 7, 20},
                {Skill.MASENKO, 1, 21}, {Skill.MASENKO, 5, 22}, {Skill.MASENKO, 6, 23},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},
                {Skill.KHIEN_NANG_LUONG, 7, 180000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 120000},
                {Skill.THOI_MIEN, 7, 30000}},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M
    );
    //XEN CON
    public static final BossData XEN_CON_1 = new BossData(
            "Xên con 1", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData XEN_CON_2 = new BossData(
            "Xên con 2", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData XEN_CON_3 = new BossData(
            "Xên con 3", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData XEN_CON_4 = new BossData(
            "Xên con 4", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData XEN_CON_5 = new BossData(
            "Xên con 5", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData XEN_CON_6 = new BossData(
            "Xên con 6", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData XEN_CON_7 = new BossData(
            "Xên con 7", //name
            ConstPlayer.XAYDA, //gender
            new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            150000, //dame
            new long[]{150000000}, //hp
            new int[]{103}, //map join
            (int[][]) Util.addArray(FULL_DEMON, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    //ROBOT SAT THU
    //ANDROID 19, 20
    public static final BossData DR_KORE = new BossData(
            "Dr.Kôrê", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{255, 256, 257, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12000, //dame
            new long[]{500000000}, //hp
            new int[]{96, 94, 93}, //map join
            new int[][]{
                {Skill.THOI_MIEN, 3, 10000},
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.LIEN_HOAN, 7, 1000},},//skill
            new String[]{"|-2|Chào anh! em đứng đây từ chiều",
                "|-1|Quái lạ! Sao chúng biết rõ tung tích của bọn ta thế nhỉ?",
                "|-1|Chúng còn biết chính xác ta sẽ xuất hiện ở đây để đón đánh nữa!",
                "|-1|Chúng mày là ai từ đâu tới?Cho tao xin cái địa chỉ",
                "|-2|Điều ấy biết hay không cũng không còn quan trọng nữa",
                "|-1|Ừ bọn bây chỉ là hạng tôm tép ta chẳng cần biết tên làm gì!",
                "|-1|Số 19! Xuất chiêu đi nào",
                "|0|Okê đại ca, em sẽ xử lý bọn này trong vòng 2 tiếng."
            }, //text chat 1
            new String[]{"|-1|Oải rồi hả?",
                "|-1|Ê cố lên nhóc",
                "|-1|Chán",
                "|-1|Mi khá đấy, nhưng so với ta cũng chỉ là hạng tôm tép",
                "|-1|Lôi Công Trảo",
                "|-1|Cho dù ngươi có mạnh đến đâu.. thì cũng không đánh bại được rôbốt bọn ta",
                "|-2|Lão già khôn thật!!",
                "|-2|Hừ! Lão già khốn kiếp!",}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M, //second rest
            new int[]{BossID.ANDROID_19}
    );
    public static final BossData ANDROID_19 = new BossData(
            "Android 19", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{249, 250, 251, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12200, //dame
            new long[]{30000000}, //hp
            new int[]{96, 94, 93}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.LIEN_HOAN, 7, 10000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?",
                "|-1|Ê cố lên nhóc",
                "|-1|Chán",
                "|-1|Ngươi sẽ không bao giờ thắng được đâu!!",
                "|-2|Ngươi vừa hút được nhiều rồi đấy, nhưng giờ thì đừng hòng!!",}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    //ANDROID 13, 14, 15
    public static final BossData ANDROID_13 = new BossData(
            "Android 13", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{252, 253, 254, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12055, //dame
            new long[]{500000000}, //hp
            new int[]{104}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.LIEN_HOAN, 7, 1000},},//skill
            new String[]{"|-1|Sôn..gôku",
                "|-2|Lại là tiến sĩ Kôrê à.. rốt cuộc ông ta đã tạo ra bao nhiêu rôbốt nhân tạo thế nhỉ?",
                "|-1|Bọn ta là rôbốt sát thủ, sinh ra từ máy tính ngài Kôrê,..",
                "|-1|..cho một mục tiêu duy nhất là giết Sôngôku!",
                "|-2|Máy tính? Để giết Gôku sao?",
                "|-1|Mong muốn trả thù Gôku của ngài Kôrê đã được lưu hết vào máy tính..",
                "|-1|.., Bọn ta sinh ra từ lòng căm thù ngày một tăng bên trong chiếc máy tính có chứa mong muốn trả thù",
                "|-1|Mục tiêu của bọn ta chỉ là Gôku, nhưng mà.. nếu ngươi mà cản đường thì là chuyện khác!",}, //text chat 1
            new String[]{"|-1|Sao thế hả? Ta mới chỉ khởi động thôi mà!",
                "|-2|Ngươi đánh giá thấp bọn ta quá đấy!",
                "|-2|Đừng có tưởng bở, lũ sâu bọ!",
                "|-1|Nếu có ý định gây trở ngại cho cuộc chiến giữa ta và Sôngôku, thì ta cũng sẽ giết ngươi ngay lập tức",
                "|-2|Ngươi tưởng ta để cho ngươi giết được ta ngay à?",
                "|-2|Đúng là mạnh mồm thật đấy!",
                "|-2|Đỡ này",}, //text chat 2
            new String[]{"|-1|Sô..Sông...gôku....."}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
    public static final BossData ANDROID_14 = new BossData(
            "Android 14", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{246, 247, 248, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12000, //dame
            new long[]{50000000}, //hp
            new int[]{104}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.LIEN_HOAN, 7, 1000},},//skill
            new String[]{"|-2|Các ngươi là ai?",
                "|-2|Ta không thể cảm nhận được khí của các ngươi, các ngươi không phải là con người đúng chứ?",
                "|-2|Ta hiểu rồi, các ngươi là rôbốt sát thủ do tiến sĩ Kôrê tạo ra chứ gì?"
            }, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|0|Số 14 và số 15 tiêu tùng cả rồi à?"}, //text chat 3
            REST_10_M,
            new int[]{BossID.ANDROID_13, BossID.ANDROID_15}
    );
    public static final BossData ANDROID_15 = new BossData(
            "Android 15", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{261, 262, 263, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            19200, //dame
            new long[]{500000000}, //hp
            new int[]{104}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.LIEN_HOAN, 7, 1000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-2|Thì ra vẫn chỉ là một đống sắt vụn!"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    //PIC, POC, KINGKONG
    public static final BossData PIC = new BossData(
            "Pic", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{237, 238, 239, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            17022, //dame
            new long[]{70000000}, //hp
            new int[]{97, 98, 99}, //map join
            (int[][]) Util.addArray(FULL_GALICK, FULL_KAMEJOKO), //skill
            new String[]{"|-1|Chào! Có Gôku ở đây không?",
                "|-3|Tôi không nghĩ Gôku ở đây đâu!",
                "|-2|Biến khỏi đây đi, Gôku không có ở đây đâu!",
                "|-1|Bọn ta cũng nghĩ vậy, ngươi nói cho bọn ta biết hắn ở đâu được không!?",
                "|-2|Ngươi nghĩ bọn ta sẽ nói sao??",
                "|-1|Nếu ngươi không chịu nói bọn ta sẽ phải ra tay.."
            }, //text chat 1
            new String[]{"|-1|Ngươi thực sự rất mạnh dù không phải là một rôbốt. Ngươi không phải là Piccôlô",
                "|-1|Nhưng ta không quan tâm ngươi là ai, ta chỉ cần biết Gôku đang ở đâu!",
                "|-1|Sao ngươi không chịu nói cho ta biết chứ!?",
                "|-2|Mục đích của ngươi không phải là giết Gôku sao? Vì vậy ta không nói cho ngươi biết cậu ấy đang ở đâu",
                "|-1|Vậy thì ta bắt buộc phải tiếp tục đánh buộc ngươi phải nói ra!",
                "|-1|Lần này ta không nương tay đâu!",
                "|-2|Ngươi thực sự rất nhanh. Nhưng chưa đủ thực lực đâu!!",
                "|-1|Cái gì!? Đó là điều ngu ngốc nhất ta từng nghe.. ta là chiến binh mạnh nhất từ trước đến giờ.!",
                "|1|Hắn thực sự rất mạnh, đúng là cuộc chiến cân sức",
                "|-3|Pic, trả nhẽ cậu lại để thua mấy tên nhóc vặt này sao?"
            }, //text chat 2
            new String[]{"|1|Pic tiêu rồi, tớ lên trước nhé!",
                "|-3|Okê, xin cứ tự nhiên"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    public static final BossData POC = new BossData(
            "Poc", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{240, 241, 242, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            18000, //dame
            new long[]{75000000}, //hp
            new int[]{97, 98, 99}, //map join
            (int[][]) Util.addArray(FULL_KAMEJOKO), //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Đừng tưởng ta đây là con gái mà dễ bắt nạt nhé",
                "|-1|Khôn hồn thì chỉ chỗ Gôku cho bọn ta nhanh đi",
                "|-3|Coi kìa, một lũ xúm lại bắt nạt một cô gái kìa..",
                "|-1|Đừng có mà trọng nam khinh nữ",
                "|-2|Tại sao cô gái xinh đẹp thế này mà lại là rôbốt nhỉ?"
            }, //text chat 2
            new String[]{"|-2|Cô gái xinh đẹp vậy mà lại bị tên tiến sĩ Kôrê biến thành người máy.."}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    public static final BossData KING_KONG = new BossData(
            "King Kong", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{243, 244, 245, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12000, //dame
            new long[]{80000000}, //hp
            new int[]{97, 98, 99}, //map join
            (int[][]) Util.addArray(FULL_LIENHOAN, FULL_MASENKO), //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Mau đền mạng cho những người bạn của ta",
                "|-1|Sức mạnh của ta chênh nhau với các ngươi một trời một vực đấy!",
                "|-1|Thằng kia đừng để bọn nó trói tao !"
            }, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M,
            new int[]{BossID.PIC, BossID.POC}
    );
    //FIDE DAI KA
    public static final BossData FIDE_DAI_CA_1 = new BossData(
            "Fide đại ca 1", //name
            ConstPlayer.XAYDA, //gender
            new short[]{183, 184, 185, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            32000, //dame
            new long[]{50000000}, //hp
            new int[]{80}, //map join
            (int[][]) Util.addArray(FULL_ANTOMIC, FULL_GALICK), //skill
            new String[]{"|-2|Fide!!!, với những gì ngươi đã làm với người Xayda và Namek...",
                "|-2|Ta phán ngươi tội: tử hình",
                "|-1|Khẩu khí ngang tàng lắm",
                "|-1|Xem bản lĩnh của ngươi thế nào đã"
            }, //text chat 1
            new String[]{"|-1|Các ngươi tới số rồi mới gặp phải ta",
                "|-1|Trận địa pháo mini"
            }, //text chat 2
            new String[]{"|-1|Ác quỷ biến hình, hây aaaa..."}, //text chat 3
            REST_10_M //second rest
    );
    public static final BossData FIDE_DAI_CA_2 = new BossData(
            "Fide đại ca 2", //name
            ConstPlayer.XAYDA, //gender
            new short[]{186, 187, 188, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new long[]{75000000}, //hp
            new int[]{80}, //map join
            (int[][]) Util.addArray(FULL_ANTOMIC, FULL_LIENHOAN), //skill
            new String[]{"|-1|Hê hê, cẩn thận đi",
                "|-1|Nếu đã biến thành thế này thì ta sẽ không nhùn nhặn như trước đâu"
            }, //text chat 1
            new String[]{"|-1|Oải rồi hả?",
                "|-1|Ê cố lên nhóc",
                "|-1|Ôi, xin lỗi nhé. Sức mạnh lớn quá nên ta cũng chẳng điều khiển nổi nữa!",
                "|-1|Hahaha! Ấn tượng đấy! Tên nào cũng lủi rất nhanh!",
                "|-2|A...Tốc độ nhanh quá!",
                "|-1|Hình như... mày không phải là một thằng nhóc bình thường!",
                "|-1|Mấy đòn vừa rồi, nói thật là cũng đau đấy!",
                "|-1|Nhưng tiếc rằng đối thủ của mày lại là Fide này...",
                "|-2|Chết tiệt.. chúng ta đã đánh giá quá thấp sức mạnh của hắn!!",
                "|-2|Đồ..Đồ quái vật..!",
                "|-2|Tốc độ kinh hoàng quá! Ai mà né nổi chứ!",}, //text chat 2
            new String[]{"|-1|Ác quỷ biến hình, Graaaaa...."}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    public static final BossData FIDE_DAI_CA_3 = new BossData(
            "Fide đại ca 3", //name
            ConstPlayer.XAYDA, //gender
            new short[]{189, 190, 191, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            122000, //dame
            new long[]{100000000}, //hp
            new int[]{80}, //map join
            (int[][]) Util.addArray(FULL_MASENKO, FULL_LIENHOAN), //skill
            new String[]{"|-1|Ta sẽ cho các ngươi thấy đâu mới là sức mạnh của ta!!"}, //text chat 1
            new String[]{"|-1|Ta nói các ngươi rồi! Sức mạnh này của ta còn đáng sợ hơn địa ngục!!",
                "|-1|Ta chơi thêm chút nữa chắc ngươi chóng mặt buồn nôn mất!!",
                "|-2|Ăn gì mà khỏe thế!",
                "|-2|Chết đi Fide!!!!",
                "|-1|Hô hô hô hô",
                "|-1|Chán thật! Khí của ngươi sắp hết rồi. Để ta tiễn ngươi về địa ngục!",
                "|-1|Ngươi quá tự cao rồi đấy,xem ta đây!",}, //text chat 2
            new String[]{"|-1|Lũ khốn..",
                "|-1|..Một ngày nào đó ta sẽ quay lại và trả thù các ngươi",
                "|-1|Nhớ mặt tao đấy !",}, //text chat 3
            TypeAppear.ANOTHER_LEVEL //type appear
    );
    //TIEU DOI SAT THU
    public static final BossData SO_4 = new BossData(
            "Số 4", //name
            ConstPlayer.XAYDA, //gender
            new short[]{168, 169, 170, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{50000000}, //hp
            new int[]{79, 82, 83}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.MASENKO, 7, 1000},
                {Skill.THOI_MIEN, 7, 100000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả",
                "|-1|Ê cố lên nhóc",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|Chán",
                "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Chỉ là bọn con nít",
                "|-1|HAHAHA"
            }, //text chat 2
            new String[]{"|-1|Fide gọi ta về, ngươi có ngon thì chờ ở đây",
                "|2|Để tao xử nó cho"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    public static final BossData SO_3 = new BossData(
            "Số 3", //name
            ConstPlayer.XAYDA, //gender
            new short[]{174, 175, 176, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            11000, //dame
            new long[]{55000000}, //hp
            new int[]{79, 82, 83}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.ANTOMIC, 4, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả",
                "|-1|Ê cố lên nhóc",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|Chán",
                "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Chỉ là bọn con nít",
                "|-1|HAHAHA"
            }, //text chat 2
            new String[]{"|-1|Fide gọi ta về, ngươi có ngon thì chờ ở đây",
                "|1|Để tao xử nó cho",
                "|0|Để tao xử nó cho"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );
    // Để tao xử nó cho S2, S1
    public static final BossData SO_2 = new BossData(
            "Số 2", //name
            ConstPlayer.XAYDA, //gender
            new short[]{171, 172, 173, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12000, //dame
            new long[]{60000000}, //hp
            new int[]{79, 82, 83}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.ANTOMIC, 3, 3000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả",
                "|-1|Ê cố lên nhóc",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|Chán",
                "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Chỉ là bọn con nít",
                "|-1|HAHAHA"
            }, //text chat 2
            new String[]{"|-1|Fide gọi ta về, ngươi có ngon thì chờ ở đây",
                "|3|Để tao xử nó cho"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );
    public static final BossData SO_1 = new BossData(
            "Số 1", //name
            ConstPlayer.XAYDA, //gender
            new short[]{177, 178, 179, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12500, //dame
            new long[]{65000000}, //hp
            new int[]{79, 82, 83}, //map join
            new int[][]{
                {Skill.LIEN_HOAN, 7, 1000},
                {Skill.KAMEJOKO, 4, 10000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả",
                "|-1|Ê cố lên nhóc",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|Chán",
                "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Chỉ là bọn con nít",
                "|-1|HAHAHA"
            }, //text chat 2
            new String[]{"|-1|Fide gọi ta về, ngươi có ngon thì chờ ở đây",
                "|3|Để tao xử nó cho"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );
    public static final BossData TIEU_DOI_TRUONG = new BossData(
            "Tiểu đội trưởng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{180, 181, 182, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            13000, //dame
            new long[]{70000000}, //hp
            new int[]{79, 82, 83}, //map join
            new int[][]{
                {Skill.MASENKO, 7, 1000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả",
                "|-1|Ê cố lên nhóc",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|Chán",
                "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Chỉ là bọn con nít",
                "|-1|HAHAHA"
            }, //text chat 2
            new String[]{"|-1|Fide gọi ta về, ngươi có ngon thì chờ ở đây"}, //text chat 3
            REST_10_M,
            new int[]{BossID.SO_2, BossID.SO_1, BossID.SO_3, BossID.SO_4} //type appear
    );
    //NAPA
    public static final BossData KUKU = new BossData(
            "Kuku", //name
            ConstPlayer.XAYDA, //gender
            new short[]{159, 160, 161, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            9000, //dame
            new long[]{12000000}, //hp
            new int[]{68, 69, 70, 71, 72}, //map join
            new int[][]{
                {Skill.MASENKO, 3, 1000},
                {Skill.LIEN_HOAN, 7, 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Haha, mày đây rồi",
                "|-1|Tao đã có lệnh của đại ca Fide rồi",
                "|-1|Tao hơn hẳn mày, nhìn máy đo đi",
                "|-1|Mày yếu đi đó, với sức chiến đấu đó sao có thể thắng được tao",
                "|-1|Định chạy trốn hả, hử"
            }, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M //second rest
    );
    public static final BossData MAP_DAU_DINH = new BossData(
            "Mập Đầu Đinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{165, 166, 167, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{13000000}, //hp
            new int[]{63, 64, 65, 66, 67}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.ANTOMIC, 7, 10000},},//skill //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|HAHAHA",
                "|-1|Tao chỉ cần 10 giây để giết hết bọn mày",
                "|-1|Được rồi tao sẽ thổi bay hết",
                "|-1|Chết hết đi cho tao",
                "|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Tao sẽ giết hết bọn mày"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M //second rest
    );
    public static final BossData RAMBO = new BossData(
            "Rambo", //name
            ConstPlayer.XAYDA, //gender
            new short[]{162, 163, 164, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            12400, //dame
            new long[]{15000000}, //hp
            new int[]{74, 75, 76, 77}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 10000},
                {Skill.KAMEJOKO, 1, 1000},},//skill //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|HAHAHA",
                "|-1|Tao chỉ cần 10 giây để giết hết bọn mày",
                "|-1|Thấy ta đẹp trai không",
                "|-1|Mày sợ tao chưa",
                "|-1|Ta sẽ tàn sát khu này trong vòng 5 phút nữa",
                "|-1|Tao sẽ giết hết bọn mày"}, //text chat 2
            new String[]{"|-1|Ôi bạn ơi..."}, //text chat 3
            REST_10_M //second rest
    );

    //TAU PAY PAY
    public static final BossData TAUPAYPAY = new BossData(
            "Tàu Pảy Pảy", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{92, 93, 94, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{1100L}, //hp
            new int[]{47}, //map join
            new int[][]{
                {Skill.MASENKO, 3, 1000},
                {Skill.DRAGON, 7, 1000}},
            new String[]{"|-1|Ta cho người 10 giây suy nghĩ",
                "|-1|Mau giao ngọc rồng ra đây",
                "|-2|Đừng trách ta",
                "|-1|Xem ta đây"
            }, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-2|Tuổi trẻ chưa trải sự đời"}, //text chat 3
            REST_5_S
    );
//---------------------BOSS 21H-------------------------------------------------
    public static final BossData GOLDEN_FRIEZA = new BossData(
            "Fide Vàng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{502, 503, 504, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{1000000000}, //hp
            new int[]{6}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 3000},}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_M,
            new int[]{BossID.DEATH_BEAM_1, BossID.DEATH_BEAM_2, BossID.DEATH_BEAM_3, BossID.DEATH_BEAM_4, BossID.DEATH_BEAM_5}
    );

    public static final BossData DEATH_BEAM = new BossData(
            "$", //name
            ConstPlayer.XAYDA, //gender
            new short[]{609, 610, 611, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1_000_000_000, //dame
            new long[]{500}, //hp
            new int[]{6}, //map join
            new int[][]{},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );
//---------------------BOSS SƠN TINH THUỶ TINH----------------------------------
    public static final BossData RONG_NHI_1S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1722, 1723, 1724, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{150_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData RONG_NHI_2S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1719, 1720, 1721, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{120_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData RONG_NHI_3S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1716, 1717, 1718, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{100_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData RONG_NHI_4S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1713, 1714, 1715, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{80_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData RONG_NHI_5S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1710, 1711, 1712, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{70_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData RONG_NHI_6S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1707, 1708, 1709, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{60_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData RONG_NHI_7S = new BossData(
            "Rồng Nhí", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1704, 1705, 1706, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{50_000_000}, //hp
            new int[]{3, 4, 5, 6, 11, 12, 13, 10, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 3_600_000},},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_10_M //second rest
    );
    public static final BossData THUY_TINH = new BossData(
            "Thủy Tinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{311, 312, 313, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{25_000}, //hp
            new int[]{1, 2, 3, 4, 8, 9, 11, 12, 15, 16, 17, 18, 24, 25, 26}, //map join
            new int[][]{
                {Skill.DEMON, 1, 500},
                {Skill.ANTOMIC, 1, 2000}, {Skill.ANTOMIC, 2, 2000}, {Skill.ANTOMIC, 3, 2000}, {Skill.ANTOMIC, 4, 2000},
                {Skill.KAMEJOKO, 1, 2000}, {Skill.KAMEJOKO, 2, 2000}, {Skill.KAMEJOKO, 3, 2000}, {Skill.KAMEJOKO, 4, 2000},
                {Skill.MASENKO, 1, 2000}, {Skill.MASENKO, 2, 2000}, {Skill.MASENKO, 3, 2000}, {Skill.MASENKO, 4, 2000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Trả Mị Nương lại cho ta",
                "|-1|Ta cho nước dâng chìm cả lũ bây giờ"}, //text chat 2
            new String[]{}, //text chat 3
            REST_15_M,//second rest
            new int[]{BossID.SON_TINH}
    );

    public static final BossData SON_TINH = new BossData(
            "Sơn Tinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{314, 315, 316, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{25_000}, //hp
            new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.DEMON, 1, 500},
                {Skill.ANTOMIC, 1, 2000}, {Skill.ANTOMIC, 2, 2000}, {Skill.ANTOMIC, 3, 2000}, {Skill.ANTOMIC, 4, 2000},
                {Skill.KAMEJOKO, 1, 2000}, {Skill.KAMEJOKO, 2, 2000}, {Skill.KAMEJOKO, 3, 2000}, {Skill.KAMEJOKO, 4, 2000},
                {Skill.MASENKO, 1, 2000}, {Skill.MASENKO, 2, 2000}, {Skill.MASENKO, 3, 2000}, {Skill.MASENKO, 4, 2000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Còn lâu á, chậm chân ráng chịu đi cưng",
                "|-1|Ta thách, chiêu này quá quen rồi"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
    public static final BossData THUY_TINH_NEW = new BossData(
            "Thủy Tinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1728, 1729, 1730, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{25_000}, //hp
            new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{
                {Skill.DEMON, 1, 500},
                {Skill.ANTOMIC, 1, 2000}, {Skill.ANTOMIC, 2, 2000}, {Skill.ANTOMIC, 3, 2000}, {Skill.ANTOMIC, 4, 2000},
                {Skill.KAMEJOKO, 1, 2000}, {Skill.KAMEJOKO, 2, 2000}, {Skill.KAMEJOKO, 3, 2000}, {Skill.KAMEJOKO, 4, 2000},
                {Skill.MASENKO, 1, 2000}, {Skill.MASENKO, 2, 2000}, {Skill.MASENKO, 3, 2000}, {Skill.MASENKO, 4, 2000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Trả Mị Nương lại cho ta",
                "|-1|Ta cho nước dâng chìm cả lũ bây giờ"}, //text chat 2
            new String[]{"|-1|Các ngươi chờ đấy, ta sẽ quay lại sau"}, //text chat 3
            REST_15_M,//second rest
            new int[]{BossID.SON_TINH_NEW}
    );

    public static final BossData SON_TINH_NEW = new BossData(
            "Sơn Tinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1725, 1726, 1727, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{25_000}, //hp
            new int[]{1, 2, 3, 4, 8, 9, 11, 12, 15, 16, 17, 18, 24, 25, 26}, //map join
            new int[][]{
                {Skill.DEMON, 1, 500},
                {Skill.ANTOMIC, 1, 2000}, {Skill.ANTOMIC, 2, 2000}, {Skill.ANTOMIC, 3, 2000}, {Skill.ANTOMIC, 4, 2000},
                {Skill.KAMEJOKO, 1, 2000}, {Skill.KAMEJOKO, 2, 2000}, {Skill.KAMEJOKO, 3, 2000}, {Skill.KAMEJOKO, 4, 2000},
                {Skill.MASENKO, 1, 2000}, {Skill.MASENKO, 2, 2000}, {Skill.MASENKO, 3, 2000}, {Skill.MASENKO, 4, 2000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Còn lâu á, chậm chân ráng chịu đi cưng",
                "|-1|Ta thách, chiêu này quá quen rồi"}, //text chat 2
            new String[]{"|-1|Các ngươi chờ đấy, ta sẽ quay lại sau"}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );
//---------------------BOSS TIỂU ĐỘI BOJACK-------------------------------------
    public static final BossData BUJIN = new BossData(
            "Bujin", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{341, 342, 343, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            70000, //dame
            new long[]{150000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.DEMON, 7, 1000},
                {Skill.MASENKO, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán",
                "|-1|Các ngươi không nhúc nhích được sao?",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!",
                "|-1|Ta mà lại thua được sao?",
                "|-1|Hãy trả thù cho ta!"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER
    );

    public static final BossData KOGU = new BossData(
            "Kogu", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{329, 330, 331, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            80000, //dame
            new long[]{160000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.DRAGON, 7, 1000},
                {Skill.TROI, 4, 50000},
                {Skill.ANTOMIC, 4, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Trói"
            }, //text chat 2
            new String[]{"|-1|Cứu"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData ZANGYA = new BossData(
            "Zangya", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{332, 333, 334, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            70000, //dame
            new long[]{170000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.TROI, 5, 50000},
                {Skill.ANTOMIC, 3, 3000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Trói"
            }, //text chat 2
            new String[]{"|-1|Cứu"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData BIDO = new BossData(
            "Bido", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{335, 336, 337, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            80000, //dame
            new long[]{200000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.DRAGON, 7, 1000},
                {Skill.KAMEJOKO, 4, 10000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!"
            }, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData BOJACK = new BossData(
            "Bojack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{323, 324, 325, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            90000, //dame
            new long[]{220000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TROI, 7, 120000},
                {Skill.MASENKO, 7, 1000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Hahaha"
            }, //text chat 2
            new String[]{"|-1|Hahaha"
            }, //text chat 3
            REST_10_M,
            new int[]{BossID.BUJIN, BossID.KOGU, BossID.BIDO, BossID.ZANGYA,} //type appear
    );

    public static final BossData SUPER_BOJACK = new BossData(
            "Siêu Bojack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{326, 327, 328, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{220000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.THOI_MIEN, 7, 100000},
                {Skill.KHIEN_NANG_LUONG, 7, 100000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Hahaha"
            }, //text chat 2
            new String[]{"|-1|Hahaha"
            }, //text chat 3
            REST_10_M,
            TypeAppear.ANOTHER_LEVEL //type appear
    );

    public static final BossData SUPER_BOJACK_2 = new BossData(
            "Siêu Bojack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{326, 327, 328, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            300000, //dame
            new long[]{220000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TROI, 3, 60000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Hahaha"
            }, //text chat 2
            new String[]{"|-1|Hahaha"
            }, //text chat 3
            REST_10_M
    );
//---------------------BOSS ÔNG GIÀ NOEL----------------------------------------
    public static final BossData ONG_GIA_NOEL = new BossData(
            "Ông già Noel", //name
            ConstPlayer.XAYDA, //gender
            new short[]{657, 658, 659, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1, //dame
            new long[]{500}, //hp
            new int[]{0, 5, 7, 13, 14, 20, 20, 102, 42, 43, 44, 84}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Hô hô hô",
                "|-1|Giáng sinh vui vẻ!"}, //text chat 2
            new String[]{"|-1|Giáng sinh vui vẻ!"}, //text chat 3
            REST_1_S //type appear
    );
//---------------------BOSS ĐÔNG NAM KARIN--------------------------------------
    public static final BossData TAU_PAY_PAY_DONG_NAM_KARIN = new BossData(
            "Tàu Pảy Pảy", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{338, 339, 340, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            300, //dame
            new long[]{12000}, //hp
            new int[]{111}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 120000},},//skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_30_M //second rest
    );
//---------------------BOSS ĐẠI HỘI VÕ THUẬT 23---------------------------------
    public static final BossData SOI_HEC_QUYN = new BossData(
            "Sói hẹc quyn", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{394, 395, 396, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new long[]{100000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData O_DO = new BossData(
            "Ở dơ", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{400, 401, 402, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            5000, //dame
            new long[]{250000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData XINBATO = new BossData(
            "Xinbatô", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{359, 360, 361, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData CHA_PA = new BossData(
            "Cha pa", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{362, 363, 364, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            15000, //dame
            new long[]{1000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );
    public static final BossData PON_PUT = new BossData(
            "Pon put", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{365, 366, 367, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            30000, //dame
            new long[]{2500000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData CHAN_XU = new BossData(
            "Chan xư", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{371, 372, 373, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            30000, //dame
            new long[]{5000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},
                {Skill.MASENKO, 1, 400},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData TAU_PAY_PAY = new BossData(
            "Tàu Pảy Pảy", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{92, 93, 94, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            40000, //dame
            new long[]{20000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData YAMCHA = new BossData(
            "Yamcha", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{374, 375, 376, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new long[]{50000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData JACKY_CHUN = new BossData(
            "Jacky Chun", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{356, 357, 358, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            60000, //dame
            new long[]{100000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},
                {Skill.TAI_TAO_NANG_LUONG, 1, 600000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData THIEN_XIN_HANG = new BossData(
            "Thiên xin hăng", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{368, 369, 370, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            80000, //dame
            new long[]{250000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},
                {Skill.THAI_DUONG_HA_SAN, 1, 15000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );
    public static final BossData THIEN_XIN_HANG_CLONE = new BossData(
            "Thiên xin hăng", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{368, 369, 370, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            60000, //dame
            new long[]{75000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},
                {Skill.THAI_DUONG_HA_SAN, 1, 30000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData LIU_LIU = new BossData(
            "Liu Liu", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{397, 398, 399, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            90000, //dame
            new long[]{500000000}, //hp
            new int[]{129}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 800},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData LOCOPO = new BossData(
            "Lôcôpô",
            ConstPlayer.TRAI_DAT,
            new short[]{9, 67, 13, -1, -1, -1},
            100000,
            new long[]{1000000000},
            new int[]{129},
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.MASENKO, 7, 2000}},
            new String[]{},
            new String[]{},
            new String[]{},
            REST_5_S
    );
//---------------------BOSS YARDART---------------------------------------------
    public static final BossData TAP_SU_0 = new BossData(
            "Tập sự-0", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_1 = new BossData(
            "Tập sự-1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_2 = new BossData(
            "Tập sự-2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_3 = new BossData(
            "Tập sự-3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_4 = new BossData(
            "Tập sự-4", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_5 = new BossData(
            "Tân binh-5", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{450000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S,
            new int[]{BossID.TAP_SU_0, BossID.TAP_SU_1, BossID.TAP_SU_2, BossID.TAP_SU_3, BossID.TAP_SU_4} //type appear
    );

    public static final BossData TAN_BINH_0 = new BossData(
            "Tân binh-0", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_1 = new BossData(
            "Tân binh-1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_2 = new BossData(
            "Tân binh-2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_3 = new BossData(
            "Tân binh-3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_4 = new BossData(
            "Tân binh-4", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_5 = new BossData(
            "Chiến binh-5", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S,
            new int[]{BossID.TAN_BINH_0, BossID.TAN_BINH_1, BossID.TAN_BINH_2, BossID.TAN_BINH_3, BossID.TAN_BINH_4} //type appear
    );

    public static final BossData CHIEN_BINH_0 = new BossData(
            "Chiến binh-0", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_1 = new BossData(
            "Chiến binh-1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_2 = new BossData(
            "Chiến binh-2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_3 = new BossData(
            "Chiến binh-3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_4 = new BossData(
            "Chiến binh-4", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData DOI_TRUONG_5 = new BossData(
            "Đội trưởng-5", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{529, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new long[]{1000000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 2000},
                {Skill.MASENKO, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S,
            new int[]{BossID.CHIEN_BINH_0, BossID.CHIEN_BINH_1, BossID.CHIEN_BINH_2, BossID.CHIEN_BINH_3, BossID.CHIEN_BINH_4} //type appear
    );

//---------------------BOSS MAJINBUU 12H----------------------------------------
    public static final BossData MABU_12H = new BossData(
            "Mabư",
            ConstPlayer.XAYDA,
            new short[]{297, 298, 299, -1, -1, -1},
            100000,
            new long[]{300000000},
            new int[]{120},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 120000},
                {Skill.GALICK, 7, 1000}},
            new String[]{"|-1|Bư! Bư! Bư!",
                "|-1|Bư! Bư!",}, //text chat 1
            new String[]{"|-1|Oe Oe"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M,
            new int[]{BossID.DRABURA_3}
    );

    public static final BossData GOKU = new BossData(
            "Gôku", //name
            ConstPlayer.XAYDA, //gender
            new short[]{101, 65, 66, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{200000000}, //hp
            new int[]{119}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 20000},
                {Skill.THAI_DUONG_HA_SAN, 1, 30000}},//skill
            new String[]{"|-1|Tỉnh lại đi Cađíc!",
                "|-1|Đừng có tấn công mọi người",
                "|-1|Cậu đang bị thế lực bóng tối khống chế đó",
                "|-1|Tỉnh lại đi!"}, //text chat 1
            new String[]{"|-1|Cađíc tỉnh lại đi!",
                "|-1|Trời ơi...Hắn định giết mọi người ở đây chắc?",
                "|-1|Cađíc! dừng tay lại! Cậu điên mất rồi!",
                "|-1|Cađíc tỉnh lại đi!",}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );

    public static final BossData CADIC = new BossData(
            "Ca Đích", //name
            ConstPlayer.XAYDA, //gender
            new short[]{103, 16, 17, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new long[]{200000000}, //hp
            new int[]{119}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 20000},},//skill
            new String[]{"|-1|Ta là ai?",
                "|-1|Ta là kẻ mạnh nhất vũ trụ",
                "|-1|Ta sẽ huỷ diệt tất cả",
                "|-1|Hahaha"}, //text chat 1
            new String[]{"|-1|Chúng ta sẽ 1 mất 1 còn!",
                "|-1|Hãy xem đây",
                "|-1|Ngươi có một tội rất lớn là...",
                "|-1|Dám qua mặt ta",
                "|-1|Kakalốt! Ta chờ đợi giây phút này đã từ lâu!"}, //text chat 2
            new String[]{}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );

    public static final BossData DRABURA = new BossData(
            "Drabura",
            ConstPlayer.XAYDA,
            new short[]{418, 419, 420, -1, -1, -1},
            50000,
            new long[]{50000000},
            new int[]{114},
            new int[][]{
                {Skill.GALICK, 7, 1000}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi"}, //text chat 3
            REST_1_M
    );
    public static final BossData DRABURA_2 = new BossData(
            "Drabura",
            ConstPlayer.XAYDA,
            new short[]{418, 419, 420, -1, -1, -1},
            100000,
            new long[]{40000000},
            new int[]{119},
            new int[][]{
                {Skill.GALICK, 7, 1000}},
            new String[]{"|-1|Ta đã trở lại, lợi hại gấp hai, hahaha"}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Hêhê..ta chẳng cần tốn sức đánh với các ngươi nữa",
                "|-1|Mà ta sẽ để cho các ngươi tự thanh toán lẫn nhau, xin chào",
                "|-2|Hắn nói sẽ để chúng ta tự tiêu diệt lẫn nhau",
                "|-2|Nghĩa là sao nhỉ?"}, //text chat 3
            REST_5_M,
            new int[]{BossID.GOKU, BossID.CADIC}
    );

    public static final BossData DRABURA_3 = new BossData(
            "Drabura",
            ConstPlayer.XAYDA,
            new short[]{418, 419, 420, -1, -1, -1},
            100000,
            new long[]{50000000},
            new int[]{114},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 20000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi"}, //text chat 3
            TypeAppear.CALL_BY_ANOTHER
    );

    public static final BossData BUI_BUI = new BossData(
            "Bui Bui",
            ConstPlayer.XAYDA,
            new short[]{451, 452, 453, -1, -1, -1},
            100000,
            new long[]{50000000},
            new int[]{115},
            new int[][]{
                {Skill.GALICK, 7, 5000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Hãy xem đây nhóc"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M
    );

    public static final BossData BUI_BUI_2 = new BossData(
            "Bui Bui",
            ConstPlayer.XAYDA,
            new short[]{451, 452, 453, -1, -1, -1},
            100000,
            new long[]{50000000},
            new int[]{117},
            new int[][]{
                {Skill.GALICK, 7, 5000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Trọng lực bây giờ đã tăng gấp 10 lần",
                "|-1|Đó là điều kiện không gian lý tưởng của ta",
                "|-1|Nhưng lại rất bất lợi cho bọn mi"}, //text chat 2
            new String[]{"|-1|Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi"}, //text chat 3
            REST_1_M
    );

    public static final BossData YACON = new BossData(
            "Ya côn",
            ConstPlayer.XAYDA,
            new short[]{415, 416, 417, -1, -1, -1},
            70000,
            new long[]{60000000},
            new int[]{118},
            new int[][]{
                {Skill.GALICK, 1, 800}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M
    );

//---------------------BOSS MAJINBUU 14H----------------------------------------
    public static final BossData MABU = new BossData(
            "Mabư mập",
            ConstPlayer.XAYDA,
            new short[]{297, 298, 299, -1, -1, -1},
            40000,
            new long[]{50000000},
            new int[]{127},
            new int[][]{
                {Skill.KAMEJOKO, Util.nextInt(5, 7), 5000},
                {Skill.DRAGON, Util.nextInt(1, 7), 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo",
                "|-1|Úm ba la xì bùa"}, //text chat 2
            new String[]{"|-1|Biến hình"}, //text chat 3
            REST_10_M
    );

    public static final BossData SUPER_BU = new BossData(
            "Super Bư",
            ConstPlayer.XAYDA,
            new short[]{421, 422, 423, -1, -1, -1},
            50000,
            new long[]{60000000},
            new int[]{127, 128},
            new int[][]{
                {Skill.KAMEJOKO, Util.nextInt(5, 7), 5000},
                {Skill.DRAGON, Util.nextInt(1, 7), 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{"|-1|Biến hình"}, //text chat 3
            REST_10_M
    );

    public static final BossData BU_TENK = new BossData(
            "Bư Tênk",
            ConstPlayer.XAYDA,
            new short[]{424, 425, 426, -1, -1, -1},
            50000,
            new long[]{120000000},
            new int[]{127},
            new int[][]{
                {Skill.KAMEJOKO, Util.nextInt(5, 7), 5000},
                {Skill.DRAGON, Util.nextInt(1, 7), 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo",
                "|-1|Ui da đau bụng quá"}, //text chat 2
            new String[]{"|-1|Biến hình"}, //text chat 3
            REST_10_M
    );

    public static final BossData BU_HAN = new BossData(
            "Bư Han",
            ConstPlayer.XAYDA,
            new short[]{427, 428, 429, -1, -1, -1},
            50000,
            new long[]{150000000},
            new int[]{127},
            new int[][]{
                {Skill.KAMEJOKO, Util.nextInt(5, 7), 5000},
                {Skill.DRAGON, Util.nextInt(1, 7), 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{"|-1|Biến hình"}, //text chat 3
            REST_10_M
    );

    public static final BossData KID_BU = new BossData(
            "Kid Bư",
            ConstPlayer.XAYDA,
            new short[]{439, 440, 441, -1, -1, -1},
            50000,
            new long[]{40000000},
            new int[]{127},
            new int[][]{
                {Skill.KAMEJOKO, Util.nextInt(5, 7), 5000},
                {Skill.DRAGON, Util.nextInt(1, 7), 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{"|-1|Biến hình"}, //text chat 3
            REST_10_M
    );

    public static final BossData SUPER_BU_BUNG = new BossData(
            "Super Bư",
            ConstPlayer.XAYDA,
            new short[]{421, 422, 423, -1, -1, -1},
            40000,
            new long[]{52000000},
            new int[]{127, 128},
            new int[][]{
                {Skill.KAMEJOKO, Util.nextInt(5, 7), 5000},
                {Skill.DRAGON, Util.nextInt(1, 7), 1000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_S
    );

//---------------------BOSS TRAINNING OFFINE------------------------------------
    public static final BossData WHIS = new BossData(
            "Whis", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{838, 839, 840, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000L, //dame
            new long[]{550000L}, //hp
            new int[]{154}, //map join
            new int[][]{
                {Skill.DRAGON, 7, 500}},//skill
            new String[]{"|-1|Ta sẽ dạy ngươi vài chiêu"}, //text chat 1
            new String[]{"|-1|Xem đây",
                "|-1|Haizzzzz",
                "|-1|Hahaha", "|-1|AAAAAAAAAA"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M
    );
    public static final BossData THAN_MEO_KARIN = new BossData(
            "Karin", //name
            ConstPlayer.XAYDA, //gender
            new short[]{89, 90, 91, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new long[]{500}, //hp
            new int[]{46}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{"|-1|Ta sẽ dạy ngươi vài chiêu",
                "|-1|Ta sẽ đánh hết sức, ngươi cẩn thận nhé"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|OK ta chịu thua"}, //text chat 3
            REST_1_S
    );

    public static final BossData YAJIRO = new BossData(
            "Yajirô", //name
            ConstPlayer.XAYDA, //gender
            new short[]{77, 78, 79, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            400, //dame
            new long[]{1100}, //hp
            new int[]{46}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{"|-1|Ngon nhào vô",
                "|-1|Cho mi biết sự lợi hại của ta"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|Ngươi thật lợi hại"}, //text chat 3
            REST_1_S
    );

    public static final BossData MR_POPO = new BossData(
            "Mr.PôPô", //name
            ConstPlayer.XAYDA, //gender
            new short[]{83, 84, 85, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            600, //dame
            new long[]{5100}, //hp
            new int[]{46}, //map join
            new int[][]{{Skill.GALICK, 1, 30000},
            {Skill.KAMEJOKO, 1, 30000},
            {Skill.THAI_DUONG_HA_SAN, 3, 30000},},
            new String[]{"|-1|Đánh trúng ta 1 cái coi như ngươi thắng",
                "|-1|Đánh trúng ta 3 cái coi như ngươi thắng"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây",
                "|-1|Phù.."}, //text chat 2
            new String[]{"|-1|Thua thì thua"}, //text chat 3
            REST_1_S
    );

    public static final BossData THUONG_DE = new BossData(
            "Thượng đế", //name
            ConstPlayer.XAYDA, //gender
            new short[]{86, 87, 88, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1500, //dame
            new long[]{10000}, //hp
            new int[]{49}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},},
            new String[]{"|-1|Ta sẽ dạy võ cho con trong phòng tập thời gian này",
                "|-1|Con hãy đánh hết sức nhé, ta sẽ không nương tay đâu"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|Ta rất tự hào về con"}, //text chat 3
            REST_1_S
    );

    public static final BossData KHI_BUBBLES = new BossData(
            "Khỉ Bubbles", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{95, 96, 97, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            4000, //dame
            new long[]{40000}, //hp
            new int[]{48}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000}}, //skill
            new String[]{"|-1|Ù ù khẹt khẹt",
                "|-1|Ù ù khẹt khẹt"}, //text chat 1
            new String[]{"|-1|Ù ù khẹt khẹt",
                "|-1|khẹt khẹt",
                "|-1|ù ù khẹc khẹc",
                "|-1|khẹc khẹc",
                "|-1|éc éc",}, //text chat 2
            new String[]{"|-1|Éc Éc Éc Éc!"}, //text chat 3
            REST_1_S //second rest
    );

    public static final BossData THAN_VU_TRU = new BossData(
            "Thần Vũ Trụ", //name
            ConstPlayer.XAYDA, //gender
            new short[]{98, 99, 100, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            7500, //dame
            new long[]{80000}, //hp
            new int[]{48}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.THAI_DUONG_HA_SAN, 7, 30000},},
            new String[]{"|-1|Ta sẽ dạy ngươi chiêu kaio-ken",
                "|-1|Ngươi cũng to gan lắm"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|Tại hôm nay ta...ta hơi bị đau bụng"}, //text chat 3
            REST_1_S
    );

    public static final BossData TO_SU_KAIO = new BossData(
            "Tổ sư Kaio", //name
            ConstPlayer.XAYDA, //gender
            new short[]{448, 449, 450, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            15000, //dame
            new long[]{160000}, //hp
            new int[]{50}, //map join
            new int[][]{
                {Skill.GALICK, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

//---------------------------BOSS VÕ ĐÀI SINH TỬ--------------------------------    
    public static final BossData DRACULA = new BossData(
            "Đracula", //name
            ConstPlayer.XAYDA, //gender
            new short[]{353, 354, 355, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1L, //dame
            new long[]{1L}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.GALICK, 1, 3000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData NGUOI_VO_HINH = new BossData(
            "Người vô hình", //name
            ConstPlayer.XAYDA, //gender
            new short[]{377, 378, 379, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1L, //dame
            new long[]{1L}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.GALICK, 1, 6000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData BONG_BANG = new BossData(
            "Bông băng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{350, 351, 352, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1L, //dame
            new long[]{1L}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.GALICK, 1, 3000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData VUA_QUY_SA_TANG = new BossData(
            "Vua Quỷ Sa tăng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{344, 345, 346, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1L, //dame
            new long[]{1L}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.GALICK, 1, 5000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData THO_DAU_BAC = new BossData(
            "Thỏ Đầu Bạc", //name
            ConstPlayer.XAYDA, //gender
            new short[]{347, 348, 349, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1L, //dame
            new long[]{1L}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.GALICK, 1, 3000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );
}
