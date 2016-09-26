package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Pomocná třída pro práci s {@link SharedPreferences}
 */
public final class ConfigurationSharedPreferences {

    // Název souboru pro sdílené nastavení
    public static final String CONFIGURATION_PREFERENCE = "configuration_preference";

    private static final String KEY_SORTING = "sorting";
    private static final String KEY_BT_SUPPORT = "bt_not_supported_alert_showed";

    /**
     * Pomocná metoda, která získá {@link SharedPreferences}
     *
     * @param context Kontext
     * @return {@link SharedPreferences}
     */
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(CONFIGURATION_PREFERENCE, MODE_PRIVATE);
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
     * Uloží způsob řazení do nastavení
     *
     * @param context Kontext
     * @param value Hodnota
     */
    public static void setSortingFlag(Context context, int value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(KEY_SORTING, value);
        editor.apply();
    }

    /**
     * Načte způsob řazení z nastavení
     *
     * @param context Kontext
     * @param defValue Výchozí hodnota
     * @return Příznak způsobu řazení
     */
    public static int getSortingFlag(Context context, int defValue) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt(KEY_SORTING, defValue);
    }

    /**
     * Uloží, zda-li už byla zobrazena zpráva o nepodporovaném bluetooth
     *
     * @param context Kontext
     * @param value True, pokud už byla zpráva zobrazena, jinak false
     */
    public static void setBTNotSupportedAlertShowed(Context context, boolean value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_BT_SUPPORT, value);
        editor.apply();
    }

    /**
     * Zjistí, zda-li už byla zobrazena zpráva o nepodporovaném bluetooth
     *
     * @param context Kontext
     * @param defValue Výchozí hodnota
     * @return True, pokud už byl dialog zobrazen, jinak false
     */
    public static boolean isBTNotSupportedAlertShowed(Context context, boolean defValue) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(KEY_BT_SUPPORT, defValue);
    }


}
