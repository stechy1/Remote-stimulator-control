package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.factory;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityProfileFactoryBinding;

public class ProfileFactoryActivity extends AppCompatActivity {

    // region Constants
    public static final int FLAG_NAME = 1 << 0;
    public static final String PROFILE_NAME = "name";
    // endregion

    // region Variables
    private final ObservableProfile profile = new ObservableProfile();
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            profile.setName(savedInstanceState.getString(PROFILE_NAME));
        }

        ActivityProfileFactoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_factory);
        binding.setController(this);
        binding.setProfile(profile);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PROFILE_NAME, profile.getName());
        super.onSaveInstanceState(outState);
    }

    public void onCreate(View view) {
        Intent intent = new Intent();
        intent.putExtra(PROFILE_NAME, profile.getName());

        setResult(RESULT_OK, intent);
        finish();

    }
}
