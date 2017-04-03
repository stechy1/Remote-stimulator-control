package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

/**
 * Třída představující jednu kódovou značku z protokolu
 */
public class Code {

    // Kódová značka
    public final byte code;
    // Popis kódové značky
    public final String description;
    // Reference na následující kódovou značku
    public final Code next;

    /**
     * Vytvoří novou kódovou značku
     *
     * @param code Identifikátor kódové značky
     * @param description Popis
     */
    public Code(byte code, String description) {
        this(code, description, null);
    }

    /**
     * Vytvoří novou kódovou značku s následníkem
     *
     * @param code Identifikátor kódové značky
     * @param description Popis
     * @param next Reference na další kódovou značku v pořadí
     */
    public Code(byte code, String description, Code next) {
        this.code = code;
        this.description = description;
        this.next = next;
    }
}
