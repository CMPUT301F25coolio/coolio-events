package com.example.coolioevents.organizer;
import android.app.DatePickerDialog;
import com.google.zxing.WriterException;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.example.coolioevents.util.QRCodeUtil;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Copyright 2025 Aasta Tsai & Parth Mittal & Ethan Diep
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
 * CreateEventActivity is a class for organizers to create events,
 * including picking or capturing poster images.
 * This activity allows organizers to input event details, select or capture a poster image,
 * and upload event information to Firebase Firestore and Storage.
 *
 * @author Aasta Tsai & Parth Mittal & Ethan Diep
 * @version 2.0
 * @since 2025-11-05
 */
public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etRegistrationPeriod, etEntrantLimit, etWaitingListLimit, etEventDateTime, etEventLocation;
    private ChipGroup etTags;
    private Button btnCreate, btnPickPoster, btnGenerateQr;
    private FrameLayout btnBack;
    private ImageView imgPosterPreview, imgQrPreview;
    private SwitchMaterial switchGeolocationVerification;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;

    private Uri posterUri = null;
    private String eventPosterPath;

    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private Calendar eventDateTimeCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);

    private ArrayList<String> selectedTags = new ArrayList<>();

    // QR-related
    private boolean qrRequested = false;
    private String eventId;

    /**
     * ActivityResultLauncher to pick poster image from gallery.
     */
    private final ActivityResultLauncher<String> pickPosterLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    posterUri = uri;
                    imgPosterPreview.setImageURI(uri);
                }
            });

    /**
     * This method initializes the activity, sets up UI components, and attaches event listeners.
     *
     * @param savedInstanceState
     *      Bundle containing the activity's previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // generate a stable eventId for this create session
        eventId = UUID.randomUUID().toString();

        etTitle = findViewById(R.id.etEventTitle);
        etDescription = findViewById(R.id.etEventDescription);
        etRegistrationPeriod = findViewById(R.id.etRegistrationPeriod);
        etEntrantLimit = findViewById(R.id.etEntrantLimit);
        etWaitingListLimit = findViewById(R.id.etWaitingListLimit);
        etEventDateTime = findViewById(R.id.etEventDateTime);
        etEventLocation = findViewById(R.id.etEventLocation);
        etTags = findViewById(R.id.etTags);

        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);
        imgPosterPreview = findViewById(R.id.imgPosterPreview);
        btnPickPoster = findViewById(R.id.btnPickPoster);
        switchGeolocationVerification = findViewById(R.id.geolocation_switch);

        // new QR views
        btnGenerateQr = findViewById(R.id.btnGenerateQr);
        imgQrPreview = findViewById(R.id.imgQrPreview);

        btnBack.setOnClickListener(v -> finish());
        btnPickPoster.setOnClickListener(v -> pickPosterLauncher.launch("image/*"));

        etRegistrationPeriod.setFocusable(false);
        etRegistrationPeriod.setOnClickListener(v -> showDateRangePicker());

        etEventDateTime.setFocusable(false);
        etEventDateTime.setOnClickListener(v -> showDateTimePicker());

        etTags.setOnCheckedStateChangeListener((chipGroup, checkedTags) -> {
            updateTags(chipGroup, checkedTags);
        });

        btnCreate.setOnClickListener(v -> createEvent());

        // generate + show QR on this create screen
        btnGenerateQr.setOnClickListener(v -> generateAndShowQr());
    }

    private void showDateTimePicker() {
        eventDateTimeCalendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    eventDateTimeCalendar.set(Calendar.YEAR, year);
                    eventDateTimeCalendar.set(Calendar.MONTH, month);
                    eventDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Now show time picker
                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                eventDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                eventDateTimeCalendar.set(Calendar.MINUTE, minute);

                                String formatted = dateTimeFormat.format(eventDateTimeCalendar.getTime());
                                etEventDateTime.setText(formatted);
                            },
                            eventDateTimeCalendar.get(Calendar.HOUR_OF_DAY),
                            eventDateTimeCalendar.get(Calendar.MINUTE),
                            true);
                    timePicker.show();
                },
                eventDateTimeCalendar.get(Calendar.YEAR),
                eventDateTimeCalendar.get(Calendar.MONTH),
                eventDateTimeCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showDateRangePicker() {
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        DatePickerDialog startPicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    startDateCalendar.set(year, month, dayOfMonth);

                    DatePickerDialog endPicker = new DatePickerDialog(this,
                            (view2, year2, month2, dayOfMonth2) -> {
                                endDateCalendar.set(year2, month2, dayOfMonth2);

                                if (endDateCalendar.before(startDateCalendar)) {
                                    Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Force end time to be 11:59 PM
                                endDateCalendar.set(Calendar.HOUR_OF_DAY, 23);
                                endDateCalendar.set(Calendar.MINUTE, 59);
                                endDateCalendar.set(Calendar.SECOND, 0);
                                endDateCalendar.set(Calendar.MILLISECOND, 0);

                                String text = dateFormat.format(startDateCalendar.getTime())
                                        + " - " + dateFormat.format(endDateCalendar.getTime());
                                etRegistrationPeriod.setText(text);
                            },
                            startDateCalendar.get(Calendar.YEAR),
                            startDateCalendar.get(Calendar.MONTH),
                            startDateCalendar.get(Calendar.DAY_OF_MONTH));
                    endPicker.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
                    endPicker.show();
                },
                startDateCalendar.get(Calendar.YEAR),
                startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH));
        startPicker.show();
    }

    /**
     * This method handles results from camera and gallery activities for poster image selection.
     *
     * @param requestCode
     *      Code identifying which activity returned a result
     * @param resultCode
     *      Result status from the called activity
     * @param data
     *      Intent containing returned data (if any)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            // Image picked from gallery
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                posterUri = selectedImageUri;
                imgPosterPreview.setImageURI(posterUri);
            }
            Toast.makeText(this, "Poster photo added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Builds a QR bitmap for this event's deep link and shows it.
     * Only marks qrRequested = true if generation worked.
     */
    private void generateAndShowQr() {
        // Block if QR already generated in this session
        if (qrRequested) {
            Toast.makeText(this, "QR code is already generated for this event.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(eventId)) {
            Toast.makeText(this, "Missing event id, can’t make QR", Toast.LENGTH_SHORT).show();
            return;
        }
        String deepLink = "coolioevents://event/" + eventId;
        try {
            Bitmap qrBitmap = QRCodeUtil.generateQRCode(deepLink);
            imgQrPreview.setImageBitmap(qrBitmap);
            qrRequested = true;   // remember that user wants QR saved on Create
        } catch (WriterException e) {
            Toast.makeText(this, "Failed to make QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This method creates a new event using input fields and uploads the details to Firebase Firestore.
     */
    private void createEvent() {
        btnCreate.setEnabled(false);  // prevent double click
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String registrationPeriod = etRegistrationPeriod.getText().toString().trim();
        String entrantLimitStr = etEntrantLimit.getText().toString().trim();
        String waitingLimitStr = etWaitingListLimit.getText().toString().trim();
        String eventLocation = etEventLocation.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(registrationPeriod) || TextUtils.isEmpty(entrantLimitStr)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            btnCreate.setEnabled(true);  // allow retry
            return;
        }

        int entrantLimit;
        try {
            entrantLimit = Integer.parseInt(entrantLimitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entrant limit must be a number", Toast.LENGTH_SHORT).show();
            btnCreate.setEnabled(true);  // allow retry
            return;
        }

        // optional waitlist limit
        Integer waitingListLimit = null;
        if (!waitingLimitStr.isEmpty()) {
            try {
                int parsed = Integer.parseInt(waitingLimitStr);
                if (parsed <= 0) {
                    Toast.makeText(this, "Waiting list limit must be greater than 0", Toast.LENGTH_SHORT).show();
                    btnCreate.setEnabled(true);
                    return;
                }
                waitingListLimit = parsed;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Waiting list limit must be a number", Toast.LENGTH_SHORT).show();
                btnCreate.setEnabled(true);
                return;
            }
        }

        Date eventDateTime = null;
        try {
            eventDateTime = dateTimeFormat.parse(etEventDateTime.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Invalid event date/time format", Toast.LENGTH_SHORT).show();
            btnCreate.setEnabled(true);  // allow retry
            return;
        }

        String organizerId = currentUser != null ? currentUser.getUid() : "unknown";

        // use the eventId we generated in onCreate so QR + event match
        if (TextUtils.isEmpty(eventId)) {
            eventId = UUID.randomUUID().toString();
        }
        String deepLink = "coolioevents://event/" + eventId;

        EventDetails details = new EventDetails(
                title,
                description,
                registrationPeriod,
                entrantLimit,
                waitingListLimit,
                eventDateTime,
                eventLocation,
                new Date(),
                selectedTags);
        if (startDateCalendar != null && endDateCalendar != null) {
            details.setStartDate(startDateCalendar.getTime());
            details.setEndDate(endDateCalendar.getTime());
        }

        details.setPosterUrl(eventPosterPath);
        Event event = new Event(eventId, organizerId, details);

        // Getting Geolocation Verification Boolean
        boolean geolocationVerificationEnabled = switchGeolocationVerification.isChecked();

        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("details", details);
        map.put("organizerId", organizerId);
        map.put("lotteryDone", false);
        map.put("waitlistEntrants", new ArrayList<String>());
        map.put("chosenEntrants", new ArrayList<String>());
        map.put("acceptedEntrants", new ArrayList<String>());
        map.put("cancelledEntrants", new ArrayList<String>());
        map.put("deepLink", deepLink);
        map.put("promoQrUrl", null);
        map.put("geolocationVerificationEnabled", geolocationVerificationEnabled);

        // 1) Create Firestore doc
        db.collection("events").document(eventId).set(map)
                .addOnSuccessListener(aVoid -> uploadAssetsAndFinish(eventId, deepLink))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCreate.setEnabled(true);  // allow retry
                });
    }

    /**
     * This method uploads the event poster and QR code to Firebase Storage, then updates the Firestore document.
     *
     * @param eventId
     *      Unique ID of the event
     * @param deepLink
     *      Deep link URL for the event QR code
     */
    private void uploadAssetsAndFinish(String eventId, String deepLink) {
        // 2) Upload poster (if selected) AND optionally upload QR image, then update Firestore with URLs

        StorageReference postersRef = storage.getReference().child("posters/" + eventId + ".jpg");
        StorageReference qrRef = storage.getReference().child("qrcodes/" + eventId + ".png");

        // Task A: Poster upload (optional)
        var posterTask = Tasks.forResult((String) null);
        if (posterUri != null) {
            try {
                InputStream in = getContentResolver().openInputStream(posterUri);
                Bitmap bmp = BitmapFactory.decodeStream(in);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                byte[] data = bos.toByteArray();
                posterTask = postersRef.putBytes(data)
                        .continueWithTask(t -> postersRef.getDownloadUrl())
                        .continueWith(t -> t.getResult() != null ? t.getResult().toString() : null);
            } catch (Exception e) {
                // Keep going even if poster fails
                posterTask = Tasks.forResult((String) null);
            }
        }

        // Task B: QR bitmap -> upload -> URL (only if user requested QR)
        var qrTask = Tasks.forResult((String) null);
        if (qrRequested) {
            qrTask = Tasks.call(() -> {
                        Bitmap qr = QRCodeUtil.generateQRCode(deepLink);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        qr.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        return bos.toByteArray();
                    })
                    .onSuccessTask(bytes -> qrRef.putBytes(bytes))
                    .continueWithTask(t -> qrRef.getDownloadUrl())
                    .continueWith(t -> t.getResult() != null ? t.getResult().toString() : null);
        }

        // When both finish, update doc
        Tasks.whenAllSuccess(posterTask, qrTask).addOnSuccessListener(results -> {
            String posterUrl = (String) results.get(0);
            String qrUrl = (String) results.get(1);  // may be null if qrRequested == false

            Map<String, Object> updates = new HashMap<>();

            if (posterUrl != null) {
                updates.put("details.posterUrl", posterUrl);
            }
            if (qrRequested && qrUrl != null) {
                updates.put("promoQrUrl", qrUrl);
            }

            // If no poster/QR uploads, just finish
            if (updates.isEmpty()) {
                Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateEventActivity.this, OrganizerActivity.class));
                finish();
                return;
            }

            db.collection("events").document(eventId).update(updates)
                    .addOnSuccessListener(x -> {
                        Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateEventActivity.this, OrganizerActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Event created, but asset update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CreateEventActivity.this, OrganizerActivity.class));
                        finish();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Event created, asset upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(CreateEventActivity.this, OrganizerActivity.class));
            finish();
        });
    }

    /**
     * This method updates the event's selectedTags list to match what is currently selected
     * on the selected chips (tags). Limits the amount of selected tags to 3
     *
     * @param chipGroup
     *      Chip group to get selected tags from
     * @param checkedTags
     *      List of checkedTags viewIds
     */
    private void updateTags(ChipGroup chipGroup, List<Integer> checkedTags) {
        ArrayList<String> newSelectTags = new ArrayList<>(); // Newly selected tags to be put into selectedTags
        if (checkedTags.size() < 4)
            // Limit the number of tags to be 3
            for (Integer tagId : checkedTags) {
                Chip tag = chipGroup.findViewById(tagId);
                newSelectTags.add(tag.getText().toString());
            }
        else {
            // If tag size is currently 3, dont add any tag, instead remove all tags and warn user
            chipGroup.clearCheck();
            Toast.makeText(this, "Max of 3 tags allowed - tags reset", Toast.LENGTH_SHORT).show();
        }
        selectedTags.clear();
        selectedTags.addAll(newSelectTags);
        System.out.println(selectedTags);
    }
}
