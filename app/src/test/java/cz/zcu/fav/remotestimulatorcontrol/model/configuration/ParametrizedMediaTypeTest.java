package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Testovací třída provádějící parametrizovaný test pro třídu typy médií z {@link AConfiguration}
 */
@RunWith(Parameterized.class)
public class ParametrizedMediaTypeTest {

    @Parameters(name = "{index}: Media value: {0}, isInvalidCombination: {1}")
    public static Collection<Object[]> mediaTypes() {
        return Arrays.asList(new Object[][]{
                {MediaType.LED.getOrdinal(), false},
                {MediaType.AUDIO.getOrdinal(), false},
                {MediaType.IMAGE.getOrdinal(), false},
                {MediaType.LED.getOrdinal() | MediaType.AUDIO.getOrdinal(), true},
                {MediaType.LED.getOrdinal() | MediaType.IMAGE.getOrdinal(), true},
                {MediaType.AUDIO.getOrdinal() | MediaType.IMAGE.getOrdinal(), true},
                {MediaType.LED.getOrdinal() | MediaType.AUDIO.getOrdinal() | MediaType.IMAGE.getOrdinal(), true},
        });
    }

    @Parameter()
    public int mediaType;
    @Parameter(1)
    public boolean valid; // False pro nevalidní kombinaci, true pro validní

    @Test
    public void testMediaTypeParametrized() throws Exception {
        assertEquals(valid, AConfiguration.isInvalidMediaCombination(mediaType));

    }
}
