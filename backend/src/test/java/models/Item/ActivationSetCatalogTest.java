package models.Item;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ActivationSetCatalogTest {

    @Test
    void containsAllOldSetsForEachGender() {
        assertArrayEquals(new int[]{129, 127, 128}, ActivationSetCatalog.getOldSetIds(0));
        assertArrayEquals(new int[]{130, 131, 132}, ActivationSetCatalog.getOldSetIds(1));
        assertArrayEquals(new int[]{133, 135, 134}, ActivationSetCatalog.getOldSetIds(2));
        assertEquals(9, ActivationSetCatalog.getOldSets().size());
    }

    @Test
    void containsAllNewSetsAndCanonicalOptions() {
        assertEquals(11, ActivationSetCatalog.getNewSets().size());
        assertArrayEquals(new int[]{251, 142},
                ActivationSetCatalog.getDefinition(251).getOptionIds());
        assertArrayEquals(new int[]{252, 255},
                ActivationSetCatalog.getDefinition(252).getOptionIds());
        assertArrayEquals(new int[]{269, 270, 271, 272},
                ActivationSetCatalog.getDefinition(269).getOptionIds());
    }

    @Test
    void everyCatalogOptionIsRecognizedAndHasNoInvalidBonus() {
        for (ActivationSetCatalog.SetDefinition definition : ActivationSetCatalog.getOldSets()) {
            assertNotNull(definition);
            assertTrue(ActivationSetCatalog.isActivationOption(definition.getSetOptionId()));
            assertTrue(ActivationSetCatalog.getBonusOptionId(definition.getSetOptionId()) > 0);
        }
        for (ActivationSetCatalog.SetDefinition definition : ActivationSetCatalog.getNewSets()) {
            for (int optionId : definition.getOptionIds()) {
                assertTrue(ActivationSetCatalog.isActivationOption(optionId));
            }
        }
        assertFalse(ActivationSetCatalog.isActivationOption(0));
        assertEquals(142, ItemService.gI().optionIdSKH(251));
        assertEquals(255, ItemService.gI().optionIdSKH(252));
        assertEquals(0, ItemService.gI().optionIdSKH(255));
    }

    @Test
    void canonicalBonusMappingCoversAllSetMarkers() {
        int[][] mapping = {
            {127, 139}, {128, 140}, {129, 141},
            {130, 143}, {131, 254}, {132, 144},
            {133, 136}, {134, 137}, {135, 138},
            {233, 234}, {237, 238}, {241, 242}, {245, 246},
            {250, 253}, {251, 142}, {252, 255},
            {263, 264}, {265, 266}, {267, 268}, {269, 270}
        };
        for (int[] pair : mapping) {
            assertEquals(pair[1], ItemService.gI().optionIdSKH(pair[0]),
                    "Unexpected bonus option for set " + pair[0]);
        }
    }
}
