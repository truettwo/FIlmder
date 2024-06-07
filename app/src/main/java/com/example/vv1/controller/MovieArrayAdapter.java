package com.example.vv1.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vv1.R;
import com.example.vv1.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private Context context;
    private List<Movie> movies;

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.item_movie, movies);
        this.context = context;
        this.movies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        TextView titleView = convertView.findViewById(R.id.title);
        TextView descriptionView = convertView.findViewById(R.id.description);
        TextView ratingView = convertView.findViewById(R.id.rating);

        titleView.setText(movie.getTitle());
        descriptionView.setText(movie.getDescription() != null ? movie.getDescription() : "No description available");
        ratingView.setText(movie.getRating() != null ? movie.getRating() : "N/A");

        Picasso.get().load(movie.getPoster()).into(imageView);

        return convertView;
    }
}