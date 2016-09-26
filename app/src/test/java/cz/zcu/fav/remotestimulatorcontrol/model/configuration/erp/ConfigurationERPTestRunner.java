package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Spouštěcí třída testů pro konfiguraci ERP
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConfigurationERPTest.class,
        ParametrizedOutTest.class,
        ParametrizedWaitTest.class,
        OutputERPTest.class,
        ParametrizedOutputPulsUpTest.class,
        ParametrizedOutputPulsDownTest.class,
        ParametrizedOutputDistributionValueTest.class,
        ParametrizedOutputDistributionDelayTest.class,
        ParametrizedOutputBrightnessTest.class
})
public class ConfigurationERPTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
