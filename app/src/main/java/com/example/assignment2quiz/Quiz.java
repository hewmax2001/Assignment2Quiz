package com.example.assignment2quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Quiz {
    private String id;
    private String name;
    private String category;
    private String startDate;
    private String endDate;
    private List<String> participants;
    private int likes;

    public static HashMap<Integer, String> categorySchema = new HashMap<Integer, String>();

    public Quiz() {}

    public Quiz(String id, String name, String category, String startDate, String endDate, int likes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.likes = likes;

        participants = new ArrayList<>();
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
}
