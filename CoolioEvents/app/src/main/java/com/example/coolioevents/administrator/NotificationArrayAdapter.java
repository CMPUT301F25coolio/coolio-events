package com.example.coolioevents.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coolioevents.NotificationData;
import com.example.coolioevents.R;
import com.example.coolioevents.User;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class is an array adapter for both the notifications
 * list views that the administrator has. It displays the notifications
 * sender, recipient, type, and date it was sent.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-19
 */
public class NotificationArrayAdapter extends ArrayAdapter<NotificationData> {
    private ArrayList<NotificationData> notificationList;
    private Context context;
    public NotificationArrayAdapter(Context context, ArrayList<NotificationData> notificationList){
        super(context,0, notificationList);
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.administrator_notification_content, parent, false);
        }

        NotificationData notification = notificationList.get(position);
        // Get Views
        TextView date = view.findViewById(R.id.date_text);
        TextView sender = view.findViewById(R.id.sender_text);
        TextView receiver = view.findViewById(R.id.receiver_text);
        TextView notifType = view.findViewById(R.id.notif_type_text);

        // Set View - Date
        Date notificationDate = notification.getCreatedAt();

        /*
        Taken From: https://stackoverflow.com/questions/17807777/simpledateformatstring-template-locale-locale-with-for-example-locale-us-for
            License: https://creativecommons.org/licenses/by-sa/3.0/
            Authored by: jasdmystery
            Taken by: Avery Dancocks
            Taken on: 11/19/25
         */
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String dateString = format.format(notificationDate);

        date.setText(dateString);

        // Set View - Sender
        String senderString = notification.getSender();
        if (senderString != null) {
            sender.setText(senderString);
        }
        else{
            sender.setText("Unknown");
        }

        // Set View - Receiver
        String receiverString = notification.getReceiver();
        if (receiverString != null) {
            receiver.setText(receiverString);
        }
        else{
            receiver.setText("Unknown");
        }

        // Set View - Notification Type
        String type = notification.getType();

            // Automated Notifications
        if (type.equals("entrantChosen")) {
            notifType.setText("Chosen");
        }
        if (type.equals("organizerLotteryDone")) {
            notifType.setText("Lottery Done");
        }
        if (type.equals("entrantNotChosen")) {
            notifType.setText("Not Chosen");
        }

            // Manual Notifications "Sent" notifications
        if (type.equals("organizerToWaitlistEntrants")) {
            notifType.setText("WL Entrant");
        }
        if (type.equals("organizerToChosenEntrants")) {
            notifType.setText("Chosen Entrant");
        }
        if (type.equals("organizerToCancelledEntrants")) {
            notifType.setText("Cancelled Entrant");
        }
        if (type.equals("organizerToAcceptedEntrants")) {
            notifType.setText("Accepted Entrant");
        }

        return view;
    }
}
