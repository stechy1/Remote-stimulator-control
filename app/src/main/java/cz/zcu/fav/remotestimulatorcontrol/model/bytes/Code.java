package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

/**
 * Třída představující jednu kódovou značku z protokolu
 */
public class Code {

    private final byte code;
    private final String description;
    private final Code next;

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

    /**
     * Vrátí kódovou značku (typ zprávy)
     *
     * @return typ zprávy
     */
    public byte getCode() {
        return code;
    }

    /**
     * Vrátí popis kódové značky
     *
     * @return popis značky
     */
    public String getDescription() {
        return description;
    }

    /**
     * Vrátí následující značku (následné značky se používají v případě,
     * že má kódová značka následovníka např. jas se nastavuje zvlášť)
     *
     * @return následující kódová značka
     */
    public Code getNext() {
        return next;
    }


}
