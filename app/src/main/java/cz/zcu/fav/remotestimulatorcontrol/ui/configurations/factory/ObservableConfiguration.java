package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity.FLAG_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity.FLAG_TYPE;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ConfigurationFactoryActivity}
 */
public class ObservableConfiguration extends BaseObservable implements IValidate {

    // region Variables
    // Validita konfigurace - 0 = validní
    @Bindable
    private int validityFlag = FLAG_NAME | FLAG_TYPE;
    // Název konfigurace
    @Bindable
    private String name = "";
    // Typ konfigurace
    @Bindable
    private ConfigurationType configurationType = ConfigurationType.UNDEFINED;
    // Příznak indikující, zda-li byla změněna interní datová struktura konfigurace
    @Bindable
    private boolean changed = false;
    // Příznak indikující, zda-li je konfigurace validní
    @Bindable
    private boolean valid;
    // endregion

    // region Public methods

    /**
     * Nastaví validační příznak
     *
     * @param validity Validační příznak
     */
    public void setValidity(int validity) {
        this.validityFlag = validity;
        notifyPropertyChanged(BR.validity);
    }

    @Override
    public int getValidityFlag() {
        return changed ? validityFlag : 0;
    }

    @Override
    public boolean isFlagValid(int flag) {
        return !((validityFlag & flag) == flag);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
        notifyPropertyChanged(BR.valid);
    }

    /**
     * Nastaví validitu zadanému příznaku
     *
     * @param flag  Příznak
     * @param value True, pokud je příznak validní, jinak false
     */
    protected void setValidityFlag(int flag, boolean value) {
        int oldFlagValue = this.validityFlag;
        if (value) {
            validityFlag |= flag;
        } else {
            validityFlag &= ~flag;
        }

        if (validityFlag == oldFlagValue) {
            return;
        }

        notifyPropertyChanged(BR.validityFlag);

        if (validityFlag == 0) {
            setValid(true);
        }
    }

    // endregion

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
        return changed ? validityFlag : 0;
    }

    // endregion
}