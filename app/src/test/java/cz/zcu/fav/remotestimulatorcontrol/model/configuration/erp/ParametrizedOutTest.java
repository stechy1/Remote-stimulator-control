package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru OUT třídy {@link ConfigurationERP}
 */
@RunWith(Parameterized.class)
public class ParametrizedOutTest {

    private ConfigurationERP configuration;

    @Parameters(name = "{index}: Out value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationERP.MIN_OUT - 1, false},
                {ConfigurationERP.MIN_OUT, true},
                {ConfigurationERP.MIN_OUT + 1, true},
                {(ConfigurationERP.MAX_OUT + ConfigurationERP.MIN_OUT) / 2, true},
                {ConfigurationERP.MAX_OUT - 1, true},
                {ConfigurationERP.MAX_OUT, true},
                {ConfigurationERP.MAX_OUT + 1, false},
        });
    }

    @Parameter()
    public int value;
    @Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationERP("test");
    }

    @Test
    public void testParametrized() throws Exception {
        configuration.setOut(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationERP.FLAG_OUT));
        assertEquals(valid, configuration.isValid());
    }
}
