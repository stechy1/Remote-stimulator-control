package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;

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
    private String name = "";
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

    /**
     * Inicializuje list výchozími hodnotami
     */
    void fillOutputConfigurations() {
        for (int i = mOutputConfigurationList.size(); i < OUTPUT_COUNT; i++) {
            mOutputConfigurationList.add(new OutputConfiguration());
        }
    }
    // endregion

    // region Public methods

    /**
     * Vrátí handler, který umožňuje čtení a zápis profilu
     *
     * @return IO handler pro príci se souborem
     */
    public IOHandler getHandler() {
        return new XMLHandlerProfile(this);
    }

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
