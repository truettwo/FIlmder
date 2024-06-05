package com.example.vv1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OMDbApi {
    @GET("/")
    Call<MovieResponse> searchMovies(@Query("apikey") String apiKey, @Query("s") String query);

    @GET("/")
    Call<MovieDetails> getMovieDetails(@Query("apikey") String apiKey, @Query("i") String imdbID);
}