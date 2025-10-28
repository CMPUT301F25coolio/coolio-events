package com.example.coolioevents.services;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PoolingService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Pops the first person from waitlistEntrants -> pushes into chosenEntrants atomically. */
    public Task<String> drawReplacement(String eventId) {
        return db.runTransaction((Transaction.Function<String>) tr -> {
            var ref = db.collection("events").document(eventId);
            Map<String, Object> snap = tr.get(ref).getData();
            if (snap == null) throw new IllegalStateException("Event not found");

            List<String> wait = (List<String>) snap.get("waitlistEntrants");
            List<String> chosen = (List<String>) snap.get("chosenEntrants");
            if (wait == null) wait = new ArrayList<>();
            if (chosen == null) chosen = new ArrayList<>();

            if (wait.isEmpty()) throw new IllegalStateException("No one in waitlist");

            String replacement = wait.get(0);

            // local mutate
            wait.remove(0);
            chosen.add(replacement);

            // write back
            tr.update(ref, "waitlistEntrants", wait);
            tr.update(ref, "chosenEntrants", chosen);

            return replacement;
        });
    }

    /** Helper if you just want server-side pop/push without reading whole arrays (requires Arrays). */
    public Task<Void> moveUid(String eventId, String uid) {
        var ref = db.collection("events").document(eventId);
        return Tasks.whenAll(
                ref.update("waitlistEntrants", FieldValue.arrayRemove(uid)),
                ref.update("chosenEntrants", FieldValue.arrayUnion(uid))
        );
    }
}
