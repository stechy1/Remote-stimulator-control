package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;
import cz.zcu.fav.remotestimulatorcontrol.util.EnumUtil;

import static cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration.isNameValid;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation.ConfigurationImportActivity.FLAG_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation.ConfigurationImportActivity.FLAG_PATH;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.importation.ConfigurationImportActivity.FLAG_TYPE;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ConfigurationImportActivity}
 */
public class ObservableConfiguration extends BaseObservable implements IValidate {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ObservableConfiguration";

    @Bindable
    private int validityFlag = FLAG_NAME + FLAG_TYPE;
    @Bindable
    private boolean valid;
    @Bindable
    private String name = "";
    @Bindable
    private ExtensionType extensionType;
    @Bindable
    private ConfigurationType configurationType = ConfigurationType.UNDEFINED;
    @Bindable
    private String filePath = "...";
    @Bindable
    boolean changed = false;

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
     * @param name Název konfigurace
     */
    public void setName(String name) {
        this.name = name;
        changed = true;
        notifyPropertyChanged(BR.name);

        if (!isNameValid(name)) {
            setValid(false);
            setValidityFlag(FLAG_NAME, true);
        } else {
            setValidityFlag(FLAG_NAME, false);
        }
    }

    /**
     * Vrátí koncovku souboru s konfigurací
     *
     * @return {@link ExtensionType}
     */
    public ExtensionType getExtensionType() {
        return extensionType;
    }

    /**
     * Nastaví koncovku souboru s konfigurací
     *
     * @param extensionType {@link ExtensionType}
     */
    public void setExtensionType(ExtensionType extensionType) {
        this.extensionType = extensionType;
        notifyPropertyChanged(BR.extensionType);
    }

    /**
     * Vrátí typ konfigurace
     *
     * @return {@link ConfigurationType}
     */
    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    /**
     * Nastaví typ konfigurace
     *
     * @param configurationType {@link ConfigurationType}
     */
    public void setConfigurationType(ConfigurationType configurationType) {
        this.configurationType = configurationType;
        notifyPropertyChanged(BR.configurationType);

        if (configurationType == ConfigurationType.UNDEFINED) {
            setValid(false);
            setValidityFlag(FLAG_TYPE, true);
        } else {
            setValidityFlag(FLAG_TYPE, false);
        }
    }

    /**
     * Vrátí cestu k vybranému souboru
     *
     * @return Cestu k vybranému souboru
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Nastavení cesty k souboru s konfigurací
     *
     * @param fileURI Cesta k souboru
     */
    public void setFileURI(Uri fileURI) {
        setFilePath(fileURI.getEncodedPath());
        File file = new File(this.filePath);
        String name = file.getName();
        String extension = name.substring(name.indexOf(".") + 1);

        ExtensionType extensionType = EnumUtil.lookup(ExtensionType.class, extension);

        // Pokud uživatel vybere soubor s nepodporovaným typem (něco jiného než [xml, json, csv]
        if (extensionType == null) {
            return;
        }

        // Získání přesného názvu souboru bez koncovky
        name = name.replace(extensionType.toString(), "");

        setExtensionType(extensionType);
        setName(name);
    }

    /**
     * Nastaví cestu k vybranému souboru
     *
     * @param filePath Cesta k vybranému souboru
     */
    private void setFilePath(String filePath) {
        this.filePath = filePath;
        notifyPropertyChanged(BR.filePath);

        if (filePath.isEmpty() || filePath.equals("...")) {
            setValid(false);
            setValidityFlag(FLAG_PATH, true);
        } else {
            setValidityFlag(FLAG_PATH, false);
        }
    }

    /**
     * Nastaví validační příznak
     *
     * @param validityFlag Validační příznak
     */
    public void setValidityFlag(int validityFlag) {
        this.validityFlag = validityFlag;
        notifyPropertyChanged(BR.validityFlag);
    }

    /**
     * Vrátí true, pokud se změnil vnitřní stav objektu, jinak false
     *
     * @return True, pokud se změnil vnitřní stav objektu, jinak false
     */
    public boolean isChanged() {
        return changed;
    }

    @Override
    public int getValidityFlag() {
        return changed ? validityFlag : 0;
    }

    @Override
    public boolean isFlagValid(int flag) {
        return !((validityFlag & flag) == flag);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
        notifyPropertyChanged(BR.valid);
    }
}
