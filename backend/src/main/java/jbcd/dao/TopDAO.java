package jbcd.dao;

public class TopDAO {
    
    //TOP MÃY Äáº¤M
    public static final String TOP_MAY_DAM_TRAI_DAT = "SELECT name, gender, items_body, data_may_dam, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_may_dam, '$[0]')) AS UNSIGNED) AS traidat, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 AND player.gender = 0 "
            + "ORDER BY traidat DESC "
            + "LIMIT 100;";
    public static final String TOP_MAY_DAM_NAMEC = "SELECT name, gender, items_body, data_may_dam, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_may_dam, '$[1]')) AS UNSIGNED) AS namec, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 AND player.gender = 1 "
            + "ORDER BY namec DESC "
            + "LIMIT 100;";
    public static final String TOP_MAY_DAM_XAYDA = "SELECT name, gender, items_body, data_may_dam, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_may_dam, '$[2]')) AS UNSIGNED) AS xayda, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 AND player.gender = 2 "
            + "ORDER BY xayda DESC "
            + "LIMIT 100;";
    //TOP POINT
    public static final String TOP_SM = "SELECT name, gender, items_body, "
            + "CAST( JSON_EXTRACT(data_point, '$[1]') AS UNSIGNED) AS sm, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY CAST( JSON_EXTRACT(data_point, '$[1]') AS UNSIGNED) DESC LIMIT 100;";
    public static final String TOP_HP = "SELECT name, gender, items_body, "
            + "CAST( JSON_EXTRACT(data_point, '$[14]') AS UNSIGNED) AS hp, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY CAST( JSON_EXTRACT(data_point, '$[14]') AS UNSIGNED) DESC LIMIT 100;";
    public static final String TOP_KI = "SELECT name, gender, items_body, "
            + "CAST( JSON_EXTRACT(data_point, '$[15]') AS UNSIGNED) AS ki, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY CAST( JSON_EXTRACT(data_point, '$[15]') AS UNSIGNED) DESC LIMIT 100;";
    public static final String TOP_SD = "SELECT name, gender, items_body, "
            + "CAST( JSON_EXTRACT(data_point, '$[13]') AS UNSIGNED) AS sd, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY CAST( JSON_EXTRACT(data_point, '$[13]') AS UNSIGNED) DESC LIMIT 100;";
    public static final String TOP_SM_PET = "SELECT name, gender, items_body, \n" +
            "       CAST(\n" +
            "           JSON_UNQUOTE(\n" +
            "               JSON_EXTRACT(\n" +
            "                   JSON_UNQUOTE(JSON_EXTRACT(pet, '$[1]')), '$[1]'\n" +
            "               )\n" +
            "           ) AS UNSIGNED\n" +
            "       ) AS smpet, \n" +
            "       LastTimeLoginGame \n" +
            "FROM player \n" +
            "INNER JOIN account ON account.id = player.account_id \n" +
            "WHERE account.ban = 0 \n" +
            "ORDER BY smpet DESC \n" +
            "LIMIT 100;";
    public static final String TOP_HP_PET = "SELECT name, gender, items_body, \n" +
            "       CAST(\n" +
            "           JSON_UNQUOTE(\n" +
            "               JSON_EXTRACT(\n" +
            "                   JSON_UNQUOTE(JSON_EXTRACT(pet, '$[1]')), '$[10]'\n" +
            "               )\n" +
            "           ) AS UNSIGNED\n" +
            "       ) AS hppet, \n" +
            "       LastTimeLoginGame \n" +
            "FROM player \n" +
            "INNER JOIN account ON account.id = player.account_id \n" +
            "WHERE account.ban = 0 \n" +
            "ORDER BY hppet DESC \n" +
            "LIMIT 100;";
    public static final String TOP_KI_PET = "SELECT name, gender, items_body, \n" +
            "       CAST(\n" +
            "           JSON_UNQUOTE(\n" +
            "               JSON_EXTRACT(\n" +
            "                   JSON_UNQUOTE(JSON_EXTRACT(pet, '$[1]')), '$[11]'\n" +
            "               )\n" +
            "           ) AS UNSIGNED\n" +
            "       ) AS kipet, \n" +
            "       LastTimeLoginGame \n" +
            "FROM player \n" +
            "INNER JOIN account ON account.id = player.account_id \n" +
            "WHERE account.ban = 0 \n" +
            "ORDER BY kipet DESC \n" +
            "LIMIT 100;";
    public static final String TOP_SD_PET = "SELECT name, gender, items_body, \n" +
            "       CAST(\n" +
            "           JSON_UNQUOTE(\n" +
            "               JSON_EXTRACT(\n" +
            "                   JSON_UNQUOTE(JSON_EXTRACT(pet, '$[1]')), '$[12]'\n" +
            "               )\n" +
            "           ) AS UNSIGNED\n" +
            "       ) AS damepet, \n" +
            "       LastTimeLoginGame \n" +
            "FROM player \n" +
            "INNER JOIN account ON account.id = player.account_id \n" +
            "WHERE account.ban = 0 \n" +
            "ORDER BY damepet DESC \n" +
            "LIMIT 100;";
    //TOP Äáº I GIA
    public static final String TOP_VND = "SELECT player.name, player.gender, player.items_body, account.vnd, player.LastTimeLoginGame " +
            "FROM player " +
            "INNER JOIN account ON account.id = player.account_id " +
            "WHERE account.ban = 0 " +
            "ORDER BY account.vnd DESC " +
            "LIMIT 100;";
    public static final String TOP_COIN = "SELECT player.name, player.gender, player.items_body, account.coin, player.LastTimeLoginGame " +
            "FROM player " +
            "INNER JOIN account ON account.id = player.account_id " +
            "WHERE account.ban = 0 " +
            "ORDER BY account.coin DESC " +
            "LIMIT 100;";
    
