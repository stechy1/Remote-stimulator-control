package cz.zcu.fav.remotestimulatorcontrol.model.stimulator;

import cz.zcu.fav.remotestimulatorcontrol.util.BitUtils;

/**
 * Pomocní knihovní třída obsahující všechny kódové značky, které jsou v přenosovém protokolu
 */
public class Stimulator {

    private Stimulator() {
        throw new AssertionError();
    }

    /**
     * Metoda pro převod [0.1 ms] na 2 bajty
     *
     * @param number číslo reprezentující počet 0.1 ms např 30.6 ms => 306*0.1 ms
     * @return pole od dvou bajtech
     */
    public static byte[] milisecondsTo2B(double number){
        return BitUtils.intTo2Bytes((int)(10 * number));
    }

    /**
     * Vytvoří nový packet s požadavkem pro spuštění stimulace
     *
     * @return {@link BtPacket}
     */
    public static BtPacket getStartPacket() {
        return new BtPacket().setMessageType(Codes.START_STIMULATION);
    }

    /**
     * Vytvoří nový packet s požadavkem pro zastavení stimulace
     *
     * @return
     */
    public static BtPacket getStopPacket() {
        return new BtPacket().setMessageType(Codes.STOP_STIMULATION);
    }

    public static class Codes {

        public static final byte REFRESH = (byte) 0x00;
        public static final byte START_STIMULATION = (byte) 0x01;
        public static final byte STOP_STIMULATION = (byte) 0x02;
        public static final byte GET_OUTPUT_STATUS = (byte) 0x03;
        public static final byte RANDOMNESS_ON = (byte) 0x04;
        public static final byte RANDOMNESS_OFF = (byte) 0x05;
        public static final byte GET_RANDOMNESS_STATUS = (byte) 0x06;
        public static final byte EDGE = (byte) 0x21;
        public static final byte PULSE_EDGE_UP = (byte) 0x00;
        public static final byte PULSE_EDGE_DOWN = (byte) 0x01;
        public static final byte SYNC_PULSE_INTERVAL = (byte) 0x20;

        public static final byte OUTPUT7_DURATION = (byte) 0x32;
        public static final byte OUTPUT7_PAUSE = (byte) 0x33;
        public static final byte OUTPUT7_DISTRIBUTION = (byte) 0x37;
        //nemá brightness - sdružené s OUTPUT6
        public static final byte OUTPUT7_FREQ = (byte) 0x40;
        public static final byte OUTPUT7_MIDDLE_PERIOD = (byte) 0x41;

        public static final byte OUTPUT6_DURATION = (byte) 0x30;
        public static final byte OUTPUT6_PAUSE = (byte) 0x31;
        public static final byte OUTPUT6_DISTRIBUTION = (byte) 0x36;
        public static final byte OUTPUT67_BRIGHTNESS = (byte) 0x39;
        public static final byte OUTPUT6_FREQ = (byte) 0x3E;
        public static final byte OUTPUT6_MIDDLE_PERIOD = (byte) 0x3F;

        public static final byte OUTPUT5_DURATION = (byte) 0x2E;
        public static final byte OUTPUT5_PAUSE = (byte) 0x2F;
        public static final byte OUTPUT5_DISTRIBUTION = (byte) 0x35;
        //nemá brightness - sdružené s OUTPUT4
        public static final byte OUTPUT5_FREQ = (byte) 0x3C;
        public static final byte OUTPUT5_MIDDLE_PERIOD = (byte) 0x3D;

        public static final byte OUTPUT4_DURATION = (byte) 0x2C;
        public static final byte OUTPUT4_PAUSE = (byte) 0x2D;
        public static final byte OUTPUT4_DISTRIBUTION = (byte) 0x34;
        public static final byte OUTPUT45_BRIGHTNESS = (byte) 0x38;
        public static final byte OUTPUT4_FREQ = (byte) 0x3A;
        public static final byte OUTPUT4_MIDDLE_PERIOD = (byte) 0x3B;

