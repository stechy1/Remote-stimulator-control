package cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea;

import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketOld;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;

public class ConfigurationREA extends AConfiguration {

    // region Constants
    // Log tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigurationREA";
    // Minimální počet cyklů
    public static final int MIN_CYCLE_COUNT = 1;
    // Maximální počet cyklů
    public static final int MAX_CYCLE_COUNT = 50;
    // Výchozí hodnota parametru počet cyklů
    public static final int DEF_CYCLE_COUNT = MIN_CYCLE_COUNT;
    // Minimální hodnota parametru waitFixed
    public static final int MIN_WAIT_FIXED = MIN_MS_TIME;
    // Maximální hodnota parametru waitFixed
    public static final int MAX_WAIT_FIXED = MAX_MS_TIME;
    // Výchozí hodnota parametru waitFixed
    public static final int DEF_WAIT_FIXED = MIN_WAIT_FIXED;
    // Minimální hodnota parametru waitRandom
    public static final int MIN_WAIT_RANDOM = MIN_MS_TIME;
    // Maximální hodnota parametru waitRandom
    public static final int MAX_WAIT_RANDOM = MAX_MS_TIME;
    // Výchozí hodnota parametru waitRandom
    public static final int DEF_WAIT_RANDOM = MIN_WAIT_RANDOM;
    // Minimální hodnota parametru missTime
    public static final int MIN_MISS_TIME = MIN_MS_TIME;
    // Maximální hodnota parametru missTime
    public static final int MAX_MISS_TIME = MAX_MS_TIME;
    // Výchozí hodnota parametru missTime
    public static final int DEF_MISS_TIME = MIN_MISS_TIME;
    // Výchozí hodnota parametru on fail
    public static final OnFail DEF_ON_FAIL = OnFail.WAIT;
    // Výchozí hodnota rodu testované osoby
    public static final Gender DEF_GENDER = Gender.MALE;
    // Minimální věk testované osoby
    public static final int MIN_AGE = 1;
    // Maximální věk testované osoby
    public static final int MAX_AGE = 120;
    // Výchozí věk testované osoby
    public static final int DEF_AGE = 18;
    // Minimální výška testované osoby
    public static final int MIN_HEIGHT = 1;
    // Maximální výška testované osoby
    public static final int MAX_HEIGHT = MAX_MS_BYTE_TIME;
    // Výchozí výška testované osoby
    public static final int DEF_HEIGHT = MIN_HEIGHT;
    // Minimální váha testované osoby
    public static final int MIN_WEIGHT = 1;
    // Maximální výha testované osoby
    public static final int MAX_WEIGHT = MAX_MS_BYTE_TIME;
    // Výchozí váha testované osoby
    public static final int DEF_WEIGHT = MIN_WEIGHT;
    // Příznak validity parametru cycle count
    public static final int FLAG_CYCLE_COUNT = 1 << 2;
    // Příznak validity parametru wait fixed
    public static final int FLAG_WAIT_FIXED = 1 << 3;
    // Příznak validity parametru wait random
    public static final int FLAG_WAIT_RANDOM = 1 << 4;
    // Příznak validity parametru miss time
    public static final int FLAG_MISS_TIME = 1 << 5;
    // Příznak validity parametru brightness
    public static final int FLAG_BRIGHTNESS = 1 << 6;
    // Příznak validity parametru age
    public static final int FLAG_AGE = 1 << 7;
    // Příznak validity parametru height
    public static final int FLAG_HEIGHT = 1 << 8;
    // Příznak validity parametru weight
    public static final int FLAG_WEIGHT = 1 << 9;
    // endregion

    // region Variables
    // Počet cyklů [1]
    @Bindable private String cycleCount;
    // Parametr wait fixed [ms]
    @Bindable private String waitFixed;
    // Parametr wait random [ms]
    @Bindable private String waitRandom;
    // Parametr miss time [ms]
    @Bindable private String missTime;
    // Intenzita jasu [%]
    @Bindable private String brightness;
    // Parametr on fail - co se stane, když osoba nestihne zareagovat
    @Bindable private OnFail onFail;
    // Muž/žena
    @Bindable private Gender gender;
    // Věk testované osoby [1]
    @Bindable private String age;
    // Výška testované osoby [cm]
    @Bindable private String height;
    // Váha testované osoby [kg]
    @Bindable private String weight;
    // endregion

