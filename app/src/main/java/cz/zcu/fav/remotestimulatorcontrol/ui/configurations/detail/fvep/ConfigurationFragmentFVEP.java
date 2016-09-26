package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.fvep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentConfigurationDetailFvepBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ADetailFragment;

public class ConfigurationFragmentFVEP extends ADetailFragment {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigFragmentFVEP";

    private FragmentConfigurationDetailFvepBinding mBinding;
    private ConfigurationFVEP configuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration_detail_fvep, container, false);

        mBinding.pagerOutput.setAdapter(new ViewPagerOutputAdapter(getChildFragmentManager(), configuration.outputList));

        return mBinding.getRoot();
    }

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    @Override
    public void setConfiguration(AConfiguration configuration) {
        this.configuration = (ConfigurationFVEP) configuration;
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
            mBinding.pagerOutput.getAdapter().notifyDataSetChanged();
        }
    }
}
