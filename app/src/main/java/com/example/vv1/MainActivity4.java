package com.example.vv1;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity4 extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView posterImageView;
    private TextView ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        titleTextView = findViewById(R.id.title);
        descriptionTextView = findViewById(R.id.description);
        posterImageView = findViewById(R.id.image);
        ratingTextView = findViewById(R.id.rating);

        TMDBService service = ApiClient.getClient().create(TMDBService.class);
        Call<MovieResponse> call = service.getPopularMovies("ваш_api_ключ");

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body().getResults().get(0);  // Получаем первый фильм из списка
                    titleTextView.setText(movie.getTitle());
                    descriptionTextView.setText(movie.getOverview());
                    ratingTextView.setText(String.valueOf(movie.getVoteAverage()));

                    // Загрузка изображения постера
                    String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
                    Picasso.get().load(posterUrl).into(posterImageView);

                    Log.d("API Response", "Title: " + movie.getTitle());
                    Log.d("API Response", "Overview: " + movie.getOverview());
                    Log.d("API Response", "Vote Average: " + movie.getVoteAverage());
                    Log.d("API Response", "Poster Path: " + movie.getPosterPath());
                } else {
                    Log.e("API Error", "Response not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage(), t);
            }
        });
    }
}