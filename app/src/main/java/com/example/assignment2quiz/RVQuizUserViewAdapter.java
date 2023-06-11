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
    private List<RVHolderUserView> holderList;
    private static final String defaultColor = "#e3c378";
    private static final String selectedColor = "#5691e3";

    public RVQuizUserViewAdapter(Consumer eventConsumer, ArrayList<Quiz> quizzes) {
        this.eventConsumer = eventConsumer;
        this.quizzes = quizzes;
        holderList = new ArrayList<>();
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
        holderList.add(holder);
        holder.setDetails(quizzes.get(position));
        holder.setEvents(new Consumer<Quiz>() {
            @Override
            public void accept(Quiz quiz) {
                for (RVHolderUserView h: holderList) {
                    h.setColor(defaultColor);
                }
                if (Handler.getCurrentUser().isAdmin()) {
                    holder.setColor(selectedColor);
                }
                eventConsumer.accept(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }
}
