package com.example.kim.popularmovies3;

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.List;

/**
 * This FavoriteAdapter creates and binds ViewHolders, which hold the overview,
 * title and rating of a movie, to a RecyclerView to efficiently display data.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    // Class variables for the List that holds task data and the Context
    private List<MovieItem> mFavoriteMovies;
    private Context mContext;
    private static final String LOG_TAG = FavoriteAdapter.class.getSimpleName();

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     * @param context  the current Context
     * @param favoriteMovies the ItemClickListener
     */

    public ReviewsAdapter(Context context, List<MovieItem> favoriteMovies) {
        this.mFavoriteMovies = favoriteMovies;
        this.mContext = context;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new FavoriteViewHolder that holds the view for each task
     */

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.favorite_list_row, parent, false);

        return new ReviewsViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {

        Log.v(LOG_TAG, "onBindViewHolder called.");

        // Determine the values of the wanted data
        MovieItem favoriteMovie = mFavoriteMovies.get(position);
        String description = favoriteMovie.getOverview();
        String title = favoriteMovie.getTitle();
        String rating = favoriteMovie.getRating();

        //Set values
        holder.favoriteOverviewView.setText(description);
        holder.favoriteTitleView.setText(title);
        holder.favoriteRatingView.setText(rating);
    }

    /**
     * Returns the number of items to display.
     */

    @Override
    public int getItemCount() {
        if (mFavoriteMovies == null) {
            return 0;
        }
        return mFavoriteMovies.size();
    }

    public List<MovieItem> getFavoriteMovies() {
        return mFavoriteMovies;
    }


    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */

    public void setFavorites(List<MovieItem> favoriteMovies) {
        mFavoriteMovies = favoriteMovies;
        notifyDataSetChanged();
    }

//    public interface ItemClickListener {
//        void onItemClickListener(int itemId);
//    }

    // Inner class for creating ViewHolders
    class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView favoriteTitleView;
        TextView favoriteOverviewView;
        TextView favoriteRatingView;

        /**
         * Constructor for the TaskViewHolders.
         * @param itemView The view inflated in onCreateViewHolder
         */

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            favoriteTitleView = itemView.findViewById(R.id.favorite_title);
            favoriteOverviewView = itemView.findViewById(R.id.favorite_overview);
            favoriteRatingView = itemView.findViewById(R.id.favorite_rating);

            itemView.setOnClickListener(this);
        }


        @Override

        public void onClick(View view) {
            int elementId = mFavoriteMovies.get(getAdapterPosition()).getId();
            //mItemClickListener.onItemClickListener(elementId);
        }
    }
}