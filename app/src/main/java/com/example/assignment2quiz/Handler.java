package com.example.assignment2quiz;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class Handler {

    private static final String USER_REF = "users";
    private static final String QUIZ_REF = "quizzes";
    private static User currentUser;
    private Handler() {}

    public static void start() {
        CloudDatabase.start();
    }

    public static void insertUser(Callback call, String username, String password) {
        getUser(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                System.out.println("Apparently this user exists");
                System.out.println(snap.toString());
                call.onFailure();
            }

            @Override
            public void onFailure() {
                String newID = CloudDatabase.getNewId();
                User newUser = new User(newID, username, password, false);
                CloudDatabase.insertData(getUsersRef().child(newID), newUser);
                call.onCallback(null);
            }
        }, username, password);
    }

    public static DatabaseReference getUsersRef() {
        return CloudDatabase.getRef(USER_REF);
    }

    public static void getUsers(Callback call) {
        CloudDatabase.readDataOnce(call, getUsersRef());
    }

    public static void getUser(String id, Callback call) {
        CloudDatabase.readDataOnce(call, getUsersRef().child(id));
    }

    public static void getUser(Callback call, String username, String password) {
        getUsers(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                Iterable<DataSnapshot> snaps = snap.getChildren();
                for (DataSnapshot userSnap: snaps) {
                    User user = userSnap.getValue(User.class);
                    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                        CloudDatabase.readDataOnce(call, getUsersRef().child(user.getId()));
                        return;
                    }
                }
                call.onFailure();
            }

            @Override
            public void onFailure() {
                Log.d("GetUser(username + password)", "Getting users failed");
            }
        });
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
