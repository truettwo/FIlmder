package com.example.vv1;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class FirebaseHelper {

    private DatabaseReference databaseRef;
    private String userId;

    public FirebaseHelper(String userId) {
        this.userId = userId;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("connections");
    }

    public String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void clearAllData() {
        databaseRef.removeValue();
    }

    public void createConnection(String code) {
        databaseRef.child(code).setValue("waiting");
    }

    public DatabaseReference getConnectionRef(String code) {
        return databaseRef.child(code);
    }

    public void addLikedMovie(String movieTitle) {
        databaseRef.child("likedMovies").child(userId).child(movieTitle).setValue(true);
    }

    public void removeLikedMovie(String movieTitle) {
        databaseRef.child("likedMovies").child(userId).child(movieTitle).removeValue();
    }

    public void checkMovieMatch(String movieTitle, ValueEventListener listener) {
        databaseRef.child("likedMovies").child(userId).child(movieTitle).addListenerForSingleValueEvent(listener);
    }

    public void setMovieMatchListener(ValueEventListener listener) {
        databaseRef.child("likedMovies").addValueEventListener(listener);
    }

    public void removeMovieMatchListener(ValueEventListener listener) {
        databaseRef.child("likedMovies").removeEventListener(listener);
    }

    public void checkForMatch(String movieTitle, ValueEventListener listener) {
        databaseRef.child("likedMovies").addListenerForSingleValueEvent(listener);
    }
}