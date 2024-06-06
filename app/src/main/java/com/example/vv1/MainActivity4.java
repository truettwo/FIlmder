package com.example.vv1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
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
    private List<String> likedMovies;
    private Set<String> dislikedMovies;
    private int currentIndex = 0;
    private OMDbApi omdbApi;
    private String apiKey = "50fcb30e"; // ваш реальный API-ключ
    private FirebaseHelper firebaseHelper;
    private String userId;

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
        userId = UUID.randomUUID().toString(); // Уникальный идентификатор пользователя
        firebaseHelper = new FirebaseHelper(userId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        omdbApi = retrofit.create(OMDbApi.class);

        // Disable buttons initially
        buttonNo.setEnabled(false);
        buttonYes.setEnabled(false);

        fetchComedyMovies();

        buttonNo.setOnClickListener(v -> onDislike());
        buttonYes.setOnClickListener(v -> onLike());
        buttonThatsIt.setOnClickListener(v -> showCurrentMovieDetails());

        // Добавляем слушателя на изменения данных о лайкнутых фильмах
        firebaseHelper.setMovieMatchListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (!userSnapshot.getKey().equals(userId)) {
                        for (DataSnapshot movieSnapshot : userSnapshot.getChildren()) {
                            String movieTitle = movieSnapshot.getKey();
                            Boolean isLiked = movieSnapshot.getValue(Boolean.class);
                            if (isLiked != null && isLiked && likedMovies.contains(movieTitle)) {
                                Toast.makeText(MainActivity4.this, "Совпадение: " + movieTitle, Toast.LENGTH_SHORT).show();
                                firebaseHelper.removeLikedMovie(movieTitle); // Удаляем фильм после совпадения
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e(databaseError.toException(), "Failed to listen for movie matches");
            }
        });
    }

    private void fetchComedyMovies() {
        omdbApi.searchMovies(apiKey, "comedy").enqueue(new Callback<MovieResponse>() {
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
                        showError("No comedy movies found.");
                    }
                } else {
                    Timber.e("Response error: %s", response.errorBody());
                    showError("Failed to fetch comedy movies. Response error.");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Timber.e(t, "Failed to fetch comedy movies");
                showError("Failed to fetch comedy movies. Network error.");
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
            if (!dislikedMovies.contains(movies.get(currentIndex).getTitle())) {
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
            String likedMovieTitle = movies.get(currentIndex).getTitle();
            likedMovies.add(likedMovieTitle); // Добавляем понравившийся фильм в список
            firebaseHelper.addLikedMovie(likedMovieTitle);
            checkForMatch(likedMovieTitle);
        }
    }

    private void checkForMatch(String movieTitle) {
        firebaseHelper.checkForMatch(movieTitle, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean matchFound = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (!userSnapshot.getKey().equals(userId)) {
                        for (DataSnapshot movieSnapshot : userSnapshot.getChildren()) {
                            if (movieTitle.equals(movieSnapshot.getKey())) {
                                matchFound = true;
                                break;
                            }
                        }
                    }
                }
                if (matchFound) {
                    Toast.makeText(MainActivity4.this, "Совпадение: " + movieTitle, Toast.LENGTH_SHORT).show();
                    firebaseHelper.removeLikedMovie(movieTitle);
                }
                showNextMovie();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e(databaseError.toException(), "Failed to check for movie match");
                showNextMovie();
            }
        });
    }

    private void onDislike() {
        if (movies != null && !movies.isEmpty()) {
            String dislikedMovieTitle = movies.get(currentIndex).getTitle();
            dislikedMovies.add(dislikedMovieTitle);
            showNextMovie();
        }
    }

    private void showCurrentMovieDetails() {
        if (movies != null && !movies.isEmpty()) {
            Movie currentMovie = movies.get(currentIndex);
            // Implement what happens when the user clicks "Это!!!"
            // For example, navigate to a new activity with detailed information
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}