package cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp;

import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.Code;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.Codes;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.DataConvertor;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP.Output.MAX_DISTRIBUTION_VALUE;
import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP.Output.MIN_DISTRIBUTION_VALUE;

/**
 * Třída představující konfiguraci ERP
 */
public class ConfigurationERP extends AConfiguration {

    // region Constants
    // Minimální hodnota parametru out
    public static final int MIN_OUT = MIN_MS_TIME;
    // Maximální hodnota parametru out
    public static final int MAX_OUT = MAX_MS_TIME;
    // Výchozí hodnota parametru out
    public static final int DEF_OUT = 0;
    // Minimální hodnota parametru wait
    public static final int MIN_WAIT = MIN_MS_TIME;
    // Maximální hodnota parametru wait
    public static final int MAX_WAIT = MAX_MS_TIME;
    // Výchozí hodnota parametru wait
    public static final int DEF_WAIT = 0;
    // Výchozí hodnota parametru edge
    public static final Edge DEF_EDGE = Edge.FALLING;
    // Výchozí hodnota parametru random
    public static final Random DEF_RANDOM = Random.OFF;
    // Příznak validity parametru out
    public static final int FLAG_OUT = 1 << 2;
    // Příznak validity parametru wait
    public static final int FLAG_WAIT = 1 << 3;
    // Příznak validity všech výstupů dohromady
    public static final int FLAG_OUTPUT = 1 << 4;

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfiurationERP";
    // endregion

    // region Variables
    // Kolekce všech výstupů
    public final ObservableArrayList<Output> outputList;
    // Maximální hodnota parametru distribution value pro všechny výstupy dané konfigurace
    @Bindable
    int maxDistributionValue;
    // Parametr out
    @Bindable
    private String out;
    // Parametr wait
    @Bindable
    private String wait;
    // Hrana, na kterou bude experiment reagovat
    @Bindable
    private Edge edge;
    // Náhodnost experimentu
    @Bindable
    private Random random;
    // Příznak validity všech výstupů
    private int outputValidity;
    // endregion

    // region Constructors

    /**
     * Konstruktor třídy ConfiguraionERP
     * Vytvoří novou konfiguraci s výchozími parametry
     *
     * @param name Název konfigurace
     */
    public ConfigurationERP(String name) {
        this(name, DEF_OUTPUT_COUNT, new ObservableArrayList<Output>(),
                DEF_RANDOM,
                DEF_EDGE,
                String.valueOf(DEF_WAIT),
                String.valueOf(DEF_OUT));
    }

