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
/**
 * Copyright 2025 Ethan Diep
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class represents a tests for Signup/Login Screen. It contains tests for those screens
 *
 * RATIONALE:
 * This class was defined to test Signup/Login Screens
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-06
 */
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
    public void testLoginGoBack(){
        // Tests to make sure the back button in Login screen sends user back to welcome screen

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Click on back Button
        onView(withId(R.id.backButton)).perform(click());

        // Ensures user is sent back to welcome screen
        onView(withText("COOLIO EVENTS\n\n Welcome!")).check(matches(isDisplayed()));
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
    public void testSignUpGoBack(){
        // Tests to make sure the back button in Sign up screen sends user back to welcome screen

        // Click signup button
        onView(withId(R.id.loginButton)).perform(click());

        // Click on back Button
        onView(withId(R.id.backButton)).perform(click());

        // Ensures user is sent back to welcome screen
        onView(withText("COOLIO EVENTS\n\n Welcome!")).check(matches(isDisplayed()));
    }
    @Test
    public void testEmptyName(){
        // Tests to make sure user who signs up with no name filled does not have their account
        // created and is warned.

        String name = "";
        String username = "testUsername";
        String email = "test@testyy.com";
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


        // Check that proper warning is shown to user
        onView(withText("Please put in your Full name")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyUsername(){
        // Tests to make sure user who signs up with no username filled does not have their account
        // created and is warned.

        String name = "testName";
        String username = "";
        String email = "test@testyy.com";
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

        // Check that proper warning is shown to user
        onView(withText("Please put in a username")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyEmail(){
        // Tests to make sure user who signs up with no email filled does not have their account
        // created and is warned.

        String name = "testName";
        String username = "testUsername";
        String email = "";
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

        // Check that proper warning is shown to user
        onView(withText("Please put in an email")).check(matches(isDisplayed()));
    }


    @Test
    public void testEmptyPassword(){
        // Tests to make sure user who signs up with no password filled does not have their account
        // created and is warned.

        String name = "testName";
        String username = "testUsername";
        String email = "test@testyy.com";
        String password = "";

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

        // Check that proper warning is shown to user
        onView(withText("Please put in a password")).check(matches(isDisplayed()));
    }



//    @Test
//    public void testSignupAsOrganizer(){
//        // Test to see if Signing Up as Organizer brings you to organizer screen
//
//        String name = "testName";
//        String username = "testName";
//        String email = "test@testyy.com";
//        String password = "testpassword";
//
//        // Click signup button
//        onView(withId(R.id.signupButton)).perform(click());
//
//        // Type Name into name field
//        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText(name));
//        // Type Username into name field
//        onView(withId(R.id.usernameEditText)).perform(ViewActions.typeText(username));
//        // Type Email into name field
//        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(email));
//        // Type Name into name field
//        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(password));
//
//        // Click Organizer Button
//        onView(withId(R.id.organizerButton)).perform(click());
//
//        // Click Create Account Button
//        onView(withId(R.id.createAccountButton)).perform(click());
//
//        //Checks to see if Make event button is displayed to see if in Organizer Screen
//        onView(withId(R.id.optMakeEvent)).check(matches(isDisplayed()));
//
//        //TODO: finish
//
//    }

}
