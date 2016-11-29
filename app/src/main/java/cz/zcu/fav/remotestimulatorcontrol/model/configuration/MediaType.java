package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

/**
 * Výčtový typ typů média v konfiguracích
 */
public enum MediaType {
    LED(1) , AUDIO(2), IMAGE(4);

    private int ordinal;

    MediaType(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public static MediaType valueOf(int value) {
        return values()[value - 1];
    }
}
