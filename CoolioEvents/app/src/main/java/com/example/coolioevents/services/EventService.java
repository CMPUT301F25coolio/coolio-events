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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Creates an event doc and also generates/stores a QR image locally. */
    public Task<Void> createEventWithQR(Context ctx, String title, String description,
                                        String registrationPeriod, int entrantLimit) {
        String eventId = db.collection("events").document().getId();
        EventDetails details = new EventDetails(title, description, registrationPeriod, entrantLimit, "open", new Date());

        Map<String, Object> doc = new HashMap<>();
        doc.put("eventId", eventId);
        doc.put("details", details);
        String qrContent = "coolioevents://event/" + eventId;   // deep-link payload
        doc.put("qrContent", qrContent);

        // 1) write firestore
        Task<Void> write = db.collection("events").document(eventId).set(doc);

        // 2) make + save QR locally (Pictures/CoolioEvents)
        Task<Void> save = Tasks.call(() -> {
            Bitmap bmp = QRCodeUtil.make(qrContent, 1024);
            String name = "QR_" + eventId + ".png";
            ContentValues v = new ContentValues();
            v.put(MediaStore.Images.Media.DISPLAY_NAME, name);
            v.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CoolioEvents");
            }
            try (OutputStream os = ctx.getContentResolver()
                    .openOutputStream(ctx.getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v))) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            }
            return null;
        });

        return Tasks.whenAll(write, save);
    }
}
