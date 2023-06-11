package com.example.assignment2quiz;

import com.google.firebase.database.snapshot.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz handler to handle the presentation and player input to a quiz.
 */
public class QuizHandler implements Serializable {
    private Quiz quiz;
    // Question index
    private int currentIndex;
    // List of player's answers
    private List<String> answers;

    /**
     * Instantiate a new QuizHandler object and fill answers list with blank Strings
     * @param quiz
     */
    public QuizHandler(Quiz quiz)  {
        this.quiz = quiz;
        currentIndex = 0;
        answers = new ArrayList<>();
        fillWithBlankAnswers();
    }

    /**
     * Fill answers list with empty strings.
     * Fills list based on how many questions are in quiz.
     */
    private void fillWithBlankAnswers() {
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            answers.add("");
        }
    }

    /**
     * Return question object based on currentIndex
     * @return
     */
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

    /**
     * Get current answer of question at index.
     * @return
     */
    public String getCurrentAnswer() {
        try {
            return answers.get(currentIndex);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
}
