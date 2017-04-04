package cz.zcu.fav.remotestimulatorcontrol;

import android.content.Context;
import android.os.Build;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.TreeIterables;
import android.view.View;
import android.view.ViewConfiguration;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressMenuKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;

/**
 * Pomocní knihovní třída, která opravuje chyby z třídy {@link Espresso}
 */
public class EspressoFix {

    // Ideally, this should be only allOf(isDisplayed(), withContentDescription("More options"))
    // But the ActionBarActivity compat lib is missing a content description for this element, so
    // we add the class name matcher as another option to find the view.
    @SuppressWarnings("unchecked")
    private static final Matcher<View> OVERFLOW_BUTTON_MATCHER_ACTION_MODE =
            allOf(
                    anyOf(
                            allOf(isDisplayed(), withContentDescription("More options")),
                            allOf(isDisplayed(), withClassName(endsWith("OverflowMenuButton")))),
                    withParent(withParent(withClassName(endsWith("ActionBarContextView"))))
                    );

    /**
     * Opens the overflow menu displayed within an ActionBar.
     * <p>
     * <p>This works with both native and SherlockActionBar ActionBars.
     * <p>
     * <p>Note the significant differences of UX between ActionMode and ActionBars with respect to
     * overflows. If a hardware menu key is present, the overflow icon is never displayed in
     * ActionBars and can only be interacted with via menu key presses.
     */
    @SuppressWarnings("unchecked")
    public static void openActionModeOverflowOrOptionsMenu(Context context) {
        if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.HONEYCOMB) {
            // regardless of the os level of the device, this app will be rendering a menukey
            // in the virtual navigation bar (if present) or responding to hardware option keys on
            // any activity.
            onView(isRoot())
                    .perform(pressMenuKey());
        } else if (hasVirtualOverflowButton(context)) {
            // If we're using virtual keys - theres a chance we're in mid animation of switching
            // between a contextual action bar and the non-contextual action bar. In this case there
            // are 2 'More Options' buttons present. Lets wait till that is no longer the case.
            onView(isRoot())
                    .perform(new TransitionBridgingViewAction(OVERFLOW_BUTTON_MATCHER_ACTION_MODE));

            onView(OVERFLOW_BUTTON_MATCHER_ACTION_MODE)
                    .perform(click());
        } else {
            // either a hardware button exists, or we're on a pre-HC os.
            onView(isRoot())
                    .perform(pressMenuKey());
        }
    }

    private static boolean hasVirtualOverflowButton(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        } else {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * Handles the cases where the app is transitioning between a contextual action bar and a
     * non contextual action bar.
     */
    private static class TransitionBridgingViewAction implements ViewAction {

        private final Matcher<View> matcher;

        private TransitionBridgingViewAction(Matcher<View> matcher) {
            this.matcher = matcher;
        }

        @Override
        public void perform(UiController controller, View view) {
            int loops = 0;
            while (isTransitioningBetweenActionBars(view) && loops < 100) {
                loops++;
                controller.loopMainThreadForAtLeast(50);
            }
            // if we're not transitioning properly the next viewaction
            // will give a decent enough exception.
        }

        @Override
        public String getDescription() {
            return "Handle transition between action bar and action bar context.";
        }

        @Override
        public Matcher<View> getConstraints() {
            return isRoot();
        }

        private boolean isTransitioningBetweenActionBars(View view) {
            int actionButtonCount = 0;
            for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                if (matcher.matches(child)) {
                    actionButtonCount++;
                }
            }
            return actionButtonCount > 1;
        }
    }

}
