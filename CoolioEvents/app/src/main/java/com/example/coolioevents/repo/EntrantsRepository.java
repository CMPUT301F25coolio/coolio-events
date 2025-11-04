package com.example.coolioevents.repo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
  Purpose to Handles reading entrant lists waitlist, chosen, final for a given event
  Works as a tiny data helper so activities dont need to talk to Firestore directly
  Each list might have slightly different field names so I keep a small list of fallbacks.
  If nothing found, I just return an empty list instead of throwing.*/
public class EntrantsRepository {
    // single firestore reference that this repo will reuse
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Fetches people who are still waiting for the event.
    public Task<List<String>> getWaitlist(String eventId) {
        // some teams called it waitingEntrants instead of waitlistEntrants
        return fetchEntrantArray(eventId, Arrays.asList("waitlistEntrants", "waitingEntrants"));
    }
    // Fetches the ones who were selected or invited
    public Task<List<String>> getChosen(String eventId) {
        // different versions of the field names across groups
        return fetchEntrantArray(eventId, Arrays.asList("chosenEntrants", "invitedEntrants", "selectedEntrants"));
    }
    // Fetches people that actually enrolled or got accepted in the end
    public Task<List<String>> getFinalEnrolled(String eventId) {
        return fetchEntrantArray(eventId, Arrays.asList("acceptedEntrants", "enrolledEntrants", "finalEntrants"));
    }
    /*
      Common helper to check multiple possible field names.
      I wrote this way so I don't have to duplicate the same Firestore call*/
    private Task<List<String>> fetchEntrantArray(String eventId, List<String> possibleFields) {
        return db.collection("events").document(eventId).get()
                .onSuccessTask(snapshot -> {
                    // quick null check in case document missing
                    if (snapshot == null || !snapshot.exists()) {
                        return Tasks.forException(new IllegalStateException("Event not found in Firestore"));
                    }
                    // try to find whichever list field exists first
                    for (String field : possibleFields) {
                        Object data = snapshot.get(field);
                        if (data instanceof List) {
                            //noinspection unchecked
                            return Tasks.forResult((List<String>) data);
                        }
                    }
                    // if no matching list found, just return empty list to avoid crash
                    return Tasks.forResult(new ArrayList<String>());
                });
    }
}
