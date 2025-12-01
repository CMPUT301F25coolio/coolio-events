package com.example.coolioevents.organizer;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.example.coolioevents.services.PoolingService;
import com.example.coolioevents.util.QRCodeUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class represents an activity for a single event for an organizer.
 * Contains methods to initialize the layout as well as deals with
 * the actions given to different button controllers in the activity.
 * It displays the details of an event.
 *
 * RATIONALE:
 * Utilizes an event view model to retrieve the details of the event
 * from a previous activity.
 *
 * OUTSTANDING ISSUES:
 * The functionality of the edit button is not currently developed.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-05
 */
public class OrganizerEventActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventViewModel eventViewModel;
    private boolean lotteryDone;
    private String eventStatus;
    private int numberOfChosenEntrants;
    private int numberOfAcceptedEntrants;
    private int numberInWaitlist;
    private int maxEntrants;
    private Event currentEvent;

    // Attributes for displaying details
    private TextView eventNameTextView;
    private TextView eventOrganizerTextView;
    private TextView eventDescriptionTextView;
    private ImageView eventPosterImageView;
    private TextView eventTimeTextView;
    private TextView eventLocationTextView;
    private TextView eventRegistrationPeriodTextView;
    private TextView eventEntrantLimitTextView;
    private TextView eventWaitlistLimitTextView;
    private TextView eventStatusTextView;
    private ChipGroup tagsGroup;
    private TextView eventWaitlistEntrantCount;
    private Button viewLists;

    //private MaterialButton drawLottery;
    private MaterialButton drawNewEntrant;
    private Button sendNotifications;
    private Button saveQrButton;

    /**
     * This is a helper function to assist in changing the state of the UI
     */
    private void updateButtonState() {
        // Event is still open
        if ("open".equals(eventStatus)) {
            //drawLottery.setEnabled(false);
            drawNewEntrant.setEnabled(false);
            // keep it grey while event is open
            drawNewEntrant.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey)));
            drawNewEntrant.setTextColor(
                    ContextCompat.getColor(this, R.color.dark_grey));
            // IMPORTANT: dont run the rest of the logic when event is open
            return;
        }

        // Lottery has already been drawn
        // NOTE: we no longer depend on lotteryDone for the replacement button,
        // because the draw lottery UI is commented out.
        // if (lotteryDone) {
        {
        /*
        drawLottery.setEnabled(false);
        // Set UI
        drawLottery.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        drawLottery.setTextColor(ContextCompat.getColor(this, R.color.grey));
        drawLottery.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
         */
            // Someone left the chosen list, and there is still people in the waitlist
            if ((numberOfChosenEntrants < maxEntrants) && (numberInWaitlist >= 1) && (numberOfChosenEntrants + numberOfAcceptedEntrants < maxEntrants)) {
                Log.e("drawn new user", "number of chosen: " + numberOfChosenEntrants + "number of accepted :" + numberOfAcceptedEntrants + "max entrants :" + maxEntrants);
                drawNewEntrant.setEnabled(true);
                // Set UI
                drawNewEntrant.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_green)));
                drawNewEntrant.setTextColor(
                        ContextCompat.getColor(this, R.color.dark_green));
            } else {
                Log.e("drawn new user", "number of chosen: " + numberOfChosenEntrants + "number of accepted :" + numberOfAcceptedEntrants + "max entrants :" + maxEntrants);
                // Either waitlist empty or chosen list full → keep disabled/grey
                drawNewEntrant.setEnabled(false);
                drawNewEntrant.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey)));
                drawNewEntrant.setTextColor(
                        ContextCompat.getColor(this, R.color.dark_grey));
            }

            // The old extra checks below are now covered by the if/else above,
            // so we don't need separate blocks.
        /*
        if (numberInWaitlist == 0) {
            drawNewEntrant.setEnabled(false);
            // Set UI
            drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey)));
            drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        }
        // The chosen list is full
        if (numberOfChosenEntrants == maxEntrants) {
            drawNewEntrant.setEnabled(false);
            // Set UI
            drawNewEntrant.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey)));
            drawNewEntrant.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        }
         */
        }
    /*
    // Lottery has not been drawn
    if (!lotteryDone && eventStatus.equals("closed")) {

        drawLottery.setEnabled(true);
        // Set UI
        drawLottery.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.drawfromlottery)));
        drawLottery.setTextColor(ContextCompat.getColor(this, R.color.white));
        drawLottery.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
    }
     */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event);

        // Get intent from previous fragment
        Intent intent = getIntent();
        String currentEventId = intent.getStringExtra("EVENT_ID");

        // Establishing UI components
        eventNameTextView = findViewById(R.id.eventViewName);
        eventOrganizerTextView = findViewById((R.id.eventViewOrganizer));
        eventDescriptionTextView = findViewById(R.id.eventViewDescription);
        eventPosterImageView = findViewById(R.id.eventViewPoster);
        eventTimeTextView = findViewById(R.id.eventViewTime);
        eventLocationTextView = findViewById(R.id.eventViewLocation);
        eventRegistrationPeriodTextView = findViewById(R.id.eventViewRegistrationPeriod);
        eventEntrantLimitTextView = findViewById(R.id.eventViewLimit);
        eventWaitlistLimitTextView = findViewById(R.id.eventViewWaitlistLimit);
        eventWaitlistEntrantCount = findViewById(R.id.eventWaitlistEntrantCount);
        eventStatusTextView = findViewById(R.id.eventViewEventStatus);
        tagsGroup = findViewById(R.id.tagsGroup);

        // Establish ViewModel
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        eventViewModel.getEventById(currentEventId).observe(this, event -> {
            if (event != null) {
                EventDetails details = event.getDetails();
                if (details != null) {
                    // Get Details from event
                    lotteryDone = event.getLotteryDone();
                    System.out.println("the Lottery is Done:" + lotteryDone);
                    eventStatus = event.getDetails().getStatus();
                    numberOfChosenEntrants = event.getChosenEntrants().size();
                    numberOfAcceptedEntrants = event.getAcceptedEntrants().size();
                    numberInWaitlist = event.getWaitlistEntrants().size();
                    maxEntrants = event.getDetails().getEntrantLimit();
                    currentEvent = event;
                    // Change button based on event status
                    updateButtonState();
                    System.out.println("WE MADE IT HERE");

                    // Updating UI components to match clicked event

                    if (event.getDetails().getStatus().equals("open")) {
                        // If event open make text open with green background
                        if (eventStatusTextView != null) {
                            eventStatusTextView.setText("Open");
                            eventStatusTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.medium_green_widget));
                        }
                    } else {
                        // If event closed make text open with red background
                        if (eventStatusTextView != null) {
                            eventStatusTextView.setText("Closed");
                            eventStatusTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.red_widget));
                        }
                    }

                    // Set event tags
                    if (tagsGroup != null && event.getDetails().getTags() != null) {
                        tagsGroup.removeAllViews();
                        for (String tagString : event.getDetails().getTags()) {
                            Chip tag = new Chip(this);
                            tag.setText(tagString);
                            tag.setHeight(40);
                            tag.setClickable(false);
                            tagsGroup.addView(tag);
                        }
                    }

                    if (tagsGroup != null && event.getDetails().getTags() != null){
                        tagsGroup.removeAllViews();
                        /*
                        Taken From: Google Gemini
                            Prompt: How do i customize tags?
                            Taken By: Avery Dancocks
                            Taken On: 11/28/25
                         */
                        final Typeface poppinsFont = ResourcesCompat.getFont(this, R.font.poppins_bold);

                        for (String tagString : event.getDetails().getTags()){
                            Chip tag = new Chip(this);
                            final float scale = this.getResources().getDisplayMetrics().density;
                            tag.setText(tagString);
                            tag.setChipStrokeWidth(1.5f * this.getResources().getDisplayMetrics().density); // Use dp for consistency
                            tag.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_grey)));
                            tag.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
                            tag.setTextColor(ContextCompat.getColor(this, R.color.black));

                            tag.setTypeface(poppinsFont);

                            tag.setShapeAppearanceModel(
                                    tag.getShapeAppearanceModel()
                                            .toBuilder()
                                            .setAllCornerSizes(new RelativeCornerSize(0.5f))
                                            .build()
                            );
                            tag.setClickable(false);
                            tagsGroup.addView(tag);
                        }
                    }

                    if (eventNameTextView != null) {
                        eventNameTextView.setText(details.getEventName());
                    }

                    if (eventDescriptionTextView != null) {
                        eventDescriptionTextView.setText(String.format("%s", event.getDetails().getEventDescription()));
                    }

                    if (eventLocationTextView != null) {
                        if (event.getDetails().getEventLocation() != null) {
                            eventLocationTextView.setText(String.format("%s", event.getDetails().getEventLocation()));
                        } else {
                            eventLocationTextView.setText("Event Location: Not Available");
                        }
                    }

                    if (eventTimeTextView != null) {
                        if (event.getDetails().getEventDateTime() != null) {
                            eventTimeTextView.setText(String.format("%s", event.getDetails().getEventDateTime()));
                        } else {
                            eventTimeTextView.setText("Time: Not Available");
                        }
                    }

                    if (eventRegistrationPeriodTextView != null) {
                        eventRegistrationPeriodTextView.setText(String.format("%s", String.valueOf(details.getRegistrationPeriod())));
                    }

                    if (eventEntrantLimitTextView != null) {
                        eventEntrantLimitTextView.setText(String.format("%s", String.valueOf(details.getEntrantLimit())));
                    }

                    if (eventWaitlistLimitTextView != null) {
                        Integer waitlistLimit = details.getWaitingListLimit();
                        if (waitlistLimit != null) {
                            eventWaitlistLimitTextView.setText(String.format("%s", String.valueOf(waitlistLimit)));
                        } else {
                            eventWaitlistLimitTextView.setText("Not Set");
                        }
                    }

                    if (eventWaitlistEntrantCount != null) {
                        eventWaitlistEntrantCount.setText(String.format("%s PEOPLE IN WAITLIST", String.valueOf(event.getWaitlistEntrants().size())));
                    }

                    // UI set up specifically for organizer
                    String organizerId = event.getOrganizerId();
                    if (organizerId != null) {
                        eventViewModel.getOrganizerById(organizerId).observe(this, organizer -> {
                            if (organizer != null && organizer.getProfile() != null) {
                                String username = organizer.getProfile().getUsername();
                                if (eventOrganizerTextView != null) {
                                    if (username != null) {
                                        eventOrganizerTextView.setText(String.format("%s", username));
                                    } else {
                                        eventOrganizerTextView.setText("Posted By: Unknown");
                                    }
                                }
                            }
                        });
                    }

                    // Set event image with Glide
                    if (eventPosterImageView != null) {
                        Glide.with(this)
                                .load(event.getDetails().getPosterUrl())
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_image_error)
                                .fallback(R.drawable.ic_image_placeholder)
                                .into(eventPosterImageView);
                    }
                }
            }
        });

        // Establishing Buttons
        viewLists = findViewById(R.id.view_lists_button);
        //drawLottery = findViewById(R.id.draw_lottery_button);
        drawNewEntrant = findViewById(R.id.draw_new_user_button);
        sendNotifications = findViewById(R.id.sendNotificationsButton);
        saveQrButton = findViewById(R.id.save_qr_button);
        FrameLayout backButton = findViewById(R.id.organizer_event_back_button);
        FrameLayout editButton = findViewById(R.id.organizer_event_edit_button);
        FrameLayout mapButton = findViewById(R.id.organizer_event_map_button);

        viewLists.setOnClickListener(v -> {
            if (currentEventId == null || currentEventId.isEmpty()) {
                Toast.makeText(OrganizerEventActivity.this,
                        "Error: Event ID missing", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent listIntent = new Intent(OrganizerEventActivity.this, ListScreenActivity.class);
            listIntent.putExtra(ListScreenActivity.EXTRA_EVENT_ID, currentEventId);
            startActivity(listIntent);

        });

        // Send Notifications button
        sendNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentEvent != null) {
                    Intent intent = new Intent(OrganizerEventActivity.this, OrganizerSendNotifications.class);
                    intent.putExtra("EVENT_ID", currentEventId);
                    startActivity(intent);
                }
            }
        });

        // Save QR button
        saveQrButton.setOnClickListener(v -> {
            if (currentEventId == null || currentEventId.isEmpty()) {
                Toast.makeText(OrganizerEventActivity.this,
                        "Error: Event ID missing", Toast.LENGTH_SHORT).show();
                return;
            }
            saveQrToGallery(currentEventId);
        });

        // Draw new entrant button
        drawNewEntrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoolingService poolingService = new PoolingService();
                poolingService.drawReplacement(currentEventId)
                        .addOnSuccessListener(uid -> {
                            eventViewModel.getOrganizerById(uid).observe(OrganizerEventActivity.this, new Observer<Organizer>() {
                                @Override
                                public void onChanged(Organizer organizer) {
                                    eventViewModel.getOrganizerById(uid).removeObserver(this);

                                    if (organizer != null) {
                                        String organizerName = organizer.getProfile().getUsername();

                                        Toast.makeText(OrganizerEventActivity.this,
                                                "Selected replacement: " + organizerName,
                                                Toast.LENGTH_SHORT).show();

                                        numberInWaitlist--;
                                        numberOfChosenEntrants++;
                                        updateButtonState();
                                    }
                                    else {
                                        Toast.makeText(OrganizerEventActivity.this,
                                                "Could not find details for user: " + uid,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(OrganizerEventActivity.this,
                                    "Failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });

            }
        });

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentEventId == null || currentEventId.isEmpty()) {
                    Toast.makeText(OrganizerEventActivity.this,
                            "Error: Event ID missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(OrganizerEventActivity.this, EditEventActivity.class);
                intent.putExtra("EVENT_ID", currentEventId);
                startActivity(intent);
            }
        });

        // Map Button
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentEventId == null || currentEventId.isEmpty()) {
                    Toast.makeText(OrganizerEventActivity.this,
                            "Error: Event ID missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentEvent != null && currentEvent.isGeolocationVerificationEnabled()) {
                    Intent intent = new Intent(OrganizerEventActivity.this, MapActivity.class);
                    intent.putExtra("EVENT_ID", currentEventId);
                    startActivity(intent);
                } else {
                    Toast.makeText(OrganizerEventActivity.this,
                            "Enable Geolocation Verification", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Generates the QR for this event's deep link and saves it into
     * Pictures/CoolioEvents using MediaStore.
     */
    private void saveQrToGallery(String eventId) {
        new Thread(() -> {
            String qrContent = "coolioevents://event/" + eventId;

            Bitmap bmp;
            try {
                bmp = QRCodeUtil.make(qrContent, 1024);
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(OrganizerEventActivity.this,
                                "Failed to generate QR: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
                return;
            }

            String fileName = "QR_" + eventId + ".png";

            File picturesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
            );
            File coolioDir = new File(picturesDir, "CoolioEvents");
            if (!coolioDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                coolioDir.mkdirs();
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CoolioEvents");
            } else {
                File outFile = new File(coolioDir, fileName);
                values.put(MediaStore.Images.Media.DATA, outFile.getAbsolutePath());
            }

            try {
                Uri uri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );
                if (uri == null) {
                    throw new IOException("Failed to create MediaStore entry");
                }

                try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                    if (os == null) {
                        throw new IOException("Failed to open output stream");
                    }
                    boolean ok = bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                    if (!ok) {
                        throw new IOException("Failed to write QR image");
                    }
                }
                runOnUiThread(() ->
                        Toast.makeText(OrganizerEventActivity.this,
                                "QR code saved to gallery.",
                                Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(OrganizerEventActivity.this,
                                "Failed to save QR: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
