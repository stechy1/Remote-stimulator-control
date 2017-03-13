package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Testovací třída pro třídu {@link ConfigurationREA}
 */
public class ConfigurationREATest {

    private static final String DEFAULT_NAME = "test";

    private ConfigurationREA configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationREA(DEFAULT_NAME);
    }

    // region CYCLE_COUNT parameter
    @Test
    public void testGetDefaultCycleCountValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'cycleCount' má špatnou hodnotu.", ConfigurationREA.DEF_CYCLE_COUNT, configuration.getCycleCount());
    }
    // endregion

    // region WAIT_FIXED parameter
    @Test
    public void testGetDefaultWaitFixedValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'waitFixed' má špatnou hodnotu.", ConfigurationREA.DEF_WAIT_FIXED, configuration.getWaitFixed());
    }
    // endregion

    // region WAIT_RANDOM parameter
    @Test
    public void testGetDefaultWaitRandomValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'waitRandom' má špatnou hodnotu.", ConfigurationREA.DEF_WAIT_RANDOM, configuration.getWaitRandom());
    }
    // endregion

    // region MISS_TIME parameter
    @Test
    public void testGetDefaultMissTimeValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'missTime' má špatnou hodnotu.", ConfigurationREA.DEF_MISS_TIME, configuration.getMissTime());
    }
    // endregion

    // region BRIGHTNESS parameter
    @Test
    public void testGetDefaultBrightnessValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'brightness' má špatnou hodnotu.", AConfiguration.DEF_BRIGHTNESS, configuration.getBrightness());
    }
    // endregion

    // region ON_FAIL parameter
    @Test
    public void testGetDefaultOnFailValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'onFail' má špatnou hodnotu.", ConfigurationREA.DEF_ON_FAIL, configuration.getOnFail());
    }

    @Test
    public void testOnFailValueOfPositive1() throws Exception {
        ConfigurationREA.OnFail onFail = ConfigurationREA.OnFail.WAIT;
        int index = onFail.ordinal();

        assertEquals("Chyba: Vrácená hrana neodpovídá ordinálnímu číslu.", onFail, ConfigurationREA.OnFail.valueOf(index));
    }

    @Test
    public void testOnFailValueOfPositive2() throws Exception {
        ConfigurationREA.OnFail onFail = ConfigurationREA.OnFail.CONTINUE;
        int index = onFail.ordinal();

        assertEquals("Chyba: Vrácená hrana neodpovídá ordinálnímu číslu.", onFail, ConfigurationREA.OnFail.valueOf(index));
    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void testOnFailValueOfNegative() throws Exception {
        int index = -1;

        ConfigurationREA.OnFail.valueOf(index);

    }

    // endregion

    // region GENDER parameter
    @Test
    public void testGetDefaultGenderValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'gender' má špatnou hodnotu.", ConfigurationREA.DEF_GENDER, configuration.getGender());
    }

    @Test
    public void testGenderValueOfPositive1() throws Exception {
        ConfigurationREA.Gender gender = ConfigurationREA.Gender.MALE;
        int index = gender.ordinal();

        assertEquals("Chyba: Vrácená hrana neodpovídá ordinálnímu číslu.", gender, ConfigurationREA.Gender.valueOf(index));
    }

    @Test
    public void testGenderValueOfPositive2() throws Exception {
        ConfigurationREA.Gender gender = ConfigurationREA.Gender.FEMALE;
        int index = gender.ordinal();

        assertEquals("Chyba: Vrácená hrana neodpovídá ordinálnímu číslu.", gender, ConfigurationREA.Gender.valueOf(index));
    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void testGenderValueOfNegative() throws Exception {
        int index = -1;

        ConfigurationREA.Gender.valueOf(index);

    }

    // endregion

    // region AGE parameter
    @Test
    public void testGetDefaultAgeValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'age' má špatnou hodnotu.", ConfigurationREA.DEF_AGE, configuration.getAge());
    }
    // endregion

    // region HEIGHT parameter
    @Test
    public void testGetDefaultHeightValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'height' má špatnou hodnotu.", ConfigurationREA.DEF_HEIGHT, configuration.getHeight());
    }
    // endregion

    // region WEIGHT parameter
    @Test
    public void testGetDefaultweightValue() throws Exception {
        assertEquals("Chyba: Výchozí hodnota parametru 'weight' má špatnou hodnotu.", ConfigurationREA.DEF_WEIGHT, configuration.getWeight());
    }
    // endregion

    // region ConfigurationDuplicate
    @Test
    public void testDuplicatePositive() throws Exception {
        String duplicatedName = "duplicated";
        ConfigurationREA duplicated = (ConfigurationREA) configuration.duplicate(duplicatedName);

        // Kontrola, jestli se duplikovaly informace z abstraktní třídy AConfiguration
        assertNotSame("Chyba: Duplikovaná konfigurace má stejný název jeko originální.", configuration.getName(), duplicated.getName());
        assertEquals("Chyba: Duplikovaná konfigurace není stejného typu jako originální.", configuration.getConfigurationType(), duplicated.getConfigurationType());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný počet výstupů jako originální.", configuration.getOutputCount(), duplicated.getOutputCount());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'valid' jako originální", configuration.isValid(), duplicated.isValid());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'validityFlag' jako originální", configuration.getValidityFlag(), duplicated.getValidityFlag());

        // Kontrola, jestli se zduplikovaly informace z konkrétní třídy ConfigurationERP
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'cycleCount' jako originální.", configuration.getCycleCount(), duplicated.getCycleCount());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'waitFixed' jako originální.", configuration.getWaitFixed(), duplicated.getWaitFixed());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'waitRandom' jako originální.", configuration.getWaitRandom(), duplicated.getWaitRandom());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'missTime' jako originální.", configuration.getMissTime(), duplicated.getMissTime());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'brightness' jako originální.", configuration.getBrightness(), duplicated.getBrightness());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'onFail' jako originální.", configuration.getOnFail(), duplicated.getOnFail());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'gender' jako originální.", configuration.getGender(), duplicated.getGender());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'age' jako originální.", configuration.getAge(), duplicated.getAge());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'height' jako originální.", configuration.getHeight(), duplicated.getHeight());
        assertEquals("Chyba: Duplikovaná konfigurace nemá stejný parametr 'weight' jako originální.", configuration.getWeight(), duplicated.getWeight());

    }
    // endregion
}