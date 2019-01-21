package pfe.thebikelock;

import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.DrawerActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pfe.thebikelock.controller.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TestView {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testFragmentAboutOpens(){
        // we go on the About Fragment
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("About")).perform(click());
        onView(withId(R.id.fragmentAbout)).check(matches(isDisplayed()));
    }


    @Test
    public void testFragmentAboutCloses(){
        // we go on the About Fragment
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("About")).perform(click());

        // and we go on the Home Fragment
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.fragmentUnlock)).check(matches(isDisplayed()));
        onView(withId(R.id.fragmentMain)).check(matches(isDisplayed()));
        onView(withId(R.id.fragmentAbout)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationDrawerHome(){
        // Home
        int FRAGMENTS_LIST[] = {
                R.id.fragmentInformation,
                R.id.fragmentLocation,
                R.id.fragmentUnlock,
                R.id.fragmentMain,
        };

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.home));

        for(int aFRAGMENTS_LIST: FRAGMENTS_LIST){
            onView(withId(aFRAGMENTS_LIST)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testPopUpShows() {
        // click on the speaker icon
        onView(withId(R.id.button_unlock)).perform(click());
        //onView(withText("")).check(matches(isDisplayed()));
        onView(withText("Close")).check(matches(isDisplayed()));
    }

    @Test
    public void testPopUpBackPressed() {
        // click on the speaker icon
        onView(withId(R.id.button_unlock)).perform(click());
        // and we go on the Home Fragment
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.fragmentUnlock)).check(matches(isDisplayed()));
    }

}
