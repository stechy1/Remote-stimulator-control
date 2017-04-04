package cz.zcu.fav.remotestimulatorcontrol;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cz.zcu.fav.remotestimulatorcontrol.ui.MainActivityInstrumentTest;
import cz.zcu.fav.remotestimulatorcontrol.ui.MainFragmentInstrumentTest;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.EspressoConfigurationsTestRunner;

/**
 * Spouštěcí třída jednotlivých testů
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityInstrumentTest.class,
        MainFragmentInstrumentTest.class,
        EspressoConfigurationsTestRunner.class
})
public final class EspressoTestRunner {
    // Zde opravdu nic není
    // Třída je použita jen jako nosič předchozích anotací
}
