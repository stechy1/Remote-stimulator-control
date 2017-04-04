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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;

/**
 * Test obsahuje metody pro otestování procesu vytváření nových konfigurací
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ConfigurationFractoryInstrumentTest {

    private static final int CONFIGURATION_TYPE = R.string.experiment_erp;

    private static final String POSITIVE_TEST_NAME = "test";
    private static final String NEGATIVE_TEST_NAME = "špatný název";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        // Kliknout na FAB pro vytvoření nové konfigurace
        onView(
                allOf(
                        withId(R.id.fab_new_configuration),
                        isDisplayed()))
                .perform(click());

        // Pro jistotu schováme klávesnici už při startu testu
        onView(
                withId(R.id.editConfigurationName))
                .perform(closeSoftKeyboard());
    }

    @After
    public void tearDown() throws Exception {
        pressBack();
    }

    /**
     * Test na správné zobrazený položek ve view
     */
    @Test
    public void emptyDialogTest() throws Exception {
        // Kontrola, že dialog neobsahuje žádné výchozí jméno
        onView(
                withId(R.id.editConfigurationName))
                .check(matches(hasFocus()))
                .check(matches(withText("")));

        // Kontrola, že počítadlo zobrazuje nulový počet znaků
        onView(
                withId(R.id.textinput_counter))
                .check(matches(withText("0 / 32")));

        // Kontrola, že není zobrazen text s chybovou hláškou
        onView(
                withId(R.id.textinput_error))
                .check(doesNotExist());

        // Kontrola, že je přítomné tlačítko na potvrzení vytvoření
        // nové konfigurace a že na toto tlačítko nejde kliknout
        onView(
                withText(R.string.configuration_config_create))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));
    }

    /**
     * Pozitivní test. Do dialogu se zadají validní parametry
     */
    @Test
    public void positiveDialogTest() throws Exception {
        // Zapíšeme do editTextu validní název konfigurace
        onView(
                withId(R.id.editConfigurationName))
                .perform(replaceText(POSITIVE_TEST_NAME));

        // Kontrola, že počítadlo zobrazuje správný počet znaků
        onView(
                withId(R.id.textinput_counter))
                .check(matches(withText(POSITIVE_TEST_NAME.length() + " / 32")));

        // Kontrola, že není zobrazen text s chybovou hláškou
        onView(
                withId(R.id.textinput_error))
                .check(doesNotExist());

        // Kontrola, že je přítomné tlačítko na potvrzení vytvoření
        // nové konfigurace a že na toto tlačítko nejde kliknout
        onView(
                withText(R.string.configuration_config_create))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

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
                                withText(getTargetContext().getString(R.string.experiment_cvep)),
                                withText(getTargetContext().getString(R.string.experiment_fvep)))))
                .perform(swipeDown());

        // Vyberu typ konfigurace
        onView(
                allOf(
                        withId(android.R.id.text1),
                        withText(CONFIGURATION_TYPE)))
                .perform(click());

        // Kontrola, že je přítomné tlačítko na potvrzení vytvoření
        // nové konfigurace a že na toto tlačítko už jde kliknout
        onView(
                withText(R.string.configuration_config_create))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
    }

    /**
     * Negativní test, který zkontroluje, zobrazení chybové hlášky
     * při zadání nevalidníno názvu konfigurace
     */
    @Test
    public void negativeDialogTest1() throws Exception {
        // Zapíšeme do editTextu nevalidní název konfigurace
        onView(
                withId(R.id.editConfigurationName))
                .perform(replaceText(NEGATIVE_TEST_NAME));

        // Kontrola, že počítadlo zobrazuje správný počet znaků
        onView(
                withId(R.id.textinput_counter))
                .check(matches(withText(NEGATIVE_TEST_NAME.length() + " / 32")));

        // Kontrola, že je zobrazen text s chybovou hláškou
        onView(
                withId(R.id.textinput_error))
                .check(matches(isDisplayed()));

        // Kontrola, že je přítomné tlačítko na potvrzení vytvoření
        // nové konfigurace a že na toto tlačítko nejde kliknout
        onView(
                withText(R.string.configuration_config_create))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));
    }

    /**
     * Negativní test, který zkontroluje, že když se jako první
     * vybere typ konfigurace, tak chybová hláška
     * zůstane nezobrazena
     */
    @Test
    public void negativeDialogTest2() throws Exception {
        // Kontrola, že není zobrazen text s chybovou hláškou
        onView(
                withId(R.id.textinput_error))
                .check(doesNotExist());

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
                                withText(getTargetContext().getString(R.string.experiment_cvep)),
                                withText(getTargetContext().getString(R.string.experiment_fvep)))))
                .perform(swipeDown());

        // Vyberu typ konfigurace
        onView(
                allOf(
                        withId(android.R.id.text1),
                        withText(CONFIGURATION_TYPE)))
                .perform(click());

        // Kontrola, že stále není zobrazen text s chybovou hláškou
        onView(
                withId(R.id.textinput_error))
                .check(doesNotExist());

        // Kontrola, že je přítomné tlačítko na potvrzení vytvoření
        // nové konfigurace a že na toto tlačítko už jde kliknout
        onView(
                withText(R.string.configuration_config_create))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));
    }
}
