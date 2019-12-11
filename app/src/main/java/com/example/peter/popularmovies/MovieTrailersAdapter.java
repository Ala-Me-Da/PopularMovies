package com.example.peter.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peter.popularmovies.utilities.MovieNetworkUtils;

public class MovieTrailersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MOVIE_DETAILS_LAYOUT = 0;
    private static final int MOVIE_TRAILERS_LAYOUT = 1;

    private MovieTrailersClickHandler movieDetailActivity;
    private String[] mMovieTrailersYoutubeLinks;
    private String mMovieReviewsLink;
    private String[] mMovieDetails;

    public interface MovieTrailersClickHandler {
        void onClick(String youtubeUrl, String reviewUrl);
    }

    public MovieTrailersAdapter(MovieTrailersClickHandler clickHandler, String[] youtubeLinks,
                                String reviewLinks, String[] movieDetails) {
        movieDetailActivity = clickHandler;
        mMovieTrailersYoutubeLinks = youtubeLinks;
        mMovieDetails = movieDetails;
        mMovieReviewsLink = reviewLinks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType == MOVIE_TRAILERS_LAYOUT ) {
            /* Display List view of all the movie's trailers */
            Context context = viewGroup.getContext();
            int layoutId = R.layout.linear_rv_movie_trailers;
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(layoutId, viewGroup, false);
            return new MovieTrailersViewHolder(view);
        } else {
            /* Display other information about movie: synopsis, rating, duration, release year */
            Context context = viewGroup.getContext();
            int layoutId = R.layout.movie_details;
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(layoutId, viewGroup, false);
            return new MovieDetailsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
       if (viewType != 0) {
            MovieTrailersViewHolder trailersViewHolder = (MovieTrailersViewHolder) holder;
            trailersViewHolder.playButton.setImageResource(R.drawable.play_button_small);
            String trailerName = "Trailer " + position;
            trailersViewHolder.trailerName.setText(trailerName);
        } else {
            MovieDetailsViewHolder movieDetailsViewHolder = (MovieDetailsViewHolder) holder;
            movieDetailsViewHolder.movieReleaseData.setText(mMovieDetails[2]);
            movieDetailsViewHolder.movieVoteAverage.setText(mMovieDetails[1]);
            movieDetailsViewHolder.movieSynopsis.setText(mMovieDetails[0]);
            movieDetailsViewHolder.movieReviews.setText(R.string.reviews);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieTrailersYoutubeLinks != null ? mMovieTrailersYoutubeLinks.length : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position != 0 ? MOVIE_TRAILERS_LAYOUT : MOVIE_DETAILS_LAYOUT;
    }



    public class MovieTrailersViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private TextView trailerName;
        private ImageView playButton;


        MovieTrailersViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.trailer_tv);
            playButton = itemView.findViewById(R.id.play_button);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            // Position for recycler view in Movie Detail activity.
            int adapterPosition = getAdapterPosition();
            String youtubeLink = mMovieTrailersYoutubeLinks[adapterPosition];
            movieDetailActivity.onClick(youtubeLink, null);
        }
    }

    public class MovieDetailsViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView movieReleaseData;
        private TextView movieVoteAverage;
        private TextView movieSynopsis;
        private TextView movieReviews;

        MovieDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            movieReleaseData = itemView.findViewById(R.id.movie_release_data);
            movieVoteAverage = itemView.findViewById(R.id.movie_vote_avg);
            movieSynopsis = itemView.findViewById(R.id.movie_synopsis);
            movieReviews = itemView.findViewById(R.id.movie_reviews);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            movieDetailActivity.onClick(null, mMovieReviewsLink);
        }
    }
}
