package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.ui.MainActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.clearConfigurations;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.createConfiguration;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Test obsahuje metody pro otestování procesu přejmenování stávající konfigurace
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ConfigurationRenameInstrumentTest {

    private static final String CONFIGURATION_NAME = "test";
    private static final String RENAMED_NAME = "renamed";
    private static final String INVALID_CONFIGURATION_NAME = "špatný název";
    private static final int CONFIGURATION_TYPE_NAME = R.string.experiment_erp;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        // Vytvoření konfigurace, na které budu testovat přejmenování
        createConfiguration(getTargetContext(), CONFIGURATION_NAME, CONFIGURATION_TYPE_NAME);

        // Zobrazení action mode baru pro zobrazení možností práce s konfiguracemi
        onView(
                withId(R.id.recyclerViewConfigurations))
                .perform(actionOnItemAtPosition(0, longClick()));

        // Kliknutí na tlačítko pro zobrzení dialogu pro přejmenování konfigurace
        onView(
                withId(R.id.context_rename))
                .perform(click());
    }

    @After
    public void tearDown() throws Exception {
        clearConfigurations(getTargetContext());
        pressBack();
    }

    @Test
    public void baseCheckDialogTest() throws Exception {
        // Kontrola, že se zobrazil dialog pro vytvoření nové konfigurace
        onView(
                withId(R.id.layout_configuration_rename))
                .check(matches(isDisplayed()));

        // Kontrola, že je přítomno textView pro zadání nového názvu konfigurace s původním názvem
        onView(
                withId(R.id.editConfigurationName))
                .check(matches(isDisplayed()))
                .check(matches(hasFocus()))
                .check(matches(withText(CONFIGURATION_NAME)));

        // Kontrola, že není zobrazena hláška o nevalidním vstupu
        onView(
                withText(R.string.error_invalid_name))
                .check(doesNotExist());

        // Kontrola, že počítadlo zobrazuje správný počet znaků
        onView(
                withId(R.id.textinput_counter))
                .check(matches(withText(CONFIGURATION_NAME.length() + " / 32")));

        // Kontrola, že je zobrazeno tlačítko pro zrušení přejmenování konfigurace a je klikatelné
        onView(
                withId(R.id.buttonCancel))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));

        // Kontrola, že je zobrazeno tlačítko pro potvrzení přejmenování konfigurace a není klikatelné
        onView(
                withId(R.id.buttonRename))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));
    }

    /**
     * Pozitivní test, pro otestování správného chování při zadání validního názvu konfigurace
     */
    @Test
    public void positiveDialogTest() throws Exception {
        // Do políčka pro název konfigurace vyplnit název dle parametru
        onView(
                allOf(withId(R.id.editConfigurationName), isDisplayed()))
                .perform(replaceText(RENAMED_NAME), closeSoftKeyboard());

        // Kontrola, že není zobrazena hláška o nevalidním vstupu
        onView(
                withText(R.string.error_invalid_name))
                .check(doesNotExist());

        // Kontrola, že počítadlo zobrazuje správný počet znaků
        onView(
                withId(R.id.textinput_counter))
                .check(matches(withText(RENAMED_NAME.length() + " / 32")));

        // Kontrola, že je tlačítko pro potvrzení přejmenování konfigurace je klikatelné
        onView(
                withId(R.id.buttonRename))
                .check(matches(isEnabled()));
    }

    /**
     * Negativní test, pro otestování správného chování při zadání nevalidního názvu konfigurace
     */
    @Test
    public void negativeTest() throws Exception {
        // Do políčka pro název konfigurace vyplnit název dle parametru
        onView(
                allOf(withId(R.id.editConfigurationName), isDisplayed()))
                .perform(replaceText(INVALID_CONFIGURATION_NAME), closeSoftKeyboard());

        // Kontrola, že není zobrazena hláška o nevalidním vstupu
        onView(
                withText(R.string.error_invalid_name))
                .check(matches(isDisplayed()));

        // Kontrola, že počítadlo zobrazuje správný počet znaků
        onView(
                withId(R.id.textinput_counter))
                .check(matches(withText(INVALID_CONFIGURATION_NAME.length() + " / 32")));

        // Kontrola, že je tlačítko pro potvrzení přejmenování konfigurace není klikatelné
        onView(
                withId(R.id.buttonRename))
                .check(matches(not(isEnabled())));
    }
}
