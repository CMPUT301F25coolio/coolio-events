package com.example.coolioevents;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import androidx.lifecycle.MutableLiveData;

import com.example.coolioevents.authentication.WelcomeActivity;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.organizer.Organizer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;


public class eventViewModelTest {

    private static boolean emulatorStarted = false;
    private final int firestorePort = 8080; // PORT OF firestore emulator - change to firestore emulator port
    private final int mAuthPort = 9099; // PORT OF firestore emulator - change to mAuth emulator port
    private final String projectId = "coolio-events"; // String of of project id emulator is running on


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

    public Event makeMockEvent(Organizer organizer, String eventName, String eventDescription, Date eventTime,  String eventLocation, String regPeriod, int entrantLimit, Date postedDate) {



        EventDetails testDetails = new EventDetails(eventName, eventDescription, regPeriod, entrantLimit, eventTime, eventLocation, postedDate);
        return new Event(UUID.randomUUID().toString(), organizer.getProfile().getUser_id(), testDetails);
    }


    @Before
    public void initializeTest(){
        // This connects to to the firestore and mAuth emulators - also resets the emulator
        if (!emulatorStarted) {
            // If emulator not connected, connect it. (All future calls of getInstance will be to emulator)
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
    public void testGetEventById(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//        Organizer organizer = new Organizer(new Profile(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "test", "test@cool.com"));
//
//        db.collection("users").document(organizer.getProfile().getUser_id()).set(organizer.getProfile());
//        db.collection("users").document(organizer.getProfile().getUser_id()).update("role", "organizer");
//
//        Event event1 = makeMockEvent(organizer, "event1", "", new Date(), "", "2025/10/25-2099/12/25", 2, new Date());
//        Event event2 = makeMockEvent(organizer, "event2", "", new Date(), "", "2025/10/25-2099/12/25", 2, new Date());
//        Event event3 = makeMockEvent(organizer, "event2", "", new Date(), "", "2025/10/25-2099/12/25", 2, new Date());
//
//        db.collection("events").document(event1.getEventId()).set(event1);
//        db.collection("events").document(event2.getEventId()).set(event2);
//        db.collection("events").document(event3.getEventId()).set(event3);
//
//        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
//
//        EventViewModel eventViewModel = new EventViewModel(db);
//
//        eventViewModel.getEventById(event2.getEventId()).observe(null, );
//
//
//        try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
//
//        assert(returnedEvent.getEventId().equals(event2.getEventId()) && returnedEvent.getDetails().getEventName().equals(event2.getDetails().getEventName()));

    }
}
