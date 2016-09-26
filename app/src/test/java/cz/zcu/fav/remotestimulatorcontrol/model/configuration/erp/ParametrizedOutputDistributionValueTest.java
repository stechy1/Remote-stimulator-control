package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP.*;
import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro hodnoty parametru DISTRIBUTION_VALUE třídy {@link Output}
 */
@RunWith(Parameterized.class)
public class ParametrizedOutputDistributionValueTest {

    private Output output;

    @Parameterized.Parameters(name = "{index}: Output - DistributionValue value: {0}, isValid: {1}")
    public static Collection<Object[]> outValues() {
        return Arrays.asList(new Object[][]{
                {Output.MIN_DISTRIBUTION_VALUE - 1, false},
                {Output.MIN_DISTRIBUTION_VALUE, true},
                {Output.MIN_DISTRIBUTION_VALUE + 1, true},
                {(Output.MAX_DISTRIBUTION_VALUE + Output.MIN_DISTRIBUTION_VALUE) / 2, true},
                {Output.MAX_DISTRIBUTION_VALUE - 1, true},
                {Output.MAX_DISTRIBUTION_VALUE, true},
                {Output.MAX_DISTRIBUTION_VALUE + 1, false},
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
        output.setDistributionValue(value);
        assertEquals(valid, output.isFlagValid(Output.FLAG_DISTRIBUTION_VALUE));
        assertEquals(valid, output.isValid());
    }
    
}
