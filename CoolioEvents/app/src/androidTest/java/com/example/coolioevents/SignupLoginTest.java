package com.example.coolioevents;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.annotation.NonNull;
import androidx.test.rule.GrantPermissionRule;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.fail;

import android.Manifest;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private static boolean emulatorStarted = false;
    private final int firestorePort = 8080; // PORT OF firestore emulator - change to firestore emulator port
    private final int mAuthPort = 9099; // PORT OF firestore emulator - change to firestore emulator port
    private final String projectId = "coolio-events"; // String of of project id emulator is running on


    @Rule
    public ActivityScenarioRule<WelcomeActivity> scenario = new
            ActivityScenarioRule<WelcomeActivity>(WelcomeActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS); // Auto allow notifications
    public void clearFirestoreEmulatorData() throws IOException {
        /*Taken from: Google Gemini
        Prompt: how to clear firestore and authenticator emulator in android studio java tests
        Taken by: Ethan Diep
        Taken on: 11/29/25*/
        // Clears Firestore emulator
        String host = "10.0.2.2";

        String urlString = String.format("http://%s:%d/emulator/v1/projects/%s/databases/(default)/documents", host, firestorePort, projectId);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // Handle error
            System.err.println("Failed to clear firestore data, response code: " + responseCode);
        }

        connection.disconnect();
    }
    public void clearAuthEmulator() throws IOException {
        /*Taken from: Google Gemini
        Prompt: how to clear firestore and authenticator emulator in android studio java tests
        Taken by: Ethan Diep
        Taken on: 11/29/25*/
        // Clears mAuth emulator
        String host = "10.0.2.2";

        URL url = new URL(String.format("http://%s:%d/emulator/v1/projects/%s/accounts", host, mAuthPort, projectId));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        // Optional: Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // Handle error
            System.err.println("Failed to clear Auth emulator data, response code: " + responseCode);
        }
        connection.disconnect();
    }
    @Before
    public void initializeTest(){
        // This connects to to the firestore and mAuth emulators - also resets the emulator
        if (!emulatorStarted) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.useEmulator("10.0.2.2", firestorePort);
            mAuth.useEmulator("10.0.2.2", mAuthPort);
            emulatorStarted = true;
        }

        try {
            // Clears firestore and authenticator data
            clearFirestoreEmulatorData();
            clearAuthEmulator();
        }
        catch (IOException e){

        }

    }
    @Test
    public void testGoToLogin(){
        // Tests to make sure the Welcome Screen login button directs user to login screen
        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Ensures login button sends user to login screen (welcome back text should be displayed)
        onView(withText("Log In")).check(matches(isDisplayed()));
    }
    @Test
    public void testLoginGoBack(){
        // Tests to make sure the back button in Login screen sends user back to welcome screen

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Click on back Button
        onView(withId(R.id.backButton)).perform(click());

        // Ensures user is sent back to welcome screen
        onView(withText("Luckii")).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToSignup(){
        // Tests to make sure the Welcome Screen signup button directs user to signup screen

        // Click signup button
        onView(withId(R.id.signupButton)).perform(click());

        // Ensures signup button sends user to signup screen (create an account text should be displayed)
        onView(withText("Sign Up")).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpGoBack(){
        // Tests to make sure the back button in Sign up screen sends user back to welcome screen

        // Click signup button
        onView(withId(R.id.loginButton)).perform(click());

        // Click on back Button
        onView(withId(R.id.backButton)).perform(click());

        // Ensures user is sent back to welcome screen
        onView(withText("Luckii")).check(matches(isDisplayed()));
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
    @Test
    public void testSignUpAsOrganizer(){
        // Test to make sure newly signed up organizer has their account created on mAuth and Database, and is displayed correct screen
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String name = "organizer";
        String username = "organizer";
        String email = "organizer@test.com";
        String password = "password";

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

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        assert(mAuth.getCurrentUser() != null); // Check that user is logged in - ie current user is not null

        // Check that the user was made on the database - with correct fields
        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qSnap = task.getResult();
                    if (qSnap.isEmpty()){
                        fail("Couldn't find notification to entrant in waiting list in firestore.");
                    }
                    else {
                        DocumentSnapshot doc = qSnap.getDocuments().get(0);
                        if (!doc.getString("name").equals(name) ||
                                !doc.getString("role").equals("Organizer") ||
                                !doc.getString("username").equals(username)){
                            fail("User document has wrong fields");
                        }
                    }
                }
                else{
                    fail("Query Failed");
                }
            }
        });
    }

    @Test
    public void testSignUpAsEntrant(){
        // Test to make sure newly signed up entrant has their account created on mAuth and Database, and is displayed correct screen
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String name = "entrant";
        String username = "entrant";
        String email = "entrant@test.com";
        String password = "password";

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
        onView(withId(R.id.entrantButton)).perform(click());

        // Click Create Account Button
        onView(withId(R.id.createAccountButton)).perform(click());

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withId(R.id.eventList)).check(matches(isDisplayed())); // Check if home screen is displayed
        assert(mAuth.getCurrentUser() != null); // Check that user is logged in - ie current user is not null

        // Check that the user's document was made on the database - with correct fields
        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qSnap = task.getResult();
                    if (qSnap.isEmpty()){
                        fail("Couldn't find notification to entrant in waiting list in firestore.");
                    }
                    else {
                        DocumentSnapshot doc = qSnap.getDocuments().get(0);
                        if (!doc.getString("name").equals(name) ||
                                !doc.getString("role").equals("Entrant") ||
                                !doc.getString("username").equals(username)){
                            fail("User document has wrong fields");
                        }
                    }
                }
                else{
                    fail("Query Failed");
                }
            }
        });
    }

    @Test
    public void testLogOutOrganizer(){
        // Test to make sure an entrant is properly logged out of their account (sent back to welcome screen + mAuth is null)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String name = "organizer";
        String username = "organizer";
        String email = "organizer@test.com";
        String password = "password";

        // 1. SIGN UP THE USER ----------------------------------------------------------------------------------------
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

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        // 2. LOG OUT ----------------------------------------------------------------------------------------
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withText("Luckii")).check(matches(isDisplayed())); // Check if user sent back to welcome screen
        assert(mAuth.getCurrentUser() == null);
    }

    @Test
    public void testLogOutEntrant(){
        // Test to make sure an entrant is properly logged out of their account (sent back to welcome screen + mAuth is null)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String name = "entrant";
        String username = "entrant";
        String email = "entrant@test.com";
        String password = "password";

        // 1. SIGN UP THE USER ----------------------------------------------------------------------------------------
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
        onView(withId(R.id.entrantButton)).perform(click());

        // Click Create Account Button
        onView(withId(R.id.createAccountButton)).perform(click());

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        // 2. LOG OUT ----------------------------------------------------------------------------------------
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withText("Luckii")).check(matches(isDisplayed())); // Check if user sent back to welcome screen
        assert(mAuth.getCurrentUser() == null);
    }

    @Test
    public void testLoginOrganizer(){
        // Test to make sure an organizer can properly log in (after they sign up with an account)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String name = "organizer";
        String username = "organizer";
        String email = "organizer@test.com";
        String password = "password";

        // 1. SIGN UP THE USER ----------------------------------------------------------------------------------------
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

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // 2. LOG OUT ----------------------------------------------------------------------------------------
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // 3. LOG IN ----------------------------------------------------------------------------------------

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());


        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(email));  // Type Email into name field
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(password));  // Type Password into password field
        onView(withId(R.id.loginButton)).perform(click()); // Login
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // 3. CHECK PROFILE ----------------------------------------------------------------------------------------
        onView(withId(R.id.optMyProfile)).perform(click());

        onView(withText(email)).check(matches(isDisplayed())); // Check if user is signed into correct account (with same email)
        assert(mAuth.getCurrentUser() != null); // Check that currentUser is not null
    }

    @Test
    public void testLoginEntrant(){
        // Test to make sure an entrant can properly log in (after they sign up with an account)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String name = "entrant";
        String username = "entrant";
        String email = "entrant@test.com";
        String password = "password";

        // 1. SIGN UP THE USER ----------------------------------------------------------------------------------------
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
        onView(withId(R.id.entrantButton)).perform(click());

        // Click Create Account Button
        onView(withId(R.id.createAccountButton)).perform(click());

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // 2. LOG OUT ----------------------------------------------------------------------------------------
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // 3. LOG IN ----------------------------------------------------------------------------------------

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());


        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(email));  // Type Email into name field
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(password));  // Type Password into password field
        onView(withId(R.id.loginButton)).perform(click()); // Login
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // 3. CHECK PROFILE ----------------------------------------------------------------------------------------
        onView(withId(R.id.profile)).perform(click());

        onView(withText(email)).check(matches(isDisplayed())); // Check if user is signed into correct account (with same email)
        assert(mAuth.getCurrentUser() != null); // Check that currentUser is not null
    }
}
