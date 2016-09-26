package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP.Pattern;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída pro třídu {@link Pattern}
 */
public class PatternTVEPTest {

    private static final int DEFAULT_ID = 1;

    private Pattern pattern;

    @Before
    public void setUp() throws Exception {
        pattern = new Pattern(DEFAULT_ID);
    }

    // region VALUE parameter

    @Test
    public void testGetDefaultValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'value' má špatnou hodnotu.", Pattern.DEF_VALUE, pattern.getValue());
    }

    // endregion
}
