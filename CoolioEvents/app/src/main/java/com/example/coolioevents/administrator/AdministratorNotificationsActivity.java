package com.example.coolioevents.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.NotificationData;
import com.example.coolioevents.NotificationViewModel;
import com.example.coolioevents.R;
import com.example.coolioevents.User;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class represents the administrator notifications screen.
 * It displays all of the sent notifications from the firebase.
 *
 * RATIONALE:
 * This class was designed to allow administrators to view and
 * interact with sent notifications.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-19
 */
public class AdministratorNotificationsActivity extends AppCompatActivity {
    NotificationViewModel notificationViewModel; // View Model eventList up to date with database
    ArrayList<NotificationData> notificationsList; // My Organizer specific arraylist for array adapter ()
    NotificationArrayAdapter notificationsAdapter; // Array adapter for organizer
    ListView notificationsListView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrator_notifications);

        // Establishing views
        ImageButton backButton = findViewById(R.id.btnBack);
        notificationsListView = findViewById(R.id.notifications_list_view);

        // Establishing Adapter
        notificationsList = new ArrayList<NotificationData>();
        notificationsAdapter = new NotificationArrayAdapter(this, notificationsList);
        notificationsListView.setAdapter(notificationsAdapter);

        // Establish ViewModel
        notificationViewModel = new NotificationViewModel();

        // Get notifications from view model
        notificationViewModel.getNotifications().observe(this, notificationData -> {
            if (notificationData != null) {
                notificationsList.clear(); // Clear old list
                notificationsList.addAll(notificationData); // Add all notification objects
                Collections.sort(notificationsList); // Sort notifications based on date
                notificationsAdapter.notifyDataSetChanged(); // Tell adapter data has been changed
            }
        });

        // Click specific notification --> Show fragment with notification details
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationData clickedNotification = (NotificationData) parent.getItemAtPosition(position);
                Date notificationDate = clickedNotification.getCreatedAt();

                /*
                Taken From: https://stackoverflow.com/questions/17807777/simpledateformatstring-template-locale-locale-with-for-example-locale-us-for
                    License: https://creativecommons.org/licenses/by-sa/3.0/
                    Authored by: jasdmystery
                    Taken by: Avery Dancocks
                    Taken on: 11/19/25
                 */
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                String dateString = format.format(notificationDate);

                String senderString = clickedNotification.getSender();
                String receiverString = clickedNotification.getReceiver();
                String typeString = clickedNotification.getType();
                String messageString = clickedNotification.getMessage();

                // If notification is null do nothing
                if (clickedNotification == null) {
                    return;
                }

                // Set the fragment's background colour
                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                fragmentContainer.setBackgroundResource(R.drawable.whitebackground);

                // Make header and column layout invisible
                View header = findViewById(R.id.header);
                View columnLayout = findViewById(R.id.column_layout);
                header.setVisibility(View.GONE);
                columnLayout.setVisibility(View.GONE);

                // Create and display the fragment
                AdministratorNotificationFragment notificationFragment = AdministratorNotificationFragment.newInstance(dateString, senderString, receiverString, typeString, messageString);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, notificationFragment) // Replace the current fragment
                        .addToBackStack(null) // This allows the user to press the close button to return to the list
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
