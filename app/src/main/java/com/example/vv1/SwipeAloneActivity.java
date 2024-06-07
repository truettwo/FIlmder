package com.example.vv1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class SwipeAloneActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleView, descriptionView, ratingView;
    private Button buttonNo, buttonYes, buttonThatsIt;
    private List<Movie> movies;
    private int currentIndex = 0;
    private OMDbApi omdbApi;
    private String apiKey = "50fcb30e"; // ваш реальный API-ключ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_alone);

        // Инициализация Timber


        imageView = findViewById(R.id.image);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        ratingView = findViewById(R.id.rating);
        buttonNo = findViewById(R.id.button_no);
        buttonYes = findViewById(R.id.button_yes);
        buttonThatsIt = findViewById(R.id.button_thats_it);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        omdbApi = retrofit.create(OMDbApi.class);

        // Disable buttons initially
        buttonNo.setEnabled(false);
        buttonYes.setEnabled(false);

        fetchMovies();

        buttonNo.setOnClickListener(v -> showNextMovie());
        buttonYes.setOnClickListener(v -> showNextMovie());
        buttonThatsIt.setOnClickListener(v -> showCurrentMovieDetails());
    }

    private void fetchMovies() {
        // OMDb API does not provide a popular movies endpoint, so we'll use a search query
        omdbApi.searchMovies(apiKey, "hero").enqueue(new Callback<MovieResponse>() {
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
        if (movies != null && !movies.isEmpty() && currentIndex < movies.size() - 1) {
            currentIndex++;
            showMovie(movies.get(currentIndex));
        } else {
            showError("No more movies to show.");
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