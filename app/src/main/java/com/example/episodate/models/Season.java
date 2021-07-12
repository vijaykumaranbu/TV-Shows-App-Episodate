package com.example.episodate.models;

public class Season {

    private String imageUrl;
    private String title;
    private String startDate;

    public Season(String imageUrl, String title, String startDate) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.startDate = startDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }
}
