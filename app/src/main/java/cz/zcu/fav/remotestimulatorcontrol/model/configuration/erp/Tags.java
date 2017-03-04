package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

/**
 * Pomocná třída pro standardizaci tagů pro různé typy souborů
 */
final class Tags {

    // region Constants
    public static final String TAG_OUT = "out";
    public static final String TAG_WAIT = "wait";
    public static final String TAG_EDGE = "edge";
    public static final String TAG_RANDOM = "random";
    public static final String TAG_OUTPUTS = "outputs";
    public static final String TAG_OUTPUT = "output";
    public static final String TAG_PULS_UP = "puls_up";
    public static final String TAG_PULS_DOWN = "puls_down";
    public static final String TAG_DISTRIBUTION_VALUE = "distribution_value";
    public static final String TAG_DISTRIBUTION_DELAY = "distribution_delay";
    public static final String TAG_BRIGHTNESS = "brightness";
    public static final String TAG_MEDIA_OUTPUT = "output_media";
    public static final String TAG_MEDIA_OUTPUT_NAME = "media_name";
    public static final String TAG_MEDIA_OUTPUT_TYPE = "media_type";
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
