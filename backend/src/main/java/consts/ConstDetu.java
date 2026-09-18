package consts;


public class ConstDetu {

    public static final byte NORMAL = 0;
    public static final byte MABU = 1;
    //PET NEW
    public static final byte U_BU = 2;
    public static final byte KID_JIREN = 3;
    public static final byte KID_BEER = 4;
    public static final byte BLACK = 5;

    public static final byte SPECIAL_PET_INITIAL_LIMIT = 13;

    public static boolean isSpecialPetType(byte typeDeTu) {
        return typeDeTu == U_BU
                || typeDeTu == KID_JIREN
                || typeDeTu == KID_BEER
                || typeDeTu == BLACK;
    }

    public static byte normalizeSpecialPetLimit(byte typeDeTu, byte limitPower) {
        if (isSpecialPetType(typeDeTu) && limitPower > SPECIAL_PET_INITIAL_LIMIT) {
            return SPECIAL_PET_INITIAL_LIMIT;
        }
        return limitPower;
    }
}





