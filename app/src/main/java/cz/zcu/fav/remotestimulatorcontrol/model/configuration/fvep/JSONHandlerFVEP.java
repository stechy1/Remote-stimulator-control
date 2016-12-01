package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

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

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_BRIGHTNESS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_DUTY_CYCLE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_FREQUENCY;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_OUTPUTS;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_PULS_DOWN;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.Tags.TAG_PULS_UP;

/**
 * Třída představující IO handler pro čtení a zápis JSON dat
 */
class JSONHandlerFVEP extends JSONHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationFVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na JSON hodnoty
     *
     * @param configuration {@link ConfigurationFVEP}
     */
    JSONHandlerFVEP(ConfigurationFVEP configuration) {
        super(configuration);

        this.mConfiguration = configuration;
    }
    // endregion

    // region Private methods

    /**
     * Zapíše jeden výstup
     *
     * @param writer Reference na JSON writer
     * @param output Reference na výstup, který se má zapsat
     * @throws IOException
     */
    private void writeOutput(JsonWriter writer, ConfigurationFVEP.Output output) throws IOException {
        writer.beginObject();

        writer.name(TAG_PULS_UP).value(output.getPulsUp());
        writer.name(TAG_PULS_DOWN).value(output.getPulsDown());
        writer.name(TAG_FREQUENCY).value(output.getFrequency());
        writer.name(TAG_DUTY_CYCLE).value(output.getDutyCycle());
        writer.name(TAG_BRIGHTNESS).value(output.getBrightness());

        writer.endObject();
    }

    /**
     * Zapíše všechny výstupy
     *
     * @param writer Reference na JSON writer
     * @throws IOException
     */
    private void writeOutputs(JsonWriter writer) throws IOException {
        writer.name(TAG_OUTPUTS);
        writer.beginArray();

        for (ConfigurationFVEP.Output output : mConfiguration.outputList) {
            writeOutput(writer, output);
        }

        writer.endArray();
    }

    /**
     * Načte všechny outputy
     *
     * @param outputs JSON pole
     * @throws JSONException
     */
    private void readOutputs(JSONArray outputs) throws JSONException {
        List<ConfigurationFVEP.Output> outputList = mConfiguration.outputList;
        outputList.clear();
        int length = outputs.length();
        for (int i = 0; i < length; i++) {
            JSONObject outputObject = outputs.getJSONObject(i);
            outputList.add(readOutput(outputObject, i));
        }
    }

    /**
     * Naparsuje jeden output
     *
     * @param outputObject JSON objekt
     * @param id Jednoznačný identifikátor výstupu
     * @return ConfigurationFVEP.Output
     * @throws JSONException
     */
    private ConfigurationFVEP.Output readOutput(JSONObject outputObject, int id) throws JSONException {

        int pulsUp = outputObject.getInt(TAG_PULS_UP);
        int pulsDown = outputObject.getInt(TAG_PULS_DOWN);
        double frequency = outputObject.getDouble(TAG_FREQUENCY);
        int dutyCycle = outputObject.getInt(TAG_DUTY_CYCLE);
        int brightness = outputObject.getInt(TAG_BRIGHTNESS);

        return new ConfigurationFVEP.Output(mConfiguration, id, pulsUp, pulsDown, frequency, dutyCycle, brightness);
    }
    // endregion

    // region Public methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
        writer.setIndent("  ");

        writer.beginObject();
        super.writeSelf(writer);

        writeOutputs(writer);
        writer.endObject();

        writer.close();

    }

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

            JSONArray outputArray = jsonConfiguration.getJSONArray(TAG_OUTPUTS);
            readOutputs(outputArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // endregion

}
