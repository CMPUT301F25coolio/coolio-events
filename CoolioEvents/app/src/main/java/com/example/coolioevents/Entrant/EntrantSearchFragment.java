package com.example.coolioevents.Entrant;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EntrantEventArrayAdapter;
import com.example.coolioevents.events.EventFragment;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.zxing.Result;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Copyright 2025 Ethan Diep, Niharika Rawat
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
 * @author Ethan Diep, Niharika Rawat
 * @version 1.5
 * @since 2025-11-06
 */
public class EntrantSearchFragment extends Fragment {
    EventViewModel eventViewModel; // View Model with eventList up to date with database
    ArrayList<Event> eventsList; // Home specific arraylist for array adapter
    ArrayList<Event> eventsSearchList; // Search list
    EntrantEventArrayAdapter eventAdapter; // Array adapter for events

    SearchView searchBar;
    ListView eventsListView; // ListView on home fragment screen
    Button filterButton; // Button to filter events
    Button clearFilterButton; // Button to clear filter
    FloatingActionButton scanQrButton; // Scan QR button

    Pair<Date, Date> dateRange; // dateRange to apply
    ArrayList<String> selectedTags; // tags to apply
    Boolean filterApplied = false; // Boolean checking if filter is applied or not
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ZXing scanner launcher
    private ActivityResultLauncher<ScanOptions> scanQrLauncher;

    public EntrantSearchFragment() {
        // Required empty public constructor
    }

