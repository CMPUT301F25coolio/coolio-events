package com.example.coolioevents;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.example.coolioevents.authentication.WelcomeActivity;
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


public class LotteryTest {

    private static boolean emulatorStarted = false;
    private final int firestorePort = 8080; // PORT OF firestore emulator - change to firestore emulator port
    private final int mAuthPort = 9099; // PORT OF firestore emulator - change to mAuth emulator port
    private final int functionsPort = 5001; // PORT OF firebase functions emulator - change to functoins emulator port
    private final String projectId = "coolio-events"; // String of of project id emulator is running on


    FirebaseAuth mAuth = mock(FirebaseAuth.getInstance());

    FirebaseFirestore db = mock(FirebaseFirestore.getInstance());

    FirebaseFunctions functions = mock(FirebaseFunctions.getInstance());
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
    public void callAutoLottery(){
        FirebaseFunctions.getInstance()
                .getHttpsCallable("testAutoLottery")
                .call()
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        System.out.println("Successfully called function");
                    }
                });
    }

    @Before
    public void initializeTest(){
        // This connects to to the firestore and mAuth emulators - also resets the emulator
        if (!emulatorStarted) {
            // If emulator not connected, connect it. (All future calls of getInstance will be to emulator)

            db.useEmulator("10.0.2.2", firestorePort);
            mAuth.useEmulator("10.0.2.2", mAuthPort);
            functions.useEmulator("10.0.2.2", functionsPort);

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
    public void wow(){
        callAutoLottery();
    }
}
