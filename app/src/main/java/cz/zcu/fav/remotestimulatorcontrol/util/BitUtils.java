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

    /**
     * Vyparsuje z bytového pole integer na základě offsetu
     *
     * @param src Pole bytů
     * @param offset Offset, od kterého se má číst číslo
     * @return Číslo, které je sestavené ze čtyř bytů
     */
    public static int intFromBytes(byte[] src, int offset) {
        int fileSize = 0;
        fileSize |= ((src[offset + 0] << 24) & 0xFF000000); // První byte
        fileSize |= ((src[offset + 1] << 16) & 0xFF0000);   // Druhý byte
        fileSize |= ((src[offset + 2] << 8) & 0xFF00);      // Třetí byte
        fileSize |= (src[offset + 3] & 0xFF);               // Čtvrtý byte

        return fileSize;
    }

    /**
     * Naparsuje integer do bytového pole od zadaného offsetu
     *
     * @param value Hodnota, která se má parsovat
     * @param dest Cílové pole, do kterého se hodnota vloží
     * @param offset Offset v poli
     */
    public static void intToBytes(int value, byte[] dest, int offset) {
        dest[offset + 0] = (byte) ((value >>> 24) & 0xFF); // První byte
        dest[offset + 1] = (byte) ((value >>> 16) & 0xFF); // První byte
        dest[offset + 2] = (byte) ((value >>> 8) & 0xFF); // První byte
        dest[offset + 3] = (byte) (value  & 0xFF);        // První byte
    }

    /**
     * Metoda pro konverzi bytů na hexa
     *
     * @param bytes Byty, které se mají konvertovat
     * @return Řetězec z hexa znaků
     */
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b : a) {
            sb.append(String.format("%02x ", b));
        }
        return sb.toString();
    }
}
