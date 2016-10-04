package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Abstraktní třída média konfigurace
 */
public abstract class AMedia extends BaseObservable {

    public static final int THUMBNAIL_SIZE = 75;

    protected final File mediaFile;
    @Bindable
    protected final String name;

    public AMedia(File mediaFile, String name) {
        this.mediaFile = mediaFile;
        this.name = name;
    }

    /**
     * Vrátí obrázek náhledu media
     *
     * @return Obrázek náhledu média
     */
    public abstract Bitmap getImagePreview();

    /**
     * Vrátí typ média
     *
     * @return Typ média
     */
    public abstract MediaType getMediaType();

    /**
     * Vrátí název média
     *
     * @return Název média
     */
    public String getName() {
        return name;
    }

    /**
     * Vrátí soubor, kde se nachází médium
     *
     * @return Soubor
     */
    public File getMediaFile() {
        return mediaFile;
    }
}
