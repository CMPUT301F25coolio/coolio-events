package com.example.coolioevents.events;

public class EventImageData {
    private String eventPoster;
    private String organizerUsername;
    private String organizerId;

    public EventImageData() {}

    public String getEventPoster() {
        return eventPoster;
    }

    public String getOrganizerUsername() {
        return organizerUsername;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    public void setOrganizerUsername(String organizerUsername) {
        this.organizerUsername = organizerUsername;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
}


