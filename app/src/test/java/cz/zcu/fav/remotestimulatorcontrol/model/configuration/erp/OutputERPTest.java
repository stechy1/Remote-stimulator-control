package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP.Output;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

/**
 * Testovací třída pro třídu {@link Output}
 */
public class OutputERPTest {

    private static final String DEFAULT_NAME = "test";

    private ConfigurationERP configuration;
    private Output output;

    @Before
    public void setUp() throws Exception {

        configuration = new ConfigurationERP(DEFAULT_NAME);
        configuration.setOutputCount(1);

        output = configuration.outputList.get(0);
    }

    // region PULS_UP parameter
    @Test
    public void testGetDefaultPulsUpValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'pulsUp' má špatnou hodnotu", Output.DEF_PULS_UP, output.getPulsUp());
    }

    @Test
    public void testPulsUpValueNegativeToPositive() throws Exception {
        output.setPulsUp(Output.MIN_PULS_UP - 1);
        assertFalse("Chyba: Podle validačního příznaku pro parametr 'pulsUp' je výstup validní", output.isFlagValid(Output.FLAG_PULS_UP));
        assertFalse("Chyba: Celý výstup je validní", output.isValid());

        output.setPulsUp(Output.DEF_PULS_UP);
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'pulsUp' je výstup nevalidní", output.isFlagValid(Output.FLAG_PULS_UP));
        assertTrue("Chyba: Celý výstup je validní", output.isValid());
    }
    // endregion

    // region PULS_DOWN parameter
    @Test
    public void testGetDefaultPulsDownValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'pulsDown' má špatnou hodnotu", Output.DEF_PULS_DOWN, output.getPulsDown());
    }
    // endregion

    // region DISTRIBUTION_VALUE parameter
    @Test
    public void testGetDefaultDistributionValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'distributionValue' má špatnou hodnotu", Output.DEF_DISTRIBUTION_VALUE, output.getDistributionValue());
    }
    // endregion

    // region DISTRIBUTION_DELAY parameter
    @Test
    public void testGetDefaultDistributionDelayValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'distributionDelay' má špatnou hodnotu", Output.DEF_DISTRIBUTION_DELAY, output.getDistributionDelay());
    }
    // endregion

    // region BRIGTHTNESS
    @Test
    public void testGetDefaultBrightnessValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'brightness' má špatnou hodnotu", AConfiguration.DEF_BRIGHTNESS, output.getBrightness());
    }
    // endregion

    // region Parent configuration
    @Test
    public void testGetParentConfigurationPositive() throws Exception {
        assertSame("Chyba: Konfigurace přidružená k výstupu se neshoduje s definovanou konfigurací.", configuration, output.getParentConfiguration());

    }
    // endregion
}
