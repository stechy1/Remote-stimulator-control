package cz.zcu.fav.remotestimulatorcontrol.io;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

/**
 * Abstraktní třída pro uložení základních dat konfigurací
 */
public abstract class CSVHandler implements IOHandler {

    // region Variables
    // Výchozí oddělovací znak hodnot
    private static String _separator = ",";
    private final AConfiguration mConfiguration;
    protected String mSeparator;
    // endregion

    // region Public static methods
    /**
     * Vrátí výchozí znak oddělovače
     *
     * @return Výchozí znak oddělovače
     */
    public static String getDefaultSeparator() {
        return _separator;
    }

    /**
     * Nastaví výchozí znak oddělovače
     *
     * @param defSeparator Výchozí znak oddělovače
     */
    public static void setDefaultSeparator(String defSeparator) {
        _separator = defSeparator;
    }
    // endregion

    // region Constructors
    /**
     * Konstruktor třídy {@link CSVHandler}
     *
     * @param configuration {@link AConfiguration}
     */
    public CSVHandler(AConfiguration configuration) {
        this(configuration, _separator);
    }

    /**
     * Konstruktor třídy {@link CSVHandler}
     *
     * @param configuration {@link AConfiguration}
     * @param separator Oddělovací znak hodnot
     */
    public CSVHandler(AConfiguration configuration, String separator) {
        mConfiguration = configuration;
        mSeparator = separator;
    }
    // endregion

    // region Public methods
    /**
     * Zapíše hodnotu s oddělovačem
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     * @param value Hodnota, která se má zapsat
     */
    protected void writeValue(StringBuilder builder, double value) {
        builder.append(value)
                .append(mSeparator);
    }
    /**
     * Zapíše hodnotu s oddělovačem
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     * @param value Hodnota, která se má zapsat
     */
    protected void writeValue(StringBuilder builder, int value) {
        builder.append(value)
                .append(mSeparator);
    }

    /**
     * Zapíše hodnotu s oddělovačem
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     * @param value Hodnota, která se má zapsat
     */
    protected void writeValue(StringBuilder builder, String value) {
        builder.append(value)
                .append(mSeparator);
    }

    /**
     * Zapíše základní parametry konfigurace
     *
     * @param builder {@link StringBuilder} StringBuilder ve kterém se sestavuje výsledný řetězec
     */
    protected void writeSelf(StringBuilder builder) {
        builder.append(mConfiguration.getOutputCount())
                .append(mSeparator);
    }

    /**
     * Načte základní parametry konfigurace
     *
     * @param values {@link IndexedValues} Pole hodnot
     */
    protected void readSelf(IndexedValues values) {
        mConfiguration.setOutputCount(Integer.parseInt(values.getNext()));
    }
    // endregion

    /**
     * Třída pro zjednodušení čtení hodnot z pole
     */
    protected class IndexedValues {
        // Index, který ukazuje na aktuální hodnotu v poli hodnot
        private int mmIndex = 0;
        // Pole čtených hodnot
        private final String[] mmValues;

        /**
         * Vytvoří nový zásobník pro čtení hodnot z pole
         *
         * @param values Pole hodnot, které se bude číst
         */
        public IndexedValues(String[] values) {
            this.mmValues = values;
        }

        /**
         * Vrátí další hodnotu z pole
         *
         * @return Další hodnotu z pole
         * @throws IndexOutOfBoundsException Pokud je index mimo hranice pole
         */
        public String getNext() throws IndexOutOfBoundsException {
            return mmValues[mmIndex++];
        }

        /**
         * Zjistí, jestli lze vybrat další prvek
         *
         * @return True, pokud lze vybrat další prvek, jinak false
         */
        public boolean hasNext() {
            return mmIndex == mmValues.length;
        }
    }
}
