package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.stimulator.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;

public class ConfigurationFVEP extends AConfiguration {

    // region Constants
    @SuppressWarnings("unused")
    private static final String TAG = "ConfiurationFVEP";
    // Příznak validity všech výstupů dohromady
    public static final int FLAG_OUTPUT = 1 << 2;
    // endregion

    // region Variables
    // Příznak validity všech výstupů
    private int outputValidity;
    // Kolekce všech výstupů
    public final ObservableArrayList<Output> outputList;
    // endregion

    // region Constructors
    /**
     * Konstruktor třídy ConfiguraionFVEP
     * Vytvoří novou konfiguraci s výchozími parametry
     *
     * @param name Název konfigurace
     */
    public ConfigurationFVEP(String name) {
        this(name, DEF_OUTPUT_COUNT, new ObservableArrayList<Output>());
    }

    /**
     * Konstruktor třídy ConfiguraionFVEP
     * Vytvoří novou konfiguraci s parametry
     *
     * @param name Název konfigurace
     * @param outputCount Počet výstupů
     * @param outputList Reference na kolekci výstupů
     */
    public ConfigurationFVEP(String name, int outputCount, ObservableArrayList<Output> outputList) {
        super(name, ConfigurationType.FVEP, outputCount);

        this.outputList = outputList;
        rearangeOutputs();
    }
    // endregion

    // region Private methods

    /**
     * Upraví počet výstupů
     * Pokud je jich víc, než je požadováno, tak odstraní poslední
     * Pokud je jich méně, tak vytvoří nové
     */
    private void rearangeOutputs() {
        if (outputList == null || outputCount == outputList.size()) {
            return;
        }

        int listCount = outputList.size();
        if (outputCount > listCount) {
            for (int i = listCount; i < outputCount; i++) {
                outputList.add(new Output(this, i + 1));
            }
        } else {
            for (int i = --listCount; i >= outputCount; i--) {
                outputList.remove(i);
            }
        }
    }

    /**
     * Nastaví příznak validity jednotlivých výstupů
     *
     * @param outputID ID výstupu
     * @param value    True, pokud je výstup nevalidní, jinak false
     */
    private void setOutputValidity(int outputID, boolean value) {
        if (value) {
            outputValidity |= 1 << outputID;
        } else {
            outputValidity &= ~(1 << outputID);
        }

        notifyPropertyChanged(BR.validityFlag);

        if (outputValidity != 0) {
            setValid(false);
            setValidityFlag(FLAG_OUTPUT, true);
        } else {
            setValidityFlag(FLAG_OUTPUT, false);
        }
    }
    // endregion

