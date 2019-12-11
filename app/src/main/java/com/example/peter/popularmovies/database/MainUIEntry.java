package com.example.peter.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "MoviePosters")
public class MainUIEntry {
    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "Posters")
    private String posters;

    /*
    @ColumnInfo(name = "PopularMovies")
    private String popularMoviesPosters;

    @ColumnInfo(name = "HighestRatedMovies")
    private String highestRatedMoviePosters;

    @ColumnInfo(name ="UsersFavoriteMovies")
    private String usersFavoriteMoviesPosters;
    */
    public long getUid() {
        return uid;
    }

    public MainUIEntry() {}

    public void setUid(long id) { uid = id;  }

    public void setPosters(String posters) {
        this.posters = posters;
    }

    public String getPosters() {
        return posters;
    }

    /*
    public void setPopularMoviesPosters(String poster) { popularMoviesPosters = poster; }

    public void setHighestRatedMoviePosters(String poster) { highestRatedMoviePosters = poster; }

    public void setUsersFavoriteMoviesPosters(String poster) { usersFavoriteMoviesPosters = poster; }


    public String getPopularMoviesPosters() {
        return popularMoviesPosters;
    }

    public String getHighestRatedMoviePosters() {
        return highestRatedMoviePosters;
    }

    public String getUsersFavoriteMoviesPosters() {
        return usersFavoriteMoviesPosters;
    }
    */
}
