package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.R;

public class OrganizerHome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        LinearLayout optMakeEvent = findViewById(R.id.optMakeEvent);
        LinearLayout optMyEvents = findViewById(R.id.optMyEvents);
        LinearLayout optMyProfile = findViewById(R.id.optMyProfile);
        LinearLayout optSendNotification = findViewById(R.id.optSendNotification);

        optMakeEvent.setOnClickListener(v ->
                startActivity(new Intent(this, CreateEventActivity.class)));

        /**
        optMyEvents.setOnClickListener(v ->
                startActivity(new Intent(this, MyEvents.class)));

        optMyProfile.setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));

        optSendNotification.setOnClickListener(v ->
                startActivity(new Intent(this, SendNotification.class)));
         **/
    }
}
