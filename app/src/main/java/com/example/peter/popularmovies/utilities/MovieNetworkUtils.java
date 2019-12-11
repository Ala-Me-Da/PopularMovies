package com.example.peter.popularmovies.utilities;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.peter.popularmovies.database.MoviesRoomDatabase;
import com.example.peter.popularmovies.database.UserFavoritesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * Network Utilities Class to handle all JSON parsing, and Networking operations necessary
 * to fetch and parse data from the themoviesDB website.
 */
public final class MovieNetworkUtils {
    /**
     * Constant string of class name for Log debugging. Left in for code reviewers if needed.
     */
    private static final String TAG = MovieNetworkUtils.class.getSimpleName();

    /**
     *
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START HERE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     * For anyone using this code, hardcode your themovieDB API key here before running the app.
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START HERE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     */
    private static final String API_KEY = "YOUR_API_KEY_HERE";

    /**
     * TODO (1) Helper String Constants for Networking
     * String constants of /movie/popular and /movie/top_rated APIs for MovieNetworkUtils methods.
     */
    private static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?"
            + "api_key=" + API_KEY;
    private static final String TOP_RATED_MOVIES_URL = "https://api.themoviedb.org/3/movie/top_rated?"
            + "api_key=" + API_KEY;
    private static final String MOVIESDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    /**
     * JSONArray that holds the movie poster path, movie title data, movie vote average, plot synopsis,
     * and release date data.
     */
    private static JSONArray moviesResultsJsonArray;

    /**
     * String constants of necessary JSON keys to access movie data stored in the results JSON array
     */
    private static final String TITLE_KEY = "title";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String PLOT_SYNOPSIS_KEY = "overview";
    private static final String RELEASE_DATA_KEY = "release_date";

    /**
     * String constants to build the URL to access movie data.
     */
    private static final String RESULTS_KEY = "results";
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String BASE_URL_FOR_POSTER_PATH = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    /**
     * Building blocks to the movie trailer urls
     */
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String MOVIE_TRAILER_KEY = "key";
    private static final String MOVIE_ID_KEY = "id";

    /*
     * Building block(s) for the movie review urls, i.e. json keys to important data.
     */
    private static final String URL_KEY = "url";

    /**
     * Integers are used to represent a user's sorting options
     * Top Rated = 1
     * Most Popular = 0 (
     */
    private static final int MOST_POPULAR_OPTION = 0;
    private static final int TOP_RATED_OPTION = 1;

    private static String movieUID;

    /**
     * Collection of methods to retrieve movie title, release date, vote average, and synopsis
     *
     * @param position Location of movie in the results JSONArray.
     * @return A String to return the above data as a String to populate a Movie Detail TextView.
     */
    public static String parseMovieTitleFromJson(int position) {
        return moviesResultsJsonArray.optJSONObject(position).optString(TITLE_KEY);
    }

    public static String parseMovieReleaseDataFromJson(int position) {
        return moviesResultsJsonArray.optJSONObject(position).optString(RELEASE_DATA_KEY);
    }

    public static String parseMovieVoteAverageFromJson(int position) {
        return moviesResultsJsonArray.optJSONObject(position).optString(VOTE_AVERAGE_KEY);
    }

    public static String parseMoviePlotSynopsisFromJson(int position) {
        return moviesResultsJsonArray.optJSONObject(position).optString(PLOT_SYNOPSIS_KEY);
    }

    public static int getMovieUID() { return Integer.parseInt(movieUID); }

