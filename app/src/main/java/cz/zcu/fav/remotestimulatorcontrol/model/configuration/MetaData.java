package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import android.databinding.BaseObservable;

import java.util.Date;

import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;

/**
 * Třída obsahující metadata k dané konfiguraci
 */
public final class MetaData extends BaseObservable {

    // region Constants
    // Výchozí koncovka souboru
    private static ExtensionType defExtension = ExtensionType.XML;
    // endregion

    //region Variables
    // Použitá koncovka souboru
    public ExtensionType extensionType;
    // Datum změny souboru
    public final Date changed;
    // endregion

    // TODO možná bude obsahovat i informace o použitých obrázcích/zvucích

    // region Public static methods
    /**
     * Vrátí výchozí koncovku souboru
     *
     * @return Výchozí koncovku souboru
     */
    public static ExtensionType getDefaultExtension() {
        return defExtension;
    }

    /**
     * Nastaví výchozí koncovku souboru
     *
     * @param defExtension Výchozí koncovka souboru
     */
    public static void setDefaultExtension(ExtensionType defExtension) {
        MetaData.defExtension = defExtension;
    }
    // endregion

    // region Constructors
    /**
     * Konstruktor třídy {@link MetaData}
     */
    public MetaData() {
        this(defExtension);
    }

    /**
     * Konstruktor třídy {@link MetaData}
     *
     * @param extensionType Typ koncovky souboru
     */
    public MetaData(ExtensionType extensionType) {
        this(extensionType, new Date());
    }

    /**
     * Konstruktor třídy {@link MetaData}
     *
     * @param extensionType Typ koncovky souboru
     * @param changed Datum poslední změny souboru
     */
    public MetaData(ExtensionType extensionType, Date changed) {
        this.extensionType = extensionType;
        this.changed = changed;
    }
    // endregion

}
