package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.regex.Pattern;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;

public abstract class AConfiguration extends BaseObservable implements IValidate, IDuplicable {

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

    // Pole nevalidních kombinací typů medií
    public static final int[] INVALID_MEDIA_COMBINATION = {
            0b011, // MEDIA_LED&AUDIO
            0b101, // MEDIA_LED&IMAGE
            0b110, // MEDIA_AUDIO&IMAGE
            0b111, // MEDIA_LED&AUDIO&IMAGE
    };
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
    // Typ media, které bude použito pro experiment
    @Bindable
    protected int mediaType;
    // Validita konfigurace
    @Bindable
    protected boolean valid = true;
    // Příznak validity jednotlivých parametrů
    @Bindable
    protected int validityFlag;
    // Přiznak určující, zda-li konfigurace obsahuje nějaká externí média
    @Bindable
    protected boolean media;
    // Příznak, zda-li se změníl stav konfigurace od posledního načteníprotected boolean changed;
    protected boolean changed;
    // Kolekce medií
    public final ObservableList<AMedia> mediaList;
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
        mediaList = new ObservableArrayList<>();

        setName(name);
        this.configurationType = configurationType;
        setOutputCount(outputCount);
        setMediaType(DEF_MEDIA_TYPE);
    }

    // endregion

    // region Public static methods

    /**
     * Zjistí, zda-li je validní kombinace použitých medií
     *
     * @param media Příznak media type z konfigurace
     * @return True, pokud není kombinace validní, jinak false
     */
    public static boolean isInvalidMediaCombination(int media) {
        for (int combination : INVALID_MEDIA_COMBINATION)
            if (combination == media) {
                return true;
            }

        return false;
    }

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
     * Nastaví validitu zadanému příznaku
     *
     * @param flag  Příznak
     * @param value True, pokud je příznak validní, jinak false
     */
    protected void setValidityFlag(int flag, boolean value) {
        int oldFlagValue = this.validityFlag;
        if (value) {
            validityFlag |= flag;
        }
        else {
            validityFlag &= ~flag;
        }

        if (validityFlag == oldFlagValue) {
            return;
        }

        notifyPropertyChanged(BR.validityFlag);
        setChanged();

        if (validityFlag == 0) {
            setValid(true);
        }
    }

    /**
     * Duplikuje základní vlastnosti konfigurace
     *
     * @param configuration Konfigurace
     */
    protected void duplicateDefault(AConfiguration configuration) {
        configuration.configurationType = configurationType;
        configuration.outputCount = outputCount;
        configuration.mediaType = mediaType;
        configuration.valid = valid;
        configuration.validityFlag = validityFlag;
        configuration.changed = changed;
    }

    /**
     * Nastaví, příznak changed na true
     */
    private void setChanged() {
        changed = true;
    }

    /**
     * Nastaví příznak média
     */
    private void setMedia() {
        media = ((this.mediaType & MediaType.AUDIO.getOrdinal()) != 0) || ((this.mediaType & MediaType.IMAGE.getOrdinal()) != 0);
        notifyPropertyChanged(BR.media);
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
     * Vrází příznak validity parametrů
     *
     * @return Příznak validity parametrů
     */
    @Override
    public int getValidityFlag() {
        return validityFlag;
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

    /**
     * Zjistí validitu celkové konfigurace
     *
     * @return True, pokud je celá konfigurace validní, jinak false
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * Nastavi validitu konfigurace
     *
     * @param valid True, pokud je konfigurace validní, jinak false
     */
    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
        notifyPropertyChanged(BR.valid);
    }

    /**
     * Zjistí, zda-li konfigurace obshuje nějaká externí média
     *
     * @return True, pokud konfigurace obsahujě externí média, jinak false
     */
    public boolean isMedia() {
        return media;
    }
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

    /**
     * Vrátí typ média
     *
     * @return Typ média
     */
    public int getMediaType() {
        return mediaType;
    }

    /**
     * Nastaví, jaky typ media bude podporovaný
     *
     * @param mediaType Typ media
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = (mediaType.getOrdinal());
        notifyPropertyChanged(BR.mediaType);
        setMedia();
    }

    /**
     * Nastaví, jaky typ media bude podporovaný
     *
     * @param mediaType Typ media
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
        notifyPropertyChanged(BR.mediaType);
        setMedia();
    }

    /**
     * Zjistí, zda-li je nastaveno zkoumané medium
     *
     * @param mediaType Typ media
     * @return True, pokud konfigurace ma nastavené dané medium
     */
    public boolean isMediaType(MediaType mediaType) {
        return (this.mediaType & (mediaType.getOrdinal())) != 0;
    }

    /**
     * Nastaví, jaké médium bude použito a jaké nikoliv
     *
     * @param mediaFlag Typ media
     * @param value     True, pokud se bude používat, jinak false
     */
    public void setMediaType(MediaType mediaFlag, boolean value) {
        if (value) {
            this.mediaType |= (mediaFlag.getOrdinal());
        } else {
            this.mediaType &= ~(mediaFlag.getOrdinal());
        }
        notifyPropertyChanged(BR.mediaType);
        setMedia();
    }

    /**
     * Zjistí, zda-li se konfigurace změnila od posledního načtení
     *
     * @return True, pokud se konfigurace změnila, jinak false
     */
    public boolean isChanged() {
        return changed;
    }
    // endregion

}
