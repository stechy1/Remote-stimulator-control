package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import android.util.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import cz.zcu.fav.remotestimulatorcontrol.io.JSONHandler;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_BIT_SHIFT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_MAIN_PATTERN_VALUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.Tags.TAG_PULSE_LENGHT;

/**
 * Třída představující IO handler pro čtení a zápis JSON dat
 */
public class JSONHandlerCVEP extends JSONHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationCVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na JSON hodnoty
     *
     * @param configuration {@link ConfigurationCVEP}
     */
    public JSONHandlerCVEP(ConfigurationCVEP configuration) {
        super(configuration);
        
        this.mConfiguration = configuration;
    }
    // endregion

    // region Public methods
    /**
     * {@inheritDoc}
     */
    @Override
    public void read(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = reader.readLine()) != null)
            builder.append(line);
        reader.close();

        String src = builder.toString();

        try {
            JSONObject configurationObject = new JSONObject(src);

            super.readSelf(configurationObject);
            mConfiguration.setPulsLength(configurationObject.getString(TAG_PULSE_LENGHT));
            mConfiguration.setBitShift(configurationObject.getString(TAG_BIT_SHIFT));
            mConfiguration.setBrightness(configurationObject.getString(TAG_BRIGHTNESS));
            mConfiguration.mainPattern.setValue(configurationObject.getInt(TAG_MAIN_PATTERN_VALUE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
        writer.setIndent("  ");

        writer.beginObject();

        super.writeSelf(writer);
        writer.name(TAG_PULSE_LENGHT).value(mConfiguration.getPulsLength());
        writer.name(TAG_BIT_SHIFT).value(mConfiguration.getBitShift());
        writer.name(TAG_BRIGHTNESS).value(mConfiguration.getBrightness());
        writer.name(TAG_MAIN_PATTERN_VALUE).value(mConfiguration.mainPattern.getValue());

        writer.endObject();
        writer.close();
    }
    // endregion
}
