package com.example.assignment2quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Quiz {
    private String id;
    private String name;
    private String category;
    private String difficulty;
    private String type;
    private String startDate;
    private String endDate;
    private List<String> participants;
    private List<Question> questions;
    private int likes;

    public static HashMap<Integer, String> categorySchema = new HashMap<Integer, String>();
    public static final String[] difficulties = {"Any", "easy", "medium", "hard"};
    public static final String[] types = {"Any", "multiple", "boolean"};

    public Quiz() {}

    public Quiz(String id, String name, String category, String difficulty, String type, String startDate, String endDate, int likes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;

        this.startDate = startDate;
        this.endDate = endDate;
        this.likes = likes;

        participants = new ArrayList<>();
        questions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void insertParticipant(String userId) {
        participants.add(userId);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
