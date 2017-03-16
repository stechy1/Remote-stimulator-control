package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory;

import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity.FLAG_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity.FLAG_TYPE;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ConfigurationFactoryActivity}
 */
public class ObservableConfiguration extends BaseModel {

    // region Variables
    @Bindable
    private String name = "";
    // Typ konfigurace
    @Bindable
    private ConfigurationType configurationType = ConfigurationType.UNDEFINED;
    // endregion

    {
        validityFlag = FLAG_NAME | FLAG_TYPE;
    }

    // region Getters & Setters

    /**
     * Vrátí typ konfigurace
     *
     * @return Typ konfigurace
     */
    public String getConfigurationType() {
        return configurationType.name();
    }

    /**
     * Nastaví typ konfigurace
     *
     * @param type Nový typ konfigurace
     */
    public void setConfigurationType(ConfigurationType type) {
        this.configurationType = type;
        changed = true;
        notifyPropertyChanged(BR.configurationType);

        if (type == ConfigurationType.UNDEFINED) {
            setValid(false);
            setValidityFlag(FLAG_TYPE, true);
        } else {
            setValidityFlag(FLAG_TYPE, false);
        }
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
            setValid(false);
            setValidityFlag(FLAG_NAME, true);
        } else {
            setValidityFlag(FLAG_NAME, false);
        }
    }

    // endregion
}