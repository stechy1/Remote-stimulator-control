package cz.zcu.fav.remotestimulatorcontrol.model;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

public class ConfigurationManagerTest {

    private static File workingDirectory;

    private ConfigurationManager manager;

    @BeforeClass
    public static void init() throws Exception {
        workingDirectory = new File("tmp");
    }

    @Before
    public void setUp() throws Exception {
        manager = new ConfigurationManager(workingDirectory);
    }

    @AfterClass
    public static void clear() throws Exception {
        workingDirectory.delete();
    }

    @Test
    public void test() throws Exception {
        manager.add(new AConfiguration("test") {
            @Override
            public IOHandler getHandler() {
                return null;
            }

            @Override
            public AConfiguration duplicate(String newName) {
                return null;
            }
        });

    }
}
