package com.example.coolioevents.services;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import com.example.coolioevents.EventDetails;
import com.example.coolioevents.util.QRCodeUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
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
 *
 * OUTSTANDING ISSUES (to tackle next part):
 * 1) Add basic input validation for required fields (e.g., title/description not empty).
 * 2) Surface clearer error messages to the UI (distinguish Firestore vs. MediaStore failure).
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-07
 */
/*
  Tiny service for event creation.
  Flow I used:
   1. create Firestore doc with some default arrays so pooling doesnt crash
   2. generate a deeplink string for the event
   3. render QR and save it to MediaStore Pictures/CoolioEvents
  Kept the public API the same so other files wont break.*/
public class EventService {
    // shared Firestore instance for this app
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /*
      Creates a brand new event and also saves a QR image locally.
      @param ctx Android context needed for MediaStore
      return a Task that completes after both Firestore write and QR save finish*/
    public Task<Void> createEventWithQR(Context ctx,
                                        String title,
                                        String description,
                                        String registrationPeriod,
                                        int entrantLimit,
                                        Date time,
                                        String location) {
        // generate an id upfront so we can use it both in Firestore and the qr content
        String eventId = db.collection("events").document().getId();
        // pack some basic info open is our initial status and date is createdAt
        EventDetails details = new EventDetails(
                title, description, registrationPeriod, entrantLimit, time, location, new Date()
        );
        // build the event document (I keep arrays initialized so pooling can safely mutate them)
        Map<String, Object> eventDoc = new HashMap<>();
        eventDoc.put("eventId", eventId);
        eventDoc.put("details", details);
        eventDoc.put("waitlistEntrants", new ArrayList<String>()); // default empty
        eventDoc.put("chosenEntrants",  new ArrayList<String>());  // default empty
        // deeplink payload (the app has an intentfilter for this scheme host)
        String qrContent = "coolioevents://event/" + eventId;
        eventDoc.put("qrContent", qrContent);
        // 1) write Firestore doc
        Task<Void> writeTask = db.collection("events").document(eventId).set(eventDoc);
        // 2) render and store the QR bitmap done off the main thread via Tasks.call
        Task<Void> saveTask = saveQrToGallery(ctx, qrContent, eventId);
        // complete when both are done
        return Tasks.whenAll(writeTask, saveTask);
    }
    /*
      Renders a QR code and saves it to MediaStore under Pictures CoolioEvents.
      I broke this out just to keep the main method readable.*/
    private Task<Void> saveQrToGallery(Context ctx, String qrContent, String eventId) {
        return Tasks.call(() -> {
            // generate a reasonably sharp QR (1024px)
            Bitmap bmp = QRCodeUtil.make(qrContent, 1024);
            String fileName = "QR_" + eventId + ".png";
            // describe the media entry we want to insert
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // on Android 10+, we can target a relative folder without write external storage
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CoolioEvents");
            }
            // insert the row and stream the bitmap into it
            var resolver = ctx.getContentResolver();
            var uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = uri == null ? null : resolver.openOutputStream(uri)) {
                if (os != null) {
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                }
            }
            return null;
        });
    }
}
