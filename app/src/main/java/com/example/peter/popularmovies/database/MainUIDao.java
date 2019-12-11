package com.example.peter.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

@Dao
public interface MainUIDao {

    /*
    @Query("SELECT PopularMovies FROM MoviePosters")
    String[] getPopularMoviesPosters();

    @Query("SELECT HighestRatedMovies FROM MoviePosters")
    String[] getHighestRatedMovies();

    @Query("SELECT UsersFavoriteMovies FROM MoviePosters")
    String[] getUsersFavoriteMovies();
    */

    @Query("SELECT Posters FROM MoviePosters")
    String[] getMoviePosters();

    @Query("DELETE FROM MoviePosters")
    void deleteAllEntries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMoviePosters(MainUIEntry mainUIEntry);

    @Delete
    void deleteMoviePosters(MainUIEntry mainUIEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMoviePosters(MainUIEntry mainUIEntry);
}
