package cz.zcu.fav.remotestimulatorcontrol.service;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
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
    public static final String PARAM_REMOTE_ENTRY_LIST = PARAM_PREFIX + "REMOTE_ENTRY_LIST";

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
        packet.insertData(new byte[]{(byte) 0});
        packet.insertData(fileMask.getBytes());
        packet.insertData(new byte[]{(byte) 0});
        sendData(packet);

        waitOnSemaphore();

        byte[] firstData = incommingPacket.getData();
        if (!incommingPacket.isResponse(RemoteFileServer.Codes.RESPONSE_OK)) {
            Log.e(TAG, "Příkaz LS selhal");
            sendEchoDone(new Intent(), FileSynchronizerService.SERVICE_NAME);
        }
        // Odtud získám počet packetů
        int size = 0;
        size |= firstData[1] << 24;
        size |= firstData[2] << 16;
        size |= firstData[3] << 8;
        size |= firstData[4];

        byte[] hash = new byte[16];
        System.arraycopy(firstData, 0, hash, 0, hash.length);

        int count = (int) Math.round(Math.ceil(size / 60.0));

        {
            int i = 0;

            do {
                waitOnSemaphore();
                byte[] data = incommingPacket.getData();
                bytes.add(data);
                i++;
            } while (i < count);
        }

        byte[] totalBytes = new byte[BtPacket.PACKET_SIZE * bytes.size()];
        final int totalBytesLength = totalBytes.length;
        int index = 0;
        for (byte[] aByte : bytes) {
            System.arraycopy(aByte, 0, totalBytes, index, aByte.length);
            index += aByte.length;
        }

        index = 2;
        int fileCount = 0;
        fileCount |= totalBytes[0] << 8;
        fileCount |= totalBytes[1];

        final ArrayList<RemoteFileEntry> entries = new ArrayList<>(fileCount);
        for (int i = 0; i < fileCount; i++) {
            if (index >= totalBytesLength) {
                break;
            }
            int fileSize = 0;
            fileSize |= ((totalBytes[index + 0] << 24) & 0xFF000000);
            fileSize |= ((totalBytes[index + 1] << 16) & 0xFF0000);
            fileSize |= ((totalBytes[index + 2] << 8) & 0xFF00);
            fileSize |= (totalBytes[index + 3] & 0xFF);
            index += 4;

            byte[] fileHashBytes = new byte[16];
            System.arraycopy(totalBytes, index, fileHashBytes, 0, fileHashBytes.length);
            String fileHash = new String(fileHashBytes);
            index += 16;
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (index >= totalBytesLength) {
                    break;
                }
                char nextByte = (char) totalBytes[index];
                index++;
                if (nextByte == 0) {
                    break;
                }
                sb.append(nextByte);
            }
            String fileName = sb.toString();
            entries.add(new RemoteFileEntry(fileName, fileSize, fileHashBytes));
            Log.d(TAG, "Název souboru: " + fileName + "; velikost souboru: " + fileSize + "; hash: " + fileHash);
        }

        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PARAM_REMOTE_ENTRY_LIST, entries);
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

    public static class RemoteFileEntry implements Parcelable {
        public final String name;
        public final int size;
        public final byte[] hash;

        public RemoteFileEntry(String name, int size, byte[] hash) {
            this.name = name;
            this.size = size;
            this.hash = hash;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeInt(this.size);
            dest.writeByteArray(this.hash);
        }

        protected RemoteFileEntry(Parcel in) {
            this.name = in.readString();
            this.size = in.readInt();
            this.hash = in.createByteArray();
        }

        public static final Parcelable.Creator<RemoteFileEntry> CREATOR = new Parcelable.Creator<RemoteFileEntry>() {
            @Override
            public RemoteFileEntry createFromParcel(Parcel source) {
                return new RemoteFileEntry(source);
            }

            @Override
            public RemoteFileEntry[] newArray(int size) {
                return new RemoteFileEntry[size];
            }
        };
    }

}
