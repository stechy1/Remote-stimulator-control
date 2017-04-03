package cz.zcu.fav.remotestimulatorcontrol.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketAdvanced;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;

/**
 * Service sloužící ke smazání jednoho souboru ze vzdáleného serveru
 */
public class FileDeleteService extends RemoteServerIntentService {

    // region Constants
    private static final String TAG = "FileDeleteService";

    public static final String SERVICE_NAME = "FileDeleteService";

    private static final String ACTION_DELETE = ACTION_PREFIX + "DELETE";
    private static final String PARAM_FILE_NAME = PARAM_PREFIX + "FILE_NAME";
    private static final String PARAM_REMOTE_DIRECTORY = PARAM_PREFIX + "REMOTE_DIRECTORY";

    // endregion

    // region Variables

    // endregion

    // region Constructors

    public FileDeleteService() {
        super(SERVICE_NAME);
    }

    // endregion

    // region Public static methods

    /**
     * Spustí službu zodpovědnou za smazání souboru
     *
     * @param context {@link Context}
     * @param fileName Název souboru, který se má smazat
     * @param callbackServiceName Název služby/aktivity, která má zaregistrovaný {@link android.content.BroadcastReceiver}
     *                            pomocí něhož dokáže zareagovat na odpověď
     */
    public static void startActionDelete(Context context, String fileName, String remoteDirectory, String callbackServiceName) {
        Intent intent = new Intent(context, FileDeleteService.class);
        intent.setAction(ACTION_DELETE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PARAM_FILE_NAME, fileName);
        intent.putExtra(PARAM_REMOTE_DIRECTORY, remoteDirectory);
        intent.putExtra(PARAM_ECHO_SERVICE_NAME, callbackServiceName);
        context.startService(intent);
    }

    // endregion

    // region Private methods

    // region Handle methods

    private void handleActionDelete(String fileName, String remoteDirectory) {
        final String remoteFile = remoteDirectory + fileName;
        BtPacketAdvanced packet = RemoteFileServer.getDelPacket();
        packet.insertData(remoteFile.getBytes());
        sendData(packet);

        try {
            BtPacketAdvanced response = incommintPackets.poll(DEFAULT_WAIT_TIME_FOR_PACKET, DEFAULT_WAIT_UNIT);
            if (response == null) {
                sendEchoDone(new Intent(), VALUE_ECHO_SERVICE_STATUS_ERROR);
                return;
            }

            if (response.isResponse(RemoteFileServer.Codes.RESPONSE_OK)) {
                sendEchoDone(new Intent());
            } else {
                sendEchoDone(new Intent(), VALUE_ECHO_SERVICE_STATUS_ERROR);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            case ACTION_DELETE:
                final String fileName = intent.getStringExtra(PARAM_FILE_NAME);
                final String remoteDirectory = intent.getStringExtra(PARAM_REMOTE_DIRECTORY);
                callbackName = intent.getStringExtra(PARAM_ECHO_SERVICE_NAME);
                handleActionDelete(fileName, remoteDirectory);
                break;
        }
    }
}
