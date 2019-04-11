package com.example.kim.popularmovies3;

public class TrailerListItem {
    private int trailerId;
    private String trailerTitle;
    private String trailerUrl;

    /**
     * No args constructor for use in serialization
     */
    public TrailerListItem() {
    }

    public TrailerListItem(String reviewAuthor, String reviewText, String reviewUrl) {
        this.trailerTitle = trailerTitle;
        this.trailerUrl = trailerUrl;
    }

    public TrailerListItem(int reviewId, String reviewAuthor, String reviewText, String reviewUrl) {
        this.trailerId = trailerId;
        this.trailerTitle = trailerTitle;
        this.trailerUrl = trailerUrl;
    }

    public int getTrailerId() { return trailerId; }

    public void setTrailerId(int id) { this.trailerId = id; }


    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String author) {
        this.trailerTitle = author;
    }


    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String reviewText) {
        this.trailerUrl = reviewText;
    }

}