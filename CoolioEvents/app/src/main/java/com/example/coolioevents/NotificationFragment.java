package com.example.coolioevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationData> notificationList = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notificationRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Check if a user is logged in before attempting to get their UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUid = currentUser.getUid();
            listenForNotifications();
        } else {
            Toast.makeText(getContext(), "Please log in to see notifications", Toast.LENGTH_SHORT).show();
        }

        // Removed the duplicate listenForNotifications() call that was in your original code.
        return view;
    }

    private void listenForNotifications() {
        if (currentUid == null || getContext() == null) return;

        // Corrected: Single, unified addSnapshotListener call
        db.collection("notifications").whereEqualTo("uid", currentUid)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        // Log the error for debugging in the console
                        Log.e("NotifFragment", "Error loading notifications: ", error);
                        Toast.makeText(getContext(), "Error loading data. Check console log for details.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    notificationList.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {

                            // Safely convert Firestore document to your Notifications model object
                            NotificationData notif = doc.toObject(NotificationData.class);
                            notif.setNotifId(doc.getId()); // Set the document ID
                            notificationList.add(notif);
                        }
                    }

                    // *** CRUCIAL DEBUGGING STEP ***
                    // This toast confirms how many items were retrieved from the database
                    Toast.makeText(getContext(), "Notifications loaded: " + notificationList.size() + " items.", Toast.LENGTH_SHORT).show();

                    adapter.notifyDataSetChanged();
                });
    }
}