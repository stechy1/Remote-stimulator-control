package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;

@SuppressWarnings("unused")
public class ConfigurationBindings {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigurationBindings";
    
    private static final int[] drawable = {
            R.drawable.bg_erp,
            R.drawable.bg_tvep,
            R.drawable.bg_fvep,
            R.drawable.bg_cvep,
            R.drawable.bg_rea
    };

    @InverseBindingAdapter(attribute = "value")
    public static String getTextFromEditText(TextInputEditText editText) {
        return editText.getText().toString();
    }

    @BindingAdapter(value = {"validityFlag", "validity", "errorText"}, requireAll = false)
    public static void setErrorMessage(TextInputLayout view, int validityFlag, int validity, String message) {
        view.setError(((validity & validityFlag) == validityFlag) ? message : null);
    }

    @BindingAdapter(value = {"android:background", "valid"}, requireAll = false)
    public static void setColorByConfigurationType(TextView textView, String type, boolean valid) {
        int resourceID;
        if (valid) {
            resourceID = drawable[ConfigurationType.valueOf(type).ordinal()];
        } else {
            resourceID = R.drawable.bg_invalid;
        }

        final Drawable drawable = ContextCompat.getDrawable(textView.getContext(), resourceID);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(drawable);
        }
    }

    @BindingAdapter({"configuration_type", "valid"})
    public static void setBackgroundColorByConfigurationType(ImageView imageView, String type, boolean valid) {
        if (valid) {
            int[] validBackgrounds = imageView.getContext().getResources().getIntArray(R.array.config_type_color_array);
            imageView.setBackgroundColor(validBackgrounds[ConfigurationType.valueOf(type).ordinal()]);
        } else {
            int[] invalidBackground = imageView.getContext().getResources().getIntArray(R.array.config_invalid_color);
            imageView.setBackgroundColor(invalidBackground[0]);
        }
    }

    @BindingAdapter({"configuration_type", "valid"})
    public static void setToolbarBackground(Toolbar toolbar, String type, boolean valid) {
        if (valid) {
            int[] validBackgrounds = toolbar.getContext().getResources().getIntArray(R.array.config_type_color_array);
            toolbar.setBackgroundColor(validBackgrounds[ConfigurationType.valueOf(type).ordinal()]);
        } else {
            int[] invalidBackground = toolbar.getContext().getResources().getIntArray(R.array.config_invalid_color);
            toolbar.setBackgroundColor(invalidBackground[0]);
        }
    }

    @BindingAdapter({"media_type", "media_mask"})
    public static void setLedImageOverlay(ImageView imageView, int mediaType, MediaType mediaMask) {
        boolean visible = (mediaType & mediaMask.getOrdinal()) == mediaMask.getOrdinal();
        imageView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter({"media_type", "media_flag"})
    public static void mediaTypeProccess(CheckBox checkBox, int mediaType, MediaType mediaFlag) {
        boolean checked = (mediaType & mediaFlag.getOrdinal()) == mediaFlag.getOrdinal();
        checkBox.setChecked(checked);

        boolean invalid = AConfiguration.isInvalidMediaCombination(mediaType);
        if (!checked) {
            checkBox.setEnabled(!invalid);
        }
    }

    @BindingAdapter({"value", "flag"})
    public static void setCompatRadioButtonChecked(AppCompatRadioButton view, int value, int flag) {
        view.setChecked((value & flag) == flag);
    }

    @BindingAdapter({"value", "flag"})
    public static void setCheckBoxChecked(CheckBox view, int value, int flag) {
        view.setChecked((value & flag) == flag);
    }

    @BindingAdapter({"preview"})
    public static void setPreviewBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @BindingConversion
    public static String configurationTypeToString(ConfigurationType type) {
        return type.name();
    }

    @BindingConversion
    public static int observableIntToInt(ObservableInt observableInt) {
        return observableInt.get();
    }

    @BindingConversion
    public static boolean observableBooleanToBoolean(ObservableBoolean observableBoolean) {
        return observableBoolean.get();
    }
}
