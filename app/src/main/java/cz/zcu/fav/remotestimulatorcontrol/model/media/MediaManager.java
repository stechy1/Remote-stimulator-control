package cz.zcu.fav.remotestimulatorcontrol.model.media;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.fav.remotestimulatorcontrol.ui.media.MediaAsyncReader;
import cz.zcu.fav.remotestimulatorcontrol.util.EnumUtil;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;

/**
 * Třída představující správce externích médii pro jednu konfiguraci
 * Pomocí manageru se spravují jednotlivá externí média
 *
 *  - import
 *  - mazání
 */
public final class MediaManager implements MediaAsyncReader.OnMediaLoadedListener {

    // region Constants
    private static final String TAG = "MediaManager";

    // region Id jednotlivých zpráv, které se předávají handlerem ven
    public static final int MESSAGE_MEDIA_LOADED = 1;
    public static final int MESSAGE_MEDIA_IMPORT = 2;
    public static final int MESSAGE_MEDIA_PREPARED_TO_DELETE = 3;
    public static final int MESSAGE_MEDIA_UNDO_DELETE = 4;

    public static final int MESSAGE_SUCCESSFUL = 1;
    public static final int MESSAGE_UNSUCCESSFUL = 2;

    // endregion
    public static final String MEDIA_FOLDER = "media";

    // endregion

    // region Variables
    // Složka s pracovním adresářem obsahující veškerá externí média konfigurace
    private final File mWorkingDirectory;
    // Kolekce externích médii
    public final ObservableList<AMedia> mediaList;
    private final Set<AMedia> mMediaToDelete;
    // Handler posílající zprávy o stavu operace v manažeru
    private Handler mHandler;
    // endregion

    // region Constructors
    /**
     * Konstruktory pro správce externích médii
     *
     *  @param workingDirectory Pracovní adresář obsahující externí média
     */
    public MediaManager(File workingDirectory) {
        mWorkingDirectory = workingDirectory;
        mediaList = new ObservableArrayList<>();
        mMediaToDelete = new HashSet<>();
    }
    // endregion

    // region Static methods
    /**
     * Sestaví cestu k médiu na základě kořenové složky medií a konkrétního média
     *
     * @param workingDirectory Kořenová složka médií
     * @param media Konkrétní médium
     * @return Cesta k médiu
     */
    public static File buildMediaFilePath(File workingDirectory, AMedia media) {
        final File mediaFolder = new File(workingDirectory, MEDIA_FOLDER);
        if (!mediaFolder.exists()) {
            if (!mediaFolder.mkdirs()) {
                Log.e(TAG, "Nemám přístup k souborovému systému");
            }
        }

        return new File(mediaFolder, media.getName());
    }
    // endregion

    // region Private methods
    private File buildMediaFilePath(AMedia media) {
        return buildMediaFilePath(mWorkingDirectory, media);
    }
    // endregion

    // region Public methods
    public void refresh() {
        Log.d(TAG, "Aktualizuji seznam médií");
        mediaList.clear();
        new MediaAsyncReader(mWorkingDirectory, mediaList, this).execute();
    }

    /**
     * Importuje nové externí médium ke konfiguraci
     *
     * @param externalFile Soubor, který se bude importovat
     */
    public void importt(File externalFile) {
        String fileName = externalFile.getName();
        File mediaFolder = new File(mWorkingDirectory, MEDIA_FOLDER);
        if (!mediaFolder.exists()) {
            if (!mediaFolder.mkdirs()) {
                Log.e(TAG, "Nemám přístup k souborovému systému");
                return;
            }
        }
        File destinationFile = new File(mediaFolder, fileName);

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
     * @param selectedItems Index souboru, který se má smazat
     */
    public void prepareToDelete(List<Integer> selectedItems) {
        if (selectedItems.size() == 0) {
            return;
        }

        // Potřebuji odebírat profily od nejvyššího indexu po nejnižší,
        // abych zabránil NullPointerExceptionu
        if (selectedItems.size() > 1) {
            Collections.sort(selectedItems);
            Collections.reverse(selectedItems);
        }

        for (Integer selectedItem : selectedItems) {
            AMedia media = mediaList.get(selectedItem);
            mMediaToDelete.add(media);
            mediaList.remove(selectedItem.intValue());
        }

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_MEDIA_PREPARED_TO_DELETE, selectedItems).sendToTarget();
        }
    }

    /**
     * Zruší akci mazání
     * Vrátí smezané medium zpět do kolekce
     */
    public void undoDelete() {
        for (AMedia profile : mMediaToDelete) {
            mediaList.add(profile);
        }

        mMediaToDelete.clear();

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_MEDIA_UNDO_DELETE).sendToTarget();
        }
    }

    /**
     * Potvrzení smazání média
     */
    public void confirmDelete() {
        for (AMedia configuration : mMediaToDelete) {
            File configFile = buildMediaFilePath(configuration);
            if (!configFile.delete()) {
                Log.e(TAG, "Nepodařilo se smazat profil: " + configFile.getName());
            }
        }
    }

    @Override
    public void onLoaded(int successfuly, int unsuccessfuly) {
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_MEDIA_LOADED, successfuly, unsuccessfuly).sendToTarget();
        }
    }
    // endregion
}
