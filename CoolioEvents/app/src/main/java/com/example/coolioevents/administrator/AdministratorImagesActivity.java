package com.example.coolioevents.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolioevents.R;
import com.example.coolioevents.events.EventImageData;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.example.coolioevents.organizer.Organizer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdministratorImagesActivity extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private RecyclerView imagesRecyclerView;
    private ImagesGridAdapter gridAdapter;
    private ArrayList<EventImageData> imagesList;
    //Maybe make image class that holds image URL and organizer
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_images);

        // Establish Views
        ImageButton backButton = findViewById(R.id.btnBack);
        imagesRecyclerView = findViewById(R.id.images_recycler_view);

        // Establish Adapter
        imagesList = new ArrayList<EventImageData>();
        gridAdapter = new ImagesGridAdapter(imagesList, this);

        int numberOfColumns = 2; // Number of columns the images will be displayed in
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        imagesRecyclerView.setLayoutManager(layoutManager);

        imagesRecyclerView.setAdapter(gridAdapter);
        imagesRecyclerView.setNestedScrollingEnabled(true);

        // Establish ViewModel
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        // Getting images from eventViewModel
        eventViewModel.getEventImages().observe(this, newEventImageData -> {
            if (newEventImageData != null) {
                imagesList.clear(); // Clear old list
                imagesList.addAll(newEventImageData); // Add all image data objects
                gridAdapter.notifyDataSetChanged(); // Tell adapter data has been changed
            }
        });

        // Back button onclick activity --> Leads to Home activity
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorHomeActivity.class)));
        }
    }
}
