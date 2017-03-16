package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.GlobalPreferences;
import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentConfigurationsBinding;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationManager;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationComparator;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MetaData;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ConfigurationDetailActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.duplicate.ConfigurationDuplicateActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation.ConfigurationImportActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.rename.ConfigurationRenameActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.sorting.ConfigurationSortingActivity;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static android.app.Activity.RESULT_OK;

public class ConfigurationsFragment extends Fragment {

    // region Constants

    private static final String TAG = "ConfigFragment";

    public static final int FLAG_SORT_NAME = 1 << 0;
    public static final int FLAG_SORT_TYPE = 1 << 1;

    // Stringy pro ukládání stavu instance
    private static final String SAVE_STATE_IN_ACTION_MODE = "action_mode";
    private static final String SAVE_STATE_SELECTED_ITEMS_COUNT = "selected_items_count";
    private static final String SAVE_STATE_SORTING = "sorting";

    // Seznam requestů
    private static final int REQUEST_NEW_CONFIGURATION = 3;
    private static final int REQUEST_DETAIL_CONFIGURATION = 4;
    private static final int REQUEST_RENAME_CONFIGURATION = 5;
    private static final int REQUEST_DUPLICATE_CONFIGURATION = 6;
    private static final int REQUEST_SORTING = 7;
    private static final int REQUEST_IMPORT_CONFIGURATION = 9;

    // endregion

    // region Variables

    private final ObservableBoolean mIsRecyclerViewEmpty = new ObservableBoolean(true);
    private final ObservableBoolean mShowExtension = new ObservableBoolean(false);

