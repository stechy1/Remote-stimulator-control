package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentConfgurationDetailTvepBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ADetailFragment;
import cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar.EditableSeekBar;

public class ConfigurationFragmentTVEP extends ADetailFragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "ConfigFragmentTVEP";
    // endregion

    // region Variables
    private FragmentConfgurationDetailTvepBinding mBinding;
    private ConfigurationTVEP mConfiguration;
    // Listener pro změnu délky patternu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged patternLengthChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mConfiguration.setPatternLength(String.valueOf(progress));
        }
    };
    // Listener pro změnu intenzity jasu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged brightnessChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            mConfiguration.setBrightness(String.valueOf(progress));
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_confguration_detail_tvep, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(mConfiguration);
        mBinding.pagerPattern.setAdapter(new ViewPagerPatternAdapter(getChildFragmentManager(), mConfiguration));

        return mBinding.getRoot();
    }

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    @Override
    public void setConfiguration(AConfiguration configuration) {
        mConfiguration = (ConfigurationTVEP) configuration;
    }

    /**
     * Metoda zachycující změnu počtu výstupů
     *
     * @param outputCount Nový počet výstupů
     */
    @Override
    public void onOutputCountChange(int outputCount) {
        mConfiguration.setOutputCount(outputCount);
        if (mBinding != null) {
            mBinding.pagerPattern.getAdapter().notifyDataSetChanged();
        }
    }
}
