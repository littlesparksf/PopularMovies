package com.example.kim.popularmovies3;

public class ReviewListItem {
    private int reviewId;
    private String reviewAuthor;
    private String reviewText;
    private String reviewUrl;

    /**
     * No args constructor for use in serialization
     */
    public ReviewListItem() {
    }

    public ReviewListItem(String reviewAuthor, String reviewText, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewText = reviewText;
        this.reviewUrl = reviewUrl;
    }

    public ReviewListItem(int reviewId, String reviewAuthor, String reviewText, String reviewUrl) {
        this.reviewId = reviewId;
        this.reviewAuthor = reviewAuthor;
        this.reviewText = reviewText;
        this.reviewUrl = reviewUrl;
    }

    public int getReviewId() { return reviewId; }

    public void setReviewId(int id) { this.reviewId = id; }


    public String getAuthor() {
        return reviewAuthor;
    }

    public void setAuthor(String author) {
        this.reviewAuthor = author;
    }


    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }


    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setTitle(String reviewUrl) { this.reviewUrl = reviewUrl; }
}

