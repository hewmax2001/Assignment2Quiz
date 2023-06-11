package com.example.assignment2quiz;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TDBAPI {

    private static RequestQueue queue;
    private static final String BASE_QUIZ_URL = "https://opentdb.com/api.php?";
    private static final String CATEGORY_LIST_URL = "https://opentdb.com/api_category.php";

    private TDBAPI() {}

    public static void start(Context context) {
        setRequestQueue(context);
    }

    private static void setRequestQueue(Context context)
    {
        Log.d("JSON", " getRequestQueue ");
        if(queue == null)
        {
            queue = Volley.newRequestQueue(context);
            return;
        }

        return;
    }

    public static void setCategorySchema(APICallback callback) {
        getJSON(new APICallback() {
            @Override
            public void onCallback(JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("trivia_categories");
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject cat = categories.getJSONObject(i);
                        int id = cat.getInt("id");
                        String name = cat.getString("name");
                        Quiz.categorySchema.put(id, name);
                    }
                    // TODO get rid of this redundant
                    callback.onCallback(null);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure() {

            }
        }, CATEGORY_LIST_URL);
    }

    public static void generateNewQuiz(APICallback apiCallback, String name, String amount, String category, String difficulty, String type, String startDate, String endDate) {
        String quizURL = generateQuizAPIURL(amount, category, difficulty, type);
        getJSON(new APICallback() {
            @Override
            public void onCallback(JSONObject response) {
                apiCallback.onCallback(response);
            }

            @Override
            public void onFailure() {

            }
        }, quizURL);
    }

    private static void getJSON(APICallback callback, String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onCallback(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    public static String generateQuizAPIURL(String amount, String category, String difficulty, String type) {

        String url = BASE_QUIZ_URL;
        url += "amount=" + amount;

        if (!category.equals("") && !category.equals("Any")) {
            for (Map.Entry<Integer, String> entry : Quiz.categorySchema.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();
                if (value.equals(category)) {
                    url += "&category=" + key;
                    break;
                }
            }
        }

        if (!difficulty.equals("") && !difficulty.equals("Any")) {
            url += "&difficulty=" + difficulty;
        }

        if(!type.equals("") && !type.equals("Any")) {
            url += "&type=" + type;
        }

        return url;
    }
}

