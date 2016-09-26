package cz.zcu.fav.remotestimulatorcontrol.io;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

/**
 * Abstraktní třída pro uložení základních dat konfigurací
 */
public abstract class XMLHandler implements IOHandler {

    // region Constants
    // Výchozí očekávané kódování XML souboru
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final boolean STAND_ALONE = true;
    // Namespace
    public static final String NAMESPACE = "";
    protected static final String TAG_OUTPUT_COUNT = "output_count";
    protected static final String TAG_MEDIA = "media";
    // endregion

    // region Variables
    private static boolean _minify = false;
    private final AConfiguration configuration;
    // endregion

    // region Static methods
    /**
     * Nastaví minifikaci XML dokumentu
     *
     * @param minify True, pokud se má xml dokument minifikovat, jinak false
     */
    public static void setMinify(boolean minify) {
        _minify = minify;
    }
    // endregion

    // region Constructors

    /**
     * Konstruktor třídy XML handler
     *
     * @param configuration Konfigurace
     */
    public XMLHandler(AConfiguration configuration) {
        this.configuration = configuration;
    }
    // endregion

    // region Public methods
    /**
     * Vytvoří nový XML serializer s automatickým zalamováním řádků
     *
     * @return {@link XmlSerializer}
     */
    protected XmlSerializer createSerializer() {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", !_minify);

        return serializer;
    }

    /**
     * Vytvoří novou XmlPullParserFactory s podporou namespacu
     *
     * @return {@link XmlPullParserFactory}
     * @throws XmlPullParserException
     */
    protected XmlPullParserFactory createFactory() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        return factory;
    }

    /**
     * Vytvoří a zapíše nový XML tag
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @param tag Tag
     * @param value Hodnota
     * @throws IOException Pokud něco nevyjde
     */
    protected void writeTag(XmlSerializer serializer, String tag, int value) throws IOException {
        writeTag(serializer, tag, String.valueOf(value));
    }

    /**
     * Vytvoří a zapíše nový XML tag
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @param tag Tag
     * @param value Hodnota
     * @throws IOException Pokud něco nevyjde
     */
    protected void writeTag(XmlSerializer serializer, String tag, double value) throws IOException {
        writeTag(serializer, tag, String.valueOf(value));
    }

    /**
     * Vytvoří a zapíše nový XML tag
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @param tag Tag
     * @param value Hodnota
     * @throws IOException Pokud něco nevyjde
     */
    protected void writeTag(XmlSerializer serializer, String tag, String value) throws IOException {
        writeTag(serializer, NAMESPACE, tag, value);
    }

    /**
     * Vytvoří a zapíše nový XML tag
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @param namespace Namespace
     * @param tag Tag
     * @param value Hodnota
     * @throws IOException Pokud něco nevyjde
     */
    protected void writeTag(XmlSerializer serializer, String namespace, String tag, String value) throws IOException {
        serializer.startTag(namespace, tag);
        serializer.text(value);
        serializer.endTag(namespace, tag);
    }

    /**
     * Zapíše základní parametry konfigurace na výstup
     *
     * @param serializer XmlSerializer
     * @throws IOException
     */
    protected void writeSelf(XmlSerializer serializer) throws IOException {
        writeTag(serializer, TAG_OUTPUT_COUNT, configuration.getOutputCount());
        writeTag(serializer, TAG_MEDIA, configuration.getMediaType());
    }
    // endregion

}
