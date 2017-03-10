package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

/**
 * Třída pro asynchroní načítání profilů
 */
public class ProfileAsyncReader extends AsyncTask<Void, Integer, Integer[]> {

    // region Constants
    // Tag
    private static final String TAG = "ProfileAsyncReader";

    // endregion

    // region Variables
    private final File mWorkingDirectory;
    // Kolekce, do které se přidávají načtené profily
    private final List<OutputProfile> mItems;
    // Listener, který se zavolá po dokončení načítacího procesu
    private final OnProfileLoadedListener mListener;

    // endregion

    // region Constructors

    /**
     * Vytvoří nový loader profilů
     *
     * @param workingDirectory Pracovní adresář, ve kterém se nachází všechny profily
     * @param items Reference na list, do kterého se budou vkládat načtené profily
     * @param listener Listener, který se zavolá po úspěšném načtění všech profilů
     */
    public ProfileAsyncReader(File workingDirectory, List<OutputProfile> items, OnProfileLoadedListener listener) {
        this.mWorkingDirectory = workingDirectory;
        this.mItems = items;
        this.mListener = listener;
    }

    // endregion

    // endregion

    /**
     * Načte jeden profile
     *
     * @param file Soubor s profilem
     */
    private boolean loadProfile(File file) {
        // Získání názvu konfigurace ze souboru
        String name = file.getName();

        // Ošetření na existenci nějaké koncovky. Pokud soubor nemá koncovku, přeskočí se.
        if (name.indexOf(".") <= 0) {
            Log.i(TAG, "Přeskakuji soubor: " + file);
            return false;
        }

        // Získání koncovky souboru jako podřetězec začínající o 1 větším indexem tečky.
        String extension = name.substring(name.indexOf(".") + 1);
        name = name.replace("." + extension, "");

        OutputProfile profile = new OutputProfile(name);
        mItems.add(profile);

        return true;
    }

    // region Private methods

    @Override
    protected Integer[] doInBackground(Void... params) {
        final Integer[] success = {0, 0};

        File[] files = mWorkingDirectory.listFiles();
        if (files == null) {
            return success;
        }

        for (File file : files) {
            boolean result = loadProfile(file);
            if (result) {
                success[0]++;
            } else {
                success[1]++;
            }
        }

        return success;
    }

    interface OnProfileLoadedListener {

        /**
         * Zavolá se po načtení všech profilů
         *
         * @param successfuly Počet úspěšně načtených profilů
         * @param unsuccessfuly Počet neúspěšně načtených profilů
         */
        void onLoaded(int successfuly, int unsuccessfuly);

    }
}
