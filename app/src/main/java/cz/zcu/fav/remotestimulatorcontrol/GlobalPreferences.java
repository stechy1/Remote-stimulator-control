package cz.zcu.fav.remotestimulatorcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class GlobalPreferences {

    private static final String KEY_EXTENSION = "extension";
    private static final String KEY_SHOW_EXTENSION = "show_extension";

    /**
     * Pomocná metoda, která získá {@link SharedPreferences}
     *
     * @param context Kontext
     * @return {@link SharedPreferences}
     */
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Pomocná metoda, která získá {@link SharedPreferences.Editor}
     *
     * @param context Kontext
     * @return {@link SharedPreferences.Editor}
     */
    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    /**
     * Vrátí výchozí nastavenou koncovku pro konfigurace
     *
     * @param context Kontext
     * @param defValue Výchozí hodnota
     * @return Koncovku konfigurace {@link cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType}
     */
    public static String getDefaultExtension(Context context, String defValue) {
        SharedPreferences preferences = getPreferences(context);
        String result = preferences.getString(KEY_EXTENSION, defValue);

        // "-1" byla vypozorována jako návratová hodnota při nedefinovaném stavu (ještě nebylo nic nastaveno)
        return result.equals("-1") ? defValue : result;
    }

    /**
     * Uloží výchozí koncovku konfigurací
     *
     * @param context Kontext
     * @param value Typ koncovky {@link cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType}
     */
    public static void setDefaultExtension(Context context, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_EXTENSION, value);
        editor.apply();
    }

    /**
     * Zjistí, zda-li se má zobrazit typ souboru, či nikoliv
     *
     * @param context Kontext
     * @param defValue Výchozí hodnota
     * @return True, pokud se má zobrazít typ souboru, jinak false
     */
    public static boolean isExtensionVisible(Context context, boolean defValue) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(KEY_SHOW_EXTENSION, defValue);
    }

    /**
     * Uloží do nastavení, zda-li se má zobrazovat koncovka souboru, či nikoliv
     *
     * @param context Kontext
     * @param value True, pokud se má koncovka zobrazit, jinak false
     */
    public static void setExtensionVisible(Context context, boolean value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_SHOW_EXTENSION, value);
        editor.apply();
    }
}
