package com.example.assignment2quiz;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Static CloudDatabase interface of application
 */
public class CloudDatabase {
    private static FirebaseDatabase db;
    private static DatabaseReference root;
    // Static URL to database
    private static final String URL = "https://assignment2quiz-default-rtdb.asia-southeast1.firebasedatabase.app/";

    /**
     * Cannot instantiate CloudDatabase
     */
    private CloudDatabase() {}

    /**
     * Start CloudDatabase, set database and root variables
     */
    public static void start() {
        db = FirebaseDatabase.getInstance(URL);
        root = db.getReference();
    }

    /**
     * Read data once and return a snapshot of passed reference.
     * @param callback
     * @param ref
     */
    public static void readDataOnce(Callback callback, DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCallback(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });
    }

    /**
     * Insert data of a generic type at passed reference.
     * @param key
     * @param value
     * @param <T>
     */
    public static <T> void insertData(DatabaseReference key, T value) {key.setValue(value);}

    /**
     * Return a database reference from root of Database based on url string passed.
     * @param ref
     * @return
     */
    public static DatabaseReference getRef(String ref) {
        return db.getReference(ref);
    }

    /**
     * Return root refernce of database
     * @return
     */
    public static DatabaseReference getRoot() {
        return root;
    }

    /**
     * Returns a new valid ID sources from Firebase Database functionality.
     * @return
     */
    public static String getNewId() {
        return root.push().getKey();
    }
}