        public static final byte OUTPUT3_DURATION = (byte) 0x16;
        public static final byte OUTPUT3_PAUSE = (byte) 0x17;
        public static final byte OUTPUT3_DISTRIBUTION = (byte) 0x1B;
        public static final byte OUTPUT3_BRIGHTNESS = (byte) 0x1F;
        public static final byte OUTPUT3_FREQ = (byte) 0x28;
        public static final byte OUTPUT3_MIDDLE_PERIOD = (byte) 0x29;

        public static final byte OUTPUT2_DURATION = (byte) 0x14;
        public static final byte OUTPUT2_PAUSE = (byte) 0x15;
        public static final byte OUTPUT2_DISTRIBUTION = (byte) 0x1A;
        public static final byte OUTPUT2_BRIGHTNESS = (byte) 0x1E;
        public static final byte OUTPUT2_FREQ = (byte) 0x26;
        public static final byte OUTPUT2_MIDDLE_PERIOD = (byte) 0x27;

        public static final byte OUTPUT1_DURATION = (byte) 0x12;
        public static final byte OUTPUT1_PAUSE = (byte) 0x13;
        public static final byte OUTPUT1_DISTRIBUTION = (byte) 0x19;
        public static final byte OUTPUT1_BRIGHTNESS = (byte) 0x1D;
        public static final byte OUTPUT1_FREQ = (byte) 0x24;
        public static final byte OUTPUT1_MIDDLE_PERIOD = (byte) 0x25;

        public static final byte OUTPUT0_DURATION = (byte) 0x10;
        public static final byte OUTPUT0_PAUSE = (byte) 0x11;
        public static final byte OUTPUT0_DISTRIBUTION = (byte) 0x18;
        public static final byte OUTPUT0_BRIGHTNESS = (byte) 0x1C;
        public static final byte OUTPUT0_FREQ = (byte) 0x22;
        public static final byte OUTPUT0_MIDDLE_PERIOD = (byte) 0x23;

        public static byte[] DURATION = { OUTPUT0_DURATION, OUTPUT1_DURATION, OUTPUT2_DURATION,
                OUTPUT3_DURATION, OUTPUT4_DURATION, OUTPUT5_DURATION, OUTPUT6_DURATION,
                OUTPUT7_DURATION };

        public static byte[] PAUSE = { OUTPUT0_PAUSE, OUTPUT1_PAUSE, OUTPUT2_PAUSE, OUTPUT3_PAUSE,
                OUTPUT4_PAUSE, OUTPUT5_PAUSE, OUTPUT6_PAUSE, OUTPUT7_PAUSE };

        public static byte[] DISTRIBUTION = { OUTPUT0_DISTRIBUTION, OUTPUT1_DISTRIBUTION,
                OUTPUT2_DISTRIBUTION, OUTPUT3_DISTRIBUTION, OUTPUT4_DISTRIBUTION,
                OUTPUT5_DISTRIBUTION, OUTPUT6_DISTRIBUTION, OUTPUT7_DISTRIBUTION };

        public static byte[] BRIGHTNESS = { OUTPUT0_BRIGHTNESS, OUTPUT1_BRIGHTNESS,
                OUTPUT2_BRIGHTNESS, OUTPUT3_BRIGHTNESS, OUTPUT45_BRIGHTNESS, OUTPUT45_BRIGHTNESS,
                OUTPUT67_BRIGHTNESS, OUTPUT67_BRIGHTNESS };

        public static byte[] FREQ = { OUTPUT0_FREQ, OUTPUT1_FREQ, OUTPUT2_FREQ, OUTPUT3_FREQ,
                OUTPUT4_FREQ, OUTPUT5_FREQ, OUTPUT6_FREQ, OUTPUT7_FREQ };

        public static byte[] MIDDLE_PERIOD = { OUTPUT0_MIDDLE_PERIOD, OUTPUT1_MIDDLE_PERIOD,
                OUTPUT2_MIDDLE_PERIOD, OUTPUT3_MIDDLE_PERIOD, OUTPUT4_MIDDLE_PERIOD,
                OUTPUT5_MIDDLE_PERIOD, OUTPUT6_MIDDLE_PERIOD, OUTPUT7_MIDDLE_PERIOD };
    }
}
