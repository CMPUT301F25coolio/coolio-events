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

import java.util.List;

public class EventFragment extends Fragment {

    private EventViewModel eventViewModel;
    private FirebaseUser currentUser;
    private String currentEventId;
    private boolean isUserOnWaitList = false;
    private boolean isUserChosen = false;
    private boolean isUserAccepted = false;

    //Attributes for displaying details
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private ImageView eventPosterImageView;
    private TextView eventTimeTextView;
    private TextView eventRegistrationPeriodTextView;
    private TextView eventEntrantLimitTextView;
    private TextView eventStatusTextView;
    private TextView eventUserStatusView;
    private TextView eventWaitlistEntrantCount;
    private Button joinLeaveWaitlistButton;
    private Button acceptInviteButton;
    private Button declineInviteButton;
    private Button unregisterButton;

    public static EventFragment newInstance(String eventId) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventId); //Bundle holds the event id
        fragment.setArguments(args); //Attach the bundle to the fragment
        return fragment;
    }

    //getting color and setting button background - https://stackoverflow.com/questions/48717021/setbackgroundtintlist-for-button-programmatically-with-a-hex-value-colordrawab
    // Rajesh Satvara on oct29
    // Helper function that could be turned into a class later for simplicity
    private void updateButtonState() {
        // User is on the waitlist --> Button shows option to leave
        if (isUserOnWaitList) {
            // Set visibility of buttons
            joinLeaveWaitlistButton.setVisibility(View.VISIBLE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.VISIBLE);

            // Set text and colour of button
            joinLeaveWaitlistButton.setText("Leave Waitlist");
            joinLeaveWaitlistButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.leavewaitinglist)));
        }

        // User is NOT on the waitlist --> Button shows option to join
        if (!isUserOnWaitList && !isUserChosen && !isUserAccepted) {
            // Set visibility of buttons
            joinLeaveWaitlistButton.setVisibility(View.VISIBLE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.VISIBLE);

            // Set text and colour of button
            joinLeaveWaitlistButton.setText("Join Waitlist");
            joinLeaveWaitlistButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.joinwaitinglist)));
        }

        // User is chosen --> Button shows options to accept or decline the invite
        if (isUserChosen) {
            // Set visibility of buttons
            joinLeaveWaitlistButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.VISIBLE);
            declineInviteButton.setVisibility(View.VISIBLE);
            unregisterButton.setVisibility(View.GONE);
            eventWaitlistEntrantCount.setVisibility(View.GONE);
        }

        // User is accepted --> Button shows options to unregister from the event
        if (isUserAccepted) {
            // Set visibility of buttons
            joinLeaveWaitlistButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.GONE);
            declineInviteButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);
            eventWaitlistEntrantCount.setVisibility(View.GONE);
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

        //Establishing UI components
        eventNameTextView = view.findViewById(R.id.eventViewName);
        eventDescriptionTextView = view.findViewById(R.id.eventViewDescription);
        eventPosterImageView = view.findViewById(R.id.eventViewPoster);
        eventTimeTextView = view.findViewById(R.id.eventViewTime);
        eventRegistrationPeriodTextView = view.findViewById(R.id.eventViewRegistrationPeriod);
        eventEntrantLimitTextView = view.findViewById(R.id.eventViewLimit);
        eventStatusTextView = view.findViewById(R.id.eventViewEventStatus);
        eventUserStatusView = view.findViewById(R.id.eventViewUserStatus);
        eventWaitlistEntrantCount = view.findViewById(R.id.eventWaitlistEntrantCount);

        //Getting ViewModel and displaying event details
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        //TODO: Implement a check to make sure the event ID exists
        eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                EventDetails details = event.getDetails();
                if (details != null) {
                    //Determining User Status
                    List<String> waitlist = event.getWaitlistEntrants();
                    List<String> chosenEntrants = event.getChosenEntrants();
                    List<String> acceptedEntrants = event.getAcceptedEntrants();
                    String userId = currentUser.getUid();

                    isUserOnWaitList = waitlist.contains(userId);
                    isUserChosen = chosenEntrants.contains(userId);
                    isUserAccepted = acceptedEntrants.contains(userId);

                    //Change button based on user status
                    updateButtonState();
                    System.out.println("WE MADE IT HERE");
                    //Updating UI components to match clicked event
                    eventNameTextView.setText(details.getEventName());
                    eventDescriptionTextView.setText(details.getEventDescription());

                    //eventPosterImageView - how to do
                    // -- something to do with getPosterUrl() in events
                    //eventTimeTextView.setText(details.getEventTime()); - add getEventTime
                    eventRegistrationPeriodTextView.setText(details.getRegistrationPeriod());
                    eventEntrantLimitTextView.setText(String.valueOf(details.getEntrantLimit()));

                    if (event.getDetails().getStatus().equals("open")) {
                        //If event open make text open with green background
                        eventStatusTextView.setText("Open");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));
                    }
                    else{
                        //If event closed make text open with red background
                        eventStatusTextView.setText("Closed");
                        eventStatusTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.redshapebackground));
                    }
                    //eventUserStatusView = view.findViewById(R.id.eventViewUserStatus);
                }
            }
        });

        //Establishing Buttons
        joinLeaveWaitlistButton = view.findViewById(R.id.eventViewJoinWaitListButton);
        acceptInviteButton = view.findViewById(R.id.eventAcceptInviteButton);
        declineInviteButton = view.findViewById(R.id.eventDeclineInviteButton);
        unregisterButton = view.findViewById(R.id.eventUnregisterButton);
        Button backButton = view.findViewById((R.id.eventViewBackButton));

        // Join/Leave waitlist button onclick activity
        joinLeaveWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add userId to event waitlist
                eventViewModel.getEventById(currentEventId).observe(getViewLifecycleOwner(), event -> {
                    if (event != null) {
                        String currentUserId = currentUser.getUid();

                        //Look at if user is on waitlist or not to see what the button click did
                        if (isUserOnWaitList) { //User is currently in waiting list
                            eventViewModel.leaveWaitlist(currentEventId, currentUserId);
                            Toast.makeText(getContext(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();
                        }
                        else { //User not currently in waiting list
                            eventViewModel.joinWaitlist(currentEventId, currentUserId);
                            Toast.makeText(getContext(), "You have been added to the waitlist.", Toast.LENGTH_SHORT).show();
                        }

                        //Change the User state
                        isUserOnWaitList = !isUserOnWaitList;
                        //Change button state
                        updateButtonState();

                        //Update number of people in event waitlist
                        //updateWaitingListCount()

                        //TODO: update the user status/figure out best way to show user status
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

                        //Change the User state
                        isUserChosen = false;
                        isUserAccepted = true;
                        //Change button state
                        updateButtonState();

                        // TEMPORARY: Go back to My Events fragment
                        getParentFragmentManager().popBackStack();
                        // TODO: Juliane - Show unregister button once user accepts invite
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

                        //Change the User state
                        isUserChosen = false;
                        //Change button state
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

                        //Change the User state
                        isUserAccepted = false;
                        //Change button state
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
                //Go back to home fragment
                getParentFragmentManager().popBackStack();
            }
        });

    }
}
