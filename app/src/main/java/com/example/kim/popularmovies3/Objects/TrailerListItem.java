package com.example.kim.popularmovies3.Objects;

public class TrailerListItem {
    private int trailerId;
    private String trailerTitle;
    private String trailerUrlKey;

    /**
     * No args constructor for use in serialization
     */
    public TrailerListItem() {
    }

    public TrailerListItem(String trailerTitle, String trailerUrlKey) {
        this.trailerTitle = trailerTitle;
        this.trailerUrlKey = trailerUrlKey;
    }

    public TrailerListItem(int trailerId, String trailerTitle, String trailerUrlKey) {
        this.trailerId = trailerId;
        this.trailerTitle = trailerTitle;
        this.trailerUrlKey = trailerUrlKey;
    }

    public int getTrailerId() { return trailerId; }

    public void setTrailerId(int id) { this.trailerId = id; }


    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }


    public String getTrailerUrlKey() {
        return trailerUrlKey;
    }

    public void setTrailerUrlKey(String trailerUrlKey) {
        this.trailerUrlKey = trailerUrlKey;
    }

}