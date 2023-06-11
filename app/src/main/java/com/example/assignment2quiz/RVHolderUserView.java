package com.example.assignment2quiz;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

public class RVHolderUserView extends RecyclerView.ViewHolder {
    private View itemView;
    private CardView cardView;
    private TextView txtID, txtName, txtCat, txtDif, txtType, txtDateRange, txtLikes;
    private Quiz quiz;

    public RVHolderUserView(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        setElements();
    }

    private void setElements() {
        cardView = itemView.findViewById(R.id.car_quiz);

        txtID = itemView.findViewById(R.id.txt_quiz_id);
        txtName = itemView.findViewById(R.id.txt_quiz_name);
        txtCat = itemView.findViewById(R.id.txt_quiz_category);
        txtDif = itemView.findViewById(R.id.txt_quiz_difficulty);
        txtType = itemView.findViewById(R.id.txt_quiz_type);
        txtDateRange = itemView.findViewById(R.id.txt_quiz_date_range);
        txtLikes = itemView.findViewById(R.id.txt_quiz_likes);
    }

    public void setDetails(Quiz quiz) {
        txtID.setText("ID: " + quiz.getId());
        txtName.setText("Name: " + quiz.getName());
        txtCat.setText("Category: " + quiz.getCategory());
        txtDif.setText("Difficulty: " + quiz.getDifficulty());
        txtType.setText("Type: " + quiz.getType());
        txtDateRange.setText("Date Range: " + quiz.getStartDate() + " to " + quiz.getEndDate());
        txtLikes.setText("Likes: " + quiz.getLikes().size());

        this.quiz = quiz;
    }

    public void setEvents(Consumer<Quiz> consumer) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consumer.accept(quiz);
                setDetails(quiz);
                if (Handler.getCurrentUser().isAdmin()) {

                }
            }
        });
    }

    public void setColor(String color) {
        int selectedColor = Color.parseColor(color);
        cardView.setCardBackgroundColor(selectedColor);
    }

}
