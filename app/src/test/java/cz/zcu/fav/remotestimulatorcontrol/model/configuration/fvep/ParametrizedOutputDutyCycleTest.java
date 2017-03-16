package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP.*;
import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru DUTY_CYCLE třídy {@link Output}
 */
@RunWith(Parameterized.class)
public class ParametrizedOutputDutyCycleTest {

    private Output output;

    @Parameterized.Parameters(name = "{index}: Output - DutyCycle value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {Output.MIN_DUTY_CYCLE - 1, false},
                {Output.MIN_DUTY_CYCLE, true},
                {Output.MIN_DUTY_CYCLE + 1, true},
                {(Output.MAX_DUTY_CYCLE + Output.MIN_DUTY_CYCLE) / 2, true},
                {Output.MAX_DUTY_CYCLE - 1, true},
                {Output.MAX_DUTY_CYCLE, true},
                {Output.MAX_DUTY_CYCLE + 1, false},
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
        output.setDutyCycle(String.valueOf(value));
        assertEquals(valid, output.isFlagValid(Output.FLAG_DUTY_CYCLE));
        assertEquals(valid, output.isValid());
    }
    
}
