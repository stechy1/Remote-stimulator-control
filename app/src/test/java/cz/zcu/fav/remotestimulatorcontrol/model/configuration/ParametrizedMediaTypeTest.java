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
                {AConfiguration.MEDIA_LED, false},
                {AConfiguration.MEDIA_AUDIO, false},
                {AConfiguration.MEDIA_IMAGE, false},
                {AConfiguration.MEDIA_LED | AConfiguration.MEDIA_AUDIO, true},
                {AConfiguration.MEDIA_LED | AConfiguration.MEDIA_IMAGE, true},
                {AConfiguration.MEDIA_AUDIO | AConfiguration.MEDIA_IMAGE, true},
                {AConfiguration.MEDIA_LED | AConfiguration.MEDIA_AUDIO | AConfiguration.MEDIA_IMAGE, true},
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
