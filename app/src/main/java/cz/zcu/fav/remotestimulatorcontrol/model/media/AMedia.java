package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Abstraktní třída média konfigurace
 */
public abstract class AMedia extends BaseObservable {

    public static final int THUMBNAIL_SIZE = 75;

    protected final File mediaFile;
    @Bindable
    protected final String name;
    @Bindable
    protected Bitmap thumbnail;
    @Bindable
    protected boolean showPlayingIcon = false;

    public AMedia(File mediaFile, String name) {
        this.mediaFile = mediaFile;
        this.name = name;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
        notifyPropertyChanged(BR.thumbnail);
    }

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

    public boolean isShowPlayingIcon() {
        return showPlayingIcon;
    }

    public void setShowPlayingIcon(boolean showPlayingIcon) {
        this.showPlayingIcon = showPlayingIcon;
        notifyPropertyChanged(BR.showPlayingIcon);
    }
}
