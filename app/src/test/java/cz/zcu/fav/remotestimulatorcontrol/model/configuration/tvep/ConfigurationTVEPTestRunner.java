package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Spouštěcí třída testů pro konfiguraci T-VEP
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConfigurationTVEPTest.class,
        PatternTVEPTest.class,
        ParametrizedPatternLengthTest.class,
        ParametrizedPulsLengthTest.class,
        ParametrizedTimeBetweenTest.class,
        ParametrizedBrightnessTest.class
})
public class ConfigurationTVEPTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotac
}
