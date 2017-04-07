package cz.zcu.fav.remotestimulatorcontrol.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import cz.zcu.fav.remotestimulatorcontrol.model.stimulator.BtPacketAdvanced;

/**
 * Základní intent service pro komunikaci se vzdálenm souborovm systémem
 */
public abstract class RemoteServerIntentService extends IntentService {

    // region Constants
    private static final String TAG = "ARemoteServerService";

    private static final int DEFAULT_QUEUE_SIZE = 100;
    protected static final int DEFAULT_WAIT_TIME_FOR_PACKET = 3;
    protected static final TimeUnit DEFAULT_WAIT_UNIT = TimeUnit.SECONDS;

    protected static final String ACTION_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.action.";
    protected static final String PARAM_PREFIX = "cz.zcu.fav.remotestimulatorcontrol.service.extra.";

    public static final String ACTION_ECHO_SERVICE_DONE = ACTION_PREFIX + "ECHO_SERVICE_DONE";
    public static final String PARAM_ECHO_SERVICE_NAME = PARAM_PREFIX + "ECHO_SERVICE_NAME";
    public static final String PARAM_SRC_SERVICE_NAME = PARAM_PREFIX + "SRC_SERVICE_NAME";
    public static final String PARAM_ECHO_SERVICE_STATUS = PARAM_PREFIX + "ECHO_SERVICE_STATUS";
    public static final int VALUE_ECHO_SERVICE_STATUS_OK = 0;
    public static final int VALUE_ECHO_SERVICE_STATUS_ERROR = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VALUE_ECHO_SERVICE_STATUS_OK, VALUE_ECHO_SERVICE_STATUS_ERROR})
    public @interface ServiceStatus {}


    public static final String ACTION_INCREASE_PROGRESS = ACTION_PREFIX + "INCREASE_PROGRESS";
    public static final String ACTION_INCREASE_MAX_PROGRESS = ACTION_PREFIX + "INCREASE_MAX_MAIN_PROGRESS";
    public static final String ACTION_UPDATE_PROGRESS_MESSAGE = ACTION_PREFIX + "UPDATE_PROGRESS_MESSAGE";
    public static final String PARAM_MAIN_PROGRESS = PARAM_PREFIX + "MAIN_PROGRESS";
    public static final String PARAM_MAX_PROGRESS = PARAM_PREFIX + "MAX_MAIN_PROGRESS";
    public static final String PARAM_PROGRESS_MESSAGE = PARAM_PREFIX + "PROGRESS_TITLE";

    private static byte GLOBAL_ITERATION = 10;

    // endregion

    // region Variables
    protected final BlockingQueue<BtPacketAdvanced> incommintPackets = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);

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

                incommintPackets.add(packet);
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
    //protected BtPacketAdvanced incommingPacket;
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
     * Předpokládá se, že akce byla úspěšná
     *
     * @param intent {@link Intent} obsahující data
     */
    protected void sendEchoDone(Intent intent) {
        sendEchoDone(intent, VALUE_ECHO_SERVICE_STATUS_OK);
    }

    /**
     * Rozešle broadcast s informací, že podservice už je hotová
     *
     * @param intent {@link Intent} obsahující data
     * @param status Příznak určující, zda-li byla operace úspěšná, či nikoliv
     */
    protected void sendEchoDone(Intent intent, @ServiceStatus int status) {
        intent.setAction(ACTION_ECHO_SERVICE_DONE);
        intent.putExtra(PARAM_ECHO_SERVICE_NAME, callbackName);
        intent.putExtra(PARAM_SRC_SERVICE_NAME, getServiceName());
        intent.putExtra(PARAM_ECHO_SERVICE_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

//    /**
//     * Počká na semaforu, dokud "nepadne zelená"
//     */
//    protected void waitOnSemaphore() {
//        try {
//            sem.acquire();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Pošle data k odeslání do {@link BluetoothService}
     *
     * @param packet Packet, který se má odeslat
     */
    protected void sendData(BtPacketAdvanced packet) {
        packet.setIteration(iterator);
        BluetoothService.sendData(this, packet);
    }

    /**
     * Pošle broadcast s informací o aktualizaci hlavního progresu
     *
     * @param progress Nový progress
     */
    protected void increaseMainProgress(int progress) {
        Intent intent = new Intent(ACTION_INCREASE_PROGRESS);
        intent.putExtra(PARAM_MAIN_PROGRESS, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Zvýší maximální hodnotu progress baru o zadanou hodnotu
     *
     * @param delta O kolik se zvýší maximální hodnota progress baru
     */
    protected void increaseMaxProgress(int delta) {
        Intent intent = new Intent(ACTION_INCREASE_MAX_PROGRESS);
        intent.putExtra(PARAM_MAX_PROGRESS, delta);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Pošle broadcast s informací o aktualizaci nadpisu progressu
     *
     * @param resId Resource id identifikující text, který se má zobrazit
     */
    protected void updateProgressMessage(@StringRes int resId) {
        updateProgressMessage(getString(resId));
    }

    /**
     * Pošle broadcast s informací o aktualizaci nadpisu progressu
     *
     * @param title Nový titulek, který se má zobrazit v nadpisu
     */
    protected void updateProgressMessage(CharSequence title) {
        Intent intent = new Intent(ACTION_UPDATE_PROGRESS_MESSAGE);
        intent.putExtra(PARAM_PROGRESS_MESSAGE, title);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Vrátí název služby
     *
     * @return Název služby
     */
    protected abstract String getServiceName();

    /**
     * Reakce na dokončení subservice
     *
     * @param intent {@link Intent} Intent obsahující výsledná data
     */
    protected void onSubServiceDone(Intent intent) {}

    // endregion

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(mServiceEchoReceiver,
                new IntentFilter(ACTION_ECHO_SERVICE_DONE));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDataReceiver, new IntentFilter(BluetoothService.ACTION_DATA_RECEIVED));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceEchoReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReceiver);
        Log.d(TAG, "Zavírám intent service: " + getServiceName());
        super.onDestroy();
    }
}
