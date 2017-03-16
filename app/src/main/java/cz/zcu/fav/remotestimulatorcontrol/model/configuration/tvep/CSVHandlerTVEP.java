package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.io.CSVHandler;

/**
 * Třída představující IO handler pro čtení a zápis CSV dat
 */
public class CSVHandlerTVEP extends CSVHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationTVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationTVEP}
     */
    public CSVHandlerTVEP(ConfigurationTVEP configuration) {
        super(configuration);

        this.mConfiguration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Načte všechny patterny
     *
     * @param values {@link IndexedValues} Pole hodnot
     */
    private void readPatterns(IndexedValues values) {
        List<ConfigurationTVEP.Pattern> patternList = mConfiguration.patternList;
        patternList.clear();
        int count = mConfiguration.getOutputCount();

        for (int i = 0; i < count; i++) {
            patternList.add(new ConfigurationTVEP.Pattern(i, Integer.parseInt(values.getNext())));
        }
    }
    /**
     * Zapíše hodnoty všech patternů
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     */
    private void writePatterns(StringBuilder builder) {
        for (ConfigurationTVEP.Pattern pattern : mConfiguration.patternList) {
            writeValue(builder, pattern.getValue());
        }
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
        mConfiguration.setPatternLength(values.getNext());
        mConfiguration.setTimeBetween(values.getNext());
        mConfiguration.setPulsLength(values.getNext());
        mConfiguration.setBrightness(values.getNext());

        readPatterns(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream);

        StringBuilder builder = new StringBuilder();
        writeSelf(builder);

        writeValue(builder, mConfiguration.getPatternLength());
        writeValue(builder, mConfiguration.getTimeBetween());
        writeValue(builder, mConfiguration.getPulsLength());
        writeValue(builder, mConfiguration.getBrightness());

        writePatterns(builder);

        writer.write(builder.toString());
        writer.close();
    }

    // endregion

}
