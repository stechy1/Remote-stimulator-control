package cz.zcu.fav.remotestimulatorcontrol.model.bytes;

/**
 * Pomocná knihovní třída obsahující všechny kódové značky, které jsou potřebné pro komunikaci
 * se respbery PI, kvůlí vzdálenému souborovému systému
 */
public final class RemoteFileServer {

    private RemoteFileServer() {
        throw new AssertionError();
    }

    public static class Codes {
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
        public static final byte LS_FLAG_DIRS = (byte) 0x01;
    }
}
