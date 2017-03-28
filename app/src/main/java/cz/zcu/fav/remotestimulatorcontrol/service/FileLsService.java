package cz.zcu.fav.remotestimulatorcontrol.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketAdvanced;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;


/**
 * Služba sloužící pro získání obsahu souborů vzdáleného serveru
 */
public class FileLsService extends RemoteServerIntentService {

    // region Constants

    private static final String TAG = "FileLsService";

    private static final String ACTION_LS = ACTION_PREFIX + "LS";

    private static final String PARAM_REMOTE_FOLDER = PARAM_PREFIX + "REMOTE_FOLDER";
    private static final String PARAM_FILE_MASK = PARAM_PREFIX + "FILE_MASK";

    // endregion

    // region Variables
    private final List<byte[]> bytes = new ArrayList<>();
    // endregion

    // region Constructors

    /**
     * Vytvoří novou service, která se postará o získání obsahu vzdáleného adresáře
     */
    public FileLsService() {
        super("FileLsService");
    }

    // endregion

    // region Public static methods

    /**
     * Spustí intent pro získání obsahu vzdáleného adresáře
     *
     * @param context {@link Context}
     */
    public static void startActionLs(Context context, String remoteFolder, String fileMask) {
        Intent intent = new Intent(context, FileLsService.class);
        intent.setAction(ACTION_LS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PARAM_REMOTE_FOLDER, remoteFolder);
        intent.putExtra(PARAM_FILE_MASK, fileMask);
        context.startService(intent);
    }
    // endregion

    //region Private methods

    // region Handle methods

    /**
     * Zde se získá obsah vzdáleného adresáře
     *
     * @param remoteFolder Cesta ke vzdálenému adresáři
     */
    private void handleActionLs(String remoteFolder, String fileMask) {
        BtPacketAdvanced packet = RemoteFileServer.getLsPacket();
        packet.insertData(remoteFolder.getBytes());
        packet.insertData(new byte[] {(byte) 0});
        packet.insertData(fileMask.getBytes());
        packet.insertData(new byte[] {(byte) 0});
        sendData(packet);

        int i = 0;

        do {
            Log.d(TAG, "Čekám na semaforu pro LS");
            waitOnSemaphore();
            byte[] data = incommingPacket.getData();
            bytes.add(data);
            Log.d(TAG, Arrays.toString(data));
            Log.d(TAG, "IsLastPart: " + incommingPacket.hasCommand(RemoteFileServer.Codes.PART_LAST));
            i++;
        } while(i < 2);//while (!incommingPacket.hasCommand(RemoteFileServer.Codes.PART_LAST));

        Log.d(TAG, Arrays.toString(bytes.toArray()));


        Intent intent = new Intent();
        // TODO vložit seznam souborů
        sendEchoDone(intent, FileSynchronizerService.SERVICE_NAME);

    }

    // endregion

    // endregion

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        if (action.equals(ACTION_LS)) {
            String remoteFolder = intent.getStringExtra(PARAM_REMOTE_FOLDER);
            String fileMask = intent.getStringExtra(PARAM_FILE_MASK);
            handleActionLs(remoteFolder, fileMask);
        }
    }



}
