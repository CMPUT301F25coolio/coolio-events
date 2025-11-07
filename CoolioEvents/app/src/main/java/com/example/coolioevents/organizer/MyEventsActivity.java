package com.example.coolioevents.organizer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coolioevents.R;
import com.example.coolioevents.services.PoolingService;
/**
 * Copyright 2025 Parth Mittal
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
 * A small organizer-only screen that lets the user pick an event ID and
 * draw a replacement entrant from the waitlist using Firestore transactions.
 *
 * RATIONALE:
 * This was built mainly as a simple test screen for pooling logic.
 * Keeping it separate avoids breaking other teammates’ flows and makes debugging easier.
 *
 * OUTSTANDING ISSUES:
 * Currently, there’s no check for invalid event IDs or detailed error display.
 * Could later add clearer messages or auto-fill valid event IDs from Firestore.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-07
 */
/*
  Screen super small utility page for organizers to pull a replacement entrant
   1 User pastes an event document ID
   2 Tap the button runs a Firestore transaction via PoolingService.
   3 If there was anyone in waitlist, the first gets moved to chosen*/
public class MyEventsActivity extends AppCompatActivity {
    // tiny service wrapper that does the transaction
    private PoolingService pooling;
    // view refs (kept local in onCreate earlier and I prefer fields here for readability)
    private EditText eventIdInput;
    private Button drawBtn;
    private TextView statusText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        pooling = new PoolingService();
        // wire up the views once I always forget ids so keeping them obvious
        eventIdInput = findViewById(R.id.eventIdInput);
        drawBtn      = findViewById(R.id.btnDrawReplacement);
        statusText   = findViewById(R.id.statusText);
        // If someone launched this activity with an eventId,just prefill it
        String prefill = getIntent().getStringExtra("eventId");
        if (prefill != null && !prefill.isEmpty()) {
            eventIdInput.setText(prefill);
        }
        // main action attempt a replacement draw
        drawBtn.setOnClickListener(v -> handleDrawClick());
    }
    // Split out the click logic so onCreate stays tidy.
    private void handleDrawClick() {
        String eventId = eventIdInput.getText().toString().trim();
        if (eventId.isEmpty()) {
            toast("Enter eventId");
            return;
        }
        setLoading(true, "Drawing replacement…");
        pooling.drawReplacement(eventId)
                .addOnSuccessListener(uid -> {
                    // success path show who got picked and reenable button
                    setLoading(false, "Replacement chosen: " + uid);
                    toast("Selected " + uid);
                    // If later we add notifications:
                    // SendNotification.sendToUser(uid, "You have been selected!", eventId);
                })
                .addOnFailureListener(e -> {
                    // common failures no event doc or waitlist was empty
                    setLoading(false, "Failed: " + e.getMessage());
                });
    }
    // Tiny helper so I dont repeat the same 2 lines everywhere.
    private void setLoading(boolean isBusy, String message) {
        drawBtn.setEnabled(!isBusy);
        statusText.setText(message);
    }
    // Toast wrapper short. Just easier to read
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
