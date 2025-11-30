package com.example.coolioevents.administrator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Copyright 2025 Juliane Phan
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
 * This class represents a fragment for a specific image.
 * It is displayed when an Administrator clicks on a specific image in the Images screen.
 * It displays the image (and the organizer's username), and allows the administrator to
 * delete the image if they wish.
 *
 * RATIONALE:
 * Utilizes an event view model to retrieve the details of the image and to delete the image.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-20
 */

public class AdministratorImageFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentImageURL;
    EventViewModel eventViewModel;

    // Attributes for displaying details
    private ImageView eventPoster;
    private TextView organizerUsername;
    private Button deleteButton;
    private Button closeButton;

    /**
     * This is a constructor for the image fragment
     *
     * @param imageURL
     *      the user we want the fragment to display
     * @return the fragment
     */
    public static AdministratorImageFragment newInstance(String imageURL) {
        AdministratorImageFragment fragment = new AdministratorImageFragment();
        Bundle args = new Bundle();
        args.putString("image_url", imageURL); // Bundle holds the event id
        fragment.setArguments(args); // Attach the bundle to the fragment
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentImageURL = getArguments().getString("image_url");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentImageLayout = inflater.inflate(R.layout.fragment_image, container, false);
        return fragmentImageLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Establishing UI components for the image details
        eventPoster = view.findViewById(R.id.eventPoster);
        organizerUsername = view.findViewById(R.id.organizerUsername);

        // Get the EventViewModel
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);
        //eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);


        // Set image
        Glide.with(this)
                .load(currentImageURL) // loads poster URL
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .fallback(R.drawable.ic_image_placeholder) // If imageURL is null
                .into(eventPoster);

        // Set organizer username
        eventViewModel.getEventImages().observe(getViewLifecycleOwner(), eventImages -> {
            if (eventImages == null) {
                return;
            }

            eventViewModel.getOrganizerByImageURL(currentImageURL).observe(getViewLifecycleOwner(), organizer -> {
                if (organizer != null) {
                    organizerUsername.setText(organizer.getProfile().getUsername());
                }
            });
        });

        // Establishing buttons and fragment container
        deleteButton = view.findViewById(R.id.deleteButton);
        closeButton = view.findViewById((R.id.closeButton));
        FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);

        // Delete button onclick activity --> Deletes the clicked image and goes back to Images screen
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("DELETE BUTTON WAS CLICKED IN AdministratorImageFragment.java");

                eventViewModel.deletePoster(currentImageURL);  // Delete image
                getParentFragmentManager().popBackStack();  // Go back to Images screen

                // Remove white background from fragment container
                fragmentContainer.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        // Establish views associated with closing the fragment
        View header = getActivity().findViewById(R.id.header);
        View imagesRecyclerView = getActivity().findViewById(R.id.images_recycler_view);

        // Close button onclick activity --> Goes back to Images screen
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();  // Go back to Images screen

                // Remove white background from fragment container
                fragmentContainer.setBackgroundColor(Color.TRANSPARENT);

                // Make header and recyclier view visible
                header.setVisibility(View.VISIBLE);
                imagesRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}