package com.fxly.creatsms;


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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestCASE2 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testCASE2() throws InterruptedException {
        Thread.sleep(5000);
        ViewInteraction materialEditText = onView(
                allOf(withId(R.id.sms_body), withText("Pls input any, this area should not null")));
        materialEditText.perform(scrollTo(), replaceText("Pls input any, this area sh"), closeSoftKeyboard());
        Thread.sleep(3000);
        ViewInteraction editText = onView(
                allOf(withId(R.id.sms_body), withText("Pls input any, this area sh"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("Pls input any, this area sh")));
        Thread.sleep(3000);
        ViewInteraction materialEditText2 = onView(
                allOf(withId(R.id.sms_phone_number), withText("13800138000")));
        materialEditText2.perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        Thread.sleep(3000);
        ViewInteraction materialEditText3 = onView(
                withId(R.id.sms_phone_number));
        materialEditText3.perform(scrollTo(), click());
        Thread.sleep(3000);
        ViewInteraction materialEditText4 = onView(
                withId(R.id.sms_phone_number));
        materialEditText4.perform(scrollTo(), replaceText("10010"), closeSoftKeyboard());
        Thread.sleep(3000);
        ViewInteraction editText2 = onView(
                allOf(withId(R.id.sms_phone_number), withText("10010"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                1),
                        isDisplayed()));
        editText2.check(matches(withText("10010")));
        Thread.sleep(3000);
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.creat_sms_btn), isDisplayed()));
        floatingActionButton.perform(click());
        Thread.sleep(3000);
        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.content_main_ui),
                                0),
                        0),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));
        Thread.sleep(3000);
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
