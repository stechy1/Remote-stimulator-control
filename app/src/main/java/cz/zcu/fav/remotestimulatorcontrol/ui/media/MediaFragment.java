package cz.zcu.fav.remotestimulatorcontrol.ui.media;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentMediaBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaAudio;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaManager;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static android.app.Activity.RESULT_OK;

public class MediaFragment extends Fragment {

    // region Constants
    private static final String TAG = "MediaFragment";

    // Stringy pro ukládání stavu instance
    private static final String SAVE_STATE_IN_ACTION_MODE = "action_mode";
    private static final String SAVE_STATE_SELECTED_ITEMS_COUNT = "selected_items_count";

    // Seznam requestů
    private static final int REQUEST_FILE_PATH = 1;
    private static final int REQUEST_PERMISSION = 2;
    // endregion

    // region Variables
    private final ObservableBoolean isRecyclerViewEmpty = new ObservableBoolean(true);
    private FragmentMediaBinding mBinding;

    private RecyclerView mRecyclerView;
    private MediaAdapter mMediaAdapter;
    private MediaManager mManager;
    private GestureDetectorCompat mGestureDetector;
    private ActionMode mActionMode;
    private FloatingActionButton mFab;
    private MediaPlayer mediaPlayer;

    private boolean permissionGranted = false;
    private boolean mDeleteConfirmed = true;

