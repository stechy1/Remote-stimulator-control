package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import cz.zcu.fav.remotestimulatorcontrol.io.XMLHandler;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PATTERN;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PATTERNS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PATTERN_LENGHT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PULS_LENGHT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PULS_SKEW;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Třída představující IO handler pro čtení a zápis XML dat
 */
public class XMLHandlerTVEP extends XMLHandler {

    // region Constants
    public static final String TAG_ROOT = "tvep";
    // endregion

    // region Variables
    // Pracovní konfigurace
    private ConfigurationTVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na XML hodnoty
     *
     * @param configuration {@link ConfigurationTVEP}
     */
    public XMLHandlerTVEP(ConfigurationTVEP configuration) {
        super(configuration);

        this.mConfiguration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Zapíše hodnoty všech patternů
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @throws IOException Pokud něco nevyjde
     */
    private void writePatterns(XmlSerializer serializer) throws IOException {
        serializer.startTag(NAMESPACE, TAG_PATTERNS);

        for (ConfigurationTVEP.Pattern pattern : mConfiguration.patternList) {
            writeTag(serializer, TAG_PATTERN, pattern.getValue());
        }

        serializer.endTag(NAMESPACE, TAG_PATTERNS);
    }
    // endregion

    // region Public methods
    /**
     * {@inheritDoc}
     */
    @Override
    public void read(InputStream inputStream) throws IOException {
        XmlPullParserFactory factory;
        try {
            factory = createFactory();

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, DEFAULT_ENCODING);

            int eventType = parser.getEventType();
            int configID = 0;
            String text = "";
            ConfigurationTVEP.Pattern pattern = new ConfigurationTVEP.Pattern(configID++);

            while (eventType != END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case START_DOCUMENT:
                        break;
                    case START_TAG:
                        if (tagName.equals(TAG_PATTERN)) {
                            pattern = new ConfigurationTVEP.Pattern(configID);
                        }
                        break;
                    case TEXT:
                        text = parser.getText().trim();
                        break;
                    case END_TAG:
                        switch (tagName) {
                            case TAG_OUTPUT_COUNT:
                                mConfiguration.setOutputCount(Integer.valueOf(text));
                                break;
                            case TAG_PATTERN_LENGHT:
                                mConfiguration.setPatternLength(text);
                                break;
                            case TAG_PULS_SKEW:
                                mConfiguration.setTimeBetween(text);
                                break;
                            case TAG_PULS_LENGHT:
                                mConfiguration.setPulsLength(text);
                                break;
                            case TAG_BRIGHTNESS:
                                mConfiguration.setBrightness(text);
                            case TAG_PATTERN:
                                mConfiguration.patternList.add(pattern);
                        }
                        break;
                }

                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        XmlSerializer serializer = createSerializer();
        Writer writer = new OutputStreamWriter(outputStream);

        serializer.setOutput(writer);
        serializer.startDocument(DEFAULT_ENCODING, STAND_ALONE);
        serializer.startTag(NAMESPACE, TAG_ROOT);

        super.writeSelf(serializer);
        writeTag(serializer, TAG_PATTERN_LENGHT, mConfiguration.getPatternLength());
        writeTag(serializer, TAG_PULS_SKEW, mConfiguration.getTimeBetween());
        writeTag(serializer, TAG_PULS_LENGHT, mConfiguration.getPulsLength());
        writeTag(serializer, TAG_BRIGHTNESS, mConfiguration.getBrightness());

        writePatterns(serializer);

        serializer.endTag(NAMESPACE, TAG_ROOT);
        serializer.endDocument();
        writer.close();
    }
    // endregion
}
