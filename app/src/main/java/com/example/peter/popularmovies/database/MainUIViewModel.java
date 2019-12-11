/**
package com.example.peter.popularmovies.database;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainUIViewModel extends AndroidViewModel {
    private MainUIRepository repository;

    public MainUIViewModel(@NonNull Application application) {
        super(application);
        repository = new MainUIRepository(application);
    };

    public void insert(MainUIEntry mainUIEntry) {
        repository.insert(mainUIEntry);
    }

    public void delete(MainUIEntry mainUIEntry) {
        repository.delete(mainUIEntry);
    }

    public LiveData<List<String>> getPopularMovies() {
        return repository.getPopularMoviesUI();
    }

    public LiveData<List<String>> getHighestRatedMovies() {
        return repository.getHighestRatedUI();
    }

    public LiveData<List<String>> getUserFavorites() {
        return repository.getFavoritesUI();
    }

}
*/