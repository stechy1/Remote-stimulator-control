package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.ConfigurationCVEPTestRunner;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERPTestRunner;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEPTestRunner;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.ConfigurationREATestRunner;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEPTestRunner;

/**
 * Spouštěcí třída testů pro všechny konfigurace
 */
@RunWith(Suite.class)
@SuiteClasses({
        AConfigurationTest.class,
        UndefinedConfigurationTest.class,
        ConfigurationTypeTest.class,
        ParametrizedMediaTypeTest.class,
        ConfigurationComparatorTest.class,
        ConfigurationERPTestRunner.class,
        ConfigurationFVEPTestRunner.class,
        ConfigurationTVEPTestRunner.class,
        ConfigurationCVEPTestRunner.class,
        ConfigurationREATestRunner.class,
        MetaDataTest.class
})
public class ConfigurationTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
