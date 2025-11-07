package com.example.coolioevents.organizer;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
/**
 * Copyright 2025 Niharika
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
 * Handles sending a notification to a specified user
 *
 * OUTSTANDING ISSUES:
 * This class is not fully complete or functional, still need to testing and debugging,
 * which  will be complete in project part 4
 *
 * @author Niharika
 * @version 1.0
 * @since 2025-11-05
 */
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
