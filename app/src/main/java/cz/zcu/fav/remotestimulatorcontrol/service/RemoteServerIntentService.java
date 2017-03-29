package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.Semaphore;

import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketAdvanced;

/**
 * Základní intent service pro komunikaci se vzdálenm souborovm systémem
 */
public abstract class RemoteServerIntentService extends IntentService {

    // region Constants
    private static final String TAG = "ARemoteServerService";

    protected static final String ACTION_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.action.";
    protected static final String PARAM_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.extra.";

    protected static final String ACTION_ECHO_SERVICE_DONE = ACTION_PREFIX + "ECHO_SERVICE_DONE";
    protected static final String PARAM_ECHO_SERVICE_NAME = PARAM_PREFIX + "ECHO_SERVICE_NAME";

    private static byte GLOBAL_ITERATION = 10;

    // endregion

    // region Variables
    private final Semaphore sem = new Semaphore(0);

    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_DATA_RECEIVED)) {
                byte[] received = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA_CONTENT);

                BtPacketAdvanced packet = new BtPacketAdvanced(received);
                if (packet.getIteration() != iterator) {
                    return;
                }

                incommingPacket = packet;

                sem.release();
            }
        }
    };

    private final BroadcastReceiver mServiceEchoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(ACTION_ECHO_SERVICE_DONE)) {
                onSubServiceDone(intent);
            }
        }
    };

    // Název služby/aktivity, která bude reagovat na výsledek
    protected String callbackName;
    protected BtPacketAdvanced incommingPacket;
    protected byte iterator;
    // endregion

    // region Constructors

    /**
     * Vytvoří novou intent service
     *
     * @param name Název služby
     */
    public RemoteServerIntentService(String name) {
        super(name);
        iterator = (byte) (GLOBAL_ITERATION++ % Byte.MAX_VALUE);
    }

    // endregion

    // region Private methods

    /**
     * Rozešle broadcast s informací, že podservice už je hotová
     *
     * @param intent {@link Intent} obsahující data
     */
    protected void sendEchoDone(Intent intent) {
        intent.setAction(ACTION_ECHO_SERVICE_DONE);
        intent.putExtra(PARAM_ECHO_SERVICE_NAME, callbackName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void waitOnSemaphore() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pošle data k odeslání do {@link BluetoothService}
     *
     * @param packet Packet, který se má odeslat
     */
    protected void sendData(BtPacketAdvanced packet) {
        packet.setIteration(iterator);
        BluetoothService.sendData(this, packet);
    }

    protected void registerReceiverInternal() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mDataReceiver, new IntentFilter(BluetoothService.ACTION_DATA_RECEIVED));
    }

    protected void unregisterReceiverInternal() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReceiver);
    }

    protected void onSubServiceDone(Intent intent) {}

    /**
     * Vrátí iterátor pro danou službu
     * Slouží pouze pro komunikaci se vzdáleným souborovým serverem
     * Když se iterátor dostane na maximální hodnotu bytu, tak se opět
     * vynuluje a jede od začátku
     *
     * @return Iterátor, pro identifikaci služby
     */
    protected byte getIterator() {
        return iterator;
    }

    // endregion

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(mServiceEchoReceiver,
                new IntentFilter(ACTION_ECHO_SERVICE_DONE));
        registerReceiverInternal();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceEchoReceiver);
        unregisterReceiverInternal();
        Log.d(TAG, "Zavírám intent service");
        super.onDestroy();
    }

}
