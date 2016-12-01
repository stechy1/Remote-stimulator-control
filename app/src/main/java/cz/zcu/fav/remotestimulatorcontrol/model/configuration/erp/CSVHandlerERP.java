package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

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
public class CSVHandlerERP extends CSVHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationERP configuration;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationERP}
     */
    public CSVHandlerERP(ConfigurationERP configuration) {
        super(configuration);

        this.configuration = configuration;
    }
    // endregion

    // region Private methods
    /**
     * Načte všechny výstupy
     *
     * @param values {@link IndexedValues} Pole hodnot
     */
    private void readOutputs(IndexedValues values) {
        List<ConfigurationERP.Output> outputList = configuration.outputList;
        outputList.clear();
        int count = configuration.getOutputCount();

        for (int i = 0; i < count; i++) {
            outputList.add(readOutput(values, i));
        }
    }

    /**
     * Načte jeden výstup
     *
     * @param values {@link IndexedValues} Pole hodnot
     * @param id Jednoznačný identifikátor výstupu
     * @return {@link ConfigurationERP.Output}
     */
    private ConfigurationERP.Output readOutput(IndexedValues values, int id) {
        int pulsUp = Integer.parseInt(values.getNext());
        int pulsDown = Integer.parseInt(values.getNext());
        int distValue = Integer.parseInt(values.getNext());
        int distDelay = Integer.parseInt(values.getNext());
        int brightness = Integer.parseInt(values.getNext());

        return new ConfigurationERP.Output(configuration, id, pulsUp, pulsDown, distValue, distDelay, brightness);
    }

    /**
     * Zapíše hodnoty všech výstupů
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     */
    private void writeOutputs(StringBuilder builder) {
        for (ConfigurationERP.Output output : configuration.outputList) {
            writeOutput(builder, output);
        }
    }

    /**
     * Zapíše hodnoty jednoho výstupu
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     * @param output Výstup, který se má zapsat
     */
    private void writeOutput(StringBuilder builder, ConfigurationERP.Output output) {
        writeValue(builder, output.getPulsUp());
        writeValue(builder, output.getPulsDown());
        writeValue(builder, output.getDistributionValue());
        writeValue(builder, output.getDistributionDelay());
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
        configuration.setOut(Integer.parseInt(values.getNext()));
        configuration.setWait(Integer.parseInt(values.getNext()));
        configuration.setEdge(ConfigurationERP.Edge.valueOf(Integer.parseInt(values.getNext())));
        configuration.setRandom(ConfigurationERP.Random.valueOf(Integer.parseInt(values.getNext())));

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

        writeValue(builder, configuration.getOut());
        writeValue(builder, configuration.getWait());
        writeValue(builder, configuration.getEdge().ordinal());
        writeValue(builder, configuration.getRandom().ordinal());

        writeOutputs(builder);

        writer.write(builder.toString());
        writer.close();
    }
    // endregion
}
