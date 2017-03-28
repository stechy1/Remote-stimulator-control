package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

import java.util.Arrays;

import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_COMMAND;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_DATA;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_HELLO_DATA;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_HELLO_VERSION;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_ITER;

/**
 * Rozšíření standartní třídy {@link BtPacket} o funkce pro práci s daty ze vzdáleného FS
 */
public class BtPacketAdvanced extends BtPacket {

    // region Constants

    private static final int MAX_HELLO_DATA_SIZE = 59;
    private static final int MAX_DATA_SIZE = 60;

    // endregion

    // region Variables

    private boolean hello = false;

    // endregion

    // region Constructors

    /**
     * Vytvoří prázdný packet
     */
    public BtPacketAdvanced() {
        super();
    }

    /**
     * Vytvoří prázdný packet
     *
     * @param hello True, pokud se jedná o Hello packet
     *              Rozdíl je v indexu nosných dat
     */
    public BtPacketAdvanced(boolean hello) {
        super();
        setHello(hello);
    }

    /**
     * Vytvoří nový packet z bufferu
     *
     * @param buffer Buffer obsahující surová data packetu
     */
    public BtPacketAdvanced(byte[] buffer) {
        super(buffer);
    }

    /**
     * Vytvoří nový packet z bufferu
     *
     * @param buffer Buffer obsahující surová data packetu
     * @param length Celková délka surových dat.
     *               Délka surových dat != délka obsahových dat.
     */
    public BtPacketAdvanced(byte[] buffer, int length) {
        super(buffer, length);
    }

    // endregion

    // region Private methods

    /**
     * Vrátí index, od kterého se nachází vlastní data
     *
     * @return Index dat
     */
    private int getDataIndex() {
        return hello ? INDEX_HELLO_DATA : INDEX_DATA;
    }
    // endregion

    // region Public methods

    /**
     * Vrátí maximální počet bytu, které můžou obsahovat nosná data
     * Pokud se jedná o hello packet, tak počet odpovídá {@value MAX_HELLO_DATA_SIZE},
     * jinak počet odpovídá {@value MAX_DATA_SIZE}
     *
     * @return Maximální počet bytu, které můžou obsahovat nosná data
     */
    public int getMaxDataSize() {
        return hello ? MAX_HELLO_DATA_SIZE : MAX_DATA_SIZE;
    }

    /**
     * Zjistí, zda-li packet obsahuje testovaný příkaz
     *
     * @param command Příkaz, který se testuje
     * @return True, pokud packet obsahuje hledaný příkaz, jinak false
     */
    public boolean hasCommand(byte command) {
        return ((content[INDEX_COMMAND]) &  command) ==  command;
    }

    // endregion

    // region Getters & Setters

    /**
     * Vrátí byte, ve kterém je uložený příkaz, který se provádí
     *
     * @return Příkaz, který se provádí
     */
    public byte getCommand() {
        return content[INDEX_COMMAND];
    }

    /**
     * Nastaví příkaz, který se bude provádět
     *
     * @param command Příkaz
     * @return {@link BtPacketAdvanced} pro flow interface
     */
    public BtPacketAdvanced setCommand(byte command) {
        content[INDEX_COMMAND] = command;

        return this;
    }

    /**
     * Vrátí byte, který obsahuje iteraci packetu <=> pořadí packetu
     *
     * @return Pořadí packetu
     */
    public byte getIteration() {
        return content[INDEX_ITER];
    }

    /**
     * Nastaví iteraci aktuálního packetu
     *
     * @param iteration Iterace (pořadí) packetu
     * @return {@link BtPacketAdvanced} pro flow interface
     */
    public BtPacketAdvanced setIteration(byte iteration) {
        content[INDEX_ITER] = iteration;

        return this;
    }

    @Override
    public byte[] getData() {
        int index = getDataIndex();

        return Arrays.copyOfRange(content, index, content.length);
    }

    @Override
    public BtPacket setData(byte[] data) {
        int dataLength = data.length;
        int maxLength = getMaxDataSize();
        if (data.length > maxLength) {
            throw new IllegalArgumentException("Velikost dat je příliš velká");
        }

        System.arraycopy(data, 0, content, getDataIndex(), dataLength);
        setDataLength(dataLength);

        return this;
    }

    /**
     * Zjistí, zda-li se jedná o hello packet
     *
     * @return True, pokud se jedná o hello packet, jinak false
     */
    public boolean isHello() {
        return hello;
    }

    /**
     * Nastaví, zda-li se jedná o hello packet
     *
     * @param hello True, pokud se jedná o hello packet, jinak false
     *              Pokud se jedná o Hello packet, nastaví se automaticky verze protokolu
     * @return {@link BtPacketAdvanced} pro flow interface
     */
    public BtPacketAdvanced setHello(boolean hello) {
        this.hello = hello;

        if (hello) {
            content[INDEX_HELLO_VERSION] = RemoteFileServer.PROTOCOL_VERSION;
        }

        return this;
    }

    // endregion


}
