package com.example.assignment2quiz;

import com.google.firebase.database.DataSnapshot;

/**
 * Interfaced used as a callback for asynchronous functions for inputting external code.
 * Primary use is executing code when data is retrieved from Firebase CloudDatabase.
 */
public interface Callback {
    /**
     * On successful retrieval of desired data. Pass DataSnapshot parameter if required.
     * @param snap
     */
    void onCallback(DataSnapshot snap);

    /**
     * On failure on attempt to retrieve desired data.
     */
    void onFailure();
}
