package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru WAIT_RANDOM třídy {@link ConfigurationREA}
 */
@RunWith(Parameterized.class)
public class ParametrizedWaitRandomTest {

    private ConfigurationREA configuration;

    @Parameterized.Parameters(name = "{index}: WaitRandom value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationREA.MIN_WAIT_RANDOM - 1, false},
                {ConfigurationREA.MIN_WAIT_RANDOM, true},
                {ConfigurationREA.MIN_WAIT_RANDOM + 1, true},
                {(ConfigurationREA.MAX_WAIT_RANDOM + ConfigurationREA.MIN_WAIT_RANDOM) / 2, true},
                {ConfigurationREA.MAX_WAIT_RANDOM - 1, true},
                {ConfigurationREA.MAX_WAIT_RANDOM, true},
                {ConfigurationREA.MAX_WAIT_RANDOM + 1, false},
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
        configuration.setWaitRandom(value);
        assertEquals(valid, configuration.isFlagValid(ConfigurationREA.FLAG_WAIT_RANDOM));
        assertEquals(valid, configuration.isValid());
    }

}
