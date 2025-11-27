package com.example.coolioevents.administrator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventFragment;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Copyright 2025 Juliane Phan
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
 * This class represents a fragment for a specific event.
 * It is displayed when an Administrator clicks on a specific event in the Events screen.
 * It displays the details of the event, and allows the administrator to delete the event
 * if they wish.
 *
 * RATIONALE:
 * Utilizes an event view model to retrieve the details of the event
 * from a previous fragment.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-20
 */

public class AdministratorEventFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventViewModel eventViewModel;
    private String currentEventId;

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
    private TextView eventViewUserStatusLabel;
    private TextView eventUserStatusView;
    private TextView eventUserStatusRegistrationView;
    private TextView eventWaitlistEntrantCount;
    private Button administratorDeleteEventButton;

    /**
     * This is a constructor for the Event Fragment
     *
     * @param eventId
     *      the event we want the fragment to display
     * @return the fragment
     */
    public static AdministratorEventFragment newInstance(String eventId) {
        AdministratorEventFragment fragment = new AdministratorEventFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventId); // Bundle holds the event id
        fragment.setArguments(args); // Attach the bundle to the fragment
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEventId = getArguments().getString("event_id");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentEventLayout = inflater.inflate(R.layout.fragment_event, container, false);
        return fragmentEventLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Establishing UI components
        eventNameTextView = view.findViewById(R.id.eventViewName);
        eventOrganizerTextView = view.findViewById(R.id.eventViewOrganizer);
        eventDescriptionTextView = view.findViewById(R.id.eventViewDescription);
        eventPosterImageView = view.findViewById(R.id.eventViewPoster);
        eventTimeTextView = view.findViewById(R.id.eventViewTime);
        eventLocationTextView = view.findViewById(R.id.eventViewLocation);
        eventRegistrationPeriodTextView = view.findViewById(R.id.eventViewRegistrationPeriod);
        eventEntrantLimitTextView = view.findViewById(R.id.eventViewLimit);
        eventStatusTextView = view.findViewById(R.id.eventViewEventStatus);
        eventViewUserStatusLabel = view.findViewById(R.id.eventViewUserStatusLabel);
        eventUserStatusView = view.findViewById(R.id.eventViewUserStatus);
        eventWaitlistEntrantCount = view.findViewById(R.id.eventWaitlistEntrantCount);
        eventUserStatusRegistrationView = view.findViewById(R.id.eventViewUserStatusRegistration);

        // Setting visibility of certain views
        eventViewUserStatusLabel.setVisibility(View.GONE);
        eventUserStatusView.setVisibility(View.GONE);
        eventUserStatusRegistrationView.setVisibility(View.GONE);
        eventWaitlistEntrantCount.setVisibility(View.GONE);

        // Getting ViewModel and displaying event details
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        //TODO: Implement a check to make sure the event ID exists
        eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                EventDetails details = event.getDetails();
                if (details != null) {
                    System.out.println("WE MADE IT HERE");

                    // Updating UI components to match clicked event
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
                        eventLocationTextView.setText("Time: Not Available"); // Sets event time if  null
                    }
                    eventRegistrationPeriodTextView.setText(String.format("Registration Period: %s", String.valueOf(details.getRegistrationPeriod())));
                    eventEntrantLimitTextView.setText(String.format("Max Entrees: %s", String.valueOf(details.getEntrantLimit())));

                    // UI set up specifically for organizer
                    String organizerId = event.getOrganizerId();
                    if (organizerId != null) {
                        eventViewModel.getOrganizerById(organizerId).observe(getViewLifecycleOwner(), organizer -> {
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

                    if (event.getDetails().getStatus().equals("open")) {
                        // If event open make text open with green background
                        eventStatusTextView.setText("Open");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));
                    }
                    else{
                        // If event closed make text open with red background
                        eventStatusTextView.setText("Closed");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.redshapebackground));
                    }
                }
            }
        });


        // Establishing buttons and fragment container
        administratorDeleteEventButton = view.findViewById(R.id.administratorDeleteEventButton);
        Button backButton = view.findViewById((R.id.eventViewBackButton));
        FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);

        // Set visibility of "Delete Event" button for Administrator
        administratorDeleteEventButton.setVisibility(View.VISIBLE);


        // Join/Leave waitlist button onclick activity
        administratorDeleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add userId to event waitlist
                eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
                    if (event != null) {
                        // Delete event from database
                        eventViewModel.deleteEvent(currentEventId);

                        // Go back to Events screen
                        getParentFragmentManager().popBackStack();

                        // Remove white background from fragment container
                        fragmentContainer.setBackgroundColor(Color.TRANSPARENT);
                    }
                    else {
                        Toast.makeText(getContext(), "Event was not deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Back button onclick activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to Events screen
                getParentFragmentManager().popBackStack();

                // Remove white background from fragment container
                fragmentContainer.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }
}
