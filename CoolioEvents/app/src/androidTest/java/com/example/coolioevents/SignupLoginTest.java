package com.example.coolioevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;

public class SignupLoginTest {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityScenarioRule<WelcomeActivity> scenario = new
            ActivityScenarioRule<WelcomeActivity>(WelcomeActivity.class);

    @Test
    public void testGoToLogin(){
        // Tests to make sure the Welcome Screen login button directs user to login screen

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Ensures login button sends user to login screen (welcome back text should be displayed)
        onView(withText("Welcome Back!\n\n LOGIN")).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToSignup(){
        // Tests to make sure the Welcome Screen signup button directs user to signup screen

        // Click signup button
        onView(withId(R.id.signupButton)).perform(click());

        // Ensures signup button sends user to signup screen (create an account text should be displayed)
        onView(withText("CREATE \nAN ACCOUNT")).check(matches(isDisplayed()));
    }

    @Test
    public void testSignupAsOrganizer(){
        // Test to see if Signing Up as Organizer brings you to organizer screen

        String name = "testName";
        String username = "testName";
        String email = "test@testy.com";
        String password = "testpassword";

        // Click signup button
        onView(withId(R.id.signupButton)).perform(click());

        // Type Name into name field
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText(name));
        // Type Username into name field
        onView(withId(R.id.usernameEditText)).perform(ViewActions.typeText(username));
        // Type Email into name field
        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(email));
        // Type Name into name field
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(password));

        // Click Organizer Button
        onView(withId(R.id.organizerButton)).perform(click());

        // Click Create Account Button
        onView(withId(R.id.createAccountButton)).perform(click());

        //Checks to see if Make event button is displayed to see if in Organizer Screen
        onView(withId(R.id.optMakeEvent)).check(matches(isDisplayed()));

        //TODO: finish

    }

}
