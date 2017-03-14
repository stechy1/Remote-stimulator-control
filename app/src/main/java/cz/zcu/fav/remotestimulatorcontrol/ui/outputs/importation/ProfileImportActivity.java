package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityProfileImportBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputProfile;

public class ProfileImportActivity extends AppCompatActivity {

    // region Constants
    public static final int FLAG_NAME = 1 << 0;
    public static final int FLAG_PATH = 1 << 2;
    public static final String PROFILE_FILE_PATH = "path";
    public static final String PROFILE_NAME = "name";
    // Logovací tag
    private static final String TAG = "ImportActivity";
    private static final int REQUEST_FILE_PATH = 1;
    private static final int REQUEST_PERMISSION = 2;
    // endregion

    // region Variables
    private final ObservableProfile mProfile = new ObservableProfile();

    private boolean mPermissionGranted = false;
    // endregion

    // region Private methods
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void verifyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            mPermissionGranted = true;
        }
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(PROFILE_FILE_PATH);
            String name = savedInstanceState.getString(PROFILE_NAME);

            mProfile.setFileURI(new Uri.Builder().appendEncodedPath(path).build());
            mProfile.setName(name);
        }

        ActivityProfileImportBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_import);
        binding.setProfile(mProfile);
        binding.textInputLayout.clearFocus();

        verifyPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_PATH:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mProfile.setFileURI(uri);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PROFILE_FILE_PATH, mProfile.getFilePath());
        outState.putString(PROFILE_NAME, mProfile.getName());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                }
                break;
        }
    }

    public void onFilePathRequest(View view) {
        if (!mPermissionGranted) {
            Log.i(TAG, "Nebyla udělena práva pro přístup k úložišti");
            verifyPermission();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");
        startActivityForResult(Intent.createChooser(intent, "Vyberte profil"), REQUEST_FILE_PATH);
    }

    // region Public methods
    public void onCancel(View view) {
        finish();
    }

    public void onImport(View view) {

        try {
            OutputProfile profile = new OutputProfile(this.mProfile.getName());
            profile.getHandler().read(new FileInputStream(this.mProfile.getFilePath()));

        } catch (Exception e) {
            Log.e(TAG, "Profil se nepodařilo importovat", e);
            Toast.makeText(this, R.string.import_failded, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(PROFILE_FILE_PATH, this.mProfile.getFilePath());
        intent.putExtra(PROFILE_NAME, this.mProfile.getName());

        setResult(RESULT_OK, intent);
        finish();

    }
    // endregion
}
