package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Spouštěcí třída jednotlivých testů
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigurationsFragmentInstrumentTest.class,
        ConfigurationFractoryInstrumentTest.class,
        ConfigurationRenameInstrumentTest.class,
        ConfigurationsFragmentParametrizedTest.class
})
public final class EspressoConfigurationsTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}