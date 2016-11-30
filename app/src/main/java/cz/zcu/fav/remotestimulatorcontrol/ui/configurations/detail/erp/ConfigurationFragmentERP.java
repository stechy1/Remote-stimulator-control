package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.erp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentConfigurationDetailErpBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.ADetailFragment;
import cz.zcu.fav.remotestimulatorcontrol.widget.labeledspinner.LabelledSpinner;

public class ConfigurationFragmentERP extends ADetailFragment {

    // region Constants
    // Logovací tag
    private static final String TAG = "ConfigFragmentERP";
    // endregion

    // region Variables
    private FragmentConfigurationDetailErpBinding mBinding;
    private ConfigurationERP mConfiguration;
    @SuppressWarnings("unused")
    // Listener pro změnu hodnoty parametru random
    public final LabelledSpinner.OnItemSelected randomListener = new LabelledSpinner.OnItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mConfiguration.setRandom(ConfigurationERP.Random.valueOf(position));
        }
    };
    @SuppressWarnings("unused")
    // Listener pro změnu hodnoty parametru edge
    public final LabelledSpinner.OnItemSelected edgeListener = new LabelledSpinner.OnItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mConfiguration.setEdge(ConfigurationERP.Edge.valueOf(position));
        }
    };
    // endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration_detail_erp, container, false);

        mBinding.setController(this);
        mBinding.setConfiguration(mConfiguration);
        mBinding.pagerOutput.setAdapter(new ViewPagerOutputAdapter(getChildFragmentManager(), mConfiguration.outputList));

        return mBinding.getRoot();
    }

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    @Override
    public void setConfiguration(AConfiguration configuration) {
        mConfiguration = (ConfigurationERP) configuration;
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
            mBinding.pagerOutput.getAdapter().notifyDataSetChanged();
        }
    }
}
