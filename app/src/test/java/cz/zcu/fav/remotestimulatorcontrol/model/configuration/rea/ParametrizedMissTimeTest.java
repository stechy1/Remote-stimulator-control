package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru MISS_TIME třídy {@link ConfigurationREA}
 */
@RunWith(Parameterized.class)
public class ParametrizedMissTimeTest {

    private ConfigurationREA configuration;

    @Parameterized.Parameters(name = "{index}: MissTime value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationREA.MIN_MISS_TIME - 1, false},
                {ConfigurationREA.MIN_MISS_TIME, true},
                {ConfigurationREA.MIN_MISS_TIME + 1, true},
                {(ConfigurationREA.MAX_MISS_TIME + ConfigurationREA.MIN_MISS_TIME) / 2, true},
                {ConfigurationREA.MAX_MISS_TIME - 1, true},
                {ConfigurationREA.MAX_MISS_TIME, true},
                {ConfigurationREA.MAX_MISS_TIME + 1, false},
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
        configuration.setMissTime(value);
        assertEquals(valid, configuration.isFlagValid(ConfigurationREA.FLAG_MISS_TIME));
        assertEquals(valid, configuration.isValid());
    }


}
