package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.factory;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.factory.ConfigurationFactoryActivity;

import static cz.zcu.fav.remotestimulatorcontrol.ui.outputs.factory.ProfileFactoryActivity.FLAG_NAME;

/**
 * Pomocný profil sloužící pro databingind třídy {@link ConfigurationFactoryActivity}
 */
public class ObservableProfile extends BaseObservable implements IValidate {

    // region Constants

    // endregion

    // region Variables
    // Název profilu
    @Bindable
    private String name;
    // Validita konfigurace - 0 = validní
    @Bindable
    private int validityFlag = FLAG_NAME;
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

    // endregion

    // region Getters & Setters

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
