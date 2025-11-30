package com.example.coolioevents.administrator;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Entrant.Entrant;
import com.example.coolioevents.Entrant.UserViewModel;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.Profile;
import com.example.coolioevents.R;
import com.example.coolioevents.User;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.example.coolioevents.organizer.Organizer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

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
 * This class represents a fragment for a specific user.
 * It is displayed when an Administrator clicks on a specific user in the Entrants/Organizers screen.
 * It displays the user's details, and allows the administrator to delete the user if they wish.
 *
 * RATIONALE:
 * Utilizes a view model to retrieve the details of the user and to delete the user.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-20
 */

public class AdministratorUserFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserViewModel userViewModel;
    private String currentUserId;

    // Attributes for displaying details
    private TextView username;
    private TextView name;
    private TextView email;
    private Button deleteButton;
    private Button closeButton;

    /**
     * This is a constructor for the user fragment
     *
     * @param userId
     *      the user we want the fragment to display
     * @return the fragment
     */
    public static AdministratorUserFragment newInstance(String userId) {
        AdministratorUserFragment fragment = new AdministratorUserFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId); // Bundle holds the event id
        fragment.setArguments(args); // Attach the bundle to the fragment
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = getArguments().getString("user_id");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentUserLayout = inflater.inflate(R.layout.fragment_administrator_delete_user, container, false);
        return fragmentUserLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Establishing UI components for the user's details
        username = view.findViewById(R.id.profile_username);
        name = view.findViewById(R.id.profile_name);
        email = view.findViewById(R.id.profile_email);

        // Get the UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Set the user's details
        userViewModel.getUserMap().observe(getViewLifecycleOwner(), userMap -> {
            if (userMap != null && userMap.containsKey(currentUserId)) {
                Profile userProfile = userMap.get(currentUserId).getProfile();
                username.setText(userProfile.getUsername());
                name.setText(userProfile.getName());
                email.setText(userProfile.getEmail());
            }
        });

        // Establishing buttons and fragment container
        deleteButton = view.findViewById(R.id.deleteButton);
        closeButton = view.findViewById((R.id.closeButton));
        FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);


        // Delete button onclick activity --> Deletes the clicked user
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userViewModel.deleteUser(currentUserId);  // Delete user from the "users" collection in the database

                // If clicked user is an entrant --> Call entrant-specific deletion methods
                MutableLiveData<Map<String, Entrant>> entrantMap = userViewModel.getEntrantMap();
                if (entrantMap.getValue() != null && entrantMap.getValue().containsKey(currentUserId)) {
                    userViewModel.deleteEntrantFromWaitlistLocations(currentUserId);
                    userViewModel.removeUserFromAllEventLists(currentUserId)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ViewModel", "SUCCESS: User " + currentUserId + " deleted from event lists");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ViewModel", "FAILURE: Could not delete user " + currentUserId + " from event lists", e);
                            });
                }

                // If clicked user is an organizer --> Call organizer-specific deletion methods
                MutableLiveData<Map<String, Organizer>> organizerMap = userViewModel.getOrganizerMap();
                if (organizerMap.getValue() != null && organizerMap.getValue().containsKey(currentUserId)) {
                    userViewModel.deleteEventsMadeByOrganizer(currentUserId);
                }

                getParentFragmentManager().popBackStack();  // Go back to Entrants/Organizers screen
                // Remove white background from fragment container
                fragmentContainer.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        // Close button onclick activity --> Goes back to Entrants/Organizers screen
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();  // Go back to Entrants/Organizers screen

                // Remove white background from fragment container
                fragmentContainer.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }
}