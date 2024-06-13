package com.example.vv1.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.vv1.controller.MovieArrayAdapter;
import com.example.vv1.R;
import com.example.vv1.model.Movie;
import com.example.vv1.model.MovieDetails;
import com.example.vv1.model.MovieResponse;
import com.example.vv1.model.OMDbApi;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SwipeAloneActivity extends AppCompatActivity {

    private SwipeFlingAdapterView swipeView;
    private MovieArrayAdapter movieAdapter;
    private List<Movie> movies;
    private OMDbApi omdbApi;
    private String apiKey = "50fcb30e"; // ваш реальный API-ключ

    private Button buttonNo, buttonYes, buttonThatsIt;
    private Map<String, int[]> movieCoordinates; // Map to store movie coordinates
    private int[] currentCoordinates; // Current coordinates of the user
    private Movie currentMovie; // Currently displayed movie

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

        // Initialize movie coordinates map
        initializeMovieCoordinates();

        swipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                if (!movies.isEmpty()) {
                    movies.remove(0);
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                updateCoordinates(-1, -1); // Move coordinates left-down
                findNextMovie();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                updateCoordinates(1, 1); // Move coordinates right-up
                findNextMovie();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {}

            @Override
            public void onScroll(float scrollProgressPercent) {}
        });

        buttonNo.setOnClickListener(v -> swipeView.getTopCardListener().selectLeft());
        buttonYes.setOnClickListener(v -> swipeView.getTopCardListener().selectRight());
        buttonThatsIt.setOnClickListener(v -> showCurrentMovieDetails());

        // Start by showing the first movie
        fetchMovies();
    }

    private void initializeMovieCoordinates() {
        movieCoordinates = new HashMap<>();
        movieCoordinates.put("Forrest Gump", new int[]{8, -2});
        movieCoordinates.put("Inception", new int[]{5, 8});
        movieCoordinates.put("The Shawshank Redemption", new int[]{7, -5});
        movieCoordinates.put("The Lord of the Rings: The Fellowship of the Ring", new int[]{3, 9});
        movieCoordinates.put("Pulp Fiction", new int[]{5, -3});
        movieCoordinates.put("The Matrix", new int[]{2, 7});
        movieCoordinates.put("The Dark Knight", new int[]{7, 3});
        movieCoordinates.put("Interstellar", new int[]{5, 6});
        movieCoordinates.put("The Godfather", new int[]{6, -4});
        movieCoordinates.put("Inglourious Basterds", new int[]{4, -1});
        movieCoordinates.put("In Bruges", new int[]{3, -4});
        movieCoordinates.put("Eternal Sunshine of the Spotless Mind", new int[]{6, -6});
        movieCoordinates.put("Amélie", new int[]{8, -7});
        movieCoordinates.put("The Grand Budapest Hotel", new int[]{9, -1});
        movieCoordinates.put("The Truman Show", new int[]{5, -6});
        movieCoordinates.put("Up", new int[]{8, 9});
        movieCoordinates.put("The Big Lebowski", new int[]{6, -2});
        movieCoordinates.put("The Silence of the Lambs", new int[]{5, -8});
        movieCoordinates.put("How to Train Your Dragon", new int[]{7, 7});
        movieCoordinates.put("Star Wars: Episode IV - A New Hope", new int[]{6, 5});

        // Initialize the starting coordinates (example: starting with Forrest Gump)
        currentCoordinates = new int[]{8, -2};
        currentMovie = new Movie("Forrest Gump", "SomeID"); // Initialize with the starting movie
    }

    private void updateCoordinates(int dx, int dy) {
        currentCoordinates[0] += dx;
        currentCoordinates[1] += dy;
    }

    private void findNextMovie() {
        Movie closestMovie = null;
        double closestDistance = Double.MAX_VALUE;

        for (Map.Entry<String, int[]> entry : movieCoordinates.entrySet()) {
            double distance = calculateDistance(currentCoordinates, entry.getValue());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestMovie = new Movie(entry.getKey(), "SomeID");
            }
        }

        if (closestMovie != null) {
            fetchNextMovieDetails(closestMovie); // Fetch details of the next recommended movie
        }
    }

    private double calculateDistance(int[] coords1, int[] coords2) {
        return Math.sqrt(Math.pow(coords1[0] - coords2[0], 2) + Math.pow(coords1[1] - coords2[1], 2));
    }

    private void fetchMovies() {
        // Список популярных фильмов
        String[] popularMovies = {"Forrest Gump", "Inception", "The Shawshank Redemption", "The Lord of the Rings: The Fellowship of the Ring",
                "Pulp Fiction", "The Matrix", "The Dark Knight", "Interstellar", "The Godfather", "Inglourious Basterds", "In Bruges",
                "Eternal Sunshine of the Spotless Mind", "Amélie", "The Grand Budapest Hotel", "The Truman Show", "Up", "The Big Lebowski",
                "The Silence of the Lambs", "How to Train Your Dragon", "Star Wars: Episode IV - A New Hope"};

        for (String movieTitle : popularMovies) {
            omdbApi.searchMovies(apiKey, movieTitle).enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> fetchedMovies = response.body().getSearch();
                        if (fetchedMovies != null && !fetchedMovies.isEmpty()) {
                            // Сначала добавляем фильмы в список
                            Movie movie = fetchedMovies.get(0);
                            // Затем запрашиваем детали фильма
                            fetchMovieDetails(movie);
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

    private void fetchMovieDetails(Movie movie) {
        omdbApi.getMovieDetails(apiKey, movie.getImdbID()).enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetails details = response.body();
                    movie.setDescription(details.getPlot());
                    movie.setRating(getImdbRating(details.getRatings()));
                    movies.add(movie);
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
    }

    private void fetchNextMovieDetails(Movie nextMovie) {
        omdbApi.searchMovies(apiKey, nextMovie.getTitle()).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getSearch();
                    if (fetchedMovies != null && !fetchedMovies.isEmpty()) {
                        // В данном примере предполагаем, что поиск по названию вернет один фильм
                        Movie detailedMovie = fetchedMovies.get(0);
                        fetchMovieDetails(detailedMovie); // Fetch detailed info for the next movie
                    } else {
                        showError("No movies found.");
                    }
                } else {
                    Timber.e("Response error: %s", response.errorBody());
                    showError("Failed to fetch movie details. Response error.");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Timber.e(t, "Failed to fetch movie details");
                showError("Failed to fetch movie details. Network error.");
            }
        });
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

    private String getImdbRating(List<MovieDetails.Rating> ratings) {
        if (ratings == null) {
            return "N/A";
        }
        for (MovieDetails.Rating rating : ratings) {
            if ("Internet Movie Database".equals(rating.getSource())) {
                return rating.getValue();
            }
        }
        return "N/A";
    }
}