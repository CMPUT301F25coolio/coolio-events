package com.example.coolioevents;

/**
 * Copyright 2025 Niharika Rawat
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
 * This fragment provides a simple interface for editing and updating a user's
 * profile information (username, name, and email). It can be connected to Firebase,
 * SQLite, or other storage systems to persist profile updates.
 *
 * Once the profile is saved, the fragment navigates back to the previous screen.
 *
 * @author Niharika Rawat
 * @version 1.0
 * @since 2025-11-07
 */

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

/**
 * A fragment that allows users to edit their profile details.
 * This includes username, name, and email.
 * The data can later be saved to a database or cloud service such as Firebase.
 */
public class UpdateProfileFragment extends Fragment {

    /** Input field for editing the user's username */
    private EditText editUsername;

    /** Input field for editing the user's full name */
    private EditText editName;

    /** Input field for editing the user's email address */
    private EditText editEmail;

    /** Button to save the updated profile information */
    private Button btnSave;

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     * This method inflates the fragment layout, initializes input fields,
     * and sets up the save button logic.
     *
     * @param inflater LayoutInflater used to inflate the fragment layout
     * @param container Optional parent view that the fragment UI attaches to
     * @param savedInstanceState Previously saved state, if any
     * @return The created View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        // Initialize input fields and button
        editUsername = view.findViewById(R.id.edit_username);
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        btnSave = view.findViewById(R.id.btn_save_profile);

        // (Optional) Preload current user information if available
        // Example:
        // editUsername.setText("coolio_user");
        // editName.setText("John Doe");
        // editEmail.setText("john.doe@example.com");

        /**
         * Handles the save button click event.
         * Retrieves input field values, displays a confirmation toast,
         * and navigates back to the previous fragment.
         */
        btnSave.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            // TODO: Save updated info to Firebase/SQLite/etc.
            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            // Navigate back to the previous fragment (ProfileFragment)
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
