package com.example.coolioevents.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolioevents.R;
import com.example.coolioevents.events.EventImageData;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Copyright 2025 Avery Dancocks & Juliane Phan
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
 * This class represents the administrator images screen.
 * It displays all of the event posters and their organizer
 * for the administrator.
 *
 * RATIONALE:
 * This class was designed to allow administrators to view and
 * interact with event posters.
 *
 * @author Avery Dancocks & Juliane Phan
 * @version 1.0
 * @since 2025-11-19
 */

/*Taken from: Google Gemini
    Prompt: How to implement onclick activity for a recycler view?
    Taken by: Juliane Phan
    Taken on: 11/20/2025
*/

public class AdministratorImagesActivity extends AppCompatActivity implements ImagesGridAdapter.OnItemClickListener {
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
        gridAdapter.setOnItemClickListener(this);


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

    @Override
    public void onItemClick(String imageURL) {
        System.out.println("AN IMAGE WAS CLICKED IN AdministratorImagesActivity.java");
        System.out.println(imageURL);

        // Set the fragment's background colour
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.setBackgroundResource(R.drawable.whitebackground);

        // Make the header and RecyclierView invisible
        View header = findViewById(R.id.header);
        header.setVisibility(View.GONE);
        imagesRecyclerView.setVisibility(View.GONE);

        AdministratorImageFragment fragment = AdministratorImageFragment.newInstance(imageURL);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // Replace the current fragment
                .addToBackStack(null) // This allows the user to press the back button to return to the list
                .commit();
    }
}