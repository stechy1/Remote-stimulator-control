package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.Test;

import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;

import static junit.framework.Assert.assertEquals;

public class MetaDataTest {

    @Test
    public void setDefaultExtension() throws Exception {
        ExtensionType custom = ExtensionType.CSV;
        MetaData.setDefaultExtension(custom);
        assertEquals("Chyba, metoda vrátila špatnou hodnotu", custom, MetaData.getDefaultExtension());
    }

}