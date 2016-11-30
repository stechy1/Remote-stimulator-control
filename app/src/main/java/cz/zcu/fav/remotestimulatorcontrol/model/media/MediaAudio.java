package cz.zcu.fav.remotestimulatorcontrol.model.media;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

public class MediaAudio extends AMedia {

    // region Variables
    private boolean playing = false;
    // endregion

    // region Constructors
    /**
     * Vytvoří konkrétní implementaci třídy {@link AMedia}
     *
     * @param mediaFile Soubor s médiem
     * @param name Název média
     */
    public MediaAudio(File mediaFile, String name) {
        super(mediaFile, name);
    }
    // endregion

    @Override
    public MediaType getMediaType() {
        return MediaType.AUDIO;
    }

    // region Public methods
    /**
     * Zjistí, zda-li se přehrává zvuk, či nikoliv
     *
     * @return True, pokud se zvuk přehrává, jinak false
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Nastaví, zda-li se zvuk přehrává, či nikoliv
     *
     * @param playing True, pokud se zvuk přehrává, jinak false
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
        setShowPlayingIcon(playing);
    }
    // endregion
}
