package com.example.assignment2quiz;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Handler {

    private static final String USER_REF = "users";
    private static final String QUIZ_REF = "quizzes";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static User currentUser;
    private static String currentDate;
    private Handler() {}

    public static void start() {
        CloudDatabase.start();
        setCurrentDate();
    }

    private static void setCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        currentDate = dtf.format(LocalDateTime.now());
    }

    public static void getAllQuizzes(Callback callback) {
        CloudDatabase.readDataOnce(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                callback.onCallback(snap);
            }

            @Override
            public void onFailure() {

            }
        }, getQuizzesRef());
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

    public static void createQuiz(Callback call, String name, String amount, String category, String difficulty, String type, String startDate, String endDate) {
        getQuizByName(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                call.onFailure();
            }

            @Override
            public void onFailure() {
                TDBAPI.generateNewQuiz(new APICallback() {
                    @Override
                    public void onCallback(JSONObject response) {
                        try {
                            int responseCode = response.getInt("response_code");
                            if (responseCode != 0) {return;}
                            List<Question> questionsList = new ArrayList<>();
                            JSONArray questions = response.getJSONArray("results");
                            for (int i = 0; i < questions.length(); i++) {
                                JSONObject questionObject = questions.getJSONObject(i);
                                String question = questionObject.getString("question");
                                Question q = new Question(question);
                                String correctAnswer = questionObject.getString("correct_answer");
                                q.setCorrectAnswer(correctAnswer);
                                JSONArray incorrectAnswers = questionObject.getJSONArray("incorrect_answers");
                                String type = questionObject.getString("type");
                                if (type.equals("boolean")) {
                                    q.setAnswers(new ArrayList<String>(){
                                        {
                                            add("True");
                                            add("False");
                                        }
                                    });
                                }
                                else {
                                    List<String> answers = new ArrayList<>();
                                    for (int j = 0; j < incorrectAnswers.length(); j++) {
                                        answers.add(incorrectAnswers.getString(j));
                                    }
                                    int randomIndex = new Random().nextInt(3);
                                    answers.add(randomIndex, correctAnswer);
                                    q.setAnswers(answers);
                                }
                                questionsList.add(q);
                            }
                            String newID = CloudDatabase.getNewId();
                            Quiz newQuiz = new Quiz(newID, name, category, difficulty, type, startDate, endDate);
                            newQuiz.setQuestions(questionsList);

                            CloudDatabase.insertData(getQuizzesRef().child(newQuiz.getId()), newQuiz);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure() {

                    }
                }, name, amount, category, difficulty, type, startDate, endDate);
            }
        }, name);
    }

    public static void updateQuiz(Quiz quiz) {
        DatabaseReference quizRef = getQuizRef(quiz.getId());
        CloudDatabase.insertData(quizRef, quiz);
    }

    public static DatabaseReference getUsersRef() {
        return CloudDatabase.getRef(USER_REF);
    }

    private static DatabaseReference getUserRef(String id) {
        return getUsersRef().child(id);
    }

    public static void getUsers(Callback call) {
        CloudDatabase.readDataOnce(call, getUsersRef());
    }

    public static void getUser(String id, Callback call) {
        CloudDatabase.readDataOnce(call, getUsersRef().child(id));
    }

    private static DatabaseReference getQuizzesRef() {
        return CloudDatabase.getRef(QUIZ_REF);
    }

    private static DatabaseReference getQuizRef(String id) {
        return getQuizzesRef().child(id);
    }

    private static void getQuizzes(Callback call) {
        CloudDatabase.readDataOnce(call, getQuizzesRef());
    }

    public static void getQuiz(Callback call, String id) {
        CloudDatabase.readDataOnce(call, getQuizzesRef().child(id));
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

    private static void getQuizByName(Callback call, String name) {
        getQuizzes(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                Iterable<DataSnapshot> snaps = snap.getChildren();
                for (DataSnapshot quizSnap: snaps) {
                    Quiz quiz = quizSnap.getValue(Quiz.class);
                    if (quiz.getName().equals(name)) {
                        getQuiz(call, quiz.getId());
                        return;
                    }
                }
                call.onFailure();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String getCurrentDate() {
        return currentDate;
    }
}
