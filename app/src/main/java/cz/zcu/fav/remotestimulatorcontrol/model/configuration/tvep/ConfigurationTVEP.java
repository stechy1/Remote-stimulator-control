package cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep;

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

public class ConfigurationTVEP extends AConfiguration {

    // region Constants
    // Maximální délka patternu pro TVEP
    public static final int MAX_PATTERN_LENGTH = 16;
    // Výchozí délka patternu pro TVEP
    public static final int DEF_PATTERN_LENGTH = 8;
    // Minimální délka patternu pro TVEP
    public static final int MIN_PATTERN_LENGTH = 1;
    // Mínimální délka pulzu
    public static final int MIN_PULS_LENGTH = MIN_MS_TIME;
    // Maximální délka pulzu
    public static final int MAX_PULS_LENGTH = MAX_MS_TIME;
    // Výchozí délka pulzu
    public static final int DEF_PULS_LENGTH = MIN_PULS_LENGTH;
    // Minimální časová prodleva mezi dvěma pulzy
    public static final int MIN_TIME_BETWEEN = MIN_MS_TIME;
    // Maximální časová prodleva mezi dvěma pulzy
    public static final int MAX_TIME_BETWEEN = MAX_MS_TIME;
    // Výchozí časová prodleva mezi dvěma pulzy
    public static final int DEF_TIME_BETWEEN = MIN_TIME_BETWEEN;
    // Přídnak validity délky patternu
    public static final int FLAG_PATTERN_LENGTH = 1 << 2;
    // Příznak validity délky pulzu
    public static final int FLAG_PULS_LENGTH = 1 << 3;
    // Příznak validity velikosti mezery mezi dvěma pulzy
    public static final int FLAG_TIME_BETWEEN = 1 << 4;
    // Příznak validity jasu
    public static final int FLAG_BRIGHTNESS = 1 << 5;

    // endregion

    // region Variables
    // Kolekce patternů
    public final ObservableArrayList<Pattern> patternList;
    // Velikost patternu [1]
    @Bindable
    private String patternLength;
    // Délka pulzu [ms]
    @Bindable
    private String pulsLength;
    // Velikost mezery mezi dvěma pulzy [ms]
    @Bindable
    private String timeBetween;
    // Jas [%]
    @Bindable
    private String brightness;
    // endregion

    // region Constructors

    /**
     * Konstruktor třídy ConfigurationTVEP
     * Vytvoří novou konfiguraci s výchozími parametry
     *
     * @param name Název konfigurace
     */
    public ConfigurationTVEP(String name) {
        this(name, DEF_OUTPUT_COUNT, new ObservableArrayList<Pattern>(),
                String.valueOf(DEF_PATTERN_LENGTH),
                String.valueOf(DEF_PULS_LENGTH),
                String.valueOf(DEF_TIME_BETWEEN),
                String.valueOf(DEF_BRIGHTNESS));
    }

    /**
     * Konstruktor třídy ConfigurationTVEP
     * Vytvoří novou konfiguraci s parametry
     *
     * @param name          Název konfigurace
     * @param outputCount   Počet výstupů
     * @param patternList   Kolekce výstupů
     * @param patternLength Délka patternu [1-16]
     * @param pulsLength    Délka pulzu [ms]
     * @param timeBetween   Délka mezery mezi dvěma pulzy [ms]
     * @param brightness    Jas [%]
     */
    public ConfigurationTVEP(String name, int outputCount, ObservableArrayList<Pattern> patternList,
                             String patternLength, String pulsLength, String timeBetween,
                             String brightness) {
        super(name, ConfigurationType.TVEP, outputCount);

        this.patternList = patternList;
        rearangePatterns();

        setPatternLength(patternLength);
        setPulsLength(pulsLength);
        setTimeBetween(timeBetween);
        setBrightness(brightness);
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
     * Vrátí handler na základě typu souboru
     *
     * @return IO handler pro prási se souborem
     */
    @Override
    public IOHandler getHandler() {
        switch (metaData.extensionType) {
            case XML:
                return new XMLHandlerTVEP(this);
            case CSV:
                return new CSVHandlerTVEP(this);
            default:
                return new JSONHandlerTVEP(this);
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
        ConfigurationTVEP configuration = new ConfigurationTVEP(newName, outputCount, duplicatedPatternList, patternLength, pulsLength, timeBetween, brightness);
        super.duplicateDefault(configuration);

        duplicatedPatternList.clear();
        for (Pattern pattern : patternList) {
            duplicatedPatternList.add(pattern.duplicate());
        }

        return configuration;
    }

    // endregion

    // region Getters & Setters
    /**
     * Nastaví počet výstupů
     *
     * @param outputCount Počet výstupů
     */
    @Override
    public void setOutputCount(int outputCount) {
        super.setOutputCount(outputCount);

        rearangePatterns();
    }

    /**
     * Vrátí délku Patternu
     *
     * @return Délka patternu
     */
    public String getPatternLength() {
        return patternLength;
    }

    /**
     * Nastaví délku Patternu
     * Pokud se do parametru vloží hodnota, která je stejná jako aktuální, nic se nestane
     *
     * @param value Dálka Patternu [1-16]
     */
    public void setPatternLength(String value) {
        patternLength = value;
        notifyPropertyChanged(BR.patternLength);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_PATTERN_LENGTH, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_PATTERN_LENGTH || v > MAX_PATTERN_LENGTH) {
            setValid(false);
            setValidityFlag(FLAG_PATTERN_LENGTH, true);
        } else {
            setValidityFlag(FLAG_PATTERN_LENGTH, false);
        }
    }

    /**
     * Vrátí délku pulsu
     *
     * @return Délka pulsu [ms]
     */
    public String getPulsLength() {
        return pulsLength;
    }

    /**
     * Nastaví délku pulsu
     *
     * @param value Dálka pulsu [ms]
     */
    public void setPulsLength(String value) {
        pulsLength = value;
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
     * Vrátí délku mezery mezi dvěma pulzy
     *
     * @return Délka mezery mezi dvěma pulzy [ms]
     */
    public String getTimeBetween() {
        return timeBetween;
    }

    /**
     * Nastaví délku mezery mezi dvěma pulzy
     *
     * @param value Délka mezery mezi dvěma pulzy [ms]
     */
    public void setTimeBetween(String value) {
        this.timeBetween = value;
        notifyPropertyChanged(BR.timeBetween);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_TIME_BETWEEN, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_TIME_BETWEEN || v > MAX_TIME_BETWEEN) {
            setValid(false);
            setValidityFlag(FLAG_TIME_BETWEEN, true);
        } else {
            setValidityFlag(FLAG_TIME_BETWEEN, false);
        }
    }

    /**
     * Vrátí intenzitu jasu výstupu [%]
     *
     * @return Intenzitu jasu výstupu [%]
     */
    public String getBrightness() {
        return brightness;
    }

    /**
     * Nastaví intenzitu jasu výstupu [%]
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
         * @param id Identifikátor patternu
         * @param value Hodnota patternu
         */
        public Pattern(int id, int value) {
            this.id = id;
            setValue(value);
        }

        // endregion

        // region Private methods

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
