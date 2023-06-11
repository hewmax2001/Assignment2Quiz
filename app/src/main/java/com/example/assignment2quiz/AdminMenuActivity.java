package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminMenuActivity extends AppCompatActivity {
    private Button btnAdd, btnViewQuizzes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        setElements();
    }

    private void setElements() {
        btnAdd = findViewById(R.id.btn_admin_add_quiz);
        btnViewQuizzes = findViewById(R.id.btn_admin_view_quiz);

        setEvents();
    }

    private void setEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAddQuiz();
            }
        });

        btnViewQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadViewQuizzes();
            }
        });
    }

    private void loadAddQuiz() {
        Intent addQuizIntent = new Intent(this, AddQuizActivity.class);
        startActivity(addQuizIntent);
    }

    private void loadViewQuizzes() {
        Intent viewQuizIntent = new Intent(this, AdminViewQuizActivity.class);
        startActivity(viewQuizIntent);
    }
}