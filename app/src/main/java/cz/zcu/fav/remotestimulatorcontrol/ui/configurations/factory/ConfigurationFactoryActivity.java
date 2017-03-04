package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationFactoryBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.widget.labeledspinner.LabelledSpinner;

public class ConfigurationFactoryActivity extends AppCompatActivity {

    // region Constants
    // Logovací tag
    public static final int FLAG_NAME = 1 << 0;
    public static final int FLAG_TYPE = 1 << 1;
    // Proměnné, které konfigurace přijímá v intentu
    public static final String CONFIGURATION_NAME = "name";
    public static final String CONFIGURATION_TYPE = "type";
    private static final String TAG = "ConfigFactoryActivity";
    // endregion

    // region Variables
    // Konfigurace
    public final ObservableConfiguration mConfiguration = new ObservableConfiguration();
    // Listener pro změnu typu konfigurace
    public final LabelledSpinner.OnItemSelected typeListener = new LabelledSpinner.OnItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mConfiguration.setConfigurationType(ConfigurationType.valueOf(position));
        }
    };
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            mConfiguration.setName(savedInstanceState.getString(CONFIGURATION_NAME));
            mConfiguration.setConfigurationType(ConfigurationType.valueOf(savedInstanceState.getString(CONFIGURATION_TYPE)));
        }

        ActivityConfigurationFactoryBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_factory);
        mBinding.setController(this);
        mBinding.setConfiguration(mConfiguration);

        mBinding.editConfigurationName.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CONFIGURATION_NAME, mConfiguration.getName());
        outState.putString(CONFIGURATION_TYPE, mConfiguration.getConfigurationType());

        super.onSaveInstanceState(outState);
    }

    // region Public methods
    // Handler pro tlačítko create
    public void onCreate(View view) {
        Intent intent = new Intent();
        intent.putExtra(CONFIGURATION_NAME, mConfiguration.getName());
        intent.putExtra(CONFIGURATION_TYPE, mConfiguration.getConfigurationType());

        setResult(RESULT_OK, intent);
        finish();
    }
    // endregion
}
