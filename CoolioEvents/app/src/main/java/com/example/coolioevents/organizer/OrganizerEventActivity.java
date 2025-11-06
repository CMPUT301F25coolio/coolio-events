package com.example.coolioevents.organizer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class OrganizerEventActivity extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private boolean lotteryDone;
    private String eventStatus;
    private int numberOfChosenEntrants;
    private int maxEntrants;
    private Event currentEvent;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Attributes for displaying details
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private ImageView eventPosterImageView;
    private TextView eventTimeTextView;
    private TextView eventRegistrationPeriodTextView;
    private TextView eventEntrantLimitTextView;
    private TextView eventStatusTextView;
    private TextView eventUserStatusRegistrationView;
    private TextView eventWaitlistEntrantCount;
    private Button viewLists;
    private MaterialButton drawLottery;
    private MaterialButton drawNewEntrant;

    private void updateButtonState() {
        // Lottery has already been drawn
        if (lotteryDone && eventStatus.equals("closed")) {
            drawLottery.setEnabled(false);
            // Set UI
            drawLottery.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            drawLottery.setTextColor(ContextCompat.getColor(this, R.color.grey));
            drawLottery.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
            // Check to see if we can draw a new user by
            // comparing number of people in selected list
            // with max entrants
            if (numberOfChosenEntrants < maxEntrants) { // Someone left the chosen list
                drawNewEntrant.setEnabled(true);
                // Set UI
                drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.drawfromlottery)));
                drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.white));
                drawNewEntrant.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            }
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
        eventDescriptionTextView = findViewById(R.id.eventViewDescription);
        eventPosterImageView = findViewById(R.id.eventViewPoster);
        eventTimeTextView = findViewById(R.id.eventViewTime);
        eventRegistrationPeriodTextView = findViewById(R.id.eventViewRegistrationPeriod);
        eventEntrantLimitTextView = findViewById(R.id.eventViewLimit);
        eventStatusTextView = findViewById(R.id.eventViewEventStatus);
        eventWaitlistEntrantCount = findViewById(R.id.eventWaitlistEntrantCount);
        eventUserStatusRegistrationView = findViewById(R.id.eventViewUserStatusRegistration);

        // Establish ViewModel
        // eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Source - https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
        // Posted by mlykotom
        // Retrieved by Juliane Phan on 2025-11-06, License - CC BY-SA 4.0
        // Used to instantiate the EventViewModel which uses the EventViewModel Factory class
        // Modifications made: Used our own class and parameter names
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
                    eventDescriptionTextView.setText(details.getEventDescription());
                    eventRegistrationPeriodTextView.setText(String.format("Registration Period: %s", String.valueOf(details.getRegistrationPeriod())));
                    eventEntrantLimitTextView.setText(String.format("Max Entrees: %s", String.valueOf(details.getEntrantLimit())));
                    eventWaitlistEntrantCount.setText(String.format("%s PEOPLE IN WAITING LIST", String.valueOf(event.getWaitlistEntrants().size())));

                    // TODO: eventPosterImageView - how to do
                    // -- something to do with getPosterUrl() in events
                    // TODO: eventTimeTextView.setText(details.getEventTime()); - add getEventTime
                    updateButtonState(); //--> make sure all buttons and text match event status
                }
            }
        });

        // Establishing Buttons
        viewLists = findViewById(R.id.view_lists_button);
        drawLottery = findViewById(R.id.draw_lottery_button);
        drawNewEntrant = findViewById(R.id.draw_new_user_button);
        ImageButton backButton = findViewById(R.id.organizer_event_back_button);
        ImageButton settingsButton = findViewById(R.id.organizer_event_edit_button);

        viewLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - to be implemented by whoever made the view lists activity
            }
        });

        drawLottery.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (currentEvent != null && !lotteryDone) {
                   eventViewModel.runLottery(currentEvent);

                   //Toast.makeText(this, "Running lottery...", Toast.LENGTH_SHORT).show();
                   currentEvent.setLotteryDone(true);
                   lotteryDone = true;
                   updateButtonState();
               }
           }
       });

       drawNewEntrant.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //NOTE: this is temporary
               //If data base does not update the chosen list fast enough
               //I might just do a call in here to make sure chosenList.size()
               //is less than the maxEntrants
               //If not we wont let this function do anything
               PoolingService poolingService = new PoolingService();
               poolingService.drawReplacement(currentEventId);
               updateButtonState();
           }
       });

        // Back button sends user back to Organizer My Events
        backButton.setOnClickListener(v -> finish());

        // Setting button - To implement later
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a toast saying coming soon
            }
        });

    }
}
