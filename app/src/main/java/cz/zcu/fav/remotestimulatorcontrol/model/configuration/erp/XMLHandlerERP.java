package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_DISTRIBUTION_DELAY;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_DISTRIBUTION_VALUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_EDGE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_OUT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_OUTPUT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_OUTPUTS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_PULS_DOWN;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_PULS_UP;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_RANDOM;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_WAIT;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Třída představující IO handler pro čtení a zápis XML dat
 */
public class XMLHandlerERP extends XMLHandler {

    // region Constants
    public static final String TAG_ROOT = "erp";
    // endregion

    // region Variables
    // Pracovní konfigurace
    private ConfigurationERP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na XML hodnoty
     *
     * @param configuration {@link ConfigurationERP}
     */
    public XMLHandlerERP(ConfigurationERP configuration) {
        super(configuration);

        this.mConfiguration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Zapíše hodnoty všech výstupů
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @throws IOException Pokud něco nevyjde
     */
    private void writeOutputs(XmlSerializer serializer) throws IOException {
        serializer.startTag(NAMESPACE, TAG_OUTPUTS);

        for (ConfigurationERP.Output output : mConfiguration.outputList) {
            writeOutput(serializer, output);
        }

        serializer.endTag(NAMESPACE, TAG_OUTPUTS);
    }

    /**
     * Zapíše hodnoty jednoho výstupu
     *
     * @param serializer {@link XmlSerializer} Serializer, do kterého se zapisují hodnoty
     * @param output Výstup, který se má zapsat
     * @throws IOException Pokud něco nevyjde
     */
    private void writeOutput(XmlSerializer serializer, ConfigurationERP.Output output) throws IOException {
        serializer.startTag(NAMESPACE, TAG_OUTPUT);

        writeTag(serializer, TAG_PULS_UP, output.getPulsUp());
        writeTag(serializer, TAG_PULS_DOWN, output.getPulsDown());
        writeTag(serializer, TAG_DISTRIBUTION_VALUE, output.getDistributionValue());
        writeTag(serializer, TAG_DISTRIBUTION_DELAY, output.getDistributionDelay());
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
            ConfigurationERP.Output output = new ConfigurationERP.Output(mConfiguration, configID);

            while (eventType != END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case START_DOCUMENT:
                        break;
                    case START_TAG:
                        if (tagName.equals(TAG_OUTPUT)) {
                            output = new ConfigurationERP.Output(mConfiguration, configID++);
                        }
                        break;
                    case TEXT:
                        text = parser.getText().trim();
                        break;
                    case END_TAG:
                        switch (tagName) {
                            case TAG_OUTPUT_COUNT:
                                mConfiguration.setOutputCount(Integer.valueOf(text), false);
                                mConfiguration.outputList.clear();
                                break;
                            case TAG_OUT:
                                mConfiguration.setOut(text);
                                break;
                            case TAG_WAIT:
                                mConfiguration.setWait(text
                                );
                                break;
                            case TAG_EDGE:
                                mConfiguration.setEdge(ConfigurationERP.Edge.valueOf(Integer.valueOf(text)));
                                break;
                            case TAG_RANDOM:
                                mConfiguration.setRandom(ConfigurationERP.Random.valueOf(Integer.valueOf(text)));
                                break;
                            case TAG_PULS_UP:
                                output.setPulsUp(text);
                                break;
                            case TAG_PULS_DOWN:
                                output.setPulsDown(text);
                                break;
                            case TAG_DISTRIBUTION_VALUE:
                                output.setDistributionValue(text);
                                break;
                            case TAG_DISTRIBUTION_DELAY:
                                output.setDistributionDelay(text);
                                break;
                            case TAG_BRIGHTNESS:
                                output.setBrightness(text);
                                break;
                            case TAG_OUTPUT:
                                mConfiguration.outputList.add(output);
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
        writeTag(serializer, TAG_OUT, mConfiguration.getOut());
        writeTag(serializer, TAG_WAIT, mConfiguration.getWait());
        writeTag(serializer, TAG_EDGE, mConfiguration.getEdge().ordinal());
        writeTag(serializer, TAG_RANDOM, mConfiguration.getRandom().ordinal());

        writeOutputs(serializer);

        serializer.endTag(NAMESPACE, TAG_ROOT);
        serializer.endDocument();
        writer.close();
    }
    // endregion
}
