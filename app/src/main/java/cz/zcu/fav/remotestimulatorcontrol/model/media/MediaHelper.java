package cz.zcu.fav.remotestimulatorcontrol.model.media;

import java.io.File;

/**
 * Pomocná knihovní třída pro tvorbu konkrétních medií
 */
public final class MediaHelper {

    // region Constructors
    /**
     * Privátní konstruktor k zabránění vytvoření instance knihovní třídy
     */
    private MediaHelper() {
        throw new AssertionError();
    }
    // endregion

    /**
     * Vrátí konkrétní implementaci media, pokud to bude možné, jinak null
     *
     * @param mediaFile Soubor s médiem
     * @param name Název média
     * @param extensionType Typ souboru
     * @return Konkrétní implementaci {@link AMedia}
     */
    public static AMedia from(File mediaFile, String name, MediaExtensionType extensionType) {
        if (extensionType == null) {
            return null;
        }

        switch (extensionType) {
            case PNG:
            case JPEG:
            case JPG:
            case GIF:
                return new MediaImage(mediaFile, name);
            case MP3:
                return new MediaAudio(mediaFile, name);
            default:
                return null;
        }
    }

}
