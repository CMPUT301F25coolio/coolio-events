package com.example.coolioevents.services;


import java.util.List;


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
 * This class represents the result for a lottery.
 *
 * RATIONALE:
 * This class allows for objects to hold the selected entrants
 * as well as updated waitlist so they can be modified in the firebase.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-05
 */
public class LotteryResult {
    private List<String> selectedEntrants;
    private List<String> remainingWaitlist;


    /**
     * This is a constructor, it makes a new LotteryResult object that
     * holds the selected entrants and remaining waitlist entrants for
     * an event lottery.
     *
     * @param selectedEntrants entrants who were selected from the lottery
     * @param remainingWaitlist entrants still left in the waitlist after the lottery
     */
    public LotteryResult(List<String> selectedEntrants, List<String> remainingWaitlist) {
        this.selectedEntrants = selectedEntrants;
        this.remainingWaitlist = remainingWaitlist;
    }


    /**
     * This function returns the selectedEntrants
     * @return selectedEntrants
     */
    public List<String> getSelectedEntrants() {
        return selectedEntrants;
    }


    /**
     * This function returns the remainingWaitlist
     * @return remainingWaitlist
     */
    public List<String> getRemainingWaitlist() {
        return remainingWaitlist;
    }
}
