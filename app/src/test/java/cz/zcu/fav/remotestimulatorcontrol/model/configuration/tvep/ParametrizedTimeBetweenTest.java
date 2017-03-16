package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru TIME_BETWEEN třídy {@link ConfigurationTVEP}
 */
@RunWith(Parameterized.class)
public class ParametrizedTimeBetweenTest {

    private ConfigurationTVEP configuration;

    @Parameterized.Parameters(name = "{index}: TimeBetween value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationTVEP.MIN_TIME_BETWEEN- 1, false},
                {ConfigurationTVEP.MIN_TIME_BETWEEN, true},
                {ConfigurationTVEP.MIN_TIME_BETWEEN + 1, true},
                {(ConfigurationTVEP.MAX_TIME_BETWEEN + ConfigurationTVEP.MIN_TIME_BETWEEN) / 2, true},
                {ConfigurationTVEP.MAX_TIME_BETWEEN - 1, true},
                {ConfigurationTVEP.MAX_TIME_BETWEEN, true},
                {ConfigurationTVEP.MAX_TIME_BETWEEN + 1, false},
        });
    }

    @Parameterized.Parameter()
    public int value;
    @Parameterized.Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationTVEP("test");
    }

    @Test
    public void testParametrized() throws Exception {
        configuration.setTimeBetween(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationTVEP.FLAG_TIME_BETWEEN));
        assertEquals(valid, configuration.isValid());
    }
    
}
