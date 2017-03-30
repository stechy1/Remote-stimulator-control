package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

import android.os.Build;

import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_HELLO_VERSION;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.LS_FLAG_NO_DIRS;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.OP_BYE;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.OP_HELLO;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.OP_LS;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.OP_PUT;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.PART_CONTINUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.PART_LAST;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.TYPE_REQUEST;

/**
 * Pomocná knihovní třída obsahující všechny kódové značky, které jsou potřebné pro komunikaci
 * se respbery PI, kvůlí vzdálenému souborovému systému
 */
public final class RemoteFileServer {

    // Verze přenosového protokolu pouze pro souborov systém
    public static final byte PROTOCOL_VERSION = (byte) 0x00;

    // Počet bytů, které zabírá hash
    public static final int HASH_SIZE = 16;

    private RemoteFileServer() {
        throw new AssertionError();
    }

    /**
     * Vrátí packet, s parametry, aby prošel před stimulátor rovnou do vzdáleného serveru
     *
     * @return {@link BtPacketAdvanced} Packet v základní konfiguraci pro vzdálený server
     */
    public static BtPacketAdvanced getServerPacket() {
        return (BtPacketAdvanced) new BtPacketAdvanced().setHeader(Codes.FULL_LENGTH_MESSAGE).setMessageType(Codes.COMMUNICATION_OP_CODE);
    }

    /**
     * Vytvoří hello packet, kterým se aplikace představí vzdálenému serveru
     *
     * @return {@link BtPacketAdvanced} Pcket obsahující hello zprávu
     */
    public static BtPacketAdvanced getHelloPacket() {
        BtPacketAdvanced packet = getServerPacket()
                .setHello(true)
                .setCommand((byte) (TYPE_REQUEST + PART_LAST + OP_HELLO))
                .setIteration((byte) 0);

        packet.content[INDEX_HELLO_VERSION] = PROTOCOL_VERSION;
        String name = Build.MANUFACTURER + Build.MODEL;
        packet.insertData(name.getBytes());

        return packet;
    }

    /**
     * Vrátí packet, který obsahuje zprávu na rozloučení se se serverem
     *
     * @return {@link BtPacketAdvanced} Packet obsahující rozlučkovou zprávu
     */
    public static BtPacketAdvanced getByePacket() {
        return getServerPacket()
                .setCommand((byte) (TYPE_REQUEST + OP_BYE + PART_CONTINUE))
                .setIteration((byte) 0);
    }

    /**
     * Vrátí packet, který obsahuje informaci, že se bude získávat obsah vzdáleného adresáře
     *
     * @return {@link BtPacketAdvanced} Packet s příkazem LS
     */
    public static BtPacketAdvanced getLsPacket() {
        BtPacketAdvanced packet = getServerPacket()
                .setCommand((byte) (TYPE_REQUEST + OP_LS + PART_LAST));

        // Nastavení příznaku, že nebudu brát v potaz složky
        //packet.content[INDEX_DATA] = LS_FLAG_NO_DIRS;
        packet.insertData(new byte[] {LS_FLAG_NO_DIRS});

        return packet;
    }

    /**
     * Vrátí packet, který obsahuje informaci, že se bude nahrávat soubor na server
     *
     * @return {@link BtPacketAdvanced} Packet s příkazem PUT
     */
    public static BtPacketAdvanced getPutPacket() {
        return getServerPacket().setCommand((byte) (OP_PUT + TYPE_REQUEST + PART_LAST));
    }

    public static class Codes {

        // v protokolu znamena 62 znaku dlouhou zpravu
        public static final byte FULL_LENGTH_MESSAGE = (byte) 0x3E;
        // v protokolu definovan jako posledni z bloku reserved
        public static final byte COMMUNICATION_OP_CODE = (byte) 0xBF; // TODO v budoucnu zmenit na jinou

