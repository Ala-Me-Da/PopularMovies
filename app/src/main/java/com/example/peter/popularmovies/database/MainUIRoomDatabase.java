package com.example.peter.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {MainUIEntry.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class MainUIRoomDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "MoviePosters";
    private static final Object LOCK = new Object();
    private static MainUIRoomDatabase instance;

    /** Follows Singleton Design Pattern. Exactly one Room Database is created at all times.
     *  In multi-threaded environments, the synchronized block prevents a possible second instance
     *  from being created on another thread.
     * */
    public static MainUIRoomDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        MainUIRoomDatabase.class, MainUIRoomDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return instance;
    }

    public abstract MainUIDao mainUIDao();
}
