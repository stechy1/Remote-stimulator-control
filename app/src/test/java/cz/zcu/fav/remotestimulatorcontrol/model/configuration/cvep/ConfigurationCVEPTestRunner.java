package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Spouštěcí třída testů pro konfiguraci CVEP
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConfigurationCVEPTest.class,
        PatternCVEPTest.class,
        ParametrizedPulsLengthTest.class,
        ParametrizedBitShiftTest.class,
        ParametrizedBrightnessTest.class
})
public class ConfigurationCVEPTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
