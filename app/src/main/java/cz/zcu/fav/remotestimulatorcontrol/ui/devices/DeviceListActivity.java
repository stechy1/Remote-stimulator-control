package cz.zcu.fav.remotestimulatorcontrol.ui.devices;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;

public class DeviceListActivity extends Activity {

    // region Constants
    // Logovací tag
    private static final String TAG = "DeviceListActivity";
    // endregion

    // region Variables
    private BluetoothAdapter mBtAdapter;

    /**
     * Click listener pro položky v listView
     */
    private final AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Zrušit vyhledávání ostatních zařízení, protože už jsme si vybrali
            mBtAdapter.cancelDiscovery();

            // Získání MAC adresy zařízení. Najdeme jí jako posledních 17 znaků ve View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent();
            intent.putExtra(BluetoothService.DEVICE_MAC, address);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_list);
        setResult(Activity.RESULT_CANCELED);

        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, R.layout.device_name);

        // Inicializace ListView
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Získání seznamu aktuálně párovaných zařízení
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Vypsání párovaných zařízení, pokud nějaká jsou
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            Log.i(TAG, "Nebylo nalezeno žádné spárované zařízení");
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }
}
