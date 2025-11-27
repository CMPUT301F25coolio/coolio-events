package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coolioevents.R;
import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Copyright 2025 Aasta Tsai
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
 * Displays the home activity for organizers.
 * Provides different button functionalities that allows
 * the Organizer to navigate the app.
 *
 * OUTSTANDING ISSUES:
 * Some functionalities are not completed during this iteration,
 * but will be completed in future iterations; My Profile and
 * Send Notifications Button.
 *
 * @author Aasta Tsai
 * @version 1.0
 * @since 2025-11-05
 */

public class OrganizerActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer); // make sure your layout file name is activity_organizer.xml

        // Use View type since these are CardViews, not LinearLayouts
        View optMakeEvent = findViewById(R.id.optMakeEvent);
        View optMyEvents = findViewById(R.id.optMyEvents);
        View optMyProfile = findViewById(R.id.optMyProfile);
        View optSendNotification = findViewById(R.id.optSendNotification);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Click actions
        if (optMakeEvent != null) {
            optMakeEvent.setOnClickListener(v ->
                    startActivity(new Intent(this, CreateEventActivity.class)));
        }
        if (optMyEvents != null) {
            optMyEvents.setOnClickListener(v ->
                    startActivity(new Intent(this, OrganizerMyEventsActivity.class)));
        }
        if (optMyProfile != null) {
            optMyProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, OrganizerProfileActivity.class)));
        }
        if (optSendNotification != null) {
            optSendNotification.setOnClickListener(v ->
                    Toast.makeText(this, "Send Notification: coming soon", Toast.LENGTH_SHORT).show());
        }

        logoutButton.setOnClickListener(v -> {
            logout(); // If logout button pressed - perform logout
        });
    }

    /**
     * This method signs the user out of their account - it
     * signs out of the current user in mAuth, and
     * sends user back to the welcome screen
     */
    private void logout(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        mAuth.signOut();
        startActivity(intent);
        finish();
    }
}
