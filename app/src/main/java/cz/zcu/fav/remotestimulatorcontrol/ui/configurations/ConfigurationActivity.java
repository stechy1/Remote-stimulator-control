package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationBinding;
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

import static cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService.STATE_CONNECTED;
import static cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService.STATE_LISTEN;
import static cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService.STATE_NONE;

public class ConfigurationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerView.OnItemTouchListener {

    // region Constants
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigActivity";

    // Stringy pro ukládání stavu instance
    private static final String SAVE_STATE_IN_ACTION_MODE = "action_mode";
    private static final String SAVE_STATE_SELECTED_ITEMS_COUNT = "selected_items_count";
    private static final String SAVE_STATE_SORTING = "sorting";

    // Přiznaky pro řezení konfigurací
    public static final int FLAG_SORT_NAME  = 1 << 0;
    public static final int FLAG_SORT_TYPE  = 1 << 1;
    public static final int FLAG_SORT_MEDIA = 1 << 2;

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
    // Reference na hlavní layout
    private DrawerLayout mDrawerLayout;
    // Reference na menu
    private Menu menu;
    // Action bar
    private ActionMode actionMode;
    // Reference na recycler view
    private RecyclerView recyclerView;
    // FAB pro přidání nové konfigurace
    private FloatingActionButton fab;
    // Gesture detector
    private GestureDetectorCompat gestureDetector;
    // Správce experimentů
    private ConfigurationManager manager;
    // Binding do hlavní aktivity
    private ActivityConfigurationBinding mBinding;
    // Název připojeného zařízení
    private String mConnectedDeviceName;
    // Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // Reference na bluetooth service
    private BluetoothService mService;
    // Reference na adapter pro recycler view
    private ConfigurationAdapter adapter;
    // Příznak uřčující, zda-li je vytvořeno připojení se zařízením
    private boolean mBound;
    // Příznak určující, zda-li je potvrzeno smazání vybraných konfigurací
    private boolean deleteConfirmed = true;
    // Přiznak určující, podle čeho se budou konfigurace řadit
    private int sortingFlag;
    // Příznak určující, zda-li zařízení podporuje bluetooth
    private boolean bluetoothSupport = false;

