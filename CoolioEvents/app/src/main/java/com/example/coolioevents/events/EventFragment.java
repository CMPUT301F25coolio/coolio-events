package com.example.coolioevents.events;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Entrant.EntrantHomeFragment;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Copyright 2025 Avery Dancocks, Juliane Phan
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
 * This class represents a fragment for a single event.
 * Contains methods to initialize the layout as well as deals with
 * the actions given to different button controllers in the fragment.
 * It displays the details of an event.
 *
 * RATIONALE:
 * Utilizes an event view model to retrieve the details of the event
 * from a previous fragment.
 *
 * @author Avery Dancocks, Juliane Phan
 * @version 1.0
 * @since 2025-11-05
 */
public class EventFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventViewModel eventViewModel;
    private FirebaseUser currentUser;
    private String currentEventId;
    private boolean isUserOnWaitList = false;
    private boolean isUserChosen = false;
    private boolean isUserAccepted = false;

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
    private TextView eventUserStatusView;
    private TextView eventUserStatusRegistrationView;
    private TextView eventWaitlistEntrantCount;
    private Button joinLeaveWaitlistButton;
    private Button acceptInviteButton;
    private Button declineInviteButton;
    private Button unregisterButton;
    private int waitlistCount;

    /**
     * This is a constructor for the Event Fragment
     *
     * @param eventId
     *      the event we want the fragment to display
     * @return the fragment
     */
    public static EventFragment newInstance(String eventId) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventId); // Bundle holds the event id
        fragment.setArguments(args); // Attach the bundle to the fragment
        return fragment;
    }


    /*Taken from: https://stackoverflow.com/questions/48717021/setbackgroundtintlist-for-button-programmatically-with-a-hex-value-colordrawab
            License: http://www.apache.org/licenses/LICENSE-4.0
            Authored by: Rejesh Satvara
            Taken by: Avery Dancocks
            Taken on: 10/29/25
        */
    /**
     * This is a helper function to update the state of the UI
     */
    private void updateButtonState() {
        // User is on the waitlist --> Button shows option to leave, user status says "In Waitlist"
        if (isUserOnWaitList) {
            // Set visibility of buttons, waitlist entrants count, and user statuses
            joinLeaveWaitlistButton.setVisibility(View.VISIBLE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.VISIBLE);
            eventUserStatusRegistrationView.setVisibility(View.GONE);

            // Set text and colour of button
            joinLeaveWaitlistButton.setText("Leave Waitlist");
            joinLeaveWaitlistButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.leavewaitinglist)));

            // Set text and colour of user status
            eventUserStatusView.setText("In Waitlist");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));
        }

        // User is NOT on the waitlist --> Button shows option to join, user status says "Not in Waitlist"
        if (!isUserOnWaitList && !isUserChosen && !isUserAccepted) {
            // Set visibility of buttons, waitlist entrants count, and user statuses
            joinLeaveWaitlistButton.setVisibility(View.VISIBLE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.VISIBLE);
            eventUserStatusRegistrationView.setVisibility(View.GONE);

            // Set text and colour of button
            joinLeaveWaitlistButton.setText("Join Waitlist");
            joinLeaveWaitlistButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.joinwaitinglist)));

            // Set text and colour of user status
            eventUserStatusView.setText("Not in Waitlist");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greybackground));
        }

        // User is chosen --> Button shows options to accept or decline the invite, user status says "Chosen" and "Not Registered"
        if (isUserChosen) {
            // Set visibility of buttons and waitlist entrants count
            joinLeaveWaitlistButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.VISIBLE);
            declineInviteButton.setVisibility(View.VISIBLE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.GONE);

            // Set visibility of registration status
            eventUserStatusRegistrationView.setVisibility(View.VISIBLE);

            // Set text and colour of user status
            eventUserStatusView.setText("Chosen");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));
        }

        // User is accepted --> Button shows options to unregister from the event, user status says "Chosen" and "Registered"
        if (isUserAccepted) {
            // Set visibility of buttons and waitlist entrants count
            joinLeaveWaitlistButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);
            eventWaitlistEntrantCount.setVisibility(View.GONE);

            // Set visibility of registration status
            eventUserStatusRegistrationView.setVisibility(View.VISIBLE);

            // Set text and colour of user status
            eventUserStatusView.setText("Chosen");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));

            eventUserStatusRegistrationView.setText("Registered");
            eventUserStatusRegistrationView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEventId = getArguments().getString("event_id");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
        eventUserStatusView = view.findViewById(R.id.eventViewUserStatus);
        eventWaitlistEntrantCount = view.findViewById(R.id.eventWaitlistEntrantCount);
        eventUserStatusRegistrationView = view.findViewById(R.id.eventViewUserStatusRegistration);

        // Getting ViewModel and displaying event details
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        //TODO: Implement a check to make sure the event ID exists
        eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                EventDetails details = event.getDetails();
                if (details != null) {
                    // Determining User Status
                    List<String> waitlist = event.getWaitlistEntrants();
                    List<String> chosenEntrants = event.getChosenEntrants();
                    List<String> acceptedEntrants = event.getAcceptedEntrants();
                    String userId = currentUser.getUid();

                    isUserOnWaitList = waitlist.contains(userId);
                    isUserChosen = chosenEntrants.contains(userId);
                    isUserAccepted = acceptedEntrants.contains(userId);

                    // Change button based on user status
                    updateButtonState();

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
                    if (event.getDetails().getEventTime() != null){
                        eventTimeTextView.setText(String.format("Time: %s",event.getDetails().getEventTime())); // Sets event time if not null
                    }
                    else {
                        eventLocationTextView.setText("Time: Not Available"); // Sets event time if  null
                    }
                    eventRegistrationPeriodTextView.setText(String.format("Registration Period: %s", String.valueOf(details.getRegistrationPeriod())));
                    eventEntrantLimitTextView.setText(String.format("Max Entrees: %s", String.valueOf(details.getEntrantLimit())));
                    eventWaitlistEntrantCount.setText(String.format("%s PEOPLE IN WAITING LIST", String.valueOf(event.getWaitlistEntrants().size())));

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

                    // Keeping track of current waitlist size in a figure
                    waitlistCount = event.getWaitlistEntrants().size();

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


        // Establishing Buttons
        joinLeaveWaitlistButton = view.findViewById(R.id.eventViewJoinWaitListButton);
        acceptInviteButton = view.findViewById(R.id.eventAcceptInviteButton);
        declineInviteButton = view.findViewById(R.id.eventDeclineInviteButton);
        unregisterButton = view.findViewById(R.id.eventUnregisterButton);
        Button backButton = view.findViewById((R.id.eventViewBackButton));


        // Join/Leave waitlist button onclick activity
        joinLeaveWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add userId to event waitlist
                eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
                    if (event != null) {
                        String currentUserId = currentUser.getUid();

                        // Look at if user is on waitlist or not to see what the button click did
                        if (isUserOnWaitList) { //User is currently in waiting list
                            eventViewModel.leaveWaitlist(currentEventId, currentUserId);
                            Toast.makeText(getContext(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();

                            // Update and display new waitlist count
                            waitlistCount--;
                            eventWaitlistEntrantCount.setText(String.format("%s PEOPLE IN WAITING LIST", String.valueOf(waitlistCount))); //Update waitlist count
                        }
                        else { //User not currently in waiting list
                            eventViewModel.joinWaitlist(currentEventId, currentUserId);
                            Toast.makeText(getContext(), "You have been added to the waitlist.", Toast.LENGTH_SHORT).show();

                            // Update and display new waitlist count
                            waitlistCount++;
                            eventWaitlistEntrantCount.setText(String.format("%s PEOPLE IN WAITING LIST", String.valueOf(waitlistCount))); //Update waitlist count
                        }

                        // Change the User state
                        isUserOnWaitList = !isUserOnWaitList;
                        // Change button state
                        updateButtonState();
                    }
                    else {
                        Toast.makeText(getContext(), "You were not added to the waitlist.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Accept invite button onclick activity
        acceptInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
                    if (event != null) {
                        String currentUserId = currentUser.getUid();
                        eventViewModel.acceptInvite(currentEventId, currentUserId);  // Update firebase
                        Toast.makeText(getContext(), "You have registered for this event.", Toast.LENGTH_SHORT).show();  // Confirmation message

                        // Change the User state
                        isUserChosen = false;
                        isUserAccepted = true;
                        // Change button state
                        updateButtonState();

                        // Hide Accept/Decline buttons and show Unregister button
                        acceptInviteButton.setVisibility(View.GONE);
                        declineInviteButton.setVisibility(View.GONE);
                        unregisterButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(getContext(), "You were not registered for this event.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Decline invite button onclick activity
        declineInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
                    if (event != null) {
                        String currentUserId = currentUser.getUid();
                        eventViewModel.declineInvite(currentEventId, currentUserId);  // Update firebase
                        Toast.makeText(getContext(), "You have declined this event.", Toast.LENGTH_SHORT).show();  // Confirmation message

                        // Change the User state
                        isUserChosen = false;
                        // Change button state
                        updateButtonState();

                        // Go back to My Events fragment
                        getParentFragmentManager().popBackStack();
                    }
                    else {
                        Toast.makeText(getContext(), "You did not decline this event.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Unregister button onclick activity
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
                    if (event != null) {
                        String currentUserId = currentUser.getUid();
                        eventViewModel.unregisterFromEvent(currentEventId, currentUserId);  // Update firebase
                        Toast.makeText(getContext(), "You have unregistered from this event.", Toast.LENGTH_SHORT).show();  // Confirmation message

                        // Change the User state
                        isUserAccepted = false;
                        // Change button state
                        updateButtonState();

                        // Go back to My Events fragment
                        getParentFragmentManager().popBackStack();
                    }
                    else {
                        Toast.makeText(getContext(), "You were not unregistered from this event.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Back button onclick activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to home fragment
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
