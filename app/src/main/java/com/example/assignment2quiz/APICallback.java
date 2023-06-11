package com.example.assignment2quiz;

import org.json.JSONObject;

/**
 * Interface used to return a JSON response and execute external code.
 */
public interface APICallback {
    void onCallback(JSONObject response);
    void onFailure();
}
