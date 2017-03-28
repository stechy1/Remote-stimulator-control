package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;

public class BluetoothService extends Service {

    // region Constants
    private static final Object lock = new Object();
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "BluetoothService";
    private static final String ACTION_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.action.";
    private static final String EXTRA_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.extra";
    private static final String PARAM_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.PARAM";


    // Akce service
    public static final String ACTION_DEVICE_NAME = ACTION_PREFIX + "EXTRA_DEVICE_NAME";
    public static final String ACTION_STATE_CHANGE = ACTION_PREFIX + "BLUETOOTH_STATE_CHANGE";
    public static final String ACTION_CONNECTION_FAILED = ACTION_PREFIX + "CONNECTION_FAILED";
    public static final String ACTION_CONNECTION_LOST = ACTION_PREFIX + "CONNECTION_LOST";
    public static final String ACTION_DATA_RECEIVED = ACTION_PREFIX + "DATA_RECEIVED";
    public static final String ACTION_REQUEST_STATE_CHANGE = ACTION_PREFIX + "REQUEST_STATE_CHANGE";
    public static final String ACTION_SEND_DATA = ACTION_PREFIX + "SEND_DAT";

    // Názvy konstant představující proměnné pro různé akce
    public static final String EXTRA_DEVICE_NAME = EXTRA_PREFIX + "EXTRA_DEVICE_NAME";
    public static final String EXTRA_DEVICE_MAC = EXTRA_PREFIX + "EXTRA_DEVICE_MAC";
    public static final String EXTRA_STATE_CHANGE = EXTRA_PREFIX + "EXTRA_STATE_CHANGE";
    public static final String EXTRA_REQUEST_STATE = EXTRA_PREFIX + "EXTRA_REQUEST_STATE";
    public static final String EXTRA_DEVICE = EXTRA_PREFIX + "EXTRA_DEVICE";
    public static final String EXTRA_DATA_CONTENT = EXTRA_PREFIX + "EXTRA_DATA_CONTENT";

    public enum RequestState {
        STATE_ON, STATE_OFF
    }
//    public static final String PARAM_STATE_OFF = PARAM_PREFIX + "PARAM_STATE_OFF";
//    public static final String PARAM_STATE_ON = PARAM_PREFIX + "PARAM_STATE_ON";

    // Stav připojení zařízení
    // Výchozí stav
    public static final int STATE_NONE = 0;
    // Čekám na příchozí spojení
    public static final int STATE_LISTEN = 1;
    // Připojuji se k zařízení
    public static final int STATE_CONNECTING = 2;
    // Jsem spojený a můžu komunikovat
    public static final int STATE_CONNECTED = 3;
    // endregion

    // region Variables
    private static boolean running = false;

