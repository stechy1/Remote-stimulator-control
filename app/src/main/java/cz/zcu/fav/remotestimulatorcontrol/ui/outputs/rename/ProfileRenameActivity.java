package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.rename;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityProfileRenameBinding;

public class ProfileRenameActivity extends AppCompatActivity {

    // region Constants
    public static final int FLAG_NAME = 1 << 0;
    public static final int PROFILE_UNKNOWN_ID = -1;
    public static final String PROFILE_ID = "id";
    public static final String PROFILE_NAME = "name";
    public static final String OLD_PROFILE_NAME = "old_name";
    // endregion

    // region Variables
    private final ObservableProfile mProfile = new ObservableProfile();
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        int id;

        String name, oldName;
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(PROFILE_ID);
            name = savedInstanceState.getString(PROFILE_NAME);
            oldName = savedInstanceState.getString(OLD_PROFILE_NAME);
        } else {
            Intent intent = getIntent();
            id = intent.getIntExtra(PROFILE_ID, PROFILE_UNKNOWN_ID);
            name = intent.getStringExtra(PROFILE_NAME);
            oldName = name;
        }

        mProfile.setId(id);
        mProfile.setOldName(oldName);
        mProfile.setName(name);
        mProfile.setChanged(false);

        ActivityProfileRenameBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_rename);
        binding.setProfile(mProfile);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(PROFILE_ID, mProfile.getId());
        outState.putString(PROFILE_NAME, mProfile.getName());
        outState.putSerializable(OLD_PROFILE_NAME, mProfile.getOldName());

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

        intent.putExtra(PROFILE_ID, mProfile.getId());
        intent.putExtra(PROFILE_NAME, mProfile.getName());

        setResult(RESULT_OK, intent);
        finish();
    }
    // endregion

}
