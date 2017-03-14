package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.util.FileUtils;

/**
 * Třída představující správce profilů
 */
public final class ProfileManager implements ProfileAsyncReader.OnProfileLoadedListener {

    // region Constants
    private static final String TAG = "ProfileManager";

    // region Id jednotlivých zpráv, které se předávají handlerem ven
    public static final int MESSAGE_PROFILES_LOADED = 1;
    public static final int MESSAGE_PROFILE_CREATE = 2;
    public static final int MESSAGE_PROFILE_RENAME = 3;
    public static final int MESSAGE_PROFILE_UPDATE = 4;
    public static final int MESSAGE_PROFILE_DUPLICATE = 5;
    public static final int MESSAGE_PROFILE_PREPARED_TO_DELETE = 6;
    public static final int MESSAGE_PROFILE_UNDO_DELETE = 7;
    public static final int MESSAGE_NAME_EXISTS = 8;
    public static final int MESSAGE_INVALID_NAME = 9;
    public static final int MESSAGE_PROFILE_IMPORT = 10;
    public static final int MESSAGE_SUCCESSFUL = 1;
    public static final int MESSAGE_UNSUCCESSFUL = 2;

    // endregion

    public static final String PROFILE_FOLDER = "profiles";

    private static final String EXTENSION = ".xml";

    // endregion

    // region Variables

    // Pracovní adresář obsahující všechny profily
    private final File mWorkingDirectory;
    // Kolekce profilů
    public final ObservableList<OutputProfile> profiles;
    // Kolekce profilů určených ke smazání
    private final Set<OutputProfile> mProfilesToDelete;
    // Handler posílající zprávy o stavu operace manažeru
    private Handler mHandler;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce profilů
     *
     * @param workingDirectory Adresář obsahující jednotlivé profily
     */
    public ProfileManager(File workingDirectory) {
        mWorkingDirectory = workingDirectory;
        profiles = new ObservableArrayList<>();
        mProfilesToDelete = new HashSet<>();
    }

    // endregion

    // region Public static methods
    public static File buildProfileFilePath(File workingDirectory, OutputProfile profile) {
        File profilesFile = new File(workingDirectory, PROFILE_FOLDER);
        if (!profilesFile.exists()) {
            if (!profilesFile.mkdirs()) {
                Log.e(TAG, "Nemám přístup k souborovému systému");
            }
        }

        return new File(profilesFile, profile.getName() + EXTENSION);
    }
    // endregion

    // region Private methods

    private File buildProfileFilePath(OutputProfile profile) {
        return buildProfileFilePath(mWorkingDirectory, profile);
    }

