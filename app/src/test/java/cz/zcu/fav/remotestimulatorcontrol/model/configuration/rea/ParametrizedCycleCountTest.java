package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru CYCLE_COUNT třídy {@link ConfigurationREA}
 */
@RunWith(Parameterized.class)
public class ParametrizedCycleCountTest {

    private ConfigurationREA configuration;

    @Parameterized.Parameters(name = "{index}: CycleCount value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationREA.MIN_CYCLE_COUNT - 1, false},
                {ConfigurationREA.MIN_CYCLE_COUNT, true},
                {ConfigurationREA.MIN_CYCLE_COUNT + 1, true},
                {(ConfigurationREA.MAX_CYCLE_COUNT + ConfigurationREA.MIN_CYCLE_COUNT) / 2, true},
                {ConfigurationREA.MAX_CYCLE_COUNT - 1, true},
                {ConfigurationREA.MAX_CYCLE_COUNT, true},
                {ConfigurationREA.MAX_CYCLE_COUNT + 1, false},
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
        configuration.setCycleCount(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationREA.FLAG_CYCLE_COUNT));
        assertEquals(valid, configuration.isValid());
    }
    
}
