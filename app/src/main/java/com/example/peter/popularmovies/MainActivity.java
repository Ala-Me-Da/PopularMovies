package com.example.peter.popularmovies;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.peter.popularmovies.PopularMoviesAdapter.PopularMoviesClickHandler;
import com.example.peter.popularmovies.database.MainUIEntry;
import com.example.peter.popularmovies.database.MainUIRepository;
import com.example.peter.popularmovies.database.MainUIRoomDatabase;
import com.example.peter.popularmovies.utilities.MovieNetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements PopularMoviesClickHandler {
    /**
     * For Code Reviewers / testing purposes if necessary
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SPAN_COUNT = 2; // Number of Movies to display horizontally on the Grid
    private static final int DEFAULT_MOVIE_DISPLAY = 0; //Display Most Popular Movies first
    private static final int USER_FAVORITES_OPTION = 2;
    private static final int TOP_RATED_OPTION = 1;
    private static final int MOST_POPULAR_OPTION = 0;

    private GridLayoutManager gridLayoutManager;
    private String[] posterImageUrls;
    // private MainUIViewModel viewModel;
    private static int UI_FLAG = -1;
    private static MainUIRoomDatabase mainUIRoomDatabase;
    private MainUIRepository repository;

    @BindView(R.id.grid_layout_rv)
    RecyclerView recyclerView;

    @BindView(R.id.no_connection_error_tv)
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //viewModel = ViewModelProviders.of(this).get(MainUIViewModel.class);

        if (!isOnline()) {
            String errorMsg = "Cannot display movies.\n Please check your internet connection.\n" +
                    "Use top right corner menu to reload movies once reconnected.\n";
            errorTextView.setText(errorMsg);
            return;
        }

        mainUIRoomDatabase = MainUIRoomDatabase.getInstance(this);
        repository = new MainUIRepository(mainUIRoomDatabase);

        switch (UI_FLAG) {
            case MOST_POPULAR_OPTION:
                //fillGridWithMovies(viewModel.getPopularMovies());
                posterImageUrls = repository.getPostersUI();
                break;
            case TOP_RATED_OPTION:
                //fillGridWithMovies(viewModel.getHighestRatedMovies());
                posterImageUrls = repository.getPostersUI();
                break;
            case USER_FAVORITES_OPTION:
               // fillGridWithMovies(viewModel.getUserFavorites());
                posterImageUrls = repository.getPostersUI();
                break;
            default:
                posterImageUrls = setMoviePosters(DEFAULT_MOVIE_DISPLAY);
                new QueryingAsyncTask(false).execute(posterImageUrls);
        }

        /* Set up Recycler view and Spinner view */
        setUpRecyclerGridView(posterImageUrls);

    }



   /* private void fillGridWithMovies(final LiveData<List<String>> liveData) {
        liveData.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                setUpRecyclerGridView(strings.toArray(new String[10]));
            }
        });

    }
    */

    /**
     * Function created thanks to:
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * Provided U Implementation guide for this Popular Movies app.
     *
     * @return 'true' if exitValue is 0, 'false' if other. This determines if internet is connected.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
       /*
        int exitValue;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
        */
    }

    /**
     * TODO (8) Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.
     * RecyclerView with Grid Layout displaying all movie posters upon app start-up.
     *
     * @param movieUrls String array contain poster paths of all movie posters.
     */
    private void setUpRecyclerGridView(String[] movieUrls) {
        /* Set up GridView through the RecyclerView. */
        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        PopularMoviesAdapter adapter = new PopularMoviesAdapter(this, movieUrls);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


    /**
     * Extra String parsing necessary because "Release Date: " (currentText String) is a UI String,
     * and must be put in strings.xml per project specifications.
     *
     * @param position Location of movie in the results JSONArray.
     */
    private String getMovieReleaseData(int position) {
        String releaseData = MovieNetworkUtils.parseMovieReleaseDataFromJson(position);
        String currentText = getResources().getString(R.string.release_date);
        releaseData = currentText + releaseData;
        return releaseData;
    }

    /**
     * Extra String parsing necessary because "Average Rating: " (firstPart) and "/10" (secondPart)
     * are UI Strings, and must be put in strings.xml per project specifications.
     *
     * @param position Location of movie in the results JSONArray.
     */
    private String getMovieVoteAverage(int position) {
        String voteAverage = MovieNetworkUtils.parseMovieVoteAverageFromJson(position);
        String currentText = getResources().getString(R.string.vote_average);
        String firstPart = currentText.substring(0, 15);
        String secondPart = currentText.substring(16, currentText.length());
        voteAverage = firstPart + " " + voteAverage + secondPart;
        return voteAverage;
    }

    private String getMovieSynopsis(int position) {
        return MovieNetworkUtils.parseMoviePlotSynopsisFromJson(position);
    }


    /**
     * TODO (7) When a movie poster thumbnail is selected, the movie details screen is launched.
     *
     * @param movieUrl        Poster path of movie poster image.
     * @param adapterPosition Position of movie in grid / JSONArray
     */
    @Override
    public void onClick(String movieUrl, int adapterPosition) {
        Context context = MainActivity.this;
        Class destinationActivity = MovieDetailsActivity.class;

        String synopsis = getMovieSynopsis(adapterPosition);
        String voteAverage = getMovieVoteAverage(adapterPosition);
        String releaseData = getMovieReleaseData(adapterPosition);

        String[] movieData = new String[]{synopsis, voteAverage, releaseData};

        Intent intentThatOpensMovieDetails = new Intent(context, destinationActivity);
        intentThatOpensMovieDetails.putExtra(Intent.EXTRA_TEXT, movieUrl);
        intentThatOpensMovieDetails.putExtra("movieDetails", movieData);
        intentThatOpensMovieDetails.putExtra(Intent.ACTION_VIEW, adapterPosition);
        startActivity(intentThatOpensMovieDetails);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_options, menu);
        return true;
    }


    /**
     * TODO (9) When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
     *
     * @param item Menu item containing sort criteria
     * @return false or true depending on internet connection / selection processing.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isOnline()) {
            Toast.makeText(this, "No internet. Cannot display data.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    errorTextView.setVisibility(TextView.INVISIBLE);
                }
            });
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.most_popular:
                posterImageUrls = setMoviePosters(MOST_POPULAR_OPTION);
                setUpRecyclerGridView(posterImageUrls);
                new QueryingAsyncTask(true).execute(posterImageUrls);
                new QueryingAsyncTask(false).execute(posterImageUrls);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UI_FLAG = MOST_POPULAR_OPTION;
                    }
                });
                break;
            case R.id.top_rated:
                posterImageUrls = setMoviePosters(TOP_RATED_OPTION);
                setUpRecyclerGridView(posterImageUrls);
                new QueryingAsyncTask(true).execute(posterImageUrls);
                new QueryingAsyncTask(false).execute(posterImageUrls);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UI_FLAG = TOP_RATED_OPTION;
                    }
                });
                break;
            case R.id.user_favorites:
                posterImageUrls = setMoviePosters(USER_FAVORITES_OPTION);
                setUpRecyclerGridView(posterImageUrls);
                new QueryingAsyncTask(true).execute(posterImageUrls);
                new QueryingAsyncTask(false).execute(posterImageUrls);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UI_FLAG = USER_FAVORITES_OPTION;
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String[] setMoviePosters(final int flag) {
        String[] posters = null;
        try {
            posters = new MoviesDBQueryTask().execute(flag).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return posters;
    }

    /**
     * TODO (2.5) Background Threads
     * Use AsyncTask for HTTP requests on a background thread to avoid crash in UI thread.
     *
     * @Integer is the sort option (top rated or most popular)
     */
    public class MoviesDBQueryTask extends AsyncTask<Integer, Void, String[]> {

        @Override
        protected String[] doInBackground(Integer... params) {
            return (params[0] != 2) ? MovieNetworkUtils.buildPosterPathUrls(params[0]) :
                    MovieNetworkUtils.getFavoritesFromRoom(MainActivity.this);
        }
    }

    private static class QueryingAsyncTask extends AsyncTask<String[], Void, Void> {

        /*
        private static int FLAG = -1;
        private static final int POP = 0;
        private static final int TOP = 1;
        private static final int FAV = 2;
        */
        private static boolean CLEAR_ENTRIES = false;
        // Redfined as a HashMap<String, MainUIEntry> for easier updates
        private static ArrayList<MainUIEntry> currentEntries;

        QueryingAsyncTask(final boolean clearEntries) {
            CLEAR_ENTRIES = clearEntries;
        }

        /** TODO: INSERT ONCE THEN UPDATE EACH TIME */
        @Override
        protected Void doInBackground(String[]... strings) {
            if(CLEAR_ENTRIES) {
                mainUIRoomDatabase.mainUIDao().deleteAllEntries();
                String[] posters = strings[0];
                int index = 0;
                for (String poster : posters) {
                    MainUIEntry e = currentEntries.get(index++);
                    e.setPosters(poster);
                    long insertedId = mainUIRoomDatabase.mainUIDao().insertMoviePosters(e);
                    e.setUid(insertedId);
                }
            } else {
                currentEntries = new ArrayList<>();
                for (String s : strings[0]) {
                    MainUIEntry entry = new MainUIEntry();
                    entry.setPosters(s);
                    currentEntries.add(entry);
                    // Log.v("Testing", s);
                    long insertedId = mainUIRoomDatabase.mainUIDao().insertMoviePosters(entry);
                    entry.setUid(insertedId);
                }
            }
            return null;
        }

    }
}
