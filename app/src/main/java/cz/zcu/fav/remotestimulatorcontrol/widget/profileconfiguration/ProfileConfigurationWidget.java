package cz.zcu.fav.remotestimulatorcontrol.widget.profileconfiguration;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.OutputRowBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.MediaType;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputConfiguration;

public class ProfileConfigurationWidget extends LinearLayout {

    // region Constants

    // endregion

    // region Variables
    private OutputConfiguration outputConfiguration;
    private OutputRowBinding mBinding;
    // endregion

    // region Constructors

    public ProfileConfigurationWidget(Context context) {
        this(context, null);
    }

    public ProfileConfigurationWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileConfigurationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeLayout(context, attrs);
    }

    // endregion

    // region Private methods
    private void initializeLayout(Context context, AttributeSet attrs) {
        prepareLayout(context);
    }

    private void prepareLayout(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.output_row, this, true);
        mBinding.setController(this);
    }
    // endregion

    // region Public methods
    public void setOutputConfiguration(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = outputConfiguration;
        mBinding.setConfiguration(outputConfiguration);
        mBinding.executePendingBindings();
    }

    /**
     * Metoda která se zavolá při stisku nějakého radiobutonu
     *
     * @param view
     */
    public void onMediaRadioClick(View view) {
        switch (view.getId()) {
            case R.id.radioMediaLed:
                outputConfiguration.setMediaType(MediaType.LED);
                break;

            case R.id.radioMediaAudio:
                outputConfiguration.setMediaType(MediaType.AUDIO);
                break;

            case R.id.radioMediaImage:
                outputConfiguration.setMediaType(MediaType.IMAGE);
                break;
        }
    }
    // endregion
}
