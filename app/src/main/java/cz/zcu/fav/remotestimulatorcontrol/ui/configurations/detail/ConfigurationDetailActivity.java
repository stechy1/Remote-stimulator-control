package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

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
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.DividerItemDecoration;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.cvep.ConfigurationFragmentCVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.erp.ConfigurationFragmentERP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.fvep.ConfigurationFragmentFVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.mediaimport.MediaImportActivity;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.rea.ConfigurationFragmentREA;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep.ConfigurationFragmentTVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class ConfigurationDetailActivity extends AppCompatActivity
        implements ConfigurationLoader.OnConfigurationLoaded, MediaAdapter.OnAddMediaClickListener {

    // region Constants
    public static final int CONFIGURATION_UNKNOWN_ID = -1;
    public static final String CONFIGURATION_ID = "id";
    public static final String CONFIGURATION_NAME = "name";
    public static final String CONFIGURATION_TYPE = "type";
    public static final String CONFIGURATION_EXTENSION_TYPE = "extension_type";
    public static final String CONFIGURATION_RELOAD = "reload";
    
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigDetailActivity";
    private static final String FRAGMENT = "fragment";
    private static final int REQUEST_PATH = 1;
    // endregion

    // region Variables
    private ActivityConfigurationDetailBinding mBinding;
    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private ADetailFragment detailFragment;
    private AConfiguration configuration;
    private MediaManager mediaManager;
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

        setResult(RESULT_OK, intent);
        finish();
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

        saveConfiguration();

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PATH:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(MediaImportActivity.MEDIA_PATH);
                    mediaManager.importt(new File(path));
                }
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
        detailFragment = getFragment(configuration.getConfigurationType());
        detailFragment.setConfiguration(configuration);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_configuration_detail, detailFragment, FRAGMENT)
                .commit();
        initRecyclerView();
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
        startActivityForResult(new Intent(this, MediaImportActivity.class), REQUEST_PATH);
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
            }

            if (snackbarMessage != -1) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            }

            return true;
        }
    };

    private final Handler mediaManagerHandler = new Handler(mediaManagerCallback);
    // endregion
}
