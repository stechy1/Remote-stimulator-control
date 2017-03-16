package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Testovací třída pro třídu {@link ConfigurationTVEP}
 */
public class ConfigurationTVEPTest {

    private static final String DEFAULT_NAME = "test";

    private ConfigurationTVEP configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationTVEP(DEFAULT_NAME);
    }

    // region PATTERN_LENGTH parameter
    @Test
    public void testGetDefaultPatternLengthValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'patternLength' má špatnou hodnotu.", ConfigurationTVEP.DEF_PATTERN_LENGTH, Integer.parseInt(configuration.getPatternLength()));
    }
    // endregion

    // region PULS_LENGTH parameter
    @Test
    public void testGetDefaultPulsLengthValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'pulsLength' má špatnou hodnotu.", ConfigurationTVEP.DEF_PULS_LENGTH, Integer.parseInt(configuration.getPulsLength()));
    }
    // endregion

    // region TIME_BETWEEN parameter
    @Test
    public void testGetDefaultTimeBetweenValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'timeBetween' má špatnou hodnotu.", ConfigurationTVEP.DEF_TIME_BETWEEN, Integer.parseInt(configuration.getTimeBetween()));
    }
    // endregion

    // region BRIGHTNESS parameter
    @Test
    public void testGetDefaultBrightnessValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'brightness' má špatnou hodnotu.", AConfiguration.DEF_BRIGHTNESS, Integer.parseInt(configuration.getBrightness()));
    }
    // endregion

    // region ConfigurationDuplicate
    @Test
    public void testDuplicatePositive() throws Exception {
        String duplicatedName = "duplicated";
        ConfigurationTVEP duplicated = (ConfigurationTVEP) configuration.duplicate(duplicatedName);

        // Kontrola, jestli se duplikovaly informace z abstraktní třídy AConfiguration
        assertNotSame("Chyba: Duplikovaná konfigurace má stejný název jeko originální.", configuration.getName(), duplicated.getName());
        assertEquals("Chyba: Duplikovaná konfigurace není stejného typu jako originální.", configuration.getConfigurationType(), duplicated.getConfigurationType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.getOutputCount());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'valid' jako originální", configuration.isValid(), duplicated.isValid());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'validityFlag' jako originální", configuration.getValidityFlag(), duplicated.getValidityFlag());

        // Kontrola, jestli se zduplikovaly informace z konkrétní třídy ConfigurationTVEP
        assertNotSame("Chyba: Kolekce výstupů v duplikované konfiguraci ukazuje na stejnou referenci jako originální.", configuration.patternList, duplicated.patternList);
        assertEquals("Chyba: Kolekce výstupů v duplikované konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.patternList.size());
        assertEquals("Chyba: Velikost duplikované kolekce neodpovídá velikosti originální kolekce.", configuration.patternList.size(), duplicated.patternList.size());

        for (int i = 0; i < duplicated.getOutputCount(); i++) {
            Pattern duplicatedOutput = duplicated.patternList.get(i);
            Pattern originalOutput   = configuration.patternList.get(i);

            assertEquals("Chyba: Duplikovaný pattern nemá stejné ID jako originální.", originalOutput.getId(), duplicatedOutput.getId());
            assertEquals("Chyba: Duplicovaný pattern nemá stejnou hodnotu jako originální.", originalOutput.getValue(), duplicatedOutput.getValue());
        }
    }
    // endregion

    // region OutputCount
    @Test
    public void testGetDefaultOutputCount() throws Exception {
        assertEquals("Chyba: Výchozí počet výstupů má špatnou hdnotu.", AConfiguration.DEF_OUTPUT_COUNT, configuration.getOutputCount());
        assertEquals("Chyba: Výchozí velikost kolekce neodpovídá výchozímu počtu výstupů.", AConfiguration.DEF_OUTPUT_COUNT, configuration.patternList.size());
    }

    @Test
    public void testOutputCountPositive() throws Exception {
        int outputCount = AConfiguration.DEF_OUTPUT_COUNT - 1;
        configuration.setOutputCount(outputCount);

        assertEquals("Chyba: Vrácený počet výstupů se neshoduje s nastavenou hodnotou.", outputCount, configuration.getOutputCount());
        assertEquals("Chyba: Velikost kolekce s výstupy se neshoduje s parametrem 'outputCount'.", outputCount, configuration.patternList.size());
    }
    // endregion
}