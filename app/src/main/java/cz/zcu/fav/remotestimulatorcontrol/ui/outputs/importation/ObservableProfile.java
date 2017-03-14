package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;

import static cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation.ProfileImportActivity.FLAG_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation.ProfileImportActivity.FLAG_PATH;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ProfileImportActivity}
 */
public class ObservableProfile extends BaseObservable implements IValidate {

    // region Constants
    // Logovací tag
    private static final String TAG = "ObservableProfile";
    // endregion

    // region Variables
    // Validita konfigurace - 0 = validní
    @Bindable
    private int validityFlag = FLAG_NAME | FLAG_PATH;
    // Příznak validity konfigurace. True, pokud je validní, jinak false
    @Bindable
    private boolean valid;
    // Název konfigurace
    @Bindable
    private String name = "";
    // Cesta k souboru
    @Bindable
    private String filePath = "...";
    // Příznak indikující, zda-li byla změněna interní datová struktura konfigurace
    @Bindable
    boolean changed = false;
    // endregion

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

    // region Getters & Setters
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

        if (!AConfiguration.isNameValid(name)) {
            setValid(false);
            setValidityFlag(FLAG_NAME, true);
        } else {
            setValidityFlag(FLAG_NAME, false);
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

        // Získání přesného názvu souboru bez koncovky
        name = name.replace("." + extension, "");

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
    // endregion

}
