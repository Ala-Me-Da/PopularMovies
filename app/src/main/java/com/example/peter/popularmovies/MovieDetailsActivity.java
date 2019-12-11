package com.example.peter.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.peter.popularmovies.database.MoviesRoomDatabase;
import com.example.peter.popularmovies.database.UserFavoritesDao;
import com.example.peter.popularmovies.database.UserFavoritesEntry;
import com.example.peter.popularmovies.utilities.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

/** TODO (5) Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
 *  TODO (6) UI contains a screen for displaying the details for a selected movie.
 * Public class to load & display all movie details fetched from movieDB.
 */
public class MovieDetailsActivity extends AppCompatActivity implements
        MovieTrailersAdapter.MovieTrailersClickHandler {

    @BindView(R.id.movie_trailers_rv)
    RecyclerView recyclerView;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private boolean wasClicked;
    private int thisActivitiesPos;
    private LinearLayoutManager linearLayoutManager;
    private String[] movieDetailsFromIntent;
    private MoviesRoomDatabase moviesRoomDatabase;

    private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // setTitle(R.string.details_app_name);
        ButterKnife.bind(this);

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
        supportPostponeEnterTransition();



       // TextView title = findViewById(R.id.title);
        // title.setText("Item");

        moviesRoomDatabase = MoviesRoomDatabase.getInstance(getApplicationContext());

        /**
         * Receives intent data passed by the Main Activity UI
         */
        Intent intent = getIntent();
        final String movieUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        movieDetailsFromIntent = intent.getStringArrayExtra("movieDetails");
        int pos = intent.getIntExtra(Intent.ACTION_VIEW, -1);
        thisActivitiesPos = pos;

        loadPrefs();


        final ImageView image = findViewById(R.id.image);
        Picasso.get()
                .load(movieUrl)
                .into(image);

        final String movieTitle = MovieNetworkUtils.parseMovieTitleFromJson(pos);
        // collapsingToolbarLayout.setTitle(movieTitle);
        setTitle(movieTitle);
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.
                getColor(this, android.R.color.transparent));

        setUpRecyclerView(pos);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wasClicked) {
                    fab.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
                    wasClicked = false;
                    int movieUID = MovieNetworkUtils.getMovieUID();
                    UserFavoritesEntry entry = new UserFavoritesEntry();
                    entry.setMovieID(movieUID);
                    entry.setMovieTitle(movieTitle);
                    entry.setMovieURL(movieUrl);
                    DatabaseQueryingTask queryingTask = new DatabaseQueryingTask();
                    queryingTask.execute(entry);
                } else {
                    fab.setImageResource(R.drawable.baseline_favorite_black_18dp);
                    wasClicked = true;
                    int movieUID = MovieNetworkUtils.getMovieUID();
                    UserFavoritesEntry entry = new UserFavoritesEntry();
                    entry.setMovieID(movieUID);
                    entry.setMovieTitle(movieTitle);
                    entry.setMovieURL(movieUrl);
                    DatabaseQueryingTask queryingTask = new DatabaseQueryingTask();
                    queryingTask.execute(entry);
                }
        }

        });

    }

    @Override
    public void onClick(String youtubeUrl, String reviewUrl) {
        String url = (youtubeUrl != null) ? youtubeUrl : reviewUrl;
        if(reviewUrl == null) {
            Toast.makeText(this, "No reviews found.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        Intent intentClient = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intentClient);
    }

    /** On Click method for Movie Reviews */
    private void setUpRecyclerView(int position) {
        /* Set up Movie Detail ListView through the RecyclerView. */
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);

        String[] youtubeLinks = null;
        String reviewLinks = null;
        try {
            youtubeLinks = new MovieDetailTask().execute(position).get();
            reviewLinks = new GetMovieReviewsTask().execute(position).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.getStackTrace();
        }

        MovieTrailersAdapter adapter = new MovieTrailersAdapter(this, youtubeLinks,
                reviewLinks, movieDetailsFromIntent);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStop() {
        savePrefs();
        super.onStop();
    }

    private void savePrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(thisActivitiesPos + "",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("wc", wasClicked);
        editor.apply();
    }

    private void loadPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(thisActivitiesPos + "",
                MODE_PRIVATE);
        Boolean clicked = sharedPreferences.getBoolean("wc", false);
        wasClicked = clicked;
        if(clicked) {
            fab.setImageResource(R.drawable.baseline_favorite_black_18dp);
        }
    }

    public class DatabaseQueryingTask extends AsyncTask<UserFavoritesEntry, Void, Void> {
        @Override
        protected Void doInBackground(UserFavoritesEntry... userFavoritesEntries) {
           if(wasClicked) {
               moviesRoomDatabase.userFavoritesDao().insertMovie(userFavoritesEntries[0]);
           } else {
               moviesRoomDatabase.userFavoritesDao().deleteMovie(userFavoritesEntries[0]);

           }

           return null;
        }
    }

    public class GetMovieReviewsTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... positions) {
            return MovieNetworkUtils.retrieveMovieReviews(positions[0]);
        }
    }


    public class MovieDetailTask extends AsyncTask<Integer, Void, String[]> {
        @Override
        protected String[] doInBackground(Integer... positions) {
            return MovieNetworkUtils.retrieveMovieTrailerVideos(positions[0]);
        }
    }

}
