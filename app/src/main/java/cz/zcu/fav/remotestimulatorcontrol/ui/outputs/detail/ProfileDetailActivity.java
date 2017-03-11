package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityProfileDetailBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputProfile;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.ProfileManager;
import cz.zcu.fav.remotestimulatorcontrol.widget.profileconfiguration.ProfileConfigurationWidget;

public class ProfileDetailActivity extends AppCompatActivity implements ProfileLoader.OnProfileLoaded {

    // region Constants
    public static final String PROFILE_NAME = "profile_name";
    // endregion

    // region Variables

    LinearLayout container;
    OutputProfile profile;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail);
        binding.setController(this);
        container = binding.profileContainer;

        String name;
        if (savedInstanceState != null) {
            name = savedInstanceState.getString(PROFILE_NAME);
        } else {
            Intent intent = getIntent();
            name = intent.getStringExtra(PROFILE_NAME);
        }

        profile = new OutputProfile(name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ProfileLoader(profile, this).execute(getFilesDir());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PROFILE_NAME, profile.getName());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        File file = ProfileManager.buildProfileFilePath(getFilesDir(), profile);
        try {
            profile.getHandler().write(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onProfileLoaded() {
        container.removeAllViews();
        for (OutputConfiguration outputConfiguration : profile.mOutputConfigurationList) {
            ProfileConfigurationWidget widget = new ProfileConfigurationWidget(this);
            widget.setOutputConfiguration(outputConfiguration);
            container.addView(widget);
        }
    }
}
