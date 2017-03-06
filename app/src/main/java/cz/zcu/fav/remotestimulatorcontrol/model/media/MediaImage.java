package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Parcel;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Třída představující médium typu obrázek v konfiguraci
 */
public class MediaImage extends AMedia {

    // region Variables
    private boolean mThumbnailLoaded = false;

    private final Loader.OnLoadedListener mListener = new Loader.OnLoadedListener() {
        @Override
        public void onLoaded(Bitmap bitmap) {
            setThumbnail(bitmap);
            mThumbnailLoaded = true;
        }
    };
    // endregion

    // region Constructors
    /**
     * Vytvoří novou specifikaci média typu obrázek
     *
     * @param mediaFile Soubor o obrázkem
     * @param name Název média
     */
    public MediaImage(File mediaFile, String name) {
        super(mediaFile, name);
    }

    // endregion

    @Override
    public Bitmap getThumbnail() {
        if (!mThumbnailLoaded) {
            new Loader(mListener).execute(mMediaFile.getPath());
        }
        return super.getThumbnail();
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.IMAGE;
    }

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


    @Override
    public int describeContents() {
        return 0;
    }


    public MediaImage(Parcel in) {
        super(in);
    }

    public static final Creator<MediaImage> CREATOR = new Creator<MediaImage>() {
        @Override
        public MediaImage createFromParcel(Parcel source) {
            return new MediaImage(source);
        }

        @Override
        public MediaImage[] newArray(int size) {
            return new MediaImage[size];
        }
    };
}
