package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_BIT_SHIFT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_MAIN_PATTERN_VALUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_PULSE_LENGHT;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class XMLHandlerCVEP extends XMLHandler {

    // region Constants
    public static final String TAG_ROOT = "cvep";
    // endregion

    // region Variables
    // Pracovní konfigurace
    private ConfigurationCVEP configuration;
    // endregion

    /**
     * Vytvoří nový IO handler se zaměřením na XML hodnoty
     *
     * @param configuration {@link ConfigurationCVEP}
     */
    public XMLHandlerCVEP(ConfigurationCVEP configuration) {
        super(configuration);

        this.configuration = configuration;
    }

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
            String text = "";

            while (eventType != END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case START_DOCUMENT:
                        break;
                    case START_TAG:
                        break;
                    case TEXT:
                        text = parser.getText();
                        break;
                    case END_TAG:
                        switch (tagName) {
                            case TAG_OUTPUT_COUNT:
                                configuration.setOutputCount(Integer.valueOf(text));
                                break;
                            case TAG_MEDIA:
                                configuration.setMediaType(Integer.valueOf(text));
                                break;
                            case TAG_PULSE_LENGHT:
                                configuration.setPulsLength(Integer.valueOf(text));
                                break;
                            case TAG_BIT_SHIFT:
                                configuration.setBitShift(Integer.valueOf(text));
                                break;
                            case TAG_BRIGHTNESS:
                                configuration.setBrightness(Integer.valueOf(text));
                                break;
                            case TAG_MAIN_PATTERN_VALUE:
                                configuration.mainPattern.setValue(Integer.valueOf(text));
                                break;
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
        writeTag(serializer, TAG_PULSE_LENGHT, configuration.getPulsLength());
        writeTag(serializer, TAG_BIT_SHIFT, configuration.getBitShift());
        writeTag(serializer, TAG_BRIGHTNESS, configuration.getBrightness());
        writeTag(serializer, TAG_MAIN_PATTERN_VALUE, configuration.mainPattern.getValue());

        serializer.endTag(NAMESPACE, TAG_ROOT);
        serializer.endDocument();
        writer.close();
    }
    // endregion
}
