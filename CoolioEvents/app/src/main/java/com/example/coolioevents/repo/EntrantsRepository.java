package com.example.coolioevents.repo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Copyright 2025 Parth Mittal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * Handles fetching entrant lists (waitlist, chosen, and final) for a given event
 * from Firestore. Lets activities grab entrant data without touching Firestore
 * logic directly.
 *
 * RATIONALE:
 * Having a small data helper like this keeps Firestore code in one place and
 * avoids repeating the same query logic in multiple activities. Also supports
 * different field names used by teammates for better compatibility.
 *
 * OUTSTANDING ISSUES:
 * Firestore reads happen every time — no caching or live updates yet.
 * Future improvement could be using listeners so UI auto-refreshes when lists change.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-07
 */
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
