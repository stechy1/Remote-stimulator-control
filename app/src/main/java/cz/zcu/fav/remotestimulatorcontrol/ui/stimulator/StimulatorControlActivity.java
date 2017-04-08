package cz.zcu.fav.remotestimulatorcontrol.ui.stimulator;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityStimulatorControlBinding;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationHelper;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.stimulator.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.stimulator.Stimulator;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ConfigurationLoader;

public class StimulatorControlActivity extends AppCompatActivity implements ConfigurationLoader.OnConfigurationLoaded {

    // region Constants
    private static final String TAG = "StimulatorControlAct";

    public static final String CONFIGURATION_NAME = "name";
    public static final String CONFIGURATION_TYPE = "type";
    public static final String CONFIGURATION_EXTENSION_TYPE = "extension_type";

    private static final String STIMULATION_IS_RUNNING = "running";

    // endregion

    // region Variables
    private final ObservableBoolean loaded = new ObservableBoolean(false);
    private final ObservableBoolean running = new ObservableBoolean(false);

    private ExtensionType extensionType;
    private AConfiguration configuration;
    private ActivityStimulatorControlBinding binding;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String configName;
        ConfigurationType type;
        if (savedInstanceState != null) {
            configName = savedInstanceState.getString(CONFIGURATION_NAME);
            type = (ConfigurationType) savedInstanceState.getSerializable(CONFIGURATION_TYPE);
            extensionType = (ExtensionType) savedInstanceState.getSerializable(CONFIGURATION_EXTENSION_TYPE);
            running.set(savedInstanceState.getBoolean(STIMULATION_IS_RUNNING));
        } else {
            Intent intent = getIntent();
            configName = intent.getStringExtra(CONFIGURATION_NAME);
            type = (ConfigurationType) intent.getSerializableExtra(CONFIGURATION_TYPE);
            extensionType = (ExtensionType) intent.getSerializableExtra(CONFIGURATION_EXTENSION_TYPE);
        }

        configuration = ConfigurationHelper.from(configName, type);
        configuration.metaData.extensionType = extensionType;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_stimulator_control);
        binding.setController(this);
        binding.setLoaded(loaded);
        binding.setRunning(running);
        binding.executePendingBindings();

        setSupportActionBar(binding.toolbar);

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
        outState.putString(CONFIGURATION_NAME, configuration.getName());
        outState.putSerializable(CONFIGURATION_TYPE, configuration.getConfigurationType());
        outState.putSerializable(CONFIGURATION_EXTENSION_TYPE, extensionType);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationLoaded() {
        loaded.set(true);
        Snackbar.make(binding.stimulatorControlContainer, "Configuration loaded", Snackbar.LENGTH_SHORT).show();
    }

    // region Button handles

    public void onStimulationStart(View view) {
        BluetoothService.sendData(this, Stimulator.getStartPacket());
        running.set(true);
    }

    public void onStimulationStop(View view) {
        BluetoothService.sendData(this, Stimulator.getStopPacket());
        running.set(false);
    }

    public void onStimulatorUploadConfig(View view) {
        for (BtPacket btPacket : configuration.getPackets()) {
            BluetoothService.sendData(this, btPacket);
        }
    }

    // endregion
}
