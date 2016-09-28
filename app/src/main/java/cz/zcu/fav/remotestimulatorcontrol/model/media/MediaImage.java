package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
     * @param name Název média
     * @param preview Náhledový obrázek
     */
    public MediaImage(File mediaFile, String name) {
        super(mediaFile, name);
    }

    private void loadPreview() {
        preview = BitmapFactory.decodeFile(mediaFile.getPath());
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
