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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.matchToolbarTitle;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.openDrawer;
import static org.hamcrest.Matchers.allOf;

/**
 * Test obsahuje metody pro otestování funkčnosti prvků, které se týkají třídy {@link MainFragment}
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainFragmentInstrumentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Test na správné zobratení hlavního nadpisu aplikace
     */
    @Test
    public void toolbarTitleTest1() {
        final CharSequence title = InstrumentationRegistry
                .getTargetContext().getString(R.string.nav_text_configurations);
        matchToolbarTitle(title);
    }

    /**
     * Test, že je vybraná správná položka v menu
     */
    @Test
    public void navigationCheckedTest() throws Exception {
        openDrawer();

        onView(
                allOf(
                        withId(R.id.design_menu_item_text),
                        withText(R.string.nav_text_configurations)))
                .check(matches(isChecked()));
    }

    //@Test
    private void bottomNavBarTest1() throws Exception {
        final int[] captions = {
                R.string.nav_text_configurations,
                R.string.nav_text_profiles,
                R.string.nav_text_media
        };
        final int[] images = {
                R.drawable.experiments,
                android.R.drawable.ic_menu_edit,
                R.drawable.default_media_image_thumbnail
        };

//        onView(
//                allOf(
//                        withId(R.id.largeLabel),
//                        isDescendantOfA(withId(R.id.nav_bottom_media))))
//                .check(matches(withText(R.string.nav_text_configurations)));

        // Test názvu itemu
//        onView(
//                allOf(
//                        withId(R.id.largeLabel),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.nav_bottom_experiments),
//                                        1),
//                                0)))
//                .check(matches(isDisplayed()))
//                .check(matches(withText(R.string.nav_text_configurations)));

        // Test obrázku itemu
//        onView(
//                allOf(
//                        withId(R.id.icon),
//                        hasSibling(
//                                childAtPosition(
//                                        childAtPosition(
//                                                withId(R.id.nav_bottom_experiments),
//                                                1),
//                                        0))))
//                .check(matches(withImageDrawable(R.drawable.experiments)));

    }
}
