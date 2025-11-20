package com.example.coolioevents;

import java.util.Date;
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
 * This class defines a Notification
 * It contains attributes related to a given notification
 * This object it used to store and send notifications to users
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-18
 */
public class NotificationData {

    String notifId;
    Date createdAt;
    String eventId;
    String title;
    String message;
    boolean shown;
    String type;
    String uid;
    String sender;
    String receiver;
    public NotificationData() {
    }
    public NotificationData(Date createdAt, String eventId, String message, boolean shown, String type, String uid, String sender, String receiver) {
        this.notifId = notifId;
        this.createdAt = createdAt;
        this.eventId = eventId;
        this.message = message;
        this.shown = shown;
        this.type = type;
        this.uid = uid;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * This method gets notification's unique Id
     * @return
     *      Returns the notification's id
     */
    public String getNotifId() {
        return notifId;
    }

    /**
     * This method sets notification's Id
     * @param notifId
     *      Notification id
     */
    public void setNotifId(String notifId) {
        this.notifId = notifId;
    }

    /**
     * This method gets the Date the notification was created
     * @return
     *  Returns the date at which the notification was made
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method sets the date the notification was made
     * @param createdAt
     *      The date at which the notification was made
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method gets eventId that is associated with this notification
     * @return
     *      Notification's eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * This method sets eventId associated with the notification
     * @param eventId
     *      EventId associated with the notification
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * This method gets the title of the notification
     * @return
     *      Title of the notification
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method sets Title of the notification
     * @param title
     *      Title of notification
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method gets the message of the notification
     * @return
     *      Message of the notification
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method sets message of the notification
     * @param message
     *      Message of notification
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This method checks if the notification was shown to its user or not
     * @return
     * True:    If the notification was shown to its user
     * False:   If the notification was not shown to its user
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * This method sets the shown status of the notification to true or false
     * @param shown
     * Whether the notification was shown (true) or not shown to its user (false)
     */
    public void setShown(boolean shown) {
        this.shown = shown;
    }

    /**
     * This method gets the type of the notification
     * @return
     *     Type of the notification
     */
    public String getType() {
        return type;
    }

    /**
     * This method sets the type of the notification
     * @param type
     *     The Type of the notification
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method gets the user id this notification is for
     * @return
     *     UserId of user to receive the notification
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method sets the user id this notification is for
     * @param uid
     *     UserId of user to receive the notification
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
