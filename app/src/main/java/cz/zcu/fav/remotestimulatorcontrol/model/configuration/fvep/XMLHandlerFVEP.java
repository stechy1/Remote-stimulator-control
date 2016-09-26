package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_DUTY_CYCLE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_FREQUENCY;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_OUTPUT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_OUTPUTS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_PULS_DOWN;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_PULS_UP;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Třída představující IO handler pro čtení a zápis XML dat
 */
public class XMLHandlerFVEP extends XMLHandler {

    // region Constants
    public static final String TAG_ROOT = "fvep";
    // endregion

    // region Variables
    private ConfigurationFVEP configuration;
    // endregion

    /**
     * Vytvoří nový IO handler se zaměřením na XML hodnoty
     *
     * @param configuration {@link ConfigurationFVEP}
     */
    public XMLHandlerFVEP(ConfigurationFVEP configuration) {
        super(configuration);

        this.configuration = configuration;
    }

    // region Private methods
    private void writeOutputs(XmlSerializer serializer) throws IOException {
        serializer.startTag(NAMESPACE, TAG_OUTPUTS);

        for (ConfigurationFVEP.Output output : configuration.outputList) {
            writeOutput(serializer, output);
        }

        serializer.endTag(NAMESPACE, TAG_OUTPUTS);
    }

    private void writeOutput(XmlSerializer serializer, ConfigurationFVEP.Output output) throws IOException {
        serializer.startTag(NAMESPACE, TAG_OUTPUT);

        writeTag(serializer, TAG_PULS_UP, output.getPulsUp());
        writeTag(serializer, TAG_PULS_DOWN, output.getPulsDown());
        writeTag(serializer, TAG_FREQUENCY, output.getFrequency());
        writeTag(serializer, TAG_DUTY_CYCLE, output.getDutyCycle());
        writeTag(serializer, TAG_BRIGHTNESS, output.getBrightness());

        serializer.endTag(NAMESPACE, TAG_OUTPUT);
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
            ConfigurationFVEP.Output output = new ConfigurationFVEP.Output(configuration, configID);

            while (eventType != END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case START_DOCUMENT:
                        break;
                    case START_TAG:
                        if (tagName.equals(TAG_OUTPUT)) {
                            output = new ConfigurationFVEP.Output(configuration, configID++);
                        }
                        break;
                    case TEXT:
                        text = parser.getText();
                        break;
                    case END_TAG:
                        switch (tagName) {
                            case TAG_OUTPUT_COUNT:
                                configuration.setOutputCount(Integer.valueOf(text), false);
                                configuration.outputList.clear();
                                break;
                            case TAG_MEDIA:
                                configuration.setMediaType(Integer.valueOf(text));
                                break;
                            case TAG_PULS_UP:
                                output.setPulsUp(Integer.valueOf(text));
                                break;
                            case TAG_PULS_DOWN:
                                output.setPulsDown(Integer.valueOf(text));
                                break;
                            case TAG_FREQUENCY:
                                output.setFrequency(Double.valueOf(text));
                                break;
                            case TAG_DUTY_CYCLE:
                                output.setDutyCycle(Integer.valueOf(text));
                                break;
                            case TAG_BRIGHTNESS:
                                output.setBrightness(Integer.valueOf(text));
                                break;
                            case TAG_OUTPUT:
                                configuration.outputList.add(output);
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
        writeOutputs(serializer);

        serializer.endTag(NAMESPACE, TAG_ROOT);
        serializer.endDocument();
        writer.close();
    }
    // endregion
}
