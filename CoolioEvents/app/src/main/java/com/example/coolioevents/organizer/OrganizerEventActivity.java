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
import com.example.coolioevents.organizer.ListScreenActivity;

/**
 * Copyright 2025 Avery Dancocks
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
 * The functionality of the edit button is not currently developed.
 *
 * @author Avery Dancocks
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
    private TextView eventWaitlistEntrantCount;
    private Button viewLists;
    private MaterialButton drawLottery;
    private MaterialButton drawNewEntrant;
    private Button sendNotifications;

    /**
     * This is a helper function to assist in changing the state of the UI
     */
    private void updateButtonState() {
        // Lottery has already been drawn
        if (lotteryDone && eventStatus.equals("closed")) {
            drawLottery.setEnabled(false);
            // Set UI
            drawLottery.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            drawLottery.setTextColor(ContextCompat.getColor(this, R.color.grey));
            drawLottery.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
            // Someone left the chosen list, and there is still people in the waitlist
            if ((numberOfChosenEntrants < maxEntrants) && (numberInWaitlist >= 1)) {
                drawNewEntrant.setEnabled(true);
                // Set UI
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.drawfromlottery)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.white));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            }
            if (numberInWaitlist == 0) {
                drawNewEntrant.setEnabled(false);
                // Set UI
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.grey));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
            }
            // The chosen list is full
            if (numberOfChosenEntrants == maxEntrants) {
                drawNewEntrant.setEnabled(false);
                // Set UI
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.grey));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
            }
        }
        // Lottery has not been drawn
        if (!lotteryDone && eventStatus.equals("closed")) {
            drawLottery.setEnabled(true);
            // Set UI
            drawLottery.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.drawfromlottery)));
            drawLottery.setTextColor(ContextCompat.getColor(this, R.color.white));
            drawLottery.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }
        // Event is still open
        if (eventStatus.equals("open")) {
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
        //Get city name
        String currentEventId = intent.getStringExtra("EVENT_ID");

        // Establishing UI components
        eventNameTextView = findViewById(R.id.eventViewName);
        eventOrganizerTextView = findViewById((R.id.eventViewOrganizer));
        eventDescriptionTextView = findViewById(R.id.eventViewDescription);
        eventPosterImageView = findViewById(R.id.eventViewPoster);
        eventTimeTextView = findViewById(R.id.eventViewTime);
        eventLocationTextView = findViewById(R.id.eventViewLocation);
        eventRegistrationPeriodTextView = findViewById(R.id.eventViewRegistrationPeriod);
        eventEntrantLimitTextView = findViewById(R.id.eventViewLimit);
        eventWaitlistEntrantCount = findViewById(R.id.eventWaitlistEntrantCount);
        eventStatusTextView = findViewById(R.id.eventViewEventStatus);

        // Establish ViewModel
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        eventViewModel.getEventById(currentEventId).observe(this, event -> {
            if (event != null) {
                EventDetails details = event.getDetails();
                if (details != null) {
                    // Get Details from event
                    lotteryDone = event.getLotteryDone();
                    System.out.println("the Lottery is Done:" + lotteryDone);
                    eventStatus = event.getDetails().getStatus();
                    numberOfChosenEntrants = event.getChosenEntrants().size();
                    numberInWaitlist = event.getWaitlistEntrants().size();
                    maxEntrants = event.getDetails().getEntrantLimit();
                    currentEvent = event;
                    // Change button based on event status
                    updateButtonState();
                    System.out.println("WE MADE IT HERE");
                    // Updating UI components to match clicked event


                    if (event.getDetails().getStatus().equals("open")) {
                        // If event open make text open with green background
                        eventStatusTextView.setText("Open");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.greenshapebackground));
                    }
                    else{
                        // If event closed make text open with red background
                        eventStatusTextView.setText("Closed");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.redshapebackground));
                    }


                    eventNameTextView.setText(details.getEventName());
                    eventDescriptionTextView.setText(String.format("Description: %s", event.getDetails().getEventDescription()));
                    if (event.getDetails().getEventLocation() != null){
                        eventLocationTextView.setText(String.format("Event Location: %s",event.getDetails().getEventLocation())); // Sets event location if not null
                    }
                    else {
                        eventLocationTextView.setText("Event Location: Not Available"); // Sets event location if  null
                    }
                    if (event.getDetails().getEventDateTime() != null){
                        eventTimeTextView.setText(String.format("Time: %s",event.getDetails().getEventDateTime())); // Sets event time if not null
                    }
                    else {
                        eventTimeTextView.setText("Time: Not Available"); // Sets event time if  null
                    }
                    eventRegistrationPeriodTextView.setText(String.format("Registration Period: %s", String.valueOf(details.getRegistrationPeriod())));
                    eventEntrantLimitTextView.setText(String.format("Max Entrees: %s", String.valueOf(details.getEntrantLimit())));
                    eventWaitlistEntrantCount.setText(String.format("%s PEOPLE IN WAITING LIST", String.valueOf(event.getWaitlistEntrants().size())));

                    // UI set up specifically for organizer
                    String organizerId = event.getOrganizerId();
                    if (organizerId != null) {
                        eventViewModel.getOrganizerById(organizerId).observe(this, organizer -> {
                            if (organizer != null && organizer.getProfile() != null) {
                                String username = organizer.getProfile().getUsername();
                                if (username != null) { // Organizer has a username
                                    eventOrganizerTextView.setText(String.format("Posted By: %s", username)); // Set the text for organizer
                                }
                                else {
                                    eventOrganizerTextView.setText("Posted By: Unknown");
                                }
                            }
                        });
                    }
                    // TODO: eventPosterImageView - how to do
                }
            }
        });

        // Establishing Buttons
        viewLists = findViewById(R.id.view_lists_button);
        drawLottery = findViewById(R.id.draw_lottery_button);
        drawNewEntrant = findViewById(R.id.draw_new_user_button);
        sendNotifications = findViewById(R.id.sendNotificationsButton);
        ImageButton backButton = findViewById(R.id.organizer_event_back_button);
        ImageButton editButton = findViewById(R.id.organizer_event_edit_button);


        viewLists.setOnClickListener(v -> {
            // use the EVENT_ID that was passed into this Activity
            if (currentEventId == null || currentEventId.isEmpty()) {
                Toast.makeText(OrganizerEventActivity.this,
                        "Error: Event ID missing", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent listIntent = new Intent(OrganizerEventActivity.this, ListScreenActivity.class);
            listIntent.putExtra(ListScreenActivity.EXTRA_EVENT_ID, currentEventId);
            startActivity(listIntent);

        });


        // Draw lottery button calls function to draw lottery
        drawLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentEvent != null && !lotteryDone) {
                    eventViewModel.runLottery(currentEvent);

                    Toast.makeText(OrganizerEventActivity.this, "Running lottery...", Toast.LENGTH_SHORT).show();

                    // Change state of lottery done
                    currentEvent.setLotteryDone(true);
                    lotteryDone = true;
                    updateButtonState();
                }
            }
        });

        // Send Notifications button to go to an activity to send notifications to entrants
        sendNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentEvent != null) {
                    Intent intent = new Intent(OrganizerEventActivity.this, OrganizerSendNotifications.class);
                    intent.putExtra("EVENT_ID", currentEventId);
                    startActivity(intent);
                }
            }
        });

        // Draw new entrant button onclick activity
        drawNewEntrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoolingService poolingService = new PoolingService();
                poolingService.drawReplacement(currentEventId);
                numberInWaitlist--; // Update this local value
                updateButtonState();
            }
        });

        // Back button onclick activity
        backButton.setOnClickListener(v -> finish());

        // Edit button onclick activity
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentEventId == null || currentEventId.isEmpty()) {
                    Toast.makeText(OrganizerEventActivity.this,
                            "Error: Event ID missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(OrganizerEventActivity.this, EditEventActivity.class);
                intent.putExtra("EVENT_ID", currentEventId);
                startActivity(intent);
            }
        });
    }
}
