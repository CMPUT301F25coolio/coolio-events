package com.example.coolioevents;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.PickerActions.setTime;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.fail;

import android.Manifest;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Copyright 2025 Ethan Diep & Juliane Phan
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
 * This class represents contains tests for Organizers.
 *
 * RATIONALE:
 * This class was defined to test Organizers.
 *
 * @author Ethan Diep & Juliane Phan
 * @version 1.0
 * @since 2025-11-29
 */
public class OrganizersTest {
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

    public void makeOrganizerAccount(){
        // Signs up or Makes an Account for Organizer through signup screen
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
    }

    public void makeEntrantAccount() {
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

        // Click Entrant Button
        onView(withId(R.id.entrantButton)).perform(click());

        // Click Create Account Button
        onView(withId(R.id.createAccountButton)).perform(click());

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }
    public void inputMockEventFields(String eventName, String eventDesc, String eventEntrantLimit, String eventLocation){
        // Inputs mock event fields for making an event
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Type Event Title into name field
        onView(withId(R.id.etEventTitle)).perform(ViewActions.typeText(eventName));

        // Type Event Description
        onView(withId(R.id.etEventDescription)).perform(ViewActions.typeText(eventDesc));

        // Input date range
        onView(withId(R.id.etRegistrationPeriod)).perform(scrollTo(), click());
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2025, 11, 29));
        onView(withText("OK")).perform((click()));
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2026, 11, 29));
        onView(withText("OK")).perform((click()));

        // Type Entrant Limit
        onView(withId(R.id.etEntrantLimit)).perform(ViewActions.typeText(eventEntrantLimit));

        // Input Time of Event
        onView(withId(R.id.etEventDateTime)).perform(scrollTo(), click());
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2026, 11, 30));
        onView(withText("OK")).perform((click()));
        onView(isAssignableFrom(TimePicker.class)).perform(setTime(10,30));
        onView(withText("OK")).perform((click()));

        // Type Event Location
        onView(withId(R.id.etEventLocation)).perform(scrollTo(), ViewActions.typeText(eventLocation));

        // Click on contest tag
        onView(withText("Contest")).perform(scrollTo(), (click()));
    }

    /* ORGANIZER MAKE EVENT TESTS ------------------------------------------------------------ */

    @Test
    public void testGoToMakeEvent() {
        // Test that Organizer navigating to make events works
        makeOrganizerAccount();

        // Go to Make event
        onView(withId(R.id.optMakeEvent)).perform(click());
        onView(withId(R.id.btnCreate)).check(matches(isDisplayed()));
    }
    @Test
    public void testMakeEventBack(){
        // TODO: is being dumb
        // Test Make event page's back button
        makeOrganizerAccount();

        // Go to Make Events
        onView(withId(R.id.optMakeEvent)).perform(click());
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        // Click Back Button
        onView(withId(R.id.btnBack)).perform(click());

        // Check that screen is back to Organizer home
        onView(withId(R.id.optMakeEvent)).check(matches(isDisplayed()));
    }
    @Test
    public void testMakeEventWithNoFields() {
        // Test to make sure event that has no fields is not created
        makeOrganizerAccount();

        // Go to Make Event
        onView(withId(R.id.optMakeEvent)).perform(click());

        // Click on Create Event, with no fields filled
        onView(withId(R.id.btnCreate)).perform(click());

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Check that still on Make event Screen (Shouldn't make event)
        onView(withId(R.id.btnCreate)).check(matches(isDisplayed()));
    }
    @Test
    public void testMakeEvent(){
        // Test that making an event actually works (on firestore side and displays on my events)

        String eventName = "Test Event Name"; // Name of event
        String eventDesc = "Description"; // Name of Desc
        String eventEntrantLimit = "3";
        String eventLocation = "Some Location";

        // Make an organizer account
        makeOrganizerAccount();

        // Navigate to make event and make event
        onView(withId(R.id.optMakeEvent)).perform(click());
        inputMockEventFields(eventName, eventDesc, eventEntrantLimit, eventLocation);
        onView(withId(R.id.btnCreate)).perform((click()));

        // Navigate to my events to see if event was made
        onView(withId(R.id.optMyEvents)).perform((click()));

        // Check if the event was made (should appear in my events) + check if document made on db
        onView(withText("Test Event Name")).check(matches(isDisplayed()));
        db.collection("events").whereEqualTo("details.eventName", eventName).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If document not found, fail the test
                fail("Couldn't find event with eventname in firestore db");
            }
        });
    }

    /* ORGANIZER MY EVENTS TESTS ------------------------------------------------------------ */

    @Test
    public void testGoToMyEvents() {
        // Test navigation to my events
        makeOrganizerAccount();

        // Go to My Events
        onView(withId(R.id.optMyEvents)).perform(click());

        // Check that eventList on myEvents is displayed
        onView(withId(R.id.eventList)).check(matches(isDisplayed()));
    }
    @Test
    public void testMyEventsBack(){
        // Test Make event page's back button
        makeOrganizerAccount();

        // Go to Make Events
        onView(withId(R.id.optMyEvents)).perform(click());

        // Click Back Button
        onView(withId(R.id.btnBack)).perform(click());

        // Check that screen is back to Organizer home
        onView(withId(R.id.optMakeEvent)).check(matches(isDisplayed()));
    }


    @Test
    public void testClickedEventMyEvents() {
        // Test that clicking on an event in my events shows event display

        String eventName = "Test Event Name"; // Name of event
        String eventDesc = "Description"; // Name of Desc
        String eventEntrantLimit = "3"; // Entrant Limit of Event
        String eventLocation = "Some Location"; // Location of event

        // Make Account
        makeOrganizerAccount();

        // Navigate to make event and make event
        onView(withId(R.id.optMakeEvent)).perform(click());
        inputMockEventFields(eventName, eventDesc, eventEntrantLimit, eventLocation);
        onView(withId(R.id.btnCreate)).perform((click()));

        onView(withId(R.id.optMyEvents)).perform((click())); // Navigate to my events

        // Click on the newly created event
        onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(0).perform(click());

        onView(withText(eventName)).check(matches(isDisplayed())); // Check that the event is displayed
    }

    /* ORGANIZER PROFILE TESTS ------------------------------------------------------------ */
    @Test
    public void testSendWaitlistNotification(){
        String eventName = "Test Event Name"; // Name of event
        String eventDesc = "Description"; // Name of Desc
        String eventEntrantLimit = "3"; // Entrant Limit of Event
        String eventLocation = "Some Location"; // Location of event

        makeOrganizerAccount();

        // Make an event
        onView(withId(R.id.optMakeEvent)).perform(click());
        inputMockEventFields(eventName, eventDesc, eventEntrantLimit, eventLocation);
        onView(withId(R.id.btnCreate)).perform((click()));

        // Logout
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        //Sign up as entrant
        makeEntrantAccount();

        // Click on the newly created event as entrant
        onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(0).perform(click());

        // Scroll to bottom of screen
        onView(withId(R.id.scrollView2))
                .perform(swipeUp(), swipeUp());

        // Click join button
        onView(withId(R.id.eventViewJoinWaitListButton))
                .perform(click());

        // Logout
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Log into organizer account
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText("organizer@test.com"));
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText("password"));
        onView(withId(R.id.loginButton)).perform(click()); // Login
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Go to My Events (as Organizer) and click the created event
        onView(withId(R.id.optMyEvents)).perform(click());
        onView(withText("Test Event Name")).perform(click());

        // Click the Send Notifications button
        onView(withId(R.id.scrollView2))
                .perform(swipeUp(), swipeUp());
        onView(withId(R.id.sendNotificationsButton)).perform(click());

        // Type and send the notification message
        onView(withId(R.id.messageEditText)).perform(ViewActions.typeText("Test Notification Message"));
        onView(withId(R.id.sendMessageButton)).perform(click());

        // Check that notification was made on the firestore
        db.collection("notficiations").whereEqualTo("receiver", eventName).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If document not found, fail the test
                fail("Couldn't find notification to entrant in waiting list in firestore.");
            }
        });
    }

    /* ORGANIZER PROFILE TESTS ------------------------------------------------------------ */

    @Test
    public void testGotoProfile() {
        // Test that clicking on an event in my events shows event display
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        makeOrganizerAccount();

        onView(withId(R.id.optMyProfile)).perform(click()); // Navigate to make event

        // Check if user is signed into correct account (with same email) + if on profile screen
        onView(withText("organizer@test.com")).check(matches(isDisplayed()));
        onView(withId(R.id.profile_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfile() {
        // Test that clicking on an event in my events shows event display
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        makeOrganizerAccount();

        onView(withId(R.id.optMyProfile)).perform(click()); // Navigate to make event

        // Edit Profile
        onView(withId(R.id.btn_edit_profile)).perform(click());
        onView(withId(R.id.etPassword)).perform(ViewActions.typeText("password"));
        onView(withId(R.id.edit_name)).perform(ViewActions.typeText("new name"));
        onView(withId(R.id.edit_username)).perform(ViewActions.typeText("newusername"));
        onView(withId(R.id.edit_email)).perform(ViewActions.typeText("new@test.com"));
        onView(withText("Confirm")).perform(click());
        onView(withId(R.id.btnSave));

    }


}