    /**
     * Konstruktor třídy ConfigurationERP
     * Vytvoří novou konfiguraci s parametry
     *
     * @param name        Název konfigurace
     * @param outputCount Počet výstupů
     * @param edge        Typ hrany
     * @param random      Náhodnost
     * @param outputList  Reference na kolekci výstupů
     */
    public ConfigurationERP(String name, int outputCount, ObservableArrayList<Output> outputList, Random random, Edge edge, String wait, String out) {
        super(name, ConfigurationType.ERP, outputCount);

        this.outputList = outputList;
        rearangeOutputs();

        setRandom(random);
        setEdge(edge);
        setWait(wait);
        setOut(out);
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
        }
        else {
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
     * {@inheritDoc}
     */
    @Override
    public IOHandler getHandler() {
        switch (metaData.extensionType) {
            case XML:
                return new XMLHandlerERP(this);
            case CSV:
                return new CSVHandlerERP(this);
            default:
                return new JSONHandlerERP(this);
        }
    }

    @Override
    public List<BtPacket> getPackets() {
        List<BtPacket> packets = new ArrayList<>();

        packets.add(new BtPacket(Codes.EDGE, DataConvertor.intTo1B(edge.ordinal())));
        packets.add(new BtPacket(Codes.RANDOMNESS_ON, DataConvertor.intTo1B(random.ordinal()))); //TODO jak je to s tím kódem náhodnosti?

        Code actualDURATION = Codes.OUTPUT0_DURATION;
        Code actualPAUSE = Codes.OUTPUT0_PAUSE;
        Code actualDISTRIBUTION = Codes.OUTPUT0_DISTRIBUTION;
        Code actualBRIGHTNESS = Codes.OUTPUT0_BRIGHTNESS;

        int vystup = 0; //index výstupu, slouží pro odfiltrování jasu kvůli sdružení u LED 5 a 7

        for(Output output : outputList){
            packets.add(new BtPacket(actualDURATION, DataConvertor.milisecondsTo2B(Integer.parseInt(output.pulsUp))));
            packets.add(new BtPacket(actualPAUSE, DataConvertor.milisecondsTo2B(Integer.parseInt(output.pulsDown))));
            packets.add(new BtPacket(actualDISTRIBUTION, DataConvertor.intTo1B(Integer.parseInt(output.distributionValue)))); //TODO u distribution parametru ještě neposíláme delay

            if(vystup != 5 && vystup != 7) {  //neukládáme hodnoty pro výstupy 5 a 7 protože jsou sdružené (bereme ty nižší)
                packets.add(new BtPacket(actualBRIGHTNESS, DataConvertor.intTo1B(Integer.parseInt(output.brightness))));
                actualBRIGHTNESS = actualBRIGHTNESS.getNext();
            }

            actualDURATION = actualDURATION.getNext();
            actualPAUSE = actualPAUSE.getNext();
            actualDISTRIBUTION = actualDISTRIBUTION.getNext();


            vystup++;
        }

        return packets;
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
        ConfigurationERP configuration = new ConfigurationERP(newName, outputCount, duplicatedOutputList, random, edge, wait, out);
        super.duplicateDefault(configuration);

        configuration.maxDistributionValue = maxDistributionValue;
        configuration.outputValidity = outputValidity;

        duplicatedOutputList.clear();
        for (Output output : outputList) {
            duplicatedOutputList.add(output.duplicate(configuration));
        }

        return configuration;
    }

    /**
     * Spočítá součet všech hodnot parametru distribution value
     *
     * @return Suma hodnot parametru distribution value
     */
    protected int calculateSumOfDistributionValue() {
        int sum = 0;
        for (Output output : outputList)
            sum += Integer.parseInt(output.distributionValue);

        maxDistributionValue = Math.max(Math.min(MAX_DISTRIBUTION_VALUE - sum, MAX_DISTRIBUTION_VALUE), MIN_DISTRIBUTION_VALUE);
        notifyPropertyChanged(BR.maxDistributionValue);

        return sum;
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

    /**
     * Vrátí hodnotu parametru out
     *
     * @return Hodnota parametru out
     */
    public String getOut() {
        return out;
    }

    /**
     * Nastaví parametr out
     *
     * @param value Číslo
     */
    public void setOut(String value) {
        out = value;
        notifyPropertyChanged(BR.out);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_OUT, true);
            return;
        }
        int v = Integer.valueOf(value);

        if (v < MIN_OUT || v > MAX_OUT) {
            setValid(false);
            setValidityFlag(FLAG_OUT, true);
        } else {
            setValidityFlag(FLAG_OUT, false);
        }
    }

    /**
     * Vrátí hodnotu parametru wait
     *
     * @return Hodnota parametru wait
     */
    public String getWait() {
        return wait;
    }

