package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_DISTRIBUTION_DELAY;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_DISTRIBUTION_VALUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_EDGE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_OUT;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_OUTPUTS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_PULS_DOWN;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_PULS_UP;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_RANDOM;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.Tags.TAG_WAIT;

/**
 * Třída představující IO handler pro čtení a zápis JSON dat
 */
class JSONHandlerERP extends JSONHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationERP configuration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na JSON hodnoty
     *
     * @param configuration {@link ConfigurationERP}
     */
    JSONHandlerERP(ConfigurationERP configuration) {
        super(configuration);

        this.configuration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Načte všechny výstupy
     *
     * @param outputs JSON pole
     * @throws JSONException Pokud něco nevyjde
     */
    private void readOutputs(JSONArray outputs) throws JSONException {
        List<ConfigurationERP.Output> outputList = configuration.outputList;
        outputList.clear();
        int count = configuration.getOutputCount();

        for (int i = 0; i < count; i++) {
            JSONObject outputObject = outputs.getJSONObject(i);
            outputList.add(readOutput(outputObject, i));
        }
    }

    /**
     * Načte jeden výstup
     *
     * @param outputObject JSON objekt
     * @param id Jednoznačný identifikátor výstupu
     * @return {@link ConfigurationERP.Output}
     * @throws JSONException Pokud něco nevyjde
     */
    private ConfigurationERP.Output readOutput(JSONObject outputObject, int id) throws JSONException {

        int pulsUp = outputObject.getInt(TAG_PULS_UP);
        int pulsDown = outputObject.getInt(TAG_PULS_DOWN);
        int distValue = outputObject.getInt(TAG_DISTRIBUTION_VALUE);
        int distDelay = outputObject.getInt(TAG_DISTRIBUTION_DELAY);
        int brightness = outputObject.getInt(TAG_BRIGHTNESS);

        return new ConfigurationERP.Output(configuration, id, pulsUp, pulsDown, distValue, distDelay, brightness);
    }

    /**
     * Zapíše všechny výstupy
     *
     * @param writer Reference na JSON writer
     * @throws IOException Pokud něco nevyjde
     */
    private void writeOutputs(JsonWriter writer) throws IOException {
        writer.name(TAG_OUTPUTS);
        writer.beginArray();

        for (ConfigurationERP.Output output : configuration.outputList) {
            writeOutput(writer, output);
        }

        writer.endArray();
    }

    /**
     * Zapíše jeden výstup
     *
     * @param writer Reference na JSON writer
     * @param output Reference na výstup, který se má zapsat
     * @throws IOException Pokud něco nevyjde
     */
    private void writeOutput(JsonWriter writer, ConfigurationERP.Output output) throws IOException {
        writer.beginObject();

        writer.name(TAG_PULS_UP).value(output.getPulsUp());
        writer.name(TAG_PULS_DOWN).value(output.getPulsDown());
        writer.name(TAG_DISTRIBUTION_VALUE).value(output.getDistributionValue());
        writer.name(TAG_DISTRIBUTION_DELAY).value(output.getDistributionDelay());
        writer.name(TAG_BRIGHTNESS).value(output.getBrightness());

        writer.endObject();
    }
    // endregion

    //region Public methods
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

            readSelf(jsonConfiguration);
            configuration.setOut(jsonConfiguration.getInt(TAG_OUT));
            configuration.setWait(jsonConfiguration.getInt(TAG_WAIT));
            configuration.setEdge(ConfigurationERP.Edge.valueOf(jsonConfiguration.getInt(TAG_EDGE)));
            configuration.setRandom(ConfigurationERP.Random.valueOf(jsonConfiguration.getInt(TAG_RANDOM)));

            JSONArray outputArray = jsonConfiguration.getJSONArray(TAG_OUTPUTS);
            readOutputs(outputArray);

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
        writer.name(TAG_OUT).value(configuration.getOut());
        writer.name(TAG_WAIT).value(configuration.getWait());
        writer.name(TAG_EDGE).value(configuration.getEdge().ordinal());
        writer.name(TAG_RANDOM).value(configuration.getRandom().ordinal());

        writeOutputs(writer);
        writer.endObject();

        writer.close();
    }
    // endregion
}
