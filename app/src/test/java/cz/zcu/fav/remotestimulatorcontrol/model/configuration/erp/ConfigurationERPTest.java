package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP.Output;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

/**
 * Testovací třída pro třídu {@link ConfigurationERP}
 */
public class ConfigurationERPTest {

    private static final String DEFAULT_NAME = "test";

    private ConfigurationERP configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationERP(DEFAULT_NAME);
    }

    // region OUT parameter
    @Test
    public void testGetDefaultOutValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'out' má špatnou hodnotu.", ConfigurationERP.DEF_OUT, configuration.getOut());
    }
    // endregion

    // region WAIT parameter

    @Test
    public void testGetDefaultWaitValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'wait' má špatnou hodnotu.", ConfigurationERP.DEF_WAIT, configuration.getWait());
    }
    // endregion

    // region EDGE parameter
    @Test
    public void testGetDefaultEdgeValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru edge má špatnou hodnotu.", ConfigurationERP.DEF_EDGE, configuration.getEdge());
    }

//    @Test
//    public void testEdgeValuePositive1() throws Exception {
//        ConfigurationERP.Edge edge = ConfigurationERP.Edge.FALLING;
//        configuration.setEdge(edge);
//
//        assertEquals("Chyba: Vrácený parametr neodpovádí nastavenému.", edge, configuration.getEdge());
//    }
//
//    @Test
//    public void testEdgeValuePositive2() throws Exception {
//        ConfigurationERP.Edge edge = ConfigurationERP.Edge.LEADING;
//        configuration.setEdge(edge);
//
//        assertEquals("Chyba: Vrácený parametr neodpovádí nastavenému.", edge, configuration.getEdge());
//    }

    @Test
    public void testEdgeValueOfPositive() throws Exception {
        ConfigurationERP.Edge edge = ConfigurationERP.Edge.FALLING;
        int index = edge.ordinal();

        assertEquals("Chyba: Vrácená hrana neodpovídá ordinálnímu číslu.", edge, ConfigurationERP.Edge.valueOf(index));
    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void testEdgeValueOfNegative() throws Exception {
        int index = -1;

        ConfigurationERP.Edge.valueOf(index);

    }

    // endregion

    // region RANDOM parameter
    @Test
    public void testGetDefaultRandomValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru random má špatnou hodnotu.", ConfigurationERP.DEF_RANDOM, configuration.getRandom());
    }

