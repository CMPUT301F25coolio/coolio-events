package com.example.coolioevents.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coolioevents.R;
import com.example.coolioevents.User;

import java.util.ArrayList;

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
 * This class is an array adapter for both the entrant and organizer
 * list views that the administrator has. It displays the user's
 * username.
 *
 * RATIONALE:
 * Organizers and entrants both extend user so a single array adapter
 * was designed to display both types of users and to reduce code redundancy.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-19
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> userList;
    private Context context;
    public UserArrayAdapter(Context context, ArrayList<User> userList){
        super(context,0, userList);
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.profile_content, parent, false);
        }

        User organizer = userList.get(position);
        // Get Views
        TextView organizerName = view.findViewById(R.id.profile_name);
        // Set View
        organizerName.setText(organizer.getProfile().getUsername());

        return view;
    }
}
