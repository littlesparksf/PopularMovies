package com.example.kim.popularmovies3;

        import android.arch.lifecycle.Observer;
        import android.arch.lifecycle.ViewModelProviders;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.DividerItemDecoration;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.helper.ItemTouchHelper;
        import android.util.Log;

        import com.example.kim.popularmovies3.Adapters.FavoriteAdapter;
        import com.example.kim.popularmovies3.Objects.MovieItem;
        import com.example.kim.popularmovies3.database.AppDatabase;

        import java.util.ArrayList;
        import java.util.List;
        import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class FavoritesActivity extends AppCompatActivity {
    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private FavoriteAdapter mAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.favorites_recycler_view);
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Call the constructor of CustomAdapter to send the reference and data to the Adapter
        mAdapter = new FavoriteAdapter(FavoritesActivity.this, new ArrayList<MovieItem>());
        // Initialize the adapter and attach it to the RecyclerView
        //mAdapter = new FavoriteAdapter(Context context, FavoriteAdapter.ItemClickListener listener);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {

                    @Override

                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<MovieItem> tasks = mAdapter.getFavoriteMovies();
                        mDb.favoriteDao().deleteFavorite( tasks.get(position));
                    }

                });
            }
        }).attachToRecyclerView(mRecyclerView);

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        FavoriteViewModel viewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<MovieItem>>() {

            @Override

            public void onChanged(@Nullable List<MovieItem> favoriteMovies) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setFavorites(favoriteMovies);
            }
        });
    }

    //@Override
    //public void onItemClickListener(int itemId) {
    //}
}