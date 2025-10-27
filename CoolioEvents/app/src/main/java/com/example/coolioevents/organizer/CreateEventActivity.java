package com.example.coolioevents.organizer;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class CreateEventActivity extends AppCompatActivity{
    private EditText etTitle, etDescription, etRegistrationPeriod, etEntrantLimit;
    private Button btnCreate;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Bind UI elements
        etTitle = findViewById(R.id.etEventTitle);
        etDescription = findViewById(R.id.etEventDescription);
        etRegistrationPeriod = findViewById(R.id.etRegistrationPeriod);
        etEntrantLimit = findViewById(R.id.etEntrantLimit);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);

        // Handle back arrow
        btnBack.setOnClickListener(v -> {
            finish(); // Return to Organizer Home
        });

        // Handle create button
        btnCreate.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String registrationPeriod = etRegistrationPeriod.getText().toString().trim();
        String entrantLimitStr = etEntrantLimit.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(registrationPeriod) || TextUtils.isEmpty(entrantLimitStr)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int entrantLimit;
        try {
            entrantLimit = Integer.parseInt(entrantLimitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entrant limit must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = currentUser != null ? currentUser.getUid() : "unknown";
        String status = "open"; // Default for new events

        // Create EventDetails object
        EventDetails eventDetails = new EventDetails(title, description, registrationPeriod, entrantLimit, status);

        // Create Event object (without poster/QR)
        String eventId = db.collection("events").document().getId(); // Auto-generate ID
        Event event = new Event(eventId, eventDetails);

        // Convert to Firestore-friendly map
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventId", eventId);
        eventMap.put("details", eventDetails);

        // Save event to Firestore
        db.collection("events").document(eventId)
                .set(eventMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                    // Go to MyEvents screen
                    Intent intent = new Intent(CreateEventActivity.this, MyEventsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
