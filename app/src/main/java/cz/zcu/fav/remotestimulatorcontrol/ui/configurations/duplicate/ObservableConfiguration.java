package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.duplicate;

import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.duplicate.ConfigurationDuplicateActivity.FLAG_NAME;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ConfigurationDuplicateActivity}
 */
public class ObservableConfiguration extends BaseModel {

    // region Variables
    // Název konfigurace
    @Bindable
    private String name = "";
    // Starý název
    private String oldName;
    // Id konfigurace
    private int id;
    // endregion

    // region Public methods
    /**
     * Vrátí původní název konfigurace
     *
     * @return Původní název konfigurace
     */
    public String getOldName() {
        return oldName;
    }

    /**
     * Nastaví původní název konfigurace
     *
     * @param oldName Původní název konfigurace
     */
    public void setOldName(String oldName) {
        this.oldName = oldName;
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

        if (!AConfiguration.isNameValid(name) || oldName.equals(name)) {
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
     * Nastaví příznak, že je konfigurace změněna
     *
     * @param changed True, pokud je konfigurace změněna, jinak false
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
        notifyPropertyChanged(BR.changed);
    }

    /**
     * Vrátí Id konfigurace
     *
     * @return Id konfigurace
     */
    public int getId() {
        return id;
    }

    /**
     * Nastaví Id konfigurace
     *
     * @param id Id konfigurace
     */
    public void setId(int id) {
        this.id = id;
    }
    // endregion
}