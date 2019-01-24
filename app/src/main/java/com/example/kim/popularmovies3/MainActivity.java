 package com.example.kim.popularmovies3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


     private RecyclerView mRecyclerView;
     private MovieAdapter mAdapter;
     private TextView mEmptyStateTextView;
     private ProgressBar mLoadingIndicator;
     public String orderBy;
     private static final String LOG_TAG = MainActivity.class.getSimpleName();

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG, "onCreate called.");

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

        // Setting to improve performance if changes in content do not change in child layout size
        mRecyclerView.setHasFixedSize(true);

        // Call the constructor of CustomAdapter to send the reference and data to the Adapter
        mAdapter = new MovieAdapter(MainActivity.this, new ArrayList<MovieItem>());

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        mRecyclerView.setAdapter(mAdapter);

        Log.v(LOG_TAG, "Adapter set on recycler view.");

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

         // And register to be notified of preference changes
         // So we know when the user has adjusted the query settings
         prefs.registerOnSharedPreferenceChangeListener(this);

         orderBy = prefs.getString(
                 getString(R.string.settings_order_by_key),
                 getString(R.string.settings_order_by_default)
         );

         Log.v(LOG_TAG, "orderBy called  " + orderBy);

         Log.v(LOG_TAG, "Shared preferences called.");

         new FetchMovieTask().execute(orderBy);

         Log.v(LOG_TAG, "FetchMovieTask called.");
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
 }