    /**
     * Nastaví parametr wait
     *
     * @param value Číslo
     */
    public void setWait(String value) {
        wait = value;
        notifyPropertyChanged(BR.wait);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_WAIT, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_WAIT || v > MAX_WAIT) {
            setValid(false);
            setValidityFlag(FLAG_WAIT, true);
        } else {
            setValidityFlag(FLAG_WAIT, false);
        }
    }

    /**
     * Vrátí hranu, na kterou bude měření reagovat
     *
     * @return Hrana, na kterou bude měření reagovat
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Nastaví hranu, na kterou bude měření reagovat
     *
     * @param value Hrana, na kterou bude měření reagovat
     */
    public void setEdge(Edge value) {
        edge = value;
        notifyPropertyChanged(BR.edge);
    }

    /**
     * Vrátí náhodnost experimentu
     *
     * @return Náhodnost experimentu
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Nastaví náhodnost experimentu
     *
     * @param value Názodnost experimentu
     */
    public void setRandom(Random value) {
        random = value;
        notifyPropertyChanged(BR.random);
    }

    /**
     * Vrátí maximální hodnotu parametru distribution delay pro všechny výstupy
     * Hodnota se automaticky přepočítává na základě nastavování příslušného parametru
     *
     * @return Maximální hodnotu parametru distribution delay
     */
    public int getMaxDistributionValue() {
        return maxDistributionValue;
    }

    // endregion

    // region Enums

    /**
     * Výčet typu hrany sestupná/náběžná
     */
    public enum Edge {
        LEADING, FALLING;

        /**
         * Vrátí položku podle indexu
         *
         * @param index Index položky
         * @return Edge
         */
        public static Edge valueOf(int index) {
            return Edge.values()[index];
        }
    }

    /**
     * Výčet typu náhodnosti žádná/krátká/dlouhá/krátká i dlouhý
     */
    public enum Random {
        OFF, SHORT, LONG, SHORT_LONG;

        /**
         * Vrátí položku podle indexu
         *
         * @param index Index položky
         * @return Random
         */
        public static Random valueOf(int index) {
            return Random.values()[index];
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
        Output duplicate(ConfigurationERP parent);
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
        // Minimání hodnota parametru distirbutionValue
        public static final int MIN_DISTRIBUTION_VALUE = MIN_PERCENT;
        // Maximální hodnota parametru distirbutionValue
        public static final int MAX_DISTRIBUTION_VALUE = MAX_PERCENT;
        // Výchozí hodnota parametru distirbutionValue
        public static final int DEF_DISTRIBUTION_VALUE = MIN_DISTRIBUTION_VALUE;
        // Minimání hodnota parametru distributionDelay
        public static final int MIN_DISTRIBUTION_DELAY = MIN_MS_TIME;
        // Maximální hodnota parametru distributionDelay
        public static final int MAX_DISTRIBUTION_DELAY = MAX_MS_TIME;
        // Výchozí hodnota parametru distributionDelay
        public static final int DEF_DISTRIBUTION_DELAY = MIN_DISTRIBUTION_DELAY;
        // Příznak validity pro parametr pulsUp
        public static final int FLAG_PULS_UP = 1 << 0;
        // Příznak validity pro parametr pulsDown
        public static final int FLAG_PULS_DOWN = 1 << 1;
        // Příznak validity pro parametr distributionValue
        public static final int FLAG_DISTRIBUTION_VALUE = 1 << 2;
        // Příznak validity pro parametr distributionDelay
        public static final int FLAG_DISTRIBUTION_DELAY = 1 << 3;
        // Příznak validity pro parametr brightness
        public static final int FLAG_BRIGHTNESS = 1 << 4;
        // endregion

        // region Variables
        private final ConfigurationERP config;
        // Identifikátor výstupu
        @Bindable
        private final int id;
        // Doba, po kterou je výstup aktivní [ms]
        @Bindable
        private String pulsUp;
        // Doba, po kterou je výstup neaktivní [ms]
        @Bindable
        private String pulsDown;
        // Parametr distribution value [%]
        @Bindable
        private String distributionValue;
        // Parametr distribution delay [ms]
        @Bindable
        private String distributionDelay;
        // Jas výstupu [%]
        @Bindable
        private String brightness;
        // endregion

        // region Constructors

        /**
         * Konstruktor třídy Output
         * Vytvoří nový výstup s výchozími hodnotami
         *
         * @param config Konfigurace, které je výstup přiřazen
         * @param id     Jednoznačný identifikátor výstupu
         */
        public Output(ConfigurationERP config, int id) {
            this(config, id,
                    String.valueOf(DEF_PULS_UP),
                    String.valueOf(DEF_PULS_DOWN),
                    String.valueOf(DEF_DISTRIBUTION_VALUE),
                    String.valueOf(DEF_DISTRIBUTION_DELAY),
                    String.valueOf(DEF_BRIGHTNESS));
        }

        /**
         * Konstruktor třídy Output
         * Vytvoří nový výstup na základě parametrů
         *
         * @param config            Konfigurace, které je výstup přiřazen
         * @param id                Jednoznačný identifikátor výstupu
         * @param pulsUp            Doba, po kterou je výstup aktivní [ms]
         * @param pulsDown          Doba, po kterou je výstup neaktivní [ms]
         * @param distributionValue Patametr value [%]
         * @param distributionDelay Patametr delay [ms]
         * @param brightness        Intenzita jasu [%]
         */
        public Output(ConfigurationERP config, int id, String pulsUp, String pulsDown,
                      String distributionValue, String distributionDelay, String brightness) {
            this.config = config;
            this.id = id;
            setPulsUp(pulsUp);
            setPulsDown(pulsDown);
            setDistributionValue(distributionValue);
            setDistributionDelay(distributionDelay);
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
        public Output duplicate(ConfigurationERP parent) {
            Output output = new Output(parent, id, pulsUp, pulsDown, distributionValue, distributionDelay, brightness);
            output.valid = valid;
            output.validityFlag = validityFlag;

            return output;
        }

        // endregion

        // region Getters & Setters

        /**
         * Vrátí referenci na konfiguraci, ke které výstup patří
         *
         * @return Referenci na konfiguraci, ke které výstup patří
         */
        public ConfigurationERP getParentConfiguration() {
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
         * Vrátí dobu, po kterou je výstup aktivní
         *
         * @return Doba [ms] po kterou je výstup aktivní
         */
        public String getPulsUp() {
            return pulsUp;
        }

        /**
         * Nastaví dobu, po kterou je výstup aktivní
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
            int v = Integer.parseInt(value);

            if (v < MIN_PULS_UP || v > MAX_PULS_UP) {
                setValid(false);
                setValidityFlag(FLAG_PULS_UP, true);
            } else {
                setValidityFlag(FLAG_PULS_UP, false);
            }
        }

        /**
         * Vrátí dobu, po kterou je výstup neaktivní
         *
         * @return Doba [ms] po kterou je výstup neaktivní
         */
        public String getPulsDown() {
            return pulsDown;
        }

        /**
         * Nastaví dobu, po kterou je výstup neaktivní
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
            int v = Integer.parseInt(value);

            if (v < MIN_PULS_DOWN || v > MAX_PULS_DOWN) {
                setValid(false);
                setValidityFlag(FLAG_PULS_DOWN, true);
            } else {
                setValidityFlag(FLAG_PULS_DOWN, false);
            }
        }

        /**
         * Vrátí distribution value
         *
         * @return Distribution value [%]
         */
        public String getDistributionValue() {
            return distributionValue;
        }

        /**
         * Nastaví distribution value
         *
         * @param value Distribution value [%]
         */
        public void setDistributionValue(String value) {
            distributionValue = value;
            notifyPropertyChanged(BR.distributionValue);
            if (value == null || value.isEmpty()) {
                setValid(false);
                setValidityFlag(FLAG_DISTRIBUTION_VALUE, true);
                return;
            }
            int v = Integer.parseInt(value);

            if (/*config.calculateSumOfDistributionValue() > MAX_DISTRIBUTION_VALUE ||*/ v < MIN_DISTRIBUTION_VALUE || v > MAX_DISTRIBUTION_VALUE) {
                setValid(false);
                setValidityFlag(FLAG_DISTRIBUTION_VALUE, true);
            } else {
                setValidityFlag(FLAG_DISTRIBUTION_VALUE, false);
            }
        }

        /**
         * Vrátí distribution delay
         *
         * @return Distribution delay [ms]
         */
        public String getDistributionDelay() {
            return distributionDelay;
        }

        /**
         * Nastaví distribution delay
         *
         * @param value Distribution delay [ms]
         */
        public void setDistributionDelay(String value) {
            distributionDelay = value;
            notifyPropertyChanged(BR.distributionDelay);
            if (value == null || value.isEmpty()) {
                setValid(false);
                setValidityFlag(FLAG_DISTRIBUTION_DELAY, true);
                return;
            }
            int v = Integer.parseInt(value);

            if (v < MIN_DISTRIBUTION_DELAY || v > MAX_DISTRIBUTION_DELAY) {
                setValid(false);
                setValidityFlag(FLAG_DISTRIBUTION_DELAY, true);
            } else {
                setValidityFlag(FLAG_DISTRIBUTION_DELAY, false);
            }
        }

        /**
         * Vrátí intenzitu jasu výstupu
         *
         * @return Intenzitu jasu výstupu [%]
         */
        public String getBrightness() {
            return brightness;
        }

        /**
         * Nastaví intenzitu jasu výstupu
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
    }
}
