package com.example.coolioevents.organizer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coolioevents.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Copyright 2025 Parth Mittal
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
 * This activity shows the final entrants for a specific event. Basically,
 * after the organizer runs the lottery and users accept their invite,
 * this screen lists all the invited people and marks whether they actually
 * registered or not.
 *
 * RATIONALE:
 * This screen was made to give organizers a simple way to check who
 * officially enrolled in their event. Instead of digging through Firestore
 * fields, we combine the chosen and accepted lists and display them in a
 * clean, two-column format using a RecyclerView.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-16
 */
public class EventEntrantListActivity extends AppCompatActivity {
    // Firestore reference only one needed
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Data coming from the previous screen
    private String eventId;
    private String eventName;
    // RecyclerView and its adapter
    private RecyclerView recyclerView;
    private EventEntrantListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_entrant_list);
        // Receiving the event id and name from OrganizerEventActivity
        eventId = getIntent().getStringExtra("EVENT_ID");
        eventName = getIntent().getStringExtra("EVENT_NAME");
        // Top bar setup
        ImageButton backButton = findViewById(R.id.btn_event_entrant_back);
        TextView titleText = findViewById(R.id.text_event_entrant_title);
        // If we have an event name use it as the title
        if (eventName != null && !eventName.isEmpty()) {
            titleText.setText(eventName + " Entrants List");
        } else {
            // fallback title if name missing
            titleText.setText("Event Entrant List");
        }
        // Back button simply closes this screen
        backButton.setOnClickListener(v -> finish());
        // Setting up the RecyclerView
        recyclerView = findViewById(R.id.recycler_event_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventEntrantListAdapter(); // empty at first
        recyclerView.setAdapter(adapter);
        // If the event ID somehow didnt arrive then show error and stop
        if (eventId == null || eventId.isEmpty()) {
            toast("Missing event id");
            return;
        }
        // Load all the data for the list
        loadEntrants();
    }
    /* Loads the chosenEntrants and acceptedEntrants arrays from Firestore.
      - chosenEntrants = everyone who got selected by lottery
      - acceptedEntrants = people who actually accepted the invite
      We compare both lists and prepare rows that say
      UID | Yes/No*/
    @SuppressWarnings("unchecked")
    private void loadEntrants() {
        // Grab the event document from Firestore
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    // If event doesnt exist, tell user
                    if (snapshot == null || !snapshot.exists()) {
                        toast("Event not found");
                        return;
                    }
                    // Pull chosen with accepted arrays may be null
                    List<String> chosen = (List<String>) snapshot.get("chosenEntrants");
                    List<String> accepted = (List<String>) snapshot.get("acceptedEntrants");
                    // Avoid null pointer crashes
                    if (chosen == null) chosen = new ArrayList<>();
                    if (accepted == null) accepted = new ArrayList<>();
                    // Use a Set for fast lookup when checking is accepted
                    Set<String> acceptedSet = new HashSet<>(accepted);
                    // Build rows for the adapter
                    List<EventEntrantRow> rows = new ArrayList<>();
                    for (String uid : chosen) {
                        // If the user is in acceptedEntrants then mark Yes
                        boolean registered = acceptedSet.contains(uid);
                        rows.add(new EventEntrantRow(uid, registered));
                    }
                    // Give this list to the adapter to show on screen
                    adapter.submitList(rows);

                })
                .addOnFailureListener(e -> toast("Error loading entrants: " + e.getMessage()));
    }
    //Simple helper for quick toasts
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    /*Row model for the RecyclerView.
      Contains:
      - entrantId i.e user ID string
      - registered i.e true if they accepted, false otherwise*/
    public static class EventEntrantRow {
        public final String entrantId;
        public final boolean registered;
        public EventEntrantRow(String entrantId, boolean registered) {
            this.entrantId = entrantId;
            this.registered = registered;
        }
    }
}
