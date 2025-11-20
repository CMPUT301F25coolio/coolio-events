package com.example.coolioevents.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.R;
import com.example.coolioevents.organizer.CreateEventActivity;

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
 * This class represents the administrator home screen.
 * It displays options which the administrator can click to navigate to different screens such as
 * the list of all events, the list of all uploaded images, the list of all entrant profiles,
 * the list of all organizer profiles, and the list of all notification logs.
 *
 * RATIONALE:
 * This class was designed to allow administrators to navigate to different screens.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-16
 */

public class AdministratorHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_home);

        // Establishing views
        View eventsOption = findViewById(R.id.optAdminEvents);
        View imagesOption = findViewById(R.id.optAdminImages);
        View entrantsOption = findViewById(R.id.optAdminEntrants);
        View organizersOption = findViewById(R.id.optAdminOrganizers);
        View notificationsOption = findViewById(R.id.optAdminNotifications);

        // Events option onclick activity --> Leads to Events activity
        if (eventsOption != null) {
            eventsOption.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorEventsActivity.class)));
        }

        // Images option onclick activity --> Leads to Images activity
        if (imagesOption != null) {
            imagesOption.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorImagesActivity.class)));
        }


        // Entrants option onclick activity --> Leads to Entrants activity
        if (entrantsOption != null) {
            entrantsOption.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorEntrantsActivity.class)));
        }

        // Organizers option onclick activity --> Leads to Organizers activity
        if (organizersOption != null) {
            organizersOption.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorOrganizersActivity.class)));
        }

        // Notifications option onclick activity --> Leads to Notification activity
        if (notificationsOption != null) {
            notificationsOption.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorNotificationsActivity.class)));
        }

    }
}