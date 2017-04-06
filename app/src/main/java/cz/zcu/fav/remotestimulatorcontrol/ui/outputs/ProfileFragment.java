package cz.zcu.fav.remotestimulatorcontrol.ui.outputs;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
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

import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentProfileBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputProfile;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.ProfileManager;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.DividerItemDecoration;
import cz.zcu.fav.remotestimulatorcontrol.ui.outputs.detail.ProfileDetailActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.outputs.duplicate.ProfileDuplicateActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.outputs.factory.ProfileFactoryActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation.ProfileImportActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.outputs.rename.ProfileRenameActivity;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    // region Constants
    private static final String TAG = "OutputProfiles";

    // Stringy pro ukládání stavu instance
    private static final String SAVE_STATE_IN_ACTION_MODE = "action_mode";
    private static final String SAVE_STATE_SELECTED_ITEMS_COUNT = "selected_items_count";

    // Seznam requestů
    private static final int REQUEST_NEW_PROFILE = 1;
    private static final int REQUEST_RENAME_PROFILE = 2;
    private static final int REQUEST_DUPLICATE_PROFILE = 3;
    private static final int REQUEST_IMPORT_PROFILE = 4;
    // endregion

    // region Variables

    private final ObservableBoolean isRecyclerViewEmpty = new ObservableBoolean(true);

    private FragmentProfileBinding mBinding;
    private RecyclerView mRecyclerView;
    private ProfileAdapter mProfileAdapter;
    private ProfileManager mManager;
    private GestureDetectorCompat mGestureDetector;
    private ActionMode mActionMode;
    private FloatingActionButton mFab;

    private boolean mDeleteConfirmed = true;
    private Handler.Callback managerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean success;
            int snackbarMessage = -1;
            switch (msg.what) {
                case ProfileManager.MESSAGE_PROFILES_LOADED:
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    isRecyclerViewEmpty.set(mProfileAdapter.getItemCount() == 0);
                    mBinding.recyclerViewProfiles.getAdapter().notifyDataSetChanged();
                    break;
                case ProfileManager.MESSAGE_NAME_EXISTS:
                    snackbarMessage = R.string.error_name_exists;
                    break;
                case ProfileManager.MESSAGE_INVALID_NAME:
                    snackbarMessage = R.string.error_invalid_name;
                    break;
                case ProfileManager.MESSAGE_PROFILE_CREATE:
                    success = msg.arg1 == ProfileManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        isRecyclerViewEmpty.set(false);
                        mProfileAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ProfileManager.MESSAGE_PROFILE_IMPORT:
                    success = msg.arg1 == ProfileManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        isRecyclerViewEmpty.set(false);
                        mProfileAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case ProfileManager.MESSAGE_PROFILE_RENAME:
                    success = msg.arg1 == ProfileManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        mProfileAdapter.notifyItemChanged(msg.arg2);
                    }
                    break;
                case ProfileManager.MESSAGE_PROFILE_PREPARED_TO_DELETE:
                    List<Integer> itemsToDelete = mProfileAdapter.getSelectedItemsIndex();
                    mProfileAdapter.saveSelectedItems();

                    for (Integer item : itemsToDelete)
                        mProfileAdapter.notifyItemRemoved(item);

                    mProfileAdapter.clearSelections();
                    mActionMode.setTitle(getString(R.string.selected_count, mProfileAdapter.getSelectedItemCount()));

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
                                        boolean empty = mProfileAdapter.getItemCount() == 0;
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
                case ProfileManager.MESSAGE_PROFILE_UNDO_DELETE:
                    mProfileAdapter.restoreSelectedItems();
                    List<Integer> itemsToUndo = mProfileAdapter.getSelectedItemsIndex();

                    for (Integer item : itemsToUndo)
                        mProfileAdapter.notifyItemInserted(item);

                    if (mActionMode != null) {
                        mActionMode.setTitle(getString(R.string.selected_count, mProfileAdapter.getSelectedItemCount()));
                    }
                    break;
                case ProfileManager.MESSAGE_PROFILE_UPDATE:
                    mProfileAdapter.notifyItemChanged(msg.arg1);
                    break;
                case ProfileManager.MESSAGE_PROFILE_DUPLICATE:
                    success = msg.arg1 == ProfileManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        mProfileAdapter.notifyItemChanged(msg.arg2);
                    }
                    break;
            }

            if (snackbarMessage != -1) {
                Snackbar.make(mFab, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
    };
    private final Handler managerHandler = new Handler(managerCallback);

    public final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mManager.refresh();
        }
    };
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
    private Menu mMenu;
    // endregion

    // region Private methods
    /**
     * Inicializuje recycler view
     */
    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerViewProfiles;
        mProfileAdapter = new ProfileAdapter(mManager.profiles);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mProfileAdapter);
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

    private void toggleSelection(int index) {
        mProfileAdapter.toggleSelection(index);
        String title = getString(R.string.selected_count, mProfileAdapter.getSelectedItemCount());
        mActionMode.setTitle(title);

        setEnabledPropertyOnActionModeItems(mProfileAdapter.getSelectedItemCount() == 1);
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

    private void startActionMode() {
        ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionBarCallback());
    }
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mManager = new ProfileManager(getActivity().getFilesDir());
        mManager.setHandler(managerHandler);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        mBinding.setController(this);
        mBinding.setIsRecyclerViewEmpty(isRecyclerViewEmpty);
        mBinding.executePendingBindings();

        initRecyclerView();

        mFab = mBinding.fabNewProfile;

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            boolean isInActionMode = savedInstanceState.getBoolean(SAVE_STATE_IN_ACTION_MODE);
            if (isInActionMode && mActionMode == null) {
                startActionMode();
                List<Integer> selectedItems = savedInstanceState.getIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT);
                assert selectedItems != null;
                mProfileAdapter.selectItems(selectedItems);
                mActionMode.setTitle(getString(R.string.selected_count, selectedItems.size()));
            }
        }

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshRecyclerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVE_STATE_IN_ACTION_MODE, mActionMode != null);
        outState.putIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT, mProfileAdapter.getSelectedItemsIndex());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_NEW_PROFILE:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra(ProfileFactoryActivity.PROFILE_NAME);
                    mManager.create(name);
                }
                break;
            case REQUEST_RENAME_PROFILE:
                if (resultCode == RESULT_OK) {
                    int id = data.getIntExtra(ProfileRenameActivity.PROFILE_ID, ProfileRenameActivity.PROFILE_UNKNOWN_ID);
                    String name = data.getStringExtra(ProfileRenameActivity.PROFILE_NAME);

                    if (id == ProfileRenameActivity.PROFILE_UNKNOWN_ID) {
                        return;
                    }

                    mManager.rename(id, name);

                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }
                break;
            case REQUEST_DUPLICATE_PROFILE:
                if (resultCode == RESULT_OK) {
                    int id = data.getIntExtra(ProfileDuplicateActivity.PROFILE_ID, ProfileDuplicateActivity.PROFILE_UNKNOWN_ID);
                    String name = data.getStringExtra(ProfileDuplicateActivity.PROFILE_NAME);

                    if (id == ProfileRenameActivity.PROFILE_UNKNOWN_ID) {
                        return;
                    }

                    mManager.duplicate(id, name);

                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }
                break;
            case REQUEST_IMPORT_PROFILE:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(ProfileImportActivity.PROFILE_FILE_PATH);
                    String name = data.getStringExtra(ProfileImportActivity.PROFILE_NAME);

                    mManager.importt(name, path);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroyView() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;

        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_import:
                startActivityForResult(new Intent(getActivity(), ProfileImportActivity.class), REQUEST_IMPORT_PROFILE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // region Click handlers

    // Kliknutí na FAB tlačítko
    public void fabClick(View view) {
        startActivityForResult(new Intent(getActivity(), ProfileFactoryActivity.class), REQUEST_NEW_PROFILE);
    }

    // Kliknutí na položku v recyclerView
    public void onItemClick(View view) {
        int position = mBinding.recyclerViewProfiles.getChildAdapterPosition(view);

        if (position == -1) {
            return;
        }

        OutputProfile profile = mManager.profiles.get(position);
        Intent intent = new Intent(getActivity(), ProfileDetailActivity.class);
        intent.putExtra(ProfileDetailActivity.PROFILE_NAME, profile.getName());

        ActivityCompat.startActivity(getActivity(), intent, null);
    }

    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        private void internal_toggleSelection(View v) {
            int index = mRecyclerView.getChildAdapterPosition(v);
            toggleSelection(index);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

            if (view == null) {
                return false;
            }


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

            //startSupportActionMode(new OutputProfilesActivity.ActionBarCallback());
            startActionMode();
            internal_toggleSelection(view);
            super.onLongPress(e);
        }

    }

    private class ActionBarCallback implements ActionMode.Callback {
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
            final List<Integer> selectedItems = mProfileAdapter.getSelectedItemsIndex();
            if (selectedItems.size() == 0) {
                return false;
            }

            String name = mManager.profiles.get(selectedItems.get(0)).getName();
            Intent intent;
            switch (item.getItemId()) {
                case R.id.context_duplicate: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

                    intent = new Intent(getActivity(), ProfileDuplicateActivity.class);
                    intent.putExtra(ProfileDuplicateActivity.PROFILE_ID, selectedItems.get(0));
                    intent.putExtra(ProfileDuplicateActivity.PROFILE_NAME, name);
                    startActivityForResult(intent, REQUEST_DUPLICATE_PROFILE);

                    return true;
                case R.id.context_delete: // Smažeme konfigurace
                    mManager.prepareToDelete(selectedItems);

                    return true;
                case R.id.context_rename: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

                    intent = new Intent(getActivity(), ProfileRenameActivity.class);
                    intent.putExtra(ProfileRenameActivity.PROFILE_ID, selectedItems.get(0));
                    intent.putExtra(ProfileRenameActivity.PROFILE_NAME, name);
                    startActivityForResult(intent, REQUEST_RENAME_PROFILE);

                    return true;
                case R.id.context_select_all:
                    mProfileAdapter.selectAll();
                    mActionMode.setTitle(getString(R.string.selected_count, mProfileAdapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_inverse:
                    mProfileAdapter.invertSelection();
                    mActionMode.setTitle(getString(R.string.selected_count, mProfileAdapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_none:
                    mProfileAdapter.selectNone();
                    mActionMode.setTitle(getString(R.string.selected_count, mProfileAdapter.getSelectedItemCount()));

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mProfileAdapter.clearSelections();
            mFab.setVisibility(View.VISIBLE);

            isRecyclerViewEmpty.set(mProfileAdapter.getItemCount() == 0);
        }
    }

    // endregion

}
