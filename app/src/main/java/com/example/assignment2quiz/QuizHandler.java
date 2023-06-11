package com.example.assignment2quiz;

import com.google.firebase.database.snapshot.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuizHandler implements Serializable {
    private Quiz quiz;
    private int currentIndex;
    private List<String> answers;

    public QuizHandler(Quiz quiz)  {
        this.quiz = quiz;
        currentIndex = 0;
        answers = new ArrayList<>();
        fillWithBlankAnswers();
    }

    private void fillWithBlankAnswers() {
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            answers.add("");
        }
    }

    public Question getCurrentQuestion() {
        try {
            return quiz.getQuestions().get(currentIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentQuestion) {
        this.currentIndex = currentQuestion;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void setCurrentAnswer(String answer) {
        answers.set(currentIndex, answer);
    }

    public String getCurrentAnswer() {
        try {
            return answers.get(currentIndex);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
}
