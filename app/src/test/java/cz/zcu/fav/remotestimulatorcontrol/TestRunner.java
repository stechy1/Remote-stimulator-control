package cz.zcu.fav.remotestimulatorcontrol;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationTestRunner;

/**
 * Spouštěcí třída jednotlivých testů
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConfigurationTestRunner.class
})
public class TestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
