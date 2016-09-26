package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationHelper;

/**
 * Otestováni konfigurace s nespecifikovaným typem
 */
public class UndefinedConfigurationTest {

    private static final String DEFAULT_NAME = "test";

    private AConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = ConfigurationHelper.from(DEFAULT_NAME);
    }

    // region ConfigurationDuplicate
    @Test(expected = UnsupportedOperationException.class)
    public void testConfigurationDuplicate() throws Exception {
        String name = "duplicated";
        configuration.duplicate(name);
    }
    // endregion

    // region HandlerCreation
    @Test(expected = UnsupportedOperationException.class)
    public void testGetHandler() throws Exception {
        configuration.getHandler();
    }
    // endregion
}
