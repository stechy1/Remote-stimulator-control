package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cz.zcu.fav.remotestimulatorcontrol.R;

public class BluetoothService extends Service {

    // region Constants
    private static final Object lock = new Object();
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "BluetoothService";
    // Bluetooth UUID
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    // Typy zpráv, které prochází přes BluetoothCommunicationService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_SHOW = 5;

    // Příznaky přijaté z BluetoothCommunicationService Handleru
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_MAC = "device_mac";
    public static final String TOAST = "toast";

    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // endregion

    // region Variables
    // Vlákno udržující spojení
    private static ConnectedThread mConnectedThread;
    // Handler posílající zprávy o změně stavu bluetoothu
    private static Handler mHandler = null;
    // Název připojeného zařízení
    private static String deviceName;

    // Stav připojení
    public static int state = STATE_NONE;

    // Binder
    private final IBinder mBinder = new LocalBinder();

    // Vlákno ve kterém se vytváří nové spojení se zařízením
    private ConnectThread mConnectThread;
    // Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // endregion

    @Override
    public void onCreate() {
        Log.d("BluetoothService", "Služba spuštěna");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBinder;
    }

    /**
     * Pokusí se vytvořít spojení s zařízením
     *
     * @param macAddress Mac adresa spojovaného zařízení
     */
    public synchronized void connectToDevice(String macAddress) {
        if (mBluetoothAdapter == null) {
            return;
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        if (state == STATE_CONNECTING) {
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
     * Nastaví interní stav, ve kterém se spojení nachází
     *
     * @param state Nový stav
     *              @see #STATE_NONE
     *              @see #STATE_LISTEN
     *              @see #STATE_CONNECTING
     *              @see #STATE_CONNECTED
     */
    private void setState(int state) {
        BluetoothService.state = state;
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        }
    }

    /**
     * Ukončí všechna spojení a celou službu
     */
    private synchronized void stop() {
        setState(STATE_NONE);
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
        stopSelf();
    }

    /**
     * Pokud dojde k selhání spojení
     */
    private void connectionFailed() {
        Log.i(TAG, "Připojení selhalo");
        BluetoothService.this.stop();
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, getString(R.string.error_connect_failed));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Pokud dojde ke ztrátě spojení
     */
    private void connectionLost() {
        Log.i(TAG, "Připojení bylo ztraceno");
        BluetoothService.this.stop();
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, getString(R.string.error_lost_connection));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Zavolá se v případě vytvořeného spojení
     *
     * @param mmSocket Bluetooth socket
     * @param mmDevice Bluetooth device
     */
    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
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

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        deviceName = mmDevice.getName();

        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, deviceName);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);

    }

    @Override
    public boolean stopService(Intent name) {
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mBluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        stop();
        Log.d(TAG, "Služba ukončena");
        super.onDestroy();
    }

    // region Public methods
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (lock) {
            if (state != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    public synchronized void disconnect() {
        if (state == STATE_CONNECTING) {
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

        setState(STATE_LISTEN);
    }

    // endregion

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            } catch (IOException e) {
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
                e.printStackTrace();
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
                Log.e(TAG, "Selhalo odpojení socketu", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Nepodařilo se vytvořit dočasné sockety", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            String msg = "Hello world";
            write(msg.getBytes());

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);

                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (Exception e) {
                    connectionLost();
                    break;
                }

            }
        }

        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, buffer.length, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Nastala neočekávaná vyjímka během zápisu dat", e);
            }
        }

        void cancel() {
            try {
                mmSocket.close();

            } catch (IOException e) {
                Log.e(TAG, "Selhalo odpojení socketu", e);
            }
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
}