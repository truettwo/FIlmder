package com.example.vv1.model;

import com.example.vv1.model.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("Search")
    private List<Movie> search;

    public List<Movie> getSearch() {
        return search;
    }
}