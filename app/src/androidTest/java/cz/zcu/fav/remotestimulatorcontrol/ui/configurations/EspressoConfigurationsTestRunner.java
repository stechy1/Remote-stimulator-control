package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity;

/**
 * Spouštěcí třída jednotlivých testů
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigurationsFragmentInstrumentTest.class,
        ConfigurationFactoryActivity.class
})
public final class EspressoConfigurationsTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}