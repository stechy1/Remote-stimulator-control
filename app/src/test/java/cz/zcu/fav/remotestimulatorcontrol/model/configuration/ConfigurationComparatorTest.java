package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.Test;

import java.util.Comparator;

import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationHelper;

import static junit.framework.Assert.assertTrue;

/**
 * Testovací třída pro třídu {@link ConfigurationComparator}
 */
public class ConfigurationComparatorTest {

    // region NAME_COMPARATOR
    @Test
    public void testEqual1() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.NAME_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a");
        AConfiguration configuration2 = ConfigurationHelper.from("a");

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace se nerovnají.", result == 0);
    }

    @Test
    public void testGreaterThan1() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.NAME_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("b");
        AConfiguration configuration2 = ConfigurationHelper.from("a");

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result >= 1);
    }

    @Test
    public void testLessThan1() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.NAME_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a");
        AConfiguration configuration2 = ConfigurationHelper.from("b");

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result <= -1);
    }

    @Test
    public void testGreaterThanInverted() throws Exception {
        Comparator<AConfiguration> comparator = ConfigurationComparator.decending(ConfigurationComparator.NAME_COMPARATOR);
        AConfiguration configuration1 = ConfigurationHelper.from("b");
        AConfiguration configuration2 = ConfigurationHelper.from("a");

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result <= -1);
    }

    // endregion

    // region TYPE_COMPARATOR
    @Test
    public void testEqual2() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.TYPE_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a", ConfigurationType.CVEP);
        AConfiguration configuration2 = ConfigurationHelper.from("a", ConfigurationType.CVEP);

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace se nerovnají.", result == 0);
    }

    @Test
    public void testGreaterThan2() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.TYPE_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a", ConfigurationType.CVEP);
        AConfiguration configuration2 = ConfigurationHelper.from("a", ConfigurationType.ERP);

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result >= 1);
    }

    @Test
    public void testLessThan2() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.TYPE_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a", ConfigurationType.ERP);
        AConfiguration configuration2 = ConfigurationHelper.from("a", ConfigurationType.CVEP);

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result <= -1);
    }
    // endregion

    // region NAME_COMPARATOR
    @Test
    public void testEqual3() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.MEDIA_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a"); // Media == LED
        AConfiguration configuration2 = ConfigurationHelper.from("a"); // Media == LED

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace se nerovnají.", result == 0);
    }

    @Test
    public void testGreaterThan3() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.MEDIA_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a");
        AConfiguration configuration2 = ConfigurationHelper.from("a"); // Media == LED

        configuration1.setMediaType(MediaType.AUDIO);

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result >= 1);
    }

    @Test
    public void testLessThan3() throws Exception {
        ConfigurationComparator comparator = ConfigurationComparator.MEDIA_COMPARATOR;
        AConfiguration configuration1 = ConfigurationHelper.from("a"); // MEDIA == LED
        AConfiguration configuration2 = ConfigurationHelper.from("b");

        configuration2.setMediaType(MediaType.AUDIO);

        int result = comparator.compare(configuration1, configuration2);
        assertTrue("Chyba: Konfigurace č. 1 není 'větší' než konfigurace č. 2.", result <= -1);
    }
    // endregion
}