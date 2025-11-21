package com.example.coolioevents;

import android.app.Notification;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.organizer.Organizer;
import com.example.coolioevents.services.LotteryService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 2025 Ethan Diep
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
 * This class represents a Notification View Model
 * It is used to do any notification-related queries to or from the database
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-18
 */
public class NotificationViewModel  {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<ArrayList<NotificationData>> notificationsList = new MutableLiveData<>();

    /**
     * This method gets a list of a given user's (given by uid), unseen notifications
     * @param uid
     *      Notifications to the user of uid
     * @return
     *      List of notifications a given user has not seen yet
     */
    public ArrayList<NotificationData> getUserUnSeenNotifications(String uid){
        ArrayList<NotificationData> NotficationArray = new ArrayList<>();
        System.out.println(uid);
        db.collection("notifications").get().addOnSuccessListener(documentSnapshots -> {
            for (DocumentSnapshot document : documentSnapshots){

                if (document.getString("uid").equals(uid) && document.getBoolean("shown") == false) {
                    System.out.println("hello");
                    NotificationData notifObject = document.toObject(NotificationData.class);
                    notifObject.setNotifId(document.getId());
                    NotficationArray.add(notifObject);
                }
            }
        });
        return NotficationArray;
    }

    /**
     * This method updates a notification to be labelled as shown (user has seen the notification)
     * @param notifId
     *      Notification Id of the notification that was shown
     */
    public void setNotificationShown(String notifId) {
        db.collection("notifications").document(notifId).update("shown", true);
    }

    public MutableLiveData<ArrayList<NotificationData>> getNotifications() {
        ArrayList<NotificationData> notifications = new ArrayList<>();

        db.collection("notifications").whereEqualTo("shown", true).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        NotificationData notification = documentSnapshot.toObject(NotificationData.class);
                        if (notification != null) {
                            notifications.add(notification);
                        }
                    }
                    notificationsList.postValue(notifications);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "Error fetching notifications", e);
                    notificationsList.postValue(null);
                });

        return notificationsList;
    }


    /**
     * This method creates notifications on the db for a given group of people (entrant Lists)
     * @param eventId
     *  eventId of the notification
     * @param eventName
     *  name of the event the notification is being sent form
     * @param message
     *  Message to be sent
     * @param sendList
     *  List of people to send the notification to
     */
    public void createNotifications(String eventId, String eventName, String message, List<String> sendList) {
        for (String entrantId : sendList){
            // Iterates through each entrant on the sendlist and creates/sends them a notification
            String notifId = UUID.randomUUID().toString(); // Generate random notificaiton id for notification
            String title = String.format("New Notification from %s", eventName);
            NotificationData notification = new NotificationData(notifId, new Date(), eventId, title, message, false, "organizerToEntrant",  entrantId);
            db.collection("notifications").document(notifId).set(notification);
        }
    }
}
