package com.example.coolioevents.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class represents a lottery service for events.
 *
 * RATIONALE:
 * This class was designed to do different lottery functions based on
 * what the organizer wants to do.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-05
 */
public class LotteryService {

    public LotteryService() {
    }

    /**
     * This function selects random users from waitList, removes
     * them from the waitList and adds them to chosenList in the database.
     *
     * @param originalWaitlist entrants in waitlist before we select
     * @param entrantLimit the number of entrants to select
     */
    public LotteryResult selectEntrants(List<String> originalWaitlist, int entrantLimit) {
        List<String> entrantsToChoose = new ArrayList<>(originalWaitlist);
        List<String> selectedEntrants = new ArrayList<>();

        if (originalWaitlist.isEmpty()) {
            //Display a message somehow
            return new LotteryResult(selectedEntrants, entrantsToChoose);
        }

        if (entrantsToChoose.size() < entrantLimit) {
            selectedEntrants.addAll(entrantsToChoose);
            entrantsToChoose.clear(); // The whole waitlist is empty
        }
        else {
            Random random = new Random();
            for (int i = 0; i < entrantLimit; i++) {
                // Get Random Index within bounds of the list
                int randomIndex = random.nextInt(entrantsToChoose.size());

                // Get the entrant at that index
                String randomEntrant = entrantsToChoose.get(randomIndex);

                // Add entrant to selected entrants
                selectedEntrants.add(randomEntrant);

                // Remove entrant from waitlist
                entrantsToChoose.remove(randomIndex);
            }
        }
        // Return LotteryResult object with selected entrants and updated
        return new LotteryResult(selectedEntrants, entrantsToChoose);
    }
}
