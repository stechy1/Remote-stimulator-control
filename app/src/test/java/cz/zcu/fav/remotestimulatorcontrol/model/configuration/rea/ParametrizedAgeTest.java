package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru AGE třídy {@link ConfigurationREA}
 */
@RunWith(Parameterized.class)
public class ParametrizedAgeTest {

    private ConfigurationREA configuration;

    @Parameterized.Parameters(name = "{index}: Age value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationREA.MIN_AGE - 1, false},
                {ConfigurationREA.MIN_AGE, true},
                {ConfigurationREA.MIN_AGE + 1, true},
                {(ConfigurationREA.MAX_AGE + ConfigurationREA.MIN_AGE) / 2, true},
                {ConfigurationREA.MAX_AGE - 1, true},
                {ConfigurationREA.MAX_AGE, true},
                {ConfigurationREA.MAX_AGE + 1, false},
        });
    }

    @Parameterized.Parameter()
    public int value;
    @Parameterized.Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationREA("test");
    }

    @Test
    public void testParametrized() throws Exception {
        configuration.setAge(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationREA.FLAG_AGE));
        assertEquals(valid, configuration.isValid());
    }

}
