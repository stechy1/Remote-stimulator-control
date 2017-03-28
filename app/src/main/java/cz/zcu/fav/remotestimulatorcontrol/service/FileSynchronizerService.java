package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.Semaphore;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;

/**
 * Service sloužící ke komunikaci se vzdáleným souborovým serverem
 */
public class FileSynchronizerService extends IntentService {

    private static final String TAG = "FileSyncService";

    private static final String SERVICE_NAME = "FileSynchronizerService";

    public static final int COUNT = 10;

    private static final String ACTION_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.action.";
    private static final String PARAM_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.extra.";

    private static final String ACTION_SYNCHRONIZE = ACTION_PREFIX + "SYNCHRONIZE";
    private static final String ACTION_UPLOAD = ACTION_PREFIX + "UPLOAD_MEDIA";
    public static final String ACTION_SYNCHRONIZATION = ACTION_PREFIX + "SYNCHRONIZATION";
    public static final String ACTION_LS = ACTION_PREFIX + "LS";
    public static final String ACTION_DONE = ACTION_PREFIX + "DONE";

    private static final String PARAM_MEDIA_ROOT = PARAM_PREFIX + "MEDIA_ROOT";
    private static final String PARAM_MEDIA_PATH = PARAM_PREFIX + "MEDIA_PATH";
    public static final String PARAM_UPDATE_PROCESS = PARAM_PREFIX + "UPDATE_PROCESS";
    private static final String PARAM_REMOTE_FOLDER = PARAM_PREFIX + "REMOTE_FOLDER";

    private final Semaphore sem = new Semaphore(0);

    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_DATA_RECEIVED)) {
                byte[] received = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA_CONTENT);

                incommingPacket = new BtPacket(received);

                sem.release();
            }
        }
    };

    private BtPacket incommingPacket;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotifyBuilder;

    /**
     * Vytvoří novou IntentService
     */
    public FileSynchronizerService() {
        super(SERVICE_NAME);
    }

    public static void startActionSynchronize(Context context, String mediaRootDirectory) {
        Intent intent = new Intent(context, FileSynchronizerService.class);
        intent.setAction(ACTION_SYNCHRONIZE);
        intent.putExtra(PARAM_MEDIA_ROOT, mediaRootDirectory);
        context.startService(intent);
    }

    public static void startActionUpload(Context context, AMedia media) {
        Intent intent = new Intent(context, FileSynchronizerService.class);
        intent.setAction(ACTION_SYNCHRONIZE);
        intent.putExtra(PARAM_MEDIA_PATH, media.getMediaFile().getAbsolutePath());
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotifyBuilder = new NotificationCompat.Builder(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mDataReceiver, new IntentFilter(BluetoothService.ACTION_DATA_RECEIVED));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReceiver);
        super.onDestroy();
    }

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
            case ACTION_LS:
                String remoteFolder = intent.getStringExtra(PARAM_REMOTE_FOLDER);
                handleActionLs(remoteFolder);
                break;
            case ACTION_UPLOAD:
                String filePath = intent.getStringExtra(PARAM_MEDIA_PATH);
                handleActionUpload(filePath);
                break;
        }
    }

    /**
     * Pošle data k odeslání do {@link BluetoothService}
     *
     * @param packet Packet, který se má odeslat
     */
    private void sendData(BtPacket packet) {
        BluetoothService.sendData(this, packet);
    }

    /**
     * Počká na semaforu, dokud nebude uvolněn <=> příjdou nová data
     */
    private void waitOnSemaphore() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleActionSynchronize(String mediaRootDirectory) {



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

    private void handleActionUpload(String filePath) {

    }

    /**
     * Zde se získá obsah vzdáleného adresáře
     *
     * @param remoteFolder Cesta ke vzdálenému adresáři
     */
    private void handleActionLs(String remoteFolder) {



    }
}
