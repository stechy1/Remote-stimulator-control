package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru BRIGHTNESS třídy {@link ConfigurationCVEP}
 */
@RunWith(Parameterized.class)
public class ParametrizedBrightnessTest {

    private ConfigurationCVEP configuration;

    @Parameterized.Parameters(name = "{index}: Brightness value: {0}, isValid: {1}")
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
        configuration = new ConfigurationCVEP("test");
    }

    @Test
    public void testParametrized() throws Exception {
        configuration.setBrightness(String.valueOf(value));
        assertEquals(valid, configuration.isFlagValid(ConfigurationCVEP.FLAG_BRIGHTNESS));
        assertEquals(valid, configuration.isValid());
    }
    
}
