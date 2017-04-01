package cz.zcu.fav.remotestimulatorcontrol.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityMainBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketAdvanced;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;
import cz.zcu.fav.remotestimulatorcontrol.ui.about.AboutFragment;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.ConfigurationSharedPreferences;
import cz.zcu.fav.remotestimulatorcontrol.ui.devices.DeviceListActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.help.HelpFragment;
import cz.zcu.fav.remotestimulatorcontrol.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // region Constants

    private static final String TAG = "MainActivity";

    private static final String BLUETOOTH_DEVICE_NAME = "bluetooth_dev_name";
    private static final String BLUETOOTH_STATUS = "bluetooth_status";
    private static final String ACTIVITY_TITLE = "title";
    private static final String SELECTED_FRAGMENT_ID = "selected_fragment_id";

    private static final Map<Integer, Class> FRAGMENT_MAP = new HashMap<>(4);
    static {
        FRAGMENT_MAP.put(R.id.nav_experiments, MainFragment.class);
        FRAGMENT_MAP.put(R.id.nav_help, HelpFragment.class);
        FRAGMENT_MAP.put(R.id.nav_about, AboutFragment.class);
    }

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // endregion

    // region Variables

    private ActivityMainBinding mBinding;
    private DrawerLayout mDrawerLayout;
    private Menu mMenu;
    // Příznak určující, zda-li zařízení podporuje bluetooth
    private boolean mBluetoothSupport = false;
    private BluetoothAdapter mBluetoothAdapter;

    private String mConnectedDeviceName;
    private CharSequence mTitle;
    private int mBluetoothServiceStatus;
    private int mFragmentId;
    // BroadcastReceiver pro reakci na změnu stavu připojení k zařízení
    private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case BluetoothService.ACTION_STATE_CHANGE:
                    final int state = intent.getIntExtra(BluetoothService.EXTRA_STATE_CHANGE, BluetoothService.STATE_NONE);
                    setBluetoothStatusIcon(state);
                    if (state == BluetoothService.STATE_CONNECTED) {
                        BluetoothService.sendData(MainActivity.this, RemoteFileServer.getHelloPacket());
                    }
                    break;
                case BluetoothService.ACTION_DEVICE_NAME:
                    mConnectedDeviceName = intent.getStringExtra(BluetoothService.EXTRA_DEVICE_NAME);
                    break;
                case BluetoothService.ACTION_DATA_RECEIVED:
                    BtPacketAdvanced packet = new BtPacketAdvanced(intent.getByteArrayExtra(BluetoothService.EXTRA_DATA_CONTENT));
                    if (packet.hasCommand(RemoteFileServer.Codes.OP_HELLO)) {
                        Toast.makeText(MainActivity.this, new String(packet.getData(), 0, packet.getMaxDataSize()), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    // endregion

    // region Private methods

    /**
     * Zobrazí vybraný fragment
     *
     * @param fragmentId Id fragmentu
     */
    private boolean showFragment(int fragmentId) {
        if (!FRAGMENT_MAP.containsKey(fragmentId) || fragmentId == mFragmentId) {
            return false;
        }

        try {
            Fragment fragment = (Fragment) FRAGMENT_MAP.get(fragmentId).newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            mFragmentId = fragmentId;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Nastaví ikonu představující stav připojení k zařízení
     *
     * @param state Stav připojení
     */
    private void setBluetoothStatusIcon(int state) {
        switch (state) {
            case BluetoothService.STATE_CONNECTED:
                Log.d(TAG, "Zařízení je připojeno");
                if (mMenu != null) {
                    MenuItem item = mMenu.findItem(R.id.menu_main_connect);
                    if (item != null) {
                        item.setIcon(R.drawable.bluetooth_connected);
                    }
                }
                mBluetoothServiceStatus = BluetoothService.STATE_CONNECTED;
                setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                break;
            case BluetoothService.STATE_CONNECTING:
                Log.d(TAG, "Zařízení se připojuje");
                mBluetoothServiceStatus = BluetoothService.STATE_CONNECTING;
                setStatus(R.string.title_connecting);
                break;
            case BluetoothService.STATE_LISTEN:
            case BluetoothService.STATE_NONE:
                Log.d(TAG, "Zařízení je odpojeno");
                if (mMenu != null) {
                    MenuItem item = mMenu.findItem(R.id.menu_main_connect);
                    if (item != null) {
                        item.setIcon(R.drawable.bluetooth_connect);
                    }
                }
                mBluetoothServiceStatus = BluetoothService.STATE_NONE;
                setStatus(R.string.title_not_connected);
                break;
        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setSubtitle(subTitle);
    }

    /**
     * Vytvoří nový požadavek na zapnutí bluetooth
     */
    private void requestBluetoothEnable() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        super.setTitle(title);
    }

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setController(this);
        mBinding.executePendingBindings();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothSupport = mBluetoothAdapter != null;
        if (!mBluetoothSupport) {
            if (!ConfigurationSharedPreferences.isBTNotSupportedAlertShowed(getApplicationContext(), false)) {
                Snackbar.make(mBinding.frameContainer, R.string.bt_not_supported, Snackbar.LENGTH_LONG)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                ConfigurationSharedPreferences.setBTNotSupportedAlertShowed(getApplicationContext(), true);
                                super.onDismissed(snackbar, event);
                            }
                        })
                        .show();
            }
        }

        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);

        mDrawerLayout = mBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = mBinding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        CharSequence title;
        if (savedInstanceState != null) {
            setBluetoothStatusIcon(savedInstanceState.getInt(BLUETOOTH_STATUS));
            title = savedInstanceState.getString(ACTIVITY_TITLE);
            mFragmentId = savedInstanceState.getInt(SELECTED_FRAGMENT_ID);
        } else {
            setBluetoothStatusIcon(BluetoothService.STATE_NONE);
            title = getString(R.string.nav_configurations);
            showFragment(R.id.nav_experiments);
        }

        setTitle(title);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothService.ACTION_DEVICE_NAME);
        filter.addAction(BluetoothService.ACTION_STATE_CHANGE);
        //filter.addAction(BluetoothService.ACTION_DATA_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBluetoothStateReceiver, filter);

        mBinding.navView.setCheckedItem(mFragmentId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBluetoothSupport) {
            if (!BluetoothService.isRunning()) {
                startService(new Intent(this, BluetoothService.class));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    try {
                        Log.d(TAG, "Pokus o připojení k bluetooth zařízení");
                        String mac = data.getStringExtra(BluetoothService.EXTRA_DEVICE_MAC);
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
                        BluetoothService.changeState(this, BluetoothService.RequestState.STATE_ON, device);
                    } catch (Exception e) {
                        Toast.makeText(this, R.string.unknown_device, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Nastala neočekávaná vyjímka při připojování k bluetooth zařízení", e);
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Bluetooth je aktivovaný");
                } else {
                    Log.i(TAG, "Bluetooth není aktivovaný");
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BLUETOOTH_DEVICE_NAME, mConnectedDeviceName);
        outState.putInt(BLUETOOTH_STATUS, mBluetoothServiceStatus);
        outState.putString(ACTIVITY_TITLE, (String) mTitle);
        outState.putInt(SELECTED_FRAGMENT_ID, mFragmentId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBluetoothStateReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        setBluetoothStatusIcon(mBluetoothServiceStatus);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_connect:
                if (!mBluetoothSupport) {
                    return false;
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    requestBluetoothEnable();
                    return false;
                }

                switch (mBluetoothServiceStatus) {
                    case BluetoothService.STATE_NONE:
                    case BluetoothService.STATE_LISTEN:
                        startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE);
                        break;
                    case BluetoothService.STATE_CONNECTED:
                        BluetoothService.sendData(this, RemoteFileServer.getByePacket());
                        BluetoothService.changeState(this, BluetoothService.RequestState.STATE_OFF);
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            mDrawerLayout.closeDrawers();
            return false;
        }

        showFragment(id);
        item.setChecked(true);
        setTitle(item.getTitle());

        mDrawerLayout.closeDrawers();
        return true;
    }
}
