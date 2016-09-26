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

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigFragmentTVEP";

    private FragmentConfgurationDetailTvepBinding mBinding;
    private ConfigurationTVEP configuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_confguration_detail_tvep, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(configuration);
        mBinding.pagerPattern.setAdapter(new ViewPagerPatternAdapter(getChildFragmentManager(), configuration));

        return mBinding.getRoot();
    }

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    @Override
    public void setConfiguration(AConfiguration configuration) {
        this.configuration = (ConfigurationTVEP) configuration;
    }

    /**
     * Metoda zachycující změnu počtu výstupů
     *
     * @param outputCount Nový počet výstupů
     */
    @Override
    public void onOutputCountChange(int outputCount) {
        configuration.setOutputCount(outputCount);
        if (mBinding != null) {
            mBinding.pagerPattern.getAdapter().notifyDataSetChanged();
        }
    }

    @SuppressWarnings("unused")
    // Listener pro změnu délky patternu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged patternLengthChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            configuration.setPatternLength(progress);
        }
    };

    @SuppressWarnings("unused")
    // Listener pro změnu intenzity jasu
    public final EditableSeekBar.OnEditableSeekBarProgressChanged brightnessChanged = new EditableSeekBar.OnEditableSeekBarProgressChanged() {
        @Override
        public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
            configuration.setBrightness(progress);
        }
    };
}
