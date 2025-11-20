package com.example.coolioevents.organizer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.example.coolioevents.NotificationViewModel;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/**
 * Copyright 2025 Ethan Diep
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class represents an Activity where Organizers can send message notifications
 * to their entrants in a certain event
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-20
 */
public class OrganizerSendNotifications extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String eventId;
    private Event currentEvent;
    private EventViewModel eventViewModel;
    private NotificationViewModel notificationViewModel;

    private TextView eventNameTextView;

    private Button waitListButton;
    private Button chosenButton;
    private Button cancelledButton;
    private Button acceptedButton;

    private EditText messageEditText;

    private Button sendMessageButton;
    private String messageRecipient = "Waitlist Entrants"; // The currently selected group of entrants to send notifications to
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_send_notifications);

        // Get event id from intent
        Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENT_ID");


        //Initialize viewmodels
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);
        notificationViewModel = new NotificationViewModel();

        // Initializing view items
        eventNameTextView = findViewById(R.id.eventViewName);
        waitListButton = findViewById(R.id.waitListButton);
        chosenButton = findViewById(R.id.chosenButton);
        cancelledButton = findViewById(R.id.cancelledButton);
        acceptedButton = findViewById(R.id.acceptedButton);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);


        // Initialize view text, buttons, etc.
        if (eventId != null){
            eventViewModel.getEventById(eventId).observe(this, event -> {
                currentEvent = event;
                eventNameTextView.setText(event.getDetails().getEventName());
                waitListButton.setText(String.format("Waitlist Entrants (%d)", event.getWaitlistEntrants().size()));
                chosenButton.setText(String.format("Chosen Entrants (%d)", event.getChosenEntrants().size()));
                cancelledButton.setText(String.format("Cancelled Entrants (%d)", event.getCancelledEntrants().size()));
                acceptedButton.setText(String.format("Accepted Entrants (%d)", event.getAcceptedEntrants().size()));
                sendMessageButton.setText(String.format("Send Notifications to %d %s", event.getWaitlistEntrants().size(), messageRecipient));
            });
        }

        // If Waitlist Button pressed change message recipient to Waitlist Entrants
        waitListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRecipientOption("Waitlist Entrants");
            }
        });
        // If Chosen Button pressed change message recipient to Chosen Entrants
        chosenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRecipientOption("Chosen Entrants");
            }
        });
        // If Cancelled Button pressed change message recipient to Cancelled Entrants
        cancelledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRecipientOption("Cancelled Entrants");
            }
        });
        // If Accepted Button pressed change message recipient to Accepted Entrants
        acceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRecipientOption("Accepted Entrants");
            }
        });
        // If send message button pressed, create and send notifications via database
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotifications();
            }
        });
    }

    /**
     * This method switches the notification message recipient (people to send notification to)
     * @param recipient
     *      The recipient entrants desired to switch to
     */
    private void switchRecipientOption(String recipient){
        if (recipient.equals("Waitlist Entrants")){
            // If option is waitlist entrants - change buttons and text to allign with this
            if (currentEvent.getWaitlistEntrants().isEmpty()){return;} // If there is nobody in the waitlist, dont allow user to select the option
            messageRecipient = recipient;
            waitListButton.setBackgroundColor(Color.parseColor("#2962FF"));
            chosenButton.setBackgroundColor(Color.parseColor("#8F949B"));
            cancelledButton.setBackgroundColor(Color.parseColor("#8F949B"));
            acceptedButton.setBackgroundColor(Color.parseColor("#8F949B"));

            sendMessageButton.setText(String.format("Send Notifications to %d %s", currentEvent.getWaitlistEntrants().size(), messageRecipient));
        }
        else if (recipient.equals("Chosen Entrants")){
            // If recipient is chosen entrants - change buttons and text to allign with this
            if (currentEvent.getChosenEntrants().isEmpty()){return;} // If there is nobody in the chosen list, dont allow user to select the option
            messageRecipient = recipient;
            waitListButton.setBackgroundColor(Color.parseColor("#8F949B"));
            chosenButton.setBackgroundColor(Color.parseColor("#2962FF"));
            cancelledButton.setBackgroundColor(Color.parseColor("#8F949B"));
            acceptedButton.setBackgroundColor(Color.parseColor("#8F949B"));

            sendMessageButton.setText(String.format("Send Notifications to %d %s", currentEvent.getChosenEntrants().size(), messageRecipient));
        }
        else if (recipient.equals("Cancelled Entrants")){
            // If recipient is cancelled entrants - change buttons and text to allign with this
            if (currentEvent.getCancelledEntrants().isEmpty()){return;} // If there is nobody in the cancelled list, dont allow user to select the option
            messageRecipient = recipient;
            waitListButton.setBackgroundColor(Color.parseColor("#8F949B"));
            chosenButton.setBackgroundColor(Color.parseColor("#8F949B"));
            cancelledButton.setBackgroundColor(Color.parseColor("#2962FF"));
            acceptedButton.setBackgroundColor(Color.parseColor("#8F949B"));

            sendMessageButton.setText(String.format("Send Notifications to %d %s", currentEvent.getCancelledEntrants().size(), messageRecipient));
        }
        else if (recipient.equals("Accepted Entrants")){
            // If recipient is accepted entrants - change buttons and text to allign with this
            if (currentEvent.getAcceptedEntrants().isEmpty()){return;} // If there is nobody in the cancelled list, dont allow user to select the option
            messageRecipient = recipient;
            waitListButton.setBackgroundColor(Color.parseColor("#8F949B"));
            chosenButton.setBackgroundColor(Color.parseColor("#8F949B"));
            cancelledButton.setBackgroundColor(Color.parseColor("#8F949B"));
            acceptedButton.setBackgroundColor(Color.parseColor("#2962FF"));

            sendMessageButton.setText(String.format("Send Notifications to %d %s", currentEvent.getAcceptedEntrants().size(), messageRecipient));
        }
    }

    /**
     * This method sends/creates notification to the current messageRecipient
     */
    private void sendNotifications(){
        String message = messageEditText.getText().toString(); // Message of notification
        List<String> sendList = new ArrayList<>(); // List of entrants to send notifications to
        if (message.isEmpty()){
            // If message is empty show a toast telling the user they need to put in a message
            Toast.makeText(OrganizerSendNotifications.this,
                    "Message Empty - Please put in a message to send.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (messageRecipient.equals("Waitlist Entrants")){
            // If recipient is waitlist entrants - change sendList to be entrants in waitlist
            sendList = currentEvent.getWaitlistEntrants();
        }
        else if (messageRecipient.equals("Chosen Entrants")){
            // If recipient is chosen entrants - change sendList to be entrants that are chosen
            sendList = currentEvent.getChosenEntrants();
        }
        else if (messageRecipient.equals("Cancelled Entrants")){
            // If recipient is cancelled entrants - change sendList to be entrants that are cancelled
            sendList = currentEvent.getCancelledEntrants();
        }
        else if (messageRecipient.equals("Accepted Entrants")){
            // If recipient is accepted entrants - change sendList to be entrants that are accepted
            sendList = currentEvent.getAcceptedEntrants();
        }

        // Create notification document on db
        notificationViewModel.createNotifications(eventId, currentEvent.getDetails().getEventName(), message, sendList);
    }


}