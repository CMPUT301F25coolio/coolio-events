package com.example.coolioevents.organizer;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coolioevents.R;
/**
 * Copyright 2025 Parth Mittal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * PURPOSE:
 * This activity shows the four entrant list options for a selected event:
 * Enrolled, Cancelled, Chosen, and Waitlist. Each button redirects to
 * EventEntrantListActivity with the correct type so the organizer can view
 * the matching entrant list instantly.
 *
 * RATIONALE:
 * Keeping this as a separate screen makes navigation simpler and reduces
 * clutter inside the main event view. It acts as a clean shortcut
 * into event entrant management and passing the event ID forward safely.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-27
 */
public class ListScreenActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "eventId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);
        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        ImageButton back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
        // Enrolled (acceptedEntrants)
        findViewById(R.id.cardEnrolled)
                .setOnClickListener(v ->
                        openList(eventId, EventEntrantListActivity.TYPE_ENROLLED));
        // Cancelled (canceledEntrants)
        findViewById(R.id.cardCancelled)
                .setOnClickListener(v ->
                        openList(eventId, EventEntrantListActivity.TYPE_CANCELLED));
        // Chosen (chosenEntrants)
        findViewById(R.id.cardChosen)
                .setOnClickListener(v ->
                        openList(eventId, EventEntrantListActivity.TYPE_CHOSEN));
        // Waitlist (waitlistEntrants)
        findViewById(R.id.cardWait)
                .setOnClickListener(v ->
                        openList(eventId, EventEntrantListActivity.TYPE_WAIT));
    }
    private void openList(String eventId, int type) {
        Intent i = new Intent(this, EventEntrantListActivity.class);
        i.putExtra(EXTRA_EVENT_ID, eventId);
        i.putExtra(EventEntrantListActivity.EXTRA_TYPE, type);
        startActivity(i);
    }
}
