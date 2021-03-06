package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.fvep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FvepOutputConfigBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class OutputFragment extends Fragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "OutputFragmentFVEP";
    // endregion

    // region Variables
    private ConfigurationFVEP.Output mOutput;

    // Listener pro změnu intenzity jasu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged brightnessChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mOutput.setBrightness(progress);
        }
    };
    public final EditableSeekBar.OnEditableSeekBarProgressChanged frequencyChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mOutput.setFrequency(progress);
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FvepOutputConfigBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fvep_output_config, container, false);

        mBinding.setController(this);
        mBinding.setOutput(mOutput);

        return mBinding.getRoot();
    }

    // region Public methods
    /**
     * Inicializuje output
     *
     * @param output Output
     */
    void setOutput(ConfigurationFVEP.Output output) {
        mOutput = output;
    }
    // endregion
}
