package nro.template;

public class Template {
   
    public static class TaiXiuMD5Data {

        public long playerId;
        public long tai;
        public long xiu;

        public TaiXiuMD5Data(long playerId, long tai, long xiu) {
            this.playerId = playerId;
            this.tai = tai;
            this.xiu = xiu;
        }
    }

    public static class TaiXiuData {

        public long playerId;
        public long tai;
        public long xiu;

        public TaiXiuData(long playerId, long tai, long xiu) {
            this.playerId = playerId;
            this.tai = tai;
            this.xiu = xiu;
        }
    }

    public static class XocDiaData {

        public long playerId;

        public long chanX1;
        public long chanXiu;
        public long chanX3;
        public long chanX15;

        public long leX1;
        public long leTai;
        public long leX3;
        public long leX15;

        public XocDiaData(long playerId, long chanX1, long chanXiu, long chanX3, long chanX15, long leX1, long leTai, long leX3, long leX15) {
            this.playerId = playerId;
            this.chanX1 = chanX1;
            this.chanXiu = chanXiu;
            this.chanX3 = chanX3;
            this.chanX15 = chanX15;
            this.leX1 = leX1;
            this.leTai = leTai;
            this.leX3 = leX3;
            this.leX15 = leX15;
        }
    }

    public static class BauCuaData {

        public long playerId;

        public long bau;
        public long cua;
        public long tom;
        public long ca;
        public long nai;
        public long ga;

        public BauCuaData(long playerId, long bau, long cua, long tom, long ca, long nai, long ga) {
            this.playerId = playerId;
            this.bau = bau;
            this.cua = cua;
            this.tom = tom;
            this.ca = ca;
            this.nai = nai;
            this.ga = ga;
        }
    }

}





