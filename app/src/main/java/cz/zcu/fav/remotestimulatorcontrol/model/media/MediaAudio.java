package cz.zcu.fav.remotestimulatorcontrol.model.media;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

public class MediaAudio extends AMedia {

    private boolean playing = false;

    public MediaAudio(File mediaFile, String name) {
        super(mediaFile, name);
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.AUDIO;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        setShowPlayingIcon(playing);
    }
}