    // region Constructors
    /**
     * Konstruktor třídy ConfigurationREA
     * Vytvoří novou konfiguraci s výchozími parametry
     *
     * @param name Název konfigurace
     */
    public ConfigurationREA(String name) {
        this(name, DEF_OUTPUT_COUNT,
                String.valueOf(DEF_CYCLE_COUNT),
                String.valueOf(DEF_WAIT_FIXED),
                String.valueOf(DEF_WAIT_RANDOM),
                String.valueOf(DEF_MISS_TIME),
                String.valueOf(DEF_BRIGHTNESS),
                DEF_ON_FAIL, DEF_GENDER,
                String.valueOf(DEF_AGE),
                String.valueOf(DEF_HEIGHT),
                String.valueOf(DEF_WEIGHT));
    }

    /**
     * Konstruktor třídy ConfigurationREA
     * Vytvoří novou konfiguraci s parametry
     *
     * @param name Název konfigurace
     * @param outputCount Počet výstupů
     * @param cycleCount Počet cyklů
     * @param waitFixed Parametr wait fixed [ms]
     * @param waitRandom Parametr wait random [ms]
     * @param missTime Parametr miss time
     * @param brightness Intenzita jasu [%]
     * @param onFail Parametr onFail
     * @param gender Rod testované osoby
     * @param age Věk testované osoby
     * @param height Výška testované osoby
     * @param weight Váha testované osoby
     */
    public ConfigurationREA(String name, int outputCount, String cycleCount, String waitFixed,
                            String waitRandom, String missTime, String brightness, OnFail onFail,
                            Gender gender, String age, String height, String weight) {
        super(name, ConfigurationType.REA, outputCount);

        setCycleCount(cycleCount);
        setWaitFixed(waitFixed);
        setWaitRandom(waitRandom);
        setMissTime(missTime);
        setBrightness(brightness);
        setOnFail(onFail);
        setGender(gender);
        setAge(age);
        setHeight(height);
        setWeight(weight);
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
                return new XMLHandlerREA(this);
            case CSV:
                return new CSVHandlerREA(this);
            default:
                return new JSONHandlerREA(this);
        }
    }

    @Override
    public List<BtPacketOld> getPackets() {
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
        ConfigurationREA configuration = new ConfigurationREA(newName, outputCount, cycleCount, waitFixed, waitRandom, missTime, brightness, onFail, gender, age, height, weight);
        super.duplicateDefault(configuration);

        return configuration;
    }

    // endregion

    // region Getters & Setters

    /**
     * Vrátí počet cyklů
     *
     * @return Počet cyklů [1]
     */
    public String getCycleCount() {
        return cycleCount;
    }

    /**
     * Nastaví počet cyklů
     *
     * @param value Počet cyklů [1]
     */
    public void setCycleCount(String value) {
        this.cycleCount = value;
        notifyPropertyChanged(BR.cycleCount);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_CYCLE_COUNT, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_CYCLE_COUNT || v > MAX_CYCLE_COUNT) {
            setValid(false);
            setValidityFlag(FLAG_CYCLE_COUNT, true);
        } else {
            setValidityFlag(FLAG_CYCLE_COUNT, false);
        }
    }

    /**
     * Vrátí hodnotu parametru wait fixed
     *
     * @return Hodnota parametru wait fixed [ms]
     */
    public String getWaitFixed() {
        return waitFixed;
    }

    /**
     * Nastaví hodnotu parametru wait fixed
     *
     * @param value Hodnota parametru wait fixed [ms]
     */
    public void setWaitFixed(String value) {
        this.waitFixed = value;
        notifyPropertyChanged(BR.waitFixed);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_WAIT_FIXED, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_WAIT_FIXED || v > MAX_WAIT_FIXED) {
            setValid(false);
            setValidityFlag(FLAG_WAIT_FIXED, true);
        } else {
            setValidityFlag(FLAG_WAIT_FIXED, false);
        }
    }

    /**
     * Vrátí hodnotu parametru wait random
     *
     * @return Hodnota parametru wait random [ms]
     */
    public String getWaitRandom() {
        return waitRandom;
    }

    /**
     * Nastaví hodnotu parametru wait random
     *
     * @param value Hodnota parametru wait random [ms]
     */
    public void setWaitRandom(String value) {
        this.waitRandom = value;
        notifyPropertyChanged(BR.waitRandom);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_WAIT_RANDOM, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_WAIT_RANDOM || v > MAX_WAIT_RANDOM) {
            setValid(false);
            setValidityFlag(FLAG_WAIT_RANDOM, true);
        } else {
            setValidityFlag(FLAG_WAIT_RANDOM, false);
        }
    }

    /**
     * Vrátí hodnotu parametru miss time
     *
     * @return Hodnota parametru miss time [ms]
     */
    public String getMissTime() {
        return missTime;
    }

    /**
     * Nastaví hodntu parametru miss time
     *
     * @param value Hodnota parametru miss time [ms]
     */
    public void setMissTime(String value) {
        this.missTime = value;
        notifyPropertyChanged(BR.missTime);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_MISS_TIME, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_MISS_TIME || v > MAX_MISS_TIME) {
            setValid(false);
            setValidityFlag(FLAG_MISS_TIME, true);
        } else {
            setValidityFlag(FLAG_MISS_TIME, false);
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

    /**
     * Vrátí hodnotu parametru {@link OnFail}
     *
     * @return Hodnota parametru on fail
     */
    public OnFail getOnFail() {
        return onFail;
    }

    /**
     * Nastaví hodnotu parametru {@link OnFail}
     *
     * @param value Hodnota parametru on fail
     */
    public void setOnFail(OnFail value) {
        this.onFail = value;
        notifyPropertyChanged(BR.onFail);
    }

    /**
     * Vrátí rod testované osoby {@link Gender}
     *
     * @return Rod testované osoby
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Nastaví rod testované osoby {@link Gender
     *
     * @param value Rod testované osoby
     */
    public void setGender(Gender value) {
        this.gender = value;
        notifyPropertyChanged(BR.gender);
    }

    /**
     * Vrátí věk testované osoby
     *
     * @return Věk testované osoby [1]
     */
    public String getAge() {
        return age;
    }

    /**
     * Nastaví věk testované osoby
     *
     * @param value Věk testované osoby
     */
    public void setAge(String value) {
        this.age = value;
        notifyPropertyChanged(BR.age);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_AGE, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_AGE || v > MAX_AGE) {
            setValid(false);
            setValidityFlag(FLAG_AGE, true);
        } else {
            setValidityFlag(FLAG_AGE, false);
        }
    }

    /**
     * Vrátí výšku testované osoby
     *
     * @return Výška testované osoby [cm]
     */
    public String getHeight() {
        return height;
    }

    /**
     * Nastaví výšku testované osoby
     *
     * @param value Výška testované osoby [cm]
     */
    public void setHeight(String value) {
        this.height = value;
        notifyPropertyChanged(BR.height);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_HEIGHT, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_HEIGHT || v > MAX_HEIGHT) {
            setValid(false);
            setValidityFlag(FLAG_HEIGHT, true);
        } else {
            setValidityFlag(FLAG_HEIGHT, false);
        }
    }

    /**
     * Vrátí váhu testované osoby
     *
     * @return Váha testované osoby [kg]
     */
    public String getWeight() {
        return weight;
    }

    /**
     * Nastaví váhu testované osoby
     *
     * @param value Váha testované osoby [kg]
     */
    public void setWeight(String value) {
        this.weight = value;
        notifyPropertyChanged(BR.weight);
        if (value == null || value.isEmpty()) {
            setValid(false);
            setValidityFlag(FLAG_WEIGHT, true);
            return;
        }
        int v = Integer.parseInt(value);

        if (v < MIN_WEIGHT || v > MAX_WEIGHT) {
            setValid(false);
            setValidityFlag(FLAG_WEIGHT, true);
        } else {
            setValidityFlag(FLAG_WEIGHT, false);
        }
    }

    // endregion

    public enum OnFail {
        WAIT, CONTINUE;

        public static OnFail valueOf(int index) {
            return OnFail.values()[index];
        }
    }

    public enum Gender {
        MALE, FEMALE;
        public static Gender valueOf(int index) {
            return Gender.values()[index];
        }

    }
}
