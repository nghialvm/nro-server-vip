package nro.player;

import org.json.simple.JSONArray;

public class Charms {

    // CÃ¡c buff bÃ¹a cÆ¡ báº£n
    public long tdTriTue;
    public long tdManhMe;
    public long tdDaTrau;
    public long tdOaiHung;
    public long tdBatTu;
    public long tdDeoDai;
    public long tdThuHut;

    // CÃ¡c buff Ä‘á»‡ tá»­
    public long tdDeTu;
    public long tdDeTu2;
    public long tdDeTu3;
    public long tdDeTu4;
    public long tdDeTu5;
    public long tdDeTu7;
    public long tdDeTu10;
    public long tdDeTu20;

    // Buff trÃ­ tuá»‡ nÃ¢ng cao
    public long tdTriTue3;
    public long tdTriTue4;
    public long tdTriTue5;
    public long tdTriTue7;
    public long tdTriTue10;
    public long tdTriTue20;
    public long lastTimeSubMinTriTueX4;

    // Buff clan
    public long tdDaTrauClan;
    public long tdManhMeClan;
    public long tdTriTueClan;

    // ============================ ADD TIME ============================
    public void addTimeCharms(int itemId, int min) {
        switch (itemId) {
            case 213:
                tdTriTue = Math.max(tdTriTue, System.currentTimeMillis());
                tdTriTue += min * 60 * 1000L;
                break;
            case 214:
                tdManhMe = Math.max(tdManhMe, System.currentTimeMillis());
                tdManhMe += min * 60 * 1000L;
                break;
            case 215:
                tdDaTrau = Math.max(tdDaTrau, System.currentTimeMillis());
                tdDaTrau += min * 60 * 1000L;
                break;
            case 216:
                tdOaiHung = Math.max(tdOaiHung, System.currentTimeMillis());
                tdOaiHung += min * 60 * 1000L;
                break;
            case 217:
                tdBatTu = Math.max(tdBatTu, System.currentTimeMillis());
                tdBatTu += min * 60 * 1000L;
                break;
            case 218:
                tdDeoDai = Math.max(tdDeoDai, System.currentTimeMillis());
                tdDeoDai += min * 60 * 1000L;
                break;
            case 219:
                tdThuHut = Math.max(tdThuHut, System.currentTimeMillis());
                tdThuHut += min * 60 * 1000L;
                break;
            case 522:
                tdDeTu = Math.max(tdDeTu, System.currentTimeMillis());
                tdDeTu += min * 60 * 1000L;
                break;
            case 671:
                tdTriTue3 = Math.max(tdTriTue3, System.currentTimeMillis());
                tdTriTue3 += min * 60 * 1000L;
                break;
            case 672:
                tdTriTue4 = Math.max(tdTriTue4, System.currentTimeMillis());
                tdTriTue4 += min * 60 * 1000L;
                break;
            case 1734:
                tdDeTu2 = Math.max(tdDeTu2, System.currentTimeMillis());
                tdDeTu2 += min * 60 * 1000L;
                break;
            case 1735:
                tdDeTu3 = Math.max(tdDeTu3, System.currentTimeMillis());
                tdDeTu3 += min * 60 * 1000L;
                break;
            case 1736:
                tdDeTu4 = Math.max(tdDeTu4, System.currentTimeMillis());
                tdDeTu4 += min * 60 * 1000L;
                break;
            case 1737:
                tdDeTu5 = Math.max(tdDeTu5, System.currentTimeMillis());
                tdDeTu5 += min * 60 * 1000L;
                break;
            case 1738:
                tdDeTu7 = Math.max(tdDeTu7, System.currentTimeMillis());
                tdDeTu7 += min * 60 * 1000L;
                break;
            case 1739:
                tdDeTu10 = Math.max(tdDeTu10, System.currentTimeMillis());
                tdDeTu10 += min * 60 * 1000L;
                break;
            case 1740:
                tdDeTu20 = Math.max(tdDeTu20, System.currentTimeMillis());
                tdDeTu20 += min * 60 * 1000L;
                break;
            case 1741:
                tdTriTue5 = Math.max(tdTriTue5, System.currentTimeMillis());
                tdTriTue5 += min * 60 * 1000L;
                break;
            case 1742:
                tdTriTue7 = Math.max(tdTriTue7, System.currentTimeMillis());
                tdTriTue7 += min * 60 * 1000L;
                break;
            case 1743:
                tdTriTue10 = Math.max(tdTriTue10, System.currentTimeMillis());
                tdTriTue10 += min * 60 * 1000L;
                break;
            case 1744:
                tdTriTue20 = Math.max(tdTriTue20, System.currentTimeMillis());
                tdTriTue20 += min * 60 * 1000L;
                break;
            case 797:
                tdTriTueClan = Math.max(tdTriTueClan, System.currentTimeMillis());
                tdTriTueClan += min * 60 * 1000L;
                break;
            case 798:
                tdManhMeClan = Math.max(tdManhMeClan, System.currentTimeMillis());
                tdManhMeClan += min * 60 * 1000L;
                break;
            case 799:
                tdDaTrauClan = Math.max(tdDaTrauClan, System.currentTimeMillis());
                tdDaTrauClan += min * 60 * 1000L;
                break;
            case 1950: // item reset toÃ n bá»™ bÃ¹a
                resetAllCharms();
                break;
        }
    }

