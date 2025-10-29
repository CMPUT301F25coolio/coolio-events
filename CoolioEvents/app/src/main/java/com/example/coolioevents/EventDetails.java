package com.example.coolioevents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This is a class the defines an Event's Details
 */
public class EventDetails {
    private String eventName;
    private String eventDescription;
    private String registrationPeriod; // string input, e.g. "2025/10/28 - 2025/11/11"
    private int entrantLimit;
    private String status; // "open" or "closed"
    private Date postedDate;
    private Date startDate;
    private Date endDate;

    public EventDetails() {
        // Empty constructor needed for Firebase
    }

    public EventDetails(String eventName, String eventDescription, String registrationPeriod,
                        int entrantLimit, String status, Date postedDate) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.registrationPeriod = registrationPeriod;
        this.entrantLimit = entrantLimit;
        this.status = status;
        this.postedDate = postedDate;

        parseRegistrationPeriod();  // Convert to date objects
        updateStatus();             // Update open/closed based on date

    }

    // Getters

    /**
     * This method gets the name of the event.
     * @return
     *      The name of the event
     */
    public String getEventName() { return eventName; }

    /**
     * This method gets the description of the event.
     * @return
     *      The description of the event
     */
    public String getEventDescription() { return eventDescription; }
    /**
     * This method gets the registration period of the event.
     * @return
     *      The registration period of the event
     */
    public String getRegistrationPeriod() { return registrationPeriod; }

    /**
     * This method gets the entrant limit of the event.
     * @return
     *      The entrant limit of the event
     */
    public int getEntrantLimit() { return entrantLimit; }

    /**
     * This method gets the status of the event.
     * @return
     *      The status of the event
     */
    public String getStatus() { return status; }

    /**
     * This method gets the date the the event was posted.
     * @return
     *      The date the event was posted
     */
    public Date getPostedDate() { return postedDate; }

    // Setters

    /**
     * This method sets the event's name to eventName
     * @param eventName
     *      The name of the vent
     */
    public void setEventName(String eventName) { this.eventName = eventName; }

    /**
     * This method sets the event's description to eventDescription
     * @param eventDescription
     *      The description of the event
     */
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    /**
     * This method sets the event's registration period to registrationPeriod
     * @param registrationPeriod
     *      The registration period of the vent
     */
    public void setRegistrationPeriod(String registrationPeriod) {
        this.registrationPeriod = registrationPeriod;
        parseRegistrationPeriod();
        updateStatus();
    }

    /**
     * This method sets the entrant limit of the event to entrantLimit
     * @param entrantLimit
     *      The limit of entrants in the event
     */
    public void setEntrantLimit(int entrantLimit) { this.entrantLimit = entrantLimit; }

    /**
     * This method sets the entrant limit of the event to status
     * @param status
     *      The status of the event
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Parses the registration period string (e.g., "2025/10/28 - 2025/11/11") into startDate and endDate.
     */
    private void parseRegistrationPeriod() {
        if (registrationPeriod == null || !registrationPeriod.contains("-")) {
            startDate = null;
            endDate = null;
            return;
        }

        try {
            String[] parts = registrationPeriod.split("-");
            if (parts.length == 2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                startDate = sdf.parse(parts[0].trim());
                endDate = sdf.parse(parts[1].trim());
            }
        } catch (ParseException e) {
            startDate = null;
            endDate = null;
            e.printStackTrace();
        }
    }

    /**
     * Updates the event's status based on the current date and registration period.
     * - "open" if today's date is between start and end
     * - "closed" otherwise
     */
    private void updateStatus() {
        if (startDate == null || endDate == null) {
            status = "unknown";
            return;
        }

        Date now = new Date();
        if (now.after(startDate) && now.before(endDate)) {
            status = "open";
        } else {
            status = "closed";
        }
    }
}
