package com.example.coolioevents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright 2025 Aasta Tsai
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
 * This class defines an event's event Details.
 * It provides necessary getters and setters for the different attributes
 * of the events details.
 *
 * @author Aasta Tsai
 * @version 1.0
 * @since 2025-11-05
 */
public class EventDetails {
    private String eventName;
    private String eventDescription;
    private String eventLocation;
    private String registrationPeriod; // string for display only, e.g. "2025/10/28 - 2025/11/11"
    private int entrantLimit;

    private Date eventDateTime; // actual event date and time
    private Date postedDate;
    private Date startDate;
    private Date endDate;
    private String posterUrl;

    private ArrayList<String> tags = new ArrayList<>();

    public EventDetails() {
        // Empty constructor needed for Firebase
    }


    public EventDetails(String eventName, String eventDescription, String registrationPeriod, int entrantLimit, Date eventDateTime, String eventLocation,
                         Date postedDate) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDateTime = eventDateTime;
        this.eventLocation = eventLocation;
        this.registrationPeriod = registrationPeriod;
        this.entrantLimit = entrantLimit;

        this.postedDate = postedDate;

        parseRegistrationPeriod();  // Convert to date objects
    }


    public EventDetails(String eventName, String eventDescription, String registrationPeriod, int entrantLimit, Date eventDateTime, String eventLocation,
                        Date postedDate, ArrayList<String> tags) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDateTime = eventDateTime;
        this.eventLocation = eventLocation;
        this.registrationPeriod = registrationPeriod;
        this.entrantLimit = entrantLimit;
        this.postedDate = postedDate;
        this.tags = tags;

        parseRegistrationPeriod();  // Convert to date objects
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
     * This method gets the description of the event.
     * @return
     *      The time of the event
     */
    public Date getEventDateTime() { return eventDateTime; }

    /**
     * This method gets the description of the event.
     * @return
     *      The location of the event
     */
    public String getEventLocation() { return eventLocation; }

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
    public String getStatus() {
        if (startDate == null || endDate == null) {
            return "unknown";
        }

        Date now = new Date();
        if (now.after(startDate) && now.before(endDate)) {
            return "open";
        } else {
            return "closed";
        }
        }

    /**
     * This method gets the date the the event was posted.
     * @return
     *      The date the event was posted
     */
    public Date getPostedDate() { return postedDate; }

    /**
     * This method gets the startDate of the event's Registration Period.
     * @return
     *      The start Date of the event's Registration Period
     */
    public Date getStartDate() { return startDate; }

    /**
     * This method gets the endDate of the event's Registration Period.
     * @return
     *      The end Date of the event's Registration Period
     */
    public Date getEndDate() { return endDate; }

    /**
     * This method gets the posterUrl of the event's poster
     * @return
     *      The Url of the event's poster
     */
    public String getPosterUrl() { return posterUrl; }

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
    }

    /**
     * This method sets the event's date and time to eventDateTime
     * @param eventDateTime
     *      The date and time of the event
     */
    public void setEventDateTime(Date eventDateTime) { this.eventDateTime = eventDateTime; }

    /**
     * This method sets the event's location to eventLocation
     * @param eventLocation
     *      The location of the event
     */
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    /**
     * This method sets the entrant limit of the event to entrantLimit
     * @param entrantLimit
     *      The limit of entrants in the event
     */
    public void setEntrantLimit(int entrantLimit) { this.entrantLimit = entrantLimit; }

    /**
     * This method sets the poster's Url of the event to posterUrl
     * @param posterUrl
     *      The Url of the poster
     */
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    /**
     * This method sets te poster's start date of the event to startDate
     * @param startDate
     *      the start date of the event
     */
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    /**
     * This method gets the tags of the event
     *      @return
     *      The tags of the event (Arraylist)
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * This method sets the tags of the event to array list "tags"
     * @param tags
     *      List of tags to be applied to the event
     */
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

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


}
