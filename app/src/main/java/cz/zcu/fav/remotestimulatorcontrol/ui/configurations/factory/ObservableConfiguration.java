package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity.FLAG_NAME;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ConfigurationFactoryActivity}
 */
public class ObservableConfiguration extends BaseObservable {

    @Bindable
    int validity = FLAG_NAME;
    @Bindable
    String name = "";
    @Bindable
    ConfigurationType configurationType = ConfigurationType.ERP;
    @Bindable
    boolean changed = false;

    /**
     * Nastaví typ konfigurace
     *
     * @param type Nový typ konfigurace
     */
    public void setConfigurationType(ConfigurationType type) {
        this.configurationType = type;

        notifyPropertyChanged(BR.configurationType);
    }

    /**
     * Vrátí typ konfigurace
     *
     * @return Typ konfigurace
     */
    public String getConfigurationType() {
        return configurationType.name();
    }

    /**
     * Vrátí název konfigurace
     *
     * @return Název konfigurace
     */
    public String getName() {
        return name;
    }

    /**
     * Nastaví název konfigurace
     *
     * @param name Název konfigurace
     */
    public void setName(String name) {
        this.name = name;
        changed = true;
        notifyPropertyChanged(BR.name);

        if (!AConfiguration.isNameValid(name)) {
            setValidity(FLAG_NAME);
        } else {
            setValidity(0);
        }
    }

    /**
     * Vrátí true, pokud se změnil vnitřní stav objektu, jinak false
     *
     * @return True, pokud se změnil vnitřní stav objektu, jinak false
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Vrátí validační příznak
     *
     * @return Validační příznak
     */
    public int getValidity() {
        return changed ? validity : 0;
    }

    /**
     * Nastaví validační příznak
     *
     * @param validity Validační příznak
     */
    public void setValidity(int validity) {
        this.validity = validity;
        notifyPropertyChanged(BR.validity);
    }
}