package com.example.vv1.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieDetails {
    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Poster")
    private String poster;

    @SerializedName("Plot")
    private String plot;

    @SerializedName("Ratings")
    private List<Rating> ratings;

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getPoster() {
        return poster;
    }

    public String getPlot() {
        return plot;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public static class Rating {
        @SerializedName("Source")
        private String source;

        @SerializedName("Value")
        private String value;

        public String getSource() {
            return source;
        }

        public String getValue() {
            return value;
        }
    }
}