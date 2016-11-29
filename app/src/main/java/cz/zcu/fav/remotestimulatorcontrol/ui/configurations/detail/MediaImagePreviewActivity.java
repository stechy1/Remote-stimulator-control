package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityMediaImagePreviewBinding;

public class MediaImagePreviewActivity extends AppCompatActivity {

    public static final String IMAGE_PATH = "image_path";

    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString(IMAGE_PATH);
        } else {
            Intent intent = getIntent();
            filePath = intent.getStringExtra(IMAGE_PATH);
        }

        // TODO pokud bude potřeba, tak dodělat procházení v zadaně složce mezi obrázky
        ActivityMediaImagePreviewBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_media_image_preview);
        mBinding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(IMAGE_PATH, filePath);

        super.onSaveInstanceState(outState);
    }

    public void onClose(View view) {
        finish();
    }
}
