package com.example.coolioevents.organizer;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
/*
  Lightweight notification log. Safe even without FCM
  Call SendNotification.sendToUser uid,message,eventId*/
public class SendNotification {
    public static void sendToUser(String uid, String message, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> n = new HashMap<>();
        n.put("uid", uid);
        n.put("message", message);
        n.put("eventId", eventId);
        n.put("createdAt", System.currentTimeMillis());
        db.collection("notifications").add(n);
    }
}
