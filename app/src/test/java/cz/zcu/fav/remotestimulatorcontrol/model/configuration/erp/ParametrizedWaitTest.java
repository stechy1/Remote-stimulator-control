package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru OUT třídy {@link ConfigurationERP}
 */
@RunWith(Parameterized.class)
public class ParametrizedWaitTest {

    private ConfigurationERP configuration;

    @Parameterized.Parameters(name = "{index}: Wait value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationERP.MIN_WAIT - 1, false},
                {ConfigurationERP.MIN_WAIT, true},
                {ConfigurationERP.MIN_WAIT + 1, true},
                {(ConfigurationERP.MAX_WAIT + ConfigurationERP.MIN_WAIT) / 2, true},
                {ConfigurationERP.MAX_WAIT - 1, true},
                {ConfigurationERP.MAX_WAIT, true},
                {ConfigurationERP.MAX_WAIT + 1, false},
        });
    }

    @Parameterized.Parameter()
    public int value;
    @Parameterized.Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationERP("test");
    }

    @Test
    public void testParametrized() throws Exception {
        configuration.setWait(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationERP.FLAG_WAIT));
        assertEquals(valid, configuration.isValid());
    }
}
