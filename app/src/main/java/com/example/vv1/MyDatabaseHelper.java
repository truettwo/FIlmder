package com.example.vv1;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
public class MyDatabaseHelper {
    private DatabaseReference myRef;

    public MyDatabaseHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
    }

    public void writeMessage(String message) {
        myRef.setValue(message);
    }
}
