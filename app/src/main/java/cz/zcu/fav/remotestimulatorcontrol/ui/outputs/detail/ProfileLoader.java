package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.detail;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputProfile;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.ProfileManager;


public class ProfileLoader extends AsyncTask<File, Void, Void> {

    // region Constants
    // Logovací tag
    private static final String TAG = "ProfileLoader";
    // endregion

    // region Variables
    private final OutputProfile mProfile;
    private final OnProfileLoaded mOnLoaded;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový loader konfigurace
     *
     * @param profile Profil, která se má načíst
     * @param onLoaded Handler, který se zavolá po úspěšném načtení profilu
     */
    ProfileLoader(OutputProfile profile, OnProfileLoaded onLoaded) {
        this.mProfile = profile;
        this.mOnLoaded = onLoaded;
    }
    // endregion

    @Override
    protected Void doInBackground(File... params) {
        try {
            IOHandler handler = mProfile.getHandler();
            File file = ProfileManager.buildProfileFilePath(params[0], mProfile);

            handler.read(new FileInputStream(file));

        } catch (IOException e) {
            Log.e(TAG, "Nepodařilo se načíst profil: " + mProfile.getName(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mOnLoaded != null) {
            mOnLoaded.onProfileLoaded();
        }
    }

    /**
     * Rozhraní definující kontrakt pro zachycení načtení konfigurace
     */
    interface OnProfileLoaded {

        /**
         * Zavolá se po načtení konfigurace z disku
         */
        void onProfileLoaded();

    }
}