    private final BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(ACTION_REQUEST_STATE_CHANGE)) {
                final RequestState state = (RequestState) intent.getSerializableExtra(EXTRA_REQUEST_STATE);
                switch (state) {
                    case STATE_ON:
                    Log.d(TAG, "Připojuji se k zařízení");
                    connectToDevice((BluetoothDevice) intent.getParcelableExtra(EXTRA_DEVICE));
                        break;
                    case STATE_OFF:
                    Log.d(TAG, "Odpojuji zařízení");
                    stop();
                        break;
                }
            }
        }
    };
    private final BroadcastReceiver mSenderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(ACTION_SEND_DATA)) {
                final byte[] bytes = intent.getByteArrayExtra(EXTRA_DATA_CONTENT);
                write(bytes);
            }
        }
    };

    // Vlákno udržující spojení
    private ConnectedThread mConnectedThread;
    // Vlákno ve kterém se vytváří nové spojení se zařízením
    private ConnectThread mConnectThread;
    // Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // Stav připojení
    private int mState = STATE_NONE;
    // endregion

    // region Public static methods

    /**
     * Statická pomocná metoda pro odeslání dat pomocí broadcastu do služby
     *
     * @param context {@link Context}
     * @param packet {@link BtPacket} Packet, který se má odeslat
     */
    public static void sendData(Context context, BtPacket packet) {
        Intent intent = new Intent(ACTION_SEND_DATA);
        intent.putExtra(EXTRA_DATA_CONTENT, packet.getContent());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void changeState(Context context, RequestState requestState) {
        changeState(context, requestState, null);
    }

    public static void changeState(Context context, RequestState requestState, BluetoothDevice device) {
        Intent intent = new Intent(BluetoothService.ACTION_REQUEST_STATE_CHANGE);
        intent.putExtra(BluetoothService.EXTRA_REQUEST_STATE, requestState);
        if (device != null) {
            intent.putExtra(EXTRA_DEVICE, device);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    // endregion

    // region Private methods
    /**
     * Nastaví interní stav, ve kterém se spojení nachází
     *
     * @param state Nový stav
     *              @see #STATE_NONE
     *              @see #STATE_LISTEN
     *              @see #STATE_CONNECTING
     *              @see #STATE_CONNECTED
     */
    private synchronized void setState(int state) {
        mState = state;

        Intent intent = new Intent(ACTION_STATE_CHANGE);
        intent.putExtra(EXTRA_STATE_CHANGE, state);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Ukončí všechna spojení a celou službu
     */
    private synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        setState(STATE_NONE);
    }

    /**
     * Pokud dojde k selhání spojení
     */
    private void connectionFailed() {
        Log.w(TAG, "Připojení selhalo");
        stop();

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CONNECTION_FAILED));
    }

    /**
     * Pokud dojde ke ztrátě spojení
     */
    private void connectionLost() {
        Log.i(TAG, "Připojení bylo ztraceno");
        stop();

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CONNECTION_LOST));
    }

    /**
     * Zavolá se v případě vytvořeného spojení
     *
     * @param socket Bluetooth socket
     * @param device Bluetooth device
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Intent intent = new Intent(ACTION_DEVICE_NAME);
        intent.putExtra(EXTRA_DEVICE_NAME, device.getName());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        setState(STATE_CONNECTED);
    }

    /**
     * Pokusí se vytvořít spojení s zařízením
     *
     * @param device {@link BluetoothDevice}
     */
    private synchronized void connectToDevice(BluetoothDevice device) {
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Pošle data přes bluetooth
     *
     * @param out Data
     */
    private void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (lock) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    // endregion

    @Override
    public void onCreate() {
        Log.d(TAG, "Služba spuštěna");
        LocalBroadcastManager.getInstance(this).registerReceiver(mStatusReceiver, new IntentFilter(ACTION_REQUEST_STATE_CHANGE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSenderReceiver, new IntentFilter(ACTION_SEND_DATA));
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        running = true;
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        stop();
        running = false;
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        stop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStatusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSenderReceiver);
        Log.d(TAG, "Služba ukončena");
        super.onDestroy();
    }

    // region Public methods

    /**
     * @return True, pokud služba běží, jinak false
     */
    public static boolean isRunning() {
        return running;
    }

    // endregion

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                // Vytvoření nezabezpečeného spojení
                // Nikdo neví, proč to nejde jednoduššeji
                Class[] clsArr = new Class[STATE_LISTEN];
                clsArr[STATE_NONE] = Integer.TYPE;
                Method method = device.getClass().getMethod("createRfcommSocket", clsArr);
                Object[] objArr = new Object[STATE_LISTEN];
                objArr[STATE_NONE] = STATE_LISTEN;
                tmp = (BluetoothSocket) method.invoke(device, objArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            setName("ConnectThread");
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                connectionFailed();
                return;
            }
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Selhalo odpojení socketu");
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final byte[] data = new byte[BtPacket.PACKET_SIZE];

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Nepodařilo se vytvořit dočasné sockety");
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] tempBuffer = new byte[BtPacket.PACKET_SIZE];
            int count;
            int totalSize = 0;

            while (true) {
                try {
                    Arrays.fill(tempBuffer, (byte)0);
                    count = mmInStream.read(tempBuffer);

                    Log.d(TAG, "Received: " + count + " bytes; " + Arrays.toString(tempBuffer));

                    int freeBytes = BtPacket.PACKET_SIZE - totalSize;
                    int byteCount = count > freeBytes ? freeBytes : count;

                    System.arraycopy(tempBuffer, 0, data, totalSize, byteCount);
                    totalSize += count;

                    if (totalSize >= BtPacket.PACKET_SIZE) {

                        Log.d(TAG, "Vytvářím nový packet: " + Arrays.toString(data));
                        Intent intent = new Intent(ACTION_DATA_RECEIVED);
                        intent.putExtra(EXTRA_DATA_CONTENT, Arrays.copyOf(data, data.length));
                        LocalBroadcastManager.getInstance(BluetoothService.this).sendBroadcast(intent);
                        totalSize %= BtPacket.PACKET_SIZE;
                        Log.d(TAG, "Bylo přijato navíc: " + totalSize + " bytů");
                        // Zkopírování zbývajících dat z bufferu do hlavních dat pro příští použití
                        Arrays.fill(data, totalSize, data.length, (byte) 0);
                        System.arraycopy(tempBuffer, count - totalSize, data, 0, totalSize);
                        Log.d(TAG, "Zbyvajici data byla prenesena do noveho kola. " + Arrays.toString(data));
                    }
                } catch (Exception e) {
                    connectionLost();
                    break;
                }

            }
        }

        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                Log.d(TAG, "Bylo odesláno: " + Arrays.toString(buffer));

            } catch (IOException e) {
                Log.e(TAG, "Nastala neočekávaná vyjímka během zápisu dat");
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Selhalo odpojení socketu");
            }
        }
    }
}