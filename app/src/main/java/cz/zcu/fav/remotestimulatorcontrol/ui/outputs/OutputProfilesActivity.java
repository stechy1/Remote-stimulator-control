package cz.zcu.fav.remotestimulatorcontrol.ui.outputs;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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

import java.io.File;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityOutputProfilesBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.ProfileManager;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.DividerItemDecoration;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class OutputProfilesActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {

    private static final String TAG = "OutputProfiles";
    private final ObservableBoolean isRecyclerViewEmpty = new ObservableBoolean(true);

    private ActivityOutputProfilesBinding mBinding;
    private RecyclerView mRecyclerView;
    private ProfileAdapter mProfileAdapter;
    private ProfileManager mManager;
    private GestureDetectorCompat mGestureDetector;
    private ActionMode mActionMode;
    private FloatingActionButton mFab;

    @SuppressWarnings("unused")
    public final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mManager.refresh();
        }
    };

    /**
     * Inicializuje recycler view
     */
    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerViewProfiles;
        mProfileAdapter = new ProfileAdapter(mManager.profiles);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setItemAnimator(new LandingAnimator(new FastOutLinearInInterpolator()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mProfileAdapter));
        mRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());
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

        View duplicateBtn = findViewById(R.id.context_duplicate);
        View renameBtn = findViewById(R.id.context_rename);

        if (duplicateBtn == null || renameBtn == null) {
            return;
        }

        duplicateBtn.setEnabled(enabled);
        renameBtn.setEnabled(enabled);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new ProfileManager(new File(getFilesDir(), "profiles"));

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_output_profiles);
        mBinding.setController(this);
        mBinding.setIsRecyclerViewEmpty(isRecyclerViewEmpty);

        initRecyclerView();

        mFab = mBinding.fabNewProfile;

        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecyclerView();
    }

    public void fabClick(View view) {

    }

    // Kliknutí na položku v recyclerView
    public void onItemClick(View view) {
        int position = mBinding.recyclerViewProfiles.getChildAdapterPosition(view);

        if (position == -1) {
            return;
        }

        Log.d(TAG, String.valueOf(position));
    }

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

            startSupportActionMode(new ActionBarCallback());
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

//                    intent = new Intent(ConfigurationsActivity.this, ConfigurationDuplicateActivity.class);
//                    intent.putExtra(ConfigurationDuplicateActivity.CONFIGURATION_ID, selectedItems.get(0));
//                    intent.putExtra(ConfigurationDuplicateActivity.CONFIGURATION_NAME, name);
//                    startActivityForResult(intent, REQUEST_DUPLICATE_CONFIGURATION);

                    return true;
                case R.id.context_delete: // Smažeme konfigurace
                    //mManager.prepareToDelete(selectedItems);

                    return true;
                case R.id.context_rename: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

//                    intent = new Intent(ConfigurationsActivity.this, ConfigurationRenameActivity.class);
//                    intent.putExtra(ConfigurationRenameActivity.CONFIGURATION_ID, selectedItems.get(0));
//                    intent.putExtra(ConfigurationRenameActivity.CONFIGURATION_NAME, name);
//                    startActivityForResult(intent, REQUEST_RENAME_CONFIGURATION);

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
}
