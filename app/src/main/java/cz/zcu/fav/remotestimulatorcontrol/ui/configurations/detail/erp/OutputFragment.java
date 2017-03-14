package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.erp;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ErpOutputConfigBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.MediaChoserActivity;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class OutputFragment extends Fragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "ERPOutputFragment";

    private static final int REQUEST_SELECT_OUTPUT = 1;
    // endregion

    // region Variables
    private ConfigurationERP.Output mOutput;
    @SuppressWarnings("unused")
    // Listener pro změnu hodnoty parametru distribution value
    public final EditableSeekBar.OnEditableSeekBarProgressChanged distributionValueChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mOutput.setDistributionValue(progress);
        }
    };
    @SuppressWarnings("unused")
    // Listener pro změnu intenzity jasu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged brightnessChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mOutput.setBrightness(progress);
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ErpOutputConfigBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.erp_output_config, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(mOutput.getParentConfiguration());
        mBinding.setOutput(mOutput);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_OUTPUT:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }

                int index = data.getIntExtra(MediaChoserActivity.MEDIA_INDEX, -1);
                if (index == -1) {
                    return;
                }

                break;
        }
    }

    /**
     * Inicializuje output
     *
     * @param output Output
     */
    void setOutput(ConfigurationERP.Output output) {
        mOutput = output;
    }
}
