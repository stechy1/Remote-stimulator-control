package cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;

/**
 * Třída představující konfiguraci CVEP
 */
public class ConfigurationCVEP extends AConfiguration {

    // region Constants
    // Minimální délka pulzu
    public static final int MIN_PULS_LENGTH = MIN_MS_TIME;
    // Maximální délka pulzu
    public static final int MAX_PULS_LENGTH = MAX_MS_TIME;
    // Výchozí délka pulzu
    public static final int DEF_PULS_LENGTH = MIN_PULS_LENGTH;
    // Minimální bitový rozdíl jednotlivých patternů
    public static final int MIN_BIT_SHIFT = 1;
    // Výchozí bitový rozdíl jednotlivých patternů
    public static final int DEF_BIT_SHIFT = MIN_BIT_SHIFT;
    // Maximální bitový rozdíl jednotlivých patternů
    public static final int MAX_BIT_SHIFT = 31;
    // Délka patternu
    public static final int PATTERN_LENGTH = 32;
    // Příznak validity parametru puls length
    public static final int FLAG_PULS_LENGTH = 1 << 2;
    // Příznak validity parametru bit shift
    public static final int FLAG_BIT_SHIFT = 1 << 3;
    // Příznak validity parametru brightness
    public static final int FLAG_BRIGHTNESS = 1 << 4;
    // endregion

    // region Variables
    // Kolekce patternů
    public final ObservableArrayList<Pattern> patternList;
    // Hlavní pattern
    @Bindable
    public final Pattern mainPattern = new Pattern(0);
    // Délka pulzu [ms]
    @Bindable
    private String pulsLength;
    // Bitový posun jednotlivých patternu od hlavního [1 - 31]
    @Bindable
    private String bitShift;
    // Jas [%]
    @Bindable
    private String brightness;
    // endregion

    // region Constructors
    /**
     * Konstruktor třídy ConfigurationCVEP
     *
     * @param name Název konfigurace
     */
    public ConfigurationCVEP(String name) {
        this(name, DEF_OUTPUT_COUNT,
                String.valueOf(DEF_PULS_LENGTH),
                String.valueOf(DEF_BIT_SHIFT),
                String.valueOf(DEF_BRIGHTNESS),
                new ObservableArrayList<Pattern>());
    }

    /**
     * Konstruktor třídy ConfigurationCVEP
     * Vytvoří novou konfiguraci s parametry
     *
     * @param name Název konfigurace
     * @param outputCount Počet patternů
     * @param pulsLength Délka pulzu [ms]
     * @param bitShift Bitový rozdíl mezi jednotlivými patterny [1-31]
     * @param brightness Jas [%]
     * @param patternList Kolekce patternů
     */
    public ConfigurationCVEP(String name, int outputCount, String pulsLength, String bitShift, String brightness, ObservableArrayList<Pattern> patternList) {
        super(name, ConfigurationType.CVEP, outputCount);

        setPulsLength(pulsLength);
        setBitShift(bitShift);
        setBrightness(brightness);

        this.patternList = patternList;
        rearangePatterns();
    }

    // endregion

    // region Private methods
    /**
     * Upraví počet patternů
     * Pokud je jich víc, než je požadováno, tak odstraní přebýtečné
     * Pokud je jich méně, tak vytvoří nové
     */
    private void rearangePatterns() {
        if (patternList == null || outputCount == patternList.size()) {
            return;
        }

        int listCount = patternList.size();
        if (outputCount > listCount) {
            for (int i = listCount; i < outputCount; i++) {
                patternList.add(new Pattern(i + 1));
            }
        } else {
            for (int i = --listCount; i >= outputCount; i--) {
                patternList.remove(i);
            }
        }
    }
    // endregion

    // region Public methods
    /**
     * {@inheritDoc}
     */
    @Override
    public IOHandler getHandler() {
        switch (metaData.extensionType) {
            case XML:
                return new XMLHandlerCVEP(this);
            case CSV:
                return new CSVHandlerCVEP(this);
            default:
                return new JSONHandlerCVEP(this);
        }
    }

    @Override
    public List<BtPacket> getPackets() {
        return new ArrayList<>();
    }

    /**
     * Vytvoří a vrátí novou zduplikovanou konfiguraci
     *
     * @param newName Nový název konfigurace
     * @return Novou zduplikovanou konfiguraci
     */
    @Override
    public AConfiguration duplicate(String newName) {
        ObservableArrayList<Pattern> duplicatedPatternList = new ObservableArrayList<>();
        ConfigurationCVEP configuration = new ConfigurationCVEP(newName, outputCount, pulsLength, bitShift, brightness, duplicatedPatternList);
        super.duplicateDefault(configuration);

        configuration.mainPattern.value = mainPattern.value;

        duplicatedPatternList.clear();
        for (Pattern pattern : patternList) {
            duplicatedPatternList.add(pattern.duplicate());
        }

        return configuration;
    }

