package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.rename;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationRenameBinding;

public class ConfigurationRenameActivity extends AppCompatActivity {

    // region Constants
    // Validační příznak pro název
    public static final int FLAG_NAME = 1 << 0;
    public static final int CONFIGURATION_UNKNOWN_ID = -1;
    public static final String CONFIGURATION_ID = "id";
    public static final String CONFIGURATION_NAME = "name";
    public static final String OLD_CONFIGURATION_NAME = "old_name";
    // endregion

    // region Variables
    private final ObservableConfiguration mConfiguration = new ObservableConfiguration();
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        int id;
        String name, oldName;
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(CONFIGURATION_ID);
            name = savedInstanceState.getString(CONFIGURATION_NAME);
            oldName = savedInstanceState.getString(OLD_CONFIGURATION_NAME);
        } else {
            Intent intent = getIntent();
            id = intent.getIntExtra(CONFIGURATION_ID, CONFIGURATION_UNKNOWN_ID);
            name = intent.getStringExtra(CONFIGURATION_NAME);
            oldName = name;
        }

        mConfiguration.setId(id);
        mConfiguration.setOldName(oldName);
        mConfiguration.setName(name);
        mConfiguration.setChanged(false);

        ActivityConfigurationRenameBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_rename);
        binding.setConfiguration(mConfiguration);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CONFIGURATION_ID, mConfiguration.getId());
        outState.putString(CONFIGURATION_NAME, mConfiguration.getName());
        outState.putSerializable(OLD_CONFIGURATION_NAME, mConfiguration.getOldName());

        super.onSaveInstanceState(outState);
    }

    // region Public methods
    // Handler na tlačítko cancel
    public void onCancel(View view) {
        finish();
    }

    // Handler na tlačítko rename
    public void onRename(View view) {
        Intent intent = new Intent();

        intent.putExtra(CONFIGURATION_ID, mConfiguration.getId());
        intent.putExtra(CONFIGURATION_NAME, mConfiguration.getName());

        setResult(RESULT_OK, intent);
        finish();
    }
    // endregion

}
