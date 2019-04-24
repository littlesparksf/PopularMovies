package com.example.kim.popularmovies3;

import android.app.Application;
import android.util.Log;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.kim.popularmovies3.Objects.MovieItem;
import com.example.kim.popularmovies3.database.AppDatabase;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel{

    // Constant for logging
    private static final String TAG = FavoriteViewModel.class.getSimpleName();

    private LiveData<List<MovieItem>> favorites;

    public FavoriteViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        favorites = database.
                favoriteDao().loadAllFavorites();
    }

    public LiveData<List<MovieItem>> getFavorites() {
        return favorites;
    }
}
