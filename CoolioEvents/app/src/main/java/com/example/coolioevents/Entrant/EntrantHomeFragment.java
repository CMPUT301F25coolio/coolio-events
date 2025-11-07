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
import com.example.coolioevents.events.EntrantEventArrayAdapter;
import com.example.coolioevents.events.EventFragment;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Copyright 2025 Ethan Diep
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
 * This class represents the Entrant's Home Fragment
 * It shows a entrant's home page consisting of a list of events
 * sorted by most recently posted. Each event has some details shown
 * about it including its title, organizer, time, registration period,etc.
 * The user can click on an event to view more details of the event and join the event.
 *
 * RATIONALE:
 * This class was designed to allow users to browse events they may be
 * interested in.
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-06
 */
public class EntrantHomeFragment extends Fragment {
    EventViewModel eventViewModel; // View Model with eventList up to date with database
    ArrayList<Event> eventsList; // Home specific arraylist for array adapter ()
    EntrantEventArrayAdapter eventAdapter; // Array adapter for events
    ListView eventsListView; // ListView on home fragment screen
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


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

        // eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Source - https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
        // Posted by mlykotom
        // Retrieved by Juliane Phan on 2025-11-06, License - CC BY-SA 4.0
        // Used to instantiate the EventViewModel which uses the EventViewModel Factory class
        // Modifications made: Used our own class and parameter names
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        eventsList = new ArrayList<>();

        eventViewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            // When event list in viewmodel is updated, update eventList too (as well as notify array adapter)
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
        eventsListView = view.findViewById(R.id.eventList); // Listview in home
        eventAdapter = new EntrantEventArrayAdapter(getActivity(), eventsList); // Make new event adapter linked to eventList
        eventsListView.setAdapter(eventAdapter); // Make listview have adapter connected to eventList

        // Navigating to Event Fragment
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = (Event) parent.getItemAtPosition(position);

                // If event is null do nothing
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