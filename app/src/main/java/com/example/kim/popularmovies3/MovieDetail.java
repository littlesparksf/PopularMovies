package com.example.kim.popularmovies3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {
    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    android.os.Bundle data = getIntent().getExtras();
    MovieItem movieItem = (MovieItem) data.getParcelable("movie");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ImageView posterDetailView = findViewById(R.id.movie_detail_poster_view);

        populateUI();
        Picasso.with(this)
                .load(movieItem.getImage())
                .into(posterDetailView);
    }

    private void populateUI() {

        // find movie item extras and hook up with UI

        // Find the TextView with view ID movie_title_tv
        TextView movieDetailNameView = (TextView) findViewById(R.id.movie_title_tv);
        // Find name of current movie
        String movieItemTitle = movieItem.getTitle();
        // Display the name of the current movie in that TextView
        movieDetailNameView.setText(movieItemTitle);

        // Find the TextView with view ID movie_rating_tv
        TextView movieRatingView = (TextView) findViewById(R.id.movie_rating_tv);
        // Find name of current movie
        String movieItemRating = movieItem.getRating();
        // Display the rating of the current movie in that TextView
        movieRatingView.setText(movieItemRating);

        // Find the TextView with view ID movie_overview_tv
        TextView movieOverviewView = (TextView) findViewById(R.id.movie_overview_tv);
        // Find name of current movie
        String movieOverview = movieItem.getOverview();
        // Display the overview of the current movie in that TextView
        movieOverviewView.setText(movieOverview);

        // Find the TextView with view ID movie_date_tv
        TextView movieReleaseDateView = (TextView) findViewById(R.id.movie_date_tv);
        // Find release date of current movie
        String movieReleaseDate = movieItem.getReleaseDate();
        // Display the release date of the current movie in that TextView
        movieOverviewView.setText(movieReleaseDate);
    }

}
