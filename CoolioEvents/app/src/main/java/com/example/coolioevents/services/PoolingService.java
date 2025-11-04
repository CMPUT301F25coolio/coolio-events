package com.example.coolioevents.services;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
  Pooling logic for organizers.
   - Take the first uid from waitlistEntrants and append to chosenEntrants
   - Do it inside a Firestore transaction so two organizers cant race each other
   - Expose a simple non transactional helper as well for future flows
   - I copy lists before mutating to avoid weird cases where the snapshot list might be immutable.
   - Public API (method names signatures) match what teammates already used.*/
public class PoolingService {
    // one Firestore instance for the service
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /*
      Pops the first person from waitlistEntrants and pushes into chosenEntrants atomically.
      @param eventId Firestore document id inside the events collection
      return Task resolved with the uid that got promoted*/
    public Task<String> drawReplacement(String eventId) {
        return db.runTransaction((Transaction.Function<String>) transaction -> {
            // the event doc we want to read or mutate
            var eventRef = db.collection("events").document(eventId);
            // current snapshot data
            Map<String, Object> data = transaction.get(eventRef).getData();
            if (data == null) {
                throw new IllegalStateException("Event not found");
            }
            // pull lists copy into mutable lists so remove or add is safe
            List<String> wait = mutableStringList(data, "waitlistEntrants");
            List<String> chosen = mutableStringList(data, "chosenEntrants");
            if (wait.isEmpty()) {
                throw new IllegalStateException("No one in waitlist");
            }
            // popfront then push and simple queue semantics
            String promoted = wait.remove(0);
            chosen.add(promoted);
            // write both arrays back within the same transaction
            transaction.update(eventRef, "waitlistEntrants", wait);
            transaction.update(eventRef, "chosenEntrants", chosen);
            // return who we picked so the UI can show it
            return promoted;
        });
    }
    /*
      Direct move nontransactional remove from waitlist and add to chosen
      Useful for admin tools or single writer flows Not used by the main story,
      but keeping it for future hooks.*/
    public Task<Void> moveUid(String eventId, String uid) {
        var ref = db.collection("events").document(eventId);
        return Tasks.whenAll(
                ref.update("waitlistEntrants", FieldValue.arrayRemove(uid)),
                ref.update("chosenEntrants", FieldValue.arrayUnion(uid))
        );
    }
    //helpers
    /*
      Reads a field from the snapshot map and returns a mutable List<String>
      If the field is missing or not a list returns an empty list*/
    @SuppressWarnings("unchecked")
    private static List<String> mutableStringList(Map<String, Object> snap, String field) {
        Object raw = snap.get(field);
        if (raw instanceof List) {
            // copy so we never mutate Firestores internal list directly
            return new ArrayList<>((List<String>) raw);
        }
        return new ArrayList<>();
    }
}
