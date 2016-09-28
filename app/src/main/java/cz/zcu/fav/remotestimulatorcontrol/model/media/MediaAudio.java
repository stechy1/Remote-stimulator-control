package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

public class MediaAudio extends AMedia {

    public MediaAudio(File mediaFile, String name) {
        super(mediaFile, name);
    }

    @Override
    public Bitmap getImagePreview() {
        return null;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.AUDIO;
    }
}
