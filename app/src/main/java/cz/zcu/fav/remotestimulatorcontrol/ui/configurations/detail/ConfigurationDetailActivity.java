package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.cvep.ConfigurationFragmentCVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.erp.ConfigurationFragmentERP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.fvep.ConfigurationFragmentFVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.rea.ConfigurationFragmentREA;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep.ConfigurationFragmentTVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class ConfigurationDetailActivity extends AppCompatActivity
        implements ConfigurationLoader.OnConfigurationLoaded {

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
    private static final int REQUEST_WAIT_FOR_PREVIEW_RESULT = 3;
    // endregion

    private ADetailFragment detailFragment;
    private AConfiguration configuration;

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
            File file = ConfigurationManager.buildConfigurationFilePath(getFilesDir(), configuration);
            handler.write(new FileOutputStream(file));
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

        ActivityConfigurationDetailBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_detail);
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
            case REQUEST_WAIT_FOR_PREVIEW_RESULT:
                new ConfigurationLoader(configuration, this).execute(getFilesDir());
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    protected void onStop() {
        super.onStop();
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
    }

    // endregion

}
