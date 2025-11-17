package com.example.coolioevents.organizer;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.example.coolioevents.services.PoolingService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Copyright 2025 Avery Dancocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * PURPOSE:
 * This class represents an activity for a single event for an organizer.
 * Contains methods to initialize the layout as well as deals with
 * the actions given to different button controllers in the activity.
 * It displays the details of an event.
 *
 * RATIONALE:
 * Utilizes an event view model to retrieve the details of the event
 * from a previous activity.
 *
 * OUTSTANDING ISSUES:
 * The functionality of the settings button is not currently developed.
 *
 * @author Avery Dancocks, Parth Mittal
 * @version 1.0
 * @since 2025-11-05
 */
public class OrganizerEventActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventViewModel eventViewModel;

    private boolean lotteryDone;
    private String eventStatus;
    private int numberOfChosenEntrants;
    private int numberInWaitlist;
    private int maxEntrants;
    private Event currentEvent;

    // Attributes for displaying details
    private TextView eventNameTextView;
    private TextView eventOrganizerTextView;
    private TextView eventDescriptionTextView;
    private ImageView eventPosterImageView;
    private TextView eventTimeTextView;
    private TextView eventLocationTextView;
    private TextView eventRegistrationPeriodTextView;
    private TextView eventEntrantLimitTextView;
    private TextView eventStatusTextView;
    private TextView eventWaitlistEntrantCount; // NOTE: currently unused but kept for teammates

    private Button viewLists;
    private MaterialButton drawLottery;
    private MaterialButton drawNewEntrant;

    /*
     * Helper to change the state of the UI buttons based on lottery/event status.
     */
    private void updateButtonState() {
        // Lottery has already been drawn
        if (lotteryDone && "closed".equals(eventStatus)) {
            drawLottery.setEnabled(false);
            drawLottery.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.white)));
            drawLottery.setTextColor(ContextCompat.getColor(this, R.color.grey));
            drawLottery.setStrokeColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.grey)));

            // Chosen list not full and there are people in waitlist
            if (numberOfChosenEntrants < maxEntrants && numberInWaitlist >= 1) {
                drawNewEntrant.setEnabled(true);
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.drawfromlottery)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.white));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.white)));
            }

            // No one in waitlist
            if (numberInWaitlist == 0) {
                drawNewEntrant.setEnabled(false);
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.white)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.grey));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.grey)));
            }

            // Chosen list is full
            if (numberOfChosenEntrants == maxEntrants) {
                drawNewEntrant.setEnabled(false);
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.white)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.grey));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.grey)));
            }
        }

        // Lottery has not been drawn yet
        if (!lotteryDone && "closed".equals(eventStatus)) {
            drawLottery.setEnabled(true);
            drawLottery.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.drawfromlottery)));
            drawLottery.setTextColor(ContextCompat.getColor(this, R.color.white));
            drawLottery.setStrokeColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.white)));
        }

        // Event is still open
        if ("open".equals(eventStatus)) {
            drawLottery.setEnabled(false);
            drawNewEntrant.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event);

        // Get intent from previous fragment
        Intent intent = getIntent();
        // Event id passed into this screen
        String currentEventId = intent.getStringExtra("EVENT_ID");

        // Establishing UI components
        eventNameTextView = findViewById(R.id.eventViewName);
        eventOrganizerTextView = findViewById(R.id.eventViewOrganizer);
        eventDescriptionTextView = findViewById(R.id.eventViewDescription);
        eventPosterImageView = findViewById(R.id.eventViewPoster);
        eventTimeTextView = findViewById(R.id.eventViewTime);
        eventLocationTextView = findViewById(R.id.eventViewLocation);
        eventRegistrationPeriodTextView = findViewById(R.id.eventViewRegistrationPeriod);
        eventEntrantLimitTextView = findViewById(R.id.eventViewLimit);
        eventStatusTextView = findViewById(R.id.eventViewEventStatus);

        // Buttons
        viewLists = findViewById(R.id.view_lists_button);
        drawLottery = findViewById(R.id.draw_lottery_button);
        drawNewEntrant = findViewById(R.id.draw_new_user_button);
        ImageButton backButton = findViewById(R.id.organizer_event_back_button);
        ImageButton settingsButton = findViewById(R.id.organizer_event_edit_button);

        // Establish ViewModel
        eventViewModel = new ViewModelProvider(
                this,
                new EventViewModelFactory(db)
        ).get(EventViewModel.class);

        eventViewModel.getEventById(currentEventId).observe(this, event -> {
            if (event != null) {
                EventDetails details = event.getDetails();
                if (details != null) {
                    // Get details from event
                    lotteryDone = event.getLotteryDone();
                    eventStatus = details.getStatus();
                    numberOfChosenEntrants = event.getChosenEntrants().size();
                    numberInWaitlist = event.getWaitlistEntrants().size();
                    maxEntrants = details.getEntrantLimit();
                    currentEvent = event;

                    // Change button based on event status
                    updateButtonState();

                    // Updating UI components to match clicked event
                    if ("open".equals(details.getStatus())) {
                        eventStatusTextView.setText("Open");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(
                                this, R.drawable.greenshapebackground));
                    } else {
                        eventStatusTextView.setText("Closed");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(
                                this, R.drawable.redshapebackground));
                    }

                    eventNameTextView.setText(details.getEventName());
                    eventDescriptionTextView.setText(
                            String.format("Description: %s", details.getEventDescription())
                    );

                    if (details.getEventLocation() != null) {
                        eventLocationTextView.setText(
                                String.format("Event Location: %s", details.getEventLocation()));
                    } else {
                        eventLocationTextView.setText("Event Location: Not Available");
                    }

                    if (details.getEventTime() != null) {
                        eventTimeTextView.setText(
                                String.format("Time: %s", details.getEventTime()));
                    } else {
                        eventTimeTextView.setText("Time: Not Available");
                    }

                    eventRegistrationPeriodTextView.setText(
                            String.format("Registration Period: %s",
                                    String.valueOf(details.getRegistrationPeriod()))
                    );
                    eventEntrantLimitTextView.setText(
                            String.format("Max Entrees: %s",
                                    String.valueOf(details.getEntrantLimit()))
                    );

                    // Organizer info
                    String organizerId = event.getOrganizerId();
                    if (organizerId != null) {
                        eventViewModel.getOrganizerById(organizerId).observe(this, organizer -> {
                            if (organizer != null && organizer.getProfile() != null) {
                                String username = organizer.getProfile().getUsername();
                                if (username != null) {
                                    eventOrganizerTextView.setText(
                                            String.format("Posted By: %s", username));
                                } else {
                                    eventOrganizerTextView.setText("Posted By: Unknown");
                                }
                            }
                        });
                    }

                    // TODO: eventPosterImageView - load poster if available
                }
            }
        });

        // Open our "Lists" screen (OrganizerEntrantsActivity) with this event id
        viewLists.setOnClickListener(v -> {
            if (currentEventId != null && !currentEventId.isEmpty()) {
                Intent i = new Intent(OrganizerEventActivity.this,
                        OrganizerEntrantsActivity.class);
                i.putExtra("EVENT_ID", currentEventId);
                startActivity(i);
            } else {
                Toast.makeText(this, "Missing event id", Toast.LENGTH_SHORT).show();
            }
        });

        // Draw lottery button
        drawLottery.setOnClickListener(view -> {
            if (currentEvent != null && !lotteryDone) {
                eventViewModel.runLottery(currentEvent);
                Toast.makeText(this, "Running lottery...", Toast.LENGTH_SHORT).show();
                currentEvent.setLotteryDone(true);
                lotteryDone = true;
                updateButtonState();
            }
        });

        // Draw new entrant button
        drawNewEntrant.setOnClickListener(v -> {
            PoolingService poolingService = new PoolingService();
            poolingService.drawReplacement(currentEventId);
            numberInWaitlist--; // local update
            updateButtonState();
        });

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Settings button (still TODO)
        settingsButton.setOnClickListener(v ->
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        );
    }
}
