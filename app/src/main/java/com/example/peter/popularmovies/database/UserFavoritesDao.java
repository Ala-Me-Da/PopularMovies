package com.example.peter.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.OnConflictStrategy;

/*
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
*/

import java.util.List;

@Dao
public interface UserFavoritesDao {

    @Query("SELECT * FROM UserFavorites")
    LiveData<List<UserFavoritesEntry>> allUserFavorites();

    @Query("SELECT * FROM UserFavorites")
    List<UserFavoritesEntry> getAllUserFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(UserFavoritesEntry entry);

    @Delete
    void deleteMovie(UserFavoritesEntry entry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(UserFavoritesEntry entry);
}