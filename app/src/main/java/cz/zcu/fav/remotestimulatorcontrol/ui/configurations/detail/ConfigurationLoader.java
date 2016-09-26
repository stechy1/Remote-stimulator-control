package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationManager;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MetaData;

/**
 * Třída pro asynchronní načtení zadané konfigurace
 */
class ConfigurationLoader extends AsyncTask<File, Void, Void> {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigurationLoader";

    private final AConfiguration configuration;
    private final OnConfigurationLoaded onLoaded;

    /**
     * Vytvoří nový loader konfigurace
     *
     * @param configuration Konfigurace, která se má načíst
     * @param onLoaded Handler, který se zavolá po úspěšném načtení konfigurace
     */
    ConfigurationLoader(AConfiguration configuration, OnConfigurationLoaded onLoaded) {
        this.configuration = configuration;
        this.onLoaded = onLoaded;
    }

    @Override
    protected Void doInBackground(File... params) {
        try {
            IOHandler handler = configuration.getHandler();
            File file = ConfigurationManager.buildConfigurationFilePath(params[0], configuration);
            handler.read(new FileInputStream(file));

            MetaData metaData = configuration.metaData;
            metaData.changed.setTime(file.lastModified());

        } catch (IOException e) {
            Log.e(TAG, "Nepodařilo se načíst konfiguraci: " + configuration.getName());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (onLoaded != null) {
            onLoaded.onConfigurationLoaded();
        }
    }

    /**
     * Rozhraní definující kontrakt pro zachycení načtení konfigurace
     */
    interface OnConfigurationLoaded {

        /**
         * Zavolá se po načtení konfigurace z disku
         */
        void onConfigurationLoaded();

    }

}
