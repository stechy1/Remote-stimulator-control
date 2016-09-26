package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationFactoryBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.widget.labeledspinner.LabelledSpinner;

public class ConfigurationFactoryActivity extends AppCompatActivity {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigFactoryActivity";
    public static final int FLAG_NAME = 1 << 0;

    public static final String CONFIGURATION_NAME = "name";
    public static final String CONFIGURATION_TYPE = "type";

    // Konfigurace
    public final ObservableConfiguration configuration = new ObservableConfiguration();
    private final List<String> nameList = new ArrayList<>();

    /**
     * Naplní kolekci obsazenými názvy konfigurací
     */
//    private void fillNameList() {
//        // Pole složek podle typů konfigucare
//        File[] files = getFilesDir().listFiles();
//
//        for (File configTypeFolder : files) {
//            File[] configs = configTypeFolder.listFiles();
//            for (File configFile : configs) {
//                if (configFile.isDirectory())
//                    continue;
//                if (!configFile.getName().contains(ConfigurationManager.EXTENSION.toString().toLowerCase()))
//                    continue;
//
//                String name = configFile.getName().replace(ConfigurationManager.EXTENSION.toString().toLowerCase(), "");
//                nameList.add(name);
//                Log.d(TAG, name);
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            configuration.setName(savedInstanceState.getString(CONFIGURATION_NAME));
            configuration.setConfigurationType(ConfigurationType.valueOf(savedInstanceState.getString(CONFIGURATION_TYPE)));
        }

        ActivityConfigurationFactoryBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_factory);
        mBinding.setController(this);
        mBinding.setConfiguration(configuration);

        mBinding.editConfigurationName.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

//        fillNameList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CONFIGURATION_NAME, configuration.getName());
        outState.putString(CONFIGURATION_TYPE, configuration.getConfigurationType());

        super.onSaveInstanceState(outState);
    }

    // Handler pro tlačítko create
    public void onCreate(View view) {
        Intent intent = new Intent();
        intent.putExtra(CONFIGURATION_NAME, configuration.getName());
        intent.putExtra(CONFIGURATION_TYPE, configuration.getConfigurationType());

        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressWarnings("unused")
    // Listener pro změnu typu konfigurace
    public final LabelledSpinner.OnItemSelected typeListener = new LabelledSpinner.OnItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            configuration.setConfigurationType(ConfigurationType.valueOf(position));
        }
    };
}
