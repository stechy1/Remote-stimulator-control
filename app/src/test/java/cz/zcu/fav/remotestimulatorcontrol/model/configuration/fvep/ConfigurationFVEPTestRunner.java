package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Spouštěcí třída testů pro konfiguraci F-VEP
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConfigurationFVEPTest.class,
        ParametrizedOutputPulsUpTest.class,
        ParametrizedOutputPulsDownTest.class,
        ParametrizedOutputFrequencyTest.class,
        ParametrizedOutputDutyCycleTest.class,
        ParametrizedOutputBrightnessTest.class
})
public class ConfigurationFVEPTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
