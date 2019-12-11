package com.example.peter.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/*
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
*/
@Entity(tableName = "UserFavorites")
public class UserFavoritesEntry {

    // autoGenerate not set since the movieID from moviesDB is already an UID.
    @PrimaryKey
    private int movieID;

    @ColumnInfo(name = "MovieTitle")
    private String movieTitle;

    @ColumnInfo(name = "MovieURL")
    private String movieURL;

    public UserFavoritesEntry() {};

    public int getMovieID() { return movieID; }

    public String getMovieTitle() { return movieTitle; }

    public String getMovieURL() { return movieURL; }

    public void setMovieID(int id) { movieID = id; }

    public void setMovieTitle(String title) { movieTitle = title; }

    public void setMovieURL(String url) { movieURL = url; }
}
