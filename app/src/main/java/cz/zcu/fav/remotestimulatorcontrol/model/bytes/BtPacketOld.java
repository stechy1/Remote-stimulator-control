package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

import java.nio.ByteBuffer;

/**
 * Třída reprezentující jeden packet
 */
@Deprecated
public class BtPacketOld {

    /**
     * Pevná velikost packetu
     */
    private static final int PACKET_SIZE = 64;

    private Code code;
    private byte[] value = new byte[PACKET_SIZE];
    private int usedBytes;

    /**
     * Konstruktor pro bezdatové packety
     *
     * @param code typ packetu
     */
    public BtPacketOld(Code code) {
        this.code = code;
        fillPacket(new byte[]{0x00, code.code});
    }

    /**
     * Konstruktor pro packety s daty
     *
     * @param code typ packetu
     * @param data data packetu
     */
    public BtPacketOld(Code code, byte[] data) {
        this.code = code;
        ByteBuffer buffer = ByteBuffer.allocate(2 + data.length)
                .put(ByteBuffer.allocate(2).put(1, code.code)).put((data));

        buffer.put(0, (byte) (buffer.capacity() - 2));
        fillPacket(buffer.array());
    }

    @Override
    public String toString() {
        String text = new String(value, 0, usedBytes);
        return usedBytes + "B | " + code.description + " | " + text;
    }

    /**
     * Naplní packet obsahem a ostatní bajty nastaví na 0x00
     *
     * @param content nenulový obsah packetu
     */
    private void fillPacket(byte[] content) {
        usedBytes = content.length;
        System.arraycopy(content, 0, value, 0, usedBytes);
    }

    /**
     * Vrátí bajtové pole reprezentující packet, toto se používá při BT posílání
     *
     * @return bajtové pole
     */
    public byte[] getValue() {
        return value;
    }

}
