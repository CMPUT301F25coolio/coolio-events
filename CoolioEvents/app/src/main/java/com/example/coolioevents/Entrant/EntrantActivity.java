package com.example.coolioevents.Entrant;
import com.example.coolioevents.events.EventFragment;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.coolioevents.Event;
import com.example.coolioevents.NotificationData;
import com.example.coolioevents.NotificationFragment;
import com.example.coolioevents.NotificationViewModel;
import com.example.coolioevents.ProfileFragment;
import com.example.coolioevents.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
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
 * This class represents the Entrant Activity which is an activity with
 * a frame container which holds entrant fragment screens such as home, search, profile
 * and my events aswell as a navigation bar to navigate between these screens.
 *
 * RATIONALE:
 * This class was designed give entrant the ability to navigate through
 * the different fragments they have access to
 *
 * @author Ethan Diep, Niharika Rawat
 * @version 1.5
 * @since 2025-11-06
 */
public class EntrantActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; // Authenticator to create user accounts
    private FirebaseFirestore db; // Database
    private CollectionReference userCollection; // Collection of users in firebase database

    private FirebaseUser user; // The current user
    private Fragment homeFragment; // Fragment for home screen
    private Fragment myEventsFragment; // Fragment for home screen
    private Fragment ProfileFragment; // Fragment for profile screen
    private Fragment SearchFragment;
    private BottomNavigationView bottomNavView; // Bottom navigation bar
    private NotificationManager notificationManager; // Notification manager to handle notifications
    private NotificationViewModel notificationViewModel; // Notification viewmodel to access DB
    ArrayList<NotificationData> newNotifications; // List of any new notifications to be shown
    private String CHANNEL_ID = "Channel"; // Channel Id for notification to be sent
    private Map<String, Entrant> entrantMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant);
        // Deep link: open event details directly
        String deepEventId = getIntent().getStringExtra("EVENT_ID");
        if (deepEventId != null && !deepEventId.isEmpty()) {
            // Open EventFragment immediately
            EventFragment fragment = EventFragment.newInstance(deepEventId);
            SwitchFragment(fragment);
            return;  // Skip loading the normal home screen
        }
        homeFragment = new EntrantHomeFragment();
        myEventsFragment = new EntrantMyEventsFragment();
        ProfileFragment = new ProfileFragment();
        SearchFragment = new EntrantSearchFragment();

        SwitchFragment(homeFragment); // Default fragment is home

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        user = mAuth.getCurrentUser();
        bottomNavView = findViewById(R.id.bottomNavigationView);
        bottomNavView.setItemIconTintList(null);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationViewModel = new NotificationViewModel();
        SwitchFragment(homeFragment);
        createNotificationChannel();
        newNotifications = notificationViewModel.getUserUnSeenNotifications(user.getUid());


        // eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Source - https://stackoverflow.com/questions/41664409/wait-for-5-seconds
        // Posted by seekingStillness
        // Retrieved by Ethan Diep on 2025-11-18, License - CC BY-SA 4.0
        // Used to add a delay for notifications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // 5 second delay to show notifications
                if (!newNotifications.isEmpty()){
                    // If there are new notifications to be shown, show them
                    showNewNotifications(newNotifications);
                }
            }
        }, 5000);

        bottomNavView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.home){
                // If home item is selected in nav bar switch to home fragment
                SwitchFragment(homeFragment);
            } else if (menuItem.getItemId() == R.id.myevents) {
                // If myevents item is selected in nav bar switch to home fragment
                SwitchFragment(myEventsFragment);
            } else if (menuItem.getItemId() == R.id.profile) {
                // If myevents item is selected in nav bar switch to home fragment
                SwitchFragment(ProfileFragment);
            }
              else if (menuItem.getItemId() == R.id.search) {
                  SwitchFragment(SearchFragment);
              }
            return true;
        });
    }
    // 1. Make the Bell Icon appear
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // 2. Handle what happens when the Bell is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {

            // Create the notification fragment
            NotificationFragment notificationFragment = new NotificationFragment();

            // We manually do the transaction here because we want 'addToBackStack'
            // This allows the user to press the "Back" button on their phone to return to Home
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, notificationFragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Switches fragment to fragment in the fragment container
     * @param fragment The fragment to switch to
     */
    public void SwitchFragment(Fragment fragment){
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.fragment_container, fragment);
        fragTransaction.commit();
    }


    /**
     * Shows any new notifications with a given notificationList
     * @param notificationList
     * List of notifications to display to the user
     */
    private void showNewNotifications(ArrayList<NotificationData> notificationList){
        int i = 1; // Notification Id to increment for each new notification
        for (NotificationData notification : notificationList){
            showNotification(notification.getTitle(), notification.getMessage(), i++);
            notificationViewModel.setNotificationShown(notification.getNotifId());
            i++;
        }
    }

    /**
     * Displays a notification with a given title, text, and id
     * @param title
     * Title of the notification
     * @param text
     * Text to be displayed within the notification
     * @param id
     * Notification id (NOT TO BE CONFUSED WITH THE ACTUAL NOTIFICATION ID OF THE OBJECT/DOCUMENT ID)
     */
    private void showNotification(String title, String text, int id){
        // Source Help - https://developer.android.com/develop/ui/views/notifications/build-notification#java_1
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_settings_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        NotificationManagerCompat.from(this).notify(id, builder.build());
    }

    /**
     * Creates a new notification channel to send notifications in
     */
    private void createNotificationChannel() {
        // Source Help - https://developer.android.com/develop/ui/views/notifications/build-notification#java_1
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notification";
            String description = "Displays Notification about events";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager.createNotificationChannel(channel);
        }
    }
}



