package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.rename;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.rename.ConfigurationRenameActivity.FLAG_NAME;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ConfigurationRenameActivity}
 */
public class ObservableConfiguration extends BaseObservable {

    // region Variables
    // Starý název konfigurace
    private String oldName;
    // Validita konfigurace - 0 = validní
    @Bindable
    private int validity = FLAG_NAME;
    // Název konfigurace
    @Bindable
    private String name = "";
    // Příznak indikující, zda-li byla změněna interní datová struktura konfigurace
    @Bindable
    private boolean changed = false;
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
     * Nastaví příznak, že je konfigurace změněna
     *
     * @param changed True, pokud je konfigurace změněna, jinak false
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
        notifyPropertyChanged(BR.changed);
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