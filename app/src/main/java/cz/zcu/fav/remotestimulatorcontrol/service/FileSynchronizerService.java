package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;

/**
 * Service sloužící ke komunikaci se vzdáleným souborovým serverem
 */
public class FileSynchronizerService extends IntentService {

    private static final String TAG = "FileSyncService";

    private static final String SERVICE_NAME = "FileSynchronizerService";

    public static final int COUNT = 50;

    private static final String ACTION_SYNCHRONIZE = "cz.zcu.fav.remotestimulatorcontrol.service.action.SYNCHRONIZE";
    private static final String ACTION_UPLOAD = "cz.zcu.fav.remotestimulatorcontrol.service.action.UPLOAD_MEDIA";
    public static final String ACTION_SYNCHRONIZATION = "cz.zcu.fav.remotestimulatorcontrol.service.action.SYNCHRONIZATION";
    public static final String ACTION_DONE = "cz.zcu.fav.remotestimulatorcontrol.service.action.DONE";

    private static final String PARAM_MEDIA_ROOT = "cz.zcu.fav.remotestimulatorcontrol.service.extra.MEDIA_ROOT";
    private static final String PARAM_MEDIA_PATH = "cz.zcu.fav.remotestimulatorcontrol.service.extra.MEDIA_PATH";
    public static final String PARAM_UPDATE_PROCESS = "cz.zcu.fav.remotestimulatorcontrol.service.extra.UPDATE_PROCESS";

    private final Semaphore sem = new Semaphore(0);

    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_DATA_RECEIVED)) {
                final int NO_DATA = -1;
                int length = intent.getIntExtra(BluetoothService.DATA_RECEIVED_BYTES, NO_DATA);
                byte[] received = intent.getByteArrayExtra(BluetoothService.DATA_RECEIVED_BUFFER);
                if (length == NO_DATA) {
                    return;
                }

                bytes = Arrays.copyOf(received, length);

                sem.release();
            }
        }
    };

    private byte[] bytes;
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

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyBuilder = new NotificationCompat.Builder(this);
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
            case ACTION_UPLOAD:
                String filePath = intent.getStringExtra(PARAM_MEDIA_PATH);
                handleActionUpload(filePath);
                break;
        }
    }

    /**
     * Pošle data k odeslání do {@link BluetoothService}
     *
     * @param buffer Data, která se mají odeslat
     */
    private void sendData(byte[] buffer) {
        Intent intent = new Intent(BluetoothService.ACTION_SEND_DATA);
        intent.putExtra(BluetoothService.DATA_CONTENT, buffer);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.put(RemoteFileServer.Codes.OP_HELLO.code);

        mNotifyBuilder
                .setContentTitle("Media synchronization")
                .setContentText("Synchronization is in progress")
                .setSmallIcon(R.mipmap.launcher_icon)
                //.add
                .setProgress(COUNT, 0, false);
        mNotifyManager.notify(1, mNotifyBuilder.build());

        int i = 0;
        sendData(buffer.array());

        do {
            waitOnSemaphore();

            mNotifyBuilder.setProgress(COUNT, i++, false);
            mNotifyManager.notify(1, mNotifyBuilder.build());

            Intent intent = new Intent(ACTION_SYNCHRONIZATION);
            intent.putExtra(PARAM_UPDATE_PROCESS, i);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        } while(i != COUNT);

        mNotifyBuilder
                .setContentText("Synchronization complete")
                .setProgress(0,0,false);
        mNotifyManager.notify(1, mNotifyBuilder.build());

        Intent intent = new Intent(ACTION_DONE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleActionUpload(String filePath) {

    }
}