    public static EntrantHomeFragment newInstance() {
        EntrantHomeFragment fragment = new EntrantHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the EventViewModel using the factory
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        eventsList = new ArrayList<>();
        eventsSearchList = new ArrayList<>();

        eventViewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            // When event list in viewmodel is updated, update eventList too (as well as notify array adapter)
            @Override
            public void onChanged(ArrayList<Event> events) {
                updateEventList();
            }
        });

        // ZXing scanner setup
        scanQrLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() == null) {
                        // User cancelled
                        return;
                    }

                    String text = result.getContents();
                    if (text != null && text.startsWith("coolioevents://event/")) {
                        String eventId = text.substring("coolioevents://event/".length());
                        openEventFromId(eventId);
                    } else {
                        Toast.makeText(requireContext(), "Unrecognized QR code", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrant_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBar = view.findViewById(R.id.searchBar);
        eventsListView = view.findViewById(R.id.eventList); // Listview in search
        filterButton = view.findViewById(R.id.filterButton); // Filter button in search
        clearFilterButton = view.findViewById(R.id.clearFilterButton); // Clear Filter button in search
        scanQrButton = view.findViewById(R.id.scanQrFab); // Scan QR FAB in search

        eventAdapter = new EntrantEventArrayAdapter(getActivity(), eventsList); // Make new event adapter linked to eventList
        eventAdapter = new EntrantEventArrayAdapter(getActivity(), eventsSearchList);
        eventsListView.setAdapter(eventAdapter);

        // If a filter is applied make sure clearFilterButton is visible
        if (filterApplied){
            clearFilterButton.setVisibility(VISIBLE);
        }
        else {
            clearFilterButton.setVisibility(GONE);
        }

        // Navigating to Event Fragment when clicking on list item
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

        // Press filter button - Shows filter dialog menu
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        // Press clear filter button - unapplies any filters
        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterApplied = false;
                updateEventList();
                clearFilterButton.setVisibility(GONE);
            }
        });

        // Scan QR button -> open ZXing scanner
        scanQrButton.setOnClickListener(v -> startQrScan());

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBar.clearFocus();
                eventsSearchList.clear();
                for (Event event : eventsList){
                    if (event.getDetails().getEventName().toLowerCase().contains(query.toLowerCase())){
                        eventsSearchList.add(event);
                    }
                }
                System.out.println(eventsSearchList);
                eventAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    /**
     * This method updates eventList and updates eventAdapter to be up to date with
     * the database and apply any filters/searches.
     */
    private void updateEventList(){
        ArrayList<Event> events; // events to iterate over to set eventList to

        if (filterApplied) {
            // If filter applied apply filters and set events to filtered event list
            events = eventViewModel.getFilteredEventList(dateRange.first, dateRange.second, selectedTags);
        }
        else {
            // If not, just set events to eventlist in viewmodel
            events = eventViewModel.getEventList().getValue();
        }

        eventsList.clear();
        System.out.println("CHANGED OMG");
        if (events != null) {
            for (Event event : events){
                if (event.getDetails().getStatus().equals("open")){
                    // Only add events that are currently open
                    eventsList.add(event);
                }
            }
            Collections.sort(eventsList);
        }
        searchBar.setQuery(searchBar.getQuery(), true);
    }

    /**
     * This method displays user with the filter menu Dialog prompt - allowing
     * user to filter events based on a Date range or tags
     */
    private void showFilterDialog(){
        Dialog dialog = new Dialog(requireActivity()); // Make new dialog
        dialog.setContentView(R.layout.filter_menu); // Set the content of the dialog to be the filter menu
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.whitebackground)); // Set background of dialog to be rounded
        ChipGroup tagsGroup = dialog.findViewById(R.id.tagsGroup); // ChipGroup of tags
        Button dateButton = dialog.findViewById(R.id.dateRangeButton); // Button to allow user to choose date range
        TextView dateText = dialog.findViewById(R.id.dateRangeView); // Textview showing user the date range they chose
        Button applyButton = dialog.findViewById(R.id.applyButton); // Apply button to apply filter

        // Gets all selected tags and put them in filterTags
        ArrayList<String> filterTags = new ArrayList<>();
        tagsGroup.setOnCheckedStateChangeListener((chipGroup, tagIds) -> {
            filterTags.clear();
            for (Integer tagId : tagIds){
                Chip tag = chipGroup.findViewById(tagId);
                filterTags.add(tag.getText().toString());
            }
        });

        // Make Date Range Picker
        final Date[] DateRange = new Date[2]; // Holds date range (0-startDate, 1-endDate)
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date range of events you wish to view")
                .build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> longLongPair) {
                // If date range is selected, update dateText to show the range and update DateRange values
                SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                DateRange[0] = millisToDate(longLongPair.first); // Convert Long ms to Date type
                DateRange[1] = millisToDate(longLongPair.second); // Convert Long ms to Date type
                dateText.setText(SDF.format(DateRange[0].getTime()) + " - " + SDF.format(DateRange[1].getTime()));
            }
        });

        // When Date button is clicked show user date range picker
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getActivity().getSupportFragmentManager(), "TAG");
            }
        });

        // When Apply button is clicked apply any filters set (Date range, tags)
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRange = new Pair<>(DateRange[0], DateRange[1]);
                selectedTags = filterTags;
                System.out.println(selectedTags);
                filterApplied = true; // Set boolean filter applied to true
                updateEventList(); // Update the event list and adapter to show filtered events
                clearFilterButton.setVisibility(VISIBLE); // Make clear filter button visible
                dialog.dismiss(); // Close dialog prompt
            }
        });

        dialog.show(); // Show filter popup menu
    }

    /**
     * Converts Long millisecond time to Date type
     * @param millis
     *      Time in milliseconds
     * @return
     *       Time in Date format
     */
    private Date millisToDate(Long millis){
        Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        UTC.setTimeInMillis(millis);
        Date date = UTC.getTime();
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return UTC.getTime();
    }

    /**
     * Starts ZXing's built-in scanner Activity.
     */
    private void startQrScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan the event QR code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        scanQrLauncher.launch(options);
    }

    /**
     * Opens the EventFragment for the given eventId.
     */
    private void openEventFromId(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid event link", Toast.LENGTH_SHORT).show();
            return;
        }

        EventFragment eventDetailsFragment = EventFragment.newInstance(eventId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, eventDetailsFragment)
                .addToBackStack(null)
                .commit();
    }
}
