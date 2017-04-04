package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.support.annotation.StringRes;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils;
import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.ui.MainActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoFix.openActionModeOverflowOrOptionsMenu;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.childAtPosition;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.createConfiguration;
import static org.hamcrest.Matchers.allOf;

/**
 * Test obsahuje metody pro otestování funkčnosti prvků, které se týkají
 * třídy {@link ConfigurationsFragment}
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ConfigurationsFragmentInstrumentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        EspressoTestUtils.clearConfigurations(getTargetContext());
        EspressoTestUtils.clearConfigurations(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        EspressoTestUtils.clearConfigurations(getTargetContext());
        EspressoTestUtils.clearConfigurations(getTargetContext());
    }

    /**
     * Test na zobrazení textu s informací, že nejsou nalezeny žádné konfigurace
     */
    @Test
    public void emptyConfigurationsTest() throws Exception {
        onView(
                withId(R.id.textNoConfigurationFound))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.no_configuration_found)));
    }

    /**
     * Test na přítomnost FAB tlačítka
     */
    @Test
    public void fabExistsTest() throws Exception {
        onView(
                withId(R.id.fab_new_configuration))
                .check(matches(isDisplayed()));
    }

    /**
     * Test na přítomnost správných tlačítek v toolbaru
     */
    @Test
    public void toolbarItemstTest() throws Exception {
        onView(
                withId(R.id.menu_main_sort))
                .check(matches(isDisplayed()))
                .check(matches(withContentDescription(R.string.action_sort_by)));

        onView(
                childAtPosition(
                        childAtPosition(
                                withId(R.id.toolbar),
                                3),
                        2))
                //.check(matches(withContentDescription("Další možnosti")))
                .check(matches(isDisplayed()));

        openActionBarOverflowOrOptionsMenu(getTargetContext());

        onView(
                allOf(
                        withId(R.id.title),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.action_import)));
    }

    @Test
    public void actionModeItemsTest() throws Exception {
        final String configurationName = "test";
        @StringRes final int configurationTypeName = R.string.experiment_erp;
        createConfiguration(getTargetContext(), configurationName, configurationTypeName);

        // Zobrazení action mode baru pro zobrazení možností práce s konfiguracemi
        onView(
                withId(R.id.recyclerViewConfigurations))
                .perform(actionOnItemAtPosition(0, longClick()));

        // Kontrola, že je zobrazeno tlačítko pro opuštění action mode
        onView(
                withId(R.id.action_mode_close_button))
                .check(matches(isDisplayed()))
                .check(matches(withContentDescription(android.support.v7.appcompat.R.string.abc_action_mode_done)));

        // Kontrola, že je zobrazen správný počet vybraných konfigurací
        onView(
                withId(R.id.action_bar_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("1")));

        // Kontrola, že je zobrazeno tlačítko pro smazání konfigurace
        onView(
                withId(R.id.context_delete))
                .check(matches(isDisplayed()))
                .check(matches(withContentDescription(R.string.action_delete)));

        // Kontrola, že je zobrazeno tlačítko pro přejmenování konfigurace
        onView(
                withId(R.id.context_rename))
                .check(matches(isDisplayed()))
                .check(matches(withContentDescription(R.string.action_rename)));

        // Zobrazení rozšířené kontextové nabídky
        openActionModeOverflowOrOptionsMenu(getTargetContext());

        // Kontrola, že se zobrazilo tlačítko pro duplikovaní konfigurace
        onView(
                allOf(
                        withId(R.id.title),
                        withText(R.string.action_duplicate)))
                .check(matches(isDisplayed()));

        // Kontrola, že se zobrazilo tlačítko pro možnost vybrat vše
        onView(
                allOf(
                        withId(R.id.title),
                        withText(R.string.action_select_all)))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
    }
}
