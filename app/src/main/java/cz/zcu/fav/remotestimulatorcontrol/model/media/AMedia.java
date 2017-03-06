package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Abstraktní třída média konfigurace
 */
public abstract class AMedia extends BaseObservable implements Parcelable {

    // region Constants
    public static final int THUMBNAIL_SIZE = 75;
    // endregion

    // region Variables
    // Soubor s médiem
    protected File mMediaFile;
    // Název média
    @Bindable
    protected String mName;
    // Thumbnail
    @Bindable
    protected Bitmap thumbnail;
    // Přiznak pro audio médium, zda-li se má zobrazit přehrávací ikona
    @Bindable
    protected boolean showPlayingIcon = false;
    // endregion

    // region Constructors
    /**
     * Konstruktor třídy {@link AMedia}
     *
     * @param mediaFile Soubor s médiem
     * @param name Název média
     */
    public AMedia(File mediaFile, String name) {
        mMediaFile = mediaFile;
        mName = name;
    }
    // endregion

    /**
     * Vrátí typ média
     *
     * @return Typ média
     */
    public abstract MediaType getMediaType();

    // region Public methods
    /**
     * Vrátí ikonu média
     *
     * @return Ikonu média
     */
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    /**
     * Nastaví ikonu média
     *
     * @param thumbnail Ikona
     */
    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
        notifyPropertyChanged(BR.thumbnail);
    }

    /**
     * Vrátí název média
     *
     * @return Název média
     */
    public String getName() {
        return mName;
    }

    /**
     * Vrátí soubor, kde se nachází médium
     *
     * @return Soubor
     */
    public File getMediaFile() {
        return mMediaFile;
    }

    /**
     * Zjistí, zda-li se má zobrazit ikona indikující přehrávání
     *
     * @return True, pokud se má ikona zobrazit, jinak false
     */
    public boolean isShowPlayingIcon() {
        return showPlayingIcon;
    }

    /**
     * Nastaví, zda-li se má zobrazit ikona indikující přehrávání
     *
     * @param showPlayingIcon True, pro zobrazení, jinak false
     */
    public void setShowPlayingIcon(boolean showPlayingIcon) {
        this.showPlayingIcon = showPlayingIcon;
        notifyPropertyChanged(BR.showPlayingIcon);
    }
    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AMedia aMedia = (AMedia) o;

        return mName.equals(aMedia.mName);

    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMediaFile.getAbsolutePath());
        dest.writeString(this.mName);
    }

    protected AMedia(Parcel in) {
        this.mMediaFile = new File(in.readString());
        this.mName = in.readString();
    }
}
