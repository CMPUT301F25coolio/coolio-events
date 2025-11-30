package com.example.coolioevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.PickerActions.setTime;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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


    @Test
    public void testMakeEvent(){
        String eventName = "Test Event Name"; // Name of event
        String eventDesc = "Description"; // Name of Desc
        String eventEntrantLimit = "3";
        String eventLocation = "Some Location";

        makeOrganizerAccount();
        onView(withId(R.id.optMakeEvent)).perform(click());

        inputMockEventFields(eventName, eventDesc, eventEntrantLimit, eventLocation);
        onView(withId(R.id.btnCreate)).perform((click()));
        onView(withId(R.id.optMyEvents)).perform((click())); // Navigate to my events to see if event was made

        boolean eventOnDB = false;
        onView(withText("Test Event Name")).check(matches(isDisplayed())); // Check if the event was made (should appear in my events)
        db.collection("events").whereEqualTo("details.eventName", eventName).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If document not found, fail the test
                fail("Couldn't find event with eventname in db");
            }
        }); // Check that event document was made in db
    }
}
