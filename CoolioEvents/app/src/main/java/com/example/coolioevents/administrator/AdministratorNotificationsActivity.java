package com.example.coolioevents.administrator;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.R;
import com.example.coolioevents.User;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdministratorNotificationsActivity extends AppCompatActivity {
    NotificationViewModel notificationViewModel; // View Model eventList up to date with database
    ArrayList<NotificationData> notificationsList; // My Organizer specific arraylist for array adapter ()
    NotificationsArrayAdapter notificationsAdapter; // Array adapter for organizer
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
        notificationsAdapter = new UserArrayAdapter(this, notificationsList);
        notificationsListView.setAdapter(notificationsAdapter);

        // Establish ViewModel
        notificationViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);



    }
}
