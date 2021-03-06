package cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep;

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
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;

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

    public static final class Output extends BaseObservable implements IValidate, IDuplicable {

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
        private int pulsUp;
        // Doba, po kterou je výstup neaktivní [ms]
        @Bindable
        private int pulsDown;
        // Frekvence [ms]
        @Bindable
        private double frequency;
        // Délka pulzu při nastavení frekvence [%]
        @Bindable
        private int dutyCycle;
        // Jas výstupu [%]
        @Bindable
        private int brightness;
        // Příznak, zda-li je výstup validní
        @Bindable
        private boolean valid = true;
        // Příznak validity jednotlivých hodnot
        @Bindable
        private int validityFlag;
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
            this(config, id, DEF_PULS_UP, DEF_PULS_DOWN, DEF_FREQUENCY, DEF_DUTY_CYCLE, DEF_BRIGHTNESS);
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
        public Output(ConfigurationFVEP config, int id, int pulsUp, int pulsDown, double frequency, int dutyCycle, int brightness) {
            this.config = config;
            this.id = id;
            setPulsUp(pulsUp);
            setPulsDown(pulsDown);
            setFrequency(frequency);
            setDutyCycle(dutyCycle);
            setBrightness(brightness);
        }

        // endregion

        // region Private methods

        /**
         * Nastaví validitu zadanému příznaku
         *
         * @param flag  Příznak
         * @param value True, pokud je příznak validní, jinak false
         */
        private void setValidityFlag(int flag, boolean value) {
            int oldFlagValue = validityFlag;
            if (value) {
                validityFlag |= flag;
            } else {
                validityFlag &= ~flag;
            }

            if (validityFlag == oldFlagValue) {
                return;
            }

            notifyPropertyChanged(BR.validityFlag);

            if (validityFlag == 0) {
                setValid(true);
            }
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

        /**
         * Vrází příznak validity parametrů
         *
         * @return Příznak validity parametrů
         */
        @Override
        public int getValidityFlag() {
            return validityFlag;
        }

        /**
         * Zjistí validitu výstupu
         *
         * @return True, pokud je výstup validní, jinak false
         */
        @Override
        public boolean isValid() {
            return valid;
        }

        /**
         * Nastaví validitu výstupu
         *
         * @param valid True, pokud je výstup validní, jinak false
         */
        @Override
        public void setValid(boolean valid) {
            this.valid = valid;
            notifyPropertyChanged(BR.valid);

            config.setOutputValidity(id, !valid);
        }

        /**
         * Zjistí, zda-li je příznak validní, nebo ne
         *
         * @param flag Příznak
         * @return True, pokud je validní, jinak false
         */
        @Override
        public boolean isFlagValid(int flag) {
            return !((validityFlag & flag) == flag);
        }
        // endregion

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
        public int getPulsUp() {
            return pulsUp;
        }

        /**
         * Nastaví dobu [ms], po kterou je výstup aktivní
         *
         * @param value Doba [ms] po kterou je výstup aktivní
         */
        public void setPulsUp(int value) {
            pulsUp = value;
            notifyPropertyChanged(BR.pulsUp);

            if (value < MIN_PULS_UP || value > MAX_PULS_UP) {
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
        public int getPulsDown() {
            return pulsDown;
        }

        /**
         * Nastaví dobu [ms] po kterou je výstup neaktivní
         *
         * @param value Doba [ms] po kterou je výstup neaktivní
         */
        public void setPulsDown(int value) {
            pulsDown = value;
            notifyPropertyChanged(BR.pulsDown);

            if (value < MIN_PULS_DOWN || value > MAX_PULS_DOWN) {
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
        public double getFrequency() {
            return frequency;
        }

        /**
         * Nastaví frekvenci
         *
         * @param value Frekvence [%]
         */
        public void setFrequency(double value) {
            this.frequency = value;
            notifyPropertyChanged(BR.frequency);

            if (value < MIN_FREQUENCY || value > MAX_FREQUENCY || value % FREQUENCY_STEP != 0) {
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
        public int getDutyCycle() {
            return dutyCycle;
        }

        /**
         * Nastaví délku pulzu při nastavení frekvence
         *
         * @param value Délka pulzu při nastavení frekvence [%]
         */
        public void setDutyCycle(int value) {
            this.dutyCycle = value;
            notifyPropertyChanged(BR.dutyCycle);

            if (value < MIN_DUTY_CYCLE || value > MAX_DUTY_CYCLE) {
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
        public int getBrightness() {
            return brightness;
        }

        /**
         * Nastaví intenzitu jasu výstupu [%]
         *
         * @param value Intenzita jasu výstupu [%]
         */
        public void setBrightness(int value) {
            brightness = value;
            notifyPropertyChanged(BR.brightness);

            if (value < MIN_BRIGHTNESS || value > MAX_BRIGHTNESS) {
                setValid(false);
                setValidityFlag(FLAG_BRIGHTNESS, true);
            } else {
                setValidityFlag(FLAG_BRIGHTNESS, false);
            }
        }
        // endregion
    }
}
