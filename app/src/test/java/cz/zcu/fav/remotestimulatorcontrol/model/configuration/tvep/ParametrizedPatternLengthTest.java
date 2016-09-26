package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru PATTERN_LENGTH třídy {@link ConfigurationTVEP}
 */
@RunWith(Parameterized.class)
public class ParametrizedPatternLengthTest {

    private ConfigurationTVEP configuration;

    @Parameterized.Parameters(name = "{index}: PatternLength value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {ConfigurationTVEP.MIN_PATTERN_LENGTH- 1, false},
                {ConfigurationTVEP.MIN_PATTERN_LENGTH, true},
                {ConfigurationTVEP.MIN_PATTERN_LENGTH + 1, true},
                {(ConfigurationTVEP.MAX_PATTERN_LENGTH + ConfigurationTVEP.MIN_PATTERN_LENGTH) / 2, true},
                {ConfigurationTVEP.MAX_PATTERN_LENGTH - 1, true},
                {ConfigurationTVEP.MAX_PATTERN_LENGTH, true},
                {ConfigurationTVEP.MAX_PATTERN_LENGTH + 1, false},
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
        configuration.setPatternLength(value);
        assertEquals(valid, configuration.isFlagValid(ConfigurationTVEP.FLAG_PATTERN_LENGTH));
        assertEquals(valid, configuration.isValid());
    }
    
}