    public final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mManager.refresh();
        }
    };

    private FragmentConfigurationsBinding mBinding;
    // Reference na recycler view
    private RecyclerView mRecyclerView;
    // Reference na adapter pro recycler view
    private ConfigurationAdapter mConfigurationAdapter;
    // Gesture detector
    private GestureDetectorCompat mGestureDetector;
    // Správce experimentů
    private ConfigurationManager mManager;
    private Menu mMenu;
    private int mSortingFlag;
    // Příznak určující, zda-li je potvrzeno smazání vybraných konfigurací
    private boolean mDeleteConfirmed = true;

    private ActionMode mActionMode;
    private View mFab;

    private final Handler.Callback managerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean success;
            int snackbarMessage = -1;
            switch (msg.what) {
                case ConfigurationManager.MESSAGE_CONFIGURATIONS_LOADED:
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    mIsRecyclerViewEmpty.set(mConfigurationAdapter.getItemCount() == 0);
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
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        mIsRecyclerViewEmpty.set(false);
                        mConfigurationAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_IMPORT:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        mIsRecyclerViewEmpty.set(false);
                        mConfigurationAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ConfigurationManager.MESSAGE_CONFIGURATION_RENAME:
                    success = msg.arg1 == ConfigurationManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

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

                    Snackbar.make(mFab, R.string.manager_message_successful, Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mManager.undoDelete();

                                    mDeleteConfirmed = false;
                                }
                            })
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (mDeleteConfirmed) {
                                        mManager.confirmDelete();
                                        boolean empty = mConfigurationAdapter.getItemCount() == 0;
                                        mIsRecyclerViewEmpty.set(empty);
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
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

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
    private RecyclerView.OnItemTouchListener mItemTouchListener = new RecyclerView.OnItemTouchListener() {
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
    };

    // endregion

    // region Private methods

    /**
     * Inicializuje Recycler view
     * Nastaví Layout manager, Item decoration, Item animator, adapter a onClickListener
     */
    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerViewConfigurations;
        mConfigurationAdapter = new ConfigurationAdapter(mManager.configurations, mShowExtension);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mRecyclerView.setItemAnimator(new LandingAnimator(new FastOutLinearInInterpolator()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mConfigurationAdapter));
        mRecyclerView.addOnItemTouchListener(mItemTouchListener);
        mGestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewGestureListener());
    }

    /**
     * Obnoví data v recyclerView
     */
    private void refreshRecyclerView() {
        mBinding.swipeRefreshLayout.setRefreshing(true);
        mManager.refresh();
    }

    private void setEnabledPropertyOnActionModeItems(boolean enabled) {
        if (mActionMode == null) {
            return;
        }

        View duplicateBtn = getActivity().findViewById(R.id.context_duplicate);
        View renameBtn = getActivity().findViewById(R.id.context_rename);

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

    private boolean isFlagValid(int flag, int value) {
        return (value & flag) == flag;
    }

    private void startActionMode() {
        ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionBarCallback());
    }

    /**
     * Načte globální nastavení
     */
    private void loadGlobalSettings() {
        Activity activity = getActivity();
        GlobalPreferences.setDefaultExtension(activity, GlobalPreferences.getDefaultExtension(activity, MetaData.getDefaultExtension().name()));
        MetaData.setDefaultExtension(ExtensionType.valueOf(GlobalPreferences.getDefaultExtension(activity, MetaData.getDefaultExtension().name())));

        GlobalPreferences.setExtensionVisible(activity, GlobalPreferences.isExtensionVisible(activity, mShowExtension.get()));
        mShowExtension.set(GlobalPreferences.isExtensionVisible(activity, mShowExtension.get()));
    }

    /**
     * Naparsuje comparator
     *
     * @return Komparátor
     */
    private Comparator<AConfiguration> parseComparator() {
        List<ConfigurationComparator> comparators = new ArrayList<>();

        if (isFlagValid(FLAG_SORT_TYPE, mSortingFlag)) {
            comparators.add(ConfigurationComparator.TYPE_COMPARATOR);
        }
        if (isFlagValid(FLAG_SORT_NAME, mSortingFlag)) {
            comparators.add(ConfigurationComparator.NAME_COMPARATOR);
        }

        return ConfigurationComparator.getComparator(comparators);
    }

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mManager = new ConfigurationManager(getActivity().getFilesDir());
        mManager.setHandler(managerhandler);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configurations, container, false);
        mBinding.setController(this);
        mBinding.setIsRecyclerViewEmpty(mIsRecyclerViewEmpty);

        mBinding.executePendingBindings();

        initRecyclerView();

        mFab = mBinding.fabNewConfiguration;

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mSortingFlag = savedInstanceState.getInt(SAVE_STATE_SORTING);
            boolean isInActionMode = savedInstanceState.getBoolean(SAVE_STATE_IN_ACTION_MODE);
            if (isInActionMode && mActionMode == null) {
                startActionMode();
                List<Integer> selectedItems = savedInstanceState.getIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT);
                assert selectedItems != null;
                mConfigurationAdapter.selectItems(selectedItems);
                mActionMode.setTitle(getString(R.string.selected_count, selectedItems.size()));
            }
        }

        mManager.setConfigurationComparator(parseComparator());

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        loadGlobalSettings();
        refreshRecyclerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVE_STATE_IN_ACTION_MODE, mActionMode != null);
        outState.putIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT, mConfigurationAdapter.getSelectedItemsIndex());
        outState.putInt(SAVE_STATE_SORTING, mSortingFlag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
                    ConfigurationSharedPreferences.setSortingFlag(getActivity(), mSortingFlag);
                    mManager.setConfigurationComparator(parseComparator());
                    mManager.refresh();
                }
                break;
            case REQUEST_IMPORT_CONFIGURATION:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(ConfigurationImportActivity.CONFIGURATION_FILE_PATH);
                    String name = data.getStringExtra(ConfigurationImportActivity.CONFIGURATION_NAME);
                    ConfigurationType configurationType = (ConfigurationType) data.getSerializableExtra(ConfigurationImportActivity.CONFIGURATION_TYPE);
                    ExtensionType extensionType = (ExtensionType) data.getSerializableExtra(ConfigurationImportActivity.CONFIGURATION_EXTENSION);

                    mManager.importt(name, path, configurationType, extensionType);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;

        inflater.inflate(R.menu.main_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_sort:
                Intent intent = new Intent(getActivity(), ConfigurationSortingActivity.class);
                intent.putExtra(ConfigurationSortingActivity.SORTING_FLAG, mSortingFlag);
                startActivityForResult(intent, REQUEST_SORTING);
                return true;
            case R.id.menu_main_import:
                startActivityForResult(new Intent(getActivity(), ConfigurationImportActivity.class), REQUEST_IMPORT_CONFIGURATION);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // region Click handlers
    
    // Kliknutí na FAB tlačítko
    public void fabClick(View view) {
        startActivityForResult(new Intent(getActivity(), ConfigurationFactoryActivity.class), REQUEST_NEW_CONFIGURATION);
    }
    // Kliknutí na položku v recyclerView
    public void onItemClick(View view) {
        int position = mBinding.recyclerViewConfigurations.getChildAdapterPosition(view);

        if (position == -1) {
            return;
        }

        AConfiguration configuration = mManager.configurations.get(position);
        Intent intent = new Intent(getActivity(), ConfigurationDetailActivity.class);
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_ID, position);
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_NAME, configuration.getName());
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_TYPE, configuration.getConfigurationType());
        intent.putExtra(ConfigurationDetailActivity.CONFIGURATION_EXTENSION_TYPE, configuration.metaData.extensionType);

        startActivityForResult(intent, REQUEST_DETAIL_CONFIGURATION);
    }

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

                    intent = new Intent(getActivity(), ConfigurationDuplicateActivity.class);
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

                    intent = new Intent(getActivity(), ConfigurationRenameActivity.class);
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

            mIsRecyclerViewEmpty.set(mConfigurationAdapter.getItemCount() == 0);
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
            if (mActionMode != null || view == null) {
                return;
            }

            startActionMode();
            internal_toggleSelection(view);
            super.onLongPress(e);
        }
    }

    // endregion
    
}
