package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

/**
 * Pomocná třída pro standardizaci tagů pro různé typy souborů
 */
final class Tags {

    // region Constants
    public static final String TAG_PULSE_LENGHT = "pulse_lenght";
    public static final String TAG_BIT_SHIFT = "bit_shift";
    public static final String TAG_BRIGHTNESS = "brightness";
    public static final String TAG_MAIN_PATTERN_VALUE = "main_pattern_value";
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
