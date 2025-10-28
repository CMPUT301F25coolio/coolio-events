package com.example.coolioevents.events;

import com.example.coolioevents.Event;

import java.util.ArrayList;
import java.util.Observable;

public class EventList extends Observable {
    private ArrayList<Event> eventList;

    public EventList() {
        this.eventList = new ArrayList<Event>();
    }
    public EventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }



}
