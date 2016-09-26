package cz.zcu.fav.remotestimulatorcontrol.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Rozhraní definující metodu pro uložení hodnot do souboru
 */
public interface IStoragable {

    /**
     * Zapíše data z konfigurace
     *
     * @param outputStream Output stream
     */
    void write(OutputStream outputStream) throws IOException;

}
