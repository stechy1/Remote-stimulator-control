package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import android.util.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.io.JSONHandler;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PATTERNS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PATTERN_LENGHT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PATTERN_VALUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PULS_LENGHT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.Tags.TAG_PULS_SKEW;

/**
 * Třída představující IO handler pro čtení a zápis JSON dat
 */
public class JSONHandlerTVEP extends JSONHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationTVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na JSON hodnoty
     *
     * @param configuration {@link ConfigurationTVEP}
     */
    public JSONHandlerTVEP(ConfigurationTVEP configuration) {
        super(configuration);

        this.mConfiguration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Načte všechny patterny
     *
     * @param patterns JSON kolekce patternů
     * @throws JSONException Pokud něco nevyjde
     */
    private void readPatterns(JSONArray patterns) throws JSONException {
        List<ConfigurationTVEP.Pattern> patternList = mConfiguration.patternList;
        patternList.clear();
        int count = mConfiguration.getOutputCount();

        for (int i = 0; i < count; i++) {
            JSONObject patternObject = patterns.getJSONObject(i);
            patternList.add(new ConfigurationTVEP.Pattern(i, patternObject.getInt(TAG_PATTERN_VALUE)));
        }
    }

    /**
     * Zapíše všechny patterny
     *
     * @param writer JSON writer
     * @throws IOException Pokud něco nevyjde
     */
    private void writePatterns(JsonWriter writer) throws IOException {
        writer.name(TAG_PATTERNS);
        writer.beginArray();

        for (ConfigurationTVEP.Pattern a : mConfiguration.patternList) {
            writer.beginObject();

            writer.name(TAG_PATTERN_VALUE).value(a.getValue());

            writer.endObject();
        }

        writer.endArray();
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
            JSONObject jsonConfiguration = new JSONObject(src);

            super.readSelf(jsonConfiguration);
            mConfiguration.setPatternLength(jsonConfiguration.getInt(TAG_PATTERN_LENGHT));
            mConfiguration.setTimeBetween(jsonConfiguration.getInt(TAG_PULS_SKEW));
            mConfiguration.setPulsLength(jsonConfiguration.getInt(TAG_PULS_LENGHT));
            mConfiguration.setBrightness(jsonConfiguration.getInt(TAG_BRIGHTNESS));

            JSONArray patternArray = jsonConfiguration.getJSONArray(TAG_PATTERNS);
            readPatterns(patternArray);

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
        writer.name(TAG_PATTERN_LENGHT).value(mConfiguration.getPatternLength());
        writer.name(TAG_PULS_SKEW).value(mConfiguration.getTimeBetween());
        writer.name(TAG_PULS_LENGHT).value(mConfiguration.getPulsLength());
        writer.name(TAG_BRIGHTNESS).value(mConfiguration.getBrightness());

        writePatterns(writer);
        writer.endObject();

        writer.close();
    }
    // endregion
}
