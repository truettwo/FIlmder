package com.example.vv1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainActivity4 extends AppCompatActivity {
    private ImageView imageView;
    private TextView titleView, descriptionView, ratingView;
    private Button buttonNo, buttonYes, buttonThatsIt;
    private List<Movie> movies;
    private List<Movie> likedMovies;
    private Set<Movie> dislikedMovies;
    private int currentIndex = 0;
    private OMDbApi omdbApi;
    private String apiKey = "50fcb30e"; // ваш реальный API-ключ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // Инициализация Timber


        imageView = findViewById(R.id.image);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        ratingView = findViewById(R.id.rating);
        buttonNo = findViewById(R.id.button_no);
        buttonYes = findViewById(R.id.button_yes);
        buttonThatsIt = findViewById(R.id.button_thats_it);

        likedMovies = new ArrayList<>();
        dislikedMovies = new HashSet<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        omdbApi = retrofit.create(OMDbApi.class);

        // Disable buttons initially
        buttonNo.setEnabled(false);
        buttonYes.setEnabled(false);

        fetchPopularMovies();

        buttonNo.setOnClickListener(v -> onDislike());
        buttonYes.setOnClickListener(v -> onLike());
        buttonThatsIt.setOnClickListener(v -> showCurrentMovieDetails());
    }

    private void fetchPopularMovies() {
        omdbApi.searchMovies(apiKey, "popular").enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movies = response.body().getSearch();
                    if (movies != null && !movies.isEmpty()) {
                        showMovie(movies.get(currentIndex));
                        // Enable buttons after movies are loaded
                        buttonNo.setEnabled(true);
                        buttonYes.setEnabled(true);
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

    private void showMovie(Movie movie) {
        omdbApi.getMovieDetails(apiKey, movie.getImdbID()).enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetails movieDetails = response.body();
                    titleView.setText(movieDetails.getTitle());
                    descriptionView.setText(movieDetails.getPlot());
                    String imdbRating = getImdbRating(movieDetails.getRatings());
                    ratingView.setText(imdbRating.isEmpty() ? "N/A" : imdbRating);
                    Picasso.get().load(movieDetails.getPoster()).into(imageView);
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

    private String getImdbRating(List<MovieDetails.Rating> ratings) {
        for (MovieDetails.Rating rating : ratings) {
            if ("Internet Movie Database".equals(rating.getSource())) {
                return rating.getValue();
            }
        }
        return "";
    }

    private void showNextMovie() {
        if (movies != null && !movies.isEmpty()) {
            currentIndex++;
            if (currentIndex >= movies.size()) {
                currentIndex = 0; // Циклически проходим по списку фильмов
            }
            if (!dislikedMovies.contains(movies.get(currentIndex))) {
                showMovie(movies.get(currentIndex));
            } else {
                showNextMovie();
            }
        } else {
            showError("No more movies to show.");
        }
    }

    private void onLike() {
        if (movies != null && !movies.isEmpty()) {
            likedMovies.add(movies.get(currentIndex)); // Добавляем понравившийся фильм в список
            recommendMovies(); // Пробуем показать рекомендованные фильмы
        }
    }

    private void onDislike() {
        if (movies != null && !movies.isEmpty()) {
            dislikedMovies.add(movies.get(currentIndex)); // Добавляем не понравившийся фильм в список
            showNextMovie();
        }
    }

    private void recommendMovies() {
        if (!likedMovies.isEmpty()) {
            // Создаем список для хранения рекомендованных фильмов
            List<Movie> recommendedMovies = new ArrayList<>();

            for (Movie likedMovie : likedMovies) {
                // Например, добавляем фильмы того же года, что и понравившийся фильм
                String likedYear = likedMovie.getYear();
                for (Movie movie : movies) {
                    if (!likedMovies.contains(movie) && !dislikedMovies.contains(movie) && movie.getYear().equals(likedYear)) {
                        recommendedMovies.add(movie);
                    }
                }
            }

            // Если рекомендованных фильмов достаточно, перемешаем их и покажем
            if (!recommendedMovies.isEmpty()) {
                movies = recommendedMovies;
                currentIndex = 0;
                showMovie(movies.get(currentIndex));
            } else {
                showError("No recommendations available.");
                showNextMovie(); // Покажем следующий случайный фильм, если нет рекомендаций
            }
        } else {
            showNextMovie(); // Покажем следующий случайный фильм, если нет понравившихся
        }
    }

    private void showCurrentMovieDetails() {
        if (movies != null && !movies.isEmpty()) {
            Movie currentMovie = movies.get(currentIndex);
            Toast.makeText(this, "You selected: " + currentMovie.getTitle(), Toast.LENGTH_SHORT).show();
            // Implement what happens when the user clicks "Это!!!"
            // For example, navigate to a new activity with detailed information
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}