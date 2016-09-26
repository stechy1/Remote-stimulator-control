package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.graphics.Bitmap;

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
    public MediaImage(String name, Bitmap preview) {
        super(name);

        this.preview = preview;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Bitmap getImagePreview() {
        return preview;
    }
}
