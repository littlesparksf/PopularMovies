package com.example.kim.popularmovies3;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving movie data from MovieDB.
 */

public final class ReviewsJsonUtils {

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String API_KEY = "947ec954e528c2a44ab6e1e3cf12b7f0";
    // Example API requests:
    // https://api.themoviedb.org/3/movie/550?api_key="api_key"
    // https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1

    //Need to get this from detail activity
    private static final String MOVIE_ID = "id";

    // Put this in strings.xml
    private static final String REVIEWS = "reviews";

    /** Tag for the log messages */
    private static final String LOG_TAG = MovieJsonUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link MovieJsonUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    private ReviewsJsonUtils() {
    }

    /**
     * Builds the URL used to query The MovieDB.
     * @return The URL to use to query the MovieDB server.
     */
    public static URL buildUrl() {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("id", MOVIE_ID)
                .appendQueryParameter("reviews", REVIEWS)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "buildUrl called." + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Query the MovieDB dataset and return a list of {@link ReviewListItem} objects.
     */
    public static List<ReviewListItem> fetchReviews() {

        // Create URL object
        URL url = buildUrl();
        Log.v(LOG_TAG, "buildUrl called.");

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        Log.v(LOG_TAG, "Json Response: " + jsonResponse);

        // Extract relevant fields from the JSON response and create a list of {@link MovieItem}s
        List<ReviewListItem> reviewListItems = extractFeatureFromJson(jsonResponse);

        Log.v(LOG_TAG, "List created.");

        // Return the list of {@link MovieItem}s
        return reviewListItems;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            Log.v(LOG_TAG, jsonResponse);
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the MovieItem JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link MovieItem} objects that has been built up from
     * parsing the given JSON response.
     */

    public static List<ReviewListItem> extractFeatureFromJson(String movieItemJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieItemJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding MovieItems to
        List<ReviewListItem> reviewsList = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieItemJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or MovieItems).
            JSONArray reviewsArray = baseJsonResponse.getJSONArray("results");

            // For each MovieItem in the MovieItemArray, create an {@link MovieItem} object
            for (int i = 0; i < reviewsArray.length(); i++) {

                // Get a single MovieItem at position i within the list of MovieItems
                JSONObject currentReview = reviewsArray.getJSONObject(i);

                // Extract the value for the key called "author"
                String author = currentReview.getString("author");

                // Extract the value for the key called "text"
                String text = currentReview.getString("content");

                // Extract the value for the key called "text"
                String url = currentReview.getString("url");

                // Create a new {@link MovieItem} object with the image, title and synopsis
                // from the JSON response.
                ReviewListItem reviewListItem = new ReviewListItem(author, text, url);

                // Add the new {@link MovieItem} to the list of MovieItems.
                reviewsList.add(reviewListItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movieItem JSON results", e);
        }

        Log.v(LOG_TAG, "movieItemList created.");

        // Return the list of reviews
        return reviewsList;
    }
}