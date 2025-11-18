package com.example.coolioevents.models;

public class Notifications {
    private String id;
    private String eventId;
    private String message;
    private String type;
    private String uid;
    private boolean shown;
    private String createdAt;

    public Notifications() {
        // Required for Firebase deserialization
    }

    public Notifications(String id, String eventId, String message, String type,
                        String uid, boolean shown, String createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.message = message;
        this.type = type;
        this.uid = uid;
        this.shown = shown;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getUid() { return uid; }
    public boolean isShown() { return shown; }
    public String getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setUid(String uid) { this.uid = uid; }
    public void setShown(boolean shown) { this.shown = shown; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}


