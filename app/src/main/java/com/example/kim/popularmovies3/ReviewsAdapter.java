package com.example.kim.popularmovies3;

        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
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
    private List<ReviewListItem> mReviewsList;
    private Context mContext;
    private static final String LOG_TAG = FavoriteAdapter.class.getSimpleName();

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     * @param context  the current Context
     * @param reviewsList the ItemClickListener
     */

    public ReviewsAdapter(Context context, List<ReviewListItem> reviewsList) {
        this.mReviewsList = reviewsList;
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
                .inflate(R.layout.review_list_row, parent, false);

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

        Log.v(LOG_TAG, "onBindViewHolder for Reviews called.");

        // Determine the values of the wanted data
        ReviewListItem movieReview = mReviewsList.get(position);
        int reviewId = movieReview.getReviewId();
        String reviewAuthor = movieReview.getAuthor();
        String reviewText = movieReview.getReviewText();
        String reviewUrl = movieReview.getReviewUrl();

        //Set values
        holder.reviewAuthorTextView.setText(reviewAuthor);
        holder.reviewContentTextView.setText(reviewText);
    }

    /**
     * Returns the number of items to display.
     */

    @Override
    public int getItemCount() {
        if (mReviewsList == null) {
            return 0;
        }
        return mReviewsList.size();
    }

    public List<ReviewListItem> getReviews() {
        return mReviewsList;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */

    public void setReviews(List<ReviewListItem> reviewList) {
        mReviewsList = reviewList;
        notifyDataSetChanged();
    }

//    public interface ItemClickListener {
//        void onItemClickListener(int itemId);
//    }

    // Inner class for creating ViewHolders
    class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the author and content TextViews
        TextView reviewAuthorTextView;
        TextView reviewContentTextView;

        /**
         * Constructor for the TaskViewHolders.
         * @param itemView The view inflated in onCreateViewHolder
         */

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            reviewAuthorTextView = itemView.findViewById(R.id.review_list_author);
            reviewContentTextView = itemView.findViewById(R.id.review_list_text);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int reviewPosition = getAdapterPosition();
            ReviewListItem review = mReviewsList.get(reviewPosition);

            String url = review.getReviewUrl();
            Intent reviewUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(reviewUrlIntent);
        }
     }
}