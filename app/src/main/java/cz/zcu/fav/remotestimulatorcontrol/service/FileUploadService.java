package cz.zcu.fav.remotestimulatorcontrol.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketAdvanced;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;
import cz.zcu.fav.remotestimulatorcontrol.util.BitUtils;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;

/**
 * Service sloužící k nahrání jednoho souboru na vzdálený server
 */
public class FileUploadService extends RemoteServerIntentService {

    // region Constants

    private static final String TAG = "FileUploadService";
    public static final String SERVICE_NAME = "FileUploadService";

    private static final String ACTION_UPLOAD = ACTION_PREFIX + "UPLOAD";
    private static final String PARAM_FILE_PATH = PARAM_PREFIX + "FILE_PATH";
    private static final String PARAM_REMOTE_DIRECTORY = PARAM_PREFIX + "REMOTE_DIRECTORY";

    // endregion

    // region Variables

    // endregion

    // region Constructors

    /**
     * Vytvoří novou service pro nahrání jednoho souboru
     */
    public FileUploadService() {
        super(SERVICE_NAME);
    }

    // endregion

    // region Public static methods

    /**
     * Spustí službu zodpovědnou za upload souboru
     *
     * @param context {@link Context}
     * @param filePath Cesta k souboru, který se má nahrát
     */
    public static void startActionUpload(Context context, String filePath, String remoteDirectory) {
        Intent intent = new Intent(context, FileUploadService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PARAM_FILE_PATH, filePath);
        intent.putExtra(PARAM_REMOTE_DIRECTORY, remoteDirectory);
        context.startService(intent);
    }
    // endregion

    // region Private methods

    /**
     * Odešle první packet s informacemi o souboru
     *
     * @param file Soubor, který se bude nahrávat
     * @param remoteDirectory Vzdálený adresář, kam se má soubor nahrát
     */
    private void sendFirstPacket(File file, String remoteDirectory) throws IOException {
        BtPacketAdvanced packet = RemoteFileServer.getPutPacket();
        final int size = (int) file.length();
        final byte[] data = new byte[BtPacket.PACKET_SIZE - 4];
        BitUtils.intToBytes(size, data, 0);

        final byte[] hash = FileUtils.md5FromFile(file);
        System.arraycopy(hash, 0, data, 4, hash.length);
        final byte[] nameBytes = (remoteDirectory + file.getName()).getBytes();
        System.arraycopy(nameBytes, 0, data, 20, nameBytes.length);
        packet.insertData(data);
        sendData(packet);
    }

    // region Handle methods

    private void handleActionUpload(String filePath, String remoteDirectory) {
        final File file = new File(filePath);

        try {
            sendFirstPacket(file, remoteDirectory);
        } catch (IOException e) {
            Log.e(TAG, "Nepodařilo se poslat první packet", e);
        }


        FileInputStream in = null;
        final int maxDataSize = BtPacket.PACKET_SIZE - RemoteFileServer.Codes.INDEX_DATA;
        final byte[] totalData = new byte[maxDataSize];
        final byte[] buffer = new byte[maxDataSize];
        int totalSize = 0;
        int count = 0;
        try {
            in = new FileInputStream(file);
            do {
                Arrays.fill(buffer, (byte) 0);
                count = in.read(buffer);

                int freeBytes = maxDataSize - totalSize;
                int byteCount = count > freeBytes ? freeBytes : count;

                if (count != -1) {
                    System.arraycopy(buffer, 0, totalData, totalSize, byteCount);
                    totalSize += count;
                } else {
                    totalSize = maxDataSize;
                }

                if (totalSize >= maxDataSize) {
                    BtPacketAdvanced packet = RemoteFileServer.getServerPacket();
                    packet.setCommand(RemoteFileServer.Codes.TYPE_UPLOAD);
                    // Pokud jsem přečetl poslední kus souboru, tak přiložím do hlavíčky
                    // informaci o posledním packetu
                    if (count == -1) {
                        packet.addCommand(RemoteFileServer.Codes.PART_LAST);
                    }
                    packet.insertData(totalData);
                    sendData(packet);

                    if (count != -1) {
                        totalSize %= maxDataSize;
                        // Zkopírování zbývajících dat z bufferu do hlavních dat pro příští použití
                        Arrays.fill(totalData, totalSize, totalData.length, (byte) 0);
                        System.arraycopy(buffer, count - totalSize, totalData, 0, totalSize);
                    }
                }

            } while (count != -1);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Nemělo by nikdy nastat
                }
            }
        }

        sendEchoDone(new Intent());
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
            case ACTION_UPLOAD:
                final String filePath = intent.getStringExtra(PARAM_FILE_PATH);
                final String remoteDirectory = intent.getStringExtra(PARAM_REMOTE_DIRECTORY);
                handleActionUpload(filePath, remoteDirectory);
                break;
        }
    }
}
