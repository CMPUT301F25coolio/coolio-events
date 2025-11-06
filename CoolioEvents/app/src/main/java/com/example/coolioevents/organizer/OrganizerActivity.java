package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coolioevents.R;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer); // make sure your layout file name is activity_organizer.xml

        // Use View type since these are CardViews, not LinearLayouts
        View optMakeEvent = findViewById(R.id.optMakeEvent);
        View optMyEvents = findViewById(R.id.optMyEvents);
        View optMyProfile = findViewById(R.id.optMyProfile);
        View optSendNotification = findViewById(R.id.optSendNotification);

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
                    Toast.makeText(this, "My Profile: coming soon", Toast.LENGTH_SHORT).show());
        }
        if (optSendNotification != null) {
            optSendNotification.setOnClickListener(v ->
                    Toast.makeText(this, "Send Notification: coming soon", Toast.LENGTH_SHORT).show());
        }
    }
}
