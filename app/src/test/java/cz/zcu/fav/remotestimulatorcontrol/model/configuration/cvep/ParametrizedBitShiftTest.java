package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru BIT_SHIFT třídy {@link ConfigurationCVEP}
 */
@RunWith(Parameterized.class)
public class ParametrizedBitShiftTest {

    private ConfigurationCVEP configuration;

    @Parameterized.Parameters(name = "{index}: BitShift value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationCVEP.MIN_BIT_SHIFT- 1, false},
                {ConfigurationCVEP.MIN_BIT_SHIFT, true},
                {ConfigurationCVEP.MIN_BIT_SHIFT + 1, true},
                {(ConfigurationCVEP.MAX_BIT_SHIFT + ConfigurationCVEP.MIN_BIT_SHIFT) / 2, true},
                {ConfigurationCVEP.MAX_BIT_SHIFT - 1, true},
                {ConfigurationCVEP.MAX_BIT_SHIFT, true},
                {ConfigurationCVEP.MAX_BIT_SHIFT + 1, false},
        });
    }

    @Parameterized.Parameter()
    public int value;
    @Parameterized.Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationCVEP("test");
    }

    @Test
    public void testParametrized() throws Exception {
        configuration.setBitShift(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationCVEP.FLAG_BIT_SHIFT));
        assertEquals(valid, configuration.isValid());
    }
    
}
