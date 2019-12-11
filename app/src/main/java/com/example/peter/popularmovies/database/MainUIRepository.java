package com.example.peter.popularmovies.database;

import android.app.Application;
import android.arch.persistence.room.RoomDatabase;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

public class MainUIRepository {
    private MainUIDao mainUIDao;
    /**
    public MainUIRepository(Application application) {
        MainUIRoomDatabase database = MainUIRoomDatabase.getInstance(application);
        mainUIDao = database.mainUIDao();

        allPopularMovies = mainUIDao.getPopularMoviesPosters();
        allHighestRatedMovies = mainUIDao.getHighestRatedMovies();
        allUserFavorites = mainUIDao.getUsersFavoriteMovies();

    }
    */

    public MainUIRepository(MainUIRoomDatabase database) {
        mainUIDao = database.mainUIDao();
    }

    /** The rest of this is basically wrapping the same methods in your DAO into methods
     * defined here .
     */

    public void insert(MainUIEntry mainUIEntry) {
        new InsertAsyncTask(mainUIDao).execute(mainUIEntry);
    }

    public void delete(MainUIEntry mainUIEntry) {
       // new DeleteAsyncTask(mainUIDao).execute(mainUIEntry);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mainUIDao).execute();
    }

    public String[] getPostersUI() {
        String[] posters = null;
        try {
            posters = new getPostersAsyncTask(mainUIDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return posters;
    }

    /*
    public String[] getPopularMoviesUI() {
        String[] popularMoviesPosters = null;
        try {
            popularMoviesPosters = new getPopularMoviesAsyncTask(mainUIDao)
                    .execute()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return popularMoviesPosters;
    }

    public String[] getHighestRatedUI() {
        String[] highestRatedPosters = null;
        try {
            highestRatedPosters = new getHighestRatedMoviesAsyncTask(mainUIDao)
                    .execute()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return highestRatedPosters;
    }

    public String[] getFavoritesUI() {
        String[] userFavoritesPosters = null;
        try {
            userFavoritesPosters = new getFavoritesAsyncTask(mainUIDao)
                    .execute()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return userFavoritesPosters;
    }
    */
    /**
     *
     *
     *  Room Queries absolutely CANNOT run on the main thread. So each of the above methods
     *  is given an AsyncTask subclass to run these queries in the background to avoid
     *  any UI errors. (Except the queries retriving ColumnInfo)
     *
     */

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private MainUIDao mainUIDao;

        private deleteAllAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mainUIDao.deleteAllEntries();
            return null;
        }
    }

    private static class getPostersAsyncTask extends AsyncTask<Void, Void, String[]> {
        private MainUIDao mainUIDao;

        private getPostersAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            return mainUIDao.getMoviePosters();
        }
    }
    /**
     * AsyncTask for insert()
     */
    private static class InsertAsyncTask extends AsyncTask<MainUIEntry, Void, Void> {
        private MainUIDao mainUIDao;

        private InsertAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected Void doInBackground(MainUIEntry... mainUIEntries) {
            mainUIDao.insertMoviePosters(mainUIEntries[0]);
            return null;
        }
    }


    /**
     * AsyncTask for delete()

    private static class DeleteAsyncTask extends AsyncTask<MainUIEntry, Void, Void> {
        private MainUIDao mainUIDao;

        private DeleteAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected Void doInBackground(MainUIEntry... mainUIEntries) {
            mainUIDao.deleteMoviePosters(mainUIEntries[0]);
            return null;
        }
    }
    */

    /**
     * AsyncTask for getPopularMoviesUI()

    private static class getPopularMoviesAsyncTask extends AsyncTask<Void, Void, String[]> {
        private MainUIDao mainUIDao;

        private getPopularMoviesAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            return mainUIDao.getPopularMoviesPosters();
        }
    }

    /**
     * AsyncTask for getHighestRatedMoviesUI()

    private static class getHighestRatedMoviesAsyncTask extends AsyncTask<Void, Void, String[]> {
        private MainUIDao mainUIDao;

        private getHighestRatedMoviesAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            return mainUIDao.getHighestRatedMovies();
        }
    }

    /**
     * AsyncTask for getFavoritesUI()

    private static class getFavoritesAsyncTask extends AsyncTask<Void, Void, String[]> {
        private MainUIDao mainUIDao;

        private getFavoritesAsyncTask(MainUIDao dao) {
            this.mainUIDao = dao;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
           return mainUIDao.getUsersFavoriteMovies();
        }
    }
     */
}
