package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

/**
 * Rozhraní pro popis metod, které slouží k práci s validitou objektu
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
     * Zjistí validitu celého objektu
     *
     * @return True, pokud je celý objekt validní, jinak false
     */
    boolean isValid();

    /**
     * Nastavi validitu objektu
     *
     * @param valid True, pokud je objekt validní, jinak false
     */
    void setValid(boolean valid);

    /**
     * Zjistí, zda-li se objekt změnil od posledního použití
     *
     * @return True, pokud se objekt změnil, jinak false
     */
    boolean isChanged();

    /**
     * Nastaví příznak, zda-li je objekt validní, či nikoliv
     * Nemělo by se nastavovat svévolně, pouze v nutných případech
     *
     * @param changed True, pokud je objekt validní, jinak false
     */
    void setChanged(boolean changed);
}
