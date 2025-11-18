package com.example.coolioevents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolioevents.models.Notifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notifications> notificationList = new ArrayList<>();
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

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listenForNotifications();

        listenForNotifications();
        return view;
    }

    private void listenForNotifications() {
        db.collection("Notifications")
                .whereEqualTo("uid", currentUid)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        return;
                    }

                    notificationList.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Notifications notif = doc.toObject(Notifications.class);
                            notificationList.add(notif);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
