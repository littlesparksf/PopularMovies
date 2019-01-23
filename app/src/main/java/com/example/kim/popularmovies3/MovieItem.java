package com.example.kim.popularmovies3;

import android.os.Parcel;
import android.os.Parcelable;


public class MovieItem implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    private String title;
    private String image;
    private String overview;

    /**
     * No args constructor for use in serialization
     */
    public MovieItem() {}

    // Constructor
    public MovieItem(String title, String image, String overview) {
        this.title = title;
        this.image = image;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    // Parcelling part
    public MovieItem(Parcel in){
        this.title = in.readString();
        this.image =  in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image);
    }

//    @Override
//    public String toString() {
//        return "Movie{" +
//                "Title='" + title + '\'' +
//                ", Poster='" + image + '\'' +
//                '}';
//    }
}