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

public class AdminViewQuizActivity extends AppCompatActivity {
    private Button btnOngoing, btnUpcoming, btnPast, btnDelete;
    private RecyclerView recQuizView;
    private List<Quiz> allQuizzes;
    private Quiz selectedQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_quiz);
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
        btnUpcoming = findViewById(R.id.btn_view_upcoming);
        btnPast = findViewById(R.id.btn_view_past);
        btnDelete = findViewById(R.id.btn_admin_quiz_delete);

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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedQuiz == null) {
                    Toast.makeText(getApplicationContext(), "No quiz selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                deleteQuiz();
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
                selectedQuiz = (Quiz) o;
            }
        }, ongoingQuizzes);
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
                selectedQuiz = (Quiz) o;
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
                selectedQuiz = (Quiz) o;
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

    private void deleteQuiz() {
        if (selectedQuiz == null) {return;}
        Handler.deleteQuiz(selectedQuiz);
        getQuizzes();
        viewOngoing();
    }
}