package com.example.coolioevents.events;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
 * @version 1.5
 * @since 2025-11-05
 */
public class EventFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventViewModel eventViewModel;
    private FirebaseUser currentUser;
    private String currentEventId;
    private Event currentEvent;
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
    private ChipGroup tagsGroup;
    private TextView eventWaitlistEntrantCount;
    private Button joinLeaveWaitlistButton;
    private Button acceptInviteButton;
    private Button declineInviteButton;
    private Button unregisterButton;
    private int waitlistCount;
    private LinearLayout acceptAndDecline;
    private LinearLayout joinWaitlist;
    private LinearLayout unregister;
    private LinearLayout adminDelete;

    /*
    Taken From:  https://developer.android.com/develop/sensors-and-location/location/retrieve-current
        License: http://www.apache.org/licenses/LICENSE-2.0
        Authored by: Android Developers
        Taken by: Avery Dancocks
        Taken on: 11/23/25
    */
    // Handling Geolocation
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

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


    /*
    Taken from: https://stackoverflow.com/questions/48717021/setbackgroundtintlist-for-button-programmatically-with-a-hex-value-colordrawab
        License: https://creativecommons.org/licenses/by-sa/4.0/
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

            joinWaitlist.setVisibility(View.VISIBLE);
            acceptAndDecline.setVisibility(View.GONE);
            unregister.setVisibility(View.GONE);

            // Set text and colour of button
            joinLeaveWaitlistButton.setText("Leave Waitlist");
            joinLeaveWaitlistButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.light_red)));
            joinLeaveWaitlistButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            // Set text and colour of user status
            eventUserStatusView.setText("In Waitlist");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.purple_widget));
            eventUserStatusView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_purple));
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

            joinWaitlist.setVisibility(View.VISIBLE);
            acceptAndDecline.setVisibility(View.GONE);
            unregister.setVisibility(View.GONE);

            // Set text and colour of button
            joinLeaveWaitlistButton.setText("Join Waitlist");
            joinLeaveWaitlistButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.dark_green)));
            joinLeaveWaitlistButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            // Set text and colour of user status
            eventUserStatusView.setText("Not in Waitlist");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.grey_widget));
            eventUserStatusView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        }

        // User is chosen --> Button shows options to accept or decline the invite, user status says "Chosen" and "Not Registered"
        if (isUserChosen) {
            // Set visibility of buttons and waitlist entrants count
            joinLeaveWaitlistButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.VISIBLE);
            declineInviteButton.setVisibility(View.VISIBLE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.GONE);

            joinWaitlist.setVisibility(View.GONE);
            acceptAndDecline.setVisibility(View.VISIBLE);
            unregister.setVisibility(View.GONE);

            // Set visibility of registration status
            eventUserStatusRegistrationView.setVisibility(View.VISIBLE);

            // Set text and colour of user status
            eventUserStatusView.setText("Chosen");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_widget));
            eventUserStatusView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));

        }

        // User is accepted --> Button shows options to unregister from the event, user status says "Chosen" and "Registered"
        if (isUserAccepted) {
            // Set visibility of buttons and waitlist entrants count
            joinLeaveWaitlistButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);
            eventWaitlistEntrantCount.setVisibility(View.GONE);

            joinWaitlist.setVisibility(View.GONE);
            acceptAndDecline.setVisibility(View.GONE);
            unregister.setVisibility(View.VISIBLE);

            // Set visibility of registration status
            eventUserStatusRegistrationView.setVisibility(View.VISIBLE);

            // Set text and colour of user status
            eventUserStatusView.setText("Chosen");
            eventUserStatusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_widget));
            eventUserStatusView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));

            eventUserStatusRegistrationView.setText("Registered");
            eventUserStatusRegistrationView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_widget));
            eventUserStatusRegistrationView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEventId = getArguments().getString("event_id");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Geolocation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        /*
        Taken from: https://stackoverflow.com/questions/62202471/how-to-get-a-permission-request-in-new-activityresult-api-1-3-0-alpha05
            License: https://creativecommons.org/licenses/by-sa/4.0/
            Authored by: ACE
            Taken by: Avery Dancocks
            Taken on: 11/23/25
        */
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission was granted
                Log.d("Waitlist", "Permission granted from dialog. Joining waitlist.");
                joinWaitlistWithGeolocationCheck();
            } else {
                // Permission was denied
                Toast.makeText(getContext(), "Location permission is required to join this waitlist.", Toast.LENGTH_LONG).show();
            }
        });
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
        tagsGroup = view.findViewById(R.id.tagsGroup);
        //New layouts
        acceptAndDecline = view.findViewById(R.id.invite_button_layout);
        joinWaitlist = view.findViewById(R.id.join_button_layout);
        unregister = view.findViewById(R.id.unregister_button_layout);
        adminDelete = view.findViewById(R.id.delete_event_button_layout);

        // Getting ViewModel and displaying event details
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        //TODO: Implement a check to make sure the event ID exists
        eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                currentEvent = event;
                EventDetails details = event.getDetails();
                if (details != null) {
                    currentEvent.setDetails(details);
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
                    eventDescriptionTextView.setText(String.format("%s", event.getDetails().getEventDescription()));
                    if (event.getDetails().getEventLocation() != null){
                        eventLocationTextView.setText(String.format("%s",event.getDetails().getEventLocation())); // Sets event location if not null
                    }
                    else {
                        eventLocationTextView.setText("Event Location: Not Available"); // Sets event location if  null
                    }
                    if (event.getDetails().getEventDateTime() != null){
                        Date eventDate = event.getDetails().getEventDateTime();
                        String pattern = "MMM d h:mma";
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
                        String formattedTime = sdf.format(eventDate);
                        formattedTime = formattedTime.replace("AM", "am").replace("PM", "pm");
                        eventTimeTextView.setText(formattedTime);
                    }
                    else {
                        eventLocationTextView.setText("Time: Not Available"); // Sets event time if  null
                    }

                    // Formatting registration period
                    /*
                    Taken From: Google Gemini
                        Prompt: How to format a string date with a hyphen
                        Taken By: Avery Dancocks
                        Taken On: 11/29/25
                     */
                    String dateRangeString = String.valueOf(details.getRegistrationPeriod());

                    String finalFormattedDate = "Date not available";

                    if (dateRangeString != null && dateRangeString.contains("-")) {
                        try {
                            // Split the string into start and end date parts
                            String[] dates = dateRangeString.split("\\s*-\\s*");
                            String startDateString = dates[0]; // "2025/12/13"
                            String endDateString = dates[1];   // "2025/12/14"

                            // Define the formatter for the inputted format
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

                            // Create formatter
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

                            // Parse input strings into LocalDate objects
                            LocalDate startDate = LocalDate.parse(startDateString, inputFormatter);
                            LocalDate endDate = LocalDate.parse(endDateString, inputFormatter);

                            // Format the LocalDate objects into the new string format
                            String formattedStartDate = startDate.format(outputFormatter);
                            String formattedEndDate = endDate.format(outputFormatter);

                            // Combine into the final string
                            finalFormattedDate = String.format("%s - %s", formattedStartDate, formattedEndDate);

                        } catch (Exception e) {
                            finalFormattedDate = dateRangeString;
                            e.printStackTrace();
                            Log.e("DateFormattingError", "An unexpected error occurred for date string: " + dateRangeString, e);
                        }

                    }

                    eventRegistrationPeriodTextView.setText(finalFormattedDate);
                    //eventRegistrationPeriodTextView.setText(String.format("%s", String.valueOf(details.getRegistrationPeriod())));
                    eventEntrantLimitTextView.setText(String.format("%s", String.valueOf(details.getEntrantLimit())));
                    eventWaitlistEntrantCount.setText(String.format("People in the Waitlist: %s", String.valueOf(event.getWaitlistEntrants().size())));

                    // UI set up specifically for organizer
                    String organizerId = event.getOrganizerId();
                    if (organizerId != null) {
                        eventViewModel.getOrganizerById(organizerId).observe(getViewLifecycleOwner(), organizer -> {
                            if (organizer != null && organizer.getProfile() != null) {
                                String username = organizer.getProfile().getUsername();
                                if (username != null) { // Organizer has a username
                                    eventOrganizerTextView.setText(String.format("%s", username)); // Set the text for organizer
                                }
                                else {
                                    eventOrganizerTextView.setText("Posted By: Unknown");
                                }
                            }
                        });
                    }
                    /*
                    Taken from: https://stackoverflow.com/questions/45232608/how-to-load-image-into-imageview-from-url-using-glide-v4-0-0rc1
                        License: https://creativecommons.org/licenses/by-sa/4.0/
                        Authored by: Parmesh Bahala
                        Taken by: Avery Dancocks
                        Taken on: 11/23/25
                     */
                    // Set event image with Glide
                    Glide.with(this)
                            .load(event.getDetails().getPosterUrl()) // loads poster URL
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_error)
                            .fallback(R.drawable.ic_image_placeholder) // If imageURL is null
                            .into(eventPosterImageView);

                    // Keeping track of current waitlist size in a figure
                    waitlistCount = event.getWaitlistEntrants().size();

                    if (event.getDetails().getStatus().equals("open")) {
                        // If event open make text open with green background
                        eventStatusTextView.setText("Open");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.medium_green_widget));
                    }
                    else{
                        // If event closed make text open with red background
                        eventStatusTextView.setText("Closed");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_widget));
                    }

                    //Set event tags
                    if (event.getDetails().getTags() != null){
                        /*
                        Taken From: Google Gemini
                            Prompt: How do i customize tags?
                            Taken By: Avery Dancocks
                            Taken On: 11/28/25
                         */
                        final Typeface poppinsFont = ResourcesCompat.getFont(getContext(), R.font.poppins_bold);

                        for (String tagString : event.getDetails().getTags()){
                            Chip tag = new Chip(getContext());
                            final float scale = getContext().getResources().getDisplayMetrics().density;
                            tag.setText(tagString);
                            tag.setChipStrokeWidth(1.5f * getContext().getResources().getDisplayMetrics().density); // Use dp for consistency
                            tag.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.dark_grey)));
                            tag.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
                            tag.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                            tag.setTypeface(poppinsFont);

                            tag.setShapeAppearanceModel(
                                    tag.getShapeAppearanceModel()
                                            .toBuilder()
                                            .setAllCornerSizes(new RelativeCornerSize(0.5f))
                                            .build()
                            );
                            tag.setClickable(false);
                            tagsGroup.addView(tag);
                        }
                    }
                }
            }
        });


        // Establishing Buttons
        joinLeaveWaitlistButton = view.findViewById(R.id.eventViewJoinWaitListButton);
        acceptInviteButton = view.findViewById(R.id.eventAcceptInviteButton);
        declineInviteButton = view.findViewById(R.id.eventDeclineInviteButton);
        unregisterButton = view.findViewById(R.id.eventUnregisterButton);
        FrameLayout backButton = view.findViewById((R.id.eventViewBackButton));


        // Join/Leave waitlist button onclick activity
        joinLeaveWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add userId to event waitlist
                    if (currentEvent != null) {
                        String currentUserId = currentUser.getUid();

                        // Look at if user is on waitlist or not to see what the button click did
                        if (isUserOnWaitList) { //User is currently in waiting list
                            eventViewModel.leaveWaitlist(currentEventId, currentUserId);
                            Toast.makeText(getContext(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();

                            // Update and display new waitlist count
                            waitlistCount--;
                            eventWaitlistEntrantCount.setText(String.format("People in the Waitlist: %s", String.valueOf(waitlistCount))); //Update waitlist count

                            // Change the User state
                            isUserOnWaitList = !isUserOnWaitList;
                            // Change button state
                            updateButtonState();
                        }
                        else { // User is NOT currently on waitlist
                            
                            Integer limitObject = currentEvent.getDetails().getWaitingListLimit();

                            if (limitObject == null) {
                                // Otherwise allow joining
                                joinWaitlistWithGeolocationCheck();
                            }
                            else {
                                int limit = limitObject.intValue();
                                Log.e("Waitlist limit", "Limit: "+ limit);
                                //Block user from joining if full
                                if (waitlistCount >= limit) {
                                    Toast.makeText(getContext(),
                                            "The waiting list is full. Please check back later.",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        }
                    }
                    else {
                        Toast.makeText(getContext(), "You were not added to the waitlist.", Toast.LENGTH_SHORT).show();
                    }
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

    private void joinWaitlistWithGeolocationCheck() {
        if (currentEvent == null) return;

        if (currentEvent.isGeolocationVerificationEnabled()) { // Geolocation required
            // Check we have permission
            /*
             Taken from: https://developer.android.com/training/permissions/requesting
                License: http://www.apache.org/licenses/LICENSE-4.0
                Authored by: Android Developers
                Taken by: Avery Dancocks
                Taken on: 11/23/25
             */
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // We have location permission
                Log.d("Waitlist", "Permission already granted. Fetching location.");
                getLocationAndJoin();
            }
            else {
                // We don't have permission, so we will request it
                Log.d("Waitlist", "Permission not granted. Requesting it now.");
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        else { // Geolocation not required
            String currentUserId = currentUser.getUid();
            // Add Entrant to Waitlist
            eventViewModel.joinWaitlist(currentEventId, currentUserId, null); // Pass null for location
            Toast.makeText(getContext(), "You have been added to the waitlist.", Toast.LENGTH_SHORT).show();

            // Update and display new waitlist count
            waitlistCount++;
            eventWaitlistEntrantCount.setText(String.format("People in the Waitlist: %s", String.valueOf(waitlistCount))); //Update waitlist count

            // Change the User state
            isUserOnWaitList = !isUserOnWaitList;

            // Change button state
            updateButtonState();
        }
    }

    @SuppressLint("MissingPermission") // Only called after checking permission
    private void getLocationAndJoin() {
        //
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) { // Location was found
                /*
                Taken from: https://stackoverflow.com/questions/11645273/getting-the-user-geopoint
                    License: https://creativecommons.org/licenses/by-sa/3.0/
                    Authored by: User
                    Taken by: Avery Dancocks
                    Taken on: 11/23/25
                 */
                // Getting location of entrant
                GeoPoint entrantLocation;
                entrantLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                String currentUserId = currentUser.getUid();

                // Add Entrant to Waitlist
                eventViewModel.joinWaitlist(currentEventId, currentUserId, entrantLocation);
                Toast.makeText(getContext(), "You have been added to the waitlist.", Toast.LENGTH_SHORT).show();

                // Update and display new waitlist count
                waitlistCount++;
                eventWaitlistEntrantCount.setText(String.format("People in the Waitlist: %s", String.valueOf(waitlistCount))); //Update waitlist count

                // Change the User state
                isUserOnWaitList = !isUserOnWaitList;

                // Change button state
                updateButtonState();
            }
            else { // Location was not found
                Toast.makeText(getContext(), "Could not get your location. Please enable location tracking and try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

