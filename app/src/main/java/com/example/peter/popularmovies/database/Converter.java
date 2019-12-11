package com.example.peter.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

public class Converter {
    private static final String delimiter = " ";

    @TypeConverter
    public String[] toArray(String s) {
        return s.split(delimiter);
    }

    @TypeConverter
    public String toText(String[] strings) {
        StringBuilder result = new StringBuilder(" ");
        if(strings != null) {
            for (String s : strings) {
                result.append(s).append(delimiter);
            }
        }

        return result.toString();
    }


}
