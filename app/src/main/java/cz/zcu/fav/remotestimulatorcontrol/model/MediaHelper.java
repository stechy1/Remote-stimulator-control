package cz.zcu.fav.remotestimulatorcontrol.model;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.MediaExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaAudio;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaImage;

/**
 * Pomocní knihovní třída pro tvorbu konkrétních medií
 */
public class MediaHelper {

    private MediaHelper() {}

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
