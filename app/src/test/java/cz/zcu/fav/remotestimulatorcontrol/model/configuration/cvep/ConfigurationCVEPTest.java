package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.ConfigurationCVEP.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Testovací třída pro třídu {@link ConfigurationCVEP}
 */
public class ConfigurationCVEPTest {

    private static final String DEFAULT_NAME = "test";

    private ConfigurationCVEP configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationCVEP(DEFAULT_NAME);
    }

    // region PULS_LENGTH parameter
    @Test
    public void testGetDefaultPulsLengthValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'pulsLength' má špatnou hodnotu.", ConfigurationCVEP.DEF_PULS_LENGTH, configuration.getPulsLength());
    }
    // endregion

    // region BIT_SHIFT parameter
    @Test
    public void testGetDefaultBitShiftValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'bitShift' má špatnou hodnotu.", ConfigurationCVEP.DEF_BIT_SHIFT, configuration.getBitShift());
    }
    // endregion

    // region BRIGHTNESS parameter
    @Test
    public void testGetDefaultBrightnessValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'brightness' má špatnou hodnotu.", AConfiguration.DEF_BRIGHTNESS, configuration.getBrightness());
    }
    // endregion

    // region ConfigurationDuplicate
    @Test
    public void testDuplicatePositive() throws Exception {
        String duplicatedName = "duplicated";
        ConfigurationCVEP duplicated = (ConfigurationCVEP) configuration.duplicate(duplicatedName);

        // Kontrola, jestli se duplikovaly informace z abstraktní třídy AConfiguration
        assertNotSame("Chyba: Duplikovaná konfigurace má stejný název jeko originální.", configuration.getName(), duplicated.getName());
        assertEquals("Chyba: Duplikovaná konfigurace není stejného typu jako originální.", configuration.getConfigurationType(), duplicated.getConfigurationType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.getOutputCount());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'valid' jako originální", configuration.isValid(), duplicated.isValid());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'validityFlag' jako originální", configuration.getValidityFlag(), duplicated.getValidityFlag());

        // Kontrola, jestli se zduplikovaly informace z konkrétní třídy ConfigurationCVEP
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'pulsLength' jako originální.", configuration.getPulsLength(), duplicated.getPulsLength());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'bitShift' jako originální.", configuration.getBitShift(), duplicated.getBitShift());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'brightness' jako originální.", configuration.getBrightness(), duplicated.getBrightness());
        assertNotSame("Chyba: Kolekce výstupů v duplikované konfiguraci ukazuje na stejnou referenci jako originální.", configuration.patternList, duplicated.patternList);
        assertEquals("Chyba: Kolekce výstupů v duplikované konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.patternList.size());
        assertEquals("Chyba: Velikost duplikované kolekce neodpovídá velikosti originální kolekce.", configuration.patternList.size(), duplicated.patternList.size());

        for (int i = 0; i < duplicated.getOutputCount(); i++) {
            Pattern duplicatedOutput = duplicated.patternList.get(i);
            Pattern originalOutput   = configuration.patternList.get(i);

            assertEquals("Chyba: Duplikovaný výstup nemá stejné ID jako originální.", originalOutput.getId(), duplicatedOutput.getId());
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