package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.cvep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentConfigurationDetailCvepBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.ConfigurationCVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ADetailFragment;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;
import cz.zcu.fav.remotestimulatorcontrol.widget.patternlayout.PatternWidget;

public class ConfigurationFragmentCVEP extends ADetailFragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "ConfiFragmentCVEP";
    // endregion

    // region Variables
    private ConfigurationCVEP mConfiguration;
    // Listener pro změnu hodnoty parametru bit shift
    public final EditableSeekBar.OnEditableSeekBarProgressChanged bitShiftChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mConfiguration.setBitShift(progress);
        }
    };
    // Listener pro změnu intenzity jasu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged brightnessChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mConfiguration.setBrightness(progress);
        }
    };
    // Listener pro změnu hodnoty hlavního patternu
    public final PatternWidget.OnBitChangeListener patternValueChange = new PatternWidget.OnBitChangeListener() {
        @Override
        public void onBitChange(int oldValue, int newValue) {
            mConfiguration.mainPattern.setValue(newValue);
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentConfigurationDetailCvepBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration_detail_cvep, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(mConfiguration);
        mBinding.setPattern(mConfiguration.mainPattern);

        return mBinding.getRoot();
    }

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    @Override
    public void setConfiguration(AConfiguration configuration) {
        mConfiguration = (ConfigurationCVEP) configuration;
    }

    // region Public methods
    /**
     * Vymaže hodnotu patternu
     */
    public void clearBitPattern() {
        mConfiguration.mainPattern.setValue(ConfigurationCVEP.Pattern.DEF_VALUE);
    }

    /**
     * Invertuje hodnotu patternu
     */
    public void toggleBitPattern() {
        mConfiguration.mainPattern.toggleValue();
    }
    // endregion
}