    // ============================ RESET ============================
    public void resetAllCharms() {
        tdTriTue = tdManhMe = tdDaTrau = tdOaiHung = tdBatTu = tdDeoDai = tdThuHut = 0;
        tdDeTu = tdDeTu2 = tdDeTu3 = tdDeTu4 = tdDeTu5 = tdDeTu7 = tdDeTu10 = tdDeTu20 = 0;
        tdTriTue3 = tdTriTue4 = tdTriTue5 = tdTriTue7 = tdTriTue10 = tdTriTue20 = 0;
        lastTimeSubMinTriTueX4 = 0;
        tdDaTrauClan = tdManhMeClan = tdTriTueClan = 0;
    }

    // ============================ JSON SAVE ============================
    public JSONArray toJSONArray() {
        JSONArray arr = new JSONArray();
        arr.add(tdTriTue);
        arr.add(tdManhMe);
        arr.add(tdDaTrau);
        arr.add(tdOaiHung);
        arr.add(tdBatTu);
        arr.add(tdDeoDai);
        arr.add(tdThuHut);
        arr.add(tdDeTu);
        arr.add(tdTriTue3);
        arr.add(tdTriTue4);
        arr.add(tdDeTu2);
        arr.add(tdDeTu3);
        arr.add(tdDeTu4);
        arr.add(tdDeTu5);
        arr.add(tdDeTu7);
        arr.add(tdDeTu10);
        arr.add(tdDeTu20);
        arr.add(tdTriTue5);
        arr.add(tdTriTue7);
        arr.add(tdTriTue10);
        arr.add(tdTriTue20);
        return arr;
    }

    public void loadFromJSONArray(JSONArray arr) {
        if (arr == null || arr.isEmpty()) return;
        tdTriTue = Long.parseLong(String.valueOf(arr.get(0)));
        tdManhMe = Long.parseLong(String.valueOf(arr.get(1)));
        tdDaTrau = Long.parseLong(String.valueOf(arr.get(2)));
        tdOaiHung = Long.parseLong(String.valueOf(arr.get(3)));
        tdBatTu = Long.parseLong(String.valueOf(arr.get(4)));
        tdDeoDai = Long.parseLong(String.valueOf(arr.get(5)));
        tdThuHut = Long.parseLong(String.valueOf(arr.get(6)));
        tdDeTu = Long.parseLong(String.valueOf(arr.get(7)));
        tdTriTue3 = Long.parseLong(String.valueOf(arr.get(8)));
        tdTriTue4 = Long.parseLong(String.valueOf(arr.get(9)));
        tdDeTu2 = Long.parseLong(String.valueOf(arr.get(10)));
        tdDeTu3 = Long.parseLong(String.valueOf(arr.get(11)));
        tdDeTu4 = Long.parseLong(String.valueOf(arr.get(12)));
        tdDeTu5 = Long.parseLong(String.valueOf(arr.get(13)));
        tdDeTu7 = Long.parseLong(String.valueOf(arr.get(14)));
        tdDeTu10 = Long.parseLong(String.valueOf(arr.get(15)));
        tdDeTu20 = Long.parseLong(String.valueOf(arr.get(16)));
        tdTriTue5 = Long.parseLong(String.valueOf(arr.get(17)));
        tdTriTue7 = Long.parseLong(String.valueOf(arr.get(18)));
        tdTriTue10 = Long.parseLong(String.valueOf(arr.get(19)));
        tdTriTue20 = Long.parseLong(String.valueOf(arr.get(20)));
    }

    public void dispose() {
         // clear khi cần
    }
}





