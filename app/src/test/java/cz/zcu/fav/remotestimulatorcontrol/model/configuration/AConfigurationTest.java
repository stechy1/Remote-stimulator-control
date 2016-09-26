package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;

import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationHelper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Testovací třída třídy {@link AConfiguration}
 */
@RunWith(MockitoJUnitRunner.class)
public class AConfigurationTest {

    private static final String DEFAULT_NAME = "test";
    private AConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = ConfigurationHelper.from(DEFAULT_NAME);
    }

    // region NameValidation
    @Test
    public void testGetDefaultNamePositive() throws Exception {
        assertEquals("Chaby: Vrácený název se neshoduje s nastaveným", DEFAULT_NAME, configuration.getName());
    }

    @Test
    public void testNameValidationPositive1() throws Exception {
        assertTrue("Chyba: Výchozí název není validní.", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testNameValidationPositive2() throws Exception {
        String goodName = "Te_St";
        configuration.setName(goodName);

        assertTrue("Chyba: Název nemůže obsahovat _.", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testNameValidationPositive3() throws Exception {
        String goodName = "TeSt1";
        configuration.setName(goodName);

        assertTrue("Chyba: Název nemůže obsahovat čísla.", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testNameValidationPositive4() throws Exception {
        String goodName = "_TeSt";
        configuration.setName(goodName);

        assertTrue("Chyba: Název nemůže obsahovat _ na začátku.", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testNameValidationPositive5() throws Exception {
        String goodName = "velmi_dlouhy_nazev_o_maximalni_v";
        configuration.setName(goodName);

        assertTrue("Chyba: Název nemůže být kratší než 32 znaků.", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testNameValidationNegative1() throws Exception {
        String badName = "1test";
        configuration.setName(badName);

        assertFalse("Chyba: Název může obsahovat číslo [0-9] na začátku.", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertFalse("Chyba: Celá konfigurace je validní", configuration.isValid());
    }

    @Test
    public void testNameValidationNegative2() throws Exception {
        String badName = "te.st";
        configuration.setName(badName);

        assertFalse("Chyba: Název může obsahovat nepovolené znaky. Např.: .,-!?'()[]{}", configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertFalse("Chyba: Celá konfigurace je validní", configuration.isValid());
    }

    @Test
    public void testNameValidationNegative3() throws Exception {
        String badName = "velmi_dlouhy_nazev_prekracujici_maximalni_velikost";
        configuration.setName(badName);

        String errorMessage = String.format(Locale.getDefault(), "Chyba: Název může být delší než %d znaků.", AConfiguration.MAX_NAME_LENGTH);
        assertFalse(errorMessage, configuration.isFlagValid(AConfiguration.FLAG_NAME));
        assertFalse("Chyba: Celá konfigurace je validní", configuration.isValid());
    }
    // endregion

    // region OutputCountValidation
    @Test
    public void testGetDefaultOutputCountPositive() throws Exception {
        assertEquals("Chyba: Vrácený počet výstupů se neshoduje s nastaveným.", AConfiguration.DEF_OUTPUT_COUNT, configuration.getOutputCount());
    }

    @Test
    public void testOutputCountValidationPositive1() throws Exception {
        int goodOutputCount = AConfiguration.MIN_OUTPUT_COUNT;
        configuration.setOutputCount(goodOutputCount);

        assertEquals("Chyba: Počet výstupů se nemůže nastavit na minimální hodnotu.", AConfiguration.MIN_OUTPUT_COUNT, configuration.getOutputCount());
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'output_count' je konfigurace nevalidní", configuration.isFlagValid(AConfiguration.FLAG_OUTPUT_COUNT));
    }

    @Test
    public void testOutputCountValidationPositive2() throws Exception {
        int goodOutputCount = AConfiguration.MIN_OUTPUT_COUNT + 1;
        configuration.setOutputCount(goodOutputCount);

        assertEquals("Chyba: Počet výstupů se nemůže nastavit na minimální hodnotu.", AConfiguration.MIN_OUTPUT_COUNT + 1, configuration.getOutputCount());
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'output_count' je konfigurace nevalidní", configuration.isFlagValid(AConfiguration.FLAG_OUTPUT_COUNT));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testOutputCountValidationPositive3() throws Exception {
        int goodOutputCount = AConfiguration.MAX_OUTPUT_COUNT;
        configuration.setOutputCount(goodOutputCount);

        assertEquals("Chyba: Počet výstupů se nemůže nastavit na maximální hodnotu.", AConfiguration.MAX_OUTPUT_COUNT, configuration.getOutputCount());
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'output_count' je konfigurace nevalidní", configuration.isFlagValid(AConfiguration.FLAG_OUTPUT_COUNT));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testOutputCountValidationPositive4() throws Exception {
        int goodOutputCount = AConfiguration.MAX_OUTPUT_COUNT - 1;
        configuration.setOutputCount(goodOutputCount);

        assertEquals("Chyba: Počet výstupů se nemůže nastavit na maximální hodnotu.", AConfiguration.MAX_OUTPUT_COUNT - 1, configuration.getOutputCount());
        assertTrue("Chyba: Podle validačního příznaku pro parametr 'output_count' je konfigurace nevalidní", configuration.isFlagValid(AConfiguration.FLAG_OUTPUT_COUNT));
        assertTrue("Chyba: Celá konfigurace není validní", configuration.isValid());
    }

    @Test
    public void testOutputCountValidationNegative1() throws Exception {
        int goodOutputCount = AConfiguration.MIN_OUTPUT_COUNT - 1;
        configuration.setOutputCount(goodOutputCount);

        assertEquals("Chyba: Počet výstupů se může nastavit na hodnotu menší než minimální.", AConfiguration.MIN_OUTPUT_COUNT - 1, configuration.getOutputCount());
        assertFalse("Chyba: Podle validačního příznaku pro parametr 'output_count' je konfigurace validní", configuration.isFlagValid(AConfiguration.FLAG_OUTPUT_COUNT));
        assertFalse("Chyba: Celá konfigurace je validní", configuration.isValid());
    }

    @Test
    public void testOutputCountValidationNegative2() throws Exception {
        int goodOutputCount = AConfiguration.MAX_OUTPUT_COUNT + 1;
        configuration.setOutputCount(goodOutputCount);

        assertEquals("Chyba: Počet výstupů se může nastavit na hodnotu větší než maximální.", AConfiguration.MAX_OUTPUT_COUNT + 1, configuration.getOutputCount());
        assertFalse("Chyba: Podle validačního příznaku pro parametr 'output_count' je konfigurace validní", configuration.isFlagValid(AConfiguration.FLAG_OUTPUT_COUNT));
        assertFalse("Chyba: Celá konfigurace je validní", configuration.isValid());
    }
    // endregion

    // region MediaTypeValidation
    @Test
    public void testGetDefaultMediaTypePositive() throws Exception {
        assertEquals("Chyba: Vrácený typ používaného média se neshoduje s výchozím typem.", AConfiguration.DEF_MEDIA_TYPE, MediaType.valueOf(configuration.getMediaType()));
        assertFalse("Chyba: Podle validačního příznaku pro parametr 'mediaType' je konfigurace nevalidní.", AConfiguration.isInvalidMediaCombination(configuration.getMediaType()));
        assertTrue("Chyba: Celá konfigurace není validní.", configuration.isValid());
    }

    @Test
    public void testIsMediaTypePositive1() throws Exception {
        MediaType myMedia = MediaType.LED;
        configuration.setMediaType(myMedia, true);

        assertTrue("Chyba: Nepodařilo se nastavit požadovaný typ media.", configuration.isMediaType(myMedia));
    }

    @Test
    public void testIsMediaTypePositive2() throws Exception {
        MediaType myMedia = MediaType.LED;
        configuration.setMediaType(myMedia);
        configuration.setMediaType(myMedia, false);

        assertFalse("Chyba: Nepodařilo se odebrat typ media.", configuration.isMediaType(myMedia));
    }

    // Zbytek testů je v samostatné třídě

    // endregion

    // region ConfigurationType
    @Test
    public void testGetDefaultConfigurationTypePositive() throws Exception {
        assertEquals("Chyba: Typ konfigurace nemá nastavenou výchozí hodnotu.", AConfiguration.DEF_CONFIGURATION_TYPE, configuration.getConfigurationType());
    }
    // endregion

    // region ConfigurationIsChanged
    @Test
    public void testIsDefaultConfigurationChangedPositive() throws Exception {
        assertFalse("Chyba: Nově vytvořená konfigurace má příznak změněno.", configuration.isChanged());
    }
    // endregion
}