package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.rea;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentConfigurationDetailReaBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.ConfigurationREA;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ADetailFragment;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class ConfigurationFragmentREA extends ADetailFragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "ConfigFragmentREA";
    // endregion

    // region Variables
    private ConfigurationREA mConfiguration;

    public final EditableSeekBar.OnEditableSeekBarProgressChanged brightnessChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mConfiguration.setBrightness(progress);
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentConfigurationDetailReaBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration_detail_rea, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(mConfiguration);

        return mBinding.getRoot();
    }

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    @Override
    public void setConfiguration(AConfiguration configuration) {
        mConfiguration = (ConfigurationREA) configuration;
    }

    // region Public methods
    public void onFailRadioButtonClicked(View view) {
        boolean checked = ((AppCompatRadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioOnFailWait:
                mConfiguration.setOnFail(checked ? ConfigurationREA.OnFail.WAIT : ConfigurationREA.OnFail.CONTINUE);
                break;
            case R.id.radioOnFailContinue:
                mConfiguration.setOnFail(checked ? ConfigurationREA.OnFail.CONTINUE : ConfigurationREA.OnFail.WAIT);
                break;
        }
    }

    public void onGenderRadioButtonClicked(View view) {
        boolean checked = ((AppCompatRadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioGenderMale:
                mConfiguration.setGender(checked ? ConfigurationREA.Gender.MALE : ConfigurationREA.Gender.FEMALE);
                break;
            case R.id.radioGenderFemale:
                mConfiguration.setGender(checked ? ConfigurationREA.Gender.FEMALE : ConfigurationREA.Gender.MALE);
                break;
        }
    }
    // endregion
}
