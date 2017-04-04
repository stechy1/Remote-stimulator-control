package cz.zcu.fav.remotestimulatorcontrol.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.zcu.fav.remotestimulatorcontrol.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.childAtPosition;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.matchToolbarSubtitle;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.openDrawer;
import static org.hamcrest.Matchers.allOf;

/**
 * Test obsahuje metody pro otestování funkčnosti prvků, které se týkají třídy {@link MainActivity}
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Test na správné zobrazení podnadpisu
     */
    @Test
    public void toolbarSuttitleTest1() throws Exception {
        final CharSequence subtitle = InstrumentationRegistry
                .getTargetContext().getString(R.string.title_not_connected);
        matchToolbarSubtitle(subtitle);
    }

    /**
     * Test na přítomnost tlačítka pro menu
     */
    @Test
    public void hungerBarTest1() throws Exception {
        onView(
                withContentDescription(R.string.nav_drawer_open))
                .check(matches(isDisplayed()));
    }

    /**
     * Test na správné umístění položek v menu
     */
    @Test
    public void hungerBarTest2() throws Exception {
        final int[] captions = {
                R.string.nav_text_configurations,
                R.string.nav_text_settings,
                R.string.nav_text_help,
                R.string.nav_text_about
        };

        openDrawer();

        for (int i = 0, j = 1; i < captions.length; i++, j++) {
            onView(
                    childAtPosition(
                            childAtPosition(
                                    withId(R.id.design_navigation_view),
                                    j
                            ),
                            0))
                    .check(matches(withText(captions[i])))
                    .check(matches(isDisplayed()));
        }
    }

    /**
     * Test na správné přepínání mezi fragmenty
     */
    @Test
    public void hungerBarTest3() throws Exception {
        openDrawer();
        onView(
                allOf(
                        withId(R.id.design_menu_item_text),
                        withText(R.string.nav_text_configurations)))
                .check(matches(isChecked()));

        onView(
                allOf(
                        withId(R.id.design_menu_item_text),
                        withText(R.string.nav_text_help)))
                .perform(click());

        openDrawer();
        onView(
                allOf(
                        withId(R.id.design_menu_item_text),
                        withText(R.string.nav_text_help)))
                .check(matches(isChecked()));
    }

    /**
     * Zkontroluje, zda-li je zobrazen správně název aplikace
     * a jméno autora aplikace
     */
    @Test
    public void hungerBarTest4() throws Exception {
        openDrawer();

        onView(
                childAtPosition(
                        childAtPosition(
                                withId(R.id.navigation_header_container),
                                0),
                        0))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.app_name)));

        onView(
                childAtPosition(
                        childAtPosition(
                                withId(R.id.navigation_header_container),
                                0),
                        1))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.app_author)));
    }

    /**
     * Test na přítomnost tlačítka pro připojení k bluetooth zařízení
     */
    @Test
    public void toolbarItemTest() throws Exception {
        onView(
                withId(R.id.menu_main_connect))
                .check(matches(isDisplayed()))
                .check(matches(withContentDescription(R.string.action_connect)));
    }
}
