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

/**
 * Static handler for OpenTDB API
 */
public class TDBAPI {

    // Volley Request queue
    private static RequestQueue queue;
    // Base URL to make requests to OpenTDB
    private static final String BASE_QUIZ_URL = "https://opentdb.com/api.php?";
    // URL to request categories of quizzes and their associated IDs
    private static final String CATEGORY_LIST_URL = "https://opentdb.com/api_category.php";

    /**
     * Cannot be instantiated.
     */
    private TDBAPI() {}

    /**
     * Start the TDBAPI handler and create requestQueue
     * @param context
     */
    public static void start(Context context) {
        setRequestQueue(context);
    }

    /**
     * Create and set Volley RequestQueue
     * @param context
     */
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

    /**
     * Set the static Category Schema for all Quizzes.
     * Associate a Category name to appropriate integer ID in static global HashMap.
     * @param callback
     */
    public static void setCategorySchema(APICallback callback) {
        // Request JSON response using category URL
        getJSON(new APICallback() {
            @Override
            public void onCallback(JSONObject response) {
                try {
                    // Get JSONArray containing categories
                    JSONArray categories = response.getJSONArray("trivia_categories");
                    // Iterate through array and associate
                    for (int i = 0; i < categories.length(); i++) {
                        // Category object
                        JSONObject cat = categories.getJSONObject(i);
                        // ID
                        int id = cat.getInt("id");
                        // Name
                        String name = cat.getString("name");
                        // Associate id with name
                        Quiz.categorySchema.put(id, name);
                    }
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

    /**
     * Get and return quiz JSON data through APICallback interface.
     * Quiz JSON generated through API.
     * @param apiCallback
     * @param name
     * @param amount
     * @param category
     * @param difficulty
     * @param type
     * @param startDate
     * @param endDate
     */
    public static void generateNewQuiz(APICallback apiCallback, String name, String amount, String category, String difficulty, String type, String startDate, String endDate) {
        // Generate appropriate URL based on quiz details
        String quizURL = generateQuizAPIURL(amount, category, difficulty, type);
        // Retrieve and Return JSON through callback
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

    /**
     * Generic JSON request method.
     * Retrieves and returns JSON through callback.
     * @param callback
     * @param url
     */
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

    /**
     * Generates and returns appropriate API url based on Quiz details.
     * @param amount
     * @param category
     * @param difficulty
     * @param type
     * @return
     */
    public static String generateQuizAPIURL(String amount, String category, String difficulty, String type) {
        // Start with base url and successively append
        String url = BASE_QUIZ_URL;
        // Append amount of questions
        url += "amount=" + amount;

        // If specified category
        if (!category.equals("") && !category.equals("Any")) {
            // Retrieve category ID based on category name from global schema
            for (Map.Entry<Integer, String> entry : Quiz.categorySchema.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();
                if (value.equals(category)) {
                    // Append category to url
                    url += "&category=" + key;
                    break;
                }
            }
        }

        // If specified difficulty
        if (!difficulty.equals("") && !difficulty.equals("Any")) {
            // Append
            url += "&difficulty=" + difficulty;
        }

        // If specified type
        if(!type.equals("") && !type.equals("Any")) {
            // Append
            url += "&type=" + type;
        }

        return url;
    }
}

