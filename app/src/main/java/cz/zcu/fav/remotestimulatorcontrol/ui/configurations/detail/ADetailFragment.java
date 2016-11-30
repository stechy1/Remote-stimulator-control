package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.support.v4.app.Fragment;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

/**
 * Předek tříd pro detail fragmentu konfigurace
 */
public abstract class ADetailFragment extends Fragment implements OnOutputCountChange {

    /**
     * Nastaví konfiguraci do fragmentu
     *
     * @param configuration Konfigurace
     */
    public abstract void setConfiguration(AConfiguration configuration);

    /**
     * Metoda zachycující změnu počtu výstupů
     *
     * @param outputCount Nový počet výstupů
     */
    @Override
    public void onOutputCountChange(int outputCount) {
        // Tady ve výchozím stavu nic nebude
    }
}
