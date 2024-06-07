package com.example.vv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        TextView titleView = convertView.findViewById(R.id.title);
        TextView descriptionView = convertView.findViewById(R.id.description);
        TextView ratingView = convertView.findViewById(R.id.rating);

        titleView.setText(movie.getTitle());
        descriptionView.setText(movie.getYear()); // Assuming you want to show the year as description
        ratingView.setText("N/A"); // Placeholder as no rating is in the Movie class
        Picasso.get().load(movie.getPoster()).into(imageView);

        return convertView;
    }
}