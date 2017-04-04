package cz.zcu.fav.remotestimulatorcontrol.ui;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.zcu.fav.remotestimulatorcontrol.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_new_configuration), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction repairedEditText = onView(
                allOf(withId(R.id.editConfigurationName), isDisplayed()));
        repairedEditText.perform(replaceText("xxx"), closeSoftKeyboard());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("Undefined"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("ERP"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withText("Vytvořit"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerViewConfigurations),
                        withParent(withId(R.id.swipeRefreshLayout)),
                        isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.action_mode_close_button), withContentDescription("Hotovo"),
                        childAtPosition(
                                allOf(withId(R.id.action_mode_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_root),
                                                2)),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.action_bar_title), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.menu_main_connect), withContentDescription("připojit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        3),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.menu_main_sort), withContentDescription("Třídit podle"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        3),
                                1),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.title), withText("Duplikovat"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("Duplikovat")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.title), withText("Vybrat vše"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView6.check(matches(withText("Vybrat vše")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.title), withText("Zrušit výběr"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView7.check(matches(withText("Zrušit výběr")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.title), withText("Invertovat výběr"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView8.check(matches(withText("Invertovat výběr")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.title), withText("Sdílet"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView9.check(matches(withText("Sdílet")));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.title), withText("Sdílet"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView10.check(matches(withText("Sdílet")));

    }

    private static Matcher<View> childAtPosition(
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
}
