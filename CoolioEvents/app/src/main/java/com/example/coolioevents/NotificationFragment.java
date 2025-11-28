package com.example.coolioevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
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
        //adding the delete notification feature
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false; // no drag & drop
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        NotificationData notif = notificationList.get(position);

                        if (direction == ItemTouchHelper.LEFT) {
                            // 🔴 DELETE from Firestore + list
                            db.collection("notifications")
                                    .document(notif.getNotifId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Notification deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error deleting", Toast.LENGTH_SHORT).show();
                                    });

                            notificationList.remove(position);
                            adapter.notifyItemRemoved(position);

                        } else if (direction == ItemTouchHelper.RIGHT) {
                            // ✅ Optional: swipe right = mark as read
                            db.collection("notifications")
                                    .document(notif.getNotifId())
                                    .update("shown", true)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Marked as read", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error updating", Toast.LENGTH_SHORT).show();
                                    });

                            // put it back visually (we didn't remove it)
                            adapter.notifyItemChanged(position);
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;
                        Paint paint = new Paint();

                        if (dX < 0) {
                            // 🔴 Swiping LEFT → red delete background
                            paint.setColor(Color.parseColor("#FF756C"));
                            RectF background = new RectF(
                                    itemView.getRight() + dX,  // follow finger
                                    itemView.getTop(),
                                    itemView.getRight(),
                                    itemView.getBottom()
                            );
                            c.drawRect(background, paint);

                        } else if (dX > 0) {
                            // 🟢 Swiping RIGHT →  "mark as read" color
                            paint.setColor(Color.parseColor("#90E79C")); // light green
                            RectF background = new RectF(
                                    itemView.getLeft(),
                                    itemView.getTop(),
                                    itemView.getLeft() + dX,
                                    itemView.getBottom()
                            );
                            c.drawRect(background, paint);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
                        Collections.sort(notificationList); // Sorts notifications based on date sent
                    }

                    // *** CRUCIAL DEBUGGING STEP ***
                    // This toast confirms how many items were retrieved from the database
                    //Toast.makeText(getContext(), "Notifications loaded: " + notificationList.size() + " items.", Toast.LENGTH_SHORT).show();

                    adapter.notifyDataSetChanged();
                });
    }
}