package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

import java.util.Arrays;

/**
 * Třída představující jeden packet
 */
public class BtPacket {

    // region Constants

    private static final String TAG = "BtPacket";

    // Index určující pozici hlavičky packetu
    private static final int HEADER_INDEX = 0;
    // Maska v hlavičce pro parametr ID
    private static final int ID_MASK = 0b11000000;
    // Maska v hlavičce pro parametr DATA_LEN
    private static final int DATA_LEN_MASK = 0b111111;
    // Index určující pozici typu zprávy v packetu
    private static final int MESSAGE_TYPE_INDEX = 1;
    // Počet bytu, které zabírají hlavičku a typ zprávy
    private static final int HEADER_AND_MESSAGE_TYPE_SIZE = 2;

    // Velikost packetu
    public static final int PACKET_SIZE = 64;

    // endregion

    // region Variables

    // Obsah celého packetu
    protected final byte[] content;
    // Délka samotných dat v packetu
    private int dataLength;

    // endregion

    // region Constructors

    /**
     * Vytvoří prázdný packet s výchozí velikostí
     */
    public BtPacket() {
        this(new byte[PACKET_SIZE]);
    }

    /**
     * Vytvoří nový packet z bufferu
     *
     * @param buffer Buffer obsahující surová data packetu
     */
    public BtPacket(byte[] buffer) {
        this(buffer, buffer.length);
    }

    /**
     * Vytvoří nový packet z bufferu
     *
     * @param buffer Buffer obsahující surová data packetu
     * @param length Celková délka surových dat.
     *               Délka surových dat != délka obsahových dat.
     */
    public BtPacket(byte[] buffer, int length) {
        if (buffer.length > PACKET_SIZE) {
            throw new IllegalArgumentException("Velikost bufferu je moc velká");
        }

        content = new byte[PACKET_SIZE];
        System.arraycopy(buffer, 0, content, 0, length);

        // Velikost dat
        dataLength = length - HEADER_AND_MESSAGE_TYPE_SIZE;
    }

    // endregion

    // region Public methods

    /**
     * Vrátí obsah celého packetu připraveného k odeslání
     *
     * @return Obsah packetu v bytech
     */
    public byte[] getContent() {
        return content;
    }
    // endregion

    // region Getters & Setters

    // region Header

    /**
     * Vrátí obsah hlavičky packetu
     * Hlavička obsahuje dvě hodnoty:
     * <ul>
     *     <li>ID - 2 bity, rezerva pro další implementaci. Prozatím ponechat na 00</li>
     *     <li>LEN - 6 bitů, 0-62 délka dat v bytech</li>
     * </ul>
     *
     * @return Obsah hlavičky packetu
     */
    public byte getHeader() {
        return content[HEADER_INDEX];
    }

    /**
     * Nastaví novou hlavičku packetu
     *
     * @param header Nová hlavička packetu
     * @return {@link BtPacket} pro flow interface
     */
    public BtPacket setHeader(byte header) {
        content[HEADER_INDEX] = header;

        return this;
    }

    /**
     * Vrátí parametr Id z hlavičky
     *
     * @return Id parametr z hlavičky
     */
    public int getHeaderId() {
        return getHeader() & ID_MASK;
    }

    /**
     * Nastaví v hlavičce parametr Id
     *
     * @param id Nový parametr Id. Nosná informace je pouze ve spodních dvou bitech,
     *           zbytek se ořízne.
     * @return {@link BtPacket} pro flow interface
     */
    public BtPacket setHeaderId(byte id) {
        byte header = getHeader();
        header |= id << 6;
        setHeader(header);

        return this;
    }

    /**
     * Vrátí délku dat v počtu bytů
     *
     * @return Délku dat v počtu bytů
     */
    public int getDataLength() {
        return getHeader() & DATA_LEN_MASK;
    }

    /**
     * Nastaví délku přenášených dat
     *
     * @param length Délka přenášených dat
     * @return {@link BtPacket} pro flow interface
     */
    public BtPacket setDataLength(int length) {
        byte header = getHeader();
        header |= (length & ~ID_MASK);
        setHeader(header);

        return this;
    }

    // endregion

    // region Message type

    /**
     * Vrátí typ zprávy obsažené v datech
     *
     * @return Typ zprávy v datech
     */
    public byte getMessageType() {
        return content[MESSAGE_TYPE_INDEX];
    }

    /**
     * Nastaví nový typ zprávy
     *
     * @param messageType Typ zprávy
     * @return {@link BtPacket} pro flow interface
     */
    public BtPacket setMessageType(byte messageType) {
        content[MESSAGE_TYPE_INDEX] = messageType;

        return this;
    }

    // endregion

    // region Data

    /**
     * Vrátí obsah přenášených dat
     *
     * @return Obsah dat
     */
    public byte[] getData() {
        return Arrays.copyOfRange(content, HEADER_AND_MESSAGE_TYPE_SIZE - 1, content.length);
    }

    /**
     * Nastaví data packetu
     *
     * @param data Data
     * @return {@link BtPacket} pro flow interface
     */
    public BtPacket setData(byte[] data) {
        int dataLength = data.length;
        if (dataLength > PACKET_SIZE - HEADER_AND_MESSAGE_TYPE_SIZE) {
            throw new IllegalArgumentException("Velikost dat je příliš velká");
        }

        System.arraycopy(data, 0, content, HEADER_AND_MESSAGE_TYPE_SIZE, dataLength);
        setDataLength(dataLength);

        return this;
    }

    // endregion

    // endregion
}
