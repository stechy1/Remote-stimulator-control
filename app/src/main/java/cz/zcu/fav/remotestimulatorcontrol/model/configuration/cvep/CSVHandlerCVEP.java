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
    private final ConfigurationCVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationCVEP}
     */
    public CSVHandlerCVEP(ConfigurationCVEP configuration) {
        super(configuration);

        this.mConfiguration = configuration;

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
        String[] stringValues = text.split(mSeparator);
        IndexedValues values = new IndexedValues(stringValues);

        reader.close();

        readSelf(values);
        mConfiguration.setPulsLength(values.getNext());
        mConfiguration.setBitShift(values.getNext());
        mConfiguration.setBrightness(values.getNext());
        mConfiguration.mainPattern.setValue(Integer.parseInt(values.getNext()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream);

        StringBuilder builder = new StringBuilder();
        writeSelf(builder);

        writeValue(builder, mConfiguration.getPulsLength());
        writeValue(builder, mConfiguration.getBitShift());
        writeValue(builder, mConfiguration.getBrightness());
        writeValue(builder, mConfiguration.mainPattern.getValue());

        writer.write(builder.toString());
        writer.close();
    }
    // endregion
}