    public static final String TOP_THOI_VANG = 
    "SELECT name, gender, items_body, account.id AS accountId, player.name, " +
    "  CAST( " +
    "    REPLACE( " +
    "      SUBSTRING_INDEX( " +
    "        SUBSTRING_INDEX( " +
    "          CONCAT( " +
    "            '[457,', " +
    "            SUBSTRING_INDEX( " +
    "              SUBSTRING_INDEX(player.items_bag, '[457,', -1), " +
    "              ']', " +
    "              1 " +
    "            ) " +
    "          ), " +
    "          ',', " +
    "          2 " +
    "        ), " +
    "        ']', " +
    "        1 " +
    "      ), " +
    "      '[457,', '' " +
    "    ) AS UNSIGNED " +
    "  ) AS so_luong_457, " +
    "  CAST( " +
    "    REPLACE( " +
    "      SUBSTRING_INDEX( " +
    "        SUBSTRING_INDEX( " +
    "          CONCAT( " +
    "            '[457,', " +
    "            SUBSTRING_INDEX( " +
    "              SUBSTRING_INDEX(player.items_box, '[457,', -1), " +
    "              ']', " +
    "              1 " +
    "            ) " +
    "          ), " +
    "          ',', " +
    "          2 " +
    "        ), " +
    "        ']', " +
    "        1 " +
    "      ), " +
    "      '[457,', '' " +
    "    ) AS UNSIGNED " +
    "  ) AS so_luong_457_ruong, " +
    "  CAST( " +
    "    REPLACE( " +
    "      SUBSTRING_INDEX( " +
    "        SUBSTRING_INDEX( " +
    "          CONCAT( " +
    "            '[457,', " +
    "            SUBSTRING_INDEX( " +
    "              SUBSTRING_INDEX(player.items_bag, '[457,', -1), " +
    "              ']', " +
    "              1 " +
    "            ) " +
    "          ), " +
    "          ',', " +
    "          2 " +
    "        ), " +
    "        ']', " +
    "        1 " +
    "      ), " +
    "      '[457,', '' " +
    "    ) AS UNSIGNED " +
    "  ) + CAST( " +
    "    REPLACE( " +
    "      SUBSTRING_INDEX( " +
    "        SUBSTRING_INDEX( " +
    "          CONCAT( " +
    "            '[457,', " +
    "            SUBSTRING_INDEX( " +
    "              SUBSTRING_INDEX(player.items_box, '[457,', -1), " +
    "              ']', " +
    "              1 " +
    "            ) " +
    "          ), " +
    "          ',', " +
    "          2 " +
    "        ), " +
    "        ']', " +
    "        1 " +
    "      ), " +
    "      '[457,', '' " +
    "    ) AS UNSIGNED " +
    "  ) AS thoi_vang " +
    "FROM player " +
    "INNER JOIN account ON account.id = player.account_id " +
    "WHERE (player.items_box LIKE '%\"[457,%' OR player.items_bag LIKE '%\"[457,%') " +
    "ORDER BY thoi_vang DESC " +
    "LIMIT 20;";
    public static final String TOP_HONG_NGOC = "SELECT name, gender, items_body, "
            + "CAST( JSON_EXTRACT(data_inventory, '$[1]') AS UNSIGNED) AS hongngoc, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY CAST( JSON_EXTRACT(data_inventory, '$[1]') AS UNSIGNED) DESC LIMIT 100;";
    public static final String TOP_DA_NAP = "SELECT player.name, player.gender, player.items_body, account.danap, player.LastTimeLoginGame " +
            "FROM player " +
            "INNER JOIN account ON account.id = player.account_id " +
            "WHERE account.ban = 0 " +
            "ORDER BY account.danap DESC " +
            "LIMIT 100;";
    //    
    public static final String TOP_NAMEK_WAR = "SELECT name, gender, items_body, diem_chien_truong_namek, LastTimeLoginGame FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY diem_chien_truong_namek DESC LIMIT 100;";
    //EVENT NEW YEAR
    public static final String TOP_MO_LIXI = "SELECT name, gender, items_body, data_event_new_year, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_new_year, '$[0]')) AS UNSIGNED) AS mo_li_xi, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY mo_li_xi DESC "
            + "LIMIT 100;";
    public static final String TOP_TANG_LIXI = "SELECT name, gender, items_body, data_event_new_year, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_new_year, '$[1]')) AS UNSIGNED) AS tang_li_xi_nam_moi, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY tang_li_xi_nam_moi DESC "
            + "LIMIT 100;";
    public static final String TOP_BAN_PHAO_HOA = "SELECT name, gender, items_body, data_event_new_year, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_new_year, '$[2]')) AS UNSIGNED) AS top_phao_hoa, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY top_phao_hoa DESC "
            + "LIMIT 100;";
    public static final String TOP_BAN_PHAO_HOA_VIP = "SELECT name, gender, items_body, data_event_new_year, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_new_year, '$[3]')) AS UNSIGNED) AS top_phao_hoa_vip, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY top_phao_hoa_vip DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_TRANG_TRI_CAY_NOEL = "SELECT name, gender, items_body, data_event_christ_mas, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_christ_mas, '$[0]')) AS UNSIGNED) AS top_trang_tri_cay_noel, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY top_trang_tri_cay_noel DESC "
            + "LIMIT 100;";
    public static final String CHE_TAO_NGUOI_TUYET = "SELECT name, gender, items_body, data_event_christ_mas, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_christ_mas, '$[1]')) AS UNSIGNED) AS che_tao_nguoi_tuyet, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY che_tao_nguoi_tuyet DESC "
            + "LIMIT 100;";
    public static final String CHE_TAO_NGUOI_TUYET_BANG_GIA = "SELECT name, gender, items_body, data_event_christ_mas, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_christ_mas, '$[2]')) AS UNSIGNED) AS che_tao_nguoi_tuyet_bang_gia, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY che_tao_nguoi_tuyet_bang_gia DESC "
            + "LIMIT 100;";
    public static final String TOP_DOT_DIEM = "SELECT name, gender, items_body, data_event_christ_mas, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_christ_mas, '$[3]')) AS UNSIGNED) AS dot_diem, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY dot_diem DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_DOT_PHAO_VU_LAN = "SELECT name, gender, items_body, data_event_vulan, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_vulan, '$[0]')) AS UNSIGNED) AS dot_phao, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY dot_phao DESC "
            + "LIMIT 100;";
    public static final String TOP_HOA_DANG = "SELECT name, gender, items_body, data_event_vulan, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_vulan, '$[1]')) AS UNSIGNED) AS hoa_dang, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY hoa_dang DESC "
            + "LIMIT 100;";
    public static final String TOP_HOA_DANG_LOI_CHUC = "SELECT name, gender, items_body, data_event_vulan, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_vulan, '$[2]')) AS UNSIGNED) AS hoa_dang_loi_chuc, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY hoa_dang_loi_chuc DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_MO_HOP_QUA_MA_QUY = "SELECT name, gender, items_body, data_event_halloween, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_halloween, '$[0]')) AS UNSIGNED) AS ma_quy, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY ma_quy DESC "
            + "LIMIT 100;";
    public static final String TOP_THIEP_HALLOWEEN = "SELECT name, gender, items_body, data_event_halloween, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_halloween, '$[1]')) AS UNSIGNED) AS thiep_halloween, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY thiep_halloween DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_MO_THIEP_83 = "SELECT name, gender, items_body, data_event_8_3, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_8_3, '$[0]')) AS UNSIGNED) AS thiep_83, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY thiep_83 DESC "
            + "LIMIT 100;";
    public static final String TOP_TANG_BONG_HOA_HONG = "SELECT name, gender, items_body, data_event_8_3, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_8_3, '$[1]')) AS UNSIGNED) AS tanghoahong, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY tanghoahong DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_LAM_BANH_TRUNG_THU = "SELECT name, gender, items_body, data_event_trung_thu, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_trung_thu, '$[0]')) AS UNSIGNED) AS lambanhtrungthu, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY lambanhtrungthu DESC "
            + "LIMIT 100;";
    public static final String TOP_MO_BANH_TRUNG_THU_DAC_BIET = "SELECT name, gender, items_body, data_event_trung_thu, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_trung_thu, '$[1]')) AS UNSIGNED) AS mobanhdacbiet, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY mobanhdacbiet DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_MO_TRUNG_VANG = "SELECT name, gender, items_body, data_event_hung_vuong, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_hung_vuong, '$[0]')) AS UNSIGNED) AS motrungvang, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY motrungvang DESC "
            + "LIMIT 100;";
    public static final String TOP_MO_HOP_QUA_GIO_TO = "SELECT name, gender, items_body, data_event_hung_vuong, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_hung_vuong, '$[3]')) AS UNSIGNED) AS mohopqua, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY mohopqua DESC "
            + "LIMIT 100;";
    public static final String TOP_DANG_BANH = "SELECT name, gender, items_body, data_event_hung_vuong, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_hung_vuong, '$[4]')) AS UNSIGNED) AS dangbanh, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY dangbanh DESC "
            + "LIMIT 100;";
    public static final String TOP_DOI_DUA_HAU = "SELECT name, gender, items_body, data_event_hung_vuong, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_hung_vuong, '$[5]')) AS UNSIGNED) AS doiduahau, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY doiduahau DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_MO_HOP_BLACK_FRIDAY = "SELECT name, gender, items_body, data_event_black_friday, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_black_friday, '$[0]')) AS UNSIGNED) AS black_friday, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY black_friday DESC "
            + "LIMIT 100;";
    public static final String TOP_MUA_SAM_BLACK_FRIDAY = "SELECT name, gender, items_body, data_event_black_friday, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(data_event_black_friday, '$[1]')) AS UNSIGNED) AS mua_sam, LastTimeLoginGame "
            + "FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "ORDER BY mua_sam DESC "
            + "LIMIT 100;";
    //
    public static final String TOP_NV = 
    "SELECT name, gender, items_body, " +
    "  CAST(JSON_EXTRACT(data_task, '$[0]') AS UNSIGNED) AS nv, " +
    "  CAST(JSON_EXTRACT(data_task, '$[1]') AS UNSIGNED) AS subnv, " +
    "  CAST(JSON_EXTRACT(data_task, '$[2]') AS UNSIGNED) AS progress, " +
    "  LastTimeLoginGame " +
    "FROM player " +
    "INNER JOIN account ON account.id = player.account_id " +
    "WHERE account.ban = 0 " +
    "ORDER BY nv DESC, subnv DESC, progress DESC, LastTimeLoginGame ASC " +
    "LIMIT 100;";
    //
    public static final String TOP_WHIS = "SELECT name, player.id, gender, items_body, "
            + "CAST( JSON_EXTRACT(data_luyentap, '$[5]') AS UNSIGNED) AS top, "
            + "CAST( JSON_EXTRACT(data_luyentap, '$[6]') AS UNSIGNED) AS time, LastTimeLoginGame "
            + "FROM player INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.ban = 0 "
            + "AND CAST( JSON_EXTRACT(data_luyentap, '$[5]') AS UNSIGNED) > 0 "
            + "ORDER BY CAST( JSON_EXTRACT(data_luyentap, '$[5]') AS UNSIGNED) DESC, "
            + "CAST( JSON_EXTRACT(data_luyentap, '$[6]') AS UNSIGNED) ASC LIMIT 10;";
    //
    public static final String TOP_SK = "SELECT name, gender, items_body, CAST( split_str( data_inventory,',',5)  AS UNSIGNED) AS event FROM player INNER JOIN account ON account.id = player.account_id WHERE account.is_admin = 0 AND account.ban = 0 ORDER BY CAST( split_str( data_inventory,',',5)  AS UNSIGNED) DESC LIMIT 10;";
    public static final String TOP_PVP = "SELECT name, gender, items_body, CAST( pointPvp AS UNSIGNED) AS pointPvp FROM player INNER JOIN account ON account.id = player.account_id WHERE account.ban = 0 ORDER BY CAST( pointPvp AS UNSIGNED) DESC LIMIT 100;";
    public static final String TOP_NHS = "SELECT name, gender, items_body, NguHanhSonPoint FROM player INNER JOIN account ON account.id = player.account_id WHERE account.ban = 0 ORDER BY NguHanhSonPoint DESC LIMIT 100;";
    public static final String TOP_DC = "SELECT name, gender, items_body, dicanh, juventus FROM player INNER JOIN account ON account.id = player.account_id WHERE account.ban = 0 ORDER BY dicanh DESC LIMIT 100;";
    public static final String TOP_VDST = "SELECT name, gender, items_body, CAST( JSON_EXTRACT(vodaisinhtu, '$[2]') AS UNSIGNED) AS LastTimeLoginGame, "
            + "CAST( JSON_EXTRACT(vodaisinhtu, '$[3]') AS UNSIGNED) AS time FROM player INNER JOIN account ON account.id = player.account_id WHERE account.ban = 0 AND "
            + "CAST( JSON_EXTRACT(vodaisinhtu, '$[3]') AS UNSIGNED) > 0 ORDER BY CAST( JSON_EXTRACT(vodaisinhtu, '$[3]') AS UNSIGNED) DESC LIMIT 20;";
    public static final String TOP_3_WHIS = "SELECT name, id, gender, items_body, CAST( JSON_EXTRACT(data_luyentap, '$[5]') AS UNSIGNED) AS top, CAST( JSON_EXTRACT(data_luyentap, '$[6]') AS UNSIGNED) AS time, CAST( JSON_EXTRACT(data_luyentap, '$[7]') AS UNSIGNED) AS LastTimeLoginGame FROM player INNER JOIN account ON account.id = player.account_id WHERE account.ban = 0 AND CAST( JSON_EXTRACT(data_luyentap, '$[5]') AS UNSIGNED) > 0 ORDER BY CAST( JSON_EXTRACT(data_luyentap, '$[5]') AS UNSIGNED) DESC, CAST( JSON_EXTRACT(data_luyentap, '$[6]') AS UNSIGNED) ASC LIMIT 3;";
    public static final String TOP_NAP = "SELECT name, gender, items_body,CAST( cash AS UNSIGNED) AS cash FROM account, player WHERE account.id = player.account_id ORDER BY cash DESC LIMIT 20;";

//    public static final String TOP_DUA_SM = "SELECT name, gender, items_body, CAST( JSON_EXTRACT(data_point, '$[1]') AS UNSIGNED) AS sm FROM player WHERE create_time > '2024-" + ConstDataEventSM.MONTH_OPEN + "-" + ConstDataEventSM.DATE_OPEN + " " + ConstDataEventSM.HOUR_OPEN + ":" + ConstDataEventSM.MIN_OPEN + ":00' ORDER BY CAST( split_str(data_point,',',2) AS UNSIGNED) DESC LIMIT 20;";
    public static final String TOP_DUA_NAP = "SELECT name, gender, items_body, CAST( danap AS UNSIGNED) AS danap FROM account, player WHERE account.id = player.account_id AND account.danap >= 100000 ORDER BY account.danap DESC LIMIT 10;";
    //
}





