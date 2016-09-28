package cz.zcu.fav.remotestimulatorcontrol.model;

import android.databinding.BaseObservable;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationComparator;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.util.ObservableSortedList;

/**
 * Třída představující správce konfigurací
 * Pomocí manageru se spravují jednotlivé konfigurace
 * - vytváření
 * - import
 * - duplikace
 * - aktualizace
 * - mazání
 */
public final class ConfigurationManager extends BaseObservable implements ConfigurationAsyncReader.OnConfigurationLoadedListener {

    // region Constants

    // region ID jednotlivých zpráv, která se předávají handlerem ven
    public static final int MESSAGE_CONFIGURATIONS_LOADED = 1;
    public static final int MESSAGE_CONFIGURATION_CREATE = 2;
    public static final int MESSAGE_CONFIGURATION_RENAME = 3;
    public static final int MESSAGE_CONFIGURATION_UPDATE = 4;
    public static final int MESSAGE_CONFIGURATION_DUPLICATE = 5;
    public static final int MESSAGE_CONFIGURATION_PREPARED_TO_DELETE = 6;
    public static final int MESSAGE_CONFIGURATION_UNDO_DELETE = 7;
    public static final int MESSAGE_NAME_EXISTS = 8;
    public static final int MESSAGE_INVALID_NAME = 9;
    public static final int MESSAGE_CONFIGURATION_IMPORT = 10;
    public static final int MESSAGE_SUCCESSFUL = 1;
    public static final int MESSAGE_UNSUCCESSFUL = 2;

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ConfigurationManager";
    // endregion

    private static final String MEDIA_FOLDER = "media";

    // endregion

    // region Variables

    // Kolekce konfigurací
    public final ObservableSortedList<AConfiguration> configurations;

    // Složka s pracovním adresářem aplikace
    private final File mWorkingDirectory;
    // Kolekce konfigurací určených ke smazání
    private final Set<AConfiguration> mConfigurationsToDelete;

    // Handler posílající zprávy o stavu operace manažeru
    private Handler mHandler;
    // Komparátor konfigurací
    private Comparator<AConfiguration> mConfigurationComparator;
    // endregion

    // region Constructors

    /**
     * Základní konstruktor pro správce konfigurací
     *
     * @param workingDirectory Pracovní adresář
     */
    public ConfigurationManager(File workingDirectory) {
        this(workingDirectory, ConfigurationComparator.TYPE_COMPARATOR);
    }

    /**
     * Konstruktor rozšířený o komparátor
     *
     * @param workingDirectory Pracovní adresář
     * @param comparator       Komparátor, podle kterého budou konfigurace seřazeny
     */
    public ConfigurationManager(File workingDirectory, Comparator<AConfiguration> comparator) {
        mWorkingDirectory = workingDirectory;
        mConfigurationComparator = comparator;
        configurations = new ObservableSortedList<>(AConfiguration.class, new SortedCallback());
        mConfigurationsToDelete = new HashSet<>();
    }
    // endregion

    // region Public static methods

    /**
     * Sestaví cestu ke konfiguraci a složce s externími médii
     *
     * @param workingDirectory Výchozí pracovní adresář
     * @param configuration Konfigurace, pro kterou je soubor určen
     * @return Pár souborů, kde první soubor ukazuje na samotnou konfiguraci a druhý představuje složku s externími médii
     */
    public static Pair<File, File> buildConfigurationFilePath(File workingDirectory, AConfiguration configuration) {
        // Získání složky podle typu konfigurace.  Path: /workingDirectory/configurationType
        final File confTypeFolder = new File(workingDirectory, configuration.getConfigurationType().name().toLowerCase());
        if (!confTypeFolder.exists()) {
            if (!confTypeFolder.mkdirs()) {
                Log.e(TAG, "Nemám přístup k souborovému systému");
            }
        }

        // Získání složky s konkrétní konfigurací. Path: /workingDirectory/configurationType/configurationName
        final File confFolder = new File(confTypeFolder, configuration.getName());
        if (!confFolder.exists()) {
            if (!confFolder.mkdirs()) {
                Log.e(TAG, "Nemám přístup k souborovému systému");
            }
        }

        // Získání složky s externími médii v konfiguraci. Path: /workingDirectory/configurationType/configurationName/media
        final File mediaFolder = new File(confFolder, MEDIA_FOLDER);
        if (!mediaFolder.exists()) {
            if (!mediaFolder.mkdirs()) {
                Log.e(TAG, "Nemám přístup k souborovému systému");
            }
        }

        // Získání souboru s konfigurací. Path: /workingDirectory/configurationType/configurationName/configurationName.extension
        final File configurationFile = new File(confFolder, configuration.getName() + configuration.metaData.extensionType);

        return new Pair<>(configurationFile, mediaFolder);
    }

