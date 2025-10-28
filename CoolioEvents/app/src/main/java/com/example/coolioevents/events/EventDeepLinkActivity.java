package com.example.coolioevents.events;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
            Toast.makeText(this, "Opening event: " + eventId, Toast.LENGTH_SHORT).show();
            // TODO: launch your EventDetailsActivity if needed
        } else {
            Toast.makeText(this, "Invalid event link", Toast.LENGTH_SHORT).show();
        }

        finish(); // no UI for now
    }
}
