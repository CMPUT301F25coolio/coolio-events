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
import android.widget.ListView;

import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventArrayAdapter;

import java.util.ArrayList;
import java.util.Observable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantHomeFragment extends Fragment {
    EntrantViewModel viewModel;
    ArrayList<Event> eventsList;
    EventArrayAdapter eventAdapter;

    ListView eventsListView;

    public EntrantHomeFragment() {
        // Required empty public constructor
    }



    public static EntrantHomeFragment newInstance(String param1, String param2) {
        EntrantHomeFragment fragment = new EntrantHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        eventsList = new ArrayList<>();



        viewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                eventsList.clear();
                System.out.println("CHANGED OMG");
                eventsList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsListView = view.findViewById(R.id.eventList);
        eventAdapter = new EventArrayAdapter(getActivity(), eventsList);
        eventsListView.setAdapter(eventAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrant_home, container, false);
    }


}