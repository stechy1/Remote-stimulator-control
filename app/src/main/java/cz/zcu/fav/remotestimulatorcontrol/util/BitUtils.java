package cz.zcu.fav.remotestimulatorcontrol.util;

/**
 * Pomocní knihovní třída pro práci s bity
 */
public final class BitUtils {

    /**
     * Soukromý konstruktor
     */
    private BitUtils() {
        throw new AssertionError();
    }

    /**
     * Nastaví příznak na zadanou pozici
     *
     * @param original Číslo představující kontejner obsahující příznaky
     * @param flag Příznak, který se má nastavit - udává index
     * @param value Hodnota, který se má zapsat
     * @return Změněnou hodnotu
     */
    public static int setBit(int original, int flag, boolean value) {
        if (value) {
            original |= flag;
        } else {
            original &= ~flag;
        }

        return original;
    }

    /**
     * Zjistí, zda-li je bit na zadané pozici nastaven na 1
     *
     * @param original Číslo představující kontejner obsahující příznaky
     * @param value Hodnota, která se testuje
     * @return True, pokud "original" obsahuje na indexu "value" hodnotu "1"
     */
    public static boolean isBitSet(int original, int value) {
        return (original & value) == value;
    }

    /**
     * Vymaže příznak z hodnoty
     *
     * @param original Číslo představující kontejner obsahující příznaky
     * @param value Hodnota, která ukazuje, jaký index v "originálu" se má nastavit na "0"
     * @return Změněnou hodnotu
     */
    public static int clearBit(int original, int value) {
        return setBit(original, value, false);
    }
}
