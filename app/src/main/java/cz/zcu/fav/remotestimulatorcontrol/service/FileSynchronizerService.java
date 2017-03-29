package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.Semaphore;

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

    // region Handle methods

    private void handleActionSynchronize(String mediaRootDirectory) {

        FileLsService.startActionLs(this, DEFAUT_REMOTE_DIRECTORY, FILE_MASK, SERVICE_NAME);

        lockService();


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
    protected void onSubServiceDone(Intent intent) {
        String destService = intent.getStringExtra(PARAM_ECHO_SERVICE_NAME);
        if (!destService.equals(SERVICE_NAME)) {
            return;
        }

        serviceLock.release();
    }



    // endregion
}
