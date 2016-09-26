package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

/**
 * Rozhraní pro popis validačních metod konfigurace
 */
public interface IValidate {

    /**
     * Vrází příznak validity parametrů
     *
     * @return Příznak validity parametrů
     */
    int getValidityFlag();

    /**
     * Zjistí, zda-li je příznak validní, nebo ne
     *
     * @param flag Příznak
     * @return True, pokud je validní, jinak false
     */
    boolean isFlagValid(int flag);

    /**
     * Zjistí validitu celkové konfigurace
     *
     * @return True, pokud je celá konfigurace validní, jinak false
     */
    boolean isValid();

    /**
     * Nastavi validitu konfigurace
     *
     * @param valid True, pokud je konfigurace validní, jinak false
     */
    void setValid(boolean valid);
}
