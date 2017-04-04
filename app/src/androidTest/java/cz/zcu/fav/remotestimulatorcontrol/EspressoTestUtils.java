package cz.zcu.fav.remotestimulatorcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.ConfigurationSharedPreferences;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Pomocná knihovní třída, která obsahuje užitečné matchery, které se používají
 * na více místech
 */
public final class EspressoTestUtils {

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    public static ViewInteraction matchToolbarSubtitle(CharSequence subtitle) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarSubtitle(is(subtitle))));
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    private static Matcher<Object> withToolbarSubtitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getSubtitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar subtitle: ");
                textMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    /**
     * Vymaže všechny dříve uložené konfigurace
     *
     * @param context {@link Context}
     */
    public static void clearConfigurations(Context context) {
        File workingDirectory = context.getFilesDir();

        for (ConfigurationType type : ConfigurationType.values()) {
            // Vytvoří novou referenci na třídu File obsahující složku, ve které jsou konfigurace daného typu
            File[] files = new File(workingDirectory, type.toString().toLowerCase()).listFiles();
            // Pokud žádné konfigurace daného typu neexistují, tak se poračuje dál a nic se nenačítá
            if (files == null) {
                continue;
            }

            for (File file : files) {
                if (file.delete()) {
                    System.out.println("Mažu soubor: " + file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Vymaže sdílené nastavení aplikace
     *
     * @param context Kontext
     */
    public static void clearSharedPrefs(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        SharedPreferences prefs = context.getSharedPreferences(
                ConfigurationSharedPreferences.CONFIGURATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static void openDrawer() {
        onView(withContentDescription(R.string.nav_drawer_open))
                .perform(click());
    }

    /**
     * Vytvoří novou konfigurace
     *
     * @param context {@link Context}
     * @param configurationName Název konfigurace
     * @param configurationTypeNameResource Typ konfigurace
     */
    public static void createConfiguration(Context context, String configurationName, @StringRes int configurationTypeNameResource) throws Exception {
        // Kliknu na FAB pro otevření dialogu pro novou konfiguraci
        onView(
                allOf(
                        withId(R.id.fab_new_configuration),
                        isDisplayed()))
                .perform(click());

        // Do pole s názvem napíšu název konfigurace
        onView(
                allOf(
                        withId(R.id.editConfigurationName),
                        isDisplayed()))
                .perform(replaceText(configurationName), closeSoftKeyboard());

        // Kliknu na výběr z typů konfigurací
        onView(
                allOf(
                        withId(android.R.id.text1),
                        withText(R.string.experiment_undefinded),
                        isDisplayed()))
                .perform(click());

        // Pokud je potřeba, tak se posune celý obsah listu až na začátek
        onView(
                withChild(
                        anyOf(
                                withText(context.getString(R.string.experiment_cvep)),
                                withText(context.getString(R.string.experiment_fvep)))))
                .perform(swipeDown());

        // Vyberu správný typ konfigurace
        final String confTypeName = getInstrumentation().getTargetContext().getString(configurationTypeNameResource);
        onView(
                allOf(
                        withId(android.R.id.text1),
                        withText(confTypeName)))
                .perform(click());

        // Kliknu na tlačítko vytvořit
        onView(
                allOf(
                        withText(R.string.configuration_config_create),
                        isDisplayed()))
                .perform(click());
    }
}
