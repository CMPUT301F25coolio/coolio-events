package com.example.coolioevents.events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.Entrant.EntrantActivity;

/**
 * Simple deep-link handler.
 * Example: coolioevents://event/123
 */
public class EventDeepLinkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        String eventId = null;

        if (data != null
                && "coolioevents".equals(data.getScheme())
                && "event".equals(data.getHost())) {
            eventId = data.getLastPathSegment();
        }

        if (eventId != null && !eventId.isEmpty()) {
            Intent i = new Intent(this, EntrantActivity.class);
            i.putExtra("EVENT_ID", eventId);
            startActivity(i);
        } else {
            Toast.makeText(this, "Invalid event link", Toast.LENGTH_SHORT).show();
        }

        // No UI, just hand off to EntrantActivity
        finish();
    }
}