    // region Public methods
    /**
     * Vrátí handler na základě typu souboru
     *
     * @return IO handler pro práci se souborem
     */
    @Override
    public IOHandler getHandler() {
        switch (metaData.extensionType) {
            case XML:
                return new XMLHandlerFVEP(this);
            case CSV:
                return new CSVHandlerFVEP(this);
            default:
                return new JSONHandlerFVEP(this);
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
        ObservableArrayList<Output> duplicatedOutputList = new ObservableArrayList<>();
        ConfigurationFVEP configuration = new ConfigurationFVEP(newName, outputCount, duplicatedOutputList);
        super.duplicateDefault(configuration);

        configuration.outputValidity = outputValidity;

        duplicatedOutputList.clear();
        for (Output output : outputList) {
            duplicatedOutputList.add(output.duplicate(configuration));
        }

        return configuration;
    }

    // endregion

    // region Getters & Setters

    /**
     * Nastaví počet výstupů
     * Po nastavení se výstupy přepočítají, aby počet odpovídal počtu položek v kolekci výstupů
     *
     * @param outputCount Počet výstupů
     */
    @Override
    public void setOutputCount(int outputCount) {
        setOutputCount(outputCount, true);
    }

    /**
     * Nastaví počet výstupů
     *
     * @param outputCount     Počet výstupů
     * @param rearangeOutputs True, pokud se mají přepočitat výstupy, jinak false
     */
    public void setOutputCount(int outputCount, boolean rearangeOutputs) {
        super.setOutputCount(outputCount);

        if (rearangeOutputs) {
            rearangeOutputs();
        }
    }
    // endregion

    /**
     * Kontrakt pro duplikovaný výstup
     */
    public interface IDuplicable {
        /**
         * Vytvoří a vrátí nový zduplikovaný output
         *
         * @param parent Konfigurace, která vlastní výstup
         * @return Nový zduplikovaný output
         */
        Output duplicate(ConfigurationFVEP parent);
    }

    public static final class Output extends BaseModel implements IDuplicable {

        // region Constants
        // Minimání hodnota parametru pulsUp
        public static final int MIN_PULS_UP = MIN_MS_TIME;
        // Maximální hodnota parametru pulsUp
        public static final int MAX_PULS_UP = MAX_MS_TIME;
        // Výchozí hodnota parametru pulsUp
        public static final int DEF_PULS_UP = MIN_PULS_UP;
        // Minimání hodnota parametru pulsDown
        public static final int MIN_PULS_DOWN = MIN_MS_TIME;
        // Maximální hodnota parametru pulsDown
        public static final int MAX_PULS_DOWN = MAX_MS_TIME;
        // Výchozí hodnota parametru pulsDown
        public static final int DEF_PULS_DOWN = MIN_PULS_DOWN;
        // Minimání hodnota parametru pulsDown
        public static final double MIN_FREQUENCY = MIN_MS_TIME;
        // Maximální hodnota parametru pulsDown
        public static final double MAX_FREQUENCY = 20;
        // Krok frekvence
        public static final double FREQUENCY_STEP = 0.5;
        // Výchozí hodnota parametru pulsDown
        public static final double DEF_FREQUENCY = MIN_FREQUENCY;
        // Minimání hodnota parametru pulsDown
        public static final int MIN_DUTY_CYCLE = MIN_PERCENT;
        // Maximální hodnota parametru pulsDown
        public static final int MAX_DUTY_CYCLE = MAX_PERCENT;
        // Výchozí hodnota parametru pulsDown
        public static final int DEF_DUTY_CYCLE = MIN_DUTY_CYCLE;
        // Příznak validity pro parametr pulsUp
        public static final int FLAG_PULS_UP = 1 << 0;
        // Příznak validity pro parametr pulsDown
        public static final int FLAG_PULS_DOWN = 1 << 1;
        // Příznak validity pro parametr frequency
        public static final int FLAG_FREQUENCY = 1 << 2;
        // Příznak validity pro parametr duty cycle
        public static final int FLAG_DUTY_CYCLE = 1 << 3;
        // Příznak validity pro parametr brightness
        public static final int FLAG_BRIGHTNESS = 1 << 4;
        // endregion

        // region Variables
        private final ConfigurationFVEP config;
        @Bindable
        private final int id;
        // Doba, po kterou je výstup aktivní [ms]
        @Bindable
        private String pulsUp;
        // Doba, po kterou je výstup neaktivní [ms]
        @Bindable
        private String pulsDown;
        // Frekvence [ms]
        @Bindable
        private String frequency;
        // Délka pulzu při nastavení frekvence [%]
        @Bindable
        private String dutyCycle;
        // Jas výstupu [%]
        @Bindable
        private String brightness;
        // endregion

        // region Constructors

        /**
         * Konstruktor třídy Output
         * Vytvoří nový výstup s výchozími hodnotami
         *
         * @param config Konfigurace, ke které je výstup přiřazen
         * @param id     Jednoznačný identifikátor výstupu
         */
        public Output(ConfigurationFVEP config, int id) {
            this(config, id,
                    String.valueOf(DEF_PULS_UP),
                    String.valueOf(DEF_PULS_DOWN),
                    String.valueOf(DEF_FREQUENCY),
                    String.valueOf(DEF_DUTY_CYCLE),
                    String.valueOf(DEF_BRIGHTNESS));
        }

        /**
         * Konstruktor třídy Output
         * Vytvoří nový výstup na základě parametrů
         *
         * @param config     Konfigurace, ke které je výstup přiřazen
         * @param id         Jednoznačný identifikátor výstupu
         * @param pulsUp     Doba, po kterou je výstup aktivní [ms]
         * @param pulsDown   Doba, po kterou je výstup neaktivní [ms]
         * @param frequency  Frekvence [ms]
         * @param dutyCycle  Délka pulzu při nastavení frekvence
         * @param brightness Jas výstupu
         */
        public Output(ConfigurationFVEP config, int id, String pulsUp, String pulsDown,
                      String frequency, String dutyCycle, String brightness) {
            this.config = config;
            this.id = id;
            setPulsUp(pulsUp);
            setPulsDown(pulsDown);
            setFrequency(frequency);
            setDutyCycle(dutyCycle);
            setBrightness(brightness);
        }

        // endregion

        // region Public methods
        /**
         * Vytvoří a vrátí nový zduplikovaný output
         *
         * @param parent Konfigurace, která vlastní výstup
         * @return Nový zduplikovaný output
         */
        @Override
        public Output duplicate(ConfigurationFVEP parent) {
            Output output = new Output(parent, id, pulsUp, pulsDown, frequency, dutyCycle, brightness);
            output.valid = valid;
            output.validityFlag = validityFlag;

            return output;
        }

        @Override
        public void setValid(boolean valid) {
           super.setValid(valid);

            config.setOutputValidity(id, !valid);
        }

        // region Getters & Setters

        /**
         * Vrátí referenci na konfiguraci, ke které výstup patří
         *
         * @return Referenci na konfiguraci, ke které výstup patří
         */
        public ConfigurationFVEP getParentConfiguration() {
            return config;
        }

        /**
         * Vrátí identifikátor outputu
         *
         * @return Identifikátor outputu
         */
        public int getId() {
            return id;
        }

        /**
         * Vrátí dobu [ms] po kterou je výstup aktivní
         *
         * @return Doba [ms] po kterou je výstup aktivní
         */
        public String getPulsUp() {
            return pulsUp;
        }

        /**
         * Nastaví dobu [ms], po kterou je výstup aktivní
         *
         * @param value Doba [ms] po kterou je výstup aktivní
         */
        public void setPulsUp(String value) {
            pulsUp = value;
            notifyPropertyChanged(BR.pulsUp);
            if (value == null || value.isEmpty()) {
                setValid(false);
                setValidityFlag(FLAG_PULS_UP, true);
                return;
            }
            int v = Integer.valueOf(value);

            if (v < MIN_PULS_UP || v > MAX_PULS_UP) {
                setValid(false);
                setValidityFlag(FLAG_PULS_UP, true);
            } else {
                setValidityFlag(FLAG_PULS_UP, false);
            }
        }

        /**
         * Vrátí dobu [ms] po kterou je výstup neaktivní
         *
         * @return Doba [ms] po kterou je výstup neaktivní
         */
        public String getPulsDown() {
            return pulsDown;
        }

        /**
         * Nastaví dobu [ms] po kterou je výstup neaktivní
         *
         * @param value Doba [ms] po kterou je výstup neaktivní
         */
        public void setPulsDown(String value) {
            pulsDown = value;
            notifyPropertyChanged(BR.pulsDown);
            if (value == null || value.isEmpty()) {
                setValid(false);
                setValidityFlag(FLAG_PULS_DOWN, true);
                return;
            }
            int v = Integer.valueOf(value);

            if (v < MIN_PULS_DOWN || v > MAX_PULS_DOWN) {
                setValid(false);
                setValidityFlag(FLAG_PULS_DOWN, true);
            } else {
                setValidityFlag(FLAG_PULS_DOWN, false);
            }
        }

        /**
         * Vrátí frekvenci
         *
         * @return Frekvence [%]
         */
        public String getFrequency() {
            return frequency;
        }

        /**
         * Nastaví frekvenci
         *
         * @param value Frekvence [%]
         */
        public void setFrequency(String value) {
            this.frequency = value;
            notifyPropertyChanged(BR.frequency);
            if (value == null || value.isEmpty()) {
                setValid(false);
                setValidityFlag(FLAG_FREQUENCY, true);
                return;
            }
            double v = Double.valueOf(value);

            if (v < MIN_FREQUENCY || v > MAX_FREQUENCY || v % FREQUENCY_STEP != 0) {
                setValid(false);
                setValidityFlag(FLAG_FREQUENCY, true);
            } else {
                setValidityFlag(FLAG_FREQUENCY, false);
            }
        }

        /**
         * Vrátí Délku pulzu při nastavení frekvence
         *
         * @return Délka pulzu při nastavení frekvence [%]
         */
        public String getDutyCycle() {
            return dutyCycle;
        }

        /**
         * Nastaví délku pulzu při nastavení frekvence
         *
         * @param value Délka pulzu při nastavení frekvence [%]
         */
        public void setDutyCycle(String value) {
            this.dutyCycle = value;
            notifyPropertyChanged(BR.dutyCycle);
            if (value == null || value.isEmpty()) {
                setValid(false);
                setValidityFlag(FLAG_DUTY_CYCLE, true);
                return;
            }
            int v = Integer.valueOf(value);

            if (v < MIN_DUTY_CYCLE || v > MAX_DUTY_CYCLE) {
                setValid(false);
                setValidityFlag(FLAG_DUTY_CYCLE, true);
            } else {
                setValidityFlag(FLAG_DUTY_CYCLE, false);
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
            int v = Integer.valueOf(value);

            if (v < MIN_BRIGHTNESS || v > MAX_BRIGHTNESS) {
                setValid(false);
                setValidityFlag(FLAG_BRIGHTNESS, true);
            } else {
                setValidityFlag(FLAG_BRIGHTNESS, false);
            }
        }
        // endregion
    }
}
