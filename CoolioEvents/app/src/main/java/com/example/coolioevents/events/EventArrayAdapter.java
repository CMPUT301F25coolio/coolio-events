package com.example.coolioevents.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.coolioevents.Entrant.EntrantViewModel;
import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Context context;

    private FirebaseUser currentUser;
    public EventArrayAdapter(Context context, ArrayList<Event> eventList){
        super(context,0, eventList);
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_content, parent,false);
        }
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        EntrantViewModel viewModel = new EntrantViewModel();
        Event event = eventList.get(position);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView eventOrganizer = view.findViewById(R.id.eventOrganizer);
        TextView eventDescription = view.findViewById(R.id.eventDescription);
        TextView eventTime = view.findViewById(R.id.eventTime);
        TextView eventLocation = view.findViewById(R.id.eventLocation);
        TextView eventRegPrd = view.findViewById(R.id.eventRegPeriod);
        TextView eventMaxEntrees = view.findViewById(R.id.eventmaxEntrees);
        TextView eventStatus = view.findViewById(R.id.eventStatus);
        TextView eventUserStatus = view.findViewById(R.id.eventuserStatus);
        TextView eventWaitingCount = view.findViewById(R.id.eventWaitingCount);

        eventName.setText(event.getDetails().getEventName());
        eventOrganizer.setText(String.format("Posted By: %s", event.getOrganizer().getProfile().getUsername())); // Sets event organizer text
        eventDescription.setText(String.format("Description: %s", event.getDetails().getEventDescription())); // Sets event description text
        eventRegPrd.setText(String.format("Registration Period: %s", event.getDetails().getRegistrationPeriod())); // Sets event registration period text
        eventMaxEntrees.setText(String.format("Entrant Limit: %s", String.valueOf(event.getDetails().getEntrantLimit()))); // Sets entrant limit organizer text

        if (event.getDetails().getStatus().equals("open")) {
            //If event open make text open with green background
            eventStatus.setText("Open");
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greenshapebackground));
        }
        else{
            //If event closed make text open with red background
            eventStatus.setText(event.getDetails().getStatus());
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.redshapebackground));
        }
        if (event.getWaitlistEntrants().contains(currentUser.getUid())){
            //If the current user is in the waitlist, display an indicator that user is in the waiting list
            eventUserStatus.setText("In Waiting List");
            eventUserStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greenshapebackground));
        }
        else{
            //If the current user is not in the waitlist, display an indicator that user is  not in the waiting list
            eventUserStatus.setText("Not In Waiting List");
            eventUserStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greybuttonbackground));
        }
        return view;
    }

}
