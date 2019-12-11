
package com.example.peter.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/*
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
*/

// exportScheme may throw a warning, so setting it false is necessary to avoid the warnings.
@Database(entities = {UserFavoritesEntry.class}, version = 1, exportSchema = false)
public abstract class MoviesRoomDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "UserFavorites";
    private static final Object LOCK = new Object();
    private static MoviesRoomDatabase instance;

    /** Follows Singleton Design Pattern. Exactly one Room Database is created at all times.
     *  In multi-threaded environments, the synchronized block prevents a possible second instance
     *  from being created on another thread.
     * */
    public static MoviesRoomDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        MoviesRoomDatabase.class, MoviesRoomDatabase.DATABASE_NAME)
                        .build();
                }
            }
            return instance;
    }

    public abstract UserFavoritesDao userFavoritesDao();
}

