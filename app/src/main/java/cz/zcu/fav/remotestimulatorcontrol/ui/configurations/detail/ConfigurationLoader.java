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

    // region Constants
    // Logovací tag
    private static final String TAG = "ConfigurationLoader";
    // endregion

    // region Variables
    private final AConfiguration mConfiguration;
    private final OnConfigurationLoaded mOnLoaded;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový loader konfigurace
     *
     * @param configuration Konfigurace, která se má načíst
     * @param onLoaded Handler, který se zavolá po úspěšném načtení konfigurace
     */
    ConfigurationLoader(AConfiguration configuration, OnConfigurationLoaded onLoaded) {
        this.mConfiguration = configuration;
        this.mOnLoaded = onLoaded;
    }
    // endregion

    @Override
    protected Void doInBackground(File... params) {
        try {
            IOHandler handler = mConfiguration.getHandler();
            File configurationFile = ConfigurationManager.buildConfigurationFilePath(params[0], mConfiguration);
            handler.read(new FileInputStream(configurationFile));

            MetaData metaData = mConfiguration.metaData;
            metaData.changed.setTime(configurationFile.lastModified());

        } catch (IOException e) {
            Log.e(TAG, "Nepodařilo se načíst konfiguraci: " + mConfiguration.getName());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mOnLoaded != null) {
            mOnLoaded.onConfigurationLoaded();
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
