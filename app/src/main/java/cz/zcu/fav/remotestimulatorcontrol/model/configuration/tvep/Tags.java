package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

/**
 * Pomocná třída pro standardizaci tagů pro různé typy souborů
 */
final class Tags {

    // region Constants
    public static final String TAG_PATTERNS = "patterns";
    public static final String TAG_PATTERN = "pattern";
    public static final String TAG_PATTERN_VALUE = "pattern_value";
    public static final String TAG_PATTERN_LENGHT = "pattern_lenght";
    public static final String TAG_PULS_SKEW = "puls_skew";
    public static final String TAG_PULS_LENGHT = "puls_lenght";
    public static final String TAG_BRIGHTNESS = "brightness";
    // endregion

    // region Constructors
    /**
     * Privátní konstruktor k zabránění vytvoření instance knihovní třídy
     */
    private Tags() {
        throw new AssertionError();
    }
    // endregion
}