    // endregion

    // region Private methods

    /**
     * Sestaví cestu k souboru s konfigurací
     *
     * @param configuration Konfigurace, pro kterou je soubor určen
     * @return Soubor s konfigurací na disku
     */
    private File buildConfigurationFilePath(final AConfiguration configuration) {
        return buildConfigurationFilePath(mWorkingDirectory, configuration).first;
    }

    /**
     * Uloží konfiguraci
     *
     * @param configuration Konfigurace
     * @return True, pokud se podařilo konfiguraci uložit, jinak false
     */
    private boolean save(AConfiguration configuration) {
        File file = buildConfigurationFilePath(configuration);
        boolean success = true;
        try {
            configuration.getHandler().write(new FileOutputStream(file));
        } catch (IOException e) {
            Log.e(TAG, "Konfiguraci se nepodařilo uložit", e);
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    /**
     * Znovu načte všechny konfigurace
     */
    public void refresh() {
        Log.d(TAG, "Aktualizuji seznam konfiguraci");
        configurations.beginBatchedUpdates();
        configurations.clear();
        new ConfigurationAsyncReader(mWorkingDirectory, configurations, this).execute(
                ConfigurationType.ERP,
                ConfigurationType.FVEP,
                ConfigurationType.TVEP,
                ConfigurationType.CVEP,
                ConfigurationType.REA
        );
    }
    // endregion

    // region Public methods

    /**
     * Vytvoří novou konfiguraci
     * Kontroluje se, zda-li už název konfigurace někde neexistuje,
     * pokud název existuje, akce se neprovede.
     * Pokud se nepodaří soubor uložit, akce se neprovede.
     * Metoda vytváří tyto zprávy:
     *
     * @param name Název konfigurace
     * @param type Typ konfigurace
     * @return Vrátí instanci vytvořené konfigurace
     * @see #MESSAGE_NAME_EXISTS pokud název již existuje
     * @see #MESSAGE_CONFIGURATION_CREATE
     * @see #MESSAGE_SUCCESSFUL konfigurace se vytvořila úspěšně.
     * Další parametr zprávy je index nově přidané konfigurace
     * @see #MESSAGE_UNSUCCESSFUL konfiguraci se nepodařilo vytvořit.
     */
    public AConfiguration create(String name, ConfigurationType type) {
        for (AConfiguration configuration : configurations) {
            if (configuration.getName().equals(name)) {
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_NAME_EXISTS).sendToTarget();
                }
                return null;
            }
        }

        AConfiguration configuration = ConfigurationHelper.from(name, type);
        if (!save(configuration)) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_CREATE, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
            return null;
        }

