package cz.zcu.fav.remotestimulatorcontrol.io;

import android.util.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

/**
 * Abstraktní třída pro uložení základních dat konfigurací
 */
public abstract class JSONHandler implements IOHandler {

    // region Constants
    private static final String TAG_OUTPUT_COUNT = "output_count";
    private static final String TAG_MEDIA = "media";
    // endregion

    // region Variables
    // Pracovní konfigurace
    private final AConfiguration mConfiguration;
    // endregion

    // region Constructors

    /**
     * Konstruktor třídy JSON handler
     *
     * @param configuration Konfigurace
     */
    public JSONHandler(AConfiguration configuration) {
        mConfiguration = configuration;
    }
    // endregion

    // region Public methods

    /**
     * Zapíše základní parametry konfigurace
     *
     * @param writer JsonWriter
     * @throws IOException
     */
    protected void writeSelf(JsonWriter writer) throws IOException {
        writer.name(TAG_OUTPUT_COUNT).value(mConfiguration.getOutputCount());
    }

    /**
     * Načte základní parametry konfigurace
     *
     * @param jsonConfiguration JSON objekt s konfigurací
     * @throws JSONException
     */
    protected void readSelf(JSONObject jsonConfiguration) throws JSONException {
        mConfiguration.setOutputCount(jsonConfiguration.getInt(TAG_OUTPUT_COUNT));
    }
    // endregion
}
