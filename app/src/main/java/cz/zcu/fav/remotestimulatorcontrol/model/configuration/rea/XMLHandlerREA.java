package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_A;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_CYCLE_COUNT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_GENDER;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_H;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_MISS_TIME;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_ON_FAIL;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_W;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_WAIT_FIXED;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.TAG_WAIT_RANDOM;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Třída představující IO handler pro čtení a zápis XML dat
 */
public class XMLHandlerREA extends XMLHandler {

    // region Constants
    public static final String TAG_ROOT = "rea";
    // endregion

    // region Variables
    // Pracovní konfigurace
    private ConfigurationREA configuration;
    // endregion

    /**
     * Vytvoří nový IO handler se zaměřením na XML hodnoty
     *
     * @param configuration {@link ConfigurationREA}
     */
    public XMLHandlerREA(ConfigurationREA configuration) {
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
                            case TAG_CYCLE_COUNT:
                                configuration.setCycleCount(Integer.valueOf(text));
                                break;
                            case TAG_WAIT_FIXED:
                                configuration.setWaitFixed(Integer.valueOf(text));
                                break;
                            case TAG_WAIT_RANDOM:
                                configuration.setWaitRandom(Integer.valueOf(text));
                                break;
                            case TAG_MISS_TIME:
                                configuration.setMissTime(Integer.valueOf(text));
                                break;
                            case TAG_BRIGHTNESS:
                                configuration.setBrightness(Integer.valueOf(text));
                                break;
                            case TAG_ON_FAIL:
                                configuration.setOnFail(ConfigurationREA.OnFail.valueOf(Integer.valueOf(text)));
                                break;
                            case TAG_GENDER:
                                configuration.setGender(ConfigurationREA.Gender.valueOf(Integer.valueOf(text)));
                                break;
                            case TAG_A:
                                configuration.setAge(Integer.valueOf(text));
                                break;
                            case TAG_H:
                                configuration.setHeight(Integer.valueOf(text));
                                break;
                            case TAG_W:
                                configuration.setWeight(Integer.valueOf(text));
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
        writeTag(serializer, TAG_CYCLE_COUNT, configuration.getCycleCount());
        writeTag(serializer, TAG_WAIT_FIXED, configuration.getWaitFixed());
        writeTag(serializer, TAG_WAIT_RANDOM, configuration.getWaitRandom());
        writeTag(serializer, TAG_MISS_TIME, configuration.getMissTime());
        writeTag(serializer, TAG_BRIGHTNESS, configuration.getBrightness());
        writeTag(serializer, TAG_ON_FAIL, configuration.getOnFail().ordinal());
        writeTag(serializer, TAG_GENDER, configuration.getGender().ordinal());
        writeTag(serializer, TAG_A, configuration.getAge());
        writeTag(serializer, TAG_H, configuration.getHeight());
        writeTag(serializer, TAG_W, configuration.getWeight());

        serializer.endTag(NAMESPACE, TAG_ROOT);
        serializer.endDocument();
        writer.close();
    }
    // endregion
}
