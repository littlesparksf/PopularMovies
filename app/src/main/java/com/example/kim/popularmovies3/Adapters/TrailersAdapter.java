package com.example.kim.popularmovies3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kim.popularmovies3.R;
import com.example.kim.popularmovies3.Objects.TrailerListItem;

import java.util.List;

/**
 * This FavoriteAdapter creates and binds ViewHolders, which hold the overview,
 * title and rating of a movie, to a RecyclerView to efficiently display data.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    // Class variables for the List that holds task data and the Context
    private List<TrailerListItem> mTrailersList;
    private Context mContext;
    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     * @param context  the current Context
     * @param trailersList the ItemClickListener
     */

    public TrailersAdapter(Context context, List<TrailerListItem> trailersList) {
        this.mTrailersList = trailersList;
        this.mContext = context;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new FavoriteViewHolder that holds the view for each task
     */

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.v(LOG_TAG, "OnCreateViewHolder called for trailers");

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.trailer_list_row, parent, false);

        return new TrailersViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {

        Log.v(LOG_TAG, "onBindViewHolder called for trailers.");

        // Determine the values of the wanted data
        TrailerListItem movieTrailer = mTrailersList.get(position);
        String trailerTitle = movieTrailer.getTrailerTitle();
        //int trailerId = movieTrailer.getTrailerId();
        //String trailerUrlKey  = movieTrailer.getTrailerUrlKey();

        //Set values
        holder.trailerTitleTextView.setText(trailerTitle);
    }

    /**
     * Returns the number of items to display.
     */

    @Override
    public int getItemCount() {
        if (mTrailersList == null) {
            return 0;
        }
        return mTrailersList.size();
    }

    public List<TrailerListItem> getTrailers() {
        return mTrailersList;
    }


    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */

    public void setmTrailersList(List<TrailerListItem> movieTrailers) {
        mTrailersList = movieTrailers;
        notifyDataSetChanged();
    }

//    public interface ItemClickListener {
//        void onItemClickListener(int itemId);
//    }

    // Inner class for creating ViewHolders
    class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView trailerTitleTextView;

        /**
         * Constructor for the TaskViewHolders.
         * @param itemView The view inflated in onCreateViewHolder
         */

        public TrailersViewHolder(View itemView) {
            super(itemView);

            trailerTitleTextView = itemView.findViewById(R.id.trailer_movie_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int trailerPosition = getAdapterPosition();
            TrailerListItem trailer = mTrailersList.get(trailerPosition);

            String url = "https://www.youtube.com/watch?v=" + trailer.getTrailerUrlKey();
            Intent trailerUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(trailerUrlIntent);
        }
    }
}