    public final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mManager.refresh();
        }
    };
    private int selectedMedia;

    private Handler.Callback managerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean success;
            int snackbarMessage = -1;
            switch (msg.what) {
                case MediaManager.MESSAGE_MEDIA_LOADED:
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    isRecyclerViewEmpty.set(mMediaAdapter.getItemCount() == 0);
                    mBinding.recyclerViewMedia.getAdapter().notifyDataSetChanged();
                    break;
                case MediaManager.MESSAGE_MEDIA_IMPORT:
                    success = msg.arg1 == MediaManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_successful : R.string.manager_message_unsuccessful;

                    if (success) {
                        isRecyclerViewEmpty.set(false);
                        mMediaAdapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case MediaManager.MESSAGE_MEDIA_PREPARED_TO_DELETE:
                    List<Integer> itemsToDelete = mMediaAdapter.getSelectedItemsIndex();
                    mMediaAdapter.saveSelectedItems();

                    for (Integer item : itemsToDelete)
                        mMediaAdapter.notifyItemRemoved(item);

                    mMediaAdapter.clearSelections();
                    mActionMode.setTitle(getString(R.string.selected_count, mMediaAdapter.getSelectedItemCount()));

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
                                        boolean empty = mMediaAdapter.getItemCount() == 0;
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
                case MediaManager.MESSAGE_MEDIA_UNDO_DELETE:
                    mMediaAdapter.restoreSelectedItems();
                    List<Integer> itemsToUndo = mMediaAdapter.getSelectedItemsIndex();

                    for (Integer item : itemsToUndo)
                        mMediaAdapter.notifyItemInserted(item);

                    if (mActionMode != null) {
                        mActionMode.setTitle(getString(R.string.selected_count, mMediaAdapter.getSelectedItemCount()));
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
     * Inicializuje recycler view
     */
    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerViewMedia;
        mMediaAdapter = new MediaAdapter(mManager.mediaList);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setItemAnimator(new LandingAnimator(new FastOutLinearInInterpolator()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mMediaAdapter));
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
        mMediaAdapter.toggleSelection(index);
        String title = getString(R.string.selected_count, mMediaAdapter.getSelectedItemCount());
        mActionMode.setTitle(title);
    }

    private boolean checkReadExternalPermission()
    {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void verifyPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            permissionGranted = true;
        }
    }

    private void startActionMode() {
        ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionBarCallback());
    }
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mManager = new MediaManager(getActivity().getFilesDir());
        mManager.setHandler(managerHandler);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_media, container, false);
        mBinding.setController(this);
        mBinding.executePendingBindings();

        initRecyclerView();

        mFab = mBinding.fabNewMedia;

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            boolean isInActionMode = savedInstanceState.getBoolean(SAVE_STATE_IN_ACTION_MODE);
            if (isInActionMode && mActionMode == null) {
                startActionMode();
                List<Integer> selectedItems = savedInstanceState.getIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT);
                assert selectedItems != null;
                mMediaAdapter.selectItems(selectedItems);
                mActionMode.setTitle(getString(R.string.selected_count, selectedItems.size()));
            }
        }

        permissionGranted = checkReadExternalPermission();

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
        outState.putIntegerArrayList(SAVE_STATE_SELECTED_ITEMS_COUNT, mMediaAdapter.getSelectedItemsIndex());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_PATH:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(getActivity(), uri);

                    if (path == null) {
                        Snackbar.make(mRecyclerView, "Fail", Snackbar.LENGTH_SHORT).show();
                        Log.e(TAG, "Nepodařilo se naimportovat externí médium.");
                        Log.e(TAG, uri.toString());
                        return;
                    }

                    mManager.importt(new File(path));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                Log.d(TAG, "Byl vynesen request na práva");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                }
                break;
        }
    }

    public void fabClick(View view) {
        if (!permissionGranted) {
            Log.i(TAG, "Nebyla udělena práva pro přístup k úložišti");
            verifyPermission();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*, audio/*");
        startActivityForResult(Intent.createChooser(intent, "Vyberte externí médium"), REQUEST_FILE_PATH);
    }

    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        // region Variables
        private final MediaPlayer.OnPreparedListener mmMediaPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ((MediaAudio)(mManager.mediaList.get(selectedMedia))).setPlaying(true);
                mediaPlayer.start();
            }
        };

        private final MediaPlayer.OnCompletionListener mmMediaCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ((MediaAudio) (mManager.mediaList.get(selectedMedia))).setPlaying(false);
                selectedMedia = -1;
                mediaPlayer.reset();
            }
        };
        // endregion

        // region Private methods
        /**
         * Odstartuje přehrávač
         *
         * @param filePath Cesta k souboru, který se má přehrát
         * @param position Index media, který se bude přehrávat
         */
        private void playMediaPlayer(String filePath, int position) {
            try {
                mediaPlayer.setDataSource(filePath);
                selectedMedia = position;
                mediaPlayer.prepareAsync();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Zvuk nebylo možné přehrát", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Zastaví přehrávání přehrávače
         *
         * @param position Index média, který se má zastavit
         */
        private void stopMediaPlayer(int position) {
            mediaPlayer.stop();
            ((MediaAudio) (mManager.mediaList.get(position))).setPlaying(false);
            selectedMedia = -1;
            mediaPlayer.reset();
        }
        // endregion

        private void internal_toggleSelection(View v) {
            int index = mRecyclerView.getChildAdapterPosition(v);
            toggleSelection(index);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = mRecyclerView.getChildAdapterPosition(view);

            if (mActionMode != null) {
                internal_toggleSelection(view);
                return false;
            }

            if (position == -1) {
                return false;
            }

            AMedia media = mManager.mediaList.get(position);
            String filePath = media.getMediaFile().getAbsolutePath();
            if (media.getMediaType() == MediaType.AUDIO) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnPreparedListener(mmMediaPreparedListener);
                    mediaPlayer.setOnCompletionListener(mmMediaCompletionListener);
                }

                if (mediaPlayer.isPlaying()) {
                    final boolean nextPlay = position != selectedMedia;
                    stopMediaPlayer(selectedMedia);
                    if (nextPlay) {
                        playMediaPlayer(filePath, position);
                    }
                    return true;
                }

                playMediaPlayer(filePath, position);

            }

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
            final List<Integer> selectedItems = mMediaAdapter.getSelectedItemsIndex();
            if (selectedItems.size() == 0) {
                return false;
            }

            String name = mManager.mediaList.get(selectedItems.get(0)).getName();
            Intent intent;
            switch (item.getItemId()) {
                case R.id.context_duplicate: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

//                    intent = new Intent(OutputProfilesActivity.this, ProfileDuplicateActivity.class);
//                    intent.putExtra(ProfileDuplicateActivity.PROFILE_ID, selectedItems.get(0));
//                    intent.putExtra(ProfileDuplicateActivity.PROFILE_NAME, name);
//                    startActivityForResult(intent, REQUEST_DUPLICATE_PROFILE);

                    return true;
                case R.id.context_delete: // Smažeme konfigurace
                    mManager.prepareToDelete(selectedItems);

                    return true;
                case R.id.context_rename: // Spustime novou aktivitu ve formě dialogu
                    if (selectedItems.size() > 1) {
                        return false;
                    }

//                    intent = new Intent(OutputProfilesActivity.this, ProfileRenameActivity.class);
//                    intent.putExtra(ProfileRenameActivity.PROFILE_ID, selectedItems.get(0));
//                    intent.putExtra(ProfileRenameActivity.PROFILE_NAME, name);
//                    startActivityForResult(intent, REQUEST_RENAME_PROFILE);

                    return true;
                case R.id.context_select_all:
                    mMediaAdapter.selectAll();
                    mActionMode.setTitle(getString(R.string.selected_count, mMediaAdapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_inverse:
                    mMediaAdapter.invertSelection();
                    mActionMode.setTitle(getString(R.string.selected_count, mMediaAdapter.getSelectedItemCount()));

                    return true;
                case R.id.context_select_none:
                    mMediaAdapter.selectNone();
                    mActionMode.setTitle(getString(R.string.selected_count, mMediaAdapter.getSelectedItemCount()));

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mMediaAdapter.clearSelections();
            mFab.setVisibility(View.VISIBLE);

            isRecyclerViewEmpty.set(mMediaAdapter.getItemCount() == 0);
        }
    }

}