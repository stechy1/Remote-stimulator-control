package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation;

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
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.FileInputStream;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationImportBinding;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.ConfigurationHelper;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.widget.labeledspinner.LabelledSpinner;


public class ConfigurationImportActivity extends AppCompatActivity {

    public static final int FLAG_NAME = 1 << 0;
    public static final int FLAG_TYPE = 1 << 1;
    public static final int FLAG_PATH = 1 << 2;
    public static final String CONFIGURATION_FILE_PATH = "path";
    public static final String CONFIGURATION_NAME = "name";
    public static final String CONFIGURATION_TYPE = "type";
    public static final String CONFIGURATION_EXTENSION = "extension";
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ImportActivity";
    private static final int REQUEST_FILE_PATH = 1;
    private static final int REQUEST_PERMISSION = 2;
    private final ObservableConfiguration configuration = new ObservableConfiguration();
    @SuppressWarnings("unused")
    public final LabelledSpinner.OnItemSelected typeListener = new LabelledSpinner.OnItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            configuration.setConfigurationType(ConfigurationType.valueOf(position));
        }
    };
    private boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(CONFIGURATION_FILE_PATH);
            String name = savedInstanceState.getString(CONFIGURATION_NAME);
            ConfigurationType type = (ConfigurationType) savedInstanceState.getSerializable(CONFIGURATION_TYPE);
            ExtensionType extensionType = (ExtensionType) savedInstanceState.getSerializable(CONFIGURATION_EXTENSION);

            configuration.setFileURI(new Uri.Builder().appendEncodedPath(path).build());
            configuration.setName(name);
            configuration.setConfigurationType(type);
            configuration.setExtensionType(extensionType);
        }

        ActivityConfigurationImportBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_import);
        mBinding.setController(this);
        mBinding.setConfiguration(configuration);

        mBinding.textInputLayout.clearFocus();

        verifyPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_PATH:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    configuration.setFileURI(uri);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CONFIGURATION_FILE_PATH, configuration.getFilePath());
        outState.putString(CONFIGURATION_NAME, configuration.getName());
        outState.putSerializable(CONFIGURATION_TYPE, configuration.getConfigurationType());
        outState.putSerializable(CONFIGURATION_EXTENSION, configuration.getExtensionType());

        super.onSaveInstanceState(outState);
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

    public void onFilePathRequest(View view) {
        if (!permissionGranted) {
            Log.i(TAG, "Nebyla udělena práva pro přístup k úložišti");
            verifyPermission();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml,text/json,text/csv");
        startActivityForResult(Intent.createChooser(intent, "Vyberte konfiguraci"), REQUEST_FILE_PATH);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void verifyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            permissionGranted = true;
        }
    }

    public void onCancel(View view) {
        finish();
    }

    public void onImport(View view) {

        try {
            AConfiguration configuration = ConfigurationHelper.from(this.configuration.getName(), this.configuration.getConfigurationType());
            configuration.metaData.extensionType = this.configuration.getExtensionType();

            configuration.getHandler().read(new FileInputStream(this.configuration.getFilePath()));
        } catch (Exception e) {
            Log.e(TAG, "Konfiguraci se nepodařilo importovat", e);
            Toast.makeText(this, R.string.import_failded, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(CONFIGURATION_FILE_PATH, this.configuration.getFilePath());
        intent.putExtra(CONFIGURATION_NAME, this.configuration.getName());
        intent.putExtra(CONFIGURATION_EXTENSION, this.configuration.getExtensionType());
        intent.putExtra(CONFIGURATION_TYPE, this.configuration.getConfigurationType());

        setResult(RESULT_OK, intent);
        finish();

    }
}
