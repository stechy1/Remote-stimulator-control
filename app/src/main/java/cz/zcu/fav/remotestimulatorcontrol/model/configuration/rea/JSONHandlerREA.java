package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.Tags.*;

/**
 * Třída představující IO handler pro čtení a zápis JSON dat
 */
public class JSONHandlerREA extends JSONHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationREA mConfiguration;
    // endregion
    
    /**
     * Vytvoří nový IO handler se zaměřením na JSON hodnoty
     *
     * @param configuration {@link ConfigurationREA}
     */
    public JSONHandlerREA(ConfigurationREA configuration) {
        super(configuration);
        
        this.mConfiguration = configuration;
    }

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
            mConfiguration.setCycleCount(configurationObject.getInt(TAG_CYCLE_COUNT));
            mConfiguration.setWaitFixed(configurationObject.getInt(TAG_WAIT_FIXED));
            mConfiguration.setWaitRandom(configurationObject.getInt(TAG_WAIT_RANDOM));
            mConfiguration.setMissTime(configurationObject.getInt(TAG_MISS_TIME));
            mConfiguration.setBrightness(configurationObject.getInt(TAG_BRIGHTNESS));
            mConfiguration.setOnFail(ConfigurationREA.OnFail.valueOf(configurationObject.getInt(TAG_ON_FAIL)));
            mConfiguration.setGender(ConfigurationREA.Gender.valueOf(configurationObject.getInt(TAG_GENDER)));
            mConfiguration.setAge(configurationObject.getInt(TAG_A));
            mConfiguration.setHeight(configurationObject.getInt(TAG_H));
            mConfiguration.setWeight(configurationObject.getInt(TAG_W));
        } catch(JSONException e){
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
        writer.name(TAG_CYCLE_COUNT).value(mConfiguration.getCycleCount());
        writer.name(TAG_WAIT_FIXED).value(mConfiguration.getWaitFixed());
        writer.name(TAG_WAIT_RANDOM).value(mConfiguration.getWaitRandom());
        writer.name(TAG_MISS_TIME).value(mConfiguration.getMissTime());
        writer.name(TAG_BRIGHTNESS).value(mConfiguration.getBrightness());
        writer.name(TAG_ON_FAIL).value(mConfiguration.getOnFail().ordinal());
        writer.name(TAG_GENDER).value(mConfiguration.getGender().ordinal());
        writer.name(TAG_A).value(mConfiguration.getAge());
        writer.name(TAG_H).value(mConfiguration.getHeight());
        writer.name(TAG_W).value(mConfiguration.getWeight());
        writer.endObject();

        writer.close();
    }
    // endregion
}
