package com.example.kim.popularmovies3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<MovieItem> movieItemList;
    private Context mContext;
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public interface MovieClickHandler {
        void onClick(MovieItem movieClicked);
    }

    public MovieAdapter(Context context, List<MovieItem> movieItemList) {
        this.movieItemList = movieItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflate the item layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        ViewHolder viewHolder = new ViewHolder(view);
        Log.v(LOG_TAG, "onCreateViewHolder called.");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final MovieItem movieItem = movieItemList.get(position);

        Log.v(LOG_TAG, "onBindViewHolder called.");

        // Get poster path
        String posterPath = movieItem.getImage();
        Log.v(LOG_TAG, "Poster path: " + posterPath);

        // Build poster url
        String posterUrl = "http://image.tmdb.org/t/p/" + "w185" + posterPath;
        // Log poster url
        Log.v(LOG_TAG, "Poster Url: " + posterUrl);

        //Render image using Picasso library
        Picasso.with(mContext).load(posterUrl)
                .error(R.drawable.cat)
                .placeholder(R.drawable.cat)
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return (null != movieItemList ? movieItemList.size() : 0);
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param movieData The new weather data to be displayed.
     */
    public void setMovieData(List<MovieItem> movieData) {
        movieItemList = movieData;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        // Initialize the view within this item
        protected ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            // Get a reference to the view within this item
            this.imageView = view.findViewById(R.id.poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int moviePosition = getAdapterPosition();
            MovieItem movie = movieItemList.get(moviePosition);

            Intent intent = new Intent(imageView.getContext(), MovieDetail.class);
            intent.putExtra ("movie", new MovieItem(movie.getTitle(), movie.getImage(), movie.getOverview()));
            imageView.getContext().startActivity(intent);
        }
    }
}