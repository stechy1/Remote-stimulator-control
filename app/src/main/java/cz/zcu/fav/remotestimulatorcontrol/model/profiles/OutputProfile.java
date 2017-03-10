package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

/**
 * Třída představující jeden profil výstupů
 */
public final class OutputProfile extends BaseObservable {

    // region Constants
    private static final int OUTPUT_COUNT = 8;
    // endregion

    // region Variables
    // Název profilu
    @Bindable
    private String name;
    // Kolekce konfigurací jednotlivých výstupů
    public final ObservableList<OutputConfiguration> mOutputConfigurationList;
    // endregion

    // region Constructors

    /**
     * Vytvoří nový profil výstupů
     *
     * @param name Název profilu
     */
    public OutputProfile(String name) {
        setName(name);
        this.mOutputConfigurationList = new ObservableArrayList<>();
    }

    // endregion

    // region Private methods

    // endregion

    // region Public methods

    // endregion

    // region Getters & Setters

    /**
     * Vrátí název profilu
     *
     * @return Název profilu
     */
    public String getName() {
        return name;
    }

    /**
     * Nastaví nový název profilu
     *
     * @param name Nový název profilu
     */
    public void setName(String name) {
        this.name = name;
    }

    // endregion
}
