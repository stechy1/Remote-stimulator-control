package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.factory;

import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static cz.zcu.fav.remotestimulatorcontrol.ui.outputs.factory.ProfileFactoryActivity.FLAG_NAME;

/**
 * Pomocný profil sloužící pro databingind třídy {@link ProfileFactoryActivity}
 */
public class ObservableProfile extends BaseModel {

    // region Constants

    // endregion

    // region Variables
    // Název profilu
    @Bindable
    private String name;
    // endregion

    {
        validityFlag = FLAG_NAME;
    }


    // region Getters & Setters

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
