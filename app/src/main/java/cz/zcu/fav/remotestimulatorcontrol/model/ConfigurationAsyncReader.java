package cz.zcu.fav.remotestimulatorcontrol.model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MetaData;
import cz.zcu.fav.remotestimulatorcontrol.util.EnumUtil;

/**
 * Třída pro asynchroní načtení konfigurací
 */
final class ConfigurationAsyncReader extends AsyncTask<ConfigurationType, Integer, Integer[]> {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigAsyncReader";

    // Pracovní adresář
    private final File mWorkingDirectory;
    // Kolekce, do které se přidávají načtené konfigurace
    private final List<AConfiguration> mItems;
    // Listener, který se zavolá po dokončení načítacího procesu
    private final OnConfigurationLoadedListener mListener;

    /**
     * Vytvoří nový loader konfigurací
     *
     * @param workingDirectory Pracovní adresář, ve kterém se nachází všechny konfigurace
     * @param items Reference na list, do kterého se budou vkládat načtené konfigurace
     * @param listener Listener, který se zavolá po úspěšném načtění všech konfigurací
     */
    public ConfigurationAsyncReader(File workingDirectory, List<AConfiguration> items, OnConfigurationLoadedListener listener) {
        mWorkingDirectory = workingDirectory;
        mItems = items;
        mListener = listener;
    }

    /**
     * Načte konfigurace zadaného typu
     *
     * @param files Pole konfigurací jednoho typu
     * @param type Typ zadaných konfigurací
     * @return Jednorozměrné pole o dvou hodnotách
     *         První hodnota představuje počet úspěšně načtených konfigurací
     *         Druhá hodnota představuje počet neúspěšně načtených konfigurací
     */
    private int[] loadConfigurations(File[] files, ConfigurationType type) {
        final int[] success = {0, 0};
        for (File file : files) {
            Log.d(TAG, "Otevírám soubor: " + file);
            if (file.isDirectory()) {
                continue;
            }
            String name = file.getName();

            // Ošetření na existenci nějaké koncovky. Pokud soubor nemá koncovku, přeskočí se.
            if (name.indexOf(".") <= 0) {
                Log.i(TAG, "Přeskakuji soubor: " + file);
                continue;
            }

            try {
                // Získání koncovky souboru jako podřetězec začínající o 1 větším indexem tečky.
                String extension = name.substring(name.indexOf(".") + 1);

                ExtensionType extensionType = EnumUtil.lookup(ExtensionType.class, extension);
                // Získání přesného názvu souboru bet koncovky
                assert extensionType != null;
                name = name.replace(extensionType.toString(), "");

                AConfiguration configuration = ConfigurationHelper.from(name, type);

                MetaData metaData = configuration.metaData;
                metaData.extensionType = extensionType;
                metaData.changed.setTime(file.lastModified());

                configuration.getHandler().read(new FileInputStream(file));

                mItems.add(configuration);

                success[0]++;
            } catch (Exception e) {
                Log.e(TAG, "Konfigurace: " + file.getName() + " nebyla načtena");
                success[1]++;
            }
        }

        return success;
    }

    @Override
    protected Integer[] doInBackground(ConfigurationType... types) {
        final Integer[] success = {0, 0};
        for (ConfigurationType type : types) {
            // Vytvoří novou referenci na třídu File obsahující složku, ve které jsou konfigurace daného typu
            File[] files = new File(mWorkingDirectory, type.toString().toLowerCase()).listFiles();
            // Pokud žádné konfigurace daného typu neexistují, tak se poračuje dál a nic se nenačítá
            if (files == null) {
                continue;
            }

            int[] result = loadConfigurations(files, type);
            success[0] += result[0];
            success[1] += result[1];
        }

        return success;
    }

    @Override
    protected void onPostExecute(Integer[] result) {
        Log.i(TAG, "Načítání konfigurací doběhlo. Úspěšně: " + result[0] + "; Neúspěšně: " + result[1]);
        if (mListener != null) {
            mListener.onLoaded(result[0], result[1]);
        }
    }

    /**
     * Kontrakt pro obsluhu události po načtení všech konfigurací
     */
    interface OnConfigurationLoadedListener {

        /**
         * Zavolá se po načtení všech konfigurací
         *
         * @param successfuly Počet úspěšně načtených konfigurací
         * @param unsuccessfuly Počet neúspěšně načtených konfigurací
         */
        void onLoaded(int successfuly, int unsuccessfuly);
    }
}
