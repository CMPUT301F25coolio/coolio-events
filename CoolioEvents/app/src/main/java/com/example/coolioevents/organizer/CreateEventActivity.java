package com.example.coolioevents.organizer;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etRegistrationPeriod, etEntrantLimit;
    private Button btnCreate, btnPickPoster;
    private ImageButton btnBack;
    private ImageView imgPosterPreview;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;

    private Uri posterUri = null;

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
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        etTitle = findViewById(R.id.etEventTitle);
        etDescription = findViewById(R.id.etEventDescription);
        etRegistrationPeriod = findViewById(R.id.etRegistrationPeriod);
        etEntrantLimit = findViewById(R.id.etEntrantLimit);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);
        imgPosterPreview = findViewById(R.id.imgPosterPreview);
        btnPickPoster = findViewById(R.id.btnPickPoster);

        btnBack.setOnClickListener(v -> finish());
        btnPickPoster.setOnClickListener(v -> pickPosterLauncher.launch("image/*"));
        btnCreate.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String registrationPeriod = etRegistrationPeriod.getText().toString().trim();
        String entrantLimitStr = etEntrantLimit.getText().toString().trim();

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

        String organizerId = currentUser != null ? currentUser.getUid() : "unknown";
        String status = "open";
        String eventId = UUID.randomUUID().toString();
        String deepLink = "coolioevents://event/" + eventId;

        EventDetails details = new EventDetails(title, description, registrationPeriod, entrantLimit, status, new Date());
        Event event = new Event(eventId, organizerId, details);

        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("details", details);
        map.put("organizerId", organizerId);
        map.put("waitlistEntrants", new ArrayList<String>());
        map.put("deepLink", deepLink);
        map.put("posterUrl", null);
        map.put("promoQrUrl", null);

        // 1) Create Firestore doc
        db.collection("events").document(eventId).set(map)
                .addOnSuccessListener(aVoid -> uploadAssetsAndFinish(eventId, deepLink))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

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