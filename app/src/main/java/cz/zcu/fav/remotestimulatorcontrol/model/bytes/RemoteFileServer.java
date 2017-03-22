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
        public static final Code SECTION_TYPE = new Code((byte) 0xC0, "SECTION_TYPE");
        // to co posila klient serveru
        public static final Code TYPE_REQUEST = new Code((byte) 0x00, "TYPE_REQUEST");
        // to co posila server klientu
        public static final Code TYPE_RESPONSE = new Code((byte) 0x40, "TYPE_REQUEST");
        // smerem do klieta
        public static final Code TYPE_DOWNLOAD = new Code((byte) 0x80, "TYPE_REQUEST");
        //smerem od klienta
        public static final Code TYPE_UPLOAD = new Code((byte) 0xC0, "TYPE_REQUEST");
        // prenosy jsou vzdy uvozeny prikazy GET PUT LS
        // v pripade GET a PUT jeste i responsem s delkou a hashem


        public static final Code SECTION_PART = new Code((byte) 0x20, "SECTION_PART");
        public static final Code PART_CONTINUE = new Code((byte) 0x00, "PART_CONTINUE");
        public static final Code PART_LAST = new Code((byte) 0x20, "PART_LAST");

        public static final Code SECTION_OP = new Code((byte)0x1F, "SECTION_OP");
        public static final Code OP_ZERO = new Code((byte)0x00, "OP_ZERO"); // v pripade transferu
        public static final Code OP_HELLO = new Code((byte)0x01, "OP_HELLO");

        // zadny parametr
        public static final Code OP_BYE = new Code((byte) 0x02, "OP_BYE");
        // vytvoreni adresare, parametr cela vzdalena cesta
        public static final Code OP_MD = new Code((byte) 0x03, "OP_MD");
        // listuje adresar jako parametr ma cestu k adresari ~ je domovsky adresar pro soubory
        public static final Code OP_LS = new Code((byte) 0x04, "OP_LS");
        // v odpovedi posilam delku souboru a sha-1 hash, pak zahajim transfer
        // stahne soubor parametr je nazev souboru na serveru vcetne cesty
        public static final Code OP_GET = new Code((byte) 0x05, "OP_GET");
        // v odpovedi posilam delku souboru a sha-1 hash, pak zahajim transfer
        // nahrava soubor, parametry: delka souboru(4 byte),sha-1(20 byte),nazev souboru na serveru
        public static final Code OP_PUT = new Code((byte) 0x06, "OP_PUT");
        // smaze polozku - prazdny adresar nebo soubor
        public static final Code OP_DEL = new Code((byte) 0x07, "OP_DEL");
        // startne proces s obrazem a zvuky na serveru, jako parametr je cesta ke config.xml
        public static final Code OP_START = new Code((byte) 0x08, "OP_START");
        // stopne proces
        public static final Code OP_STOP = new Code((byte) 0x09, "OP_STOP");
        // vraci soubor s nahledem obrazku, je ve formatu jpg s nizkym rozlisenim , prenos probiha stejne jako GET
        public static final Code OP_GET_PREVIEW = new Code((byte) 0x10, "OP_GET_PREVIEW");

        // operace probehla v poradku
        // nenulova znamena ze se to nejak nepovedlo, soubor se nepodarilo ulozit, mas jinou verzi protokolu, hash nesedi
        // response kody budu postupne doplnovat, tobe zatim staci !=0 je chyba
        public static final Code RESPONSE_OK = new Code((byte) 0x00, "RESPONSE_OK");

        //dalsi response kody
        public static final Code RESPONSE_MD_DIR_EXIST = new Code((byte) 0x01, "RESPONSE_MD_DIR_EXIST");
        public static final Code RESPONSE_MD_FAIL = new Code((byte) 0x02, "RESPONSE_MD_FAIL");
        public static final Code RESPONSE_LS_DIR_NOT_FOUND = new Code((byte) 0x03, "RESPONSE_LS_DIR_NOT_FOUND");
        public static final Code RESPONSE_DEL_FAIL = new Code((byte) 0x04, "RESPONSE_DEL_FAIL");
        public static final Code RESPONSE_PUT_SHA1_FAIL = new Code((byte) 0x05, "RESPONSE_PUT_SHA1_FAIL");
        public static final Code RESPONSE_GET_FILE_NOT_FOUND = new Code((byte) 0x06, "RESPONSE_GET_FILE_NOT_FOUND");

        // LS flagy
        public static final Code LS_FLAG_DIRS = new Code((byte) 0x01, "LS_FLAG_DIRS");
    }
}
