package com.example.vv1;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class FirebaseHelper {

    private DatabaseReference databaseRef;

    public FirebaseHelper() {
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
}