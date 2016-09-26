package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Spouštěcí třída testů pro konfiguraci REA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigurationREATest.class,
        ParametrizedCycleCountTest.class,
        ParametrizedWaitFixedTest.class,
        ParametrizedWaitRandomTest.class,
        ParametrizedMissTimeTest.class,
        ParametrizedBrightnessTest.class,
        ParametrizedAgeTest.class,
        ParametrizedHeightTest.class,
        ParametrizedWeightTest.class
})
public class ConfigurationREATestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
