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
    private final ConfigurationREA mConfiguration;
    // endregion

    // region Constructors

    /**
     * Vytvoří nový IO handler se zaměřením na CSV hodnoty
     *
     * @param configuration {@link ConfigurationREA}
     */
    public CSVHandlerREA(ConfigurationREA configuration) {
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
        mConfiguration.setCycleCount(values.getNext());
        mConfiguration.setWaitFixed(values.getNext());
        mConfiguration.setWaitRandom(values.getNext());
        mConfiguration.setMissTime(values.getNext());
        mConfiguration.setBrightness(values.getNext());
        mConfiguration.setOnFail(ConfigurationREA.OnFail.valueOf(Integer.parseInt(values.getNext())));
        mConfiguration.setGender(ConfigurationREA.Gender.valueOf(Integer.parseInt(values.getNext())));
        mConfiguration.setAge(values.getNext());
        mConfiguration.setHeight(values.getNext());
        mConfiguration.setWeight(values.getNext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream);

        StringBuilder builder = new StringBuilder();
        writeSelf(builder);

        writeValue(builder, mConfiguration.getCycleCount());
        writeValue(builder, mConfiguration.getWaitFixed());
        writeValue(builder, mConfiguration.getWaitRandom());
        writeValue(builder, mConfiguration.getMissTime());
        writeValue(builder, mConfiguration.getBrightness());
        writeValue(builder, mConfiguration.getOnFail().ordinal());
        writeValue(builder, mConfiguration.getGender().ordinal());
        writeValue(builder, mConfiguration.getAge());
        writeValue(builder, mConfiguration.getHeight());
        writeValue(builder, mConfiguration.getWeight());

        writer.write(builder.toString());
        writer.close();
    }

    // endregion

}
