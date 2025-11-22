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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.Event;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.R;
import com.example.coolioevents.util.QRCodeUtil;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * EditEventActivity: same behaviour as CreateEventActivity but pre-fills fields
 * and updates the existing Firestore document instead of creating a new one.
 */
public class EditEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etRegistrationPeriod, etEntrantLimit, etEventDateTime, etEventLocation;
    private ChipGroup etTags;
    private Button btnSave, btnPickPoster, btnTakePhoto;
    private ImageButton btnBack;
    private ImageView imgPosterPreview;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Uri posterUri = null;
    private Camera camera;
    private String eventPosterPath;

    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private Calendar eventDateTimeCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
    private ArrayList<String> selectedTags = new ArrayList();
    private String eventId; // event being edited

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        camera = new Camera(this);

        etTitle = findViewById(R.id.etEventTitle);
        etDescription = findViewById(R.id.etEventDescription);
        etRegistrationPeriod = findViewById(R.id.etRegistrationPeriod);
        etEntrantLimit = findViewById(R.id.etEntrantLimit);
        etEventDateTime = findViewById(R.id.etEventDateTime);
        etEventLocation = findViewById(R.id.etEventLocation);
        etTags = findViewById(R.id.etTags);
        btnSave = findViewById(R.id.btnSave);
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
        etTags.setOnCheckedStateChangeListener((chipGroup, checkedTags) -> {
            updateTags(chipGroup, checkedTags);
        });

        // get event id from intent
        Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENT_ID");

        if (eventId != null) {
            loadEventAndPrefill(eventId);
        } else {
            Toast.makeText(this, "No event id provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadEventAndPrefill(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    if (doc.exists()) {
                        // attempt to convert to Event if your Event class exists
                        Event event = doc.toObject(Event.class);
                        if (event != null && event.getDetails() != null) {
                            EventDetails details = event.getDetails();

                            // prefill text fields
                            etTitle.setText(details.getEventName());
                            etDescription.setText(details.getEventDescription());
                            if (details.getRegistrationPeriod() != null) {
                                etRegistrationPeriod.setText(details.getRegistrationPeriod());
                            }
                            etEntrantLimit.setText(String.valueOf(details.getEntrantLimit()));
                            if (details.getEventDateTime() != null) {
                                eventDateTimeCalendar = Calendar.getInstance();
                                eventDateTimeCalendar.setTime(details.getEventDateTime());
                                etEventDateTime.setText(dateTimeFormat.format(details.getEventDateTime()));
                            }
                            etEventLocation.setText(details.getEventLocation());

                            if (details.getStartDate() != null) {
                                startDateCalendar = Calendar.getInstance();
                                startDateCalendar.setTime(details.getStartDate());
                            }
                            if (details.getEndDate() != null) {
                                endDateCalendar = Calendar.getInstance();
                                endDateCalendar.setTime(details.getEndDate());
                            }
                            for (String tag : event.getDetails().getTags()){
                                System.out.println(tag);
                            }
                            // Pre"check" already selected tags for the event
                                    /*Taken from: Google Gemini
                                    Prompt: how to check all chips in a chipgroup java android stuydio
                                    Taken by: Ethan Diep
                                    Taken on: 11/22/25
                                         */
                                for (int i=0; i < etTags.getChildCount(); i++){
                                    View child = etTags.getChildAt(i);
                                    if (child instanceof Chip){
                                        if (event.getDetails().getTags().contains(((Chip) child).getText().toString())){
                                            etTags.check(child.getId());
                                        }
                                    }
                                }




                            // if posterUrl is a local path you could attempt to show it.
                            // We leave poster preview alone for remote URLs (Firebase Storage).
                            if (details.getPosterUrl() != null && details.getPosterUrl().startsWith("file")) {
                                try {
                                    Uri u = Uri.parse(details.getPosterUrl());
                                    posterUri = u;
                                    imgPosterPreview.setImageURI(u);
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void showDateTimePicker() {
        if (eventDateTimeCalendar == null) {
            eventDateTimeCalendar = Calendar.getInstance();
        }

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
        if (startDateCalendar == null) startDateCalendar = Calendar.getInstance();
        if (endDateCalendar == null) endDateCalendar = Calendar.getInstance();

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
     * Save changes to existing event document and upload assets if needed.
     */
    private void saveChanges() {
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
            String dtText = etEventDateTime.getText().toString();
            if (!TextUtils.isEmpty(dtText)) {
                eventDateTime = dateTimeFormat.parse(dtText);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid event date/time format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Read the existing details object and update fields
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Event event = doc.toObject(Event.class);
                    EventDetails details;
                    if (event != null && event.getDetails() != null) {
                        details = event.getDetails();
                    } else {
                        // fallback: build new details if mapping failed
                        details = new EventDetails();
                    }

                    details.setEventName(title);
                    details.setEventDescription(description);
                    details.setRegistrationPeriod(registrationPeriod);
                    details.setEntrantLimit(entrantLimit);
                    details.setEventLocation(eventLocation);
                    details.setTags(selectedTags);
                    if (eventDateTimeCalendar != null) details.setEventDateTime(eventDateTimeCalendar.getTime());
                    if (startDateCalendar != null) details.setStartDate(startDateCalendar.getTime());
                    if (endDateCalendar != null) details.setEndDate(endDateCalendar.getTime());
                    // posterUrl will be updated after poster upload (if any)
                    // write updated details back to doc (other fields remain)
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("details", details);

                    db.collection("events").document(eventId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                // Now upload assets (poster + QR) same as create
                                uploadAssetsAndFinish(eventId, "coolioevents://event/" + eventId);
                            })
                            .addOnFailureListener(e1 -> Toast.makeText(EditEventActivity.this, "Failed to save details: " + e1.getMessage(), Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e -> Toast.makeText(EditEventActivity.this, "Failed to load event for update: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * Upload poster if selected and generate + upload QR; then update posterUrl/promoQrUrl fields.
     * Reused (with minimal changes) from CreateEventActivity.
     */
    private void uploadAssetsAndFinish(String eventId, String deepLink) {
        StorageReference postersRef = storage.getReference().child("posters/" + eventId + ".jpg");
        StorageReference qrRef = storage.getReference().child("qrcodes/" + eventId + ".png");

        // Poster upload task
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
                posterTask = Tasks.forResult((String) null);
            }
        }

        // QR generation + upload
        var qrTask = Tasks.call(() -> {
            Bitmap qr = QRCodeUtil.generateQRCode(deepLink);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            qr.compress(Bitmap.CompressFormat.PNG, 100, bos);
            return bos.toByteArray();
        }).onSuccessTask(bytes -> qrRef.putBytes(bytes)
        ).continueWithTask(t -> qrRef.getDownloadUrl()
        ).continueWith(t -> t.getResult() != null ? t.getResult().toString() : null);

        Tasks.whenAllSuccess(posterTask, qrTask).addOnSuccessListener(results -> {
            String posterUrl = (String) results.get(0);
            String qrUrl = (String) results.get(1);

            Map<String, Object> updates = new HashMap<>();
            updates.put("posterUrl", posterUrl);
            updates.put("promoQrUrl", qrUrl);

            db.collection("events").document(eventId).update(updates)
                    .addOnSuccessListener(x -> {
                        Toast.makeText(this, "Event saved! Poster/QR saved.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditEventActivity.this, OrganizerActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Event saved, but asset update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditEventActivity.this, OrganizerActivity.class));
                        finish();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Event saved, but asset upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(EditEventActivity.this, OrganizerActivity.class));
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


