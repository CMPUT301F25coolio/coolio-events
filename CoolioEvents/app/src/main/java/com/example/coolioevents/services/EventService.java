package com.example.coolioevents.services;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.util.QRCodeUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
 * Creates a new event in Firestore and generates a deep-link QR code for it.
 * The QR image is saved to MediaStore (Pictures/CoolioEvents). Also initializes
 * basic entrant arrays so pooling can work safely later.
 *
 * RATIONALE:
 * Keeping event creation and QR generation in one service keeps the flow simple,
 * avoids duplicate code in activities, and makes it easier to test the full path
 * (Firestore write + QR save) in one place.
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-07
 */
/*  Flow:
    1. Make Firestore document for the event
    2. Make a deeplink string using the event ID
    3. Turn the deep link into a QR image
    4. Save the QR inside /Pictures/CoolioEvents on the device*/
public class EventService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /*
        Creates an event and also saves a QR code for it.
        ctx is context so we can access MediaStore
        other params are event info from CreateEventActivity
        Returns a Task that finishes only after both Firestore write
        and QR saving are done*/
    public Task<Void> createEventWithQR(Context ctx,
                                        String title,
                                        String description,
                                        String registrationPeriod,
                                        int entrantLimit,
                                        Date time,
                                        String location) {
        // Generate a fresh eventId so we can use it everywhere consistently
        String eventId = db.collection("events").document().getId();
        // Build event details object (Firestore stores this in "details")
        EventDetails details = new EventDetails(
                title, description, registrationPeriod, entrantLimit, time, location, new Date()
        );
        // Build the Firestore document for this event
        Map<String, Object> eventDoc = new HashMap<>();
        eventDoc.put("eventId", eventId);
        eventDoc.put("details", details);
        // Initialize arrays so nothing breaks later
        eventDoc.put("waitlistEntrants", new ArrayList<String>());
        eventDoc.put("chosenEntrants", new ArrayList<String>());
        // Deep link stored inside Firestore. App uses this for QR.
        String qrContent = "coolioevents://event/" + eventId;
        eventDoc.put("qrContent", qrContent);
        // Step 1 write event to Firestore
        Task<Void> writeTask = db.collection("events").document(eventId).set(eventDoc);
        // Step 2 save QR image to device gallery
        Task<Void> saveTask = saveQrToGallery(ctx, qrContent, eventId);
        // Wait for both to finish
        return Tasks.whenAll(writeTask, saveTask);
    }
    private Task<Void> saveQrToGallery(Context ctx, String qrContent, String eventId) {
        return Tasks.call(() -> {
            // Generate the QR bitmap
            Bitmap bmp = QRCodeUtil.make(qrContent, 1024);
            String fileName = "QR_" + eventId + ".png";
            // Make sure Pictures/CoolioEvents exists cuz emulator sometimes has nothing
            File picturesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
            );
            File coolioDir = new File(picturesDir, "CoolioEvents");
            // Create folder if missing
            if (!coolioDir.exists()) {
                coolioDir.mkdirs();
            }
            // Build metadata for MediaStore insert
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped storage style (Android 10+)
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CoolioEvents");
            } else {
                // Older style full path
                File outFile = new File(coolioDir, fileName);
                values.put(MediaStore.Images.Media.DATA, outFile.getAbsolutePath());
            }
            // Insert into MediaStore
            ContentResolver resolver = ctx.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                throw new IOException("Failed to create MediaStore entry for QR");
            }
            // Write the bitmap to the file
            try (OutputStream os = resolver.openOutputStream(uri)) {
                if (os == null) {
                    throw new IOException("Couldn't open output stream for QR");
                }
                boolean ok = bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                if (!ok) {
                    throw new IOException("Failed to compress QR image");
                }
            }
            return null;
        });
    }
}
