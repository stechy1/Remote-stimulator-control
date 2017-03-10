package cz.zcu.fav.remotestimulatorcontrol.io;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

/**
 * Abstraktní třída pro uložení základních dat konfigurací
 */
public abstract class XMLHandler extends GenericXMLHandler {

    // region Constants
    protected static final String TAG_OUTPUT_COUNT = "output_count";
    protected static final String TAG_MEDIA = "media";
    // endregion

    // region Variables
    private final AConfiguration mConfiguration;
    // endregion

    // region Constructors

    /**
     * Konstruktor třídy XML handler
     *
     * @param configuration Konfigurace
     */
    public XMLHandler(AConfiguration configuration) {
        mConfiguration = configuration;
    }
    // endregion

    // region Public methods

    /**
     * Zapíše základní parametry konfigurace na výstup
     *
     * @param serializer XmlSerializer
     * @throws IOException
     */
    protected void writeSelf(XmlSerializer serializer) throws IOException {
        writeTag(serializer, TAG_OUTPUT_COUNT, mConfiguration.getOutputCount());
        writeTag(serializer, TAG_MEDIA, mConfiguration.getMediaType());
    }
    // endregion

}
