package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;

public class MediaAudio extends AMedia {


    public MediaAudio(String name) {
        super(name);
    }

    @Override
    Bitmap getImagePreview() {
        return null;
    }
}
