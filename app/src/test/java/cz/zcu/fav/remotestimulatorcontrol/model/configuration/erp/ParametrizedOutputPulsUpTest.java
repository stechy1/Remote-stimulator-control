package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP.Output;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru PULS_UP třídy {@link Output}
 */
@RunWith(Parameterized.class)
public class ParametrizedOutputPulsUpTest {

    private Output output;

    @Parameterized.Parameters(name = "{index}: Output - PulsUp value: {0}, isValid: {1}")
    public static Collection<Object[]> pulsUpValues() {
        return Arrays.asList(new Object[][]{
                {(Output.MIN_PULS_UP - 1), false},
                {Output.MIN_PULS_UP, true},
                {Output.MIN_PULS_UP + 1, true},
                {(Output.MAX_PULS_UP + Output.MIN_PULS_UP) / 2, true},
                {Output.MAX_PULS_UP - 1, true},
                {Output.MAX_PULS_UP, true},
                {Output.MAX_PULS_UP + 1, false},
        });
    }

    @Parameterized.Parameter()
    public int value;
    @Parameterized.Parameter(1)
    public boolean valid;

    @Before
    public void setUp() throws Exception {
        ConfigurationERP configuration = new ConfigurationERP("test");
        configuration.setOutputCount(1);

        output = configuration.outputList.get(0);
    }

    @Test
    public void testParametrized() throws Exception {
        output.setPulsUp(String.valueOf(value));
        assertEquals(valid, output.isFlagValid(Output.FLAG_PULS_UP));
        assertEquals(valid, output.isValid());
    }

}
