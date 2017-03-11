package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Třída představující konfiguraci jednoho výstupu
 */
public class OutputConfiguration extends BaseObservable {

    // region Variables

    @Bindable
    private String fileName = "";
    @Bindable
    private MediaType mediaType = MediaType.LED;

    // endregion

    // region Constructors

    // endregion

    // region Private methods

    // endregion

    // region Public methods

    // endregion

    // region Getters & Setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }


    // endregion

}
