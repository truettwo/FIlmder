package com.example.vv1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

public class SwipeAloneActivity extends AppCompatActivity {

    private SwipeFlingAdapterView swipeView;
    private MovieArrayAdapter movieAdapter;
    private List<Movie> movies;
    private OMDbApi omdbApi;
    private String apiKey = "50fcb30e"; // ваш реальный API-ключ

    private Button buttonNo, buttonYes, buttonThatsIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_alone);

        swipeView = findViewById(R.id.swipe_view);
        buttonNo = findViewById(R.id.button_no);
        buttonYes = findViewById(R.id.button_yes);
        buttonThatsIt = findViewById(R.id.button_thats_it);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        omdbApi = retrofit.create(OMDbApi.class);

        movies = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movies);
        swipeView.setAdapter(movieAdapter);

        swipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                movies.remove(0);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Toast.makeText(SwipeAloneActivity.this, "No", Toast.LENGTH_SHORT).show();
                showNextMovie();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(SwipeAloneActivity.this, "Yes", Toast.LENGTH_SHORT).show();
                showNextMovie();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {}

            @Override
            public void onScroll(float scrollProgressPercent) {}
        });

        buttonNo.setOnClickListener(v -> swipeView.getTopCardListener().selectLeft());
        buttonYes.setOnClickListener(v -> swipeView.getTopCardListener().selectRight());
        buttonThatsIt.setOnClickListener(v -> showCurrentMovieDetails());

        fetchMovies();
    }

    private void fetchMovies() {
        // Список популярных фильмов
        String[] popularMovies = {"The Dark Knight"};

        for (String movieTitle : popularMovies) {
            omdbApi.searchMovies(apiKey, movieTitle).enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movies.addAll(response.body().getSearch());
                        if (!movies.isEmpty()) {
                            movieAdapter.notifyDataSetChanged();
                        } else {
                            showError("No movies found.");
                        }
                    } else {
                        Timber.e("Response error: %s", response.errorBody());
                        showError("Failed to fetch movies. Response error.");
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Timber.e(t, "Failed to fetch movies");
                    showError("Failed to fetch movies. Network error.");
                }
            });
        }
    }

    private void showNextMovie() {
        if (!movies.isEmpty()) {
            Movie movie = movies.get(0);
            omdbApi.getMovieDetails(apiKey, movie.getImdbID()).enqueue(new Callback<MovieDetails>() {
                @Override
                public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MovieDetails movieDetails = response.body();
                        movie.setTitle(movieDetails.getTitle());
                        movie.setDescription(movieDetails.getPlot());
                        movie.setPoster(movieDetails.getPoster());
                        movieAdapter.notifyDataSetChanged();
                    } else {
                        Timber.e("Response error: %s", response.errorBody());
                        showError("Failed to fetch movie details. Response error.");
                    }
                }

                @Override
                public void onFailure(Call<MovieDetails> call, Throwable t) {
                    Timber.e(t, "Failed to fetch movie details");
                    showError("Failed to fetch movie details. Network error.");
                }
            });
        } else {
            showError("No more movies to show.");
        }
    }

    private void showCurrentMovieDetails() {
        if (!movies.isEmpty()) {
            Movie currentMovie = movies.get(0);
            Toast.makeText(this, "You selected: " + currentMovie.getTitle(), Toast.LENGTH_SHORT).show();
            // Implement what happens when the user clicks "Это!!!"
            // For example, navigate to a new activity with detailed information
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}