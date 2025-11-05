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

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantHomeFragment extends Fragment {
    EntrantViewModel viewModel; // View Model containing eventList, organizerList, entrantList
    ArrayList<Event> eventsList; // Home specific arraylist for array adapter ()
    EventArrayAdapter eventAdapter; // Array adapter for events
    ListView eventsListView; // ListView on home fragment screen
    EventViewModel eventViewModel; //EventViewModel for clicking events

    public EntrantHomeFragment() {
        // Required empty public constructor
    }

    public static EntrantHomeFragment newInstance() {
        EntrantHomeFragment fragment = new EntrantHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        eventsList = new ArrayList<>();

        viewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            // When eventlist in viewmodel is updated, update eventList too (aswell as notify array adapter)
            @Override
            public void onChanged(ArrayList<Event> events) {
                eventsList.clear();
                System.out.println("CHANGED OMG");
                for (Event event : events){
                    if (event.getDetails().getStatus().equals("open")){
                        //Only add events that are currently open
                        eventsList.add(event);
                     }
                Collections.sort(eventsList);
                eventAdapter.notifyDataSetChanged();
            }
        }});

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrant_home, container, false);
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

}