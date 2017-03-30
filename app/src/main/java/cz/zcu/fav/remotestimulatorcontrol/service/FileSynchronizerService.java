package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

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
    private static final String PARAM_MEDIA_PATH = PARAM_PREFIX + "MEDIA_PATH";
    private static final String PARAM_REMOTE_FOLDER = PARAM_PREFIX + "REMOTE_FOLDER";
    public static final String PARAM_UPDATE_PROCESS = PARAM_PREFIX + "UPDATE_PROCESS";

    // endregion

    // region Variables

    private final Semaphore serviceLock = new Semaphore(0);

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotifyBuilder;
    private final List<FileLsService.RemoteFileEntry> remoteFileEntries = new ArrayList<>();

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
     * @param context {@link Context}
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
     *
     * @param localDirectory Lokální adresář se soubory, které se budou mergovat
     */
    private Pair<ArrayList<File>, ArrayList<String>> mergeFiles(File localDirectory) {
        final Pair<ArrayList<File>, ArrayList<String>> result =
                new Pair<>(new ArrayList<File>(), new ArrayList<String>());
        final File[] localFilesArray = localDirectory.listFiles();
        final List<File> localFiles;
        if (localFilesArray == null) {
            localFiles = new ArrayList<>(0);
        } else {
            localFiles = new ArrayList<>(localFilesArray.length);
            localFiles.addAll(Arrays.asList(localFilesArray));
        }

        // Vypočítám hashe jednotlivých souborů
        // Pozor!!! Tato akce trvá velmi dlouho, takže je nutné opravdu čekat
        List<byte[]> hashes = new ArrayList<>(localFiles.size());
        for (File file : localFiles) {
            try {
                Log.d(TAG, "Počítam hash souboru: " + file.getName());
                hashes.add(FileUtils.md5FromFile(file));
            } catch (IOException e) {
                Log.e(TAG, "Nepodařilo se získat MD5 hash ze souboru: " + file.getName());
            }
        }

        // Projdu všechny vzdálené soubory a zjistím, které musím stáhnout
        for (FileLsService.RemoteFileEntry remoteFileEntry : remoteFileEntries) {
            // Získám index hashe
            int index = hashes.indexOf(remoteFileEntry.hash);
            if (index != -1) {
                // Když existuje lokální hash, tak smažu záznam
                hashes.remove(index);
                localFiles.remove(index);
            } else {
                // Když neexistuje, tak to dám na seznam souborů, co musím stáhnout
                result.second.add(DEFAUT_REMOTE_DIRECTORY + remoteFileEntry.name);
            }
        }

        // Zbylé soubory se musejí nahrát na server
        result.first.addAll(localFiles);

        return result;
    }

    // region Handle methods

    private void handleActionSynchronize(String mediaRootDirectory) {
        // Nejdříve se spustí další intent service pro načtení souborů ze vzdáleného
        // adresáře
        FileLsService.startActionLs(this, DEFAUT_REMOTE_DIRECTORY, FILE_MASK, SERVICE_NAME);
        // Abych počkal na dokončení, tak se zamknu na semaforu
        lockService();
        // Teď mám přístupnou proměnnou "remoteFileEntries"
        Pair<ArrayList<File>, ArrayList<String>> mergedFiles =
                mergeFiles(new File(mediaRootDirectory));

        for (File toUpload : mergedFiles.first) {
            Log.d(TAG, "Musím nahrát: " + toUpload + " soubor");
            FileUploadService.startActionUpload(this, toUpload.getAbsolutePath(), DEFAUT_REMOTE_DIRECTORY);
            //lockService();
        }

        for (String toDownload : mergedFiles.second) {
            Log.d(TAG, "Musím stáhnout: " + toDownload + " soubor");
        }

        // TODO nahrát potřebné soubory
        // TODO stáhnout potřebné soubory
        // Synchonizace je dokončena

//        mNotifyBuilder
//                .setContentTitle("Media synchronization")
//                .setContentText("Synchronization is in progress")
//                .setSmallIcon(R.mipmap.launcher_icon)
//                //.add
//                .setProgress(COUNT, 0, false);
//        mNotifyManager.notify(1, mNotifyBuilder.build());
//
//        int i = 0;
//
//        do {
//            waitOnSemaphore();
//
//            mNotifyBuilder.setProgress(COUNT, ++i, false);
//            mNotifyManager.notify(1, mNotifyBuilder.build());
//
//            Intent intent = new Intent(ACTION_SYNCHRONIZATION);
//            intent.putExtra(PARAM_UPDATE_PROCESS, i);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//
//        } while(i != COUNT);
//
//        mNotifyBuilder
//                .setContentText("Synchronization complete")
//                .setProgress(0,0,false);
//        mNotifyManager.notify(1, mNotifyBuilder.build());
//
//        Intent intent = new Intent(ACTION_DONE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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

        if (action.equals(ACTION_ECHO_SERVICE_DONE)) {
            final String destService = intent.getStringExtra(PARAM_ECHO_SERVICE_NAME);
            if (!destService.equals(SERVICE_NAME)) {
                return;
            }

            final String srcService = intent.getStringExtra(PARAM_SRC_SERVICE_NAME);

            switch (srcService) {
                case FileLsService.SERVICE_NAME:
                    final List<FileLsService.RemoteFileEntry> entries = intent
                            .getParcelableArrayListExtra(FileLsService.PARAM_REMOTE_ENTRY_LIST);
                    remoteFileEntries.addAll(entries);
                    break;
            }
        }

        serviceLock.release();
    }



    // endregion
}
