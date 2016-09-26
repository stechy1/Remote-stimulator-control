package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

public enum ConfigurationType {

    ERP, FVEP, TVEP, CVEP, REA, AUT, BIO, UNDEFINED;

    /**
     * Vrátí položku podle indexu
     *
     * @param index Index položky
     * @return ConfigurationType
     */
    public static ConfigurationType valueOf(int index) {
        return ConfigurationType.values()[index];
    }
}
