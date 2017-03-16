package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru BRIGHTNESS třídy {@link ConfigurationFVEP.Output}
 */
@RunWith(Parameterized.class)
public class ParametrizedOutputBrightnessTest {

    private ConfigurationFVEP.Output output;

    @Parameterized.Parameters(name = "{index}: Output - Brightness value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {AConfiguration.MIN_BRIGHTNESS - 1, false},
                {AConfiguration.MIN_BRIGHTNESS, true},
                {AConfiguration.MIN_BRIGHTNESS + 1, true},
                {(AConfiguration.MAX_BRIGHTNESS + AConfiguration.MIN_BRIGHTNESS) / 2, true},
                {AConfiguration.MAX_BRIGHTNESS - 1, true},
                {AConfiguration.MAX_BRIGHTNESS, true},
                {AConfiguration.MAX_BRIGHTNESS + 1, false},
        });
    }

    @Parameterized.Parameter()
    public int value;
    @Parameterized.Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        ConfigurationFVEP configuration = new ConfigurationFVEP("test");
        configuration.setOutputCount(1);

        output = configuration.outputList.get(0);
    }

    @Test
    public void testParametrized() throws Exception {
        output.setBrightness(String.valueOf(value));
        assertEquals(valid, output.isFlagValid(ConfigurationFVEP.Output.FLAG_BRIGHTNESS));
        assertEquals(valid, output.isValid());
    }
    
}
