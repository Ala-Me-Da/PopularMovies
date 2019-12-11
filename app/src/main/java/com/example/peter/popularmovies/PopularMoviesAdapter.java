package com.example.peter.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {
        private String[] popularMoviesPosterImageUrls;
        private PopularMoviesClickHandler mainActivity;

        @SuppressWarnings("UnnecessaryInterfaceModifier")
        public interface PopularMoviesClickHandler {
            public void onClick(String movieUrl, int adapterPosition);
        }

        public PopularMoviesAdapter(PopularMoviesClickHandler clickHandler, String[] urls) {
            boolean isNull = (urls == null);
            Log.v("Dude", "is urls null?" + Boolean.toString(isNull));
            mainActivity = clickHandler;
            popularMoviesPosterImageUrls = urls;
        }

        @Override
        @NonNull
        public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForGridView = R.layout.grid_rv_layout;
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(layoutIdForGridView, viewGroup, false);
            return new PopularMoviesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PopularMoviesViewHolder holder, int position) {
            Log.v("Dude", "check: " + popularMoviesPosterImageUrls[position]);
            Picasso.get()
                    .load(popularMoviesPosterImageUrls[position])
                    .error(R.drawable.no_image_error)
                    .placeholder(R.drawable.ajax_loader_symbol)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return popularMoviesPosterImageUrls != null ? popularMoviesPosterImageUrls.length : 0;
        }

        public class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            public final ImageView imageView;

            PopularMoviesViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.movie_poster_image_view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                String moviePosterUrl = popularMoviesPosterImageUrls[adapterPosition];
                mainActivity.onClick(moviePosterUrl, adapterPosition);
            }
        }
}
