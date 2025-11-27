package com.example.coolioevents.administrator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.example.coolioevents.User;
import com.example.coolioevents.administrator.AdministratorEventArrayAdapter;
import com.example.coolioevents.administrator.AdministratorEventFragment;
import com.example.coolioevents.administrator.AdministratorHomeActivity;
import com.example.coolioevents.events.EventFragment;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Copyright 2025 Juliane Phan
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
 * This class represents the administrator events screen.
 * It displays a list of all events.
 * Administrators can click an event to delete it.
 *
 * RATIONALE:
 * This class was designed to allow administrators to browse and delete events.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-20
 */

public class AdministratorEventsActivity extends AppCompatActivity {
    private EventViewModel eventViewModel; // View Model eventList up to date with database
    private ArrayList<Event> eventsList; // ArrayList containing all events
    private AdministratorEventArrayAdapter eventAdapter; // Array adapter for events
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrator_events);

        // Establishing views
        ImageButton backButton = findViewById(R.id.btnBack);
        ListView eventsListView = findViewById(R.id.eventList);

        // Establishing Adapter
        eventsList = new ArrayList<Event>();
        eventAdapter = new AdministratorEventArrayAdapter(this, eventsList);
        eventsListView.setAdapter(eventAdapter);

        // Establish ViewModel
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        eventViewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            // When event List in viewmodel is updated, update eventList too (as well as notify array adapter)
            @Override
            public void onChanged(ArrayList<Event> events) {
                eventsList.clear();
                System.out.println("CHANGED OMG");
                eventsList.addAll(events);
                Collections.sort(eventsList);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Click specific event --> Show fragment with event details and delete button
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = (Event) parent.getItemAtPosition(position);

                // If event is null do nothing
                if (clickedEvent == null) {
                    return;
                }

                // Set the fragment's background colour
                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                fragmentContainer.setBackgroundResource(R.drawable.whitebackground);

                AdministratorEventFragment eventFragment = AdministratorEventFragment.newInstance(clickedEvent.getEventId());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventFragment) // Replace the current fragment
                        .addToBackStack(null) // This allows the user to press the back button to return to the list
                        .commit();
            }
        });


        // Back button onclick activity --> Leads to Home activity
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorHomeActivity.class)));
        }


    }
}