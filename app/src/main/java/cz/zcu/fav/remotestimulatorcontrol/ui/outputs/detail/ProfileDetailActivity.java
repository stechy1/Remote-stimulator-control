package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private static final String TAG = "ProfileDetailAct";
    private static final int REQUEST_MEDIA_SELECT = 1;

    public static final String PROFILE_NAME = "profile_name";
    // endregion

    // region Variables

    private LinearLayout container;
    private OutputProfile profile;
    private OutputConfiguration requestedConfiguration;

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
        binding.setProfile(profile);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ProfileLoader(profile, this).execute(getFilesDir());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MEDIA_SELECT:
                if (resultCode == RESULT_OK) {
                    if (requestedConfiguration != null) {
                        String path = data.getStringExtra(MediaChoserActivity.MEDIA_NAME);
                        requestedConfiguration.setMediaFile(new File(path));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            widget.setOnMediaRequestListener(mediaSelectRequestListener);
            container.addView(widget);
        }
    }

    private final ProfileConfigurationWidget.OnMediaSelectRequestListener mediaSelectRequestListener = new ProfileConfigurationWidget.OnMediaSelectRequestListener() {
        @Override
        public void onMediaSelectRequest(OutputConfiguration configuration) {
            requestedConfiguration = configuration;
            startActivityForResult(new Intent(ProfileDetailActivity.this, MediaChoserActivity.class), REQUEST_MEDIA_SELECT);
        }
    };
}