    @Override
    public void setOutputCount(int outputCount) {
        super.setOutputCount(outputCount);

        rearangePatterns();
    }

    // endregion

    // region Getters & Setters

    /**
     * Vrátí délku pulsu
     *
     * @return Délka pulsu
     */
    public String getPulsLength() {
        return pulsLength;
    }

    /**
     * Nastaví délku pulsu
     *
     * @param value Délka pulsu [ms]
     */
    public void setPulsLength(String value) {
        this.pulsLength = value;
        notifyPropertyChanged(BR.pulsLength);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_PULS_LENGTH, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_PULS_LENGTH || v > MAX_PULS_LENGTH) {
            setValid(false);
            setValidityFlag(FLAG_PULS_LENGTH, true);
        } else {
            setValidityFlag(FLAG_PULS_LENGTH, false);
        }
    }

    /**
     * Vrátí bitový posun, o který liší jednotlivé patterny
     *
     * @return Bitový posun
     */
    public String getBitShift() {
        return bitShift;
    }

    /**
     * Nastaví bitový posun patternu
     *
     * @param value Bitový posun [1-31]
     */
    public void setBitShift(String value) {
        this.bitShift = value;
        notifyPropertyChanged(BR.bitShift);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_BIT_SHIFT, true);
            return;
        }
        rearangePatterns();
        int v = Integer.parseInt(value);

        if (v < MIN_BIT_SHIFT || v > MAX_BIT_SHIFT) {
            setValid(false);
            setValidityFlag(FLAG_BIT_SHIFT, true);
        } else {
            setValidityFlag(FLAG_BIT_SHIFT, false);
        }
    }

    /**
     * Vrátí intenzitu jasu
     *
     * @return Intenzitu jasu výstupu [%]
     */
    public String getBrightness() {
        return brightness;
    }

    /**
     * Nastaví intenzitu jasu
     *
     * @param value Intenzita jasu výstupu [%]
     */
    public void setBrightness(String value) {
        brightness = value;
        notifyPropertyChanged(BR.brightness);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_BRIGHTNESS, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_BRIGHTNESS || v > MAX_BRIGHTNESS) {
            setValid(false);
            setValidityFlag(FLAG_BRIGHTNESS, true);
        } else {
            setValidityFlag(FLAG_BRIGHTNESS, false);
        }
    }

    // endregion

    /**
     * Kontrakt pro duplikovaný pattern
     */
    public interface IDuplicable {
        /**
         * Vytvoří a vrátí nový zduplikovaný pattern
         *
         * @return Nový zduplikovaný pattern
         */
        Pattern duplicate();
    }

    public static final class Pattern extends BaseObservable implements IDuplicable {

        // region Constants
        // Výchozí hodnota patternu
        public static final int DEF_VALUE = 0;
        // endregion

        // region Variables
        // Identifikátor patternu
        @Bindable
        private final int id;
        // Hodnota patternu
        @Bindable
        private int value;
        // endregion

        // region Constructors

        /**
         * Vytvoří nový pattern s výchozí hodnotou
         *
         * @param id Identifikátor patternu
         */
        public Pattern(int id) {
            this(id, DEF_VALUE);
        }

        /**
         * Vytvoří nový pattern
         *
         * @param id    Identifikátor patternu
         * @param value Hodnota patternu
         */
        public Pattern(int id, int value) {
            this.id = id;
            setValue(value);
        }
        // endregion

        // region Public methods
        /**
         * Vytvoří a vrátí nový zduplikovaný pattern
         *
         * @return Nový zduplikovaný pattern
         */
        @Override
        public Pattern duplicate() {
            return new Pattern(id, value);
        }

        /**
         * Invertuje hodnotu patternu
         */
        public void toggleValue() {
            setValue(~value);
        }
        // endregion

        // region Getters & Setters

        /**
         * Vrátí identifikátor patternu
         *
         * @return Identifikátor patternu
         */
        public int getId() {
            return id;
        }

        /**
         * Vrátí aktuální hodnotu patternu
         *
         * @return Hodnota patternu
         */
        public int getValue() {
            return value;
        }

        /**
         * Nastaví novou hodnotu patternu
         *
         * @param value Nová hodnota patternu
         */
        public void setValue(int value) {
            this.value = value;
            notifyPropertyChanged(BR.value);
        }
        // endregion
    }
}
