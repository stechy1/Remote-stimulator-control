package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

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
public class CSVHandlerREA extends CSVHandler {

    // region Variables
    // Pracovní konfigurace
    private final ConfigurationREA configuration;
    // endregion

    // region Constructors

    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationREA}
     */
    public CSVHandlerREA(ConfigurationREA configuration) {
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
        configuration.setCycleCount(Integer.parseInt(values.getNext()));
        configuration.setWaitFixed(Integer.parseInt(values.getNext()));
        configuration.setWaitRandom(Integer.parseInt(values.getNext()));
        configuration.setMissTime(Integer.parseInt(values.getNext()));
        configuration.setBrightness(Integer.parseInt(values.getNext()));
        configuration.setOnFail(ConfigurationREA.OnFail.valueOf(Integer.parseInt(values.getNext())));
        configuration.setGender(ConfigurationREA.Gender.valueOf(Integer.parseInt(values.getNext())));
        configuration.setAge(Integer.parseInt(values.getNext()));
        configuration.setHeight(Integer.parseInt(values.getNext()));
        configuration.setWeight(Integer.parseInt(values.getNext()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream);

        StringBuilder builder = new StringBuilder();
        writeSelf(builder);

        writeValue(builder, configuration.getCycleCount());
        writeValue(builder, configuration.getWaitFixed());
        writeValue(builder, configuration.getWaitRandom());
        writeValue(builder, configuration.getMissTime());
        writeValue(builder, configuration.getBrightness());
        writeValue(builder, configuration.getOnFail().ordinal());
        writeValue(builder, configuration.getGender().ordinal());
        writeValue(builder, configuration.getAge());
        writeValue(builder, configuration.getHeight());
        writeValue(builder, configuration.getWeight());

        writer.write(builder.toString());
        writer.close();
    }

    // endregion

}
