package com.example.assignment2quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RVQuizUserViewAdapter extends RecyclerView.Adapter<RVHolderUserView> {
    private List<Quiz> quizzes;
    private Consumer eventConsumer;

    public RVQuizUserViewAdapter(Consumer eventConsumer, ArrayList<Quiz> quizzes) {
        this.eventConsumer = eventConsumer;
        this.quizzes = quizzes;
    }
    @NonNull
    @Override
    public RVHolderUserView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        View view = lf.inflate(R.layout.quiz_item,parent,false);
        return new RVHolderUserView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderUserView holder, int position) {
        holder.setDetails(quizzes.get(position));
        holder.setEvents(eventConsumer);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }
}
