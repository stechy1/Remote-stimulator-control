package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import cz.zcu.fav.remotestimulatorcontrol.io.CSVHandler;

/**
 * Třída představující IO handler pro čtení a zápis CSV dat
 */
public class CSVHandlerCVEP extends CSVHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationCVEP configuration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationCVEP}
     */
    public CSVHandlerCVEP(ConfigurationCVEP configuration) {
        super(configuration);

        this.configuration = configuration;

    }
    // endregion

    // region Public methods
    /**
     * {@inheritDoc}
     */
    @Override
    public void read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String text = reader.readLine();
        String[] stringValues = text.split(separator);
        IndexedValues values = new IndexedValues(stringValues);

        reader.close();

        readSelf(values);
        configuration.setPulsLength(Integer.parseInt(values.getNext()));
        configuration.setBitShift(Integer.parseInt(values.getNext()));
        configuration.setBrightness(Integer.parseInt(values.getNext()));
        configuration.mainPattern.setValue(Integer.parseInt(values.getNext()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream);

        StringBuilder builder = new StringBuilder();
        writeSelf(builder);

        writeValue(builder, configuration.getPulsLength());
        writeValue(builder, configuration.getBitShift());
        writeValue(builder, configuration.getBrightness());
        writeValue(builder, configuration.mainPattern.getValue());

        writer.write(builder.toString());
        writer.close();
    }
    // endregion
}
