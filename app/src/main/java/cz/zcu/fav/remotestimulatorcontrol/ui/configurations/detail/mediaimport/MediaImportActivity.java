package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.mediaimport;

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

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityMediaImportBinding;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;

public class MediaImportActivity extends AppCompatActivity {

    // region Constants
    private static final int REQUEST_FILE_PATH = 1;
    private static final int REQUEST_PERMISSION = 2;
    public static final String TAG = "MediaImportActivity";
    public static final String MEDIA_PATH = "media_path";
    public static final int FLAG_PATH = 1;
    // endregion

    // region Variables
    private final ObservableMedia media = new ObservableMedia();

    private boolean permissionGranted = false;
    // endregion

    // region Private methods
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void verifyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            permissionGranted = true;
        }
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(MEDIA_PATH);
            media.setPath(path);
        }

        ActivityMediaImportBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_media_import);
        binding.setControler(this);
        binding.setMedia(media);

        verifyPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_PATH:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);

                    media.setPath(path);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(MEDIA_PATH, media.getPath());
    }

    // region Button handlers
    public void onFilePathRequest(View view) {
        if (!permissionGranted) {
            Log.i(TAG, "Nebyla udělena práva pro přístup k úložišti");
            verifyPermission();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*, audio/*");
        startActivityForResult(Intent.createChooser(intent, "Vyberte externí médium"), REQUEST_FILE_PATH);
    }

    public void onCancel(View view) {
        finish();
    }

    public void onImport(View view) {
        String path = media.getPath();

        Intent intent = new Intent();
        intent.putExtra(MEDIA_PATH, path);

        setResult(RESULT_OK, intent);
        finish();
//        try {
//            AConfiguration configuration = ConfigurationHelper.from(this.configuration.getName(), this.configuration.getConfigurationType());
//            configuration.metaData.extensionType = this.configuration.getExtensionType();
//
//            configuration.getHandler().read(new FileInputStream(this.configuration.getFilePath()));
//        } catch (Exception e) {
//            Log.e(TAG, "Konfiguraci se nepodařilo importovat", e);
//            Toast.makeText(this, R.string.import_failded, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.putExtra(CONFIGURATION_FILE_PATH, this.configuration.getFilePath());
//        intent.putExtra(CONFIGURATION_NAME, this.configuration.getName());
//        intent.putExtra(CONFIGURATION_EXTENSION, this.configuration.getExtensionType());
//        intent.putExtra(CONFIGURATION_TYPE, this.configuration.getConfigurationType());
//
//        setResult(RESULT_OK, intent);
//        finish();

    }
    // endregion
}
