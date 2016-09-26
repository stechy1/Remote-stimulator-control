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
    private final ConfigurationREA configuration;
    // endregion
    
    /**
     * Vytvoří nový IO handler se zaměřením na JSON hodnoty
     *
     * @param configuration {@link ConfigurationREA}
     */
    public JSONHandlerREA(ConfigurationREA configuration) {
        super(configuration);
        
        this.configuration = configuration;
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
            configuration.setCycleCount(configurationObject.getInt(TAG_CYCLE_COUNT));
            configuration.setWaitFixed(configurationObject.getInt(TAG_WAIT_FIXED));
            configuration.setWaitRandom(configurationObject.getInt(TAG_WAIT_RANDOM));
            configuration.setMissTime(configurationObject.getInt(TAG_MISS_TIME));
            configuration.setBrightness(configurationObject.getInt(TAG_BRIGHTNESS));
            configuration.setOnFail(ConfigurationREA.OnFail.valueOf(configurationObject.getInt(TAG_ON_FAIL)));
            configuration.setGender(ConfigurationREA.Gender.valueOf(configurationObject.getInt(TAG_GENDER)));
            configuration.setAge(configurationObject.getInt(TAG_A));
            configuration.setHeight(configurationObject.getInt(TAG_H));
            configuration.setWeight(configurationObject.getInt(TAG_W));
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
        writer.name(TAG_CYCLE_COUNT).value(configuration.getCycleCount());
        writer.name(TAG_WAIT_FIXED).value(configuration.getWaitFixed());
        writer.name(TAG_WAIT_RANDOM).value(configuration.getWaitRandom());
        writer.name(TAG_MISS_TIME).value(configuration.getMissTime());
        writer.name(TAG_BRIGHTNESS).value(configuration.getBrightness());
        writer.name(TAG_ON_FAIL).value(configuration.getOnFail().ordinal());
        writer.name(TAG_GENDER).value(configuration.getGender().ordinal());
        writer.name(TAG_A).value(configuration.getAge());
        writer.name(TAG_H).value(configuration.getHeight());
        writer.name(TAG_W).value(configuration.getWeight());
        writer.endObject();

        writer.close();
    }
    // endregion
}
