package com.example.kim.popularmovies3;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kim.popularmovies3.database.AppDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieDetail extends AppCompatActivity {

    // Recycler view variables
    private RecyclerView mReviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;
    private TextView mReviewsEmptyView;
    private ProgressBar mReviewsLoadingIndicator;

    // Trailer view variables
    private RecyclerView mTrailersRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private TextView mTrailersEmptyView;
    private ProgressBar mTrailersLoadingIndicator;

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

        /**Dealing with favorite database here */

        mDb = AppDatabase.getInstance(getApplicationContext());

        checkIfFav(movieItem.getTitle());

        mFavoriteCheckbox = findViewById(R.id.add_favorite_checkbox);
        mFavoriteCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavoriteChecked();
            }
        });


        // Set up Reviews Recycler View

        //  Get a reference to the RecyclerView
        mReviewsRecyclerView = findViewById(R.id.reviews_recycler_view);

        Log.v(LOG_TAG, "Reviews recycler view found.");

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mReviewsEmptyView = (TextView) findViewById(R.id.reviews_empty_view);

        // Loading indicator will be shown as data loads
        mReviewsLoadingIndicator = (ProgressBar) findViewById(R.id.reviews_loading_indicator);

        // Set a LinearLayoutManager
        LinearLayoutManager reviewsLinearLayoutManager = new LinearLayoutManager(getApplicationContext());

        // Set the Layout Manager to the RecyclerView
        mReviewsRecyclerView.setLayoutManager(reviewsLinearLayoutManager);

        // Setting to improve performance if changes in content do not change in child layout size
        mReviewsRecyclerView.setHasFixedSize(true);

        // Call the constructor of CustomAdapter to send the reference and data to the Adapter
        mReviewsAdapter = new ReviewsAdapter(MovieDetail.this, new ArrayList<ReviewListItem>());

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        Log.v(LOG_TAG, "Adapter set on recycler view.");

        /* Once all of our views are setup, we can load the reviews data. */
        loadReviews();

        Log.v(LOG_TAG, "loadReviewscalled.");

        /* Set up Trailers Recycler View */

        /* Get a reference to the RecyclerView */
        mTrailersRecyclerView = findViewById(R.id.trailers_recycler_view);

        Log.v(LOG_TAG, "Recycler view found.");

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mTrailersEmptyView = (TextView) findViewById(R.id.trailers_empty_view);

        // Loading indicator will be shown as data loads
        mTrailersLoadingIndicator = (ProgressBar) findViewById(R.id.trailers_loading_indicator);

        // Set a LinearLayoutManager for trailers
        LinearLayoutManager trailersLinearLayoutManager = new LinearLayoutManager(getApplicationContext());

        // Set the Layout Manager to the RecyclerView
        mTrailersRecyclerView.setLayoutManager(trailersLinearLayoutManager);

        // Setting to improve performance if changes in content do not change in child layout size
        mTrailersRecyclerView.setHasFixedSize(true);

        // Call the constructor of CustomAdapter to send the reference and data to the Adapter
        mTrailersAdapter = new TrailersAdapter(MovieDetail.this, new ArrayList<TrailerListItem>());

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);

        Log.v(LOG_TAG, "Adapter set on  trailer recycler view.");

        /* Once all of our views are setup, we can load the trailer data. */
        loadTrailers();

        Log.v(LOG_TAG, "loadTrailers called.");
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

    // Check to see if movie is in the database
    private void checkIfFav(final String title){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override public void run() {
                final MovieItem movie = mDb.favoriteDao().findMovieById(title);
                runOnUiThread(new Runnable() { @Override public void run() {
                    if(movie == null){ Log.d("!!!!!!!!", "is null");
                    mFavoriteCheckbox.setChecked(false);
                    }else {
                        Log.d("!!!!!!!!", "is not null");
                        mFavoriteCheckbox.setChecked(true); }
                }});
            } });
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
                if (mFavoriteCheckbox.isChecked()) {
                    // if favorite is new, insert new favorite
                    mDb.favoriteDao().insertFavorite(favorite);
                    Log.v(LOG_TAG, "insertFavorite called");
                } else {
                    // if the favorite is already in the list, remove when checkbox is clicked
                    mDb.favoriteDao().deleteByMovieTitle(favorite.getTitle());
                    Log.v(LOG_TAG, "deleteFavorite called.");
                }
            }
        });
    }

    /* Fetching review data */

    private void loadReviews() {
        showReviewsDataView();
        Log.v(LOG_TAG, "showReviewsDataView called.");

        new FetchReviewsTask().execute();

        Log.v(LOG_TAG, "FetchReviewsTask called.");
    }

    private void showReviewsDataView() {
        mReviewsEmptyView.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showReviewsErrorMessage() {
        /* First, hide the currently visible data */
        mReviewsRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mReviewsEmptyView.setVisibility(View.VISIBLE);
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, List<ReviewListItem>> {

        @Override
        protected void onPreExecute() {
            Log.v(LOG_TAG, "onPreExecute called in FetchReviewsTask.");
            super.onPreExecute();
            mReviewsLoadingIndicator.setVisibility(View.VISIBLE);
            Log.v(LOG_TAG, "setVisibility called on reviews loading indicator.");
        }

        @Override
        protected List<ReviewListItem> doInBackground(String... params) {

            try {
                int movieId = movieItem.getId();
                String movieIdString = Integer.toString(movieId);
                Log.v(LOG_TAG, "movieId toString called: " + movieId);
                List<ReviewListItem> reviewListItems = ReviewsJsonUtils.fetchReviews(movieIdString);
                Log.v(LOG_TAG, "fetchReviews called.");
                return reviewListItems;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ReviewListItem> reviewData) {
            mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
            if (reviewData != null) {
                showReviewsDataView();
                mReviewsAdapter.setReviews(reviewData);
            } else {
                showReviewsErrorMessage();
            }
        }
    }

    /* Fetching trailer data */

    private void loadTrailers() {
        showTrailersDataView();
        Log.v(LOG_TAG, "showTrailersDataView called.");

        new FetchTrailersTask().execute();
        Log.v(LOG_TAG, "FetchTrailersTask called.");
    }

    private void showTrailersDataView() {
        mTrailersEmptyView.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mTrailersRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showTrailersErrorMessage() {
        /* First, hide the currently visible data */
        mTrailersRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mTrailersEmptyView.setVisibility(View.VISIBLE);
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, List<TrailerListItem>> {

        @Override
        protected void onPreExecute() {
            Log.v(LOG_TAG, "onPreExecute called in FetchReviewsTask.");
            super.onPreExecute();
            mTrailersLoadingIndicator.setVisibility(View.VISIBLE);
            Log.v(LOG_TAG, "setVisibility called on trailers loading indicator.");
        }

        @Override
        protected List<TrailerListItem> doInBackground(String... params) {

            try {
                List<TrailerListItem> trailerListItems = TrailersJsonUtils.fetchTrailerData();
                Log.v(LOG_TAG, "fetchTrailerData called.");
                return trailerListItems;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<TrailerListItem> trailerData) {
            mTrailersLoadingIndicator.setVisibility(View.INVISIBLE);
            if (trailerData != null) {
                showTrailersDataView();
                mTrailersAdapter.setmTrailersList(trailerData);
            } else {
                showTrailersErrorMessage();
            }
        }
    }
}
