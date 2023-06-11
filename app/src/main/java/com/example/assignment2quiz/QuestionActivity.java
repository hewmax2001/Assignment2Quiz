package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    private TextView txtPrompt, txtQuestionNumber;
    private Button btnNext, btnPrev;
    private AutoCompleteTextView autAnswer;
    private QuizHandler quizHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        quizHandler = (QuizHandler) getIntent().getSerializableExtra("quizHandler");
        setElements();
    }

    protected void onResume() {
        super.onResume();
        setUpQuestion();
    }

    private void setElements() {
        txtPrompt = findViewById(R.id.txt_question_prompt);
        txtQuestionNumber = findViewById(R.id.txt_question_number);

        btnNext = findViewById(R.id.btn_question_next);
        btnPrev = findViewById(R.id.btn_question_prev);

        autAnswer = findViewById(R.id.aut_answer);

        setButtonEvents();
    }

    private void setButtonEvents() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validation()) {
                    Toast.makeText(getApplicationContext(), "Question must have an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkEndOfQuiz()) {
                    endQuiz();
                    return;
                }
                quizHandler.setCurrentIndex(quizHandler.getCurrentIndex() + 1);
                setUpQuestion();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizHandler.setCurrentIndex(quizHandler.getCurrentIndex() - 1);
                setUpQuestion();
            }
        });

        autAnswer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String answer = parent.getItemAtPosition(position).toString();
                quizHandler.setCurrentAnswer(answer);
            }
        });
    }

    private void setUpQuestion() {
        if (quizHandler.getCurrentIndex() < 1)
            btnPrev.setEnabled(false);
        else
            btnPrev.setEnabled(true);

        if (checkEndOfQuiz())
            btnNext.setText("Submit");
        else
            btnNext.setText("Next");

        Question question = quizHandler.getCurrentQuestion();
        String questionPrompt = question.getQuestion();

        txtPrompt.setText(questionPrompt);
        txtQuestionNumber.setText("Question: " + (quizHandler.getCurrentIndex() + 1));
        setAnswerBox();
    }

    private void setAnswerBox() {
        autAnswer.clearListSelection();
        String currentAnswer = quizHandler.getCurrentAnswer();
        autAnswer.setText(currentAnswer);
        List<String> answers = quizHandler.getCurrentQuestion().getAnswers();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.answer_dropdown_item, answers);
        autAnswer.setAdapter(adapter);
    }

    private boolean validation() {
        return !autAnswer.getText().toString().equals("");
    }

    private boolean checkEndOfQuiz() {
        return quizHandler.getCurrentIndex() >= quizHandler.getQuiz().getQuestions().size() - 1;
    }

    private void endQuiz() {
        Intent endQuizIntent = new Intent(this, QuizFinishedActivity.class);
        endQuizIntent.putExtra("quizHandler", quizHandler);
        startActivity(endQuizIntent);
        finish();
    }
}