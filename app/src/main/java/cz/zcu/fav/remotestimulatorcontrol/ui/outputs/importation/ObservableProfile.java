package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation;

import android.databinding.Bindable;
import android.net.Uri;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.BaseModel;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;

import static cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation.ProfileImportActivity.FLAG_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.ui.outputs.importation.ProfileImportActivity.FLAG_PATH;

/**
 * Pomocná konfigurace sloužící pro databingind třídy {@link ProfileImportActivity}
 */
public class ObservableProfile extends BaseModel {

    // region Constants
    // Logovací tag
    private static final String TAG = "ObservableProfile";
    // endregion

    // region Variables
    // Název konfigurace
    @Bindable
    private String name = "";
    // Cesta k souboru
    @Bindable
    private String filePath = "...";
    // endregion

    {
        validityFlag = FLAG_NAME | FLAG_PATH;
    }

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

    // endregion

}
