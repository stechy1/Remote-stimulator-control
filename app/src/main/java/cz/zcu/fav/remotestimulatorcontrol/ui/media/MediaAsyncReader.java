package cz.zcu.fav.remotestimulatorcontrol.ui.media;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaHelper;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaManager;
import cz.zcu.fav.remotestimulatorcontrol.util.EnumUtil;

/**
 * Třída pro asynchronní načítání médií
 */
public final class MediaAsyncReader extends AsyncTask<Void, Integer, Integer[]> {

    // region Constants
    // Tag
    private static final String TAG = "MediaAsyncReader";

    // endregion

    // region Variables
    private final File mWorkingDirectory;
    // Kolekce, do které se přidávají načtená média
    private final List<AMedia> mItems;
    // Listener, který se zavolá po dokončení načítacího procesu
    private final OnMediaLoadedListener mListener;
    // endregion

    // region Constructors

    /**
     * Vytvoří nový loader médií
     *
     * @param workingDirectory Pracovní adresář, ve kterém se nachází všechna média
     * @param items Reference na list, do kterého se budou vkládat načtená média
     * @param listener Listener, který se zavolá po úspěšném načtění všech médií
     */
    public MediaAsyncReader(File workingDirectory, List<AMedia> items, OnMediaLoadedListener listener) {
        this.mWorkingDirectory = workingDirectory;
        this.mItems = items;
        this.mListener = listener;
    }

    // endregion

    /**
     * Načte jedno médium
     *
     * @param file Soubor s médiem
     * @return True, pokud bylo načtení úspěšné, jinak false
     */
    private boolean loadMedia(File file) {
        // Získání názvu media souboru
        String name = file.getName();

        // Ošetření na existenci nějaké koncovky. Pokud soubor nemá koncovku, přeskočí se.
        if (name.indexOf(".") <= 0) {
            Log.i(TAG, "Přeskakuji soubor: " + file);
            return false;
        }

        // Získání koncovky souboru jako podřetězec začínající o 1 větším indexem tečky.
        String extension = name.substring(name.indexOf(".") + 1);

        // Převedení koncovky na výčtový typ pro jednodušší práci
        MediaExtensionType extensionType = EnumUtil.lookup(MediaExtensionType.class, extension);
        if (extensionType == null) {
            Log.e(TAG, "Nebyla rozpoznána koncovka souboru. Původní typ na koncovku byl: " + extension);
            return false;
        }

        AMedia media = MediaHelper.from(file, name, extensionType);
        mItems.add(media);

        return true;
    }


    @Override
    protected Integer[] doInBackground(Void... params) {
        final Integer[] success = {0, 0};

        File mediaFolder = new File(mWorkingDirectory, MediaManager.MEDIA_FOLDER);
        File[] files = mediaFolder.listFiles();

        if (files == null) {
            return success;
        }

        for (File file : files) {
            boolean result = loadMedia(file);
            if (result) {
                success[0]++;
            } else {
                success[1]++;
            }
        }

        return success;
    }

    @Override
    protected void onPostExecute(Integer[] result) {
        Log.i(TAG, "Načítání médií doběhlo. Úspěšně: " + result[0] + "; Neúspěšně: " + result[1]);
        if (mListener != null) {
            mListener.onLoaded(result[0], result[1]);
        }
    }

    public interface OnMediaLoadedListener {

        /**
         * Zavolá se po načtení všech profilů
         *
         * @param successfuly Počet úspěšně načtených profilů
         * @param unsuccessfuly Počet neúspěšně načtených profilů
         */
        void onLoaded(int successfuly, int unsuccessfuly);
    }
}
