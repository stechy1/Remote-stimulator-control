package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

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
public class CSVHandlerFVEP extends CSVHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationFVEP mConfiguration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationFVEP}
     */
    public CSVHandlerFVEP(ConfigurationFVEP configuration) {
        super(configuration);

        this.mConfiguration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Načte všechny výstupy
     *
     * @param values {@link IndexedValues} Pole hodnot
     */
    private void readOutputs(IndexedValues values) {
        List<ConfigurationFVEP.Output> outputList = mConfiguration.outputList;
        outputList.clear();
        int count = mConfiguration.getOutputCount();

        for (int i = 0; i < count; i++) {
            outputList.add(readOutput(values, i));
        }
    }

    /**
     * Načte jeden výstup
     *
     * @param values {@link IndexedValues} Pole hodnot
     * @param id Jednoznačný identifikátor výstupu
     * @return {@link ConfigurationFVEP.Output}
     */
    private ConfigurationFVEP.Output readOutput(IndexedValues values, int id) {
        String pulsUp = values.getNext();
        String pulsDown = values.getNext();
        String frequency = values.getNext();
        String dutyCycle = values.getNext();
        String brightness = values.getNext();

        return new ConfigurationFVEP.Output(mConfiguration, id, pulsUp, pulsDown, frequency, dutyCycle, brightness);
    }

    /**
     * Zapíše hodnoty všech výstupů
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     */
    private void writeOutputs(StringBuilder builder) {
        for (ConfigurationFVEP.Output output : mConfiguration.outputList) {
            writeOutput(builder, output);
        }
    }

    /**
     * Zapíše hodnoty jednoho výstupu
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     * @param output Výstup, který se má zapsat
     */
    private void writeOutput(StringBuilder builder, ConfigurationFVEP.Output output) {
        writeValue(builder, output.getPulsUp());
        writeValue(builder, output.getPulsDown());
        writeValue(builder, output.getFrequency());
        writeValue(builder, output.getDutyCycle());
        writeValue(builder, output.getBrightness());
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
        readOutputs(values);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream);

        StringBuilder builder = new StringBuilder();
        writeSelf(builder);

        writeOutputs(builder);

        writer.write(builder.toString());
        writer.close();
    }
    // endregion


}
