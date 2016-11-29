package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Třída představující médium typu obrázek v konfiguraci
 */
public class MediaImage extends AMedia {

    private boolean thumbnailLoaded = false;

    /**
     * Vytvoří novou specifikaci média typu obrázek
     *
     * @param mediaFile Soubor o obrázkem
     * @param name Název média
     */
    public MediaImage(File mediaFile, String name) {
        super(mediaFile, name);
    }

    @Override
    public Bitmap getThumbnail() {
        if (!thumbnailLoaded) {
            new Loader(mListener).execute(mediaFile.getPath());
        }
        return super.getThumbnail();
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.IMAGE;
    }

    private final Loader.OnLoadedListener mListener = new Loader.OnLoadedListener() {
        @Override
        public void onLoaded(Bitmap bitmap) {
            setThumbnail(bitmap);
            thumbnailLoaded = true;
        }
    };

    private static class Loader extends AsyncTask<String, Void, Bitmap> {

        private final OnLoadedListener listener;

        public Loader(OnLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected Bitmap doInBackground(String... paths) {
            Bitmap fullImage = BitmapFactory.decodeFile(paths[0]);
            return ThumbnailUtils.extractThumbnail(fullImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (listener != null) {
                listener.onLoaded(bitmap);
            }
        }

        interface OnLoadedListener {
            void onLoaded(Bitmap bitmap);
        }
    }


}
