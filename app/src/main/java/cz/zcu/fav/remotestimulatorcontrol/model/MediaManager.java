package cz.zcu.fav.remotestimulatorcontrol.model;

import android.databinding.ObservableList;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.util.EnumUtil;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;

/**
 * Třída představující správce externích médii pro jednu konfiguraci
 * Pomocí manageru se spravují jednotlivá externí média
 */
public final class MediaManager {

    // region Constants
    private static final String TAG = "MediaManager";

    public static final int MESSAGE_MEDIA_IMPORT = 1;
    public static final int MESSAGE_MEDIA_PREPARED_TO_DELETE = 2;
    public static final int MESSAGE_CONFIGURATION_UNDO_DELETE = 3;

    public static final int MESSAGE_SUCCESSFUL = 1;
    public static final int MESSAGE_UNSUCCESSFUL = 2;
    // endregion

    // region Variables
    // Složka s pracovním adresářem obsahující veškerá externí média konfigurace
    private final File mWorkingDirectory;
    // Konfigurace, ke které se vztahují externí média
    private final AConfiguration mConfiguration;
    // Kolekce externích médii
    public final ObservableList<AMedia> mediaList;
    // Handler posílající zprávy o stavu operace v manažeru
    private Handler mHandler;
    private Pair<Integer, AMedia> mDeletedMedia = null;
    // endregion

    // region Constructors
    /**
     * Konstruktory pro správce externích médii
     *
     * @param workingDirectory Pracovní adresář obsahující externí média
     * @param configuration Konfigurace, ke které patří externí média
     */
    public MediaManager(File workingDirectory, AConfiguration configuration) {
        mWorkingDirectory = workingDirectory;
        mConfiguration = configuration;

        mediaList = configuration.mediaList;
    }
    // endregion

    // region Static methods
    /**
     * Načte jedeno externí médium
     *
     * @param mediaFile Soubor s externím médiem
     * @param mediaList Kolekce, do které se média přidávají
     */
    public static void loadMediaFile(File mediaFile, List<AMedia> mediaList) {
        if (mediaFile.isDirectory())
            return;

        // Získání názvu media souboru
        String name = mediaFile.getName();

        // Ošetření na existenci nějaké koncovky. Pokud soubor nemá koncovku, přeskočí se.
        if (name.indexOf(".") <= 0) {
            Log.i(TAG, "Přeskakuji soubor: " + mediaFile);
            return;
        }

        // Získání koncovky souboru jako podřetězec začínající o 1 větším indexem tečky.
        String extension = name.substring(name.indexOf(".") + 1);

        // Převedení koncovky na výčtový typ pro jednodušší práci
        MediaExtensionType extensionType = EnumUtil.lookup(MediaExtensionType.class, extension);
        if (extensionType == null) {
            Log.e(TAG, "Nebyla rozpoznána koncovka souboru. Původní typ na koncovku byl: " + extension);
            return;
        }

        AMedia media = MediaHelper.from(mediaFile, name, extensionType);
        mediaList.add(media);
    }

    /**
     * Načte externí média do konfigurace
     *
     * @param mediaDirectory Složka, kde jsou uložena externí média
     */
    public static void loadMediaFiles(File mediaDirectory, AConfiguration configuration) {
        File[] mediaFiles = mediaDirectory.listFiles();

        // Pokud složka neobsahuje žádná média, tak není co načítat
        if (mediaFiles.length == 0)
            return;

        List<AMedia> mediaList = configuration.mediaList;
        mediaList.clear();

        // Proiteruj mi každý media soubor
        for (File mediaFile : mediaFiles) {
            loadMediaFile(mediaFile, mediaList);
        }
    }

    /**
     * Sestaví cestu k médiu na základě kořenové složky medií a konkrétního média
     *
     * @param mediaDirectory Kořenová složka médií
     * @param media Konkrétní médium
     * @return Cesta k médiu
     */
    public static File buildMediaFilePath(File mediaDirectory, AMedia media) {
        return new File(mediaDirectory, media.getName());
    }
    // endregion

    // region Public methods
    /**
     * Importuje nové externí médium ke konfiguraci
     *
     * @param externalFile Soubor, který se bude importovat
     */
    public void importt(File externalFile) {
        String fileName = externalFile.getName();
        File destinationFile = new File(mWorkingDirectory, fileName);

        try {
            FileUtils.copy(externalFile, destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_MEDIA_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
            return;
        }

        // Získání koncovky souboru a odebrání tečky
        String extension = FileUtils.getExtension(destinationFile.getPath()).replace(".", "");
        MediaExtensionType extensionType = EnumUtil.lookup(MediaExtensionType.class, extension);
        if (extensionType == null) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_MEDIA_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }

            return;
        }

        AMedia media = MediaHelper.from(destinationFile, fileName, extensionType);
        if (media == null) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_MEDIA_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
        }
        mediaList.add(media);

        int position = mediaList.indexOf(media);

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_MEDIA_IMPORT, MESSAGE_SUCCESSFUL, position).sendToTarget();
        }
    }

    /**
     * Nastaví handler, který reaguje na přijaté zprávy
     *
     * @param handler Handler
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * Připravý soubor na daném indexu ke smazání
     *
     * @param index Index souboru, který se má smazat
     */
    public void prepareToDelete(int index) {
        if (index < 0) {
            return;
        }

        mDeletedMedia = new Pair<>(index, mediaList.get(index));
        mediaList.remove(index);

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_MEDIA_PREPARED_TO_DELETE, MESSAGE_SUCCESSFUL, index).sendToTarget();
        }
    }

    /**
     * Zruší akci mazání
     * Vrátí smezané medium zpět do kolekce
     */
    public void undoDelete() {
        if (mDeletedMedia == null) {
            return;
        }

        int index = mDeletedMedia.first;
        mediaList.add(index, mDeletedMedia.second);
        mDeletedMedia = null;

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_CONFIGURATION_UNDO_DELETE, MESSAGE_SUCCESSFUL, index).sendToTarget();
        }
    }

    /**
     * Potvrzení smazání média
     */
    public void confirmDelete() {
        File file = buildMediaFilePath(mWorkingDirectory, mDeletedMedia.second);
        if (!file.delete()) {
            Log.e(TAG, "Nepodařilo se smazat mediální soubor: " + mDeletedMedia.second);
        }

        mDeletedMedia = null;
    }
    // endregion
}
