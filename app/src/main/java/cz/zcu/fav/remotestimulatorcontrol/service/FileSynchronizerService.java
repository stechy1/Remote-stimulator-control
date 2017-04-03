package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;

/**
 * Service sloužící k synchronizaci souborů se vzdáleným souborovým serverem
 */
public class FileSynchronizerService extends RemoteServerIntentService {

    // region Constants

    private static final String TAG = "FileSyncService";

    public static final String SERVICE_NAME = "FileSynchronizerService";

    private static final String FILE_MASK = "*.jpg;*.gif;*.png";
    private static final String DEFAUT_REMOTE_DIRECTORY = "~/";

    private static final String ACTION_SYNCHRONIZE = ACTION_PREFIX + "SYNCHRONIZE";
    public static final String ACTION_DONE = ACTION_PREFIX + "DONE";

    private static final String PARAM_MEDIA_ROOT = PARAM_PREFIX + "MEDIA_ROOT";

    private static final int NOTIFICATION_ID = 15;

    // endregion

    // region Variables

    private final Semaphore serviceLock = new Semaphore(0);
    private final List<FileLsService.RemoteFileEntry> remoteFileEntries = new ArrayList<>();

    private final BroadcastReceiver mSecondaryProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case ACTION_UPDATE_SECONDARY_PROGRESS:
                    final int progress = intent.getIntExtra(PARAM_SECONDARY_PROGRESS, 0);
                    secProgress += progress;
                    mainProgress += progress;
                    updateProgress(mainProgress);
                    break;
                case ACTION_UPDATE_PROGRESS_MESSAGE:
                    final String title = intent.getStringExtra(PARAM_PROGRESS_MESSAGE);
                    setNotifyTitle(title);
                    break;
            }
        }
    };

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotifyBuilder;
    private File mediaRootDirectory;
    private int mainMaxProgress;
    private int mainProgress;
    private int secProgress;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou IntentService
     */
    public FileSynchronizerService() {
        super(SERVICE_NAME);
    }

    // endregion

    // region Public static methods

    /**
     * Spustí intent zodpovědný za synchronizaci souborů
     *
     * @param context            {@link Context}
     * @param mediaRootDirectory Lokální adresář, který se má synchronizovat se vzdáleným
     */
    public static void startActionSynchronize(Context context, String mediaRootDirectory) {
        Intent intent = new Intent(context, FileSynchronizerService.class);
        intent.setAction(ACTION_SYNCHRONIZE);
        intent.putExtra(PARAM_MEDIA_ROOT, mediaRootDirectory);
        context.startService(intent);
    }

    // endregion

    // region Private methods

    /**
     * Inicializuje notifikaci
     */
    private void initNotification() {
        mNotifyBuilder = new NotificationCompat.Builder(this);
        mainProgress = 0;
        mNotifyBuilder
                .setContentTitle("Media synchronization")
                .setContentText("Synchronization is in progress")
                .setSmallIcon(R.mipmap.launcher_icon)
                .setProgress(0, 0, false);

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
    }

    private void updateMaxProgress(int max) {
        if (mNotifyBuilder != null) {
            mNotifyBuilder.setProgress(max, mainProgress, false);
            mNotifyManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
        }
    }

    private void updateProgress(int progress) {
        if (mNotifyBuilder != null) {
            mNotifyBuilder.setProgress(mainMaxProgress, progress, false);
            mNotifyManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
        }
    }

    private void setNotifyTitle(CharSequence title) {
        if (mNotifyBuilder != null) {
            mNotifyBuilder.setSubText(title);
            mNotifyManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
        }
    }

    /**
     * Pozastaví vykonávání služby až do doby, kdy bude potřeba
     */
    private void lockService() {
        try {
            serviceLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Projde všechny lokální a vzdálené soubory a zjistí, které je potřeba
     * stahnout a které se musí nahrát na vzdálený server
     */
    private Pair<ArrayList<File>, ArrayList<String>> mergeFiles() {
        updateProgressTitle("Merging files");
        final Pair<ArrayList<File>, ArrayList<String>> result =
                new Pair<>(new ArrayList<File>(), new ArrayList<String>());
        final File[] localFilesArray = mediaRootDirectory.listFiles();
        final List<File> localFiles;
        if (localFilesArray == null) {
            localFiles = new ArrayList<>(0);
        } else {
            localFiles = new ArrayList<>(localFilesArray.length);
            localFiles.addAll(Arrays.asList(localFilesArray));
        }

        // Vypočítám hashe jednotlivých souborů
        // Pozor!!! Tato akce trvá velmi dlouho, takže je nutné opravdu čekat
//        List<byte[]> hashes = new ArrayList<>(localFiles.size());
//        for (File file : localFiles) {
//            try {
//                Log.d(TAG, "Počítam hash souboru: " + file.getName());
//                hashes.add(FileUtils.md5FromFile(file));
//            } catch (IOException e) {
//                Log.e(TAG, "Nepodařilo se získat MD5 hash ze souboru: " + file.getName());
//            }
//        }
        List<String> names = new ArrayList<>(localFiles.size());
        for (File localFile : localFiles) {
            names.add(localFile.getName());
        }

        // Projdu všechny vzdálené soubory a zjistím, které musím stáhnout
        increaseMaxProgress(names.size());
        // Dokud nebude fungovat checksuma, budu kontrolovat názvy souborů
        for (FileLsService.RemoteFileEntry remoteFileEntry : remoteFileEntries) {
            int index = names.indexOf(remoteFileEntry.name);
            if (index != -1) {
                names.remove(index);
                localFiles.remove(index);
            } else {
                result.second.add(DEFAUT_REMOTE_DIRECTORY + remoteFileEntry.name);
            }
            increaseMainProgress(1);
//            // Získám index hashe
//            int index = hashes.indexOf(remoteFileEntry.hash);
//            if (index != -1) {
//                // Když existuje lokální hash, tak smažu záznam
//                hashes.remove(index);
//                localFiles.remove(index);
//            } else {
//                // Když neexistuje, tak to dám na seznam souborů, co musím stáhnout
//                result.second.add(DEFAUT_REMOTE_DIRECTORY + remoteFileEntry.name);
//            }
        }

        // Zbylé soubory se musejí nahrát na server
        result.first.addAll(localFiles);
        increaseMainProgress(localFiles.size());

        return result;
    }

    // region Handle methods

    private void handleActionSynchronize(String mediaRootDirectory) {
        //initNotification();
        this.mediaRootDirectory = new File(mediaRootDirectory);
        increaseMaxProgress(2);
        // Nejdříve se spustí další intent service pro načtení souborů ze vzdáleného
        // adresáře
        FileLsService.startActionLs(this, DEFAUT_REMOTE_DIRECTORY, FILE_MASK, SERVICE_NAME);
        // Abych počkal na dokončení, tak se zamknu na semaforu
        lockService();
        increaseMainProgress(1);
        // Teď mám přístupnou proměnnou "remoteFileEntries"
        Pair<ArrayList<File>, ArrayList<String>> mergedFiles =
                mergeFiles();

        increaseMaxProgress(mergedFiles.first.size());
        for (File toUpload : mergedFiles.first) {
            Log.d(TAG, "Musím nahrát: " + toUpload + " soubor");
            FileUploadService.startActionUpload(this, toUpload.getAbsolutePath(), DEFAUT_REMOTE_DIRECTORY, SERVICE_NAME);
            lockService();
            increaseMainProgress(1);
            // Pozor, zde uspávám proces, abych uměle zvýšil prodlevu mezi jednotlivými operacemi
            // Je to kvůli pomalému zpracování v arduinu, které mi zahazovalo packety
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        increaseMaxProgress(mergedFiles.second.size());
        for (String toDownload : mergedFiles.second) {
            Log.d(TAG, "Musím stáhnout: " + toDownload + " soubor");
            FileDownloadService.startActionDownload(this, toDownload, SERVICE_NAME);
            lockService();
            increaseMainProgress(1);
            // Pozor, zde uspávám proces, abych uměle zvýšil prodlevu mezi jednotlivými operacemi
            // Je to kvůli pomalému zpracování v arduinu, které mi zahazovalo packety
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_DONE));
    }

    // endregion

    // endregion

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        final String action = intent.getAction();
        switch (action) {
            case ACTION_SYNCHRONIZE:
                String mediaRootDirectory = intent.getStringExtra(PARAM_MEDIA_ROOT);
                handleActionSynchronize(mediaRootDirectory);
                break;
        }
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected void onSubServiceDone(Intent intent) {
        final String action = intent.getAction();

        if (!action.equals(ACTION_ECHO_SERVICE_DONE)) {
            return;
        }

        final String destService = intent.getStringExtra(PARAM_ECHO_SERVICE_NAME);
        if (!SERVICE_NAME.equals(destService)) {
            return;
        }

        final String srcService = intent.getStringExtra(PARAM_SRC_SERVICE_NAME);
        final int success = intent.getIntExtra(PARAM_ECHO_SERVICE_STATUS, VALUE_ECHO_SERVICE_STATUS_ERROR);

        switch (srcService) {
            case FileLsService.SERVICE_NAME:
                if (success == VALUE_ECHO_SERVICE_STATUS_ERROR) {
                    Log.e(TAG, "Nepodařilo se získat obsah vzdáleného adresáře, končím...");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_DONE));
                    stopSelf();
                    return;
                }
                final List<FileLsService.RemoteFileEntry> entries = intent
                        .getParcelableArrayListExtra(FileLsService.PARAM_REMOTE_ENTRY_LIST);
                remoteFileEntries.addAll(entries);
                break;
            case FileUploadService.SERVICE_NAME:
                if (success == VALUE_ECHO_SERVICE_STATUS_ERROR) {
                    Log.e(TAG, "Nepodařilo se nahrát soubor");
                    break;
                }
                Log.d(TAG, "Upload completed");
                break;
            case FileDownloadService.SERVICE_NAME:
                if (success == VALUE_ECHO_SERVICE_STATUS_ERROR) {
                    Log.e(TAG, "Nepodařilo se stáhnout soubor");
                    break;
                }
                final String fileName = intent.getStringExtra(FileDownloadService.PARAM_DOWNLOADED_FILE_NAME);
                File file = new File(getCacheDir(), fileName);
                try {
                    FileUtils.copy(file, new File(mediaRootDirectory, fileName));
                } catch (IOException e) {
                    Log.e(TAG, "Nepodařilo se nakopírovat soubor", e);
                }
                break;
            default:
                Log.w(TAG, "Nebyl rozpoznán done event");
                break;
        }

        serviceLock.release();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_SECONDARY_PROGRESS);
        filter.addAction(ACTION_UPDATE_PROGRESS_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mSecondaryProgressReceiver, filter);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSecondaryProgressReceiver);
        super.onDestroy();
    }

    // endregion
}
