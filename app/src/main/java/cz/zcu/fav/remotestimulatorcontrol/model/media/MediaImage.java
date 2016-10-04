package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

/**
 * Třída představující médium typu obrázek v konfiguraci
 */
public class MediaImage extends AMedia {

    private Bitmap preview;

    /**
     * Vytvoří novou specifikaci média typu obrázek
     *
     * @param mediaFile Soubor o obrázkem
     * @param name Název média
     */
    public MediaImage(File mediaFile, String name) {
        super(mediaFile, name);
    }

    private void loadPreview() {
        Bitmap fullImage = BitmapFactory.decodeFile(mediaFile.getPath());
        preview = ThumbnailUtils.extractThumbnail(fullImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bitmap getImagePreview() {
        if (preview == null) {
            loadPreview();
        }

        return preview;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.IMAGE;
    }
}