        configurations.add(configuration);
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_CONFIGURATION_CREATE, MESSAGE_SUCCESSFUL, configurations.indexOf(configuration)).sendToTarget();
        }

        return configuration;
    }

    /**
     * Přidá novou konfiguraci do kolekce
     *
     * @param configuration Konfigurace
     */
    public void add(AConfiguration configuration) {
        configurations.add(configuration);
    }

    /**
     * Přidá novou konfiguraci na určitý index
     *
     * @param index         Index, kam se má konfigurace přidat
     * @param configuration Konfigurace
     */
    public void add(int index, AConfiguration configuration) {
        configurations.add(index, configuration);
    }

    /**
     * Importuje konfiguraci
     *
     * @param name              Název konfigurace
     * @param path              Cesta k importované konfiguraci
     * @param configurationType Typ importované konfigurace
     * @param extensionType     Typ souboru s konfigurací
     */
    public void importt(String name, String path, ConfigurationType configurationType, ExtensionType extensionType) {
        AConfiguration configuration = ConfigurationHelper.from(name, configurationType);
        configuration.metaData.extensionType = extensionType;

        try {
            configuration.getHandler().read(new FileInputStream(path));

            if (!save(configuration)) {
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_CONFIGURATION_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
                }
                return;
            }

            configurations.add(configuration);
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_IMPORT, MESSAGE_SUCCESSFUL, configurations.indexOf(configuration)).sendToTarget();
            }

        } catch (IOException e) {
            Log.e(TAG, "Konfiguraci se nepodařilo importovat", e);
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_IMPORT, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
        }
    }

    /**
     * Vytvoří kopii konfigurace
     * Metoda vytváří tyto zprávy:
     *
     * @param index Index konfigurace
     * @param name  Nový název konfigurace
     * @see #MESSAGE_CONFIGURATION_DUPLICATE
     * @see #MESSAGE_SUCCESSFUL konfiguraci se podařilo zduplikovat
     * Další parametr zprávy je index nové konfigurace
     * @see #MESSAGE_UNSUCCESSFUL konfiguraci se nepodařilo zduplikovat
     */
    public void duplicate(int index, String name) {
        duplicate(configurations.get(index), name);
    }

    /**
     * Vytvoří kopii konfigurace
     * Metoda vytváří tyto zprávy:
     *
     * @param configuration Konfigurace, která se má zduplikovat
     * @param name          Nový název konfigurace
     * @see #MESSAGE_CONFIGURATION_DUPLICATE
     * @see #MESSAGE_SUCCESSFUL konfiguraci se podařilo zduplikovat
     * Další parametr zprávy je index nové konfigurace
     * @see #MESSAGE_UNSUCCESSFUL konfiguraci se nepodařilo zduplikovat
     */
    public void duplicate(AConfiguration configuration, String name) {
        for (AConfiguration aConfiguration : configurations) {
            if (aConfiguration.getName().equals(name)) {
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_NAME_EXISTS).sendToTarget();
                }
                return;
            }
        }

        AConfiguration duplicated = configuration.duplicate(name);

        if (!save(duplicated)) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_DUPLICATE, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
            return;
        }

        add(duplicated);
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_CONFIGURATION_DUPLICATE, MESSAGE_SUCCESSFUL, configurations.indexOf(duplicated)).sendToTarget();
        }
    }

    /**
     * Přejmenuje konfiguraci
     * Metoda vytváří tyto zprávy:
     *
     * @param index   Index konfigurace
     * @param newName Nový název
     * @see #MESSAGE_CONFIGURATION_RENAME
     * @see #MESSAGE_SUCCESSFUL konfigurace se přejmenovala úspěšně
     * Další parametr zprávy je index přejmenované konfigurace
     * @see #MESSAGE_UNSUCCESSFUL konfiguraci se nepodařilo přejmenovat
     */
    public void rename(int index, String newName) {
        rename(configurations.get(index), newName);
    }

    /**
     * Přejmenuje konfiguraci
     * Metoda vytváří tyto zprávy:
     *
     * @param configuration Konfigurace která se má přejmenovat
     * @param newName       Nový název
     * @see #MESSAGE_CONFIGURATION_RENAME
     * @see #MESSAGE_SUCCESSFUL konfigurace se přejmenovala úspěšně
     * Další parametr zprávy je index přejmenované konfigurace
     * @see #MESSAGE_UNSUCCESSFUL konfiguraci se nepodařilo přejmenovat
     */
    public void rename(AConfiguration configuration, String newName) {
        String oldName = configuration.getName();
        File oldConfigPath = buildConfigurationFilePath(configuration);

        if (!AConfiguration.isNameValid(newName)) {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_INVALID_NAME).sendToTarget();
            }
            return;
        }

        configuration.setName(newName);

        File newConfigPath = buildConfigurationFilePath(configuration);

        if (!oldConfigPath.renameTo(newConfigPath)) {
            configuration.setName(oldName);

            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_RENAME, MESSAGE_UNSUCCESSFUL).sendToTarget();
            }
        } else {
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_RENAME, MESSAGE_SUCCESSFUL, configurations.indexOf(configuration)).sendToTarget();
            }
        }
    }

    /**
     * Aktualizuje konfigurace na zadaném indexu
     *
     * @param index Index konfigurace
     */
    public void update(int index) {
        update(configurations.get(index));
    }

    /**
     * Aktualizuje konfiguraci. Vynutí nové čtení z disku
     *
     * @param configuration Konfigurace
     */
    public void update(AConfiguration configuration) {
        File file = buildConfigurationFilePath(configuration);
        try {
            configuration.getHandler().read(new FileInputStream(file));
            if (mHandler != null) {
                mHandler.obtainMessage(MESSAGE_CONFIGURATION_UPDATE, configurations.indexOf(configuration)).sendToTarget();
            }
        } catch (IOException e) {
            Log.e(TAG, "Konfiguraci se nepodařilo aktualizovat", e);
        }
    }

    /**
     * Připraví vybrané konfigurace ke smazání
     *
     * @param selectedItems Kolekce indexů konfigurací ke smazání
     */
    public void prepareToDelete(List<Integer> selectedItems) {
        if (selectedItems.size() == 0) {
            return;
        }

        // Potřebuji odebírat konfigurace od nejvyššího indexu po nejnižší,
        // abych zabránil NullPointerExceptionu
        if (selectedItems.size() > 1) {
            Collections.sort(selectedItems);
            Collections.reverse(selectedItems);
        }

        configurations.beginBatchedUpdates();
        for (Integer selectedItem : selectedItems) {
            AConfiguration configuration = configurations.get(selectedItem);
            mConfigurationsToDelete.add(configuration);
            configurations.remove(selectedItem.intValue());
        }
        configurations.endBatchedUpdates();

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_CONFIGURATION_PREPARED_TO_DELETE, selectedItems).sendToTarget();
        }

    }

    /**
     * Zruší akci mazání konfigurací
     * Vrátí smazané konfigurace zpět do hlavní kolekce
     */
    public void undoDelete() {

        configurations.beginBatchedUpdates();
        for (AConfiguration configuration : mConfigurationsToDelete)
            configurations.add(configuration);
        configurations.endBatchedUpdates();

        mConfigurationsToDelete.clear();

        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_CONFIGURATION_UNDO_DELETE).sendToTarget();
        }
    }

    /**
     * Potvrzení smazání vybraných konfigurací
     */
    public void confirmDelete() {
        for (AConfiguration configuration : mConfigurationsToDelete) {
            File configFile = buildConfigurationFilePath(configuration);
            if (!configFile.delete()) {
                Log.d(TAG, "Nepodarilo se smazat konfiguraci: " + configFile.getName());
            }
        }
    }

    /**
     * Zavolá se po načtení všech konfigurací
     * Metoda vytváří tyto zprávy:
     *
     * @see #MESSAGE_CONFIGURATIONS_LOADED
     * int successfuly - počet úspěšně načtených konfigurací
     * int unsuccessfuly - počet neúspěšně načtených konfigurací
     */
    @Override
    public void onLoaded(int successfuly, int unsuccessfuly) {
        configurations.endBatchedUpdates();
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_CONFIGURATIONS_LOADED, successfuly, unsuccessfuly).sendToTarget();
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

    /**
     * Nastaví komparátor
     *
     * @param comparator Komparátor @see ConfigurationComparator
     *                   {@link ConfigurationComparator#NAME_COMPARATOR }
     *                   {@link ConfigurationComparator#TYPE_COMPARATOR }
     *                   {@link ConfigurationComparator#MEDIA_COMPARATOR}
     */
    public void setConfigurationComparator(Comparator<AConfiguration> comparator) {
        this.mConfigurationComparator = comparator;

    }

    /**
     * Callback pro sorted set
     */
    private class SortedCallback implements ObservableSortedList.Callback<AConfiguration> {

        @Override
        public int compare(AConfiguration o1, AConfiguration o2) {
            return mConfigurationComparator.compare(o1, o2);
        }

        @Override
        public boolean areItemsTheSame(AConfiguration item1, AConfiguration item2) {
            return item1 == item2;
        }

        @Override
        public boolean areContentsTheSame(AConfiguration oldItem, AConfiguration newItem) {
            return false;
        }
    }
    // endregion
}
