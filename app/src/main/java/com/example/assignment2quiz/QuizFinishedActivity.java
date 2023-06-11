package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizFinishedActivity extends AppCompatActivity {
    private TextView txtName, txtScore;
    private Button btnFinish;
    private QuizHandler quizHandler;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_finished);
        quizHandler = (QuizHandler) getIntent().getSerializableExtra("quizHandler");
        quiz = quizHandler.getQuiz();
        setElements();
    }

    private void setElements() {
        txtName = findViewById(R.id.txt_quiz_finish_name);
        txtScore = findViewById(R.id.txt_quiz_finish_score);

        btnFinish = findViewById(R.id.btn_quiz_finish_finish);

        setDetails();
        setButtonEvents();
    }

    private void setButtonEvents() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishQuiz();
            }
        });
    }

    private void setDetails() {
        String name = quiz.getName();
        int score = calculateScore();
        int numberOfQuestions = quiz.getQuestions().size();

        txtName.setText(name);
        txtScore.setText("Score: " + score + "/" + numberOfQuestions);
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < quizHandler.getAnswers().size(); i++) {
            String answer = quizHandler.getAnswers().get(i);
            Question question = quiz.getQuestions().get(i);
            String correctAnswer = question.getCorrectAnswer();

            if (correctAnswer.equals(answer))
                score++;
        }
        return score;
    }

    private void finishQuiz() {
        String userID = Handler.getCurrentUser().getId();
        quiz.insertParticipant(userID);
        Handler.updateQuiz(quiz);
        finish();
    }
}