package models.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Utils.Util;

/**
 * Single source of truth for activation-set option ids.
 *
 * The first option in a definition is the set marker. The remaining options
 * are the effects displayed with that set. Advanced sets intentionally keep
 * all of their tier options on the item, matching the existing drop/capsule
 * format.
 */
public final class ActivationSetCatalog {

    public static final class SetDefinition {

        private final int setOptionId;
        private final int[] optionIds;

        private SetDefinition(int setOptionId, int... optionIds) {
            this.setOptionId = setOptionId;
            this.optionIds = optionIds.clone();
        }

        public int getSetOptionId() {
            return setOptionId;
        }

        public int[] getOptionIds() {
            return optionIds.clone();
        }

        public boolean isAdvanced() {
            return optionIds.length > 2;
        }
    }

    private static final List<SetDefinition> OLD_SETS;
    private static final List<SetDefinition> NEW_SETS;
    private static final Map<Integer, SetDefinition> BY_OPTION_ID;
    private static final Set<Integer> ALL_OPTION_IDS;

    /* Order is kept compatible with the existing normal/VIP pools. */
    private static final int[][] OLD_SET_IDS_BY_GENDER = {
        {129, 127, 128},
        {130, 131, 132},
        {133, 135, 134}
    };

    /* Set pool used by normal and VIP activation upgrades. */
    private static final int[][] UPGRADE_SET_IDS_BY_GENDER = {
        {129, 127, 128, 250}, // Earth: includes Yamcha
        {130, 131, 132},
        {133, 135, 134}
    };

    private static final int[][] STANDARD_DROP_SET_IDS_BY_GENDER = {
        {128, 127, 129, 233, 250, 263, 265, 267},
        {130, 131, 132, 233, 251, 263, 265, 267},
        {134, 135, 133, 233, 252, 263, 265, 267}
    };

    /*
     * New activation sets available to each planet when exchanging at Ba Hat Mit.
     * The five all-gender sets are available in every pool; the other two are
     * the planet-specific sets declared in SetClothes.
     */
    private static final int[][] NEW_SET_IDS_BY_GENDER = {
        {250, 245, 233, 263, 265, 267, 269}, // Earth: Yamcha, Kaio + shared
        {251, 237, 233, 263, 265, 267, 269}, // Namek: Slug, Nail + shared
        {252, 241, 233, 263, 265, 267, 269}  // Saiyan: Broly, Cadic M + shared
    };

    private static final int[] ADVANCED_DROP_SET_IDS_BY_GENDER = {245, 237, 241, 269};

    static {
        List<SetDefinition> old = new ArrayList<>();
        old.add(new SetDefinition(129, 129, 141)); // Songoku
        old.add(new SetDefinition(127, 127, 139)); // Thien Xin Hang
        old.add(new SetDefinition(128, 128, 140)); // Kirin
        old.add(new SetDefinition(130, 130, 143)); // Picolo3
        old.add(new SetDefinition(131, 131, 254)); // Oc Tieu
        old.add(new SetDefinition(132, 132, 144)); // Pikkoro Daimao
        old.add(new SetDefinition(133, 133, 136)); // Kakarot
        old.add(new SetDefinition(135, 135, 138)); // Nappa
        old.add(new SetDefinition(134, 134, 137)); // Cadic
        OLD_SETS = Collections.unmodifiableList(old);

        List<SetDefinition> newer = new ArrayList<>();
        newer.add(new SetDefinition(233, 233, 234)); // Gohan
        newer.add(new SetDefinition(250, 250, 253)); // Yamcha
        newer.add(new SetDefinition(251, 251, 142)); // Slug
        newer.add(new SetDefinition(252, 252, 255)); // Broly
        newer.add(new SetDefinition(263, 263, 264)); // Goten
        newer.add(new SetDefinition(265, 265, 266)); // Frieza
        newer.add(new SetDefinition(267, 267, 268)); // Cumber
        newer.add(new SetDefinition(237, 237, 238, 239, 240)); // Nail
        newer.add(new SetDefinition(241, 241, 242, 243, 244)); // Cadic M
        newer.add(new SetDefinition(245, 245, 246, 247, 248)); // Kaio
        newer.add(new SetDefinition(269, 269, 270, 271, 272)); // Champa
        NEW_SETS = Collections.unmodifiableList(newer);

        Map<Integer, SetDefinition> byOptionId = new HashMap<>();
        Set<Integer> allOptionIds = new HashSet<>();
        for (SetDefinition definition : OLD_SETS) {
            for (int optionId : definition.optionIds) {
                byOptionId.put(optionId, definition);
                allOptionIds.add(optionId);
            }
        }
        for (SetDefinition definition : NEW_SETS) {
            for (int optionId : definition.optionIds) {
                byOptionId.put(optionId, definition);
                allOptionIds.add(optionId);
            }
        }
        BY_OPTION_ID = Collections.unmodifiableMap(byOptionId);
        ALL_OPTION_IDS = Collections.unmodifiableSet(allOptionIds);
    }

    private ActivationSetCatalog() {
    }

    public static List<SetDefinition> getOldSets() {
        return OLD_SETS;
    }

    public static List<SetDefinition> getNewSets() {
        return NEW_SETS;
    }

    public static SetDefinition getByOptionId(int optionId) {
        return BY_OPTION_ID.get(optionId);
    }

    public static Set<Integer> getAllOptionIds() {
        return ALL_OPTION_IDS;
    }

    public static int[] getOldSetIds(int gender) {
        return OLD_SET_IDS_BY_GENDER[normalizeGender(gender)].clone();
    }

    public static int[] getUpgradeSetIds(int gender) {
        return UPGRADE_SET_IDS_BY_GENDER[normalizeGender(gender)].clone();
    }

    public static int[] getStandardDropSetIds(int gender) {
        return STANDARD_DROP_SET_IDS_BY_GENDER[normalizeGender(gender)].clone();
    }

    public static int[] getNewSetIds(int gender) {
        return NEW_SET_IDS_BY_GENDER[normalizeGender(gender)].clone();
    }

    public static int getAdvancedDropSetId(int gender) {
        int normalizedGender = gender >= 0 && gender < ADVANCED_DROP_SET_IDS_BY_GENDER.length ? gender : 3;
        return ADVANCED_DROP_SET_IDS_BY_GENDER[normalizedGender];
    }

    public static SetDefinition getDefinition(int setOptionId) {
        SetDefinition definition = BY_OPTION_ID.get(setOptionId);
        if (definition == null || definition.setOptionId != setOptionId) {
            return null;
        }
        return definition;
    }

    public static SetDefinition randomOldSet(int gender) {
        int[] ids = getOldSetIds(gender);
        return getDefinition(ids[Util.nextInt(ids.length)]);
    }

    public static SetDefinition randomUpgradeSet(int gender) {
        int[] ids = getUpgradeSetIds(gender);
        return getDefinition(ids[Util.nextInt(ids.length)]);
    }

    public static SetDefinition randomNewSet(int gender) {
        int[] ids = getNewSetIds(gender);
        return getDefinition(ids[Util.nextInt(ids.length)]);
    }

    private static int normalizeGender(int gender) {
        return gender >= 0 && gender < NEW_SET_IDS_BY_GENDER.length ? gender : 2;
    }

    public static boolean isActivationOption(int optionId) {
        return ALL_OPTION_IDS.contains(optionId);
    }

    public static int getBonusOptionId(int setOptionId) {
        SetDefinition definition = getDefinition(setOptionId);
        if (definition == null || definition.optionIds.length < 2) {
            return 0;
        }
        return definition.optionIds[1];
    }
}
