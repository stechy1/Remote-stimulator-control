package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída výčtového typu {@link ConfigurationType}
 */
public class ConfigurationTypeTest {

    @Test
    public void testValueOfPositive() throws Exception {
        ConfigurationType erp = ConfigurationType.ERP;
        int index = erp.ordinal();
        assertEquals("Chyba: Vrácený typ konfigurace neodpovídá ordinálnímu číslu.", erp, ConfigurationType.valueOf(index));
    }

}