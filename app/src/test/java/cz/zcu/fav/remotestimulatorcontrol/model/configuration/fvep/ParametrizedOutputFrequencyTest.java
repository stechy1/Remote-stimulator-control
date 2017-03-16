package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP.Output;
import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru FREQUENCY třídy {@link Output}
 */
@RunWith(Parameterized.class)
public class ParametrizedOutputFrequencyTest {

    private Output output;

    @Parameterized.Parameters(name = "{index}: Output - Frequency value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {Output.MIN_FREQUENCY - 1, false},
                {Output.MIN_FREQUENCY, true},
                {Output.MIN_FREQUENCY + 1, true},
                {(Output.MAX_FREQUENCY + Output.MIN_FREQUENCY) / 2, true},
                {Output.MAX_FREQUENCY - 1, true},
                {Output.MAX_FREQUENCY, true},
                {Output.MAX_FREQUENCY + 1, false},
        });
    }

    @Parameterized.Parameter()
    public double value;
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
        output.setFrequency(String.valueOf(value));
        assertEquals(valid, output.isFlagValid(Output.FLAG_FREQUENCY));
        assertEquals(valid, output.isValid());
    }
    
}
