package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationDetailBinding;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationHelper;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationManager;
import cz.zcu.fav.remotestimulatorcontrol.model.MediaManager;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaAudio;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.DividerItemDecoration;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.cvep.ConfigurationFragmentCVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.erp.ConfigurationFragmentERP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.fvep.ConfigurationFragmentFVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.rea.ConfigurationFragmentREA;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep.ConfigurationFragmentTVEP;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class ConfigurationDetailActivity extends AppCompatActivity
        implements ConfigurationLoader.OnConfigurationLoaded, MediaAdapter.OnAddMediaClickListener, RecyclerView.OnItemTouchListener {

    // region Constants
    public static final int CONFIGURATION_UNKNOWN_ID = -1;
    public static final String CONFIGURATION_ID = "id";
    public static final String CONFIGURATION_NAME = "name";
    public static final String CONFIGURATION_TYPE = "type";
    public static final String CONFIGURATION_EXTENSION_TYPE = "extension_type";
    public static final String CONFIGURATION_RELOAD = "reload";
    private static final String SELECTED_MEDIA_ID = "media_id";
    
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigDetailActivity";
    private static final String FRAGMENT = "fragment";
    private static final int REQUEST_FILE_PATH = 1;
    private static final int REQUEST_PERMISSION = 2;
    private static final int REQUEST_WAIT_FOR_PREVIEW_RESULT = 3;
    // endregion

    // region Variables
    private ActivityConfigurationDetailBinding mBinding;
    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private ADetailFragment detailFragment;
    private AConfiguration configuration;
    private MediaManager mediaManager;
    private MediaPlayer mediaPlayer;
    private int selectedMedia = -1;
    // Gesture detector
    private GestureDetectorCompat gestureDetector;
    private boolean deleteConfirmed = true;
    @SuppressWarnings("unused")
    // Listener pro změnu počtu výstupů
    public final EditableSeekBar.OnEditableSeekBarProgressChanged outputCountChange = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            if (detailFragment != null) {
                detailFragment.onOutputCountChange(progress);
            }
        }
    };
    // ID konfigurace
    private int id;
    // Použitý typ souboru
    private ExtensionType extensionType;
    // Příznak určující, zda-li mi uživatel povolil přístup k souborovému systému
    private boolean permissionGranted = false;
    // endregion

    // region Private methods
    /**
     * Vrátí fragment podle typu konfigurace
     *
     * @param type Typ konfigurace
     * @return Fragment
     */
    private ADetailFragment getFragment(ConfigurationType type) {
        switch (type) {
            case ERP:
                return new ConfigurationFragmentERP();
            case FVEP:
                return new ConfigurationFragmentFVEP();
            case TVEP:
                return new ConfigurationFragmentTVEP();
            case CVEP:
                return new ConfigurationFragmentCVEP();
            case REA:
                return new ConfigurationFragmentREA();
            default:
                return new ConfigurationFragmentERP();
        }
    }

    private void initRecyclerView() {
        recyclerView = mBinding.recyclerViewMedia;
        adapter = new MediaAdapter(configuration.mediaList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.Orientation.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());
    }

    /**
     * Připraví data a ukončí aktivitu
     */
    private void saveAndExit() {
        saveConfiguration();
        exit();
    }

    /**
     * ULoží konfiguraci do perzistentního úložiště pokud se konfigurace změnila
     */
    private void saveConfiguration() {
        try {
            Log.i(TAG, "Ukládám konfiguraci: " + configuration.getName());
            IOHandler handler = configuration.getHandler();
            Pair<File, File> files = ConfigurationManager.buildConfigurationFilePath(getFilesDir(), configuration);
            handler.write(new FileOutputStream(files.first));
        } catch (IOException e) {
            Log.e(TAG, "Nepodařilo se uložit konfiguraci: " + configuration.getName(), e);
        }
    }

    /**
     * Pošle feedback do hlavní aktivity a ukončí tuto aktivitu
     */
    private void exit() {
        Intent intent = new Intent();
        intent.putExtra(CONFIGURATION_ID, id);
        intent.putExtra(CONFIGURATION_RELOAD, configuration.isChanged());

        if (mediaPlayer != null)
            mediaPlayer.release();

        setResult(RESULT_OK, intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void verifyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            permissionGranted = true;
        }
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        String configName;
        ConfigurationType type;
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(CONFIGURATION_ID);
            configName = savedInstanceState.getString(CONFIGURATION_NAME);
            type = (ConfigurationType) savedInstanceState.getSerializable(CONFIGURATION_TYPE);
            extensionType = (ExtensionType) savedInstanceState.getSerializable(CONFIGURATION_EXTENSION_TYPE);
            selectedMedia = savedInstanceState.getInt(SELECTED_MEDIA_ID);
        } else {
            Intent intent = getIntent();
            id = intent.getIntExtra(CONFIGURATION_ID, -1);
            configName = intent.getStringExtra(CONFIGURATION_NAME);
            type = (ConfigurationType) intent.getSerializableExtra(CONFIGURATION_TYPE);
            extensionType = (ExtensionType) intent.getSerializableExtra(CONFIGURATION_EXTENSION_TYPE);
        }

        configuration = ConfigurationHelper.from(configName, type);
        configuration.metaData.extensionType = extensionType;
        Pair<File, File> files = ConfigurationManager.buildConfigurationFilePath(getFilesDir(), configuration);
        mediaManager = new MediaManager(files.second, configuration);
        mediaManager.setHandler(mediaManagerHandler);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_detail);
        mBinding.setController(this);
        mBinding.setConfiguration(configuration);

        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        verifyPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ConfigurationLoader(configuration, this).execute(getFilesDir());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();

        outState.putInt(CONFIGURATION_ID, id);
        outState.putString(CONFIGURATION_NAME, configuration.getName());
        outState.putSerializable(CONFIGURATION_TYPE, configuration.getConfigurationType());
        outState.putSerializable(CONFIGURATION_EXTENSION_TYPE, extensionType);
        outState.putInt(SELECTED_MEDIA_ID, selectedMedia);

        saveConfiguration();

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_PATH:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);

                    if (path == null) {
                        Snackbar.make(recyclerView, "Fail", Snackbar.LENGTH_SHORT).show();
                        Log.e(TAG, "Nepodařilo se naimportovat externí médium.");
                        Log.e(TAG, uri.toString());
                        return;
                    }

                    mediaManager.importt(new File(path));
                }
                break;
            case REQUEST_WAIT_FOR_PREVIEW_RESULT:
                new ConfigurationLoader(configuration, this).execute(getFilesDir());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        saveAndExit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Zavolá se po načtení konfigurace z disku
     */
    @Override
    public void onConfigurationLoaded() {
        if (selectedMedia != -1) {
            ((MediaAudio)(mediaManager.mediaList.get(selectedMedia))).setPlaying(true);
        }

        detailFragment = getFragment(configuration.getConfigurationType());
        detailFragment.setConfiguration(configuration);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_configuration_detail, detailFragment, FRAGMENT)
                .commit();
        initRecyclerView();
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

    @SuppressWarnings("unused")
    public void onMediaCheckBoxClick(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkboxMediaLed:
                configuration.setMediaType(MediaType.LED, checked);
                break;

            case R.id.checkboxMediaAudio:
                configuration.setMediaType(MediaType.AUDIO, checked);
                break;

            case R.id.checkboxMediaImage:
                configuration.setMediaType(MediaType.IMAGE, checked);
                break;
        }
    }

    @Override
    public void onAddMediaClick() {
//        startActivityForResult(new Intent(this, MediaImportActivity.class), REQUEST_PATH);
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

    // region MediaManager handler
    private final Handler.Callback mediaManagerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean success;
            int snackbarMessage = -1;
            switch (msg.what) {
                case MediaManager.MESSAGE_MEDIA_IMPORT:
                    success = msg.arg1 == MediaManager.MESSAGE_SUCCESSFUL;
                    snackbarMessage = success ? R.string.manager_message_import_successful : R.string.manager_message_import_unsuccessful;

                    if (success) {
                        adapter.notifyItemInserted(msg.arg2);
                    }
                    break;
                case MediaManager.MESSAGE_MEDIA_PREPARED_TO_DELETE:
                    int deletedIndex = msg.arg2;

                    adapter.notifyItemRemoved(deletedIndex);

                    Snackbar.make(recyclerView, R.string.manager_message_delete_successful, Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mediaManager.undoDelete();

                                    deleteConfirmed = false;
                                }
                            })
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (deleteConfirmed) {
                                        mediaManager.confirmDelete();
                                    } else {
                                        deleteConfirmed = true;
                                    }
                                }
                    }).show();

                    break;
                case MediaManager.MESSAGE_CONFIGURATION_UNDO_DELETE:
                    int undoDeletedIndex = msg.arg2;

                    adapter.notifyItemInserted(undoDeletedIndex);
                    break;
            }

            if (snackbarMessage != -1) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            }

            return true;
        }
    };

    private final Handler mediaManagerHandler = new Handler(mediaManagerCallback);
    // endregion

    // region RecyclerView handlers
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

    // region GestureDetector for RecyclerView
    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        private void playMediaPlayer(String filePath, int position) {
            try {
                mediaPlayer.setDataSource(filePath);
                selectedMedia = position;
                mediaPlayer.prepareAsync();
            } catch (IOException ex) {
                Toast.makeText(ConfigurationDetailActivity.this, "Zvuk nebylo možné přehrát", Toast.LENGTH_SHORT).show();
            }
        }

        private void stopMediaPlayer(int position) {
            mediaPlayer.stop();
            ((MediaAudio) (mediaManager.mediaList.get(position))).setPlaying(false);
            selectedMedia = -1;
            mediaPlayer.reset();
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = recyclerView.getChildAdapterPosition(view);

            // Vyfiltrování poslední položky
            if (position == mediaManager.mediaList.size()) {
                return false;
            }

            AMedia media = mediaManager.mediaList.get(position);
            String filePath = media.getMediaFile().getAbsolutePath();
            if (media.getMediaType() == MediaType.AUDIO) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnPreparedListener(mediaPreparedListener);
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

            } else {
                Intent intent = new Intent(ConfigurationDetailActivity.this, MediaImagePreviewActivity.class);
                intent.putExtra(MediaImagePreviewActivity.IMAGE_PATH, filePath);
                startActivityForResult(intent, REQUEST_WAIT_FOR_PREVIEW_RESULT);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = recyclerView.getChildAdapterPosition(view);

            if (position == mediaManager.mediaList.size()) {
                return;
            }

            if (position == selectedMedia) {
                Toast.makeText(ConfigurationDetailActivity.this, "Pro smazání zastavte přehrávání", Toast.LENGTH_SHORT).show();
                return;
            }

            mediaManager.prepareToDelete(position);
        }
    }

    private final MediaPlayer.OnPreparedListener mediaPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            ((MediaAudio)(mediaManager.mediaList.get(selectedMedia))).setPlaying(true);
            mediaPlayer.start();
        }
    };
    // endregion
    // endregion
}
