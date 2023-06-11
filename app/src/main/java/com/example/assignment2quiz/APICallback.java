package com.example.assignment2quiz;

import org.json.JSONObject;

public interface APICallback {
    void onCallback(JSONObject response);
    void onFailure();
}
