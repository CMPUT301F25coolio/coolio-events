package com.example.coolioevents.organizer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coolioevents.R;
import com.example.coolioevents.repo.EntrantsRepository;
import java.util.List;
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
 * This activity lets the organizer check different entrant lists
 * waitlist, chosen entrants, cancelled entrants for a specific event
 * Instead of putting everything inside OrganizerEventActivity,
 * we moved all the list related UI here so the main event screen stays clean.
 *
 * RATIONALE:
 * The goal was to split the UI so organizers have a separate place to
 * browse lists without cluttering the main event details. Also keeps the
 * Firestore calls organized by using EntrantsRepository instead of repeating
 * code everywhere.
 *
 * NOTES:
 *  The Wait List and Cancelled list are shown directly on this screen.
 *  The actual Entrant List with Yes/No enrolled opens a new screen
 *  This keeps things simple and matches what the design wanted.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-16
 */
public class OrganizerEntrantsActivity extends AppCompatActivity {
    private EntrantsRepository repo;     // helps fetch lists from Firestore
    private EntrantIDAdapter adapter;    // adapter for wait and cancelled lists
    private String eventId;              // event we are viewing lists for
    private String eventName;            // used when launching next screen
    private TextView currentListTitle;   // label above recycler that changes depending on which list user picks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_entrants);
        // Getting event information from previous screen
        eventId = getIntent().getStringExtra("EVENT_ID");
        eventName = getIntent().getStringExtra("EVENT_NAME");
        // Quick sanity check in case eventId didnt get passed
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Missing event id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        repo = new EntrantsRepository(); // setting up data helper
        // Back button in top bar
        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());
        // Hidden title above recycler (we show it when user picks a list)
        currentListTitle = findViewById(R.id.text_current_list_title);
        // RecyclerView used only for Waitlist and Cancelled list
        RecyclerView recycler = findViewById(R.id.recyclerEntrantIds);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantIDAdapter();
        recycler.setAdapter(adapter);
        // These are the three cards user can click
        LinearLayout cardWait = findViewById(R.id.card_wait_list);
        LinearLayout cardEntrant = findViewById(R.id.card_entrant_list);
        LinearLayout cardCancelled = findViewById(R.id.card_cancelled_list);
        // Wait List  stays on this activity
        cardWait.setOnClickListener(v -> {
            setCurrentTitle("Wait List");
            loadWaitList(); // calls Firestore to get waitlistEntrants
        });
        // Entrant List opens new screen
        // the Yes/no final enrollment screen
        cardEntrant.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEntrantsActivity.this, EventEntrantListActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            // if eventName exists we pass it so the title looks nice
            if (eventName != null) {
                intent.putExtra("EVENT_NAME", eventName);
            }
            startActivity(intent);
        });
        // Cancelled List stays on this screen
        cardCancelled.setOnClickListener(v -> {
            setCurrentTitle("Cancelled Entrant List");
            loadCancelledList(); // using repo's cancelled list (or fallback)
        });
        // Nothing is loaded at the beginning, user chooses one card first
    }
    //Updates the bold title above recycler view based on what user clicked.
    private void setCurrentTitle(String title) {
        currentListTitle.setText(title);
        currentListTitle.setVisibility(View.VISIBLE);
    }
    //Loads the waitlist for the event using the repo helper class
    private void loadWaitList() {
        repo.getWaitlist(eventId)
                .addOnSuccessListener(this::show)
                .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
    }
    /*Loads cancelled entrants list.
      For now, this is reusing enrolled list as a placeholder
      because the original project didnt include an official cancelled field.
      (I added support for cancelled in repo but some events might not have it) */
    private void loadCancelledList() {
        repo.getCancelled(eventId)
                .addOnSuccessListener(this::show)
                .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
    }
    //Shows the fetched list in the recycler
    private void show(List<String> ids) {
        adapter.updateData(ids); // simple list of strings
        toast("Loaded " + (ids == null ? 0 : ids.size()));
    }
    // Quick wrapper to display toasts
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
