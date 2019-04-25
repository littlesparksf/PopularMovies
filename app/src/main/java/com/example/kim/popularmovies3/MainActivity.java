package com.example.kim.popularmovies3;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Query;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kim.popularmovies3.Adapters.MovieAdapter;
import com.example.kim.popularmovies3.JsonUtils.MovieJsonUtils;
import com.example.kim.popularmovies3.Objects.MovieItem;
import com.example.kim.popularmovies3.database.AppDatabase;
import com.example.kim.popularmovies3.database.FavoriteDao;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

 public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


     private RecyclerView mRecyclerView;
     private MovieAdapter mAdapter;
     private TextView mEmptyStateTextView;
     private ProgressBar mLoadingIndicator;
     public String orderBy;
     public AppDatabase mDb;
     public MovieAdapter favoriteAdapter;
     private static final String LOG_TAG = MainActivity.class.getSimpleName();

     /* Constant values for the names of each respective lifecycle callback */
     private static final String ON_CREATE = "onCreate";
     private static final String ON_START = "onStart";
     private static final String ON_RESUME = "onResume";
     private static final String ON_PAUSE = "onPause";
     private static final String ON_STOP = "onStop";
     private static final String ON_RESTART = "onRestart";
     private static final String ON_DESTROY = "onDestroy";
     private static final String ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG, "onCreate called.");
        Log.v(LOG_TAG, ON_CREATE);

        // Main Recycler View

        // Get a reference to the RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);

        Log.v(LOG_TAG, "Recycler view found.");

         /* This TextView is used to display errors and will be hidden if there are no errors */
         mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

         // Loading indicator will be shown as data loads
         mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Set a GridLayoutManager with default vertical orientation and 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        // Set the Layout Manager to the RecyclerView
        mRecyclerView.setLayoutManager(gridLayoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        // Setting to improve performance if changes in content do not change in child layout size
        mRecyclerView.setHasFixedSize(true);

        // Call the constructor of CustomAdapter to send the reference and data to the Adapter
        mAdapter = new MovieAdapter(MainActivity.this, new ArrayList<MovieItem>());

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        mRecyclerView.setAdapter(mAdapter);

        Log.v(LOG_TAG, "Adapter set on recycler view.");

        // Database
         mDb = AppDatabase.getInstance(getApplicationContext());

        /* Once all of our views are setup, we can load the movie data. */
         loadMovieData();

         Log.v(LOG_TAG, "loadMovieData called.");

    }

     /**
      * This method will get the user's preferred sorting order (top rated or most popular), and then tell a
      * background method to get the movie data in the background.
      */
     private void loadMovieData() {
         showMovieDataView();
         Log.v(LOG_TAG, "showMovieData called.");

         // Obtain a reference to the SharedPreferences file for this app
         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         Log.v(LOG_TAG, "Shared preferences called.");

         // And register to be notified of preference changes
         // So we know when the user has adjusted the query settings
         prefs.registerOnSharedPreferenceChangeListener(this);

         orderBy = prefs.getString(
                 getString(R.string.settings_order_by_key),
                 getString(R.string.settings_order_by_default)
         );
         Log.v(LOG_TAG, "orderBy called  " + orderBy);

         // If order by favorites selected, show favorites
         if (orderBy.equals(getString(R.string.settings_order_by_favorites_value))) {
             FavoriteViewModel favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
             favoriteViewModel.getFavorites().observe(this, new Observer<List<MovieItem>>() {
                 @Override
                 public void onChanged(@Nullable List<MovieItem> favoriteMovies) {
                     mLoadingIndicator.setVisibility(View.INVISIBLE);
                     if (favoriteMovies != null) {
                         mAdapter.setMovieData(favoriteMovies);
                         mAdapter.notifyDataSetChanged();
                     } else {
                         showErrorMessage();
                     }
                 }
             });
         } else {
             new FetchMovieTask().execute(orderBy);
             Log.v(LOG_TAG, "FetchMovieTask called.");
         }
     }

     private void showMovieDataView() {
         /* First, make sure the error is invisible */
         mEmptyStateTextView.setVisibility(View.INVISIBLE);
         /* Then, make sure the movie data is visible */
         mRecyclerView.setVisibility(View.VISIBLE);
     }

     /**
      * This method will make the error message visible and hide the movie
      * View.
      */
     private void  showErrorMessage() {
         /* First, hide the currently visible data */
         mRecyclerView.setVisibility(View.INVISIBLE);
         /* Then, show the error */
         mEmptyStateTextView.setVisibility(View.VISIBLE);
     }

     public class FetchMovieTask extends AsyncTask<String, Void, List<MovieItem>> {

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             mLoadingIndicator.setVisibility(View.VISIBLE);
         }

         @Override
         protected List<MovieItem> doInBackground(String... params) {

             if (params.length == 0) {
                 return null;
             }

             try {
                 List<MovieItem> movieItems = MovieJsonUtils.fetchMovieData(orderBy);
                 Log.v(LOG_TAG, "fetchMovieData called.");
                 return movieItems;

             } catch (Exception e) {
                 e.printStackTrace();
                 return null;
             }
         }

         @Override
         protected void onPostExecute(List<MovieItem> movieData) {
             mLoadingIndicator.setVisibility(View.INVISIBLE);
             if (movieData != null) {
                 showMovieDataView();
                 mAdapter.setMovieData(movieData);
             } else {
                 showErrorMessage();
             }
         }
     }

     @Override
     public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

         if (key.equals(getString(R.string.settings_order_by_key))) {

             // Hide the empty state text view as the loading indicator will be displayed
             mEmptyStateTextView.setVisibility(View.GONE);

             // Show the loading indicator while new data is being fetched
             View loadingIndicator = findViewById(R.id.loading_indicator);
             loadingIndicator.setVisibility(View.VISIBLE);

             loadMovieData();
         }
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
         if (id == R.id.action_settings) {
             Intent settingsIntent = new Intent(this, SettingsActivity.class);
             startActivity(settingsIntent);
             return true;
         }
         return super.onOptionsItemSelected(item);
     }

      /**
      * Called when the activity is becoming visible to the user.
      *
      * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes
      * hidden.
      */
     @Override
     protected void onStart() {
         super.onStart();

         logAndAppend(ON_START);
     }

     /**
      * Called when the activity will start interacting with the user. At this point your activity
      * is at the top of the activity stack, with user input going to it.
      *
      * Always followed by onPause().
      */
     @Override
     protected void onResume() {
         super.onResume();

         logAndAppend(ON_RESUME);
     }

     /**
      * Called when the system is about to start resuming a previous activity. This is typically
      * used to commit unsaved changes to persistent data, stop animations and other things that may
      * be consuming CPU, etc. Implementations of this method must be very quick because the next
      * activity will not be resumed until this method returns.
      *
      * Followed by either onResume() if the activity returns back to the front, or onStop() if it
      * becomes invisible to the user.
      */
     @Override
     protected void onPause() {
         super.onPause();

         logAndAppend(ON_PAUSE);
     }

     // COMPLETED (5) Override onStop, call super.onStop, and call logAndAppend with ON_STOP
     /**
      * Called when the activity is no longer visible to the user, because another activity has been
      * resumed and is covering this one. This may happen either because a new activity is being
      * started, an existing one is being brought in front of this one, or this one is being
      * destroyed.
      *
      * Followed by either onRestart() if this activity is coming back to interact with the user, or
      * onDestroy() if this activity is going away.
      */
     @Override
     protected void onStop() {
         super.onStop();

         logAndAppend(ON_STOP);
     }

     // COMPLETED (6) Override onRestart, call super.onRestart, and call logAndAppend with ON_RESTART
     /**
      * Called after your activity has been stopped, prior to it being started again.
      *
      * Always followed by onStart()
      */
     @Override
     protected void onRestart() {
         super.onRestart();

         logAndAppend(ON_RESTART);
     }

     // COMPLETED (7) Override onDestroy, call super.onDestroy, and call logAndAppend with ON_DESTROY
     /**
      * The final call you receive before your activity is destroyed. This can happen either because
      * the activity is finishing (someone called finish() on it, or because the system is
      * temporarily destroying this instance of the activity to save space. You can distinguish
      * between these two scenarios with the isFinishing() method.
      */
     @Override
     protected void onDestroy() {
         super.onDestroy();

         logAndAppend(ON_DESTROY);
     }

     @Override
     protected void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
         logAndAppend(ON_SAVE_INSTANCE_STATE);
         // Not sure if I need to store movie list in  or if this
         // works without the @Override 
     }

     /**
      * Logs to the console and appends the lifecycle method name to the TextView so that you can
      * view the series of method callbacks that are called both from the app and from within
      * Android Studio's Logcat.
      *
      * @param lifecycleEvent The name of the event to be logged.
      */
     private void logAndAppend(String lifecycleEvent) {
         Log.d(LOG_TAG, "Lifecycle Event: " + lifecycleEvent);
     }
 }
