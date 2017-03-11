package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Třída představující konfiguraci jednoho výstupu
 */
public class OutputConfiguration extends BaseObservable {

    // region Constants
    public static final int THUMBNAIL_SIZE = 75;
    private static final String TAG = "OutputConfiguration";
    // endregion

    // region Variables

    private File mMediaFile;
    @Bindable
    private String fileName = "";
    @Bindable
    private MediaType mediaType = MediaType.LED;
    @Bindable
    protected Bitmap thumbnail;
    // Přiznak pro audio médium, zda-li se má zobrazit přehrávací ikona
    @Bindable
    protected boolean showPlayingIcon = false;

    private final Loader.OnLoadedListener mListener = new Loader.OnLoadedListener() {
        @Override
        public void onLoaded(Bitmap bitmap) {
            setThumbnail(bitmap);
        }
    };

    // endregion

    // region Constructors

    public OutputConfiguration() {
        this(null);
    }

    public OutputConfiguration(File file) {
        mMediaFile = file;
    }

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
        notifyPropertyChanged(BR.fileName);
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        Log.d(TAG, mediaType.name());
        this.mediaType = mediaType;
        notifyPropertyChanged(BR.mediaType);
        setThumbnail(null);
        setMediaFile(null);
    }

    /**
     * Vrátí ikonu média
     *
     * @return Ikonu média
     */
    public Bitmap getThumbnail() {
        if (thumbnail == null) {
            if (mMediaFile != null && mediaType == MediaType.IMAGE) {
                new Loader(mListener).execute(mMediaFile.getPath());
            } else {
                return null;
            }
        }

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

    public File getMediaFile() {
        return mMediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mMediaFile = mediaFile;
        setFileName(mediaFile != null ? mediaFile.getName() : "");
    }

    // endregion

    private static class Loader extends AsyncTask<String, Void, Bitmap> {
        private final OnLoadedListener mmListener;

        public Loader(OnLoadedListener listener) {
            mmListener = listener;
        }

        @Override
        protected Bitmap doInBackground(String... paths) {
            Bitmap fullImage = BitmapFactory.decodeFile(paths[0]);
            return ThumbnailUtils.extractThumbnail(fullImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mmListener != null) {
                mmListener.onLoaded(bitmap);
            }
        }

        interface OnLoadedListener {
            void onLoaded(Bitmap bitmap);
        }
    }

}
