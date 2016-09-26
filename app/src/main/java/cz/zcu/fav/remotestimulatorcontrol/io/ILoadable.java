package cz.zcu.fav.remotestimulatorcontrol.io;

import java.io.IOException;
import java.io.InputStream;

public interface ILoadable {

    /**
     * Načte soubor
     *
     * @param inputStream Input stream
     */
    void read(InputStream inputStream) throws IOException;

}
