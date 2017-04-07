package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import android.databinding.Bindable;

import java.util.List;
import java.util.regex.Pattern;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;

public abstract class AConfiguration extends BaseModel implements IDuplicable {

    // region Constants
    // Logovací tag
    private static final String TAG = "AConfiguration";
    // Pattern pro název konfigurace:
    // - musí se jednat o neprázdné slovo
    // - délka slova je z intervalu <1, 32>
    // - slovo může obsahovat pouze [a-zA-Z0-9] a "_"
    //   přičemž číslo nesmí být na první pozici
    private static final Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]{0,31}");
    // Maximální délka názvu konfigurace
    public static final int MAX_NAME_LENGTH = 32;
    // Výchozí typ konfigurace
    public static final ConfigurationType DEF_CONFIGURATION_TYPE = ConfigurationType.UNDEFINED;
    // Maximální počet výstupů
    public static final int MAX_OUTPUT_COUNT = 8;
    // Minimání počet výstupů
    public static final int MIN_OUTPUT_COUNT = 1;
    // Výchozí počet výstupů
    public static final int DEF_OUTPUT_COUNT = 4;
    // Minimální doba trvání [ms]
    public static final int MIN_MS_TIME = 0;
    // Maximální doba trvání [ms]
    public static final int MAX_MS_TIME = 1 << 16;
    // Maximální doba trvání pro byte hodnoty [ms]
    public static final int MAX_MS_BYTE_TIME = 255;
    // Minimální hodnota v procentech
    public static final int MIN_PERCENT = 0;
    // Maximální hodnota v procentech
    public static final int MAX_PERCENT = 100;
    // Výchozí typ média
    public static final MediaType DEF_MEDIA_TYPE = MediaType.LED;
    // Minimální jas výstupu
    public static final int MIN_BRIGHTNESS = 0;
    // Výchozí jas výstupu
    public static final int DEF_BRIGHTNESS = 50;
    // Maximální jas výstupu
    public static final int MAX_BRIGHTNESS = 100;
    // Příznak validity názvu konfigurace
    public static final int FLAG_NAME = 1 << 0;
    // Příznak validity počtu výstupů
    public static final int FLAG_OUTPUT_COUNT = 1 << 1;
    // endregion

    // region Variables
    // Název konfigurace
    @Bindable
    protected String name;
    // Typ konfigurace
    @Bindable
    protected ConfigurationType configurationType;
    // Počet výstupů
    @Bindable
    protected int outputCount;
    // Validita konfigurace
    // Dodatečné informace o konfiguraci
    public final MetaData metaData = new MetaData();
    // endregion

    // region Constructors

    /**
     * Vytvoří novou konfiguraci s nedefinovaným typem
     *
     * @param name Název konfigurace
     */
    public AConfiguration(String name) {
        this(name, DEF_CONFIGURATION_TYPE);
    }

    /**
     * Konstruktor třídy AConfiguration
     * Počet výstupů bude nastaven na výchozí hodnotu
     *
     * @param name              Název konfigurace
     * @param configurationType Typ konfigurace
     * @see #DEF_OUTPUT_COUNT
     */
    public AConfiguration(String name, ConfigurationType configurationType) {
        this(name, configurationType, DEF_OUTPUT_COUNT);
    }

    /**
     * Konstruktor třídy AConfiguration
     *
     * @param name              Název konfigurace
     * @param configurationType Typ konfigurace
     * @param outputCount       Počet výstupů
     */
    public AConfiguration(String name, ConfigurationType configurationType, int outputCount) {
        setName(name);
        this.configurationType = configurationType;
        setOutputCount(outputCount);
    }

    // endregion

    // region Public static methods

    /**
     * Zjistí, zda-li je název validní
     *
     * @param name Název konfigurace
     * @return True, pokud je název validní, jinak false
     */
    public static boolean isNameValid(String name) {
        return pattern.matcher(name).matches();
    }
    // endregion

    // region Private methods

    /**
     * Duplikuje základní vlastnosti konfigurace
     *
     * @param configuration Konfigurace
     */
    protected void duplicateDefault(AConfiguration configuration) {
        configuration.configurationType = configurationType;
        configuration.outputCount = outputCount;
        configuration.valid = valid;
        configuration.validityFlag = validityFlag;
        configuration.changed = changed;
    }

    // endregion

    // region Public methods

    /**
     * Vrátí handler na základě typu souboru
     *
     * @return IO handler pro prási se souborem
     */
    public abstract IOHandler getHandler();

    /**
     * @return Vrátí kolekcí packetů reprezentující konfiguraci
     */
    public abstract List<BtPacket> getPackets();

    // endregion

    // region Getters & Setters

    /**
     * Vrátí název konfigurace
     *
     * @return Název konfigurace
     */
    public String getName() {
        return name;
    }

    /**
     * Nastaví název konfigurace
     *
     * @param name Nový název konfigurace
     */
    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

        if (!isNameValid(name)) {
            setValid(false);
            setValidityFlag(FLAG_NAME, true);
        } else {
            setValidityFlag(FLAG_NAME, false);
        }
    }

    /**
     * Vrátí typ konfigurace
     *
     * @return Typ konfigurace
     */
    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    /**
     * Vrátí počet výstupů
     *
     * @return Počet výstupů
     */
    public int getOutputCount() {
        return outputCount;
    }

    /**
     * Nastaví počet výstupů
     *
     * @param outputCount Počet výstupů
     */
    public void setOutputCount(int outputCount) {
        this.outputCount = outputCount;
        notifyPropertyChanged(BR.outputCount);

        if (outputCount < MIN_OUTPUT_COUNT || outputCount > MAX_OUTPUT_COUNT) {
            setValid(false);
            setValidityFlag(FLAG_OUTPUT_COUNT, true);
        } else {
            setValidityFlag(FLAG_OUTPUT_COUNT, false);
        }
    }
    // endregion

}
