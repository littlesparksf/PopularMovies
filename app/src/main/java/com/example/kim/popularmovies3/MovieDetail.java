package com.example.kim.popularmovies3;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kim.popularmovies3.database.AppDatabase;
import com.squareup.picasso.Picasso;



public class MovieDetail extends AppCompatActivity {
//    public static final String EXTRA_POSITION = "extra_position";
//    private static final int DEFAULT_POSITION = -1;
    public static final int DEFAULT_FAVORITE_ID = -1;

    private AppDatabase mDb;
    MovieItem movieItem = null;
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    public CheckBox mFavoriteCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ImageView posterDetailView = findViewById(R.id.movie_detail_poster_view);

        android.os.Bundle data = getIntent().getExtras();
        movieItem = data.getParcelable("movie");

        // Get poster path
        String posterPath = movieItem.getImage();
        Log.v(LOG_TAG, "Poster path: " + posterPath);

        // Build poster url
        String posterUrl = "https://image.tmdb.org/t/p/" + "w185" + posterPath;
        // Log poster url
        Log.v(LOG_TAG, "Poster Url: " + posterUrl);

        populateUI();
        Picasso.with(this)
                .load(posterUrl)
                .into(posterDetailView);

        mFavoriteCheckbox = findViewById(R.id.add_favorite_checkbox);
        mFavoriteCheckbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onFavoriteChecked();
            }
        });

        // Need to trigger api calls for reviews and trailers, then set up json utils for them.
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
        movieReleaseDateView.setText(movieReleaseDate);
    }

// Checkbox when clicked adds the corresponding movie to the favorite movie list
    public void onFavoriteChecked() {

        // Need to get title and overview of movie associated with favorite checkbox
        final int favoriteId = movieItem.getId();
        String favoriteTitle = movieItem.getTitle();
        String favoriteReleaseDate = movieItem.getReleaseDate();
        String favoriteImage = movieItem.getImage();
        String favoriteRating = movieItem.getRating();
        String favoriteOverview = movieItem.getOverview();

        final MovieItem favorite = new MovieItem(favoriteId, favoriteTitle, favoriteReleaseDate, favoriteImage, favoriteRating, favoriteOverview);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (favoriteId == DEFAULT_FAVORITE_ID) {
                    // if favorite is new, insert new favorite
                    mDb.favoriteDao().insertTask(favorite);
                } else {
                    // if the favorite is already in the list, re-insert in case of update
                    // What if favorite is changed from checked to unchecked? Need to remove it.
                    favorite.setId(favoriteId);
                    mDb.favoriteDao().updateFavorite(favorite);
                }
                finish();
            }
        });
    }
}


