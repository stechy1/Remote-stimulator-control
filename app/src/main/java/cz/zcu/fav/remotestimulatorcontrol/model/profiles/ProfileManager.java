package cz.zcu.fav.remotestimulatorcontrol.model.profiles;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;

import java.io.File;

/**
 * Třída představující správce profilů
 */
public final class ProfileManager implements ProfileAsyncReader.OnProfileLoadedListener {
    private static final String TAG = "ProfileManager";

    // region Constants

    // endregion

    // region Variables

    // Pracovní adresář obsahující všechny profily
    private final File mWorkingDirectory;
    // Kolekce profilů
    public final ObservableList<OutputProfile> profiles;

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
    }

    // endregion

    // region Private methods

    // endregion

    // region Public methods
    public void refresh() {
        Log.d(TAG, "Aktualizuji seznam profilů");
        profiles.clear();
        new ProfileAsyncReader(mWorkingDirectory, profiles, this).execute();
    }

    @Override
    public void onLoaded(int successfuly, int unsuccessfuly) {

    }
    // endregion

    // region Getters & Setters

    // endregion
}
