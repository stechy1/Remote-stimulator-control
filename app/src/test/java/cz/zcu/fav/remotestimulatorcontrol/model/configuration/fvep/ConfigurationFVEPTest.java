package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Testovací třída pro třídu {@link ConfigurationFVEP}
 */
public class ConfigurationFVEPTest {

    private static final String DEFAULT_NAME = "test";

    private ConfigurationFVEP configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationFVEP(DEFAULT_NAME);
    }

    // region ConfigurationDuplicate
    @Test
    public void testDuplicatePositive() throws Exception {
        String duplicatedName = "duplicated";
        ConfigurationFVEP duplicated = (ConfigurationFVEP) configuration.duplicate(duplicatedName);

        // Kontrola, jestli se duplikovaly informace z abstraktní třídy AConfiguration
        assertNotSame("Chyba: Duplikovaná konfigurace má stejný název jeko originální.", configuration.getName(), duplicated.getName());
        assertEquals("Chyba: Duplikovaná konfigurace není stejného typu jako originální.", configuration.getConfigurationType(), duplicated.getConfigurationType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá styjný typ media jako originální.", configuration.getMediaType(), duplicated.getMediaType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.getOutputCount());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'valid' jako originální", configuration.isValid(), duplicated.isValid());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'validityFlag' jako originální", configuration.getValidityFlag(), duplicated.getValidityFlag());

        // Kontrola, jestli se zduplikovaly informace z konkrétní třídy ConfigurationFVEP
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejnou validitu výstupů jako originání", configuration.isFlagValid(FLAG_OUTPUT), duplicated.isFlagValid(FLAG_OUTPUT));
        assertNotSame("Chyba: Kolekce výstupů v duplikované konfiguraci ukazuje na stejnou referenci jako originální.", configuration.outputList, duplicated.outputList);
        assertEquals("Chyba: Velikost duplikované kolekce neodpovídá velikosti originální kolekce.", configuration.outputList.size(), duplicated.outputList.size());

        for (int i = 0; i < duplicated.getOutputCount(); i++) {
            Output duplicatedOutput = duplicated.outputList.get(i);
            Output originalOutput   = configuration.outputList.get(i);

            assertEquals("Chyba: Duplikovaný výstup nemá stejné ID jako originální.", originalOutput.getId(), duplicatedOutput.getId());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'pulsUp' jako originální.", originalOutput.getPulsUp(), duplicatedOutput.getPulsUp());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'pulsDown' jako originální.", originalOutput.getPulsDown(), duplicatedOutput.getPulsDown());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'frequency' jako originální.", originalOutput.getFrequency(), duplicatedOutput.getFrequency());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'dutyCycle' jako originální.", originalOutput.getDutyCycle(), duplicatedOutput.getDutyCycle());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'brightness' jako originální.", originalOutput.getBrightness(), duplicatedOutput.getBrightness());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'valid' jako originální.", originalOutput.isValid(), duplicatedOutput.isValid());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'validityFlag' jako originální.", originalOutput.getValidityFlag(), duplicatedOutput.getValidityFlag());
        }
    }
    // endregion

    // region OutputCount
    @Test
    public void testGetDefaultOutputCount() throws Exception {
        assertEquals("Chyba: Výchozí počet výstupů má špatnou hdnotu.", AConfiguration.DEF_OUTPUT_COUNT, configuration.getOutputCount());
        assertEquals("Chyba: Výchozí velikost kolekce neodpovídá výchozímu počtu výstupů.", AConfiguration.DEF_OUTPUT_COUNT, configuration.outputList.size());
    }

    @Test
    public void testOutputCountPositive() throws Exception {
        int outputCount = AConfiguration.DEF_OUTPUT_COUNT - 1;
        configuration.setOutputCount(outputCount);

        assertEquals("Chyba: Vrácený počet výstupů se neshoduje s nastavenou hodnotou.", outputCount, configuration.getOutputCount());
        assertEquals("Chyba: Velikost kolekce s výstupy se neshoduje s parametrem 'outputCount'.", outputCount, configuration.outputList.size());
    }
    // endregion
}