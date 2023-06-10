package com.example.assignment2quiz;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CloudDatabase {
    private static FirebaseDatabase db;
    private static DatabaseReference root;
    private static final String URL = "https://assignment2quiz-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private CloudDatabase() {}

    public static void start() {
        db = FirebaseDatabase.getInstance(URL);
        root = db.getReference();
    }

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

    public static <T> void insertData(DatabaseReference key, T value) {key.setValue(value);}

    public static DatabaseReference getRef(String ref) {
        return db.getReference(ref);
    }

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
