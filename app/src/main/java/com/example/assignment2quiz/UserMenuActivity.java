package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class UserMenuActivity extends AppCompatActivity {
    private Button btnOngoing, btnParticipated, btnUpcoming, btnPast;
    private RecyclerView recQuizView;
    private static List<Quiz> allQuizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
        Handler.start();
        allQuizzes = new ArrayList<>();
        setElements();
    }

    protected void onResume() {
        super.onResume();
        getQuizzes();
    }

    private void getQuizzes() {
        allQuizzes = new ArrayList<>();
        Handler.getAllQuizzes(new Callback() {
            @Override
            public void onCallback(DataSnapshot snap) {
                Iterable<DataSnapshot> snaps = snap.getChildren();
                for (DataSnapshot quizSnap : snaps) {
                    Quiz quiz = quizSnap.getValue(Quiz.class);
                    allQuizzes.add(quiz);
                }
                viewOngoing();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void setElements() {
        btnOngoing = findViewById(R.id.btn_view_ongoing);
        btnParticipated = findViewById(R.id.btn_view_participated);
        btnUpcoming = findViewById(R.id.btn_view_upcoming);
        btnPast = findViewById(R.id.btn_view_past);

        recQuizView = findViewById(R.id.rec_user_quiz_view);

        setButtonEvents();
    }

    private void setButtonEvents() {
        btnOngoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOngoing();
            }
        });

        btnParticipated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewParticipated();
            }
        });

        btnUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUpcoming();
            }
        });

        btnPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPast();
            }
        });
    }

    private void viewOngoing() {
        if (Handler.getCurrentUser() == null) {return;}
        clearRecycler();
        ArrayList<Quiz> ongoingQuizzes = new ArrayList<>();
        for (Quiz quiz : allQuizzes) {
            if (compareDates(Handler.getCurrentDate(), quiz.getStartDate()) || compareDates(quiz.getEndDate(), Handler.getCurrentDate())) {
                continue;
            }

            String userID = Handler.getCurrentUser().getId();
            if (quiz.checkParticipant(userID))
                continue;

            ongoingQuizzes.add(quiz);
        }

        RVQuizUserViewAdapter adapter = new RVQuizUserViewAdapter(new Consumer() {
            @Override
            public void accept(Object o) {
                Quiz quiz = (Quiz) o;
                Toast.makeText(getApplicationContext(), quiz.getName(), Toast.LENGTH_SHORT).show();
                QuizHandler quizHandler = new QuizHandler(quiz);
                Intent quizIntent = new Intent(getApplicationContext(), QuestionActivity.class);
                quizIntent.putExtra("quizHandler", quizHandler);
                startActivity(quizIntent);
            }
        }, ongoingQuizzes);
        recQuizView.setAdapter(adapter);
    }

    private void viewParticipated() {
        if (Handler.getCurrentUser() == null) {return;}
        clearRecycler();
        ArrayList<Quiz> participatedQuizzes = new ArrayList<>();
        for (Quiz quiz : allQuizzes) {
            String userID = Handler.getCurrentUser().getId();
            if (quiz.checkParticipant(userID))
                participatedQuizzes.add(quiz);
        }

        RVQuizUserViewAdapter adapter = new RVQuizUserViewAdapter(new Consumer() {
            @Override
            public void accept(Object o) {
                Quiz quiz = (Quiz) o;
                String userID = Handler.getCurrentUser().getId();
                if (quiz.checkParticipantLiked(userID))
                    quiz.getLikes().remove(userID);
                else
                    quiz.insertParticipantLiked(userID);
                Handler.updateQuiz(quiz);
            }
        }, participatedQuizzes);
        recQuizView.setAdapter(adapter);
    }

    private void viewUpcoming() {
        if (Handler.getCurrentUser() == null) {return;}
        clearRecycler();
        ArrayList<Quiz> upcomingQuizzes = new ArrayList<>();
        for (Quiz quiz : allQuizzes) {
            if (compareDates(Handler.getCurrentDate(), quiz.getStartDate()))
                upcomingQuizzes.add(quiz);
        }

        RVQuizUserViewAdapter adapter = new RVQuizUserViewAdapter(new Consumer() {
            @Override
            public void accept(Object o) {
                // Nothing needs to happen
            }
        }, upcomingQuizzes);
        recQuizView.setAdapter(adapter);
    }

    private void viewPast() {
        if (Handler.getCurrentUser() == null) {return;}
        clearRecycler();
        ArrayList<Quiz> pastQuizzes = new ArrayList<>();
        for (Quiz quiz : allQuizzes) {
            if (compareDates(quiz.getEndDate(), Handler.getCurrentDate()))
                pastQuizzes.add(quiz);
        }

        RVQuizUserViewAdapter adapter = new RVQuizUserViewAdapter(new Consumer() {
            @Override
            public void accept(Object o) {
                // Nothing needs to happen
            }
        }, pastQuizzes);
        recQuizView.setAdapter(adapter);
    }

    /**
     * Compares two dates;
     * Returns true if date1 is further in the future than date2
     * @param date1
     * @param date2
     * @return
     */
    private boolean compareDates(String date1, String date2) {
        SimpleDateFormat formatter = new SimpleDateFormat(Handler.DATE_FORMAT);
        try {
            Date start = formatter.parse(date1);
            Date end = formatter.parse(date2);
            if (start.getTime() < end.getTime())
                return true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void clearRecycler() {
        recQuizView.removeAllViewsInLayout();
        recQuizView.setLayoutManager(new LinearLayoutManager(this));
    }
}