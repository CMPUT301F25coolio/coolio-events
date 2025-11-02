package com.example.coolioevents.Entrant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventArrayAdapter;
import com.example.coolioevents.events.EventFragment;
import com.example.coolioevents.events.EventViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantMyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantMyEventsFragment extends Fragment {
    EntrantViewModel viewModel; // View Model containing eventList, organizerList, entrantList
    FirebaseAuth mAuth;
    ArrayList<Event> eventsList; // My Event specific arraylist for array adapter ()
    EventArrayAdapter eventAdapter; // Array adapter for events
    ListView eventsListView; // ListView on myevents fragment screen
    EventViewModel eventViewModel; //EventViewModel for clicking events
    FirebaseUser user;

    public EntrantMyEventsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EntrantMyEventsFragment newInstance(String param1, String param2) {
        EntrantMyEventsFragment fragment = new EntrantMyEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        eventsList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        viewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            // When eventlist in viewmodel is updated, update eventList too (aswell as notify array adapter)
            @Override
            public void onChanged(ArrayList<Event> events) {
                eventsList.clear();
                System.out.println("CHANGED OMG");
                for (Event event : events){
                    String userUID = user.getUid();
                    List<String> waitlistEntrants = event.getWaitlistEntrants();
                    List<String> chosenEntrants = event.getChosenEntrants();
                    List<String> acceptedEntrants = event.getAcceptedEntrants();

                    // Show events where the user is in the waitlist, is chosen, or accepted their invite
                    if (waitlistEntrants.contains(userUID) || chosenEntrants.contains(userUID) || acceptedEntrants.contains(userUID)){
                        eventsList.add(event);
                    }
                    Collections.sort(eventsList);
                    eventAdapter.notifyDataSetChanged();
                }
            }});
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsListView = view.findViewById(R.id.eventList);
        eventAdapter = new EventArrayAdapter(getActivity(), eventsList);
        eventsListView.setAdapter(eventAdapter);

        //Getting EventViewModel
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        //Navigating to Event Fragment
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = (Event) parent.getItemAtPosition(position);

                //If event is null do nothing
                if (clickedEvent == null) {
                    return;
                }

                EventFragment eventDetailsFragment = EventFragment.newInstance(clickedEvent.getEventId());

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventDetailsFragment) // Replace the current fragment
                        .addToBackStack(null) // This allows the user to press the back button to return to the list
                        .commit();
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrant_my_events, container, false);
    }
}