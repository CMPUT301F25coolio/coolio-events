package com.example.coolioevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.PickerActions.setTime;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Copyright 2025 Juliane Phan
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
 * This class represents contains tests for Entrants.
 *
 * RATIONALE:
 * This class was defined to test Entrants.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-30
 */

public class EntrantsTest {
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
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2025, 12, 29));
        onView(withText("OK")).perform((click()));

        // Type Entrant Limit
        onView(withId(R.id.etEntrantLimit)).perform(ViewActions.typeText(eventEntrantLimit));

        // Input Time of Event
        onView(withId(R.id.etEventDateTime)).perform(scrollTo(), click());
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2025, 12, 30));
        onView(withText("OK")).perform((click()));
        onView(isAssignableFrom(TimePicker.class)).perform(setTime(10,30));
        onView(withText("OK")).perform((click()));

        // Type Event Location
        onView(withId(R.id.etEventLocation)).perform(scrollTo(), ViewActions.typeText(eventLocation));

        // Click on contest tag
        onView(withText("Contest")).perform(scrollTo(), (click()));
    }

    public void makeEventAsOrganizer() {
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
    }

    public void signUpAsEntrant() {
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
    public void testGoToProfile() {
        // Tests whether navigating to the profile page as an entrant works

        signUpAsEntrant();

        // Click Profile Button in bottom navigation bar
        onView(withId(R.id.profile)).perform(click());

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Check if profile screen is displayed
        onView(withId(R.id.profile_title)).check(matches(isDisplayed()));

        // Check if username matches entrant's username
        onView(withId(R.id.text_username)).check(matches(withText("entrant")));

        // Check if name matches entrant's name
        onView(withId(R.id.text_name)).check(matches(withText("entrant")));

        // Check if email matches entrant's email
        onView(withId(R.id.text_email)).check(matches(withText("entrant@test.com")));
    }

    @Test
    public void testEventAppearsInHome() {
        // Tests if an event made by an organizer appears in the home screen for an entrant

        // Make event as organizer
        makeEventAsOrganizer();

        // Log out as organizer
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Sign up as entrant
        signUpAsEntrant();

        // Check that event is displayed
        onView(withText("Test Event Name")).check(matches(isDisplayed()));
    }

    @Test
    public void testSearch() {
        // Tests whether searching for an event works for an entrant

        // Make event as organizer
        makeEventAsOrganizer();

        // Log out as organizer
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Sign up as entrant
        signUpAsEntrant();

        // Click Search button in bottom navigation bar
        onView(withId(R.id.search)).perform(click());

        // Search for event made by the organizer
        String searchInput = "Test Event Name";
        onView(withId(R.id.searchBar)).perform(ViewActions.typeText(searchInput));

        // Check that event is displayed
        onView(withText("Test Event Name")).check(matches(isDisplayed()));
    }

    @Test
    public void testFilterByTags() {
        // Tests whether filtering by tags works for an entrant

        // Make event as organizer
        makeEventAsOrganizer();

        // Log out as organizer
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Sign up as entrant
        signUpAsEntrant();

        // Click Search button in bottom navigation bar
        onView(withId(R.id.search)).perform(click());

        // Click filter button
        onView(withId(R.id.filterButton)).perform(click());

        // Click the contest tag (the tag the event was made with)
        onView(withText("Contest")).perform(click());

        // Click the apply button
        onView(withId(R.id.applyButton)).perform(click());

        // Search for event made by the organizer
        String searchInput = "Test Event Name";
        onView(withId(R.id.searchBar)).perform(ViewActions.typeText(searchInput));

        // Check that event is displayed
        onView(withText("Test Event Name")).check(matches(isDisplayed()));
    }

    @Test
    public void testJoinWaitlist() {
        // Tests whether joining a waitlist works for an entrant

        // Make event as organizer
        makeEventAsOrganizer();

        // Log out as organizer
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Sign up as entrant
        signUpAsEntrant();

        // Click event
        onView(withText("Test Event Name")).perform(click());

        // Scroll to bottom of screen
        onView(withId(R.id.scrollView2))
                .perform(swipeUp(), swipeUp());

        // Click join button
        onView(withId(R.id.eventViewJoinWaitListButton))
                .perform(click());

        // Click My Events
        onView(withId(R.id.myevents)).perform(click());

        // Check if event is in My Events
        onView(withText("Test Event Name")).check(matches(isDisplayed()));

        // Check if event's waitlistEntrants contains the entrant id in the database
        db.collection("events")
                .whereEqualTo("details.eventName", "Test Event Name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String eventId = documentSnapshot.getId();
                        ArrayList<String> waitlistEntrants = (ArrayList<String>) documentSnapshot.get("waitlistEntrants");
                        String entrantId = FirebaseAuth.getInstance().getUid();

                        assert (waitlistEntrants.contains(entrantId));
                    } else {
                        throw new AssertionError("Event not found");
                    }
                });
    }

    @Test
    public void testGoToCriteriaAndGuidelines() {
        // Tests whether going to the Criteria and Guidelines page works for an entrant

        signUpAsEntrant();

        // Click Profile Button in bottom navigation bar
        onView(withId(R.id.profile)).perform(click());

        // Scroll to bottom of screen
        onView(withId(R.id.scroll_view))
                .perform(swipeUp(), swipeUp());

        // Click Criteria and Guidelines button
        onView(withId(R.id.criteria_button)).perform(click());

        // Check if Criteria and Guidelines screen is displayed
        onView(withId(R.id.text_1_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewNotifications() {
        // Tests whether viewing notifications works (clicking the notification bell in Home) for an entrant

        // Make event as organizer
        makeEventAsOrganizer();

        // Log out as organizer
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Sign up as entrant
        signUpAsEntrant();

        // Click event
        onView(withText("Test Event Name")).perform(click());

        // Scroll to bottom of screen
        onView(withId(R.id.scrollView2))
                .perform(swipeUp(), swipeUp());

        // Click join button
        onView(withId(R.id.eventViewJoinWaitListButton))
                .perform(click());

        // Go to profile fragment
        onView(withId(R.id.profile)).perform(click());

        // Scroll to bottom of screen
        onView(withId(R.id.scroll_view))
                .perform(swipeUp(), swipeUp());

        // Log out as entrant
        onView(withId(R.id.logoutButton)).perform(click());

        // Log in as organizer
        String orgEmail = "organizer@test.com";
        String orgPassword = "password";
        onView(withId(R.id.loginButton)).perform(click());  // Click login button
        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(orgEmail));  // Type Email into name field
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(orgPassword));  // Type Password into password field
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

        // Log out as organizer
        onView(withId(R.id.btnBack)).perform(click());
        onView(withId(R.id.organizer_event_back_button)).perform(click());
        onView(withId(R.id.btnBack)).perform(click());
        onView(withId(R.id.optMyProfile)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());

        // Log in as entrant
        String entrantEmail = "entrant@test.com";
        String entrantPassword = "password";
        onView(withId(R.id.loginButton)).perform(click());  // Click login button
        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(entrantEmail));  // Type Email into name field
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(entrantPassword));  // Type Password into password field
        onView(withId(R.id.loginButton)).perform(click()); // Login
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Click notification bell in the home screen
        onView(withId(R.id.notification_button)).perform(click());

        // Check if notification is displayed
        onView(withText("Test Notification Message")).check(matches(isDisplayed()));

    }


}