    // Udržuje informaci, zda-li je recyclerView prázdný, či nikoliv
    public final ObservableBoolean isRecyclerViewEmpty = new ObservableBoolean(false);
    // Udržuje informaci, zda-li se má zobrazit koncovka jednotlivých souborů, či nikoliv
    private final ObservableBoolean showExtension = new ObservableBoolean(false);
    // Přijímač reagující na změnu stavu bluetoothu
    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Bluetooth state -> off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Bluetooth state -> on");
                        break;
                }
            }
        }
    };
    // endregion

    // region Private methods

    /**
     * Inicializuje Recycler view
     * Nastaví Layout manager, Item decoration, Item animator, adapter a onClickListener
     */
    private void initRecyclerView() {
        recyclerView = mBinding.recyclerViewConfigurations;
        adapter = new ConfigurationAdapter(manager.configurations, showExtension);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(new LandingAnimator(new FastOutLinearInInterpolator()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
        recyclerView.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());
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

        GlobalPreferences.setExtensionVisible(this, GlobalPreferences.isExtensionVisible(this, showExtension.get()));
        showExtension.set(GlobalPreferences.isExtensionVisible(this, showExtension.get()));
    }

    /**
     * Obnoví data v recyclerView
     */
    private void refreshRecyclerView() {
        mBinding.swipeRefreshLayout.setRefreshing(true);
        manager.refresh();
    }

    // region Sorting stuff

    private boolean isFlagValid(int flag, int value) {
        return (value & flag) == flag;
    }

    /**
     * Naparsuje comparator
     *
     * @return Komparátor
     */
    private Comparator<AConfiguration> parseComparator() {
        List<ConfigurationComparator> comparators = new ArrayList<>();

        if (isFlagValid(FLAG_SORT_MEDIA, sortingFlag)) {
            comparators.add(ConfigurationComparator.MEDIA_COMPARATOR);
        }
        if (isFlagValid(FLAG_SORT_TYPE, sortingFlag)) {
            comparators.add(ConfigurationComparator.TYPE_COMPARATOR);
        }
        if (isFlagValid(FLAG_SORT_NAME, sortingFlag)) {
            comparators.add(ConfigurationComparator.NAME_COMPARATOR);
        }

        return ConfigurationComparator.getComparator(comparators);
    }
    // endregion

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new ConfigurationManager(getFilesDir());
        manager.setHandler(managerhandler);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration);
        mBinding.setController(this);
        mBinding.setIsRecyclerViewEmpty(isRecyclerViewEmpty);

        initRecyclerView();

        fab = mBinding.fabNewConfiguration;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothSupport = mBluetoothAdapter != null;
        if (!bluetoothSupport) {
            if (!ConfigurationSharedPreferences.isBTNotSupportedAlertShowed(getApplicationContext(), false)) {
                Snackbar.make(fab, R.string.bt_not_supported, Snackbar.LENGTH_LONG)
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
            sortingFlag = savedInstanceState.getInt(SAVE_STATE_SORTING);
            boolean isInActionMode = savedInstanceState.getBoolean(SAVE_STATE_IN_ACTION_MODE);
            if (isInActionMode && actionMode == null) {
                startSupportActionMode(new ActionBarCallback());
                List<Integer> selectedItems = savedInstanceState.getIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT);
                adapter.selectItems(selectedItems);
                assert selectedItems != null;
                actionMode.setTitle(getString(R.string.selected_count, selectedItems.size()));
            }
        } else {
            sortingFlag = ConfigurationSharedPreferences.getSortingFlag(this, FLAG_SORT_NAME);
        }

        manager.setConfigurationComparator(parseComparator());

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReciever, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadGlobalSettings();
        refreshRecyclerView();

        if (bluetoothSupport) {
            // Bind to Bluetooth service
            Intent intent = new Intent(this, BluetoothService.class);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
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
        outState.putBoolean(SAVE_STATE_IN_ACTION_MODE, actionMode != null);
        outState.putIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT, adapter.getSelectedItemsIndex());
        outState.putInt(SAVE_STATE_SORTING, sortingFlag);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReciever);
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
                        mService.connectToDevice(device);

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
                    manager.create(name, type);
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
                        manager.update(id);
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

                    manager.rename(id, name);

                    if (actionMode != null) {
                        actionMode.finish();
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

                    manager.duplicate(id, name);

                    if (actionMode != null) {
                        actionMode.finish();
                    }
                }
                break;
            case REQUEST_SORTING:
                if (resultCode == RESULT_OK) {
                    int sortingFlagNew = data.getIntExtra(ConfigurationSortingActivity.SORTING_FLAG, sortingFlag);
                    if (sortingFlagNew == sortingFlag) {
                        return;
                    }

                    sortingFlag = sortingFlagNew;
                    ConfigurationSharedPreferences.setSortingFlag(this, sortingFlag);
                    manager.setConfigurationComparator(parseComparator());
                    manager.refresh();
                }
                break;
            case REQUEST_IMPORT:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(ConfigurationImportActivity.CONFIGURATION_FILE_PATH);
                    String name = data.getStringExtra(ConfigurationImportActivity.CONFIGURATION_NAME);
                    ConfigurationType configurationType = (ConfigurationType) data.getSerializableExtra(ConfigurationImportActivity.CONFIGURATION_TYPE);
                    ExtensionType extensionType = (ExtensionType) data.getSerializableExtra(ConfigurationImportActivity.CONFIGURATION_EXTENSION);

                    manager.importt(name, path, configurationType, extensionType);
                }
                break;
        }
    }

    // region Statusbar subtitle method

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
    // endregion

    // region Menu handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem bluetoothMenuItem = menu.findItem(R.id.menu_main_connect);
        bluetoothMenuItem.setEnabled(bluetoothSupport);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically onPositiveClick clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_main_connect:
                if (!bluetoothSupport) {
                    return false;
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    requestBluetoothEnable();
                    return false;
                }

                switch (mService.getState()) {
                    case STATE_NONE:
                    case STATE_LISTEN:
                        startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE);
                        break;
                    case STATE_CONNECTED:
                        mService.stopService(new Intent(this, BluetoothService.class));
                        break;
                }
                break;
            case R.id.menu_main_sort:
                Intent intent = new Intent(this, ConfigurationSortingActivity.class);
                intent.putExtra(ConfigurationSortingActivity.SORTING_FLAG, sortingFlag);
                startActivityForResult(intent, REQUEST_SORTING);
                break;
            case R.id.menu_main_import:
                startActivityForResult(new Intent(this, ConfigurationImportActivity.class), REQUEST_IMPORT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

    // region ActionMode
    private void setEnabledPropertyOnActionModeItems(boolean enabled) {
        if (actionMode == null) {
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
        adapter.toggleSelection(index);
        String title = getString(R.string.selected_count, adapter.getSelectedItemCount());
        actionMode.setTitle(title);

        setEnabledPropertyOnActionModeItems(adapter.getSelectedItemCount() == 1);
    }

    class ActionBarCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.experiments_context_menu, menu);
            actionMode = mode;

            fab.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final List<Integer> selectedItems = adapter.getSelectedItemsIndex();
            if (selectedItems.size() == 0) {
                return false;
            }

            String name = manager.configurations.get(selectedItems.get(0)).getName();
            Intent intent;
            switch (item.getItemId()) {
                case R.id.context_duplicate: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

                    intent = new Intent(ConfigurationActivity.this, ConfigurationDuplicateActivity.class);
                    intent.putExtra(ConfigurationDuplicateActivity.CONFIGURATION_ID, selectedItems.get(0));
                    intent.putExtra(ConfigurationDuplicateActivity.CONFIGURATION_NAME, name);
                    startActivityForResult(intent, REQUEST_DUPLICATE_CONFIGURATION);

                    return true;
                case R.id.context_delete: // Smažeme konfigurace
                    manager.prepareToDelete(selectedItems);

                    return true;
                case R.id.context_rename: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

                    intent = new Intent(ConfigurationActivity.this, ConfigurationRenameActivity.class);
                    intent.putExtra(ConfigurationRenameActivity.CONFIGURATION_ID, selectedItems.get(0));
                    intent.putExtra(ConfigurationRenameActivity.CONFIGURATION_NAME, name);
                    startActivityForResult(intent, REQUEST_RENAME_CONFIGURATION);

                    return true;
                case R.id.context_select_all:
                    adapter.selectAll();
                    actionMode.setTitle(getString(R.string.selected_count, adapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_inverse:
                    adapter.invertSelection();
                    actionMode.setTitle(getString(R.string.selected_count, adapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_none:
                    adapter.selectNone();
                    actionMode.setTitle(getString(R.string.selected_count, adapter.getSelectedItemCount()));

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.clearSelections();
            fab.setVisibility(View.VISIBLE);

            isRecyclerViewEmpty.set(adapter.getItemCount() == 0);
        }
    }

    // region GestureDetector for RecyclerView
    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        private void internal_toggleSelection(View v) {
            int index = recyclerView.getChildAdapterPosition(v);
            toggleSelection(index);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

            if (actionMode != null) {
                internal_toggleSelection(view);
                return false;
            }

            onItemClick(view);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (actionMode != null) {
                return;
            }

            startSupportActionMode(new ActionBarCallback());
            internal_toggleSelection(view);
            super.onLongPress(e);
        }


    }
    // endregion

    // endregion

    // endregion

    // region Recycler view OnItemTouchListner

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
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

    // region Data binding
    // Kliknutí na FAB tlačítko pro vytvoření nové konfigurace
    public void fabClick(View v) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, v, "transition_fab");
            startActivityForResult(new Intent(ConfigurationActivity.this, ConfigurationFactoryActivity.class), REQUEST_NEW_CONFIGURATION, options.toBundle());
        } else
            startActivityForResult(new Intent(ConfigurationActivity.this, ConfigurationFactoryActivity.class), REQUEST_NEW_CONFIGURATION);
    }

    // Kliknutí na položku v recyclerView
    public void onItemClick(View view) {
        int position = mBinding.recyclerViewConfigurations.getChildAdapterPosition(view);

        if (position == -1) {
            return;
        }

        AConfiguration configuration = manager.configurations.get(position);
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

    @SuppressWarnings("unused")
    public final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            manager.refresh();
        }
    };
    // endregion

    // region Bluetooth service handler and connection
    /**
     * Definice callbacku pro binding do service
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mService.setHandler(bluetoothServiceHandler);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private final Handler.Callback bluetoothServiceCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            menu.getItem(0).setIcon(R.drawable.bluetooth_connected);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            menu.getItem(0).setIcon(R.drawable.bluetooth_connect);
                            break;
                    }
                    break;

                case BluetoothService.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                    break;

                case BluetoothService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(ConfigurationActivity.this, readMessage, Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothService.MESSAGE_SHOW:
                    Toast.makeText(ConfigurationActivity.this, msg.getData().getString(BluetoothService.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }

            return true;
        }
    };

    private final Handler bluetoothServiceHandler = new Handler(bluetoothServiceCallback);

    // endregion

    // region Configuration manager handler
    private final Handler.Callback managerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean success;
            int snackbarMessage = -1;
            switch (msg.what) {
                case ConfigurationManager.MESSAGE_CONFIGURATIONS_LOADED:
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    isRecyclerViewEmpty.set(adapter.getItemCount() == 0);
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
                        adapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_IMPORT:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_import_successful : R.string.manager_message_import_unsuccessful;

                    if (success) {
                        isRecyclerViewEmpty.set(false);
                        adapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_RENAME:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_rename_successful : R.string.manager_message_rename_unsuccessful;

                    if (success) {
                        adapter.notifyItemChanged(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_PREPARED_TO_DELETE:
                    List<Integer> itemsToDelete = adapter.getSelectedItemsIndex();
                    adapter.saveSelectedItems();

                    for (Integer item : itemsToDelete)
                        adapter.notifyItemRemoved(item);

                    adapter.clearSelections();
                    actionMode.setTitle(getString(R.string.selected_count, adapter.getSelectedItemCount()));

                    Snackbar.make(fab, R.string.manager_message_delete_successful, Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    manager.undoDelete();

                                    deleteConfirmed = false;
                                }
                            })
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (deleteConfirmed) {
                                        manager.confirmDelete();
                                        boolean empty = adapter.getItemCount() == 0;
                                        isRecyclerViewEmpty.set(empty);
                                        if (empty && actionMode != null) {
                                            actionMode.finish();
                                        }
                                    }
                                    else {
                                        deleteConfirmed = true;
                                    }

                                    super.onDismissed(snackbar, event);
                                }
                            })
                            .show();
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_UNDO_DELETE:
                    adapter.restoreSelectedItems();
                    List<Integer> itemsToUndo = adapter.getSelectedItemsIndex();

                    for (Integer item : itemsToUndo)
                        adapter.notifyItemInserted(item);

                    if (actionMode != null) {
                        actionMode.setTitle(getString(R.string.selected_count, adapter.getSelectedItemCount()));
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_UPDATE:
                    adapter.notifyItemChanged(msg.arg1);
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_DUPLICATE:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_duplicate_successful : R.string.manager_message_duplicate_unsuccessful;

                    if (success) {
                        adapter.notifyItemChanged(msg.arg2);
                    }
                    break;
            }

            if (snackbarMessage != -1) {
                Snackbar.make(fab, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    private final Handler managerhandler = new Handler(managerCallback);
    // endregion
}