//    @Test
//    public void testRandomValuePositive1() throws Exception {
//        ConfigurationERP.Random random = ConfigurationERP.Random.OFF;
//        configuration.setRandom(random);
//
//        assertEquals("Chyba: Vrácený parametr neodpovídá nastavenému.", random, configuration.getRandom());
//    }
//
//    @Test
//    public void testRandomValuePositive2() throws Exception {
//        ConfigurationERP.Random random = ConfigurationERP.Random.SHORT;
//        configuration.setRandom(random);
//
//        assertEquals("Chyba: Vrácený parametr neodpovídá nastavenému.", random, configuration.getRandom());
//    }
//
//    @Test
//    public void testRandomValuePositive3() throws Exception {
//        ConfigurationERP.Random random = ConfigurationERP.Random.LONG;
//        configuration.setRandom(random);
//
//        assertEquals("Chyba: Vrácený parametr neodpovídá nastavenému.", random, configuration.getRandom());
//    }
//
//    @Test
//    public void testRandomValuePositive4() throws Exception {
//        ConfigurationERP.Random random = ConfigurationERP.Random.SHORT_LONG;
//        configuration.setRandom(random);
//
//        assertEquals("Chyba: Vrácený parametr neodpovídá nastavenému.", random, configuration.getRandom());
//    }

    @Test
    public void testRandomValueOfPositive() throws Exception {
        ConfigurationERP.Random random = ConfigurationERP.Random.OFF;
        int index = random.ordinal();

        assertEquals("Chyba: Vrácený random neodpovídá ordinálnímu číslu.", random, ConfigurationERP.Random.valueOf(index));

    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void testRandomValueOfNegative() throws Exception {
        int index = -1;

        ConfigurationERP.Random.valueOf(index);

    }

    // endregion
    
    // region ConfigurationDuplicate
    @Test
    public void testDuplicatePositive() throws Exception {
        String duplicatedName = "duplicated";
        ConfigurationERP duplicated = (ConfigurationERP) configuration.duplicate(duplicatedName);

        // Kontrola, jestli se duplikovaly informace z abstraktní třídy AConfiguration
        assertNotSame("Chyba: Duplikovaná konfigurace má stejný název jeko originální.", configuration.getName(), duplicated.getName());
        assertEquals("Chyba: Duplikovaná konfigurace není stejného typu jako originální.", configuration.getConfigurationType(), duplicated.getConfigurationType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá styjný typ media jako originální.", configuration.getMediaType(), duplicated.getMediaType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.getOutputCount());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'valid' jako originální", configuration.isValid(), duplicated.isValid());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'validityFlag' jako originální", configuration.getValidityFlag(), duplicated.getValidityFlag());
        
        // Kontrola, jestli se zduplikovaly informace z konkrétní třídy ConfigurationERP
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'out' jako originální.", configuration.getOut(), duplicated.getOut());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'wait' jako originální.", configuration.getWait(), duplicated.getWait());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'edge' jako originální.", configuration.getEdge(), duplicated.getEdge());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'edge' jako originální.", configuration.getRandom(), duplicated.getRandom());
        assertNotSame("Chyba: Kolekce výstupů v duplikované konfiguraci ukazuje na stejnou referenci jako originální.", configuration.outputList, duplicated.outputList);
        assertEquals("Chyba: Velikost duplikované kolekce neodpovídá velikosti originální kolekce.", configuration.outputList.size(), duplicated.outputList.size());

        for (int i = 0; i < duplicated.getOutputCount(); i++) {
            Output duplicatedOutput = duplicated.outputList.get(i);
            Output originalOutput   = configuration.outputList.get(i);
            
            assertEquals("Chyba: Duplikovaný výstup nemá stejné ID jako originální.", originalOutput.getId(), duplicatedOutput.getId());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'pulsUp' jako originální.", originalOutput.getPulsUp(), duplicatedOutput.getPulsUp());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'pulsDown' jako originální.", originalOutput.getPulsDown(), duplicatedOutput.getPulsDown());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'distributionValue' jako originální.", originalOutput.getDistributionValue(), duplicatedOutput.getDistributionValue());
            assertEquals("Chyba: Duplikovaný výstup nemá stejný parametr 'distributionDelay' jako originální.", originalOutput.getDistributionDelay(), duplicatedOutput.getDistributionDelay());
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

    // region MaxDistributionValue parameter


    @Test
    public void testMaxDistributionValuePositive1() throws Exception {
        final int outputCount = 1;
        final int distributionValue = (Output.MAX_DISTRIBUTION_VALUE + Output.MIN_DISTRIBUTION_VALUE) / 2;
        final int expectedMaxDistValue = (Output.MAX_DISTRIBUTION_VALUE + Output.MIN_DISTRIBUTION_VALUE) / 2;

        // Nastavení počtu výstupů pouze na 2 pro snažší testování
        configuration.setOutputCount(outputCount);

        // Získání referencí pracovních výstupů
        final Output output = configuration.outputList.get(0);

        // Nastavení testované hodnoty
        output.setDistributionValue(distributionValue);

        // Otestování správně přepočítané hodnoty parametru 'maxDistributionValue'
        assertEquals("Chyba: Očekávaná maximální hodnota parametru 'maxDistributionValue se neshoduje s vypočítanou hodnotou.", expectedMaxDistValue, configuration.getMaxDistributionValue());
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'distributionValue' je výstup nevalidní.", output.isFlagValid(Output.FLAG_DISTRIBUTION_VALUE));
        assertTrue("Chyba: Celý výstup není validní.", output.isValid());
    }

    @Test
    public void testMaxDistributionValuePositive2() throws Exception {
        final int outputCount = 2;
        final int distributionValue1 = (Output.MAX_DISTRIBUTION_VALUE + Output.MIN_DISTRIBUTION_VALUE) / 2;
        final int distributionValue2 = (Output.MAX_DISTRIBUTION_VALUE + Output.MIN_DISTRIBUTION_VALUE) / 2;
        final int expectedMaxDistValue = Output.MIN_DISTRIBUTION_VALUE;

        // Nastavení počtu výstupů pouze na 2 pro snažší testování
        configuration.setOutputCount(outputCount);

        // Získání referencí pracovních výstupů
        final Output output1 = configuration.outputList.get(0);
        final Output output2 = configuration.outputList.get(1);

        output1.setDistributionValue(distributionValue1);
        output2.setDistributionValue(distributionValue2);

        // Otestování správně přepočítané hodnoty parametru 'maxDistributionValue'
        assertEquals("Chyba: Očekávaná maximální hodnota parametru 'maxDistributionValue se neshoduje s vypočítanou hodnotou.", expectedMaxDistValue, configuration.getMaxDistributionValue());
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'distributionValue' je výstup č. 1 nevalidní.", output1.isFlagValid(Output.FLAG_DISTRIBUTION_VALUE));
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'distributionValue' je výstup č. 2 nevalidní.", output2.isFlagValid(Output.FLAG_DISTRIBUTION_VALUE));
        assertTrue("Chyba: Celý výstup č. 1 není validní.", output1.isValid());
        assertTrue("Chyba: Celý výstup č. 2 není validní.", output2.isValid());
    }

    @Test
    public void testMaxDistributionValueNegative1() throws Exception {
        final int outputCount = 2;
        final int distributionValue = Output.MAX_DISTRIBUTION_VALUE + 20;
        final int expectedMaxDistValue = Output.MIN_DISTRIBUTION_VALUE;
        // Nastavení počtu výstupů pouze na 2 pro snažší testování
        configuration.setOutputCount(outputCount);

        // Získání reference pouze jednoho pracovního výstupu
        final Output output = configuration.outputList.get(0);

        assertEquals("Chyba: Parametr 'maxDistributionValue' nemá nastavenou hodnotu na MAX_DISTRIBUTION_VALUE.", Output.MAX_DISTRIBUTION_VALUE, configuration.getMaxDistributionValue());

        output.setDistributionValue(distributionValue);

        // Otestování správně přepočítané hodnoty parametru 'maxDistributionValue'
        assertEquals("Chyba: Očekávaná maximální hodnota parametru 'maxDistributionValue se neshoduje s vypočítanou hodnotou.", expectedMaxDistValue, configuration.getMaxDistributionValue());
        assertFalse("Chyba: Podle validačního příznaku pro parametr 'distributionValue' je výstup validní.", output.isFlagValid(Output.FLAG_DISTRIBUTION_VALUE));
        assertFalse("Chyba: Celý výstup není validní.", output.isValid());
    }
    // endregion
}