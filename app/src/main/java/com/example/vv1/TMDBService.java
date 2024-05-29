package com.example.vv1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("6cca2127ce122aa9be379c2a829a6f6f") String apiKey);
}