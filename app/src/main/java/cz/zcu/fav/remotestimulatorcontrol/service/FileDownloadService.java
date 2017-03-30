package cz.zcu.fav.remotestimulatorcontrol.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketAdvanced;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;
import cz.zcu.fav.remotestimulatorcontrol.util.BitUtils;

/**
 * Service sloužící ke stažení jednoho souboru ze vzdáleného serveru
 */
public class FileDownloadService extends RemoteServerIntentService {

    // region Constants
    private static final String TAG = "FileDownloadService";
    public static final String SERVICE_NAME = "FileDownloadService";

    private static final String ACTION_DOWNLOAD = ACTION_PREFIX + "DOWNLOAD";
    private static final String PARAM_REMOTE_FILE_PATH = PARAM_PREFIX + "REMOTE_FILE_PATH";
    public static final String PARAM_DOWNLOADED_FILE_NAME = PARAM_PREFIX + "DOWNLOADED_FILE_NAME";
    // endregion

    // region Variables

    // endregion

    // region Constructors

    public FileDownloadService() {
        super(SERVICE_NAME);
    }

    // endregion

    // region Public static methods

    /**
     * Spustí službu zodpovědnou za stažení souboru
     *
     * @param context {@link Context}
     * @param remoteFilePath Cesta k souboru na serveru
     * @param callbackServiceName Název služby/aktivity, která má zaregistrovaný {@link android.content.BroadcastReceiver}
     *                            pomocí něhož dokáže zareagovat na odpověď
     */
    public static void startActionDownload(Context context, String remoteFilePath, String callbackServiceName) {
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(PARAM_REMOTE_FILE_PATH, remoteFilePath);
        intent.putExtra(PARAM_ECHO_SERVICE_NAME, callbackServiceName);
        context.startService(intent);
    }
    // endregion

    // region Private methods

    /**
     * Odešle první packet se žádostí o stažení souboru
     *
     * @param remoteFilePath Cesta ke stahovanému souboru
     */
    private void sendFirstPacket(String remoteFilePath) {
        BtPacketAdvanced packet = RemoteFileServer.getGetPacket();
        packet.insertData(remoteFilePath.getBytes());
        sendData(packet);
    }

    // region Handle methods
    private void handleActionDownload(String remoteFilePath) {
        sendFirstPacket(remoteFilePath);
        waitOnSemaphore();

        if (incommingPacket.isResponse(RemoteFileServer.Codes.RESPONSE_GET_FILE_NOT_FOUND)) {
            Log.e(TAG, "Soubor nebyl nalezen");
            return;
        }

        final String fileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/"));
        final byte[] firstData = incommingPacket.getData();
        final int size = BitUtils.intFromBytes(firstData, 1); // Na indexu 0 je result
        final byte[] hash = new byte[RemoteFileServer.HASH_SIZE];
        System.arraycopy(firstData, 0, hash, 0, hash.length);
        File outputFile = new File(getCacheDir(), fileName);
        FileOutputStream outputStream = null;
        try {
             outputStream = new FileOutputStream(outputFile);
            do {
                waitOnSemaphore();
                byte[] data = incommingPacket.getData();
                outputStream.write(data);
            } while (!incommingPacket.hasCommand(RemoteFileServer.Codes.PART_LAST));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Nemělo by nikdy nastat
                }
            }
        }

        Intent intent = new Intent();
        intent.putExtra(PARAM_DOWNLOADED_FILE_NAME, fileName);
        sendEchoDone(intent);
    }
    // endregion

    // endregion


    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        switch (action) {
            case ACTION_DOWNLOAD:
                final String remoteFilePath = intent.getStringExtra(PARAM_REMOTE_FILE_PATH);
                callbackName = intent.getStringExtra(PARAM_ECHO_SERVICE_NAME);
                handleActionDownload(remoteFilePath);
                break;
        }
    }
}
