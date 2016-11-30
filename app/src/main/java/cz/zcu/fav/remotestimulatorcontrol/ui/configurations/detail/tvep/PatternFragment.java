package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.TvepPatternConfigBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.patternlayout.PatternWidget;

public class PatternFragment extends Fragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "PatternFragment";
    // endregion

    // region Variables
    private ConfigurationTVEP mConfiguration;
    private ConfigurationTVEP.Pattern mPattern;

    // Listener pro změnu hodnoty patternu
    public final PatternWidget.OnBitChangeListener patternValueChange = new PatternWidget.OnBitChangeListener() {
        @Override
        public void onBitChange(int oldValue, int newValue) {
            mPattern.setValue(newValue);
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TvepPatternConfigBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.tvep_pattern_config, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(mConfiguration);
        mBinding.setPattern(mPattern);

        return mBinding.getRoot();
    }

    // region Public methods
    /**
     * Nastaví konfiguraci
     *
     * @param configuration Konfigurace TVEP
     */
    void setConfiguration(ConfigurationTVEP configuration) {
        mConfiguration = configuration;
    }

    /**
     * Nastaví pattern
     *
     * @param pattern Pattern
     */
    void setPattern(ConfigurationTVEP.Pattern pattern) {
        mPattern = pattern;
    }
    // endregion
}
