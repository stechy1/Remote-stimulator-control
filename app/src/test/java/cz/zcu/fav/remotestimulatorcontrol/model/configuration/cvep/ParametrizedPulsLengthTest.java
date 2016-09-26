package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru PULS_LENGTH třídy {@link ConfigurationCVEP}
 */
@RunWith(Parameterized.class)
public class ParametrizedPulsLengthTest {

    private ConfigurationCVEP configuration;

    @Parameterized.Parameters(name = "{index}: PulsLength value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationCVEP.MIN_PULS_LENGTH- 1, false},
                {ConfigurationCVEP.MIN_PULS_LENGTH, true},
                {ConfigurationCVEP.MIN_PULS_LENGTH + 1, true},
                {(ConfigurationCVEP.MAX_PULS_LENGTH + ConfigurationCVEP.MIN_PULS_LENGTH) / 2, true},
                {ConfigurationCVEP.MAX_PULS_LENGTH - 1, true},
                {ConfigurationCVEP.MAX_PULS_LENGTH, true},
                {ConfigurationCVEP.MAX_PULS_LENGTH + 1, false},
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
        configuration.setPulsLength(value);
        assertEquals(valid, configuration.isFlagValid(ConfigurationCVEP.FLAG_PULS_LENGTH));
        assertEquals(valid, configuration.isValid());
    }
    
}