    /** Run this method on a background thread */
    public static String[] retrieveMovieTrailerVideos(int position) {
        String movieId = moviesResultsJsonArray.optJSONObject(position).optString(MOVIE_ID_KEY);
        String linkToMovieTrailer = MOVIESDB_BASE_URL + movieId + "/videos?api_key=" + API_KEY;
        movieUID = movieId;

        String movieTrailerJsonString;
        JSONObject movieTrailerJson;
        JSONArray resultsArray;
        String[] youtubeLinks = null;
        try {
            movieTrailerJsonString = getMovieDataJsonFromHttpsUrl(-1, linkToMovieTrailer);
            movieTrailerJson = new JSONObject(movieTrailerJsonString);
            resultsArray = movieTrailerJson.optJSONArray(RESULTS_KEY);

            youtubeLinks = new String[resultsArray.length()];
            for(int i = 0; i < resultsArray.length(); i++) {
                String videoKey = resultsArray.optJSONObject(i).optString(MOVIE_TRAILER_KEY);
                youtubeLinks[i] = BASE_YOUTUBE_URL + videoKey;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return youtubeLinks;
    }

    public static String retrieveMovieReviews(int position) {
            String movieId = moviesResultsJsonArray.optJSONObject(position).optString(MOVIE_ID_KEY);
            String linkToMovieReviews = MOVIESDB_BASE_URL + movieId + "/reviews?api_key=" + API_KEY;

            String movieReviewJsonString;
            JSONObject movieReviewEndpointJson;
            JSONObject movieReviewJsonObject;
            String reviewLink = null;
            try {
                movieReviewJsonString = getMovieDataJsonFromHttpsUrl(-1,
                        linkToMovieReviews);
                movieReviewEndpointJson = new JSONObject(movieReviewJsonString);

                movieReviewJsonObject = movieReviewEndpointJson.optJSONArray(RESULTS_KEY)
                        .optJSONObject(0);  // Array of size one, only index is 0

                reviewLink = (movieReviewJsonObject != null) ? movieReviewJsonObject
                        .optString(URL_KEY) : null;
            } catch(IOException e) {
                e.printStackTrace();
            } catch(JSONException e) {
                e.printStackTrace();
            }


            return reviewLink;
    }

    /**
     * TODO (4) app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu (Helper method)
     * Method to Store JSON data into a JSONObject, store movies results into a JSONArray, then build the poster path for each movies in the JSONArray.
     *
     * @param sortOption For use in the buildUrl(sortOption) method.
     * @return String array holding all movie poster paths to each movie retrived from moviesDB.
     */
    public static String[] buildPosterPathUrls(int sortOption) {
        String[] popularMoviesPosterImageUrls = null;
        try {
            String moviesJsonString = getMovieDataJsonFromHttpsUrl(sortOption, null);
            JSONObject moviesJson = new JSONObject(moviesJsonString);
            moviesResultsJsonArray = moviesJson.optJSONArray(RESULTS_KEY);
            int numberOfPopularMovies = moviesResultsJsonArray.length();

            popularMoviesPosterImageUrls = new String[numberOfPopularMovies];
            for (int i = 0; i < numberOfPopularMovies; i++) {
                String posterPath = moviesResultsJsonArray.optJSONObject(i).getString(POSTER_PATH_KEY);
                popularMoviesPosterImageUrls[i] = BASE_URL_FOR_POSTER_PATH + POSTER_SIZE + posterPath;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return popularMoviesPosterImageUrls;
    }

    public static String[] getFavoritesFromRoom(Context context) {
        MoviesRoomDatabase roomDatabase = MoviesRoomDatabase.getInstance(context);
        List<UserFavoritesEntry> allEntries = roomDatabase.userFavoritesDao()
                .getAllUserFavorites();
        String[] moviePosterURLs = new String[allEntries.size()];
        int index = 0;
            for (UserFavoritesEntry entry : allEntries) {
                String moviePosterURL = entry.getMovieURL();
                moviePosterURLs[index++] = moviePosterURL;
            }
        return moviePosterURLs;
    }


    /** TODO (2) app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu
     *
     * @param sortOption A 1 (TOP_RATED_OPTION) or a 0 (else / MOST_POPULAR) specifying sort option.
     * @return A moviesDB URL to fetch the Most Popular movies or the Top Rated Movies
     */
    private static URL buildUrl(int sortOption) {
        URL moviesJsonUrl = null;
        try {
            switch (sortOption) {
                case MOST_POPULAR_OPTION:
                    moviesJsonUrl = new URL(POPULAR_MOVIES_URL);
                    break;
                case TOP_RATED_OPTION:
                    moviesJsonUrl = new URL(TOP_RATED_MOVIES_URL);
                    break;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return moviesJsonUrl;
    }

    /** TODO(3) app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu (Helper method)
     * Credits: Used the http querying technique from NetworkUtils.java taught in Udacity course exercises.
     * @param sortOption For use in the buildUrl(sortOption) method.
     * @return A String containing all JSON about Most Popular movies or Top Rated movies.
     * @throws IOException throws exception if http request fails.
     */
    private static String getMovieDataJsonFromHttpsUrl(int sortOption, String otherUrl) throws IOException {
        URL url = ( otherUrl != null ? new URL(otherUrl) : buildUrl(sortOption) );
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasNext = scanner.hasNext();
            return hasNext ? scanner.next() : null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
