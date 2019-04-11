package com.example.kim.popularmovies3.database;

        import android.arch.lifecycle.LiveData;
        import android.arch.persistence.room.Dao;
        import android.arch.persistence.room.Delete;
        import android.arch.persistence.room.Insert;
        import android.arch.persistence.room.OnConflictStrategy;
        import android.arch.persistence.room.Query;
        import android.arch.persistence.room.Update;

        import com.example.kim.popularmovies3.MovieItem;

        import java.util.List;

        import static android.arch.persistence.room.OnConflictStrategy.*;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favoriteMovies ORDER BY id")
    LiveData<List<MovieItem>> loadAllFavorites();

    @Insert
    void insertFavorite(MovieItem newFavoriteMovie);

    @Update(onConflict = REPLACE)
    void updateFavorite(MovieItem updatedFavoriteMovie);

    @Delete
    void deleteFavorite(MovieItem existingFavoriteMovie);

    @Query("SELECT * FROM favoriteMovies WHERE id = :id")
    LiveData<MovieItem> loadTaskById(int id);
}