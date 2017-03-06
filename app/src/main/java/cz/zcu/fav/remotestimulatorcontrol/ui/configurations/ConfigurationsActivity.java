package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.GlobalPreferences;
import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationsBinding;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationManager;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationComparator;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MetaData;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;
import cz.zcu.fav.remotestimulatorcontrol.ui.about.AboutActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ConfigurationDetailActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.duplicate.ConfigurationDuplicateActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation.ConfigurationImportActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.rename.ConfigurationRenameActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.sorting.ConfigurationSortingActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.devices.DeviceListActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.help.HelpActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.settings.SettingsActivity;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class ConfigurationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerView.OnItemTouchListener {

    // region Constants
    // Přiznaky pro řezení konfigurací
    public static final int FLAG_SORT_NAME = 1 << 0;
    public static final int FLAG_SORT_TYPE = 1 << 1;
    public static final int FLAG_SORT_MEDIA = 1 << 2;
    // Logovací tag
    private static final String TAG = "ConfigActivity";
    // Stringy pro ukládání stavu instance
    private static final String SAVE_STATE_IN_ACTION_MODE = "action_mode";
    private static final String SAVE_STATE_SELECTED_ITEMS_COUNT = "selected_items_count";
    private static final String SAVE_STATE_SORTING = "sorting";
    private static final String BLUETOOTH_DEVICE_NAME = "bluetooth_dev_name";
    private static final String BLUETOOTH_STATUS = "bluetooth_status";
    // Seznam requestů
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_NEW_CONFIGURATION = 3;
    private static final int REQUEST_DETAIL_CONFIGURATION = 4;
    private static final int REQUEST_RENAME_CONFIGURATION = 5;
    private static final int REQUEST_DUPLICATE_CONFIGURATION = 6;
    private static final int REQUEST_SORTING = 7;
    private static final int REQUEST_SETTINGS = 8;
    private static final int REQUEST_IMPORT = 9;
    // endregion

    // region Variables
    // Udržuje informaci, zda-li je recyclerView prázdný, či nikoliv
    public final ObservableBoolean isRecyclerViewEmpty = new ObservableBoolean(false);

    // Udržuje informaci, zda-li se má zobrazit koncovka jednotlivých souborů, či nikoliv
    private final ObservableBoolean mShowExtension = new ObservableBoolean(false);
    // BroadcastReceiver pro nastavení názvu zařízení
    private final BroadcastReceiver mBluetoothDeviceNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_DEVICE_NAME)) {
                mConnectedDeviceName = intent.getStringExtra(BluetoothService.DEVICE_NAME);
            }
        }
    };
    // BroadcastReceiver pro reakci na změnu stavu připojení k zařízení
    private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_STATE_CHANGE)) {
                final int state = intent.getIntExtra(BluetoothService.STATE_CHANGE, BluetoothService.STATE_NONE);
                setBluetoothStatusIcon(state);
            }
        }
    };

    // Reference na hlavní layout
    private DrawerLayout mDrawerLayout;
    // Reference na menu
    private Menu mMenu;
    // Action bar
    private ActionMode mActionMode;
    // Reference na recycler view
    private RecyclerView mRecyclerView;
    // FAB pro přidání nové konfigurace
    private FloatingActionButton mFab;
    // Gesture detector
    private GestureDetectorCompat mGestureDetector;
    // Správce experimentů
    private ConfigurationManager mManager;
    @SuppressWarnings("unused")
    public final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mManager.refresh();
        }
    };
    // Binding do hlavní aktivity
    private ActivityConfigurationsBinding mBinding;
    // Název připojeného zařízení
    private String mConnectedDeviceName;
    // Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    private int mBluetoothServiceStatus;
    // Reference na adapter pro recycler view
    private ConfigurationAdapter mConfigurationAdapter;
    // Příznak určující, zda-li je potvrzeno smazání vybraných konfigurací
    private boolean mDeleteConfirmed = true;

    private final Handler.Callback managerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean success;
            int snackbarMessage = -1;
            switch (msg.what) {
                case ConfigurationManager.MESSAGE_CONFIGURATIONS_LOADED:
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    isRecyclerViewEmpty.set(mConfigurationAdapter.getItemCount() == 0);
                    mBinding.recyclerViewConfigurations.getAdapter().notifyDataSetChanged();
                    break;
                case ConfigurationManager.MESSAGE_NAME_EXISTS:
                    snackbarMessage = R.string.error_name_exists;
                    break;
                case ConfigurationManager.MESSAGE_INVALID_NAME:
                    snackbarMessage = R.string.error_invalid_name;
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_CREATE:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_create_successful : R.string.manager_message_create_unsuccessful;

                    if (success) {
                        isRecyclerViewEmpty.set(false);
                        mConfigurationAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_IMPORT:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_import_successful : R.string.manager_message_import_unsuccessful;

                    if (success) {
                        isRecyclerViewEmpty.set(false);
                        mConfigurationAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_RENAME:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_rename_successful : R.string.manager_message_rename_unsuccessful;

                    if (success) {
                        mConfigurationAdapter.notifyItemChanged(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_PREPARED_TO_DELETE:
                    List<Integer> itemsToDelete = mConfigurationAdapter.getSelectedItemsIndex();
                    mConfigurationAdapter.saveSelectedItems();

                    for (Integer item : itemsToDelete)
                        mConfigurationAdapter.notifyItemRemoved(item);

                    mConfigurationAdapter.clearSelections();
                    mActionMode.setTitle(getString(R.string.selected_count, mConfigurationAdapter.getSelectedItemCount()));

                    Snackbar.make(mFab, R.string.manager_message_delete_successful, Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mManager.undoDelete();

                                    mDeleteConfirmed = false;
                                }
                            })
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (mDeleteConfirmed) {
                                        mManager.confirmDelete();
                                        boolean empty = mConfigurationAdapter.getItemCount() == 0;
                                        isRecyclerViewEmpty.set(empty);
                                        if (empty && mActionMode != null) {
                                            mActionMode.finish();
                                        }
                                    } else {
                                        mDeleteConfirmed = true;
                                    }

                                    super.onDismissed(snackbar, event);
                                }
                            })
                            .show();
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_UNDO_DELETE:
                    mConfigurationAdapter.restoreSelectedItems();
                    List<Integer> itemsToUndo = mConfigurationAdapter.getSelectedItemsIndex();

                    for (Integer item : itemsToUndo)
                        mConfigurationAdapter.notifyItemInserted(item);

                    if (mActionMode != null) {
                        mActionMode.setTitle(getString(R.string.selected_count, mConfigurationAdapter.getSelectedItemCount()));
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_UPDATE:
                    mConfigurationAdapter.notifyItemChanged(msg.arg1);
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_DUPLICATE:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_duplicate_successful : R.string.manager_message_duplicate_unsuccessful;

                    if (success) {
                        mConfigurationAdapter.notifyItemChanged(msg.arg2);
                    }
                    break;
            }

            if (snackbarMessage != -1) {
                Snackbar.make(mFab, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
    };
    private final Handler managerhandler = new Handler(managerCallback);


    // Přiznak určující, podle čeho se budou konfigurace řadit
    private int mSortingFlag;
    // Příznak určující, zda-li zařízení podporuje bluetooth
    private boolean mBluetoothSupport = false;
    // endregion

    // region Private methods
    /**
     * Inicializuje Recycler view
     * Nastaví Layout manager, Item decoration, Item animator, adapter a onClickListener
     */
    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerViewConfigurations;
        mConfigurationAdapter = new ConfigurationAdapter(mManager.configurations, mShowExtension);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setItemAnimator(new LandingAnimator(new FastOutLinearInInterpolator()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mConfigurationAdapter));
        mRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());
    }

    /**
     * Vytvoří nový požadavek na zapnutí bluetooth
     */
    private void requestBluetoothEnable() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    /**
     * Načte globální nastavení
     */
    private void loadGlobalSettings() {
        GlobalPreferences.setDefaultExtension(this, GlobalPreferences.getDefaultExtension(this, MetaData.getDefaultExtension().name()));
        MetaData.setDefaultExtension(ExtensionType.valueOf(GlobalPreferences.getDefaultExtension(this, MetaData.getDefaultExtension().name())));

        GlobalPreferences.setExtensionVisible(this, GlobalPreferences.isExtensionVisible(this, mShowExtension.get()));
        mShowExtension.set(GlobalPreferences.isExtensionVisible(this, mShowExtension.get()));
    }

    /**
     * Obnoví data v recyclerView
     */
    private void refreshRecyclerView() {
        mBinding.swipeRefreshLayout.setRefreshing(true);
        mManager.refresh();
    }

    private boolean isFlagValid(int flag, int value) {
        return (value & flag) == flag;
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
                    mMenu.getItem(0).setIcon(R.drawable.bluetooth_connected);
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
                    mMenu.getItem(0).setIcon(R.drawable.bluetooth_connect);
                }
                mBluetoothServiceStatus = BluetoothService.STATE_NONE;
                setStatus(R.string.title_not_connected);
                break;
        }
    }

    /**
     * Naparsuje comparator
     *
     * @return Komparátor
     */
    private Comparator<AConfiguration> parseComparator() {
        List<ConfigurationComparator> comparators = new ArrayList<>();

        if (isFlagValid(FLAG_SORT_MEDIA, mSortingFlag)) {
            comparators.add(ConfigurationComparator.MEDIA_COMPARATOR);
        }
        if (isFlagValid(FLAG_SORT_TYPE, mSortingFlag)) {
            comparators.add(ConfigurationComparator.TYPE_COMPARATOR);
        }
        if (isFlagValid(FLAG_SORT_NAME, mSortingFlag)) {
            comparators.add(ConfigurationComparator.NAME_COMPARATOR);
        }

        return ConfigurationComparator.getComparator(comparators);
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

    private void setEnabledPropertyOnActionModeItems(boolean enabled) {
        if (mActionMode == null) {
            return;
        }

        View duplicateBtn = findViewById(R.id.context_duplicate);
        View renameBtn = findViewById(R.id.context_rename);

        if (duplicateBtn == null || renameBtn == null) {
            return;
        }

        duplicateBtn.setEnabled(enabled);
        renameBtn.setEnabled(enabled);
    }

    private void toggleSelection(int index) {
        mConfigurationAdapter.toggleSelection(index);
        String title = getString(R.string.selected_count, mConfigurationAdapter.getSelectedItemCount());
        mActionMode.setTitle(title);

        setEnabledPropertyOnActionModeItems(mConfigurationAdapter.getSelectedItemCount() == 1);
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new ConfigurationManager(getFilesDir());
        mManager.setHandler(managerhandler);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configurations);
        mBinding.setController(this);
        mBinding.setIsRecyclerViewEmpty(isRecyclerViewEmpty);

        initRecyclerView();

        mFab = mBinding.fabNewConfiguration;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothSupport = mBluetoothAdapter != null;
        if (!mBluetoothSupport) {
            if (!ConfigurationSharedPreferences.isBTNotSupportedAlertShowed(getApplicationContext(), false)) {
                Snackbar.make(mFab, R.string.bt_not_supported, Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        if (savedInstanceState != null) {
            mSortingFlag = savedInstanceState.getInt(SAVE_STATE_SORTING);
            boolean isInActionMode = savedInstanceState.getBoolean(SAVE_STATE_IN_ACTION_MODE);
            mConnectedDeviceName = savedInstanceState.getString(BLUETOOTH_DEVICE_NAME);
            if (isInActionMode && mActionMode == null) {
                startSupportActionMode(new ActionBarCallback());
                List<Integer> selectedItems = savedInstanceState.getIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT);
                assert selectedItems != null;
                mConfigurationAdapter.selectItems(selectedItems);
                mActionMode.setTitle(getString(R.string.selected_count, selectedItems.size()));
            } else {
                mBluetoothServiceStatus = savedInstanceState.getInt(BLUETOOTH_STATUS);
            }

        } else {
            mSortingFlag = ConfigurationSharedPreferences.getSortingFlag(this, FLAG_SORT_NAME);
            setBluetoothStatusIcon(BluetoothService.STATE_NONE);
        }

        mManager.setConfigurationComparator(parseComparator());

        registerReceiver(mBluetoothDeviceNameReceiver, new IntentFilter(BluetoothService.ACTION_DEVICE_NAME));
        registerReceiver(mBluetoothStateReceiver, new IntentFilter(BluetoothService.ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadGlobalSettings();
        refreshRecyclerView();

        if (mBluetoothSupport) {
            if (!BluetoothService.isRunning()) {
                startService(new Intent(this, BluetoothService.class));
            }
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVE_STATE_IN_ACTION_MODE, mActionMode != null);
        outState.putIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT, mConfigurationAdapter.getSelectedItemsIndex());
        outState.putInt(SAVE_STATE_SORTING, mSortingFlag);
        outState.putString(BLUETOOTH_DEVICE_NAME, mConnectedDeviceName);
        outState.putInt(BLUETOOTH_STATUS, mBluetoothServiceStatus);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBluetoothDeviceNameReceiver);
        unregisterReceiver(mBluetoothStateReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {

                    try {
                        Log.d(TAG, "Pokus o vytvoření naslouchací služby bluetooth");
                        String mac = data.getStringExtra(BluetoothService.DEVICE_MAC);
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
                        Intent intent = new Intent(BluetoothService.ACTION_REQUEST_STATE_CHANGE);
                        intent.putExtra(BluetoothService.REQUEST_STATE, BluetoothService.REQUEST_STATE_ON);
                        intent.putExtra(BluetoothService.DEVICE, device);
                        sendBroadcast(intent);

                    } catch (Exception e) {
                        Toast.makeText(this, R.string.unknown_device, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Nastala neočekávaná vyjímka při vytváření naslouchací služby bluetooth", e);
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
            case REQUEST_NEW_CONFIGURATION:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra(ConfigurationFactoryActivity.CONFIGURATION_NAME);
                    ConfigurationType type = ConfigurationType.valueOf((String) data.getSerializableExtra(ConfigurationFactoryActivity.CONFIGURATION_TYPE));
                    mManager.create(name, type);
                }
                break;
            case REQUEST_DETAIL_CONFIGURATION:
                if (resultCode == RESULT_OK) {
                    boolean reload = data.getBooleanExtra(ConfigurationDetailActivity.CONFIGURATION_RELOAD, false);
                    int id = data.getIntExtra(ConfigurationDetailActivity.CONFIGURATION_ID, ConfigurationDetailActivity.CONFIGURATION_UNKNOWN_ID);
                    if (id == ConfigurationDetailActivity.CONFIGURATION_UNKNOWN_ID) {
                        return;
                    }

                    if (reload) {
                        mManager.update(id);
                    }
                }
                break;
            case REQUEST_RENAME_CONFIGURATION:
                if (resultCode == RESULT_OK) {
                    int id = data.getIntExtra(ConfigurationRenameActivity.CONFIGURATION_ID, ConfigurationRenameActivity.CONFIGURATION_UNKNOWN_ID);
                    String name = data.getStringExtra(ConfigurationRenameActivity.CONFIGURATION_NAME);

                    if (id == ConfigurationRenameActivity.CONFIGURATION_UNKNOWN_ID) {
                        return;
                    }

                    mManager.rename(id, name);

                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }
                break;
            case REQUEST_DUPLICATE_CONFIGURATION:
                if (resultCode == RESULT_OK) {
                    int id = data.getIntExtra(ConfigurationDuplicateActivity.CONFIGURATION_ID, ConfigurationDuplicateActivity.CONFIGURATION_UNKNOWN_ID);
                    String name = data.getStringExtra(ConfigurationDuplicateActivity.CONFIGURATION_NAME);

                    if (id == ConfigurationRenameActivity.CONFIGURATION_UNKNOWN_ID) {
                        return;
                    }

                    mManager.duplicate(id, name);

                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }
                break;
            case REQUEST_SORTING:
                if (resultCode == RESULT_OK) {
                    int sortingFlagNew = data.getIntExtra(ConfigurationSortingActivity.SORTING_FLAG, mSortingFlag);
                    if (sortingFlagNew == mSortingFlag) {
                        return;
                    }

                    mSortingFlag = sortingFlagNew;
                    ConfigurationSharedPreferences.setSortingFlag(this, mSortingFlag);
                    mManager.setConfigurationComparator(parseComparator());
                    mManager.refresh();
                }
                break;
            case REQUEST_IMPORT:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(ConfigurationImportActivity.CONFIGURATION_FILE_PATH);
                    String name = data.getStringExtra(ConfigurationImportActivity.CONFIGURATION_NAME);
                    ConfigurationType configurationType = (ConfigurationType) data.getSerializableExtra(ConfigurationImportActivity.CONFIGURATION_TYPE);
                    ExtensionType extensionType = (ExtensionType) data.getSerializableExtra(ConfigurationImportActivity.CONFIGURATION_EXTENSION);

                    mManager.importt(name, path, configurationType, extensionType);
                }
                break;
        }
    }

    // region Menu handlers

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem bluetoothMenuItem = menu.findItem(R.id.menu_main_connect);
        bluetoothMenuItem.setEnabled(mBluetoothSupport);
        setBluetoothStatusIcon(mBluetoothServiceStatus);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
                        Intent intent = new Intent(BluetoothService.ACTION_REQUEST_STATE_CHANGE);
                        intent.putExtra(BluetoothService.REQUEST_STATE, BluetoothService.REQUEST_STATE_OFF);
                        sendBroadcast(intent);
                        break;
                }
                break;
            case R.id.menu_main_sort:
                Intent intent = new Intent(this, ConfigurationSortingActivity.class);
                intent.putExtra(ConfigurationSortingActivity.SORTING_FLAG, mSortingFlag);
                startActivityForResult(intent, REQUEST_SORTING);
                break;
            case R.id.menu_main_import:
                startActivityForResult(new Intent(this, ConfigurationImportActivity.class), REQUEST_IMPORT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    // endregion

    // region RecyclerView itemTouch listener

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // Zde opravdu nic není
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // Zde opravdu nic není
    }

    // endregion

    // region Public methods
    // Kliknutí na FAB tlačítko pro vytvoření nové konfigurace
    public void fabClick(View v) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, v, "transition_fab");
            startActivityForResult(new Intent(ConfigurationsActivity.this, ConfigurationFactoryActivity.class), REQUEST_NEW_CONFIGURATION, options.toBundle());
        } else
            startActivityForResult(new Intent(ConfigurationsActivity.this, ConfigurationFactoryActivity.class), REQUEST_NEW_CONFIGURATION);
    }

    // Kliknutí na položku v recyclerView
    public void onItemClick(View view) {
        int position = mBinding.recyclerViewConfigurations.getChildAdapterPosition(view);

        if (position == -1) {
            return;
        }

        AConfiguration configuration = mManager.configurations.get(position);
        Intent intent = new Intent(this, ConfigurationDetailActivity.class);
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_ID, position);
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_NAME, configuration.getName());
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_TYPE, configuration.getConfigurationType());
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_EXTENSION_TYPE, configuration.metaData.extensionType);

        TextView textConfType = (TextView) view.findViewById(R.id.text_configuration_type);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                textConfType, "transition_configuration_type");

        ActivityCompat.startActivityForResult(this, intent, REQUEST_DETAIL_CONFIGURATION, options.toBundle());
    }
    // endregion

    class ActionBarCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.experiments_context_menu, menu);
            mActionMode = mode;

            mFab.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final List<Integer> selectedItems = mConfigurationAdapter.getSelectedItemsIndex();
            if (selectedItems.size() == 0) {
                return false;
            }

            String name = mManager.configurations.get(selectedItems.get(0)).getName();
            Intent intent;
            switch (item.getItemId()) {
                case R.id.context_duplicate: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

                    intent = new Intent(ConfigurationsActivity.this, ConfigurationDuplicateActivity.class);
                    intent.putExtra(ConfigurationDuplicateActivity.CONFIGURATION_ID, selectedItems.get(0));
                    intent.putExtra(ConfigurationDuplicateActivity.CONFIGURATION_NAME, name);
                    startActivityForResult(intent, REQUEST_DUPLICATE_CONFIGURATION);

                    return true;
                case R.id.context_delete: // Smažeme konfigurace
                    mManager.prepareToDelete(selectedItems);

                    return true;
                case R.id.context_rename: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

                    intent = new Intent(ConfigurationsActivity.this, ConfigurationRenameActivity.class);
                    intent.putExtra(ConfigurationRenameActivity.CONFIGURATION_ID, selectedItems.get(0));
                    intent.putExtra(ConfigurationRenameActivity.CONFIGURATION_NAME, name);
                    startActivityForResult(intent, REQUEST_RENAME_CONFIGURATION);

                    return true;
                case R.id.context_select_all:
                    mConfigurationAdapter.selectAll();
                    mActionMode.setTitle(getString(R.string.selected_count, mConfigurationAdapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_inverse:
                    mConfigurationAdapter.invertSelection();
                    mActionMode.setTitle(getString(R.string.selected_count, mConfigurationAdapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_none:
                    mConfigurationAdapter.selectNone();
                    mActionMode.setTitle(getString(R.string.selected_count, mConfigurationAdapter.getSelectedItemCount()));

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mConfigurationAdapter.clearSelections();
            mFab.setVisibility(View.VISIBLE);
            setBluetoothStatusIcon(mBluetoothServiceStatus);


            isRecyclerViewEmpty.set(mConfigurationAdapter.getItemCount() == 0);
        }
    }

    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        private void internal_toggleSelection(View v) {
            int index = mRecyclerView.getChildAdapterPosition(v);
            toggleSelection(index);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

            if (mActionMode != null) {
                internal_toggleSelection(view);
                return false;
            }

            onItemClick(view);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (mActionMode != null) {
                return;
            }

            startSupportActionMode(new ActionBarCallback());
            internal_toggleSelection(view);
            super.onLongPress(e);
        }


    }
}
