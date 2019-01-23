package com.example.kim.popularmovies3;

        import android.content.Context;
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

public final class MovieJsonUtils {

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String API_KEY = "947ec954e528c2a44ab6e1e3cf12b7f0";
    // Example API request:
    // https://api.themoviedb.org/3/movie/550?api_key="api_key"
    // https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1
    /*
     * The sort field. Can be most_popular or top_rated.
     * Default: results are sorted by top_rated if no field is specified.
     */
    //final static String PARAM_ORDERBY = "orderby";
    //final static String orderBy = "popular";
    /** Tag for the log messages */
    private static final String LOG_TAG = MovieJsonUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link MovieJsonUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    private MovieJsonUtils() {
    }

    /**
     * Builds the URL used to query The MovieDB.
     * @return The URL to use to query the MovieDB server.
     */
    public static URL buildUrl(String orderByBuildUrl) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(orderByBuildUrl)
                .appendQueryParameter("api_key", API_KEY)
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
     * Query the MovieDB dataset and return a list of {@link MovieItem} objects.
     */
    public static List<MovieItem> fetchMovieData(String orderBy) {

        // Create URL object
        URL url = buildUrl(orderBy);
        Log.v(LOG_TAG, "buildUrl called.");

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link MovieItem}s
        List<MovieItem> movieItems = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link MovieItem}s
        return movieItems;
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

    public static List<MovieItem> extractFeatureFromJson(String movieItemJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieItemJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding MovieItems to
        List<MovieItem> movieItemList = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieItemJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or MovieItems).
            JSONArray movieItemArray = baseJsonResponse.getJSONArray("results");

            // For each MovieItem in the MovieItemArray, create an {@link MovieItem} object
            for (int i = 0; i < movieItemArray.length(); i++) {

                // Get a single MovieItem at position i within the list of MovieItems
                JSONObject currentMovieItem = movieItemArray.getJSONObject(i);

                // Extract the value for the key called "place"
                String title = currentMovieItem.getString("title");

                // Extract the value for the key called "time"
                String image = currentMovieItem.getString("poster_path");

                String overview = currentMovieItem.getString("overview");

                // Extract the value for the key called "url"
                // String url = currentMovieItem.getString("url");

                // Create a new {@link MovieItem} object with the image, title and synopsis
                // from the JSON response.
                MovieItem movieItem = new MovieItem(title, image, overview);

                // Add the new {@link MovieItem} to the list of MovieItems.
                movieItemList.add(movieItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movieItem JSON results", e);
        }

        // Return the list of earthquakes
        return movieItemList;
    }
}