    /**
     * Uloží konfiguraci
     *
     * @param profile Profil
     * @return True, pokud se podařilo profil uložit, jinak false
     */
    private boolean save(OutputProfile profile) {
        File file = buildProfileFilePath(profile);
        boolean success = true;
        try {
            profile.getHandler().write(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }
    // endregion

    // region Public methods
    public void refresh() {
        Log.d(TAG, "Aktualizuji seznam profilů");
        profiles.clear();
        new ProfileAsyncReader(mWorkingDirectory, profiles, this).execute();
    }

    /**
     * Vytvoří nový profil
     * Kontroluje se, zda-li už název konfigurace někde neexistuje,
     * pokud název existuje, akce se neprovede.
     * Pokud se nepodaří soubor uložit, akce se neprovede.
     * Metoda vytváří tyto zprávy:
     *
     * @param name Název profilu
     * @return Vrátí instanci vytvořeného profilu
     * @see #MESSAGE_NAME_EXISTS pokud název již existuje
     * @see #MESSAGE_PROFILE_CREATE
     * @see #MESSAGE_SUCCESSFUL profil se vytvoříl úspěšně.
     * Další parametr zprávy je index nově přidaného profilu
     * @see #MESSAGE_UNSUCCESSFUL profil se nepodařilo vytvořit.
     */
    public OutputProfile create(String name) {
        for (OutputProfile profile : profiles) {
            if (profile.getName().equals(name)) {
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_NAME_EXISTS).sendToTarget();
                }
                return null;
            }
        }

        OutputProfile profile = new OutputProfile(name);
        profile.fillOutputConfigurations();
        if (!save(profile)) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_CREATE, MESSAGE_SUCCESSFUL).sendToTarget();
            }
            return null;
        }

        profiles.add(profile);
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_PROFILE_CREATE, MESSAGE_SUCCESSFUL, profiles.indexOf(profile)).sendToTarget();
        }

        return profile;
    }

    /**
     * Přidá nový profil do kolekce profilů
     *
     * @param profile Nový profil
     */
    public void add(OutputProfile profile) {
        profiles.add(profile);
    }

    /**
     * Přidá novou konfiguraci na určitý index
     *
     * @param index Index, kam se má konfigurace přidat
     * @param profile Konfigurace
     */
    public void add(int index, OutputProfile profile) {
        profiles.add(index, profile);
    }

    /**
     * Importuje profil
     *
     * @param name              Název konfigurace
     * @param path              Cesta k importované konfiguraci
     */
    public void importt(String name, String path) {
        OutputProfile profile = new OutputProfile(name);

        try {
            profile.getHandler().read(new FileInputStream(path));

            if (!save(profile)) {
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_PROFILE_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
                }
                return;
            }

            profiles.add(profile);
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_IMPORT, MESSAGE_SUCCESSFUL, profiles.indexOf(profile)).sendToTarget();
            }

        } catch (IOException e) {
            Log.e(TAG, "Profil se nepodařilo importovat", e);
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
        }
    }

    /**
     * Vytvoří kopii profilu
     * Metoda vytváří tyto zprávy:
     *
     * @param index Index profilu
     * @param name  Nový název profilu
     * @see #MESSAGE_PROFILE_DUPLICATE
     * @see #MESSAGE_SUCCESSFUL profil se podařilo zduplikovat
     * Další parametr zprávy je index nové konfigurace
     * @see #MESSAGE_UNSUCCESSFUL profil se nepodařilo zduplikovat
     */
    public void duplicate(int index, String name) {
        duplicate(profiles.get(index), name);
    }

    /**
     * Vytvoří kopii profilu
     * Metoda vytváří tyto zprávy:
     *
     * @param profile Profil, který se má zduplikovat
     * @param name  Nový název profilu
     * @see #MESSAGE_PROFILE_DUPLICATE
     * @see #MESSAGE_SUCCESSFUL profil se podařilo zduplikovat
     * Další parametr zprávy je index nové konfigurace
     * @see #MESSAGE_UNSUCCESSFUL profil se nepodařilo zduplikovat
     */
    public void duplicate(OutputProfile profile, String name) {
        for (OutputProfile aProfile : profiles) {
            if (aProfile.getName().equals(name)) {
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_NAME_EXISTS).sendToTarget();
                }
                return;
            }
        }

        OutputProfile duplicated = profile.duplicate(name);

        File originalFile = buildProfileFilePath(profile);
        File duplicateFile = buildProfileFilePath(duplicated);

        try {
            FileUtils.copy(originalFile, duplicateFile);

            add(duplicated);
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_DUPLICATE, MESSAGE_SUCCESSFUL, profiles.indexOf(duplicated)).sendToTarget();
            }

        } catch (IOException e) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_DUPLICATE, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
        }
    }

    /**
     * Přejmenuje profil
     * Metoda vytváří tyto zprávy:
     *
     * @param index   Index konfigurace
     * @param newName Nový název
     * @see #MESSAGE_PROFILE_RENAME
     * @see #MESSAGE_SUCCESSFUL profil se přejmenoval úspěšně
     * Další parametr zprávy je index přejmenovaného profilu
     * @see #MESSAGE_UNSUCCESSFUL profil se nepodařilo přejmenovat
     */
    public void rename(int index, String newName) {
        rename(profiles.get(index), newName);
    }

    /**
     * Přejmenuje profil
     * Metoda vytváří tyto zprávy:
     *
     * @param profile Profil, který se má přejmenovat
     * @param newName Nový název
     * @see #MESSAGE_PROFILE_RENAME
     * @see #MESSAGE_SUCCESSFUL profil se přejmenoval úspěšně
     * Další parametr zprávy je index přejmenovaného profilu
     * @see #MESSAGE_UNSUCCESSFUL profil se nepodařilo přejmenovat
     */
    public void rename(OutputProfile profile, String newName) {
        String oldName = profile.getName();
        File oldConfigPath = buildProfileFilePath(profile);

        if (!AConfiguration.isNameValid(newName)) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_INVALID_NAME).sendToTarget();
            }
            return;
        }

        profile.setName(newName);

        File newConfigPath = buildProfileFilePath(profile);

        if (!oldConfigPath.renameTo(newConfigPath)) {
            profile.setName(oldName);

            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_RENAME, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
        } else {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_PROFILE_RENAME, MESSAGE_SUCCESSFUL, profiles.indexOf(profile)).sendToTarget();
            }
        }
    }

    /**
     * Aktualizuje profil na zadaném indexu
     *
     * @param index Index profilu
     */
    public void update(int index) {
        update(profiles.get(index));
    }

    /**
     * Aktualizuje vybraný profil
     *
     * @param profile Profil, který se má aktualizovat
     */
    private void update(OutputProfile profile) {
        // TODO implementovat aktualizaci profilu
    }

    /**
     * Připraví vybrané profily ke smazání
     *
     * @param selectedItems Kolekce indexů profilů ke smazání
     */
    public void prepareToDelete(List<Integer> selectedItems) {
        if (selectedItems.size() == 0) {
            return;
        }

        // Potřebuji odebírat profily od nejvyššího indexu po nejnižší,
        // abych zabránil NullPointerExceptionu
        if (selectedItems.size() > 1) {
            Collections.sort(selectedItems);
            Collections.reverse(selectedItems);
        }

        for (Integer selectedItem : selectedItems) {
            OutputProfile profile = profiles.get(selectedItem);
            mProfilesToDelete.add(profile);
            profiles.remove(selectedItem.intValue());
        }

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_PROFILE_PREPARED_TO_DELETE, selectedItems).sendToTarget();
        }

    }

    /**
     * Zruší akci mazání profilů
     * Vrátí smazané profily zpět do hlavní kolekce
     */
    public void undoDelete() {
        for (OutputProfile profile : mProfilesToDelete) {
            profiles.add(profile);
        }

        mProfilesToDelete.clear();

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_PROFILE_UNDO_DELETE).sendToTarget();
        }
    }

    /**
     * Potvrzení smazání vybraných konfigurací
     */
    public void confirmDelete() {
        for (OutputProfile configuration : mProfilesToDelete) {
            File configFile = buildProfileFilePath(configuration);
            if (!configFile.delete()) {
                Log.e(TAG, "Nepodařilo se smazat profil: " + configFile.getName());
            }
        }
    }

    @Override
    public void onLoaded(int successfuly, int unsuccessfuly) {
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_PROFILES_LOADED, successfuly, unsuccessfuly).sendToTarget();
        }
    }

    /**
     * Nastaví handler, který reaguje na přijaté zprávy
     *
     * @param handler Handler
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }
    // endregion

    // region Getters & Setters

    // endregion
}
