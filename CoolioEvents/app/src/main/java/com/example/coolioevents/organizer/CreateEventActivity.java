package com.example.coolioevents.organizer;

import com.example.coolioevents.organizer.Camera;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.example.coolioevents.util.QRCodeUtil;
import com.google.android.gms.tasks.Tasks;
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
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * Copyright 2025 Aasta Tsai & Parth Mittal
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
 * CreateEventActivity is a class for organizers to create events,
 * including picking or capturing poster images.
 * This activity allows organizers to input event details, select or capture a poster image,
 * and upload event information to Firebase Firestore and Storage.
 *
 * @author Aasta Tsai & Parth Mittal
 * @version 1.0
 * @since 2025-11-05
 */
public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etRegistrationPeriod, etEntrantLimit, etEventDateTime, etEventLocation;
    private Button btnCreate, btnPickPoster, btnTakePhoto;
    private ImageButton btnBack;
    private ImageView imgPosterPreview;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;

    private Uri posterUri = null;
    private Camera camera;
    private String eventPosterPath;

    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private Calendar eventDateTimeCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);

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

        camera = new Camera(this);

        etTitle = findViewById(R.id.etEventTitle);
        etDescription = findViewById(R.id.etEventDescription);
        etRegistrationPeriod = findViewById(R.id.etRegistrationPeriod);
        etEntrantLimit = findViewById(R.id.etEntrantLimit);
        etEventDateTime = findViewById(R.id.etEventDateTime);
        etEventLocation = findViewById(R.id.etEventLocation);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);
        imgPosterPreview = findViewById(R.id.imgPosterPreview);
        btnPickPoster = findViewById(R.id.btnPickPoster);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        btnBack.setOnClickListener(v -> finish());
        btnPickPoster.setOnClickListener(v -> pickPosterLauncher.launch("image/*"));
        btnTakePhoto.setOnClickListener(v -> camera.takePicture(this));

        etRegistrationPeriod.setFocusable(false);
        etRegistrationPeriod.setOnClickListener(v -> showDateRangePicker());

        etEventDateTime.setFocusable(false);
        etEventDateTime.setOnClickListener(v -> showDateTimePicker());

        btnCreate.setOnClickListener(v -> createEvent());
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

        if (resultCode == RESULT_OK) {
            if (requestCode == Camera.REQUEST_IMAGE_CAPTURE) {
                // Picture taken with camera
                eventPosterPath = camera.getCurrentPhotoPath();
                if (eventPosterPath != null) {
                    File file = new File(eventPosterPath);
                    posterUri = Uri.fromFile(file);
                    Bitmap bitmap = BitmapFactory.decodeFile(eventPosterPath);
                    imgPosterPreview.setImageBitmap(bitmap);
                }
            }
            else if (requestCode == Camera.REQUEST_IMAGE_PICK && data != null) {
                // Image picked from gallery
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    posterUri = selectedImageUri;
                    imgPosterPreview.setImageURI(posterUri);
                }
            }
            Toast.makeText(this, "Poster photo added successfully", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * This method creates a new event using input fields and uploads the details to Firebase Firestore.
     */
    private void createEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String registrationPeriod = etRegistrationPeriod.getText().toString().trim();
        String entrantLimitStr = etEntrantLimit.getText().toString().trim();
        String eventLocation = etEventLocation.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(registrationPeriod) || TextUtils.isEmpty(entrantLimitStr)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int entrantLimit;
        try {
            entrantLimit = Integer.parseInt(entrantLimitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entrant limit must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        Date eventDateTime = null;
        try {
            eventDateTime = dateTimeFormat.parse(etEventDateTime.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Invalid event date/time format", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = currentUser != null ? currentUser.getUid() : "unknown";
        String eventId = UUID.randomUUID().toString();
        String deepLink = "coolioevents://event/" + eventId;

        EventDetails details = new EventDetails(
                title,
                description,
                registrationPeriod,
                entrantLimit,
                eventDateTime,
                eventLocation,
                new Date());
        if (startDateCalendar != null && endDateCalendar != null) {
            details.setStartDate(startDateCalendar.getTime());
            details.setEndDate(endDateCalendar.getTime());
        }

        details.setPosterUrl(eventPosterPath);
        details.setPosterUrl(eventPosterPath);
        Event event = new Event(eventId, organizerId, details);

        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("details", details);
        map.put("organizerId", organizerId);
        map.put("waitlistEntrants", new ArrayList<String>());
        map.put("chosenEntrants", new ArrayList<String>());
        map.put("acceptedEntrants", new ArrayList<String>());
        map.put("cancelledEntrants", new ArrayList<String>());
        map.put("deepLink", deepLink);
        map.put("posterUrl", null);
        map.put("promoQrUrl", null);

        // 1) Create Firestore doc
        db.collection("events").document(eventId).set(map)
                .addOnSuccessListener(aVoid -> uploadAssetsAndFinish(eventId, deepLink))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
        // 2) Upload poster (if selected) AND upload QR image, then update Firestore with URLs

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

        // Task B: QR bitmap -> upload -> URL
        var qrTask = Tasks.call(() -> {
            Bitmap qr = QRCodeUtil.generateQRCode(deepLink);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            qr.compress(Bitmap.CompressFormat.PNG, 100, bos);
            return bos.toByteArray();
        }).onSuccessTask(bytes -> qrRef.putBytes(bytes)
        ).continueWithTask(t -> qrRef.getDownloadUrl()
        ).continueWith(t -> t.getResult() != null ? t.getResult().toString() : null);

        // When both finish, update doc
        Tasks.whenAllSuccess(posterTask, qrTask).addOnSuccessListener(results -> {
            String posterUrl = (String) results.get(0);
            String qrUrl = (String) results.get(1);

            Map<String, Object> updates = new HashMap<>();
            updates.put("posterUrl", posterUrl);
            updates.put("promoQrUrl", qrUrl);

            db.collection("events").document(eventId).update(updates)
                    .addOnSuccessListener(x -> {
                        Toast.makeText(this, "Event created! Poster/QR saved.", Toast.LENGTH_SHORT).show();
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
}