        public static final int TRASFER_DATA_SIZE = 60;
        // pri prijeti nevidim prvni dva byte musim je tedy odecist
        public static final int PREFIX = 2;
        // indexy bytu
        public static final int INDEX_COMMAND = 2;
        public static final int INDEX_ITER = 3;
        public static final int INDEX_HELLO_VERSION = 4;
        public static final int INDEX_RESPONSE = 4;
        public static final int INDEX_HELLO_DATA = 5; // Pouze pro hello command
        public static final int INDEX_DATA = 4; // Pro všechny ostatní data

        public static final byte SECTION_TYPE = (byte) 0xC0;
        // to co posila klient serveru
        public static final byte TYPE_REQUEST = (byte) 0x00;
        // to co posila server klientu
        public static final byte TYPE_RESPONSE = (byte) 0x40;
        // smerem do klieta
        public static final byte TYPE_DOWNLOAD = (byte) 0x80;
        //smerem od klienta
        public static final byte TYPE_UPLOAD = (byte) 0xC0;
        // prenosy jsou vzdy uvozeny prikazy GET PUT LS
        // v pripade GET a PUT jeste i responsem s delkou a hashem

        public static final byte SECTION_PART = (byte) 0x20;
        public static final byte PART_CONTINUE = (byte) 0x00;
        public static final byte PART_LAST = (byte) 0x20;

        public static final byte SECTION_OP = (byte) 0x1F;
        public static final byte OP_ZERO = (byte) 0x00; // V pripade transferu
        public static final byte OP_HELLO = (byte) 0x01;

        // zadny parametr
        public static final byte OP_BYE = (byte) 0x02;
        // vytvoreni adresare, parametr cela vzdalena cesta
        public static final byte OP_MD = (byte) 0x03;
        // listuje adresar jako parametr ma cestu k adresari ~ je domovsky adresar pro soubory
        public static final byte OP_LS = (byte) 0x04;
        // v odpovedi posilam delku souboru a sha-1 hash, pak zahajim transfer
        // stahne soubor parametr je nazev souboru na serveru vcetne cesty
        public static final byte OP_GET = (byte) 0x05;
        // v odpovedi posilam delku souboru a sha-1 hash, pak zahajim transfer
        // nahrava soubor, parametry: delka souboru(4 byte),sha-1(20 byte),nazev souboru na serveru
        public static final byte OP_PUT = (byte) 0x06;
        // smaze polozku - prazdny adresar nebo soubor
        public static final byte OP_DEL = (byte) 0x07;
        // startne proces s obrazem a zvuky na serveru, jako parametr je cesta ke config.xml
        public static final byte OP_START = (byte) 0x08;
        // stopne proces
        public static final byte OP_STOP = (byte) 0x09;
        // vraci soubor s nahledem obrazku, je ve formatu jpg s nizkym rozlisenim , prenos probiha stejne jako GET
        public static final byte OP_GET_PREVIEW = (byte) 0x10;

        // operace probehla v poradku
        // nenulova znamena ze se to nejak nepovedlo, soubor se nepodarilo ulozit, mas jinou verzi protokolu, hash nesedi
        // response kody budu postupne doplnovat, tobe zatim staci !=0 je chyba
        public static final byte RESPONSE_OK = (byte) 0x00;

        //dalsi response kody
        public static final byte RESPONSE_MD_DIR_EXIST = (byte) 0x01;
        public static final byte RESPONSE_MD_FAIL = (byte) 0x02;
        public static final byte RESPONSE_LS_DIR_NOT_FOUND = (byte) 0x03;
        public static final byte RESPONSE_DEL_FAIL = (byte) 0x04;
        public static final byte RESPONSE_PUT_SHA1_FAIL = (byte) 0x05;
        public static final byte RESPONSE_GET_FILE_NOT_FOUND = (byte) 0x06;

        // LS flagy
        public static final byte LS_FLAG_NO_DIRS = (byte) 0x00;
        public static final byte LS_FLAG_DIRS = (byte) 0x01;
    }
}
