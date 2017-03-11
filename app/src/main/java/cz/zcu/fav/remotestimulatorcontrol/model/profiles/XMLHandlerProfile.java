package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import cz.zcu.fav.remotestimulatorcontrol.io.GenericXMLHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

import static cz.zcu.fav.remotestimulatorcontrol.model.profiles.Tags.TAG_MEDIA_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.model.profiles.Tags.TAG_PROFILE;
import static cz.zcu.fav.remotestimulatorcontrol.model.profiles.Tags.TAG_TYPE;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class XMLHandlerProfile extends GenericXMLHandler {

    // region Constants
    public static final String TAG_ROOT = "profiles";

    // endregion

    // region Variables
    private final OutputProfile profile;
    // endregion

    // region Constructors

    /**
     * Vytvoří nový IO handler se zaměřením na XML hodnoty
     *
     * @param profile {@link OutputProfile}
     */
    public XMLHandlerProfile(OutputProfile profile) {
        this.profile = profile;
    }

    // endregion

    // region Private methods

    /**
     * Zapíše hodnty všech konfigurací výstupů
     *
     * @param serializer
     */
    private void writeOutputConfigurations(XmlSerializer serializer) throws IOException  {
        for (OutputConfiguration outputConfiguration : profile.mOutputConfigurationList) {
            writeOutputConfiguration(serializer, outputConfiguration);
        }
    }

    private void writeOutputConfiguration(XmlSerializer serializer, OutputConfiguration outputConfiguration) throws IOException {
        serializer.startTag(NAMESPACE, TAG_PROFILE);

        writeTag(serializer, TAG_MEDIA_NAME, outputConfiguration.getFileName());
        writeTag(serializer, TAG_TYPE, outputConfiguration.getMediaType().toString());

        serializer.endTag(NAMESPACE, TAG_PROFILE);
    }

    // endregion

    @Override
    public void read(InputStream inputStream) throws IOException {
        XmlPullParserFactory factory;
        try {
            factory = createFactory();

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, DEFAULT_ENCODING);

            int eventType = parser.getEventType();
            String text = "";
            OutputConfiguration outputConfiguration = new OutputConfiguration();

            while(eventType != END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case START_DOCUMENT:
                        break;
                    case START_TAG:
                        if (tagName.equals(TAG_PROFILE)) {
                            outputConfiguration = new OutputConfiguration();
                        }
                        break;
                    case TEXT:
                        text = parser.getText();
                        break;
                    case END_TAG:
                        switch (tagName) {
                            case TAG_MEDIA_NAME:
                                outputConfiguration.setFileName(text);
                                break;
                            case TAG_TYPE:
                                outputConfiguration.setMediaType(MediaType.valueOf(text));
                                break;
                            case TAG_PROFILE:
                                profile.mOutputConfigurationList.add(outputConfiguration);
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

    @Override
    public void write(OutputStream outputStream) throws IOException {
        XmlSerializer serializer = createSerializer();
        Writer writer = new OutputStreamWriter(outputStream);

        serializer.setOutput(writer);
        serializer.startDocument(DEFAULT_ENCODING, STAND_ALONE);
        serializer.startTag(NAMESPACE, TAG_ROOT);

        writeOutputConfigurations(serializer);

        serializer.endTag(NAMESPACE, TAG_ROOT);
        serializer.endDocument();
        writer.close();
    }
}
