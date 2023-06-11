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

/**
 * Static Handler for the application
 */
public class Handler {

    private static final String USER_REF = "users";
    private static final String QUIZ_REF = "quizzes";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    // Current User of application
    private static User currentUser;
    // Today's date
    private static String currentDate;

    /**
     * Handler cannot be instantiated
     */
    private Handler() {}

    /**
     * Start the Handler and CloudDatabase
     */
    public static void start() {
        CloudDatabase.start();
        setCurrentDate();
    }

    /**
     * Set the current date to today's date
     */
    private static void setCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        currentDate = dtf.format(LocalDateTime.now());
    }

    /**
     * Return a DatasnapShot of all Quizzes in the database.
     * @param callback
     */
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

    /**
     * Create and insert a new user with username and password.
     * @param call
     * @param username
     * @param password
     */
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

    /**
     * Create and insert a new Quiz with parameters.
     * @param call
     * @param name
     * @param amount
     * @param category
     * @param difficulty
     * @param type
     * @param startDate
     * @param endDate
     */
    public static void createQuiz(Callback call, String name, String amount, String category, String difficulty, String type, String startDate, String endDate) {
        // Check if quiz with the same name already exists
        getQuizByName(new Callback() {
            // Failure of quiz creation
            // Implies quiz with the same name exists
            @Override
            public void onCallback(DataSnapshot snap) {
                call.onFailure();
            }

            // Implies quiz does not exist in database
            @Override
            public void onFailure() {
                // Generate a new quiz from the OpenTDB API
                TDBAPI.generateNewQuiz(new APICallback() {
                    // Successful API response
                    @Override
                    public void onCallback(JSONObject response) {
                        try {
                            // Response code of 0 implies success
                            int responseCode = response.getInt("response_code");
                            if (responseCode != 0) {return;}

                            // Create new list of questions
                            List<Question> questionsList = new ArrayList<>();

                            // Get question JSONArray
                            JSONArray questions = response.getJSONArray("results");
                            // For each JSON key in array
                            for (int i = 0; i < questions.length(); i++) {
                                // Retrieve JSON object from key index
                                JSONObject questionObject = questions.getJSONObject(i);
                                // Retrieve question prompt and instantiate a new question
                                String question = questionObject.getString("question");
                                Question q = new Question(question);
                                // Get and set correct Answer
                                String correctAnswer = questionObject.getString("correct_answer");
                                q.setCorrectAnswer(correctAnswer);
                                // Get and set incorrect answer based on type
                                JSONArray incorrectAnswers = questionObject.getJSONArray("incorrect_answers");
                                String type = questionObject.getString("type");
                                // "boolean" = True and False
                                if (type.equals("boolean")) {
                                    // Only two possible answers
                                    q.setAnswers(new ArrayList<String>(){
                                        {
                                            add("True");
                                            add("False");
                                        }
                                    });
                                }
                                // Multiple choice
                                else {
                                    // Create answers array
                                    List<String> answers = new ArrayList<>();
                                    // Insert incorrect answers to answers list
                                    for (int j = 0; j < incorrectAnswers.length(); j++) {
                                        answers.add(incorrectAnswers.getString(j));
                                    }
                                    // Insert correct answer at a random index
                                    int randomIndex = new Random().nextInt(3);
                                    answers.add(randomIndex, correctAnswer);
                                    // Set the answers for this question
                                    q.setAnswers(answers);
                                }
                                // Add the question to the list
                                questionsList.add(q);
                            }
                            // Create new quiz object with unique ID
                            String newID = CloudDatabase.getNewId();
                            Quiz newQuiz = new Quiz(newID, name, category, difficulty, type, startDate, endDate);
                            // Set questions list
                            newQuiz.setQuestions(questionsList);

                            // Insert quiz object into database
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

    /**
     * Update quiz with object based on ID.
     * @param quiz
     */
    public static void updateQuiz(Quiz quiz) {
        DatabaseReference quizRef = getQuizRef(quiz.getId());
        CloudDatabase.insertData(quizRef, quiz);
    }

    /**
     * Delete Quiz based on object ID.
     * @param quiz
     */
    public static void deleteQuiz(Quiz quiz) {
        DatabaseReference quizRef = getQuizRef(quiz.getId());
        CloudDatabase.insertData(quizRef, null);
    }

    /**
     * Get references to users list.
     * @return
     */
    public static DatabaseReference getUsersRef() {
        return CloudDatabase.getRef(USER_REF);
    }

    /**
     * Get reference to a specific user based on id.
     * @param id
     * @return
     */
    private static DatabaseReference getUserRef(String id) {
        return getUsersRef().child(id);
    }

    /**
     * Return a DataSnapshot containing all users of the database
     * @param call
     */
    public static void getUsers(Callback call) {
        CloudDatabase.readDataOnce(call, getUsersRef());
    }

    /**
     * Return a DataSnapshot contain a user based on id
     * @param id
     * @param call
     */
    public static void getUser(String id, Callback call) {
        CloudDatabase.readDataOnce(call, getUsersRef().child(id));
    }

    /**
     * Get reference to quizzes list.
     * @return
     */
    private static DatabaseReference getQuizzesRef() {
        return CloudDatabase.getRef(QUIZ_REF);
    }

    /**
     * Get reference to specific quiz based on id.
     * @param id
     * @return
     */
    private static DatabaseReference getQuizRef(String id) {
        return getQuizzesRef().child(id);
    }

    /**
     * Returns a DataSnapshot containing all Quizzes of the database
     * @param call
     */
    private static void getQuizzes(Callback call) {
        CloudDatabase.readDataOnce(call, getQuizzesRef());
    }

    /**
     * Return a DataSnapshot containing a quiz based on id.
     * @param call
     * @param id
     */
    public static void getQuiz(Callback call, String id) {
        CloudDatabase.readDataOnce(call, getQuizzesRef().child(id));
    }

    /**
     * Return a DataSnapshot containing a user based on username and password.
     * @param call
     * @param username
     * @param password
     */
    public static void getUser(Callback call, String username, String password) {
        // Get all users and find user with username and password
        getUsers(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                // Iterate through all users
                Iterable<DataSnapshot> snaps = snap.getChildren();
                for (DataSnapshot userSnap: snaps) {
                    User user = userSnap.getValue(User.class);
                    // If username and password are the same as parameters
                    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                        // Return DataSnapshot based on found user id
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

    /**
     * Return a DataSnapshot containing a quiz based on name.
     * @param call
     * @param name
     */
    private static void getQuizByName(Callback call, String name) {
        // Get all quizzes, iterate till found quiz with same name
        getQuizzes(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                // Iterate through all quizzes from database
                Iterable<DataSnapshot> snaps = snap.getChildren();
                for (DataSnapshot quizSnap: snaps) {
                    Quiz quiz = quizSnap.getValue(Quiz.class);
                    // Quiz has same name as parameter
                    if (quiz.getName().equals(name)) {
                        // Return DataSnapshot of quiz with same name based on id
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

    /**
     * Get current user of application.
     * @return
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set current user of application.
     * @param user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get today's date.
     * @return
     */
    public static String getCurrentDate() {
        return currentDate;
    }
}
