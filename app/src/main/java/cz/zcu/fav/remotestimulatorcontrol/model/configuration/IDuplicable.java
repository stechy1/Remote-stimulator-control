package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

/**
 * Kontrakt pro duplikovatelné konfigurace
 */
public interface IDuplicable {

    /**
     * Vytvoří a vrátí novou zduplikovanou konfiguraci
     *
     * @param newName Nový název konfigurace
     * @return Novou zduplikovanou konfiguraci
     */
    AConfiguration duplicate(String newName);
}
