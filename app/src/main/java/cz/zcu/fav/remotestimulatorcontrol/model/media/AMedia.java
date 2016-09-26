package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;

/**
 * Abstraktní třída média konfigurace
 */
public abstract class AMedia extends BaseObservable {

    @Bindable
    protected String name;

    public AMedia(String name) {
        this.name = name;
    }

    /**
     * Vrátí obrázek náhledu media
     *
     * @return Obrázek náhledu média
     */
    abstract Bitmap getImagePreview();

    /**
     * Vrátí název média
     *
     * @return Název média
     */
    public String getName() {
        return name;
    }

    /**
     * Nastaví nový název média
     *
     * @param name Nový název média
     */
    public void setName(String name) {
        this.name = name;
    }
}
